package pe.com.ci.sed.integrator.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GDocument {
    private String sistemaOrigen;
    private String contenido;
}
