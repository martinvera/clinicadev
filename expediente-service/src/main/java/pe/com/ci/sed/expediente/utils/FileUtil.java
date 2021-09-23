package pe.com.ci.sed.expediente.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.http.HttpStatus;

import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import pe.com.ci.sed.expediente.errors.ExpedienteException;
import pe.com.ci.sed.expediente.persistence.entity.PdfFile;

public class FileUtil {

    public static InputStream unirArchivosPdf(List<InputStream> streamOfPDFFiles, boolean paginate) {

        Document document = new Document();
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            List<PdfReader> readers = new ArrayList<>();
            for (InputStream pdf : streamOfPDFFiles) readers.add(new PdfReader(pdf));

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
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (Exception e) {
            throw new ExpedienteException(e.getLocalizedMessage());
        } finally {
            if (document.isOpen()) document.close();
        }
    }

//    public static byte[] comprimirArchivosZip2(List<Archivo> archivos) {
//        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
//            ZipOutputStream zos = new ZipOutputStream(bos);
//            archivos.forEach(archivo -> agregarArchivoAlZip(archivo.getStream(), archivo.getNombre(), zos));
//            zos.close();
//            return bos.toByteArray();
//        } catch (IOException e) {
//            throw new ExpedienteException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
    
    public static byte[] comprimirArchivosZip(PdfFile archivo) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ZipOutputStream zos = new ZipOutputStream(bos);
            agregarArchivoAlZip(archivo.getStream(), archivo.getNombre(), zos);
            zos.close();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new ExpedienteException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static void agregarArchivoAlZip(InputStream pdfInputStream, String pdfname, ZipOutputStream zos) {
        try {
            zos.putNextEntry(new ZipEntry(pdfname));
            int count;
            byte[] data = new byte[2048];
            BufferedInputStream entryStream = new BufferedInputStream(pdfInputStream, 2048);
            while ((count = entryStream.read(data, 0, 2048)) != -1) {
                zos.write(data, 0, count);
            }
            entryStream.close();
            zos.closeEntry();
        } catch (IOException e) {
            throw new ExpedienteException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
