package pe.com.ci.sed.document.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import pe.com.ci.sed.document.model.request.xhis.GroupLaboratorio;
import pe.com.ci.sed.document.model.request.xhis.GuiaFarmacia;
import pe.com.ci.sed.document.model.request.xhis.LaboratorioImagenes;
import pe.com.ci.sed.document.model.request.xhis.Xhis;
import pe.com.ci.sed.document.model.validacion.OrdenProcedimiento;
import pe.com.ci.sed.document.persistence.entity.Archivo;
import pe.com.ci.sed.document.persistence.entity.Documento;
import pe.com.ci.sed.document.service.GenFormatXhisService;
import pe.com.ci.sed.document.util.FileUtil;
import pe.com.ci.sed.document.util.GenericUtil;
import pe.com.ci.sed.document.util.TipoDocXhis;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static pe.com.ci.sed.document.util.Constants.FORMAT_HORA;
import static pe.com.ci.sed.document.util.Constants.PARAM_FIRMAMEDICO;
import static pe.com.ci.sed.document.util.FileUtil.generarReporte;
import static pe.com.ci.sed.document.util.FileUtil.getArchivo;
import static pe.com.ci.sed.document.util.FileUtil.getJson;
import static pe.com.ci.sed.document.util.FileUtil.getJsonMap;

@Log4j2
@Service
@AllArgsConstructor
public class GenFormatXhisImpl implements GenFormatXhisService {

    private final Validator validator;

    @Override
    public List<Archivo>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     generarFormatos(Xhis request, Documento documento) {
        request.getCabecera().setFechaEnc(request.getCabecera().getFechaEnc().replace(FORMAT_HORA, EMPTY));
        request.getCabecera().setFechaNacPaciente(request.getCabecera().getFechaNacPaciente().replace(FORMAT_HORA, EMPTY));
        request.getCabecera().setFechaVenc(request.getCabecera().getFechaVenc().replace(FORMAT_HORA, EMPTY));
        if (Objects.nonNull(request.getCabecera().getFinCarencia()))
            request.getCabecera().setFinCarencia(request.getCabecera().getFinCarencia().replace(FORMAT_HORA, EMPTY));
        List<Archivo> archivos = new ArrayList<>();
        List<String> keys = documento.getArchivos().stream().map(Archivo::getTipoDocumentoId).collect(Collectors.toList());
        final String messageError = "Ocurrio un error inesperado al generar el pdf : {} | error = {}";

        keys.stream().filter(t -> TipoDocXhis.RECETA_MEDICA.getCodigo().equalsIgnoreCase(t)).findAny().ifPresent(id -> {
            try {
                archivos.add(generarRecetaMedica(request));
            } catch (Exception e) {
                log.error(messageError, TipoDocXhis.RECETA_MEDICA.getNombre(), e);
            }
        });

        keys.stream().filter(t -> TipoDocXhis.ORDEN_FARMACIA.getCodigo().equalsIgnoreCase(t)).findAny().ifPresent(id -> {
            try {
                archivos.add(generarOrdenFarmacia(request));
            } catch (Exception e) {
                log.error(messageError, TipoDocXhis.ORDEN_FARMACIA.getNombre(), e);
            }
        });

        keys.stream().filter(t -> TipoDocXhis.PASE_AMBULATORIO.getCodigo().equalsIgnoreCase(t)).findAny().ifPresent(id -> {
            try {
                archivos.add(generarPaseAmbulatorio(request));
            } catch (Exception e) {
                log.error(messageError, TipoDocXhis.PASE_AMBULATORIO.getNombre(), e);
            }
        });

        keys.stream().filter(t -> TipoDocXhis.ORDEN_PROCEDIMIENTO.getCodigo().equalsIgnoreCase(t)).findAny().ifPresent(id -> {
            try {
                archivos.add(generarOrdenProcedimiento(request));
            } catch (Exception e) {
                log.error(messageError, TipoDocXhis.ORDEN_PROCEDIMIENTO.getNombre(), e);
            }
        });


        keys.stream().filter(t -> TipoDocXhis.ORDEN_LABORATORIO.getCodigo().equalsIgnoreCase(t)).findAny().ifPresent(id -> {
            try {
                archivos.add(generarOrdenLaboratorio(request));
            } catch (Exception e) {
                log.error(messageError, TipoDocXhis.ORDEN_LABORATORIO.getNombre(), e);
            }
        });

        archivos.addAll(generarBoletaFarmacia(request));

        return archivos.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Archivo generarRecetaMedica(Xhis request) {
        String json = getJson(request.getOrdenFarmacia().getMedicamentos());
        String diagnosticos = request.getGuiaFarmacia().getDiagnosticos().stream()
                .map(x -> String.format("%s-%s", x.getCodigo(), x.getDescripcion()))
                .collect(Collectors.joining(", "));
        request.getCabecera().setDiagnosticos(diagnosticos);
        return generarPdf(json, request, TipoDocXhis.RECETA_MEDICA);
    }

    private Archivo generarOrdenFarmacia(Xhis request) {
        String codigos = request.getGuiaFarmacia().getDiagnosticos().stream()
                .map(x -> String.format("%s-%s", x.getCodigo(), x.getDescripcion()))
                .collect(Collectors.joining(", "));
        request.getCabecera().setCodigosCi10(codigos);

        String guias = request.getGuiaFarmacia().getDetalles().stream()
                .filter(GenericUtil.distinctByKey(GuiaFarmacia.GuiaDetalle::getNuGuiaFar))
                .map(GuiaFarmacia.GuiaDetalle::getNuGuiaFar)
                .collect(Collectors.joining(", "));
        request.getCabecera().setNroGuias(guias);

        request.getGuiaFarmacia().getDetalles().forEach(d -> request.getOrdenFarmacia().getMedicamentos().stream()
                .filter(m -> m.getDescripcion().equalsIgnoreCase(d.getNoGenerico())).findAny()
                .ifPresent(m -> {
                    d.setCaMultidosis(m.getDosis());
                    d.setVia(m.getVia());
                }));

        String json = getJson(request.getGuiaFarmacia().getDetalles());
        return generarPdf(json, request, TipoDocXhis.ORDEN_FARMACIA);
    }

    private Archivo generarPaseAmbulatorio(Xhis request) {
        request.getCabecera().setPaseAmbulatorio(request.getPaseAmbulatorio());
        String json = getJson(request);
        return generarPdf(json, request, TipoDocXhis.PASE_AMBULATORIO);
    }


    private Archivo generarOrdenProcedimiento(Xhis request) {
        String json = getJson(request.getProcedimiento());
        request.getCabecera().setIsquirurgico(!request.getProcedimiento().getAntecedentes().isEmpty());
        return generarPdf(json, request, TipoDocXhis.ORDEN_PROCEDIMIENTO);
    }

    private Archivo generarOrdenLaboratorio(Xhis request) {
        request.setLaboratorios(new ArrayList<>());
        request.getLaboratorioImagenes().stream()
                .collect(Collectors.groupingBy(LaboratorioImagenes::getCodigo))
                .forEach((k, list) -> {
                    var lista = list.get(0);
                    request.getLaboratorios().add(new GroupLaboratorio<>(lista.getDescripcion(), lista.getTipo(), lista.getCodigo(), list));
                });
        String json = getJson(request.getLaboratorios());
        return generarPdf(json, request, TipoDocXhis.ORDEN_LABORATORIO);
    }

    private List<Archivo> generarBoletaFarmacia(Xhis request) {
        List<List<Archivo>> archivos = request.getGuiaFarmacia().getFacturas().stream().filter(Objects::nonNull).map(f -> f.getMontoGuias().stream()
                        .filter(c -> Strings.isNotBlank(c.getUrlDocFarmacia()) && Objects.nonNull(c.getUrlDocFarmacia()))
                        .map(c -> {
                            byte[] file = FileUtil.downloadFromUrl(c.getUrlDocFarmacia());
                            return Archivo.builder()
                                    .bytes(file)
                                    .build();
                        }).filter(a -> Objects.nonNull(a.getBytes())).collect(Collectors.toList()))
                .collect(Collectors.toList());
        if (archivos.isEmpty()) return new ArrayList<>();
        List<Archivo> archivoss = new ArrayList<>();
        archivos.forEach(archivoss::addAll);
        return List.of(Archivo.builder()
                .nroEncuentro(request.getEpisodio())
                .archivoBytes(FileUtil.unirArchivosPdf(archivoss, true))
                .tipoDocumentoId(TipoDocXhis.BOLETA_FARMACIA.getCodigo())
                .tipoDocumentoDesc(TipoDocXhis.BOLETA_FARMACIA.getNombre())
                .build());
    }

    private Archivo generarPdf(String json, Xhis request, TipoDocXhis tipodoc) {
        Map<String, Object> params = getJsonMap(request.getCabecera());
        params.put(PARAM_FIRMAMEDICO, request.getCabecera().getFirmaMedico());

        String pdfBase64 = EMPTY;
        String messageError = getValidations(request, OrdenProcedimiento.class);
        if (Strings.isEmpty(messageError)) {
            pdfBase64 = Base64.getEncoder().encodeToString(generarReporte(json, params, "xhis/" + tipodoc.getName()));
            log.debug("Documento {} generado correctamente ", tipodoc.getNombre());
        } else {
            log.debug("Documento {} no se pudo generar, no pasó las siguientes validaciones : {} ", tipodoc.getNombre(), messageError);
        }
        return getArchivo(Strings.isNotBlank(messageError), tipodoc, messageError, pdfBase64, request.getEpisodio());
    }

    private <E> String getValidations(E object, Class<?> tclass) {
        Set<ConstraintViolation<E>> violations = validator.validate(object, tclass);
        return violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(" \n "));
    }

}
