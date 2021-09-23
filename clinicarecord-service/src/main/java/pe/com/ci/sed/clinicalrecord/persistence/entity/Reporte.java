package pe.com.ci.sed.clinicalrecord.persistence.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.microsoft.azure.storage.table.TableServiceEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Reporte extends TableServiceEntity {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date fecha;
    private String usuario;
    private String tipo;
    private String nombre;
    private String filtros;
    private String estado;
    private String url;
}
