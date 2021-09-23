package pe.com.ci.sed.document.model.request;

import javax.validation.constraints.NotBlank;

import lombok.*;
import pe.com.ci.sed.document.model.generic.RequestHeader;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocRequest {
    private RequestHeader header = new RequestHeader();
    private DocRequest.Request request = new DocRequest.Request();

    @Data
    public static class Request {
    	
    	@NotBlank
        private String nroLote;
    	
    	@NotBlank
        private String facturaNro;
    	
        private String garanteid;
        private String mecanismoid;
        private String modofacturacionid;
        private String tipodocumentoid;
        private String codigoServicioOrigen;
    }
}
