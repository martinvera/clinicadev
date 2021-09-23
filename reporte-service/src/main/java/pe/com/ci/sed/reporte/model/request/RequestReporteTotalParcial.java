package pe.com.ci.sed.reporte.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class RequestReporteTotalParcial {
    private String garanteId;

    private String garanteDescripcion;

    private String mesanio;

    private String nroLote;
    @NotBlank
    private String tipoReporte;

}
