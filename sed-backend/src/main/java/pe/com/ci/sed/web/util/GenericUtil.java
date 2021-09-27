package pe.com.ci.sed.web.util;

import org.springframework.http.HttpStatus;
import pe.com.ci.sed.web.model.generic.HeaderRequest;
import pe.com.ci.sed.web.model.generic.GenericResponse;
import pe.com.ci.sed.web.model.generic.Paginacion;
import pe.com.ci.sed.web.model.generic.ResultSet;

import java.security.Principal;
import java.util.UUID;

public class GenericUtil {

    public static <T> GenericResponse<ResultSet<T>> getResult(HeaderRequest header, T response) {
        return new GenericResponse<>(header, new ResultSet<>(response));
    }

    public static <T> GenericResponse<ResultSet<T>> getResultPaginacion(HeaderRequest header, T response, Paginacion paginacion) {
        return new GenericResponse<>(header, new ResultSet<>(response, paginacion));
    }

    public static <T> GenericResponse<ResultSet<T>> getResultNotFound(HeaderRequest header, T response) {
        return new GenericResponse<>(header, new ResultSet<>(response, null, HttpStatus.NOT_FOUND));
    }

    public static HeaderRequest getHeader(Principal principal, String applicationId) {
        return HeaderRequest.builder().userId(principal.getName()).applicationId(applicationId).transactionId(UUID.randomUUID().toString()).build();
    }
}
