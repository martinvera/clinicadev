package pe.com.ci.sed.clinicalrecord.persistence.entity;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.validation.annotation.Validated;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static pe.com.ci.sed.clinicalrecord.utils.GenericUtil.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
@Container(containerName = "clinicalrecord")
public class ClinicalRecord {

    private String origen;
    @NotBlank
    @PartitionKey
    private long nroLote;

    @NotBlank
    private String mecanismoFacturacionId;
    private String mecanismoFacturacionDesc;

    private String modoFacturacion;
    private String modoFacturacionId;

    @NotBlank
    private String[] nroEncuentro;

    private String pacienteTipoDocIdentDesc;
    private String pacienteTipoDocIdentId;
    private String pacienteNroDocIdent;
    private String pacienteNombre;
    private String pacienteApellidoPaterno;
    private String pacienteApellidoMaterno;

    private String garanteDescripcion;
    private String garanteId;

    @Id
    private String facturaNro;
    private String facturaNroAfecta;
    private String facturaNroInafecta;

    @JsonFormat(shape = Shape.STRING, pattern = FORMATSLASH_DDMMYYYY, locale = DEFAULT_LOCALE)
    private Date facturaFecha;

    private double facturaImporte;
    private String archivoAnexoDet;

    private String archivoAnexoDetSas;

    private String facturaArchivo;
    private String facturaArchivoSas;
    private String nroHistoriaClinica;
    private String beneficioId;
    private String beneficioDescripcion;

    private String estado;
    private String estadoFactura;
    private String origenServicioId;
    private String origenServicioDesc;
    private long nroRemesa;
    @JsonFormat(shape = Shape.STRING, pattern = FORMAT_YYYYMMDD, locale = DEFAULT_LOCALE)
    private Date fechaAtencion;
    private String sedeRenipress;
    private String coEstru;
    private String coCentro;
}
