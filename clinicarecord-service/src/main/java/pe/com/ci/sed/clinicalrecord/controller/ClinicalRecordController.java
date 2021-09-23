package pe.com.ci.sed.clinicalrecord.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

@RestController
@AllArgsConstructor
@RequestMapping("/v1/clinicalrecord")
public class ClinicalRecordController {

    private final ClinicalRecordService clinicalRecordService;

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

    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> health() {
        Map<String,String> result = new HashMap<>();
        result.put("status", "UP");
        return ResponseEntity.ok(result);
    }
}

