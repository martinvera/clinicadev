package pe.com.ci.sed.document.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pe.com.ci.sed.document.errors.DocumentException;
import pe.com.ci.sed.document.model.request.salesforce.Salesforce;
import pe.com.ci.sed.document.model.validacion.SalesforceValidator;
import pe.com.ci.sed.document.persistence.entity.Archivo;
import pe.com.ci.sed.document.persistence.entity.Documento;
import pe.com.ci.sed.document.service.GenFormatSalesforceService;
import pe.com.ci.sed.document.util.GenericUtil;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
@AllArgsConstructor
public class SalesForceServiceImpl {

    private final RestTemplate restTemplateIntegracion;
    private final GenFormatSalesforceService genFormatSalesforceService;
    private final Validator validator;

    public List<Archivo> generarDocumentoSalesforce(Documento documento) {
        List<Archivo> archivos = new ArrayList<>();
        try {
            log.info("Inicio de integración con salesforce para el encuentro {}", documento.getNroEncuentro());
            Salesforce salesforce = this.obtenerEncuentro(documento.getNroEncuentro());
            String errors = GenericUtil.getValidations(validator, salesforce, SalesforceValidator.class);
            if (Strings.isNotBlank(errors)) {
                throw new DocumentException("Debe completar los campos mínimos requeridos: " + errors);
            }
            log.debug("Datos del encuentro {}, {} ", documento.getNroEncuentro(), salesforce);
            if (salesforce.getCodRespuesta().equals("0"))
                return genFormatSalesforceService.generarFormatos(salesforce, documento);
        } catch (Exception e) {
            log.error(e);
        }
        return archivos;
    }

    public Salesforce obtenerEncuentro(String nroEncuentro) {
        try {
            Map<String, String> request = new HashMap<>();
            request.put("encHisNum", nroEncuentro);
            log.debug("Obteniendo datos desde salesforce para el encuentro {} ", nroEncuentro);
            return restTemplateIntegracion.postForObject("/ExpedienteDigital/Salesforce", request, Salesforce.class);
        } catch (Exception e) {
            throw new DocumentException(String.format("Ocurrió un error en la comunicación con Salesforce para el encuentro %s", nroEncuentro), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

}
