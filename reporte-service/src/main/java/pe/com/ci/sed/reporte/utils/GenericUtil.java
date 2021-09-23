package pe.com.ci.sed.reporte.utils;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import pe.com.ci.sed.reporte.errors.ReporteException;
import pe.com.ci.sed.reporte.model.generic.GenericRequest;
import pe.com.ci.sed.reporte.model.generic.GenericResponse;
import pe.com.ci.sed.reporte.model.generic.RequestHeader;
import pe.com.ci.sed.reporte.model.generic.ResultSet;
import pe.com.ci.sed.reporte.model.request.BusquedaLotes;
import pe.com.ci.sed.reporte.model.request.RequestReporte;
import pe.com.ci.sed.reporte.utils.Constants.TipoGarante;

public class GenericUtil {
    public static final int DEFAULT_TAMANIO = 20;
    public static final int DEFAULT_INICIAL = 1;
    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> GenericResponse<ResultSet<T>> getResult(RequestHeader header, T response) {
        return new GenericResponse<>(header, new ResultSet<>(response));
    }

    public static <T> GenericResponse<ResultSet<T>> getResultNotFound(T response, RequestHeader header) {
        return new GenericResponse<>(header, new ResultSet<>(response, null, HttpStatus.NOT_FOUND));
    }

    public static RequestHeader getHeader(Principal principal, String applicationId) {
        String username = Strings.EMPTY;
        if (Objects.nonNull(principal)) username = principal.getName();
        return RequestHeader.builder().userId(username).applicationId(applicationId).transactionId(ThreadContext.get("transactionId")).build();
    }

    public static <T> List<T> obtenerPorObjetoPOST(RestTemplate template, String url, GenericRequest<RequestReporte> request, Class<T> claseRespuesta) {

        HttpHeaders restHeaders = new HttpHeaders();
        restHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> entity = new HttpEntity<>(request, restHeaders);
        try {
            return template.exchange(url, HttpMethod.POST, entity, new ParameterizedTypeReference<List<T>>() {
            }).getBody();
        } catch (HttpClientErrorException e) {
            throw new ReporteException(e.getMessage(), e.getStatusCode());
        }
    }

    public static <T> List<T> obtenerPorObjetoPOST2(RestTemplate template, String url, GenericRequest<BusquedaLotes> request, Class<T> claseRespuesta) {

        HttpHeaders restHeaders = new HttpHeaders();
        restHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> entity = new HttpEntity<>(request, restHeaders);
        try {
            return template.exchange(url, HttpMethod.POST, entity, new ParameterizedTypeReference<List<T>>() {
            }).getBody();
        } catch (HttpClientErrorException e) {
            throw new ReporteException(e.getMessage(), e.getStatusCode());
        }
    }


    public static String obtenerNombreReporteTotal(String garante) {
        switch (garante.trim()) {
            case TipoGarante.RIMAC_EPS:
                return garante;
            case TipoGarante.RIMAC_SEGUROS:
                return garante;
            default:
                return TipoGarante.OTROS_GARANTES;
        }
    }
}
