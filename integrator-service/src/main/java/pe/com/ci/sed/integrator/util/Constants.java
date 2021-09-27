package pe.com.ci.sed.integrator.util;

public class Constants {
    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String ZONE_ID = "America/Lima";

    public static final String UNILAB_FECHA_MSG = "YYYYMMDDHHMMSS";
    public static final String UNILAB_FECHA_TRX = "YYYYMMDDHHMMSS";
    public static final String UNILAB_FECHA_EFEC_ORDEN = "YYYYMMDDHHMMSS";
    public static final String IAFAS_FORMAT_DATE = "yyyy-MM-dd";
    public static final String IAFAS_FORMAT_HOUR = "hh:mm:ss";
    public static final String TRANSACTION_ID = "transactionId";
    public static final String PARAM_STATUS = "STATUS";
    public static final String PARAM_CO_RESPUESTA = "co_respuesta";
    public static final String PARAM_DE_RESPUESTA = "de_respuesta";
    public static final String ERROR_EN = "Error en {} : {} ";
    public static final String ERROR_INESPERADO = "Error inesperado : {} ";
}
