package pe.com.ci.sed.expediente.service;

import java.util.List;

import pe.com.ci.sed.expediente.model.request.ActualizarFacturaGenerada;
import pe.com.ci.sed.expediente.model.request.RequestHeader;
import pe.com.ci.sed.expediente.persistence.entity.ClinicalRecord;

public interface ClinicalRecordService {

    public void actualizarFacturaGenerada(RequestHeader header, List<ActualizarFacturaGenerada> actualizarFacturaGeneradas);
    public ClinicalRecord obtenerFactura(RequestHeader header, String facturaNro, long nroLote);
}
