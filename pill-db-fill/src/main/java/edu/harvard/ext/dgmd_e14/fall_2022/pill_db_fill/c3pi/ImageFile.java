package edu.harvard.ext.dgmd_e14.fall_2022.pill_db_fill.c3pi;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Class used to represent the File child element in each Image element in the C3PI XML metadata, containing
 * information about the actual image file.
 */
public class ImageFile {

    @JacksonXmlProperty(localName = "Name")
    private String fileName;

    @JacksonXmlProperty(localName = "Type")
    private String fileType;

    @JacksonXmlProperty(localName = "Size")
    private long size;

    @JacksonXmlProperty(localName = "Sha1")
    private String sha1Checksum;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getSha1Checksum() {
        return sha1Checksum;
    }

    public void setSha1Checksum(String sha1Checksum) {
        this.sha1Checksum = sha1Checksum;
    }
}
