package pe.com.ci.sed.integrator.util;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.springframework.http.HttpStatus;

import pe.com.ci.sed.integrator.model.request.RequestHeader;
import pe.com.ci.sed.integrator.model.response.GenericResponse;
import pe.com.ci.sed.integrator.model.response.ResultSet;

public class GenericUtil {

    public static <T> GenericResponse<ResultSet<T>> getResult(T response, RequestHeader header) {
        return new GenericResponse<>(header, new ResultSet<>(response));
    }


    public static <T> GenericResponse<ResultSet<T>> getResultNotFound(T response, RequestHeader header) {
        return new GenericResponse<>(header, new ResultSet<>(response, HttpStatus.NOT_FOUND));
    }

    public static RequestHeader getHeader(Principal principal, String applicationId) {
        return RequestHeader.builder().userId(principal.getName()).applicationId(applicationId).transactionId(UUID.randomUUID().toString()).build();
    }
    
    public static Date toDate(String strDate, String dateFormat) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());
		return formatter.parse(strDate);
	}
}
