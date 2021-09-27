package pe.com.ci.sed.web.model.generic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Paginacion {
    private Object siguiente;
    private Object atras;
    private Integer actual;
    private Integer totalitems;
    private Integer totalpaginas;
}
