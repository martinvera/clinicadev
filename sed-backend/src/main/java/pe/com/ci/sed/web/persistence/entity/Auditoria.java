package pe.com.ci.sed.web.persistence.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.microsoft.azure.storage.table.TableServiceEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Auditoria extends TableServiceEntity {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String usuarioCreacion;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private Date fechaCreacion;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String usuarioModificacion;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private Date fechaModificacion;

    public void creacion(String username) {
        this.usuarioCreacion = username;
        this.fechaCreacion = new Date();
    }

    public void modificar(String username) {
        this.usuarioModificacion = username;
        this.fechaModificacion = new Date();
    }
}
