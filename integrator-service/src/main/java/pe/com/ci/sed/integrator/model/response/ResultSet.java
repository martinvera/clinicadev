package pe.com.ci.sed.integrator.model.response;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class ResultSet<T> {
    private Integer code;
    private T data;

    public ResultSet(T data) {
        this.code = HttpStatus.OK.value();
        this.data = data;
    }

    public ResultSet(T data, HttpStatus httpStatus) {
    	this.data = data;
        this.code = httpStatus.value();
    }
}