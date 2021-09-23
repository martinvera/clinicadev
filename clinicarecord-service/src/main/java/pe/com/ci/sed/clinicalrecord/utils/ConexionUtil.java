package pe.com.ci.sed.clinicalrecord.utils;

import com.microsoft.azure.storage.ResultContinuation;
import com.microsoft.azure.storage.ResultContinuationType;
import com.microsoft.azure.storage.ResultSegment;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import pe.com.ci.sed.clinicalrecord.model.generic.GenericResponse;
import pe.com.ci.sed.clinicalrecord.model.generic.Paginacion;
import pe.com.ci.sed.clinicalrecord.model.generic.HeaderRequest;
import pe.com.ci.sed.clinicalrecord.model.response.ResultSet;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

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
    public static <T> ResultContinuation getContinuationToken(Paginacion param) {
        ResultContinuation token;
        if (param == null) {
            return null;
        } else {
            token = new ResultContinuation();
            token.setContinuationType(ResultContinuationType.TABLE);
        }

        if (Objects.nonNull(param.getSiguiente())) {
            token.setNextPartitionKey(String.valueOf(param.getSiguiente()));
        }

        if (Objects.nonNull(param.getAtras())) {
            token.setNextRowKey(String.valueOf(param.getAtras()));
        }

        return token;
    }

    public static <T> GenericResponse<ResultSet<List<T>>> getResultSetPaginacion(HeaderRequest header, ResultSegment<T> response) {
        ResultContinuation continuationToken;
        Paginacion pagination = null;
        continuationToken = response.getContinuationToken();
        if (continuationToken != null) {
            pagination = new Paginacion();
            pagination.setSiguiente(continuationToken.getNextPartitionKey());
            pagination.setAtras(continuationToken.getNextRowKey());
            pagination.setTotalitems(response.getPageSize());
        }
        return GenericUtil.getResultPaginacion(header, response.getResults(), pagination);
    }
}
