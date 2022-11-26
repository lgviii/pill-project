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
            assertThat(result, is(1.0));
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
        @Test
        void given_validServiceData_when_formatServiceResponse_then_responsePopulated() {
            var tempOcr = "rad;se;rad;kindness;rad;joy;ocal;comil;eastsideral;see;read;rendnsness;rad;jony;comlocal;eastside";
            var tempColor = "('BLUE', 0.7100010514259338)('WHITE', 0.11876345425844193)('BROWN', 0.06079021841287613)('PURPLE', 0.030904419720172882)('GREEN', 0.0276800487190485)('ORANGE', 0.022973377257585526)('TURQUOISE', 0.00940349604934454)('GRAY', 0.0077321697026491165)('BLACK', 0.005431152880191803)('PINK', 0.0047547463327646255)('RED', 0.0011841788655146956)('YELLOW', 0.0003815217351075262)";
            var tempShape = "('CAPSULE', 0.9885231256484985)('OVAL', 0.0045670801773667336)('RECTANGLE', 0.002677515847608447)('ROUND', 0.0015578584279865026)('DIAMOND', 0.0005599552532657981)('SQUARE', 0.0004942992236465216)('TRAPEZOID', 0.0004664442967623472)('HEXAGON (6 sided)', 0.0003721187822520733)('PENTAGON (5 sided)', 0.00019967702974099666)('TEAR', 0.00017700176977086812)('TRIANGLE', 0.00017204538744408637)('SEMI-CIRCLE', 0.0001441368367522955)('FREEFORM', 8.891469042282552e-05)";

            var result = service.formatServiceResponse(tempOcr, tempColor, tempShape);

            var resultItem = result.stream().findFirst().get();
            assertThat(resultItem.getColorModelMatches().size(), is(12));
            assertThat(resultItem.getShapeModelMatches().size(), is(13));
            assertThat(resultItem.getImprintPredictions().size(), is(16));
        }
}
