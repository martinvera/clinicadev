package pe.com.ci.sed.clinicalrecord.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericDocument<T> {
	private String sistemaOrigen;
	private T contenido;
	
	
}
