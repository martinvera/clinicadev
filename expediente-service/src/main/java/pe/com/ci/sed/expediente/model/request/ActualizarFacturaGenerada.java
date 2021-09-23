package pe.com.ci.sed.expediente.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActualizarFacturaGenerada {
	private String nroLote;
	private String garanteId;
	private String[] facturaNro;
	private String facturaNroAfecta;
	@JsonIgnore
	private String facturaGenerada;
	private String urlArchivoZipLote;
}
