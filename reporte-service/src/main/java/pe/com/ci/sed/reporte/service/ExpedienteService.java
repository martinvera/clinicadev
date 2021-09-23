package pe.com.ci.sed.reporte.service;

import pe.com.ci.sed.reporte.model.generic.GenericRequest;
import pe.com.ci.sed.reporte.model.request.RequestReporte;

public interface ExpedienteService {

    public String validarNroLote(GenericRequest<RequestReporte> genericRequest);
    
}
