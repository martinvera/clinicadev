package pe.com.ci.sed.web.service;

import pe.com.ci.sed.web.model.request.Busqueda;
import pe.com.ci.sed.web.model.generic.HeaderRequest;
import pe.com.ci.sed.web.persistence.entity.Usuario;

import java.util.List;

public interface UsuarioService {
    public Object registrarUsuario(Usuario usuario, HeaderRequest header);

    public void registrarUsuarioAdmin(Usuario usuario);

    public void registrarUsuarios(List<Usuario> usuarios);

    public Object modificarUsuario(Usuario request, HeaderRequest header);

    public Object eliminarUsuario(String username, HeaderRequest header);

    public Object listarUsuarios(Busqueda param);

    public Object obtenerUsuario(String username, HeaderRequest header);

}
