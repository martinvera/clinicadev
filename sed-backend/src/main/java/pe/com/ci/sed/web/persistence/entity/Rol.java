package pe.com.ci.sed.web.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Validated
@NoArgsConstructor
public class Rol extends Auditoria {

    public void setKeys(String codigo) {
        this.partitionKey = codigo;
        this.rowKey = codigo;
    }

    private String codigo;

    @NotBlank
    private String nombre;

    @NotBlank
    private String descripcion;

    @NotBlank
    private String privilegios;

    @NotNull
    private String estado;
}
