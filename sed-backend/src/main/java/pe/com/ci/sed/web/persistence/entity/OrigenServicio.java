package pe.com.ci.sed.web.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@Validated
@Getter
@Setter
public class OrigenServicio extends Auditoria {
    public void setKeys(String codigo) {
        this.partitionKey = codigo;
        this.rowKey = codigo;
    }
    private String catalogo;
    private String codigo;
    private String descripcion;
    private Integer size;
    private String codigopadre;
    private List<TipoDocumento> tipodocumento = new ArrayList<>();
}

