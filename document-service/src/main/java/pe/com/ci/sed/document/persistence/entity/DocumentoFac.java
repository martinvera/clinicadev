package pe.com.ci.sed.document.persistence.entity;

import com.azure.spring.data.cosmos.core.mapping.Container;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Container(containerName = "document")
public class DocumentoFac {

    private String facturaNro;
    private String origenServicio;
    private long nroLote;
}
