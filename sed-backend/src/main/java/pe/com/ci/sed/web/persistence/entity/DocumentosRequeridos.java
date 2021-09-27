package pe.com.ci.sed.web.persistence.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentosRequeridos extends Auditoria{
    private String garanteid;
    private String mecanismoid;
    private String modofacturacionid;
    private String tipodocumentoid;
    private String descripcion;
}
