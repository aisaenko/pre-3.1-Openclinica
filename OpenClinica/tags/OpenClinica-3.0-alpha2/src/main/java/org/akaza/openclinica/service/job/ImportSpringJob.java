package org.akaza.openclinica.service.job;

import org.akaza.openclinica.bean.admin.TriggerBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.rule.XmlSchemaValidationHelper;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ODMContainer;
import org.akaza.openclinica.bean.submit.crfdata.SubjectDataBean;
import org.akaza.openclinica.bean.submit.crfdata.SummaryStatsBean;
import org.akaza.openclinica.control.admin.CreateJobImportServlet;
import org.akaza.openclinica.control.submit.CrfBusinessLogicHelper;
import org.akaza.openclinica.control.submit.DisplayItemBeanWrapper;
import org.akaza.openclinica.control.submit.VerifyImportedCRFDataServlet;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.dao.admin.AuditEventDAO;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.service.crfdata.ImportCRFDataService;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Unmarshaller;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SimpleTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.scheduling.quartz.JobDetailBean;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.sql.DataSource;

/**
 * Import Spring Job, a job running asynchronously on the Tomcat server using
 * Spring and Quartz.
 * 
 * @author thickerson, 04/2009
 * 
 */
public class ImportSpringJob extends QuartzJobBean {

    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

    XmlSchemaValidationHelper schemaValidator = new XmlSchemaValidationHelper();
    /*
     * variables to be pulled out of the JobDataMap. Note that these are stored
     * in binary format in the database.
     */
    public static final String DIRECTORY = "filePathDir";
    public static final String EMAIL = "contactEmail";
    public static final String USER_ID = "user_id";
    public static final String STUDY_NAME = "study_name";
    public static final String DEST_DIR = "Event_CRF_Data";

    // below is the directory where we copy the files to, our target
    private static final String IMPORT_DIR = SQLInitServlet.getField("filePath") + CreateJobImportServlet.DIR_PATH + File.separator; // +

    private static final String IMPORT_DIR_2 = SQLInitServlet.getField("filePath") + DEST_DIR + File.separator;

    private DataSource dataSource;
    private UserAccountBean userBean;
    private JobDetailBean jobDetailBean;
    private ImportCRFDataService dataService;
    private ItemDataDAO itemDataDao;// = new ItemDataDAO(sm.getDataSource());
    private EventCRFDAO eventCrfDao;// = new EventCRFDAO(sm.getDataSource());
    private AuditEventDAO auditEventDAO;
    private TriggerService triggerService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Locale locale = new Locale("en-US");
        ResourceBundleProvider.updateLocale(locale);
        ResourceBundle respage = ResourceBundleProvider.getPageMessagesBundle();
        triggerService = new TriggerService();

        JobDataMap dataMap = context.getMergedJobDataMap();
        SimpleTrigger trigger = (SimpleTrigger) context.getTrigger();
        TriggerBean triggerBean = new TriggerBean();
        triggerBean.setFullName(trigger.getName());
        String contactEmail = dataMap.getString(EMAIL);
        System.out.println("=== starting to run trigger " + trigger.getName() + " ===");
        try {
            ApplicationContext appContext = (ApplicationContext) context.getScheduler().getContext().get("applicationContext");
            dataSource = (DataSource) appContext.getBean("dataSource");

            itemDataDao = new ItemDataDAO(dataSource);
            eventCrfDao = new EventCRFDAO(dataSource);
            auditEventDAO = new AuditEventDAO(dataSource);

            int userId = dataMap.getInt(USER_ID);
            UserAccountDAO userAccountDAO = new UserAccountDAO(dataSource);

            UserAccountBean ub = (UserAccountBean) userAccountDAO.findByPK(userId);
            triggerBean.setUserAccount(ub);

            String directory = dataMap.getString(DIRECTORY);
            String studyName = dataMap.getString(STUDY_NAME);
            StudyDAO studyDAO = new StudyDAO(dataSource);
            StudyBean studyBean = (StudyBean) studyDAO.findByName(studyName);
            // might also need study id here for the data service?
            File fileDirectory = new File(IMPORT_DIR);
            if ("".equals(directory)) { // avoid NPEs
                // do nothing here?
            } else {
                // there is a separator at the end of IMPORT_DIR already...
                fileDirectory = new File(IMPORT_DIR + directory + File.separator);
            }
            if (!fileDirectory.isDirectory()) {
                fileDirectory.mkdirs();
            }
            // this is necessary the first time this is run, tbh
            File destDirectory = new File(IMPORT_DIR_2);
            if (!destDirectory.isDirectory()) {
                destDirectory.mkdirs();
            }
            // look at directory, if there are new files, move them over and
            // read them
            // File fileDirectory = new File(directory);
            String[] files = fileDirectory.list();
            System.out.println("found " + files.length + " files under directory " + IMPORT_DIR + directory);
            File[] target = new File[files.length];
            File[] destination = new File[files.length];
            int placeHolder = 0;
            for (int i = 0; i < files.length; i++) {
                // hmm
                if (!(new File(fileDirectory + File.separator + files[i])).isDirectory()) {
                    File f = new File(fileDirectory + File.separator + files[i]);
                    if ((f == null) || (f.getName() == null)) {
                        System.out.println("found a null file");
                    } else if (f.getName().indexOf(".xml") < 0 && f.getName().indexOf(".XML") < 0) {
                        System.out.println("does not seem to be an xml file");

                        // we need a place holder to avoid 'gaps' in the file
                        // list
                    } else {
                        System.out.println("adding: " + f.getName());
                        target[i] = f;// new File(IMPORT_DIR +
                        // directory +
                        // File.separator + files[i]);
                        destination[i] = new File(IMPORT_DIR_2 + files[i]);
                    }
                    // target[i] = new File(IMPORT_DIR + directory +
                    // File.separator + files[i]);
                    // destination[i] = new File(IMPORT_DIR_2 + files[i]);
                }
            }
            if ((target.length > 0) && (destination.length > 0)) {
                cutAndPaste(target, destination);
                // do everything else here with 'destination'
                System.out.println("=== about to start processData... ===");
                ArrayList<String> auditMessages = processData(destination, dataSource, respage, ub, studyBean);
                System.out.println("=== finished process data, audit message returned ===");
                // String[] messages = auditMessage.split("===+");

                auditEventDAO.createRowForExtractDataJobSuccess(triggerBean, auditMessages.get(1));
                sendEmail(generateMsg(auditMessages.get(0), contactEmail), triggerBean, contactEmail);

            } else {
                System.out.println("no real files found");
                auditEventDAO.createRowForExtractDataJobSuccess(triggerBean, "Job ran but no real files found.");
                // no email here, tbh
            }

            // use the business logic to go through each one and import that
            // data

            // check to see if they were imported before?

            // using the four methods:
            // importCRFDataServce.validateStudyMetadata,
            // service.lookupValidationErrors, service.fetchEventCRFBeans(?),
            // and
            // service.generateSummaryStatsBean(for the email we send out later)
        } catch (Exception e) {
            // more detailed reporting here
            System.out.println("found a fail exception: " + e.getMessage());
            e.printStackTrace();
            auditEventDAO.createRowForExtractDataJobFailure(triggerBean, e.getMessage());
            sendEmail(e.getMessage(), "Job FAILURE for " + triggerBean.getFullName(), triggerBean, contactEmail);
        }
    }

    private ImportCRFDataService getImportCRFDataService(DataSource dataSource) {
        // TODO dynamic locale?
        Locale locale = new Locale("en-US");
        dataService = this.dataService != null ? dataService : new ImportCRFDataService(dataSource, locale);
        return dataService;
    }

    private void sendEmail(String auditMessage, TriggerBean triggerBean, String contactEmail) {
        try {
            EmailEngine em = new EmailEngine(EmailEngine.getSMTPHost());
            em.processHtml(contactEmail.trim(), EmailEngine.getAdminEmail(), "Job ran for " + triggerBean.getFullName(), auditMessage);
        } catch (MessagingException me) {
            System.out.println("failed to send email");
            me.printStackTrace();
        }
    }

	private String generateMsg(String msg, String contactEmail) {
		String returnMe = "Dear " + contactEmail + ",<br/>Your job ran successfully with the following messages returned.  " 
			+ "Please review the data, or modify the files accordingly so that they can be imported.<br/>" + msg;
		return returnMe;
	}

    private void sendEmail(String auditMessage, String title, TriggerBean triggerBean, String contactEmail) {
        try {
            EmailEngine em = new EmailEngine(EmailEngine.getSMTPHost());
            em.processHtml(contactEmail.trim(), EmailEngine.getAdminEmail(), title, auditMessage);
        } catch (MessagingException me) {
            System.out.println("failed to send email");
            me.printStackTrace();
        }
    }

    /*
     * processData, a method which should take in all XML files, check to see if
     * they were imported previously, ? insert them into the database if not,
     * and return a message which will go to audit and to the end user.
     */
    private ArrayList<String> processData(File[] dest, DataSource dataSource, ResourceBundle respage, UserAccountBean ub, StudyBean studyBean) throws Exception {
        StringBuffer msg = new StringBuffer();
        StringBuffer auditMsg = new StringBuffer();
        Mapping myMap = new Mapping();

        String propertiesPath = SQLInitServlet.PROPERTY_DIR;

        File xsdFile = new File(propertiesPath + File.separator + "ODM1-3-0.xsd");
        File xsdFile2 = new File(propertiesPath + File.separator + "ODM1-2-1.xsd");
        boolean fail = false;
        myMap.loadMapping(propertiesPath + File.separator + "cd_odm_mapping.xml");
        Unmarshaller um1 = new Unmarshaller(myMap);
        ODMContainer odmContainer = new ODMContainer();
        for (File f : dest) {
			if (f != null)
			{
				msg.append("<P>" + f.getName() + ": ");
			} else {
				msg.append("<P>Unreadable File: ");
			}
			
            try {

                // schemaValidator.validateAgainstSchema(f, xsdFile);
                odmContainer = (ODMContainer) um1.unmarshal(new FileReader(f));

                System.out.println("Found crf data container for study oid: " + odmContainer.getCrfDataPostImportContainer().getStudyOID());
                System.out.println("found length of subject list: " + odmContainer.getCrfDataPostImportContainer().getSubjectData().size());
            } catch (Exception me1) {
                // fail against one, try another
                System.out.println("failed in unmarshaling, trying another version");
                try {
                    schemaValidator.validateAgainstSchema(f, xsdFile2);
                    // for backwards compatibility, we also try to validate vs
                    // 1.2.1 ODM 06/2008
                    odmContainer = (ODMContainer) um1.unmarshal(new FileReader(f));
                } catch (Exception me2) {
                    // not sure if we want to report me2

                    MessageFormat mf = new MessageFormat("");
                    mf.applyPattern(respage.getString("your_xml_is_not_well_formed"));
                    Object[] arguments = { me1.getMessage() };
                    msg.append(mf.format(arguments) + "<br/>");
                    auditMsg.append(mf.format(arguments) + "<br/>");
                    // break here with an exception
                    System.out.println("found an error with XML: " + msg.toString());
                    // throw new Exception(msg.toString());
                    // instead of breaking the entire operation, we should
                    // continue looping
                    continue;
                }
            }
            // next: check, then import
            List<String> errors = getImportCRFDataService(dataSource).validateStudyMetadata(odmContainer, studyBean.getId());
            // this needs to be replaced with the study name from the job, since
            // the user could be in any study ...
            if (errors != null) {
                // add to session
                // forward to another page
                System.out.println(errors.toString());
                for (String error : errors) {
                    msg.append(error + "<br/>");
                }
                if (errors.size() > 0) {
                    // fail = true;
                    // forwardPage(Page.IMPORT_CRF_DATA);
                    // break here with an exception
                    // throw new Exception("Your XML in the file " + f.getName()
                    // + " was well formed, but generated metadata errors: " +
                    // errors.toString());
                    // msg.append("Your XML in the file " + f.getName() +
                    // " was well formed, but generated metadata errors: " +
                    // errors.toString());
                    auditMsg.append("Your XML in the file " + f.getName() + " was well formed, but generated " + errors.size() + " metadata errors." + "<br/>");
                    continue;
                } else {
                    msg.append(respage.getString("passed_study_check") + "<br/>");
                    msg.append(respage.getString("passed_oid_metadata_check") + "<br/>");
                    auditMsg.append(respage.getString("passed_study_check") + "<br/>");
                    auditMsg.append(respage.getString("passed_oid_metadata_check") + "<br/>");
                }

            }
            // validation errors, the same as in the ImportCRFDataServlet. DRY?
            List<EventCRFBean> eventCRFBeans = getImportCRFDataService(dataSource).fetchEventCRFBeans(odmContainer, ub);

            ArrayList<Integer> permittedEventCRFIds = new ArrayList<Integer>();
            System.out.println("found a list of eventCRFBeans: " + eventCRFBeans.toString());

            List<DisplayItemBeanWrapper> displayItemBeanWrappers = new ArrayList<DisplayItemBeanWrapper>();
            HashMap<String, String> totalValidationErrors = new HashMap<String, String>();
            HashMap<String, String> hardValidationErrors = new HashMap<String, String>();

            // -- does the event already exist? if not, fail
            if (!eventCRFBeans.isEmpty()) {
                for (EventCRFBean eventCRFBean : eventCRFBeans) {
                    DataEntryStage dataEntryStage = eventCRFBean.getStage();
                    Status eventCRFStatus = eventCRFBean.getStatus();

                    System.out.println("Event CRF Bean: id " + eventCRFBean.getId() + ", data entry stage " + dataEntryStage.getName() + ", status "
                        + eventCRFStatus.getName());
                    if (eventCRFStatus.equals(Status.AVAILABLE) || dataEntryStage.equals(DataEntryStage.INITIAL_DATA_ENTRY)
                        || dataEntryStage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE)
                        || dataEntryStage.equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE) || dataEntryStage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
                        permittedEventCRFIds.add(new Integer(eventCRFBean.getId()));
                    } else {
                        // break out here with an exception

                        // throw new
                        // Exception("Your listed Event CRF in the file " +
                        // f.getName() +
                        // " does not exist, or has already been locked for import."
                        // );
                        msg.append("Your listed Event CRF in the file " + f.getName() + " does not exist, or has already been locked for import." + "<br/>");
                        auditMsg.append("Your listed Event CRF in the file " + f.getName() + " does not exist, or has already been locked for import."
                            + "<br/>");
                        continue;
                    }
                }

                if (eventCRFBeans.size() >= permittedEventCRFIds.size()) {
                    msg.append(respage.getString("passed_event_crf_status_check") + "<br/>");
                    auditMsg.append(respage.getString("passed_event_crf_status_check") + "<br/>");
                } else {
                    fail = true;
                    msg.append(respage.getString("the_event_crf_not_correct_status") + "<br/>");
                    auditMsg.append(respage.getString("the_event_crf_not_correct_status") + "<br/>");
                }

                // create a 'fake' request to generate the validation errors
                // here, tbh 05/2009

                MockHttpServletRequest request = new MockHttpServletRequest();
                Locale locale = new Locale("en-US");
                request.addPreferredLocale(locale);
                try {
                    List<DisplayItemBeanWrapper> tempDisplayItemBeanWrappers = new ArrayList<DisplayItemBeanWrapper>();
                    tempDisplayItemBeanWrappers =
                        getImportCRFDataService(dataSource).lookupValidationErrors(request, odmContainer, ub, totalValidationErrors, hardValidationErrors,
                                permittedEventCRFIds);
                    System.out.println("size of total validation errors: " + totalValidationErrors.size());
                    displayItemBeanWrappers.addAll(tempDisplayItemBeanWrappers);
                } catch (NullPointerException npe1) {
                    // what if you have 2 event crfs but the third is a fake?
                    npe1.printStackTrace();
                    fail = true;
                    logger.debug("threw a NPE after calling lookup validation errors");
                    msg.append(respage.getString("an_error_was_thrown_while_validation_errors") + "<br/>");
                    System.out.println("=== threw the null pointer, import ===");
                } catch (OpenClinicaException oce1) {
                    fail = true;
                    logger.debug("threw an OCE after calling lookup validation errors " + oce1.getOpenClinicaMessage());
                    msg.append(oce1.getOpenClinicaMessage() + "<br/>");
                    System.out.println("=== threw the openclinica message, import ===");
                }
            } else {
                // fail = true;
                // break here with an exception
                msg.append(respage.getString("no_event_crfs_matching_the_xml_metadata") + "<br/>");
                // throw new Exception(msg.toString());
                continue;
            }

            if (fail) {
                // forwardPage(Page.IMPORT_CRF_DATA);
                // break here with an exception
                // throw new Exception("Problems encountered with file " +
                // f.getName() + ": " + msg.toString());
                msg.append("Problems encountered with file " + f.getName() + ": " + msg.toString() + "<br/>");
                continue;
            } else {
                msg.append(respage.getString("passing_crf_edit_checks") + "<br/>");
                auditMsg.append(respage.getString("passing_crf_edit_checks") + "<br/>");
                // session.setAttribute("importedData",
                // displayItemBeanWrappers);
                // session.setAttribute("validationErrors",
                // totalValidationErrors);
                // session.setAttribute("hardValidationErrors",
                // hardValidationErrors);
                // above are to be sent to the user, but what kind of message
                // can we make of them here?

                // if hard validation errors are present, we only generate one
                // table
                // otherwise, we generate the other two: validation errors and
                // valid data
                System.out.println("found total validation errors: " + totalValidationErrors.size());
                SummaryStatsBean ssBean = getImportCRFDataService(dataSource).generateSummaryStatsBean(odmContainer, displayItemBeanWrappers);
                // msg.append("===+");
                // the above is a special key that we will use to split the
                // message into two parts
                // a shorter version for the audit and
                // a longer version for the email
                msg.append(triggerService.generateSummaryStatsMessage(ssBean, respage) + "<br/>");
                // session.setAttribute("summaryStats", ssBean);
                // will have to set hard edit checks here as well
                // session.setAttribute("subjectData",
                ArrayList<SubjectDataBean> subjectData = odmContainer.getCrfDataPostImportContainer().getSubjectData();
                // forwardPage(Page.VERIFY_IMPORT_SERVLET);
                // instead of forwarding, go ahead and save it all, sending a
                // message at the end

                if (!hardValidationErrors.isEmpty()) {
                    msg.append(triggerService.generateHardValidationErrorMessage(subjectData, hardValidationErrors, false));
                } else {
                    if (!totalValidationErrors.isEmpty()) {
                        msg.append(triggerService.generateHardValidationErrorMessage(subjectData, totalValidationErrors, false));
                    }
                    msg.append(triggerService.generateValidMessage(subjectData, totalValidationErrors));
                }

                CrfBusinessLogicHelper crfBusinessLogicHelper = new CrfBusinessLogicHelper(dataSource);
                for (DisplayItemBeanWrapper wrapper : displayItemBeanWrappers) {

                    int eventCrfBeanId = -1;
                    EventCRFBean eventCrfBean = new EventCRFBean();

                    System.out.println("right before we check to make sure it is savable: " + wrapper.isSavable());
                    if (wrapper.isSavable()) {
                        ArrayList<Integer> eventCrfInts = new ArrayList<Integer>();
                        System.out.println("wrapper problems found : " + wrapper.getValidationErrors().toString());
                        for (DisplayItemBean displayItemBean : wrapper.getDisplayItemBeans()) {
                            eventCrfBeanId = displayItemBean.getData().getEventCRFId();
                            eventCrfBean = (EventCRFBean) eventCrfDao.findByPK(eventCrfBeanId);
                            System.out.println("found value here: " + displayItemBean.getData().getValue());
                            System.out.println("found status here: " + eventCrfBean.getStatus().getName());
                            ItemDataBean itemDataBean = new ItemDataBean();
                            itemDataBean =
                                itemDataDao.findByItemIdAndEventCRFIdAndOrdinal(displayItemBean.getItem().getId(), eventCrfBean.getId(), displayItemBean
                                        .getData().getOrdinal());
                            if (wrapper.isOverwrite() && itemDataBean.getStatus() != null) {
                                System.out.println("just tried to find item data bean on item name " + displayItemBean.getItem().getName());
                                itemDataBean.setUpdatedDate(new Date());
                                itemDataBean.setUpdater(ub);
                                itemDataBean.setValue(displayItemBean.getData().getValue());
                                // set status?
                                itemDataDao.update(itemDataBean);
                                System.out.println("updated: " + itemDataBean.getItemId());
                                // need to set pk here in order to create dn
                                displayItemBean.getData().setId(itemDataBean.getId());
                            } else {
                                itemDataDao.create(displayItemBean.getData());
                                System.out.println("created: " + displayItemBean.getData().getItemId());
                                ItemDataBean itemDataBean2 =
                                    itemDataDao.findByItemIdAndEventCRFIdAndOrdinal(displayItemBean.getItem().getId(), eventCrfBean.getId(), displayItemBean
                                            .getData().getOrdinal());
                                System.out.println("found: id " + itemDataBean2.getId() + " name " + itemDataBean2.getName());
                                displayItemBean.getData().setId(itemDataBean2.getId());
                            }
                            ItemDAO idao = new ItemDAO(dataSource);
                            ItemBean ibean = (ItemBean) idao.findByPK(displayItemBean.getData().getItemId());
                            System.out.println("*** checking for validation errors: " + ibean.getName());
                            String itemOid =
                                displayItemBean.getItem().getOid() + "_" + wrapper.getStudyEventRepeatKey() + "_" + displayItemBean.getData().getOrdinal()
                                    + "_" + wrapper.getStudySubjectOid();
                            if (wrapper.getValidationErrors().containsKey(itemOid)) {
                                ArrayList messageList = (ArrayList) wrapper.getValidationErrors().get(itemOid);
                                for (int iter = 0; iter < messageList.size(); iter++) {
                                    String message = (String) messageList.get(iter);

                                    DiscrepancyNoteBean parentDn =
                                        VerifyImportedCRFDataServlet.createDiscrepancyNote(ibean, message, eventCrfBean, displayItemBean, null, ub, dataSource,
                                                studyBean);
                                    VerifyImportedCRFDataServlet.createDiscrepancyNote(ibean, message, eventCrfBean, displayItemBean, parentDn.getId(), ub,
                                            dataSource, studyBean);
                                    System.out.println("*** created disc note with message: " + message);
                                    // displayItemBean);
                                }
                            }
                            if (!eventCrfInts.contains(new Integer(eventCrfBean.getId()))) {
                                crfBusinessLogicHelper.markCRFComplete(eventCrfBean, ub);
                                System.out.println("*** just updated event crf bean: " + eventCrfBean.getId());
                                eventCrfInts.add(new Integer(eventCrfBean.getId()));
                            }
                        }

                    }

                }
                // msg.append("===+");
                msg.append(respage.getString("data_has_been_successfully_import") + "<br/>");
                auditMsg.append(respage.getString("data_has_been_successfully_import") + "<br/>");
                String finalLine =
                    "<p>You can review the entered data <a href='" + SQLInitServlet.getField("sysURL.base") + "ListStudySubjectsSubmit'>here</a>.";
                msg.append(finalLine);
                auditMsg.append(finalLine);
            }
        }// end for loop
        ArrayList<String> retList = new ArrayList<String>();
        retList.add(msg.toString());
        retList.add(auditMsg.toString());
        return retList;// msg.toString();

    }

    private void cutAndPaste(File[] tar, File[] dest) throws IOException {
        for (int j = 0; j < tar.length; j++) {
            // System.out.println("starting to cut and paste " +
            // tar[j].getName());
            try {
                java.io.InputStream in = new FileInputStream(tar[j]);
                java.io.OutputStream out = new FileOutputStream(dest[j]);

                byte[] buf = new byte[1024];
                int len = 0;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                tar[j].delete();
            } catch (NullPointerException npe) {
                // list can be 'gappy' which is why we need to catch this
                System.out.println("found Npe: " + npe.getMessage());
            }
        }

    }

}
