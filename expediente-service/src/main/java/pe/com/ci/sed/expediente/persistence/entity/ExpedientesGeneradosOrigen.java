package pe.com.ci.sed.expediente.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpedientesGeneradosOrigen {
    private String origen;
    private Integer totalxorigen;
}
