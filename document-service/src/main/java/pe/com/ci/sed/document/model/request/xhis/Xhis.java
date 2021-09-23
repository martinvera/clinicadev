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
public class Xhis {

    @NotNull
    @JsonProperty("NU_ENCUENTRO")
    private @Valid String episodio;

    @JsonProperty("DE_RESPUESTA")
    private String descRespuesta;

    @JsonProperty("CO_RESPUESTA")
    private String codRespuesta;

    @NotNull
    @JsonProperty("OBJ_CAB_EXPDIGITAL")
    private @Valid CabeceraExpDigital cabecera;

    @JsonProperty("OBJ_PASE_AMB")
    private @Valid PaseAmbulatorio paseAmbulatorio;

    @JsonProperty("OBJ_ORD_FARMACIA")
    private OrdenFarmacia ordenFarmacia;

    @JsonProperty("OBJ_GUIA_FARMACIA")
    private GuiaFarmacia guiaFarmacia;

    @JsonProperty("OBJ_LAB_IMG")
    private List<LaboratorioImagenes> laboratorioImagenes;

    @JsonProperty("OBJ_PROCEDIMIENTO")
    private Procedimiento procedimiento;

    private List<GroupLaboratorio> laboratorios;

}
