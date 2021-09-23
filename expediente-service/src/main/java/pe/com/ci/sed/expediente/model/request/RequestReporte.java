package pe.com.ci.sed.expediente.model.request;

import org.springframework.validation.annotation.Validated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class RequestReporte {

    private String fechaDesde;

    private String fechaHasta;

    private String tipoReporte;

    private String mecanismoFacturacionId;


}
