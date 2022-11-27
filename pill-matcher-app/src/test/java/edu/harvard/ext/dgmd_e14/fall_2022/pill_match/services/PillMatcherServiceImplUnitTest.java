package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.Ndc;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.Pill;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.repositories.PillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class PillMatcherServiceImplUnitTest {

    @Mock
    private PillRepository pillRepositoryMock;

    private PillMatcherServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PillMatcherServiceImpl(pillRepositoryMock);
    }

    static Pill createPill(String ndc11, int part) {
        Ndc ndc = new Ndc();
        ndc.setNdc11(ndc11);
        Pill pill = new Pill();
        pill.setNdc(ndc);
        pill.setPart(part);
        return pill;
    }

    @Test
    void matchPillsByPredictedImprints() {
    }

    @Test
    void checkAllPredictionsAgainstImprint() {
    }

    @Test
    void findMatchingPills() {
    }

    @Test
    void limitModelOutput() {
        var map = new HashMap<String, Double>();
        map.put("BLUE", 0.9996551275253296);
        map.put("TURQUOISE", 0.0003351218765601516);
        map.put("GREEN", 8.499319847032893e-06);
        map.put("RED", 6.21621040863829e-07);
        map.put("ORANGE", 2.979411419801181e-07);
        map.put("BROWN", 1.2477769928409543e-07);
        map.put("GRAY", 4.4911708130257466e-08);
        map.put("PURPLE", 4.1617742141397684e-08);
        map.put("BLACK", 3.7359097149192166e-08);
        map.put("PINK", 2.7760169274415603e-08);
        map.put("YELLOW", 5.409113157384127e-09);
        map.put("WHITE", 4.558284416589231e-09);

        var result = service.limitModelOutput(map);
        assertThat(result, contains("BLUE", "TURQUOISE"));
    }

    @Test
    void limitPillMatchOutput() {
        var map = new HashMap<Pill, Double>();
        map.put(createPill("11110001101", 1), 0.37190);     // 7
        map.put(createPill("11110001102", 1), 0.24715);     // 9
        map.put(createPill("11110001103", 1), 0.6092);      // 3
        map.put(createPill("11110001104", 1), 3e-6);
        map.put(createPill("11110001105", 1), 0.3158);      // 8
        map.put(createPill("11110001106", 1), 0.8571);      // 2
        map.put(createPill("11110001107", 1), 0.0147);
        map.put(createPill("11110001108", 1), 2.58191e-4);
        map.put(createPill("11110001109", 1), 0.5817);      // 4
        map.put(createPill("11110001110", 1), 4.247e-4);
        map.put(createPill("11110001111", 1), 0.3809);      // 6
        map.put(createPill("11110001112", 1), 0.99301);     // 1
        map.put(createPill("11110001113", 1), 9.17501e-5);
        map.put(createPill("11110001114", 1), 0.57194);     // 5
        map.put(createPill("11110001115", 1), 0.03155);     // 10

        var result = service.limitPillMatchOutput(map);
        assertThat(result.size(), is(10));
        assertThat(result, allOf(hasEntry(createPill("11110001112", 1), 0.99301),
                                 hasEntry(createPill("11110001106", 1), 0.8571),
                                 hasEntry(createPill("11110001103", 1), 0.6092),
                                 hasEntry(createPill("11110001109", 1), 0.5817),
                                 hasEntry(createPill("11110001114", 1), 0.57194),
                                 hasEntry(createPill("11110001111", 1), 0.3809),
                                 hasEntry(createPill("11110001101", 1), 0.37190),
                                 hasEntry(createPill("11110001105", 1), 0.3158),
                                 hasEntry(createPill("11110001102", 1), 0.24715),
                                 hasEntry(createPill("11110001115", 1), 0.03155)));
        var expResult = new LinkedHashMap<Pill, Double>();
        expResult.put(createPill("11110001112", 1), 0.99301);     // 1
        expResult.put(createPill("11110001106", 1), 0.8571);      // 2
        expResult.put(createPill("11110001103", 1), 0.6092);      // 3
        expResult.put(createPill("11110001109", 1), 0.5817);      // 4
        expResult.put(createPill("11110001114", 1), 0.57194);     // 5
        expResult.put(createPill("11110001111", 1), 0.3809);      // 6
        expResult.put(createPill("11110001101", 1), 0.37190);     // 7
        expResult.put(createPill("11110001105", 1), 0.3158);      // 8
        expResult.put(createPill("11110001102", 1), 0.24715);     // 9
        expResult.put(createPill("11110001115", 1), 0.03155);     // 10
        assertThat(result, is(expResult));
    }

    @Test
    void combineImprintOutputs() {
    }

    @Nested
    @DisplayName("checkPredictionGroup")
    class CheckPredictionGroupTests {

        @Test
        void doubleText_ExactMatches() {
            double result =
                    service.checkPredictionGroup("advil", Arrays.asList("Advil", "test"));
            assertThat(result, is(0.5));
        }

        @Test
        void checkPredictionGroup_ExactMatches() {
            double result =
                    service.checkPredictionGroup("TEST;400mg", Arrays.asList("400mg", "test"));
            assertThat(result, is(1.0));
        }

        @Test
        void checkPredictionGroup_SingleMatch() {
            double result =
                    service.checkPredictionGroup("RAPID;400mg", Arrays.asList("400mg", "test"));
            assertThat(result, is(0.5));
        }

        @Test
        void checkPredictionGroup_PartialMatches() {
            double result =
                    service.checkPredictionGroup("T3ST;450mg", Arrays.asList("400mg", "test"));
            // Accuracy of T3ST match should be 0.75, accuracy of 450mg match should be 0.8
            assertThat(result, is(0.775));
        }

        @Test
        void checkPredictionGroup_LowMatch() {
            double result =
                    service.checkPredictionGroup("atest410;450mg", Arrays.asList("400mg", "test"));
            // Accuracy of atest410 match should be 0.5 (see test below) and discarded, accuracy of 450mg match
            // should be 0.8
            assertThat(result, is(0.4));
        }

        @Test
        void checkPredictionGroup_NoMatch() {
            double result =
                    service.checkPredictionGroup("atest410;390", Arrays.asList("400mg", "test"));
            // Accuracy of atest410 match should be 0.5 (see test below) and discarded, accuracy of 390 match
            // should be 0.2 and discarded
            assertThat(result, is(0.0));
        }
    }

    @Nested
    @DisplayName("checkMatch")
    class CheckMatchTests {

        @Test
        void checkMatch_ExactMatch() {
            PillMatcherServiceImpl.ImprintMatch result =
                    service.checkMatch("test", Arrays.asList("nonsense", "test", "other"));
            assertThat(result.index, is(1));
            assertThat(result.accuracy, is(1.0));
        }

        @Test
        void checkMatch_CloseMatch() {
            PillMatcherServiceImpl.ImprintMatch result =
                    service.checkMatch("test1", Arrays.asList("nonsense", "test", "rapid"));
            assertThat(result.index, is(1));
            assertThat(result.accuracy, is(0.8));

            // Doesn't matter whether prediction or imprint has the longer length, accuracy is the same
            PillMatcherServiceImpl.ImprintMatch result2 =
                    service.checkMatch("test", Arrays.asList("nonsense", "test1", "rapid"));
            assertThat(result2.index, is(1));
            assertThat(result2.accuracy, is(0.8));
        }

        @Test
        void checkMatch_HalfMatch() {
            PillMatcherServiceImpl.ImprintMatch result =
                    service.checkMatch("atest410", Arrays.asList("nonsense", "test", "rapid"));
            assertThat(result.index, is(1));
            assertThat(result.accuracy, is(0.5));

            PillMatcherServiceImpl.ImprintMatch result2 =
                    service.checkMatch("test", Arrays.asList("nonsense", "atest410", "rapid"));
            assertThat(result2.index, is(1));
            assertThat(result2.accuracy, is(0.5));
        }

        @Test
        void checkMatch_PoorMatch() {
            PillMatcherServiceImpl.ImprintMatch result =
                    service.checkMatch("test", Arrays.asList("nonsense", "official", "rapid"));
            assertThat(result.index, is(0));
            assertThat(result.accuracy, is(0.25));

            PillMatcherServiceImpl.ImprintMatch result2 =
                    service.checkMatch("nonsense", Arrays.asList("test", "official", "rapid"));
            assertThat(result2.index, is(0));
            assertThat(result2.accuracy, is(0.25));
        }

        @Test
        void checkMatch_NoMatch() {
            PillMatcherServiceImpl.ImprintMatch result =
                    service.checkMatch("test", Arrays.asList("onyx", "official", "rapid"));
            assertThat(result.index, is(-1));
            assertThat(result.accuracy, is(0.0));
        }
    }
}
