package pe.com.ci.sed.integrator.controller;

import java.util.List;

import javax.validation.Valid;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import pe.com.ci.sed.integrator.model.request.RegistrarDocFromUnilabRequest;
import pe.com.ci.sed.integrator.model.request.RegistrarFacturaFromCdRequest;
import pe.com.ci.sed.integrator.model.request.RegistrarFacturaFromIafaRequest;
import pe.com.ci.sed.integrator.service.QueueServiceImpl;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/integrator")
public class IntegratorController {

    private final QueueServiceImpl queueService;


    @PostMapping(value = "/unilab", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> crearUnilab(@RequestBody RegistrarDocFromUnilabRequest request) {
        return ResponseEntity.ok(queueService.encolarDocumentoUnilab(request));
    }

    @PostMapping(value = "/iafas", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> registrarIafas(@Valid @RequestBody List<@Valid RegistrarFacturaFromIafaRequest> request) {
        return ResponseEntity.ok(queueService.encolarFacturaFromIafas(request));
    }

    @PostMapping(value = "/controldocumentario", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> registrarCd(@Valid @RequestBody List<@Valid RegistrarFacturaFromCdRequest> request) {
        return ResponseEntity.ok(queueService.encolarFacturaFromCd(request));
    }
 
    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> health() {
        Map<String,String> result = new HashMap<>();
        result.put("status", "UP");
        return ResponseEntity.ok(result);
    }

}
