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
@RequestMapping("/v1/gestionlotes")
public class MGestionLotesController extends BaseController{
    private final RestTemplate restTemplateClinicalRecord;

    public MGestionLotesController(ApplicationContext context, RestTemplate restTemplateClinicalRecord) {
        super(context);
        this.restTemplateClinicalRecord = restTemplateClinicalRecord;
    }

    @PostMapping("/buscarLotes")
    public ResponseEntity<Object> buscarLotes(@RequestBody Map<String, Object> request, Principal principal) {
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        return ResponseEntity.ok(restTemplateClinicalRecord.postForEntity("/gestionlotes/buscarLotes", request, Object.class).getBody());
    }

    @PostMapping(value = "/eliminarLote")
    public ResponseEntity<Object> eliminarLote(@RequestBody Map<String, Object> request, Principal principal) {
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        return ResponseEntity.ok(restTemplateClinicalRecord.postForEntity("/gestionlotes/eliminarLote", request, Object.class).getBody());
    }

    @PostMapping(value = "/buscarHistorial")
    public ResponseEntity<Object> buscarHistorialGarante(@RequestBody Map<String, Object> request, Principal principal) {
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        return ResponseEntity.ok(restTemplateClinicalRecord.postForEntity("/gestionlotes/buscarHistorial", request, Object.class).getBody());
    }

    @PostMapping(value = "/registrarHistorial")
    public ResponseEntity<Object> registrarHistorial(@RequestBody Map<String, Object> request, Principal principal) {
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        return ResponseEntity.ok(restTemplateClinicalRecord.postForEntity("/gestionlotes/registrarHistorial", request, Object.class).getBody());
    }

    @PostMapping(value = "/listarEnviadoGarante")
    public ResponseEntity<Object> listarEnviadoGarante(@RequestBody Map<String, Object> request, Principal principal) {
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        return ResponseEntity.ok(restTemplateClinicalRecord.postForEntity("/gestionlotes/listarEnviadoGarante", request, Object.class).getBody());
    }

    @PostMapping(value = "/EnviadoGaranteReporte")
    public ResponseEntity<Object> EnviadoGaranteReporte(@RequestBody Map<String, Object> request, Principal principal) {
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        return ResponseEntity.ok(restTemplateClinicalRecord.postForEntity("/gestionlotes/EnviadoGaranteReporte", request, Object.class).getBody());
    }

    @PostMapping(value = "/eliminarExpXlote")
    public ResponseEntity<Object> eliminarExpXlote(@RequestBody Map<String, Object> request, Principal principal){
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        return ResponseEntity.ok(restTemplateClinicalRecord.postForEntity("/gestionlotes/eliminarExpXlote", request, Object.class).getBody());
    }

}
