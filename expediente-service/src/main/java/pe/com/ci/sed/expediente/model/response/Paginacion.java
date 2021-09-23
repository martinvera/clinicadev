package pe.com.ci.sed.expediente.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Paginacion {
    private String siguiente;
    private String atras;
    private Integer actual;
    private Integer totalitems;
    private Integer totalpaginas;
}
