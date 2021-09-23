package pe.com.ci.sed.document.persistence.entity;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.apache.logging.log4j.util.Strings;
import org.springframework.data.annotation.Id;
import org.springframework.validation.annotation.Validated;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Container(containerName = "document")
public class Documento implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @NotBlank
    private String nroEncuentro;

    private String pacienteApellidoPaterno;

    @NotBlank
    private String pacienteNombre;

    private String pacienteApellidoMaterno;

    private String pacienteTipoDocIdentId;

    private String pacienteNroDocIdent;

    private String pacienteTipoDocIdentDesc;

    @PartitionKey
    private long nroLote;

    private String facturaNro;

    private String garanteId;
    private String garanteDescripcion;
    private long nroRemesa;
    private double facturaImporte;
    private String facturaIdDocumento;

    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    private Date fechaAtencion;

    private long mecanismoFacturacionId;
    private long modoFacturacionId;
    private String origenServicio;

    private String beneficio;
    private String beneficioDesc;

    private String sede;

    @NotEmpty
    private List<@Valid Archivo> archivos;

    private Integer estado;

    private String sexoPaciente;

    private Date fechaMensaje;

    private String codigoOrigenPaciente;

    private String codigoServicioOrigen;

    private String codigoCamaPaciente;

    private String codigoSede;

    private String codigoCMP;

    private String codigoServDestino;

    private String descripServDestino;

    private String codigoApp;

    private String peticionHisID;

    private String peticionLisID;

    private Date fechaTransaccion;

    private Date fechaEfectivaOrden;

    private String codigoServSolicita;

    private String descripcionServSolicita;

    private String fullName;

    private String transactionId;
    private String applicationId;
    private String userId;
    private String userName;
    private String origenDescripcion;

    public void modificar(Documento request) {
        this.nroEncuentro = replace(request.nroEncuentro, this.nroEncuentro);
        this.peticionHisID = replace(request.peticionHisID, this.peticionHisID);
        this.pacienteTipoDocIdentId = replace(request.pacienteTipoDocIdentId, this.pacienteTipoDocIdentId);
        this.pacienteNroDocIdent = replace(request.pacienteNroDocIdent, this.pacienteNroDocIdent);
        this.codigoServSolicita = replace(request.codigoServSolicita, this.codigoServSolicita);
        this.descripcionServSolicita = replace(request.descripcionServSolicita, this.descripcionServSolicita);
        this.pacienteApellidoPaterno = replace(request.pacienteApellidoPaterno, this.pacienteApellidoPaterno);
        this.pacienteNombre = replace(request.pacienteNombre, this.pacienteNombre);
        this.pacienteApellidoMaterno = replace(request.pacienteApellidoMaterno, this.pacienteApellidoMaterno);
        this.fechaAtencion = Objects.nonNull(request.fechaAtencion) ? request.fechaAtencion : this.fechaAtencion;
        this.beneficio = replace(request.beneficio, this.beneficio);
        this.pacienteTipoDocIdentDesc = replace(request.pacienteTipoDocIdentDesc, this.pacienteTipoDocIdentDesc);
        this.sede = replace(request.sede, this.sede);
        this.facturaNro = replace(request.facturaNro, this.facturaNro);
        this.garanteId = replace(request.garanteId, this.garanteId);
        this.garanteDescripcion = replace(request.garanteDescripcion, this.garanteDescripcion);
    }

    public String replace(String replace, String target) {
        return Strings.isNotBlank(replace) ? replace : target;
    }
}

