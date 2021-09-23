package pe.com.ci.sed.expediente.model.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class GenerarExpedienteRequest {

    @NotBlank
    private String facturaNro;

    private String facturaNroInafecta;

    @NotBlank
    private String ejecutar;

    @NotBlank
    private String garanteId;

    @NotBlank
    private String garanteDescripcion;

    private String creadoPor;

    @NotBlank
    private String origen;

    @NotBlank
    private String mecanismoFacturacionId;
    private String mecanismoFacturacionDesc;

    @NotBlank
    private String modoFacturacionId;
    private String modoFacturacion;

    @NotNull
    private double facturaImporte;

    @NotNull
    private long nroLote;


    private String beneficioDescripcion;

    private String pacienteTipoDocIdentDesc;

    private String pacienteNroDocIdent;

    private String pacienteApellidoMaterno;

    private String pacienteApellidoPaterno;

    private String pacienteNombre;

    private String nroEncuentro;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date fechaAtencion;

    @JsonIgnore
    private boolean generado;
}
