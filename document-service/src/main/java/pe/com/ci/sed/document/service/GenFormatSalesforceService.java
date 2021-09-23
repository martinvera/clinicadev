package pe.com.ci.sed.document.service;

import pe.com.ci.sed.document.model.request.salesforce.Salesforce;
import pe.com.ci.sed.document.persistence.entity.Archivo;
import pe.com.ci.sed.document.persistence.entity.Documento;

import java.util.List;

public interface GenFormatSalesforceService {
    public List<Archivo> generarFormatos(Salesforce request, Documento documento);
}
