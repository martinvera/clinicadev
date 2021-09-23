package pe.com.ci.sed.expediente.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import pe.com.ci.sed.expediente.model.request.GenerarExpedienteRequest;
import pe.com.ci.sed.expediente.model.request.GenericRequest;
import pe.com.ci.sed.expediente.service.GenerarExpedienteService;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/expediente")
public class GenerarExpedienteController {

    private final GenerarExpedienteService generarExpedienteService;

    @PostMapping(value = "/enviar", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> enviar(@RequestBody @Valid GenericRequest<List<GenerarExpedienteRequest>> request) {
        return ResponseEntity.ok(generarExpedienteService.iniciarProcesoExpediente(request));
    }

}
