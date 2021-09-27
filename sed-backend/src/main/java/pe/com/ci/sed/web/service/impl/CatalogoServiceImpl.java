package pe.com.ci.sed.web.service.impl;


import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static pe.com.ci.sed.web.util.GenericUtil.getResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableBatchOperation;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TableQuery;

import pe.com.ci.sed.web.errors.AutenticacionException;
import pe.com.ci.sed.web.model.generic.HeaderRequest;
import pe.com.ci.sed.web.model.request.ValidarRequest;
import pe.com.ci.sed.web.persistence.entity.*;
import pe.com.ci.sed.web.service.CatalogoService;
import pe.com.ci.sed.web.util.ConexionUtil;
import pe.com.ci.sed.web.util.Constants;
import pe.com.ci.sed.web.util.Constants.TableStorage;

@Log4j2
@Service
public class CatalogoServiceImpl extends ConexionUtil implements CatalogoService {
    private static final String ROW_KEY = "Catalogo";
    private static final String COLUMN_CODIGOPADRE = "Codigopadre";
    private static final String ORIGEN_SERVICIO = "Os";
    private static final String CATALOGO_TIPODOCUMENTO = "TIPODOCUMENTO";
    private static final String CATALOGO_GARANTE = "GARANTE";
    private static final String COLUMN_DESCRIPCION = "Descripcion";
    private static final String COLUMN_ESPECIAL = "Especial";
    private static final String COLUMN_GARANTEID = "Garanteid";
    private static final String COLUMN_MECANISMOID = "Mecanismoid";
    private static final String COLUMN_MODOFACTURACIONID = "Modofacturacionid";
    private static final String PARTITION_KEY = "PartitionKey";

    public CatalogoServiceImpl(CloudTableClient cloudTableClient) {
        super(cloudTableClient);
    }

    @Override
    public Object listarCatalogo(String codigo, HeaderRequest header) {
        String where = TableQuery.generateFilterCondition(PARTITION_KEY, TableQuery.QueryComparisons.EQUAL, codigo);
        if (codigo.equals("SERVICIOS")) {
            List<OrigenServicio> result = new ArrayList<>();
            List<TipoDocumento> tipoDocs = obtenerTiposDocumento();
            Map<String, List<TipoDocumento>> group = tipoDocs.parallelStream().collect(Collectors.groupingBy(TipoDocumento::getCodigopadre));
            TableQuery<OrigenServicio> rangeQuery = TableQuery.from(OrigenServicio.class).where(where);
            getTable(TableStorage.CATALOGO).execute(rangeQuery).forEach(origen -> {
                origen.setTipodocumento(group.get(origen.getCodigo()).stream()
                        .sorted(Comparator.comparing(TipoDocumento::getDescripcion))
                        .collect(Collectors.toList()));
                result.add(origen);
            });
            return getResult(header, result.stream().sorted(Comparator.comparing(OrigenServicio::getDescripcion)).collect(Collectors.toList()));
        } else {
            List<Catalogo> result2 = new ArrayList<>();
            TableQuery<Catalogo> rangeQuery = TableQuery.from(Catalogo.class).where(where);
            getTable(TableStorage.CATALOGO).execute(rangeQuery).forEach(result2::add);
            return getResult(header, result2.stream().sorted(Comparator.comparing(Catalogo::getDescripcion)).collect(Collectors.toList()));
        }
    }


    public List<TipoDocumento> obtenerTiposDocumento() {
        try {
            List<TipoDocumento> result = new ArrayList<>();
            String where = TableQuery.generateFilterCondition(PARTITION_KEY, TableQuery.QueryComparisons.EQUAL, CATALOGO_TIPODOCUMENTO);
            TableQuery<TipoDocumento> rangeQuery = TableQuery.from(TipoDocumento.class).where(where);
            getTable(TableStorage.CATALOGO).execute(rangeQuery).forEach(result::add);
            return result;
        } catch (Exception e) {
            throw new AutenticacionException(e.getLocalizedMessage());
        }
    }

    @Override
    public void registrarCatalogo(List<Catalogo> catalogos) {
        catalogos.forEach(catalogo -> catalogo.creacion(Constants.PARAM_ADMIN));
        catalogos.parallelStream().collect(Collectors.groupingBy(Catalogo::getCatalogo)).forEach((key, list) -> {
            TableBatchOperation batchOperation = new TableBatchOperation();
            list.forEach(batchOperation::insertOrReplace);
            try {
                getTable(TableStorage.CATALOGO).execute(batchOperation);
            } catch (StorageException e) {
                throw new AutenticacionException(e.getErrorCode(), null);
            }
        });
    }

    @Override
    public List<Catalogo> listarCatalogosByKey(String codigo) {
        try {
            List<Catalogo> result = new ArrayList<>();
            String where = TableQuery.generateFilterCondition(PARTITION_KEY, TableQuery.QueryComparisons.EQUAL, codigo);
            TableQuery<Catalogo> rangeQuery = TableQuery.from(Catalogo.class).where(where);
            getTable(TableStorage.CATALOGO).execute(rangeQuery).forEach(result::add);
            return result;
        } catch (Exception e) {
            throw new AutenticacionException(e.getLocalizedMessage());
        }
    }

    @Override
    public void registrarCatalogoPrivilegio(List<Catalogo> catalogos) {
        TableBatchOperation batchOperation = new TableBatchOperation();
        catalogos.forEach(catalogo -> {
            batchOperation.insertOrReplace(catalogo);
            catalogo.creacion(Constants.PARAM_ADMIN);
        });
        try {
            getTable(TableStorage.CATALOGO).execute(batchOperation);
        } catch (StorageException e) {
            throw new AutenticacionException(e.getErrorCode(), null);
        }
    }

    @Override
    public void registrarDocumentosRequeridos(List<DocumentosRequeridos> catalogos) {
        catalogos.forEach(catalogo -> catalogo.creacion(Constants.PARAM_ADMIN));
        catalogos.parallelStream().collect(Collectors.groupingBy(DocumentosRequeridos::getPartitionKey)).forEach((key, list) -> {
            TableBatchOperation batchOperation = new TableBatchOperation();
            list.forEach(batchOperation::insertOrReplace);
            try {
                getTable(TableStorage.GARANTEMECANISMO).execute(batchOperation);
            } catch (StorageException e) {
                throw new AutenticacionException(e.getErrorCode(), HttpStatus.resolve(e.getHttpStatusCode()));
            }
        });
    }

    @Override
    public Object validarDocumentosRequeridos(ValidarRequest request) {

        List<Catalogo> result = new ArrayList<>();
        List<Catalogo> result2 = new ArrayList<>();

        Integer codigoServicio = request.getRequest().getCodigoServicioOrigen();
        if (codigoServicio == 4 || codigoServicio == 2) {
            String where = TableQuery.generateFilterCondition(ROW_KEY, TableQuery.QueryComparisons.EQUAL, CATALOGO_TIPODOCUMENTO);
            String where2 = TableQuery.generateFilterCondition(COLUMN_CODIGOPADRE, TableQuery.QueryComparisons.EQUAL, codigoServicio.toString());
            String where3 = TableQuery.generateFilterCondition(ORIGEN_SERVICIO, TableQuery.QueryComparisons.EQUAL, 1);
            String combinedFilter3 = TableQuery.combineFilters(where, TableQuery.Operators.AND, where2);
            String combinedFilter2 = TableQuery.combineFilters(combinedFilter3, TableQuery.Operators.AND, where3);
            TableQuery<Catalogo> rangeQuery = TableQuery.from(Catalogo.class).where(combinedFilter2);

            getTable(TableStorage.CATALOGO).execute(rangeQuery).forEach(result::add);
        } else if (isNotBlank(request.getRequest().getGaranteid())) {
            String where = TableQuery.generateFilterCondition(ROW_KEY, TableQuery.QueryComparisons.EQUAL, CATALOGO_GARANTE);
            String wher2 = TableQuery.generateFilterCondition(COLUMN_DESCRIPCION, TableQuery.QueryComparisons.EQUAL, request.getRequest().getGaranteid());
            String where3 = TableQuery.generateFilterCondition(COLUMN_ESPECIAL, TableQuery.QueryComparisons.EQUAL, 1);
            String combinedFilter3 = TableQuery.combineFilters(where, TableQuery.Operators.AND, wher2);
            String combinedFilter2 = TableQuery.combineFilters(combinedFilter3, TableQuery.Operators.AND, where3);
            TableQuery<Catalogo> rangeQuery = TableQuery.from(Catalogo.class).where(combinedFilter2);
            getTable(TableStorage.CATALOGO).execute(rangeQuery).forEach(result2::add);
            if (!result2.isEmpty()) {
                request.getRequest().setGaranteid("0");
            }
            String whereDoc = TableQuery.generateFilterCondition(COLUMN_GARANTEID, TableQuery.QueryComparisons.EQUAL, request.getRequest().getGaranteid());
            String whereDoc2 = TableQuery.generateFilterCondition(COLUMN_MECANISMOID, TableQuery.QueryComparisons.EQUAL, request.getRequest().getMecanismoFacturacionId());
            String whereDoc3 = TableQuery.generateFilterCondition(COLUMN_MODOFACTURACIONID, TableQuery.QueryComparisons.EQUAL, request.getRequest().getModofacturacionId());
            String combinedFilterDoc = TableQuery.combineFilters(whereDoc, TableQuery.Operators.AND, whereDoc2);
            String combinedFilterDoc2 = TableQuery.combineFilters(whereDoc3, TableQuery.Operators.AND, combinedFilterDoc);
            TableQuery<DocumentosRequeridos> rangeQuery2 = TableQuery.from(DocumentosRequeridos.class).where(combinedFilterDoc2);
            getTable(TableStorage.GARANTEMECANISMO).execute(rangeQuery2).forEach(c -> result.add(getDocumento(c.getTipodocumentoid())));
        }
        return getResult(request.getHeader(), result);
    }

    public Catalogo getDocumento(String codigo) {
        try {
            TableOperation query = TableOperation.retrieve(PARTITION_KEY, codigo, Catalogo.class);
            return getTable(TableStorage.CATALOGO).execute(query).getResultAsType();

        } catch (StorageException e) {
            throw new AutenticacionException(e.getLocalizedMessage());
        }
    }

}

