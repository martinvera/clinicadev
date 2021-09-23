package pe.com.ci.sed.reporte.persistence.entity;

import com.microsoft.azure.storage.table.TableServiceEntity;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Garantes extends TableServiceEntity {
    private String codigo;
    private String descripcion;
}
