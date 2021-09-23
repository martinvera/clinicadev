package pe.com.ci.sed.clinicalrecord.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TemplateConfiguration {

    @Value("${rest.client.expediente.endpoint}")
    public String microserviceExpediente;

    @Bean
    public RestTemplate restTemplateExpediente() {
        return new RestTemplateBuilder().rootUri(microserviceExpediente).build();
    }

    @Value("${rest.client.document.endpoint}")
    public String microserviceDocument;

    @Bean
    public RestTemplate restTemplateDocument() {
        return new RestTemplateBuilder().rootUri(microserviceDocument).build();
    }
}
