package pe.com.ci.sed.expediente.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Catalogo extends AuditoriaStorage {
    private String catalogo;
    private String codigo;
    private String descripcion;
    private Integer size;
    private String codigopadre;
    private Integer especial;
    private Integer os;
}
