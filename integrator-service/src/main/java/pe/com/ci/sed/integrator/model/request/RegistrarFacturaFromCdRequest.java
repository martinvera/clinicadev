package pe.com.ci.sed.integrator.model.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@EqualsAndHashCode(callSuper = false)
public class RegistrarFacturaFromCdRequest extends RegistrarFacturaGeneric {
    @NotNull
    @JsonProperty("CO_ESTRU")
    @NotEmpty(message = "No existe codigo de equivalencia")
    private String coEstru;
}
