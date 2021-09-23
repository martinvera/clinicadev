package pe.com.ci.sed.clinicalrecord.persistence.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentosValidar {
    private String nroEncuentro;
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    private Date fechaAtencion;
    private String estadoArchivo;
    private String tipoDocumentoId;
    private String urlArchivo;
    private String tipoDocumentoDes;
}
