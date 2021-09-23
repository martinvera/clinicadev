package pe.com.ci.sed.reporte.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;
import java.util.Map;

@Data
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "application")
@PropertySource(value = "classpath:garanteTotal.yml", factory = GarantePropertiesFactory.class)
public class GaranteTotalProperty {
    private List<Map<String,String>> garantes;
}
