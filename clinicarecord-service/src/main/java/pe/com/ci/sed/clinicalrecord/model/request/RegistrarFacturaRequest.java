package pe.com.ci.sed.clinicalrecord.model.request;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import pe.com.ci.sed.clinicalrecord.utils.Constants;

@Data
@JsonDeserialize
public class RegistrarFacturaRequest {

    @NotNull
    @JsonProperty("NU_LOTE")
    private long nuLote;

    @JsonProperty("NU_REMESA")
    private long nuRemesa;

    @JsonProperty("TI_DOC_PAGO")
    private String tiDocPago;

    @NotNull
    @JsonProperty("NU_DOC_PAGO")
    private String nuDocPago;

    @NotNull
    @JsonProperty("URL_DOC_PAGO")
    private String urlDocPago;

    @JsonProperty("FE_COMPROBANTE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.IAFAS_FORMAT_DATE)
    private Date feComprobante;

    @NotNull
    @JsonProperty("VA_MONTO_FACT")
    private double vaMontoFact;

    @NotNull
    @JsonProperty("TI_DOC_PACIENTE")
    private String tiDocPaciente;

    @NotNull
    @JsonProperty("DE_TI_DOC_PACIENTE")
    private String deTiDocPaciente;

    @NotNull
    @JsonProperty("NU_DOC_PACIENTE")
    private String nuDocPaciente;

    @NotNull
    @JsonProperty("AP_PAT_PACIENTE")
    private String apePatPaciente;

    @NotNull
    @JsonProperty("AP_MAT_PACIENTE")
    private String apeMatPaciente;

    @NotNull
    @JsonProperty("VA_NOM_PACIENTE")
    private String vaNoPaciente;

    @NotNull
    @JsonProperty("NU_NHC_PACIENTE")
    private String nuNhcPaciente;

    @NotNull
    @JsonProperty("CO_MECA_PAGO")
    private long coMecaPago;

    @NotNull
    @JsonProperty("DE_MECA_PAGO")
    private String deMecaPago;

    @NotNull
    @JsonProperty("CO_SUB_MECA_PAGO")
    private long coSubMecaPago;

    @NotNull
    @JsonProperty("DE_SUB_MECA_PAGO")
    private String deSubMecaPago;

    @NotNull
    @JsonProperty("CO_GARANTE")
    private String coGarante;

    @NotNull
    @JsonProperty("NO_GARANTE")
    private String noGarante;

    @NotNull
    @JsonProperty("TI_EPISODIO_XHIS")
    private String tiEpisodioXhis;

    @NotNull
    @JsonProperty("DE_TI_EPISODIO_XHIS")
    private String deEpisodioXhis;

    @NotNull
    @JsonProperty("PDF_ANEXO_DETALLADO")
    private String pdfAnexoDetallado;

    @JsonProperty("CO_ESTRU")
    private String coEstru;

    @JsonProperty("CO_CENTRO")
    private String coCentro;

    private String facturaNroAfecta;

    @NotNull
    @JsonProperty("ENCUENTROS")
    private Detalle[] encuentros;

    @Data
    public static class Detalle {
        @NotNull
        @JsonProperty("CO_PRESTACION")
        private String coPrestacion;

        @NotNull
        @JsonProperty("FE_PRESTACION")
        @JsonFormat(pattern = Constants.IAFAS_FORMAT_DATE)
        private Date fePrestacion;

        @NotNull
        @JsonProperty("HO_PRESTACION")
        @JsonFormat(pattern = Constants.IAFAS_FORMAT_HOUR)
        private Date hoPrestacion;

        @NotNull
        @JsonProperty("PDF_HOJA_AUTORIZACION")
        private String pdfHojaAutorizacion;

        @NotNull
        @JsonProperty("CO_SERVICIO")
        private String coServicio;

        @NotNull
        @JsonProperty("DE_SERVICIO")
        private String deServicio;

        @NotNull
        @JsonProperty("CO_RUBRO")
        private String coRubro;

        @NotNull
        @JsonProperty("CO_BENEFICIO")
        private String coBeneficio;

        @NotNull
        @JsonProperty("DE_BENEFICIO")
        private String deBeneficio;

        @JsonProperty("COMP_ENCUENTRO")
        @NotEmpty(message = "No existe comprobantes de pago")
        private List<@Valid Comprobante> comprobantes;

        private String tipoDocumentoDesc;
        private String tipoDocumentoId;
    }

    @Data
    @Validated
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Comprobante {
        @JsonProperty("IN_FARMACIA")
        private String indicador;

        @JsonProperty("TI_DOC_FARMACIA")
        private String tipDocumento;

        @JsonProperty("NU_DOC_FARMACIA")
        private String numDocumento;

        @JsonProperty("FE_EMI_DOC_FARMACIA")
        private String fecEmision;

        @JsonProperty("URL_DOC_FARMACIA")
        @NotEmpty(message = "URL de comprobante es obligatorio")
        private String urlDoc;

        private String tipoDocumentoDesc;
        private String tipoDocumentoId;
    }
}
