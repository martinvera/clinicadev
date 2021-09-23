package pe.com.ci.sed.clinicalrecord.persistence.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class GestionLotesEnviados  {

    private List<EstadoTotal> estadoTotalList;
    private String garanteDescripcion;
    private String fechaDesdeHasta;
}
