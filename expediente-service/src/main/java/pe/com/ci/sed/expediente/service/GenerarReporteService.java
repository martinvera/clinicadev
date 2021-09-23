package pe.com.ci.sed.expediente.service;


import java.util.List;

import pe.com.ci.sed.expediente.model.request.GenericRequest;
import pe.com.ci.sed.expediente.model.request.ReporteTotalParcialRequest;
import pe.com.ci.sed.expediente.model.request.RequestReporte;
import pe.com.ci.sed.expediente.model.response.ResponseExpError;
import pe.com.ci.sed.expediente.model.response.ResponseMecYMod;
import pe.com.ci.sed.expediente.persistence.entity.ReportExcelTotalParcial;

public interface GenerarReporteService {

    public List<ResponseExpError> reporteExpError(GenericRequest<RequestReporte> request);

    public List<ResponseMecYMod> reporteMecYMod(GenericRequest<RequestReporte> request);

    public List<ReportExcelTotalParcial> reportExcelTotalParcials(GenericRequest<ReporteTotalParcialRequest> request);

    public String validarNumeroLote(GenericRequest<ReporteTotalParcialRequest> request);

}
