package pe.com.ci.sed.reporte.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseTP {
    private long nroLote;
    private String facturaNro;
    private String nrosEncuentro;
    private String fechaAtencion;
    private double facturaImporte;
    private String modoFacturacion;
    private String mecanismoFacturacionDesc;
    private String estadoFactura;
    private String estado;
    private String garanteDescripcion;
    private String paciente;
    private String fechaAceptacion;
    private String fechaCreacion;
    private String partitionKey;
    private String rowKey;
    private String etag;
    private String fechaModificacion;
    private String garanteId;
    private String timestamp;

}
