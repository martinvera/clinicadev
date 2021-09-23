package pe.com.ci.sed.expediente.model.request;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Data
@Validated
public class EliminarRequest {

    @NotBlank
    private String nroLote;
    
    @NotBlank
    private String garanteId;
}
