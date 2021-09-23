package pe.com.ci.sed.document.model.request.xhis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Validated
@AllArgsConstructor
@NoArgsConstructor
public class PaseAmbulatorio {

    @NotNull
    @JsonProperty("OBJ_COPAGOS")
    private @Valid List<Copago> copagos;

    @JsonProperty("OBJ_PRE_EXISTENCIA")
    private @Valid List<PreExistencia> preExistencias;

    @JsonProperty("OBJ_DIAGNOSTICO")
    private @Valid List<Diagnostico> diagnosticos;

    @Data
    @Validated
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Copago {
        @JsonProperty("CO_COPAGO_DIF")
        private String codigo;

        @JsonProperty("DE_PROCEDIMIENTO")
        private String procedimiento;

        @JsonProperty("VA_COPAGO_FIJO_DIF")
        private String copagoFijoDif;

        @JsonProperty("VA_COPAGO_VAR_DIF")
        private String copagoVarDif;

        @JsonProperty("OBS_COPAGO_DIF")
        private String obsCopagoDif;
    }

    @Data
    @Validated
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PreExistencia {
        @JsonProperty("CO_PRE_EXISTENCIA")
        private String codigo;

        @JsonProperty("DE_PRE_EXISTENCIA")
        private String descripcion;

        @JsonProperty("OBS_PRE_EXISTENCIA")
        private String observacion;
    }

    @Data
    @Validated
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Diagnostico {
        @JsonProperty("CO_CIE10")
        private String codigo;

        @JsonProperty("DE_CIE10")
        private String descripcion;

        @JsonProperty("TI_DIAGNOSTICO")
        private String tipo;
    }
}
