package pe.com.ci.sed.expediente.service.impl;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.microsoft.azure.storage.StorageErrorCodeStrings;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableQuery;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import pe.com.ci.sed.expediente.errors.ExpedienteException;
import pe.com.ci.sed.expediente.model.request.BusquedaExpediente;
import pe.com.ci.sed.expediente.model.request.GenericRequest;
import pe.com.ci.sed.expediente.model.request.ReporteTotalParcialRequest;
import pe.com.ci.sed.expediente.model.request.RequestReporte;
import pe.com.ci.sed.expediente.model.response.ResponseExpError;
import pe.com.ci.sed.expediente.model.response.ResponseMecYMod;
import pe.com.ci.sed.expediente.persistence.entity.ExpedienteDigitalTable;
import pe.com.ci.sed.expediente.persistence.entity.GestionLote;
import pe.com.ci.sed.expediente.persistence.entity.ReportExcelTotalParcial;
import pe.com.ci.sed.expediente.property.GaranteProperty;
import pe.com.ci.sed.expediente.service.GenerarReporteService;

@Log4j2
@Service
@AllArgsConstructor
public class GenerarReporteServiceImpl implements GenerarReporteService {

    private final ExpedienteServicesTable expedienteImp;
    private final GaranteProperty garanteProperty;
    private final CloudTableClient cloudTableClient;
    private static final String STORAGETABLELOTE = "storagetablelote";
    private static final String STORAGETABLEEXPEDIENTE = "storagetableexpediente";

    public enum ESTADO {EN_PROCESO, PENDIENTE, TERMINADO, ERROR}

    @Override
    public List<ResponseExpError> reporteExpError(GenericRequest<RequestReporte> request) {
        RequestReporte requests = request.getRequest();
        TableQuery<ExpedienteDigitalTable> rangeQuery = TableQuery.from(ExpedienteDigitalTable.class);
        StringBuilder sb = new StringBuilder();
        sb.append("Estado eq '").append(ESTADO.ERROR.name()).append("'");
        Optional.ofNullable(requests.getFechaDesde()).filter(p -> !p.isEmpty()).ifPresent(p -> {
            sb.append(" and FechaCreacion ge datetime'").append(requests.getFechaDesde()).append("T00:00:00.000Z'");
        });
        Optional.ofNullable(requests.getFechaHasta()).filter(p -> !p.isEmpty()).ifPresent(p -> {
            sb.append(" and FechaCreacion lt datetime'").append(requests.getFechaHasta()).append("T23:59:59.000Z'");
        });
        log.debug(" where {}", sb.toString());
        List<ExpedienteDigitalTable> listaTable = new ArrayList<>();
        garanteProperty.getGarantes().parallelStream().forEach(garanteId -> {
            String tableName = STORAGETABLEEXPEDIENTE + garanteId;
            log.info("tableName: {}", tableName);
            try {
                rangeQuery.where(sb.toString());
                CloudTable table = cloudTableClient.getTableReference(tableName);
                if (table.exists()) table.execute(rangeQuery).forEach(listaTable::add);
            } catch (URISyntaxException | StorageException e) {
                // TODO Auto-generated catch block
            }
        });
        List<ResponseExpError> response = new ArrayList<>();
        listaTable.forEach(c -> response.add(ResponseExpError.builder()
                .beneficio(c.getBeneficioDescripcion())
                .nroLote(String.valueOf(c.getNroLote()))
                .facturaNro(c.getFacturaNro())
                .paciente(c.getPacienteNombre() + " " + c.getPacienteApellidoPaterno() + " " + c.getPacienteApellidoMaterno())
                .garanteDescripcion(c.getGaranteDescripcion())
                .pacienteTipoDocIdentDesc(c.getPacienteTipoDocIdentDesc())
                .pacienteNroDocIdent(c.getPacienteNroDocIdent())
                .msjError(c.getMsjError())
                .fechaCreacion(c.getFechaCreacion().toString())

                .build()));
        return response;
    }

    @Override
    public List<ResponseMecYMod> reporteMecYMod(GenericRequest<RequestReporte> requestBusqueda) {
        RequestReporte request = requestBusqueda.getRequest();
        TableQuery<ExpedienteDigitalTable> rangeQuery = TableQuery.from(ExpedienteDigitalTable.class);

        StringBuilder sb = new StringBuilder();
        sb.append(" MecanismoFacturacionId eq '").append(request.getMecanismoFacturacionId()).append("'");
        Optional.ofNullable(request.getFechaDesde()).filter(p -> !p.isEmpty()).ifPresent(p -> {
            sb.append(" and FechaCreacion ge datetime'").append(request.getFechaDesde()).append("T00:00:00.000Z'");
        });
        Optional.ofNullable(request.getFechaHasta()).filter(p -> !p.isEmpty()).ifPresent(p -> {
            sb.append(" and FechaCreacion lt datetime'").append(request.getFechaHasta()).append("T23:59:59.000Z'");
        });

        log.debug(" where {}", sb.toString());

        List<ExpedienteDigitalTable> listaTable = new ArrayList<>();
        garanteProperty.getGarantes().parallelStream().forEach(garanteId -> {
            String tableName = STORAGETABLEEXPEDIENTE + garanteId;
            log.info("tableName: {}", tableName);
            try {
                rangeQuery.where(sb.toString());
                CloudTable table = cloudTableClient.getTableReference(tableName);
                if (table.exists()) table.execute(rangeQuery).forEach(listaTable::add);
            } catch (URISyntaxException | StorageException e) {
                // TODO Auto-generated catch block
            }
        });

        List<ResponseMecYMod> response = new ArrayList<>();

        Map<String, Map<String, List<ExpedienteDigitalTable>>> map = listaTable.stream()
                .collect(Collectors.groupingBy(ExpedienteDigitalTable::getModoFacturacion,
                        Collectors.groupingBy(ExpedienteDigitalTable::getMecanismoFacturacionDesc)));

        map.forEach((modoFacturacion, value) -> value.forEach((mecanismoFacturacion, lista) -> response.add(ResponseMecYMod.builder()
                .mecanismoFacturacionDesc(mecanismoFacturacion)
                .modoFacturacion(modoFacturacion)
                .importe(lista.stream().map(ExpedienteDigitalTable::getFacturaImporte).reduce(0.00, Double::sum))
                .cantidad(lista.size())
                .build())));
        return response;
    }

    @Override
    public List<ReportExcelTotalParcial> reportExcelTotalParcials(GenericRequest<ReporteTotalParcialRequest> request) {
        try {
            List<ReportExcelTotalParcial> result = new ArrayList<>();


            TableQuery<GestionLote> rangeQuery = TableQuery.from(GestionLote.class);
            StringBuilder sb = new StringBuilder();
            sb.append("PartitionKey eq '").append(request.getRequest().getGaranteId()).append("'");
            Optional.of(request.getRequest().getNroLote()).filter(p -> p > 0)
                    .ifPresent(p -> sb.append(" and RowKey eq '").append(request.getRequest().getNroLote()).append("'"));
            rangeQuery.where(sb.toString());
            if (request.getRequest().getTipoReporte().equals("TOTAL")) {

                cloudTableClient.getTableReference(STORAGETABLELOTE).execute(rangeQuery).forEach(c -> {
                    BusquedaExpediente filtro = new BusquedaExpediente();
                    ReportExcelTotalParcial datos = new ReportExcelTotalParcial();
                    datos.setEstado(c.getEstado());
                    datos.setNroLote(c.getNroLote());
                    datos.setGaranteId(c.getGaranteId());
                    datos.setGaranteDescripcion(c.getGaranteDescripcion());
                    datos.setNrosEncuentro(c.getNroEncuentro());
                    filtro.setGaranteId(datos.getGaranteId());
                    filtro.setLote(datos.getNroLote());
                    filtro.setFechaDesde(request.getRequest().getFechaDesde());
                    filtro.setFechaHasta(request.getRequest().getFechaHasta());
                    List<ExpedienteDigitalTable> expedientes;
                    try {
                        expedientes = expedienteImp.listaExpediente(filtro);
                        expedientes.forEach(d -> {
                            ReportExcelTotalParcial doc = new ReportExcelTotalParcial();
                            doc.setFacturaNro(d.getFacturaNro());
                            doc.setFacturaImporte(d.getFacturaImporte());
                            doc.setNroLote(datos.getNroLote());
                            doc.setGaranteId(datos.getGaranteId());
                            doc.setGaranteDescripcion(datos.getGaranteDescripcion());
                            doc.setEstado(datos.getEstado());
                            doc.setNrosEncuentro(d.getNroEncuentro());
                            doc.setFechaCreacion(d.getFechaCreacion());
                            doc.setEstadoFactura(d.getEstado());
                            doc.setMecanismoFacturacionDesc(d.getMecanismoFacturacionDesc());
                            doc.setModoFacturacion(d.getModoFacturacion());
                            doc.setFechaAtencion(d.getFechaAtencion());
                            doc.setFechaAceptacion(c.getFechaAceptacion());
                            doc.setPaciente(d.getPacienteNombre() + " " + d.getPacienteApellidoPaterno() + " " + d.getPacienteApellidoMaterno());
                            result.add(doc);
                        });
                    } catch (URISyntaxException | StorageException e) {
                       log.error("Error message: {}",e.getMessage());
                    }
                });
            } else {
                ReportExcelTotalParcial datos = new ReportExcelTotalParcial();
                BusquedaExpediente filtro = new BusquedaExpediente();
                cloudTableClient.getTableReference(STORAGETABLELOTE).execute(rangeQuery).forEach(c -> {
                    datos.setEstado(c.getEstado());
                    datos.setNroLote(c.getNroLote());
                    datos.setGaranteId(c.getGaranteId());
                    datos.setGaranteDescripcion(c.getGaranteDescripcion());

                });
                filtro.setGaranteId(datos.getGaranteId());
                filtro.setLote(datos.getNroLote());
                List<ExpedienteDigitalTable> expedientes = expedienteImp.listaExpediente(filtro);
                expedientes.forEach(d -> {
                    ReportExcelTotalParcial doc = new ReportExcelTotalParcial();
                    doc.setFacturaNro(d.getFacturaNro());
                    doc.setFacturaImporte(d.getFacturaImporte());
                    doc.setNroLote(datos.getNroLote());
                    doc.setGaranteId(datos.getGaranteId());
                    doc.setGaranteDescripcion(datos.getGaranteDescripcion());
                    doc.setEstado(datos.getEstado());
                    doc.setNrosEncuentro(d.getNroEncuentro());
                    doc.setFechaCreacion(d.getFechaCreacion());
                    doc.setEstadoFactura(d.getEstado());
                    doc.setMecanismoFacturacionDesc(d.getMecanismoFacturacionDesc());
                    doc.setModoFacturacion(d.getModoFacturacion());
                    doc.setFechaAtencion(d.getFechaAtencion());
                    doc.setFechaAceptacion(doc.getFechaAceptacion());
                    doc.setPaciente(d.getPacienteNombre() + " " + d.getPacienteApellidoPaterno() + " " + d.getPacienteApellidoMaterno());
                    result.add(doc);
                });
            }
            return result;
        } catch (StorageException e) {
            String message = e.getLocalizedMessage();
            if (e.getErrorCode().equals(StorageErrorCodeStrings.ENTITY_ALREADY_EXISTS))
                message = "Ya Existe";
            throw new ExpedienteException(message, HttpStatus.CONFLICT, request.getHeader());
        } catch (URISyntaxException e) {
            throw new ExpedienteException(e.getMessage(), HttpStatus.CONFLICT, request.getHeader());
        }
    }

    @Override
    public String validarNumeroLote(GenericRequest<ReporteTotalParcialRequest> genericRequest) {
        ReporteTotalParcialRequest request = genericRequest.getRequest();
        if (Strings.isBlank(request.getGaranteId())) return "El Garante ID es Requerido";
        if (request.getNroLote() == 0) return "El Nro Lote es Requerido";

        TableQuery<ExpedienteDigitalTable> rangeQuery = TableQuery.from(ExpedienteDigitalTable.class);
        List<ExpedienteDigitalTable> lista = new ArrayList<>();
        rangeQuery.where("RowKey eq '" + request.getNroLote() + "'");
        try {
            cloudTableClient.getTableReference(STORAGETABLELOTE).execute(rangeQuery).forEach(lista::add);
            if (lista.isEmpty()) return "No se encontró el Nro Lote: " + request.getNroLote();
            return Strings.EMPTY;
        } catch (URISyntaxException | StorageException e) {
            return e.getMessage();
        }
    }
}
