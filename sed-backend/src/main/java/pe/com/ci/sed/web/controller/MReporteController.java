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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/v1/reporte")
public class MReporteController extends BaseController {

    private final RestTemplate restTemplateReporte;

    public MReporteController(ApplicationContext context, RestTemplate restTemplateReporte) {
        super(context);
        this.restTemplateReporte = restTemplateReporte;
    }

    @PostMapping(value = "/listar", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> generarReporte(@RequestBody Map<String, Object> request, Principal principal) {
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        return ResponseEntity.ok(restTemplateReporte.postForEntity("/listar", request, Object.class).getBody());
    }

    @PostMapping("/enviar")
    public ResponseEntity<Object> encolarReporte(@RequestBody Map<String, Object> request, Principal principal) {
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        return ResponseEntity.ok(restTemplateReporte.postForEntity("/enviar", request, Object.class).getBody());
    }

    @PostMapping("/enviarTP")
    public ResponseEntity<Object> encolarReporteTP(@RequestBody Map<String, Object> request, Principal principal) {
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        return ResponseEntity.ok(restTemplateReporte.postForEntity("/enviarTP", request, Object.class).getBody());
    }
}
