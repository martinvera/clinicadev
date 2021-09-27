package pe.com.ci.sed.document.service;

import pe.com.ci.sed.document.model.request.enterprise.EnterpriseIRequest;
import pe.com.ci.sed.document.persistence.entity.Archivo;

public interface EnterpriceImageService {
    public Archivo obtenerInformeImagenes(EnterpriseIRequest request);

    public String obtenerPdfFromUrl(String url);
}
