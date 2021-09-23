package pe.com.ci.sed.document.model.request.xhis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@AllArgsConstructor
@NoArgsConstructor
public class LaboratorioImagenes {

    @JsonProperty("CO_CIE10")
    private String codigo;

    @JsonProperty("DE_CIE10")
    private String descripcion;

    @JsonProperty("TI_DIAGNOSTICO")
    private String tipo;

    @JsonProperty("TI_EXAMEN")
    private String tipoExamen;

    @JsonProperty("DE_EXAMEN")
    private String descExamen;

    @JsonProperty("TI_EXAMEN_NIVEL_1")
    private String tipoExamen1;

    @JsonProperty("DE_EXAMEN_NIVEL_1")
    private String descExamen1;

    @JsonProperty("DE_PRIORIDAD")
    private String prioridad;

    @JsonProperty("CO_SEGUS_EXAMEN")
    private String codSegusExamen;

    @JsonProperty("DE_SEGUS_EXAMEN")
    private String descSegusExamen;

    @JsonProperty("CO_HIS_EXAMEN")
    private String hisExamen;

    @JsonProperty("DE_INDICACIONES")
    private String indicaciones;

    @JsonProperty("CO_EXAMEN_NIVEL_2")
    private String tipoExamen2;

    @JsonProperty("DE_EXAMEN_NIVEL_2")
    private String descExamen2;

}
