package pe.com.ci.sed.expediente.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import lombok.AllArgsConstructor;
import pe.com.ci.sed.expediente.model.request.BusquedaExpediente;
import pe.com.ci.sed.expediente.model.request.EliminarRequest;
import pe.com.ci.sed.expediente.model.request.GenericRequest;
import pe.com.ci.sed.expediente.model.request.ValidarRequest;
import pe.com.ci.sed.expediente.service.ExpedienteService;
import pe.com.ci.sed.expediente.service.ExpedienteStorageService;
import pe.com.ci.sed.expediente.service.ValidarService;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/expediente")
public class ExpedienteController {

    private final ExpedienteService expedienteService;
    private final ValidarService validarService;
    private final ExpedienteStorageService expedienteStorageService;

    @PostMapping(value = "/buscar", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buscarExpediente(@RequestBody GenericRequest<BusquedaExpediente> request) {
        return ResponseEntity.ok(expedienteService.buscarExpediente(request));
    }

    @PostMapping(value = "/eliminarExpdienteXNroLote", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> eliminarLote(@Valid @RequestBody GenericRequest<EliminarRequest> request) {
        return ResponseEntity.ok(expedienteService.eliminarExpedienteXLote(request));
    }
    
    @PostMapping(value = "/validar", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> validarDocumentosRequeridos(@RequestBody ValidarRequest request){
        return ResponseEntity.ok(validarService.ValidarDocumentosRequeridos(request));
    }

    @PostMapping(value = "/eliminarArchivoXNroLote", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> eliminarArchivo(@RequestBody GenericRequest<EliminarRequest> request) {
        Integer nroLote = Integer.parseInt(request.getRequest().getNroLote());
        expedienteStorageService.eliminarArchivosXLote(nroLote);
        return ResponseEntity.ok("Los archivos del lote "+nroLote+" se eliminaron correctamente");
    }

    @GetMapping(value = "/health", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> health() {
        Map<String,String> result = new HashMap<>();
        result.put("status", "UP");
        return ResponseEntity.ok(result);
    }
}
