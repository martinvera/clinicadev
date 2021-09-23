package pe.com.ci.sed.clinicalrecord.controller;

import javax.validation.Valid;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.com.ci.sed.clinicalrecord.model.request.BusquedaHistorial;
import pe.com.ci.sed.clinicalrecord.model.request.BusquedaLotes;
import pe.com.ci.sed.clinicalrecord.model.request.EliminarRequest;
import pe.com.ci.sed.clinicalrecord.model.generic.GenericRequest;
import pe.com.ci.sed.clinicalrecord.persistence.entity.GestionLote;
import pe.com.ci.sed.clinicalrecord.persistence.entity.GestionLotesEnviados;
import pe.com.ci.sed.clinicalrecord.persistence.entity.HistorialLoteGarante;
import pe.com.ci.sed.clinicalrecord.service.GestionLotesService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/gestionlotes")
public class GestionLotesController {

    private final GestionLotesService gestionLotesService;

    @PostMapping(value = "/buscarLotes", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> buscarLotes(@Valid @RequestBody GenericRequest<BusquedaLotes> request) {
        return ResponseEntity.ok(gestionLotesService.buscarLotes(request));
    }

    @PostMapping(value = "/eliminarLote", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> eliminarDocumentos(@Valid @RequestBody GenericRequest<EliminarRequest> request) {
        return ResponseEntity.ok(gestionLotesService.eliminarLote(request));
    }

    @PostMapping(value = "/registrarHistorial", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> registrarHistorial(@RequestBody GenericRequest<HistorialLoteGarante> request) {
        return ResponseEntity.ok(gestionLotesService.registrarHistorial(request));
    }

    @PostMapping(value = "/buscarHistorial", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> buscarHistorialGarante(@RequestBody GenericRequest<BusquedaHistorial> request) {
        return ResponseEntity.ok(gestionLotesService.buscarHistorialGarante(request));
    }

    @PostMapping(value = "/listarEnviadoGarante", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GestionLotesEnviados>> listarEnviadoGarante(@Valid @RequestBody GenericRequest<BusquedaLotes> request) {
        return ResponseEntity.ok(gestionLotesService.listarEnviadoGarante(request));
    }

    @PostMapping(value = "/EnviadoGaranteReporte", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GestionLote>> listarEnviadosGaranteReporte(@Valid @RequestBody GenericRequest<BusquedaLotes> request) {
        return ResponseEntity.ok(gestionLotesService.listarEnviadosGaranteReporte(request));
    }

    @PostMapping(value = "/eliminarExpXlote", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> eliminarExpXlote(@Valid @RequestBody GenericRequest<EliminarRequest> request) {
        return ResponseEntity.ok(gestionLotesService.eliminarExpGeneradoXlote(request));
    }
}
