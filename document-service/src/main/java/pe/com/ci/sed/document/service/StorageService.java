package pe.com.ci.sed.document.service;

public interface StorageService {

    public String upload(String bytes, long lote, String facturaNumero, String numeroEncuentro, String codigoTipoDocumento); 
    
    public void delete(String fileName);
    
    public String getUrlWithoutSas(String resourceUrl);
    
    public String getFileName(String facturaNumero, String numeroEncuentro, String codigoTipoDocumento);
    
    public String getPath(long lote, String facturaNumero, String numeroEncuentro, String filename);
    
    public String moveBlob( String urlFrom, String urlTo);
    
    public String getUrlWithSas(String resourceUrl);
}
