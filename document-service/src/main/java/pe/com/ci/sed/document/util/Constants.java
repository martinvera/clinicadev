package pe.com.ci.sed.document.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

public class Constants {

    public static final String QUERY = "query = ";
    public static final String MESSAGE = "datos : {}";
    public static final String OCURRIO_UN_ERROR_EN_LA_CARGA_DEL_DOCUMENTOS = "Ocurrió un error en la carga del documentos";

    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String UNILAB_FECHA_TRX = "YYYYMMDDHHMMSS";
    public static final String IAFAS_FORMAT_DATE = "yyyy-MM-dd";
    public static final String IAFAS_FORMAT_HOUR = "hh:mm:ss";
    public static final String FORMAT_HORA = "00:00:00";
    public static final String AZURE_STORAGE_ACCOUNT_URI = "https://%s.blob.core.windows.net";

    /**
     * Constantes de valores estáticos
     */

    public static final String VALUE_ERROR = "ERROR";
    public static final String VALUE_OK = "OK";
    public static final String VALUE_PENDIENTE = "PENDIENTE";

    /**
     * Constantes de extención de archivos
     */

    public static final String PDF_FILE_EXT = ".pdf";
    public static final String JSON_FILE_EXT = ".json";

    /**
     * Constantes de Param
     */

    public static final String PARAM_FIRMAMEDICO = "FIRMAMEDICO";

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class TipoDetalle {
        public static final String PROCEDIMIENTO = "Procedimiento";
        public static final String EXCEPCIONES = "Excepciones";
        public static final String PRE_EXISTENCIAS = "Pre Existencias";
    }

    public enum ESTADO_FACTURA {INCOMPLETO, COMPLETO}

    public enum ORIGEN_SISTEMA {UNILAB, SALESFORCE, IAFAS, CONTROLDOCUMENTARIO, ENTERPRISEIMAGING}

    public static final String NOMBRE_CARPETA_DOCUMENTOS = "documentos";
    public static final String NOMBRE_CARPETA_INTEGRACION = "integracion";
}
