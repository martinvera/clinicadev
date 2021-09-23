package pe.com.ci.sed.integrator.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.ci.sed.integrator.model.request.RequestHeader;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse<T> {
    private RequestHeader header;
    private T response;
    
    public GenericResponse(T response) {
        this.response = response;
        this.header = RequestHeader.builder()
                .transactionId("")
                .applicationId("")
                .userId("")
                .build();
    }
}