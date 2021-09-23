package pe.com.ci.sed.reporte.service.impl;

import static pe.com.ci.sed.reporte.utils.Constants.*;
import static pe.com.ci.sed.reporte.utils.Constants.ESTADO.GENERADO;
import static pe.com.ci.sed.reporte.utils.Constants.ESTADO.SINREGISTRO;
import static pe.com.ci.sed.reporte.utils.TipoReporte.ENCUENTROSINLOTE;
import static pe.com.ci.sed.reporte.utils.TipoReporte.ENVIADOGARANTE;
import static pe.com.ci.sed.reporte.utils.TipoReporte.EXPEDIENTECONERROR;
import static pe.com.ci.sed.reporte.utils.TipoReporte.EXPEDIENTEMECANISMO;
import static pe.com.ci.sed.reporte.utils.TipoReporte.PARCIAL;
import static pe.com.ci.sed.reporte.utils.TipoReporte.TOTAL;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.azure.core.util.serializer.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableQuery;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import pe.com.ci.sed.reporte.model.generic.GenericRequest;
import pe.com.ci.sed.reporte.model.request.BusquedaLotes;
import pe.com.ci.sed.reporte.model.request.GestionLote;
import pe.com.ci.sed.reporte.model.request.RequestReporte;
import pe.com.ci.sed.reporte.model.response.ResponseExpError;
import pe.com.ci.sed.reporte.model.response.ResponseMecYMod;
import pe.com.ci.sed.reporte.model.response.ResponseSinLote;
import pe.com.ci.sed.reporte.model.response.ResponseTP;
import pe.com.ci.sed.reporte.persistence.entity.Garantes;
import pe.com.ci.sed.reporte.persistence.entity.Historial;
import pe.com.ci.sed.reporte.property.GaranteTotalProperty;
import pe.com.ci.sed.reporte.service.HistorialService;
import pe.com.ci.sed.reporte.service.ReporteService;
import pe.com.ci.sed.reporte.service.StorageService;
import pe.com.ci.sed.reporte.utils.Constants;
import pe.com.ci.sed.reporte.utils.GenericUtil;
import pe.com.ci.sed.reporte.utils.ReporteUtil;
import pe.com.ci.sed.reporte.utils.TipoReporte;

@Log4j2
@Service
@AllArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final StorageService storageService;
    private final HistorialService historialService;
    private final GaranteTotalProperty garanteTotalProperty;
    private final CloudTableClient cloudTableClient;

    private final RestTemplate restTemplateExpediente;
    private final RestTemplate restTemplateDocument;
    private final RestTemplate restTemplateClinical;

    @Override
    public void generarReporteMecYMod(GenericRequest<RequestReporte> genericRequest) {
        List<ResponseMecYMod> lista = GenericUtil.obtenerPorObjetoPOST(restTemplateExpediente, "/reporte/mecanismoFacturacion", genericRequest, ResponseMecYMod.class);
        this.genericReport(genericRequest.getHistorial(), lista, EXPEDIENTEMECANISMO);
    }

    @Override
    public void generarReporteExpError(GenericRequest<RequestReporte> genericRequest) {
        List<ResponseExpError> lista = GenericUtil.obtenerPorObjetoPOST(restTemplateExpediente, "/reporte/expedienteError", genericRequest, ResponseExpError.class);
        this.genericReport(genericRequest.getHistorial(), lista, EXPEDIENTECONERROR);
    }

    @Override
    public void generarReporteSinLote(GenericRequest<RequestReporte> genericRequest) {
        List<ResponseSinLote> lista = GenericUtil.obtenerPorObjetoPOST(restTemplateDocument, "/reporte/sinLote", genericRequest, ResponseSinLote.class);
        this.genericReport(genericRequest.getHistorial(), lista, ENCUENTROSINLOTE);
    }

    @Override
    public void generarReporteParcial(GenericRequest<RequestReporte> genericRequest) {
        List<ResponseTP> lista = GenericUtil.obtenerPorObjetoPOST(restTemplateExpediente, "/reporte/Totalparcial", genericRequest, ResponseTP.class);
        this.genericReport(genericRequest.getHistorial(), lista, PARCIAL);
    }

    @Override
    public void generarReporteEnviadoGarante(GenericRequest<RequestReporte> genericRequest) {
        GenericRequest<BusquedaLotes> busquedaRequest = GenericRequest.<BusquedaLotes>builder()
                .request(BusquedaLotes.builder()
                        .estado(genericRequest.getRequest().getEstado())
                        .garanteId(genericRequest.getRequest().getGaranteId())
                        .fechaLoteDesde(genericRequest.getRequest().getFechaDesde())
                        .fechaLoteHasta(genericRequest.getRequest().getFechaHasta())
                        .build())
                .header(genericRequest.getHeader())
                .build();

        List<GestionLote> lista = GenericUtil.obtenerPorObjetoPOST2(restTemplateClinical, "/gestionlotes/EnviadoGaranteReporte", busquedaRequest, GestionLote.class);

        this.genericReport(genericRequest.getHistorial(), lista, ENVIADOGARANTE);
    }

    @Override
    public void generarReporteTotal() {
        garanteTotalProperty.getGarantes().forEach(garanteId -> {
            List<Garantes> garantes = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            String fecha = cal.get(Calendar.YEAR) + "-" + String.format("%02d", (cal.get(Calendar.MONTH) + 1)) + "-" + (cal.get(Calendar.DATE) - 1);
            TableQuery<Garantes> rangeQuery = TableQuery.from(Garantes.class);
            StringBuilder sb = new StringBuilder();

            log.debug("garante agrupacion", garanteId.get("garanteDesc"));
            log.info("Ingresa al garante padre: " + garanteId.get("garanteId"));

            sb.append("PartitionKey eq 'GARANTE'");
            sb.append("and Codigopadre eq '").append(garanteId.get("garanteId")).append("'");
            log.debug(" where {}", sb.toString());
            rangeQuery.where(sb.toString());
            List<ResponseTP> lista = new ArrayList<>();
            try {
                CloudTable table = cloudTableClient.getTableReference(Constants.STORAGETABLECATALOGO);
                if (table.exists()) table.execute(rangeQuery).forEach(garantes::add);

                log.debug("Garantes Obtenidos: " + garantes);
                /*Luego obtenemos de la tabla storagelote*/
                garantes.forEach(garante -> {
                    try {
                        CloudTable table2 = cloudTableClient.getTableReference(STORAGETABLEEXPEDIENTE + garante.getCodigo());
                        if (table2.exists()) {
                            GenericRequest<RequestReporte> request = GenericRequest.<RequestReporte>builder()
                                    .request(new RequestReporte()).build();
                            request.getRequest().setGaranteId(garante.getCodigo());
                            request.getRequest().setTipoReporte("TOTAL");
                            request.getRequest().setFechaDesde(fecha);
                            request.getRequest().setFechaHasta(fecha);

                            lista.addAll(GenericUtil.obtenerPorObjetoPOST(restTemplateExpediente, "/reporte/Totalparcial", request, ResponseTP.class));
                            log.info("reporte-total-item-size: " + lista.size());
                        }
                    } catch (URISyntaxException | StorageException e) {
                        log.error(e);
                    }
                });
            } catch (URISyntaxException | StorageException e) {
                log.error(e);
            }


            if (!lista.isEmpty()) {
                byte[] archivo = ReporteUtil.genericCrearReporte(TOTAL, lista);
                this.subirArhivoRegistrarHistorial(archivo, garanteId.get("garanteDesc"));
            } else {
                log.info(EXCEL_NO_GENERADO, TOTAL.name());
                String listgarantes = garantes.stream().map(Garantes::getDescripcion).collect(Collectors.joining(", "));
                historialService.registrarHistorial(Historial.builder()
                        .estado(SINREGISTRO.name())
                        .fecha(new Date())
                        .garante(garanteId.get("garanteDesc"))
                        .tipo(TOTAL.getNombre())
                        .usuario("ADMINISTRADOR")
                        .filtros("Garantes: " + listgarantes)
                        .url(Strings.EMPTY)
                        .mensajeError("No se encontraron registros en expediente-service para los garantes: " + listgarantes)
                        .build());
            }
            log.info("generando-reporte: {}", "TOTAL");
        });
    }

    private void subirArhivoRegistrarHistorial(byte[] archivo, String garanteDes) {
        Map<String, String> response = storageService.uploadExcel(archivo, TOTAL.getNombre(), garanteDes);
        log.debug("ruta-excel-generado: {}" + response.toString());

        historialService.registrarHistorial(Historial.builder()
                .estado(GENERADO.name())
                .garante(garanteDes)
                .fecha(new Date())
                .tipo(TOTAL.getNombre())
                .usuario("ADMINISTRADOR")
                .filtros("Garante: " + garanteDes)
                .url(storageService.getUrlWithoutSas(response.get(PARAM_URL)))
                .urlSas(response.get(PARAM_URL))
                .build());
    }

    private <T> void genericReport(Historial historial, List<T> lista, TipoReporte tipoReporte) {

        log.debug(REPORTES_ITEM_SIZE + lista.size());

        if (!lista.isEmpty()) {
            byte[] archivo = ReporteUtil.genericCrearReporte(tipoReporte, lista);
            Map<String, String> response = storageService.uploadExcel(archivo, tipoReporte.getNombre());
            historial.setEstado(Constants.ESTADO.GENERADO.name());
            historial.setUrl(storageService.getUrlWithoutSas(response.get(PARAM_URL)));
            historial.setUrlSas(response.get(PARAM_URL));
        } else {
            log.debug(EXCEL_NO_GENERADO, tipoReporte.name());
            historial.setEstado(Constants.ESTADO.SINREGISTRO.name());
            historial.setMensajeError("No se encontraron registros en expediente-service: " + tipoReporte.name());
        }

        historial.setFecha(new Date());
        historialService.actualizarHistorial(historial);
    }
}
