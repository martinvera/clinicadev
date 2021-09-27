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
import pe.com.ci.sed.web.persistence.entity.Privilegio;
import pe.com.ci.sed.web.persistence.entity.Rol;
import pe.com.ci.sed.web.service.PrivilegioService;
import pe.com.ci.sed.web.util.ConexionUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static pe.com.ci.sed.web.util.GenericUtil.getResult;

@Log4j2
@Service
public class PrivilegioServiceImpl extends ConexionUtil implements PrivilegioService {

    private static final String STORAGETABLE_PRIVILEGIO = "storagetableprivilegio";
    private static final String STORAGETABLE_CATALOGO = "storagetablecatalogo";
    private static final String PARTITION_KEY = "PartitionKey";

    public PrivilegioServiceImpl(CloudTableClient cloudTableClient) {
        super(cloudTableClient);
    }

    @Override
    public void crearPrivilegiosByRol(Rol rol, boolean isUpdate, List<Catalogo> catalogos) {
        List<Catalogo> catalogosXRol = catalogos.stream()
                .filter(c -> Arrays.asList(rol.getPrivilegios().split(",")).contains(c.getCodigo()))
                .collect(Collectors.toList());

        if (isUpdate) eliminarPrivilegioByRol(rol.getCodigo());
        crearPrivilegioByRol(rol.getCodigo(), catalogosXRol);
    }

    @Override
    public void crearPrivilegioByRol(String rolId, List<Catalogo> catalogos) {
        try {
            if (!catalogos.isEmpty()) {
                TableBatchOperation batchOperation = new TableBatchOperation();
                catalogos.stream().map(c -> new Privilegio(c, rolId)).forEach(batchOperation::insertOrReplace);
                getTable(STORAGETABLE_PRIVILEGIO).execute(batchOperation);
            }
        } catch (StorageException e) {
            String message = e.getLocalizedMessage();
            if (e.getErrorCode().equals(StorageErrorCodeStrings.ENTITY_ALREADY_EXISTS))
                message = "Privilegio Registrado ya Existe";
            throw new AutenticacionException(message, HttpStatus.CONFLICT);
        }
    }

    @Override
    public Object listarPrivilegio(Busqueda busqueda) {
        ResultContinuation continuationToken = ConexionUtil.getContinuationToken(busqueda);
        TableQuery<Privilegio> rangeQuery = TableQuery.from(Privilegio.class).take(busqueda.getSize());
        rangeQuery.where(TableQuery.generateFilterCondition(PARTITION_KEY, TableQuery.QueryComparisons.EQUAL, "PRIVILEGIOS"));
        try {
            ResultSegment<Privilegio> response = getTable(STORAGETABLE_CATALOGO).executeSegmented(rangeQuery, continuationToken);
            return getResultSetPaginacion(busqueda.getHeader(), response);
        } catch (StorageException e) {
            e.printStackTrace();
            throw new AutenticacionException(e.getLocalizedMessage());
        }
    }

    @Override
    public Object obtenerPrivilegio(String codigo, HeaderRequest header) {
        return getResult(header, getPrivilegio(codigo));
    }

    @Override
    public void eliminarPrivilegioByRol(String rolId) {
        try {
            List<Privilegio> deletes = getPrivilegiosByRolid(rolId);
            if (!deletes.isEmpty()) {
                TableBatchOperation batchOperation = new TableBatchOperation();
                deletes.forEach(batchOperation::delete);
                getTable(STORAGETABLE_PRIVILEGIO).execute(batchOperation);
            }
        } catch (StorageException e) {
            log.error("ERROR {}", e.getLocalizedMessage());
            throw new AutenticacionException(e.getLocalizedMessage());
        }
    }

    @Override
    public Privilegio getPrivilegio(String codigo) {
        try {
            TableOperation query = TableOperation.retrieve(codigo, codigo, Privilegio.class);
            return getTable(STORAGETABLE_PRIVILEGIO).execute(query).getResultAsType();
        } catch (StorageException e) {
            log.error("ERROR {}", e.getLocalizedMessage());
            throw new AutenticacionException(e.getLocalizedMessage());
        }
    }

    @Override
    public List<Privilegio> getPrivilegiosByRolid(String rolId) {
        List<Privilegio> response = new ArrayList<>();
        String where = TableQuery.generateFilterCondition(PARTITION_KEY, TableQuery.QueryComparisons.EQUAL, rolId);
        TableQuery<Privilegio> query = TableQuery.from(Privilegio.class).where(where);
        getTable(STORAGETABLE_PRIVILEGIO).execute(query).forEach(response::add);
        return response;
    }
}
