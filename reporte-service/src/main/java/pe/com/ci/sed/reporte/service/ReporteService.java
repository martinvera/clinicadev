package pe.com.ci.sed.reporte.service;

import pe.com.ci.sed.reporte.model.generic.GenericRequest;
import pe.com.ci.sed.reporte.model.request.RequestReporte;

public interface ReporteService {

    public void generarReporteMecYMod(GenericRequest<RequestReporte> genericRequest);

    public void generarReporteExpError(GenericRequest<RequestReporte> genericRequest);

    public void generarReporteSinLote(GenericRequest<RequestReporte> genericRequest);

    public void generarReporteParcial(GenericRequest<RequestReporte> genericRequest);

    public void generarReporteEnviadoGarante(GenericRequest<RequestReporte> genericRequest);

    public void generarReporteTotal();
}
