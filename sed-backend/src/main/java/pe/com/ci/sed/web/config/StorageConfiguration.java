package pe.com.ci.sed.web.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTableClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

@Log4j2
@Component
@Order(2)
public class StorageConfiguration {

    @Value("${storage.conexion}")
    private String storageConnection;

    @Bean(name = "conexion")
    public CloudTableClient getConexion() {
        try {
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnection);
            return storageAccount.createCloudTableClient();
        } catch (URISyntaxException | InvalidKeyException e) {
            log.info("{}", e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
        return mapper;
    }
}
