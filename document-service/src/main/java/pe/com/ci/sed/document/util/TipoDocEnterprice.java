package pe.com.ci.sed.document.util;

public enum TipoDocEnterprice {
    INFORME_DE_IMAGENES("DCA030", "Informe de Imágenes");

    private final String codigo;
    private final String nombre;

    TipoDocEnterprice(String codigo, String nombre) {
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
