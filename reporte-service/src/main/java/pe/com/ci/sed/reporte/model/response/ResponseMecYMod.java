package pe.com.ci.sed.reporte.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMecYMod {
    private String mecanismoFacturacionDesc;
    private String modoFacturacion;
    private Integer cantidad;
    private double importe;

}
