package pe.com.ci.sed.document.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.com.ci.sed.document.model.request.BusquedaSinLote;
import pe.com.ci.sed.document.model.generic.GenericRequest;
import pe.com.ci.sed.document.model.response.ResponseSinLote;
import pe.com.ci.sed.document.service.DocumentoService;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/documento/reporte")
public class ReporteController {

    private final DocumentoService documentoService;

    @PostMapping(value = "/sinLote", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ResponseSinLote>> reporteSinLote(@RequestBody GenericRequest<BusquedaSinLote> request) {
        return ResponseEntity.ok(documentoService.reporteSinLote(request));
    }
}
