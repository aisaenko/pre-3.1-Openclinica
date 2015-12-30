package org.akaza.openclinica.service.crfdata;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.ResponseOptionBean;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.submit.crfdata.FormDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemGroupDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ODMContainer;
import org.akaza.openclinica.bean.submit.crfdata.StudyEventDataBean;
import org.akaza.openclinica.bean.submit.crfdata.SubjectDataBean;
import org.akaza.openclinica.control.submit.DisplayItemBeanWrapper;
import org.akaza.openclinica.control.submit.ImportHelper;
import org.akaza.openclinica.core.form.DiscrepancyValidator;
import org.akaza.openclinica.core.form.FormDiscrepancyNotes;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.ItemGroupDAO;
import org.akaza.openclinica.exception.OpenClinicaException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

public class ImportCRFDataService {

    private DataSource ds;

    public ImportCRFDataService(DataSource ds) {
        this.ds = ds;
    }

    /*
     * purpose: look up EventCRFBeans by the following: Study Subject, Study
     * Event, CRF Version, using the findByEventSubjectVersion method in
     * EventCRFDAO. May return more than one, hmm.
     */
    public List<EventCRFBean> fetchEventCRFBeans(ODMContainer odmContainer, UserAccountBean ub) {
        ArrayList<EventCRFBean> eventCRFBeans = new ArrayList<EventCRFBean>();
        ArrayList<Integer> eventCRFBeanIds = new ArrayList<Integer>();
        EventCRFDAO eventCrfDAO = new EventCRFDAO(ds);
        StudySubjectDAO studySubjectDAO = new StudySubjectDAO(ds);
        StudyEventDefinitionDAO studyEventDefinitionDAO = new StudyEventDefinitionDAO(ds);
        StudyDAO studyDAO = new StudyDAO(ds);
        StudyEventDAO studyEventDAO = new StudyEventDAO(ds);

        String studyOID = odmContainer.getCrfDataPostImportContainer().getStudyOID();
        StudyBean studyBean = studyDAO.findByOid(studyOID);
        ArrayList<SubjectDataBean> subjectDataBeans = odmContainer.getCrfDataPostImportContainer().getSubjectData();
        for (SubjectDataBean subjectDataBean : subjectDataBeans) {
            ArrayList<StudyEventDataBean> studyEventDataBeans = subjectDataBean.getStudyEventData();

            StudySubjectBean studySubjectBean = studySubjectDAO.findByOidAndStudy(subjectDataBean.getSubjectOID(), studyBean.getId());
            for (StudyEventDataBean studyEventDataBean : studyEventDataBeans) {
                ArrayList<FormDataBean> formDataBeans = studyEventDataBean.getFormData();

                StudyEventDefinitionBean studyEventDefinitionBean =
                    studyEventDefinitionDAO.findByOidAndStudy(studyEventDataBean.getStudyEventOID(), studyBean.getId());
                ArrayList<StudyEventBean> studyEventBeans = studyEventDAO.findAllByDefinitionAndSubject(studyEventDefinitionBean, studySubjectBean);
                for (FormDataBean formDataBean : formDataBeans) {

                    CRFVersionDAO crfVersionDAO = new CRFVersionDAO(ds);

                    ArrayList<CRFVersionBean> crfVersionBeans = crfVersionDAO.findAllByOid(formDataBean.getFormOID());
                    for (CRFVersionBean crfVersionBean : crfVersionBeans) {
                        // iterate the studyeventbeans here
                        for (StudyEventBean studyEventBean : studyEventBeans) {
                            ArrayList<EventCRFBean> eventCrfBeans = eventCrfDAO.findByEventSubjectVersion(studyEventBean, studySubjectBean, crfVersionBean);
                            // what if we have begun with creating a study
                            // event, but haven't entered data yet? this would
                            // have us with a study event, but no corresponding
                            // event crf, yet.
                            if (eventCrfBeans.isEmpty()) {
                                System.out.println("   found no event crfs from Study Event id " + studyEventBean.getId() + ", location "
                                    + studyEventBean.getLocation());
                                // spell out criteria and create a bean if
                                // necessary, avoiding false-positives
                                if (studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.SCHEDULED)) {
                                    EventCRFBean newEventCrfBean = new EventCRFBean();
                                    newEventCrfBean.setStudyEventId(studyEventBean.getId());
                                    newEventCrfBean.setStudySubjectId(studySubjectBean.getId());
                                    newEventCrfBean.setCRFVersionId(crfVersionBean.getId());
                                    newEventCrfBean.setDateInterviewed(new Date());
                                    newEventCrfBean.setOwner(ub);
                                    newEventCrfBean.setInterviewerName(ub.getName());
                                    newEventCrfBean.setCompletionStatusId(1);// place
                                    // filler
                                    newEventCrfBean.setStatus(Status.AVAILABLE);
                                    newEventCrfBean.setStage(DataEntryStage.INITIAL_DATA_ENTRY);
                                    // these will be updated later in the
                                    // workflow
                                    newEventCrfBean = (EventCRFBean) eventCrfDAO.create(newEventCrfBean);
                                    eventCrfBeans.add(newEventCrfBean);
                                    System.out.println("   created and added new event crf");
                                }
                            }

                            // below to prevent duplicates

                            for (EventCRFBean ecb : eventCrfBeans) {
                                Integer ecbId = new Integer(ecb.getId());

                                if (!eventCRFBeanIds.contains(ecbId)) {
                                    eventCRFBeans.add(ecb);
                                    eventCRFBeanIds.add(ecbId);
                                }
                            }
                            // eventCRFBeans.addAll(eventCrfBeans);
                        }
                        // 
                        // 
                    }
                }
            }
        }
        // if it's null, throw an error, since they should be existing beans for
        // iteration one
        return eventCRFBeans;
    }

    public List<DisplayItemBeanWrapper> lookupValidationErrors(HttpServletRequest request, ODMContainer odmContainer, UserAccountBean ub) {

        HashMap validationErrors = new HashMap();
        List<DisplayItemBeanWrapper> wrappers = new ArrayList<DisplayItemBeanWrapper>();
        ImportHelper importHelper = new ImportHelper();
        FormDiscrepancyNotes discNotes = new FormDiscrepancyNotes();
        DiscrepancyValidator discValidator = new DiscrepancyValidator(request, discNotes);
        StudyEventDAO studyEventDAO = new StudyEventDAO(ds);
        StudyDAO studyDAO = new StudyDAO(ds);
        StudyBean studyBean = studyDAO.findByOid(odmContainer.getCrfDataPostImportContainer().getStudyOID());
        StudySubjectDAO studySubjectDAO = new StudySubjectDAO(ds);
        StudyEventDefinitionDAO sedDao = new StudyEventDefinitionDAO(ds);

        ArrayList<SubjectDataBean> subjectDataBeans = odmContainer.getCrfDataPostImportContainer().getSubjectData();
        for (SubjectDataBean subjectDataBean : subjectDataBeans) {
            System.out.println("iterating through subject data beans: found " + subjectDataBean.getSubjectOID());
            ArrayList<StudyEventDataBean> studyEventDataBeans = subjectDataBean.getStudyEventData();
            DisplayItemBeanWrapper displayItemBeanWrapper = null;
            // ArrayList<Integer> eventCRFBeanIds = new ArrayList<Integer>();
            // to stop repeats...?
            StudySubjectBean studySubjectBean = studySubjectDAO.findByOidAndStudy(subjectDataBean.getSubjectOID(), studyBean.getId());
            for (StudyEventDataBean studyEventDataBean : studyEventDataBeans) {
                StudyEventDefinitionBean sedBean = sedDao.findByOidAndStudy(studyEventDataBean.getStudyEventOID(), studyBean.getId());
                ArrayList<FormDataBean> formDataBeans = studyEventDataBean.getFormData();
                System.out.println("iterating through study event data beans: found " + studyEventDataBean.getStudyEventOID());
                ArrayList<DisplayItemBean> displayItemBeans = new ArrayList<DisplayItemBean>();
                int ordinal = 1;
                try {
                    ordinal = new Integer(studyEventDataBean.getStudyEventRepeatKey()).intValue();
                } catch (Exception e) {
                    // trying to catch NPEs, because tags can be without the
                    // repeat key
                }
                StudyEventBean studyEvent =
                    (StudyEventBean) studyEventDAO.findByStudySubjectIdAndDefinitionIdAndOrdinal(studySubjectBean.getId(), sedBean.getId(), ordinal);

                for (FormDataBean formDataBean : formDataBeans) {

                    displayItemBeans = new ArrayList<DisplayItemBean>();
                    CRFVersionDAO crfVersionDAO = new CRFVersionDAO(ds);
                    EventCRFDAO eventCRFDAO = new EventCRFDAO(ds);
                    ArrayList<CRFVersionBean> crfVersionBeans = crfVersionDAO.findAllByOid(formDataBean.getFormOID());
                    ArrayList<ImportItemGroupDataBean> itemGroupDataBeans = formDataBean.getItemGroupData();

                    CRFVersionBean crfVersion = crfVersionBeans.get(0);
                    System.out.println("iterating through form beans: found " + crfVersion.getOid());
                    // may be the point where we cut off item groups etc and
                    // instead work on sections
                    EventCRFBean eventCRFBean = eventCRFDAO.findByEventCrfVersion(studyEvent, crfVersion);
                    for (ImportItemGroupDataBean itemGroupDataBean : itemGroupDataBeans) {
                        ArrayList<ImportItemDataBean> itemDataBeans = itemGroupDataBean.getItemData();
                        System.out.println("iterating through group beans: " + itemGroupDataBean.getItemGroupOID());
                        for (ImportItemDataBean importItemDataBean : itemDataBeans) {
                            System.out.println("   iterating through item data beans: " + importItemDataBean.getItemOID());
                            ItemDAO itemDAO = new ItemDAO(ds);
                            ItemFormMetadataDAO itemFormMetadataDAO = new ItemFormMetadataDAO(ds);
                            ItemDataDAO itemDataDAO = new ItemDataDAO(ds);

                            List<ItemBean> itemBeans = itemDAO.findByOid(importItemDataBean.getItemOID());
                            ItemBean itemBean = itemBeans.get(0);
                            System.out.println("   found " + itemBean.getName());
                            // throw a
                            // null
                            // pointer?
                            // hopefully
                            // not if
                            // its been
                            // checked...
                            DisplayItemBean displayItemBean = new DisplayItemBean();
                            displayItemBean.setItem(itemBean);

                            ArrayList<ItemFormMetadataBean> metadataBeans = itemFormMetadataDAO.findAllByItemId(itemBean.getId());
                            System.out.println("      found metadata item beans: " + metadataBeans.size());

                            ItemDataBean itemDataBean = createItemDataBean(itemBean, eventCRFBean, importItemDataBean.getValue(), ub);
                            ItemFormMetadataBean metadataBean = metadataBeans.get(0);
                            // also
                            // possible
                            // nullpointer
                            displayItemBean.setData(itemDataBean);
                            displayItemBean.setMetadata(metadataBean);

                            attachValidator(displayItemBean, importHelper, discValidator, request);
                            displayItemBeans.add(displayItemBean);
                        }
                    }
                    // for (CRFVersionBean crfVersionBean : crfVersionBeans) {
                    // if (eventCRFBean.getCRFVersionId() ==
                    // crfVersionBean.getId()) {
                    // this is the one that we want

                    // SectionDAO sectionDAO = new SectionDAO(ds);

                    // ArrayList<SectionBean> sectionBeans =
                    // sectionDAO.findAllByCRFVersionId(crfVersionBean.getId());
                    // for (SectionBean sectionBean : sectionBeans) {
                    //
                    // ItemDAO itemDAO = new ItemDAO(ds);
                    //
                    // ArrayList<ItemBean> itemBeans =
                    // itemDAO.findAllBySectionId(sectionBean.getId());
                    //
                    // ItemFormMetadataDAO itemFormMetadataDAO = new
                    // ItemFormMetadataDAO(ds);
                    // for (ItemBean itemBean : itemBeans) {
                    // ArrayList<ItemFormMetadataBean> metadataBeans =
                    // itemFormMetadataDAO.findAllByItemId(itemBean.getId());
                    // ItemDataBean itemDataBean =
                    // createItemDataBean(itemBean, eventCRFBean);
                    // DisplayItemBean displayItemBean = new
                    // DisplayItemBean();
                    // displayItemBean.setData(itemDataBean);
                    // displayItemBean.setMetadata(metadataBeans.get(0));
                    // displayItemBean.setItem(itemBean);
                    //                                    
                    // ImportHelper importHelper = new ImportHelper();
                    // attachValidator(displayItemBean, importHelper,
                    // );// disc
                    // // validator
                    // }
                    // }// after sections, validate?
                    // }
                    // }
                    // CRFVersionBean crfVersionBean = crfVersionBeans.get(0);

                    CRFDAO crfDAO = new CRFDAO(ds);
                    CRFBean crfBean = crfDAO.findByVersionId(crfVersion.getCrfId());
                    // seems like an extravagance, but is not contained in crf
                    // version or event crf bean
                    validationErrors = discValidator.validate();
                    for (Object errorKey : validationErrors.keySet()) {
                        System.out.println(errorKey.toString() + " -- " + validationErrors.get(errorKey));
                    }
                    String studyEventId = studyEvent.getId() + "";
                    String crfVersionId = crfVersion.getId() + "";

                    System.out.println("creation of wrapper: count of display item beans " + displayItemBeans.size() + " count of validation errors "
                        + validationErrors.size());
                    // check if we need to overwrite
                    DataEntryStage dataEntryStage = eventCRFBean.getStage();
                    Status eventCRFStatus = eventCRFBean.getStatus();
                    boolean overwrite = false;
                    if (eventCRFStatus.equals(Status.UNAVAILABLE) || dataEntryStage.equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE)
                        || dataEntryStage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE)) {
                        overwrite = true;
                    }

                    displayItemBeanWrapper =
                        new DisplayItemBeanWrapper(displayItemBeans, true, overwrite, validationErrors, studyEventId, crfVersionId, studyEventDataBean
                                .getStudyEventOID(), studySubjectBean.getLabel(), eventCRFBean.getCreatedDate(), crfBean.getName(), crfVersion.getName());
                    validationErrors = new HashMap();
                    discValidator = new DiscrepancyValidator(request, discNotes);
                    // reset to allow for new errors...
                }// after forms
                wrappers.add(displayItemBeanWrapper);
            }// after study events

            // remove repeats here? remove them below by only forwarding the
            // first
            // each wrapper represents an Event CRF and a Form, but we don't
            // have all events for all forms
            // need to not add a wrapper for every event + form combination,
            // but instead for every event + form combination which is present
            // look at the hack below and see what happens
        }// after study subjects

        return wrappers;
    }

    private ItemDataBean createItemDataBean(ItemBean itemBean, EventCRFBean eventCrfBean, String value, UserAccountBean ub) {

        ItemDataBean itemDataBean = new ItemDataBean();
        itemDataBean.setItemId(itemBean.getId());
        itemDataBean.setEventCRFId(eventCrfBean.getId());
        itemDataBean.setCreatedDate(new Date());
        itemDataBean.setOrdinal(1);
        itemDataBean.setOwner(ub);
        itemDataBean.setStatus(Status.UNAVAILABLE);
        itemDataBean.setValue(value);

        return itemDataBean;
    }

    private void attachValidator(DisplayItemBean displayItemBean, ImportHelper importHelper, DiscrepancyValidator v, HttpServletRequest request) {

        org.akaza.openclinica.bean.core.ResponseType rt = displayItemBean.getMetadata().getResponseSet().getResponseType();

        if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXT) || rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXTAREA)) {
            // logger.info(displayItemBean.getItem().getName() + "is a TEXT or a
            // TEXTAREA ");
            request.setAttribute(displayItemBean.getItem().getName(), displayItemBean.getData().getValue());
            displayItemBean = importHelper.validateDisplayItemBeanText(v, displayItemBean, displayItemBean.getItem().getName());
            // errors = v.validate();
        }

        else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.RADIO) || rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECT)) {
            // logger.info(displayItemBean.getItem().getName() + "is a RADIO or
            // a SELECT ");
            String theValue =
                matchValueWithOptions(displayItemBean, displayItemBean.getData().getValue(), displayItemBean.getMetadata().getResponseSet().getOptions());
            request.setAttribute(displayItemBean.getItem().getName(), theValue);
            System.out.println("        found the value: " + theValue);
            displayItemBean = importHelper.validateDisplayItemBeanSingleCV(v, displayItemBean, displayItemBean.getItem().getName());
            // errors = v.validate();
        } else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CHECKBOX) || rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECTMULTI)) {
            // logger.info(displayItemBean.getItem().getName() + "is a CHECKBOX
            // or a SELECTMULTI ");
            String theValue =
                matchValueWithOptions(displayItemBean, displayItemBean.getData().getValue(), displayItemBean.getMetadata().getResponseSet().getOptions());
            request.setAttribute(displayItemBean.getItem().getName(), theValue);
            displayItemBean = importHelper.validateDisplayItemBeanMultipleCV(v, displayItemBean, displayItemBean.getItem().getName());
            // errors = v.validate();
        }
    }

    private String matchValueWithOptions(DisplayItemBean displayItemBean, String value, List options) {
        String returnedValue = null;
        if (!options.isEmpty()) {
            for (Object responseOption : options) {
                ResponseOptionBean responseOptionBean = (ResponseOptionBean) responseOption;
                if (responseOptionBean.getValue().equals(value)) {
                    // if (((ResponseOptionBean)
                    // responseOption).getText().equals(value)) {
                    displayItemBean.getData().setValue(((ResponseOptionBean) responseOption).getValue());
                    return ((ResponseOptionBean) responseOption).getValue();

                }
            }
        }
        return returnedValue;
    }

    /*
     * meant to answer the following questions 3.a. is that study subject in
     * that study? 3.b. is that study event def in that study? 3.c. is that site
     * in that study? 3.d. is that crf version in that study event def? 3.e. are
     * those item groups in that crf version? 3.f. are those items in that item
     * group?
     */
    public List<String> validateStudyMetadata(ODMContainer odmContainer, int currentStudyId) {
        List<String> errors = new ArrayList<String>();
        try {
            StudyDAO studyDAO = new StudyDAO(ds);
            String studyOid = odmContainer.getCrfDataPostImportContainer().getStudyOID();
            StudyBean studyBean = studyDAO.findByOid(studyOid);
            if (studyBean == null) {
                errors.add("Your Study OID " + studyOid + " does not reference an existing Study or Site in the database.  Please check it and try again.");
                // throw an error here because getting the ID would be difficult
                // otherwise
                System.out.println("unknown study OID");
                throw new OpenClinicaException("Unknown Study OID", "");

            } else if (studyBean.getId() != currentStudyId) {
                errors.add("Your current study is not the same as the Study " + studyBean.getName()
                    + ", for which you are trying to enter data.  Please log out of your current study and into the study for which the data is keyed.");
            }
            ArrayList<SubjectDataBean> subjectDataBeans = odmContainer.getCrfDataPostImportContainer().getSubjectData();

            StudySubjectDAO studySubjectDAO = new StudySubjectDAO(ds);
            StudyEventDefinitionDAO studyEventDefinitionDAO = new StudyEventDefinitionDAO(ds);
            CRFVersionDAO crfVersionDAO = new CRFVersionDAO(ds);
            ItemGroupDAO itemGroupDAO = new ItemGroupDAO(ds);
            ItemDAO itemDAO = new ItemDAO(ds);

            if (subjectDataBeans != null) {// need to do this so as not to
                // throw the exception below and
                // report all available errors, tbh
                for (SubjectDataBean subjectDataBean : subjectDataBeans) {
                    String oid = subjectDataBean.getSubjectOID();
                    StudySubjectBean studySubjectBean = studySubjectDAO.findByOidAndStudy(oid, studyBean.getId());
                    if (studySubjectBean == null) {
                        errors.add("Your Subject OID " + oid + " does not reference an existing Subject in the Study.");
                        System.out.println("logged an error with subject oid " + oid);
                    }

                    ArrayList<StudyEventDataBean> studyEventDataBeans = subjectDataBean.getStudyEventData();
                    if (studyEventDataBeans != null) {
                        for (StudyEventDataBean studyEventDataBean : studyEventDataBeans) {
                            String sedOid = studyEventDataBean.getStudyEventOID();
                            StudyEventDefinitionBean studyEventDefintionBean = studyEventDefinitionDAO.findByOidAndStudy(sedOid, studyBean.getId());
                            if (studyEventDefintionBean == null) {
                                errors.add("Your Study Event OID " + sedOid + " for Subject OID " + oid
                                    + " does not reference an existing Study Event in the Study.");
                                System.out.println("logged an error with se oid " + sedOid + " and subject oid " + oid);
                            }

                            ArrayList<FormDataBean> formDataBeans = studyEventDataBean.getFormData();
                            if (formDataBeans != null) {
                                for (FormDataBean formDataBean : formDataBeans) {
                                    String formOid = formDataBean.getFormOID();
                                    ArrayList<CRFVersionBean> crfVersionBeans = crfVersionDAO.findAllByOid(formOid);
                                    // ideally we should look to compare
                                    // versions within
                                    // seds;
                                    // right now just check nulls
                                    if (crfVersionBeans != null) {
                                        for (CRFVersionBean crfVersionBean : crfVersionBeans) {
                                            if (crfVersionBean == null) {
                                                errors.add("Your CRF Version OID " + formOid + " for Study Event OID " + sedOid
                                                    + " does not reference a proper CRF Version in that Study Event.");
                                                System.out.println("logged an error with form " + formOid + " and se oid " + sedOid);
                                            }
                                        }
                                    } else {
                                        errors.add("Your CRF Version OID " + formOid
                                            + " did not generate any resultss in the database.  Please check it and try again.");
                                    }

                                    ArrayList<ImportItemGroupDataBean> itemGroupDataBeans = formDataBean.getItemGroupData();
                                    if (itemGroupDataBeans != null) {
                                        for (ImportItemGroupDataBean itemGroupDataBean : itemGroupDataBeans) {
                                            String itemGroupOID = itemGroupDataBean.getItemGroupOID();
                                            List<ItemGroupBean> itemGroupBeans = itemGroupDAO.findByOid(itemGroupOID);
                                            if (itemGroupBeans != null) {
                                                for (ItemGroupBean itemGroupBean : itemGroupBeans) {
                                                    if (itemGroupBean == null) {
                                                        errors.add("Your Item Group OID " + itemGroupOID + " for Form OID " + formOid
                                                            + " does not reference a proper Item Group in that CRF Version.");
                                                    }
                                                }
                                            } else {
                                                errors.add("The Item Group OID " + itemGroupOID
                                                    + " did not generate any results in the database, please check it and try again.");
                                            }

                                            ArrayList<ImportItemDataBean> itemDataBeans = itemGroupDataBean.getItemData();
                                            if (itemDataBeans != null) {
                                                for (ImportItemDataBean itemDataBean : itemDataBeans) {
                                                    String itemOID = itemDataBean.getItemOID();
                                                    List<ItemBean> itemBeans = itemDAO.findByOid(itemOID);
                                                    if (itemBeans != null) {
                                                        for (ItemBean itemBean : itemBeans) {
                                                            if (itemBean == null) {
                                                                errors.add("Your Item OID " + itemOID + " for Item Group OID " + itemGroupOID
                                                                    + " does not reference a proper Item in the Item Group.");
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                errors.add("The Item Group OID " + itemGroupOID
                                                    + " did not contain any Item Data in the XML file, please check it and try again.");
                                            }
                                        }
                                    } else {
                                        errors.add("Your Study Event " + sedOid
                                            + " contains no Form Data, or the Form OIDs are incorrect.  Please check it and try again.");
                                    }
                                }

                            }
                        }
                    }
                }
            }
        } catch (OpenClinicaException oce) {

        } catch (NullPointerException npe) {
            System.out.println("found a nullpointer here");
        }
        // if errors == null you pass, if not you fail
        return errors;
    }
}
