package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing the NDC code which may contain multiple parts, each matching a different pill.
 * Class based on data available in the C3PI dataset XML metadata files.
 * Not based on the actual FDA SPL documents, since we won't be using that for this project due to problems
 * reconciling the C3PI dataset with the FDA SPL documents.
 */
@Entity
@Table(name = "Ndc")
@AttributeOverride(name = "id", column = @Column(name = "NdcSer"))
public class Ndc extends BaseEntity {

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "GenericDrugSer", nullable = false,
                foreignKey = @ForeignKey(name = "FK_Ndc_GenericDrugSer_GenericDrug"))
    private GenericDrug genericDrug;

    @Column(name = "Ndc9", length = 9)
    private String ndc9;

    @Column(name = "Ndc11", length = 11)
    private String ndc11;

    @Column(name = "LabeledBy", length = 500)
    private String labeledBy;

    @Column(name = "ProprietaryName", nullable = false, length = 500)
    private String proprietaryName;

    @Column(name = "TotalParts", nullable = false)
    private int totalParts;

    @OneToMany(mappedBy = "ndc")
    private List<Pill> pills;

    public GenericDrug getGenericDrug() {
        return genericDrug;
    }

    public void setGenericDrug(GenericDrug genericDrug) {
        this.genericDrug = genericDrug;
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

    public String getProprietaryName() {
        return proprietaryName;
    }

    public void setProprietaryName(String proprietaryName) {
        this.proprietaryName = proprietaryName;
    }

    public int getTotalParts() {
        return totalParts;
    }

    public void setTotalParts(int totalParts) {
        this.totalParts = totalParts;
    }

    public List<Pill> getPills() {
        return pills;
    }

    public void setPills(List<Pill> pills) {
        this.pills = pills;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Ndc ndc = (Ndc) o;
        return Objects.equals(ndc11, ndc.ndc11);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ndc11);
    }
}
