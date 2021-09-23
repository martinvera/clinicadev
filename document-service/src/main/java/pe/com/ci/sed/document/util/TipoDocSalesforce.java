package pe.com.ci.sed.document.util;

public enum TipoDocSalesforce {
    GUIA_FARMACIA(Constants.DCA_013, "Guía de Farmacia"),
    GUIA_FARMACIA_AFECTO(Constants.DCA_013, "Guia de Farmacia Afecto"),
    GUIA_FARMACIA_INAFECTO(Constants.DCA_013, "Guia de Farmacia Inafecto"),
    ORDEN_IMAGEN_PATOLOGICO("DCA019", "Orden de Laboratorio / Imágenes"),
    ORDEN_HOSPITALIZACION("DCA021", "Orden de Procedimiento"),
    PASE_AMBULATORIO("DCA023", "Pase Ambulatorio "),
    ORDEN_ATENCION_MEDICA("DCA017", "Orden de Atención Médica (Hoja Siteds)"),
    ORDEN_FARMACIA("DCA018", "Orden de Farmacia");

    private final String codigo;
    private final String nombre;

    TipoDocSalesforce(String cddigo, String nombre) {
        this.codigo = cddigo;
        this.nombre = nombre;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public String getNombre() {
        return this.nombre.toUpperCase();
    }

    public String getName() {
        return this.name().toLowerCase();
    }

    private static class Constants {
        public static final String DCA_013 = "DCA013";
    }
}
