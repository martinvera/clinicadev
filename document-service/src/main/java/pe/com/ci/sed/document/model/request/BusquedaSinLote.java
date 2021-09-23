package pe.com.ci.sed.document.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusquedaSinLote {
    @NotBlank
    private String fechaDesde;
    @NotBlank
    private String fechaHasta;
}
