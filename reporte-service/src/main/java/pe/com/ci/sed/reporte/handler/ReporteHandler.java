package pe.com.ci.sed.reporte.handler;

import com.azure.core.http.rest.Response;
import com.azure.core.util.Context;
import com.azure.core.util.serializer.TypeReference;
import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.models.QueueMessageItem;
import com.azure.storage.queue.models.SendMessageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pe.com.ci.sed.reporte.errors.ReporteException;
import pe.com.ci.sed.reporte.model.generic.GenericRequest;
import pe.com.ci.sed.reporte.model.request.RequestReporte;
import pe.com.ci.sed.reporte.persistence.entity.Historial;
import pe.com.ci.sed.reporte.service.ExpedienteService;
import pe.com.ci.sed.reporte.service.HistorialService;
import pe.com.ci.sed.reporte.service.ReporteService;
import pe.com.ci.sed.reporte.utils.ReporteUtil;
import pe.com.ci.sed.reporte.utils.TipoReporte;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static pe.com.ci.sed.reporte.utils.Constants.ESTADO.PENDIENTE;
import static pe.com.ci.sed.reporte.utils.GenericUtil.getResult;

@Log4j2
@Service
@AllArgsConstructor
public class ReporteHandler {

    private final QueueClient queueClient;
    private final ReporteService reporteService;
    private final HistorialService historialService;
    private final ExpedienteService expedienteServie;
    private final ObjectMapper mapper;

    public Object enviarColaReporte(GenericRequest<RequestReporte> request) {
        try {
            String validacion = validaciondeNroLote(request);
            if (Strings.isNotBlank(validacion))
                return getResult(request.getHeader(), validacion);

            Historial historial = historialService.registrarHistorial(GenericRequest.<Historial>builder()
                    .request(Historial.builder()
                            .estado(PENDIENTE.name())
                            .fecha(new Date())
                            .tipo(request.getRequest().getTipoReporte())
                            .url(EMPTY)
                            .usuario(request.getHeader().getUserId().toUpperCase())
                            .filtros(ReporteUtil.getFiltros(request.getRequest()))
                            .build())
                    .header(request.getHeader())
                    .build());
            request.setHistorial(historial);
            Response<SendMessageResult> result = queueClient.sendMessageWithResponse(mapper.writeValueAsString(request), null, Duration.ofSeconds(-1), null, Context.NONE);
            if (result.getStatusCode() == HttpStatus.CREATED.value()) {
                String mensaje = "Enviado a cola correctamente";
                return getResult(request.getHeader(), mensaje);
            }
            throw new ReporteException("Ocurrió un error al enviar la cola", INTERNAL_SERVER_ERROR);
        } catch (JsonProcessingException e) {
            throw new ReporteException(e.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }

    private String validaciondeNroLote(GenericRequest<RequestReporte> request) {
        if (request.getRequest().getTipoReporte().equals(TipoReporte.PARCIAL.name())) {
            String validacion = expedienteServie.validarNroLote(request);
            if (Strings.isNotBlank(validacion))
                return validacion;
        }
        return EMPTY;
    }

    @Async
    @Scheduled(initialDelay = 5000, fixedDelay = 5000)
    void leerColaReporte() {
        String transactionId = UUID.randomUUID().toString();
        ThreadContext.put("transactionId", transactionId);
        try {
            Optional.ofNullable(queueClient.receiveMessage()).ifPresent(message -> {
                GenericRequest<RequestReporte> reporte = message.getBody().toObject(new TypeReference<>() {
                });
                String tipoReporte = reporte.getRequest().getTipoReporte();
                log.info("procesando-message: {} , tipoReporte: {}", message.getMessageId(), tipoReporte);
                log.debug("generando-reporte: {}", tipoReporte);
                if (tipoReporte.equals(TipoReporte.EXPEDIENTEMECANISMO.getNombre())) {
                    reporteService.generarReporteMecYMod(reporte);
                } else if (tipoReporte.equals(TipoReporte.EXPEDIENTECONERROR.getNombre())) {
                    reporteService.generarReporteExpError(reporte);
                } else if (tipoReporte.equals(TipoReporte.ENCUENTROSINLOTE.getNombre())) {
                    reporteService.generarReporteSinLote(reporte);
                } else if (tipoReporte.equals(TipoReporte.PARCIAL.getNombre())) {
                    reporteService.generarReporteParcial(reporte);
                } else if (tipoReporte.equals(TipoReporte.ENVIADOGARANTE.getNombre())) {
                    reporteService.generarReporteEnviadoGarante(reporte);
                }
                log.info("termino-reporte: {}", tipoReporte);
                log.debug("eliminando-message: {} , tipoReporte: {}", message.getMessageId(), tipoReporte);
                queueClient.deleteMessage(message.getMessageId(), message.getPopReceipt());
            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Async
    @Scheduled(cron = "0 0 0 * * *")
    void generarReporteGenera() {
        String transactionId = UUID.randomUUID().toString();
        ThreadContext.put("transactionId", transactionId);
        log.info("inicio-reporte: {}", "TOTAL");
        reporteService.generarReporteTotal();
        log.info("termino-reporte: {}", "TOTAL");
    }
}
