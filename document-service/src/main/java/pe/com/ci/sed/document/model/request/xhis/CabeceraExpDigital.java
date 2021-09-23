package pe.com.ci.sed.document.model.request.xhis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@Validated
@AllArgsConstructor
@NoArgsConstructor
public class CabeceraExpDigital {

    private Boolean isquirurgico;
    @JsonProperty("FE_ENCUENTRO")
    private String fechaEnc;

    @JsonProperty("FE_VENCE_ENCUENTRO")
    private String fechaVenc;

    @JsonProperty("HO_CITA")
    private String horaAperturaCita;

    @JsonProperty("NU_RUC_CI")
    private String rucCI;

    @JsonProperty("NU_TELEF_CI")
    private String telefonoCI;

    @JsonProperty("NU_ENCUENTRO")
    private String nroEnc;

    @JsonProperty("DE_SEDE")
    private String sede;

    @JsonProperty("NO_PACIENTE")
    private String nombrePaciente;

    @JsonProperty("APE_PAT_PACIENTE")
    private String apePaternoPaciente;

    @JsonProperty("APE_MAT_PACIENTE")
    private String apeMaternoPaciente;

    @JsonProperty("TI_DOC_PACIENTE")
    private String tipoDocPaciente;

    @JsonProperty("DE_TIPO_DOC_PACIENTE")
    private String tipoDocPacienteDesc;

    @JsonProperty("NU_DOC_PACIENTE")
    private String nroDocPaciente;

    @JsonProperty("CA_EDAD_PACIENTE")
    private String edadPaciente;

    @JsonProperty("FE_NAC_PACIENTE")
    private String fechaNacPaciente;

    @JsonProperty("DE_SEXO")
    private String sexo;

    @JsonProperty("NU_HIS_PACIENTE")
    private String hhccPaciente;

    @JsonProperty("NU_TELEF_PACIENTE")
    private String telefonoPaciente;

    @JsonProperty("NU_MOVIL_PACIENTE")
    private String celularPaciente;

    @JsonProperty("DE_DIREC_PACIENTE")
    private String direccionPaciente;

    @JsonProperty("DE_DISTRITO_PACIENTE")
    private String distritoPaciente;

    @JsonProperty("CO_PARTICULAR")
    private String particular;

    @JsonProperty("CO_GARANTE")
    private String garante;

    @JsonProperty("DE_PRODUCTO")
    private String producto;

    @JsonProperty("CO_PARENTESCO")
    private String parentesco;

    @JsonProperty("ES_POLIZA")
    private String estadoPoliza;

    @JsonProperty("NO_TITULAR")
    private String nombreTitular;

    @JsonProperty("APE_PAT_TITULAR")
    private String apePaternoTitular;

    @JsonProperty("APE_MAT_TITULAR")
    private String apeMaternoTitular;

    @JsonProperty("NU_CONTRATO")
    private String numContrato;

    @JsonProperty("NU_PLAN_SITEDS")
    private String planSiteds;

    @JsonProperty("DE_COMPANIA")
    private String compania;

    @JsonProperty("CO_PLAN")
    private String plan;

    @JsonProperty("DE_PLAN_SITEDS")
    private String planSitedsDesc;

    @JsonProperty("NU_AUTORIZACION")
    private String nroAutorizacion;

    @JsonProperty("DE_BENEFICIO")
    private String beneficio;

    @JsonProperty("OBS_BENEFICIO")
    private String obsBeneficio;

    @JsonProperty("VA_COPAGO_FIJO")
    private String deducible;

    @JsonProperty("VA_COPAGO_VAR")
    private String coaseguro;

    @JsonProperty("FE_FIN_CARENCIA")
    private String finCarencia;

    @JsonProperty("OBS_ASEGURADO")
    private String observAsegurado;

    @JsonProperty("NO_MEDICO")
    private String nombreMedico;

    @JsonProperty("APE_PAT_MEDICO")
    private String apePaternoMedico;

    @JsonProperty("APE_MAT_MEDICO")
    private String apeMaternoMedico;

    @JsonProperty("CO_CMP")
    private String cmp;

    @JsonProperty("DE_TIPO_CITA")
    private String tipoCita;

    @JsonProperty("DE_MOTIVO_CONSULTA")
    private String motivoConsulta;

    @JsonProperty("RELATO_MEDICO")
    private String relato;

    @JsonProperty("FIRMA_MEDICO")
    private String firmaMedico;

    @JsonProperty("DE_DIREC_SEDE")
    private String sedeDireccion;

    @JsonProperty("CO_ESPECIAL_MEDICO")
    private String especialidadCod;

    @JsonProperty("DE_ESPECIALIDAD")
    private String especialidadDesc;

    private String nroGuias;

    private String codigosCi10;

    private String diagnosticos;

    private PaseAmbulatorio paseAmbulatorio;

}
