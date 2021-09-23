package pe.com.ci.sed.expediente.persistence.entity;

import java.io.InputStream;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PdfFile {
	private InputStream stream;
    private String nombre;
}
