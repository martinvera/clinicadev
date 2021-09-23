package pe.com.ci.sed.reporte.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TemplateConfiguration {

    @Value("${rest.client.expediente.endpoint}")
    public String microservice_expediente;

    @Value("${rest.client.document.endpoint}")
    public String microservice_document;

    @Value("${rest.client.clinical.endpoint}")
    public String microservice_clinical;

    @Bean
    public RestTemplate restTemplateExpediente() {
        return new RestTemplateBuilder().rootUri(microservice_expediente).build();
    }

    @Bean
    public RestTemplate restTemplateDocument() {
        return new RestTemplateBuilder().rootUri(microservice_document).build();
    }

    @Bean
    public  RestTemplate restTemplateClinical(){
        return new RestTemplateBuilder().rootUri(microservice_clinical).build();
    }
}
