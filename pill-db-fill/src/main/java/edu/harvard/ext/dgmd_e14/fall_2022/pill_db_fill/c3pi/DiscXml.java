package edu.harvard.ext.dgmd_e14.fall_2022.pill_db_fill.c3pi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

/**
 * Class used to represent the entire contents of a C3PI image directory metadata XML file.
 */
@JacksonXmlRootElement(localName = "MedicosConsultants")
public class DiscXml {

    @JacksonXmlElementWrapper(localName = "ImageExport")
    @JacksonXmlProperty(localName = "Image")
    private List<Image> images;

    /**
     * Name of the PillProject "disc", used for both the XML file name and the associated image directory name.  Not
     * part of the actual XML, stored for convenience.
     */
    @JsonIgnore
    private String disc;

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getDisc() {
        return disc;
    }

    public void setDisc(String disc) {
        this.disc = disc;
    }
}
