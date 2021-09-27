package pe.com.ci.sed.clinicalrecord.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import pe.com.ci.sed.clinicalrecord.model.generic.GenericRequest;
import pe.com.ci.sed.clinicalrecord.model.request.ActualizarFacturaCompleta;
import pe.com.ci.sed.clinicalrecord.model.request.ActualizarFacturaGeneradaRequest;
import pe.com.ci.sed.clinicalrecord.model.request.ListarRequest;
import pe.com.ci.sed.clinicalrecord.service.ClinicalRecordService;
import pe.com.ci.sed.clinicalrecord.service.QueueService;

import static pe.com.ci.sed.clinicalrecord.utils.GenericUtil.getResult;

@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/v1/clinicalrecord")
public class ClinicalRecordController {

    private final ClinicalRecordService clinicalRecordService;
    private final QueueService queueService;

    @PostMapping(value = "/lista", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> listarDocumentos(@Valid @RequestBody GenericRequest<ListarRequest> genericRequest) {
        return ResponseEntity.ok(clinicalRecordService.listar(genericRequest));
    }

    @PostMapping(value = "/obtenerFactura", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> obtenerFactura(@Valid @RequestBody GenericRequest<Map<String, String>> request) {
        return ResponseEntity.ok(clinicalRecordService.obtenerFactura(request));
    }

    @PostMapping(value = "/actualizarFacturaGenerada", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> actualizarFacturaGenerada(@Valid @RequestBody GenericRequest<List<ActualizarFacturaGeneradaRequest>> request) {
        return ResponseEntity.ok(clinicalRecordService.actualizarFacturaGenerada(request));
    }

    @PostMapping(value = "/actualizarFacturaEstado", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> actualizarFacturaCompleta(@Valid @RequestBody GenericRequest<ActualizarFacturaCompleta> request) {
        return ResponseEntity.ok(clinicalRecordService.actualizarFacturaEstado(request));
    }

    @PostMapping(value = "/procesarFacturasError", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> reprocesarFacturaConError(@RequestBody GenericRequest<String> request) {
        queueService.procesarMensajeConError();
        return ResponseEntity.ok(getResult(request.getHeader(), "Facturas se mandaron a reprocesar correctamente"));
    }

}

