package pe.com.ci.sed.document.persistence.entity;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Validated
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Archivo {

    private String archivoBytes;

    private byte[] bytes;

    private String nombreArchivo;

    private String urlArchivo;
    
    private String urlArchivoSas;

    @NotBlank
    private String nroEncuentro;

    @NotBlank
    private String tipoDocumentoId;
    private String tipoDocumentoDesc;
    private String estadoArchivo;
    private String msjError;
    private boolean existe;
    private boolean error;
}
