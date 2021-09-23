package pe.com.ci.sed.clinicalrecord.model.generic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeaderRequest {

    @JsonInclude(Include.NON_NULL)
    private String transactionId;

    @JsonInclude(Include.NON_NULL)
    private String applicationId;

    @JsonInclude(Include.NON_NULL)
    private String userId;

    @JsonInclude(Include.NON_NULL)
    private String status;
}
