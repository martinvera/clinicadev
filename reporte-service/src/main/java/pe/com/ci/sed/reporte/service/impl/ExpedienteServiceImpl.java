package pe.com.ci.sed.reporte.service.impl;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.AllArgsConstructor;
import pe.com.ci.sed.reporte.model.generic.GenericRequest;
import pe.com.ci.sed.reporte.model.request.RequestReporte;
import pe.com.ci.sed.reporte.service.ExpedienteService;

@Service
@AllArgsConstructor
public class ExpedienteServiceImpl implements ExpedienteService {

    private final RestTemplate restTemplateExpediente;

    @Override
    public String validarNroLote(GenericRequest<RequestReporte> genericRequest) {
    	
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GenericRequest<RequestReporte>> entity = new HttpEntity<>(genericRequest, headers);
        ResponseEntity<String> response = restTemplateExpediente.exchange("/reporte/validarNroLote", HttpMethod.POST, entity, String.class);
        return response.getBody();
    }
}
