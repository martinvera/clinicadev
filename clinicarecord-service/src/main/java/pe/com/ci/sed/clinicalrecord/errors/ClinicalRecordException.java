package pe.com.ci.sed.clinicalrecord.errors;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ClinicalRecordException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private HttpStatus status = HttpStatus.NOT_FOUND;

    public ClinicalRecordException(String mensaje) {
        super(mensaje);
    }

    public ClinicalRecordException(String mensaje, HttpStatus status) {
        super(mensaje);
        this.status = status;
    }
}