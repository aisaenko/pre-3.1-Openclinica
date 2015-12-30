/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.extract;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.odmbeans.CodeListBean;
import org.akaza.openclinica.bean.odmbeans.CodeListItemBean;
import org.akaza.openclinica.bean.odmbeans.ElementRefBean;
import org.akaza.openclinica.bean.odmbeans.FormDefBean;
import org.akaza.openclinica.bean.odmbeans.ItemDefBean;
import org.akaza.openclinica.bean.odmbeans.ItemGroupDefBean;
import org.akaza.openclinica.bean.odmbeans.MetaDataVersionBean;
import org.akaza.openclinica.bean.odmbeans.MetaDataVersionProtocolBean;
import org.akaza.openclinica.bean.odmbeans.OdmClinicalDataBean;
import org.akaza.openclinica.bean.odmbeans.StudyEventDefBean;
import org.akaza.openclinica.bean.submit.crfdata.FormDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemGroupDataBean;
import org.akaza.openclinica.bean.submit.crfdata.StudyEventDataBean;
import org.akaza.openclinica.bean.submit.crfdata.SubjectDataBean;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.logic.odmExport.MetaDataCollector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

/**
 * 
 * @author ywang (May, 2008) *
 */

public class OdmExtractDAO extends DatasetDAO {
    public OdmExtractDAO(DataSource ds) {
        super(ds);
    }

    @Override
    protected void setDigesterName() {
        digesterName = SQLFactory.getInstance().DAO_ODM_EXTRACT;
    }

    public void setStudyEventAndFormMetaTypesExpected() {
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.STRING);// sed_oid
        this.setTypeExpected(2, TypeNames.STRING);// cv_oid
        this.setTypeExpected(3, TypeNames.STRING);// sed_name
        this.setTypeExpected(4, TypeNames.BOOL);// sed_repeating
        this.setTypeExpected(5, TypeNames.STRING);// sed_type
        this.setTypeExpected(6, TypeNames.STRING);// cv_name
        this.setTypeExpected(7, TypeNames.BOOL);// cv_required
        this.setTypeExpected(8, TypeNames.STRING);// crf_name
    }

    public void setItemGroupAndItemMetaTypesExpected() {
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.STRING);// crf_version_oid
        this.setTypeExpected(2, TypeNames.STRING);// item_group_oid
        this.setTypeExpected(3, TypeNames.STRING);// item_oid
        this.setTypeExpected(4, TypeNames.INT);// response_set_id
        this.setTypeExpected(5, TypeNames.STRING); // item_group_name
        this.setTypeExpected(6, TypeNames.STRING);// item_name
        this.setTypeExpected(7, TypeNames.INT);// item_data_type_id
        this.setTypeExpected(8, TypeNames.STRING);// header
        this.setTypeExpected(9, TypeNames.STRING);// left_item_text
        this.setTypeExpected(10, TypeNames.STRING);// right_item_text
        this.setTypeExpected(11, TypeNames.BOOL);// item_required
        this.setTypeExpected(12, TypeNames.STRING);// regexp
        this.setTypeExpected(13, TypeNames.STRING);// regexp_error_msg
        this.setTypeExpected(14, TypeNames.INT);// response_type_id
        this.setTypeExpected(15, TypeNames.STRING);// options_text
        this.setTypeExpected(16, TypeNames.STRING);// options_values
        this.setTypeExpected(17, TypeNames.STRING);// response_label
    }

    public void setClinicalDataTypesExpected() {
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT);// subject_id;
        this.setTypeExpected(2, TypeNames.STRING);// study_subject_oid;
        this.setTypeExpected(3, TypeNames.STRING);// study_event_definition_oid;
        this.setTypeExpected(4, TypeNames.BOOL);// study_event_repeating
        this.setTypeExpected(5, TypeNames.INT);// sample_ordinal
        this.setTypeExpected(6, TypeNames.STRING);// crf_version_oid
        this.setTypeExpected(7, TypeNames.STRING);// item_group_oid
        this.setTypeExpected(8, TypeNames.STRING);// item_oid
        this.setTypeExpected(9, TypeNames.STRING);// item_group_name
        this.setTypeExpected(10, TypeNames.INT);// item_data_ordinal
        this.setTypeExpected(11, TypeNames.STRING);// value
        this.setTypeExpected(12, TypeNames.INT);// item_data_type_id
    }

    public void getStudyEventAndFormMetaByStudyId(int studyId, MetaDataVersionBean metadata) {
        this.setStudyEventAndFormMetaTypesExpected();

        HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
        variables.put(1, studyId);

        ArrayList alist = this.select(this.digester.getQuery("getStudyEventAndFormMetaByStudyId"), variables);
        Iterator it = alist.iterator();
        String sedprev = "";
        String cvprev = "";
        MetaDataVersionProtocolBean protocol = metadata.getProtocol();
        Set<String> existcvd = new TreeSet<String>();
        while (it.hasNext()) {
            // study_event_difinition_oid, mandatory, se_name, repeating, type,

            // cv: oid, mandatory(required), name, repeating
            HashMap row = (HashMap) it.next();
            String sedOID = (String) row.get("sed_oid");
            String cvOID = (String) row.get("cv_oid");
            String sedName = (String) row.get("sed_name");
            Boolean sedRepeat = (Boolean) row.get("sed_repeating");
            String sedType = (String) row.get("sed_type");
            String cvName = (String) row.get("cv_name");
            Boolean cvRequired = (Boolean) row.get("cv_required");
            String crfName = (String) row.get("crf_name");

            StudyEventDefBean sedef = new StudyEventDefBean();
            if (!sedprev.equals(sedOID)) {
                sedprev = sedOID;
                ElementRefBean sedref = new ElementRefBean();
                sedref.setElementDefOID(sedOID);
                sedref.setMandatory("Yes");
                protocol.getStudyEventRefs().add(sedref);
                sedef.setOid(sedOID);
                sedef.setName(sedName);
                sedef.setRepeating(sedRepeat ? "Yes" : "No");
                String type = sedType.toLowerCase();
                type = type.substring(0, 1).toUpperCase() + type.substring(1);
                sedef.setType(type);
                metadata.getStudyEventDefs().add(sedef);
            } else {
                int p = metadata.getStudyEventDefs().size() - 1;
                sedef = metadata.getStudyEventDefs().get(p);
            }
            String key = sedOID + cvOID;
            if (!cvprev.equals(key)) {
                cvprev = key;
                ElementRefBean formref = new ElementRefBean();
                formref.setElementDefOID(cvOID);
                formref.setMandatory(cvRequired ? "Yes" : "NO");
                sedef.getFormRefs().add(formref);

                if (!existcvd.contains(cvOID)) {
                    existcvd.add(cvOID);
                    FormDefBean formdef = new FormDefBean();
                    formdef.setOid(cvOID);
                    formdef.setName(crfName + " - " + cvName);
                    formdef.setRepeating("No");
                    metadata.getFormDefs().add(formdef);
                }
            }
        }
    }

    public void getItemGroupAndItemMetaByCRFVersionOID(String crfVersionOID, FormDefBean formDef, MetaDataVersionBean metadata, HashMap<String, Integer> igpos,
            HashMap<String, Integer> itpos, HashMap<String, Integer> clpos) {
        this.unsetTypeExpected();
        this.setItemGroupAndItemMetaTypesExpected();

        HashMap<Integer, String> variables = new HashMap<Integer, String>();
        variables.put(1, crfVersionOID);

        ArrayList alist = this.select(digester.getQuery("getItemGroupAndItemMetaByCRFVersionOID"), variables);
        Iterator it = alist.iterator();
        ArrayList<ElementRefBean> itemGroupRefs = (ArrayList<ElementRefBean>) formDef.getItemGroupRefs();
        ArrayList<ItemGroupDefBean> itemGroupDefs = (ArrayList<ItemGroupDefBean>) metadata.getItemGroupDefs();
        ArrayList<ItemDefBean> itemDefs = (ArrayList<ItemDefBean>) metadata.getItemDefs();
        ArrayList<CodeListBean> codeLists = (ArrayList<CodeListBean>) metadata.getCodeLists();
        Set<String> igset = new TreeSet<String>();
        Set<String> itset = new TreeSet<String>();
        String igprev = "";
        while (it.hasNext()) {
            HashMap row = (HashMap) it.next();
            String igOID = (String) row.get("item_group_oid");
            String itOID = (String) row.get("item_oid");
            Integer rsId = (Integer) row.get("response_set_id");
            String igName = (String) row.get("item_group_name");
            String itName = (String) row.get("item_name");
            Integer dataTypeId = (Integer) row.get("item_data_type_id");
            String header = (String) row.get("header");
            String left = (String) row.get("left_item_text");
            String right = (String) row.get("right_item_text");
            Boolean itRequired = (Boolean) row.get("item_required");
            String regexp = (String) row.get("regexp");
            String regexpErr = (String) row.get("regexp_error_msg");
            Integer rsTypeId = (Integer) row.get("response_type_id");
            String rsText = (String) row.get("options_text");
            String rsValue = (String) row.get("options_values");
            String rsLabel = (String) row.get("response_label");

            String mandatory = itRequired ? "Yes" : "No";
            String key = igOID + "-" + crfVersionOID;
            ItemGroupDefBean igdef = new ItemGroupDefBean();
            if (!igprev.equals(key)) {
                igprev = key;
                if (!igset.contains(key)) {
                    igset.add(key);
                    ElementRefBean igref = new ElementRefBean();
                    igref.setElementDefOID(igOID + "-" + crfVersionOID);
                    igref.setMandatory(mandatory);
                    itemGroupRefs.add(igref);
                }
                if (!igpos.containsKey(key)) {
                    int p = igpos.size();
                    igpos.put(key, p);
                    igdef.setOid(igOID + "-" + crfVersionOID);
                    igdef.setName(igName);
                    igdef.setRepeating(igName.equalsIgnoreCase("ungrouped") ? "No" : "Yes");
                    igdef.setPreSASDatasetName(igName);
                    itemGroupDefs.add(igdef);
                }
            } else {
                int p = igpos.get(key);
                igdef = itemGroupDefs.get(p);
            }

            if (!itset.contains(key + itOID)) {
                itset.add(igOID + itOID);
                ElementRefBean itemRef = new ElementRefBean();
                itemRef.setElementDefOID(itOID);
                itemRef.setMandatory(mandatory);
                igdef.getItemRefs().add(itemRef);
            }

            boolean hasCode = MetaDataCollector.needCodeList(rsTypeId, dataTypeId);
            HashMap<String, String> codes = MetaDataCollector.parseCode(rsText, rsValue);
            String datatype = MetaDataCollector.getOdmItemDataType(dataTypeId);

            if (!itpos.containsKey(itOID)) {
                int pit = itpos.size();
                itpos.put(itOID, pit);
                ItemDefBean idef = new ItemDefBean();
                idef.setOid(itOID);
                idef.setName(itName);
                idef.setPreSASFieldName(itName);
                idef.setCodeListOID(hasCode ? "CL_" + rsId : "");
                idef.getQuestion().setText(MetaDataCollector.getItemQuestionText(header, left, right));
                if (regexp != null && regexp.startsWith("func:")) {
                    idef.setRangeCheck(MetaDataCollector.getItemRangeCheck(regexp.substring(5).trim(), metadata.getSoftHard(), regexpErr));
                }
                idef.setDataType(datatype);
                idef.setLength(MetaDataCollector.getDataTypeLength(datatype, new ArrayList<String>(codes.values()), hasCode));
                idef.setSignificantDigits(MetaDataCollector.getSignificantDigits(datatype, new ArrayList<String>(codes.values()), hasCode));
                itemDefs.add(idef);
            }

            if (hasCode && !clpos.containsKey(rsId + "")) {
                int pcl = clpos.size();
                clpos.put(rsId + "", pcl);
                CodeListBean cl = new CodeListBean();
                cl.setOid("CL_" + rsId);
                cl.setName(rsLabel);
                cl.setPreSASFormatName(rsLabel);
                cl.setDataType(datatype);
                Iterator<String> iter = codes.keySet().iterator();
                while (iter.hasNext()) {
                    String de = iter.next();
                    CodeListItemBean cli = new CodeListItemBean();
                    cli.setCodedValue(codes.get(de));
                    cli.getDecode().setText(de);
                    cl.getCodeListItems().add(cli);
                }
                codeLists.add(cl);
            }
        }
    }

    public void getClinicalData(StudyBean study, String datasetSql, OdmClinicalDataBean data) {
        HashMap<String, Integer> subpos = new HashMap<String, Integer>();
        String subprev = "";
        HashMap<String, Integer> sepos = new HashMap<String, Integer>();
        String seprev = "";
        HashMap<String, Integer> formpos = new HashMap<String, Integer>();
        String formprev = "";
        HashMap<String, Integer> igpos = new HashMap<String, Integer>();
        String igprev = "";

        String columns =
            "subject_id, study_subject_oid, study_event_definition_oid, study_event_definition_repeating, sample_ordinal, crf_version_oid, item_group_oid, "
                + "item_oid, item_group_name, item_data_ordinal, value, item_data_type_id";
        String sql = parseDatasetSql(datasetSql, columns);
        this.setClinicalDataTypesExpected();
        ArrayList viewRows = select(sql);
        Iterator iter = viewRows.iterator();
        HashMap<String, Integer> validSubs = new HashMap<String, Integer>();
        while (iter.hasNext()) {
            HashMap row = (HashMap) iter.next();

            Integer subjectId = (Integer) row.get("subject_id");
            String studySubjectLabel = (String) row.get("study_subject_oid");
            int studySubjectId = -1;
            if (!validSubs.containsKey(studySubjectLabel)) {
                // make sure a StudySubject belong to current study which could
                // be a site
                // otherwise it is not necessary to extract this StudySubject.
                // if currentStudy is a study not a site, the all subjects
                // belong to
                // its sites will be got
                StudySubjectDAO ssdao = new StudySubjectDAO(this.ds);
                StudySubjectBean ssbean = ssdao.findBySubjectIdAndStudy(subjectId.intValue(), study);
                studySubjectId = ssbean.getId();
                if (studySubjectId > 0) {
                    validSubs.put(studySubjectLabel, studySubjectId);
                }
            }
            if (validSubs.containsKey(studySubjectLabel)) {
                String sedOID = (String) row.get("study_event_definition_oid");
                Boolean studyEventRepeating = (Boolean) row.get("study_event_definition_repeating");
                Integer sampleOrdinal = (Integer) row.get("sample_ordinal");
                String cvOID = (String) row.get("crf_version_oid");
                String igOID = (String) row.get("item_group_oid");
                String itOID = (String) row.get("item_oid");
                String igName = (String) row.get("item_group_name");
                Integer itDataOrdinal = (Integer) row.get("item_data_ordinal");
                String itValue = (String) row.get("value");
                Integer datatypeid = (Integer) row.get("item_data_type_id");

                if (itValue != null && itValue.length() > 0) {
                    String key = studySubjectLabel;
                    SubjectDataBean sub = new SubjectDataBean();
                    if (!subprev.equals(studySubjectLabel)) {
                        subprev = studySubjectLabel;
                        sub.setSubjectOID(studySubjectLabel);
                        data.getSubjectData().add(sub);
                    } else {
                        int p = data.getSubjectData().size() - 1;
                        sub = data.getSubjectData().get(p);
                    }
                    StudyEventDataBean se = new StudyEventDataBean();
                    key += sedOID + sampleOrdinal;
                    if (!seprev.equals(key)) {
                        seprev = key;
                        se.setStudyEventOID(sedOID);
                        se.setStudyEventRepeatKey(studyEventRepeating ? sampleOrdinal + "" : "-1");
                        sub.getStudyEventData().add(se);
                    } else {
                        int p = sub.getStudyEventData().size() - 1;
                        se = sub.getStudyEventData().get(p);
                    }
                    FormDataBean form = new FormDataBean();
                    key += cvOID;
                    if (!formprev.equals(key)) {
                        formprev = key;
                        form.setFormOID(cvOID);
                        se.getFormData().add(form);
                    } else {
                        int p = se.getFormData().size() - 1;
                        form = se.getFormData().get(p);
                    }
                    ImportItemGroupDataBean ig = new ImportItemGroupDataBean();
                    key += igOID + "-" + cvOID + itDataOrdinal;
                    if (!igprev.equals(key)) {
                        igprev = key;
                        ig.setItemGroupOID(igOID + "-" + cvOID);
                        ig.setItemGroupRepeatKey(igName.equalsIgnoreCase("ungrouped") ? "-1" : itDataOrdinal + "");
                        form.getItemGroupData().add(ig);
                    } else {
                        int p = form.getItemGroupData().size() - 1;
                        ig = form.getItemGroupData().get(p);
                    }
                    // item should be distinct
                    ImportItemDataBean it = new ImportItemDataBean();
                    it.setItemOID(itOID);
                    it.setTransactionType("Insert");
                    if (datatypeid == 9) {
                        try {
                            itValue = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("MM/dd/yyyy").parse(itValue));
                        } catch (Exception fe) {
                            logger.info("Item -" + itOID + " value " + itValue + " can not be converted to ODM date format.");
                            return;
                        }
                    }
                    it.setValue(itValue);
                    ig.getItemData().add(it);
                }
            }
        }
    }

    protected String parseDatasetSql(String datasetSql, String columns) {
        if (!datasetSql.contains("*")) {
            logger.info("dataset sql doesn't contain *.");
            return new String();
        }
        if (!datasetSql.contains("order")) {
            logger.info("dataset sql doesn't contain the word \"order\"");
            return new String();
        }
        String s = datasetSql;
        s = s.replace("*", columns);
        s = s.split("order")[0].trim() + ";";
        return s;
    }
}
