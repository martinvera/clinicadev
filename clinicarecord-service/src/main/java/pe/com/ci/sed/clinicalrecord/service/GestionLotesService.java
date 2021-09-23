package pe.com.ci.sed.clinicalrecord.service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.azure.cosmos.models.PartitionKey;
import com.microsoft.azure.storage.ResultContinuation;
import com.microsoft.azure.storage.ResultSegment;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TableQuery;
import com.microsoft.azure.storage.table.TableResult;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import pe.com.ci.sed.clinicalrecord.errors.ClinicalRecordException;
import pe.com.ci.sed.clinicalrecord.model.generic.Paginacion;
import pe.com.ci.sed.clinicalrecord.model.request.BusquedaHistorial;
import pe.com.ci.sed.clinicalrecord.model.request.BusquedaLotes;
import pe.com.ci.sed.clinicalrecord.model.request.EliminarRequest;
import pe.com.ci.sed.clinicalrecord.model.generic.GenericRequest;
import pe.com.ci.sed.clinicalrecord.persistence.entity.*;
import pe.com.ci.sed.clinicalrecord.persistence.repository.ClinicalRecordRepository;
import pe.com.ci.sed.clinicalrecord.utils.ConexionUtil;
import pe.com.ci.sed.clinicalrecord.utils.Constants;
import pe.com.ci.sed.clinicalrecord.utils.GenericUtil;

import static pe.com.ci.sed.clinicalrecord.utils.Constants.Filtro.AND_ESTADO_EQ;
import static pe.com.ci.sed.clinicalrecord.utils.Constants.Filtro.AND_TIMESTAMP_GE_DATETIME;
import static pe.com.ci.sed.clinicalrecord.utils.Constants.Filtro.AND_TIMESTAMP_LT_DATETIME;
import static pe.com.ci.sed.clinicalrecord.utils.Constants.Filtro.T_00_00_00_000_Z;
import static pe.com.ci.sed.clinicalrecord.utils.Constants.Filtro.T_23_59_59_000_Z;
import static pe.com.ci.sed.clinicalrecord.utils.GenericUtil.FORMATSLASH_DDMMYYYY;
import static pe.com.ci.sed.clinicalrecord.utils.GenericUtil.FORMAT_YYYYMMDD;
import static pe.com.ci.sed.clinicalrecord.utils.GenericUtil.getDate;
import static pe.com.ci.sed.clinicalrecord.utils.GenericUtil.getResult;
import static pe.com.ci.sed.clinicalrecord.utils.GenericUtil.getResultPaginacion;
import static pe.com.ci.sed.clinicalrecord.utils.GenericUtil.getResultSetPaginacion;

@Log4j2
@Service
@AllArgsConstructor
public class GestionLotesService {

    public static final String PARAM_TODOS = "TODOS";
    public static final String PARTITION_KEY = "PartitionKey";
    public static final String ELIMINAR_EXPEDIENTE_XNRO_LOTE = "/eliminarExpdienteXNroLote";

    private enum estado {PENDIENTE, EN_PROCESO, TERMINADO}

    private final CloudTable cloudTable;
    private final CloudTable cloudTableH;
    private final ClinicalRecordRepository clinicalRecordRepository;
    private final RestTemplate restTemplateExpediente;
    private final RestTemplate restTemplateDocument;
    private final StorageService storageService;


    public Object buscarLotes(GenericRequest<BusquedaLotes> requestBusqueda) {
        BusquedaLotes request = requestBusqueda.getRequest();
        ResultSegment<GestionLote> lotes = this.listarLotes(request);
        Comparator<GestionLote> comparator = Comparator.comparingLong(c -> c.getTimestamp().getTime());
        lotes.getResults().sort(comparator);
        Collections.reverse(lotes.getResults());
        lotes.getResults().forEach(x -> x.setUrlArchivoZipSas(storageService.getUrlWithSas(x.getUrlArchivoZip())));
        return getResultSetPaginacion(requestBusqueda.getHeader(), lotes);
    }

    private ResultSegment<GestionLote> listarLotes(BusquedaLotes request) {
        ResultContinuation continuationToken = ConexionUtil.getContinuationToken(Paginacion.builder()
                .siguiente(request.getSiguiente())
                .atras(request.getAtras())
                .build());
        TableQuery<GestionLote> rangeQuery = TableQuery.from(GestionLote.class).take(request.getSize());

        StringBuilder sb = new StringBuilder();

        sb.append(PARTITION_KEY + " eq '").append(request.getGaranteId()).append("'");

        Optional.ofNullable(request.getEstado()).filter(p -> !p.isEmpty())
                .ifPresent(estado -> sb.append(AND_ESTADO_EQ).append(estado).append("'"));
        Optional.of(request.getNroLote()).filter(p -> p > 0)
                .ifPresent(nroLote -> sb.append(" and RowKey eq '").append(nroLote).append("'"));
        Optional.ofNullable(request.getFechaLoteDesde()).filter(p -> !p.isEmpty())
                .ifPresent(fecDesde -> sb.append(AND_TIMESTAMP_GE_DATETIME).append(fecDesde).append(T_00_00_00_000_Z));
        Optional.ofNullable(request.getFechaLoteHasta()).filter(p -> !p.isEmpty())
                .ifPresent(fecHasta -> sb.append(AND_TIMESTAMP_LT_DATETIME).append(fecHasta).append(T_23_59_59_000_Z));
        Optional.ofNullable(request.getEstadoGarante()).filter(p -> !p.isEmpty())
                .ifPresent(estGarante -> sb.append(" and EstadoGarante eq '").append(estGarante).append("'"));

        rangeQuery.where(sb.toString());


        OperationContext.setLoggingEnabledByDefault(false);
        OperationContext ctx = new OperationContext();
        ctx.setLoggingEnabled(true);

        try {
            return cloudTable.executeSegmented(rangeQuery, continuationToken, null, ctx);
        } catch (StorageException e) {
            e.printStackTrace();
            throw new ClinicalRecordException(e.getLocalizedMessage());
        }
    }

    public List<GestionLote> listarEnviadosGaranteReporte(GenericRequest<BusquedaLotes> requestBusqueda) {
        List<GestionLote> lotesEnviados = new ArrayList<>();
        TableQuery<GestionLote> rangeQuery = TableQuery.from(GestionLote.class);
        StringBuilder sb = new StringBuilder();
        if (!requestBusqueda.getRequest().getGaranteId().equals(PARAM_TODOS)) {
            sb.append(PARTITION_KEY + " eq '").append(requestBusqueda.getRequest().getGaranteId()).append("'");
        }
        Optional.ofNullable(requestBusqueda.getRequest().getEstado()).filter(p -> !p.isEmpty()).ifPresent(p -> {
            if (requestBusqueda.getRequest().getGaranteId().equals(PARAM_TODOS)) {
                sb.append(" Estado eq '").append(estado.TERMINADO.name()).append("'");
            } else {
                sb.append(AND_ESTADO_EQ).append(estado.TERMINADO.name()).append("'");
            }
        });
        Optional.ofNullable(requestBusqueda.getRequest().getFechaLoteDesde()).filter(p -> !p.isEmpty())
                .ifPresent(fecDesde -> sb.append(AND_TIMESTAMP_GE_DATETIME).append(fecDesde).append(T_00_00_00_000_Z));

        Optional.ofNullable(requestBusqueda.getRequest().getFechaLoteHasta()).filter(p -> !p.isEmpty())
                .ifPresent(fecHasta -> sb.append(AND_TIMESTAMP_LT_DATETIME).append(fecHasta).append(T_23_59_59_000_Z));

        rangeQuery.where(sb.toString());
        cloudTable.execute(rangeQuery).forEach(d -> {
            d.setFechaLote(getDate(d.getTimestamp(), FORMATSLASH_DDMMYYYY));
            lotesEnviados.add(d);
        });
        return lotesEnviados;
    }

    public List<GestionLotesEnviados> listarEnviadoGarante(GenericRequest<BusquedaLotes> request) {

        List<GestionLotesEnviados> result = new ArrayList<>();
        List<EstadoTotal> lotesEnviados = new ArrayList<>();
        List<GestionLote> lotes = new ArrayList<>();
        TableQuery<GestionLote> rangeQuery = TableQuery.from(GestionLote.class);
        StringBuilder sb = new StringBuilder();
        if (!request.getRequest().getGaranteId().equals(PARAM_TODOS)) {
            sb.append(PARTITION_KEY + " eq '").append(request.getRequest().getGaranteId()).append("'");
        }
        Optional.ofNullable(request.getRequest().getEstado()).filter(p -> !p.isEmpty()).ifPresent(estado -> {
            if (request.getRequest().getGaranteId().equals(PARAM_TODOS)) {
                sb.append(" Estado eq '").append(estado).append("'");
            } else {
                sb.append(AND_ESTADO_EQ).append(estado).append("'");
            }
        });
        Optional.ofNullable(request.getRequest().getFechaLoteDesde()).filter(p -> !p.isEmpty())
                .ifPresent(fecDesde -> sb.append(AND_TIMESTAMP_GE_DATETIME).append(fecDesde).append(T_00_00_00_000_Z));

        Optional.ofNullable(request.getRequest().getFechaLoteHasta()).filter(p -> !p.isEmpty())
                .ifPresent(fecHasta -> sb.append(AND_TIMESTAMP_LT_DATETIME).append(fecHasta).append(T_23_59_59_000_Z));

        rangeQuery.where(sb.toString());
        cloudTable.execute(rangeQuery).forEach(lotes::add);
        Map<String, List<GestionLote>> map = lotes.stream()
                .collect(Collectors.groupingBy(GestionLote::getEstadoGarante));

        map.forEach((estado, value) -> lotesEnviados.add(EstadoTotal.builder()
                .estadoGarante(estado)
                .total(value.size())
                .build()));
        GestionLotesEnviados resultado = GestionLotesEnviados.builder()
                .estadoTotalList(lotesEnviados)
                .fechaDesdeHasta(request.getRequest().getFechaLoteDesde() + " - " + request.getRequest().getFechaLoteHasta())
                .garanteDescripcion(request.getRequest().getGaranteDescripcion())
                .build();
        result.add(resultado);
        return result;
    }

    public Object eliminarLote(GenericRequest<EliminarRequest> request) {

        TableOperation retrieveGestionLote = TableOperation.retrieve(request.getRequest().getGaranteId(), request.getRequest().getNroLote(), GestionLote.class);

        AtomicReference<String> mensaje = new AtomicReference<>("Lote elminado correctamente.");
        try {
            Optional.ofNullable((GestionLote) cloudTable.execute(retrieveGestionLote).getResultAsType())
                    .ifPresentOrElse(gestionLote -> {
                        try {
                            TableOperation deleteGestionLote = TableOperation.delete(gestionLote);

                            log.info("Eliminando expedientes digitales del lote: {}", gestionLote.getNroLote());
                            this.eliminarExpedienteDigitalOrDocumentos(restTemplateExpediente, ELIMINAR_EXPEDIENTE_XNRO_LOTE, request);

                            log.info("Eliminando registro de lote");
                            cloudTable.execute(deleteGestionLote);

                            log.info("Eliminando facturas del lote");
                            clinicalRecordRepository.deleteAllByNroLote(Long.parseLong(request.getRequest().getNroLote()));

                        } catch (StorageException e) {
                            throw new ClinicalRecordException(e.getMessage(), HttpStatus.resolve(e.getHttpStatusCode()));
                        }
                    }, () -> mensaje.set("Número de lote no encontrado."));
        } catch (StorageException e) {
            throw new ClinicalRecordException(e.getMessage(), HttpStatus.resolve(e.getHttpStatusCode()));
        }
        return getResult(request.getHeader(), mensaje.get());
    }

    public void eliminarExpedienteDigitalOrDocumentos(RestTemplate restTemplate, String url, GenericRequest<EliminarRequest> request) {
        Integer nroLote = Integer.valueOf(request.getRequest().getNroLote());
        log.info("Ingresando eliminar en expediente-service :: nroLote={}", nroLote);
        ResponseEntity<String> response = GenericUtil.getStringResponseEntity(restTemplate, url, request);
        if (response.getStatusCodeValue() != HttpStatus.OK.value())
            throw new ClinicalRecordException("Error en la eliminación de expediente generados.");
        log.info("Terminó eliminar en expediente-service :: nroLote={}", nroLote);
    }

    public void actualizarFacturaGenerada(String garanteId, String nroLote, int cantidad, String urlZipLote) {
        try {
            TableOperation retrieve = TableOperation.retrieve(garanteId, nroLote, GestionLote.class);
            GestionLote existLote = cloudTable.execute(retrieve).getResultAsType();

            existLote.setFacturasGeneradas(existLote.getFacturasGeneradas() + cantidad);
            existLote.setUrlArchivoZip(urlZipLote);

            if (existLote.getFacturasGeneradas().equals(existLote.getTotalFacturas())) {
                existLote.setEstado(estado.TERMINADO.name());
            } else {
                if (cantidad > 0) existLote.setEstado(estado.EN_PROCESO.name());
            }

            TableOperation replaceEntity = TableOperation.replace(existLote);
            TableResult result = cloudTable.execute(replaceEntity);

            if (result.getHttpStatusCode() == HttpStatus.NO_CONTENT.value())
                log.debug("lote actualizado Nro: {}", existLote.getRowKey());
        } catch (StorageException e) {
            log.error("Erro code: {}, Message: {}", e.getErrorCode(), e.getMessage());
        }
    }

    public void reprocesoActualizarLote(String garanteId, String nroLote) {
        try {
            TableOperation retrieve = TableOperation.retrieve(garanteId, nroLote, GestionLote.class);
            GestionLote existLote = cloudTable.execute(retrieve).getResultAsType();
            if (Objects.nonNull(existLote)) {
                existLote.setEstado(estado.PENDIENTE.name());
                existLote.setEstadoGarante(estado.PENDIENTE.name());
                existLote.setFechaEnvio("-");
                existLote.setFechaAceptacion("-");
                existLote.setFechaRechazo("-");
                existLote.setFechaRegistroSolicitud("-");
                existLote.setRegistroSolicitud("-");
                existLote.setFacturasGeneradas(0);
                TableOperation replaceEntity = TableOperation.replace(existLote);
                TableResult result = cloudTable.execute(replaceEntity);
                if (result.getHttpStatusCode() == HttpStatus.NO_CONTENT.value())
                    log.debug("lote actualizado Nro: {}", existLote.getRowKey());
                HistorialLoteGarante historial = buildHistorial(existLote);
                registrarHistorial(historial);
            }
        } catch (Exception e) {
            throw new ClinicalRecordException("Error cambiar estado de Lote", HttpStatus.CONFLICT);
        }
    }

    public void registrarActualizarLotes(GestionLote gestionLote) {
        try {
            gestionLote.setPartitionKey(gestionLote.getGaranteId());
            gestionLote.setRowKey(String.valueOf(gestionLote.getNroLote()));
            TableOperation retrieve = TableOperation.retrieve(gestionLote.getPartitionKey(), gestionLote.getRowKey(), GestionLote.class);


            Optional.ofNullable((GestionLote) cloudTable.execute(retrieve).getResultAsType())
                    .ifPresentOrElse(existLote -> {
                        try {
                            existLote.setTotalFacturas(existLote.getTotalFacturas() + 1);

                            if (existLote.getFacturasGeneradas() > 0) {
                                existLote.setEstado(estado.EN_PROCESO.name());
                            } else {
                                existLote.setEstado(estado.PENDIENTE.name());
                            }

                            TableOperation replaceEntity = TableOperation.replace(existLote);
                            TableResult result = cloudTable.execute(replaceEntity);
                            if (result.getHttpStatusCode() == HttpStatus.NO_CONTENT.value())
                                log.debug("lote actualizado {}", existLote.getRowKey());
                            else
                                log.debug("lote no actualizado");

                        } catch (StorageException e) {
                            log.error(e);
                        }
                    }, () -> {
                        try {
                            gestionLote.setEstado(estado.PENDIENTE.name());
                            TableOperation insert = TableOperation.insertOrReplace(gestionLote);
                            TableResult result = cloudTable.execute(insert);
                            HistorialLoteGarante historial = buildHistorial(gestionLote);
                            registrarHistorial(historial);
                            if (result.getHttpStatusCode() == HttpStatus.NO_CONTENT.value())
                                log.debug("lote registrado");
                            else
                                log.debug("lote no registrado");
                        } catch (Exception e2) {
                            log.error(e2);
                        }
                    });

        } catch (Exception e) {
            throw new ClinicalRecordException("Error registro lote", HttpStatus.CONFLICT);
        }
    }

    public HistorialLoteGarante registrarHistorial(GenericRequest<HistorialLoteGarante> genericRequest) {
        HistorialLoteGarante request = genericRequest.getRequest();

        GestionLote gestionLote = GestionLote.builder()
                .garanteId(request.getGaranteId())
                .nroLote(request.getNroLote())
                .build();
        actualizar(gestionLote, request);
        return this.registrarHistorial(request);
    }

    public HistorialLoteGarante registrarHistorial(HistorialLoteGarante historialgarante) {
        try {
            historialgarante.setPartitionKey(historialgarante.getGaranteId() + "_" + historialgarante.getNroLote());
            historialgarante.setRowKey(String.valueOf(UUID.randomUUID().toString()));
            TableOperation tableOperation = TableOperation.insert(historialgarante);
            TableResult result = cloudTableH.execute(tableOperation);
            if (result.getHttpStatusCode() == HttpStatus.NO_CONTENT.value()) {
                log.info("historial-registrado: {}", historialgarante.getGaranteId());

                return result.getResultAsType();
            }
        } catch (StorageException e) {
            throw new ClinicalRecordException(e.getLocalizedMessage());
        }
        return null;
    }

    public Object buscarHistorialGarante(GenericRequest<BusquedaHistorial> requestBusqueda) {
        BusquedaHistorial request = requestBusqueda.getRequest();
        List<HistorialLoteGarante> historial = this.listarHistorail(request);
        Comparator<HistorialLoteGarante> comparator = Comparator.comparingLong(c -> c.getTimestamp().getTime());
        historial.sort(comparator);
        Collections.reverse(historial);
        Paginacion paginacion = GenericUtil.paginacion(request.getActual(), historial.size(), request.getSize());
        return getResultPaginacion(requestBusqueda.getHeader(), historial, paginacion);
    }

    private List<HistorialLoteGarante> listarHistorail(BusquedaHistorial request) {
        List<HistorialLoteGarante> historialLoteGarantes = new ArrayList<>();
        TableQuery<HistorialLoteGarante> rangeQuery = TableQuery.from(HistorialLoteGarante.class);
        StringBuilder sb = new StringBuilder();
        sb.append(PARTITION_KEY + " eq '").append(request.getGaranteId()).append("_").append(request.getNroLote()).append("'");
        Optional.ofNullable(request.getEstado()).filter(p -> !p.isEmpty())
                .ifPresent(estado -> sb.append(AND_ESTADO_EQ).append(estado).append("'"));
        rangeQuery.where(sb.toString());
        OperationContext.setLoggingEnabledByDefault(false);
        OperationContext ctx = new OperationContext();
        ctx.setLoggingEnabled(true);
        cloudTableH.execute(rangeQuery, null, ctx).forEach(historialLoteGarantes::add);
        return historialLoteGarantes;
    }

    private HistorialLoteGarante buildHistorial(GestionLote gestionLote) {
        return HistorialLoteGarante.builder()
                .nroLote(gestionLote.getNroLote())
                .estado(gestionLote.getEstadoGarante())
                .fechaLote(getDate(gestionLote.getTimestamp(), FORMAT_YYYYMMDD))
                .fechaEnvio(gestionLote.getFechaEnvio())
                .fechaAceptacion(gestionLote.getFechaAceptacion())
                .userName(gestionLote.getUsuario())
                .garanteId(gestionLote.getGaranteId())
                .fechaRechazo(gestionLote.getFechaRechazo())
                .fechaRegistroSolicitud(gestionLote.getFechaRegistroSolicitud())
                .registroSolicitud(gestionLote.getRegistroSolicitud())
                .build();
    }

    private void actualizar(GestionLote gestionLote, HistorialLoteGarante historial) {
        try {
            gestionLote.setPartitionKey(gestionLote.getGaranteId());
            gestionLote.setRowKey(String.valueOf(gestionLote.getNroLote()));
            TableOperation retrieve = TableOperation.retrieve(gestionLote.getPartitionKey(), gestionLote.getRowKey(), GestionLote.class);
            Optional.ofNullable((GestionLote) cloudTable.execute(retrieve).getResultAsType())
                    .ifPresent(existLote -> {
                        try {
                            existLote.setEstadoGarante(historial.getEstado());
                            existLote.setUsuario(historial.getUserName());
                            existLote.setFechaAceptacion(historial.getFechaAceptacion());
                            existLote.setFechaRechazo(historial.getFechaRechazo());
                            existLote.setFechaEnvio(historial.getFechaEnvio());
                            existLote.setFechaRegistroSolicitud(historial.getFechaRegistroSolicitud());
                            existLote.setRegistroSolicitud(historial.getRegistroSolicitud());
                            TableOperation replaceEntity = TableOperation.replace(existLote);
                            TableResult result = cloudTable.execute(replaceEntity);
                            if (result.getHttpStatusCode() == HttpStatus.NO_CONTENT.value())
                                log.debug("lote actualizado {}", existLote.getRowKey());
                            else
                                log.debug("lote no actualizado");
                        } catch (StorageException e) {
                            log.error(e);
                        }
                    });
        } catch (StorageException e) {
            e.printStackTrace();
            throw new ClinicalRecordException(e.getLocalizedMessage());
        }
    }

    public void eliminarArchivoXloteBlobStorage(GenericRequest<EliminarRequest> request) {
        Integer nroLote = Integer.valueOf(request.getRequest().getNroLote());

        final String URL_ELIMINAR_ARCHIVO = "/eliminarArchivoXNroLote";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GenericRequest<EliminarRequest>> entity = new HttpEntity<>(request, headers);

        log.info("Ingresando eliminar en expediente-service :: nroLote={}", nroLote);
        ResponseEntity<String> response = restTemplateExpediente.exchange(URL_ELIMINAR_ARCHIVO, HttpMethod.POST, entity, String.class);
        if (response.getStatusCodeValue() != HttpStatus.OK.value())
            throw new ClinicalRecordException("Error en la eliminación de archivos del lote.");
        log.info("Terminó eliminar en expediente-service :: nroLote={}", nroLote);
    }

    public void actualizarListdoFacturaGenerada(String nroLote) {

        List<ClinicalRecord> listUpd = new ArrayList<>();
        try {
            clinicalRecordRepository.findAll(new PartitionKey(Long.parseLong(nroLote))).forEach(c -> {
                c.setEstado(Constants.ESTADO.EXPEDIENTE_PENDIENTE.name());
                listUpd.add(c);
            });

            if (!listUpd.isEmpty()) {
                clinicalRecordRepository.saveAll(listUpd);
            }
        } catch (Exception e) {
            throw new ClinicalRecordException(String.format("Error en la actualización de Facturas del lote: %s a PENDIENTE", nroLote));
        }
    }

    public Object eliminarExpGeneradoXlote(GenericRequest<EliminarRequest> eliminarRequest) {
        EliminarRequest request = eliminarRequest.getRequest();

        //Eliminar los expedientes asociados al lote del Table Storage
        this.eliminarExpedienteDigitalOrDocumentos(restTemplateExpediente, ELIMINAR_EXPEDIENTE_XNRO_LOTE, eliminarRequest);

        //Actualizar Storage
        this.reprocesoActualizarLote(request.getGaranteId(), request.getNroLote());

        //eliminar los archivos asociados al lote del blob storage
        this.eliminarArchivoXloteBlobStorage(eliminarRequest);

        //Actualizar Cosmos
        this.actualizarListdoFacturaGenerada(request.getNroLote());

        return getResult(eliminarRequest.getHeader(), "Se esta eliminando los expedientes y archivos asociados al lote");
    }
}
