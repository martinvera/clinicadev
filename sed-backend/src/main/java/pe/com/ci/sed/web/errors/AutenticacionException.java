package pe.com.ci.sed.web.errors;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import pe.com.ci.sed.web.model.generic.HeaderRequest;

@Getter
@Setter
public class AutenticacionException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HttpStatus status = HttpStatus.NOT_FOUND;
    private HeaderRequest header;

    public AutenticacionException(String mensaje) {
        super(mensaje);
    }

    public AutenticacionException(String mensaje, HttpStatus status) {
        super(mensaje);
        this.status = status;
    }

    public AutenticacionException(String message, HttpStatus conflict, HeaderRequest header) {
        this(message, conflict);
        this.header = header;
    }
}
