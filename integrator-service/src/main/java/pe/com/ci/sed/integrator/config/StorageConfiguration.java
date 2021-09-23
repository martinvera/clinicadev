package pe.com.ci.sed.integrator.config;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfiguration {

	@Value("${azure.storage.blob-endpoint}")
    public String endpoint;

    @Value("${azure.storage.container-name}")
    public String container;

    @Bean
    public BlobServiceClient blobServiceClient() {
        return new BlobServiceClientBuilder().endpoint(endpoint).buildClient();
    }
    
    @Bean
    public BlobContainerClient getBlobContainerClient() {
        return blobServiceClient().getBlobContainerClient(container);
    }

}
