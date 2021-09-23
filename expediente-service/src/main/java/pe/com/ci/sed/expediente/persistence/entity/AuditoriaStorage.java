package pe.com.ci.sed.expediente.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.microsoft.azure.storage.table.TableServiceEntity;

import java.util.Date;

public class AuditoriaStorage extends TableServiceEntity {
    private String usuarioCreacion;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private Date fechaCreacion;

    private String usuarioModificacion;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
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
