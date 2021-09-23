package pe.com.ci.sed.expediente.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.ci.sed.expediente.model.request.RequestHeader;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse<T> {
    private RequestHeader header;
    private T response;
}
