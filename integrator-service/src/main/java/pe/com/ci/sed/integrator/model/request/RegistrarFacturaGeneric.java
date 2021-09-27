package pe.com.ci.sed.integrator.model.request;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import pe.com.ci.sed.integrator.util.Constants;
import pe.com.ci.sed.integrator.validator.CDValidator;
import pe.com.ci.sed.integrator.validator.IAFASValidator;

@Data
@Validated
public class RegistrarFacturaGeneric {

    @JsonProperty("NU_LOTE")
    @NotNull(message = "No existe el número de lote")
    private long nuLote;

    @JsonProperty("NU_REMESA")
    private long nuRemesa;

    @JsonProperty("TI_DOC_PAGO")
    private String tiDocPago;

    @NotEmpty(message = "No existe el número de factura", groups = {IAFASValidator.class})
    @JsonProperty("NU_DOC_PAGO")
    private String nuDocPago;

    @JsonProperty("URL_DOC_PAGO")
    @NotEmpty(message = "No existe url de la factura")
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

    @JsonProperty("CO_MECA_PAGO")
    @NotNull(message = "No existe código de mecanismo de facturación")
    private long coMecaPago;

    @JsonProperty("DE_MECA_PAGO")
    @NotEmpty(message = "No existe descripción de mecanismo de facturación")
    private String deMecaPago;

    @JsonProperty("CO_SUB_MECA_PAGO")
    @NotNull(message = "No existe código de modo de facturación")
    private long coSubMecaPago;

    @JsonProperty("DE_SUB_MECA_PAGO")
    @NotEmpty(message = "No existe descripción de modo de facturación")
    private String deSubMecaPago;

    @JsonProperty("CO_GARANTE")
    @NotEmpty(message = "No existe código de garante")
    private String coGarante;

    @JsonProperty("NO_GARANTE")
    @NotEmpty(message = "No existe nombre de garante")
    private String noGarante;

    @JsonProperty("TI_EPISODIO_XHIS")
    @NotEmpty(message = "No existe código de origen de servicio")
    private String tiEpisodioXhis;

    @JsonProperty("DE_TI_EPISODIO_XHIS")
    @NotEmpty(message = "No existe descripción de origen de servicio")
    private String deEpisodioXhis;

    @JsonProperty("PDF_ANEXO_DETALLADO")
    @NotEmpty(message = "No existe bytes de anexo detallado")
    private String pdfAnexoDetallado;


    @NotEmpty
    @JsonProperty("ENCUENTROS")
    private List<@Valid Detalle> encuentros;

    @Data
    @Validated
    public static class Detalle {
        @JsonProperty("CO_PRESTACION")
        @NotEmpty(message = "No existe código de encuentro", groups = {IAFASValidator.class})
        private String coPrestacion;

        @JsonProperty("FE_PRESTACION")
        @JsonFormat(pattern = Constants.IAFAS_FORMAT_DATE)
        private Date fePrestacion;

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

        @JsonProperty("CO_BENEFICIO")
        @NotEmpty(message = "No existe código de beneficio del encuentro")
        private String coBeneficio;

        @JsonProperty("DE_BENEFICIO")
        @NotEmpty(message = "No existe descripción de beneficio del encuentro")
        private String deBeneficio;
        private String tipoDocumentoId = "DCA017";
        private String tipoDocumentoDesc = "Orden de Atención Médica";

        @JsonProperty("COMP_ENCUENTRO")
        @NotEmpty(message = "No existe comprobantes de pago", groups = CDValidator.class)
        private List<@Valid Comprobante> comprobantes;

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
        @NotEmpty(message = "URL de comprobante es obligatorio", groups = CDValidator.class)
        private String urlDoc;

        private String tipoDocumentoDesc;
        private String tipoDocumentoId;
    }
}
