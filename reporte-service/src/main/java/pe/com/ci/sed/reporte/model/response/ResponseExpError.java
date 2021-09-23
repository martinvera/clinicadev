package pe.com.ci.sed.reporte.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseExpError {

    private String nroLote;
    private String facturaNro;
    private String fechaCreacion;
    private String garanteDescripcion;
    private String pacienteTipoDocIdentDesc;
    private String pacienteNroDocIdent;
    private String paciente;
    private String beneficio;
    private String msjError;
}
