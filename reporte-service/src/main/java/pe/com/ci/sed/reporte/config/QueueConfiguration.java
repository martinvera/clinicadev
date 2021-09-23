package pe.com.ci.sed.reporte.config;

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

    @Value("${azure.queue.reporte.cola}")
    public String reporteCola;

    @Bean
    public QueueServiceClient queueServiceClient() {
        return new QueueServiceClientBuilder().endpoint(conection).buildClient();
    }

    @Bean
    public QueueClient queueClient() {
        return queueServiceClient().getQueueClient(reporteCola);
    }

}
