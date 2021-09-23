package pe.com.ci.sed.integrator.model.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

@Validated
public class RegistrarFacturaFromIafaRequest extends RegistrarFacturaGeneric {
	@NotNull
	@JsonProperty("CO_CENTRO")
	@NotEmpty(message = "No existe codigo de centro")
	private String coCentro;
		
}
