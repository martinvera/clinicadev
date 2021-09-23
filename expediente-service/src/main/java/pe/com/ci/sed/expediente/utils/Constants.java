package pe.com.ci.sed.expediente.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class Constants {
    public static final String AZURE_STORAGE_ACCOUNT_URI = "https://%s.blob.core.windows.net";
    public static final String ZONE_ID = "America/Lima";
    public static final String TRANSACTION_ID = "transactionId";
    public static final String EXPEDIENTE_DIGITAL = "expediente-digital";
    public static ZoneId zoneId = ZoneId.of(ZONE_ID);
    public static final List<String> MODO_FACTURACION_INAFECTA = List.of("103", "104");

    public enum ORIGEN_SISTEMA {UNILAB, SALESFORCE, IAFAS, CONTROLDOCUMENTARIO}

    public static final String APPLICATION_ZIP = "application/zip";
    public static final String EXTENSION_ZIP = ".zip";
    public static final String EXTENSION_PDF = ".pdf";

    public static long getTimestamp() {
        return ZonedDateTime.now(zoneId).toInstant().toEpochMilli();
    }
}
