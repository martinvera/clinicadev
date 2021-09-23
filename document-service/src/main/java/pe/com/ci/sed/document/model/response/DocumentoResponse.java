package pe.com.ci.sed.document.model.response;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import pe.com.ci.sed.document.persistence.entity.Documento;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Data
public class DocumentoResponse {
    private String archivoBytes;
    private String nroEncuentro;
    private String nombreCompletoPaciente;
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    private @NotNull Date fechaAtencion;
    private String pacienteTipoDocIdentDesc;
    private String pacienteTipoDocIdentId;
    private String pacienteNroDocIdent;
    private String pacienteNombre;
    private String pacienteApellidoPaterno;
    private String pacienteApellidoMaterno;
    private String facturaNro;
    private double facturaImporte;
    private String facturaIdDocumento;
    private String garanteDescripcion;
    private String garanteId;
    private long nroLote;
    private long remesa;
    private String beneficio;
    private String tipoDocumentoDesc;
    private String tipoDocumentoId;
    private String archivoNombre;

    public DocumentoResponse(Documento doc) {
        this.archivoBytes = null;
        this.nroEncuentro = doc.getNroEncuentro();
        this.fechaAtencion = doc.getFechaAtencion();
        this.pacienteTipoDocIdentDesc = doc.getPacienteTipoDocIdentDesc();
        this.pacienteTipoDocIdentDesc = null;
        this.pacienteTipoDocIdentId = doc.getPacienteTipoDocIdentId();
        this.pacienteNroDocIdent = doc.getPacienteNroDocIdent();
        this.pacienteNombre = doc.getPacienteNombre();
        this.pacienteApellidoPaterno = doc.getPacienteApellidoPaterno();
        this.pacienteApellidoMaterno = doc.getPacienteApellidoMaterno();
        this.facturaNro = doc.getFacturaNro();
        this.facturaImporte = doc.getFacturaImporte();
        this.facturaIdDocumento = doc.getFacturaIdDocumento();
        this.garanteDescripcion = doc.getGaranteDescripcion();
        this.garanteId = doc.getGaranteId();
        this.nroLote = doc.getNroLote();
        this.remesa = doc.getNroRemesa();
        this.beneficio = null;
        this.tipoDocumentoDesc = null;
        this.tipoDocumentoId = null;
        this.archivoNombre = getArchivoNombre();
    }
}
