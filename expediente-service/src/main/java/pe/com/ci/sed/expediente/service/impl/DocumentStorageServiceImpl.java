package pe.com.ci.sed.expediente.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;

import lombok.extern.log4j.Log4j2;
import pe.com.ci.sed.expediente.errors.ExpedienteException;

@Log4j2
@Service
public class DocumentStorageServiceImpl {

    private final BlobContainerClient blobContainerClient;
    private static final String NOMBRE_CARPETA = "documentos";

    public DocumentStorageServiceImpl(BlobContainerClient blobContainerClient) {
        this.blobContainerClient = blobContainerClient;
    }

    public List<InputStream> obtenerDocumentosXFactura(long nroLote, String facturaNro) {
        log.info("Leyendo archivos de la factura {}", facturaNro);
        List<InputStream> listInputStream;
        String dir = String.format("%s/%s/%s/", DocumentStorageServiceImpl.NOMBRE_CARPETA, nroLote, facturaNro);

        PagedIterable<BlobItem> list = blobContainerClient.listBlobsByHierarchy(dir);

        listInputStream = list.stream().filter(i -> i.isPrefix() == null).map(i -> {

            BlobClient blobClient = blobContainerClient.getBlobClient(i.getName());
            log.debug("Archivo nombre {} , tamanio {}", blobClient.getBlobName(), blobClient.getProperties().getBlobSize());

            return new ByteArrayInputStream(blobClient.downloadContent().toBytes());
        }).collect(Collectors.toList());


        list.stream().filter(i -> i.isPrefix() != null && i.isPrefix()).map(i -> {
            log.debug("Leyendo directorio {}", i.getName());
            return i.getName();
        }).forEach(path -> {
            PagedIterable<BlobItem> archivos = blobContainerClient.listBlobsByHierarchy(path);
            archivos.forEach(a -> {
                BlobClient blobClient = blobContainerClient.getBlobClient(a.getName());
                log.debug("Archivo nombre {} , tamanio {}", blobClient.getBlobName(), blobClient.getProperties().getBlobSize());

                listInputStream.add(new ByteArrayInputStream(blobClient.downloadContent().toBytes()));
            });

        });

        return listInputStream;
    }

}
