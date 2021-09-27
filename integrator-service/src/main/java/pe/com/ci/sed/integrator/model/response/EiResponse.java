package pe.com.ci.sed.integrator.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EiResponse{
    private Map<String,String> response;
    private Map<String,String> header;
}