package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.repositories;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.Pill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PillRepository extends JpaRepository<Pill, Long> {

    @Query("SELECT DISTINCT pill FROM Pill pill JOIN FETCH pill.ndc ndc "
           + "JOIN FETCH ndc.genericDrug genericDrug JOIN FETCH pill.colors colors "
           + "WHERE ndc.ndc11 = :ndc11 AND pill.part = :part")
    Optional<Pill> findByNdc_Ndc11AndPart(@Param("ndc11") String ndc11, @Param("part") int part);

    @Query("SELECT DISTINCT pill FROM Pill pill JOIN FETCH pill.ndc ndc "
           + "JOIN FETCH ndc.genericDrug genericDrug JOIN FETCH pill.colors colors "
           + "WHERE pill.shape = :shape")
    List<Pill> findAllByShape(@Param("shape") String shape);

    @Query("SELECT DISTINCT pill FROM Pill pill JOIN FETCH pill.ndc ndc "
           + "JOIN FETCH ndc.genericDrug genericDrug JOIN FETCH pill.colors colors "
           + "WHERE :color IN ELEMENTS(pill.colors)")
    List<Pill> findAllBySingleColor(@Param("color") String color);

    /**
     * Finds all pills that have both specified colors.
     *
     * @param colorOne first color to match, case-sensitive
     * @param colorTwo second color to match, case-sensitive
     * @return List containing all pills that have both specified colors
     */
    @Query("SELECT DISTINCT pill FROM Pill pill JOIN FETCH pill.ndc ndc "
           + "JOIN FETCH ndc.genericDrug genericDrug JOIN FETCH pill.colors colors "
           + "WHERE :color1 IN ELEMENTS(pill.colors) AND :color2 IN ELEMENTS(pill.colors)")
    List<Pill> findAllByTwoColors(@Param("color1") String colorOne, @Param("color2") String colorTwo);

    @Query("SELECT DISTINCT pill FROM Pill pill JOIN FETCH pill.ndc ndc "
           + "JOIN FETCH ndc.genericDrug genericDrug JOIN FETCH pill.colors colors "
           + "WHERE pill.shape = :shape AND :color IN ELEMENTS(pill.colors)")
    List<Pill> findAllByShapeAndSingleColor(@Param("shape") String shape, @Param("color") String color);

    /**
     * Finds all pills that have the specified shape and both specified colors.
     *
     * @param shape shape to match, case-sensitive
     * @param colorOne first color to match, case-sensitive
     * @param colorTwo second color to match, case-sensitive
     * @return List containing all pills that have the specified shape and both specified colors
     */
    @Query("SELECT DISTINCT pill FROM Pill pill JOIN FETCH pill.ndc ndc "
           + "JOIN FETCH ndc.genericDrug genericDrug JOIN FETCH pill.colors colors "
           + "WHERE pill.shape = :shape AND :color1 IN ELEMENTS(pill.colors) AND :color2 IN ELEMENTS(pill.colors)")
    List<Pill> findAllByShapeAndTwoColors(@Param("shape") String shape, @Param("color1") String colorOne,
                                          @Param("color2") String colorTwo);
}
