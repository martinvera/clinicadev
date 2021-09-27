package pe.com.ci.sed.web.controller;

import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import pe.com.ci.sed.web.util.Constants;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/v1")
public class MClinicaRecordController extends BaseController {
    private final RestTemplate restTemplateClinicalRecord;

    public MClinicaRecordController(ApplicationContext context, RestTemplate restTemplateClinicalRecord) {
        super(context);
        this.restTemplateClinicalRecord = restTemplateClinicalRecord;
    }

    @PostMapping("/clinicalrecord/lista")
    public ResponseEntity<Object> listarDocumentos(@RequestBody Map<String, Object> request, Principal principal) {
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        return ResponseEntity.ok(restTemplateClinicalRecord.postForEntity("/clinicalrecord/lista", request, Object.class).getBody());
    }

    @PostMapping("/reportes/lista")
    public ResponseEntity<Object> listarReportes(@RequestBody Map<String, Object> request, Principal principal) {
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        return ResponseEntity.ok(restTemplateClinicalRecord.postForEntity("/reportes/lista", request, Object.class).getBody());
    }

    @PostMapping("/clinicalrecord/registrarSiniestro")
    public ResponseEntity<Object> registrarSiniestro(@RequestBody Map<String, Object> request, Principal principal) {
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        return ResponseEntity.ok(restTemplateClinicalRecord.postForEntity("/clinicalrecord/registrarSiniestro", request, Object.class).getBody());
    }

    @PostMapping("/clinicalrecord/procesarFacturasError")
    public ResponseEntity<Object> procesarFacturasError(@RequestBody Map<String, Object> request, Principal principal) {
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        return ResponseEntity.ok(restTemplateClinicalRecord.postForEntity("/clinicalrecord/procesarFacturasError", request, Object.class).getBody());
    }
}
