package pe.com.ci.sed.document.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import pe.com.ci.sed.document.model.generic.RequestHeader;

@Data
public class RegistrarDocFromUnilabRequest {

	private RequestHeader header;
	private DocumentCreateRequest request;
	
	@Data
	public static class DocumentCreateRequest{
		private Cabecera cabecera;
		private Detalle[] detalle;
	}
	
	@Data
	public static class Detalle {
		@JsonProperty("ti_valor")
		private String tiValor;
		
		@JsonProperty("co_prueba")
		private String coPrueba;
		
		@JsonProperty("de_prueba")
		private String dePrueba;
		
		@JsonProperty("no_laboratorio")
		private String noLaboratorio;
		
		@JsonProperty("va_observacion")
		private String vaObservacion;
		
		@JsonProperty("un_medida")
		private String unMedida;
		
		@JsonProperty("rang_min_max")
		private String rangMinMax;
		
		@JsonProperty("in_tendencia")
		private String inTendencia;
		
		@JsonProperty("ti_fila")
		private String tiFila;
		
		@JsonProperty("in_patologia")
		private String inPatologia;
		
		@JsonProperty("fe_observacion")
		private String feObservacion;
		
		@JsonProperty("co_respon_observacion")
		private String coResponObservacion;
		
		@JsonProperty("de_observacion")
		private String deObservacion;
		
		
	}
	
	@Data
	public static class Cabecera {
		
		@JsonProperty("co_origen_app")
		private String coOrigenApp;
		
		@JsonProperty("fe_mensaje")
		private String feMensaje;
		
		@JsonProperty("co_trx")
		private String coTrx;
		
		@JsonProperty("nu_hclinica_pac")
		private String nuHclinicapac;
		
		@JsonProperty("nu_documento_pac")
		private String nuDocumentoPac;
		
		@JsonProperty("ti_documento_pac")
		private String tiDocumentoPac;
		
		@JsonProperty("ape_paterno_pac")
		private String apePaternoPac;
		
		@JsonProperty("nom_pac")
		private String nomPac;
		
		@JsonProperty("ape_materno_pac")
		private String apeMaternoPac;
		
		@JsonProperty("co_sexo_pac")
		private String coSexoPac;
		
		@JsonProperty("co_origen_pac")
		private String coOrigenPac;
		
		@JsonProperty("co_serv_origen")
		private String coServOrigen;
		
		@JsonProperty("co_cama_pac")
		private String coCamaPac;
		
		@JsonProperty("co_sede")
		private String coSede;
		
		@JsonProperty("co_cmp")
		private String coCmp;
		
		@JsonProperty("co_serv_destino")
		private String coServDestino;
		
		@JsonProperty("de_serv_destino")
		private String deServDestino;
		
		@JsonProperty("nu_encuentro")
		private String nuEncuentro;
		
		@JsonProperty("id_peticion_his")
		private String idPeticionHis;
		
		@JsonProperty("id_peticion_lis")
		private String idPeticionLis;
		
		@JsonProperty("fe_trx")
		private String feTrx;
		
		@JsonProperty("resultado_pdf")
		private String resultadoPdf;
		
		@JsonProperty("fe_efectiva_orden")
		private String feEfectivaOrden;
		
		@JsonProperty("co_serv_solicita")
		private String coServSolicita;
		
		@JsonProperty("de_serv_solicita")
		private String deServSolicita;
		
		private String tipoDocumentoDesc;
    	private String tipoDocumentoId;
	}
}
