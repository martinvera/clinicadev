package pe.com.ci.sed.web.controller;

import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.com.ci.sed.web.model.request.Busqueda;
import pe.com.ci.sed.web.model.request.RequestContrasenia;
import pe.com.ci.sed.web.persistence.entity.Usuario;
import pe.com.ci.sed.web.service.UsuarioService;

import javax.validation.Valid;

import java.security.Principal;

import static pe.com.ci.sed.web.util.Constants.DEFAULT_TAMANIO;

@RestController
@RequestMapping("/v1/usuario")
public class UsuarioController extends BaseController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(ApplicationContext context, UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        super(context);
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/registro")
    public ResponseEntity<Object> regstrarUsuario(@RequestBody Usuario request, Principal principal) {
        request.setPasswordencrip(passwordEncoder.encode(request.getPassword()));
        Usuario usuario = new Usuario(request, 1);
        return ResponseEntity.ok(usuarioService.registrarUsuario(usuario, getHeader(principal)));
    }

    @PutMapping("/actualizar")
    public ResponseEntity<Object> modificarUsuario(@Valid @RequestBody Usuario request, Principal principal) {
        request.setPasswordencrip(passwordEncoder.encode(request.getPassword()));
        return ResponseEntity.ok(usuarioService.modificarUsuario(request, getHeader(principal)));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Object> eliminarUsuario(@PathVariable String username, Principal principal) {
        return ResponseEntity.ok(usuarioService.eliminarUsuario(username, getHeader(principal)));
    }

    @GetMapping("/lista")
    public ResponseEntity<Object> listarUsuarios(
            @RequestParam(required = false, defaultValue = DEFAULT_TAMANIO) Integer size,
            @RequestParam(required = false) String partition,
            @RequestParam(required = false) String row, Principal principal
    ) {
        return ResponseEntity.ok(usuarioService.listarUsuarios(Busqueda.builder()
                .size(size)
                .siguiente(partition)
                .atras(row)
                .header(getHeader(principal))
                .build()));
    }

    @GetMapping("/{username}")
    public ResponseEntity<Object> obtenerUsuario(@PathVariable String username, Principal principal) {
        return ResponseEntity.ok(usuarioService.obtenerUsuario(username, getHeader(principal)));
    }

    @PostMapping("/cambiarContrasenia")
    public ResponseEntity<Object> obtenerNuevaContrasenia(@Valid @RequestBody RequestContrasenia request, Principal principal){
        request.setPasswordencrip(passwordEncoder.encode(request.getNuevacontrasenia()));
        return ResponseEntity.ok(usuarioService.obtenerNuevaContrasenia(request, principal));
    }
}
