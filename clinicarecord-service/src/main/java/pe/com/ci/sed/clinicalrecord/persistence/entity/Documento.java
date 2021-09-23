package pe.com.ci.sed.clinicalrecord.persistence.entity;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Documento {

    private String nroEncuentro;
    private String pacienteApellidoPaterno;
    private String pacienteNombre;
    private String pacienteApellidoMaterno;
    private String pacienteTipoDocIdentId;
    private String pacienteNroDocIdent;
    private String pacienteTipoDocIdentDesc;
    private long nroLote;
    private String facturaNro;
    private String garanteid;
    private String garanteDescripcion;
    private String nroRemesa;
    private double facturaImporte;
    private String facturaIdDocumento;
    private String codigoServicioOrigen;
    
    @DateTimeFormat(pattern = "ddMMyyyy")
    private Date fechaAtencion;
    
    private String origenServicio;
    
    private List<Archivo> archivos;
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Archivo {

	    private String archivoBytes;

	    @NotBlank
	    private String nombreArchivo;

	    @NotBlank
	    private String nroEncuentro;

	    @NotBlank
	    private String beneficio;

	    @NotBlank
	    private String tipoDocumentoId;
	    private String tipoDocumentoDesc;
        private String estadoArchivo;
	}
}
