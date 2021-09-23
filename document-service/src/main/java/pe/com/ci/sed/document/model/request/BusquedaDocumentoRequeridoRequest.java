package pe.com.ci.sed.document.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusquedaDocumentoRequeridoRequest {
    private String garanteid;
    private String mecanismoFacturacionId;
    private Integer codigoServicioOrigen;
    private String modofacturacionId;
}
