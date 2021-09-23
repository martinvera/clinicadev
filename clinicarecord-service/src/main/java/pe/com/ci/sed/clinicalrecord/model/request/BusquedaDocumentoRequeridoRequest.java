package pe.com.ci.sed.clinicalrecord.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusquedaDocumentoRequeridoRequest {
    private Long nroLote;
    private String facturaNro;
    private String garanteid;
    private String mecanismoid;
    private String modofacturacionid;
    private String tipodocumentoid;
    private String codigoServicioOrigen;
}
