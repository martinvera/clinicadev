package pe.com.ci.sed.document.service.impl;

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

import static pe.com.ci.sed.document.util.Constants.JSON_FILE_EXT;
import static pe.com.ci.sed.document.util.Constants.NOMBRE_CARPETA_DOCUMENTOS;
import static pe.com.ci.sed.document.util.Constants.NOMBRE_CARPETA_INTEGRACION;
import static pe.com.ci.sed.document.util.Constants.OCURRIO_UN_ERROR_EN_LA_CARGA_DEL_DOCUMENTOS;
import static pe.com.ci.sed.document.util.Constants.PDF_FILE_EXT;

@Log4j2
@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {
    private final BlobContainerClient blobContainerClient;
    private final BlobContainerClient blobContainerClientSas;
    private final StorageConfiguration storageConfiguration;

    public String getFileName(String facturaNumero, String numeroEncuentro, String codigoTipoDocumento) {
        return String.format("%s_%s_%s%s", facturaNumero, numeroEncuentro, codigoTipoDocumento, PDF_FILE_EXT);
    }

    public String getPath(long lote, String facturaNumero, String numeroEncuentro, String filename) {
        return String.format("%s/%s/%s/%s/%s", NOMBRE_CARPETA_DOCUMENTOS, lote, facturaNumero, numeroEncuentro, filename);
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
            throw new DocumentException(OCURRIO_UN_ERROR_EN_LA_CARGA_DEL_DOCUMENTOS, HttpStatus.EXPECTATION_FAILED);
        }

        return rutaArchivo;
    }

    public String uploadJsonUnilab(byte[] bytes, String origen, String nroEncuentro) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy-HH.mm.ss");
        String fechaArchivo = LocalDateTime.now().format(formatter);

        String filename = String.format("%s_%s_%s%s", origen, nroEncuentro, fechaArchivo, JSON_FILE_EXT);
        String rutaArchivo = String.format("%s/%s/%s", NOMBRE_CARPETA_INTEGRACION, origen, filename);

        Map<String, String> metadata = new HashMap<>();
        Response<BlockBlobItem> result = uploadResponse(bytes, rutaArchivo, MediaType.APPLICATION_JSON_VALUE, metadata);

        if (result.getStatusCode() == HttpStatus.CREATED.value()) {
            log.info("Carga archivo OK , Numero encuentro = {} , url = {}", nroEncuentro, rutaArchivo);

        } else {
            log.error("Error numero encuentro = {} , error = {}", nroEncuentro, result.getStatusCode());
            throw new DocumentException(OCURRIO_UN_ERROR_EN_LA_CARGA_DEL_DOCUMENTOS, HttpStatus.EXPECTATION_FAILED);
        }

        return rutaArchivo;
    }

    public String uploadJsonFileIafasCtrlDoc(byte[] bytes, String origen, long lote, String facturaNumero) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy-HH.mm.ss");
        String fechaArchivo = LocalDateTime.now().format(formatter);

        String filename = String.format("%s_%s_%s_%s%s", origen, lote, facturaNumero, fechaArchivo, JSON_FILE_EXT);
        String rutaArchivo = String.format("%s/%s/%s", NOMBRE_CARPETA_INTEGRACION, origen, filename);

        Map<String, String> metadata = new HashMap<>();
        Response<BlockBlobItem> result = uploadResponse(bytes, rutaArchivo, MediaType.APPLICATION_JSON_VALUE, metadata);

        if (result.getStatusCode() == HttpStatus.CREATED.value()) {
            log.info("Carga archivo OK , Numero factura = {} , numero de lote = {}, url = {}", facturaNumero, lote, rutaArchivo);

        } else {
            log.error("Error numero factura = {} , numero de lote = {}, error = {}", facturaNumero, lote, result.getStatusCode());
            throw new DocumentException(OCURRIO_UN_ERROR_EN_LA_CARGA_DEL_DOCUMENTOS, HttpStatus.EXPECTATION_FAILED);
        }
        return rutaArchivo;
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
