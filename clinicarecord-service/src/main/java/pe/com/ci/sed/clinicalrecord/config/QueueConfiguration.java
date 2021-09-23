package pe.com.ci.sed.clinicalrecord.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueServiceClient;
import com.azure.storage.queue.QueueServiceClientBuilder;

@Configuration
@EnableScheduling
public class QueueConfiguration {

    @Value("${azure.queue.conection}")
    public String conection;

    @Value("${azure.queue.clinicalrecord.cola}")
    public String clinicalcola;
    
    @Value("${azure.queue.documento.cola}")
    public String documentcola;

    @Value("${azure.queue.clinicalrecorderror.cola}")
    public String errorcola;
    
    @Bean
    public QueueServiceClient queueServiceClient() {
        return new QueueServiceClientBuilder().endpoint(conection).buildClient();
    }

    @Bean
    public QueueClient queueClientClinicalRecord() {
        return queueServiceClient().getQueueClient(clinicalcola);
    }
    
    @Bean
    public QueueClient queueClientDocumento() {
        return queueServiceClient().getQueueClient(documentcola);
    }
    
    @Bean
    public QueueClient queueClientError() {
        return queueServiceClient().getQueueClient(errorcola);
    }
}