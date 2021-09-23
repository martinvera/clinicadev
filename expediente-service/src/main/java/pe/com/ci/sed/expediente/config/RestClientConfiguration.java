package pe.com.ci.sed.expediente.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestClientConfiguration {

    @Value("${rest.client.timeoutconnection}")
    private Duration restTimeOutConnection;

    @Value("${rest.client.timeoutread}")
    private Duration restTimeOutRead;

    @Value("${rest.client.document.endpoint}")
    private String endpointDocumentServices;

    @Bean
    public RestTemplate restTemplateDocument() {
        return new RestTemplateBuilder().rootUri(endpointDocumentServices)
                .setReadTimeout(restTimeOutRead)
                .setConnectTimeout(restTimeOutConnection)
                .build();
    }



    /**
     * RestTemplate para clinicalrecord-service
     */

    @Value("${rest.client.clinicalrecord.endpoint}")
    private String endpointClinicalService;

    @Bean
    public RestTemplate restTemplateClinicalRecord() {
        return new RestTemplateBuilder().rootUri(endpointClinicalService)
                .setReadTimeout(restTimeOutRead)
                .setConnectTimeout(restTimeOutConnection)
                .build();
    }


    @Value("${rest.client.clinicalrecord.endpointReport}")
    private String endpointClinicalReportService;

    @Bean
    public RestTemplate restTemplateClinicalRecordReport() {
        return new RestTemplateBuilder().rootUri(endpointClinicalReportService)
                .setReadTimeout(restTimeOutRead)
                .setConnectTimeout(restTimeOutConnection)
                .build();
    }
}
