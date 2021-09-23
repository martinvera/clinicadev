package pe.com.ci.sed.reporte.service;

import java.io.InputStream;
import java.util.Map;

public interface StorageService {

    public Map<String, String> uploadExcel(byte[] bytes, String nombreReporte);

    public Map<String, String> uploadExcel(byte[] bytes, String nombreReporte, String garante);

    public void delete(String url);

    public InputStream download(String url);
    
    public String getUrlWithSas(String resourceUrl);
    
    public String getUrlWithoutSas(String resourceUrl);
}
