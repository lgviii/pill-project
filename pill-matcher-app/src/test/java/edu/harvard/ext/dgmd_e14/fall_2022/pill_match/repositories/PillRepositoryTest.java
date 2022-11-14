package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.repositories;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.Ndc;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.Pill;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PillRepositoryTest {

    @Inject
    private PillRepository repository;

    @Test
    void testFindById() {
        Optional<Pill> result = repository.findById(554L);
        assertThat(result.isPresent(), is(true));

        Pill pill = result.get();
        checkPillWithTwoColors(pill);
    }

    @Test
    void testFindByNdc_Ndc11AndPart() {
        Optional<Pill> result = repository.findByNdc_Ndc11AndPart("42192035360", 1);
        assertThat(result.isPresent(), is(true));
    }

    @Test
    void testFindAllByShape() {
        List<Pill> result = repository.findAllByShape("CAPSULE");
        assertThat(result, hasSize(838));
        result.sort(Comparator.comparing(Pill::getNdc11));
        checkPillWithTwoColors(result.get(62));
    }

    @Test
    void testFindAllByColor() {
        List<Pill> result = repository.findAllBySingleColor("TURQUOISE");
        assertThat(result, hasSize(68));
        result.sort(Comparator.comparing(Pill::getNdc11));
        checkPillWithTwoColors(result.get(1));
    }

    @Test
    void testFindAllByTwoColors() {
        List<Pill> result = repository.findAllByTwoColors("TURQUOISE", "WHITE");
        assertThat(result, hasSize(12));
        result.sort(Comparator.comparing(Pill::getNdc11));
        checkPillWithTwoColors(result.get(0));
    }

    void checkPillWithTwoColors(Pill result) {
        assertThat(result.getId(), is(554L));
        assertThat(result.getGenericDrug().getId(), is(364L));
        assertThat(result.getGenericName(), is("DIVALPROEX"));
        assertThat(result.getNdc9(), is("000746114"));
        assertThat(result.getNdc11(), is("00074611413"));
        assertThat(result.getProprietaryName(), is("DEPAKOTE SPRINKLES 125 MG"));
        assertThat(result.getLabeledBy(), is("ABBOTT LABORATORIES"));
        assertThat(result.getImprint(), is("THIS;END;UP;DEPAKOTE;SPRINKLE;125;mg"));
        assertThat(result.getShape(), is("CAPSULE"));
        assertThat(result.getScore(), is("1"));
        assertThat(result.getSize(), is(17));
        assertThat(result.getColors(), contains("TURQUOISE", "WHITE"));
    }

    @Disabled
    @Test
    void testSave() {
        Ndc ndc = new Ndc();
        ndc.setGenericName("DRUG NAME");
        ndc.setLabeledBy("Manufacturer");
        ndc.setProprietaryName("Custom drug name");
        ndc.setTotalParts(1);

        Pill pill = new Pill();
        pill.setNdc(ndc);
        pill.setPart(1);
        pill.setShape("Round");
        pill.setColors(new TreeSet<>(Arrays.asList("White", "Blue")));
        pill.setScore("2");
        pill.setSize(9);

        repository.save(pill);
        assertThat(pill.getId(), is(greaterThan(0L)));

        Pill savedPill = repository.findById(pill.getId())
                                   .orElseThrow(() -> new RuntimeException("Error finding saved pill"));
        assertThat(savedPill.getColors(), contains("Blue", "White"));
        assertThat(savedPill.getScore(), is("2"));
    }
}
