package pe.com.ci.sed.clinicalrecord.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.microsoft.azure.storage.table.TableServiceEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GestionLote extends TableServiceEntity {

    public GestionLote(String garanteId, String nroLote) {
        this.partitionKey = garanteId;
        this.rowKey = nroLote;
    }

    private String fechaLote;
    private Integer facturasGeneradas;
    @JsonIgnore
    private String urlArchivoZip;
    private String urlArchivoZipSas;
    private Integer totalFacturas;
    private String estado;
    private String origen;
    private String garanteDescripcion;


    private long nroLote;

    @JsonIgnore
    private String garanteId;

    private String estadoGarante;

    private String fechaEnvio;

    private String fechaAceptacion;

    private String usuario;

    private String fechaRechazo;

    private String fechaRegistroSolicitud;

    private String registroSolicitud;
}