package pe.com.ci.sed.integrator.errors;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class IntegratorException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HttpStatus status = HttpStatus.NOT_FOUND;

    public IntegratorException(String mensaje) {
        super(mensaje);
    }

    public IntegratorException(String mensaje, HttpStatus status) {
        super(mensaje);
        this.status = status;
    }
}
