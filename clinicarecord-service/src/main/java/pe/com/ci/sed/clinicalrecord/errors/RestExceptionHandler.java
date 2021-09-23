package pe.com.ci.sed.clinicalrecord.errors;

import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import pe.com.ci.sed.clinicalrecord.model.generic.HeaderRequest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static pe.com.ci.sed.clinicalrecord.utils.GenericUtil.getHeader;

import java.util.*;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private final ApplicationContext context;

    public RestExceptionHandler(ApplicationContext context) {
        this.context = context;
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String mensaje = ex.getParameterName() + " parameter is missing";
        Error error = new Error(BAD_REQUEST, mensaje);
        return buildResponseEntity(getBody(error, getHeaders(request)), error.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
        Error error = new Error(HttpStatus.UNSUPPORTED_MEDIA_TYPE, builder.substring(0, builder.length() - 2));
        return buildResponseEntity(getBody(error, getHeaders(request)), error.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Error error = new Error(BAD_REQUEST);
        error.setMensaje("Debe completar los campos requeridos");
        List<Error.ValidationError> list = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> {
            list.add(new Error.ValidationError(err.getField(), err.getDefaultMessage()));
        });
        error.setErrors(list);

        return buildResponseEntity(getBody(error, getHeaders(request)), error.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        log.info("{} to {}", servletWebRequest.getHttpMethod(), servletWebRequest.getRequest().getServletPath());
        String mensaje = "fomato JSON de request está mal formado";
        log.error("", ex);
        Error error = new Error(HttpStatus.BAD_REQUEST, mensaje);
        return buildResponseEntity(getBody(error, getHeaders(request)), error.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String mensaje = "Error writing JSON output";
        Error error = new Error(HttpStatus.INTERNAL_SERVER_ERROR, mensaje);
        return buildResponseEntity(getBody(error, getHeaders(request)), error.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Error error = new Error(BAD_REQUEST);
        error.setMensaje(String.format("Could not find the %s method for URL %s", ex.getHttpMethod(), ex.getRequestURL()));
        return buildResponseEntity(getBody(error, getHeaders(request)), error.getStatus());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        Error error = new Error(BAD_REQUEST);

        if(Objects.nonNull(ex.getRequiredType())){
            error.setMensaje(String.format("The parameter '%s' of value '%s' could not be converted to type '%s'", ex.getName(), ex.getValue(),ex.getRequiredType().getSimpleName()));
        }
        return buildResponseEntity(getBody(error, getHeaders(request)), error.getStatus());
    }

    @ExceptionHandler(ClinicalRecordException.class)
    protected ResponseEntity<Object> handleClinicalRecordException(ClinicalRecordException ex, WebRequest request) {
        Error error = new Error(ex.getStatus());
        error.setMensaje(ex.getMessage());
        return buildResponseEntity(getBody(error, getHeaders(request)), error.getStatus());
    }
    
    private ResponseEntity<Object> buildResponseEntity(Map<String, Object> body, HttpStatus status) {
        return new ResponseEntity<>(body, status);
    }

    private Map<String, Object> getBody(Error error, Object header) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", error);
        body.put("header", header);
        return body;
    }

    private HeaderRequest getHeaders(WebRequest request) {
        return getHeader(request.getUserPrincipal(), context.getId());
    }

}
