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

    Optional<Pill> findBySer(@NotNull Long pillSer);

    Optional<Pill> findByNdc11(@NotNull String ndc11);

    List<Pill> findAllByShape(@NotNull String color);

    List<Pill> findAllBySingleColor(@NotNull String color);

    List<Pill> findAllByTwoColors(@NotNull String colorOne, @NotNull String colorTwo);

    List<Pill> findAllByShapeAndSingleColor(@NotNull String shape, @NotNull String color);

    List<Pill> findAllByShapeAndTwoColors(@NotNull String shape, @NotNull String colorOne, @NotNull String colorTwo);

    Pill savePill(@NotNull @Valid Pill pill);

}
