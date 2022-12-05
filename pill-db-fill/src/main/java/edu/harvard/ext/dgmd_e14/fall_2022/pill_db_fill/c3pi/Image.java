package edu.harvard.ext.dgmd_e14.fall_2022.pill_db_fill.c3pi;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * Class used to represent an Image element in the C3PI XML metadata files, containing information about a single image
 * in the C3PI dataset.
 */
public class Image {

    @JacksonXmlProperty(localName = "NDC9")
    private String ndc9;

    @JacksonXmlProperty(localName = "NDC11")
    private String ncd11;

    @JacksonXmlProperty(localName = "Part")
    private int part;

    @JacksonXmlProperty(localName = "Parts")
    private int parts;

    @JacksonXmlProperty(localName = "MedicosConsultantsID")
    private String medicosConsultantsId;

    @JacksonXmlProperty(localName = "LabeledBy")
    private String labeledBy;

    @JacksonXmlProperty(localName = "GenericName")
    private String genericName;

    @JacksonXmlProperty(localName = "ProprietaryName")
    private String proprietaryName;

    @JacksonXmlProperty(localName = "File")
    private ImageFile imageFile;

    @JacksonXmlProperty(localName = "Class")
    private String imageClass;

    @JacksonXmlProperty(localName = "Camera")
    private String camera;

    @JacksonXmlProperty(localName = "Illumination")
    private String illumination;

    @JacksonXmlProperty(localName = "Background")
    private String background;

    @JacksonXmlProperty(localName = "RatingImprint")
    private String imprintRating;

    @JacksonXmlProperty(localName = "RatingShape")
    private String shapeRating;

    @JacksonXmlProperty(localName = "RatingColor")
    private String colorRating;

    @JacksonXmlProperty(localName = "RatingShadow")
    private String shadowRating;

    @JacksonXmlProperty(localName = "RatingBackground")
    private String backgroundRating;

    @JacksonXmlProperty(localName = "Layout")
    private String layout;

    @JacksonXmlProperty(localName = "Polarizer")
    private Boolean polarizer;

    @JacksonXmlProperty(localName = "Imprint")
    private String imprint;

    @JacksonXmlProperty(localName = "ImprintType")
    private String imprintType;

    @JacksonXmlProperty(localName = "ImprintColor")
    private String imprintColor;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Color")
    private List<String> colors;

    @JacksonXmlProperty(localName = "Shape")
    private String shape;

    @JacksonXmlProperty(localName = "Score")
    private String score;

    @JacksonXmlProperty(localName = "Symbol")
    private boolean symbol;

    @JacksonXmlProperty(localName = "Size")
    private int size;

    @JacksonXmlProperty(localName = "AcquisitionDate")
    private String acquisitionDate;

    @JacksonXmlProperty(localName = "Attribution")
    private String attribution;

    public String getNdc9() {
        return ndc9;
    }

    public void setNdc9(String ndc9) {
        this.ndc9 = ndc9;
    }

    public String getNcd11() {
        return ncd11;
    }

    public void setNcd11(String ncd11) {
        this.ncd11 = ncd11;
    }

    public int getPart() {
        return part;
    }

    public void setPart(int part) {
        this.part = part;
    }

    public int getParts() {
        return parts;
    }

    public void setParts(int parts) {
        this.parts = parts;
    }

    public String getMedicosConsultantsId() {
        return medicosConsultantsId;
    }

    public void setMedicosConsultantsId(String medicosConsultantsId) {
        this.medicosConsultantsId = medicosConsultantsId;
    }

    public String getLabeledBy() {
        return labeledBy;
    }

    public void setLabeledBy(String labeledBy) {
        this.labeledBy = labeledBy;
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public String getProprietaryName() {
        return proprietaryName;
    }

    public void setProprietaryName(String proprietaryName) {
        this.proprietaryName = proprietaryName;
    }

    public ImageFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(ImageFile imageFile) {
        this.imageFile = imageFile;
    }

    public String getImageClass() {
        return imageClass;
    }

    public void setImageClass(String imageClass) {
        this.imageClass = imageClass;
    }

    public String getCamera() {
        return camera;
    }

    public void setCamera(String camera) {
        this.camera = camera;
    }

    public String getIllumination() {
        return illumination;
    }

    public void setIllumination(String illumination) {
        this.illumination = illumination;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getImprintRating() {
        return imprintRating;
    }

    public void setImprintRating(String imprintRating) {
        this.imprintRating = imprintRating;
    }

    public String getShapeRating() {
        return shapeRating;
    }

    public void setShapeRating(String shapeRating) {
        this.shapeRating = shapeRating;
    }

    public String getColorRating() {
        return colorRating;
    }

    public void setColorRating(String colorRating) {
        this.colorRating = colorRating;
    }

    public String getShadowRating() {
        return shadowRating;
    }

    public void setShadowRating(String shadowRating) {
        this.shadowRating = shadowRating;
    }

    public String getBackgroundRating() {
        return backgroundRating;
    }

    public void setBackgroundRating(String backgroundRating) {
        this.backgroundRating = backgroundRating;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public Boolean getPolarizer() {
        return polarizer;
    }

    public void setPolarizer(Boolean polarizer) {
        this.polarizer = polarizer;
    }

    public String getImprint() {
        return imprint;
    }

    public void setImprint(String imprint) {
        this.imprint = imprint;
    }

    public String getImprintType() {
        return imprintType;
    }

    public void setImprintType(String imprintType) {
        this.imprintType = imprintType;
    }

    public String getImprintColor() {
        return imprintColor;
    }

    public void setImprintColor(String imprintColor) {
        this.imprintColor = imprintColor;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public boolean isSymbol() {
        return symbol;
    }

    public void setSymbol(boolean symbol) {
        this.symbol = symbol;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(String acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public String getAttribution() {
        return attribution;
    }

    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }
}
