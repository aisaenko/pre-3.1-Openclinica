/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.extract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.odmbeans.BasicDefinitionsBean;
import org.akaza.openclinica.bean.odmbeans.CodeListBean;
import org.akaza.openclinica.bean.odmbeans.CodeListItemBean;
import org.akaza.openclinica.bean.odmbeans.ElementRefBean;
import org.akaza.openclinica.bean.odmbeans.FormDefBean;
import org.akaza.openclinica.bean.odmbeans.ItemDefBean;
import org.akaza.openclinica.bean.odmbeans.ItemGroupDefBean;
import org.akaza.openclinica.bean.odmbeans.MeasurementUnitBean;
import org.akaza.openclinica.bean.odmbeans.MetaDataVersionBean;
import org.akaza.openclinica.bean.odmbeans.MetaDataVersionProtocolBean;
import org.akaza.openclinica.bean.odmbeans.OdmClinicalDataBean;
import org.akaza.openclinica.bean.odmbeans.StudyEventDefBean;
import org.akaza.openclinica.bean.odmbeans.StudyGroupClassListBean;
import org.akaza.openclinica.bean.odmbeans.StudyGroupItemBean;
import org.akaza.openclinica.bean.odmbeans.SymbolBean;
import org.akaza.openclinica.bean.odmbeans.TranslatedTextBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.bean.submit.crfdata.ExportFormDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ExportStudyEventDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ExportSubjectDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemGroupDataBean;
import org.akaza.openclinica.bean.submit.crfdata.SubjectGroupDataBean;
import org.akaza.openclinica.control.core.Utils;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.logic.odmExport.ClinicalDataUnit;
import org.akaza.openclinica.logic.odmExport.MetaDataCollector;
import org.akaza.openclinica.logic.odmExport.MetadataUnit;

/**
 * Fetch odm data from database and load odm related classes.
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

    public void setStudyGroupClassTypesExpected() {
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT);// study_group_class_id
        this.setTypeExpected(2, TypeNames.STRING);// sgc_name(study_group_class
        // name)
        this.setTypeExpected(3, TypeNames.STRING);// sgc_type(group_class_type
        // name)
        this.setTypeExpected(4, TypeNames.INT);// (study_group_class) status_id
        this.setTypeExpected(5, TypeNames.STRING);// subject_assignment
        this.setTypeExpected(6, TypeNames.INT);// study_group_id
        this.setTypeExpected(7, TypeNames.STRING);// (study_group) sg_name
        this.setTypeExpected(8, TypeNames.STRING);// study_group description
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

    public void setItemGroupAndItemMetaWithUnitTypesExpected() {
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
        this.setTypeExpected(17, TypeNames.STRING);// width_decimal
        this.setTypeExpected(18, TypeNames.INT);// response_type_id
        this.setTypeExpected(19, TypeNames.STRING);// options_text
        this.setTypeExpected(20, TypeNames.STRING);// options_values
        this.setTypeExpected(21, TypeNames.STRING);// response_label
        this.setTypeExpected(22, TypeNames.STRING);// item_group_header
        this.setTypeExpected(23, TypeNames.STRING);// item_description
        this.setTypeExpected(24, TypeNames.STRING);// mu_oid
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

    public void setSubjectEventFormDataTypesExpected(String odmVersion) {
        if ("1.2".equals(odmVersion) || "1.3".equals(odmVersion)) {
            setSubjectEventFormDataTypesExpected();
        } else if ("oc1.2".equals(odmVersion) || "oc1.3".equals(odmVersion)) {
            this.unsetTypeExpected();
            this.setTypeExpected(1, TypeNames.STRING);// study_subject_oid;
            this.setTypeExpected(2, TypeNames.STRING);// label(study subject)
            this.setTypeExpected(3, TypeNames.STRING);// unique-identifier
            this.setTypeExpected(4, TypeNames.STRING);// secondary_label(study
            // subject)
            this.setTypeExpected(5, TypeNames.STRING);// gender
            this.setTypeExpected(6, TypeNames.DATE);// date_of_birth
            this.setTypeExpected(7, TypeNames.INT);// status_id (subject)
            this.setTypeExpected(8, TypeNames.INT);// sgc_id
            this.setTypeExpected(9, TypeNames.STRING);// sgc_name
            this.setTypeExpected(10, TypeNames.STRING);// sg_name
            this.setTypeExpected(11, TypeNames.INT);// definition_order;
            this.setTypeExpected(12, TypeNames.STRING);// definition_oid;
            this.setTypeExpected(13, TypeNames.BOOL);// definition_repeating
            this.setTypeExpected(14, TypeNames.INT);// sample_ordinal
            this.setTypeExpected(15, TypeNames.STRING);// se_location (study
            // event)
            this.setTypeExpected(16, TypeNames.DATE);// date_start (study
            // event)
            this.setTypeExpected(17, TypeNames.DATE);// date_end (study
            // event)
            this.setTypeExpected(18, TypeNames.BOOL);// start_time_flag
            this.setTypeExpected(19, TypeNames.BOOL);// end_time_flag
            this.setTypeExpected(20, TypeNames.INT);// event_status_id
            this.setTypeExpected(21, TypeNames.INT);// crf_order;
            this.setTypeExpected(22, TypeNames.STRING);// crf_version_oid
            this.setTypeExpected(23, TypeNames.STRING);// crf_version
            this.setTypeExpected(24, TypeNames.INT); // cv_status_id
            this.setTypeExpected(25, TypeNames.INT);// ec_status_id
            this.setTypeExpected(26, TypeNames.INT);// event_crf_id
            this.setTypeExpected(27, TypeNames.DATE);// date_interviewed
            this.setTypeExpected(28, TypeNames.STRING);// interviewer_name
            this.setTypeExpected(29, TypeNames.INT);// validator_id
        }
    }

    public void setEventGroupItemDataWithUnitTypesExpected() {
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
        this.setTypeExpected(10, TypeNames.STRING);// mu_oid
    }

    public void setEventCrfIdsByItemDataTypesExpected() {
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT);// event_crf_id;
    }

    public void setStudyMeasurementUnitsTypesExpected() {
        // select distinct mu.oc_oid as mu_oid, mu.name
        // mus.text
        this.unsetTypeExpected();
        int i = 1;
        this.setTypeExpected(i, TypeNames.STRING); // mu_oid
        ++i;
        this.setTypeExpected(i, TypeNames.STRING); // name
    }

    public void getBasicDefinitions(int studyId, BasicDefinitionsBean basicDef) {
        ArrayList<MeasurementUnitBean> units = basicDef.getMeasurementUnits();
        String uprev = "";
        this.setStudyMeasurementUnitsTypesExpected();
        ArrayList rows = this.select(this.getStudyMeasurementUnitsSql(studyId));
        Iterator it = rows.iterator();
        while (it.hasNext()) {
            HashMap row = (HashMap) it.next();
            String oid = (String) row.get("mu_oid");
            String name = (String) row.get("name");
            MeasurementUnitBean u = new MeasurementUnitBean();
            SymbolBean symbol = new SymbolBean();
            ArrayList<TranslatedTextBean> texts = new ArrayList<TranslatedTextBean>();
            if (uprev.equals(oid)) {
                u = units.get(units.size() - 1);
                symbol = u.getSymbol();
                texts = symbol.getTranslatedText();
            } else {
                u.setOid(oid);
                u.setName(name);
                units.add(u);
            }
            TranslatedTextBean t = new TranslatedTextBean();
            t.setText(name);
            texts.add(t);
            symbol.setTranslatedText(texts);
            u.setSymbol(symbol);
        }
    }

    public void getUpdatedSiteMetadata(int parentStudyId, int studyId, MetaDataVersionBean metadata, String odmVersion) {
        HashMap<Integer, Integer> cvIdPoses = new HashMap<Integer, Integer>();
        this.setStudyEventAndFormMetaTypesExpected();
        ArrayList rows = this.select(this.getStudyEventAndFormMetaSql(parentStudyId, studyId, true));
        Iterator it = rows.iterator();
        String sedprev = "";
        MetaDataVersionProtocolBean protocol = metadata.getProtocol();
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
                if (nullValue != null && nullValue.length() > 0) {
                }
            }
        }
    }

    public void getMetadata(int parentStudyId, int studyId, MetaDataVersionBean metadata, String odmVersion) {
        String cvIds = "";
        HashMap<Integer, Integer> cvIdPoses = new HashMap<Integer, Integer>();
        this.setStudyEventAndFormMetaTypesExpected();
        logger.debug("Begin to execute GetStudyEventAndFormMetaSql");
        logger.error("getStudyEventAndFormMetaSQl= " + this.getStudyEventAndFormMetaSql(parentStudyId, studyId, false));
        ArrayList rows = this.select(this.getStudyEventAndFormMetaSql(parentStudyId, studyId, false));
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

        this.setItemGroupAndItemMetaWithUnitTypesExpected();
        rows.clear();
        logger.debug("Begin to execute GetItemGroupAndItemMetaWithUnitSql");
        logger.error("getItemGroupandItemMetaWithUnitsql= " + this.getItemGroupAndItemMetaWithUnitSql(cvIds));
        rows = select(this.getItemGroupAndItemMetaWithUnitSql(cvIds));
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
            String widthDecimal = (String) row.get("width_decimal");
            Integer rsTypeId = (Integer) row.get("response_type_id");
            String rsText = (String) row.get("options_text");
            String rsValue = (String) row.get("options_values");
            String rsLabel = (String) row.get("response_label");
            String igHeader = (String) row.get("item_group_header");
            String itDesc = (String) row.get("item_description");
            String muOid = (String) row.get("mu_oid");
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
                    igdef.setName("ungrouped".equalsIgnoreCase(igName) ? cvOID + "-" + igName : igName);
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

            boolean hasCode = MetadataUnit.needCodeList(rsTypeId, dataTypeId);
            LinkedHashMap<String, String> codes = new LinkedHashMap<String, String>();
            if (hasCode) {
                if (nullMap.containsKey(cvId)) {
                    codes = MetadataUnit.parseCode(rsText, rsValue, nullMap.get(cvId));
                } else {
                    codes = MetadataUnit.parseCode(rsText, rsValue);
                }
                // no action has been taken if rsvalue/rstext go wrong,
                // since they have been validated when uploading crf.
            }
            String datatype = MetadataUnit.getOdmItemDataType(rsTypeId, dataTypeId, odmVersion);

            if (!itdset.contains(itId)) {
                itdset.add(itId);
                ItemDefBean idef = new ItemDefBean();
                idef.setOid(itOID);
                idef.setName(itName);
                idef.setComment(itDesc);
                if (muOid != null && muOid.length() > 0) {
                    ElementRefBean measurementUnitRef = new ElementRefBean();
                    measurementUnitRef.setElementDefOID(muOid);
                    idef.setMeasurementUnitRef(measurementUnitRef);
                }
                idef.setPreSASFieldName(itName);
                idef.setCodeListOID(hasCode ? "CL_" + rsId : "");
                idef.getQuestion().setText(MetadataUnit.getItemQuestionText(header, left, right));
                if (regexp != null && regexp.startsWith("func:")) {
                    idef.setRangeCheck(MetadataUnit.getItemRangeCheck(regexp.substring(5).trim(), metadata.getSoftHard(), regexpErr, muOid));
                }
                idef.setDataType(datatype);
                int len = 0;
                int sig = 0;
                if (widthDecimal != null && widthDecimal.length() > 0) {
                    // now there is no validation for width_decimal here.
                    len = Validator.parseWidth(widthDecimal);
                    sig = Validator.parseDecimal(widthDecimal);
                }
                if (rsTypeId == 3 || rsTypeId == 7) {// 3:checkbox;
                    // //7:multi-select
                    len = maxLengths.containsKey(itId) ? maxLengths.get(itId) : 0;
                    len = Math.max(len, Math.max(rsText.length(), rsValue.length()));
                    idef.setLength(len);
                } else if ("text".equalsIgnoreCase(datatype)) {
                    if (len > 0) {
                        idef.setLength(len);
                    } else {
                        idef.setLength(hasCode ? MetadataUnit.getDataTypeLength(codes.keySet()) : maxLengths.containsKey(itId) ? maxLengths.get(itId)
                            : MetaDataCollector.getTextLength());
                    }
                } else if ("integer".equalsIgnoreCase(datatype)) {
                    if (len > 0) {
                        idef.setLength(len);
                    } else {
                        idef.setLength(hasCode ? MetadataUnit.getDataTypeLength(codes.keySet()) : 10);
                    }
                } else if ("float".equalsIgnoreCase(datatype)) {
                    if (len > 0) {
                        idef.setLength(len);
                    } else {
                        //idef.setLength(hasCode ? MetadataUnit.getDataTypeLength(codes.keySet()) : 32);
                        idef.setLength(hasCode ? MetadataUnit.getDataTypeLength(codes.keySet()) : 25);
                    }
                } else {
                    idef.setLength(0);
                }
                idef.setSignificantDigits(sig > 0 ? sig : MetadataUnit.getSignificantDigits(datatype, codes.keySet(), hasCode));
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

        // add StudyGroupClassList
        if (odmVersion.startsWith("oc")) {
            this.setStudyGroupClassTypesExpected();
            rows.clear();
            logger.debug("Begin to execute GetStudyGroupClassSql");
            logger.error("getStudyGroupClassSql=" + getStudyGroupClassSql(parentStudyId));
            rows = select(this.getStudyGroupClassSql(parentStudyId));
            it = rows.iterator();
            ArrayList<StudyGroupClassListBean> sgcLists = (ArrayList<StudyGroupClassListBean>) metadata.getStudyGroupClassLists();
            String sgcprev = "";
            while (it.hasNext()) {
                HashMap row = (HashMap) it.next();
                Integer sgcid = (Integer) row.get("study_group_class_id");
                String sgcname = (String) row.get("sgc_name");
                String sgctype = (String) row.get("sgc_type");
                Integer statusid = (Integer) row.get("status_id");
                String subassign = (String) row.get("subject_assignment");
                String sgname = (String) row.get("sg_name");
                String des = (String) row.get("description");

                if (sgcprev.equals(sgcid + "")) {
                    StudyGroupItemBean sg = new StudyGroupItemBean();
                    sg.setName(sgname);
                    sg.setDescription(des);
                    StudyGroupClassListBean sgc = sgcLists.get(sgcLists.size() - 1);
                    sgc.getStudyGroupItems().add(sg);
                } else {
                    sgcprev = sgcid + "";
                    StudyGroupClassListBean sgc = new StudyGroupClassListBean();
                    sgc.setID("SGC_" + sgcid);
                    sgc.setName(sgcname);
                    sgc.setType(sgctype);
                    sgc.setStatus(Status.get(statusid).getName());
                    sgc.setSubjectAssignment(subassign);
                    StudyGroupItemBean sg = new StudyGroupItemBean();
                    sg.setName(sgname);
                    sg.setDescription(des);
                    sgc.getStudyGroupItems().add(sg);
                    sgcLists.add(sgc);
                }
            }
        }
        // return nullClSet;
    }

    public void getClinicalData(StudyBean study, DatasetBean dataset, OdmClinicalDataBean data, String odmVersion) {
        String dbName = SQLInitServlet.getDBName();
        String subprev = "";
        HashMap<String, Integer> sepos = new HashMap<String, Integer>();
        String seprev = "";
        String formprev = "";
        HashMap<String, Integer> igpos = new HashMap<String, Integer>();
        String igprev = "";
        String oidPos = "";
        HashMap<Integer, String> oidPoses = new HashMap<Integer, String>();

        // String studyIds = "";
        // if (study.getParentStudyId() > 0) {
        // studyIds += study.getId();
        // } else {
        // ArrayList<Integer> ids = (ArrayList<Integer>) (new
        // StudyDAO(this.ds)).findAllSiteIdsByStudy(study);
        // for (int i = 0; i < ids.size() - 1; ++i) {
        // studyIds += ids.get(i) + ",";
        // }
        // studyIds += ids.get(ids.size() - 1);
        // }

        String studyIds = study.getId() + "";
        int datasetItemStatusId = dataset.getDatasetItemStatus().getId();
        String sql = dataset.getSQLStatement().split("order by")[0].trim();
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
        logger.debug("Begin to GetSubjectEventFormSql");
        if (odmVersion.startsWith("oc")) {
            logger.error("getOCSubjectEventFormSql=" + getOCSubjectEventFormSql(studyIds, sedIds, itemIds, dateConstraint, datasetItemStatusId));
            this.setSubjectEventFormDataTypesExpected(odmVersion);
            ArrayList viewRows = select(getOCSubjectEventFormSql(studyIds, sedIds, itemIds, dateConstraint, datasetItemStatusId));
            Iterator iter = viewRows.iterator();
            this.setDataWithOCAttributes(study, dataset, data, odmVersion, iter, oidPoses);
        } else {
            logger.error("getSubjectEventFormSql=" + getSubjectEventFormSql(studyIds, sedIds, itemIds, dateConstraint, datasetItemStatusId));
            this.setSubjectEventFormDataTypesExpected();
            ArrayList viewRows = select(getSubjectEventFormSql(studyIds, sedIds, itemIds, dateConstraint, datasetItemStatusId));
            Iterator iter = viewRows.iterator();
            // if any algorithm modification has been done in this block,
            // the method "setDataWithOCAttributes" should be checked about the
            // updated as well because this method came from here.
            while (iter.hasNext()) {
                HashMap row = (HashMap) iter.next();
                String studySubjectLabel = (String) row.get("study_subject_oid");
                String sedOID = (String) row.get("definition_oid");
                Boolean studyEventRepeating = (Boolean) row.get("definition_repeating");
                Integer sampleOrdinal = (Integer) row.get("sample_ordinal");
                String cvOID = (String) row.get("crf_version_oid");
                Integer ecId = (Integer) row.get("event_crf_id");// ecId
                // should
                // be unique;

                String key = studySubjectLabel;
                ExportSubjectDataBean sub = new ExportSubjectDataBean();
                if (subprev.equals(studySubjectLabel)) {
                    int p = data.getExportSubjectData().size() - 1;
                    sub = data.getExportSubjectData().get(p);
                } else {
                    subprev = studySubjectLabel;
                    sub.setSubjectOID(studySubjectLabel);
                    data.getExportSubjectData().add(sub);
                    seprev = "";
                    formprev = "";
                    igprev = "";
                }
                oidPos = data.getExportSubjectData().size() - 1 + "";
                ExportStudyEventDataBean se = new ExportStudyEventDataBean();
                // key += sedOID + sampleOrdinal;
                key += sedOID;
                if (!seprev.equals(key) || !sepos.containsKey(key + sampleOrdinal)) {
                    sepos.put(key + sampleOrdinal, sub.getExportStudyEventData().size());
                    seprev = key;
                    se.setStudyEventOID(sedOID);
                    se.setStudyEventRepeatKey(studyEventRepeating ? sampleOrdinal + "" : "-1");
                    sub.getExportStudyEventData().add(se);
                    formprev = "";
                    igprev = "";
                } else {
                    se = sub.getExportStudyEventData().get(sepos.get(key + sampleOrdinal));
                }
                oidPos += "---" + (sub.getExportStudyEventData().size() - 1);
                ExportFormDataBean form = new ExportFormDataBean();
                key += cvOID;
                if (formprev.equals(key)) {
                    form = se.getExportFormData().get(se.getExportFormData().size() - 1);
                } else {
                    formprev = key;
                    form.setFormOID(cvOID);
                    se.getExportFormData().add(form);
                    igprev = "";
                }
                oidPos += "---" + (se.getExportFormData().size() - 1);
                // ecId should be distinct
                oidPoses.put(ecId, oidPos);
                oidPos = "";
            }
        }

        this.setEventGroupItemDataWithUnitTypesExpected();
        logger.debug("Begin to GetEventGroupItemWithUnitSql");
        ArrayList viewRows = select(getEventGroupItemWithUnitSql(studyIds, sedIds, itemIds, dateConstraint, datasetItemStatusId));
        logger.error("getEventGroupItemWithUnitSql : " + getEventGroupItemWithUnitSql(studyIds, sedIds, itemIds, dateConstraint, datasetItemStatusId));
        Iterator iter = viewRows.iterator();
        ExportSubjectDataBean sub = new ExportSubjectDataBean();
        ExportStudyEventDataBean se = new ExportStudyEventDataBean();
        ExportFormDataBean form = new ExportFormDataBean();
        int ecprev = -1;
        igprev = "";
        boolean goon = true;
        String itprev = "";
        HashMap<String, String> nullValueMap = ClinicalDataUnit.getNullValueMap();
        String yearMonthFormat = StringUtil.parseDateFormat(ResourceBundleProvider.getFormatBundle(locale).getString("date_format_year_month"));
        String yearFormat = StringUtil.parseDateFormat(ResourceBundleProvider.getFormatBundle(locale).getString("date_format_year"));
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
            String muOid = (String) row.get("mu_oid");
            String key = "";
            if (ecId != ecprev) {
                logger.error("Found ecId=" + ecId + " in subjectEventFormSql is:" + oidPoses.containsKey(ecId));
                if (oidPoses.containsKey(ecId)) {
                    goon = true;
                    String[] poses = oidPoses.get(ecId).split("---");
                    sub = data.getExportSubjectData().get(Integer.valueOf(poses[0]));
                    se = sub.getExportStudyEventData().get(Integer.valueOf(poses[1]));
                    form = se.getExportFormData().get(Integer.valueOf(poses[2]));
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
                key = itId + "_" + itDataOrdinal + key;
                if (!itprev.equals(key)) {
                    itprev = key;
                    ImportItemDataBean it = new ImportItemDataBean();
                    it.setItemOID(itOID);
                    it.setTransactionType("Insert");
                    if (nullValueMap.containsKey(itValue.trim().toUpperCase())) {
                        itValue = nullValueMap.get(itValue.trim().toUpperCase());
                    }
                    if (datatypeid == 9) {
                        try {
                            itValue = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat(oc_df_string).parse(itValue));
                        } catch (Exception fe) {
                            logger.info("Item -" + itOID + " value " + itValue + " might not be ODM date format yyyy-MM-dd.");
                        }
                    } else if (datatypeid == 10 && odmVersion.contains("1.3")) {
                        if (StringUtil.isFormatDate(itValue, oc_df_string)) {
                            try {
                                itValue = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat(oc_df_string).parse(itValue));
                            } catch (Exception e) {
                                logger.info("Item -" + itOID + " value " + itValue + " might not be ODM partialDate format yyyy[-MM[-dd]].");
                            }
                        } else {
                            if (StringUtil.isPartialYearMonth(itValue, yearMonthFormat)) {
                                try {
                                    itValue = new SimpleDateFormat("yyyy-MM").format(new SimpleDateFormat(yearMonthFormat).parse(itValue));
                                } catch (Exception e) {
                                    logger.info("Item -" + itOID + " value " + itValue + " might not be ODM partialDate format yyyy[-MM[-dd]].");
                                }
                            } else {
                                try {
                                    itValue = new SimpleDateFormat("yyyy").format(new SimpleDateFormat(yearFormat).parse(itValue));
                                } catch (Exception e) {
                                    logger.info("Item -" + itOID + " value " + itValue + " might not be ODM partialDate format yyyy[-MM[-dd]].");
                                }
                            }
                        }
                    }
                    it.setValue(itValue);
                    if (muOid != null && muOid.length() > 0) {
                        ElementRefBean measurementUnitRef = new ElementRefBean();
                        measurementUnitRef.setElementDefOID(muOid);
                        it.setMeasurementUnitRef(measurementUnitRef);
                    }
                    ig.getItemData().add(it);
                }
            }
        }
    }

    protected void setDataWithOCAttributes(StudyBean study, DatasetBean dataset, OdmClinicalDataBean data, String odmVersion, Iterator iter,
            HashMap<Integer, String> oidPoses) {
        String subprev = "";
        HashMap<String, Integer> sepos = new HashMap<String, Integer>();
        String seprev = "";
        String formprev = "";
        HashMap<String, Integer> igpos = new HashMap<String, Integer>();
        String igprev = "";
        String oidPos = "";
        StudyBean parentStudy = study.getParentStudyId() > 0 ? (StudyBean) new StudyDAO(this.ds).findByPK(study.getParentStudyId()) : study;
        setStudyParemeterConfig(parentStudy);
        HashSet<Integer> sgcIdSet = new HashSet<Integer>();
        while (iter.hasNext()) {
            HashMap row = (HashMap) iter.next();
            String studySubjectLabel = (String) row.get("study_subject_oid");
            // String label = (String) row.get("label");
            Integer sgcId = (Integer) row.get("sgc_id");
            String sgcName = (String) row.get("sgc_name");
            String sgName = (String) row.get("sg_name");
            String sedOID = (String) row.get("definition_oid");
            Boolean studyEventRepeating = (Boolean) row.get("definition_repeating");
            Integer sampleOrdinal = (Integer) row.get("sample_ordinal");
            Date startDate = (Date) row.get("date_start");
            String cvOID = (String) row.get("crf_version_oid");
            Integer ecId = (Integer) row.get("event_crf_id");// ecId should
            // be unique;
            Date dob = (Date) row.get("date_of_birth");

            String key = studySubjectLabel;
            ExportSubjectDataBean sub = new ExportSubjectDataBean();
            if (subprev.equals(studySubjectLabel)) {
                int p = data.getExportSubjectData().size() - 1;
                sub = data.getExportSubjectData().get(p);
                // ------ add openclinica subject_group
                if (sgcId > 0) {
                    int presize = sgcIdSet.size();
                    sgcIdSet.add(sgcId);
                    if (sgcIdSet.size() > presize) {
                        sgcIdSet.add(sgcId);
                        SubjectGroupDataBean sgd = new SubjectGroupDataBean();
                        sgd.setStudyGroupClassId("SGC_" + sgcId);
                        sgd.setStudyGroupClassName(sgcName);
                        sgd.setStudyGroupName(sgName);
                        sub.getSubjectGroupData().add(sgd);
                    }
                }
                // ------ finish adding openclinica subject_group
            } else {
                subprev = studySubjectLabel;
                sub.setSubjectOID(studySubjectLabel);
                // ----- add openclinica subject attributes
                sub.setStudySubjectId((String) row.get("label"));
                if (dataset.isShowSubjectUniqueIdentifier()) {
                    sub.setUniqueIdentifier((String) row.get("unique_identifier"));
                }
                if (dataset.isShowSubjectSecondaryId()) {
                    sub.setSecondaryId((String) row.get("secondary_label"));
                }
                if (dob != null) {
                    if (dataset.isShowSubjectDob()) {
                        if (parentStudy.getStudyParameterConfig().getCollectDob().equals("2")) {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(dob);
                            int year = cal.get(Calendar.YEAR);
                            if (year > 0) {
                                sub.setYearOfBirth(year);
                            }
                        } else {
                            sub.setDateOfBirth(new SimpleDateFormat("yyyy-MM-dd").format(dob));
                        }
                    }
                }
                if (dataset.isShowSubjectGender()) {
                    sub.setSubjectGender((String) row.get("gender"));
                }
                if (dataset.isShowSubjectStatus()) {
                    sub.setStatus(Status.get((Integer) row.get("status_id")).getName());
                }
                // ------ finish adding openclinica subject attributes

                // ------ add openclinica subject_group
                if (sgcId > 0) {
                    sgcIdSet.clear();
                    sgcIdSet.add(sgcId);
                    SubjectGroupDataBean sgd = new SubjectGroupDataBean();
                    sgd.setStudyGroupClassId("SGC_" + sgcId);
                    sgd.setStudyGroupClassName(sgcName);
                    sgd.setStudyGroupName(sgName);
                    sub.getSubjectGroupData().add(sgd);
                }
                // ------ finish adding openclinica subject_group

                data.getExportSubjectData().add(sub);
                seprev = "";
                formprev = "";
                igprev = "";
            }

            oidPos = data.getExportSubjectData().size() - 1 + "";
            ExportStudyEventDataBean se = new ExportStudyEventDataBean();
            // key += sedOID + sampleOrdinal;
            key += sedOID;
            if (!seprev.equals(key) || !sepos.containsKey(key + sampleOrdinal)) {
                sepos.put(key + sampleOrdinal, sub.getExportStudyEventData().size());
                seprev = key;
                se.setStudyEventOID(sedOID);
                // ----- add openclinica study event attributes
                if (startDate != null && dataset.isShowSubjectAgeAtEvent() && dob != null) {
                    se.setAgeAtEvent(Utils.getAge(dob, startDate));
                }
                if (dataset.isShowEventLocation()) {
                    se.setLocation((String) row.get("se_location"));
                }
                if (dataset.isShowEventStart() && startDate != null) {
                    if ((Boolean) row.get("start_time_flag") == Boolean.TRUE) {
                        se.setStartDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(startDate));
                    } else {
                        se.setStartDate(new SimpleDateFormat("yyyy-MM-dd").format(startDate));
                    }
                }
                Date endDate = (Date) row.get("date_end");
                if (dataset.isShowEventEnd() && endDate != null) {
                    if ((Boolean) row.get("end_time_flag") == Boolean.TRUE) {
                        se.setEndDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(endDate));
                    } else {
                        se.setEndDate(new SimpleDateFormat("yyyy-MM-dd").format(endDate));
                    }
                }
                if (dataset.isShowEventStatus()) {
                    se.setStatus(SubjectEventStatus.get((Integer) row.get("event_status_id")).getName());
                }
                // ----- finish adding study event attributes
                se.setStudyEventRepeatKey(studyEventRepeating ? sampleOrdinal + "" : "-1");
                sub.getExportStudyEventData().add(se);
                formprev = "";
                igprev = "";
            } else {
                se = sub.getExportStudyEventData().get(sepos.get(key + sampleOrdinal));
            }
            oidPos += "---" + (sub.getExportStudyEventData().size() - 1);
            ExportFormDataBean form = new ExportFormDataBean();
            key += cvOID;
            if (formprev.equals(key)) {
                form = se.getExportFormData().get(se.getExportFormData().size() - 1);
            } else {
                formprev = key;
                form.setFormOID(cvOID);
                // ----- add openclinica crf attributes
                if (dataset.isShowCRFversion()) {
                    form.setCrfVersion((String) row.get("crf_version"));
                }
                if (dataset.isShowCRFstatus()) {
                    form.setStatus(this.getCrfVersionStatus(se.getStatus(), (Integer) row.get("cv_status_id"), (Integer) row.get("ec_status_id"), (Integer) row
                            .get("validator_id")));
                }
                if (dataset.isShowCRFinterviewerName()) {
                    form.setInterviewerName((String) row.get("interviewer_name"));
                }
                if (dataset.isShowCRFinterviewerDate()) {
                    try {
                        form.setInterviewDate(new SimpleDateFormat("yyyy-MM-dd").format((Date) row.get("date_interviewed")));
                    } catch (NullPointerException npe) {
                        logger.info("caught NPE");
                        form.setInterviewDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                    }
                }
                // ----- finish adding crf attributes
                se.getExportFormData().add(form);
                igprev = "";
            }
            oidPos += "---" + (se.getExportFormData().size() - 1);
            // ecId should be distinct
            oidPoses.put(ecId, oidPos);
            oidPos = "";
        }
    }

    private String getCrfVersionStatus(String seSubjectEventStatus, int cvStatusId, int ecStatusId, int validatorId) {
        DataEntryStage stage = DataEntryStage.INVALID;
        Status status = Status.get(ecStatusId);

        // At this time, EventCRFBean stage is not in database.
        //
        // if (stage != null) {
        // if (!stage.equals(DataEntryStage.INVALID)) {
        // return stage.getName();
        // }
        // }
        //
        // if (!active || !status.isActive()) {
        // stage = DataEntryStage.UNCOMPLETED;
        // }
        if (stage.equals(DataEntryStage.INVALID) || status.equals(Status.INVALID)) {
            stage = DataEntryStage.UNCOMPLETED;
        }

        if (status.equals(Status.AVAILABLE)) {
            stage = DataEntryStage.INITIAL_DATA_ENTRY;
        }
        if (status.equals(Status.PENDING)) {
            if (validatorId != 0) {
                stage = DataEntryStage.DOUBLE_DATA_ENTRY;
            } else {
                stage = DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE;
            }
        }
        if (status.equals(Status.UNAVAILABLE)) {
            stage = DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE;
        }
        if (status.equals(Status.LOCKED)) {
            stage = DataEntryStage.LOCKED;
        }

        try {
            if (seSubjectEventStatus.equals(SubjectEventStatus.LOCKED.getName()) || seSubjectEventStatus.equals(SubjectEventStatus.SKIPPED.getName())
                || seSubjectEventStatus.equals(SubjectEventStatus.STOPPED.getName())) {
                stage = DataEntryStage.LOCKED;
            } else if (seSubjectEventStatus.equals(SubjectEventStatus.INVALID.getName())) {
                stage = DataEntryStage.LOCKED;
            } else if (cvStatusId != 1) {
                stage = DataEntryStage.LOCKED;
            }
        } catch (NullPointerException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            System.out.println("caught NPE here");
        }

        logger.info("returning " + stage.getName());

        return stage.getName();
    }

    protected void setStudyParemeterConfig(StudyBean study) {
        StudyParameterValueBean param = new StudyParameterValueDAO(this.ds).findByHandleAndStudy(study.getId(), "collectDob");
        study.getStudyParameterConfig().setCollectDob(param.getValue());
    }

    protected String getStudyGroupClassSql(int studyId) {
        return "select sgc.study_group_class_id, sgc.name as sgc_name, gct.name as sgc_type, sgc.status_id,"
            + " sgc.subject_assignment, sg.study_group_id, sg.name as sg_name, sg.description from study_group_class sgc,"
            + " study_group sg, group_class_types gct where study_id in (" + studyId + ")" + " and sgc.study_group_class_id = sg.study_group_class_id"
            + " and sgc.group_class_type_id = gct.group_class_type_id order by sgc.study_group_class_id";
    }

    protected String getStudyEventAndFormMetaSql(int parentStudyId, int studyId, boolean isIncludedSite) {
        return "select sed.ordinal as definition_order, edc.ordinal as crf_order, edc.crf_id, cv.crf_version_id,"
            + " sed.oc_oid as definition_oid, sed.name as definition_name, sed.repeating as definition_repeating,"
            + " sed.type as definition_type, cv.oc_oid as cv_oid,"
            + " cv.name as cv_name, edc.required_crf as cv_required, edc.null_values, crf.name as crf_name"
            + " from study_event_definition sed, event_definition_crf edc, crf, crf_version cv where sed.study_id = "
            + parentStudyId
            + " and sed.status_id not in (5,7) and "
            + this.eventDefinitionCrfCondition(studyId, parentStudyId, isIncludedSite)
            + " and sed.study_event_definition_id = edc.study_event_definition_id"
            + " and edc.status_id not in (5,7) and edc.crf_id = crf.crf_id and crf.status_id not in (5,7) and crf.crf_id = cv.crf_id and (cv.status_id not in (5,7))"
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
            + " ifm.right_item_text, ifm.required as item_required, ifm.regexp, ifm.regexp_error_msg, ifm.width_decimal,"
            + " rs.response_type_id, rs.options_text, rs.options_values, rs.label as response_label,"
            + " igm.item_group_header, item.description as item_description from crf_version cv,"
            + " (select crf_version_id, item_id, response_set_id, header as item_header, left_item_text, right_item_text, required, regexp,"
            + " regexp_error_msg, width_decimal from item_form_metadata where crf_version_id in (" + crfVersionIds + "))ifm, item, response_set rs,"
            + " (select crf_version_id, item_group_id, item_id, header as item_group_header from item_group_metadata where crf_version_id in (" + crfVersionIds
            + "))igm," + " item_group ig where cv.crf_version_id in (" + crfVersionIds + ") and cv.crf_version_id = ifm.crf_version_id"
            + " and ifm.item_id = item.item_id and ifm.response_set_id = rs.response_set_id"
            + " and ifm.item_id = igm.item_id and cv.crf_version_id = igm.crf_version_id and igm.item_group_id = ig.item_group_id"
            + " order by cv.crf_version_id, ig.item_group_id, item.item_id, rs.response_set_id";
    }

    protected String getSubjectEventFormSql(String studyIds, String sedIds, String itemIds, String dateConstraint, int datasetItemStatusId) {
        return "select ss.oc_oid as study_subject_oid, sed.ordinal as definition_order, sed.oc_oid as definition_oid,"
            + " sed.repeating as definition_repeating, se.sample_ordinal as sample_ordinal, edc.ordinal as crf_order, "
            + " cv.oc_oid as crf_version_oid, ec.event_crf_id from (select study_event_id, study_event_definition_id, study_subject_id,"
            + " sample_ordinal from study_event where study_event_definition_id in " + sedIds
            + " and study_subject_id in (select ss.study_subject_id from study_subject ss where ss.study_id in (" + studyIds + ") " + dateConstraint
            + ")) se, (select ss.oc_oid, ss.study_subject_id from study_subject ss where ss.study_id in (" + studyIds + ") " + dateConstraint + ") ss,"
            + " study_event_definition sed, event_definition_crf edc,"
            + " (select event_crf_id, crf_version_id, study_event_id from event_crf where event_crf_id in ("
            + getEventCrfIdsByItemDataSql(studyIds, sedIds, itemIds, dateConstraint, datasetItemStatusId) + ")) ec, crf_version cv"
            + " where sed.study_event_definition_id in " + sedIds
            + " and sed.study_event_definition_id = se.study_event_definition_id and se.study_subject_id = ss.study_subject_id"
            + " and sed.study_event_definition_id = edc.study_event_definition_id and se.study_event_id = ec.study_event_id"
            + " and edc.crf_id = cv.crf_id and ec.crf_version_id = cv.crf_version_id order by ss.oc_oid, sed.ordinal, se.sample_ordinal, edc.ordinal";
    }

    protected String getOCSubjectEventFormSql(String studyIds, String sedIds, String itemIds, String dateConstraint, int datasetItemStatusId) {
        return "select ss.oc_oid as study_subject_oid, ss.label, ss.unique_identifier, ss.secondary_label, ss.gender, ss.date_of_birth,"
            + " ss.status_id, ss.sgc_id, ss.sgc_name, ss.sg_name, sed.ordinal as definition_order, sed.oc_oid as definition_oid, sed.repeating as definition_repeating,"
            + " se.sample_ordinal as sample_ordinal, se.se_location, se.date_start, se.date_end, se.start_time_flag,"
            + " se.end_time_flag, se.subject_event_status_id as event_status_id, edc.ordinal as crf_order,"
            + " cv.oc_oid as crf_version_oid, cv.name as crf_version, cv.status_id as cv_status_id, ec.status_id as ec_status_id, ec.event_crf_id, ec.date_interviewed,"
            + " ec.interviewer_name, ec.validator_id from (select study_event_id, study_event_definition_id, study_subject_id, location as se_location,"
            + " sample_ordinal, date_start, date_end, subject_event_status_id, start_time_flag, end_time_flag from study_event "
            + " where study_event_definition_id in "
            + sedIds
            + " and study_subject_id in (select ss.study_subject_id from study_subject ss where ss.study_id in ("
            + studyIds
            + ") "
            + dateConstraint
            + ")) se, ( select st_sub.oc_oid, st_sub.study_subject_id, st_sub.label,"
            + " st_sub.secondary_label, st_sub.subject_id, st_sub.unique_identifier, st_sub.gender, st_sub.date_of_birth, st_sub.status_id,"
            + " sb_g.sgc_id, sb_g.sgc_name, sb_g.sg_id, sb_g.sg_name from (select ss.oc_oid, ss.study_subject_id, ss.label, ss.secondary_label, ss.subject_id,"
            + " s.unique_identifier, s.gender, s.date_of_birth, s.status_id from study_subject ss, subject s where ss.study_id in ("
            + studyIds
            + ") "
            + dateConstraint
            + " and ss.subject_id = s.subject_id)st_sub left join (select sgm.study_subject_id, sgc.study_group_class_id as sgc_id, sgc.name as sgc_name,"
            + " sg.study_group_id as sg_id, sg.name as sg_name from subject_group_map sgm, study_group_class sgc, study_group sg where sgc.study_id in ("
            + studyIds
            + ") and sgm.study_group_class_id = sgc.study_group_class_id and sgc.study_group_class_id = sg.study_group_class_id"
            + " and sgm.study_group_id = sg.study_group_id) sb_g on st_sub.study_subject_id = sb_g.study_subject_id) ss, "
            + " study_event_definition sed, event_definition_crf edc,"
            + " (select event_crf_id, crf_version_id, study_event_id, status_id, date_interviewed, interviewer_name, validator_id from event_crf where event_crf_id in ("
            + getEventCrfIdsByItemDataSql(studyIds, sedIds, itemIds, dateConstraint, datasetItemStatusId)
            + ")) ec, crf_version cv"
            + " where sed.study_event_definition_id in "
            + sedIds
            + " and sed.study_event_definition_id = se.study_event_definition_id and se.study_subject_id = ss.study_subject_id"
            + " and sed.study_event_definition_id = edc.study_event_definition_id and se.study_event_id = ec.study_event_id"
            + " and edc.crf_id = cv.crf_id and ec.crf_version_id = cv.crf_version_id order by ss.oc_oid, sed.ordinal, se.sample_ordinal, edc.ordinal, ss.sgc_id";
    }

    protected String getEventGroupItemSql(String studyIds, String sedIds, String itemIds, String dateConstraint, int datasetItemStatusId) {
        String ecStatusConstraint = this.getECStatusConstraint(datasetItemStatusId);
        String itStatusConstraint = this.getItemDataStatusConstraint(datasetItemStatusId);
        return "select cvidata.event_crf_id, ig.item_group_id, ig.oc_oid as item_group_oid, ig.name as item_group_name,"
            + " cvidata.item_id, cvidata.item_oid, cvidata.item_data_ordinal, cvidata.value, cvidata.item_data_type_id"
            + " from (select ec.event_crf_id, ec.crf_version_id, item.item_id, item.oc_oid as item_oid,"
            + " idata.ordinal as item_data_ordinal, idata.value as value, item.item_data_type_id from item,"
            + " (select event_crf_id, item_id, ordinal, value from item_data where (status_id "
            + itStatusConstraint
            + ")"
            + " and event_crf_id in (select distinct event_crf_id from event_crf where study_subject_id in (select distinct"
            + " ss.study_subject_id from study_subject ss where ss.study_id in ("
            + studyIds
            + ") "
            + dateConstraint
            + ") and study_event_id"
            + " in (select distinct study_event_id from study_event"
            + " where study_event_definition_id in "
            + sedIds
            + " and study_subject_id in ("
            + " select distinct ss.study_subject_id from study_subject ss where ss.study_id in ("
            + studyIds
            + ") "
            + dateConstraint
            + "))))idata,"
            + " (select event_crf_id, crf_version_id from event_crf where status_id "
            + ecStatusConstraint
            + ")ec"
            + " where item.item_id in "
            + itemIds
            + " and length(idata.value) > 0 and item.item_id = idata.item_id and idata.event_crf_id = ec.event_crf_id"
            + " order by ec.event_crf_id, ec.crf_version_id, item.item_id, idata.ordinal) cvidata, item_group_metadata igm,"
            + " item_group ig where cvidata.crf_version_id = igm.crf_version_id and cvidata.item_id = igm.item_id"
            + " and igm.item_group_id = ig.item_group_id order by cvidata.event_crf_id, ig.item_group_id, cvidata.item_id, cvidata.item_data_ordinal";
    }

    // should mapped to non-completed
    protected String getEventCrfIdsByItemDataSql(String studyIds, String sedIds, String itemIds, String dateConstraint, int datasetItemStatusId) {
        String ecStatusConstraint = getECStatusConstraint(datasetItemStatusId);
        String itStatusConstraint = this.getItemDataStatusConstraint(datasetItemStatusId);
        return "select distinct idata.event_crf_id from item_data idata" + " where idata.item_id in " + itemIds + " and (idata.status_id " + itStatusConstraint
            + ")" + " and idata.event_crf_id in (select event_crf_id from event_crf where study_subject_id in"
            + " (select ss.study_subject_id from study_subject ss WHERE ss.study_id in (" + studyIds + ") " + dateConstraint + ")"
            + " and study_event_id in (select study_event_id from study_event where study_event_definition_id in " + sedIds
            + " and study_subject_id in (select ss.study_subject_id from study_subject ss where ss.study_id in (" + studyIds + ") " + dateConstraint + "))"
            + " and (status_id " + ecStatusConstraint + ")) and length(value) > 0";
    }

    protected String eventDefinitionCrfCondition(int studyId, int parentStudyId, boolean isIncludedSite) {
        if (isIncludedSite) {
            return "edc.study_id = " + studyId;
        } else {
            if (studyId == parentStudyId) {
                return "edc.study_id = " + studyId;
            } else {
                return "((edc.study_id = " + parentStudyId + " or edc.study_id = " + studyId + ") and edc.event_definition_crf_id not in "
                    + "(select parent_id from event_definition_crf e where e.study_id = " + studyId + "))";
            }
        }
    }

    protected String getStudyMeasurementUnitsSql(int studyId) {
        return "select distinct mu.oc_oid as mu_oid, mu.name from study_subject ss, event_crf ec, item_form_metadata ifm, item, measurement_unit mu"
            + " where ss.study_id =" + studyId
            + " and ss.study_subject_id = ec.study_subject_id and ec.crf_version_id = ifm.crf_version_id and ifm.item_id = item.item_id"
            + " and item.units = mu.name order by mu.oc_oid";
    }

    protected String getEventGroupItemWithUnitSql(String studyIds, String sedIds, String itemIds, String dateConstraint, int datasetItemStatusId) {
        return "select cvit.*, mu.oc_oid as mu_oid from (" + this.getEventGroupItemSql(studyIds, sedIds, itemIds, dateConstraint, datasetItemStatusId)
            + " )cvit left join (select item.item_id, mu.oc_oid from versioning_map vm, item, measurement_unit mu where vm.item_id in " + itemIds
            + " and vm.item_id = item.item_id and item.units = mu.name )mu on cvit.item_id = mu.item_id";
    }

    protected String getItemGroupAndItemMetaWithUnitSql(String crfVersionIds) {
        return "select cv.*, mu.oc_oid as mu_oid from (" + this.getItemGroupAndItemMetaSql(crfVersionIds) + ")cv left join"
            + " (select item.item_id, mu.oc_oid from versioning_map vm, item, measurement_unit mu" + " where vm.crf_version_id in (" + crfVersionIds
            + ") and vm.item_id = item.item_id" + " and item.units = mu.name )mu on cv.item_id = mu.item_id ";
    }
}
