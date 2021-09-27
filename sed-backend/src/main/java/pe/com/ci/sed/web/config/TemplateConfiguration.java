package pe.com.ci.sed.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TemplateConfiguration {

    @Value("${microservices.document}")
    public String micorservice_document;

    @Bean
    public RestTemplate restTemplateDocument() {
        return new RestTemplateBuilder().rootUri(micorservice_document).build();
    }

    @Value("${microservices.expediente}")
    public String microservice_expediente;

    @Bean
    public RestTemplate restTemplateExpediente() {
        return new RestTemplateBuilder().rootUri(microservice_expediente).build();
    }

    @Value("${microservices.clinicalrecord}")
    public String micorservice_clinicarecord;

    @Bean
    public RestTemplate restTemplateClinicalRecord() {
        return new RestTemplateBuilder().rootUri(micorservice_clinicarecord).build();
    }

    @Value("${microservices.reporte}")
    public String micorservice_reporte;

    @Bean
    public RestTemplate restTemplateReporte() {
        return new RestTemplateBuilder().rootUri(micorservice_reporte).build();
    }
}
