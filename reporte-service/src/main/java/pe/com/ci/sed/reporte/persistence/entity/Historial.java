package pe.com.ci.sed.reporte.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.microsoft.azure.storage.table.TableServiceEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Historial extends TableServiceEntity {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date fecha;
    private String usuario;
    private String tipo;
    private String filtros;
    private String estado;
    private String url;
    private String urlSas;
    private String garante;
    private String mensajeError;
}
