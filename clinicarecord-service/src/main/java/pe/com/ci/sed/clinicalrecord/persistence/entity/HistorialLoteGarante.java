package pe.com.ci.sed.clinicalrecord.persistence.entity;

import com.microsoft.azure.storage.table.TableServiceEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialLoteGarante extends TableServiceEntity {


    private long nroLote;


    private String garanteId;

    private String estado;

    private String fechaEnvio;

    private String fechaAceptacion;

    private String userName;

    private String fechaLote;

    private String nroSiniestro;

    private String fechaRechazo;

    private String fechaRegistroSolicitud;

    private String registroSolicitud;
}
