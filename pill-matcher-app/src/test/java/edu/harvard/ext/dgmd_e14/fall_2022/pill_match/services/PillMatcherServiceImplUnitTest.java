package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.repositories.PillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class PillMatcherServiceImplUnitTest {

    @Mock
    private PillRepository pillRepositoryMock;

    private PillMatcherServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PillMatcherServiceImpl(pillRepositoryMock);
    }

    @Test
    void matchPillsByPredictedImprints() {
    }

    @Test
    void checkAllPredictionsAgainstImprint() {
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
