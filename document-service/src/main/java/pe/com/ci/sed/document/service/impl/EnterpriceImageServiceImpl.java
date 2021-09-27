package pe.com.ci.sed.document.service.impl;

import com.azure.core.util.BinaryData;
import jcifs.smb.SmbFile;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pe.com.ci.sed.document.model.request.enterprise.EnterpriseIRequest;
import pe.com.ci.sed.document.persistence.entity.Archivo;
import pe.com.ci.sed.document.service.EnterpriceImageService;
import pe.com.ci.sed.document.util.TipoDocEnterprice;

import java.io.IOException;
import java.util.Base64;

@Log4j2
@Service
@AllArgsConstructor
public class EnterpriceImageServiceImpl implements EnterpriceImageService {

    @Override
    public Archivo obtenerInformeImagenes(EnterpriseIRequest enterprice) {
        return Archivo.builder()
                .archivoBytes(this.obtenerPdfFromUrl(enterprice.getRutaResultadoRis()))
                .nroEncuentro(enterprice.getNuEncuentro())
                .tipoDocumentoId(TipoDocEnterprice.INFORME_DE_IMAGENES.getNombre())
                .tipoDocumentoDesc(TipoDocEnterprice.INFORME_DE_IMAGENES.getCodigo())
                .build();
    }

    @Override
    public String obtenerPdfFromUrl(String url) {
        try {
            SmbFile file = new SmbFile(url);
            BinaryData data = BinaryData.fromStream(file.getInputStream());
            return Base64.getEncoder().encodeToString(data.toBytes());
        } catch (IOException e) {
            log.error("Ocurrio un error al obtener el pdf de la factura , url = {} , error = {} ", url, e);
        }
        return null;
    }
}
