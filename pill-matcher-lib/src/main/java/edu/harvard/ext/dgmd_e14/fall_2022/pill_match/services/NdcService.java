package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.Ndc;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Validated
public interface NdcService {

    @NotNull
    Optional<Ndc> findByNdc11(@NotNull String ndc11);

    @NotNull
    Ndc saveNdc(@NotNull @Valid Ndc ndc);
}
