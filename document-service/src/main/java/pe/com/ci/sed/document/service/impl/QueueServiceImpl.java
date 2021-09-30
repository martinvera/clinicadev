package pe.com.ci.sed.document.service.impl;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.azure.core.http.rest.Response;
import com.azure.core.util.Context;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.models.QueueMessageItem;
import com.azure.storage.queue.models.QueueStorageException;
import com.azure.storage.queue.models.SendMessageResult;
import com.fasterxml.jackson.core.type.TypeReference;

import lombok.extern.log4j.Log4j2;
import pe.com.ci.sed.document.errors.DocumentException;
import pe.com.ci.sed.document.model.request.RegistrarDocFromUnilabRequest;
import pe.com.ci.sed.document.model.request.RegistrarDocRequest;
import pe.com.ci.sed.document.model.request.enterprise.EnterpriseIRequest;
import pe.com.ci.sed.document.persistence.entity.Archivo;
import pe.com.ci.sed.document.persistence.entity.Documento;
import pe.com.ci.sed.document.persistence.entity.DocumentRequest;
import pe.com.ci.sed.document.persistence.entity.QueueMessageProcess;
import pe.com.ci.sed.document.property.SedeProperty;
import pe.com.ci.sed.document.service.DocumentoService;
import pe.com.ci.sed.document.service.EnterpriceImageService;
import pe.com.ci.sed.document.util.Constants;
import pe.com.ci.sed.document.util.Constants.ORIGEN_SISTEMA;
import pe.com.ci.sed.document.util.GenericUtil;

import static pe.com.ci.sed.document.util.GenericUtil.writeValueAsBytes;
import static pe.com.ci.sed.document.util.GenericUtil.writeValueAsString;

@Log4j2
@Service
public class QueueServiceImpl {

    private final QueueClient queueClient;
    private final QueueClient queueClientError;
    private DocumentoService documentoService;
    private final SalesForceServiceImpl salesForceService;
    private final EnterpriceImageService enterpriceImageService;
    private final XhisServiceImpl xhisService;
    private StorageServiceImpl storageService;
    private final SedeProperty sedeProperty;


    public QueueServiceImpl(QueueClient queueClient, QueueClient queueClientError, DocumentoService documentoService,
                            SalesForceServiceImpl salesForceService, EnterpriceImageService enterpriceImageService, XhisServiceImpl xhisService,
                            StorageServiceImpl storageService, SedeProperty sedeProperty) {
        this.queueClient = queueClient;
        this.queueClientError = queueClientError;
        this.documentoService = documentoService;
        this.salesForceService = salesForceService;
        this.enterpriceImageService = enterpriceImageService;
        this.xhisService = xhisService;
        this.storageService = storageService;
        this.sedeProperty = sedeProperty;
    }

    public void procesarDocumentoError() {
        queueClientError.receiveMessages(32).forEach(this::enviarColaEncuentros);
    }

    private void enviarColaEncuentros(QueueMessageItem message) {
        Response<SendMessageResult> result = this.genericEnviarCola(queueClient, message.getBody().toString());
        if (result.getStatusCode() == HttpStatus.CREATED.value()) {
            log.debug("El mensaje fue encolado con exito en la cola {}", queueClient.getQueueName());
            this.eliminarCola(queueClientError, message);
        } else {
            log.debug("Hubo un error");
        }
    }

    private Response<SendMessageResult> genericEnviarCola(QueueClient queueClient, String data) {
        return queueClient.sendMessageWithResponse(data, null, Duration.ofSeconds(-1), null, Context.NONE);
    }

    @Async
    @Scheduled(fixedDelay = 5000)
    public void registrarDocumento() {
        String transactionId = UUID.randomUUID().toString();
        ThreadContext.put("transactionId", transactionId);

        try {
            Optional.ofNullable(queueClient.receiveMessage()).ifPresent(cd -> Stream.ofNullable(cd).map(this.crearDocumento).map(this.registrarDocumento).forEach(x -> {
                if (x.isError()) this.validarRegistrosConError(cd, x);
                else {
                    log.debug("Encuentros {}, se elimina el mensaje de la cola {}", x.getNroEncuentro(), queueClient.getQueueName());
                    log.info("Se procesó con exito el Encuentro Numero {}", x.getNroEncuentro());
                    log.info("Fin de procesamiento del documento MessageId: {}", x.getQueueMessageItem().getMessageId());
                    this.eliminarCola(x.getQueueMessageItem());
                    storageService.delete(x.getUrl());
                }
            }));
        } catch (Exception e) {
            log.fatal("El mensaje no se pudo encolar en el sistema SED", e);
            log.error("Ocurrió un error en el registro del documento, error = {}", e.getMessage());
        }
    }

    private void validarRegistrosConError(QueueMessageItem cd, QueueMessageProcess x) {
        log.info("INTENTO NRO: {}", cd.getDequeueCount());
        if (cd.getDequeueCount() >= 3) {
            log.debug("Encuentro {} con error, se elimina el mensaje de la cola {}", x.getNroEncuentro(), queueClient.getQueueName());
            this.eliminarCola(x.getQueueMessageItem());
            log.debug("Encuentro {} con error, se envía a la cola {}", x.getNroEncuentro(), queueClientError.getQueueName());
            this.enviarColaError(x.getQueueMessageItem());
            log.info("Se procesó con error el Encuentro Numero {}", x.getNroEncuentro());
            log.debug("Encuentro {} , se elimina el archivo {}", x.getNroEncuentro(), x.getUrl());
        } else {
            if (x.getSistemaOrigen().equals(ORIGEN_SISTEMA.UNILAB.name())) {
                RegistrarDocFromUnilabRequest documentUnilab = GenericUtil.readValue(storageService.download(x.getUrl()), RegistrarDocFromUnilabRequest.class);
                this.volverEncolarDocumentoUnilab(documentUnilab, x);
            }
            if (x.getSistemaOrigen().equals(ORIGEN_SISTEMA.IAFAS.name()) || x.getSistemaOrigen().equals(ORIGEN_SISTEMA.CONTROLDOCUMENTARIO.name())) {
                RegistrarDocRequest documentFromIafas = GenericUtil.readValue(storageService.download(x.getUrl()), RegistrarDocRequest.class);
                var encuentros = documentFromIafas.getEncuentros().stream()
                        .filter(d -> x.getEncuentrosError().contains(d.getCoPrestacion())).collect(Collectors.toList());
                documentFromIafas.setEncuentros(encuentros);
                this.volverEncolarDocumentoIafasCtrlDoc(documentFromIafas, x);
            }
        }
    }

    private void volverEncolarDocumentoUnilab(RegistrarDocFromUnilabRequest request, QueueMessageProcess process) {
        DocumentRequest documentRequest = new DocumentRequest();
        String url = storageService.uploadJsonUnilab(writeValueAsBytes(request), process.getSistemaOrigen(), request.getRequest().getCabecera().getNuEncuentro());
        documentRequest.setSistemaOrigen(process.getSistemaOrigen());
        documentRequest.setContenido(url);

        Response<SendMessageResult> result = queueClient.sendMessageWithResponse(writeValueAsString(documentRequest), null, Duration.ofSeconds(-1), null, Context.NONE);
        if (result.getStatusCode() == HttpStatus.CREATED.value()) {
            log.info("Mensaje de unilab encolado con exito, messageId {}", result.getValue().getMessageId());
            this.eliminarCola(process.getQueueMessageItem());
            storageService.delete(process.getUrl());
        }
    }

    private void volverEncolarDocumentoIafasCtrlDoc(RegistrarDocRequest p, QueueMessageProcess process) {
        DocumentRequest documentRequest = new DocumentRequest();
        String url = storageService.uploadJsonFileIafasCtrlDoc(writeValueAsBytes(p), process.getSistemaOrigen(), p.getNuLote(), p.getNuDocPago());
        documentRequest.setSistemaOrigen(process.getSistemaOrigen());
        documentRequest.setContenido(url);

        Response<SendMessageResult> result = queueClient.sendMessageWithResponse(writeValueAsString(documentRequest), null, Duration.ofSeconds(-1), null, Context.NONE);
        if (result.getStatusCode() == HttpStatus.CREATED.value()) {
            log.info("La factura fue encolada con exito, numero factura = {} , numero de lote = {}, messageId = {}", p.getNuDocPago(), p.getNuLote(), result.getValue().getMessageId());

            this.eliminarCola(process.getQueueMessageItem());
            storageService.delete(process.getUrl());
        }
    }

    private void eliminarCola(QueueMessageItem message) {
        Optional.ofNullable(message).ifPresent(m -> queueClient.deleteMessage(m.getMessageId(), m.getPopReceipt()));
    }

    private void eliminarCola(QueueClient queueClient, QueueMessageItem message) {
        Optional.ofNullable(message).ifPresent(m -> queueClient.deleteMessage(m.getMessageId(), m.getPopReceipt()));
    }

    private void enviarColaError(QueueMessageItem message) {
        log.error("Se envia la trama {} a la cola de error {}", message.getBody().toString(), queueClientError.getQueueName());
        Response<SendMessageResult> result;
        result = queueClientError.sendMessageWithResponse(message.getBody().toString(), null, Duration.ofSeconds(-1), null, Context.NONE);
        if (result.getStatusCode() == HttpStatus.CREATED.value()) {
            log.debug("Mensaje {} encolado en {}", message.getBody(), queueClientError.getQueueName());
        } else {
            log.debug("Hubo un error");
        }
    }

    /**
     * @param message: Mensaje recibido de la cola <queuedocumetstorage>
     *
     * Obtiene trama JSON de la cola <queuedocumentstorage> el cual en el campo <contenido>
     * que contiene la URL de la trama JSON enviado por el cliente almacenada en el BlobStorage
     * Obtiene la URL y descarga el archivo ejecutando el método <storageService.download(url)>
     * */
    private final Function<QueueMessageItem, QueueMessageProcess> crearDocumento = message -> {
        List<Documento> documentos = new ArrayList<>();
        DocumentRequest documentRequest = GenericUtil.readValue(message.getBody().toString(), new TypeReference<>() {
        });
        String url = documentRequest.getContenido();
        try {
            log.info("Inicio de procesamiento del documentos MessageId: {} | sistema origen : {}", message.getMessageId(), documentRequest.getSistemaOrigen());
            log.info("body: {}", documentRequest.getContenido());

            if (documentRequest.getSistemaOrigen().equals(ORIGEN_SISTEMA.UNILAB.name())) {
                RegistrarDocFromUnilabRequest documentUnilab = GenericUtil.mapper.readValue(storageService.download(url), RegistrarDocFromUnilabRequest.class);
                documentos = List.of(this.buildDocumentoFromUnilab(documentUnilab));
            }

            if (documentRequest.getSistemaOrigen().equals(ORIGEN_SISTEMA.IAFAS.name())) {
                RegistrarDocRequest documentFromIafas = GenericUtil.mapper.readValue(storageService.download(url), RegistrarDocRequest.class);
                documentos = this.procesarMensajeIafas(documentFromIafas);
            }

            if (documentRequest.getSistemaOrigen().equals(ORIGEN_SISTEMA.CONTROLDOCUMENTARIO.name())) {
                RegistrarDocRequest documentFromCd = GenericUtil.mapper.readValue(storageService.download(url), RegistrarDocRequest.class);
                documentos = this.procesarMensajeCd(documentFromCd);
            }
            if (documentRequest.getSistemaOrigen().equals(ORIGEN_SISTEMA.ENTERPRISEIMAGING.name())) {
                EnterpriseIRequest documentFromEi = GenericUtil.mapper.readValue(storageService.download(url), EnterpriseIRequest.class);
                documentos = List.of(this.buildDocumentoFromEImaging(documentFromEi));
            }
            return QueueMessageProcess.builder().error(false)
                    .sistemaOrigen(documentRequest.getSistemaOrigen())
                    .queueMessageItem(message)
                    .nroEncuentro(documentos.stream().map(Documento::getNroEncuentro).collect(Collectors.joining(",")))
                    .documentos(documentos).url(url).build();

        } catch (IOException | DocumentException | BlobStorageException | QueueStorageException | NoSuchElementException e) {
            log.error("Ocurrió un error al leer el mensaje {}, error = {}", message.getMessageId(), e);
            return QueueMessageProcess.builder()
                    .queueMessageItem(message)
                    .sistemaOrigen(documentRequest.getSistemaOrigen())
                    .url(url)
                    .nroEncuentro(documentos.stream().map(Documento::getNroEncuentro).collect(Collectors.joining(",")))
                    .error(true)
                    .messageError(e.getMessage())
                    .build();
        }
    };

    private List<Documento> procesarMensajeIafas(RegistrarDocRequest documentIafas) {
        List<Documento> temps = this.buildDocumentoFromIAFACD(documentIafas, ORIGEN_SISTEMA.IAFAS);
        return temps.stream().peek(temp -> temp.getArchivos().addAll(xhisService.generarDocumentosXhis(temp))).collect(Collectors.toList());
    }

    private List<Documento> procesarMensajeCd(RegistrarDocRequest documentCd) {
        List<Documento> temps = this.buildDocumentoFromIAFACD(documentCd, ORIGEN_SISTEMA.CONTROLDOCUMENTARIO);
        return temps.stream().peek(temp -> {
            temp.getArchivos().addAll(salesForceService.generarDocumentoSalesforce(temp));
            List<List<RegistrarDocRequest.Comprobante>> comprobantes = documentCd.getEncuentros().stream()
                    .map(RegistrarDocRequest.Detalle::getComprobantes)
                    .filter(Objects::nonNull).collect(Collectors.toList());
            if (!comprobantes.isEmpty())
                temp.getArchivos().addAll(xhisService.descargaComprobantes(documentCd.getEncuentros()));
        }).collect(Collectors.toList());
    }


    private final UnaryOperator<QueueMessageProcess> registrarDocumento = queueMessageProcess -> {
        if (!queueMessageProcess.isError())
            queueMessageProcess.getDocumentos().stream().filter(Objects::nonNull).forEach(documento -> documentoService.findById(documento.getNroEncuentro())
                    .ifPresentOrElse(documentoExistente -> {
                                log.debug("Encuentro {} existente", documentoExistente.getNroEncuentro());
                                try {
                                    documentoService.delete(documentoExistente);
                                    log.debug("Se eliminó el encuentro sin lote, encuentro {}", documentoExistente.getNroEncuentro());

                                    documentoService.modificarDocumentoIntegracion(documentoExistente, documento);
                                    log.debug("Se creó el nuevamente el encuentro Encuentro {} actualizado", documento.getNroEncuentro());

                                } catch (Exception e1) {
                                    log.error("Error al actualizar el documento, error {}", e1.getMessage());
                                    queueMessageProcess.setError(true);
                                    queueMessageProcess.setMessageError(e1.getMessage());
                                    queueMessageProcess.getEncuentrosError().add(documento.getNroEncuentro());
                                }
                            },
                            () -> {
                                try {
                                    log.debug("Se creará el encuentro {} ", documento.getNroEncuentro());
                                    documentoService.registrarDocumentoIntegracion(documento);
                                    log.debug("Encuentro {} creado", documento.getNroEncuentro());
                                } catch (Exception e2) {
                                    log.error("Error al insertar el documento en cosmos db, error {}", e2.getMessage());
                                    queueMessageProcess.setError(true);
                                    queueMessageProcess.setMessageError(e2.getMessage());
                                    queueMessageProcess.getEncuentrosError().add(documento.getNroEncuentro());
                                }

                            }
                    ));
        return queueMessageProcess;
    };

    private List<Documento> buildDocumentoFromIAFACD(RegistrarDocRequest factura, ORIGEN_SISTEMA origenSistema) {
        return factura.getEncuentros().stream().map(encuentro -> {
            if(origenSistema.name().equals(ORIGEN_SISTEMA.IAFAS.name())){
                encuentro.setCoServicio(sedeProperty.getEquivalenciaOrigen().get(factura.getTiEpisodioXhis()));
                encuentro.setDeServicio(sedeProperty.getOrigen().get(encuentro.getCoServicio()));
            }else if(origenSistema.name().equals(ORIGEN_SISTEMA.CONTROLDOCUMENTARIO.name())){
                encuentro.setCoServicio(factura.getTiEpisodioXhis());
                encuentro.setDeServicio(sedeProperty.getOrigen().get(encuentro.getCoServicio()));
            }
            List<Archivo> list = documentoService.inicializarTipoDocumentoRequerido(encuentro.getCoServicio(), factura.getCoMecaPago(), factura.getCoSubMecaPago(), factura.getCoGarante());
            String sede = Strings.EMPTY;
            String sedeDesc = Strings.EMPTY;
            if (ORIGEN_SISTEMA.IAFAS.name().equalsIgnoreCase(origenSistema.name())) {
                Archivo archivo = Archivo.builder()
                        .archivoBytes(encuentro.getPdfHojaAutorizacion())
                        .tipoDocumentoDesc(encuentro.getTipoDocumentoDesc())
                        .tipoDocumentoId(encuentro.getTipoDocumentoId())
                        .build();
                list.add(archivo);
                sede = sedeProperty.getEquivalenciaSede().get(factura.getCoCentro().toUpperCase());
                sedeDesc = this.getSede(factura.getCoCentro().toUpperCase());
            } else if (origenSistema.name().equals(Constants.ORIGEN_SISTEMA.CONTROLDOCUMENTARIO.name())) {
                sede = factura.getCoEstru();
                sedeDesc = factura.getCoEstru();
            }

            return Documento.builder()
                    .nroLote(factura.getNuLote())
                    .nroRemesa(factura.getNuRemesa())
                    .peticionHisID(factura.getNuNhcPaciente())
                    .facturaNro(factura.getNuDocPago().toUpperCase())
                    .facturaImporte(factura.getVaMontoFact())
                    .garanteId(factura.getCoGarante())
                    .pacienteApellidoMaterno(factura.getApeMatPaciente())
                    .pacienteApellidoPaterno(factura.getApePatPaciente())
                    .pacienteNombre(factura.getVaNoPaciente())
                    .pacienteNroDocIdent(factura.getNuDocPaciente())
                    .pacienteTipoDocIdentId(factura.getTiDocPaciente())
                    .pacienteTipoDocIdentDesc(factura.getDeTiDocPaciente())
                    .sede(sede)
                    .sedeDesc(sedeDesc)
                    .fechaAtencion(encuentro.getFePrestacion())
                    .nroEncuentro(encuentro.getCoPrestacion())
                    .origenServicio(encuentro.getDeServicio())
                    .codigoServicioOrigen(encuentro.getCoServicio())
                    .beneficio(encuentro.getCoBeneficio())
                    .beneficioDesc(encuentro.getDeBeneficio())
                    .mecanismoFacturacionId(factura.getCoMecaPago())
                    .modoFacturacionId(factura.getCoSubMecaPago())
                    .origenDescripcion(origenSistema.name())
                    .garanteDescripcion(factura.getNoGarante())
                    .beneficioDesc(Optional.ofNullable(encuentro.getDeBeneficio()).map(String::toUpperCase).orElse(null))
                    .beneficio(Optional.ofNullable(encuentro.getCoBeneficio()).map(String::toUpperCase).orElse(null))
                    .archivos(list)
                    .build();

        }).collect(Collectors.toList());

    }

    private String getSede(String coCentro) {
        String sedeDescripcion = sedeProperty.getSedes().get(coCentro);
        if (Strings.isNotBlank(sedeDescripcion))
            return sedeDescripcion;
        else return sedeProperty.getEquivalenciaSede().get(coCentro);
    }

    public Documento buildDocumentoFromUnilab(RegistrarDocFromUnilabRequest unilabCreateRequest) {
        Documento documento = new Documento();
        try {
            documento.setPeticionHisID(unilabCreateRequest.getRequest().getCabecera().getNuHclinicapac());
            documento.setPacienteApellidoPaterno(unilabCreateRequest.getRequest().getCabecera().getApePaternoPac());
            documento.setPacienteNombre(unilabCreateRequest.getRequest().getCabecera().getNomPac());
            documento.setPacienteApellidoMaterno(unilabCreateRequest.getRequest().getCabecera().getApeMaternoPac());
            documento.setSexoPaciente(unilabCreateRequest.getRequest().getCabecera().getCoSexoPac());
            documento.setPeticionHisID(unilabCreateRequest.getRequest().getCabecera().getIdPeticionHis());
            documento.setPeticionLisID(unilabCreateRequest.getRequest().getCabecera().getIdPeticionLis());
            documento.setFechaTransaccion(GenericUtil.toDate(unilabCreateRequest.getRequest().getCabecera().getFeTrx(), Constants.UNILAB_FECHA_TRX));
            documento.setNroEncuentro(unilabCreateRequest.getRequest().getCabecera().getNuEncuentro());
            documento.setPacienteTipoDocIdentDesc(unilabCreateRequest.getRequest().getCabecera().getTiDocumentoPac());
            documento.setPacienteNroDocIdent(unilabCreateRequest.getRequest().getCabecera().getNuDocumentoPac());
            documento.setSede(unilabCreateRequest.getRequest().getCabecera().getCoSede());
            documento.setSedeDesc(this.getSede(unilabCreateRequest.getRequest().getCabecera().getCoSede().replaceAll("^0+", "")));
            documento.setFacturaNro("0");
            documento.setNroLote(0);
            documento.setOrigenDescripcion(ORIGEN_SISTEMA.UNILAB.name());
            Archivo archivo = new Archivo();
            archivo.setArchivoBytes(unilabCreateRequest.getRequest().getCabecera().getResultadoPdf());
            archivo.setNroEncuentro(documento.getNroEncuentro());
            archivo.setTipoDocumentoId(unilabCreateRequest.getRequest().getCabecera().getTipoDocumentoId());
            archivo.setTipoDocumentoDesc(unilabCreateRequest.getRequest().getCabecera().getTipoDocumentoDesc());
            documento.setArchivos(new ArrayList<>());
            documento.getArchivos().add(archivo);
        } catch (Exception e) {
            throw new DocumentException("Error creando la entidad de documento para unilab", HttpStatus.BAD_REQUEST);
        }
        return documento;
    }

    /**
     * @param request: trama enviada por la integración  POST <v1/integrator/enterpriseimaging>
     * @return Documento con los campos requeridos y lista de archivos descargados desde el File Server
     * @exception DocumentException
     *
     * Crea el documento con los datos enviados desde la trama
     * Ejecuta el método <enterpriseImageService.obtenerInformeImages> para obtener los archivos
     * PDF desde el File Server
     * */
    public Documento buildDocumentoFromEImaging(EnterpriseIRequest request) {
        Documento documento = new Documento();
        try {
            documento.setCodigoApp(request.getCoOrigenApp());
            documento.setPacienteNroDocIdent(request.getNuDocumentoPac());
            documento.setPacienteTipoDocIdentId(request.getTiDocumentoPac());
            documento.setPacienteApellidoPaterno(request.getApePaternoPac());
            documento.setPacienteNombre(request.getNomPac());
            documento.setPacienteApellidoMaterno(request.getApeMaternoPac());
            documento.setSexoPaciente(request.getCoSexoPac());
            documento.setCodigoServicioOrigen(request.getCoServOrigen());
            documento.setCodigoCamaPaciente(request.getCoCamaPac());
            documento.setSede(request.getCoSede());
            documento.setCodigoCMP(request.getCoCmp());
            documento.setCodigoServDestino(request.getCoServDestino());
            documento.setDescripServDestino(request.getDeServDestino());
            documento.setMecanismoFacturacionId(Integer.parseInt(request.getCoMecaPago()));
            documento.setNroEncuentro(request.getNuEncuentro());
            documento.setPeticionHisID(request.getIdPeticionHis());
            documento.setGaranteDescripcion(request.getNoGarante());
            documento.setOrigenDescripcion(ORIGEN_SISTEMA.ENTERPRISEIMAGING.name());
            documento.setArchivos(new ArrayList<>());
            documento.getArchivos().add(enterpriceImageService.obtenerInformeImagenes(request));
        } catch (Exception e) {
            throw new DocumentException("Error creando la entidad de documento para unilab", HttpStatus.BAD_REQUEST);
        }
        return documento;
    }
}
