package pe.com.ci.sed.expediente.persistence.entity;

import com.azure.spring.data.cosmos.core.mapping.Container;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Builder
@Validated
@NoArgsConstructor
@AllArgsConstructor
@Container(containerName = "lote")
public class Lote extends Auditoria {

    @Id
    private String status;
    private Integer nroLote;
}