package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.GenericDrug;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
@Validated
public interface GenericDrugService {

    @NotNull
    Optional<GenericDrug> findByName(@NotNull String name);

    @NotNull
    List<GenericDrug> findDrugsContainingName(@NotNull String name);

    @NotNull
    List<GenericDrug> findAll();

    @NotNull
    GenericDrug saveDrug(@NotNull @Valid GenericDrug drug);

    @NotNull
    GenericDrug getOrSaveDrug(@NotNull String genericName);
}
