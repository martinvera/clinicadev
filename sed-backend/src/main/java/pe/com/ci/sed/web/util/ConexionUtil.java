package pe.com.ci.sed.web.util;

import com.microsoft.azure.storage.ResultContinuation;
import com.microsoft.azure.storage.ResultContinuationType;
import com.microsoft.azure.storage.ResultSegment;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import pe.com.ci.sed.web.model.request.Busqueda;
import pe.com.ci.sed.web.model.generic.HeaderRequest;
import pe.com.ci.sed.web.model.generic.GenericResponse;
import pe.com.ci.sed.web.model.generic.Paginacion;
import pe.com.ci.sed.web.model.generic.ResultSet;

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

    public static ResultContinuation getContinuationToken(Busqueda param) {
        ResultContinuation token;
        if (param == null) {
            return null;
        } else {
            token = new ResultContinuation();
            token.setContinuationType(ResultContinuationType.TABLE);
        }

        if (!StringUtils.isBlank(param.getSiguiente())) {
            token.setNextPartitionKey(param.getSiguiente());
        }

        if (!StringUtils.isBlank(param.getAtras())) {
            token.setNextRowKey(param.getAtras());
        }

        return token;
    }

    public static <T> GenericResponse<ResultSet<List<T>>> getResultSetPaginacion(HeaderRequest header, ResultSegment<T> result) {
        Paginacion pagination = getToken(result.getContinuationToken(), result.getPageSize(), result);
        return GenericUtil.getResultPaginacion(header, result.getResults(), pagination);
    }

    public static <T, H> GenericResponse<ResultSet<List<H>>> getResultSetPaginacion(HeaderRequest header, ResultSegment<T> result, List<H> response) {
        Paginacion pagination = getToken(result.getContinuationToken(), result.getPageSize(), result);
        return GenericUtil.getResultPaginacion(header, response, pagination);
    }

    private static <T> Paginacion getToken(ResultContinuation continuationToken2, Integer pageSize, ResultSegment<T> result) {
        ResultContinuation continuationToken;
        Paginacion pagination = null;
        continuationToken = continuationToken2;
        if (continuationToken != null) {
            pagination = new Paginacion();
            pagination.setSiguiente(continuationToken.getNextPartitionKey());
            pagination.setAtras(continuationToken.getNextRowKey());
            pagination.setTotalitems(pageSize);
        }
        return pagination;
    }
}
