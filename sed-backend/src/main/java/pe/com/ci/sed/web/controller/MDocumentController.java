package pe.com.ci.sed.web.controller;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import pe.com.ci.sed.web.util.Constants;

import java.security.Principal;
import java.util.Map;


@RestController
@RequestMapping("/v1/documento")
public class MDocumentController extends BaseController {
    private final RestTemplate restTemplateDocument;

    public MDocumentController(ApplicationContext context, RestTemplate restTemplateDocument) {
        super(context);
        this.restTemplateDocument = restTemplateDocument;
    }

    @PostMapping("/registro")
    public ResponseEntity<Object> registrarPublicDocumento(@RequestBody Map<String, Object> request, Principal principal) {
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        return ResponseEntity.ok(restTemplateDocument.postForEntity("/registro", request, Object.class).getBody());
    }

    @PostMapping("/lista")
    public ResponseEntity<Object> listarDocumentos(@RequestBody Map<String, Object> request, Principal principal) {
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        return ResponseEntity.ok(restTemplateDocument.postForEntity("/lista", request, Object.class).getBody());
    }

    @PostMapping("/documentoXfactura")
    public ResponseEntity<Object> obtenerDocumento(@RequestBody Map<String,Object> request, Principal principal) {
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        return ResponseEntity.ok(restTemplateDocument.postForEntity("/documentoXfactura", request, Object.class).getBody());
    }

    @PostMapping("/eliminarDocXlote")
    public ResponseEntity<Object> eliminarDocumentosXlote(@RequestBody Map<String,Object> request, Principal principal) {
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        return ResponseEntity.ok(restTemplateDocument.postForEntity("/eliminarDocXlote", request, Object.class).getBody());
    }

    @GetMapping("/{nrLote}/{facturaNro}")
    public ResponseEntity<Object> obtenerDocumento(@PathVariable String facturaNro, Principal principal) {
        return ResponseEntity.ok(restTemplateDocument.postForEntity( "/" + facturaNro, getHeader(principal), Object.class).getBody());
    }

    @PutMapping("/modificar")
    public ResponseEntity<Object> modificarDocumento(@RequestBody Map<String, Object> request, Principal principal) {
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        HttpEntity<Object> http = new HttpEntity<>(request);
        return ResponseEntity.ok(restTemplateDocument.exchange("/modificar", HttpMethod.PUT, http, Object.class).getBody());
    }
    @PostMapping("/detalle")
    public ResponseEntity<Object> detalle(@RequestBody Map<String, Object> request, Principal principal) {
        request.put(Constants.PARAM_HEADER, getHeader(principal));
        return ResponseEntity.ok(restTemplateDocument.postForEntity("/detalle", request, Object.class).getBody());
    }

}
