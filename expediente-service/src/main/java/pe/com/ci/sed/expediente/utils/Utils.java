package pe.com.ci.sed.expediente.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Utils {
	public static final String AZURE_STORAGE_ACCOUNT_URI = "https://%s.blob.core.windows.net";
	public static final String ZONE_ID = "America/Lima";
	public static ZoneId zoneId = ZoneId.of(ZONE_ID);
	
	public static long getTimestamp() {
		return ZonedDateTime.now(zoneId).toInstant().toEpochMilli();
	}
}
