package pe.com.ci.sed.integrator.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseIRequest {

    @JsonProperty("CO_ORIGEN_APP")
    private String coOrigenApp;

    @JsonProperty("FE_MENSAJE")
    private String feMensaje;

    @JsonProperty("CO_TRX")
    private String coTrx;

    @JsonProperty("NU_HCLINICA_PAC")
    private String nuHclinicaPac;

    @JsonProperty("NU_DOCUMENTO_PAC")
    private String nuDocumentoPac;

    @JsonProperty("TI_DOCUMENTO_PAC")
    private String tiDocumentoPac;

    @JsonProperty("APE_PATERNO_PAC")
    private String apePaternoPac;

    @JsonProperty("NOM_PAC")
    private String nomPac;

    @JsonProperty("APE_MATERNO_PAC")
    private String apeMaternoPac;

    @JsonProperty("CO_SEXO_PAC")
    private String coSexoPac;

    @JsonProperty("CO_ORIGEN_PAC")
    private String coOrigenPac;

    @JsonProperty("CO_SERV_ORIGEN")
    private String coServOrigen;

    @JsonProperty("CO_CAMA_PAC")
    private String coCamaPac;

    @JsonProperty("CO_SEDE")
    private String coSede;

    @JsonProperty("CO_CMP")
    private String coCmp;

    @JsonProperty("CO_SERV_DESTINO")
    private String coServDestino;

    @JsonProperty("DE_SERV_DESTINO")
    private String deServDestino;

    @JsonProperty("CO_MECA_PAGO")
    private String coMecaPago;

    @NotBlank
    @JsonProperty("NU_ENCUENTRO")
    private String nuEncuentro;

    @JsonProperty("ID_PETICION_HIS")
    private String idPeticionHis;

    @JsonProperty("NO_GARANTE")
    private String noGarante;

    @JsonProperty("ID_CITA_RIS")
    private String idCitaRis;

    @JsonProperty("TI_RESULTADO_RIS")
    private String tiResultadoRis;

    @NotBlank
    @JsonProperty("RUTA_RESULTADO_RIS")
    private String rutaResultadoRis;

}
