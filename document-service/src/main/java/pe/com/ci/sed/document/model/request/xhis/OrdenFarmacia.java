package pe.com.ci.sed.document.model.request.xhis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

@Data
@Validated
@AllArgsConstructor
@NoArgsConstructor
public class OrdenFarmacia {

    @JsonProperty("OBJ_MEDICAMENTO")
    private @Valid List<Medicamento> medicamentos;

    @JsonProperty("OBJ_DIAGNOSTICO")
    private @Valid List<PaseAmbulatorio.Diagnostico> diagnosticos;

    @Data
    @Validated
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Medicamento {
        @JsonProperty("TI_PROD_FARMACIA")
        private String tipo;

        @JsonProperty("DE_PROD_FARMACIA")
        private String descripcion;

        @JsonProperty("DE_INDICACIONES")
        private String indicaciones;

        @JsonProperty("DE_VIA_ADMI")
        private String via;

        @JsonProperty("CA_DOSIS")
        private String dosis;

        @JsonProperty("CA_FRECUENCIA")
        private String frecuencia;

        @JsonProperty("CA_MEDICAMENTO")
        private String caMedicamento;

        @JsonProperty("CA_DURA_MATERIAL")
        private String duracion;

        @JsonProperty("IN_CRONICOS")
        private String cronicos;
    }

}
