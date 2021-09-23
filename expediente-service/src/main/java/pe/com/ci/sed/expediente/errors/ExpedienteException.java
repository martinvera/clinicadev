package pe.com.ci.sed.expediente.errors;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import pe.com.ci.sed.expediente.model.request.RequestHeader;

@Getter
@Setter
public class ExpedienteException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private HttpStatus status = HttpStatus.NOT_FOUND;

    public ExpedienteException(String mensaje) {
        super(mensaje);
    }

    public ExpedienteException(String mensaje, HttpStatus status) {
        super(mensaje);
        this.status = status;
    }

    public ExpedienteException(String message, HttpStatus conflict, RequestHeader header) {
        this(message, conflict);
    }
}
