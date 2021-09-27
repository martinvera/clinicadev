package pe.com.ci.sed.web.service.impl;

import static pe.com.ci.sed.web.util.GenericUtil.getResult;
import static pe.com.ci.sed.web.util.GenericUtil.getResultNotFound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.microsoft.azure.storage.table.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.microsoft.azure.storage.ResultContinuation;
import com.microsoft.azure.storage.ResultSegment;
import com.microsoft.azure.storage.StorageErrorCodeStrings;
import com.microsoft.azure.storage.StorageException;

import lombok.extern.log4j.Log4j2;
import pe.com.ci.sed.web.errors.AutenticacionException;
import pe.com.ci.sed.web.model.generic.HeaderRequest;
import pe.com.ci.sed.web.model.request.Busqueda;
import pe.com.ci.sed.web.model.response.UsuarioResponse;
import pe.com.ci.sed.web.persistence.entity.Rol;
import pe.com.ci.sed.web.persistence.entity.Usuario;
import pe.com.ci.sed.web.service.RolService;
import pe.com.ci.sed.web.service.UsuarioService;
import pe.com.ci.sed.web.util.ConexionUtil;
import pe.com.ci.sed.web.util.Constants;


@Log4j2
@Service
public class UsuarioServiceImpl extends ConexionUtil implements UsuarioService, UserDetailsService {

    private final RolService rolService;
    private static final String tableName = "storagetablesecurity";
    private static final String PartitionKey = "PartitionKey";
    private static final String Estado = "Estado";

    public UsuarioServiceImpl(CloudTableClient cloudTableClient, RolService rolService) {
        super(cloudTableClient);
        this.rolService = rolService;
    }

    @Override
    public Object registrarUsuario(Usuario usuario, HeaderRequest header) {
        try {
            TableOperation insert = TableOperation.insert(usuario);
            TableResult result = getTable(tableName).execute(insert);
            if (result.getHttpStatusCode() == HttpStatus.NO_CONTENT.value())
                return getResult(header, usuario);
            else
                return getResultNotFound(header, "Usuario no registrado");
        } catch (StorageException e) {
            String message = e.getLocalizedMessage();
            if (e.getErrorCode().equals(StorageErrorCodeStrings.ENTITY_ALREADY_EXISTS))
                message = "Usuario Registrado ya Existe";
            throw new AutenticacionException(message, HttpStatus.CONFLICT, header);
        }
    }

    @Override
    public void registrarUsuarioAdmin(Usuario usuario) {
        try {
            TableOperation insert = TableOperation.insert(usuario);
            TableResult result = getTable(tableName).execute(insert);
            if (result.getHttpStatusCode() == HttpStatus.NO_CONTENT.value())
                log.info("Usuario registrado");
        } catch (StorageException e) {
            log.info("code: {}, message: {}", e.getErrorCode(), e.getLocalizedMessage());
        }
    }

    @Override
    public void registrarUsuarios(List<Usuario> usuarios) {
        usuarios.forEach(usuario -> {
            try {
                TableOperation insert = TableOperation.insert(usuario);
                getTable(tableName).execute(insert);
            } catch (StorageException e) {
                log.info("user: {}, nombres: {}", usuario.getUsername(), usuario.getFullName());
                log.info("code: {}, message: {}", e.getErrorCode(), e.getLocalizedMessage());
            }
        });
    }
    public void validarCorreo(){

    }
    @Override
    public Object modificarUsuario(Usuario request, HeaderRequest header) {
        try {
            Usuario usuarioModi = this.getUserByUsername(request.getUsername(), null);
            usuarioModi.modificar(request);

            TableOperation update = TableOperation.replace(usuarioModi);
            TableResult result = getTable(tableName).execute(update);
            if (result.getHttpStatusCode() == HttpStatus.NO_CONTENT.value())
                return getResult(header, usuarioModi);
            else
                return getResultNotFound(header, "Usuario no actualizado");
        } catch (StorageException e) {
            log.info("error: {}", e.getLocalizedMessage());
            throw new AutenticacionException(e.getLocalizedMessage());
        }
    }

    @Override
    public Object eliminarUsuario(String username, HeaderRequest header) {
        try {
            Usuario usuarioELim = this.getUserByUsername(username, null);
            if (!Objects.isNull(usuarioELim)) {
                TableOperation delete = TableOperation.delete(usuarioELim);
                TableResult result = getTable(tableName).execute(delete);
                if (result.getHttpStatusCode() == HttpStatus.NO_CONTENT.value())
                    return getResult(header, "Usuario Eliminado");
            }
            return getResultNotFound(header, "Usuario no encontrado");
        } catch (StorageException e) {
            log.info("error: {}", e.getLocalizedMessage());
            throw new AutenticacionException(e.getLocalizedMessage());
        }
    }

    @Override
    public Object listarUsuarios(Busqueda busqueda) {
        ResultContinuation continuationToken = ConexionUtil.getContinuationToken(busqueda);
        TableQuery<Usuario> rangeQuery = TableQuery.from(Usuario.class).take(busqueda.getSize());
        try {
            ResultSegment<Usuario> response = getTable(tableName).executeSegmented(rangeQuery, continuationToken);
            return getResultSetPaginacion(busqueda.getHeader(), response);
        } catch (StorageException e) {
            e.printStackTrace();
            throw new AutenticacionException(e.getLocalizedMessage());
        }
    }

    @Override
    public Object obtenerUsuario(String username, HeaderRequest header) {
        return getResult(header, this.getUsuarioResponseAndRol(username));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario user = this.getUserByUsername(username, Integer.valueOf(Constants.Estado.ACTIVO));
        Rol role = rolService.getRol(user.getRole());
        if (role.getEstado().equals(Constants.Estado.INACTIVO))
            throw new AutenticacionException("Rol de Usuario se encuentra inactivo");
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPasswordencrip(), Collections.singletonList(authority));
    }

    private UsuarioResponse getUsuarioResponseAndRol(String username) {
        try {
            TableOperation retrieveSmithJeff = TableOperation.retrieve(username, username, Usuario.class);
            Usuario usuario = getTable(tableName).execute(retrieveSmithJeff).getResultAsType();
            if (Objects.isNull(usuario)) throw new AutenticacionException("Usuario no encontrado: " + username);
            return new UsuarioResponse(usuario, rolService.getRolPrivilegios(usuario.getRole()));
        } catch (StorageException e) {
            log.error("ERROR {}", e.getLocalizedMessage());
            throw new AutenticacionException(e.getLocalizedMessage());
        }
    }

    private Usuario getUserByUsername(String username, Integer estado) {
        List<Usuario> response = new ArrayList<>();
        TableQuery<Usuario> query = TableQuery.from(Usuario.class);
        String select = TableQuery.generateFilterCondition(PartitionKey, TableQuery.QueryComparisons.EQUAL, username);
        if (Objects.nonNull(estado))
            select += " and " + TableQuery.generateFilterCondition(Estado, TableQuery.QueryComparisons.EQUAL, estado);
        getTable(tableName).execute(query.where(select)).forEach(response::add);
        if (response.size() == 1)
            return response.get(0);
        throw new AutenticacionException("Usuario activo no encontrado: " + username);
    }

}
