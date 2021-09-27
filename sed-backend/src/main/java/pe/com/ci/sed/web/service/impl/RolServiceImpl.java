package pe.com.ci.sed.web.service.impl;

import com.microsoft.azure.storage.ResultContinuation;
import com.microsoft.azure.storage.ResultSegment;
import com.microsoft.azure.storage.StorageErrorCodeStrings;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pe.com.ci.sed.web.errors.AutenticacionException;
import pe.com.ci.sed.web.model.request.Busqueda;
import pe.com.ci.sed.web.model.generic.HeaderRequest;
import pe.com.ci.sed.web.persistence.entity.Catalogo;
import pe.com.ci.sed.web.persistence.entity.Rol;
import pe.com.ci.sed.web.model.response.RolReponse;
import pe.com.ci.sed.web.service.CatalogoService;
import pe.com.ci.sed.web.service.PrivilegioService;
import pe.com.ci.sed.web.service.RolService;
import pe.com.ci.sed.web.util.ConexionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static pe.com.ci.sed.web.util.GenericUtil.getResult;
import static pe.com.ci.sed.web.util.GenericUtil.getResultNotFound;

@Log4j2
@Service
public class RolServiceImpl extends ConexionUtil implements RolService {

    private final PrivilegioService privilegioService;
    private final CatalogoService catalogoService;
    private static final String tableName = "storagetablerol";

    public RolServiceImpl(CloudTableClient cloudTableClient, PrivilegioService privilegioService,
                          CatalogoService catalogoService) {
        super(cloudTableClient);
        this.privilegioService = privilegioService;
        this.catalogoService = catalogoService;
    }

    @Override
    public Object crearRol(Rol rol, HeaderRequest header) {
        try {
            int max = Collections.max(obtenerRoles().stream().map(r -> Integer.parseInt(r.getCodigo())).collect(Collectors.toList()));
            int codigo = max + 1;
            rol.setCodigo(Integer.toString(codigo));
            rol.setKeys(Integer.toString(codigo));
            rol.creacion(header.getUserId());

            TableOperation insert = TableOperation.insert(rol);
            TableResult result = getTable(tableName).execute(insert);
            if (result.getHttpStatusCode() == HttpStatus.NO_CONTENT.value()) {
                privilegioService.crearPrivilegiosByRol(rol, false, catalogoService.listarCatalogosByKey("PRIVILEGIOS"));
                return getResult(header, rol);
            } else return getResultNotFound(header, "Rol no registrado");
        } catch (StorageException e) {
            String message = e.getLocalizedMessage();
            if (e.getErrorCode().equals(StorageErrorCodeStrings.ENTITY_ALREADY_EXISTS))
                message = "Rol Registrado ya Existe";
            throw new AutenticacionException(message, HttpStatus.CONFLICT);
        }
    }

    @Override
    public void crearRoles(List<Rol> roles) {
        List<Catalogo> catalogos = catalogoService.listarCatalogosByKey("PRIVILEGIOS");
        roles.forEach(rol -> {
            try {
                TableOperation insert = TableOperation.insert(rol);
                TableResult result = getTable(tableName).execute(insert);
                if (result.getHttpStatusCode() == HttpStatus.NO_CONTENT.value()) {
                    log.info("Rol {} registrado", rol.getDescripcion());
                    privilegioService.crearPrivilegiosByRol(rol, false, catalogos);
                }
            } catch (StorageException e) {
                log.info("code: {}, message: {}", e.getErrorCode(), e.getLocalizedMessage());
            }
        });
    }

    @Override
    public Object modificarRol(Rol request, HeaderRequest header) {
        try {
            Rol rolMod = this.getRol(request.getCodigo());
            rolMod.setNombre(request.getNombre());
            System.out.println(rolMod);
            rolMod.setDescripcion(request.getDescripcion());
            rolMod.setPrivilegios(request.getPrivilegios());
            rolMod.setEstado(request.getEstado());
            rolMod.modificar(header.getUserId());

            TableOperation update = TableOperation.replace(rolMod);
            TableResult result = getTable(tableName).execute(update);
            if (result.getHttpStatusCode() == HttpStatus.NO_CONTENT.value()) {
                privilegioService.crearPrivilegiosByRol(rolMod, true, catalogoService.listarCatalogosByKey("PRIVILEGIOS"));
                return getResult(header, rolMod);
            } else return getResultNotFound(header, "Rol no actualizado");
        } catch (StorageException e) {
            log.info("error: {}", e.getLocalizedMessage());
            throw new AutenticacionException(e.getLocalizedMessage());
        }
    }

    @Override
    public Object listarRol(Busqueda param) {
        ResultContinuation continuationToken = ConexionUtil.getContinuationToken(param);
        TableQuery<Rol> rangeQuery = TableQuery.from(Rol.class).take(param.getSize());
        try {
            List<RolReponse> response = new ArrayList<>();
            ResultSegment<Rol> result = getTable(tableName).executeSegmented(rangeQuery, continuationToken);
            result.getResults().parallelStream().forEach(r -> {
                response.add(RolReponse.builder()
                        .codigo(r.getCodigo())
                        .descripcion(r.getDescripcion())
                        .nombre(r.getNombre())
                        .estado(r.getEstado())
                        .privilegios(r.getPrivilegios())
                        .build());
            });
            return getResultSetPaginacion(param.getHeader(), result, response);
        } catch (
                StorageException e) {
            e.printStackTrace();
            throw new AutenticacionException(e.getLocalizedMessage());
        }
    }

    private List<Rol> obtenerRoles() {
        List<Rol> roles = new ArrayList<>();
        TableQuery<Rol> select = TableQuery.from(Rol.class);
        getTable(tableName).execute(select).forEach(roles::add);
        return roles;
    }

    @Override
    public Object obtenerRol(String codigo, HeaderRequest header) {
        return getResult(header, getRolPrivilegios(codigo));
    }

    public Rol getRol(String codigo) {
        try {
            TableOperation retrieve = TableOperation.retrieve(codigo, codigo, Rol.class);
            Rol rol = getTable(tableName).execute(retrieve).getResultAsType();
            if (Objects.isNull(rol)) throw new AutenticacionException("Rol no encontrado: " + codigo);
            return rol;
        } catch (StorageException e) {
            log.error("ERROR {}", e.getLocalizedMessage());
            throw new AutenticacionException(e.getLocalizedMessage());
        }
    }

    @Override
    public RolReponse getRolPrivilegios(String codigo) {
        try {
            TableOperation retrieve = TableOperation.retrieve(codigo, codigo, Rol.class);
            Rol rol = getTable(tableName).execute(retrieve).getResultAsType();
            if (Objects.isNull(rol)) throw new AutenticacionException("Rol no encontrado: " + codigo);
            return RolReponse.builder()
                    .codigo(rol.getCodigo())
                    .descripcion(rol.getDescripcion())
                    .nombre(rol.getNombre())
                    .estado(rol.getEstado())
                    .privilegios(rol.getPrivilegios())
                    .privilegioList(privilegioService.getPrivilegiosByRolid(rol.getCodigo()))
                    .build();
        } catch (StorageException e) {
            log.error("ERROR {}", e.getLocalizedMessage());
            throw new AutenticacionException(e.getLocalizedMessage());
        }
    }

}
