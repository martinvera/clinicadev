package pe.com.ci.sed.document.service.impl;

import static pe.com.ci.sed.document.util.GenericUtil.obtenerPorObjetoPOST;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import pe.com.ci.sed.document.errors.DocumentException;
import pe.com.ci.sed.document.model.generic.RequestHeader;
import pe.com.ci.sed.document.util.Constants;

@Service
@RequiredArgsConstructor
public class ClinicalRecordServiceImpl {

    private final RestTemplate restTemplateClinicalRecord;
    private static final String URL_ACTUALIZAR_FACTURA_ESTADO = "/actualizarFacturaEstado";

    public void actualizarFacturaEstado(Long nroLote, String facturaNro, Constants.ESTADO_FACTURA estado) {
        try {
            Map<String, String> request = new HashMap<>();
            request.put("facturaNro", facturaNro);
            request.put("nroLote", String.valueOf(nroLote));
            request.put("estado", estado.name());

            obtenerPorObjetoPOST(restTemplateClinicalRecord, RequestHeader.builder()
                            .applicationId("document-service")
                            .userId("DOCUMENT_SERVICE")
                            .transactionId(ThreadContext.get("transactionId"))
                            .build(),
                    URL_ACTUALIZAR_FACTURA_ESTADO, request, String.class);
        } catch (HttpClientErrorException e) {
            throw new DocumentException("Ocurrió un error al ACTUALIZA_FACTURA_" + estado.name(), e.getStatusCode());
        }
    }
}
