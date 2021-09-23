package pe.com.ci.sed.document.model.generic;

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
    private Integer totalitems;
}
