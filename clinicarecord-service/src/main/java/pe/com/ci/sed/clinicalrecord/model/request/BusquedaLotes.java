package pe.com.ci.sed.clinicalrecord.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

import static pe.com.ci.sed.clinicalrecord.utils.GenericUtil.DEFAULT_INICIAL;
import static pe.com.ci.sed.clinicalrecord.utils.GenericUtil.DEFAULT_TAMANIO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusquedaLotes {
    private long nroLote;
    private String estado;

    @NotBlank(message = "Debe seleccionar el garante")
    private String garanteId;
    private String fechaLoteDesde;
    private String fechaLoteHasta;

    private String estadoGarante;

    private String garanteDescripcion;
    @Builder.Default
    private Integer actual = DEFAULT_INICIAL;
    
    @Builder.Default
    private Integer size = DEFAULT_TAMANIO;

    private String siguiente;
    private String atras;
}
