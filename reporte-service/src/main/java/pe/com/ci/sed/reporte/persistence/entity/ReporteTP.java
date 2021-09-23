package pe.com.ci.sed.reporte.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.microsoft.azure.storage.table.TableServiceEntity;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ReporteTP extends TableServiceEntity {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date fecha;
    private String usuario;
    private String garanteDescripcion;
    private String nroLote;
    private String mesyanio;
    private String estado;
    private String url;
    private String tipoReporte;
    private String nombre;
}
