package pe.com.ci.sed.web.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.com.ci.sed.web.model.generic.HeaderRequest;
import pe.com.ci.sed.web.persistence.entity.Rol;
import pe.com.ci.sed.web.persistence.entity.Usuario;
import pe.com.ci.sed.web.service.RolService;
import pe.com.ci.sed.web.service.UsuarioService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2
@Configuration
@Order(3)
public class UsuarioConfiguration {
    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final String COMMA_DELIMITER = ",";

    public UsuarioConfiguration(UsuarioService usuarioService,
                                RolService rolService,
                                PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
        this.passwordEncoder = passwordEncoder;
    }

    @Value("classpath:usuario/roles.json")
    Resource roles;

    @Value("classpath:usuario/USUARIOSCARGA.csv")
    Resource usuarios;

    @Bean
    CommandLineRunner creacionRoles() {
        return args -> {
            List<Rol> listaRoles = mapper.readValue(roles.getInputStream(), new TypeReference<>() {
            });
            log.info("Inicio creación de roles");
            rolService.crearRoles(listaRoles);
            log.info("Fin creación de roles");

            log.info("Inicio creación de usuario administrador");
            this.usuarioService.registrarUsuarioAdmin(
                    new Usuario(
                            Usuario.builder()
                                    .apellidoMaterno("CLINICA")
                                    .apellidoPaterno("INTERNACIONAL")
                                    .nombres("ADMINISTRADOR")
                                    .cargo("ADMINISTRADOR")
                                    .tipoDocumento("RUC")
                                    .numeroDocumento("20100054184")
                                    .username("administrador")
                                    .password("clinic$admin")
                                    .passwordencrip(passwordEncoder.encode("clinic$admin"))
                                    .role("1")
                                    .telefono("111111111")
                                    .estado(1)
                                    .nombreRol("ADMINISTRADOR")
                                    .userEmail("")
                                    .proveedor("CLINICA INTERNACIONAL")
                                    .build(), 1));
            log.info("Fin creación de usuario administrador");
            Pattern pattern = Pattern.compile(COMMA_DELIMITER);

            List<Usuario> creacion;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(usuarios.getInputStream()))) {
                creacion = in.lines().skip(1).map(pattern::split).map(x -> new Usuario(x, passwordEncoder)).collect(Collectors.toList());
            }
            log.info("Inicio creación de usuarios clinica");
            this.usuarioService.registrarUsuarios(creacion);
            log.info("Fin creación de usuarios clinica");
        };
    }
}


