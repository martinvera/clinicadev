package pe.com.ci.sed.expediente.service.impl;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static pe.com.ci.sed.expediente.utils.GenericUtil.getResult;

import java.time.Duration;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.azure.core.http.rest.Response;
import com.azure.core.util.Context;
import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.models.SendMessageResult;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import pe.com.ci.sed.expediente.errors.ExpedienteException;
import pe.com.ci.sed.expediente.handler.ExpedienteHandler;
import pe.com.ci.sed.expediente.model.request.GenerarExpedienteRequest;
import pe.com.ci.sed.expediente.model.request.GenericRequest;
import pe.com.ci.sed.expediente.persistence.entity.ExpedienteDigitalTable;
import pe.com.ci.sed.expediente.service.ExpedienteService;
import pe.com.ci.sed.expediente.service.GenerarExpedienteService;
import pe.com.ci.sed.expediente.utils.GenericUtil;

@Log4j2
@Service
@AllArgsConstructor
public class GenerarExpedienteServiceImpl implements GenerarExpedienteService {
    private final QueueClient queueClient;
    private final ExpedienteService expedienteService;

    @Override
    public Object iniciarProcesoExpediente(GenericRequest<List<GenerarExpedienteRequest>> request) {
        try {
        	request.getRequest().forEach(factura -> {
            	ExpedienteDigitalTable expedienteDigital = expedienteService.registrarExpediente(this.buildExpediente(factura, request.getHeader().getUserId()));
                log.debug("Expediente digital registrado para la factura {} en estado {}", factura.getFacturaNro() , expedienteDigital.getEstado());
            });

            Response<SendMessageResult> result = queueClient.sendMessageWithResponse(GenericUtil.mapper.writeValueAsString(request.getRequest()), null, Duration.ofSeconds(-1), null, Context.NONE);
            if (result.getStatusCode() == HttpStatus.CREATED.value()) {
                String mensaje = "Enviado a cola correctamente";
                return getResult(request.getHeader(), mensaje);
            }
            throw new ExpedienteException("Ocurrió un error al enviar la cola", INTERNAL_SERVER_ERROR);
        } catch (JsonProcessingException e) {
            throw new ExpedienteException(e.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }

    private ExpedienteDigitalTable buildExpediente(GenerarExpedienteRequest factura, String userId) {
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
                .nroEncuentro(factura.getNroEncuentro())
                .fechaAtencion(factura.getFechaAtencion())
                .creadoPor(userId)
                .estado(ExpedienteHandler.ESTADO.PENDIENTE.name())
                .build();
    }

}
