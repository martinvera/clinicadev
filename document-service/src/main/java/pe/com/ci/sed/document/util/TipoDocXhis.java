package pe.com.ci.sed.document.util;

public enum TipoDocXhis {
    RECETA_MEDICA("DCA018", "Orden de Farmacia"),
    ORDEN_FARMACIA("DCA013", "Guia de Farmacia"),
    GUIA_FARMACIA("DCA037", "Guía de Farmacia 2"),
    PASE_AMBULATORIO("DCA023", "Pase Ambulatorio"),
    ORDEN_PROCEDIMIENTO("DCA021", "Orden de Procedimiento"),
    ORDEN_LABORATORIO("DCA019", "Orden de Laboratorio/Imágenes"),
    BOLETA_FARMACIA("DCA038", "Boleta de Farmacia");

    private final String codigo;
    private final String nombre;

    TipoDocXhis(String codigo, String nombre) {
        this.codigo = codigo;
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
}
