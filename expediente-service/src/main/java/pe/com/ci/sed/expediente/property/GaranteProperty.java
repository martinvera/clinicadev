package pe.com.ci.sed.expediente.property;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "application")
@PropertySource(value = "classpath:garante.yml", factory = GarantePropertiesFactory.class)
public class GaranteProperty {

    private List<String> garantes;
    
    private Map<String,String> equivalenciaSede;
    
    private String ruc;

}
