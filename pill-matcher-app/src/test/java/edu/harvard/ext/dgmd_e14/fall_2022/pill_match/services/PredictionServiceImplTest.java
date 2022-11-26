package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.constraints.AssertTrue;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class PredictionServiceImplTest {

    @Mock
    private PillMatcherServiceImpl pillMatcherServiceImpl;

    private PredictionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PredictionServiceImpl(pillMatcherServiceImpl);
    }

    @Test
    void testRandomPillSet() throws IOException {

        var map = matchPillsByPredictedImprints();

        assertThat(map.isEmpty(), is(false));

    }


    HashMap<String, Integer> matchPillsByPredictedImprints() throws IOException {

        var br = new BufferedReader(new FileReader("C:\\dev\\Classes\\DGMD14\\pill-project\\pill-matcher-app\\src\\test\\resources\\split_splimage_all_PillSer_only.csv"));

        var fileNameToPillSerMap = new HashMap<String, Integer>();

        String line;

        while ((line = br.readLine()) != null) {
            String[] csvContent = line.split(",");
            fileNameToPillSerMap.put(csvContent[0], Integer.parseInt(csvContent[1]));
        }

        return fileNameToPillSerMap;
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
