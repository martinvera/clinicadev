package pe.com.ci.sed.integrator.service;

import com.azure.core.http.rest.Response;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Context;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlockBlobItem;
import com.azure.storage.blob.options.BlobParallelUploadOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import pe.com.ci.sed.integrator.errors.IntegratorException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Log4j2
@Service
@RequiredArgsConstructor
public class StorageService {
    private final BlobContainerClient blobContainerClient;

    private static final String NOMBRE_CARPETA_INTEGRACION = "integracion";
    private static final String JSON_FILE_EXT = ".json";

    public String uploadUnilab(byte[] bytes, String origen, String nroEncuentro) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy-HH.mm.ss");
        String fechaArchivo = LocalDateTime.now().format(formatter);

        String filename = String.format("%s_%s_%s%s", origen, nroEncuentro , fechaArchivo, JSON_FILE_EXT);
        String rutaArchivo = String.format("%s/%s/%s", StorageService.NOMBRE_CARPETA_INTEGRACION, origen, filename);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("nroEncuentro", nroEncuentro);
        Response<BlockBlobItem> result = uploadResponse(bytes, rutaArchivo, MediaType.APPLICATION_JSON_VALUE, metadata);

        if (result.getStatusCode() == HttpStatus.CREATED.value()) {
            log.info("Carga archivo OK , Numero encuentro = {} , url = {}", nroEncuentro, rutaArchivo);

        } else {
            log.error("Error numero encuentro = {} , error = {}", nroEncuentro, result.getStatusCode());
            throw new IntegratorException("Ocurrió un error en la carga del documentos", HttpStatus.EXPECTATION_FAILED);
        }

        return rutaArchivo;
    }
    
    public String upload(byte[] bytes, String origen, long lote, String facturaNumero) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy-HH.mm.ss");
        String fechaArchivo = LocalDateTime.now().format(formatter);

        String filename = String.format("%s_%s_%s_%s%s", origen, lote, facturaNumero, fechaArchivo, JSON_FILE_EXT);
        String rutaArchivo = String.format("%s/%s/%s", StorageService.NOMBRE_CARPETA_INTEGRACION, origen, filename);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("facturaNro", String.valueOf(lote));
        metadata.put("nroLote", facturaNumero);
        Response<BlockBlobItem> result = uploadResponse(bytes, rutaArchivo, MediaType.APPLICATION_JSON_VALUE, metadata);

        if (result.getStatusCode() == HttpStatus.CREATED.value()) {
            log.info("Carga archivo OK , Numero factura = {} , numero de lote = {}, url = {}", facturaNumero, lote, rutaArchivo);

        } else {
            log.error("Error numero factura = {} , numero de lote = {}, error = {}", facturaNumero, lote, result.getStatusCode());
            throw new IntegratorException("Ocurrió un error en la carga del documentos", HttpStatus.EXPECTATION_FAILED);
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
}
