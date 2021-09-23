package pe.com.ci.sed.clinicalrecord.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Validar {
    private String catalogo;
    private String codigo;
    private String descripcion;
    private Integer size;
    private String codigopadre;
    private Integer contador;
    private String estado;
}
