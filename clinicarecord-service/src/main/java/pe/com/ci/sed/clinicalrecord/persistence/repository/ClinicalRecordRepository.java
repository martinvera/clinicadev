package pe.com.ci.sed.clinicalrecord.persistence.repository;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.stereotype.Repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;

import pe.com.ci.sed.clinicalrecord.persistence.entity.ClinicalRecord;

@Repository
public interface ClinicalRecordRepository extends CosmosRepository<ClinicalRecord, String> {

    public List<ClinicalRecord> findAllByNroLote(Integer nroLote);

    public void deleteAllByNroLote(@NotBlank long nroLote);

    public ClinicalRecord findByNroLoteAndFacturaNro(@NotBlank long nroLote, String facturaNro);

    public List<ClinicalRecord> findByNroEncuentro( @NotBlank String[] nroEncuentro);

}
