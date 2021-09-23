package pe.com.ci.sed.reporte.service.impl;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.azure.core.http.rest.Response;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Context;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlockBlobItem;
import com.azure.storage.blob.options.BlobParallelUploadOptions;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import pe.com.ci.sed.reporte.config.StorageConfiguration;
import pe.com.ci.sed.reporte.errors.ReporteException;
import pe.com.ci.sed.reporte.service.StorageService;
import pe.com.ci.sed.reporte.utils.Constants;
import static pe.com.ci.sed.reporte.utils.Constants.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final BlobContainerClient blobContainerClient;
    private final BlobContainerClient blobContainerClientSas;
    private final StorageConfiguration storageConfiguration;

    private static final String nombreCarpetaReporte = "reportes";
    private static final String EXCEL_FILE_EXT = ".xlsx";

    @Override
    public Map<String, String> uploadExcel(byte[] bytes, String nombreReporte) {
        return this.uploadExcel(bytes, nombreReporte, Strings.EMPTY);
    }

    @Override
    public Map<String, String> uploadExcel(byte[] bytes, String nombreReporte, String garante) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy-HH.mm.ss");
        String fechaArchivo = LocalDateTime.now().format(formatter);

        String filename = String.format("%s-%s%s", nombreReporte, fechaArchivo, EXCEL_FILE_EXT);
        String rutaArchivo;
        if (Strings.isNotBlank(garante))
            rutaArchivo = String.format("%s/%s/%s/%s", NOMBRE_CARPETA_REPORTES, nombreReporte, garante, filename);
        else
            rutaArchivo = String.format("%s/%s/%s", NOMBRE_CARPETA_REPORTES, nombreReporte, filename);

        Response<BlockBlobItem> result = this.uploadResponse(bytes, rutaArchivo, Constants.CONTENT_TYPE_EXCEL);

        if (result.getStatusCode() == HttpStatus.CREATED.value()) {
            log.debug("Carga archivo OK ,nombreReporte = {} , rutaArchivo = {}", nombreReporte, rutaArchivo);
            Map<String, String> response = new HashMap<>();
            response.put(PARAM_NOMBRE, filename);
            response.put(PARAM_URL, rutaArchivo);
            return response;
        } else {
            log.error("Error nombreReporte = {} , error = {}", nombreReporte, result.getStatusCode());
            throw new ReporteException("Ocurrió un error en la carga del reporte", HttpStatus.EXPECTATION_FAILED);
        }
    }

    @Override
    public void delete(String url) {
        if (Strings.isNotBlank(url)) {
            BlobClient blobClient = blobContainerClient.getBlobClient(url);
            if (blobClient.exists().equals(Boolean.TRUE)) blobClient.delete();
        }
    }

    @Override
    public InputStream download(String url) {
        BlobClient blobClient = blobContainerClient.getBlobClient(url);
        if (blobClient.exists().equals(Boolean.TRUE)) {
            return blobClient.downloadContent().toStream();
        }
        throw new ReporteException("No se encontró el archivo " + url);
    }

    private Response<BlockBlobItem> uploadResponse(byte[] bytes, String rutaArchivo, String contentType) {
        BlobClient blobClient = blobContainerClient.getBlobClient(rutaArchivo);

        BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(contentType);
        BinaryData data = BinaryData.fromBytes(bytes);

        log.debug("Subiendo archivo {} ", rutaArchivo);

        return blobClient.uploadWithResponse(new BlobParallelUploadOptions(data).setHeaders(headers), null, Context.NONE);
    }

    public String getUrlWithSas(String resourceUrl) {
        return Optional.ofNullable(resourceUrl).filter(Strings::isNotBlank).map(x -> {
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
}
