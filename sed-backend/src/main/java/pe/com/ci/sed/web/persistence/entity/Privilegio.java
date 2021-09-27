package pe.com.ci.sed.web.persistence.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Validated
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Privilegio extends Auditoria {

    public Privilegio(String rolId, String codigo) {
        this.partitionKey = rolId;
        this.rowKey = codigo;
    }

    @NotBlank
    private String codigo;

    @NotBlank
    private String nombre;

    @NotBlank
    private String descripcion;

    @NotNull
    private Integer estado;

    public Privilegio(Catalogo catalogo, String rolId) {
        this(rolId, catalogo.getCodigo());
        this.codigo = catalogo.getCodigo();
        this.descripcion = catalogo.getDescripcion();
        this.estado = 1;
        this.nombre = catalogo.getDescripcion();
    }

}


