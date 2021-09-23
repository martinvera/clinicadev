package pe.com.ci.sed.clinicalrecord.model.request;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class ListarRequest {
    @NotNull
    private long nroLote;
    private String garanteId;
    private String facturaNro;
    private String estadoFactura;
    @Builder.Default
    private Integer actual = 1;
    private String siguiente;
    @Builder.Default
    private Integer size = 20;
}
