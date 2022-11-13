package edu.harvard.ext.dgmd_e14.fall_2022.pill_db_fill.c3pi;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.BaseEntity;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.Pill;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Class based on data available in the C3PI dataset XML metadata files.
 * Not based on the actual FDA SPL documents, since we won't be using that for this project due to problems
 * reconciling the C3PI dataset with the FDA SPL documents.
 * For easier use, also includes some C3PI metadata values such as class, file name, file type, and directory/path.
 */
@Entity
@Table(name = "PillPhoto")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "PillPhotoSer"))
})
public class PillPhoto extends BaseEntity {

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "PillSer", nullable = false)
    private Pill pill;

    // Values provided in the C3PI metadata, not relevant for actual SPL documents.

    @Column(name = "C3piClass")
    private String c3piClass;

    @Column(name = "C3piImageFile")
    private String c3piImageFile;

    @Column(name = "C3piImageFileType")
    private String c3piImageFileType;

    @Column(name = "C3piImageDirectory")
    private String c3piImageDirectory;

    // May not be present
    @Column(name = "ImprintRating")
    private String imprintRating;

    // May not be present
    @Column(name = "ShapeRating")
    private String shapeRating;

    // May not be present
    @Column(name = "ColorRating")
    private String colorRating;

    // May not be present
    @Column(name = "ShadowRating")
    private String shadowRating;

    // May not be present
    @Column(name = "BackgroundRating")
    private String backgroundRating;

    // Will always be present in XML, set to "N/A" if no imprint - should be left null in database
    @Column(name = "ImprintType")
    private String imprintType;

    // Only present if imprint
    @Column(name = "ImprintColor")
    private String imprintColor;

    // Always present in XML, even if no imprint
    @Column(name = "ImprintSymbol")
    private boolean imprintSymbol;

    public Pill getPill() {
        return pill;
    }

    public void setPill(Pill pill) {
        this.pill = pill;
    }

    public String getC3piClass() {
        return c3piClass;
    }

    public void setC3piClass(String c3piClass) {
        this.c3piClass = c3piClass;
    }

    public String getC3piImageFile() {
        return c3piImageFile;
    }

    public void setC3piImageFile(String c3piImageFile) {
        this.c3piImageFile = c3piImageFile;
    }

    public String getC3piImageFileType() {
        return c3piImageFileType;
    }

    public void setC3piImageFileType(String c3piImageFileType) {
        this.c3piImageFileType = c3piImageFileType;
    }

    public String getC3piImageDirectory() {
        return c3piImageDirectory;
    }

    public void setC3piImageDirectory(String c3piImageDirectory) {
        this.c3piImageDirectory = c3piImageDirectory;
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

    public boolean isImprintSymbol() {
        return imprintSymbol;
    }

    public void setImprintSymbol(boolean imprintSymbol) {
        this.imprintSymbol = imprintSymbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PillPhoto pillPhoto = (PillPhoto) o;
        return c3piImageFile.equals(pillPhoto.c3piImageFile) && c3piImageDirectory.equals(pillPhoto.c3piImageDirectory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(c3piImageFile, c3piImageDirectory);
    }
}
