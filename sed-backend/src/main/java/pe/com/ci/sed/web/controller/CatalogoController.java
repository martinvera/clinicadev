package pe.com.ci.sed.web.controller;

import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.com.ci.sed.web.model.request.ValidarRequest;
import pe.com.ci.sed.web.service.CatalogoService;

import java.security.Principal;

@RestController
@RequestMapping("/v1/catalogo")
public class CatalogoController extends BaseController {

    private final CatalogoService catalogoService;

    public CatalogoController(ApplicationContext context, CatalogoService catalogoService) {
        super(context);
        this.catalogoService = catalogoService;
    }

    @GetMapping("/{catalogo}")
    public ResponseEntity<Object> listarCatalogo(@PathVariable String catalogo, Principal principal) {
        return ResponseEntity.ok(catalogoService.listarCatalogo(catalogo, getHeader(principal)));
    }
    @PostMapping(value = "/validar", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> validarDocumentosRequeridos(@RequestBody ValidarRequest request){
        return ResponseEntity.ok(catalogoService.validarDocumentosRequeridos(request));
    }
}
