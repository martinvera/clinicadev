package pe.com.ci.sed.expediente.persistence.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentosRequeridos extends AuditoriaStorage{
    private String garanteid;
    private String mecanismoid;
    private String modofacturacionid;
    private String tipodocumentoid;
}
