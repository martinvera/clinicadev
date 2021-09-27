package pe.com.ci.sed.web.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import pe.com.ci.sed.web.util.Constants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.regex.Pattern;

@Getter
@Setter
@Validated
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario extends Auditoria {

    public Usuario(String username) {
        this.partitionKey = username;
        this.rowKey = username;
    }

    public Usuario(String[] data, PasswordEncoder passwordEncoder) {
        this(data[0]);
        String user = data[0].replaceAll("[\\s]", "");
        this.username = user;
        this.password = "clinic$" + user.toLowerCase();
        this.estado = 1;
        this.tipoDocumento = data[2];
        this.numeroDocumento = data[3];
        this.apellidoPaterno = data[4];
        this.apellidoMaterno = data[5];
        this.nombres = data[6];
        this.area = data[7];
        this.cargo = data[8];
        this.role = data[9];
        this.nombreRol = data[11];
        this.proveedor = data[12];
        this.userEmail = data[13];
        this.telefono = Strings.EMPTY;
        this.passwordencrip = passwordEncoder.encode("clinic$" + user.toLowerCase());
        this.fullName = String.format(Constants.FORMAT_S_S_S, data[6], data[4], data[5]);
    }

    public Usuario(Usuario request, Integer estado) {
        this(request.username);
        this.nombres = request.nombres;
        this.apellidoPaterno = request.apellidoPaterno;
        this.apellidoMaterno = request.apellidoMaterno;
        this.tipoDocumento = request.tipoDocumento;
        this.numeroDocumento = request.numeroDocumento;
        this.userEmail = request.userEmail;
        this.telefono = request.telefono;
        this.cargo = request.cargo;
        this.proveedor = request.proveedor;
        this.estado = estado;
        this.username = request.username;
        this.password = request.password;
        this.passwordencrip = request.passwordencrip;
        this.etag = request.etag;
        this.role = request.role;
        this.nombreRol = request.nombreRol;
        this.fullName = String.format(Constants.FORMAT_S_S_S, request.nombres, request.apellidoPaterno, request.apellidoMaterno);
    }

    public void modificar(Usuario request) {
        this.nombres = request.nombres;
        this.apellidoPaterno = request.apellidoPaterno;
        this.apellidoMaterno = request.apellidoMaterno;
        this.userEmail = request.userEmail;
        this.tipoDocumento = request.tipoDocumento;
        this.numeroDocumento = request.numeroDocumento;
        this.telefono = request.telefono;
        this.cargo = request.cargo;
        this.proveedor = request.proveedor;
        this.estado = request.estado;
        this.password = request.password;
        this.passwordencrip = request.passwordencrip;
        this.role = request.role;
        this.nombreRol = request.nombreRol;
        this.fullName = String.format(Constants.FORMAT_S_S_S, request.nombres, request.apellidoPaterno, request.apellidoMaterno);
    }

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @JsonIgnore
    private String passwordencrip;

    @NotBlank
    private String nombres;

    @NotBlank
    private String apellidoPaterno;

    @NotBlank
    private String apellidoMaterno;

    @NotBlank
    private String userEmail;

    @NotBlank
    private String tipoDocumento;

    @NotBlank
    private String numeroDocumento;

    private String telefono;

    @NotBlank
    private String cargo;

    private String proveedor;

    @NotNull
    private Integer estado;

    @NotBlank
    private String role;

    private String nombreRol;

    private String fullName;

    private String area;
}
