package pe.com.ci.sed.expediente.service;

import java.io.InputStream;
import java.util.Map;

public interface StorageService {

    public String upload(byte[] bytes, String nroLote, String nombreZip);

    public InputStream descargarArchivo(String fileName);

    public void eliminarArchivosXLote(Integer nroLote);

    public Map<String, String> uploadeExcel(byte[] bytes, String nombreReporte);
}
