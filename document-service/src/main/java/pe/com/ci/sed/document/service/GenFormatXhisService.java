package pe.com.ci.sed.document.service;

import pe.com.ci.sed.document.model.request.xhis.Xhis;
import pe.com.ci.sed.document.persistence.entity.Archivo;
import pe.com.ci.sed.document.persistence.entity.Documento;

import java.util.List;

public interface GenFormatXhisService {

    public List<Archivo> generarFormatos(Xhis request, Documento documento);
}
