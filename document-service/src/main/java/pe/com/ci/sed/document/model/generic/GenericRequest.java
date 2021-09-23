package pe.com.ci.sed.document.model.generic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Data
@Builder
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class GenericRequest<T> {
    private RequestHeader header;
    private @Valid T request;
}
