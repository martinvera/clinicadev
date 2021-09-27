package pe.com.ci.sed.document.persistence.entity;

import com.azure.storage.queue.models.QueueMessageItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueMessageProcess {
    private boolean error;
    private String messageError;
    private QueueMessageItem queueMessageItem;
    private List<Documento> documentos;
    private String nroEncuentro;
    private List<String> encuentrosError;
    private String url;
    private String sistemaOrigen;
}
