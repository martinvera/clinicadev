package pe.com.ci.sed.reporte.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class GestionLote {
    private String fechaLote;
    private Integer facturasGeneradas;
    private Integer totalFacturas;
    private String estado;
    private String origen;
    private String garanteDescripcion;
    private long nroLote;
    private String garanteId;
    private String estadoGarante;
    private String fechaEnvio;
    private String fechaAceptacion;
    private String usuario;
    private String nroSiniestro;
    private String fechaRechazo;
    private String registroSolicitud;
    private String urlArchivoZip;
    private String urlArchivoZipSas;
    private String fechaRegistroSolicitud;

    private String etag;
    private String timestamp;
    private String partitionKey;
    private String rowKey;
}


