package pe.com.ci.sed.expediente.service.impl;

import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static pe.com.ci.sed.expediente.utils.GenericUtil.getResult;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.microsoft.azure.storage.StorageErrorCodeStrings;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableQuery;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import pe.com.ci.sed.expediente.errors.AutenticacionException;
import pe.com.ci.sed.expediente.errors.ExpedienteException;
import pe.com.ci.sed.expediente.model.request.ValidarRequest;
import pe.com.ci.sed.expediente.persistence.entity.Catalogo;
import pe.com.ci.sed.expediente.persistence.entity.DocumentosRequeridos;
import pe.com.ci.sed.expediente.service.ValidarService;

@Log4j2
@Service
@AllArgsConstructor
public class ValidarServiceImpl implements ValidarService {
    private static final String tableName = "storagetablecatalogo";
    private static final String tableName2 = "storagetableGaranteMecanismo";
    private static final String ROW_KEY = "Catalogo";
    private static final String PartitionKey = "TIPODOCUMENTO";
    private static final String COLUMN_NAME = "Codigopadre";
    private static final String ORIGEN_SERVICIO = "Os";
    private static final String Catalogoname = "TIPODOCUMENTO";
    private static final String Grante = "GARANTE";
    private static final String COLUMN_NAME_2 = "Descripcion";
    private static final String GARANTE_ESPECIAL = "Especial";
    private static final String GARANTE_ID = "Garanteid";
    private static final String MECANISMO_ID = "Mecanismoid";
    private static final String MODO_ID = "Modofacturacionid";

    @Autowired
    private CloudTableClient cloudTableClient;

    @Override
    public Object ValidarDocumentosRequeridos(ValidarRequest request) {
        try {
            List<Catalogo> result = new ArrayList<>();
            List<Catalogo> result2 = new ArrayList<>();
            Integer codigoServicio = request.getRequest().getCodigoServicioOrigen();
            if (codigoServicio == 4 || codigoServicio == 2) {
                String where = TableQuery.generateFilterCondition(ROW_KEY, TableQuery.QueryComparisons.EQUAL, Catalogoname);
                String where2 = TableQuery.generateFilterCondition(COLUMN_NAME, TableQuery.QueryComparisons.EQUAL, codigoServicio.toString());
                String where3 = TableQuery.generateFilterCondition(ORIGEN_SERVICIO, TableQuery.QueryComparisons.EQUAL, 1);
                String combinedFilter3 = TableQuery.combineFilters(where, TableQuery.Operators.AND, where2);
                String combinedFilter2 = TableQuery.combineFilters(combinedFilter3, TableQuery.Operators.AND, where3);
                log.info(combinedFilter2);
                TableQuery<Catalogo> rangeQuery = TableQuery.from(Catalogo.class).where(combinedFilter2);

                cloudTableClient.getTableReference(tableName).execute(rangeQuery).forEach(result::add);
            } else if (isNotBlank(request.getRequest().getGaranteid())) {
                String where = TableQuery.generateFilterCondition(ROW_KEY, TableQuery.QueryComparisons.EQUAL, Grante);
                String wher2 = TableQuery.generateFilterCondition(COLUMN_NAME_2, TableQuery.QueryComparisons.EQUAL, request.getRequest().getGaranteid());
                String where3 = TableQuery.generateFilterCondition(GARANTE_ESPECIAL, TableQuery.QueryComparisons.EQUAL, 1);
                String combinedFilter3 = TableQuery.combineFilters(where, TableQuery.Operators.AND, wher2);
                String combinedFilter2 = TableQuery.combineFilters(combinedFilter3, TableQuery.Operators.AND, where3);
                log.info(combinedFilter2);
                TableQuery<Catalogo> rangeQuery = TableQuery.from(Catalogo.class).where(combinedFilter2);
                cloudTableClient.getTableReference(tableName).execute(rangeQuery).forEach(result2::add);
                if (result2.size() == 0) {
                    request.getRequest().setGaranteid("0");
                }
                String mecanismoFacturacionId = "SERVICIO";
                String whereDoc = TableQuery.generateFilterCondition(GARANTE_ID, TableQuery.QueryComparisons.EQUAL, request.getRequest().getGaranteid());
                String whereDoc2 = TableQuery.generateFilterCondition(MECANISMO_ID, TableQuery.QueryComparisons.EQUAL, mecanismoFacturacionId);
                String whereDoc3 = TableQuery.generateFilterCondition(MODO_ID, TableQuery.QueryComparisons.EQUAL, request.getRequest().getModofacturacionId());
                String combinedFilterDoc = TableQuery.combineFilters(whereDoc, TableQuery.Operators.AND, whereDoc2);
                String combinedFilterDoc2 = TableQuery.combineFilters(whereDoc3, TableQuery.Operators.AND, combinedFilterDoc);

                log.info(combinedFilterDoc2);
                List<DocumentosRequeridos> requeridos = new ArrayList<>();
                TableQuery<DocumentosRequeridos> rangeQuery2 = TableQuery.from(DocumentosRequeridos.class).where(combinedFilterDoc2);
                cloudTableClient.getTableReference(tableName2).execute(rangeQuery2).forEach(requeridos::add);
                List<String> ids = requeridos.stream().map(DocumentosRequeridos::getTipodocumentoid).collect(Collectors.toList());
                result = getCatalogoTipoDocumento().stream().filter(c -> ids.contains(c.getRowKey())).collect(Collectors.toList());
            }
            return getResult(request.getHeader(), result);
        } catch (StorageException e) {
            String message = e.getLocalizedMessage();
            if (e.getErrorCode().equals(StorageErrorCodeStrings.ENTITY_ALREADY_EXISTS))
                message = "Lote Registrado ya Existe";
            throw new ExpedienteException(message, HttpStatus.CONFLICT, request.getHeader());
        } catch (URISyntaxException e) {
            throw new ExpedienteException("", HttpStatus.CONFLICT, request.getHeader());
        }

    }

    public List<Catalogo> getCatalogoTipoDocumento() {
        try {
            List<Catalogo> catalogos = new ArrayList<>();
            TableQuery<Catalogo> query = TableQuery.from(Catalogo.class);
            query.where(TableQuery.generateFilterCondition("PartitionKey", TableQuery.QueryComparisons.EQUAL, PartitionKey));
            cloudTableClient.getTableReference(tableName).execute(query).forEach(catalogos::add);
            return catalogos;
        } catch (StorageException | URISyntaxException e) {

            throw new AutenticacionException(e.getLocalizedMessage());
        }
    }
}
