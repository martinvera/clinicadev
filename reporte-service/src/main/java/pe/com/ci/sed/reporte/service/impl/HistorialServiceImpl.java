package pe.com.ci.sed.reporte.service.impl;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TableQuery;
import com.microsoft.azure.storage.table.TableResult;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pe.com.ci.sed.reporte.errors.ReporteException;
import pe.com.ci.sed.reporte.model.generic.GenericRequest;
import pe.com.ci.sed.reporte.model.request.RequestReporte;
import pe.com.ci.sed.reporte.persistence.entity.Historial;
import pe.com.ci.sed.reporte.service.HistorialService;
import pe.com.ci.sed.reporte.service.StorageService;
import pe.com.ci.sed.reporte.utils.Constants;
import pe.com.ci.sed.reporte.utils.TipoReporte;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.microsoft.azure.storage.table.TableQuery.*;
import static com.microsoft.azure.storage.table.TableQuery.Operators.AND;
import static com.microsoft.azure.storage.table.TableQuery.QueryComparisons.NOT_EQUAL;
import static pe.com.ci.sed.reporte.utils.GenericUtil.getResult;

@Log4j2
@Service
@AllArgsConstructor
public class HistorialServiceImpl implements HistorialService {

    private final CloudTable tableService;
    private final StorageService storageService;

    @Override
    public Historial registrarHistorial(GenericRequest<Historial> genericRequest) {
        return this.registrarHistorial(genericRequest.getRequest());
    }

    @Override
    public Historial registrarHistorial(Historial request) {
        try {
            this.validaTipoReporte(request.getTipo());

            request.setPartitionKey(request.getTipo());
            request.setRowKey(UUID.randomUUID().toString());

            this.eliminarUltimoRegistro(request);

            TableOperation tableOperation = TableOperation.insert(request);
            TableResult result = tableService.execute(tableOperation);
            if (result.getHttpStatusCode() == HttpStatus.NO_CONTENT.value()) {
                log.debug("historial-registrado: {}", request.getTipo());
                return result.getResultAsType();
            }
        } catch (StorageException e) {
            throw new ReporteException(e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public void actualizarHistorial(Historial request) {
		log.debug("Ingresando a actualizar historial: {}", request.getTipo());
        try {
            this.validaTipoReporte(request.getTipo());
            TableOperation tableOperation = TableOperation.replace(request);
            TableResult result = tableService.execute(tableOperation);
            if (result.getHttpStatusCode() == HttpStatus.NO_CONTENT.value()) {
                log.debug("historial-actualizado: {}", request.getTipo());
            }
        } catch (StorageException e) {
            log.info("Error al actualizar historial: Code: {}, Message: {}", e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public Object listarHistorial(GenericRequest<RequestReporte> genericRequest) {
        RequestReporte request = genericRequest.getRequest();
        this.validaTipoReporte(request.getTipoReporte());
        return getResult(genericRequest.getHeader(), getReportes(request.getTipoReporte(), request.getGaranteDescripcion()));
    }

    public void validaTipoReporte(String tipo) {
        List<String> tipos = Arrays.stream(Constants.REPORTE.values()).map(Constants.REPORTE::name).collect(Collectors.toList());
        if (!tipos.contains(tipo) && Strings.isNotBlank(tipo)) {
            throw new ReporteException("Debe ingresar un tipo de reporte válido: " + String.join(",", tipos));
        }
    }

    public List<Historial> getReportes(String tipoReporte, String garante) {
        List<Historial> historials = new ArrayList<>();
        TableQuery<Historial> tableQuery = from(Historial.class);
        StringBuilder filtro = new StringBuilder();
        if (Strings.isNotBlank(tipoReporte)) {
            if (tipoReporte.equals(TipoReporte.OTROS.name())) {
				filtro.append(TableQuery.generateFilterCondition("PartitionKey", NOT_EQUAL, TipoReporte.TOTAL.name())).append(" ").append(AND).append(" ").append(TableQuery.generateFilterCondition("PartitionKey", NOT_EQUAL, TipoReporte.PARCIAL.name()));
            } else {
            	filtro.append("PartitionKey eq '").append(tipoReporte).append("'");
            }
        }
        if (Strings.isNotBlank(garante)) {
        	filtro.append(" and Garante eq '").append(garante).append("'");
        }
        log.debug("query: {}", filtro);
        if (Strings.isNotBlank(filtro.toString())) tableQuery.where(filtro.toString());
        
        tableService.execute(tableQuery).forEach(historials::add);
        
        
        return historials.stream().map( x -> {
        	x.setUrl(storageService.getUrlWithSas(x.getUrlSas()) );
        	return x;
        }).sorted(Comparator.comparing(Historial::getFecha).reversed())
                .collect(Collectors.toList());
    }

    private void eliminarUltimoRegistro(Historial request) throws StorageException {
        log.debug("Ingreso a eliminar el último registro, TipoReporte: {}", request.getTipo());
        List<Historial> historials;
        if (!request.getTipo().equals(TipoReporte.TOTAL.name()))
            historials = this.getReportes(request.getTipo(), Strings.EMPTY);
        else
            historials = this.getReportes(request.getTipo(), request.getGarante());

        Optional.ofNullable(historials).filter(x -> (request.getTipo().equals(TipoReporte.PARCIAL.name()) && x.size() >= 15) || (x.size() >=5)).ifPresent( x -> confirmarELiminar(x) );
    }

    private void confirmarELiminar(List<Historial> historials) {
    	
    	try {
    		AtomicReference<Historial> ultimo = new AtomicReference<>(null);
            historials.stream().max(Comparator.comparing(Historial::getTimestamp, Comparator.nullsLast(Comparator.reverseOrder()))).ifPresent(ultimo::set);
            TableOperation deleteOperation = TableOperation.delete(ultimo.get());
            tableService.execute(deleteOperation);
            storageService.delete(ultimo.get().getUrlSas());
		} catch (StorageException e) {
			log.info("Error al eliminar el historial de reportes, Code: {}, Message: {}", e.getErrorCode(), e.getMessage());
		}
        
    }

}
