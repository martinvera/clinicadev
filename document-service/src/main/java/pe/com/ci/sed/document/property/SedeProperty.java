package pe.com.ci.sed.document.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

@Data
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "application")
@PropertySource(value = "classpath:sedes.yml", factory = SedePropertiesFactory.class)
public class SedeProperty {

    private Map<String,String> equivalencias;
    
    private Map<String,String> sedes;

}
