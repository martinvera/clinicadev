package pe.com.ci.sed.integrator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueServiceClient;
import com.azure.storage.queue.QueueServiceClientBuilder;

@Configuration
public class QueueConfiguration {

    @Value("${azure.queue.conection}")
    public String conection;

    @Value("${azure.queue.document-cola}")
    public String documentcola;

    @Value("${azure.queue.clinicalrecord-cola}")
    public String clinicalcola;
    
    @Bean
    public QueueServiceClient queueServiceClient() {
        return new QueueServiceClientBuilder().endpoint(conection).buildClient();
    }

    @Bean
    public QueueClient queueClientDocument() {
        return queueServiceClient().getQueueClient(documentcola);
    }
    
    @Bean
    public QueueClient queueClientClinicalRecord() {
        return queueServiceClient().getQueueClient(clinicalcola);
    }
}
