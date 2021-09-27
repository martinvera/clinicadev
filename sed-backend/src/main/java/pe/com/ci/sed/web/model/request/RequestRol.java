package pe.com.ci.sed.web.model.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;
import pe.com.ci.sed.web.persistence.entity.Auditoria;

@Validated
@Getter
@Setter
public class RequestRol extends Auditoria {

    public void setKeys(String codigo) {
        this.partitionKey = codigo;
        this.rowKey = codigo;
    }

    public RequestRol() {

    }

    private String codigo;

    @NotBlank
    private String nombre;

    @NotBlank
    private String descripcion;

    @NotBlank
    private String privilegios;

    @NotNull
    private Integer estado;
}