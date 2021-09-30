package pe.com.ci.sed.integrator.service;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static pe.com.ci.sed.integrator.util.Constants.PARAM_CO_RESPUESTA;
import static pe.com.ci.sed.integrator.util.Constants.PARAM_DE_RESPUESTA;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.azure.core.http.rest.Response;
import com.azure.core.util.Context;
import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.models.QueueStorageException;
import com.azure.storage.queue.models.SendMessageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import pe.com.ci.sed.integrator.errors.IntegratorException;
import pe.com.ci.sed.integrator.model.request.*;
import pe.com.ci.sed.integrator.model.response.EiResponse;
import pe.com.ci.sed.integrator.model.response.GenericResponse;
import pe.com.ci.sed.integrator.util.Constants;
import pe.com.ci.sed.integrator.validator.CDValidator;
import pe.com.ci.sed.integrator.validator.IAFASValidator;

@Log4j2
@Service
@RequiredArgsConstructor
public class QueueServiceImpl {
    private final Validator validator;
    private static final ObjectMapper mapper = new ObjectMapper();

    private final QueueClient queueClientDocument;
    private final QueueClient queueClientClinicalRecord;
    private final StorageService storageService;

    private enum origenSistema {UNILAB, SALESFORCE, IAFAS, CONTROLDOCUMENTARIO, ENTERPRISEIMAGING}

    public GenericResponse<Object> encolarDocumentoUnilab(RegistrarDocFromUnilabRequest request) {
        try {
            Map<String, Object> response = new HashMap<>();
            String messageError = getValidations(request, null);
            if (Strings.isEmpty(messageError)) {
                log.info("Inicio de envío del documento de UNILAB hacia la cola documentoqueuestorage");
                GDocument genericDocument = new GDocument();

                if (request.getRequest().getDetalle().get(0).getCoPrueba().startsWith("APAT")) {
                    request.getRequest().getCabecera().setTipoDocumentoId("DCA029");
                    request.getRequest().getCabecera().setTipoDocumentoDesc("Resultado de Patología".toUpperCase());
                } else {
                    request.getRequest().getCabecera().setTipoDocumentoId("DCA028");
                    request.getRequest().getCabecera().setTipoDocumentoDesc("Resultado de Laboratorio".toUpperCase());
                }

                String url = storageService.uploadUnilab(mapper.writeValueAsBytes(request), origenSistema.UNILAB.name(), request.getRequest().getCabecera().getNuEncuentro());
                genericDocument.setSistemaOrigen(origenSistema.UNILAB.name());
                genericDocument.setContenido(url);

                Response<SendMessageResult> result = queueClientDocument.sendMessageWithResponse(mapper.writeValueAsString(genericDocument), null, Duration.ofSeconds(-1), null, Context.NONE);
                if (result.getStatusCode() == HttpStatus.CREATED.value()) {
                    response.put(PARAM_CO_RESPUESTA, "0");
                    response.put(PARAM_DE_RESPUESTA, "OK");
                    log.info("Mensaje de unilab encolado con exito, messageId {}", result.getValue().getMessageId());
                    return GenericResponse.builder().header(request.getHeader()).response(response).build();
                }
            } else {
                response.put(PARAM_CO_RESPUESTA, "-1");
                response.put(PARAM_DE_RESPUESTA, messageError);
                return GenericResponse.builder().header(request.getHeader()).response(response).build();
            }

            throw new IntegratorException("Ocurrio un error al enviar el mensaje la cola documentqueuestorage", INTERNAL_SERVER_ERROR);
        } catch (JsonProcessingException | QueueStorageException e) {
            log.error(Constants.ERROR_EN, queueClientDocument.getQueueName(), e);
            throw new IntegratorException(e.getMessage(), INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error(Constants.ERROR_INESPERADO, e.getMessage());
            throw new IntegratorException(e.getMessage(), INTERNAL_SERVER_ERROR);
        }

    }

    public GenericResponse<Object> encolarFacturaFromIafas(List<RegistrarFacturaFromIafaRequest> request) {
        log.info("Inicio de envío de factura de IAFA hacia la cola clinicalrecordqueuestorage");
        String messageError = getValidations(request, IAFASValidator.class);
        if (Strings.isBlank(messageError))
            return this.encolarFactura(request, origenSistema.IAFAS);
        else {
            return getObjectGenericResponse(messageError);
        }
    }

    public GenericResponse<Object> encolarFacturaFromCd(List<RegistrarFacturaFromCdRequest> request) {
        log.info("Inicio de envío de factura de Control Documentario hacia la cola clinicalrecordqueuestorage");

        String messageError = getValidations(request, CDValidator.class);
        if (Strings.isBlank(messageError))
            return this.encolarFactura(request, origenSistema.CONTROLDOCUMENTARIO);
        else {
            return getObjectGenericResponse(messageError);
        }
    }

    /**
     * Método que procesa la trama que se envía desde el cliente con la ruta del File Server
     * Luego de realizar las validaciones envía la trama a la cola <documentqueuestorage>
     * para luego ser procesado por el microservicio <document-service>
     */
    public ResponseEntity<EiResponse> encolarFacturaFromEi(List<EnterpriseIRequest> request) {
        Map<String, String> header = new HashMap<>();
        header.put("TRANSACTIONID", ThreadContext.get(Constants.TRANSACTION_ID));
        header.put("APPLICATIONID", "EI");
        header.put("USERID", "EI");

        Map<String, String> response = new HashMap<>();
        response.put(Constants.PARAM_STATUS, "200");
        request.forEach(encuentro -> {
            try {
                String messageError = getValidations(request, null);
                if (Strings.isEmpty(messageError)) {
                    log.info("Inicio de envío del documento de ENTERPRISEIMAGING hacia la cola {}", queueClientDocument.getQueueName());
                    String url = storageService.uploadUnilab(mapper.writeValueAsBytes(encuentro), origenSistema.ENTERPRISEIMAGING.name(), encuentro.getNuEncuentro());
                    GDocument genericDocument = GDocument.builder()
                            .contenido(url)
                            .sistemaOrigen(origenSistema.ENTERPRISEIMAGING.name())
                            .build();
                    Response<SendMessageResult> result = queueClientDocument.sendMessageWithResponse(mapper.writeValueAsString(genericDocument), null, Duration.ofSeconds(-1), null, Context.NONE);
                    if (result.getStatusCode() == HttpStatus.CREATED.value()) {
                        response.put("CO_RESPUESTA", "0");
                        response.put("DE_RESPUESTA", "OK");
                        log.info("Mensaje de Enterprise Imaging encolado con exito, messageId {}", result.getValue().getMessageId());
                    }
                } else {
                    response.put("CO_RESPUESTA", "-1");
                    response.put("DE_RESPUESTA", messageError);
                    response.put(Constants.PARAM_STATUS, "500");
                }
            } catch (JsonProcessingException | QueueStorageException e) {
                log.error(Constants.ERROR_EN, queueClientDocument.getQueueName(), e.getMessage());
                throw new IntegratorException(e.getMessage(), INTERNAL_SERVER_ERROR);
            } catch (Exception e) {
                log.error(Constants.ERROR_INESPERADO, e.getMessage());
                throw new IntegratorException(e.getMessage(), INTERNAL_SERVER_ERROR);
            }
        });
        return ResponseEntity
                .status(Objects.requireNonNull(HttpStatus.resolve(Integer.parseInt(response.get(Constants.PARAM_STATUS)))))
                .body(EiResponse.builder()
                        .header(header)
                        .response(response)
                        .build());
    }

    private GenericResponse<Object> getObjectGenericResponse(String messageError) {
        Map<String, Object> response = new HashMap<>();
        response.put(PARAM_CO_RESPUESTA, "-1");
        response.put(PARAM_DE_RESPUESTA, messageError);
        return GenericResponse.builder().header(RequestHeader.builder()
                        .userId("INTEGRATOR_EXP")
                        .applicationId("EXPEDIENTEDIGITAL")
                        .transactionId(ThreadContext.get(Constants.TRANSACTION_ID)).build())
                .response(response).build();
    }

    public <T extends RegistrarFacturaGeneric> GenericResponse<Object> encolarFactura(List<T> request, QueueServiceImpl.origenSistema origenSistema) {


        StringBuilder sb = new StringBuilder();
        Map<String, Object> response = new HashMap<>();

        request.forEach(p -> {
                    try {

                        String messageError = getValidations(p, null);
                        if (Strings.isEmpty(messageError)) {
                            String url = storageService.upload(mapper.writeValueAsBytes(p), origenSistema.name(), p.getNuLote(), p.getNuDocPago());
                            GDocument genericDocument = GDocument.builder()
                                    .sistemaOrigen(origenSistema.name())
                                    .contenido(url).build();
                            Response<SendMessageResult> result = queueClientClinicalRecord.sendMessageWithResponse(mapper.writeValueAsString(genericDocument), null, Duration.ofSeconds(-1), null, Context.NONE);

                            if (result.getStatusCode() == HttpStatus.CREATED.value()) {
                                log.info("La factura fue encolada con exito, numero factura = {} , numero de lote = {}, messageId = {}", p.getNuDocPago(), p.getNuLote(), result.getValue().getMessageId());
                                response.put(PARAM_CO_RESPUESTA, "0");
                                response.put(PARAM_DE_RESPUESTA, "OK");
                            } else {
                                log.info("Error con la factura numero factura = {} , numero de lote = {}", p.getNuDocPago(), p.getNuLote());
                                sb.append("Factura numero ").append(p.getNuDocPago()).append("del lote ").append(p.getNuLote()).append("\n").append("No se pudo encolar la factura");
                                response.put(PARAM_CO_RESPUESTA, "-1");
                                response.put(PARAM_DE_RESPUESTA, sb.toString());
                            }
                        } else {
                            log.info("Error con la factura numero factura = {} , numero de lote = {} | mensaje error = {}", p.getNuDocPago(), p.getNuLote(), messageError);
                            sb.append("Factura número ").append(p.getNuDocPago()).append("del lote ").append(p.getNuLote()).append("\n").append(messageError);
                            response.put(PARAM_CO_RESPUESTA, "-1");
                            response.put(PARAM_DE_RESPUESTA, sb.toString());
                        }

                    } catch (JsonProcessingException e) {
                        log.error("Error : {} ", e.getMessage());
                        throw new IntegratorException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                    } catch (QueueStorageException e) {
                        log.error(Constants.ERROR_EN, queueClientClinicalRecord.getQueueName(), e);
                        throw new IntegratorException(e.getMessage(), INTERNAL_SERVER_ERROR);
                    } catch (Exception e) {
                        log.error(Constants.ERROR_INESPERADO, e.getMessage());
                        throw new IntegratorException(e.getMessage(), INTERNAL_SERVER_ERROR);
                    }
                }
        );

        return GenericResponse.builder().header(RequestHeader.builder()
                        .userId("INTEGRATOR_EXP")
                        .applicationId("EXPEDIENTEDIGITAL")
                        .transactionId(ThreadContext.get(Constants.TRANSACTION_ID)).build())
                .response(response).build();
    }

    private <E, T> String getValidations(E object, Class<T> clas) {
        Set<ConstraintViolation<E>> violations = Objects.nonNull(clas) ? validator.validate(object, clas) : validator.validate(object);
        return violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(" \n "));
    }
}
