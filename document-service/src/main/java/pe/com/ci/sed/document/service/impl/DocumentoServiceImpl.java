package pe.com.ci.sed.document.service.impl;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static pe.com.ci.sed.document.util.Constants.VALUE_OK;
import static pe.com.ci.sed.document.util.GenericUtil.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.FeedResponse;
import lombok.RequiredArgsConstructor;
import com.azure.spring.data.cosmos.exception.CosmosAccessException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.TableQuery;

import lombok.extern.log4j.Log4j2;
import pe.com.ci.sed.document.errors.DocumentException;
import pe.com.ci.sed.document.model.generic.GenericRequest;
import pe.com.ci.sed.document.model.generic.Paginacion;
import pe.com.ci.sed.document.model.request.BusquedaRequest;
import pe.com.ci.sed.document.model.request.BusquedaSinLote;
import pe.com.ci.sed.document.model.request.DocRequest;
import pe.com.ci.sed.document.model.response.ResponseSinLote;
import pe.com.ci.sed.document.persistence.entity.Archivo;
import pe.com.ci.sed.document.persistence.entity.Catalogo;
import pe.com.ci.sed.document.persistence.entity.Doc;
import pe.com.ci.sed.document.persistence.entity.Documento;
import pe.com.ci.sed.document.persistence.repository.DocumentRepository;
import pe.com.ci.sed.document.service.DocumentoService;
import pe.com.ci.sed.document.service.StorageService;
import pe.com.ci.sed.document.util.Constants;
import pe.com.ci.sed.document.util.Constants.ESTADO_FACTURA;
import pe.com.ci.sed.document.util.GenericUtil;


@Log4j2
@Service
@RequiredArgsConstructor
public class DocumentoServiceImpl implements DocumentoService {

    private final DocumentRepository documentRepository;
    private final CosmosContainer cosmosContainer;
    private final StorageService storageService;
    private final CloudTable cloudTable;
    private final ClinicalRecordServiceImpl clinicalRecordService;

    @Override
    public Object registrarDocumentoCargaManual(GenericRequest<Documento> request) {

        Optional<Documento> doc = documentRepository.findById(request.getRequest().getNroEncuentro());
        if (doc.isEmpty()) {
            request.getRequest().setOrigenServicio("EXPEDIENTE_DIGITAL");
            request.getRequest().setArchivos(this.subirArchivos(request.getRequest()));
            request.getRequest().setEstado(1);
            this.persistirDocumento(request.getRequest());
            return getResult("!Documento correctamente registrado!", request.getHeader());
        } else {
            throw new DocumentException("!No es posible registrar Documento, ya que el Número de Encuentro ya existe!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void registrarDocumentoIntegracion(Documento documento) {
        documento.setArchivos(this.subirArchivos(documento));
        documento.setEstado(1);
        this.persistirDocumento(documento);
    }

    @Override
    public Object modificarDocumentoCargaManual(GenericRequest<Documento> genericRequest) {
        AtomicReference<String> mensaje = new AtomicReference<>("!Documento no encontrado!");
        documentRepository.findById(genericRequest.getRequest().getNroEncuentro()).ifPresent(modificar -> {

            Map<String, Archivo> mapArchivosActuales = modificar.getArchivos().stream().filter(distinctByKey(Archivo::getTipoDocumentoId)).collect(Collectors.toMap(Archivo::getTipoDocumentoId, item -> item));
            Map<String, Archivo> mapArchivosNuevo = genericRequest.getRequest().getArchivos().stream().filter(distinctByKey(Archivo::getTipoDocumentoId)).collect(Collectors.toMap(Archivo::getTipoDocumentoId, item -> item));

            log.debug("archivos a eliminar q tienen pdf");
            List<Archivo> eliminarArchivos = modificar.getArchivos().stream().filter(Archivo::isExiste).filter(x -> !mapArchivosNuevo.containsKey(x.getTipoDocumentoId())).collect(Collectors.toList());
            eliminarArchivos.forEach(x -> mapArchivosActuales.remove(x.getTipoDocumentoId()));

            log.debug("actualizando lista archivos existentes y agregando nuevos");
            genericRequest.getRequest().getArchivos().stream().filter(x -> x.getArchivoBytes() != null && !x.getArchivoBytes().isBlank()).forEach(a -> mapArchivosActuales.put(a.getTipoDocumentoId(), a));


            modificar.setArchivos(new ArrayList<>(mapArchivosActuales.values()));

            eliminarArchivos.forEach(a -> {
                String fileName = storageService.getFileName(modificar.getFacturaNro(), modificar.getNroEncuentro(), a.getTipoDocumentoId());
                String path = storageService.getPath(modificar.getNroLote(), modificar.getFacturaNro(), modificar.getNroEncuentro(), fileName);
                log.debug("Actualizacion de documento : eliminando archivo {}", path);
                storageService.delete(path);
            });

            modificar.modificar(genericRequest.getRequest());
            modificar.setArchivos(this.subirArchivos(modificar));
            Documento update = this.persistirDocumentoReturn(modificar);

            log.info("inicio - validar estado Factura COMPLETADO");
            this.validarFacturaCompleta(update);
            log.info("fin - validar estado Factura COMPLETADO");

            mensaje.set("!Documento correctamente actualizado!");
        });
        return getResult(mensaje.get(), genericRequest.getHeader());
    }

    @Override
    public void modificarDocumentoIntegracion(Documento documentoExistente, Documento documentoIntegracion) {

        List<Archivo> items = documentoIntegracion.getArchivos().stream().filter(Objects::nonNull)
                .filter(f -> Strings.isNotBlank(f.getArchivoBytes())).collect(Collectors.toList());

        if (documentoExistente.getNroLote() == 0 && documentoIntegracion.getNroLote() > 0) {
            log.debug("Moviendo los archivos con lote 0");
            List<Archivo> archivosCopiados = documentoExistente.getArchivos().stream()
                    .filter(Objects::nonNull)
                    .filter(f -> Objects.nonNull(f.getUrlArchivoSas()))
                    .peek(p -> {
                                String origen = p.getUrlArchivoSas();
                                String newFileName = storageService.getFileName(documentoIntegracion.getFacturaNro(), documentoIntegracion.getNroEncuentro(), p.getTipoDocumentoId());
                                String path = storageService.getPath(documentoIntegracion.getNroLote(), documentoIntegracion.getFacturaNro(), documentoIntegracion.getNroEncuentro(), newFileName);

                                log.debug("Archivo a mover : ruta origen = {} -> ruta final = {}", origen, path);
                                p.setArchivoBytes(null);
                                p.setUrlArchivoSas(storageService.moveBlob(origen, path));
                                p.setUrlArchivo(storageService.getUrlWithoutSas(p.getUrlArchivoSas()));

                                log.debug("Archivo movido : ruta origen = {} - ruta final = {}", origen, p.getUrlArchivo());
                            }
                    ).collect(Collectors.toList());

            documentoIntegracion.setArchivos(GenericUtil.mergeList(archivosCopiados, documentoIntegracion.getArchivos()));
        }
        this.copyDocumento(documentoIntegracion, documentoExistente);

        documentoExistente.setArchivos(GenericUtil.mergeList(items, documentoExistente.getArchivos()));

        documentoExistente.setArchivos(this.subirArchivos(documentoExistente));
        this.persistirDocumento(documentoExistente);

    }

    @Override
    public Object detalleDocumento(GenericRequest<BusquedaRequest> request) {
        List<Documento> listaDocumentos = new ArrayList<>();


        String query = "SELECT * FROM c  where c.facturaNro='" + request.getRequest().getFacturaNro() + "' " +
                " AND c.nroLote =" + request.getRequest().getNroLote() +
                " AND c.nroEncuentro='" + request.getRequest().getNroEncuentro() + "' ";

        CosmosPagedIterable<Documento> result = cosmosContainer.queryItems(query, null, Documento.class);
        result.forEach(listaDocumentos::add);
        Optional<Documento> doc = listaDocumentos.stream().findFirst();
        if (doc.isPresent()) {
            Documento documento = doc.get();
            documento.setArchivos(documento.getArchivos().stream().filter(Archivo::isExiste).map(x -> {
                x.setUrlArchivo(storageService.getUrlWithSas(x.getUrlArchivoSas()));
                return x;

            }).collect(Collectors.toList()));
            return getResult(documento, request.getHeader());
        } else
            return getResult(null, request.getHeader());
    }

    @Override
    public Optional<Documento> findById(String nroEncuentro) {
        return documentRepository.findById(nroEncuentro);
    }

    @Override
    public void delete(Documento documento) {
        documentRepository.delete(documento);
    }

    @Override
    public Object obtenerDocumento(DocRequest request) {
        List<Documento> listaDocumentos = new ArrayList<>();
        List<Doc> response = new ArrayList<>();

        String query = "SELECT * FROM c where c.facturaNro='" + request.getRequest().getFacturaNro() + "' " +
                " AND c.nroLote=" + request.getRequest().getNroLote();

        CosmosPagedIterable<Documento> result = cosmosContainer.queryItems(query, null, Documento.class);
        result.forEach(listaDocumentos::add);

        listaDocumentos.forEach(documento -> {
            response.addAll(
                    documento.getArchivos().stream().map(archivo -> Doc.builder()
                            .estadoArchivo(archivo.getEstadoArchivo())
                            .nroEncuentro(documento.getNroEncuentro())
                            .fechaAtencion(documento.getFechaAtencion())
                            .tipoDocumentoId(archivo.getTipoDocumentoId())
                            .tipoDocumentoDes(archivo.getTipoDocumentoDesc())
                            .urlArchivo(storageService.getUrlWithSas(archivo.getUrlArchivoSas()))
                            .msjError(archivo.getMsjError())
                            .build()).collect(Collectors.toList())
            );
        });
        return getResult(response.stream().filter(distinctByKey(Doc::getTipoDocumentoId)).sorted(Comparator.comparing(Doc::getEstadoArchivo)).collect(Collectors.toList()), request.getHeader());
    }

    @Override
    public Object listarDocumentos(GenericRequest<BusquedaRequest> request) {
        BusquedaRequest busqueda = request.getRequest();

        String where = crearQueryListarDocumentos(busqueda);

        if (isNotBlank(busqueda.getFullName()))
            where += " AND UPPER(c.fullName) like '%" + busqueda.getFullName().toUpperCase() + "%'";

        String query = "select c.nroEncuentro, c.pacienteNombre, c.pacienteApellidoPaterno, c.pacienteApellidoMaterno, c.facturaNro, " +
                " c.fechaAtencion, c.sede, c.sedeDesc, c.facturaImporte, c.garanteDescripcion, c.nroLote, c.nroRemesa, c.pacienteTipoDocIdentDesc, " +
                " c.origenDescripcion, c.pacienteNroDocIdent, c.pacienteTipoDocIdentId, c.userName, c.userId from c where 1=1 " + where;


        CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();
        options.setQueryMetricsEnabled(true);

        FeedResponse<Documento> response;
        if (Strings.isBlank(busqueda.getSiguiente()))
            response = cosmosContainer.queryItems(query, options, Documento.class)
                    .iterableByPage(busqueda.getSize()).iterator().next();
        else
            response = cosmosContainer.queryItems(query, options, Documento.class)
                    .iterableByPage(busqueda.getSiguiente(), busqueda.getSize()).iterator().next();

        log.debug(response.getCosmosDiagnostics());

        Paginacion paginacion = Paginacion.builder()
                .siguiente(response.getContinuationToken())
                .totalitems(response.getResults().size())
                .build();

        return getResultPaginacion(response.getResults(), paginacion, request.getHeader());
    }

    @Override
    public Object eliminarDocumentos(GenericRequest<BusquedaRequest> busqueda) {
        documentRepository.deleteAllByNroLote(Integer.parseInt(busqueda.getRequest().getNroLote()));
        return getResult("Documentos eliminados del lote:" + busqueda.getRequest().getNroLote(), busqueda.getHeader());
    }

    private String crearQueryListarDocumentos(BusquedaRequest busqueda) {
        String where = EMPTY;
        if (isNotBlank(busqueda.getNroLoteDesde()) && isNotBlank(busqueda.getNroLoteHasta()))
            where += " AND c.nroLote >= " + busqueda.getNroLoteDesde() + " AND c.nroLote <= " + busqueda.getNroLoteHasta();
        else if (isNotBlank(busqueda.getNroLote()))
            where += " AND c.nroLote=" + busqueda.getNroLote();


        if (isNotBlank(busqueda.getFechaAtencionDesde()) && isNotBlank(busqueda.getFechaAtencionHasta()))
            where += " AND c.fechaAtencion >= " + "'" + busqueda.getFechaAtencionDesde() + "'" + " AND c.fechaAtencion <= " + "'" + busqueda.getFechaAtencionHasta() + "' ";
        else if (isNotBlank(busqueda.getFechaAtencion()))
            where += " AND c.fechaAtencion=" + busqueda.getFechaAtencion();

        if (isNotBlank(busqueda.getNroEncuentro()))
            where += " AND c.nroEncuentro='" + busqueda.getNroEncuentro() + "' ";

        if (isNotBlank(busqueda.getNroRemesa()))
            where += " AND c.nroRemesa=" + busqueda.getNroRemesa();

        if (isNotBlank(busqueda.getGaranteId()))
            where += " AND c.garanteId='" + busqueda.getGaranteId() + "' ";

        if (isNotBlank(busqueda.getFacturaNro()))
            where += " AND c.facturaNro='" + busqueda.getFacturaNro() + "' ";

        if (isNotBlank(busqueda.getImporteOperacion()) && Objects.nonNull(busqueda.getImporteFactura())) {
            if (busqueda.getImporteOperacion().equalsIgnoreCase("01")) {
                where += " AND c.facturaImporte<=" + busqueda.getImporteFactura();
            } else {
                where += " AND c.facturaImporte>=" + busqueda.getImporteFactura();
            }
        }
        return where;
    }

    private List<Archivo> subirArchivos(Documento requestDoc) {

        List<Archivo> archivos = requestDoc.getArchivos().stream()
                .filter(Objects::nonNull)
                .filter(a -> isNotBlank(a.getArchivoBytes()))
                .collect(Collectors.toList());

        archivos.forEach(this::validateExtension);

        archivos.stream().filter(p -> !p.isExiste()).forEach(archivo -> {
            archivo.setUrlArchivoSas(storageService.upload(archivo.getArchivoBytes(), requestDoc.getNroLote(), requestDoc.getFacturaNro(), requestDoc.getNroEncuentro(), archivo.getTipoDocumentoId()));
            archivo.setUrlArchivo(storageService.getUrlWithoutSas(archivo.getUrlArchivoSas()));
            archivo.setArchivoBytes(EMPTY);
            archivo.setExiste(true);
            archivo.setEstadoArchivo(VALUE_OK);
        });

        return GenericUtil.mergeList(archivos, requestDoc.getArchivos());
    }

    private void validateExtension(Archivo file) {

        try {
            String imageDataBytes = file.getArchivoBytes().substring(file.getArchivoBytes().indexOf(",") + 1);
            byte[] bytes = Base64.getDecoder().decode(imageDataBytes);
            final byte[] pdfpattern = new byte[]{0x25, 0x50, 0x44, 0x46, 0x2D};
            if (!isMatch(pdfpattern, bytes))
                throw new DocumentException(String.format("valida que el archivo -> %s <- sea de formato PDF", file.getNombreArchivo()));
            log.info("is-valid-pdf: {}", file.getNombreArchivo());
        } catch (Exception e) {
            throw new DocumentException(String.format("No se pudo decodificar los bytes del pdf, bytes en base64 no válidos , error = %s", e.getMessage()));
        }

    }


    private boolean isMatch(byte[] pattern, byte[] data) {
        if (pattern.length <= data.length) {
            return IntStream.range(0, pattern.length).noneMatch(idx -> pattern[idx] != data[idx]);
        }
        return false;
    }

    private Documento persistirDocumentoReturn(Documento documento) {
        documento.setFullName(String.format("%s %s %s", documento.getPacienteNombre(), documento.getPacienteApellidoPaterno(), documento.getPacienteApellidoMaterno()));
        try {
            return documentRepository.save(documento);
        } catch (CosmosAccessException e) {
            log.error(e.getMessage());
            throw new DocumentException(e.getMessage());
        }
    }

    private void persistirDocumento(Documento documento) {
        documento.setFullName(String.format("%s %s %s", documento.getPacienteNombre(), documento.getPacienteApellidoPaterno(), documento.getPacienteApellidoMaterno()));
        try {
            documentRepository.save(documento);
        } catch (CosmosAccessException e) {
            log.error(e.getMessage());
        }
    }

    private void validarFacturaCompleta(Documento documento) {
        List<Archivo> documentosRequeridos = this.inicializarTipoDocumentoRequerido(documento.getCodigoServicioOrigen(), documento.getMecanismoFacturacionId(),
                documento.getModoFacturacionId(), documento.getGaranteId()).stream().filter(distinctByKey(Archivo::getTipoDocumentoId)).collect(Collectors.toList());
        if (!documentosRequeridos.isEmpty()) {
            List<String> ids = documento.getArchivos().stream().filter(Archivo::isExiste).map(Archivo::getTipoDocumentoId).distinct().collect(Collectors.toList());
            documentosRequeridos = documentosRequeridos.stream().filter(item -> !ids.contains(item.getTipoDocumentoId())).collect(Collectors.toList());

            log.info("Documentos requeridos pendientes: {}", documentosRequeridos.size());
            if (!documentosRequeridos.isEmpty()) {
                var idss = documento.getArchivos().stream().filter(a -> !a.isExiste()).map(Archivo::getTipoDocumentoId).collect(Collectors.toList());
                var add = documentosRequeridos.stream().filter(item -> !idss.contains(item.getTipoDocumentoId())).collect(Collectors.toList());
                documento.getArchivos().addAll(add);
                documentRepository.save(documento);
            }
            clinicalRecordService.actualizarFacturaEstado(documento.getNroLote(), documento.getFacturaNro(), documentosRequeridos.isEmpty() ? ESTADO_FACTURA.COMPLETO : ESTADO_FACTURA.INCOMPLETO);
        }
    }


    @Override
    public List<ResponseSinLote> reporteSinLote(GenericRequest<BusquedaSinLote> genericRequest) {
        BusquedaSinLote request = genericRequest.getRequest();
        List<ResponseSinLote> response = new ArrayList<>();
        String where = EMPTY;
        if (Strings.isNotBlank(request.getFechaDesde()) && Strings.isNotBlank(request.getFechaHasta()))
            where += " AND c.fechaAtencion >= '" + request.getFechaDesde() + "' AND c.fechaAtencion <= '" + request.getFechaHasta() + "' ";
        else if (Strings.isNotBlank(request.getFechaHasta()))
            where += " AND c.fechaAtencion=" + request.getFechaHasta();
        String query = "select c.nroEncuentro, c.fechaAtencion, c.fullName, c.garanteDescripcion, c.pacienteTipoDocIdentDesc, c.pacienteNroDocIdent, c.beneficioDesc as beneficio," +
                " c.beneficioDesc, c.facturaImporte, c.userName from c where 1=1 " + where + " AND c.nroLote = 0 OR c.nroLote = null";
        log.info(query);
        CosmosPagedIterable<ResponseSinLote> result = cosmosContainer.queryItems(query, null, ResponseSinLote.class);
        result.forEach(response::add);
        log.info("tamano-lista: {}", response.size());
        return response;
    }

    public List<Archivo> inicializarTipoDocumentoRequerido(String origenServicio, long mecanismoId, long modofacturacionId, String garanteId) {

        List<Archivo> result = new ArrayList<>();
        TableQuery<Catalogo> query = TableQuery.from(Catalogo.class);
        StringBuilder sb = new StringBuilder();
        sb.append("PartitionKey eq '").append(origenServicio).append("_").append(mecanismoId).append("_").append(modofacturacionId).append("'");
        log.debug(" where {}", sb.toString());
        query.where(sb.toString());
        cloudTable.execute(query).forEach(r -> {
            Archivo archivo = Archivo.builder()
                    .archivoBytes(null)
                    .tipoDocumentoDesc(r.getDescripcion())
                    .tipoDocumentoId(r.getTipodocumentoid())
                    .estadoArchivo(Constants.VALUE_PENDIENTE)
                    .existe(false)
                    .urlArchivo(null).build();

            result.add(archivo);
        });
        return result;
    }

    private void copyDocumento(Documento source, Documento target) {

        log.debug("Actualizacio de lista de archivos cantidad existente {} | cantidad desde integracion {}", source.getArchivos().size(), target.getArchivos().size());

        target.setNroLote(source.getNroLote());
        target.setNroRemesa(source.getNroRemesa());
        target.setFacturaNro(source.getFacturaNro());
        target.setFacturaImporte(source.getFacturaImporte());
        target.setGaranteId(source.getGaranteId());
        target.setPacienteApellidoMaterno(source.getPacienteApellidoMaterno());
        target.setPacienteApellidoPaterno(source.getPacienteApellidoPaterno());
        target.setPacienteNombre(source.getPacienteNombre());
        target.setPacienteNroDocIdent(source.getPacienteNroDocIdent());
        target.setPacienteTipoDocIdentId(source.getPacienteTipoDocIdentId());
        target.setPacienteTipoDocIdentDesc(source.getPacienteTipoDocIdentDesc());
        target.setPeticionHisID(source.getPeticionHisID());
        target.setFechaAtencion(source.getFechaAtencion());
        target.setNroEncuentro(source.getNroEncuentro());
        target.setCodigoServicioOrigen(source.getCodigoServicioOrigen());
        target.setOrigenServicio(source.getOrigenServicio());
        target.setModoFacturacionId(source.getModoFacturacionId());
        target.setMecanismoFacturacionId(source.getMecanismoFacturacionId());
        target.setArchivos(GenericUtil.mergeList(source.getArchivos(), target.getArchivos()));
        target.setOrigenDescripcion(source.getOrigenDescripcion());
        target.setGaranteDescripcion(source.getGaranteDescripcion());

    }

}
