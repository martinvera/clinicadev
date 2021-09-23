package pe.com.ci.sed.clinicalrecord.persistence.entity;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadoTotal {

    private Integer total;

    private String estadoGarante;
}
