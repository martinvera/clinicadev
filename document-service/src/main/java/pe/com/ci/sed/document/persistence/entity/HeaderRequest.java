package pe.com.ci.sed.document.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeaderRequest {
    private String transactionId;
    private String applicationId;
    private String userId;
}
