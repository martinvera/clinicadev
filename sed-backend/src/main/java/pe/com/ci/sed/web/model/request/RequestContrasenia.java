package pe.com.ci.sed.web.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.com.ci.sed.web.model.generic.HeaderRequest;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestContrasenia {
    @NotBlank
    private String actualcontrasenia;
    @NotBlank
    private String nuevacontrasenia;
    private String passwordencrip;
    private HeaderRequest header;

}
