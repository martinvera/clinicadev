package pe.com.ci.sed.document.persistence.repository;

import org.springframework.stereotype.Repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;

import pe.com.ci.sed.document.persistence.entity.Documento;

import javax.validation.constraints.NotBlank;


@Repository
public interface DocumentRepository extends CosmosRepository<Documento, String> {

    public void deleteAllByNroLote(@NotBlank long nroLote);

}
