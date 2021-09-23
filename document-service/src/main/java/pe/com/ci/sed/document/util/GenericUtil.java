package pe.com.ci.sed.document.util;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import pe.com.ci.sed.document.model.generic.GenericResponse;
import pe.com.ci.sed.document.model.generic.Paginacion;
import pe.com.ci.sed.document.model.generic.RequestHeader;
import pe.com.ci.sed.document.model.generic.ResultSet;
import pe.com.ci.sed.document.persistence.entity.Archivo;


public class GenericUtil {

    public static final String DEFAULT_TAMANIO = "20";
    public static final String DEFAULT_INICIAL = "1";
    public static final ObjectMapper mapper = new ObjectMapper();

    public static <T> GenericResponse<ResultSet<T>> getResult(T response, RequestHeader header) {
        return new GenericResponse<>(header, new ResultSet<>(response));
    }

    public static <T> GenericResponse<ResultSet<T>> getResultPaginacion(T response, Paginacion paginacion, RequestHeader header) {
        return new GenericResponse<>(header, new ResultSet<>(response, paginacion));
    }

    public static <T> GenericResponse<ResultSet<T>> getResultNotFound(T response, RequestHeader header) {
        return new GenericResponse<>(header, new ResultSet<>(response, null, HttpStatus.NOT_FOUND));
    }

    public static RequestHeader getHeader(Principal principal, String applicationId) {
        String username = Strings.EMPTY;
        if (Objects.nonNull(principal)) username = principal.getName();
        return RequestHeader.builder().userId(username).applicationId(applicationId).transactionId(ThreadContext.get("transactionId")).build();
    }

    public static Date toDate(String strDate, String dateFormat) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());
        return formatter.parse(strDate);
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

    public static List<Archivo> mergeList(List<Archivo> source, List<Archivo> target) {

        Map<String, Archivo> mapArchivosActuales = target.stream().filter(distinctByKey(Archivo::getTipoDocumentoId)).collect(Collectors.toMap(Archivo::getTipoDocumentoId, item -> item));

        source.forEach(archivoSrc -> mapArchivosActuales.put(archivoSrc.getTipoDocumentoId(), archivoSrc));
        return new ArrayList<>(mapArchivosActuales.values());
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static String writeValueAsString(Object object) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        String[] ignorableFieldNames = {"pdfAnexoDetallado", "pdfHojaAutorizacion","resultado_pdf","resultadoPdf"};
        FilterProvider filters = new SimpleFilterProvider().addFilter("filter properties by name", SimpleBeanPropertyFilter.serializeAllExcept(ignorableFieldNames));
        mapper.setFilterProvider(filters);
        return mapper.writeValueAsString(object);
    }

    public static <T> List<T> remove(List<T> list, Predicate<T> predicate) {
        return list.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
}
