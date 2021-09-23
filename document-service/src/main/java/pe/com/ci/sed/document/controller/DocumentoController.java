package pe.com.ci.sed.document.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import pe.com.ci.sed.document.model.generic.GenericRequest;
import pe.com.ci.sed.document.model.request.BusquedaRequest;
import pe.com.ci.sed.document.model.request.DocRequest;
import pe.com.ci.sed.document.persistence.entity.Archivo;
import pe.com.ci.sed.document.persistence.entity.Documento;
import pe.com.ci.sed.document.service.DocumentoService;
import pe.com.ci.sed.document.service.impl.SalesForceServiceImpl;
import pe.com.ci.sed.document.service.impl.XhisServiceImpl;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/documento")
public class DocumentoController {

    private final DocumentoService documentoService;
    private final SalesForceServiceImpl salesForceService;
    private final XhisServiceImpl xhisService;

    @PostMapping(value = "/registro", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> registrarPublicDocumento(@Valid @RequestBody GenericRequest<Documento> request) {
        request.getRequest().setFacturaNro("0");
        return ResponseEntity.ok(documentoService.registrarDocumentoCargaManual(request));
    }

    @PutMapping(value = "/modificar", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> modificarDocumento(@Valid @RequestBody GenericRequest<Documento> request) {
        return ResponseEntity.ok(documentoService.modificarDocumentoCargaManual(request));
    }

    @PostMapping(value = "/lista", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> listarDocumentos(@RequestBody GenericRequest<BusquedaRequest> request) {
        return ResponseEntity.ok(documentoService.listarDocumentos(request));
    }

    @PostMapping(value = "/eliminarDocXlote", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> eliminarDocumentosXlote(@RequestBody GenericRequest<BusquedaRequest> request) {
        return ResponseEntity.ok(documentoService.eliminarDocumentos(request));
    }

    @PostMapping(value = "/detalle", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> obtenerDetalleDocumento(@RequestBody GenericRequest<BusquedaRequest> request) {
        return ResponseEntity.ok(documentoService.detalleDocumento(request));
    }

    @PostMapping(value = "/documentoXfactura", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> obtenerDocumento(@RequestBody DocRequest request) {
        return ResponseEntity.ok(documentoService.obtenerDocumento(request));
    }

    @PostMapping(value = "/integracionsalesforce", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Archivo>> integrarsalesforce(@RequestBody Documento documento) {
        return ResponseEntity.ok(salesForceService.generarDocumentoSalesforce(documento));
    }
    @PostMapping(value = "/integracionxhis", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Archivo>> integrarxhis(@RequestBody Documento documento) {
        return ResponseEntity.ok(xhisService.generarDocumentosXhis(documento));
    }

    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> health() {
        Map<String,String> result = new HashMap<>();
        result.put("status", "UP");
        return ResponseEntity.ok(result);
    }
}
