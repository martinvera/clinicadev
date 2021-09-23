package pe.com.ci.sed.clinicalrecord.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.List;

public class Constants {

    public static final String NOMBRE_ARCHIVO_S_S_S = "%s_%s%s";
    public static final String RUTA_ARCHIVO_S_S_S_S = "%s/%s/%s/%s";

    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String IAFAS_FORMAT_DATE = "yyyy-MM-dd";
    public static final String IAFAS_FORMAT_HOUR = "hh:mm:ss";
    public static final String AZURE_STORAGE_ACCOUNT_URI = "https://%s.blob.core.windows.net";
    public static final List<Long> MODO_FACTURACION_INAFECTA = List.of(103L, 104L);

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Filtro {
        public static final String T_23_59_59_000_Z = "T23:59:59.000Z'";
        public static final String T_00_00_00_000_Z = "T00:00:00.000Z'";
        public static final String AND_TIMESTAMP_GE_DATETIME = " and Timestamp ge datetime'";
        public static final String AND_TIMESTAMP_LT_DATETIME = " and Timestamp lt datetime'";
        public static final String AND_ESTADO_EQ = " and Estado eq '";
    }

    public enum ESTADO {EXPEDIENTE_PENDIENTE, EXPEDIENTE_GENERADO}

    public enum ESTADO_FACTURA {INCOMPLETO, COMPLETO}

    public enum ESTADO_GARANTE {PENDIENTE, ENVIADO, ACEPTADO}
}
