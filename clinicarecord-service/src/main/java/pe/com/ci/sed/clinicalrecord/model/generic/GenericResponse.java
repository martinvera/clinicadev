package pe.com.ci.sed.clinicalrecord.model.generic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse<T> {
    private HeaderRequest header;
    private T response;
    
    public GenericResponse(T response) {
        this.response = response;
        this.header = HeaderRequest.builder()
                .transactionId("")
                .applicationId("")
                .userId("")
                .build();
    }
}
