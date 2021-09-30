package pe.com.ci.sed.clinicalrecord.property;

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
@PropertySource(value = "classpath:sistemaorigen.yml", factory = OrigenPropertiesFactory.class)
public class OrigenProperty {
    private Map<String,String> equivalencia;

    private Map<String,String> origen;
}
