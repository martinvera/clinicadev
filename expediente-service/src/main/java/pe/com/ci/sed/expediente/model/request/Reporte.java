package pe.com.ci.sed.expediente.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Reporte {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date fecha;
    private String usuario;
    private String tipo;
    private String nombre;
    private String filtros;
    private String estado;
    private String url;
}
