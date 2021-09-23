package pe.com.ci.sed.reporte.controller;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.com.ci.sed.reporte.handler.ReporteHandler;
import pe.com.ci.sed.reporte.model.generic.GenericRequest;
import pe.com.ci.sed.reporte.model.request.RequestReporte;
import pe.com.ci.sed.reporte.service.HistorialService;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/reporte")
public class ReporteController {

    private final HistorialService historialService;
    private final ReporteHandler reporteHandler;

    @PostMapping(value = "/listar", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> generarReporte(@RequestBody GenericRequest<RequestReporte> request) {
        return ResponseEntity.ok(historialService.listarHistorial(request));
    }

    @PostMapping("/enviar")
    public ResponseEntity<Object> encolarReporte(@Valid @RequestBody GenericRequest<RequestReporte> request) {
        return ResponseEntity.ok(reporteHandler.enviarColaReporte(request));
    }

    @GetMapping(value = "/health", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> health() {
        Map<String,String> result = new HashMap<>();
        result.put("status", "UP");
        return ResponseEntity.ok(result);
    }

}
