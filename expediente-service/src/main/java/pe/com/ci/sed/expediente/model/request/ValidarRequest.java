package pe.com.ci.sed.expediente.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import pe.com.ci.sed.expediente.persistence.entity.Validacion;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class ValidarRequest {
    @NotNull
    private @Valid Validacion request;

    @NotNull
    private RequestHeader header;
}
