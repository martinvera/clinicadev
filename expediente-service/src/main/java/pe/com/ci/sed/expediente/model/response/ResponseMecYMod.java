package pe.com.ci.sed.expediente.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMecYMod {
    private String mecanismoFacturacionDesc;
    private String modoFacturacion;
    private Integer cantidad;
    private double importe;
}
