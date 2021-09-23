package pe.com.ci.sed.expediente.persistence.entity;

import com.microsoft.azure.storage.table.TableServiceEntity;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@Validated
@EqualsAndHashCode(callSuper=false)
public class Duplicados extends TableServiceEntity {

}
