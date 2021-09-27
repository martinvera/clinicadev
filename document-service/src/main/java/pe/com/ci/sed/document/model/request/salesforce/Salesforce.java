package pe.com.ci.sed.document.model.request.salesforce;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.ci.sed.document.model.validacion.*;

@Data
@Validated
@AllArgsConstructor
@NoArgsConstructor
public class Salesforce {

    @NotNull
    @JsonProperty("NU_ENCUENTRO")
    private @Valid Encounter encounter;

    @JsonProperty("DE_RESPUESTA")
    private String descRespuesta;

    @JsonProperty("CO_RESPUESTA")
    private String codRespuesta;

    @Data
    @Validated
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Encounter {
        private Boolean ispatologico;
        private Boolean isquirurgico;

        @JsonProperty("VA_TOT_INAFECTO")
        private String totalInafecto;

        @JsonProperty("VA_TOT_AFECTO")
        private String totalAfecto;

        @JsonProperty("TI_DOC_TITULAR")
        @NotEmpty(message = "El campo Tipo de Documento del Titular está vacio", groups = {OrdenAtencionMedica.class})
        private String tipoDocTitular;

        @JsonProperty("TI_DOC_PACIENTE")
        @NotEmpty(message = "El campo Tipo de Documento del Paciente está vacio", groups = {OrdenProcedimiento.class, GuiaFarmacia.class, PaseAmbulatorio.class, FormatoFarmacia.class, OrdenHospitalizacion.class, OrdenAtencionMedica.class})
        private String tipoDocPaciente;

        @JsonProperty("TI_CITA_PACIENTE")
        @NotEmpty(message = "El campo Tipo de Cita (Virtual, Presencial, etc) está vacio", groups = {PaseAmbulatorio.class})
        private String tipoCita;

        @JsonProperty("TI_AFILIACION")
        @NotEmpty(message = "El campo Tipo de Afiliación está vacio", groups = {OrdenAtencionMedica.class})
        private String tipoAfiTitular;

        @JsonProperty("IN_PROCEDIMIENTOS")
        @NotEmpty(message = "El campo Se practicó algún procedimiento quirurgico en consulta  está vacio", groups = {OrdenAtencionMedica.class})
        private String tieneProcedimientos;

        @JsonProperty("IN_INTERCONSULTAS")
        @NotEmpty(message = "El campo Ordenó usted interconsultas con especialista  está vacio", groups = {OrdenAtencionMedica.class})
        private String tieneInterconsultas;

        @JsonProperty("IN_EXAMENES")
        @NotEmpty(message = "El campo Tiene exámenes está vacio", groups = {OrdenAtencionMedica.class})
        private String tieneExamenes;

        @JsonProperty("IN_DIAGNOSTICOS")
        @NotEmpty(message = "El campo Diagnósticos está vacio", groups = {OrdenAtencionMedica.class})
        private String tieneDiagnosticos;

        @JsonProperty("IN_ANTECEDENTES")
        @NotEmpty(message = "El campo Antecedentes está vacio", groups = {OrdenAtencionMedica.class})
        private String tieneAntecedentes;

        @JsonProperty("CA_TIME_ENFERMEDAD")
        //TODO @NotEmpty(message = "El campo Tiempo Enfermedad está vacio", groups = {OrdenProcedimiento.class, PaseAmbulatorio.class, OrdenHospitalizacion.class})
        private String tiempoEnfermedad;

        @JsonProperty("NU_TELEF_PACIENTE")
        //TODO @NotEmpty(message = "El campo Telefono del Paciente está vacio", groups = {GuiaFarmacia.class})
        private String telefonoPaciente;

        @JsonProperty("NU_TELEF_CI")
        @NotEmpty(message = "El campo Telefono de Clínica Internacional está vacio", groups = GuiaFarmacia.class)
        private String telefonoCI;

        @JsonProperty("VA_SUBTOT_INAFECTO")
        private String subTotalInafecto;

        @JsonProperty("VA_SUBTOT_AFECTO")
        private String subTotalAfecto;

        @JsonProperty("DE_SINTOMAS_PRIN")
        //TODO @NotEmpty(message = "El campo Síntomas Principales está vacio", groups = {PaseAmbulatorio.class})
        private String sintomasPrincipales;

        @JsonProperty("DE_SIGNOS_PRIN")
        //TODO @NotEmpty(message = "El campo Signos Principales está vacio", groups = {PaseAmbulatorio.class})
        private String signosPrincipales;

        @JsonProperty("DE_SEXO_PACIENTE")
        @NotEmpty(message = "El campo Sexo está vacio", groups = {OrdenProcedimiento.class, PaseAmbulatorio.class, OrdenHospitalizacion.class})
        private String sexo;

        @JsonProperty("DE_SEDE")
        @NotEmpty(message = "El campo Sede está vacio", groups = {OrdenProcedimiento.class, FormatoFarmacia.class, OrdenHospitalizacion.class})
        private String sede;

        @JsonProperty("NU_RUC_CI")
        @NotEmpty(message = "El campo Ruc de CI está vacio", groups = {GuiaFarmacia.class})
        private String rucCI;

        @JsonProperty("DE_RESTRIC_BENE")
        private String restriccionesBeneficio;

        @JsonProperty("DE_RELATO_MEDICO")
        //TODO @NotEmpty(message = "El campo Relato está vacio", groups = {PaseAmbulatorio.class, OrdenHospitalizacion.class})
        private String relato;

        @JsonProperty("DE_REFER_PACIENTE")
        //TODO @NotEmpty(message = "El campo Referencia está vacio", groups = {GuiaFarmacia.class})
        private String referenciaPaciente;

        @JsonProperty("DE_PRODUCTO")
        @NotEmpty(message = "El campo Producto está vacio", groups = {OrdenProcedimiento.class, GuiaFarmacia.class, PaseAmbulatorio.class, FormatoFarmacia.class, OrdenHospitalizacion.class, OrdenAtencionMedica.class})
        private String producto;

        @JsonProperty("DE_PLAN_SALUD")
        @NotEmpty(message = "El campo Plan de Salud está vacio", groups = {OrdenAtencionMedica.class})
        private String planSalud;

        @JsonProperty("DE_PLAN_CLINICA")
        @NotEmpty(message = "El campo Plan Clínica está vacio", groups = {PaseAmbulatorio.class})
        private String planClinica;

        @JsonProperty("IN_PARTICULAR")
        private String particular;

        @JsonProperty("DE_PARENTESCO_PACIENTE")
        @NotEmpty(message = "El campo Parentesco está vacio", groups = {PaseAmbulatorio.class})
        private String parentesco;

        @JsonProperty("OBS_COND_ESPECIAL")
        private String observCondEspecBen;

        // TODO Observaciones PASE AMBULATORIO
        @JsonProperty("OBS_ASEGURADO")
        private String observAsegurado;

        @JsonProperty("OBS_ADICIONAL")
        private String observAdicional;

        @JsonProperty("NU_CONTRATO")
        @NotEmpty(message = "El campo Contrato está vacio", groups = {PaseAmbulatorio.class})
        private String numContrato;

        @JsonProperty("NU_SOLIC_ORIGEN")
        private String nroSolicitudOrigen;

        @JsonProperty("NU_ORDEN_INAFECTO")
        private String nroOrdenInafecto;

        @JsonProperty("NU_ORDEN_AFECTO")
        private String nroOrdenAfecto;

        @JsonProperty("NU_ENCUENTRO")
        @NotEmpty(message = "El campo Admisión está vacio", groups = {OrdenProcedimiento.class, GuiaFarmacia.class, PaseAmbulatorio.class, FormatoFarmacia.class, OrdenHospitalizacion.class, OrdenAtencionMedica.class, SalesforceValidator.class})
        private String nroEnc;

        @JsonProperty("NU_DOC_TITULAR")
        private String nroDocTitular;

        @JsonProperty("NU_DOC_PACIENTE")
        @NotEmpty(message = "El campo Nro de documento está vacio", groups = {OrdenProcedimiento.class, GuiaFarmacia.class, PaseAmbulatorio.class, FormatoFarmacia.class, OrdenHospitalizacion.class, OrdenAtencionMedica.class})
        private String nroDocPaciente;

        @JsonProperty("NU_DECLARA_ACCIDENTE")
        private String nroDeclAccidente;

        @JsonProperty("NU_AUTORIZACION")
        @NotEmpty(message = "El campo N° Autorización está vacio", groups = {PaseAmbulatorio.class})
        private String nroAutorizacion;

        @JsonProperty("NU_ASEGURADO")
        @NotEmpty(message = "El campo Nro de Asegurado está vacio", groups = {OrdenAtencionMedica.class})
        private String nroAsegurado;

        @JsonProperty("NO_TITULAR")
        @NotEmpty(message = "El campo Titular está vacio", groups = {GuiaFarmacia.class, PaseAmbulatorio.class})
        private String nomTitular;

        @JsonProperty("NO_PACIENTE")
        @NotEmpty(message = "El campo Nombre del Paciente está vacio", groups = {OrdenProcedimiento.class, GuiaFarmacia.class, PaseAmbulatorio.class, FormatoFarmacia.class, OrdenHospitalizacion.class, OrdenAtencionMedica.class})
        private String nombrePaciente;

        @JsonProperty("NO_MEDICO")
        @NotEmpty(message = "El campo Médico está vacio", groups = {OrdenProcedimiento.class, PaseAmbulatorio.class, FormatoFarmacia.class, OrdenHospitalizacion.class, OrdenAtencionMedica.class})
        private String nombreMedico;

        @JsonProperty("NO_COMERCIAL_CI")
        private String nombreComercialCI;

        @JsonProperty("NU_CEL_PACIENTE")
        @NotEmpty(message = "El campo Movil del Paciente vacio", groups = {GuiaFarmacia.class})
        private String movilPaciente;

        @JsonProperty("DE_MONEDA_POLIZA")
        @NotEmpty(message = "El campo Moneda vacio", groups = {OrdenAtencionMedica.class})
        private String monedaTitular;

        @JsonProperty("VA_IGV_INAFECTO")
        private String igvInafecto;

        @JsonProperty("VA_IGV_AFECTO")
        private String igvAfecto;

        @JsonProperty("HO_ENCUENTRO")
        @NotEmpty(message = "El campo Hora de la cita vacio", groups = {OrdenProcedimiento.class, PaseAmbulatorio.class, FormatoFarmacia.class, OrdenHospitalizacion.class})
        private String horaEnc;

        @JsonProperty("NU_HHCC_PACIENTE")
        @NotEmpty(message = "El campo N° H/C vacio", groups = {OrdenProcedimiento.class, PaseAmbulatorio.class, FormatoFarmacia.class, OrdenHospitalizacion.class})
        private String hhccPaciente;

        @JsonProperty("NO_GARANTE")
        @NotEmpty(message = "El campo Garante vacio", groups = {OrdenProcedimiento.class, GuiaFarmacia.class, PaseAmbulatorio.class, FormatoFarmacia.class, OrdenHospitalizacion.class})
        private String garante;

        @JsonProperty("FE_ULTI_MENSTRUACION")
        private String fum;

        @JsonProperty("FIRMA_MEDICO")
        //TODO @NotEmpty(message = "El campo Firma del médico vacio", groups = {OrdenProcedimiento.class, PaseAmbulatorio.class, FormatoFarmacia.class, OrdenHospitalizacion.class, OrdenAtencionMedica.class})
        private String firmaMedico;

        @JsonProperty("FE_FIN_VIGENCIA")
        private String finVigPaciente;

        @JsonProperty("FE_FIN_CARENCIA")
        private String finCarencia;

        @JsonProperty("FE_VENCE_ENCUENTRO")
        @NotEmpty(message = "El campo Vence vacio", groups = {PaseAmbulatorio.class})
        private String fechaVenc;

        @JsonProperty("FE_NAC_SF")
        private String fechaNacSF;

        @JsonProperty("FE_NAC_PACIENTE")
        @NotEmpty(message = "El campo Fecha de Nacimiento vacio", groups = {PaseAmbulatorio.class})
        private String fechaNac;

        @JsonProperty("FE_AUTORIZACION")
        @NotEmpty(message = "El campo Fecha Y Hora Autorización vacio", groups = {OrdenAtencionMedica.class})
        private String fechaHoraAutorizacion;

        @JsonProperty("CO_BENEFICIO")
        @NotEmpty(message = "El campo Código del Beneficio vacio", groups = {OrdenAtencionMedica.class})
        private String codigoBeneficio;

        @JsonProperty("FE_ENCUENTRO")
        @NotEmpty(message = "El campo Fecha Adm / Encuentro vacio", groups = {OrdenProcedimiento.class, GuiaFarmacia.class, PaseAmbulatorio.class, FormatoFarmacia.class, OrdenHospitalizacion.class})
        private String fechaEnc;
        private String fechaEncDesc;

        @JsonProperty("ES_POLIZA")
        @NotEmpty(message = "El campo Estado Poliza vacio", groups = {PaseAmbulatorio.class})
        private String estadoPoliza;

        @JsonProperty("ES_PAGO")
        private String estadoOrden;

        @JsonProperty("DE_ESTADO_CIVIL")
        //TODO @NotEmpty(message = "El campo Estado Civil vacio", groups = {OrdenAtencionMedica.class})
        private String estadoCivilPaciente;

        @JsonProperty("DE_ESPECIALIDAD")
        @NotEmpty(message = "El campo Especialidad de Interconsulta vacio", groups = {OrdenProcedimiento.class, PaseAmbulatorio.class, FormatoFarmacia.class, OrdenHospitalizacion.class, OrdenAtencionMedica.class})
        private String especialidad;

        @JsonProperty("CA_EDAD_PACIENTE_SF")
        private String edadPacienteSF;

        @JsonProperty("CA_EDAD_PACIENTE")
        @NotEmpty(message = "El campo Edad vacio", groups = {PaseAmbulatorio.class, OrdenAtencionMedica.class})
        private String edadPaciente;

        @JsonProperty("CA_EDAD_GESTACIONAL")
        private String edadGestacional;

        @JsonProperty("NO_DISTRITO_PACIENTE")
        //TODO @NotEmpty(message = "El campo Distrito vacio", groups = {GuiaFarmacia.class})
        private String distritoPaciente;

        @JsonProperty("NO_DISTRITO_CI")
        private String distritoCI;

        @JsonProperty("NO_DIREC_PACIENTE")
        //TODO @NotEmpty(message = "El campo Dirección vacio", groups = {GuiaFarmacia.class})
        private String direccionPaciente;

        @JsonProperty("NO_DIREC_CI")
        private String direccionCI;

        @JsonProperty("DE_DAT_CLINICO")
        @NotEmpty(message = "El campo Datos Clínicos (Motivo de Consulta) vacio", groups = {OrdenProcedimiento.class, OrdenHospitalizacion.class})
        private String datosClinicos;

        @JsonProperty("FE_INI_VIGENCIA")
        @NotEmpty(message = "El campo Inicio Vigencia vacio", groups = {OrdenAtencionMedica.class})
        private String inicioVigPaciente;

        @JsonProperty("CO_PAGO_VAR_COASEGURO")
        @NotEmpty(message = "El campo Coaseguro / Copago Variable vacio", groups = {PaseAmbulatorio.class})
        private String copagoVariableCoaseguro;

        @JsonProperty("CO_PAGO_FIJO_DED")
        @NotEmpty(message = "El campo Copago Fijo / Deducible vacio", groups = {PaseAmbulatorio.class})
        private String copagoFijoDeducible;

        @JsonProperty("NO_COMPANIA_CONTRATA")
        @NotEmpty(message = "El campo Compañía vacio", groups = {OrdenProcedimiento.class, GuiaFarmacia.class, PaseAmbulatorio.class, FormatoFarmacia.class, OrdenHospitalizacion.class, OrdenAtencionMedica.class})
        private String compania;

        @JsonProperty("CO_CMP")
        //TODO @NotEmpty(message = "El campo CMP vacio", groups = {OrdenProcedimiento.class, PaseAmbulatorio.class, FormatoFarmacia.class, OrdenHospitalizacion.class, OrdenAtencionMedica.class})
        private String cmp;

        @JsonProperty("CO_CM")
        private String cm;

        @JsonProperty("DE_CIUDAD_CI")
        private String ciudadCI;

        @JsonProperty("NU_CARNET_CI")
        private String carne;

        @JsonProperty("DE_BENEFICIO")
        @NotEmpty(message = "El campo beneficio vacio", groups = {OrdenProcedimiento.class, GuiaFarmacia.class, PaseAmbulatorio.class, FormatoFarmacia.class, OrdenHospitalizacion.class, OrdenAtencionMedica.class})
        private String beneficio;

        @JsonProperty("OBJ_ORDEN_MEDICA")
        List<@Valid OrderItem> orderItems;

        @JsonProperty("OBJ_INTERCONSULTAS")
        List<@Valid InterConsulta> interconsultas;

        @JsonProperty("OBJ_DIAGNOSTICO")
        List<@Valid Diagnostico> diagnosticos;

        @JsonProperty("OBJ_SITEDS")
        List<@Valid DatoSited> datosSiteds;

        List<DatoSited> copagos;

        List<Orden> existencias;

        @JsonProperty("OBJ_ANTEC_QUIRURGICOS")
        List<@Valid AntecedenteQuirurgico> antecedentesQuirurgicos;

        @JsonProperty("OBJ_ANTEC_PER_MENOR")
        List<@Valid AntecedentePersonal> antecedentesPersonalMenor;

        @JsonProperty("OBJ_ANTEC_PATOLOGICO")
        List<@Valid AntecedentePatologico> antecedentesPatologicos;

        @JsonProperty("OBJ_ANTEC_GINECOOBS")
        List<@Valid AntecedenteGineco> antecedentesGinecoObstetricos;

        List<Orden> ordenes = new ArrayList<>();

    }

    @Data
    @Validated
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AntecedenteGineco {

        @JsonProperty("IN_VIH_SIDA")
        private String vihSida;

        @JsonProperty("IN_TBC_PULMONAR")
        private String tbcPulmonar;

        @JsonProperty("IN_RETRASO_MENSTRUAL")
        private String retrasoMenstrual;

        @JsonProperty("DE_RESULT_ULT_PAP")
        private String resultUltimoPap;

        @JsonProperty("DE_RESULT_ULT_MAM")
        private String resultUltimaMamografia;

        @JsonProperty("IN_RELACIONES_SEX")
        private String relacionesSexuales;

        @JsonProperty("CA_REGIMEN_CATAMERIAL")
        private String regimenCatamenial;

        @JsonProperty("IN_PREECLAMPSIA")
        private String preEclampsia;

        @JsonProperty("NU_PARTOS")
        private String p;

        @JsonProperty("DE_OTRO_RESULTADO")
        private String otrosResultados;

        @JsonProperty("DE_OTRO_ANTECEDENTE")
        private String otros;

        @JsonProperty("NU_HIJO_NACVIVO")
        private String nv;

        @JsonProperty("NU_HIJO_NACFALLECE")
        private String nm;

        @JsonProperty("IN_METODO_ANTICONC")
        private String metodoAnticonceptivo;

        @JsonProperty("CA_EDAD_MENOPAUSIA")
        private String menopausia;

        @JsonProperty("CA_EDAD_MENARQUIA")
        private String menarquiaEdad;

        @JsonProperty("DE_HIV")
        private String hiv;

        @JsonProperty("IN_HEMO_POSTPARTO")
        private String hemorragiaPostparto;

        @JsonProperty("NU_GESTACIONES")
        private String g;

        @JsonProperty("FE_ULTI_MENSTRUACION")
        @NotEmpty(message = "El campo FUM vacio", groups = {OrdenProcedimiento.class, OrdenProcedimiento.class})
        private String fum;

        @JsonProperty("FE_ULT_PAP")
        private String fechaUltimoPap;

        @JsonProperty("FE_ULT_MAM")
        private String fechaUltimaMamografia;

        @JsonProperty("FE_PROB_PARTO")
        private String fechaProbableParto;

        @JsonProperty("DE_COVID19")
        private String covid19;

        @JsonProperty("DE_CIRUGIA_PELV")
        private String cirugiaPelvUterina;

        @JsonProperty("DE_CARDIOPATIA")
        private String cardiopatia;

        @JsonProperty("DE_BK_ESPUTO")
        private String bkEsputoHiv;

        @JsonProperty("DE_ABORTO")
        private String abortoRecurrente;

        @JsonProperty("NU_ABORTOS")
        private String a;
    }

    @Data
    @Validated
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AntecedentePatologico {

        @JsonProperty("NO_MEDICACION")
        private String medicacion;

        @JsonProperty("NO_ENFERMEDAD")
        private String enfermedad;

        @JsonProperty("DE_ORDEN_MEDICA")
        private String descripcion;
    }

    @Data
    @Validated
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AntecedentePersonal {

        @JsonProperty("TI_PARTO")
        private String parto;

        @JsonProperty("NO_PADRE")
        private String nombrePadre;

        @JsonProperty("NO_MADRE")
        private String nombreMadre;

        @JsonProperty("DE_MOTIVO_CESAREA")
        private String motivoCesarea;

        @JsonProperty("DE_INDICE_APGAR")
        private String indiceApgar;

        @JsonProperty("DE_DESA_PSICOMOTOR")
        private String descDesarrolloPsicomotor;

        @JsonProperty("DE_ALIMENTACION")
        private String descAlimentacion;

        @JsonProperty("TI_DESA_PSICOMOTOR")
        private String desaPsicomotor;

        @JsonProperty("TI_ALIMENTA_MENOR")
        private String alimentacion;
    }


    @Data
    @Validated
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItem {

        @JsonProperty("VA_PRECIO_UNIT")
        @NotEmpty(message = "El campo Precio Unitario vacio", groups = {GuiaFarmacia.class})
        private String precioUnitario;

        @JsonProperty("IN_INAFECTO")
        @NotEmpty(message = "El campo Identificador de Inafecto vacio", groups = {GuiaFarmacia.class})
        private String isInafecto;

        @JsonProperty("VA_IMPORTE_TOTAL")
        @NotEmpty(message = "El campo Importe vacio", groups = {GuiaFarmacia.class})
        private String importe;

        @JsonProperty("DE_ORDEN_MEDICA")
        @NotEmpty(message = "El campo Descripción de Medicamento vacio", groups = {GuiaFarmacia.class})
        private String descripcion;

        @JsonProperty("CO_ORDEN_MEDICA")
        @NotEmpty(message = "El campo Código de Medicamento vacio", groups = {GuiaFarmacia.class})
        private String codigo;

        @JsonProperty("CA_ORDEN_MEDICA")
        @NotEmpty(message = "El campo Cantidad vacio", groups = {GuiaFarmacia.class, FormatoFarmacia.class})
        private String cantidad;
    }

    @Data
    @Validated
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InterConsulta {

        @JsonProperty("NO_MED_INTERCONSULTA")
        private String nomMedInter;

        @JsonProperty("FE_INTERCONSULTA")
        private String fechaInter;

        @JsonProperty("DE_ESPEC_INTERCONSULTA")
        private String especInter;
    }

    @Data
    @Validated
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DatoSited {

        @JsonProperty("TI_DETALLE_SERVICIO")
        private String tipoDetalle;

        @JsonProperty("CA_TIME_COPAGO")
        private String procTiemDifServ;

        @JsonProperty("NO_PROC_COPAGO")
        private String procNombreDifServ;

        private String procGeneroDifServ;

        @JsonProperty("CA_FREC_SERVICIO")
        private String procFrecDifServ;

        @JsonProperty("CO_PAGO_VARIABLE")
        private String procCopVarDifServ;

        @JsonProperty("CO_PAGO_FIJO")
        private String procCopFijDifServ;

        @JsonProperty("CO_PROCEDIMIENTO")
//        @NotEmpty(message = "Codigo de Procedimiento - Servicio Diferenciado", groups = {PaseAmbulatorio.class})
        private String procCodigoDifServ;

        @JsonProperty("OBS_COPAGO_DIF")
        private String observacionDifServ;

        @JsonProperty("FE_FIN_ESPERA")
        private String fechaFinalDifServ;

        @JsonProperty("DE_PREXISTENCIA")
        private String descripcionDifServ;

        @JsonProperty("CO_CIE10_COPAGO_DIF")
        private String codigoDifServ;
    }

    @Data
    @Validated
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AntecedenteQuirurgico {

        @JsonProperty("DE_NOTA_PROCEDIMIENTO")
        private String notasProcedimiento;

        @JsonProperty("FE_REALIZACION")
        private String fechaRealizacion;

        @JsonProperty("NO_CIE10")
        private String cie10;
    }
}
