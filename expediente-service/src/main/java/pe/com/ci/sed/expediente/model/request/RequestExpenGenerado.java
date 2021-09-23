package pe.com.ci.sed.expediente.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestExpenGenerado {
    private RequestHeader header = new RequestHeader();
    private Request request = new Request();

    @Data
    public static class Request {
        @NotBlank
        private String fechaDesde;
        @NotBlank
        private String fechaHasta;
        @NotBlank
        private String nroLote;
        @NotBlank
        private String garanteId;
        @NotBlank
        private String garanteDescripcion;
    }
}
