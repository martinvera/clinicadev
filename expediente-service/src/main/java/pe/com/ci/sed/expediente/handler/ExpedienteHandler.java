package pe.com.ci.sed.expediente.handler;

import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.azure.core.util.BinaryData;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.azure.core.http.rest.PagedIterable;
import com.azure.core.util.Context;
import com.azure.core.util.serializer.TypeReference;
import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.models.QueueMessageItem;

import lombok.extern.log4j.Log4j2;
import pe.com.ci.sed.expediente.errors.ExpedienteException;
import pe.com.ci.sed.expediente.model.request.ActualizarFacturaGenerada;
import pe.com.ci.sed.expediente.model.request.GenerarExpedienteRequest;
import pe.com.ci.sed.expediente.model.request.RequestHeader;
import pe.com.ci.sed.expediente.persistence.entity.ClinicalRecord;
import pe.com.ci.sed.expediente.persistence.entity.ExpedienteDigitalTable;
import pe.com.ci.sed.expediente.persistence.entity.PdfFile;
import pe.com.ci.sed.expediente.property.GaranteProperty;
import pe.com.ci.sed.expediente.service.ClinicalRecordService;
import pe.com.ci.sed.expediente.service.ExpedienteService;
import pe.com.ci.sed.expediente.service.ExpedienteStorageService;
import pe.com.ci.sed.expediente.service.impl.DocumentStorageServiceImpl;
import pe.com.ci.sed.expediente.utils.Constants;
import pe.com.ci.sed.expediente.utils.FileUtil;

import static pe.com.ci.sed.expediente.utils.Constants.EXTENSION_PDF;
import static pe.com.ci.sed.expediente.utils.Constants.EXTENSION_ZIP;

@Log4j2
@Component
public class ExpedienteHandler {
    @Autowired
    private QueueClient queueClient;

    @Autowired
    private ExpedienteStorageService expedienteStorageService;

    @Autowired
    private DocumentStorageServiceImpl documentStorageService;

    @Autowired
    private ClinicalRecordService clinicalRecordService;

    @Autowired
    private ExpedienteService expedienteService;

    @Autowired
    private GaranteProperty garanteProperty;

    public enum ESTADO {EN_PROCESO, PENDIENTE, TERMINADO, ERROR}


    @Scheduled(fixedDelay = 5000)
    public void generarExpediente() {

        String transactionId = UUID.randomUUID().toString();
        ThreadContext.put(Constants.TRANSACTION_ID, transactionId);

        try {
            PagedIterable<QueueMessageItem> listMessage = queueClient.receiveMessages(30, Duration.ofSeconds(60), null, Context.NONE);

            listMessage.stream().forEach(message -> {
                log.info("Procesando mensaje = {}", message);

                List<GenerarExpedienteRequest> listGenerarExpediente = message.getBody().toObject(new TypeReference<>() {
                });

                List<ActualizarFacturaGenerada> actualizarFacturas = listGenerarExpediente.stream()
                        .map(this.obtenerClinicalRecord)
                        .filter(Objects::nonNull)
                        .map(this.generarExpedienteDigitalZip)
                        .filter(e -> e.getEstado().equalsIgnoreCase(ExpedienteHandler.ESTADO.TERMINADO.name()))
                        .map(e -> ActualizarFacturaGenerada.builder()
                                .garanteId(e.getGaranteId())
                                .nroLote(String.valueOf(e.getNroLote()))
                                .facturaGenerada(e.getFacturaNro())
                                .urlArchivoZipLote(e.getUrlArchivoZipLote())
                                .build())
                        .collect(Collectors.toList());

                if (!actualizarFacturas.isEmpty()) {
                    List<ActualizarFacturaGenerada> requestActualizarFacturas = new ArrayList<>();
                    requestActualizarFacturas.add(ActualizarFacturaGenerada.builder()
                            .garanteId(actualizarFacturas.stream().findFirst().get().getGaranteId())
                            .nroLote(String.valueOf(actualizarFacturas.stream().findFirst().get().getNroLote()))
                            .facturaNro(actualizarFacturas.stream().map(ActualizarFacturaGenerada::getFacturaGenerada).toArray(String[]::new))
                            .urlArchivoZipLote(actualizarFacturas.stream().findFirst().get().getUrlArchivoZipLote())
                            .build());

                    log.debug("Actualizando facturas a estado generada");

                    clinicalRecordService.actualizarFacturaGenerada(RequestHeader.builder()
                            .transactionId(transactionId)
                            .applicationId(Constants.EXPEDIENTE_DIGITAL)
                            .userId(Constants.EXPEDIENTE_DIGITAL).build(), requestActualizarFacturas);

                    log.info("Generacion de expediente digital finalizado con exito, expedientes generados para las facturas : {}", actualizarFacturas.stream().map(ActualizarFacturaGenerada::getFacturaGenerada).collect(Collectors.joining(",")));
                } else {
                    log.info("Generacion de expediente digital finalizado con errores");
                }

                queueClient.deleteMessage(message.getMessageId(), message.getPopReceipt());
                log.info("Mensaje eliminado de la cola expedientedigitalqueuestorage");

                log.info("Proceso terminado");
            });

        } catch (Exception e) {
            log.error("Ocurrio un error inesperado en la generacion del expediente digital : {}", e);
        }
    }

    private final Function<GenerarExpedienteRequest, ClinicalRecord> obtenerClinicalRecord = requestExpediente -> {
        log.info("Generando expediente digital para la factura  {} ", requestExpediente.getFacturaNro());
        ClinicalRecord factura = null;
        try {
            //Obtener factura afecta
            factura = clinicalRecordService.obtenerFactura(RequestHeader.builder().transactionId(ThreadContext.get(Constants.TRANSACTION_ID)).applicationId(Constants.EXPEDIENTE_DIGITAL).userId(Constants.EXPEDIENTE_DIGITAL).build(), requestExpediente.getFacturaNro(), requestExpediente.getNroLote());
            log.debug("Dato de la factura  {} ", factura);
            ExpedienteDigitalTable expediente = this.buildExpediente(factura, ExpedienteHandler.ESTADO.EN_PROCESO);
            expedienteService.actualizarEstadoExpediente(expediente, ExpedienteHandler.ESTADO.PENDIENTE.name());
            log.debug("Expediente de la factura  {} actualizada a Pendiente ", factura.getFacturaNro());
            return factura;
        } catch (Exception ex) {
            ExpedienteDigitalTable expediente = this.buildExpediente(requestExpediente, ExpedienteHandler.ESTADO.ERROR);
            expediente.setMsjError("Ocurrió un error en el proceso, error = " + ex.getMessage());
            try {
                expedienteService.actualizarEstadoExpediente(expediente, ExpedienteHandler.ESTADO.PENDIENTE.name());
                log.debug("Expediente de la factura  {} actualizada a Error, error = {} ", requestExpediente.getFacturaNro(), ex.getMessage());
            } catch (Exception e1) {
                log.error(e1);
            }
            return factura;
        }
    };

    private final Function<ClinicalRecord, ExpedienteDigitalTable> generarExpedienteDigitalZip = clinicalRecord -> {
        ExpedienteDigitalTable expediente;
        try {
            List<InputStream> listInputArchivo = documentStorageService.obtenerDocumentosXFactura(clinicalRecord.getNroLote(), clinicalRecord.getFacturaNro());

            expediente = this.buildExpediente(clinicalRecord, ExpedienteHandler.ESTADO.TERMINADO);

            listInputArchivo = listInputArchivo.stream().filter(Objects::nonNull).collect(Collectors.toList());
            if (!listInputArchivo.isEmpty()) {
                log.debug("Cantidad de archivos a incluir en el expediente digital {} para la factura {}", listInputArchivo.size(), clinicalRecord.getFacturaNro());
                InputStream pdfInputStream = FileUtil.unirArchivosPdf(listInputArchivo, true);
                log.debug("Creación de PDF unificado exitoso");

                String nombreArchivoPdfZip = this.obtenerNombreExpedienteDigital(clinicalRecord);
                PdfFile pdfUnificado = PdfFile.builder().stream(pdfInputStream).nombre(nombreArchivoPdfZip + EXTENSION_PDF).build();
                byte[] archivozip = FileUtil.comprimirArchivosZip(pdfUnificado);
                String nombrezip = nombreArchivoPdfZip + EXTENSION_ZIP;
                log.debug("Archivo ZIP creado exitosamente, zip = {}" + nombrezip);
                expediente.setUrlArchivoZipSas(expedienteStorageService.upload(archivozip, String.valueOf(clinicalRecord.getNroLote()), nombrezip));
                expediente.setUrlArchivoZip(expedienteStorageService.getUrlWithoutSas(expediente.getUrlArchivoZipSas()));

                this.generarZipLote(expediente, archivozip, nombrezip);

                log.debug("Expediente generado para la factura {}", clinicalRecord.getFacturaNro());
            } else {
                log.error(String.format("No se encontraron archivos en el repositorio para la factura %s", clinicalRecord.getFacturaNro()));
                expediente.setMsjError(String.format("No se encontraron archivos en el repositorio para la factura %s", clinicalRecord.getFacturaNro()));
                expediente.setEstado(ExpedienteHandler.ESTADO.ERROR.name());
            }
            log.debug("Actualizacion de expediente digital a estado {}", expediente.getEstado());
            expedienteService.actualizarEstadoExpediente(expediente, ExpedienteHandler.ESTADO.EN_PROCESO.name());


        } catch (Exception ex) {
            expediente = this.buildExpediente(clinicalRecord, ExpedienteHandler.ESTADO.ERROR);
            expediente.setMsjError("Ocurrió un error en el proceso, error = " + ex.getMessage());
            try {
                log.debug("Actualizacion de expediente digital a estado {}", expediente.getEstado());
                expedienteService.actualizarEstadoExpediente(expediente, ExpedienteHandler.ESTADO.PENDIENTE.name());
            } catch (Exception e1) {
                log.error(e1);
            }

        }
        return expediente;
    };

    private void generarZipLote(ExpedienteDigitalTable expediente, byte[] nuevoZip, String nombreNuevoZip) {
        InputStream inputStreamZipLote = expedienteStorageService.download(expediente.getNroLote());
        String urlLote;
        if (Objects.nonNull(inputStreamZipLote)) {
            ZipInputStream zis = new ZipInputStream(inputStreamZipLote);
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                BinaryData data = BinaryData.fromBytes(nuevoZip);
                ZipOutputStream zos = new ZipOutputStream(bos);
                FileUtil.agregarArchivoAlZip(data.toStream(), nombreNuevoZip, zos);
                ZipEntry ze;
                while ((ze = zis.getNextEntry()) != null) {
                    BinaryData datazip = BinaryData.fromBytes(zis.readAllBytes());
                    FileUtil.agregarArchivoAlZip(datazip.toStream(), ze.getName(), zos);
                    log.info("zip-nombre: {}", ze.getName());
                }
                zos.close();
                urlLote = expedienteStorageService.upload(bos.toByteArray(), String.valueOf(expediente.getNroLote()), expediente.getNroLote() + EXTENSION_ZIP);
            } catch (IOException e) {
                throw new ExpedienteException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            BinaryData data = BinaryData.fromBytes(nuevoZip);
            PdfFile primerZip = PdfFile.builder().stream(data.toStream()).nombre(nombreNuevoZip).build();
            byte[] zipCero = FileUtil.comprimirArchivosZip(primerZip);
            urlLote = expedienteStorageService.upload(zipCero, String.valueOf(expediente.getNroLote()), expediente.getNroLote() + EXTENSION_ZIP);
        }
        expediente.setUrlArchivoZipLote(urlLote);
    }

    private ExpedienteDigitalTable buildExpediente(GenerarExpedienteRequest request, ExpedienteHandler.ESTADO estado) {
        return ExpedienteDigitalTable.builder()
                .facturaNro(request.getFacturaNro())
                .nroLote(request.getNroLote())
                .garanteId(request.getGaranteId())
                .estado(estado.name())
                .build();
    }

    private ExpedienteDigitalTable buildExpediente(ClinicalRecord factura, ExpedienteHandler.ESTADO estado) {
        return ExpedienteDigitalTable.builder()
                .facturaNro(factura.getFacturaNro())
                .nroLote(factura.getNroLote())
                .facturaImporte(factura.getFacturaImporte())
                .mecanismoFacturacionDesc(factura.getMecanismoFacturacionDesc())
                .mecanismoFacturacionId(factura.getMecanismoFacturacionId())
                .modoFacturacion(factura.getModoFacturacion())
                .modoFacturacionId(factura.getModoFacturacionId())
                .garanteId(factura.getGaranteId())
                .garanteDescripcion(factura.getGaranteDescripcion())
                .origen(factura.getOrigen())
                .beneficioDescripcion(factura.getBeneficioDescripcion())
                .pacienteNroDocIdent(factura.getPacienteNroDocIdent())
                .pacienteNombre(factura.getPacienteNombre())
                .pacienteApellidoPaterno(factura.getPacienteApellidoPaterno())
                .pacienteApellidoMaterno(factura.getPacienteApellidoMaterno())
                .pacienteTipoDocIdentDesc(factura.getPacienteTipoDocIdentDesc())
                .nroEncuentro(String.join(",", factura.getNroEncuentro()))
                .fechaAtencion(factura.getFechaAtencion())
                .estado(estado.name())
                .build();
    }

    private String obtenerNombreExpedienteDigital(ClinicalRecord factura) {
        String sede = Strings.EMPTY;
        if (factura.getOrigen().equals(Constants.ORIGEN_SISTEMA.CONTROLDOCUMENTARIO.name())) {
            sede = factura.getCoEstru();
        } else if (factura.getOrigen().equals(Constants.ORIGEN_SISTEMA.IAFAS.name())) {
            sede = garanteProperty.getEquivalenciaSede().get(factura.getCoCentro().toUpperCase());
        }
        return String.format("%s_%s_%s", garanteProperty.getRuc(), sede, factura.getFacturaNro());
    }
}
