package pe.com.ci.sed.clinicalrecord.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.azure.core.http.rest.PagedIterable;
import com.azure.core.http.rest.Response;
import com.azure.core.util.Context;
import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.models.QueueMessageItem;
import com.azure.storage.queue.models.SendMessageResult;
import com.fasterxml.jackson.core.type.TypeReference;

import lombok.extern.log4j.Log4j2;
import pe.com.ci.sed.clinicalrecord.errors.ClinicalInafectaException;
import pe.com.ci.sed.clinicalrecord.errors.ClinicalRecordException;
import pe.com.ci.sed.clinicalrecord.model.request.RegistrarFacturaRequest;
import pe.com.ci.sed.clinicalrecord.persistence.entity.ClinicalRecord;
import pe.com.ci.sed.clinicalrecord.persistence.entity.GenericDocument;
import pe.com.ci.sed.clinicalrecord.persistence.entity.GestionLote;
import pe.com.ci.sed.clinicalrecord.utils.Constants;

import static pe.com.ci.sed.clinicalrecord.utils.Constants.MODO_FACTURACION_INAFECTA;
import static pe.com.ci.sed.clinicalrecord.utils.GenericUtil.convertString;
import static pe.com.ci.sed.clinicalrecord.utils.GenericUtil.readValue;

@Log4j2
@Service
public class QueueService {

    private ClinicalRecordService clinicalRecordService;
    private final QueueClient queueClientClinicalRecord;
    private final QueueClient queueClientDocumento;
    private final QueueClient queueClientError;
    private StorageService storageService;
    private GestionLotesService gestionLotesService;

    public QueueService(ClinicalRecordService clinicalRecordService, QueueClient queueClientClinicalRecord,
                        QueueClient queueClientDocumento, QueueClient queueClientError,
                        StorageService storageService, GestionLotesService gestionLotesService) {
        this.clinicalRecordService = clinicalRecordService;
        this.queueClientClinicalRecord = queueClientClinicalRecord;
        this.queueClientDocumento = queueClientDocumento;
        this.queueClientError = queueClientError;
        this.storageService = storageService;
        this.gestionLotesService = gestionLotesService;
    }

    @Scheduled(initialDelay = 5000, fixedDelay = 5000)
    public void procesarMensajeConError() {
        try {
            PagedIterable<QueueMessageItem> listMessage = queueClientError.receiveMessages(30, Duration.ofSeconds(60), null, Context.NONE);
            listMessage.forEach(this.registrarFactura);
        } catch (Exception e) {
            log.error("Ocurrió un error al obtener los mensajes de la cola {} : {}", queueClientError.getQueueName(), e);
        }
    }

    @Scheduled(initialDelay = 5000, fixedDelay = 5000)
    public void procesarMensaje() {
        try {
            QueueMessageItem messageItem = queueClientClinicalRecord.receiveMessage();
            Optional.ofNullable(messageItem).ifPresent(this.registrarFactura);
        } catch (Exception e) {
            log.error("Ocurrió un error al obtener los mensajes de la cola {} : {}", queueClientClinicalRecord.getQueueName(), e);
        }

    }

    private Consumer<QueueMessageItem> registrarFactura = message -> {
        ThreadContext.put("transactionId", UUID.randomUUID().toString());
        ClinicalRecord clinicalRecord;
        RegistrarFacturaRequest factura = null;
        GenericDocument<String> genericDocument;
        try {
            genericDocument = readValue(message.getBody().toString(), new TypeReference<>() {
            });
            String url = genericDocument.getContenido();
            factura = readValue(storageService.download(url), RegistrarFacturaRequest.class);

            this.validarInafecta(factura, genericDocument, message);

            log.info("Factura enviada desde {} - data = {}", genericDocument.getSistemaOrigen(), convertString(factura));

            clinicalRecord = this.buildClinicalRecord(factura, genericDocument.getSistemaOrigen());

            Optional<ClinicalRecord> optClinical = clinicalRecordService.findById(clinicalRecord.getFacturaNro(), clinicalRecord.getNroLote());

            String carpeta = !MODO_FACTURACION_INAFECTA.contains(factura.getCoSubMecaPago()) ? factura.getNuDocPago() : clinicalRecord.getFacturaNroAfecta();

            String urlFacturaPdf;
            InputStream archivo = this.obtenerPdfFromUrl(factura.getUrlDocPago());
            if (Objects.nonNull(archivo)) {
                urlFacturaPdf = storageService.uploadFactura(archivo, factura.getNuLote(), carpeta, factura.getNuDocPago());
                log.debug("Pdf Factura creado correctamente , Numero factura = {} , numero de lote = {}, url = {}", clinicalRecord.getFacturaNro(), clinicalRecord.getNroLote(), urlFacturaPdf);
            } else {
                throw new ClinicalRecordException("Error al descargar la factura", HttpStatus.BAD_REQUEST);
            }

            String urlAnexoDet = storageService.uploadAnexoDetallado(factura.getPdfAnexoDetallado(), factura.getNuLote(), carpeta, factura.getNuDocPago());
            log.debug("Anexo detallado creado correctamente , Numero factura = {} , numero de lote = {}, url = {}", clinicalRecord.getFacturaNro(), clinicalRecord.getNroLote(), urlAnexoDet);

            clinicalRecord.setFacturaArchivo(storageService.getUrlWithoutSas(urlFacturaPdf));
            clinicalRecord.setFacturaArchivoSas(urlFacturaPdf);
            clinicalRecord.setArchivoAnexoDet(storageService.getUrlWithoutSas(urlAnexoDet));
            clinicalRecord.setArchivoAnexoDetSas(urlAnexoDet);

            log.debug("Numero factura = {} , numero de lote = {} grabada en cosmos con exito", clinicalRecord.getFacturaNro(), clinicalRecord.getNroLote());
            clinicalRecordService.registrarFactura(clinicalRecord);
            if (!MODO_FACTURACION_INAFECTA.contains(factura.getCoSubMecaPago())) {
                this.encolarDocumento(genericDocument, clinicalRecord.getFacturaNro(), clinicalRecord.getNroLote());
                if (optClinical.isEmpty()) {
                    GestionLote lote = GestionLote.builder().nroLote(factura.getNuLote())
                            .origen(genericDocument.getSistemaOrigen())
                            .facturasGeneradas(0)
                            .estadoGarante(Constants.ESTADO_GARANTE.PENDIENTE.name())
                            .fechaEnvio("-")
                            .fechaAceptacion("-")
                            .fechaRechazo("-")
                            .fechaRegistroSolicitud("-")
                            .registroSolicitud("-")
                            .usuario("-")
                            .totalFacturas(1)
                            .garanteDescripcion(factura.getNoGarante())
                            .garanteId(factura.getCoGarante())
                            .build();
                    gestionLotesService.registrarActualizarLotes(lote);
                } else {
                    log.debug("Factura existente, no se actualiza la cantidad de facturas para el lote {}", clinicalRecord.getNroLote());
                }
            } else {
                storageService.delete(url);
            }

            this.eliminarCola(message);
            log.info("Factura {} registrada en SED exitosamente", clinicalRecord.getFacturaNro());
        } catch (ClinicalInafectaException ce) {
            log.info(ce.getMessage());
	} catch (Exception e) {
            log.error("Error  : ocurrió un error inesperado al procesar el mensaje {} , detalle = {}", message.getMessageId(), factura);
            log.error(e);
            this.eliminarCola(message);
            this.enviarColaError(message);
        }

    };

    private InputStream obtenerPdfFromUrl(String url) {
        try {
            return new URL(url).openStream();
        } catch (IOException e) {
            log.error("Ocurrio un error al obtener el pdf de la factura , url = {} , error = {} ", url, e);
        }
        return null;
    }

    public void validarInafecta(RegistrarFacturaRequest factura, GenericDocument<String> genericDocument, QueueMessageItem message) {
        String nroFacturaInafecta = factura.getNuDocPago().toUpperCase();
        log.info("Validar si factura es inafecta: {}", nroFacturaInafecta);
        if (MODO_FACTURACION_INAFECTA.contains(factura.getCoSubMecaPago())) {
            log.info("Factura: {} es inafecta", nroFacturaInafecta);
            String[] encuentros = Stream.of(factura.getEncuentros()).map(RegistrarFacturaRequest.Detalle::getCoPrestacion).toArray(String[]::new);
            var facturas = clinicalRecordService.findByNroEncuentro(encuentros).stream()
                    .filter(e -> !MODO_FACTURACION_INAFECTA.contains(Long.valueOf(e.getModoFacturacionId()))).collect(Collectors.toList());
            log.info(facturas);
            if (facturas.size() == 1) {
                Optional.ofNullable(facturas.get(0)).ifPresentOrElse(existe -> {
                    log.info("Existe factura-afecta: {} ,para factura-inafecta: {}", existe.getFacturaNro(), nroFacturaInafecta);
                    existe.setFacturaNroInafecta(nroFacturaInafecta);
                    factura.setFacturaNroAfecta(existe.getFacturaNro());
                    clinicalRecordService.guardarFactura(existe);
                }, () -> {
                    log.info("No existe factura-afecta para factura-inafecta: {}", nroFacturaInafecta);
                    this.exceptionEnconlar(genericDocument, message, nroFacturaInafecta);
                });
            } else {
                if (!facturas.isEmpty())
                    log.info("Existe más de una factura-afecta para factura-inafecta: {}, lotes: {}", nroFacturaInafecta, facturas.stream().map(ClinicalRecord::getNroLote)
                            .map(String::valueOf).collect(Collectors.joining(",")));
                this.exceptionEnconlar(genericDocument, message, nroFacturaInafecta);
            }
        }
    }

    private void exceptionEnconlar(GenericDocument<String> genericDocument, QueueMessageItem message, String nroFacturaInafecta) {
        this.volverEncolarClinical(genericDocument, nroFacturaInafecta, message);
        throw new ClinicalInafectaException(String.format("Factura %s es inafecta, se volvió a encolar", nroFacturaInafecta));
    }

    private void volverEncolarClinical(GenericDocument<String> genericDocument, String nroFactura, QueueMessageItem message) {
        log.info("Eliminando cola factura inafecta: {}", nroFactura);
        this.eliminarCola(message);
        log.info("Enviar a encolar factura inafecta: {}", nroFactura);
        Response<SendMessageResult> result = this.genericEnviarCola(queueClientClinicalRecord, convertString(genericDocument));
        if (result.getStatusCode() == HttpStatus.CREATED.value()) {
            log.info("Mensaje enviado a la cola {} con exito, numero factura = {} , messageId = {}", queueClientClinicalRecord.getQueueName(), nroFactura, result.getValue().getMessageId());
        } else {
            log.info("Error al enviar con la factura numero factura = {} , a la cola {}", nroFactura, queueClientClinicalRecord.getQueueName());
        }
    }

    private void encolarDocumento(GenericDocument<String> genericDocument, String facturaNro, long nroLote) {
        Response<SendMessageResult> result = this.genericEnviarCola(queueClientDocumento, convertString(genericDocument));
        if (result.getStatusCode() == HttpStatus.CREATED.value()) {
            log.info("Mensaje enviado a la cola {} con exito, numero factura = {} , numero de lote = {}, messageId = {}", queueClientClinicalRecord.getQueueName(), facturaNro, nroLote, result.getValue().getMessageId());
        } else {
            log.info("Error al enviar con la factura numero factura = {} , numero de lote = {} a la cola {}", facturaNro, nroLote, queueClientClinicalRecord.getQueueName());
        }
    }

    private void eliminarCola(QueueMessageItem message) {
        queueClientClinicalRecord.deleteMessageWithResponse(message.getMessageId(), message.getPopReceipt(), null, Context.NONE);
    }

    private void enviarColaError(QueueMessageItem message) {
        log.error("Se envia la trama {} a la cola de error {}", message.getBody().toString(), queueClientError.getQueueName());

        Response<SendMessageResult> result = this.genericEnviarCola(queueClientError, message.getBody().toString());
        if (result.getStatusCode() == HttpStatus.CREATED.value()) {
            log.debug("El mensaje fue encolado con exito en la cola de error");
        } else {
            log.debug("Hubo un error");
        }
    }

    private Response<SendMessageResult> genericEnviarCola(QueueClient queueClient, String data) {
        return queueClient.sendMessageWithResponse(data, null, Duration.ofSeconds(-1), null, Context.NONE);
    }

    private ClinicalRecord buildClinicalRecord(RegistrarFacturaRequest factura, String sistemaOrigen) {
        return ClinicalRecord.builder()
                .nroLote(factura.getNuLote())
                .facturaNro(factura.getNuDocPago().toUpperCase())
                .facturaImporte(factura.getVaMontoFact())
                .pacienteNroDocIdent(factura.getNuDocPaciente())
                .pacienteApellidoPaterno(factura.getApePatPaciente().toUpperCase())
                .pacienteApellidoMaterno(factura.getApeMatPaciente().toUpperCase())
                .pacienteNombre(factura.getVaNoPaciente().toUpperCase())
                .nroHistoriaClinica(factura.getNuNhcPaciente().toUpperCase())
                .pacienteTipoDocIdentId(factura.getTiDocPaciente())
                .mecanismoFacturacionId(String.valueOf(factura.getCoMecaPago()))
                .mecanismoFacturacionDesc(factura.getDeMecaPago().toUpperCase())
                .modoFacturacionId(String.valueOf(factura.getCoSubMecaPago()))
                .modoFacturacion(factura.getDeSubMecaPago().toUpperCase())
                .garanteId(factura.getCoGarante())
                .garanteDescripcion(factura.getNoGarante().toUpperCase())
                .nroEncuentro(Stream.of(factura.getEncuentros()).map(RegistrarFacturaRequest.Detalle::getCoPrestacion).toArray(String[]::new))
                .fechaAtencion(Stream.of(factura.getEncuentros()).map(RegistrarFacturaRequest.Detalle::getFePrestacion).findFirst().orElse(null))
                .facturaFecha(factura.getFeComprobante())
                .facturaArchivo(Strings.EMPTY)
                .estadoFactura(Constants.ESTADO_FACTURA.INCOMPLETO.name())
                .origenServicioId(factura.getTiEpisodioXhis())
                .origenServicioDesc(factura.getDeEpisodioXhis())
                .origen(sistemaOrigen)
                .nroRemesa(factura.getNuRemesa())
                .coEstru(factura.getCoEstru())
                .coCentro(factura.getCoCentro())
                .facturaNroAfecta(factura.getFacturaNroAfecta())
                .build();
    }

}
