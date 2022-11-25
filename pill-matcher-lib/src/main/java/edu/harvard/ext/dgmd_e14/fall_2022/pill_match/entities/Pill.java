package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;

/**
 * Entity representing a single pill linked with an NDC identifier - note that for a given NDC, there may be multiple
 * parts, each linked to a different pill.
 * Class based on data available in the C3PI dataset XML metadata files.
 * Not based on the actual FDA SPL documents, since we won't be using that for this project due to problems
 * reconciling the C3PI dataset with the FDA SPL documents.
 */
@SuppressWarnings("unused")
@Entity
@Table(name = "Pill")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "PillSer"))
})
public class Pill extends BaseEntity {

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "NdcSer", nullable = false,
                foreignKey = @ForeignKey(name = "FK_Pill_NdcSer_Ndc"))
    private Ndc ndc;

    /**
     * Tracks which part of the NDC this pill matches, if the NDC has multiple parts.
     */
    @Column(name = "Part", nullable = false)
    private int part;

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

    public Ndc getNdc() {
        return ndc;
    }

    public void setNdc(Ndc ndc) {
        this.ndc = ndc;
    }

    public GenericDrug getGenericDrug() {
        return ndc.getGenericDrug();
    }

    public String getNdc9() {
        return ndc.getNdc9();
    }

    public String getNdc11() {
        return ndc.getNdc11();
    }

    public String getLabeledBy() {
        return ndc.getLabeledBy();
    }

    public String getGenericName() {
        return ndc != null ? ndc.getGenericName() : null;
    }

    public String getProprietaryName() {
        return ndc.getProprietaryName();
    }

    public int getTotalParts() {
        return ndc.getTotalParts();
    }

    public int getPart() {
        return part;
    }

    public void setPart(int part) {
        this.part = part;
    }

    public boolean hasImprint() {
        return imprint != null && !imprint.isBlank();
    }

    public String getImprint() {
        return imprint;
    }

    public List<String> getImprintSections() {
        if (hasImprint()) {
            return Arrays.asList(imprint.toLowerCase().split(";"));
        }
        else {
            return new ArrayList<>();
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pill pill = (Pill) o;
        return part == pill.part && Objects.equals(ndc, pill.ndc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ndc, part);
    }
}
