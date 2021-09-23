package pe.com.ci.sed.clinicalrecord.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfiguration {
  @Value("${rest.client.timeout.connection}")
  private Duration restTimeOutConnection;

  @Value("${rest.client.timeout.read}")
  private Duration restTimeOutRead;

  @Value("${rest.client.expediente.endpoint}")
  private String endpointExpedienteService;

  @Bean
  public RestTemplate restTemplateExpediente() {
      return new RestTemplateBuilder().rootUri(endpointExpedienteService)
              .setConnectTimeout(restTimeOutConnection)
              .setReadTimeout(restTimeOutRead)
              //.interceptors(new LogbookClientHttpRequestInterceptor(logbook))
              .build();
  }
}
