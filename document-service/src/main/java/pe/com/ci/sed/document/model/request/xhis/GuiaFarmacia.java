package pe.com.ci.sed.document.model.request.xhis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import pe.com.ci.sed.document.model.validacion.XhisValidator;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Validated
@AllArgsConstructor
@NoArgsConstructor
public class GuiaFarmacia {

    @JsonProperty("OBJ_DET_GUIA")
    private List<@Valid GuiaDetalle> detalles;

    @JsonProperty("OBJ_DIAGNOSTICO")
    private List<PaseAmbulatorio.@Valid Diagnostico> diagnosticos;

    @JsonProperty("OBJ_FACTURAS")
    private List<@Valid Factura> facturas;

    @Data
    @Validated
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GuiaDetalle {

        @JsonProperty("NU_GUIA_FAR")
        private String nuGuiaFar;

        @JsonProperty("CO_PREFIJO_GUIA")
        private String coPrefijoGuia;

        @JsonProperty("TI_PROD_FARMACIA")
        private String tiProdFarmacia;

        @JsonProperty("CO_PROD_FARMACIA")
        private String coProdFarmacia;

        @JsonProperty("DE_PROD_FARMACIA")
        private String deProdFarmacia;

        @JsonProperty("CA_PROD_FARMACIA")
        private String caProdFarmacia;

        @JsonProperty("VA_PRECIO_UNIT")
        private String vaPrecioUnit;

        @JsonProperty("VA_IMPORTE")
        private String vaImporte;

        @JsonProperty("ES_PAGO_PROD")
        private String esPagoProd;

        @JsonProperty("NO_GENERICO")
        private String noGenerico;

        @JsonProperty("DE_UN_MEDIDA")
        private String deUnMedida;

        @JsonProperty("CA_MULTIDOSIS")
        private String caMultidosis;

        @JsonProperty("DE_UN_MULTIDOSIS")
        private String deUnMultidosis;

        private String via;
    }

    @Data
    @Validated
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Factura {

        @JsonProperty("NU_PREFACT_HIS")
        private String nuPrefactHis;

        @JsonProperty("NU_COMPROB_SAP")
        private String nuComprobSap;

        @JsonProperty("OBJ_MONTOS_GUIA")
        @NotEmpty(message = "Debe contener documentos de farmacia", groups = {XhisValidator.class})
        private List<@Valid MontoGuia> montoGuias;
    }

    @Data
    @Validated
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MontoGuia {

        @JsonProperty("NUM_PREFAC")
        private String numPrefac;

        @JsonProperty("IM_BASE_ITEM")
        private String imBaseItem;

        @JsonProperty("IM_TOTAL_IGV")
        private String imTotalIgv;

        @JsonProperty("IM_TOTAL")
        private String imTotal;

        @JsonProperty("TI_DOC_FARMACIA")
        private String tiDocFarmacia;

        @JsonProperty("NU_DOC_FARMACIA")
        private String nuDocFarmacia;

        @JsonProperty("FE_EMI_DOC_FARMACIA")
        private String feEmiDocFarmacia;

        @JsonProperty("URL_DOC_FARMACIA")
        @NotBlank(message = "URL de documento farmacia no debe ser vacio", groups = {XhisValidator.class})
        private String urlDocFarmacia;
    }
}
