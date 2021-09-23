package pe.com.ci.sed.reporte.config;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;

import lombok.Data;
import pe.com.ci.sed.reporte.utils.Constants;

@Configuration
@Data
public class StorageConfiguration {

    @Value("${azure.storage.blob-endpoint}")
    public String endpoint;

    @Value("${azure.storage.container-name}")
    public String container;

    @Value("${azure.storage.container-account}")
    public String account;
    
    @Value("${azure.storage.container-expiryTime}")
    public long expiryTime;

    @Value("${azure.storage.key}")
    public String key;
    
    @Value("${azure.storage.table-endpoint}")
    private String tablestorageConnection;

    @Value("${azure.storage.table-name}")
    private String tablestoragename;

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
    
    @Bean
    public CloudTableClient cloudTableClient() {
        try {
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(tablestorageConnection);
            return storageAccount.createCloudTableClient();
        } catch (URISyntaxException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public CloudTable tableService() {
        try {
            return cloudTableClient().getTableReference(tablestoragename);
        } catch (URISyntaxException | StorageException e) {
            throw new RuntimeException(e);
        }
    }
}
