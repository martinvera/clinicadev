package pe.com.ci.sed.clinicalrecord.model.generic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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

    @JsonInclude(Include.NON_NULL)
    private Object atras;

    @JsonInclude(Include.NON_NULL)
    private Integer actual;

    @JsonInclude(Include.NON_NULL)
    private Integer totalitems;
}
