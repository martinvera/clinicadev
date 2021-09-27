package pe.com.ci.sed.document.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRequest {
    private String tipoDocumento;
    private String sistemaOrigen;
    private String contenido;
}
