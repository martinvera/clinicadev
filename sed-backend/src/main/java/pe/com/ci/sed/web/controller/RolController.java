package pe.com.ci.sed.web.controller;

import static pe.com.ci.sed.web.util.Constants.DEFAULT_TAMANIO;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.com.ci.sed.web.model.request.Busqueda;
import pe.com.ci.sed.web.persistence.entity.Rol;
import pe.com.ci.sed.web.service.RolService;

@RestController
@RequestMapping("/v1/rol")
public class RolController extends BaseController {

    private final RolService rolService;

    public RolController(RolService rolService, ApplicationContext context) {
        super(context);
        this.rolService = rolService;
    }

    @PostMapping("/crear")
    public ResponseEntity<Object> crearRol(@RequestBody Rol request, Principal principal) {
        return ResponseEntity.ok(rolService.crearRol(request, getHeader(principal)));
    }

    @GetMapping("/lista")
    public ResponseEntity<Object> listarRol(@RequestParam(required = false, defaultValue = DEFAULT_TAMANIO) Integer size,
                                            @RequestParam(required = false) String partition,
                                            @RequestParam(required = false) String row, Principal principal) {
        return ResponseEntity.ok(this.rolService.listarRol(Busqueda.builder().size(size)
                .siguiente(partition).atras(row).header(getHeader(principal))
                .build()));
    }

    //Visualizar rol
    @GetMapping("/{codigo}")
    public ResponseEntity<Object> visualizarRol(@PathVariable String codigo, Principal principal) {
        return ResponseEntity.ok(this.rolService.obtenerRol(codigo, getHeader(principal)));
    }

    @PutMapping("/modificar")
    public ResponseEntity<Object> modificarRol(@Valid @RequestBody Rol request, Principal principal) {
        return ResponseEntity.ok(rolService.modificarRol(request, getHeader(principal)));
    }

}
