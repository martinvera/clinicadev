package pe.com.ci.sed.expediente.utils;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;


import java.net.URISyntaxException;

@Log4j2
@AllArgsConstructor
public class ConexionUtil {

    private final CloudTableClient cloudTableClient;

    public CloudTable getTable(String nombreTabla) {
        try {
            CloudTable table = cloudTableClient.getTableReference(nombreTabla);
            table.createIfNotExists();
            return table;
        } catch (URISyntaxException | StorageException e) {
            log.info("error: {}", e.getLocalizedMessage());
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }
}
