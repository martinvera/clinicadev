package pe.com.ci.sed.reporte.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pe.com.ci.sed.reporte.errors.ReporteException;
import pe.com.ci.sed.reporte.model.request.GestionLote;
import pe.com.ci.sed.reporte.model.request.RequestReporte;
import pe.com.ci.sed.reporte.model.response.ResponseExpError;
import pe.com.ci.sed.reporte.model.response.ResponseMecYMod;
import pe.com.ci.sed.reporte.model.response.ResponseSinLote;
import pe.com.ci.sed.reporte.model.response.ResponseTP;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

public class ReporteUtil {
    private ReporteUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static <T> byte[] genericCrearReporte(TipoReporte tipoReporte, List<T> items) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(tipoReporte.getNombre());
            Row roww = sheet.createRow(0);
            crearCabecerasReporte(sheet, roww, workbook, tipoReporte.getCabeceras());
            if (tipoReporte.getNombre().equals(TipoReporte.EXPEDIENTECONERROR.getNombre())) {
                List<ResponseExpError> respuesta = new ObjectMapper().convertValue(items, new TypeReference<>() {
                });
                crearContenidoExpError(respuesta, sheet);
            } else if (tipoReporte.getNombre().equals(TipoReporte.EXPEDIENTEMECANISMO.getNombre())) {
                List<ResponseMecYMod> respuesta = new ObjectMapper().convertValue(items, new TypeReference<>() {
                });
                crearContenidoMecYMod(respuesta, sheet);
            } else if (tipoReporte.getNombre().equals(TipoReporte.ENCUENTROSINLOTE.getNombre())) {
                List<ResponseSinLote> respuesta = new ObjectMapper().convertValue(items, new TypeReference<>() {
                });
                crearContenidoSinLote(respuesta, sheet);
            } else if (Arrays.asList(TipoReporte.PARCIAL.getNombre(), TipoReporte.TOTAL.getNombre()).contains(tipoReporte.getNombre())) {
                List<ResponseTP> respuesta = new ObjectMapper().convertValue(items, new TypeReference<>() {
                });
                crearContenidoParcial(respuesta, sheet);
            } else if (tipoReporte.getNombre().equals(TipoReporte.ENVIADOGARANTE.getNombre())) {
                List<GestionLote> respuesta = new ObjectMapper().convertValue(items, new TypeReference<>() {
                });
                crearContenidoLotesEnviados(respuesta, sheet);
            }
            workbook.write(output);
            return output.toByteArray();
        } catch (Exception ex) {
            throw new ReporteException(ex.getMessage());
        }
    }

    private static void crearContenidoExpError(List<ResponseExpError> items, Sheet sheet) {
        int filaindex = 1;
        for (ResponseExpError item : items) {
            Row row = sheet.createRow(filaindex);
            row.createCell(0).setCellValue(item.getNroLote());
            row.createCell(1).setCellValue(item.getFacturaNro());
            row.createCell(2).setCellValue(item.getFechaCreacion());
            row.createCell(3).setCellValue(item.getPaciente());
            row.createCell(4).setCellValue(item.getGaranteDescripcion());
            row.createCell(5).setCellValue(item.getPacienteTipoDocIdentDesc());
            row.createCell(6).setCellValue(item.getPacienteNroDocIdent());
            row.createCell(7).setCellValue(item.getBeneficio());
            row.createCell(8).setCellValue(item.getMsjError());
            filaindex++;
        }
    }

    private static void crearContenidoMecYMod(List<ResponseMecYMod> items, Sheet sheet) {
        int filaindex = 1;
        for (ResponseMecYMod item : items) {
            Row row = sheet.createRow(filaindex);
            row.createCell(0).setCellValue(item.getMecanismoFacturacionDesc());
            row.createCell(1).setCellValue(item.getModoFacturacion());
            row.createCell(2).setCellValue(item.getCantidad());
            row.createCell(3).setCellValue(item.getImporte());
            filaindex++;
        }
    }

    private static void crearContenidoSinLote(List<ResponseSinLote> items, Sheet sheet) {
        int filaindex = 1;
        for (ResponseSinLote item : items) {
            Row row = sheet.createRow(filaindex);
            row.createCell(0).setCellValue(item.getNroEncuentro());
            row.createCell(1).setCellValue(item.getFechaAtencion());
            row.createCell(2).setCellValue(item.getFullName());
            row.createCell(3).setCellValue(item.getGaranteDescripcion());
            row.createCell(4).setCellValue(item.getPacienteTipoDocIdentDesc());
            row.createCell(5).setCellValue(item.getPacienteNroDocIdent());
            row.createCell(6).setCellValue(item.getBeneficioDesc());
            row.createCell(7).setCellValue(item.getFacturaImporte());
            row.createCell(8).setCellValue(item.getUserName());
            filaindex++;
        }
    }

    private static void crearContenidoParcial(List<ResponseTP> items, Sheet sheet) {
        int filaindex = 1;
        for (ResponseTP item : items) {
            Row row = sheet.createRow(filaindex);
            row.createCell(0).setCellValue(item.getNroLote());
            row.createCell(1).setCellValue(item.getFacturaNro());
            row.createCell(2).setCellValue(item.getNrosEncuentro());
            row.createCell(3).setCellValue(item.getPaciente());
            row.createCell(4).setCellValue(item.getGaranteDescripcion());
            row.createCell(5).setCellValue(item.getFechaAtencion());
            row.createCell(6).setCellValue(item.getFechaAtencion());
            row.createCell(7).setCellValue(item.getFacturaImporte());
            row.createCell(8).setCellValue(item.getModoFacturacion());
            row.createCell(9).setCellValue(item.getMecanismoFacturacionDesc());
            row.createCell(10).setCellValue(item.getEstadoFactura());
            row.createCell(11).setCellValue(item.getEstado());
            row.createCell(12).setCellValue(item.getFechaAceptacion());
            filaindex++;
        }

    }

    private static void crearContenidoLotesEnviados(List<GestionLote> items, Sheet sheet) {
        int filaindex = 1;
        for (GestionLote item : items) {
            Row row = sheet.createRow(filaindex);
            row.createCell(0).setCellValue(item.getNroLote());
            row.createCell(1).setCellValue(item.getGaranteDescripcion());
            row.createCell(2).setCellValue(item.getFechaLote());
            row.createCell(3).setCellValue(item.getFacturasGeneradas());
            row.createCell(4).setCellValue(item.getFechaEnvio());
            row.createCell(5).setCellValue(item.getFechaRechazo());
            row.createCell(6).setCellValue(item.getEstadoGarante());
            row.createCell(7).setCellValue(item.getFechaAceptacion());
            row.createCell(8).setCellValue(item.getRegistroSolicitud());
            filaindex++;
        }
    }

    private static void crearCabecerasReporte(Sheet sheetDocumento, Row roww, Workbook workbook, String[] cabeceras) {
        Font fontcabecera = workbook.createFont();
        CellStyle stylecabecera = workbook.createCellStyle();
        fontcabecera.setBold(true);
        fontcabecera.setFontHeightInPoints((short) 13);

        stylecabecera.setFont(fontcabecera);
        stylecabecera.setBorderBottom(BorderStyle.THIN);
        stylecabecera.setBorderLeft(BorderStyle.THIN);
        stylecabecera.setBorderRight(BorderStyle.THIN);
        stylecabecera.setBorderTop(BorderStyle.THIN);

        for (int i = 0; i < cabeceras.length; i++) {
            Cell cell = roww.createCell(i);
            cell.setCellValue(cabeceras[i]);
            cell.setCellStyle(stylecabecera);
        }
        for (int i = 0; i < cabeceras.length; i++) {
            sheetDocumento.autoSizeColumn(i);
        }
    }

    public static String getFiltros(RequestReporte request) {
        StringBuilder sb = new StringBuilder();
        if (Strings.isNotBlank(request.getTipoReporte())) {
            sb.append("Tipo Reporte: ").append(request.getTipoReporte()).append(" \n ");
        }
        if (Strings.isNotBlank(request.getFechaDesde())) {
            sb.append("Fecha Desde: ").append(request.getFechaDesde()).append(" \n ");
        }
        if (Strings.isNotBlank(request.getFechaHasta())) {
            sb.append("Fecha Hasta: ").append(request.getFechaHasta()).append(" \n ");
        }
        if (Strings.isNotBlank(request.getMesanio())) {
            sb.append("Mes-Año: ").append(request.getMesanio()).append(" \n ");
        }
        if (Strings.isNotBlank(request.getNroLote())) {
            sb.append("Nro. Lote: ").append(request.getNroLote()).append(" \n ");
        }
        if (Strings.isNotBlank(request.getGaranteDescripcion())) {
            sb.append("Garante: ").append(request.getGaranteDescripcion()).append(" \n ");
        }
        if (Strings.isNotBlank(request.getMecanismoFacturacionDesc())) {
            sb.append("Mecanismo Facturación: ").append(request.getMecanismoFacturacionDesc()).append(" \n ");
        }
        if (Strings.isNotBlank(request.getEstado())) {
            sb.append("Estado: ").append(request.getEstado()).append(" \n ");
        }
        return sb.toString();
    }
}
