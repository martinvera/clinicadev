package pe.com.ci.sed.expediente.utils;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.storage.ResultContinuation;
import com.microsoft.azure.storage.ResultContinuationType;
import com.microsoft.azure.storage.ResultSegment;

import pe.com.ci.sed.expediente.model.request.RequestHeader;
import pe.com.ci.sed.expediente.model.response.GenericResponse;
import pe.com.ci.sed.expediente.model.response.Paginacion;
import pe.com.ci.sed.expediente.model.response.ResultSet;

public class GenericUtil {
	public static final String AZURE_STORAGE_ACCOUNT_URI = "https://%s.blob.core.windows.net";
	public static ObjectMapper mapper = new ObjectMapper();
    public static final Integer DEFAULT_TAMANIO = 20;
    public static final Integer DEFAULT_INICIAL = 1;

    public static final String ZONE_ID = "America/Lima";
	public static ZoneId zoneId = ZoneId.of(ZONE_ID);
	
	public static long getTimestamp() {
		return ZonedDateTime.now(zoneId).toInstant().toEpochMilli();
	}
	
	public static Date getDateWithZone(String strFecha) {
		LocalDate date = LocalDate.parse(strFecha);
		return Date.from( date.atStartOfDay(GenericUtil.zoneId ).toInstant());
	}
	
	public static Date getDateWithZone() {
		LocalDate date = LocalDate.now();
		return Date.from( date.atStartOfDay(GenericUtil.zoneId ).toInstant());
	}

    public static <T> GenericResponse<ResultSet<T>> getResult(RequestHeader header, T response) {
        return new GenericResponse<>(header, new ResultSet<>(response));
    }

    public static <T> GenericResponse<ResultSet<List<T>>> getResultSetPaginacion(RequestHeader header, ResultSegment<T> result) {
        Paginacion pagination = getToken(result.getContinuationToken(), result.getPageSize(), result);
        return GenericUtil.getResultPaginacion(header, result.getResults(), pagination);
    }
    
    public static <T> GenericResponse<ResultSet<T>> getResultPaginacion(RequestHeader header, T response, Paginacion paginacion) {
        return new GenericResponse<>(header, new ResultSet<>(response, paginacion));
    }

    public static <T> GenericResponse<ResultSet<T>> getResultNotFound(T response, RequestHeader header) {
        return new GenericResponse<>(header, new ResultSet<>(response, null, HttpStatus.NOT_FOUND));
    }

    public static RequestHeader getHeader(Principal principal, String applicationId) {
        String username = Strings.EMPTY;
        if (Objects.nonNull(principal)) username = principal.getName();
        return RequestHeader.builder().userId(username).applicationId(applicationId).transactionId(UUID.randomUUID().toString()).build();
    }

    public static <T> Paginacion getToken(ResultContinuation continuationToken2, Integer pageSize, ResultSegment<T> result) {
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

    public static <T> ResultContinuation getContinuationToken(Paginacion param) {
        ResultContinuation token;
        if (param == null) {
            return null;
        } else {
            token = new ResultContinuation();
            token.setContinuationType(ResultContinuationType.TABLE);
        }

        if (Strings.isNotBlank(param.getSiguiente())) {
            token.setNextPartitionKey(param.getSiguiente());
        }

        if (Strings.isNotBlank(param.getAtras())) {
            token.setNextRowKey(param.getAtras());
        }

        return token;
    }


    public static <T> T obtenerPorObjetoPOST(RestTemplate template, RequestHeader headers, String url, Object request, Class<T> claseRespuesta) {
        Map<String, Object> restRequest = new HashMap<>();
        restRequest.put("header", headers);
        restRequest.put("request", request);

        HttpHeaders restHeaders = new HttpHeaders();
        restHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> entity = new HttpEntity<>(restRequest, restHeaders);
        return template.exchange(url, HttpMethod.POST, entity, claseRespuesta).getBody();

    }
}
