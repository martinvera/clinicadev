package pe.com.ci.sed.web.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoDocumento extends  Auditoria{
    private String catalogo;
    private String codigo;
    private String descripcion;
    private Integer size;
    private String codigopadre;
}
