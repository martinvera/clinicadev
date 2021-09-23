package pe.com.ci.sed.document.model.request.salesforce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Orden {
    private String titulo;
    private List<Salesforce.DatoSited> lista;
}
