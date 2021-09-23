package pe.com.ci.sed.document.persistence.entity;

import com.microsoft.azure.storage.table.TableServiceEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class Catalogo extends TableServiceEntity {
	private String catalogo;
    private String codigo;
    private String descripcion;
    private Integer size;
    private String codigopadre;
    private Integer especial;
    private Integer os;
    private String tipodocumentoid;
}
