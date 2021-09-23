package pe.com.ci.sed.document.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Doc {
    private String nroEncuentro;
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    private Date fechaAtencion;
    private String estadoArchivo;
    private String tipoDocumentoId;
    private String urlArchivo;
    private String urlArchivoSas;
    private String tipoDocumentoDes;
    private String msjError;
}
