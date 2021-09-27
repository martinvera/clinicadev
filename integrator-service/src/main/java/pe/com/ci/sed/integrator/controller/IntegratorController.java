package pe.com.ci.sed.integrator.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import pe.com.ci.sed.integrator.model.request.EnterpriseIRequest;
import pe.com.ci.sed.integrator.model.request.RegistrarDocFromUnilabRequest;
import pe.com.ci.sed.integrator.model.request.RegistrarFacturaFromCdRequest;
import pe.com.ci.sed.integrator.model.request.RegistrarFacturaFromIafaRequest;
import pe.com.ci.sed.integrator.model.response.EiResponse;
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
        Map<String, String> result = new HashMap<>();
        result.put("status", "UP");
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/enterpriseimaging", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EiResponse> registrarEi(@Valid @RequestBody List<@Valid EnterpriseIRequest> request) {
        return queueService.encolarFacturaFromEi(request);
    }

}
