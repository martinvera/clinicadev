package pe.com.ci.sed.clinicalrecord.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrigenServicio {
    private String facturaNro;
    private String origenServicio;
    private long nroLote;
}
