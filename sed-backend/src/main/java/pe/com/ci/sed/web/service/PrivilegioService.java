package pe.com.ci.sed.web.service;

import pe.com.ci.sed.web.model.request.Busqueda;
import pe.com.ci.sed.web.model.generic.HeaderRequest;
import pe.com.ci.sed.web.persistence.entity.Catalogo;
import pe.com.ci.sed.web.persistence.entity.Privilegio;
import pe.com.ci.sed.web.persistence.entity.Rol;

import java.util.List;

public interface PrivilegioService {

    public Object listarPrivilegio(Busqueda param);

    public Object obtenerPrivilegio(String codigo, HeaderRequest header);

    public void eliminarPrivilegioByRol(String rolId);

    public void crearPrivilegiosByRol(Rol rol, boolean isUpdate, List<Catalogo> catalogos);

    public void crearPrivilegioByRol(String rolId, List<Catalogo> catalogos);

    public Privilegio getPrivilegio(String codigo);

    public List<Privilegio> getPrivilegiosByRolid(String rolId);
}
