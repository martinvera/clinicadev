package pe.com.ci.sed.document.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pe.com.ci.sed.document.errors.DocumentException;
import pe.com.ci.sed.document.model.request.RegistrarDocRequest;
import pe.com.ci.sed.document.model.request.xhis.Xhis;
import pe.com.ci.sed.document.persistence.entity.Archivo;
import pe.com.ci.sed.document.persistence.entity.Documento;
import pe.com.ci.sed.document.persistence.entity.GenericDocument;
import pe.com.ci.sed.document.service.GenFormatXhisService;
import pe.com.ci.sed.document.util.FileUtil;
import pe.com.ci.sed.document.util.TipoDocXhis;

import java.util.*;
import java.util.stream.Collectors;

import static pe.com.ci.sed.document.util.GenericUtil.mapper;

@Log4j2
@Service
@AllArgsConstructor
public class XhisServiceImpl {

    private final RestTemplate restTemplateIntegracion;
    private final GenFormatXhisService genFormatXhisService;

    public List<Archivo> generarDocumentosXhis(Documento documento) {
        List<Archivo> archivos = new ArrayList<>();
        try {
            log.info("Inicio de integración con response para el encuentro {}", documento.getNroEncuentro());
            Xhis xhis = this.obtenerEncuentroXhis(documento.getNroEncuentro());
            log.debug("Datos del encuentro {}, {} ", documento.getNroEncuentro(), xhis);
            if (xhis.getCodRespuesta().equals("0"))
                return genFormatXhisService.generarFormatos(xhis, documento);
        } catch (Exception e) {
            log.error(e);
        }
        return archivos;
    }

    public Xhis obtenerEncuentroXhis(String nroEncuentro) {
        try {
            Map<String, String> request = new HashMap<>();
            request.put("NU_ENCUENTRO", nroEncuentro);
            log.debug("Obteniendo datos desde xhis para el encuentro {} ", nroEncuentro);
            return restTemplateIntegracion.postForObject("/ExpedienteDigital/xHIS", request, Xhis.class);
        } catch (Exception e) {
            throw new DocumentException(String.format("Ocurrió un error en la comunicación con xhis para el encuentro %s", nroEncuentro), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public List<Archivo> descargaComprobantes(RegistrarDocRequest.Detalle[] encuentros) {
        return Arrays.stream(encuentros).filter(Objects::nonNull).map(e -> {
            List<Archivo> archivos = e.getComprobantes().stream()
                    .filter(c -> c.getIndicador().equals("1"))
                    .map(c -> {
                        byte[] file = FileUtil.downloadFromUrl(c.getUrlDoc());
                        return Archivo.builder()
                                .bytes(file)
                                .build();
                    }).collect(Collectors.toList());
            if (archivos.isEmpty()) return null;
            return Archivo.builder()
                    .nroEncuentro(e.getCoPrestacion())
                    .archivoBytes(FileUtil.unirArchivosPdf(archivos, true))
                    .tipoDocumentoId(TipoDocXhis.BOLETA_FARMACIA.getCodigo())
                    .tipoDocumentoDesc(TipoDocXhis.BOLETA_FARMACIA.getNombre())
                    .build();
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
