package pe.com.ci.sed.web.model.request;

import lombok.Builder;
import lombok.Data;
import pe.com.ci.sed.web.model.generic.HeaderRequest;

@Data
@Builder
public class Busqueda {
    private String siguiente;
    private String atras;
    private int size;

    private HeaderRequest header;
}
