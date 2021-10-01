package pe.com.ci.sed.expediente.service.impl;

import static org.apache.logging.log4j.util.Strings.isEmpty;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static pe.com.ci.sed.expediente.utils.GenericUtil.getResult;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.microsoft.azure.storage.ResultContinuation;
import com.microsoft.azure.storage.ResultSegment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableBatchOperation;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TableQuery;
import com.microsoft.azure.storage.table.TableResult;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import pe.com.ci.sed.expediente.errors.ExpedienteException;
import pe.com.ci.sed.expediente.model.request.BusquedaExpediente;
import pe.com.ci.sed.expediente.model.request.EliminarRequest;
import pe.com.ci.sed.expediente.model.request.GenericRequest;
import pe.com.ci.sed.expediente.model.response.Paginacion;
import pe.com.ci.sed.expediente.persistence.entity.ExpedienteDigitalTable;
import pe.com.ci.sed.expediente.service.ExpedienteService;
import pe.com.ci.sed.expediente.service.ExpedienteStorageService;
import pe.com.ci.sed.expediente.utils.GenericUtil;

@Log4j2
@Service
@AllArgsConstructor
public class ExpedienteServicesTable implements ExpedienteService {

    private final ExpedienteStorageService storageService;
    private final CloudTableClient cloudTableClient;
    private static final String STORAGETABLEEXPEDIENTE = "storagetableexpediente";
    private static final String PARTITION_KEY = "PartitionKey";
    private static final String ROW_KEY = "RowKey";

    public enum ESTADO {EN_PROCESO, PENDIENTE, TERMINADO, ERROR}

    @Override
    public Object buscarExpediente(GenericRequest<BusquedaExpediente> request) {

        try {
            ResultSegment<ExpedienteDigitalTable> expedientes = this.listaExpedientePaginado(request.getRequest());
            return GenericUtil.getResultSetPaginacion(request.getHeader(), expedientes);
        } catch (Exception e) {
            log.error("Error en la consulta de expedientes digitales : {} ", e.getMessage());
            throw new ExpedienteException(e.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }

    public ResultSegment<ExpedienteDigitalTable> listaExpedientePaginado(BusquedaExpediente filtros) throws URISyntaxException, StorageException {

        ArrayList<ExpedienteDigitalTable> result = new ArrayList<>();
        ResultContinuation continuationToken = GenericUtil.getContinuationToken(Paginacion.builder()
                .siguiente(filtros.getSiguiente())
                .atras(filtros.getAtras())
                .build());

        String tableNameGarante = STORAGETABLEEXPEDIENTE + filtros.getGaranteId();
        CloudTable tableExpedienteGarante = cloudTableClient.getTableReference(tableNameGarante);

        StringBuilder sb = getStringFiltros(filtros);

        log.info("lista-expediente-paginado Query : {}", sb);

        TableQuery<ExpedienteDigitalTable> rangeQuery = TableQuery.from(ExpedienteDigitalTable.class).where(sb.toString()).take(filtros.getSize());
        if (tableExpedienteGarante.exists()) {
            tableExpedienteGarante.executeSegmented(rangeQuery, continuationToken).getResults().forEach(x -> {
                x.setUrlArchivoZip(storageService.getUrlWithSas(x.getUrlArchivoZipSas()));
                result.add(x);
            });
        }

        return new ResultSegment<>(result, result.size(), continuationToken);
    }

    private StringBuilder getStringFiltros(BusquedaExpediente filtros) {
        StringBuilder sb = new StringBuilder();
        if (isEmpty(filtros.getEstado())) {
            sb.append(TableQuery.generateFilterCondition(PARTITION_KEY, TableQuery.QueryComparisons.EQUAL, String.valueOf(filtros.getLote()))).append(" ");
        } else {
            Optional.ofNullable(filtros.getEstado()).filter(e -> !e.isEmpty())
                    .ifPresent(e -> sb.append(TableQuery.generateFilterCondition(PARTITION_KEY, TableQuery.QueryComparisons.EQUAL, filtros.getLote() + "_" + e)).append(" "));
        }
        Optional.ofNullable(filtros.getFacturaNro()).filter(f -> !f.isEmpty())
                .ifPresent(f -> sb.append(TableQuery.Operators.AND).append(" ").append(TableQuery.generateFilterCondition(ROW_KEY, TableQuery.QueryComparisons.EQUAL, f)).append(" "));

        Optional.ofNullable(filtros.getFechaDesde()).filter(e -> !e.isEmpty())
                .ifPresent(e -> sb.append(TableQuery.Operators.AND).append(" ").append("Timestamp ge datetime'").append(e).append("T00:00:00.000Z'").append(" "));

        Optional.ofNullable(filtros.getFechaHasta()).filter(e -> !e.isEmpty())
                .ifPresent(e -> sb.append(TableQuery.Operators.AND).append(" ").append("Timestamp lt datetime'").append(e).append("T23:59:59.000Z'"));
        return sb;
    }

    public List<ExpedienteDigitalTable> listaExpediente(BusquedaExpediente filtros) throws URISyntaxException, StorageException {
        List<ExpedienteDigitalTable> expedientes = new ArrayList<>();
        if (Objects.nonNull(filtros.getGaranteId())) {
            String tableNameGarante = STORAGETABLEEXPEDIENTE + filtros.getGaranteId();
            CloudTable tableExpedienteGarante = cloudTableClient.getTableReference(tableNameGarante);

            StringBuilder sb = getStringFiltros(filtros);
            log.info("table-name: " + tableNameGarante);
            log.info("listaExpediente Query : {}", sb);

            TableQuery<ExpedienteDigitalTable> rangeQuery = TableQuery.from(ExpedienteDigitalTable.class).where(sb.toString());
            if (tableExpedienteGarante.exists())
                tableExpedienteGarante.execute(rangeQuery).forEach(x -> {
                    x.setUrlArchivoZip(storageService.getUrlWithSas(x.getUrlArchivoZipSas()));
                    expedientes.add(x);
                });
        }
        return expedientes;
    }

    @Override
    public ExpedienteDigitalTable registrarExpediente(ExpedienteDigitalTable expediente) {

        expediente.setFechaCreacion(GenericUtil.getDateWithZone());
        expediente.setPartitionKey(expediente.getNroLote() + "_" + expediente.getEstado());
        expediente.setRowKey(expediente.getFacturaNro());

        try {
            String tableNameGarante = STORAGETABLEEXPEDIENTE + expediente.getGaranteId();

            cloudTableClient.getTableReference(tableNameGarante).createIfNotExists();
            CloudTable tableGarante = cloudTableClient.getTableReference(tableNameGarante);

            TableOperation insert = TableOperation.insertOrReplace(expediente);
            TableResult result = tableGarante.execute(insert);

            expediente.setPartitionKey(String.valueOf(expediente.getNroLote()));
            /*Se registra un duplciado para la busqueda*/
            TableOperation insert2 = TableOperation.insertOrReplace(expediente);
            tableGarante.execute(insert2);

            if (result.getHttpStatusCode() == HttpStatus.NO_CONTENT.value())
                log.debug("expediente registrado");
            else
                log.debug("expediente no registrado");
        } catch (Exception e2) {
            log.error(e2);
            throw new ExpedienteException("No se pudo registrar el expediente", HttpStatus.SERVICE_UNAVAILABLE, null);
        }

        return expediente;
    }

    @Override
    public ExpedienteDigitalTable actualizarEstadoExpediente(ExpedienteDigitalTable expedienteDigitalTable, String estadoActual) throws URISyntaxException, StorageException {

        BusquedaExpediente filtro = new BusquedaExpediente();
        filtro.setGaranteId(expedienteDigitalTable.getGaranteId());
        filtro.setFacturaNro(expedienteDigitalTable.getFacturaNro());
        filtro.setEstado(estadoActual);
        filtro.setLote(expedienteDigitalTable.getNroLote());
        ExpedienteDigitalTable expediente = this.listaExpediente(filtro).stream().findFirst().get();

        String tableNameGarante = STORAGETABLEEXPEDIENTE + expedienteDigitalTable.getGaranteId();
        CloudTable tableGarante = cloudTableClient.getTableReference(tableNameGarante);
        tableGarante.execute(TableOperation.delete(expediente));
        expedienteDigitalTable.setCreadoPor(expediente.getCreadoPor());
        expedienteDigitalTable.setNroEncuentro(expediente.getNroEncuentro());
        expedienteDigitalTable.setPartitionKey(expedienteDigitalTable.getNroLote() + "_" + expedienteDigitalTable.getEstado());
        expedienteDigitalTable.setRowKey(expedienteDigitalTable.getFacturaNro());

        return this.registrarExpediente(expedienteDigitalTable);

    }

    @Override
    public Object eliminarExpedienteXLote(GenericRequest<EliminarRequest> request) {

        try {
            String tableNameGarante = STORAGETABLEEXPEDIENTE + request.getRequest().getGaranteId();
            CloudTable tableExpedienteGarante = cloudTableClient.getTableReference(tableNameGarante);
            if(tableExpedienteGarante.exists()){
                StringBuilder sb = new StringBuilder();
                sb.append(TableQuery.generateFilterCondition(PARTITION_KEY, TableQuery.QueryComparisons.EQUAL, request.getRequest().getNroLote())).append(" ");
                sb.append(TableQuery.Operators.OR).append(" ").append(TableQuery.generateFilterCondition(PARTITION_KEY, TableQuery.QueryComparisons.EQUAL, request.getRequest().getNroLote() + "_" + ESTADO.EN_PROCESO.name())).append(" ");
                sb.append(TableQuery.Operators.OR).append(" ").append(TableQuery.generateFilterCondition(PARTITION_KEY, TableQuery.QueryComparisons.EQUAL, request.getRequest().getNroLote() + "_" + ESTADO.ERROR.name())).append(" ");
                sb.append(TableQuery.Operators.OR).append(" ").append(TableQuery.generateFilterCondition(PARTITION_KEY, TableQuery.QueryComparisons.EQUAL, request.getRequest().getNroLote() + "_" + ESTADO.TERMINADO.name())).append(" ");
                CloudTable tableGarante = cloudTableClient.getTableReference(tableNameGarante);
                log.info("listaExpediente Query : {}", sb);
                List<ExpedienteDigitalTable> deleteList = new ArrayList<>();
                TableQuery<ExpedienteDigitalTable> query = TableQuery.from(ExpedienteDigitalTable.class).where(sb.toString());
                tableGarante.execute(query).forEach(deleteList::add);
                deleteList.stream().collect(Collectors.groupingBy(ExpedienteDigitalTable::getPartitionKey, Collectors.toList()))
                        .forEach((k, v) -> {
                            try {
                                TableBatchOperation batchOperation = new TableBatchOperation();
                                v.forEach(batchOperation::delete);
                                tableGarante.execute(batchOperation);
                            } catch (StorageException e) {
                                log.error("Error code: {}, message: {}", e.getErrorCode(), e.getMessage());
                            }
                        });

                log.info("Ingresando a eliminar archivos nroLote={}", request.getRequest().getNroLote());
                storageService.eliminarArchivosXLote(Integer.parseInt(request.getRequest().getNroLote()));
                log.info("Teminó a eliminar archivos nroLote={}", request.getRequest().getNroLote());
                return getResult(request.getHeader(), "Expedientes generados eliminado correctamente.");
            }else{
                log.info("No hay expedientes generados nroLote={}", request.getRequest().getNroLote());
                return getResult(request.getHeader(), "No existe expedientes generados.");
            }
        } catch (Exception e) {
            log.error("Error en la eliminar los expediente digitales del garante {} con lote {} : {} ", request.getRequest().getGaranteId(), request.getRequest().getNroLote(), e);
            throw new ExpedienteException(e.getMessage(), INTERNAL_SERVER_ERROR);
        }

    }
}
