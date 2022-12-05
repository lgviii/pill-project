package edu.harvard.ext.dgmd_e14.fall_2022.pill_db_fill.c3pi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.Ndc;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.Pill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class used to fill the database with pill and image information parsed from the C3PI XML metadata files.
 * Note that it skips any CR2 or WMV entries as these won't be used for the project.
 * Note that it also filters out any duplicate image file entries, which do exist in the XML files, so each image file
 * is only added to the database once.
 */
public class C3piDatabaseLoader {

    static final String XML_DIRECTORY = "E:\\NoBackup\\DGMD E-14 Final project\\images\\C3PI full data\\ALLXML";
    private static final Logger LOG = LoggerFactory.getLogger(C3piDatabaseLoader.class);

    private final PillPhotoService photoService;

    private final ObjectMapper xmlMapper;

    private final PathMatcher xmlMatcher;

    private final Set<String> filteredImageTypes;

    public C3piDatabaseLoader(PillPhotoService photoService) {
        this.photoService = photoService;
        xmlMapper = XmlMapper.xmlBuilder().build();
        xmlMatcher = FileSystems.getDefault().getPathMatcher("glob:**.xml");
        filteredImageTypes = Stream.of("CR2", "WMV").collect(Collectors.toSet());
    }

    public List<String> fillDatabase(String lastProcessedFile) throws IOException {
        XmlVisitor xmlVisitor =  new XmlVisitor(lastProcessedFile);
        Files.walkFileTree(Paths.get(XML_DIRECTORY), xmlVisitor);
        LOG.info("XML files processed: {}, XML files skipped: {}", xmlVisitor.getFilesProcessed().size(),
                 xmlVisitor.getXmlFilesSkipped().size());
        LOG.debug("XML files skipped:\n{}", String.join("\n", xmlVisitor.getXmlFilesSkipped()));
        LOG.debug("XML files processed:\n{}", String.join("\n", xmlVisitor.getFilesProcessed()));
        return xmlVisitor.getFilesProcessed();
    }

    int processXml(Path file) throws IOException {
        LOG.info("Processing XML file {}", file);
        int imagesProcessed = 0;
        int duplicatePhotos = 0;
        int imagesSkipped = 0;
        DiscXml discXml = loadXml(file);
        for (Image image : discXml.getImages()) {
            if (shouldPhotoBeSaved(image)) {
                PillPhoto pillPhoto = convertXmlImageToPillPhoto(file, image);
                // Check if the photo has already been saved since the XML may have duplicates
                if (photoService.findByC3piImageDirectoryAndC3piImageFile(pillPhoto.getC3piImageDirectory(),
                                                                          pillPhoto.getC3piImageFile()).isPresent()) {
                    LOG.info("Photo entry already exists for {}/{}", pillPhoto.getC3piImageDirectory(),
                             pillPhoto.getC3piImageFile());
                    duplicatePhotos++;
                }
                else {
                    photoService.savePillPhoto(pillPhoto);
                    imagesProcessed++;
                }
            }
            else {
                imagesSkipped++;
            }
        }
        LOG.info("Saved {} images, skipped {} images, {} duplicate photos from file {}", imagesProcessed, imagesSkipped,
                 duplicatePhotos, file.getFileName());
        return imagesProcessed;
    }

    DiscXml loadXml(Path file) throws IOException {
        return xmlMapper.readValue(file.toFile(), DiscXml.class);
    }

    boolean shouldPhotoBeSaved(Image image) {
        // Just to cut down on the noise, don't save entries for CR2 photos or WMV movies, since we're not going to use
        // these photos/movies
        return !filteredImageTypes.contains(image.getImageFile().getFileType().toUpperCase());
    }

    PillPhoto convertXmlImageToPillPhoto(Path path, Image image) {
        Ndc ndc = new Ndc();
        ndc.setNdc9(image.getNdc9());
        ndc.setNdc11(image.getNcd11());
        ndc.setLabeledBy(image.getLabeledBy());
        // Convert all generic names to upper case for storage to simplify matching
        ndc.setGenericName(image.getGenericName().toUpperCase());
        ndc.setProprietaryName(image.getProprietaryName());
        ndc.setTotalParts(image.getParts());

        Pill pill = new Pill();
        pill.setNdc(ndc);
        pill.setPart(image.getPart());
        pill.setImprint(image.getImprint());
        // The XML may have duplicate colors - by using a Set we eliminate them
        pill.setColors(new TreeSet<>(image.getColors()));
        pill.setShape(image.getShape());
        pill.setScore(image.getScore());
        pill.setSize(image.getSize());

        ImageFile imageFile = image.getImageFile();
        PillPhoto pillPhoto = new PillPhoto();
        pillPhoto.setC3piImageDirectory(getPillDirectory(path));
        pillPhoto.setC3piImageFile(imageFile.getFileName());
        pillPhoto.setC3piImageFileType(imageFile.getFileType());

        // An image will either have a "class" or "layout" element, but not both - they mean the same thing
        pillPhoto.setC3piClass(image.getImageClass());
        if (image.getLayout() != null) {
            pillPhoto.setC3piClass(image.getLayout());
        }

        pillPhoto.setImprintRating(image.getImprintRating());
        pillPhoto.setShapeRating(image.getShapeRating());
        pillPhoto.setColorRating(image.getColorRating());
        pillPhoto.setShadowRating(image.getShadowRating());
        pillPhoto.setBackgroundRating(image.getBackgroundRating());
        pillPhoto.setImprintType(image.getImprintType());
        pillPhoto.setImprintColor(image.getImprintColor());
        pillPhoto.setImprintSymbol(image.isSymbol());
        pillPhoto.setPill(pill);
        return pillPhoto;
    }

    String getPillDirectory(Path path) {
        return path.getFileName().toString().replace(".xml", "");
    }

    class XmlVisitor extends SimpleFileVisitor<Path> {

        private final String lastProcessedFile;
        private final List<String> filesProcessed;
        private final List<String> xmlFilesSkipped;
        private boolean fileReached;

        XmlVisitor(String lastProcessedFile) {
            this.lastProcessedFile = lastProcessedFile;
            filesProcessed = new ArrayList<>();
            xmlFilesSkipped = new ArrayList<>();
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
            if (xmlMatcher.matches(file)) {
                if (lastProcessedFile == null || fileReached) {
                    processXml(file);
                    filesProcessed.add(file.toString());
                }
                else {
                    fileReached = lastProcessedFile.equals(file.getFileName().toString());
                    xmlFilesSkipped.add(file.toString());
                }
            }

            return FileVisitResult.CONTINUE;
        }

        public List<String> getFilesProcessed() {
            return filesProcessed;
        }

        public List<String> getXmlFilesSkipped() {
            return xmlFilesSkipped;
        }
    }
}
