package pe.com.ci.sed.document.errors;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class DocumentException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HttpStatus status = HttpStatus.NOT_FOUND;

    public DocumentException(String mensaje) {
        super(mensaje);
    }

    public DocumentException(String mensaje, HttpStatus status) {
        super(mensaje);
        this.status = status;
    }
}
