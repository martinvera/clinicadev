package pe.com.ci.sed.expediente.service;

import java.util.List;

import pe.com.ci.sed.expediente.model.request.GenerarExpedienteRequest;
import pe.com.ci.sed.expediente.model.request.GenericRequest;

public interface GenerarExpedienteService {
    public Object iniciarProcesoExpediente(GenericRequest<List<GenerarExpedienteRequest>> request);
}
