package pe.com.ci.sed.web.model.request;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusquedaDocumento {
    Integer nroLoteDesde;
    Integer nroLoteHasta;
    Date fechaAdmisionDesde;
    Date fechaAdmisionHasta;
    String nroFactura;
    String remesa;
    String garante;
    BigDecimal importeFactura;
    String importeOperacion;
    Integer actual;
    Integer size;
}
