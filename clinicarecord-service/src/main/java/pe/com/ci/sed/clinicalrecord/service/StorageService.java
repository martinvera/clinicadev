package pe.com.ci.sed.clinicalrecord.service;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import pe.com.ci.sed.clinicalrecord.config.StorageConfiguration;
import pe.com.ci.sed.clinicalrecord.errors.ClinicalRecordException;
import pe.com.ci.sed.clinicalrecord.utils.Constants;

@Log4j2
@Service
@RequiredArgsConstructor
public class StorageService {
    private final BlobContainerClient blobContainerClient;
    private final BlobContainerClient blobContainerClientSas;
    private final StorageConfiguration storageConfiguration;


    private static final String NOMBRE_CARPETA = "documentos";
    private static final String PDF_FILE_EXT = ".pdf";
    private static final String CLINICALRECORD_ANEXO_DET = "anexo_detallado";
    private static final String CLINICALRECORD_FACTURA = "factura";


    public String uploadAnexoDetallado(String base64, long lote, String carpetaFactura, String nroFactura) {
        return this.uploadBase64(base64, lote, carpetaFactura, nroFactura);
    }

    public String uploadFactura(InputStream inputStream, long lote, String carpetaFactura, String nroFactura) {
        BinaryData data = BinaryData.fromStream(inputStream);
        return uploadGeturl(lote, carpetaFactura, nroFactura, data, "1_" + StorageService.CLINICALRECORD_FACTURA);
    }

    private String uploadBase64(String base64, long lote, String carpetaFactura, String nroFactura) {
        Optional.ofNullable(base64).filter(b -> !b.isEmpty()).orElseThrow(() -> new ClinicalRecordException("No existe archivo detallado", HttpStatus.BAD_REQUEST));

        byte[] bytes = Base64.getDecoder().decode(base64);
        BinaryData data = BinaryData.fromBytes(bytes);

        return uploadGeturl(lote, carpetaFactura, nroFactura, data, "2_" + StorageService.CLINICALRECORD_ANEXO_DET);
    }

    private String uploadGeturl(long lote, String carpetaFactura, String nroFactura, BinaryData data, String clinicalrecordAnexoDet) {
        String filename = String.format(Constants.NOMBRE_ARCHIVO_S_S_S, nroFactura, clinicalrecordAnexoDet, PDF_FILE_EXT);
        String rutaArchivo = String.format(Constants.RUTA_ARCHIVO_S_S_S_S, StorageService.NOMBRE_CARPETA, lote, carpetaFactura, filename);

        Response<BlockBlobItem> result = this.uploadResponse(rutaArchivo, data);

        if (result.getStatusCode() == HttpStatus.CREATED.value()) {
            return rutaArchivo;
        } else {
            log.error("Error numero factura = {} , numero de lote = {}, error = {}", carpetaFactura, lote, result.getStatusCode());
            throw new ClinicalRecordException("Ocurrió un error en la carga del anexo detallado", HttpStatus.EXPECTATION_FAILED);
        }
    }

    private Response<BlockBlobItem> uploadResponse(String rutaArchivo, BinaryData data) {
        BlobClient blobClient = blobContainerClient.getBlobClient(rutaArchivo);
        BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(MediaType.APPLICATION_PDF_VALUE);
        log.debug("Subiendo archivo {} ", rutaArchivo);

        return blobClient.uploadWithResponse(new BlobParallelUploadOptions(data).setHeaders(headers), null, Context.NONE);
    }

    public InputStream download(String url) {
        BlobClient blobClient = blobContainerClient.getBlobClient(url);
        if (blobClient.exists().equals(Boolean.TRUE)) {
            return blobClient.downloadContent().toStream();
        }
        throw new ClinicalRecordException("No se encontró el archivo " + url);
    }

    public String getUrlWithSas(String resourceUrl) {
        if (Strings.isNotEmpty(resourceUrl)) {
            BlobClient blobClient = blobContainerClientSas.getBlobClient(resourceUrl);
            return String.format(Constants.AZURE_STORAGE_ACCOUNT_URI + "/%s/%s?%s", storageConfiguration.getAccount(), storageConfiguration.getContainer(), resourceUrl, blobClient.generateSas(this.blobServiceSasSignatureValues()));
        }
        return Strings.EMPTY;
    }

    public String getUrlWithoutSas(String resourceUrl) {
        return String.format(Constants.AZURE_STORAGE_ACCOUNT_URI + "/%s/%s", storageConfiguration.getAccount(), storageConfiguration.getContainer(), resourceUrl);
    }

    private BlobServiceSasSignatureValues blobServiceSasSignatureValues() {
        OffsetDateTime offsetDateTime = OffsetDateTime.now().plusMinutes(10);
        BlobSasPermission permission = new BlobSasPermission().setReadPermission(true);
        return new BlobServiceSasSignatureValues(offsetDateTime, permission).setStartTime(OffsetDateTime.now());
    }

    public void delete(String url) {
        BlobClient blobClient = blobContainerClient.getBlobClient(url);
        if (blobClient.exists().equals(Boolean.TRUE)) blobClient.delete();
    }
}
