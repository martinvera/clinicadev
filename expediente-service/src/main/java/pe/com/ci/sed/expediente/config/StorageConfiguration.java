package pe.com.ci.sed.expediente.config;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTableClient;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import pe.com.ci.sed.expediente.errors.ExpedienteException;
import pe.com.ci.sed.expediente.utils.Constants;
import pe.com.ci.sed.expediente.property.GaranteProperty;

@Configuration
@Data
@Log4j2
public class StorageConfiguration {

	@Value("${azure.storage.container-account}")
    public String account;
    
    @Value("${azure.storage.container-expiryTime}")
    public long expiryTime;

    @Value("${azure.storage.key}")
    public String key;
    
    @Value("${azure.storage.table-endpoint}")
    private String storageConnection;

    @Autowired
    private final GaranteProperty garanteProperty;

    @Bean(name = "conexion")
    public CloudTableClient getConexion() {
        try {
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnection);
            return storageAccount.createCloudTableClient();
        } catch (URISyntaxException | InvalidKeyException e) {
            log.info("{}", e.getLocalizedMessage());
            throw new ExpedienteException(e.getMessage());
        }
    }

    @Value("${azure.storage.blob-endpoint}")
    public String endpoint;

    @Value("${azure.storage.container-name}")
    public String container;

    @Bean
    public BlobServiceClient blobServiceClient() {
        return new BlobServiceClientBuilder().endpoint(endpoint).buildClient();
    }

    @Bean("blobContainerClient")
    public BlobContainerClient blobContainerClient() {
        return blobServiceClient().getBlobContainerClient(container);
    }
    
    @Bean("blobContainerClientSas")
    public BlobContainerClient getBlobContainerClientSas() {
    	StorageSharedKeyCredential credential = new StorageSharedKeyCredential(account, key);
        return new BlobServiceClientBuilder().endpoint( String.format(Constants.AZURE_STORAGE_ACCOUNT_URI, account) ).credential(credential).buildClient().getBlobContainerClient(container);
    }
}
