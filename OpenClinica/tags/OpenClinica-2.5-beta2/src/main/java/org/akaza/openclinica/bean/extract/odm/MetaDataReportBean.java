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

import org.akaza.openclinica.bean.odmbeans.CodeListBean;
import org.akaza.openclinica.bean.odmbeans.CodeListItemBean;
import org.akaza.openclinica.bean.odmbeans.ElementRefBean;
import org.akaza.openclinica.bean.odmbeans.FormDefBean;
import org.akaza.openclinica.bean.odmbeans.GlobalVariablesBean;
import org.akaza.openclinica.bean.odmbeans.ItemDefBean;
import org.akaza.openclinica.bean.odmbeans.ItemGroupDefBean;
import org.akaza.openclinica.bean.odmbeans.MetaDataVersionBean;
import org.akaza.openclinica.bean.odmbeans.OdmStudyBean;
import org.akaza.openclinica.bean.odmbeans.RangeCheckBean;
import org.akaza.openclinica.bean.odmbeans.StudyEventDefBean;
import org.akaza.openclinica.bean.odmbeans.TranslatedTextBean;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;

/**
 * Create ODM XML document for entire study.
 * 
 * @author ywang (May, 2008)
 */

public class MetaDataReportBean extends OdmXmlReportBean {
    private OdmStudyBean odmstudy;

    public MetaDataReportBean(OdmStudyBean odmstudy) {
        super();
        this.odmstudy = odmstudy;
    }

    /**
     * has not been implemented yet
     */
    @Override
    public void createOdmXml(boolean isDataset) {
        // this.addHeading();
        // this.addRootStartLine();
        // addNodeStudy();
        // this.addRootEndLine();
    }

    public void addNodeStudy(boolean isDataset) {
        StringBuffer xml = this.getXmlOutput();
        String indent = this.getIndent();
        xml.append(indent + "<Study OID=\"" + StringEscapeUtils.escapeXml(odmstudy.getOid()) + "\">");
        xml.append("\n");
        addStudyGlobalVariables();
        addStudyMetaDataVersion(isDataset);
        xml.append(indent + "</Study>");
        xml.append("\n");
    }

    public void addStudyGlobalVariables() {
        StringBuffer xml = this.getXmlOutput();
        String indent = this.getIndent();
        String currentIndent = indent + indent;
        GlobalVariablesBean gv = odmstudy.getGlobalVariables();
        xml.append(currentIndent + "<GlobalVariables>");
        xml.append("\n");
        xml.append(currentIndent + indent + "<StudyName>" + StringEscapeUtils.escapeXml(gv.getStudyName()) + "</StudyName>");
        xml.append("\n");
        xml.append(currentIndent + indent + "<StudyDescription>");
        xml.append("\n");
        xml.append(currentIndent + indent + indent + StringEscapeUtils.escapeXml(gv.getStudyDescription()));
        xml.append("\n");
        xml.append(currentIndent + indent + "</StudyDescription>");
        xml.append("\n");
        xml.append(currentIndent + indent + "<ProtocolName>" + StringEscapeUtils.escapeXml(gv.getProtocolName()) + "</ProtocolName>");
        xml.append("\n");
        xml.append(currentIndent + "</GlobalVariables>");
        xml.append("\n");
    }

    public void addStudyMetaDataVersion(boolean isDataset) {
        StringBuffer xml = this.getXmlOutput();
        String indent = this.getIndent();
        String currentIndent = indent + indent;
        MetaDataVersionBean meta = odmstudy.getMetaDataVersion();
        if (isDataset) {
            xml.append(currentIndent + "<MetaDataVersion OID=\"" + StringEscapeUtils.escapeXml(meta.getOid()) + "\" Name=\""
                + StringEscapeUtils.escapeXml(meta.getName()) + "\">");
            xml.append("\n");
            // for <Include>,
            // 1. In order to have <Include>, previous metadataversionOID must
            // be
            // given.
            // 2. If there is no previous study, then previous study OID is as
            // the
            // same as the current study OID
            // 3. there is no Include if both previous study and previous
            // metadataversionOID are empty
            if (meta.getInclude() != null) {
                String pmOid = meta.getInclude().getMetaDataVersionOID();
                if (pmOid != null && pmOid.length() > 0) {
                    xml.append(currentIndent + indent);
                    String psOid = meta.getInclude().getStudyOID();
                    if (psOid != null && psOid.length() > 0) {
                        xml.append("<Include StudyOID =\"" + StringEscapeUtils.escapeXml(psOid) + "\"");
                    } else {
                        xml.append("<Include StudyOID =\"" + StringEscapeUtils.escapeXml(odmstudy.getOid()) + "\"");
                    }
                    xml.append(" MetaDataVersionOID=\"" + StringEscapeUtils.escapeXml(pmOid) + "\"/>");
                    xml.append("\n");
                }
            }
        } else {
            xml.append(currentIndent + "<MetaDataVersion>");
            xml.append("\n");
        }
        //
        addProtocol(currentIndent + indent);
        addStudyEventDef(currentIndent + indent);
        addFormDef(currentIndent + indent);
        addItemGroupDef(currentIndent + indent);
        addItemDef(currentIndent + indent);
        addCodeList(currentIndent + indent);
        xml.append(currentIndent + "</MetaDataVersion>");
        xml.append("\n");
    }

    public void addProtocol(String currentIndent) {
        // The protocol lists the kinds of study events that can occur within a
        // specific version of a Study.
        StringBuffer xml = this.getXmlOutput();
        String indent = this.getIndent();
        xml.append(currentIndent + "<Protocol>");
        xml.append("\n");
        for (ElementRefBean seref : odmstudy.getMetaDataVersion().getProtocol().getStudyEventRefs()) {
            // At this point, Mandatory hasn't been set yes
            xml.append(currentIndent + indent + "<StudyEventRef StudyEventOID=\"" + StringEscapeUtils.escapeXml(seref.getElementDefOID())
                + "\" Mandatory=\"Yes\"/>");
            xml.append("\n");
        }
        xml.append(currentIndent + "</Protocol>");
        xml.append("\n");
    }

    public void addStudyEventDef(String currentIndent) {
        StringBuffer xml = this.getXmlOutput();
        String indent = this.getIndent();
        ArrayList<StudyEventDefBean> seds = (ArrayList<StudyEventDefBean>) odmstudy.getMetaDataVersion().getStudyEventDefs();
        for (StudyEventDefBean sed : seds) {
            xml.append(currentIndent + "<StudyEventDef OID=\"" + StringEscapeUtils.escapeXml(sed.getOid()) + "\"  Name=\""
                + StringEscapeUtils.escapeXml(sed.getName()) + "\" Repeating=\"" + sed.getRepeating() + "\" Type=\"" + sed.getType() + "\">");
            xml.append("\n");
            ArrayList<ElementRefBean> forms = (ArrayList<ElementRefBean>) sed.getFormRefs();
            for (ElementRefBean form : forms) {
                xml.append(currentIndent + indent + "<FormRef FormOID=\"" + StringEscapeUtils.escapeXml(form.getElementDefOID()) + "\" Mandatory=\""
                    + form.getMandatory() + "\"/>");
                xml.append("\n");
            }
            xml.append(currentIndent + "</StudyEventDef>");
            xml.append("\n");
        }
    }

    public void addFormDef(String currentIndent) {
        StringBuffer xml = this.getXmlOutput();
        String indent = this.getIndent();
        ArrayList<FormDefBean> forms = (ArrayList<FormDefBean>) odmstudy.getMetaDataVersion().getFormDefs();
        for (FormDefBean form : forms) {
            xml.append(currentIndent + "<FormDef OID=\"" + StringEscapeUtils.escapeXml(form.getOid()) + "\" Name=\""
                + StringEscapeUtils.escapeXml(form.getName()) + "\" Repeating=\"" + form.getRepeating() + "\">");
            xml.append("\n");
            ArrayList<ElementRefBean> igs = (ArrayList<ElementRefBean>) form.getItemGroupRefs();
            for (ElementRefBean ig : igs) {
                xml.append(currentIndent + indent + "<ItemGroupRef ItemGroupOID=\"" + StringEscapeUtils.escapeXml(ig.getElementDefOID()) + "\" Mandatory=\""
                    + ig.getMandatory() + "\"/>");
                xml.append("\n");
            }
            xml.append(currentIndent + "</FormDef>");
            xml.append("\n");
        }
    }

    public void addItemGroupDef(String currentIndent) {
        StringBuffer xml = this.getXmlOutput();
        String indent = this.getIndent();
        ArrayList<ItemGroupDefBean> igs = (ArrayList<ItemGroupDefBean>) odmstudy.getMetaDataVersion().getItemGroupDefs();
        for (ItemGroupDefBean ig : igs) {
            if (ig.getComment().length() > 0) {
                xml.append(currentIndent + "<ItemGroupDef OID=\"" + StringEscapeUtils.escapeXml(ig.getOid()) + "\" Name=\""
                    + StringEscapeUtils.escapeXml(ig.getName()) + "\" Repeating=\"" + ig.getRepeating() + "\" SASDatasetName=\""
                    + this.getSasNameValidatory().getValidSasName(ig.getPreSASDatasetName()) + "\" Comment=\"" + StringEscapeUtils.escapeXml(ig.getComment())
                    + "\">");
            } else {
                xml.append(currentIndent + "<ItemGroupDef OID=\"" + StringEscapeUtils.escapeXml(ig.getOid()) + "\" Name=\""
                    + StringEscapeUtils.escapeXml(ig.getName()) + "\" Repeating=\"" + ig.getRepeating() + "\" SASDatasetName=\""
                    + this.getSasNameValidatory().getValidSasName(ig.getPreSASDatasetName()) + "\">");
            }
            xml.append("\n");
            ArrayList<ElementRefBean> items = (ArrayList<ElementRefBean>) ig.getItemRefs();
            for (ElementRefBean item : items) {
                xml.append(currentIndent + indent + "<ItemRef ItemOID=\"" + StringEscapeUtils.escapeXml(item.getElementDefOID()) + "\" Mandatory=\""
                    + item.getMandatory() + "\"/>");
                xml.append("\n");
            }
            xml.append(currentIndent + "</ItemGroupDef>");
            xml.append("\n");
        }
    }

    public void addItemDef(String currentIndent) {
        StringBuffer xml = this.getXmlOutput();
        String indent = this.getIndent();
        ArrayList<ItemDefBean> items = (ArrayList<ItemDefBean>) odmstudy.getMetaDataVersion().getItemDefs();
        for (ItemDefBean item : items) {
            xml.append(currentIndent + "<ItemDef OID=\"" + StringEscapeUtils.escapeXml(item.getOid()) + "\" Name=\""
                + StringEscapeUtils.escapeXml(item.getName()) + "\" DataType=\"" + item.getDateType() + "\"");
            String len = item.getLength();
            if (len != null && len.length() > 0) {
                xml.append(" Length=\"" + len + "\"");
            }
            len = item.getSignificantDigits();
            if (len != null && len.length() > 0) {
                xml.append(" SignificantDigits=\"" + len + "\"");
            }
            xml.append(" SASFieldName=\"" + this.getSasNameValidatory().getValidSasName(item.getPreSASFieldName()) + "\"");
            if (item.getComment().length() > 0) {
                xml.append(" Comment=\"" + StringEscapeUtils.escapeXml(item.getComment()) + "\"");
            }
            boolean hasNode = false;
            // add question
            TranslatedTextBean t = item.getQuestion();
            if (t != null && t.getText() != null && t.getText().length() > 0) {
                if (!hasNode) {
                    hasNode = true;
                    xml.append(">");
                    xml.append("\n");
                }
                xml.append(currentIndent + indent + "<Question>");
                xml.append("\n");
                xml.append(currentIndent + indent + indent + "<TranslatedText>");
                xml.append("\n");
                xml.append(currentIndent + indent + indent + StringEscapeUtils.escapeXml(t.getText()));
                xml.append("\n");
                xml.append(currentIndent + indent + indent + "</TranslatedText>");
                xml.append("\n");
                xml.append(currentIndent + indent + "</Question>");
                xml.append("\n");
            }
            // add RangeCheck
            if (item.getRangeCheck() != null) {
                ArrayList<RangeCheckBean> rcs = (ArrayList<RangeCheckBean>) item.getRangeCheck();
                for (RangeCheckBean rc : rcs) {
                    if (rc.getComparator().length() > 0) {
                        if (!hasNode) {
                            hasNode = true;
                            xml.append(">");
                            xml.append("\n");
                        }
                        xml.append(currentIndent + indent + "<RangeCheck Comparator=\"" + StringEscapeUtils.escapeXml(rc.getComparator()) + "\" SoftHard=\""
                            + rc.getSoftHard() + "\">");
                        xml.append("\n");
                        xml.append(currentIndent + indent + indent + "<CheckValue>" + StringEscapeUtils.escapeXml(rc.getCheckValue()) + "</CheckValue>");
                        xml.append("\n");
                        xml.append(currentIndent + indent + indent + "<ErrorMessage><TranslatedText>"
                            + StringEscapeUtils.escapeXml(rc.getErrorMessage().getText()) + "</TranslatedText></ErrorMessage>");
                        xml.append("\n");
                        xml.append(currentIndent + indent + "</RangeCheck>");
                        xml.append("\n");
                    }
                }
            }
            // add CodeListRef
            String clOid = item.getCodeListOID();
            if (clOid != null && clOid.length() > 0) {
                if (!hasNode) {
                    hasNode = true;
                    xml.append(">");
                    xml.append("\n");
                }
                xml.append(currentIndent + indent + "<CodeListRef CodeListOID=\"" + StringEscapeUtils.escapeXml(clOid) + "\"/>");
                xml.append("\n");
            }
            if (hasNode) {
                xml.append(currentIndent + "</ItemDef>");
                xml.append("\n");
                hasNode = false;
            } else {
                xml.append(">");
                xml.append("\n");
            }
        }
    }

    public void addCodeList(String currentIndent) {
        StringBuffer xml = this.getXmlOutput();
        String indent = this.getIndent();
        if (odmstudy.getMetaDataVersion().getCodeLists() != null) {
            ArrayList<CodeListBean> cls = (ArrayList<CodeListBean>) odmstudy.getMetaDataVersion().getCodeLists();
            if (cls.size() > 0) {
                for (CodeListBean cl : cls) {
                    boolean isString = cl.getDataType().equalsIgnoreCase("text") ? true : false;
                    xml.append(currentIndent + "<CodeList OID=\"" + StringEscapeUtils.escapeXml(cl.getOid()) + "\" Name=\""
                        + StringEscapeUtils.escapeXml(cl.getName()) + "\" DataType=\"" + cl.getDataType() + "\" SASFormatName=\""
                        + this.getSasFormValidator().getValidSASFormatName(cl.getName(), isString) + "\">");
                    xml.append("\n");
                    ArrayList<CodeListItemBean> clis = (ArrayList<CodeListItemBean>) cl.getCodeListItems();
                    if (clis != null && clis.size() > 0) {
                        for (CodeListItemBean cli : clis) {
                            xml.append(currentIndent + indent + "<CodeListItem CodedValue=\"" + StringEscapeUtils.escapeXml(cli.getCodedVale()) + "\">");
                            xml.append("\n");
                            xml.append(currentIndent + indent + indent + "<Decode>");
                            xml.append("\n");
                            xml.append(currentIndent + indent + indent + indent + "<TranslatedText>" + StringEscapeUtils.escapeXml(cli.getDecode().getText())
                                + "</TranslatedText>");
                            xml.append("\n");
                            xml.append(currentIndent + indent + indent + "</Decode>");
                            xml.append("\n");
                            xml.append(currentIndent + indent + "</CodeListItem>");
                            xml.append("\n");
                        }
                    }
                    xml.append(currentIndent + "</CodeList>");
                    xml.append("\n");
                }
            }
        }
    }

    public void setOdmStudy(OdmStudyBean odmstudy) {
        this.odmstudy = odmstudy;
    }

    public OdmStudyBean getOdmStudyBean() {
        return this.odmstudy;
    }
}