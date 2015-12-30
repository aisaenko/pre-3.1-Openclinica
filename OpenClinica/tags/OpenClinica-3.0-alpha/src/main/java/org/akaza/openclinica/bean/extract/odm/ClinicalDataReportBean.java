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

import java.util.ArrayList;

import org.akaza.openclinica.bean.odmbeans.OdmClinicalDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ExportFormDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ExportStudyEventDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ExportSubjectDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemGroupDataBean;
import org.akaza.openclinica.bean.submit.crfdata.SubjectGroupDataBean;
import org.apache.commons.lang.StringEscapeUtils;

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
        String ODMVersion = this.getODMVersion();
        // when collecting data, only item with value has been collected.
        StringBuffer xml = this.getXmlOutput();
        String indent = this.getIndent();
        String nls = System.getProperty("line.separator");
        xml.append(indent + "<ClinicalData StudyOID=\"" + StringEscapeUtils.escapeXml(clinicalData.getStudyOID()) + "\" MetaDataVersionOID=\""
            + StringEscapeUtils.escapeXml(this.clinicalData.getMetaDataVersionOID()) + "\">");
        xml.append(nls);
        ArrayList<ExportSubjectDataBean> subs = (ArrayList<ExportSubjectDataBean>) this.clinicalData.getExportSubjectData();
        for (ExportSubjectDataBean sub : subs) {
            xml.append(indent + indent + "<SubjectData SubjectKey=\"" + StringEscapeUtils.escapeXml(sub.getSubjectOID()));
            if ("oc1.2".equalsIgnoreCase(ODMVersion) || "oc1.3".equalsIgnoreCase(ODMVersion)) {
                xml.append("\" OpenClinica:StudySubjectId=\"" + StringEscapeUtils.escapeXml(sub.getStudySubjectId()));
                if (sub.getUniqueIdentifier() != null) {
                    xml.append("\" OpenClinica:UniqueIdentifier=\"" + sub.getUniqueIdentifier());
                }
                if (sub.getStatus() != null) {
                    xml.append("\" OpenClinica:Status=\"" + sub.getStatus());
                }
                if (sub.getSecondaryId() != null) {
                    xml.append("\"  OpenClinica:SecondaryId=\"" + StringEscapeUtils.escapeXml(sub.getSecondaryId()));
                }
                Integer year = sub.getYearOfBirth();
                if (year != null) {
                    xml.append("\" OpenClinica:YearOfBirth=\"" + sub.getYearOfBirth());
                } else {
                    if (sub.getDateOfBirth() != null) {
                        xml.append("\" OpenClinica:DateOfBirth=\"" + sub.getDateOfBirth());
                    }
                }
                if (sub.getSubjectGender() != null) {
                    xml.append("\" OpenClinica:Sex=\"" + sub.getSubjectGender());
                }
            }
            xml.append("\">");
            xml.append(nls);
            //
            ArrayList<ExportStudyEventDataBean> ses = (ArrayList<ExportStudyEventDataBean>) sub.getExportStudyEventData();
            for (ExportStudyEventDataBean se : ses) {
                xml.append(indent + indent + indent + "<StudyEventData StudyEventOID=\"" + StringEscapeUtils.escapeXml(se.getStudyEventOID()));
                if ("oc1.2".equalsIgnoreCase(ODMVersion) || "oc1.3".equalsIgnoreCase(ODMVersion)) {
                    if (se.getLocation() != null) {
                        xml.append("\" OpenClinica:Location=\"" + se.getLocation());
                    }
                    if (se.getStartDate() != null) {
                        xml.append("\" OpenClinica:StartDate=\"" + se.getStartDate());
                    }
                    if (se.getEndDate() != null) {
                        xml.append("\" OpenClinica:EndDate=\"" + se.getEndDate());
                    }
                    if (se.getStatus() != null) {
                        xml.append("\" OpenClinica:Status=\"" + se.getStatus());
                    }
                    if (se.getAgeAtEvent() != null) {
                        xml.append("\" OpenClinica:SubjectAgeAtEvent=\"" + se.getAgeAtEvent());
                    }
                }
                xml.append("\"");
                if (!"-1".equals(se.getStudyEventRepeatKey())) {
                    xml.append(" StudyEventRepeatKey=\"" + se.getStudyEventRepeatKey() + "\"");
                }
                xml.append(">");
                xml.append(nls);
                //
                ArrayList<ExportFormDataBean> forms = se.getExportFormData();
                for (ExportFormDataBean form : forms) {
                    xml.append(indent + indent + indent + indent + "<FormData FormOID=\"" + StringEscapeUtils.escapeXml(form.getFormOID()));
                    if ("oc1.2".equalsIgnoreCase(ODMVersion) || "oc1.3".equalsIgnoreCase(ODMVersion)) {
                        if (form.getCrfVersion() != null) {
                            xml.append("\" OpenClinica:Version=\"" + form.getCrfVersion());
                        }
                        if (form.getInterviewerName() != null) {
                            xml.append("\" OpenClinica:InterviewerName=\"" + form.getInterviewerName());
                        }
                        if (form.getInterviewDate() != null) {
                            xml.append("\" OpenClinica:InterviewDate=\"" + form.getInterviewDate());
                        }
                        if (form.getStatus() != null) {
                            xml.append("\" OpenClinica:Status=\"" + form.getStatus());
                        }
                    }
                    xml.append("\">");
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
            if ("oc1.2".equalsIgnoreCase(ODMVersion) || "oc1.3".equalsIgnoreCase(ODMVersion)) {
                ArrayList<SubjectGroupDataBean> sgddata = (ArrayList<SubjectGroupDataBean>) sub.getSubjectGroupData();
                if (sgddata.size() > 0) {
                    for (SubjectGroupDataBean sgd : sgddata) {
                        String cid =
                            sgd.getStudyGroupClassId() != null ? "OpenClinica:StudyGroupClassID=\"" + StringEscapeUtils.escapeXml(sgd.getStudyGroupClassId())
                                + "\" " : "";
                        if (cid.length() > 0) {
                            String cn =
                                sgd.getStudyGroupClassName() != null ? "OpenClinica:StudyGroupClassName=\""
                                    + StringEscapeUtils.escapeXml(sgd.getStudyGroupClassName()) + "\" " : "";
                            String gn =
                                sgd.getStudyGroupName() != null ? "OpenClinica:StudyGroupName=\"" + StringEscapeUtils.escapeXml(sgd.getStudyGroupName())
                                    + "\" " : "";
                            xml.append(indent + indent + indent + "<OpenClinica:SubjectGroupData " + cid + cn + gn);
                        }
                        xml.append(" />");
                        xml.append(nls);
                    }
                }
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