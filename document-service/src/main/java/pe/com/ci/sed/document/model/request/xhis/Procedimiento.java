package pe.com.ci.sed.document.model.request.xhis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class Procedimiento {

    @JsonProperty("OBJ_ANTECEDENTE")
    private List<Antecedente> antecedentes;

    @JsonProperty("OBJ_DIAGNOSTICO")
    private List<PaseAmbulatorio.Diagnostico> diagnosticos;

    @JsonProperty("OBJ_DETALLE")
    private List<Detalle> detalles;

    @Data
    @Validated
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Antecedente {
        @JsonProperty("FE_ANTECEDENTE_QR")
        private String fechaRealizacion;

        @JsonProperty("DE_CIE10_ANTECEDENTE_QR")
        private String cie10;

        @JsonProperty("DE_NOTA_PROCEDIMIENTO")
        private String notasProcedimiento;
    }

    @Data
    @Validated
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class Detalle extends LaboratorioImagenes {

        @JsonProperty("DE_TIPO_HOSP")
        private String tipoHospitalizacion;

        @JsonProperty("FE_PROB_HOSP")
        private String feProbHosp;

        @JsonProperty("HO_PROB_HOSP")
        private String hoProbHosp;

        @JsonProperty("FE_PROB_PROCED")
        private String feProbProced;

        @JsonProperty("HO_PROB_PROCED")
        private String hoProbProced;

        @JsonProperty("DE_TECNICA_PROCED")
        private String deTecnicaProced;

        @JsonProperty("CA_TIME_HOSP")
        private String caTimeHosp;
    }
}
