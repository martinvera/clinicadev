package pe.com.ci.sed.document.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import pe.com.ci.sed.document.model.request.salesforce.Diagnostico;
import pe.com.ci.sed.document.model.request.salesforce.Orden;
import pe.com.ci.sed.document.model.request.salesforce.Salesforce;
import pe.com.ci.sed.document.model.request.salesforce.Salesforce.DatoSited;
import pe.com.ci.sed.document.model.validacion.FormatoFarmacia;
import pe.com.ci.sed.document.model.validacion.GuiaFarmacia;
import pe.com.ci.sed.document.model.validacion.OrdenAtencionMedica;
import pe.com.ci.sed.document.model.validacion.OrdenHospitalizacion;
import pe.com.ci.sed.document.model.validacion.OrdenProcedimiento;
import pe.com.ci.sed.document.model.validacion.PaseAmbulatorio;
import pe.com.ci.sed.document.persistence.entity.Archivo;
import pe.com.ci.sed.document.persistence.entity.Documento;
import pe.com.ci.sed.document.service.GenFormatSalesforceService;
import pe.com.ci.sed.document.util.Constants.TipoDetalle;
import pe.com.ci.sed.document.util.FileUtil;
import pe.com.ci.sed.document.util.TipoDocSalesforce;

import javax.validation.Validator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static pe.com.ci.sed.document.util.FileUtil.generarReporte;
import static pe.com.ci.sed.document.util.FileUtil.getArchivo;
import static pe.com.ci.sed.document.util.FileUtil.getArchivoByte;
import static pe.com.ci.sed.document.util.FileUtil.getFirma;
import static pe.com.ci.sed.document.util.FileUtil.getJson;
import static pe.com.ci.sed.document.util.FileUtil.getJsonMap;
import static pe.com.ci.sed.document.util.GenericUtil.getValidations;

@Log4j2
@Service
@AllArgsConstructor
public class GenFormatSalesforceImpl implements GenFormatSalesforceService {

    private final Validator validator;

    @Override
    public List<Archivo> generarFormatos(Salesforce request, Documento documento) {
        List<Archivo> archivos = new ArrayList<>();
        final String messageError = "Ocurrio un error inesperado al generar el pdf : {} | error = {}";
        documento.getArchivos().stream().filter(t -> TipoDocSalesforce.GUIA_FARMACIA.getCodigo().equalsIgnoreCase(t.getTipoDocumentoId()))
                .findAny().ifPresent(x -> {
                            try {
                                archivos.add(generarGuiaFarmacia(request, documento.getNroEncuentro()));
                            } catch (Exception e) {
                                log.error(messageError, TipoDocSalesforce.GUIA_FARMACIA.getNombre(), e);
                            }
                        }
                );

        documento.getArchivos().stream().filter(t -> TipoDocSalesforce.ORDEN_IMAGEN_PATOLOGICO.getCodigo().equalsIgnoreCase(t.getTipoDocumentoId()))
                .findAny().ifPresent(x -> {
                            try {
                                archivos.add(generarOrdenImagenPatologico(request, documento.getNroEncuentro()));
                            } catch (Exception e) {
                                log.error(messageError, TipoDocSalesforce.ORDEN_IMAGEN_PATOLOGICO.getNombre(), e);
                            }
                        }
                );

        documento.getArchivos().stream().filter(t -> TipoDocSalesforce.ORDEN_FARMACIA.getCodigo().equalsIgnoreCase(t.getTipoDocumentoId()))
                .findAny().ifPresent(x -> {
                            try {
                                archivos.add(generarFormatoFarmacia(request, documento.getNroEncuentro()));
                            } catch (Exception e) {
                                log.error(messageError, TipoDocSalesforce.ORDEN_FARMACIA.getNombre(), e);
                            }
                        }
                );

        documento.getArchivos().stream().filter(t -> TipoDocSalesforce.PASE_AMBULATORIO.getCodigo().equalsIgnoreCase(t.getTipoDocumentoId()))
                .findAny().ifPresent(x -> {
                            try {
                                archivos.add(generarPaseAmbulatorio(request, documento.getNroEncuentro()));
                            } catch (Exception e) {
                                log.error(messageError, TipoDocSalesforce.PASE_AMBULATORIO.getNombre(), e);
                            }
                        }
                );

        documento.getArchivos().stream().filter(t -> TipoDocSalesforce.ORDEN_HOSPITALIZACION.getCodigo().equalsIgnoreCase(t.getTipoDocumentoId()))
                .findAny().ifPresent(x -> {
                            try {
                                archivos.add(generarOrdenHospitalizacionProcedimiento(request, documento.getNroEncuentro()));
                            } catch (Exception e) {
                                log.error(messageError, TipoDocSalesforce.ORDEN_HOSPITALIZACION.getNombre(), e);
                            }
                        }
                );

        documento.getArchivos().stream().filter(t -> TipoDocSalesforce.ORDEN_ATENCION_MEDICA.getCodigo().equalsIgnoreCase(t.getTipoDocumentoId()))
                .findAny().ifPresent(x -> {
                            try {
                                archivos.add(generarOrdenAtencionMedica(request, documento.getNroEncuentro()));
                            } catch (Exception e) {
                                log.error(messageError, TipoDocSalesforce.ORDEN_ATENCION_MEDICA.getNombre(), e);
                            }
                        }
                );

        return archivos.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Archivo generarGuiaFarmacia(Salesforce request, String nroEncuentro) {
        try {
            SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy");
            Date date = parser.parse(request.getEncounter().getFechaEnc());
            SimpleDateFormat formatter = new SimpleDateFormat("d MMMM yyyy", new Locale("es", "PE"));
            String[] stringDate = formatter.format(date).split(" ");
            request.getEncounter().setFechaEncDesc(String.format("%s de %s %s", stringDate[0], stringDate[1], stringDate[2]));
        } catch (ParseException e) {
            log.error("Ocurrio un error en generarGuiaFarmacia : {}", e.getMessage());
        }
        return getGuiasAfectoInafecto(request, nroEncuentro);
    }

    private Archivo getGuiasAfectoInafecto(Salesforce request, String nroEncuentro) {
        List<Archivo> guias = new ArrayList<>();
        Map<String, List<Salesforce.OrderItem>> groupAfecto = request.getEncounter().getOrderItems().stream()
                .collect(Collectors.groupingBy(Salesforce.OrderItem::getIsInafecto));

        groupAfecto.forEach((k, v) -> {
            if (!v.isEmpty()) {
                if (k.equals("0")) {
                    guias.add(generarPdfBytes(getJson(v), request, TipoDocSalesforce.GUIA_FARMACIA_AFECTO, nroEncuentro));
                }
                if (k.equals("1")) {
                    guias.add(generarPdfBytes(getJson(v), request, TipoDocSalesforce.GUIA_FARMACIA_INAFECTO, nroEncuentro));
                }
            }
        });

        List<Archivo> filter = guias.stream().filter(v -> !v.isError() && v.getBytes() != null).collect(Collectors.toList());
        String base64 = EMPTY;
        if (!filter.isEmpty()) base64 = FileUtil.unirArchivosPdf(filter, true);

        boolean isError = guias.stream().anyMatch(Archivo::isError);
        String errors = guias.stream().map(Archivo::getMsjError).collect(Collectors.joining(", "));
        return getArchivo(isError, TipoDocSalesforce.GUIA_FARMACIA, errors, base64, nroEncuentro);
    }

    private Archivo generarOrdenImagenPatologico(Salesforce request, String nroEncuentro) {
        List<Diagnostico> diagnosticos = request.getEncounter().getDiagnosticos();
        String json = getJson(diagnosticos);
        return generarPdf(json, request, TipoDocSalesforce.ORDEN_IMAGEN_PATOLOGICO, OrdenProcedimiento.class, nroEncuentro);
    }

    private Archivo generarPaseAmbulatorio(Salesforce request, String nroEncuentro) {
        return generarPdf(getJsonRequest(request, false), request, TipoDocSalesforce.PASE_AMBULATORIO, PaseAmbulatorio.class, nroEncuentro);
    }

    private String getJsonRequest(Salesforce request, boolean isOrden) {
        Salesforce.Encounter req = request.getEncounter();
        if (isOrden) {
            List<DatoSited> copagos = req.getDatosSiteds().stream()
                    .filter(v -> v.getTipoDetalle().equalsIgnoreCase(TipoDetalle.PROCEDIMIENTO))
                    .collect(Collectors.toList());
            List<Orden> preExistencias = new ArrayList<>();
            req.getDatosSiteds().stream()
                    .filter(v -> !v.getTipoDetalle().equalsIgnoreCase(TipoDetalle.PROCEDIMIENTO))
                    .collect(Collectors.groupingBy(DatoSited::getTipoDetalle, Collectors.toList()))
                    .forEach((k, value) -> preExistencias.add(Orden.builder()
                            .titulo(getLabel(k))
                            .lista(value)
                            .build()));

            req.setCopagos(copagos);
            req.setExistencias(preExistencias);
            req.getDiagnosticos().forEach(d ->
                    d.getExamenes().addAll(d.getHospitalProcedimientos().stream()
                            .map(Diagnostico.Examen::new).collect(Collectors.toList()))
            );
        } else {
            List<Orden> ordens = new ArrayList<>();
            req.getDatosSiteds().stream().collect(Collectors.groupingBy(DatoSited::getTipoDetalle))
                    .forEach((k, values) -> {
                        List<DatoSited> filter = values.stream()
                                .filter(v -> Strings.isNotBlank(v.getCodigoDifServ()) && Strings.isNotBlank(v.getDescripcionDifServ()))
                                .collect(Collectors.toList());
                        if (!filter.isEmpty())
                            ordens.add(new Orden(getLabel(k), filter));
                    });
            req.setOrdenes(ordens);
        }
        return getJson(req);
    }

    private String getLabel(String txt) {
        switch (txt) {
            case TipoDetalle.EXCEPCIONES:
                return "Excepciones Carencia";
            case TipoDetalle.PRE_EXISTENCIAS:
                return "Excepciones " + TipoDetalle.PRE_EXISTENCIAS;
            default:
                return txt;
        }
    }

    private Archivo generarFormatoFarmacia(Salesforce request, String nroEncuentro) {
        List<Diagnostico> diagnosticos = request.getEncounter().getDiagnosticos();
        String json = getJson(diagnosticos);
        return generarPdf(json, request, TipoDocSalesforce.ORDEN_FARMACIA, FormatoFarmacia.class, nroEncuentro);
    }

    private Archivo generarOrdenHospitalizacionProcedimiento(Salesforce request, String nroEncuentro) {
        List<Diagnostico> diagnosticos = request.getEncounter().getDiagnosticos();
        request.getEncounter().setIspatologico(!request.getEncounter().getAntecedentesPatologicos().isEmpty());
        request.getEncounter().setIsquirurgico(!request.getEncounter().getAntecedentesQuirurgicos().isEmpty());
        diagnosticos.get(0).setAntecedentesQuirurgicos(request.getEncounter().getAntecedentesQuirurgicos());
        diagnosticos.get(0).setAntecedentesPatologicos(request.getEncounter().getAntecedentesPatologicos());
        diagnosticos.get(0).setAntecedentesPersonalMenor(request.getEncounter().getAntecedentesPersonalMenor());
        String json = getJson(diagnosticos);
        return generarPdf(json, request, TipoDocSalesforce.ORDEN_HOSPITALIZACION, OrdenHospitalizacion.class, nroEncuentro);
    }

    private Archivo generarOrdenAtencionMedica(Salesforce request, String nroEncuentro) {
        return generarPdf(getJsonRequest(request, true), request, TipoDocSalesforce.ORDEN_ATENCION_MEDICA, OrdenAtencionMedica.class, nroEncuentro);
    }

    private Archivo generarPdf(String json, Salesforce request, TipoDocSalesforce tipodoc, Class<?> tclass, String nroEncuentro) {
        Map<String, Object> params = getJsonMap(request.getEncounter());
        params.put("FIRMAMEDICO", getFirma(request.getEncounter().getFirmaMedico()));

        String pdfBase64 = EMPTY;
        String messageError = getValidations(validator, request, tclass);
        if (Strings.isEmpty(messageError)) {
            pdfBase64 = Base64.getEncoder().encodeToString(generarReporte(json, params, "salesforce/" + tipodoc.getName()));
            log.debug("Documento {} generado correctamente ", tipodoc.getNombre());
        } else {
            log.debug("Documento {} no se pudo generar, no pasó las siguientes validaciones : {} ", tipodoc.getNombre(), messageError);
        }
        return getArchivo(Strings.isNotBlank(messageError), tipodoc, messageError, pdfBase64, nroEncuentro);
    }

    private Archivo generarPdfBytes(String json, Salesforce request, TipoDocSalesforce tipodoc, String nroEncuentro) {
        Map<String, Object> params = getJsonMap(request.getEncounter());
        params.put("FIRMAMEDICO", getFirma(request.getEncounter().getFirmaMedico()));

        String messageError = getValidations(validator, request, GuiaFarmacia.class);

        byte[] bytes = null;
        if (Strings.isEmpty(messageError)) {
            bytes = generarReporte(json, params, "salesforce/" + tipodoc.getName());
            log.debug("Documento {} generado correctamente ", tipodoc.getName());
        } else {
            log.debug("Documento {} no se pudo generar, no pasó las siguientes validaciones : {} ", tipodoc.getName(), messageError);
        }
        return getArchivoByte(Strings.isNotBlank(messageError), tipodoc, messageError, bytes, nroEncuentro);
    }

}
