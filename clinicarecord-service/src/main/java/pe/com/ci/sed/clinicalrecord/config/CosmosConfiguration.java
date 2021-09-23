package pe.com.ci.sed.clinicalrecord.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.DirectConnectionConfig;
import com.azure.cosmos.GatewayConnectionConfig;
import com.azure.spring.data.cosmos.config.AbstractCosmosConfiguration;
import com.azure.spring.data.cosmos.config.CosmosConfig;
import com.azure.spring.data.cosmos.core.ResponseDiagnostics;
import com.azure.spring.data.cosmos.core.ResponseDiagnosticsProcessor;
import com.azure.spring.data.cosmos.repository.config.EnableCosmosRepositories;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
@EnableCosmosRepositories(basePackages = "pe.com.ci.sed.clinicalrecord.persistence.repository")
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
        AzureKeyCredential azureKeyCredential = new AzureKeyCredential(key);
        DirectConnectionConfig directConnectionConfig = new DirectConnectionConfig();
        GatewayConnectionConfig gatewayConnectionConfig = new GatewayConnectionConfig();
        return new CosmosClientBuilder().endpoint(uri)
                .credential(azureKeyCredential)
                .endpointDiscoveryEnabled(false).readRequestsFallbackEnabled(false)
                .directMode(directConnectionConfig, gatewayConnectionConfig);
    }

    @Bean
    public CosmosContainer cosmosContainer() {
        return cosmosClientBuilder().buildClient().getDatabase(getDatabaseName()).getContainer(container);
    }

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Bean
    @Override
    public CosmosConfig cosmosConfig() {
        return CosmosConfig.builder()
                .responseDiagnosticsProcessor(new ResponseDiagnosticsProcessorImplementation())
                .enableQueryMetrics(true).build();
    }

    private static class ResponseDiagnosticsProcessorImplementation implements ResponseDiagnosticsProcessor {

        @Override
        public void processResponseDiagnostics(@Nullable ResponseDiagnostics responseDiagnostics) {

            // To log everything:
            if (log.isDebugEnabled()) {
                log.debug("Response diagnostics {}", responseDiagnostics);
            }

        }
    }
}
