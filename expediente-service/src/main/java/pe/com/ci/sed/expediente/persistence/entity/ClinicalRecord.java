package pe.com.ci.sed.expediente.persistence.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClinicalRecord {
    public static final String DEFAULT_LOCALE = "es_PE";
    public static final String DEFAULT_PATTERN = "dd/MM/yyyy";
    private String origen;

    private long nroLote;
    private String mecanismoFacturacionId;
    private String mecanismoFacturacionDesc;

    private String modoFacturacion;
    private String modoFacturacionId;

    private String[] nroEncuentro;

    private String pacienteTipoDocIdentDesc;
    private String pacienteTipoDocIdentId;
    private String pacienteNroDocIdent;
    private String pacienteNombre;
    private String pacienteApellidoPaterno;
    private String pacienteApellidoMaterno;

    private String garanteDescripcion;
    private String garanteId;

    private String facturaNro;
    private String facturaNroInafecta;

    @JsonFormat(shape = Shape.STRING, pattern = DEFAULT_PATTERN, locale = DEFAULT_LOCALE)
    private Date facturaFecha;

    private double facturaImporte;
    private String archivoAnexoDet;
    
    
    private String archivoAnexoDetSas;
    
    private String facturaArchivo;
    private String nroHistoriaClinica;
    private String beneficioId;
    private String beneficioDescripcion;

    private String estado;
    private String estadoFactura;
    private String origenServicioId;
    private String origenServicioDesc;
    private long nroRemesa;
    private  Date fechaAtencion;
    private String sedeRenipress;
    private String coEstru;
    private String coCentro;
}
