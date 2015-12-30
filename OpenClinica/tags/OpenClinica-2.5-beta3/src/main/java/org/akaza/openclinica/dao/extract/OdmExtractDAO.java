/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.extract;

import org.akaza.openclinica.bean.managestudy.StudyBean;
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
import org.akaza.openclinica.bean.odmbeans.TranslatedTextBean;
import org.akaza.openclinica.bean.submit.crfdata.FormDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemGroupDataBean;
import org.akaza.openclinica.bean.submit.crfdata.StudyEventDataBean;
import org.akaza.openclinica.bean.submit.crfdata.SubjectDataBean;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.logic.odmExport.ClinicalDataCollector;
import org.akaza.openclinica.logic.odmExport.MetaDataCollector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.sql.DataSource;

/**
 * 
 * @author ywang (May, 2008)
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
        this.setTypeExpected(1, TypeNames.INT);// definition_order
        this.setTypeExpected(2, TypeNames.INT); // crf_order
        this.setTypeExpected(3, TypeNames.INT); // edc.crf_id
        this.setTypeExpected(4, TypeNames.INT); // cv.crf_version_id
        this.setTypeExpected(5, TypeNames.STRING);// definition_oid
        this.setTypeExpected(6, TypeNames.STRING);// definition_name
        this.setTypeExpected(7, TypeNames.BOOL);// definition_repeating
        this.setTypeExpected(8, TypeNames.STRING);// definition_type
        this.setTypeExpected(9, TypeNames.STRING);// cv_oid
        this.setTypeExpected(10, TypeNames.STRING);// cv_name
        this.setTypeExpected(11, TypeNames.BOOL);// cv_required
        this.setTypeExpected(12, TypeNames.STRING);// null_values
        this.setTypeExpected(13, TypeNames.STRING);// crf_name
    }

    public void setItemDataMaxLengthTypesExpected() {
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT);// item_id
        this.setTypeExpected(2, TypeNames.INT);// max_length
    }

    public void setItemGroupAndItemMetaTypesExpected() {
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT);// crf_version_id
        this.setTypeExpected(2, TypeNames.INT);// item_group_id
        this.setTypeExpected(3, TypeNames.INT);// item_id
        this.setTypeExpected(4, TypeNames.INT);// response_set_id
        this.setTypeExpected(5, TypeNames.STRING);// crf_version_oid
        this.setTypeExpected(6, TypeNames.STRING);// item_group_oid
        this.setTypeExpected(7, TypeNames.STRING);// item_oid
        this.setTypeExpected(8, TypeNames.STRING); // item_group_name
        this.setTypeExpected(9, TypeNames.STRING);// item_name
        this.setTypeExpected(10, TypeNames.INT);// item_data_type_id
        this.setTypeExpected(11, TypeNames.STRING);// item_header
        this.setTypeExpected(12, TypeNames.STRING);// left_item_text
        this.setTypeExpected(13, TypeNames.STRING);// right_item_text
        this.setTypeExpected(14, TypeNames.BOOL);// item_required
        this.setTypeExpected(15, TypeNames.STRING);// regexp
        this.setTypeExpected(16, TypeNames.STRING);// regexp_error_msg
        this.setTypeExpected(17, TypeNames.INT);// response_type_id
        this.setTypeExpected(18, TypeNames.STRING);// options_text
        this.setTypeExpected(19, TypeNames.STRING);// options_values
        this.setTypeExpected(20, TypeNames.STRING);// response_label
        this.setTypeExpected(21, TypeNames.STRING);// item_group_header
        this.setTypeExpected(22, TypeNames.STRING);// item_description
    }

    public void setSubjectEventFormDataTypesExpected() {
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.STRING);// study_subject_oid;
        this.setTypeExpected(2, TypeNames.INT);// definition_order;
        this.setTypeExpected(3, TypeNames.STRING);// definition_oid;
        this.setTypeExpected(4, TypeNames.BOOL);// definition_repeating
        this.setTypeExpected(5, TypeNames.INT);// sample_ordinal
        this.setTypeExpected(6, TypeNames.INT);// crf_order;
        this.setTypeExpected(7, TypeNames.STRING);// crf_version_oid
        this.setTypeExpected(8, TypeNames.INT);// event_crf_id
    }

    public void setEventGroupItemDataTypesExpected() {
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT);// event_crf_id;
        this.setTypeExpected(2, TypeNames.INT);// item_group_id;
        this.setTypeExpected(3, TypeNames.STRING);// item_group_oid
        this.setTypeExpected(4, TypeNames.STRING);// item_group_name
        this.setTypeExpected(5, TypeNames.INT);// item_id
        this.setTypeExpected(6, TypeNames.STRING);// item_oid
        this.setTypeExpected(7, TypeNames.INT);// item_data_ordinal
        this.setTypeExpected(8, TypeNames.STRING);// value
        this.setTypeExpected(9, TypeNames.INT);// item_data_type_id
    }

    public void setEventCrfIdsByItemDataTypesExpected() {
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT);// event_crf_id;
    }

    public HashSet<Integer> getMetadata(int studyId, MetaDataVersionBean metadata) {
        HashSet<Integer> nullClSet = new HashSet<Integer>();
        String cvIds = "";
        HashMap<Integer, Integer> cvIdPoses = new HashMap<Integer, Integer>();
        this.setStudyEventAndFormMetaTypesExpected();
        logger.debug("Begin to execute GetStudyEventAndFormMetaSql");
        ArrayList rows = this.select(this.getStudyEventAndFormMetaSql(studyId));
        Iterator it = rows.iterator();
        String sedprev = "";
        MetaDataVersionProtocolBean protocol = metadata.getProtocol();
        HashMap<Integer, String> nullMap = new HashMap<Integer, String>();
        while (it.hasNext()) {
            HashMap row = (HashMap) it.next();
            Integer sedOrder = (Integer) row.get("definition_order");
            Integer cvId = (Integer) row.get("crf_version_id");
            String sedOID = (String) row.get("definition_oid");
            String sedName = (String) row.get("definition_name");
            Boolean sedRepeat = (Boolean) row.get("definition_repeating");
            String sedType = (String) row.get("definition_type");
            String cvOID = (String) row.get("cv_oid");
            String cvName = (String) row.get("cv_name");
            Boolean cvRequired = (Boolean) row.get("cv_required");
            String nullValue = (String) row.get("null_values");
            String crfName = (String) row.get("crf_name");

            StudyEventDefBean sedef = new StudyEventDefBean();
            if (sedprev.equals(sedOID)) {
                int p = metadata.getStudyEventDefs().size() - 1;
                sedef = metadata.getStudyEventDefs().get(p);
            } else {
                sedprev = sedOID;
                ElementRefBean sedref = new ElementRefBean();
                sedref.setElementDefOID(sedOID);
                sedref.setMandatory("Yes");
                sedref.setOrderNumber(sedOrder);
                protocol.getStudyEventRefs().add(sedref);
                sedef.setOid(sedOID);
                sedef.setName(sedName);
                sedef.setRepeating(sedRepeat ? "Yes" : "No");
                String type = sedType.toLowerCase();
                type = type.substring(0, 1).toUpperCase() + type.substring(1);
                sedef.setType(type);
                metadata.getStudyEventDefs().add(sedef);
            }
            ElementRefBean formref = new ElementRefBean();
            formref.setElementDefOID(cvOID);
            formref.setMandatory(cvRequired ? "Yes" : "No");
            sedef.getFormRefs().add(formref);

            if (!cvIdPoses.containsKey(cvId)) {
                FormDefBean formdef = new FormDefBean();
                formdef.setOid(cvOID);
                formdef.setName(crfName + " - " + cvName);
                formdef.setRepeating("No");
                metadata.getFormDefs().add(formdef);
                cvIdPoses.put(cvId, metadata.getFormDefs().size() - 1);
                cvIds += cvId + ",";
                if (nullValue != null && nullValue.length() > 0) {
                    nullMap.put(cvId, nullValue);
                }
            }
        }
        cvIds = cvIds.substring(0, cvIds.length() - 1);

        HashMap<Integer, Integer> maxLengths = new HashMap<Integer, Integer>();
        this.setItemDataMaxLengthTypesExpected();
        rows.clear();
        logger.debug("Begin to execute GetItemDataMaxLengths");
        rows = select(this.getItemDataMaxLengths(cvIds));
        it = rows.iterator();
        while (it.hasNext()) {
            HashMap row = (HashMap) it.next();
            maxLengths.put((Integer) row.get("item_id"), (Integer) row.get("max_length"));
        }

        this.setItemGroupAndItemMetaTypesExpected();
        rows.clear();
        logger.debug("Begin to execute GetItemGroupAndItemMetaSql");
        rows = select(this.getItemGroupAndItemMetaSql(cvIds));
        it = rows.iterator();
        ArrayList<ItemGroupDefBean> itemGroupDefs = (ArrayList<ItemGroupDefBean>) metadata.getItemGroupDefs();
        ArrayList<ItemDefBean> itemDefs = (ArrayList<ItemDefBean>) metadata.getItemDefs();
        ArrayList<CodeListBean> codeLists = (ArrayList<CodeListBean>) metadata.getCodeLists();
        ArrayList<ElementRefBean> itemGroupRefs = new ArrayList<ElementRefBean>();
        Set<String> igset = new HashSet<String>();
        Set<String> igdset = new HashSet<String>();
        Set<String> itset = new HashSet<String>();
        Set<Integer> itdset = new HashSet<Integer>();
        Set<Integer> clset = new HashSet<Integer>();
        int cvprev = -1;
        String igprev = "";
        String itMandatory = "No";
        int itOrder = 0;
        while (it.hasNext()) {
            HashMap row = (HashMap) it.next();
            Integer cvId = (Integer) row.get("crf_version_id");
            Integer igId = (Integer) row.get("item_group_id");
            Integer itId = (Integer) row.get("item_id");
            Integer rsId = (Integer) row.get("response_set_id");
            String cvOID = (String) row.get("crf_version_oid");
            String igOID = (String) row.get("item_group_oid");
            String itOID = (String) row.get("item_oid");
            String igName = (String) row.get("item_group_name");
            String itName = (String) row.get("item_name");
            Integer dataTypeId = (Integer) row.get("item_data_type_id");
            String header = (String) row.get("item_header");
            String left = (String) row.get("left_item_text");
            String right = (String) row.get("right_item_text");
            Boolean itRequired = (Boolean) row.get("item_required");
            String regexp = (String) row.get("regexp");
            String regexpErr = (String) row.get("regexp_error_msg");
            Integer rsTypeId = (Integer) row.get("response_type_id");
            String rsText = (String) row.get("options_text");
            String rsValue = (String) row.get("options_values");
            String rsLabel = (String) row.get("response_label");
            String igHeader = (String) row.get("item_group_header");
            String itDesc = (String) row.get("item_description");
            if (cvprev != cvId) {
                // at this point, itemGroupRefs is for old cvId
                if (itemGroupRefs != null && itemGroupRefs.size() > 0) {
                    itemGroupRefs.get(itemGroupRefs.size() - 1).setMandatory(itMandatory);
                }
                // now update to new cvId
                cvprev = cvId;
                FormDefBean formDef = new FormDefBean();
                if (cvIdPoses.containsKey(cvId)) {
                    int p = cvIdPoses.get(cvId);
                    formDef = metadata.getFormDefs().get(p);
                } else {
                    logger.debug("crf_version_id=" + cvId + " couldn't find from studyEventAndFormMetaSql");
                }
                itemGroupRefs = (ArrayList<ElementRefBean>) formDef.getItemGroupRefs();
            }

            String mandatory = itRequired ? "Yes" : "No";
            // String key = igOID + "-" + cvOID;
            String key = igId + "-" + cvId;
            ItemGroupDefBean igdef = new ItemGroupDefBean();
            if (igprev.equals(key)) {
                itMandatory = itRequired && "No".equals(itMandatory) ? "Yes" : itMandatory;
                igdef = itemGroupDefs.get(itemGroupDefs.size() - 1);
            } else {
                itOrder = 0;
                igprev = key;
                if (!igset.contains(key)) {
                    igset.add(key);
                    ElementRefBean igref = new ElementRefBean();
                    igref.setElementDefOID(igOID + "-" + cvOID);
                    int size = itemGroupRefs.size();
                    if (size > 0) {
                        itemGroupRefs.get(size - 1).setMandatory(itMandatory);
                    }
                    itemGroupRefs.add(igref);
                }
                if (!igdset.contains(key)) {
                    igdset.add(key);
                    igdef.setOid(igOID + "-" + cvOID);
                    igdef.setName(igName);
                    igdef.setRepeating("ungrouped".equalsIgnoreCase(igName) ? "No" : "Yes");
                    igdef.setComment(igHeader);
                    igdef.setPreSASDatasetName(igName.toUpperCase());
                    itemGroupDefs.add(igdef);
                }
            }

            if (!itset.contains(key + itId)) {
                ++itOrder;
                itset.add(key + itId);
                ElementRefBean itemRef = new ElementRefBean();
                itemRef.setElementDefOID(itOID);
                itemRef.setMandatory(mandatory);
                itemRef.setOrderNumber(itOrder);
                igdef.getItemRefs().add(itemRef);
            }

            boolean hasCode = MetaDataCollector.needCodeList(rsTypeId, dataTypeId);
            LinkedHashMap<String, String> codes = new LinkedHashMap<String, String>();
            if (hasCode) {
                if (nullMap.containsKey(cvId)) {
                    codes = MetaDataCollector.parseCode(rsText, rsValue, nullMap.get(cvId));
                    nullClSet.add(itId);
                } else {
                    codes = MetaDataCollector.parseCode(rsText, rsValue);
                }
                // no action has been taken if rsvalue/rstext go wrong,
                // since they have been validated when uploading crf.
            }
            String datatype = MetaDataCollector.getOdmItemDataType(rsTypeId, dataTypeId);

            if (!itdset.contains(itId)) {
                itdset.add(itId);
                ItemDefBean idef = new ItemDefBean();
                idef.setOid(itOID);
                idef.setName(itName);
                idef.setComment(itDesc);
                idef.setPreSASFieldName(itName);
                idef.setCodeListOID(hasCode ? "CL_" + rsId : "");
                idef.getQuestion().setText(MetaDataCollector.getItemQuestionText(header, left, right));
                if (regexp != null && regexp.startsWith("func:")) {
                    idef.setRangeCheck(MetaDataCollector.getItemRangeCheck(regexp.substring(5).trim(), metadata.getSoftHard(), regexpErr));
                }
                idef.setDataType(datatype);
                if (rsTypeId == 3 || rsTypeId == 7) {// 3:checkbox;
                    // //7:multi-select
                    int len = maxLengths.containsKey(itId) ? maxLengths.get(itId) : 0;
                    len = Math.max(len, Math.max(rsText.length(), rsValue.length()));
                    idef.setLength(len);
                } else if ("text".equalsIgnoreCase(datatype)) {
                    idef.setLength(hasCode ? MetaDataCollector.getDataTypeLength(new ArrayList<String>(codes.values())) : maxLengths.containsKey(itId)
                        ? maxLengths.get(itId) : MetaDataCollector.getTextLength());
                } else if ("integer".equalsIgnoreCase(datatype)) {
                    idef.setLength(hasCode ? MetaDataCollector.getDataTypeLength(new ArrayList<String>(codes.values())) : 10);
                } else if ("float".equalsIgnoreCase(datatype)) {
                    idef.setLength(hasCode ? MetaDataCollector.getDataTypeLength(new ArrayList<String>(codes.values())) : 32);
                } else {
                    idef.setLength(0);
                }
                idef.setSignificantDigits(MetaDataCollector.getSignificantDigits(datatype, new ArrayList<String>(codes.values()), hasCode));
                itemDefs.add(idef);
            }

            if (hasCode && !clset.contains(rsId)) {
                clset.add(rsId);
                CodeListBean cl = new CodeListBean();
                cl.setOid("CL_" + rsId);
                cl.setName(rsLabel);
                cl.setPreSASFormatName(rsLabel);
                cl.setDataType(datatype);
                Iterator<String> iter = codes.keySet().iterator();
                while (iter.hasNext()) {
                    String de = iter.next();
                    CodeListItemBean cli = new CodeListItemBean();
                    cli.setCodedValue(de);
                    TranslatedTextBean tt = cli.getDecode();
                    // cli.getDecode().setText(codes.get(de));
                    tt.setText(codes.get(de));
                    tt.setXmlLang(SQLInitServlet.getField("translated_text_language"));
                    cli.setDecode(tt);
                    cl.getCodeListItems().add(cli);
                }
                codeLists.add(cl);
            }
        }
        itemGroupRefs.get(itemGroupRefs.size() - 1).setMandatory(itMandatory);
        return nullClSet;
    }

    public void getClinicalData(StudyBean study, String datasetSql, Set<Integer> nullClSet, OdmClinicalDataBean data) {
        String dbName = SQLInitServlet.getDBName();
        String subprev = "";
        HashMap<String, Integer> sepos = new HashMap<String, Integer>();
        String seprev = "";
        String formprev = "";
        HashMap<String, Integer> igpos = new HashMap<String, Integer>();
        String igprev = "";
        String oidPos = "";
        HashMap<Integer, String> oidPoses = new HashMap<Integer, String>();
        String studyIds = "";
        if (study.getParentStudyId() > 0) {
            studyIds += study.getId();
        } else {
            ArrayList<Integer> ids = (ArrayList<Integer>) (new StudyDAO(this.ds)).findAllSiteIdsByStudy(study);
            for (int i = 0; i < ids.size() - 1; ++i) {
                studyIds += ids.get(i) + ",";
            }
            studyIds += ids.get(ids.size() - 1);
        }
        String sql = datasetSql.split("order by")[0].trim();
        sql = sql.split("study_event_definition_id in")[1];
        String[] ss = sql.split("and item_id in");
        String sedIds = ss[0];
        String[] sss = ss[1].split("and");
        String itemIds = sss[0];
        String dateConstraint = "";
        if ("postgres".equalsIgnoreCase(dbName)) {
            dateConstraint = "and " + sss[1] + " and " + sss[2];
            dateConstraint = dateConstraint.replace("date_created", "ss.enrollment_date");
        } else if ("oracle".equalsIgnoreCase(dbName)) {
            String[] os = (sss[1] + sss[2]).split("'");
            dateConstraint = "and trunc(ss.enrollment_date) >= to_date('" + os[1] + "') and trunc(ss.enrollment_date) <= to_date('" + os[3] + "')";
        }

        this.setSubjectEventFormDataTypesExpected();
        logger.debug("Begin to GetSubjectEventFormSql");
        ArrayList viewRows = select(getSubjectEventFormSql(studyIds, sedIds, itemIds, dateConstraint));
        Iterator iter = viewRows.iterator();
        while (iter.hasNext()) {
            HashMap row = (HashMap) iter.next();
            String studySubjectLabel = (String) row.get("study_subject_oid");
            String sedOID = (String) row.get("definition_oid");
            Boolean studyEventRepeating = (Boolean) row.get("definition_repeating");
            Integer sampleOrdinal = (Integer) row.get("sample_ordinal");
            String cvOID = (String) row.get("crf_version_oid");
            Integer ecId = (Integer) row.get("event_crf_id");// ecId should
            // be unique;

            String key = studySubjectLabel;
            SubjectDataBean sub = new SubjectDataBean();
            if (subprev.equals(studySubjectLabel)) {
                int p = data.getSubjectData().size() - 1;
                sub = data.getSubjectData().get(p);
            } else {
                subprev = studySubjectLabel;
                sub.setSubjectOID(studySubjectLabel);
                data.getSubjectData().add(sub);
                seprev = "";
                formprev = "";
                igprev = "";
            }
            oidPos = data.getSubjectData().size() - 1 + "";
            StudyEventDataBean se = new StudyEventDataBean();
            // key += sedOID + sampleOrdinal;
            key += sedOID;
            if (!seprev.equals(key) || !sepos.containsKey(key + sampleOrdinal)) {
                sepos.put(key + sampleOrdinal, sub.getStudyEventData().size());
                seprev = key;
                se.setStudyEventOID(sedOID);
                se.setStudyEventRepeatKey(studyEventRepeating ? sampleOrdinal + "" : "-1");
                sub.getStudyEventData().add(se);
                formprev = "";
                igprev = "";
            } else {
                se = sub.getStudyEventData().get(sepos.get(key + sampleOrdinal));
            }
            oidPos += "---" + (sub.getStudyEventData().size() - 1);
            FormDataBean form = new FormDataBean();
            key += cvOID;
            if (formprev.equals(key)) {
                form = se.getFormData().get(se.getFormData().size() - 1);
            } else {
                formprev = key;
                form.setFormOID(cvOID);
                se.getFormData().add(form);
                igprev = "";
            }
            oidPos += "---" + (se.getFormData().size() - 1);
            // ecId should be distinct
            oidPoses.put(ecId, oidPos);
            oidPos = "";
        }

        this.setEventGroupItemDataTypesExpected();
        viewRows.clear();
        logger.debug("Begin to GetEventGroupItemSql");
        viewRows = select(getEventGroupItemSql(studyIds, sedIds, itemIds, dateConstraint));
        iter = viewRows.iterator();
        SubjectDataBean sub = new SubjectDataBean();
        StudyEventDataBean se = new StudyEventDataBean();
        FormDataBean form = new FormDataBean();
        int ecprev = -1;
        igprev = "";
        boolean goon = true;
        String itprev = "";
        while (iter.hasNext()) {
            HashMap row = (HashMap) iter.next();
            Integer ecId = (Integer) row.get("event_crf_id");
            Integer igId = (Integer) row.get("item_group_id");
            String igOID = (String) row.get("item_group_oid");
            String igName = (String) row.get("item_group_name");
            Integer itId = (Integer) row.get("item_id");
            String itOID = (String) row.get("item_oid");
            Integer itDataOrdinal = (Integer) row.get("item_data_ordinal");
            String itValue = (String) row.get("value");
            Integer datatypeid = (Integer) row.get("item_data_type_id");
            String key = "";
            if (ecId != ecprev) {
                logger.error("Found ecId=" + ecId + " in subjectEventFormSql is:" + oidPoses.containsKey(ecId));
                if (oidPoses.containsKey(ecId)) {
                    goon = true;
                    String[] poses = oidPoses.get(ecId).split("---");
                    sub = data.getSubjectData().get(Integer.valueOf(poses[0]));
                    se = sub.getStudyEventData().get(Integer.valueOf(poses[1]));
                    form = se.getFormData().get(Integer.valueOf(poses[2]));
                } else {
                    goon = false;
                }
                ecprev = ecId;
            }
            if (goon) {
                ImportItemGroupDataBean ig = new ImportItemGroupDataBean();
                // key = ecId + igOID;
                key = ecId + "-" + igId;
                if (!igprev.equals(key) || !igpos.containsKey(key + itDataOrdinal)) {
                    igpos.put(key + itDataOrdinal, form.getItemGroupData().size());
                    igprev = key;
                    ig.setItemGroupOID(igOID + "-" + form.getFormOID());
                    ig.setItemGroupRepeatKey("ungrouped".equalsIgnoreCase(igName) ? "-1" : itDataOrdinal + "");
                    form.getItemGroupData().add(ig);
                } else {
                    ig = form.getItemGroupData().get(igpos.get(key + itDataOrdinal));
                }

                // item should be distinct; but duplicated item data have
                // been reported because "save" have been clicked twice.
                // those duplicated item data have been arranged together by
                // "distinct" because of their same
                // ecId+igOID+itOID+itDataOrdinal (08-2008)
                key = itId + itDataOrdinal + key;
                if (!itprev.equals(key)) {
                    itprev = key;
                    ImportItemDataBean it = new ImportItemDataBean();
                    it.setItemOID(itOID);
                    it.setTransactionType("Insert");
                    if (datatypeid == 9) {
                        try {
                            itValue = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("MM/dd/yyyy").parse(itValue));
                        } catch (Exception fe) {
                            logger.info("Item -" + itOID + " value " + itValue + " can not be converted to ODM date format.");
                            data.getSubjectData().clear();
                            SubjectDataBean subj = new SubjectDataBean();
                            subj.setSubjectOID("!!!Error: Item -" + itOID + " with value as " + itValue + " can not be converted to ODM date format.");
                            data.getSubjectData().add(subj);
                            return;
                        }
                    }
                    if (nullClSet.contains(itId) && ClinicalDataCollector.getNullValueMap().containsKey(itValue.trim().toUpperCase())) {
                        itValue = ClinicalDataCollector.getNullValueMap().get(itValue.trim().toUpperCase());
                    }
                    it.setValue(itValue);
                    ig.getItemData().add(it);
                }
            }
        }
    }

    protected String getStudyEventAndFormMetaSql(int studyId) {
        return "select sed.ordinal as definition_order, edc.ordinal as crf_order, edc.crf_id, cv.crf_version_id,"
            + " sed.oc_oid as definition_oid, sed.name as definition_name, sed.repeating as definition_repeating,"
            + " sed.\"type\" as definition_type, cv.oc_oid as cv_oid,"
            + " cv.name as cv_name, edc.required_crf as cv_required, edc.null_values, crf.name as crf_name"
            + " from study_event_definition sed, event_definition_crf edc, crf, crf_version cv where sed.study_id = " + studyId + " and sed.status_id = 1"
            + " and sed.study_event_definition_id = edc.study_event_definition_id"
            + " and edc.status_id = 1 and edc.crf_id = crf.crf_id and crf.status_id = 1 and crf.crf_id = cv.crf_id and cv.status_id = 1"
            + " and exists (select ifm.crf_version_id from item_form_metadata ifm, item_group_metadata igm"
            + " where cv.crf_version_id = ifm.crf_version_id and cv.crf_version_id = igm.crf_version_id and ifm.item_id = igm.item_id)"
            + " order by sed.ordinal, edc.ordinal, edc.crf_id, cv.crf_version_id";
    }

    protected String getItemDataMaxLengths(String crfVersionIds) {
        return "select item_id, max(length(value)) as max_length from item_data where item_id in ("
            + " select distinct ifm.item_id from item_form_metadata ifm where ifm.crf_version_id in (" + crfVersionIds
            + ")) and length(value) > 0 group by item_id";
    }

    protected String getItemGroupAndItemMetaSql(String crfVersionIds) {
        return "select cv.crf_version_id,"
            + " ig.item_group_id, item.item_id, rs.response_set_id, cv.oc_oid as crf_version_oid, ig.oc_oid as item_group_oid, item.oc_oid as item_oid,"
            + " ig.name as item_group_name, item.name as item_name, item.item_data_type_id, ifm.item_header, ifm.left_item_text,"
            + " ifm.right_item_text, ifm.required as item_required, ifm.regexp, ifm.regexp_error_msg,"
            + " rs.response_type_id, rs.options_text, rs.options_values, rs.label as response_label,"
            + " igm.item_group_header, item.description as item_description from crf_version cv,"
            + " (select crf_version_id, item_id, response_set_id, \"header\" as item_header, left_item_text, right_item_text, required, regexp,"
            + " regexp_error_msg from item_form_metadata where crf_version_id in (" + crfVersionIds + "))ifm, item, response_set rs,"
            + " (select crf_version_id, item_group_id, item_id, \"header\" as item_group_header from item_group_metadata where crf_version_id in ("
            + crfVersionIds + "))igm," + " item_group ig where cv.crf_version_id in (" + crfVersionIds + ") and cv.crf_version_id = ifm.crf_version_id"
            + " and ifm.item_id = item.item_id and ifm.response_set_id = rs.response_set_id"
            + " and ifm.item_id = igm.item_id and cv.crf_version_id = igm.crf_version_id and igm.item_group_id = ig.item_group_id"
            + " order by cv.crf_version_id, ig.item_group_id, item.item_id, rs.response_set_id";
    }

    protected String getSubjectEventFormSql(String studyIds, String sedIds, String itemIds, String dateConstraint) {
        return "select ss.oc_oid as study_subject_oid, sed.ordinal as definition_order, sed.oc_oid as definition_oid,"
            + " sed.repeating as definition_repeating, se.sample_ordinal as sample_ordinal, edc.ordinal as crf_order, "
            + " cv.oc_oid as crf_version_oid, ec.event_crf_id from (select study_event_id, study_event_definition_id, study_subject_id,"
            + " sample_ordinal from study_event where study_event_definition_id in " + sedIds
            + " and study_subject_id in (select ss.study_subject_id from study_subject ss where ss.study_id in (" + studyIds + ") " + dateConstraint
            + ")) se, (select ss.oc_oid, ss.study_subject_id from study_subject ss where ss.study_id in (" + studyIds + ") " + dateConstraint + ") ss,"
            + " study_event_definition sed, event_definition_crf edc,"
            + " (select event_crf_id, crf_version_id, study_event_id from event_crf where event_crf_id in ("
            + getEventCrfIdsByItemDataSql(studyIds, sedIds, itemIds, dateConstraint) + ")) ec, crf_version cv" + " where sed.study_event_definition_id in "
            + sedIds + " and sed.study_event_definition_id = se.study_event_definition_id and se.study_subject_id = ss.study_subject_id"
            + " and sed.study_event_definition_id = edc.study_event_definition_id and se.study_event_id = ec.study_event_id"
            + " and edc.crf_id = cv.crf_id and ec.crf_version_id = cv.crf_version_id order by ss.oc_oid, sed.ordinal, se.sample_ordinal, edc.ordinal";
    }

    protected String getEventGroupItemSql(String studyIds, String sedIds, String itemIds, String dateConstraint) {
        return "select cvidata.event_crf_id, ig.item_group_id, ig.oc_oid as item_group_oid, ig.name as item_group_name,"
            + " cvidata.item_id, cvidata.item_oid, cvidata.item_data_ordinal, cvidata.value, cvidata.item_data_type_id"
            + " from (select ec.event_crf_id, ec.crf_version_id, item.item_id, item.oc_oid as item_oid,"
            + " idata.ordinal as item_data_ordinal, idata.value as value, item.item_data_type_id from item,"
            + " (select event_crf_id, item_id, ordinal, value from item_data where (status_id = 2 OR status_id = 6)"
            + " and event_crf_id in (select distinct event_crf_id from event_crf where study_subject_id in (select distinct"
            + " ss.study_subject_id from study_subject ss where ss.study_id in (" + studyIds + ") " + dateConstraint + ") and study_event_id"
            + " in (select distinct study_event_id from study_event" + " where study_event_definition_id in " + sedIds + " and study_subject_id in ("
            + " select distinct ss.study_subject_id from study_subject ss where ss.study_id in (" + studyIds + ") " + dateConstraint + "))))idata,"
            + " (select event_crf_id, crf_version_id from event_crf where status_id = 2 or status_id = 6)ec" + " where item.item_id in " + itemIds
            + " and length(idata.value) > 0 and item.item_id = idata.item_id and idata.event_crf_id = ec.event_crf_id"
            + " order by ec.event_crf_id, ec.crf_version_id, item.item_id, idata.ordinal) cvidata, item_group_metadata igm,"
            + " item_group ig where cvidata.crf_version_id = igm.crf_version_id and cvidata.item_id = igm.item_id"
            + " and igm.item_group_id = ig.item_group_id order by cvidata.event_crf_id, ig.item_group_id, cvidata.item_id, cvidata.item_data_ordinal";
    }

    protected String getEventCrfIdsByItemDataSql(String studyIds, String sedIds, String itemIds, String dateConstraint) {
        return "select distinct idata.event_crf_id from item_data idata" + " where idata.item_id in " + itemIds
            + " and (idata.status_id = 2 or idata.status_id = 6)" + " and idata.event_crf_id in (select event_crf_id from event_crf where study_subject_id in"
            + " (select ss.study_subject_id from study_subject ss WHERE ss.study_id in (" + studyIds + ") " + dateConstraint + ")"
            + " and study_event_id in (select study_event_id from study_event where study_event_definition_id in " + sedIds
            + " and study_subject_id in (select ss.study_subject_id from study_subject ss where ss.study_id in (" + studyIds + ") " + dateConstraint + "))"
            + " and (status_id = 2 or status_id = 6)) and length(value) > 0";
    }
}
