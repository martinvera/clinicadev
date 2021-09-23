package pe.com.ci.sed.clinicalrecord.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class RegistroSiniestro {
    private String fechaAceptacion;

    @NotBlank
    private String nroLote;

    @NotBlank
    private String garanteId;

    @NotBlank
    private String nroSiniestro;
}
