package pe.com.ci.sed.clinicalrecord.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static pe.com.ci.sed.clinicalrecord.utils.GenericUtil.DEFAULT_INICIAL;
import static pe.com.ci.sed.clinicalrecord.utils.GenericUtil.DEFAULT_TAMANIO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusquedaHistorial {

    private String garanteId;
    private String estado;
    private String nroLote;
    @Builder.Default
    private Integer actual = DEFAULT_INICIAL;

    @Builder.Default
    private Integer size = DEFAULT_TAMANIO;
}
