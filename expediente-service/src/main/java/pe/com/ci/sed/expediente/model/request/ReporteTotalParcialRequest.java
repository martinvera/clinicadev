package pe.com.ci.sed.expediente.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteTotalParcialRequest {

    private String tipoReporte;
    private String garanteId;
    private String garanteDescripcion;
    private String mesanio;
    private String fechaDesde;
    private String fechaHasta;
    private long nroLote;
}
