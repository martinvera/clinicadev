package pe.com.ci.sed.document.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueServiceClient;
import com.azure.storage.queue.QueueServiceClientBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class QueueConfiguration {

    @Value("${azure.queue.conection}")
    public String conection;

    @Value("${azure.queue.cola}")
    public String cola;

    @Value("${azure.queue.colaerror}")
    public String colaerror;

    @Bean
    public QueueServiceClient queueServiceClient() {
        return new QueueServiceClientBuilder().endpoint(conection).buildClient();
    }

    @Bean
    public QueueClient queueClient() {
        return queueServiceClient().getQueueClient(cola);
    }

    @Bean
    public QueueClient queueClientError() {
        return queueServiceClient().getQueueClient(colaerror);
    }
}
