package pe.com.ci.sed.clinicalrecord.service;

import static pe.com.ci.sed.clinicalrecord.utils.GenericUtil.getResult;
import static pe.com.ci.sed.clinicalrecord.utils.GenericUtil.getResultPaginacion;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.FeedResponse;
import com.azure.cosmos.models.PartitionKey;
import com.google.common.base.Strings;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import pe.com.ci.sed.clinicalrecord.model.generic.GenericRequest;
import pe.com.ci.sed.clinicalrecord.model.generic.Paginacion;
import pe.com.ci.sed.clinicalrecord.model.request.ActualizarFacturaCompleta;
import pe.com.ci.sed.clinicalrecord.model.request.ActualizarFacturaGeneradaRequest;
import pe.com.ci.sed.clinicalrecord.model.request.ListarRequest;
import pe.com.ci.sed.clinicalrecord.persistence.entity.ClinicalRecord;
import pe.com.ci.sed.clinicalrecord.persistence.repository.ClinicalRecordRepository;
import pe.com.ci.sed.clinicalrecord.utils.Constants;

@Log4j2
@Service
@AllArgsConstructor
public class ClinicalRecordService {

    private final CosmosContainer cosmosContainer;
    private ClinicalRecordRepository clinicalRecordRepository;
    private GestionLotesService gestionLotesService;
    private StorageService storageService;

    public Optional<ClinicalRecord> findById(String id, long partitionKey) {
        return clinicalRecordRepository.findById(id, new PartitionKey(partitionKey));
    }

    public List<ClinicalRecord> findByNroEncuentro(String[] nroEncuentro) {
        return clinicalRecordRepository.findByNroEncuentro(nroEncuentro);
    }

    public void delete(ClinicalRecord clinicalRecord) {
        clinicalRecordRepository.delete(clinicalRecord);
    }

    public Object listar(GenericRequest<ListarRequest> genericRequest) {
        genericRequest.getHeader().setStatus(String.valueOf(HttpStatus.OK.value()));
        ListarRequest request = genericRequest.getRequest();

        StringBuilder query = new StringBuilder("select c.facturaNro, c.facturaImporte, c.origen, " +
                "c.mecanismoFacturacionId, c.mecanismoFacturacionDesc, c.garanteDescripcion, c.modoFacturacionId, " +
                "c.modoFacturacion, c.estadoFactura, c.facturaArchivo,c.facturaArchivoSas, c.archivoAnexoDet,c.archivoAnexoDetSas, " +
                "c.garanteId, c.nroLote, c.nroEncuentro, c.pacienteNombre, c.pacienteApellidoPaterno," +
                "c.pacienteApellidoMaterno, c.origenServicioDesc, c.fechaAtencion, c.pacienteTipoDocIdentDesc, c.pacienteTipoDocIdentId, c.facturaFecha, " +
                "c.nroRemesa,c.facturaNroInafecta,c.facturaNroAfecta from c where c.estado='" + Constants.ESTADO.EXPEDIENTE_PENDIENTE.name() + "'");
        query.append(" and c.nroLote = ").append(request.getNroLote());

        if (!Strings.isNullOrEmpty(request.getGaranteId()))
            query.append(" and c.garanteId = '").append(request.getGaranteId()).append("'");
        if (!Strings.isNullOrEmpty(request.getFacturaNro()))
            query.append(" and c.facturaNro = '").append(request.getFacturaNro()).append("'");

        log.info("query = " + query);
        CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();
        options.setQueryMetricsEnabled(true);

        FeedResponse<ClinicalRecord> response;
        if (Strings.isNullOrEmpty(request.getSiguiente()))
            response = cosmosContainer.queryItems(query.toString(), options, ClinicalRecord.class)
                    .iterableByPage(request.getSize()).iterator().next();
        else
            response = cosmosContainer.queryItems(query.toString(), options, ClinicalRecord.class)
                    .iterableByPage(request.getSiguiente(), request.getSize()).iterator().next();

        response.getResults().forEach(x -> {
            x.setArchivoAnexoDet(storageService.getUrlWithSas(x.getArchivoAnexoDetSas()));
            x.setFacturaArchivo(storageService.getUrlWithSas(x.getFacturaArchivoSas()));
        });

        log.debug(response.getCosmosDiagnostics());

        Paginacion paginacion = Paginacion.builder()
                .siguiente(response.getContinuationToken())
                .totalitems(response.getResults().size())
                .build();

        return getResultPaginacion(genericRequest.getHeader(), response.getResults(), paginacion);
    }

    public Object obtenerFactura(GenericRequest<Map<String, String>> request) {
        request.getHeader().setStatus(String.valueOf(HttpStatus.OK.value()));

        ClinicalRecord clinicalRecord = clinicalRecordRepository.findByNroLoteAndFacturaNro(Long.parseLong(request.getRequest().get("nroLote")), request.getRequest().get("facturaNro"));
        clinicalRecord.setArchivoAnexoDet(storageService.getUrlWithSas(clinicalRecord.getArchivoAnexoDetSas()));
        clinicalRecord.setFacturaArchivo(storageService.getUrlWithSas(clinicalRecord.getFacturaArchivoSas()));

        return getResult(request.getHeader(), clinicalRecord);
    }

    public void registrarFactura(ClinicalRecord clinicalRecord) {
        clinicalRecord.setEstado(Constants.ESTADO.EXPEDIENTE_PENDIENTE.name());
        this.guardarFactura(clinicalRecord);
    }

    public void guardarFactura(ClinicalRecord clinicalRecord) {
        clinicalRecordRepository.save(clinicalRecord);
    }

    public Object actualizarFacturaGenerada(GenericRequest<List<ActualizarFacturaGeneradaRequest>> request) {
        List<ActualizarFacturaGeneradaRequest> actualizar = request.getRequest();

        List<ClinicalRecord> listUpd = new ArrayList<>();

        actualizar.forEach(a -> {
            gestionLotesService.actualizarFacturaGenerada(a.getGaranteId(), a.getNroLote(), a.getFacturaNro().length, a.getUrlArchivoZipLote());

            Arrays.stream(a.getFacturaNro()).forEach(facturaNro ->
                    clinicalRecordRepository.findById(facturaNro, new PartitionKey(Long.parseLong(a.getNroLote())))
                            .ifPresent(clinicalRecord -> {
                                clinicalRecord.setEstado(Constants.ESTADO.EXPEDIENTE_GENERADO.name());
                                clinicalRecordRepository.findById(clinicalRecord.getFacturaNroInafecta(), new PartitionKey(Long.parseLong(a.getNroLote())))
                                        .ifPresent(inafecta -> {
                                            inafecta.setEstado(Constants.ESTADO.EXPEDIENTE_GENERADO.name());
                                            listUpd.add(inafecta);
                                        });
                                listUpd.add(clinicalRecord);
                            }));
        });

        if (!listUpd.isEmpty()) {
            clinicalRecordRepository.saveAll(listUpd);
        }

        return getResult(request.getHeader(), "Lote y facturas actualizadas");
    }

    public Object actualizarFacturaEstado(GenericRequest<ActualizarFacturaCompleta> request) {
        clinicalRecordRepository.findById(request.getRequest().getFacturaNro(), new PartitionKey(Long.parseLong(request.getRequest().getNroLote())))
                .ifPresent(afecta -> {
                    afecta.setEstadoFactura(request.getRequest().getEstado());
                    if (Objects.nonNull(afecta.getFacturaNroInafecta()))
                        clinicalRecordRepository.findById(afecta.getFacturaNroInafecta(), new PartitionKey(Long.parseLong(request.getRequest().getNroLote())))
                                .ifPresent(inafecta -> {
                                    inafecta.setEstadoFactura(request.getRequest().getEstado());
                                    clinicalRecordRepository.save(inafecta);
                                });
                    clinicalRecordRepository.save(afecta);
                });
        return getResult(request.getHeader(), "Factura actualizada");
    }

}
