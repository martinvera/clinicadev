package pe.com.ci.sed.expediente.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.com.ci.sed.expediente.model.request.GenericRequest;
import pe.com.ci.sed.expediente.model.request.ReporteTotalParcialRequest;
import pe.com.ci.sed.expediente.model.request.RequestExpenGenerado;
import pe.com.ci.sed.expediente.model.request.RequestReporte;
import pe.com.ci.sed.expediente.model.response.ResponseExpError;
import pe.com.ci.sed.expediente.persistence.entity.ReportExcelTotalParcial;
import pe.com.ci.sed.expediente.persistence.entity.ReporteTotalParcial;
import pe.com.ci.sed.expediente.service.GenerarReporteService;
import pe.com.ci.sed.expediente.service.ReporteService;

import javax.validation.Valid;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/expediente/reporte")
public class GenerarReporteController {
    private final GenerarReporteService generarReporteService;

    private final ReporteService reporteService;

    @PostMapping("/expedienteError")
    public ResponseEntity<List<ResponseExpError>> reporteExpError(@RequestBody @Valid GenericRequest<RequestReporte> request) {
        return ResponseEntity.ok(generarReporteService.reporteExpError(request));
    }

    @PostMapping(value = "/mecanismoFacturacion", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> reporteMecYMod(@Valid @RequestBody GenericRequest<RequestReporte> request) {
        return ResponseEntity.ok(generarReporteService.reporteMecYMod(request));
    }

    @PostMapping(value = "/expedienteEstadoOrigen", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> reporteExpedientesGenerados(@RequestBody RequestExpenGenerado request) {
        return ResponseEntity.ok(reporteService.reporteExpedientesGenerados(request));
    }

    @PostMapping(value = "/listaReporteTotalParcial", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> listarReporteTotalParcial(@RequestBody GenericRequest<ReporteTotalParcialRequest> request) {
        return ResponseEntity.ok(reporteService.listarReporteTotalParcial(request));
    }

    @PostMapping(value = "/registrarReporteTotalParcial", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> registrarReporteTotalParcial(@RequestBody GenericRequest<ReporteTotalParcial> request) {
        return ResponseEntity.ok(reporteService.registrarReporteTotalParcial(request));
    }

    @PostMapping(value = "/Totalparcial", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ReportExcelTotalParcial>> reportExcelTotalParcials(@RequestBody GenericRequest<ReporteTotalParcialRequest> request) {
        return ResponseEntity.ok(generarReporteService.reportExcelTotalParcials(request));
    }

    @PostMapping(value = "/validarNroLote", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> validarNroLote(@RequestBody GenericRequest<ReporteTotalParcialRequest> request) {
        return ResponseEntity.ok(generarReporteService.validarNumeroLote(request));
    }
}
