package pe.com.ci.sed.clinicalrecord.model.request;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Validated
public class ActualizarFacturaCompleta {
    @NotBlank
    private String nroLote;

    @NotBlank
    private String facturaNro;

    @NotBlank
    private String estado;
}
