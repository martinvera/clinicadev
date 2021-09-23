package pe.com.ci.sed.reporte.model.generic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
