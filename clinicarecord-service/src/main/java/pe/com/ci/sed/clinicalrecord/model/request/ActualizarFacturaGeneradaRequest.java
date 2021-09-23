package pe.com.ci.sed.clinicalrecord.model.request;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Data
@Validated
public class ActualizarFacturaGeneradaRequest {

	@NotEmpty
	private String nroLote;
	@NotEmpty
	private String garanteId;
	private String[] facturaNro;
	private String urlArchivoZipLote;
}
