package pe.com.ci.sed.expediente.persistence.entity;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class Documento {

    private String nroEncuentro;
    private String pacienteApellidoPaterno;
    private String pacienteNombre;
    private String pacienteApellidoMaterno;
    private String pacienteTipoDocIdentId;
    private String pacienteNroDocIdent;
    private String pacienteTipoDocIdentDesc;
    private Integer nroLote;
    private String facturaNro;
    private String garanteid;
    private String garanteDescripcion;
    private String nroRemesa;
    private Integer facturaImporte;
    private String facturaIdDocumento;
    
    @NotNull
    @DateTimeFormat(pattern = "ddMMyyyy")
    private Date fechaAtencion;
    
    private String origenServicio;
    
	@NotEmpty
    private List<Archivo> archivos;

    @NotBlank
    private String beneficio;
	
	@Data
	public static class Archivo {

	    private String archivoBytes;

	    @NotBlank
	    private String nombreArchivo;

	    @NotBlank
	    private String nroEncuentro;

	    @NotBlank
	    private String tipoDocumentoId;
	    private String tipoDocumentoDesc;
	}
}
