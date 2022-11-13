package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities;

import javax.persistence.*;
import java.util.SortedSet;

/**
 * Class based on data available in the C3PI dataset XML metadata files.
 * Not based on the actual FDA SPL documents, since we won't be using that for this project due to problems
 * reconciling the C3PI dataset with the FDA SPL documents.
 * For easier use, also includes some C3PI metadata values such as class, file name, file type, and directory/path.
 */
@SuppressWarnings("unused")
@Entity
@Table(name = "Pill")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "PillSer"))
})
public class Pill extends BaseEntity {

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "GenericDrugSer", nullable = false)
    private GenericDrug genericDrug;

    @Column(name = "Ndc9", length = 9)
    private String ndc9;

    @Column(name = "Ndc11", length = 11)
    private String ndc11;

    @Column(name = "LabeledBy", length = 500)
    private String labeledBy;

    @Column(name = "ProprietaryName", nullable = false, length = 500)
    private String proprietaryName;

    @Column(name = "Imprint", length = 50)
    private String imprint;

    @Column(name = "Shape", nullable = false, length = 50)
    private String shape;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "PillColor",
            joinColumns = @JoinColumn(name = "PillSer", nullable = false,
                    foreignKey = @ForeignKey(name = "FK_Color_PillSer_Pill")))
    @OrderBy
    @Column(name = "Color", nullable = false, length = 25)
    private SortedSet<String> colors;

    @Column(name = "Score", nullable = false, length = 25)
    private String score;

    @Column(name = "PillSize", nullable = false)
    private int size;

    public GenericDrug getGenericDrug() {
        return genericDrug;
    }

    public void setGenericDrug(GenericDrug genericDrug) {
        this.genericDrug = genericDrug;
    }

    public String getNdc9() {
        return ndc9;
    }

    public void setNdc9(String ndc9) {
        this.ndc9 = ndc9;
    }

    public String getNdc11() {
        return ndc11;
    }

    public void setNdc11(String ndc11) {
        this.ndc11 = ndc11;
    }

    public String getLabeledBy() {
        return labeledBy;
    }

    public void setLabeledBy(String labeledBy) {
        this.labeledBy = labeledBy;
    }

    public String getGenericName() {
        return genericDrug != null ? genericDrug.getGenericName() : null;
    }

    public void setGenericName(String genericName) {
        if (genericName != null) {
            genericDrug = new GenericDrug(genericName);
        }
        else {
            genericDrug = null;
        }
    }

    public String getProprietaryName() {
        return proprietaryName;
    }

    public void setProprietaryName(String proprietaryName) {
        this.proprietaryName = proprietaryName;
    }

    public String getImprint() {
        return imprint;
    }

    public void setImprint(String imprint) {
        this.imprint = imprint;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public SortedSet<String> getColors() {
        return colors;
    }

    public void setColors(SortedSet<String> colors) {
        this.colors = colors;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
