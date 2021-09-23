package pe.com.ci.sed.expediente.service;

import java.io.InputStream;

public interface ExpedienteStorageService {

    public String upload(byte[] bytes, String nroLote, String nombreZip);

    public InputStream descargarArchivo(String fileName);

    public void eliminarArchivosXLote(Integer nroLote);

    public String getUrlWithSas(String resourceUrl);

    public String getUrlWithoutSas(String resourceUrl);

    public InputStream download(long nroLote);
}
