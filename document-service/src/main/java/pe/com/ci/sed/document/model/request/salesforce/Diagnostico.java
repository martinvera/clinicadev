package pe.com.ci.sed.document.model.request.salesforce;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.validation.annotation.Validated;
import pe.com.ci.sed.document.model.validacion.OrdenImagePatologico;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Validated
@AllArgsConstructor
@NoArgsConstructor
public class Diagnostico {

    @JsonProperty("TI_DIAGNOSTICO")
    @NotEmpty(groups = {OrdenImagePatologico.class})
    private String tipoDiagnostico;

    @JsonProperty("NO_DIAGNOSTICO")
    @NotEmpty(groups = {OrdenImagePatologico.class})
    private String nombreDiagnostico;

    @JsonProperty("CO_CIE10")
    @NotEmpty(groups = {OrdenImagePatologico.class})
    private String cieDiagnostico;

    @JsonProperty("OBJ_MEDICAMENTOS")
    private List<@Valid Medicamento> medicamentos;

    @JsonProperty("OBJ_PROC_HOSPITAL")
    private List<@Valid Procedicimiento> hospitalProcedimientos;

    @JsonProperty("OBJ_EXAMENES")
    private List<@Valid Examen> examenes;

    // Opcional
    private List<Salesforce.AntecedenteQuirurgico> antecedentesQuirurgicos;
    // Opcional
    private List<Salesforce.AntecedentePatologico> antecedentesPatologicos;
    // Opcional
    private List<Salesforce.AntecedentePersonal> antecedentesPersonalMenor;

    @Data
    @Validated
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Examen {

        @JsonProperty("TI_PROCEDIMIENTO")
        @NotEmpty(groups = {OrdenImagePatologico.class})
        private String tipoProcedimiento;

        @JsonProperty("TI_LABORATORIO")
        private String tipoLaboratorio;

        @JsonProperty("SUB_TIPO_IMAGENES")
        private String subTipoImagenes;

        @JsonProperty("IN_SEDACION")
        private String requiereSedacion;

        @JsonProperty("NU_PRIORIDAD_EXA")
        @NotEmpty(groups = {OrdenImagePatologico.class})
        private String prioridadExamen;

        @JsonProperty("NO_PROCEDIMIENTO")
        @NotEmpty(groups = {OrdenImagePatologico.class})
        private String nombreProcedimiento;

        @JsonProperty("DE_LATERALIDAD")
        private String lateralidad;

        @JsonProperty("IN_CONTRASTE")
        private String contraste;

        @JsonProperty("DE_COMENTARIOS")
        private String comentarios;

        @JsonProperty("CO_SEGUS")
        @NotEmpty(groups = {OrdenImagePatologico.class})
        private String codigoSegus;

        public Examen(Procedicimiento p) {
            this.tipoProcedimiento = p.tipoProcedimiento;
            this.codigoSegus = p.codigoSegus;
            this.nombreProcedimiento = p.nombreProcedimiento;
            this.comentarios = p.comentIndicaciones;
        }
    }

    @Data
    @Validated
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Medicamento {
        @JsonProperty("DE_VIA_ADMINISTRACION")
        private String viaAdministracion;

        @JsonProperty("CA_TIME_MEDICAMENTO")
        private String tiempo;

        @JsonProperty("NO_MEDICAMENTO")
        private String nombre;

        @JsonProperty("DE_INDICACIONES")
        private String indicaciones;

        @JsonProperty("CA_FRECUENCIA_MEDICAMENTO")
        private String frecuencia;

        @JsonProperty("NU_DOSIS")
        private String dosis;

        @JsonProperty("CA_MEDICAMENTO")
        private String cantidad;
    }

    @Data
    @Validated
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Procedicimiento {

        @JsonProperty("DE_TRATAMIENTO")
        private String tratamiento;

        @JsonProperty("TI_PROCEDIMIENTO")
        private String tipoProcedimiento;

        @JsonProperty("TI_HOSPITALIZACION")
        private String tipoHospitalizacion;

        @JsonProperty("CA_TIME_APROX_HOSP")
        private String tiempoAproxHospitaliazcion;

        @JsonProperty("DE_TECNICA_PROCEDIMIENTO")
        private String tecnicaProcedimiento;

        @JsonProperty("IN_TRANSF_SANGUINEA")
        private String requiereTransfSanguinea;

        @JsonProperty("DE_RECURSOS_REQUERIDO")
        private String recursosRequeridos;

        @JsonProperty("PROCEDIMIENTO")
        private String procedimiento;

        @JsonProperty("NU_PRIORIDAD")
        private String prioridad;

        @JsonProperty("DE_OTRO_PROCEDIMIENTO")
        private String otroProcedimiento;

        @JsonProperty("NO_PROCEDIMIENTO")
        private String nombreProcedimiento;

        @JsonProperty("DE_LUGAR_PROCEDIMIENTO")
        private String lugarProcedimiento;

        @JsonProperty("FE_PROB_PROCEDIMEINTO")
        private String fechaHoraProbProcedimiento;

        @JsonProperty("FE_PROB_HOSPITALIZACION")
        private String fechaHoraProbHospitalizacion;

        @JsonProperty("DE_EQUIPOS_MATERIAL")
        private String equiposMaterialesOtros;

        @JsonProperty("DE_INDICACIONES")
        private String comentIndicaciones;

        @JsonProperty("CO_SEGUS")
        private String codigoSegus;
    }
}
