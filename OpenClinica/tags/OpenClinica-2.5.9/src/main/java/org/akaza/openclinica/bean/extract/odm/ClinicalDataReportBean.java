/* OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 *
 *//* OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 *
 */
package org.akaza.openclinica.bean.extract.odm;

import org.akaza.openclinica.bean.odmbeans.OdmClinicalDataBean;
import org.akaza.openclinica.bean.submit.crfdata.FormDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemGroupDataBean;
import org.akaza.openclinica.bean.submit.crfdata.StudyEventDataBean;
import org.akaza.openclinica.bean.submit.crfdata.SubjectDataBean;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;

/**
 * Create ODM XML document for entire study.
 * 
 * @author ywang (May, 2008)
 */

public class ClinicalDataReportBean extends OdmXmlReportBean {
    private OdmClinicalDataBean clinicalData;

    public ClinicalDataReportBean(OdmClinicalDataBean clinicaldata) {
        super();
        this.clinicalData = clinicaldata;
    }

    /**
     * has not been implemented yet
     */
    @Override
    public void createOdmXml(boolean isDataset) {
        // this.addHeading();
        // this.addRootStartLine();
        // addNodeClinicalData();
        // this.addRootEndLine();
    }

    public void addNodeClinicalData() {
        // when collecting data, only item with value has been collected.
        StringBuffer xml = this.getXmlOutput();
        String indent = this.getIndent();
        String nls = System.getProperty("line.separator");
        xml.append(indent + "<ClinicalData StudyOID=\"" + StringEscapeUtils.escapeXml(clinicalData.getStudyOID()) + "\" MetaDataVersionOID=\""
            + StringEscapeUtils.escapeXml(this.clinicalData.getMetaDataVersionOID()) + "\">");
        xml.append(nls);
        ArrayList<SubjectDataBean> subs = (ArrayList<SubjectDataBean>) this.clinicalData.getSubjectData();
        for (SubjectDataBean sub : subs) {
            xml.append(indent + indent + "<SubjectData SubjectKey=\"" + StringEscapeUtils.escapeXml(sub.getSubjectOID()) + "\">");
            xml.append(nls);
            //
            ArrayList<StudyEventDataBean> ses = sub.getStudyEventData();
            for (StudyEventDataBean se : ses) {
                xml.append(indent + indent + indent + "<StudyEventData StudyEventOID=\"" + StringEscapeUtils.escapeXml(se.getStudyEventOID()) + "\"");
                if (!"-1".equals(se.getStudyEventRepeatKey())) {
                    xml.append(" StudyEventRepeatKey=\"" + se.getStudyEventRepeatKey() + "\"");
                }
                xml.append(">");
                xml.append(nls);
                //
                ArrayList<FormDataBean> forms = se.getFormData();
                for (FormDataBean form : forms) {
                    xml.append(indent + indent + indent + indent + "<FormData FormOID=\"" + StringEscapeUtils.escapeXml(form.getFormOID()) + "\">");
                    xml.append(nls);
                    //
                    ArrayList<ImportItemGroupDataBean> igs = form.getItemGroupData();
                    for (ImportItemGroupDataBean ig : igs) {
                        xml.append(indent + indent + indent + indent + indent + "<ItemGroupData ItemGroupOID=\""
                            + StringEscapeUtils.escapeXml(ig.getItemGroupOID()) + "\" ");
                        if (!"-1".equals(ig.getItemGroupRepeatKey())) {
                            xml.append("ItemGroupRepeatKey=\"" + ig.getItemGroupRepeatKey() + "\" ");
                        }
                        xml.append("TransactionType=\"Insert\">");
                        xml.append(nls);
                        ArrayList<ImportItemDataBean> items = ig.getItemData();
                        for (ImportItemDataBean item : items) {
                            xml.append(indent + indent + indent + indent + indent + indent + "<ItemData ItemOID=\""
                                + StringEscapeUtils.escapeXml(item.getItemOID()) + "\" Value=\"" + StringEscapeUtils.escapeXml(item.getValue()) + "\"/>");
                            xml.append(nls);
                        }
                        xml.append(indent + indent + indent + indent + indent + "</ItemGroupData>");
                        xml.append(nls);
                    }
                    xml.append(indent + indent + indent + indent + "</FormData>");
                    xml.append(nls);
                }
                xml.append(indent + indent + indent + "</StudyEventData>");
                xml.append(nls);
            }
            xml.append(indent + indent + "</SubjectData>");
            xml.append(nls);
        }
        xml.append(indent + "</ClinicalData>");
        xml.append(nls);
    }

    public void setClinicalData(OdmClinicalDataBean clinicaldata) {
        this.clinicalData = clinicaldata;
    }

    public OdmClinicalDataBean getClinicalData() {
        return this.clinicalData;
    }
}