package pe.com.ci.sed.web.persistence.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Catalogo extends Auditoria {

    @JsonInclude(Include.NON_NULL)
    private String catalogo;

    private String codigo;
    private String descripcion;

    @JsonInclude(Include.NON_NULL)
    private Integer size;

    @JsonInclude(Include.NON_NULL)
    private String codigopadre;

    @JsonInclude(Include.NON_NULL)
    private Integer especial;

    @JsonInclude(Include.NON_NULL)
    private Integer os;
}
