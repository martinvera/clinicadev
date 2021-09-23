package pe.com.ci.sed.reporte.utils;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;

public class Constants {

    public static final String CONTENT_TYPE_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	public static final String STORAGETABLECATALOGO = "storagetablecatalogo";
    public static final String STORAGETABLEEXPEDIENTE = "storagetableexpediente";

    public static final String PARAM_URL = "url";
    public static final String PARAM_NOMBRE = "nombre";

    public static final String REPORTES_ITEM_SIZE = "reportes-item-size: ";
    public static final String EXCEL_NO_GENERADO = "excel-no-generado: {}";

    public static final String NOMBRE_CARPETA_REPORTES = "reportes";
    public static final String EXCEL_FILE_EXT = ".xlsx";
	
    public enum ESTADO {PENDIENTE, GENERADO, ERROR, SINREGISTRO}

    public enum REPORTE {ENCUENTROSINLOTE, PARCIAL, TOTAL, EXPEDIENTECONERROR, ENVIADOGARANTE, EXPEDIENTEMECANISMO, EXPEDIENTEFACTURACION, OTROS, LOTEENVIADOGARANTE}

    protected static final String[] ENCUENTROSINLOTE = {"N° Encuentro", "Fecha Atención", "Paciente", "Garante", "Tipo Documento", "N° Documento", "Beneficio", "Importe", "Usuario"};
    protected static final String[] EXPEDIENTECONERROR = {"NRO LOTE", "NRO FACTURA", "FECHA CREACION", "PACIENTE", "GARANTE", "TIPO DOC", "NRO DOC", "BENEFICIO", "MENSAJE ERROR"};
    protected static final String[] REPORT_MEC_Y_MOD = {"MECANISMO FACTURACION", "MODO FACTURACION", "Cantidad", "Importe"};
    protected static final String[] REPORTE_TOTAL_PARCIAL = {"N° LOTE", "N° FACTURA", "N° ENCUENTRO", "PACIENTE", "GARANTE", "FECHA ATENCION", "FECHA EMISION FACTURA", "IMPORTE CON IGV", "MODO FACTURACION", "MECANISMO FACTURACION", "ESTADO FACTURA", "ESTADO LOTE", "FECHA ACEPTACION"};
    protected static final String[] REPORTE_ENVIADO_GARANTE = {"N° LOTE", "GARANTE", "FECHA LOTE", "CANTIDA DE FACTURAS", "FECHA DE ENVIO", "FECHA DE RECHAZO", "ESTADO", "FECHA DE ACEPTACION", "REGISTRO SOLICITUD"};
    protected static final String[] otros = {};

    public static final String AZURE_STORAGE_ACCOUNT_URI = "https://%s.blob.core.windows.net";
    
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TipoGarante {
        public static final String RIMAC_EPS = "RIMAC EPS";
        public static final String RIMAC_SEGUROS = "RIMAC SEGUROS";
        public static final String OTROS_GARANTES = "OTROS GARANTES";
    }
}
