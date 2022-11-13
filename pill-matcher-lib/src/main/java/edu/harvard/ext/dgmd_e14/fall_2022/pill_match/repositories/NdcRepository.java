package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.repositories;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.Ndc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NdcRepository extends JpaRepository<Ndc, Long> {

    Optional<Ndc> findByNdc11(String ndc11);
}
