package pe.com.ci.sed.document.service.impl;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.azure.core.http.rest.PagedIterable;
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
import pe.com.ci.sed.document.persistence.entity.Archivo;
import pe.com.ci.sed.document.persistence.entity.Documento;
import pe.com.ci.sed.document.persistence.entity.GenericDocument;
import pe.com.ci.sed.document.persistence.entity.QueueMessageProcess;
import pe.com.ci.sed.document.property.SedeProperty;
import pe.com.ci.sed.document.service.DocumentoService;
import pe.com.ci.sed.document.service.StorageService;
import pe.com.ci.sed.document.util.Constants;
import pe.com.ci.sed.document.util.Constants.ORIGEN_SISTEMA;
import pe.com.ci.sed.document.util.GenericUtil;

@Log4j2
@Service
public class QueueServiceImpl {

    private final QueueClient queueClient;
    private final QueueClient queueClientError;
    private DocumentoService documentoService;
    private final SalesForceServiceImpl salesForceService;
    private final XhisServiceImpl xhisService;
    private StorageService storageService;
    private final SedeProperty sedeProperty;

    public QueueServiceImpl(QueueClient queueClient, QueueClient queueClientError, DocumentoService documentoService,
                            SalesForceServiceImpl salesForceService, XhisServiceImpl xhisService,
                            StorageServiceImpl storageService, SedeProperty sedeProperty) {
        this.queueClient = queueClient;
        this.queueClientError = queueClientError;
        this.documentoService = documentoService;
        this.salesForceService = salesForceService;
        this.xhisService = xhisService;
        this.storageService = storageService;
        this.sedeProperty = sedeProperty;
    }

    @Scheduled(fixedDelay = 5000)
    public void registrarDocumento() {
        String transactionId = UUID.randomUUID().toString();
        ThreadContext.put("transactionId", transactionId);

        try {
            PagedIterable<QueueMessageItem> listMessage = queueClient.receiveMessages(30, Duration.ofSeconds(60), null, Context.NONE);

            listMessage.stream().parallel().map(this.crearDocumento).map(this.registrarDocumento).forEach(x -> {

                if (x.isError()) {
                    log.debug("Encuentro {} con error, se elimina el mensaje de la cola {}", x.getDocumento().getNroEncuentro(), queueClient.getQueueName());
                    this.eliminarCola(x.getQueueMessageItem());
                    log.debug("Encuentro {} con error, se envía a la cola {}", x.getDocumento().getNroEncuentro(), queueClientError.getQueueName());
                    this.enviarColaError(x.getQueueMessageItem());
                    log.info("Se procesó con error el Encuentro Numero {}", x.getDocumento().getNroEncuentro());

                    log.debug("Encuentro {} , se elimina el archivo {}", x.getDocumento().getNroEncuentro(), x.getUrl());
                    storageService.delete(x.getUrl());

                } else {
                    log.debug("Encuentro {}, se elimina el mensaje de la cola {}", x.getDocumento().getNroEncuentro(), queueClient.getQueueName());
                    this.eliminarCola(x.getQueueMessageItem());
                    log.info("Se procesó con exito el Encuentro Numero {}", x.getDocumento().getNroEncuentro());
                }

                log.info("Fin de procesamiento del documento MessageId: {}", x.getQueueMessageItem().getMessageId());


            });

        } catch (Exception e) {
            log.fatal("El mensaje pudo ser procesado por sistema SED", e);
            log.error("Ocurrió un error en el registro del documento, error = {}", e);
        }
    }

    private void eliminarCola(QueueMessageItem message) {
        queueClient.deleteMessage(message.getMessageId(), message.getPopReceipt());
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

    private Documento procesarMensajeIafas(RegistrarDocRequest documentIafas) {
        Documento temp = this.buildDocumentoFromIAFACD(documentIafas, ORIGEN_SISTEMA.IAFAS);
        temp.getArchivos().addAll(xhisService.generarDocumentosXhis(temp));
        return temp;
    }

    private Documento procesarMensajeCd(RegistrarDocRequest documentCd) {
        Documento temp = this.buildDocumentoFromIAFACD(documentCd, ORIGEN_SISTEMA.CONTROLDOCUMENTARIO);
        temp.getArchivos().addAll(salesForceService.generarDocumentoSalesforce(temp));
        List<List<RegistrarDocRequest.Comprobante>> comprobantes = Arrays.stream(documentCd.getEncuentros())
                .map(RegistrarDocRequest.Detalle::getComprobantes)
                .filter(Objects::nonNull).collect(Collectors.toList());
        if (!comprobantes.isEmpty())
            temp.getArchivos().addAll(xhisService.descargaComprobantes(documentCd.getEncuentros()));
        return temp;
    }

    private Documento procesarMensajeUnilab(RegistrarDocFromUnilabRequest documentUnilab) {
        return this.buildDocumentoFromUnilab(documentUnilab);
    }

    private final Function<QueueMessageItem, QueueMessageProcess> crearDocumento = message -> {
        Documento documento = null;
        String url;

        try {
            GenericDocument<String> genericDocument = GenericUtil.mapper.readValue(message.getBody().toString(), new TypeReference<>() {
            });
            url = genericDocument.getContenido();

            log.info("Inicio de procesamiento del documento MessageId: {} | sistema origen : {}", message.getMessageId(), genericDocument.getSistemaOrigen());
            log.info("body: {}", GenericUtil.writeValueAsString(genericDocument.getContenido()));

            if (genericDocument.getSistemaOrigen().equals(ORIGEN_SISTEMA.UNILAB.name())) {
                RegistrarDocFromUnilabRequest documentUnilab = GenericUtil.mapper.readValue(storageService.download(url), RegistrarDocFromUnilabRequest.class);
                documento = this.procesarMensajeUnilab(documentUnilab);
            }

            if (genericDocument.getSistemaOrigen().equals(ORIGEN_SISTEMA.IAFAS.name())) {
                RegistrarDocRequest documentFromIafas = GenericUtil.mapper.readValue(storageService.download(url), RegistrarDocRequest.class);
                documento = this.procesarMensajeIafas(documentFromIafas);
            }

            if (genericDocument.getSistemaOrigen().equals(ORIGEN_SISTEMA.CONTROLDOCUMENTARIO.name())) {
                RegistrarDocRequest documentFromCd = GenericUtil.mapper.readValue(storageService.download(url), RegistrarDocRequest.class);
                documento = this.procesarMensajeCd(documentFromCd);
            }
            return QueueMessageProcess.builder().error(false).queueMessageItem(message).documento(documento).url(url).build();

        } catch (IOException | DocumentException | BlobStorageException | QueueStorageException | NoSuchElementException e) {
            log.error("Ocurrió un error al leer el mensaje {}, error = {}", message.getMessageId(), e);
            return QueueMessageProcess.builder().error(true).messageError(e.getMessage()).queueMessageItem(message).build();
        }
    };


    private final UnaryOperator<QueueMessageProcess> registrarDocumento = queueMessageProcess -> {

        Documento documento = queueMessageProcess.getDocumento();

        if (Objects.nonNull(documento))
            documentoService.findById(documento.getNroEncuentro())
                    .ifPresentOrElse(
                            documentoExistente -> {
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
                                }

                            }
                    );
        return queueMessageProcess;
    };

    private Documento buildDocumentoFromIAFACD(RegistrarDocRequest factura, ORIGEN_SISTEMA origenSistema) {

        return Stream.of(factura.getEncuentros()).map(encuentro -> {

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
                sede = sedeProperty.getEquivalencias().get(factura.getCoCentro().toUpperCase());
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
                    .beneficioDesc(Optional.ofNullable(encuentro.getDeBeneficio()).map(String::toUpperCase).get())
                    .beneficio(Optional.ofNullable(encuentro.getCoBeneficio()).map(String::toUpperCase).get())
                    .archivos(list)
                    .build();

        }).findFirst().orElseThrow(() -> new DocumentException("Error creando la entidad de documento", HttpStatus.BAD_REQUEST));

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

    private String getSede(String coCentro) {
        String sedeDescripcion = sedeProperty.getSedes().get(coCentro);
        if (Strings.isNotBlank(sedeDescripcion))
            return sedeDescripcion;
        else return sedeProperty.getEquivalencias().get(coCentro);
    }
}
