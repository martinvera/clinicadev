package pe.com.ci.sed.document.service.impl;

import static pe.com.ci.sed.document.util.Constants.EXCEL_FILE_EXT;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
import pe.com.ci.sed.document.config.StorageConfiguration;
import pe.com.ci.sed.document.errors.DocumentException;
import pe.com.ci.sed.document.service.StorageService;
import pe.com.ci.sed.document.util.Constants;


@Log4j2
@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {
    private final BlobContainerClient blobContainerClient;
    private final BlobContainerClient blobContainerClientSas;
    private final StorageConfiguration storageConfiguration;
    private static final String PDF_FILE_EXT = ".pdf";
    private static final String NOMBRE_CARPETA_DOCUMENTOS = "documentos";

    /**
     * Paramentros de Reportes
     */
    private static final String NOMBRE_CARPETA_REPORTE = "reportes";
    private static final String CONTENT_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public String getFileName(String facturaNumero, String numeroEncuentro, String codigoTipoDocumento) {
        return String.format("%s_%s_%s%s", facturaNumero, numeroEncuentro, codigoTipoDocumento, PDF_FILE_EXT);
    }

    public String getPath(long lote, String facturaNumero, String numeroEncuentro, String filename) {
        return String.format("%s/%s/%s/%s/%s", StorageServiceImpl.NOMBRE_CARPETA_DOCUMENTOS, lote, facturaNumero, numeroEncuentro, filename);
    }

    public String upload(String bytes, long lote, String facturaNumero, String numeroEncuentro, String codigoTipoDocumento) {

        byte[] decodedBytes = Base64.getDecoder().decode(bytes);

        String filename = this.getFileName(facturaNumero, numeroEncuentro, codigoTipoDocumento);

        String rutaArchivo = this.getPath(lote, facturaNumero, numeroEncuentro, filename);

        log.debug("Subiendo archivo {} ", rutaArchivo);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("facturaNro", String.valueOf(lote));
        metadata.put("nroLote", facturaNumero);
        metadata.put("nroEncuentro", numeroEncuentro);

        Response<BlockBlobItem> result = uploadResponse(decodedBytes, rutaArchivo, MediaType.APPLICATION_PDF_VALUE, metadata);

        if (result.getStatusCode() == HttpStatus.CREATED.value()) {
            log.info("Carga archivo OK , Numero encuentro = {} , Numero factura = {} , numero de lote = {}, url = {}", numeroEncuentro, facturaNumero, lote, rutaArchivo);

        } else {
            log.error("Error Numero encuentro = {} ,numero factura = {} , numero de lote = {}, error = {}", numeroEncuentro, facturaNumero, lote, result.getStatusCode());
            throw new DocumentException("Ocurrió un error en la carga del documentos", HttpStatus.EXPECTATION_FAILED);
        }

        return rutaArchivo;
    }

    public Map<String, String> uploadeExcel(byte[] bytes, String nombreReporte) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy-HH.mm.ss");
        String fechaArchivo = LocalDateTime.now().format(formatter);

        if (bytes == null || bytes.length <= 1)
            throw new DocumentException("No existe archivo detallado", HttpStatus.BAD_REQUEST);

        String filename = String.format("%s-%s%s", nombreReporte, fechaArchivo, EXCEL_FILE_EXT);
        String rutaArchivo = String.format("%s/%s/%s", NOMBRE_CARPETA_REPORTE, nombreReporte, filename);

        Response<BlockBlobItem> result = this.uploadResponse(bytes, rutaArchivo, CONTENT_EXCEL, null);

        if (result.getStatusCode() == HttpStatus.CREATED.value()) {
            log.info("Carga archivo OK ,nombreReporte = {} , rutaArchivo = {}", nombreReporte, rutaArchivo);
            Map<String, String> response = new HashMap<>();
            response.put("nombre", filename);
            response.put("url", blobContainerClient.getBlobContainerUrl() + "/" + rutaArchivo);
            return response;
        } else {
            log.error("Error nombreReporte = {} , error = {}", nombreReporte, result.getStatusCode());
            throw new DocumentException("Ocurrió un error en la carga del reporte", HttpStatus.EXPECTATION_FAILED);
        }
    }

    private Response<BlockBlobItem> uploadResponse(byte[] bytes, String rutaArchivo, String mediaType, Map<String, String> metadata) {
        BlobClient blobClient = blobContainerClient.getBlobClient(rutaArchivo);

        BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(mediaType);
        BinaryData data = BinaryData.fromBytes(bytes);

        log.debug("Subiendo archivo {} ", rutaArchivo);
        BlobParallelUploadOptions bloboptions = new BlobParallelUploadOptions(data).setHeaders(headers);
        if (Objects.nonNull(metadata)) bloboptions.setMetadata(metadata);

        return blobClient.uploadWithResponse(bloboptions, null, Context.NONE);
    }

    public InputStream download(String url) {
        BlobClient blobClient = blobContainerClient.getBlobClient(url);
        if (blobClient.exists().equals(Boolean.TRUE)) {
            return blobClient.downloadContent().toStream();
        }
        throw new DocumentException("No se encontró el archivo " + url);
    }

    public void delete(String url) {
        BlobClient blobClient = blobContainerClient.getBlobClient(url);
        if (blobClient.exists().equals(Boolean.TRUE)) blobClient.delete();
    }

    public String moveBlob(String urlFrom, String urlTo) {
        BlobClient blobClientOrigen = blobContainerClient.getBlobClient(urlFrom);
        BlobClient blobClientDestino = blobContainerClient.getBlobClient(urlTo);

        blobClientDestino.upload(blobClientOrigen.downloadContent());
        blobClientOrigen.delete();

        return urlTo;
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
}
