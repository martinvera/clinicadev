package pe.com.ci.sed.web.controller;

import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import pe.com.ci.sed.web.util.Constants;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;

@RestController
@RequestMapping("/v1/expediente")
public class MExpedienteController extends BaseController {

    private final RestTemplate restTemplateExpediente;

    public MExpedienteController(ApplicationContext context, RestTemplate restTemplateExpediente) {
        super(context);
        this.restTemplateExpediente = restTemplateExpediente;
    }

    @PostMapping(value = "/buscar")
    public ResponseEntity<Object> buscarExpediente(@RequestBody HashMap<String, Object> request, Principal principal) {
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        return ResponseEntity.ok(restTemplateExpediente.postForEntity("/buscar", request, Object.class).getBody());
    }

    @PostMapping(value = "/enviar")
    public ResponseEntity<Object> enviarExpediente(@RequestBody HashMap<String, Object> request, Principal principal) {
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        return ResponseEntity.ok(restTemplateExpediente.postForEntity("/enviar", request, Object.class).getBody());
    }

    @PostMapping("/reporte/mecanismoFacturacion")
    public ResponseEntity<Object> reporteMecYMod(@RequestBody @Valid HashMap<String, Object> request, Principal principal) {
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        return ResponseEntity.ok(restTemplateExpediente.postForEntity("/reporte/mecanismoFacturacion", request, Object.class).getBody());

    }

    @PostMapping("/reporte/expError")
    public ResponseEntity<Object> reporteExpError(@RequestBody @Valid HashMap<String, Object> request, Principal principal) {
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        return ResponseEntity.ok(restTemplateExpediente.postForEntity("/reporte/expError", request, Object.class).getBody());
    }

    @PostMapping("/reporte/expedienteEstadoOrigen")
    public ResponseEntity<Object> reporteExpedientesGenerados(@RequestBody @Valid HashMap<String, Object> request, Principal principal) {
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        return ResponseEntity.ok(restTemplateExpediente.postForEntity("/reporte/expedienteEstadoOrigen", request, Object.class).getBody());
    }

    @PostMapping("/reporte/listaReporteTotalParcial")
    public ResponseEntity<Object> listarReporteTotalParcial(@RequestBody @Valid HashMap<String, Object> request, Principal principal) {
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        return ResponseEntity.ok(restTemplateExpediente.postForEntity("/reporte/listaReporteTotalParcial", request, Object.class).getBody());
    }
}
