package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Generic drug name, used as top-level parent to facilitate searches for pills by generic name.
 */
@SuppressWarnings("unused")
@Entity
@Table(name = "GenericDrug")
@AttributeOverride(name = "id", column = @Column(name = "GenericDrugSer"))
public class GenericDrug extends BaseEntity {

    @Column(name = "GenericName", nullable = false, length = 500)
    private String genericName;

    public GenericDrug() {
    }

    public GenericDrug(String genericName) {
        this.genericName = genericName;
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GenericDrug that = (GenericDrug) o;
        return Objects.equals(genericName, that.genericName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genericName);
    }
}
