package pe.com.ci.sed.reporte.service;

import pe.com.ci.sed.reporte.model.generic.GenericRequest;
import pe.com.ci.sed.reporte.model.request.RequestReporte;
import pe.com.ci.sed.reporte.persistence.entity.Historial;

public interface HistorialService {

    public Historial registrarHistorial(GenericRequest<Historial> genericRequest);

    public Historial registrarHistorial(Historial historial);

    public void actualizarHistorial(Historial genericRequest);

    public Object listarHistorial(GenericRequest<RequestReporte> request);

}
