package pe.com.ci.sed.document.service;

import pe.com.ci.sed.document.model.generic.GenericRequest;
import pe.com.ci.sed.document.model.request.*;
import pe.com.ci.sed.document.model.response.ResponseSinLote;
import pe.com.ci.sed.document.persistence.entity.Archivo;
import pe.com.ci.sed.document.persistence.entity.Documento;


import java.util.List;
import java.util.Optional;

public interface DocumentoService {

    public void registrarDocumentoIntegracion(Documento documento);

    public Object registrarDocumentoCargaManual(GenericRequest<Documento> request);

    public Object modificarDocumentoCargaManual(GenericRequest<Documento> request);

    public void modificarDocumentoIntegracion(Documento documentoExistente, Documento documentoIntegracion);

    public Object obtenerDocumento(DocRequest request);

    public Object listarDocumentos(GenericRequest<BusquedaRequest> busqueda);

    public Object eliminarDocumentos(GenericRequest<BusquedaRequest> busqueda);

    public List<ResponseSinLote> reporteSinLote(GenericRequest<BusquedaSinLote> request);

    public List<Archivo> inicializarTipoDocumentoRequerido(String origenServicio, long mecanismoId, long modofacturacionId, String garanteId);

    public Object detalleDocumento(GenericRequest<BusquedaRequest> request);

    public Optional<Documento> findById(String nroEncuentro);

    public void delete(Documento documento);
}
