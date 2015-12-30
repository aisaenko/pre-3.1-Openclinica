/*
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 *
 */

package org.akaza.openclinica.bean.odmbeans;

/**
 * 
 * This is root bean for ODM XML document
 * 
 * @author ywang (May, 2008)
 * 
 */

public class ODMBean {
    private String description;
    private String fileType;
    private String fileOID;
    private String creationDateTime;
    private String ODMVersion;
    private String xmlns;
    private String xsi;
    private String schemaLocation;

    /**
     * In default constructor, xmlns = "http://www.cdisc.org/ns/odm/v1.2",
     * xsi="http://www.w3.org/2001/XMLSchema-instance",
     * schemaLocation="http://www.cdisc.org/ns/odm/v1.2 ODM1-2-1.xsd",
     * fileType="Snapshot", ODMVersion="1.2"
     */
    public ODMBean() {
        xmlns = "http://www.cdisc.org/ns/odm/v1.2";
        xsi = "http://www.w3.org/2001/XMLSchema-instance";
        schemaLocation = "http://www.cdisc.org/ns/odm/v1.2 ODM1-2-1.xsd";
        fileType = "Snapshot";
        ODMVersion = "1.2";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setFileType(String filetype) {
        this.fileType = filetype;
    }

    public String getFileType() {
        return this.fileType;
    }

    public void setFileOID(String oid) {
        this.fileOID = oid;
    }

    public String getFileOID() {
        return this.fileOID;
    }

    public void setCreationDateTime(String odmDateTimeString) {
        this.creationDateTime = odmDateTimeString;
    }

    public String getCreationDateTime() {
        return this.creationDateTime;
    }

    public void setODMVersion(String version) {
        this.ODMVersion = version;
    }

    public String getODMVersion() {
        return this.ODMVersion;
    }

    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }

    public String getXmlns() {
        return this.xmlns;
    }

    public void setXsi(String xsi) {
        this.xsi = xsi;
    }

    public String getXsi() {
        return this.xsi;
    }

    public void setSchemaLocation(String schemalocation) {
        this.schemaLocation = schemalocation;
    }

    public String getSchemaLocation() {
        return this.schemaLocation;
    }
}