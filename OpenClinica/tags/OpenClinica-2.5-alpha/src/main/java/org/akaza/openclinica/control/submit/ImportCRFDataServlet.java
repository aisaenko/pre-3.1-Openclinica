/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.rule.XmlSchemaValidationHelper;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ResponseOptionBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.bean.submit.crfdata.ODMContainer;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.DiscrepancyValidator;
import org.akaza.openclinica.core.form.FormDiscrepancyNotes;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.logic.importdata.ImportDataHelper;
import org.akaza.openclinica.service.crfdata.ImportCRFDataService;
import org.akaza.openclinica.view.Page;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Unmarshaller;

import com.oreilly.servlet.MultipartRequest;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 * Create a new CRF verison by uploading excel file
 * 
 * @author Krikor Krumlian
 */
public class ImportCRFDataServlet extends SecureController {

    Locale locale;

    private ImportCRFDataService dataService;

    XmlSchemaValidationHelper schemaValidator = new XmlSchemaValidationHelper();

    // < ResourceBundleresword,resexception,respage;

    /**
     * 
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {

        locale = request.getLocale();
        if (ub.isSysAdmin()) {
            return;
        }

        Role r = currentRole.getRole();
        if (r.equals(Role.STUDYDIRECTOR) || r.equals(Role.COORDINATOR) || r.equals(Role.INVESTIGATOR) || r.equals(Role.RESEARCHASSISTANT)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
    }

    private ItemDataBean createItemDataBean(ItemBean itemBean, EventCRFBean eventCrfBean, HashMap importedObject) {

        ItemDataBean itemDataBean = new ItemDataBean();
        itemDataBean.setItemId(itemBean.getId());
        itemDataBean.setEventCRFId(eventCrfBean.getId());
        itemDataBean.setCreatedDate(new Date());
        itemDataBean.setOrdinal(1);
        itemDataBean.setOwner(ub);
        itemDataBean.setStatus(Status.UNAVAILABLE);

        String value = importedObject.containsKey(itemBean.getName()) == true ? (String) importedObject.get(itemBean.getName()) : "";
        // above causes NPE, so we need to return "" instead, tbh

        logger.info("The value of the item data name " + itemBean.getName() + " is : " + value);
        itemDataBean.setValue(value);

        return itemDataBean;
    }

    private String matchValueWithOptions(DisplayItemBean displayItemBean, String value, List options) {
        String returnedValue = null;
        if (!options.isEmpty()) {
            for (Object responseOption : options) {
                if (((ResponseOptionBean) responseOption).getText().equals(value)) {
                    displayItemBean.getData().setValue(((ResponseOptionBean) responseOption).getValue());
                    return ((ResponseOptionBean) responseOption).getValue();

                }
            }
        }
        return returnedValue;
    }

    private void attachValidator(DisplayItemBean displayItemBean, ImportHelper importHelper, DiscrepancyValidator v) {

        org.akaza.openclinica.bean.core.ResponseType rt = displayItemBean.getMetadata().getResponseSet().getResponseType();

        if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXT) || rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXTAREA)) {
            logger.info(displayItemBean.getItem().getName() + "is a TEXT or a TEXTAREA ");
            request.setAttribute(displayItemBean.getItem().getName(), displayItemBean.getData().getValue());
            displayItemBean = importHelper.validateDisplayItemBeanText(v, displayItemBean, displayItemBean.getItem().getName());
            // errors = v.validate();
        }

        else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.RADIO) || rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECT)) {
            logger.info(displayItemBean.getItem().getName() + "is a RADIO or a SELECT ");
            String theValue =
                matchValueWithOptions(displayItemBean, displayItemBean.getData().getValue(), displayItemBean.getMetadata().getResponseSet().getOptions());
            request.setAttribute(displayItemBean.getItem().getName(), theValue);
            displayItemBean = importHelper.validateDisplayItemBeanSingleCV(v, displayItemBean, displayItemBean.getItem().getName());
            // errors = v.validate();
        } else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CHECKBOX) || rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECTMULTI)) {
            logger.info(displayItemBean.getItem().getName() + "is a CHECKBOX or a SELECTMULTI ");
            String theValue =
                matchValueWithOptions(displayItemBean, displayItemBean.getData().getValue(), displayItemBean.getMetadata().getResponseSet().getOptions());
            request.setAttribute(displayItemBean.getItem().getName(), theValue);
            displayItemBean = importHelper.validateDisplayItemBeanMultipleCV(v, displayItemBean, displayItemBean.getItem().getName());
            // errors = v.validate();
        }
    }

    private DisplayItemBeanWrapper testing(HttpServletRequest request, HashMap<String, String> importedObject) {
        // Define input variables
        // int eventCrfBeanId = 2;

        HashMap validationErrors = new HashMap();
        List<DisplayItemBean> displayItemBeans = new ArrayList<DisplayItemBean>();

        ImportDataHelper idHelper = new ImportDataHelper();
        idHelper.setSessionManager(sm);
        idHelper.setUserAccountBean(ub);
        EventCRFBean eventCrfBean = idHelper.createEventCRF(importedObject);

        String studyEventId = String.valueOf(eventCrfBean.getStudyEventId());
        String crfVersionId = String.valueOf(eventCrfBean.getCRFVersionId());

        String crfVersionName = eventCrfBean.getCrfVersion().getName();
        String crfName = eventCrfBean.getCrf().getName();
        logger.info("Just pulled it from the bean: " + crfName + " " + crfVersionName);
        Date dateOfEvent = eventCrfBean.getCreatedDate();
        String studySubjectId = String.valueOf(eventCrfBean.getStudySubjectId());
        // study event name? study subject name?

        StudyEventDAO studyEventDao = new StudyEventDAO(sm.getDataSource());
        StudyEventBean studyEventBean = (StudyEventBean) studyEventDao.findByPK(eventCrfBean.getStudyEventId());
        String studyEventName = studyEventBean.getLocation();// studyEventBean.getName();
        logger.info("found " + studyEventName + " from ID " + eventCrfBean.getStudyEventId());

        StudySubjectDAO studySubjectDao = new StudySubjectDAO(sm.getDataSource());
        StudySubjectBean studySubjectBean = (StudySubjectBean) studySubjectDao.findByPK(eventCrfBean.getStudySubjectId());
        String studySubjectName = studySubjectBean.getName();

        // dont really get this far, tbh 02/08
        logger.info("event crf bean: " + eventCrfBean.getId() + " " + eventCrfBean.getName());
        logger.info("event crf bean updated date: " + eventCrfBean.getUpdatedDate());
        if (eventCrfBean != null && eventCrfBean.getUpdatedDate() == null) {
            logger.info("got this far...");
            /*
             * StudySubjectDAO studySubjectDao = new StudySubjectDAO(sm
             * .getDataSource());
             */
            ItemDAO idao = new ItemDAO(sm.getDataSource());
            SectionDAO sectionDao = new SectionDAO(sm.getDataSource());
            ItemFormMetadataDAO itemFormMetadataDao = new ItemFormMetadataDAO(sm.getDataSource());

            /*
             * HashMap<String, String> importedObject = new HashMap<String,
             * String>(); importedObject.put("hair_01", "Blue");
             * importedObject.put("eye_01", "Blue");
             * importedObject.put("Height_01", "80");
             * importedObject.put("Weight_01", "120");
             */
            // If the status of the study subject bean is removed then reject.
            Status s = studySubjectBean.getStatus();
            if ("removed".equalsIgnoreCase(s.getName()) || "auto-removed".equalsIgnoreCase(s.getName())) {
                logger.info("Report Error");
            }

            ArrayList sections = sectionDao.findAllByCRFVersionId(eventCrfBean.getCRFVersionId());

            FormDiscrepancyNotes discNotes = new FormDiscrepancyNotes();
            DiscrepancyValidator v = new DiscrepancyValidator(request, discNotes);
            ImportHelper importHelper = new ImportHelper();
            // CRFVersionDAO crfVersionDao = new
            // CRFVersionDAO(sm.getDataSource());

            for (int i = 0; i < sections.size(); i++) {
                SectionBean sectionBean = (SectionBean) sections.get(i);
                logger.info("section id " + sectionBean.getId());

                ArrayList items = idao.findAllBySectionId(sectionBean.getId());
                for (int j = 0; j < items.size(); j++) {

                    ItemBean item = (ItemBean) items.get(j);
                    logger.info("item id " + item.getId());
                    ItemFormMetadataBean itemFormMetadataBean = null;
                    ArrayList itemFormMetadataBeans = itemFormMetadataDao.findAllByItemId(item.getId());

                    if (!itemFormMetadataBeans.isEmpty() && itemFormMetadataBeans.size() == 1) {
                        itemFormMetadataBean = (ItemFormMetadataBean) itemFormMetadataBeans.get(0);
                    } else {
                        logger.info("Throw an exception cause there should be an item Form Metadata Bean");
                    }

                    logger.info("hh : " + item.getName() + itemFormMetadataBeans.size());
                    ItemDataBean itemDataBean = createItemDataBean(item, eventCrfBean, importedObject);

                    // Trying to get validation to work
                    DisplayItemBean displayItemBean = new DisplayItemBean();
                    displayItemBean.setData(itemDataBean);
                    displayItemBean.setMetadata(itemFormMetadataBean);
                    displayItemBean.setItem(item);
                    displayItemBeans.add(displayItemBean);

                    attachValidator(displayItemBean, importHelper, v);

                    // itemDataDao.create(itemDataBean);
                    /*
                     * if (completeStatus != null){// to avoid null exception
                     * idb.setStatus(completeStatus); } else {
                     * idb.setStatus(Status.UNAVAILABLE); } idb.setValue("");
                     * iddao.create(idb);
                     */
                }
            }
            validationErrors = v.validate();
            for (Object errorKey : validationErrors.keySet()) {
                logger.info(errorKey.toString() + " -- " + validationErrors.get(errorKey));
            }
        }

        /*
         * if (validationErrors.isEmpty()) { for (DisplayItemBean
         * displayItemBean : displayItemBeans) {
         * itemDataDao.create(displayItemBean.getData()); } }
         */

        // currently looks like the only place where we set isSavable() here,
        // tbh
        logger.info("found study event name: " + studyEventName);
        logger.info("found crf name: " + crfName);
        return new DisplayItemBeanWrapper(displayItemBeans,
        // validationErrors
                // .isEmpty(),
                true, false, validationErrors, studyEventId, crfVersionId, studyEventName, studySubjectName, dateOfEvent, crfName, crfVersionName);
        // adding string (study event name)
        // string (study subject name)
        // date (date of event) here
    }

    @Override
    public void processRequest() throws Exception {
        resetPanel();
        panel.setStudyInfoShown(false);
        panel.setOrderedData(true);

        FormProcessor fp = new FormProcessor(request);
        // checks which module the requests are from
        String module = fp.getString(MODULE);
        // keep the module in the session
        session.setAttribute(MODULE, module);

        String action = request.getParameter("action");
        CRFVersionBean version = (CRFVersionBean) session.getAttribute("version");

        File xsdFile = new File(getServletContext().getInitParameter("propertiesDir") + "ODM1-3-0.xsd");
        if (StringUtil.isBlank(action)) {
            logger.info("action is blank");
            request.setAttribute("version", version);
            forwardPage(Page.IMPORT_CRF_DATA);
        }
        if ("confirm".equalsIgnoreCase(action)) {
            String dir = SQLInitServlet.getField("filePath");
            if (!(new File(dir)).exists()) {
                logger.info("The filePath in datainfo.properties is invalid " + dir);
                addPageMessage("The filePath you defined in datainfo.properties does not seem to be a valid path, please check it.");
                forwardPage(Page.IMPORT_CRF_DATA);
            }
            // All the uploaded files will be saved in filePath/crf/original/
            String theDir = dir + "crf" + File.separator + "original" + File.separator;
            if (!(new File(theDir)).isDirectory()) {
                (new File(theDir)).mkdirs();
                logger.info("Made the directory " + theDir);
            }
            MultipartRequest multi = new MultipartRequest(request, theDir, 50 * 1024 * 1024);
            File f = null;
            try {
                f = uploadFile(multi, theDir, version);

            } catch (Exception e) {
                logger.warning("*** Found exception during file upload***");
                e.printStackTrace();

            }
            if (f == null) {
                forwardPage(Page.IMPORT_CRF_DATA);
            }

            // TODO
            // validation steps
            // 1. valid xml - validated by file uploader below

            // LocalConfiguration config = LocalConfiguration.getInstance();
            // config.getProperties().setProperty("org.exolab.castor.parser.namespaces",
            // "true");
            // config
            // .getProperties()
            // .setProperty("org.exolab.castor.sax.features",
            // "http://xml.org/sax/features/validation,
            // http://apache.org/xml/features/validation/schema,
            // http://apache.org/xml/features/validation/schema-full-checking");
            // // above sets to validate against namespace

            Mapping myMap = new Mapping();
            String propertiesPath = SQLInitServlet.PROPERTY_DIR;
            myMap.loadMapping(propertiesPath + File.separator + "cd_odm_mapping.xml");

            Unmarshaller um1 = new Unmarshaller(myMap);
            // um1.addNamespaceToPackageMapping("http://www.cdisc.org/ns/odm/v1.3",
            // "ODMContainer");
            boolean fail = false;
            ODMContainer odmContainer = new ODMContainer();
            try {
                schemaValidator.validateAgainstSchema(f, xsdFile);
                odmContainer = (ODMContainer) um1.unmarshal(new FileReader(f));

                System.out.println("Found crf data container for study oid: " + odmContainer.getCrfDataPostImportContainer().getStudyOID());
                System.out.println("found length of subject list: " + odmContainer.getCrfDataPostImportContainer().getSubjectData().size());
                // 2. validates against ODM 1.3
                // check it all below, throw an exception and route to a
                // different
                // page if not working

                // TODO this block of code needs the xerces serializer in order
                // to
                // work

                // StringWriter myWriter = new StringWriter();
                // Marshaller m1 = new Marshaller(myWriter);
                //
                // m1.setProperty("org.exolab.castor.parser.namespaces",
                // "true");
                // m1
                // .setProperty("org.exolab.castor.sax.features",
                // "http://xml.org/sax/features/validation,
                // http://apache.org/xml/features/validation/schema,
                // http://apache.org/xml/features/validation/schema-full-checking");
                //
                // m1.setMapping(myMap);
                // m1.setNamespaceMapping("",
                // "http://www.cdisc.org/ns/odm/v1.3");
                // m1.setSchemaLocation("http://www.cdisc.org/ns/odm/v1.3
                // ODM1-3.xsd");
                // m1.marshal(odmContainer);
                // if you havent thrown it, you wont throw it here
                addPageMessage("Passed XML validation...");
            } catch (Exception me1) {
                // expanding it to all exceptions, but hoping to catch Marshal
                // Exception or SAX Exceptions
                logger.info("found exception with xml transform");
                addPageMessage("Your XML is not well-formed, and does not comply with the ODM 1.3 Schema.  Please check it, and try again.  It returned the message: "
                    + me1.getMessage());
                me1.printStackTrace();
                forwardPage(Page.IMPORT_CRF_DATA);
                // you can't really wait to forward because then you throw NPEs
                // in the next few parts of the code

            }
            // TODO need to output further here
            // 2.a. is the study the same one that the user is in right now?
            // 3. validates against study metadata
            // 3.a. is that study subject in that study?
            // 3.b. is that study event def in that study?
            // 3.c. is that site in that study?
            // 3.d. is that crf version in that study event def?
            // 3.e. are those item groups in that crf version?
            // 3.f. are those items in that item group?

            List<String> errors = getImportCRFDataService().validateStudyMetadata(odmContainer, ub.getActiveStudyId());
            if (errors != null) {
                // add to session
                // forward to another page
                logger.info(errors.toString());
                for (String error : errors) {
                    addPageMessage(error);
                }
                if (errors.size() > 0) {
                    // fail = true;
                    forwardPage(Page.IMPORT_CRF_DATA);
                } else {
                    addPageMessage("Passed Study Check...");
                    addPageMessage("Passed OID Metadata Check...");
                }

            }
            // TODO ADD many validation steps before we get to the
            // session-setting below
            // 4. is the event in the correct status to accept data import?
            // -- scheduled, data entry started, completed
            // (and the event should already be created)

            List<EventCRFBean> eventCRFBeans = getImportCRFDataService().fetchEventCRFBeans(odmContainer, ub);
            logger.info("found a list of eventCRFBeans: " + eventCRFBeans.toString());
            List<DisplayItemBeanWrapper> displayItemBeanWrappers = new ArrayList<DisplayItemBeanWrapper>();

            // -- does the event already exist? if not, fail
            if (!eventCRFBeans.isEmpty()) {
                for (EventCRFBean eventCRFBean : eventCRFBeans) {
                    DataEntryStage dataEntryStage = eventCRFBean.getStage();
                    Status eventCRFStatus = eventCRFBean.getStatus();

                    logger.info("Event CRF Bean: id " + eventCRFBean.getId() + ", data entry stage " + dataEntryStage.getName() + ", status "
                        + eventCRFStatus.getName());
                    if (eventCRFStatus.equals(Status.AVAILABLE) || dataEntryStage.equals(DataEntryStage.INITIAL_DATA_ENTRY)
                        || dataEntryStage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE)
                        || dataEntryStage.equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE) || dataEntryStage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
                        // actually want the negative
                        // was status == available and the stage questions, but
                        // when you are at 'data entry complete' your status is
                        // set to 'unavailable'.

                    } else {
                        fail = true;
                        addPageMessage("The Event CRF you are trying to update does not have the correct status.");
                        // forwardPage(Page.IMPORT_CRF_DATA);
                    }
                }

                // so that we don't repeat this following message
                if (!fail) {
                    addPageMessage("Passed Event CRF Status Check...");
                }
                // do they all have to have the right status to move
                // forward?
                // 5. do the items contain the correct data types?

                // 6. are all the related OIDs present?
                // that is to say, do we chain all the way down?
                // this is covered by the OID Metadata Check

                // 7. do the edit checks pass?
                // only then can we pass on to VERIFY_IMPORT_SERVLET

                // do we overwrite?

                // XmlParser xp = new XmlParser();
                // List<HashMap<String, String>> importedData =
                // xp.getData(f);
                try {
                    List<DisplayItemBeanWrapper> tempDisplayItemBeanWrappers = new ArrayList<DisplayItemBeanWrapper>();
                    tempDisplayItemBeanWrappers = getImportCRFDataService().lookupValidationErrors(request, odmContainer, ub);

                    displayItemBeanWrappers.addAll(tempDisplayItemBeanWrappers);
                } catch (NullPointerException npe1) {
                    // what if you have 2 event crfs but the third is a fake?
                    fail = true;
                    addPageMessage("An error was thrown while reviewing validation errors.  It could be that one of several event CRFs is not scheduled yet, please make sure all of the event CRFs in the file are scheduled.");
                }
            } else {
                fail = true;
                addPageMessage("No Event CRFs matching the XML metadata were found.  Please schedule an Event CRF and try again.");
            }
            // for (HashMap<String, String> crfData : importedData) {
            // DisplayItemBeanWrapper displayItemBeanWrapper =
            // testing(request,
            // crfData);
            // displayItemBeanWrappers.add(displayItemBeanWrapper);
            // errors = displayItemBeanWrapper.getValidationErrors();
            //
            // }
            if (fail) {
                forwardPage(Page.IMPORT_CRF_DATA);
            } else {
                addPageMessage("Passing CRF Edit Checks...");
                session.setAttribute("importedData", displayItemBeanWrappers);
                forwardPage(Page.VERIFY_IMPORT_SERVLET);
            }
        }
    }

    /*
     * Given the MultipartRequest extract the first File validate that it is an
     * xml file and then return it.
     */
    private File getFirstFile(MultipartRequest multi) {
        File f = null;
        Enumeration files = multi.getFileNames();
        if (files.hasMoreElements()) {
            String name = (String) files.nextElement();
            f = multi.getFile(name);
            if (f == null || f.getName() == null) {
                logger.info("file is empty.");
                Validator.addError(errors, "xml_file", "You have to provide an XML file!");
            } else if (f.getName().indexOf(".xml") < 0 && f.getName().indexOf(".XML") < 0) {
                logger.info("file name:" + f.getName());
                // TODO change the message below
                addPageMessage(respage.getString("file_you_uploaded_not_seem_excel_spreadsheet"));
                f = null;
            }
        }
        return f;
    }

    /**
     * Uploads the xml file
     * 
     * @param version
     * @throws Exception
     */
    public File uploadFile(MultipartRequest multi, String theDir, CRFVersionBean version) throws Exception {

        return getFirstFile(multi);
    }

    public ImportCRFDataService getImportCRFDataService() {
        dataService = this.dataService != null ? dataService : new ImportCRFDataService(sm.getDataSource());
        return dataService;
    }

    @Override
    protected String getAdminServlet() {
        if (ub.isSysAdmin()) {
            return SecureController.ADMIN_SERVLET_CODE;
        } else {
            return "";
        }
    }

}
