package pe.com.ci.sed.reporte.utils;


public enum TipoReporte {

    ENVIADOGARANTE("ENVIADOGARANTE", Constants.REPORTE_ENVIADO_GARANTE),
    ENCUENTROSINLOTE("ENCUENTROSINLOTE", Constants.ENCUENTROSINLOTE),
    EXPEDIENTECONERROR("EXPEDIENTECONERROR", Constants.EXPEDIENTECONERROR),
    EXPEDIENTEMECANISMO("EXPEDIENTEMECANISMO", Constants.REPORT_MEC_Y_MOD),
    PARCIAL("PARCIAL", Constants.REPORTE_TOTAL_PARCIAL),
    TOTAL("TOTAL", Constants.REPORTE_TOTAL_PARCIAL),
    EXPEDIENTEFACTURACION("EXPEDIENTEFACTURACION", Constants.ENCUENTROSINLOTE),
    OTROS("OTROS", Constants.otros);


    private final String nombre;
    private final String[] cabeceras;

    TipoReporte(String cddigo, String[] cabeceras) {
        this.nombre = cddigo;
        this.cabeceras = cabeceras;
    }

    public String getNombre() {
        return this.nombre;
    }

    public String[] getCabeceras() {
        return this.cabeceras;
    }
}
