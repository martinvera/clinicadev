package pe.com.ci.sed.web.controller;

import java.security.Principal;

import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.com.ci.sed.web.model.request.Busqueda;
import pe.com.ci.sed.web.service.PrivilegioService;

@RestController
@RequestMapping("/v1/privilegio")
public class PrivilegioController extends BaseController {

    private final PrivilegioService privilegioService;

    public PrivilegioController(ApplicationContext context, PrivilegioService privilegioService) {
        super(context);
        this.privilegioService = privilegioService;
    }

    @GetMapping("/lista")
    public ResponseEntity<Object> listarPrivilegios(
            @RequestParam(required = false) int size,
            @RequestParam(required = false) String partition,
            @RequestParam(required = false) String row, Principal principal
    ) {
        return ResponseEntity.ok(this.privilegioService.listarPrivilegio(Busqueda.builder()
                .size(size)
                .siguiente(partition)
                .atras(row)
                .header(getHeader(principal))
                .build()));
    }

    //Visualizar privilegio
    @GetMapping("/{codigo}")
    public ResponseEntity<Object> visualizarPrivilegio(@PathVariable String codigo, Principal principal) {
        return ResponseEntity.ok(this.privilegioService.obtenerPrivilegio(codigo, getHeader(principal)));
    }

}
