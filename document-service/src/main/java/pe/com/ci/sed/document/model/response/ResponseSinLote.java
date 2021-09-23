package pe.com.ci.sed.document.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseSinLote {
    private String nroEncuentro;
    private String fechaAtencion;
    private String fullName;
    private String garanteDescripcion;
    private String pacienteTipoDocIdentDesc;
    private String pacienteNroDocIdent;
    private String beneficio;
    private String beneficioDesc;
    private String facturaImporte;
    private String userName;
}
