package pe.com.ci.sed.expediente.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReporteExpedienteGenerados {
    @NotEmpty
    private List<ExpedientesGeneradosEstado> generadosEstadoList;
    private List<ExpedientesGeneradosOrigen> generadosOrigenList;
    private String garanteDescripcion;
    private String garanteId;
    private String fechaDesdeHasta;
}
