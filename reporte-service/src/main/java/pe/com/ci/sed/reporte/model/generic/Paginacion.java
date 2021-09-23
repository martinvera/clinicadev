package pe.com.ci.sed.reporte.model.generic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Paginacion {
    private Integer siguiente;
    private Integer atras;
    private Integer actual;
    private Integer totalitems;
    private Integer totalpaginas;
}
