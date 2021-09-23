package pe.com.ci.sed.reporte.errors;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ReporteException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private HttpStatus status = HttpStatus.NOT_FOUND;

    public ReporteException(String mensaje) {
        super(mensaje);
    }

    public ReporteException(String mensaje, HttpStatus status) {
        super(mensaje);
        this.status = status;
    }
}
