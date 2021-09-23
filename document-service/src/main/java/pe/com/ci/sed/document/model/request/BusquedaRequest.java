package pe.com.ci.sed.document.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static pe.com.ci.sed.document.util.GenericUtil.DEFAULT_INICIAL;
import static pe.com.ci.sed.document.util.GenericUtil.DEFAULT_TAMANIO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusquedaRequest {
    private String fechaAtencionDesde;
    private String fechaAtencionHasta;
    private String fechaAtencion;
    private String nroLote;
    private String nroLoteDesde;
    private String nroLoteHasta;
    private String facturaNro;
    private String garanteId;
    private String nroRemesa;
    private BigDecimal importeFactura;
    private String importeOperacion;
    private String nroEncuentro;
    private Integer actual = Integer.valueOf(DEFAULT_INICIAL);
    private Integer size = Integer.valueOf(DEFAULT_TAMANIO);
    private String siguiente;
    private String nombrePaciente;
    private String fullName;
}
