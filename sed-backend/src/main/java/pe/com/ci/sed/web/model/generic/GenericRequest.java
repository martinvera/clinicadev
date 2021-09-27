package pe.com.ci.sed.web.model.generic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericRequest<T> {
    private HeaderRequest header;
    private T request;
}
