package pe.com.ci.sed.clinicalrecord.model.generic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ResultSet<T> {
    private Integer code;
    private T data;

    @JsonInclude(Include.NON_NULL)
    private Paginacion paginacion;


    public ResultSet(T data, Paginacion paginacion) {
        this.code = HttpStatus.OK.value();
        this.data = data;
        this.paginacion = paginacion;
    }

    public ResultSet(T data) {
        this(data, null);
    }

    public ResultSet(T data, Paginacion paginacion, HttpStatus httpStatus) {
        this(data, paginacion);
        this.code = httpStatus.value();
    }

}
