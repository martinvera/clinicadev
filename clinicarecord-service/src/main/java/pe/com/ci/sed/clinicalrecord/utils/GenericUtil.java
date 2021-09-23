package pe.com.ci.sed.clinicalrecord.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.util.Strings;

import com.microsoft.azure.storage.ResultContinuation;
import com.microsoft.azure.storage.ResultSegment;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import pe.com.ci.sed.clinicalrecord.errors.ClinicalRecordException;
import pe.com.ci.sed.clinicalrecord.model.generic.GenericRequest;
import pe.com.ci.sed.clinicalrecord.model.generic.GenericResponse;
import pe.com.ci.sed.clinicalrecord.model.generic.HeaderRequest;
import pe.com.ci.sed.clinicalrecord.model.generic.Paginacion;
import pe.com.ci.sed.clinicalrecord.model.response.ResultSet;

public class GenericUtil {

    private GenericUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static final int DEFAULT_TAMANIO = 20;
    public static final int DEFAULT_INICIAL = 1;


    public static final String DEFAULT_LOCALE = "es_PE";
    public static final String FORMATSLASH_DDMMYYYY = "dd/MM/yyyy";
    public static final String FORMAT_YYYYMMDD = "yyyy-MM-dd";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> String convertString(T object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ClinicalRecordException(e.getMessage());
        }
    }

    public static <T> T readValue(String v, TypeReference<T> clas) {
        try {
            return mapper.readValue(v, clas);
        } catch (JsonProcessingException e) {
            throw new ClinicalRecordException(e.getMessage());
        }
    }

    public static <T> T readValue(InputStream v, Class<T> clas) {
        try {
            return mapper.readValue(v, clas);
        } catch (IOException e) {
            throw new ClinicalRecordException(e.getMessage());
        }
    }

    public static String getDate(Date date, String format) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            return simpleDateFormat.format(date);
        } catch (Exception e) {
            return Strings.EMPTY;
        }
    }

    public static Paginacion paginacion(Integer actual, Integer total, Integer size) {
        int atras = actual - 1;
        Paginacion paginacion = new Paginacion();
        paginacion.setActual(actual);
        if (atras > 0) paginacion.setAtras(atras);
        if (total.equals(size)) paginacion.setSiguiente(actual + 1);
        paginacion.setTotalitems(total);
        return paginacion;
    }

    public static <T> GenericResponse<ResultSet<T>> getResult(HeaderRequest header, T response) {
        return new GenericResponse<>(header, new ResultSet<>(response));
    }

    public static <T> GenericResponse<ResultSet<List<T>>> getResultSetPaginacion(HeaderRequest header, ResultSegment<T> result) {
        Paginacion pagination = getToken(result.getContinuationToken(), result.getPageSize());
        return GenericUtil.getResultPaginacion(header, result.getResults(), pagination);
    }

    public static <T> GenericResponse<ResultSet<T>> getResultPaginacion(HeaderRequest header, T response, Paginacion paginacion) {
        return new GenericResponse<>(header, new ResultSet<>(response, paginacion));
    }

    public static HeaderRequest getHeader(Principal principal, String applicationId) {
        String username = Strings.EMPTY;
        if (Objects.nonNull(principal)) username = principal.getName();
        return HeaderRequest.builder().userId(username).applicationId(applicationId).transactionId(ThreadContext.get("transactionId")).build();
    }

    private static Paginacion getToken(ResultContinuation continuationToken2, Integer pageSize) {
        ResultContinuation continuationToken;
        Paginacion pagination = null;
        continuationToken = continuationToken2;
        if (continuationToken != null) {
            pagination = new Paginacion();
            pagination.setSiguiente(continuationToken.getNextPartitionKey());
            pagination.setAtras(continuationToken.getNextRowKey());
            pagination.setTotalitems(pageSize);
        }
        return pagination;
    }

    public static <T> ResponseEntity<String> getStringResponseEntity(RestTemplate template, String url, GenericRequest<T> request) {
        ResponseEntity<String> response;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<GenericRequest<T>> entity = new HttpEntity<>(request, headers);
            response = template.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (Exception ex) {
            throw new ClinicalRecordException(ex.getMessage());
        }
        return response;
    }


}
