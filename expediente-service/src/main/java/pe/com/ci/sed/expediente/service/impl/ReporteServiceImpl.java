package pe.com.ci.sed.expediente.service.impl;

import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static pe.com.ci.sed.expediente.utils.GenericUtil.getResult;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TableQuery;
import com.microsoft.azure.storage.table.TableResult;

import lombok.extern.log4j.Log4j2;
import pe.com.ci.sed.expediente.errors.ExpedienteException;
import pe.com.ci.sed.expediente.model.request.GenericRequest;
import pe.com.ci.sed.expediente.model.request.ReporteTotalParcialRequest;
import pe.com.ci.sed.expediente.model.request.RequestExpenGenerado;
import pe.com.ci.sed.expediente.persistence.entity.ExpedienteDigitalTable;
import pe.com.ci.sed.expediente.persistence.entity.ExpedientesGeneradosEstado;
import pe.com.ci.sed.expediente.persistence.entity.ExpedientesGeneradosOrigen;
import pe.com.ci.sed.expediente.persistence.entity.ReporteExpedienteGenerados;
import pe.com.ci.sed.expediente.persistence.entity.ReporteTotalParcial;
import pe.com.ci.sed.expediente.service.ReporteService;
import pe.com.ci.sed.expediente.utils.ConexionUtil;

@Log4j2
@Service

public class ReporteServiceImpl extends ConexionUtil implements ReporteService {

    private static final String GARANTE_DESC="GaranteDescripcion";
    private static final String TIPO_REPORTE="TipoReporte";
    @Autowired
    private CloudTableClient cloudTableClient;
    private final static String tableNamePrefix = "storagetableexpediente";
    private static final String tableName = "storagetablereportegarante";

    public ReporteServiceImpl(CloudTableClient cloudTableClient) {
        super(cloudTableClient);
    }

    @Override
    public Object reporteExpedientesGenerados(RequestExpenGenerado request) {


        String tableName = tableNamePrefix + request.getRequest().getGaranteId();
        TableQuery<ExpedienteDigitalTable> rangeQuery = TableQuery.from(ExpedienteDigitalTable.class);
        StringBuffer sb = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
        Optional.ofNullable(request.getRequest().getNroLote()).filter(p->!p.isEmpty()).ifPresent(p->{
            sb.append("PartitionKey eq '").append(request.getRequest().getNroLote()).append("'");
            sb2.append("PartitionKey eq '").append(request.getRequest().getNroLote()).append("'");
        });
        Optional.ofNullable(request.getRequest().getFechaDesde()).filter(p -> !p.isEmpty()).ifPresent(p -> {
            sb.append(" and FechaCreacion ge datetime'").append(request.getRequest().getFechaDesde()).append("T00:00:00.000Z'");
            sb2.append(" and FechaCreacion ge datetime'").append(request.getRequest().getFechaDesde()).append("T00:00:00.000Z'");
        });
        Optional.ofNullable(request.getRequest().getFechaHasta()).filter(p -> !p.isEmpty()).ifPresent(p -> {
            sb.append(" and FechaCreacion lt datetime'").append(request.getRequest().getFechaHasta()).append("T23:59:59.000Z'");
            sb2.append(" and FechaCreacion lt datetime'").append(request.getRequest().getFechaHasta()).append("T23:59:59.000Z'");
        });
        sb2.append(" and Estado eq 'ERROR'");
        List<ExpedienteDigitalTable> listaTable = new ArrayList<>();
        List<ExpedienteDigitalTable> listaTable2 = new ArrayList<>();
        try {
            rangeQuery.where(sb.toString());

            log.info("tableName: {}", tableName);
            CloudTable table = cloudTableClient.getTableReference(tableName);
            if (table.exists()) table.execute(rangeQuery).forEach(listaTable::add);
            rangeQuery.where(sb2.toString());
            log.debug(" where {}", sb.toString());
            if (table.exists()) table.execute(rangeQuery).forEach(listaTable2::add);

        } catch (URISyntaxException | StorageException e) {
            // TODO Auto-generated catch block
        }
        Map<String, List<ExpedienteDigitalTable>> map = listaTable.stream()
                .collect(Collectors.groupingBy(ExpedienteDigitalTable::getEstado));

        Map<String, List<ExpedienteDigitalTable>> map2 = listaTable2.stream()
                .collect(Collectors.groupingBy(ExpedienteDigitalTable::getOrigen));

        List<ExpedientesGeneradosEstado> list = new ArrayList<>();
        List<ExpedientesGeneradosOrigen> list2 = new ArrayList<>();
        map.forEach((estado,value)->{
            list.add(ExpedientesGeneradosEstado.builder()
                    .estado(estado)
                    .totalxestado(value.size())
                    .build());
        });
        map2.forEach((origen,value)->{
            list2.add(ExpedientesGeneradosOrigen.builder()
                    .origen(origen)
                    .totalxorigen(value.size())
                    .build());
        });
        ReporteExpedienteGenerados result = new ReporteExpedienteGenerados();
        result.setGeneradosEstadoList(list);
        result.setGeneradosOrigenList(list2);
        result.setFechaDesdeHasta(request.getRequest().getFechaDesde() + " - " + request.getRequest().getFechaHasta());
        result.setGaranteDescripcion(request.getRequest().getGaranteDescripcion());
        return getResult(request.getHeader(),result);
    }
    @Override
    public Object registrarReporteTotalParcial(GenericRequest<ReporteTotalParcial> genericrequest){
        try{
            ReporteTotalParcial request = genericrequest.getRequest();
            request.setPartitionKey(request.getTipoReporte());
            request.setRowKey(UUID.randomUUID().toString());
            TableOperation tableOperation = TableOperation.insert(request);
            TableResult result = getTable(tableName).execute(tableOperation);
            if(result.getHttpStatusCode() == HttpStatus.NO_CONTENT.value()){
                log.info("reporte-registrado: {}", request.getTipoReporte());
                return getResult(genericrequest.getHeader(), result.getResultAsType());
            }else{
                log.info("error-registro-reporte");
                return getResult(genericrequest.getHeader(), "Error al registrar Reporte");
            }
        }catch (StorageException e){
            throw new ExpedienteException(e.getLocalizedMessage());
        }
    }
    @Override
    public Object listarReporteTotalParcial(GenericRequest<ReporteTotalParcialRequest> request){
        List<ReporteTotalParcial> reportes = new ArrayList<>();
        String where = TableQuery.generateFilterCondition(TIPO_REPORTE, TableQuery.QueryComparisons.EQUAL, request.getRequest().getTipoReporte());
        TableQuery<ReporteTotalParcial> tableQuery =null;
        if(request.getRequest().getTipoReporte().equals("TOTAL")){
            if(isNotBlank(request.getRequest().getGaranteDescripcion())){
                String whereDoc = TableQuery.generateFilterCondition(GARANTE_DESC, TableQuery.QueryComparisons.EQUAL, request.getRequest().getGaranteDescripcion());
                String combinedFilter = TableQuery.combineFilters(where, TableQuery.Operators.AND, whereDoc);
                tableQuery = TableQuery.from(ReporteTotalParcial.class).where(combinedFilter);

            }else{
                tableQuery = TableQuery.from(ReporteTotalParcial.class).where(where);
            }
        }else{
            tableQuery = TableQuery.from(ReporteTotalParcial.class).where(where);
        }
        getTable(tableName).execute(tableQuery).forEach(reportes::add);
        return getResult(request.getHeader(),reportes);
    }
}
