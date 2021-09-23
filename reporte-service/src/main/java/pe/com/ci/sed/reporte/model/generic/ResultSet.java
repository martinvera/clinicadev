package pe.com.ci.sed.reporte.model.generic;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ResultSet<T> {
    private Integer code;
    private T data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Paginacion paginacion;

    public ResultSet(T data) {
        this.code = HttpStatus.OK.value();
        this.data = data;
    }

    public ResultSet(T data, Paginacion paginacion) {
        this(data);
        this.paginacion = paginacion;
    }

    public ResultSet(T data, Paginacion paginacion, HttpStatus httpStatus) {
        this(data, paginacion);
        this.code = httpStatus.value();
    }
}
