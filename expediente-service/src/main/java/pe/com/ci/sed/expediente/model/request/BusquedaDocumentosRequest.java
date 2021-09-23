package pe.com.ci.sed.expediente.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusquedaDocumentosRequest {
	private String nroLote;
    private String[] facturaNro;
    private Integer actual;
    private Integer size;
}
