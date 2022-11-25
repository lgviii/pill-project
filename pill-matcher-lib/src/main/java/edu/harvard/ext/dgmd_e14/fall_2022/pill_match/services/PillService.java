package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.Pill;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
@Validated
public interface PillService {

    @NotNull
    Optional<Pill> findBySer(@NotNull Long pillSer);

    @NotNull
    Optional<Pill> findByNdc11AndPart(@NotNull String ndc11, int part);

    @NotNull
    List<Pill> findAllByShape(@NotNull String color);

    @NotNull
    List<Pill> findAllBySingleColor(@NotNull String color);

    @NotNull
    List<Pill> findAllByTwoColors(@NotNull String colorOne, @NotNull String colorTwo);

    @NotNull
    List<Pill> findAllByShapeAndSingleColor(@NotNull String shape, @NotNull String color);

    @NotNull
    List<Pill> findAllByShapeAndTwoColors(@NotNull String shape, @NotNull String colorOne, @NotNull String colorTwo);

    @NotNull
    Pill savePill(@NotNull @Valid Pill pill);

}
