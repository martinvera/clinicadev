package pe.com.ci.sed.expediente.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportExcelTotalParcial extends AuditoriaStorage {

    private long nroLote;
    private String facturaNro;
    private String nrosEncuentro;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date fechaAtencion;
    private double facturaImporte;
    private String modoFacturacion;
    private String mecanismoFacturacionDesc;
    private String estadoFactura;
    private String estado;
    private String garanteDescripcion;
    private String garanteId;
    private Date fechaCreacion;
    private String paciente;
    private String fechaAceptacion;
}
