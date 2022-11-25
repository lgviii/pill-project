package edu.harvard.ext.dgmd_e14.fall_2022.pill_db_fill.c3pi;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.Pill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test class used to actually fill the database.
 */
@PropertySource("classpath:mysql-local-datasource.properties")
@ComponentScan({"edu.harvard.ext.dgmd_e14.fall_2022.pill_db_fill",
                "edu.harvard.ext.dgmd_e14.fall_2022.pill_match"})
@DataJpaTest(
        includeFilters = {
                @ComponentScan.Filter(Service.class),
        },
        showSql = false
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class C3piDatabaseLoaderTest {

    @Inject
    private PillPhotoService photoService;
    private C3piDatabaseLoader databaseLoader;

    @BeforeEach
    void setUp() {
        databaseLoader = new C3piDatabaseLoader(photoService);
    }

    @Disabled
    @Test
    void fillDatabase() throws IOException {
        List<String> filesProcessed = databaseLoader.fillDatabase(null);
        assertThat(filesProcessed, hasSize(110));
    }

    @Disabled
    @Test
    void testProcessXml() throws IOException {
        Path file = Paths.get(C3piDatabaseLoader.XML_DIRECTORY, "PillProjectDisc1.xml");
        int saved = databaseLoader.processXml(file);
        assertThat(saved, is(1148));
    }

    @Test
    void testLoadXml() throws IOException {
        Path file = Paths.get(C3piDatabaseLoader.XML_DIRECTORY, "PillProjectDisc1.xml");
        DiscXml discXml = databaseLoader.loadXml(file);
        assertThat(discXml.getImages(), hasSize(1271));
    }

    @Test
    void testConvertXmlImageToPillPhoto() throws IOException {
        Path file = Paths.get(C3piDatabaseLoader.XML_DIRECTORY, "PillProjectDisc1.xml");
        DiscXml discXml = databaseLoader.loadXml(file);

        Image imageA = discXml.getImages().get(0);
        PillPhoto pillPhotoA = databaseLoader.convertXmlImageToPillPhoto(file, imageA);
        Pill pillA = pillPhotoA.getPill();
        assertThat(pillA.getNdc9(), is("658620549"));
        assertThat(pillA.getNdc11(), is("65862054990"));
        assertThat(pillA.getLabeledBy(), is("AUROBINDO PHARMA USA, INC."));
        assertThat(pillA.getGenericName(), is("VALSARTAN AND HYDROCHLOROTHIAZIDE"));
        assertThat(pillA.getProprietaryName(), is("VALSARTAN and HYDROCHLOROTHIAZIDE Tablets, USP"));
        assertThat(pillA.getTotalParts(), is(1));
        assertThat(pillA.getPart(), is(1));
        assertThat(pillA.getImprint(), is("I;63"));
        assertThat(pillA.getColors(), contains("BROWN"));
        assertThat(pillA.getShape(), is("OVAL"));
        assertThat(pillA.getScore(), is("1"));
        assertThat(pillA.getSize(), is(18));
        assertThat(pillPhotoA.getC3piImageDirectory(), is("PillProjectDisc1"));
        assertThat(pillPhotoA.getC3piImageFile(), is("!!0MOPXZJMHNBIWS_TN7_G7TNNGMXT.PNG"));
        assertThat(pillPhotoA.getC3piImageFileType(), is("PNG"));
        assertThat(pillPhotoA.getC3piClass(), is("MC_COOKED_CALIBRATED_V1.2"));
        assertThat(pillPhotoA.getImprintType(), is("DEBOSSED"));
        assertThat(pillPhotoA.getImprintColor(), is(nullValue()));
        assertThat(pillPhotoA.isImprintSymbol(), is(false));
        assertThat(pillPhotoA.getImprintRating(), is(nullValue()));
        assertThat(pillPhotoA.getShapeRating(), is(nullValue()));
        assertThat(pillPhotoA.getColorRating(), is(nullValue()));
        assertThat(pillPhotoA.getShadowRating(), is(nullValue()));
        assertThat(pillPhotoA.getBackgroundRating(), is(nullValue()));

        Image imageB = discXml.getImages().get(1);
        PillPhoto pillPhotoB = databaseLoader.convertXmlImageToPillPhoto(file, imageB);
        Pill pillB = pillPhotoB.getPill();
        assertThat(pillB.getNdc9(), is("001850720"));
        assertThat(pillB.getNdc11(), is("00185072060"));
        assertThat(pillB.getLabeledBy(), is("SANDOZ INC"));
        assertThat(pillB.getGenericName(), is("INDOMETHACIN"));
        assertThat(pillB.getProprietaryName(), is("INDOMETHACIN SR 75MG"));
        assertThat(pillB.getTotalParts(), is(1));
        assertThat(pillB.getPart(), is(1));
        assertThat(pillB.getImprint(), is("E;720;E;720"));
        assertThat(pillB.getColors(), contains("GREEN", "WHITE"));
        assertThat(pillB.getShape(), is("CAPSULE"));
        assertThat(pillB.getScore(), is("1"));
        assertThat(pillB.getSize(), is(18));
        assertThat(pillPhotoB.getC3piImageDirectory(), is("PillProjectDisc1"));
        assertThat(pillPhotoB.getC3piImageFile(), is("!!11F_JYDH1-PK9TCJNDAHE6B8C6TR.JPG"));
        assertThat(pillPhotoB.getC3piImageFileType(), is("JPG"));
        assertThat(pillPhotoB.getC3piClass(), is("C3PI_Test"));
        assertThat(pillPhotoB.getImprintType(), is("PRINTED"));
        assertThat(pillPhotoB.getImprintColor(), is("BLACK"));
        assertThat(pillPhotoB.isImprintSymbol(), is(false));
        assertThat(pillPhotoB.getImprintRating(), is("Partial"));
        assertThat(pillPhotoB.getShapeRating(), is("Clear"));
        assertThat(pillPhotoB.getColorRating(), is("Somewhat"));
        assertThat(pillPhotoB.getShadowRating(), is("Hard"));
        assertThat(pillPhotoB.getBackgroundRating(), is("Noisy"));
    }
}
