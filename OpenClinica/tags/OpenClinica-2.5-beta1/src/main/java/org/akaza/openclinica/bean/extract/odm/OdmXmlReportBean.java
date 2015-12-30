/* OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 *
 */
package org.akaza.openclinica.bean.extract.odm;

import org.akaza.openclinica.bean.extract.ODMSASFormatNameValidator;
import org.akaza.openclinica.bean.extract.SasNameValidationBean;
import org.akaza.openclinica.bean.odmbeans.ODMBean;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create ODM XML document.
 * 
 * @author ywang (May, 2008)
 */

public abstract class OdmXmlReportBean {
    private StringBuffer xmlOutput;
    private String xmlHeading;
    private ODMBean odmbean;

    private String indent;
    private SasNameValidationBean sasNameValidator;
    private ODMSASFormatNameValidator sasFormatValidator;

    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

    /**
     * In this constructor, xmlHeading = "<?xml version=\"1.0\"
     * encoding=\"UTF-8\"?>";
     */
    public OdmXmlReportBean() {
        xmlOutput = new StringBuffer();
        xmlHeading = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        indent = "    ";
        // locale_df_string =
        // ResourceBundleProvider.getFormatBundle().getString("date_format_string");
        sasNameValidator = new SasNameValidationBean();
        sasFormatValidator = new ODMSASFormatNameValidator();
    }

    public abstract void createOdmXml();

    public void addHeading() {
        xmlOutput.append(this.xmlHeading);
        xmlOutput.append("\n");
    }

    /**
     * Append the beginning line of ODM root element
     * 
     * @param odmXml
     * @param odmbean
     */
    public void addRootStartLine() {
        xmlOutput.append("<ODM xmlns=\"" + odmbean.getXmlns() + "\" " + "xmlns:xsi=\"" + odmbean.getXsi() + "\" xsi:schemaLocation=\""
            + odmbean.getSchemaLocation() + "\" ODMVersion=\"" + odmbean.getODMVersion() + "\" FileOID=\"" + StringEscapeUtils.escapeXml(odmbean.getFileOID())
            + "\" FileType=\"" + odmbean.getFileType() + "\" Description=\"" + StringEscapeUtils.escapeXml(odmbean.getDescription()) + "\" CreationDateTime=\""
            + odmbean.getCreationDateTime() + "\" >");
        xmlOutput.append("\n");
    }

    public void addRootEndLine() {
        xmlOutput.append("</ODM>");
    }

    @Override
    public String toString() {
        return xmlOutput.toString();
    }

    public StringBuffer getXmlOutput() {
        return this.xmlOutput;
    }

    public void setXmlOutput(StringBuffer odmXml) {
        this.xmlOutput = odmXml;
    }

    public String getXmlHeading() {
        return this.xmlHeading;
    }

    public void setXmlHeading(String xmlheading) {
        this.xmlHeading = xmlheading;
    }

    public void setOdmBean(ODMBean odmbean) {
        this.odmbean = odmbean;
    }

    public ODMBean getOdmBean() {
        return this.odmbean;
    }

    public void setSasNameValidator(SasNameValidationBean validator) {
        this.sasNameValidator = validator;
    }

    public SasNameValidationBean getSasNameValidatory() {
        return this.sasNameValidator;
    }

    public void setSasFormatValidator(ODMSASFormatNameValidator validator) {
        this.sasFormatValidator = validator;
    }

    public ODMSASFormatNameValidator getSasFormValidator() {
        return this.sasFormatValidator;
    }

    public void setIndent(String indent) {
        this.indent = indent;
    }

    public String getIndent() {
        return this.indent;
    }
}