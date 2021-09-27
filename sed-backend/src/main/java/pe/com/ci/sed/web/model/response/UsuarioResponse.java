package pe.com.ci.sed.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.com.ci.sed.web.persistence.entity.Usuario;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    public UsuarioResponse(Usuario usuario, RolReponse rolResponse) {
        this.nombres = usuario.getNombres();
        this.apellidoPaterno = usuario.getApellidoPaterno();
        this.apellidoMaterno = usuario.getApellidoMaterno();
        this.tipoDocumento = usuario.getTipoDocumento();
        this.numeroDocumento = usuario.getNumeroDocumento();
        this.userEmail = usuario.getUserEmail();
        this.telefono = usuario.getTelefono();
        this.cargo = usuario.getCargo();
        this.proveedor = usuario.getProveedor();
        this.estado = usuario.getEstado();
        this.username = usuario.getUsername();
        this.password = usuario.getPassword();
        this.passwordencrip = usuario.getPasswordencrip();
        this.role = usuario.getRole();
        this.fullName = usuario.getFullName();
        this.rol = rolResponse;
    }

    private String username;
    private String password;
    private String passwordencrip;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String userEmail;
    private String tipoDocumento;
    private String numeroDocumento;
    private String telefono;
    private String cargo;
    private String proveedor;
    private Integer estado;
    private String role;
    private RolReponse rol;
    private String fullName;
}
