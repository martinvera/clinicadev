package pe.com.ci.sed.reporte.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class RequestReporte {
    private String fechaDesde;
    private String fechaHasta;

    @NotBlank
    private String tipoReporte;

    private String mecanismoFacturacionDesc;
    private String mesanio;
    private String nroLote;
    private String garanteDescripcion;
    private String mecanismoFacturacionId;
    private String garanteId;
    private String estado;
}
