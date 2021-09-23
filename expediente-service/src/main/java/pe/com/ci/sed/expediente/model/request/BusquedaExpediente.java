package pe.com.ci.sed.expediente.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;

import static pe.com.ci.sed.expediente.utils.GenericUtil.DEFAULT_INICIAL;
import static pe.com.ci.sed.expediente.utils.GenericUtil.DEFAULT_TAMANIO;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusquedaExpediente {
    private String facturaNro;
    
    @NotBlank
    private String garanteId = Strings.EMPTY;
    
    @NotBlank
    private long lote;
    
    private String estado = Strings.EMPTY;
    private String fechaDesde;
    private String fechaHasta;
    private Integer actual = DEFAULT_INICIAL;
    private Integer size = DEFAULT_TAMANIO;
    private String siguiente;
    private String atras;
}
