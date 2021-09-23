package pe.com.ci.sed.document.config;

import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Arrays;

import javax.net.ssl.SSLContext;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;

import lombok.extern.log4j.Log4j2;
import pe.com.ci.sed.document.util.Constants;

@Log4j2
@Configuration
public class RestClientCustomizerIntegracion implements RestTemplateCustomizer {

    @Value("${rest.credentials.integracionclinica.password}")
    private String password;

    @Value("${rest.credentials.integracionclinica.url}")
    private String url;

    @Value("${azure.storage.container-name}")
    public String container;

    @Value("${azure.storage.container-account}")
    public String account;

    @Autowired
    private StorageConfiguration storageConfiguration;

    @Override
    public void customize(@Qualifier("restTemplateIntegracion") RestTemplate restTemplate) {
        configurarCertificado(restTemplate, this.password, this.url, this.storageConfiguration);
    }

    static void configurarCertificado(RestTemplate restTemplate, String password, String url, StorageConfiguration storageConfiguration) {
        final SSLContext sslContext;
        char[] contrasenia = password.toCharArray();
        try {
            log.info("url = {}", url);


            OffsetDateTime offsetDateTime = OffsetDateTime.now().plusMinutes(10);
            BlobSasPermission permission = new BlobSasPermission().setReadPermission(true);
            BlobServiceSasSignatureValues blobServiceSasSignatureValues = new BlobServiceSasSignatureValues(offsetDateTime, permission).setStartTime(OffsetDateTime.now());

            String sas = storageConfiguration.blobContainerClientSas().getBlobClient(url).generateSas(blobServiceSasSignatureValues);

            log.info("sas = {}", sas);

            String urlCert = String.format(Constants.AZURE_STORAGE_ACCOUNT_URI + "/%s/%s?%s", storageConfiguration.getAccount(), storageConfiguration.getContainer(), url, sas);

            log.info("Cargando certificado para integracion con Salesforce - url = {}", urlCert);

            sslContext = SSLContextBuilder.create()
                    .loadKeyMaterial(new URL(urlCert), contrasenia, contrasenia)
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to setup client SSL context", e);
        } finally {
            Arrays.fill(contrasenia, (char) 0);
        }
        final CloseableHttpClient httpClient = HttpClientBuilder.create().setSSLContext(sslContext).build();
        final ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        restTemplate.setRequestFactory(requestFactory);
        log.info("Termino configuración de certificado digital");
    }
}
