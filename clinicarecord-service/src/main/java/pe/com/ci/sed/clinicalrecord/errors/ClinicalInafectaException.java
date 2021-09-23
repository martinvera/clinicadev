package pe.com.ci.sed.clinicalrecord.errors;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClinicalInafectaException extends RuntimeException {

	private static final long serialVersionUID = 1L;

    public ClinicalInafectaException(String mensaje) {
        super(mensaje);
    }

}