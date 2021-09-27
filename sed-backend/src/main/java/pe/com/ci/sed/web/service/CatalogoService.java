package pe.com.ci.sed.web.service;

import com.microsoft.azure.storage.StorageException;
import pe.com.ci.sed.web.model.generic.HeaderRequest;
import pe.com.ci.sed.web.model.request.ValidarRequest;
import pe.com.ci.sed.web.persistence.entity.Catalogo;
import pe.com.ci.sed.web.persistence.entity.DocumentosRequeridos;

import java.util.List;

public interface CatalogoService {

    public Object listarCatalogo(String catalogo, HeaderRequest header);

    public void registrarCatalogo(List<Catalogo> catalogos) throws StorageException;

    public List<Catalogo> listarCatalogosByKey(String codigo);

    public void registrarCatalogoPrivilegio(List<Catalogo> catalogos) throws StorageException;

    public void registrarDocumentosRequeridos(List<DocumentosRequeridos> catalogos) throws StorageException;

    public Object validarDocumentosRequeridos(ValidarRequest request);
}
