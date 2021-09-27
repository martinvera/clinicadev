package pe.com.ci.sed.web.model.response;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import pe.com.ci.sed.web.persistence.entity.Privilegio;

@Data
@Builder
@AllArgsConstructor
public class RolReponse {
    private String codigo;
    private String nombre;
    private String descripcion;
    private String privilegios;
    private List<Privilegio> privilegioList;
    private String estado;

    public RolReponse() {
        this.privilegioList = Collections.emptyList();
    }
}
