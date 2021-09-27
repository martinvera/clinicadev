package pe.com.ci.sed.web.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class Validacion {

    @NotBlank
    private String garanteid;
    @NotBlank
    private String mecanismoFacturacionId;
    @NotBlank
    private Integer codigoServicioOrigen;
    @NotBlank
    private String modofacturacionId;

}
