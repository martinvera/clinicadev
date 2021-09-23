package pe.com.ci.sed.expediente.service.impl;

import static pe.com.ci.sed.expediente.utils.GenericUtil.obtenerPorObjetoPOST;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import pe.com.ci.sed.expediente.errors.ExpedienteException;
import pe.com.ci.sed.expediente.model.request.ActualizarFacturaGenerada;
import pe.com.ci.sed.expediente.model.request.RequestHeader;
import pe.com.ci.sed.expediente.persistence.entity.ClinicalRecord;
import pe.com.ci.sed.expediente.service.ClinicalRecordService;

@Service
@RequiredArgsConstructor
public class ClinicalRecordServiceImpl implements ClinicalRecordService {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate restTemplateClinicalRecord;
    private static final String URL_ACTUALIZAR_FACTURA_GENERADA = "/actualizarFacturaGenerada";
    private static final String URL_OBTENER_FACTURA = "/obtenerFactura";

    @Override
    public void actualizarFacturaGenerada(RequestHeader header, List<ActualizarFacturaGenerada> actualizarFacturaGeneradas) {
        try {

            obtenerPorObjetoPOST(restTemplateClinicalRecord, header, URL_ACTUALIZAR_FACTURA_GENERADA, actualizarFacturaGeneradas, String.class);
        } catch (Exception e) {
            throw new ExpedienteException("Ocurrió un error al consulta el detalle de la factura");
        }

    }

    @Override
    public ClinicalRecord obtenerFactura(RequestHeader header, String facturaNro, long nroLote) {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("facturaNro", facturaNro);
            data.put("nroLote", String.valueOf(nroLote));
            
            String response = obtenerPorObjetoPOST(restTemplateClinicalRecord, header, URL_OBTENER_FACTURA, data, String.class);
         
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES , false);
            ClinicalRecord resp = mapper.readValue(mapper.readTree(response).get("response").get("data").toString(), ClinicalRecord.class);
            
            return resp;
        } catch (Exception e) {
            throw new ExpedienteException("Ocurrió un error al consulta el detalle de la factura, error = "+e.getMessage());
        }
    }
}
