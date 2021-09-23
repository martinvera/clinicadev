package pe.com.ci.sed.expediente.service;
import pe.com.ci.sed.expediente.model.request.GenericRequest;
import pe.com.ci.sed.expediente.model.request.ReporteTotalParcialRequest;
import pe.com.ci.sed.expediente.model.request.RequestExpenGenerado;
import pe.com.ci.sed.expediente.persistence.entity.ReporteTotalParcial;

public interface ReporteService {
    public Object reporteExpedientesGenerados(RequestExpenGenerado request);
    public Object registrarReporteTotalParcial(GenericRequest<ReporteTotalParcial> genericRequest);
    public Object listarReporteTotalParcial(GenericRequest<ReporteTotalParcialRequest> genericRequest);
}