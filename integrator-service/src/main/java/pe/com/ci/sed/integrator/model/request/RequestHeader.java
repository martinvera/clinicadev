package pe.com.ci.sed.integrator.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestHeader {
    private String transactionId;
    private String applicationId;
    private String userId;

}
