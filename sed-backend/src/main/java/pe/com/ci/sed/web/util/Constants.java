package pe.com.ci.sed.web.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

public class Constants {
    public static final String PARAM_HEADER = "header";
    public static final String FORMAT_S_S_S = "%s %s %s";

    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String DEFAULT_TAMANIO = "20";
    public static final String PARAM_ADMIN = "admin";

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Estado {
        public static final String ACTIVO = "1";
        public static final String INACTIVO = "0";
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TableStorage {
        public static final String CATALOGO = "storagetablecatalogo";
        public static final String GARANTEMECANISMO = "storagetableGaranteMecanismo";
    }
}
