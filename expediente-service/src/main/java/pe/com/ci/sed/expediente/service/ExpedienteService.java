package pe.com.ci.sed.expediente.service;

import java.net.URISyntaxException;

import com.microsoft.azure.storage.StorageException;

import pe.com.ci.sed.expediente.model.request.BusquedaExpediente;
import pe.com.ci.sed.expediente.model.request.EliminarRequest;
import pe.com.ci.sed.expediente.model.request.GenericRequest;
import pe.com.ci.sed.expediente.persistence.entity.ExpedienteDigitalTable;

public interface ExpedienteService {

    public Object buscarExpediente(GenericRequest<BusquedaExpediente> request);

    public ExpedienteDigitalTable registrarExpediente(ExpedienteDigitalTable expediente);

    public ExpedienteDigitalTable actualizarEstadoExpediente(ExpedienteDigitalTable expedienteDigitalTable, String estadoActual) throws URISyntaxException, StorageException;

    public Object eliminarExpedienteXLote(GenericRequest<EliminarRequest> request);
}
