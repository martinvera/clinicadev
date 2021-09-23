package pe.com.ci.sed.document.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestClientConfiguration {

    @Value("${rest.timeout.connection}")
    private Duration restTimeOutConnection;

    @Value("${rest.timeout.read}")
    private Duration restTimeOutRead;

    @Value("${rest.client.integracionclinica}")
    private String endpointIntegracionClinica;

    @Value("${rest.client.clinicalrecord}")
    private String endpointClinicalService;

    @Bean
    public RestTemplate restTemplateIntegracion(RestTemplateBuilder builder) {
        return builder
                .rootUri(this.endpointIntegracionClinica)
                .setConnectTimeout(this.restTimeOutConnection)
                .setReadTimeout(this.restTimeOutRead)
                .build();
    }

    @Value("${rest.client.expediente}")
    private String endpointExpedienteDigital;

    @Bean
    public RestTemplate restTemplateExpedienteDigital() {
        return new RestTemplateBuilder().rootUri(endpointExpedienteDigital).build();
    }
    
    @Bean
    public RestTemplate restTemplateClinicalRecord() {
        return new RestTemplateBuilder().rootUri(endpointClinicalService)
                .setReadTimeout(restTimeOutRead)
                .setConnectTimeout(restTimeOutConnection)
                .build();
    }

}
