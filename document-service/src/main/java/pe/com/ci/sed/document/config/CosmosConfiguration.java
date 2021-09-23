package pe.com.ci.sed.document.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.spring.data.cosmos.config.AbstractCosmosConfiguration;
import com.azure.spring.data.cosmos.repository.config.EnableCosmosRepositories;

@Configuration
@EnableCosmosRepositories(basePackages = "pe.com.ci.sed.document.persistence.repository")
public class CosmosConfiguration extends AbstractCosmosConfiguration {

    @Value("${azure.cosmos.uri}")
    public String uri;

    @Value("${azure.cosmos.key}")
    public String key;

    @Value("${azure.cosmos.database}")
    public String database;
    
    @Value("${azure.cosmos.container-name}")
    public String container;

    @Bean
    public CosmosClientBuilder cosmosClientBuilder() {
        return new CosmosClientBuilder().endpoint(uri).key(key)
                .endpointDiscoveryEnabled(false).readRequestsFallbackEnabled(false);
    }

    @Bean
    public CosmosContainer cosmosContainer() {
        return cosmosClientBuilder().buildClient().getDatabase(getDatabaseName()).getContainer(container);
    }

    @Override
    protected String getDatabaseName() {
        return database;
    }

}
