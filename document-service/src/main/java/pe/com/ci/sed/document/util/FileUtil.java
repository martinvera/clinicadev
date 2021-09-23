package pe.com.ci.sed.document.util;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import pe.com.ci.sed.document.errors.DocumentException;
import pe.com.ci.sed.document.persistence.entity.Archivo;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static pe.com.ci.sed.document.util.Constants.PDF_FILE_EXT;
import static pe.com.ci.sed.document.util.Constants.VALUE_ERROR;
import static pe.com.ci.sed.document.util.Constants.VALUE_OK;

@Log4j2
public class FileUtil {

    private FileUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static byte[] generarReporte(String json, Map<String, Object> parameters, String plantilla) {
        String template = "templates/" + plantilla + ".jasper";
        try {
            ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(json.getBytes());
            JRDataSource jsonDataSource = new JsonDataSource(jsonDataStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(loadTemplate(template), parameters, jsonDataSource);
//            JasperExportManager.exportReportToPdfFile(jasperPrint, plantilla.split("/")[1] + PDF_FILE_EXT);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            log.error("Ocurrio un error al generar el reporte = {} | error = {}", template, e);
            return new byte[0];
        }
    }

    private static JasperReport loadTemplate(String template) throws JRException {
        log.info(String.format("Compile template path : %s", template));
        try (final InputStream reportInputStream = new ClassPathResource(template).getInputStream()) {
            return (JasperReport) JRLoader.loadObject(reportInputStream);
        } catch (IOException e) {
            log.error("Ocurrio un error al cargar la plantilla = {}", template);
            throw new DocumentException(e.getMessage());
        }
    }

    public static Archivo getArchivo(boolean iserror, TipoDocXhis tipodoc, String error, String base64, String nroEncuentro) {
        return Archivo.builder()
                .nroEncuentro(nroEncuentro)
                .error(iserror)
                .estadoArchivo(iserror ? VALUE_ERROR : VALUE_OK)
                .tipoDocumentoId(tipodoc.getCodigo())
                .tipoDocumentoDesc(tipodoc.getNombre())
                .nombreArchivo(tipodoc.getNombre() + PDF_FILE_EXT)
                .msjError(error)
                .archivoBytes(base64)
                .build();
    }

    public static Archivo getArchivo(boolean iserror, TipoDocSalesforce tipodoc, String error, String base64, String nroEncuentro) {
        return Archivo.builder()
                .nroEncuentro(nroEncuentro)
                .error(iserror)
                .estadoArchivo(iserror ? VALUE_ERROR : VALUE_OK)
                .tipoDocumentoId(tipodoc.getCodigo())
                .tipoDocumentoDesc(tipodoc.getNombre())
                .nombreArchivo(tipodoc.getNombre() + PDF_FILE_EXT)
                .msjError(error)
                .archivoBytes(base64)
                .build();
    }

    public static Archivo getArchivoByte(boolean iserror, TipoDocSalesforce tipodoc, String error, byte[] bytes, String nroEncuentro) {
        return Archivo.builder()
                .nroEncuentro(nroEncuentro)
                .error(iserror)
                .estadoArchivo(iserror ? VALUE_ERROR : VALUE_OK)
                .tipoDocumentoId(tipodoc.getCodigo())
                .tipoDocumentoDesc(tipodoc.getNombre())
                .nombreArchivo(tipodoc.getNombre() + PDF_FILE_EXT)
                .msjError(error)
                .bytes(bytes)
                .build();
    }

    public static String getJson(Object object) {
        GsonBuilder gson = new GsonBuilder().setPrettyPrinting();
        return gson.create().toJson(object);
    }

    public static Map<String, Object> getJsonMap(Object object) {
        try {
            GsonBuilder gson = new GsonBuilder().setPrettyPrinting();
            String string = gson.create().toJson(object);
            return new ObjectMapper().readValue(string.getBytes(StandardCharsets.UTF_8), new TypeReference<>() {
            });
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        return new HashMap<>();
    }

    public static Map<String, Object> getMap(Object object) {
        return new ObjectMapper().convertValue(object, new TypeReference<>() {
        });
    }

    public static String unirArchivosPdf(byte[] bytes) {
        List<Archivo> archivos = List.of(Archivo.builder().bytes(bytes).build());
        return unirArchivosPdf(archivos, true);
    }

    public static String unirArchivosPdf(List<Archivo> bytePdfFiles, boolean paginate) {
        Document document = new Document();
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            List<PdfReader> readers = new ArrayList<>();
            for (Archivo pdf : bytePdfFiles) readers.add(new PdfReader(pdf.getBytes()));

            PdfWriter writer = PdfWriter.getInstance(document, outputStream);

            document.open();
            PdfContentByte cb = writer.getDirectContent();

            PdfImportedPage page;
            int pageOfCurrentReaderPDF = 0;

            for (PdfReader pdfReader : readers) {
                while (pageOfCurrentReaderPDF < pdfReader.getNumberOfPages()) {
                    Rectangle rectangle = pdfReader.getPageSizeWithRotation(1);
                    document.setPageSize(rectangle);
                    document.newPage();

                    pageOfCurrentReaderPDF++;
                    page = writer.getImportedPage(pdfReader, pageOfCurrentReaderPDF);
                    switch (rectangle.getRotation()) {
                        case 0:
                            cb.addTemplate(page, 1f, 0, 0, 1f, 0, 0);
                            break;
                        case 90:
                            cb.addTemplate(page, 0, -1f, 1f, 0, 0, pdfReader.getPageSizeWithRotation(1).getHeight());
                            break;
                        case 180:
                            cb.addTemplate(page, -1f, 0, 0, -1f, 0, 0);
                            break;
                        case 270:
                            cb.addTemplate(page, 0, 1.0F, -1.0F, 0, pdfReader.getPageSizeWithRotation(1).getWidth(), 0);
                            break;
                        default:
                            break;
                    }
                    if (paginate) {
                        cb.beginText();
                        cb.getPdfDocument().getPageSize();
                        cb.endText();
                    }
                }
                pageOfCurrentReaderPDF = 0;
            }
            outputStream.flush();
            document.close();
            return Base64.encodeBase64String(outputStream.toByteArray());
        } catch (Exception e) {
            throw new DocumentException(e.getLocalizedMessage());
        } finally {
            if (document.isOpen()) document.close();
        }
    }

    public static String getFirma(String urlFirma) {
        byte[] firma = downloadFromUrl(urlFirma);
        if (firma.length > 0)
            return java.util.Base64.getEncoder().encodeToString(firma);
        return EMPTY;
    }

    public static byte[] downloadFromUrl(String url) {
        if (isNotBlank(url))
            try {
                URL urlFile = new URL(url);
                try (InputStream is = urlFile.openStream()) {
                    return is.readAllBytes();
                }
            } catch (IOException e) {
                log.error("No se pudo obtener la firma del mendico : {}", url);
            }
        return new byte[0];
    }

}
