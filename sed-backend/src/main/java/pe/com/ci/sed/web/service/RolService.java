package pe.com.ci.sed.web.service;

import pe.com.ci.sed.web.model.request.Busqueda;
import pe.com.ci.sed.web.model.generic.HeaderRequest;
import pe.com.ci.sed.web.persistence.entity.Rol;
import pe.com.ci.sed.web.model.response.RolReponse;

import java.util.List;


public interface RolService {
    public Object crearRol(Rol rol, HeaderRequest header);

    public void crearRoles(List<Rol> roles);

    public Object listarRol(Busqueda param);

    public Object obtenerRol(String codigo, HeaderRequest header);

    public Rol getRol(String codigo);

    public RolReponse getRolPrivilegios(String codigo);

    public Object modificarRol(Rol rol, HeaderRequest header);
}
