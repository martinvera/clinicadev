package pe.com.ci.sed.document.model.request.xhis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupLaboratorio<T> {
    private String nombreDiagnostico;
    private String tipoDiagnostico;
    private String cieDiagnostico;
    private List<T> examenes;
}
