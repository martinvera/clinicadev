package pe.com.ci.sed.expediente.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.models.BlobItem;
import org.springframework.stereotype.Service;

import com.azure.core.http.rest.Response;
import com.azure.core.util.Context;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlockBlobItem;
import com.azure.storage.blob.options.BlobParallelUploadOptions;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import pe.com.ci.sed.expediente.config.StorageConfiguration;
import pe.com.ci.sed.expediente.service.ExpedienteStorageService;
import pe.com.ci.sed.expediente.utils.Constants;
import reactor.core.publisher.Flux;

import static pe.com.ci.sed.expediente.utils.Constants.APPLICATION_ZIP;

@Log4j2
@Service
@AllArgsConstructor
public class ExpedienteStorageServiceImpl implements ExpedienteStorageService {

    private final BlobContainerClient blobContainerClient;
    private final BlobContainerClient blobContainerClientSas;
    private final StorageConfiguration storageConfiguration;

    private static final String NOMBRE_CARPETA_EXPEDIENTE = "expediente";
    private static final String NOMBRE_CARPETA_DOCUMENTO = "documentos";


    @Override
    public String upload(byte[] bytes, String lote, String nombreZip) {
        String rutaArchivo = String.format("%s/%s/%s", NOMBRE_CARPETA_EXPEDIENTE, lote, nombreZip);
        BlobClient blobClient = blobContainerClient.getBlobClient(rutaArchivo);
        Flux<ByteBuffer> data = Flux.just(ByteBuffer.wrap(bytes));
        BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(APPLICATION_ZIP);

        Response<BlockBlobItem> item = blobClient.uploadWithResponse(new BlobParallelUploadOptions(data).setHeaders(headers), null, Context.NONE);
        log.info("STATUS UPLOAD: " + item.getStatusCode());

        return rutaArchivo;
    }

    @Override
    public InputStream descargarArchivo(String fileName) {
        BlobClient blobClient = blobContainerClient.getBlobClient(fileName);
        return Objects.requireNonNull(blobClient.downloadContent()).toStream();
    }

    @Override
    public void eliminarArchivosXLote(Integer nroLote) {
        //Elimnar archivos de expediente
        PagedIterable<BlobItem> items = blobContainerClient.listBlobsByHierarchy(NOMBRE_CARPETA_EXPEDIENTE + "/" + nroLote + "/");
        items.stream().filter(i -> i.isPrefix() == null).forEach(i -> blobContainerClient.getBlobClient(i.getName()).delete());

        items.stream().filter(i -> i.isPrefix() != null && i.isPrefix()).map(BlobItem::getName).forEach(path -> {
            PagedIterable<BlobItem> archivos = blobContainerClient.listBlobsByHierarchy(path);
            archivos.forEach(a -> blobContainerClient.getBlobClient(a.getName()).delete());
        });
    }

    public String getUrlWithSas(String resourceUrl) {
        return Optional.ofNullable(resourceUrl).filter(x -> !x.isBlank()).map(x -> {
            BlobClient blobClient = blobContainerClientSas.getBlobClient(resourceUrl);
            return String.format(Constants.AZURE_STORAGE_ACCOUNT_URI + "/%s/%s?%s", storageConfiguration.getAccount(), storageConfiguration.getContainer(), resourceUrl, blobClient.generateSas(this.blobServiceSasSignatureValues()));
        }).orElse("");
    }

    public String getUrlWithoutSas(String resourceUrl) {
        return String.format(Constants.AZURE_STORAGE_ACCOUNT_URI + "/%s/%s", storageConfiguration.getAccount(), storageConfiguration.getContainer(), resourceUrl);
    }

    private BlobServiceSasSignatureValues blobServiceSasSignatureValues() {
        OffsetDateTime offsetDateTime = OffsetDateTime.now().plusMinutes(10);
        BlobSasPermission permission = new BlobSasPermission().setReadPermission(true);
        return new BlobServiceSasSignatureValues(offsetDateTime, permission).setStartTime(OffsetDateTime.now());
    }

    public InputStream download(long nroLote) {
        BlobClient blobClient = blobContainerClient.getBlobClient(String.format("%s/%s/%s%s", NOMBRE_CARPETA_EXPEDIENTE, nroLote, nroLote, Constants.EXTENSION_ZIP));
        if (blobClient.exists().equals(Boolean.TRUE)) {
            return blobClient.downloadContent().toStream();
        }
        return null;
    }

}
