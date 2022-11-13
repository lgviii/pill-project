package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.repositories;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.GenericDrug;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GenericDrugRepository extends JpaRepository<GenericDrug, Long> {

    Optional<GenericDrug> findByGenericName(String name);

    List<GenericDrug> findByGenericNameContainingIgnoreCase(String name);
}
