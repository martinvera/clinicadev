package pe.com.ci.sed.expediente.persistence.entity;

import java.util.Date;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.microsoft.azure.storage.table.TableServiceEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@Validated
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ExpedienteDigitalTable extends TableServiceEntity {

    private String id;

    private long nroLote;

    private String facturaNro;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date fechaCreacion;
    private String estado;
    private String msjError;
    private String garanteId;
    private String garanteDescripcion;
    private String creadoPor;
    private String origen;
    private String urlArchivoZip;
    private String urlArchivoZipSas;
    private String mecanismoFacturacionId;
    private String mecanismoFacturacionDesc;
    private String modoFacturacion;
    private String modoFacturacionId;
    private double facturaImporte;
    private String nroEncuentro;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date fechaAtencion;
    private String beneficioDescripcion;
    private String pacienteTipoDocIdentDesc;
    private String pacienteNroDocIdent;
    private String pacienteApellidoMaterno;
    private String pacienteApellidoPaterno;
    private String pacienteNombre;
    private String urlArchivoZipLote;
}
