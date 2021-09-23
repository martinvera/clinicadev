package pe.com.ci.sed.integrator.service;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
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
import pe.com.ci.sed.integrator.model.request.GenericDocument;
import pe.com.ci.sed.integrator.model.request.RegistrarDocFromUnilabRequest;
import pe.com.ci.sed.integrator.model.request.RegistrarFacturaFromCdRequest;
import pe.com.ci.sed.integrator.model.request.RegistrarFacturaFromIafaRequest;
import pe.com.ci.sed.integrator.model.request.RegistrarFacturaGeneric;
import pe.com.ci.sed.integrator.model.request.RequestHeader;
import pe.com.ci.sed.integrator.model.response.GenericResponse;

@Log4j2
@Service
@RequiredArgsConstructor
public class QueueServiceImpl {
	private final Validator validator;
    private static final ObjectMapper mapper = new ObjectMapper();

    private final QueueClient queueClientDocument;
    private final QueueClient queueClientClinicalRecord;
    private final StorageService storageService;

    private enum origenSistema {UNILAB, SALESFORCE, IAFAS, CONTROLDOCUMENTARIO}

    public GenericResponse<Object> encolarDocumentoUnilab(RegistrarDocFromUnilabRequest request) {
        try {
        	Map<String, Object> response = new HashMap<>();
        	String messageError = getValidations(request);
            if (Strings.isEmpty(messageError)) {
            	log.info("Inicio de envío del documento de UNILAB hacia la cola documentoqueuestorage");
                GenericDocument<String> genericDocument = new GenericDocument<>();

                if (request.getRequest().getDetalle()[0].getCoPrueba().startsWith("APAT")){
                	request.getRequest().getCabecera().setTipoDocumentoId("DCA029");
                	request.getRequest().getCabecera().setTipoDocumentoDesc("Resultado de Patología".toUpperCase());
                }else {
                	request.getRequest().getCabecera().setTipoDocumentoId("DCA028");
                	request.getRequest().getCabecera().setTipoDocumentoDesc("Resultado de Laboratorio".toUpperCase());
                }

                String url = storageService.uploadUnilab(mapper.writeValueAsBytes(request), origenSistema.UNILAB.name() , request.getRequest().getCabecera().getNuEncuentro() );
                genericDocument.setSistemaOrigen(origenSistema.UNILAB.name());
                genericDocument.setContenido(url);
                
                Response<SendMessageResult> result = queueClientDocument.sendMessageWithResponse(mapper.writeValueAsString(genericDocument), null, Duration.ofSeconds(-1), null, Context.NONE);
                if (result.getStatusCode() == HttpStatus.CREATED.value()) {
                    response.put("co_respuesta", "0");
                    response.put("de_respuesta", "OK");
                    log.info("Mensaje de unilab encolado con exito, messageId {}", result.getValue().getMessageId());
                    return GenericResponse.builder().header(request.getHeader()).response(response).build();
                }
            }else {
            	 response.put("co_respuesta", "-1");
                 response.put("de_respuesta", messageError);
                 return GenericResponse.builder().header(request.getHeader()).response(response).build();
            }
        	
            throw new IntegratorException("Ocurrio un error al enviar el mensaje la cola documentqueuestorage", INTERNAL_SERVER_ERROR);
        } catch (JsonProcessingException e) {
            log.error("Error en encolarDocumentoUnilab : {} ", e);
            throw new IntegratorException(e.getMessage(), INTERNAL_SERVER_ERROR);
        } catch (QueueStorageException e) {
            log.error("Error en documentqueuestorage : {} ", e);
            throw new IntegratorException(e.getMessage(), INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Error inesperado : {} ", e);
            throw new IntegratorException(e.getMessage(), INTERNAL_SERVER_ERROR);
        }

    }

    public GenericResponse<Object> encolarFacturaFromIafas(List<RegistrarFacturaFromIafaRequest> request) {
        log.info("Inicio de envío de factura de IAFA hacia la cola clinicalrecordqueuestorage");
        return this.encolarFactura(request, origenSistema.IAFAS);
    }
    
    public GenericResponse<Object> encolarFacturaFromCd(List<RegistrarFacturaFromCdRequest> request) {
        log.info("Inicio de envío de factura de Control Documentario hacia la cola clinicalrecordqueuestorage");
        return this.encolarFactura(request, origenSistema.CONTROLDOCUMENTARIO);
    }
    
    public <T extends RegistrarFacturaGeneric> GenericResponse<Object> encolarFactura(List<T> request,QueueServiceImpl.origenSistema origenSistema) {
        
        GenericDocument<String> genericDocument = new GenericDocument<>();

        genericDocument.setSistemaOrigen(origenSistema.name());
        
        StringBuilder sb = new StringBuilder();
        Map<String, Object> response = new HashMap<>();
        
        request.forEach(p -> {
                try {
                	
                	String messageError = getValidations(p);
                    if (Strings.isEmpty(messageError)) {
                    	String url = storageService.upload(mapper.writeValueAsBytes(p), origenSistema.name(), p.getNuLote(), p.getNuDocPago());
                        genericDocument.setContenido(url);
                        Response<SendMessageResult> result = queueClientClinicalRecord.sendMessageWithResponse(mapper.writeValueAsString(genericDocument), null, Duration.ofSeconds(-1), null, Context.NONE);
                        
                        if (result.getStatusCode() == HttpStatus.CREATED.value()) {
                            log.info("La factura fue encolada con exito, numero factura = {} , numero de lote = {}, messageId = {}", p.getNuDocPago(), p.getNuLote(), result.getValue().getMessageId());
                            response.put("co_respuesta", "0");
                            response.put("de_respuesta", "OK");
                        } else {
                            log.info("Error con la factura numero factura = {} , numero de lote = {}", p.getNuDocPago(), p.getNuLote());
                            sb.append("Factura numero ").append(p.getNuDocPago()).append("del lote ").append(p.getNuLote()).append("\n").append("No se pudo encolar la factura");
                            response.put("co_respuesta", "-1");
                            response.put("de_respuesta", sb.toString());
                        }
                    }else {
                    	log.info("Error con la factura numero factura = {} , numero de lote = {} | mensaje error = {}", p.getNuDocPago(), p.getNuLote(),messageError);
                    	sb.append("Factura número ").append(p.getNuDocPago()).append("del lote ").append(p.getNuLote()).append("\n").append(messageError);
                    	response.put("co_respuesta", "-1");
                        response.put("de_respuesta", sb.toString());
                    }
                    
                } catch (JsonProcessingException e) {
                	log.error("Error : {} ", e);
                    throw new IntegratorException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                } catch (QueueStorageException e) {
                    log.error("Error en clinicalrecordqueuestorage : {} ", e);
                    throw new IntegratorException(e.getMessage(), INTERNAL_SERVER_ERROR);
                } catch (Exception e) {
                    log.error("Error inesperado : {} ", e);
                    throw new IntegratorException(e.getMessage(), INTERNAL_SERVER_ERROR);
                }
            }
        );

        return GenericResponse.builder().header(RequestHeader.builder().userId("INTEGRATOR_EXP").applicationId("EXPEDIENTEDIGITAL").transactionId(ThreadContext.get("transactionId")).build()).response(response).build();
    }
    
    private <E> String getValidations(E object) {
        Set<ConstraintViolation<E>> violations = validator.validate(object);
        return violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(" \n "));
    }
}
