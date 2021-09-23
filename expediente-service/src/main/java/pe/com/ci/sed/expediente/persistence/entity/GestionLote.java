package pe.com.ci.sed.expediente.persistence.entity;

import com.microsoft.azure.storage.table.TableServiceEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GestionLote extends TableServiceEntity {
    public GestionLote(String nroLote){
        this.partitionKey = nroLote;
        this.rowKey = nroLote;
    }

    private long nroLote;
    private int cantidadGenerados;
    private Integer totalFacturas;
    private String garanteDescripcion;
    private String garanteId;
    private String nroEncuentro;
    private String estado;
    private String fechaAceptacion;
}
