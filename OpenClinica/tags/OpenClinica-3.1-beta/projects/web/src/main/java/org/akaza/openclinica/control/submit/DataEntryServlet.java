/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.NullValue;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.core.Utils;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplayItemGroupBean;
import org.akaza.openclinica.bean.submit.DisplayItemWithGroupBean;
import org.akaza.openclinica.bean.submit.DisplaySectionBean;
import org.akaza.openclinica.bean.submit.DisplayTableOfContentsBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.bean.submit.ResponseOptionBean;
import org.akaza.openclinica.bean.submit.ResponseSetBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.bean.submit.SimpleConditionalDisplayPair;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.DiscrepancyValidator;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.RuleValidator;
import org.akaza.openclinica.control.form.ScoreItemValidator;
import org.akaza.openclinica.control.form.Validation;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.control.managestudy.ViewNotesServlet;
import org.akaza.openclinica.control.managestudy.ViewStudySubjectServlet;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.admin.AuditDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
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
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.action.RuleActionRunBean.Phase;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.logic.expressionTree.ExpressionTreeHelper;
import org.akaza.openclinica.logic.rulerunner.MessageContainer.MessageType;
import org.akaza.openclinica.logic.score.ScoreCalculator;
import org.akaza.openclinica.service.DiscrepancyNoteThread;
import org.akaza.openclinica.service.DiscrepancyNoteUtil;
import org.akaza.openclinica.service.crfdata.DynamicsMetadataService;
import org.akaza.openclinica.service.managestudy.DiscrepancyNoteService;
import org.akaza.openclinica.service.rule.RuleSetServiceInterface;
import org.akaza.openclinica.service.rule.expression.ExpressionService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.form.FormBeanUtil;
import org.akaza.openclinica.web.InconsistentStateException;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * @author ssachs
 *
 *         Enabled scoring feature - ywang (Jan. 2008)
 *
 */

public abstract class DataEntryServlet extends SecureController {

    Locale locale;
    // < ResourceBundleresmessage,restext,resexception,respage;

    // these inputs come from the form, from another JSP via POST,
    // or from another JSP via GET
    // e.g. InitialDataEntry?eventCRFId=123&sectionId=234
    public static final String INPUT_EVENT_CRF_ID = "eventCRFId";

    public static final String INPUT_SECTION_ID = "sectionId";

    // these inputs are used when other servlets redirect you here
    // this is most typically the case when the user enters data and clicks the
    // "Previous" or "Next" button
    public static final String INPUT_EVENT_CRF = "event";

    public static final String INPUT_SECTION = "section";

    /**
     * A bean used to indicate that servlets to which this servlet forwards should ignore any parameters, in particular the "submitted" parameter which controls
     * FormProcessor.isSubmitted. If an attribute with this name is set in the request, the servlet to which this servlet forwards should consider
     * fp.isSubmitted to always return false.
     */
    public static final String INPUT_IGNORE_PARAMETERS = "ignore";

    /**
     * A bean used to indicate that we are not validating inputs, that is, that the user is "confirming" values which did not validate properly the first time.
     * If an attribute with this name is set in the request, this servlet should not perform any validation on the form inputs.
     */
    public static final String INPUT_CHECK_INPUTS = "checkInputs";

    /**
     * The name of the form input on which users write annotations.
     */
    public static final String INPUT_ANNOTATIONS = "annotations";

    /**
     * The name of the attribute in the request which hold the preset annotations form value.
     */
    public static final String BEAN_ANNOTATIONS = "annotations";

    // names of submit buttons in the JSP
    public static final String RESUME_LATER = "submittedResume";

    public static final String GO_PREVIOUS = "submittedPrev";

    public static final String GO_NEXT = "submittedNext";

    public static final String BEAN_DISPLAY = "section";

    public static final String TOC_DISPLAY = "toc"; // from
    // TableOfContentServlet

    // these inputs are displayed on the table of contents and
    // are used to edit Event CRF properties
    public static final String INPUT_INTERVIEWER = "interviewer";

    public static final String INPUT_INTERVIEW_DATE = "interviewDate";

    public static final String INTERVIEWER_NAME_NOTE = "InterviewerNameNote";

    public static final String INTERVIEWER_DATE_NOTE = "InterviewerDateNote";

    public static final String INPUT_TAB = "tabId";

    public static final String INPUT_MARK_COMPLETE = "markComplete";

    public static final String VALUE_YES = "Yes";

    // these are only for use with ACTION_START_INITIAL_DATA_ENTRY
    public static final String INPUT_EVENT_DEFINITION_CRF_ID = "eventDefinitionCRFId";

    public static final String INPUT_CRF_VERSION_ID = "crfVersionId";

    public static final String INPUT_STUDY_EVENT_ID = "studyEventId";

    public static final String INPUT_SUBJECT_ID = "subjectId";

    public static final String GO_EXIT = "submittedExit";

    public static final String GROUP_HAS_DATA = "groupHasData";
    public static final String HAS_DATA_FLAG = "hasDataFlag";
    // See the session variable in DoubleDataEntryServlet
    public static final String DDE_PROGESS = "doubleDataProgress";

    public static final String INTERVIEWER_NAME = "interviewer_name";

    public static final String DATE_INTERVIEWED = "date_interviewed";

    public static final String NOTE_SUBMITTED = "note_submitted";

    protected String SCOREITEMS;
    protected String SCOREITEMDATA;

    protected FormProcessor fp;
    // the input beans
    protected EventCRFBean ecb;

    protected SectionBean sb;

    protected ArrayList<SectionBean> allSectionBeans;

    /**
     * The event definition CRF bean which governs the event CRF bean into which we are entering data. Notice: It should be updated by info of a
     * siteEventDefinitionCRF if dataEntry is for a site which has its own study_event_definition,
     */
    protected EventDefinitionCRFBean edcb;

    // DAOs used throughout the c;ass
    protected EventCRFDAO ecdao;

    protected EventDefinitionCRFDAO edcdao;

    protected SectionDAO sdao;

    protected ItemDAO idao;

    protected ItemFormMetadataDAO ifmdao;

    protected ItemDataDAO iddao;

    protected DiscrepancyNoteDAO dndao;

    protected RuleSetServiceInterface ruleSetService;
    protected ExpressionService expressionService;
    protected DiscrepancyNoteService discrepancyNoteService;
    DynamicsMetadataService itemMetadataService;

    /**
     * Determines whether the form was submitted. Calculated once in processRequest. The reason we don't use the normal means to determine if the form was
     * submitted (ie FormProcessor.isSubmitted) is because when we use forwardPage, Java confuses the inputs from the just-processed form with the inputs for
     * the forwarded-to page. This is a problem since frequently we're forwarding from one (submitted) section to the next (unsubmitted) section. If we use the
     * normal means, Java will always think that the unsubmitted section is, in fact, submitted. This member is guaranteed to be calculated before
     * shouldLoadDBValues() is called.
     */
    protected boolean isSubmitted = false;

    protected boolean hasGroup = false;

    /*
     * (non-Javadoc)
     * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
     */
    @Override
    protected abstract void mayProceed() throws InsufficientPermissionException;

    /*
     * locale = request.getLocale(); //< resmessage = ResourceBundle.getBundle("org.akaza.openclinica.i18n.page_messages" ,locale); //< restext =
     * ResourceBundle.getBundle("org.akaza.openclinica.i18n.notes",locale); //< resexception =ResourceBundle.getBundle("org.akaza.openclinica.i18n.exceptions"
     * ,locale);
     */

    /*
     * (non-Javadoc)
     * @see org.akaza.openclinica.control.core.SecureController#processRequest()
     */

    private String getSectionFirstFieldId(int sectionId) {

        ItemDAO itemDAO = new ItemDAO(sm.getDataSource());
        List<ItemBean> items = itemDAO.findAllBySectionId(sectionId);
        if (!items.isEmpty()) {
            return new Integer(items.get(0).getId()).toString();
        }
        return "";
    }


    @Override
    protected  void processRequest() throws Exception {

        locale = request.getLocale();

        FormDiscrepancyNotes discNotes;

        panel.setStudyInfoShown(false);
        String age = "";
        if (fp.getString(GO_EXIT).equals("") && !isSubmitted && fp.getString("tabId").equals("") && fp.getString("sectionId").equals("")) {
            if (getUnavailableCRFList().containsKey(ecb.getId())) {
                int userId = (Integer) getUnavailableCRFList().get(ecb.getId());
                UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
                UserAccountBean ubean = (UserAccountBean) udao.findByPK(userId);
                addPageMessage(resword.getString("CRF_unavailable") + " " + ubean.getName() + " " + resword.getString("Currently_entering_data") + " "
                    + resword.getString("Leave_the_CRF"));

                forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET);
            } else {
                lockThisEventCRF(ecb.getId(), ub.getId());
            }
        }

        if (!ecb.isActive()) {
            throw new InconsistentStateException(Page.LIST_STUDY_SUBJECTS_SERVLET, resexception.getString("event_not_exists"));
        }

        // Get the status/number of item discrepancy notes
        DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());
        ArrayList<DiscrepancyNoteBean> allNotes = new ArrayList<DiscrepancyNoteBean>();
        List<DiscrepancyNoteBean> eventCrfNotes = new ArrayList<DiscrepancyNoteBean>();
        List<DiscrepancyNoteThread> noteThreads = new ArrayList<DiscrepancyNoteThread>();
        // BWP: this try block is not necessary try {
        dndao = new DiscrepancyNoteDAO(sm.getDataSource());
        allNotes = dndao.findAllTopNotesByEventCRF(ecb.getId());
        eventCrfNotes = dndao.findOnlyParentEventCRFDNotesFromEventCRF(ecb);
        if (!eventCrfNotes.isEmpty()) {
            allNotes.addAll(eventCrfNotes);

        }
        // Create disc note threads out of the various notes
        DiscrepancyNoteUtil dNoteUtil = new DiscrepancyNoteUtil();
        noteThreads = dNoteUtil.createThreadsOfParents(allNotes, sm.getDataSource(), currentStudy, null, -1, true);
        // variables that provide values for the CRF discrepancy note header
        int updatedNum = 0;
        int openNum = 0;
        int closedNum = 0;
        int resolvedNum = 0;
        int notAppNum = 0;
        DiscrepancyNoteBean tempBean;
        for (DiscrepancyNoteThread dnThread : noteThreads) {
            /*
             * 3014: do not count parent beans, only the last child disc note of the thread.
             */
            tempBean = dnThread.getLinkedNoteList().getLast();
            if (tempBean != null) {
                if (ResolutionStatus.UPDATED.equals(tempBean.getResStatus())) {
                    updatedNum++;
                } else if (ResolutionStatus.OPEN.equals(tempBean.getResStatus())) {
                    openNum++;
                } else if (ResolutionStatus.CLOSED.equals(tempBean.getResStatus())) {
                    // if (dn.getParentDnId() > 0){
                    closedNum++;
                    // }
                } else if (ResolutionStatus.RESOLVED.equals(tempBean.getResStatus())) {
                    // if (dn.getParentDnId() > 0){
                    resolvedNum++;
                    // }
                } else if (ResolutionStatus.NOT_APPLICABLE.equals(tempBean.getResStatus())) {
                    notAppNum++;
                }
            }

        }
        request.setAttribute("updatedNum", updatedNum + "");
        request.setAttribute("openNum", openNum + "");
        request.setAttribute("closedNum", closedNum + "");
        request.setAttribute("resolvedNum", resolvedNum + "");
        request.setAttribute("notAppNum", notAppNum + "");

        String fromViewNotes = fp.getString("fromViewNotes");
        if(fromViewNotes!=null && "1".equals(fromViewNotes)) {
            request.setAttribute("fromViewNotes", fromViewNotes);
        }

        // Not necessary: } catch (NullPointerException npe) {
        // temp fix, jun please address the above problems
        // try to check if dn and dn. geResStatus are not null-jxu
        // npe.printStackTrace();
        /*
         * request.setAttribute("updatedNum", "0"); request.setAttribute("openNum", "0"); request.setAttribute("closedNum", "0");
         * request.setAttribute("resolvedNum", "0"); request.setAttribute("notAppNum", "0");
         */
        // }
        StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
        StudySubjectBean ssb = (StudySubjectBean) ssdao.findByPK(ecb.getStudySubjectId());
        // YW 11-07-2007, data entry could not be performed if its study subject
        // has been removed.
        // Notice: ViewSectionDataEntryServelet, ViewSectionDataEntryPreview,
        // PrintCRFServlet and PrintDataEntryServlet, have theirs own
        // processRequest
        Status s = ssb.getStatus();
        if ("removed".equalsIgnoreCase(s.getName()) || "auto-removed".equalsIgnoreCase(s.getName())) {
            addPageMessage(respage.getString("you_may_not_perform_data_entry_on_a_CRF") + respage.getString("study_subject_has_been_deleted"));
            request.setAttribute("id", new Integer(ecb.getStudySubjectId()).toString());
            forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
        }
        // YW >>

        HashMap<String, String> newUploadedFiles = (HashMap<String, String>) session.getAttribute("newUploadedFiles");
        if (newUploadedFiles == null) {
            newUploadedFiles = new HashMap<String, String>();
        }
        request.setAttribute("newUploadedFiles", newUploadedFiles);
        if (!fp.getString("exitTo").equals("")) {
            request.setAttribute("exitTo", fp.getString("exitTo"));
        }
        if (!fp.getString(GO_EXIT).equals("")) {
            session.removeAttribute(GROUP_HAS_DATA);
            session.removeAttribute("to_create_crf");
            session.removeAttribute("mayProcessUploading");
            //Removing the user and EventCRF from the locked CRF List
            getUnavailableCRFList().remove(ecb.getId());
            if (newUploadedFiles.size() > 0) {
                if (this.unloadFiles(newUploadedFiles)) {

                } else {
                    String missed = "";
                    Iterator iter = newUploadedFiles.keySet().iterator();
                    while (iter.hasNext()) {
                        missed += " " + newUploadedFiles.get(iter.next());
                    }
                    addPageMessage(respage.getString("uploaded_files_not_deleted_or_not_exist") + ": " + missed);
                }
            }
            session.removeAttribute("newUploadedFiles");
            addPageMessage(respage.getString("exit_without_saving"));
            // addPageMessage("You chose to exit the data entry page.");
            // changed bu jxu 03/06/2007- we should use redirection to go to
            // another servlet
            if(fromViewNotes!=null && "1".equals(fromViewNotes)) {
                String viewNotesURL = (String)session.getAttribute("viewNotesURL");
                if(viewNotesURL!=null && viewNotesURL.length()>0) {
                    response.sendRedirect(response.encodeRedirectURL(viewNotesURL));
                }
                return;
            }
            String fromResolvingNotes = fp.getString("fromResolvingNotes", true);
            String winLocation = (String) session.getAttribute(ViewNotesServlet.WIN_LOCATION);
            if (!StringUtil.isBlank(fromResolvingNotes) && !StringUtil.isBlank(winLocation)) {
                response.sendRedirect(response.encodeRedirectURL(winLocation));
            } else {
                if (!fp.getString("exitTo").equals("")) {
                    response.sendRedirect(response.encodeRedirectURL(fp.getString("exitTo")));
                } else
                    response.sendRedirect(response.encodeRedirectURL("ListStudySubjects"));
            }
            // forwardPage(Page.SUBMIT_DATA_SERVLET);
            return;
        }

        // checks if the section has items in item group
        // for repeating items
        // hasGroup = getInputBeans();
        hasGroup = checkGroups();

        Boolean b = (Boolean) request.getAttribute(INPUT_IGNORE_PARAMETERS);
        isSubmitted = fp.isSubmitted() && b == null;
        // variable is used for fetching any null values like "not applicable"
        int eventDefinitionCRFId = 0;
        if (fp != null) {
            eventDefinitionCRFId = fp.getInt("eventDefinitionCRFId");
        }

        StudyBean study = (StudyBean) session.getAttribute("study");
        // constructs the list of items used on UI
        // tbh>>
        // logger.trace("trying event def crf id: "+eventDefinitionCRFId);
        edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
        if (eventDefinitionCRFId <= 0) {
            // TODO we have to get that id before we can continue
            EventDefinitionCRFBean edcBean = edcdao.findByStudyEventIdAndCRFVersionId(study, ecb.getStudyEventId(), ecb.getCRFVersionId());
            eventDefinitionCRFId = edcBean.getId();
        }

        StudyEventDAO seDao = new StudyEventDAO(sm.getDataSource());
        EventDefinitionCRFBean edcBean = (EventDefinitionCRFBean) edcdao.findByPK(eventDefinitionCRFId);
        edcb = (EventDefinitionCRFBean) edcdao.findByPK(eventDefinitionCRFId);
        StudyEventBean studyEventBean = (StudyEventBean) seDao.findByPK(ecb.getStudyEventId());
        edcBean.setId(eventDefinitionCRFId);

        StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
        StudyEventDefinitionBean studyEventDefinition = (StudyEventDefinitionBean) seddao.findByPK(edcBean.getStudyEventDefinitionId());

        CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
        CRFVersionBean crfVersionBean = (CRFVersionBean) cvdao.findByPK(ecb.getCRFVersionId());

        List<RuleSetBean> ruleSets = createAndInitializeRuleSet(currentStudy, studyEventDefinition, crfVersionBean, studyEventBean, ecb, shouldRunRules());
        DisplaySectionBean section = getDisplayBean(hasGroup, false);

        // 2790: Find out the id of the section's first field
        String firstFieldId = getSectionFirstFieldId(section.getSection().getId());
        request.setAttribute("formFirstField", firstFieldId);

        // logger.trace("now trying event def crf id: "+eventDefinitionCRFId);
        // above is necessary to give us null values during DDE
        // ironically, this only covers vertical null value result sets
        // horizontal ones are covered in FormBeanUtil, tbh 112007
        List<DisplayItemWithGroupBean> displayItemWithGroups = createItemWithGroups(section, hasGroup, eventDefinitionCRFId);

        section.setDisplayItemGroups(displayItemWithGroups);

        // why do we get previousSec and nextSec here, rather than in
        // getDisplayBeans?
        // so that we can use them in forwarding the user to the previous/next
        // section
        // if the validation was successful
        SectionBean previousSec = sdao.findPrevious(ecb, sb);
        SectionBean nextSec = sdao.findNext(ecb, sb);
        section.setFirstSection(!previousSec.isActive());
        section.setLastSection(!nextSec.isActive());

        // this is for generating side info panel
        // and the information panel under the Title
        SubjectDAO subjectDao = new SubjectDAO(sm.getDataSource());
        StudyDAO studydao = new StudyDAO(sm.getDataSource());
        SubjectBean subject = (SubjectBean) subjectDao.findByPK(ssb.getSubjectId());

        // Get the study then the parent study

        if (study.getParentStudyId() > 0) {
            // this is a site,find parent
            StudyBean parentStudy = (StudyBean) studydao.findByPK(study.getParentStudyId());
            request.setAttribute("studyTitle", study.getName());
            request.setAttribute("siteTitle",parentStudy.getName() );
        } else {
            request.setAttribute("studyTitle", study.getName());
        }

        // Let us process the age
        if (currentStudy.getStudyParameterConfig().getCollectDob().equals("1")) {
            // YW 11-16-2007 erollment-date is used for calculating age.
            Date enrollmentDate = ssb.getEnrollmentDate();
            age = Utils.getInstacne().processAge(enrollmentDate, subject.getDateOfBirth());
        }
        ArrayList beans = ViewStudySubjectServlet.getDisplayStudyEventsForStudySubject(ssb, sm.getDataSource(), ub, currentRole);
        request.setAttribute("studySubject", ssb);
        request.setAttribute("subject", subject);
        request.setAttribute("beans", beans);
        request.setAttribute("eventCRF", ecb);
        request.setAttribute("age", age);
        request.setAttribute("decryptedPassword", ((SecurityManager) SpringServletAccess.getApplicationContext(context).getBean("securityManager"))
                .encrytPassword("root", getUserDetails()));

        // set up interviewer name and date
        fp.addPresetValue(INPUT_INTERVIEWER, ecb.getInterviewerName());

        if (ecb.getDateInterviewed() != null) {
            // SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            String idateFormatted = local_df.format(ecb.getDateInterviewed());
            fp.addPresetValue(INPUT_INTERVIEW_DATE, idateFormatted);
        } else {
            fp.addPresetValue(INPUT_INTERVIEW_DATE, "");
        }
        setPresetValues(fp.getPresetValues());

        if (!isSubmitted) {
            // TODO: prevent data enterer from seeing results of first round of
            // data
            // entry, if this is second round
            request.setAttribute(BEAN_DISPLAY, section);
            request.setAttribute(BEAN_ANNOTATIONS, getEventCRFAnnotations());
            session.setAttribute("shouldRunValidation", null);
            session.setAttribute("rulesErrors", null);
            session.setAttribute(DataEntryServlet.NOTE_SUBMITTED, null);

            discNotes = new FormDiscrepancyNotes();
            //            discNotes = (FormDiscrepancyNotes) session.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
            //            if (discNotes == null) {
            //                discNotes = new FormDiscrepancyNotes();
            //            }
            // << tbh 01/2010
            section = populateNotesWithDBNoteCounts(discNotes, section);
            logger.debug("+++ just ran populateNotes, printing field notes: " + discNotes.getFieldNotes().toString());
            logger.debug("found disc notes: " + discNotes.getNumExistingFieldNotes().toString());
            session.setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);

            int keyId = ecb.getId();
            session.removeAttribute(DoubleDataEntryServlet.COUNT_VALIDATE + keyId);

            setUpPanel(section);
            if (newUploadedFiles.size() > 0) {
                if (this.unloadFiles(newUploadedFiles)) {

                } else {
                    String missed = "";
                    Iterator iter = newUploadedFiles.keySet().iterator();
                    while (iter.hasNext()) {
                        missed += " " + newUploadedFiles.get(iter.next());
                    }
                    addPageMessage(respage.getString("uploaded_files_not_deleted_or_not_exist") + ": " + missed);
                }
            }
            forwardPage(getJSPPage());
        } else {
            //
            // VALIDATION / LOADING DATA
            //
            // If validation is required for this round, we will go through
            // each item and add an appropriate validation to the Validator
            //
            // Otherwise, we will just load the data into the DisplayItemBean
            // so that we can write to the database later.
            //
            // Validation is required if two conditions are met:
            // 1. The user clicked a "Save" button, not a "Confirm" button
            // 2. In this type of data entry servlet, when the user clicks
            // a Save button, the inputs are validated
            //

            boolean validate = fp.getBoolean(INPUT_CHECK_INPUTS) && validateInputOnFirstRound();
            // did the user click a "Save" button?
            // is validation required in this type of servlet when the user
            // clicks
            // "Save"?
            // We can conclude that the user is trying to save data; therefore,
            // set a request
            // attribute indicating that default values for items shouldn't be
            // displayed
            // in the application UI that will subsequently be displayed
            // TODO: find a better, less random place for this
            // session.setAttribute(HAS_DATA_FLAG, true);

            // section.setCheckInputs(fp.getBoolean(INPUT_CHECK_INPUTS));
            errors = new HashMap();
            // ArrayList items = section.getItems();

            discNotes = (FormDiscrepancyNotes) session.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
            if (discNotes == null) {
                discNotes = new FormDiscrepancyNotes();
            }

            // populateNotesWithDBNoteCounts(discNotes, section);

            // all items- inlcude items in item groups and other single items
            List<DisplayItemWithGroupBean> allItems = section.getDisplayItemGroups();
            String attachedFilePath = Utils.getAttachedFilePath(currentStudy);

            DiscrepancyValidator v = new DiscrepancyValidator(request, discNotes);
            RuleValidator ruleValidator = new RuleValidator(request);
            logger.debug("SZE 1  :: " + allItems.size());
            for (int i = 0; i < allItems.size(); i++) {
                logger.trace("===itering through items: " + i);
                DisplayItemWithGroupBean diwg = allItems.get(i);
                if (diwg.isInGroup()) {
                    // for the items in groups
                    DisplayItemGroupBean dgb = diwg.getItemGroup();
                    List<DisplayItemGroupBean> dbGroups = diwg.getDbItemGroups();
                    //dbGroups = diwg.getItemGroups();
                    List<DisplayItemGroupBean> formGroups = new ArrayList<DisplayItemGroupBean>();
                    // List<DisplayItemGroupBean> dbGroups2 = loadFormValueForItemGroup(dgb, dbGroups, formGroups, eventDefinitionCRFId);
                    // tbh 01/2010 change here to collect manual row counts
                    logger.info("got db item group size " + dbGroups.size());

                    if (validate) {
                        // int manualGroups = getManualRows(dbGroups2);
                        // logger.info("+++ found manual rows from db group 2: " + manualGroups);
                        logger.debug("===IF VALIDATE NOT A SINGLE ITEM: got to this part in the validation loop: " + dgb.getGroupMetaBean().getName());
                        // TODO next marker tbh 112007
                        // formGroups = validateDisplayItemGroupBean(v,
                        // dgb,dbGroups, formGroups,
                        // ruleValidator,groupOrdinalPLusItemOid);
                        formGroups = validateDisplayItemGroupBean(v, dgb, dbGroups, formGroups);
                        logger.debug("form group size after validation " + formGroups.size());
                    } else {
                        logger.debug("+++ELSE NOT A SINGLE ITEM: got to this part in the validation loop: " + dgb.getGroupMetaBean().getName());
                        formGroups = loadFormValueForItemGroup(dgb, dbGroups, formGroups, eventDefinitionCRFId);
                        logger.debug("form group size without validation " + formGroups.size());
                    }

                    diwg.setItemGroup(dgb);
                    diwg.setItemGroups(formGroups);

                    allItems.set(i, diwg);

                } else {// other single items
                    DisplayItemBean dib = diwg.getSingleItem();
                    // dib = (DisplayItemBean) allItems.get(i);
                    boolean hasSCDItemInSection = section.getSection().hasSCDItem();
                    Set<Integer> showSCDItemIds = section.getShowSCDItemIds();
                    if (validate) {
                        // generate input name here?
                        // DisplayItemGroupBean dgb = diwg.getItemGroup();
                        String itemName = getInputName(dib);
                        // no Item group for single item, so just use blank
                        // string as parameter for inputName

                        dib = validateDisplayItemBean(v, dib, "");// this
                        // should be
                        // used,
                        // otherwise,
                        // DDE not
                        // working-jxu

                        if(section.getSection().hasSCDItem()) {
                            if(dib.getSCDPairs().size()>0) {
                                //for control item
                                //dib has to loadFormValue first. Here loadFormValue has been done in validateDisplayItemBean(v, dib, "")
                                section.setShowSCDItemIds(this.conditionalDisplayToBeShown(dib, section.getShowSCDItemIds()));
                            } else if(dib.getMetadata().isConditionalDisplayItem()) {
                                //for scd item
                                //a control item is always before its scd item
                                dib.setIsSCDtoBeShown(section.getShowSCDItemIds().contains(dib.getMetadata().getItemId()));
                                if(!dib.getIsSCDtoBeShown()) {
                                    dib.setIsSCDtoBeShown(this.shouldSCDBeShown(dib));
                                }
                                validateSCDItemBean(v, dib);
                            }
                        }

                        logger.debug("&&& found name: " + itemName);
                        logger.debug("input VALIDATE " + itemName + ": " + fp.getString(itemName));
                        // dib.loadFormValue(fp.getString(itemName));
                        logger.debug("input " + itemName + " has a response set of " + dib.getMetadata().getResponseSet().getOptions().size() + " options");
                    } else {
                        String itemName = getInputName(dib);
                        logger.debug("input NONVALIDATE " + itemName + ": " + fp.getString(itemName));
                        // dib.loadFormValue(itemName);
                        dib = loadFormValue(dib);
                        // String itemName = getInputName(dib);
                        // dib = loadFormValue(itemName);
                    }

                    ArrayList children = dib.getChildren();
                    for (int j = 0; j < children.size(); j++) {
                        DisplayItemBean child = (DisplayItemBean) children.get(j);
                        // DisplayItemGroupBean dgb = diwg.getItemGroup();
                        String itemName = getInputName(child);
                        child.loadFormValue(fp.getString(itemName));
                        if (validate) {
                            // child = validateDisplayItemBean(v, child,
                            // itemName, ruleValidator, groupOrdinalPLusItemOid,
                            // false);
                            child = validateDisplayItemBean(v, child, itemName);
                            // was null changed value 092007 tbh

                            if(section.getSection().hasSCDItem()) {
                                if(child.getSCDPairs().size()>0) {
                                    //for control item
                                    //dib has to loadFormValue first. Here loadFormValue has been done in validateDisplayItemBean(v, dib, "")
                                    section.setShowSCDItemIds(this.conditionalDisplayToBeShown(child, section.getShowSCDItemIds()));
                                } else if(child.getMetadata().isConditionalDisplayItem()) {
                                    //for scd item
                                    //a control item is always before its scd item
                                    child.setIsSCDtoBeShown(section.getShowSCDItemIds().contains(child.getMetadata().getItemId()));
                                    if(!child.getIsSCDtoBeShown()) {
                                        child.setIsSCDtoBeShown(this.shouldSCDBeShown(child));
                                    }
                                    validateSCDItemBean(v, child);
                                }
                            }

                        } else {
                            // child.loadFormValue(itemName);
                            child = loadFormValue(child);
                        }
                        logger.debug("Checking child value for " + itemName + ": " + child.getData().getValue());
                        children.set(j, child);
                    }

                    dib.setChildren(children);
                    diwg.setSingleItem(runDynamicsItemCheck(dib));
                    // logger.trace("just set single item on line 447:
                    // "+dib.getData().getValue());
                    // items.set(i, dib);
                    logger.debug(" I : " + i);
                    allItems.set(i, diwg);

                }
            }
            Phase phase2 = Phase.INITIAL_DATA_ENTRY;
            if (getServletPage().equals(Page.DOUBLE_DATA_ENTRY_SERVLET)) {
                phase2 = Phase.DOUBLE_DATA_ENTRY;
            } else if (getServletPage().equals(Page.ADMIN_EDIT_SERVLET)) {
                phase2 = Phase.ADMIN_EDITING;
            }
            // this.getItemMetadataService().resetItemCounter();
            HashMap<String, ArrayList<String>> groupOrdinalPLusItemOid = runRules(allItems, ruleSets, true, shouldRunRules(), MessageType.ERROR, phase2,ecb);
            System.out.println("first run of rules : " + groupOrdinalPLusItemOid.toString());
            for (int i = 0; i < allItems.size(); i++) {
                DisplayItemWithGroupBean diwg = allItems.get(i);
                if (diwg.isInGroup()) {
                    // for the items in groups
                    DisplayItemGroupBean dgb = diwg.getItemGroup();
                    List<DisplayItemGroupBean> dbGroups = diwg.getDbItemGroups();
                    //dbGroups = diwg.getItemGroups();
                    // tbh 01/2010 change the group list?
                    List<DisplayItemGroupBean> formGroups = new ArrayList<DisplayItemGroupBean>();
                    // List<DisplayItemGroupBean> dbGroups2 = loadFormValueForItemGroup(dgb, dbGroups, formGroups, eventDefinitionCRFId);
                    // jxu- this part need to be refined, why need to validate
                    // items again?
                    if (validate) {
                        // int manualGroups = getManualRows(dbGroups2);
                        // logger.info("+++ found manual rows for db group2: " + manualGroups);
                        formGroups = validateDisplayItemGroupBean(v, dgb, dbGroups, formGroups, ruleValidator, groupOrdinalPLusItemOid);
                        // formGroups = validateDisplayItemGroupBean(v, dgb,
                        // dbGroups, formGroups);
                        logger.debug("*** form group size after validation " + formGroups.size());
                    }
                    diwg.setItemGroup(dgb);
                    diwg.setItemGroups(formGroups);

                    allItems.set(i, diwg);

                } else {// other single items
                    DisplayItemBean dib = diwg.getSingleItem();
                    // dib = (DisplayItemBean) allItems.get(i);
                    if (validate) {
                        String itemName = getInputName(dib);
                        dib = validateDisplayItemBean(v, dib, "", ruleValidator, groupOrdinalPLusItemOid, false, null);//
                        // / dib = validateDisplayItemBean(v, dib, "");// this
                    }
                    ArrayList children = dib.getChildren();
                    for (int j = 0; j < children.size(); j++) {

                        DisplayItemBean child = (DisplayItemBean) children.get(j);
                        // DisplayItemGroupBean dgb = diwg.getItemGroup();
                        String itemName = getInputName(child);
                        child.loadFormValue(fp.getString(itemName));
                        if (validate) {
                            child = validateDisplayItemBean(v, child, "", ruleValidator, groupOrdinalPLusItemOid, false, null);
                            // child = validateDisplayItemBean(v, child,
                            // itemName);
                        }
                        children.set(j, child);
                        logger.debug(" J (children): " + j);
                    }

                    dib.setChildren(children);
                    diwg.setSingleItem(runDynamicsItemCheck(dib));
                    logger.debug(" I : " + i);
                    allItems.set(i, diwg);
                }
            }

            // YW, 2-1-2008 <<
            // A map from item name to item bean object.
            HashMap<String, ItemBean> scoreItems = new HashMap<String, ItemBean>();
            HashMap<String, String> scoreItemdata = new HashMap<String, String>();
            HashMap<Integer, String> oldItemdata = prepareSectionItemdata(sb.getId());
            // hold all item names of changed ItemBean in current section
            TreeSet<String> changedItems = new TreeSet<String>();
            // holds complete disply item beans for checking against 'request
            // for change' restriction
            ArrayList<DisplayItemBean> changedItemsList = new ArrayList<DisplayItemBean>();
            // key is repeating item name, value is its display item group bean
            HashMap<String, DisplayItemGroupBean> changedItemsMap = new HashMap<String, DisplayItemGroupBean>();
            // key is itemid, value is set of itemdata-ordinal
            HashMap<Integer, TreeSet<Integer>> itemOrdinals = prepareItemdataOrdinals();

            // prepare item data for scoring
            updateDataOrdinals(allItems);
            section.setDisplayItemGroups(allItems);
            scoreItems = prepareScoreItems();
            scoreItemdata = prepareScoreItemdata();
            for (int i = 0; i < allItems.size(); i++) {
                DisplayItemWithGroupBean diwb = allItems.get(i);
                if (diwb.isInGroup()) {
                    List<DisplayItemGroupBean> dbGroups = diwb.getDbItemGroups();
                    for (int j = 0; j < dbGroups.size(); j++) {
                        DisplayItemGroupBean displayGroup = dbGroups.get(j);
                        List<DisplayItemBean> items = displayGroup.getItems();
                        if ("remove".equalsIgnoreCase(displayGroup.getEditFlag())) {
                            for (DisplayItemBean displayItem : items) {
                                int itemId = displayItem.getItem().getId();
                                int ordinal = displayItem.getData().getOrdinal();
                                if (itemOrdinals.containsKey(itemId)) {
                                    itemOrdinals.get(itemId).remove(ordinal);
                                }
                                if (scoreItemdata.containsKey(itemId + "_" + ordinal)) {
                                    scoreItemdata.remove(itemId + "_" + ordinal);
                                }
                                changedItems.add(displayItem.getItem().getName());
                                changedItemsList.add(displayItem);

                                String formName = displayItem.getItem().getName();
                                // logger.debug("SET: formName:" + formName);
                                if (displayGroup.isAuto()) {
                                    formName = getGroupItemInputName(displayGroup, displayGroup.getFormInputOrdinal(), displayItem);

                                    logger.debug("GET: changed formName to " + formName);

                                } else {
                                    formName = getGroupItemManualInputName(displayGroup, displayGroup.getFormInputOrdinal(), displayItem);
                                    logger.debug("GET-MANUAL: changed formName to " + formName);
                                }
                                changedItemsMap.put(formName, displayGroup);
                                logger.debug("adding to changed items map: " + formName);
                            }
                        }
                    }

                    List<DisplayItemGroupBean> dgbs = diwb.getItemGroups();
                    int groupsize = dgbs.size();
                    HashMap<Integer, Integer> maxOrdinals = new HashMap<Integer, Integer>();
                    boolean first = true;
                    for (int j = 0; j < dgbs.size(); j++) {
                        DisplayItemGroupBean displayGroup = dgbs.get(j);
                        List<DisplayItemBean> items = displayGroup.getItems();
                        boolean isAdd = "add".equalsIgnoreCase(displayGroup.getEditFlag()) ? true : false;
                        for (DisplayItemBean displayItem : items) {
                            ItemBean ib = displayItem.getItem();
                            String itemName = ib.getName();
                            int itemId = ib.getId();
                            if (first) {
                                maxOrdinals.put(itemId, iddao.getMaxOrdinalForGroup(ecb, sb, displayGroup.getItemGroupBean()));
                            }
                            ItemDataBean idb = displayItem.getData();
                            String value = idb.getValue();
                            scoreItems.put(itemName, ib);
                            int ordinal = displayItem.getData().getOrdinal();
                            if (isAdd && scoreItemdata.containsKey(itemId + "_" + ordinal)) {
                                int formMax = 1;
                                if (maxOrdinals.containsKey(itemId)) {
                                    formMax = maxOrdinals.get(itemId);
                                }
                                int dbMax = iddao.getMaxOrdinalForGroup(ecb, sb, displayGroup.getItemGroupBean());
                                ordinal = ordinal >= dbMax ? formMax + 1 : ordinal;
                                maxOrdinals.put(itemId, ordinal);
                                displayItem.getData().setOrdinal(ordinal);
                                scoreItemdata.put(itemId + "_" + ordinal, value);
                            } else {
                                scoreItemdata.put(itemId + "_" + ordinal, value);
                            }
                            if (itemOrdinals.containsKey(itemId)) {
                                itemOrdinals.get(itemId).add(ordinal);
                            } else {
                                TreeSet<Integer> ordinalSet = new TreeSet<Integer>();
                                ordinalSet.add(ordinal);
                                itemOrdinals.put(itemId, ordinalSet);
                            }
                            if (isChanged(displayItem, oldItemdata, attachedFilePath)) {
                                changedItems.add(itemName);
                                changedItemsList.add(displayItem);
                                String formName = displayItem.getItem().getName();
                                // logger.debug("SET: formName:" + formName);
                                if (displayGroup.isAuto()) {
                                    formName = getGroupItemInputName(displayGroup, displayGroup.getFormInputOrdinal(), displayItem);
                                    logger.debug("RESET: formName group-item-input:" + formName);

                                } else {
                                    formName = getGroupItemManualInputName(displayGroup, displayGroup.getFormInputOrdinal(), displayItem);
                                    logger.debug("RESET: formName group-item-input-manual:" + formName);
                                }
                                changedItemsMap.put(formName, displayGroup);
                                logger.debug("adding to changed items map: " + formName);
                            }
                        }
                        first = false;
                    }
                } else {
                    DisplayItemBean dib = diwb.getSingleItem();
                    ItemBean ib = dib.getItem();
                    ItemDataBean idb = dib.getData();
                    int itemId = ib.getId();
                    String itemName = ib.getName();
                    String value = idb.getValue();
                    scoreItems.put(itemName, ib);
                    // for items which are not in any group, their ordinal is
                    // set as 1
                    TreeSet<Integer> ordinalset = new TreeSet<Integer>();
                    ordinalset.add(1);
                    itemOrdinals.put(itemId, ordinalset);
                    scoreItemdata.put(itemId + "_" + 1, value);
                    if (isChanged(idb, oldItemdata, dib, attachedFilePath)) {
                        changedItems.add(itemName);
                        changedItemsList.add(dib);
                        // changedItemsMap.put(dib.getItem().getName(), new
                        // DisplayItemGroupBean());
                    }

                    ArrayList children = dib.getChildren();
                    for (int j = 0; j < children.size(); j++) {
                        DisplayItemBean child = (DisplayItemBean) children.get(j);
                        ItemBean cib = child.getItem();
                        scoreItems.put(cib.getName(), cib);
                        TreeSet<Integer> cordinalset = new TreeSet<Integer>();
                        cordinalset.add(1);
                        itemOrdinals.put(itemId, cordinalset);
                        scoreItemdata.put(cib.getId() + "_" + 1, child.getData().getValue());
                        if (isChanged(child.getData(), oldItemdata, child, attachedFilePath)) {
                            changedItems.add(itemName);
                            changedItemsList.add(child);
                            // changedItemsMap.put(itemName, new
                            // DisplayItemGroupBean());
                        }
                    }
                }
            }

            // do calculation for 'calculation' and 'group-calculation' type
            // items
            // and write the result in DisplayItemBean's ItemDateBean - data
            ScoreItemValidator sv = new ScoreItemValidator(request, discNotes);
            // *** doing calc here, load it where? ***
            ScoreCalculator sc = new ScoreCalculator(sm, ecb, ub);
            for (int i = 0; i < allItems.size(); i++) {
                DisplayItemWithGroupBean diwb = allItems.get(i);
                if (diwb.isInGroup()) {
                    List<DisplayItemGroupBean> dgbs = diwb.getItemGroups();
                    for (int j = 0; j < dgbs.size(); j++) {
                        DisplayItemGroupBean displayGroup = dgbs.get(j);

                        List<DisplayItemBean> items = displayGroup.getItems();
                        for (DisplayItemBean displayItem : items) {
                            ItemFormMetadataBean ifmb = displayItem.getMetadata();
                            int responseTypeId = ifmb.getResponseSet().getResponseTypeId();
                            if (responseTypeId == 8 || responseTypeId == 9) {
                                StringBuffer err = new StringBuffer();
                                ResponseOptionBean robBean = (ResponseOptionBean) ifmb.getResponseSet().getOptions().get(0);
                                String value = "";

                                String inputName = "";
                                // note that we have to use
                                // getFormInputOrdinal() here, tbh 06/2009
                                if (displayGroup.isAuto()) {
                                    inputName = getGroupItemInputName(displayGroup, displayGroup.getFormInputOrdinal(), displayItem);
                                    logger.debug("returning input name: " + inputName);
                                } else {
                                    inputName = getGroupItemManualInputName(displayGroup, displayGroup.getFormInputOrdinal(), displayItem);
                                    logger.debug("returning input name: " + inputName);
                                }
                                if (robBean.getValue().startsWith("func: getexternalvalue") || robBean.getValue().startsWith("func: getExternalValue")) {

                                    value = fp.getString(inputName);
                                    logger.debug("*** just set " + fp.getString(inputName) + " for line 815 " + displayItem.getItem().getName()
                                        + " with input name " + inputName);

                                } else {
                                    value = sc.doCalculation(displayItem, scoreItems, scoreItemdata, itemOrdinals, err, displayItem.getData().getOrdinal());
                                }
                                displayItem.loadFormValue(value);
                                if (isChanged(displayItem, oldItemdata, attachedFilePath)) {
                                    changedItems.add(displayItem.getItem().getName());
                                    changedItemsList.add(displayItem);
                                }

                                request.setAttribute(inputName, value);
                                if (validate) {
                                    displayItem = validateCalcTypeDisplayItemBean(sv, displayItem, inputName);
                                    if (err.length() > 0) {
                                        Validation validation = new Validation(Validator.CALCULATION_FAILED);
                                        validation.setErrorMessage(err.toString());
                                        sv.addValidation(inputName, validation);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    DisplayItemBean dib = diwb.getSingleItem();
                    ItemFormMetadataBean ifmb = dib.getMetadata();
                    int responseTypeId = ifmb.getResponseSet().getResponseTypeId();
                    if (responseTypeId == 8 || responseTypeId == 9) {
                        StringBuffer err = new StringBuffer();
                        ResponseOptionBean robBean = (ResponseOptionBean) ifmb.getResponseSet().getOptions().get(0);
                        String value = "";
                        if (robBean.getValue().startsWith("func: getexternalvalue") || robBean.getValue().startsWith("func: getExternalValue")) {
                            String itemName = getInputName(dib);
                            value = fp.getString(itemName);
                            logger.debug("just set " + fp.getString(itemName) + " for " + dib.getItem().getName());
                            logger.debug("found in fp: " + fp.getString(dib.getItem().getName()));
                            // logger.debug("scoreitemdata: " +
                            // scoreItemdata.toString());
                        } else {
                            value = sc.doCalculation(dib, scoreItems, scoreItemdata, itemOrdinals, err, 1);
                        }
                        dib.loadFormValue(value);
                        if (isChanged(dib.getData(), oldItemdata, dib, attachedFilePath)) {
                            changedItems.add(dib.getItem().getName());
                            changedItemsList.add(dib);
                            // changedItemsMap.put(dib.getItem().getName(), new
                            // DisplayItemGroupBean());
                        }
                        String inputName = getInputName(dib);
                        request.setAttribute(inputName, value);
                        if (validate) {
                            dib = validateCalcTypeDisplayItemBean(sv, dib, "");
                            if (err.length() > 0) {
                                Validation validation = new Validation(Validator.CALCULATION_FAILED);
                                validation.setErrorMessage(err.toString());
                                sv.addValidation(inputName, validation);
                            }
                        }
                    }

                    ArrayList children = dib.getChildren();
                    for (int j = 0; j < children.size(); j++) {
                        DisplayItemBean child = (DisplayItemBean) children.get(j);
                        ItemFormMetadataBean cifmb = child.getMetadata();
                        int resTypeId = cifmb.getResponseSet().getResponseTypeId();
                        if (resTypeId == 8 || resTypeId == 9) {
                            StringBuffer cerr = new StringBuffer();
                            child.getDbData().setValue(child.getData().getValue());
                            ResponseOptionBean crobBean = (ResponseOptionBean) cifmb.getResponseSet().getOptions().get(0);
                            String cvalue = "";
                            if (crobBean.getValue().startsWith("func: getexternalvalue") || crobBean.getValue().startsWith("func: getExternalValue")) {
                                String itemName = getInputName(child);
                                cvalue = fp.getString(itemName);
                                logger.debug("just set " + fp.getString(itemName) + " for " + child.getItem().getName());

                            } else {
                                cvalue = sc.doCalculation(child, scoreItems, scoreItemdata, itemOrdinals, cerr, 1);
                            }
                            child.loadFormValue(cvalue);
                            if (isChanged(child.getData(), oldItemdata, child, attachedFilePath)) {
                                changedItems.add(child.getItem().getName());
                                changedItemsList.add(child);
                                // changedItemsMap.put(child.getItem().getName(),
                                // new DisplayItemGroupBean());
                            }
                            String cinputName = getInputName(child);
                            request.setAttribute(cinputName, cvalue);
                            if (validate) {
                                child = validateCalcTypeDisplayItemBean(sv, child, "");
                                if (cerr.length() > 0) {
                                    Validation cvalidation = new Validation(Validator.CALCULATION_FAILED);
                                    cvalidation.setErrorMessage(cerr.toString());
                                    sv.addValidation(cinputName, cvalidation);
                                }
                            }
                        }
                        children.set(j, child);
                    }
                }
            }
            // YW >>

            // we have to do this since we loaded all the form values into the
            // display
            // item beans above
            // section.setItems(items);
            // setting this AFTER we populate notes - will that make a difference?
            section.setDisplayItemGroups(allItems);

            // logger.debug("+++ try to populate notes");

            section = populateNotesWithDBNoteCounts(discNotes, section);
            // logger.debug("+++ try to populate notes, got count of field notes: " + discNotes.getFieldNotes().toString());

            if (currentStudy.getStudyParameterConfig().getInterviewerNameRequired().equals("yes")) {
                v.addValidation(INPUT_INTERVIEWER, Validator.NO_BLANKS);
            }

            if (currentStudy.getStudyParameterConfig().getInterviewDateRequired().equals("yes")) {
                v.addValidation(INPUT_INTERVIEW_DATE, Validator.NO_BLANKS);
            }

            if (!StringUtil.isBlank(fp.getString(INPUT_INTERVIEW_DATE))) {
                v.addValidation(INPUT_INTERVIEW_DATE, Validator.IS_A_DATE);
                v.alwaysExecuteLastValidation(INPUT_INTERVIEW_DATE);
            }

            // logger.debug("about to validate: " + v.getKeySet());
            errors = v.validate();

            // tbh >>
            if (this.isAdminForcedReasonForChange() && this.isAdministrativeEditing() && errors.isEmpty()) {
                // "You have changed data after this CRF was marked complete. "
                // +
                // "You must provide a Reason For Change discrepancy note for this item before you can save this updated information."
                String error = respage.getString("reason_for_change_error");
                // "Please enter a reason for change discrepancy note before saving."
                // ;
                int nonforcedChanges = 0;
                // change everything here from changed items list to changed
                // items map
                if (changedItemsList.size() > 0) {
                    logger.debug("found admin force reason for change: changed items " + changedItems.toString() + " and changed items list: "
                        + changedItemsList.toString() + " changed items map: " + changedItemsMap.toString());

                    for (DisplayItemBean displayItem : changedItemsList) {
                        String formName = getInputName(displayItem);

                        ItemDataBean idb = displayItem.getData();
                        ItemFormMetadataBean ifmb = displayItem.getMetadata();
                        logger.debug("-- found group label " + ifmb.getGroupLabel());
                        if (!ifmb.getGroupLabel().equalsIgnoreCase("Ungrouped") && !ifmb.getGroupLabel().equalsIgnoreCase("")) {
                            // << tbh 11/2009 sometimes the group label is blank instead of ungrouped???
                            Iterator iter = changedItemsMap.entrySet().iterator();
                            while (iter.hasNext()) {
                                Map.Entry<String, DisplayItemGroupBean> pairs = (Map.Entry) iter.next();
                                String formName2 = pairs.getKey();
                                DisplayItemGroupBean dgb = pairs.getValue();
                                // logger.debug("found auto: " +
                                // dgb.isAuto());
                                String testFormName = "";
                                if (dgb.isAuto()) {
                                    // testFormName = getGroupItemInputName(dgb, dgb.getFormInputOrdinal(), getManualRows(dgbs), displayItem);
                                    testFormName = getGroupItemInputName(dgb, dgb.getFormInputOrdinal(), displayItem);
                                } else {
                                    testFormName = getGroupItemManualInputName(dgb, dgb.getFormInputOrdinal(), displayItem);
                                }
                                logger.debug("found test form name: " + testFormName);
                                // if our test is the same with both the display
                                // item and the display group ...
                                // logger.debug("comparing " +
                                // testFormName + " and " + formName2);
                                int existingNotes = dndao.findNumExistingNotesForItem(idb.getId());
                                if (testFormName.equals(formName2)) {
                                    formName = formName2;
                                    this.setReasonForChangeError(idb, formName, error);
                                    changedItemsMap.remove(formName2);
                                    logger.debug("form name changed: " + formName);
                                    break;
                                    // .., send it as the form name
                                }
                                // ... otherwise, don't touch it
                                // how to tell vs. manual and just plain input?
                            }

                        } else {
                            this.setReasonForChangeError(idb, formName, error);
                            logger.debug("form name added: " + formName);
                        }
                    }
                }
                if (nonforcedChanges > 0) {
                    // do smething here?
                }
            }
            logger.debug("errors here: " + errors.toString());
            // <<
            if (errors.isEmpty() && shouldRunRules()) {
                logger.debug("Errors was empty");
                if (session.getAttribute("rulesErrors") != null) {
                    // rules have already generated errors, Let's compare old
                    // error list with new
                    // error list, if lists not same show errors.
                    HashMap h = ruleValidator.validate();
                    Set<String> a = (Set<String>) session.getAttribute("rulesErrors");
                    Set<String> ba = h.keySet();
                    Boolean showErrors = false;
                    for (Object key : ba) {
                        if (!a.contains(key)) {
                            showErrors = true;
                        }
                    }
                    if (showErrors) {
                        errors = h;
                        if (errors.size() > 0) {
                            session.setAttribute("shouldRunValidation", "1");
                            session.setAttribute("rulesErrors", errors.keySet());
                        } else {
                            session.setAttribute("shouldRunValidation", null);
                            session.setAttribute("rulesErrors", null);
                        }
                    } else {
                        session.setAttribute("shouldRunValidation", null);
                        session.setAttribute("rulesErrors", null);

                    }

                } else if (session.getAttribute("shouldRunValidation") != null && session.getAttribute("shouldRunValidation").toString().equals("1")) {
                    session.setAttribute("shouldRunValidation", null);
                    session.setAttribute("rulesErrors", null);
                } else {
                    errors = ruleValidator.validate();
                    if (errors.size() > 0) {
                        session.setAttribute("shouldRunValidation", "1");
                        session.setAttribute("rulesErrors", errors.keySet());
                    }
                }
            }

            if (!errors.isEmpty()) {
                logger.debug("threw an error with data entry...");
                // copying below three lines, tbh 03/2010
                String[] textFields = { INPUT_INTERVIEWER, INPUT_INTERVIEW_DATE };
                fp.setCurrentStringValuesAsPreset(textFields);
                setPresetValues(fp.getPresetValues());
                // YW, 2-4-2008 <<
                HashMap<String, ArrayList<String>> siErrors = sv.validate();
                if (siErrors != null && !siErrors.isEmpty()) {
                    Iterator iter = siErrors.keySet().iterator();
                    while (iter.hasNext()) {
                        String fieldName = iter.next().toString();
                        errors.put(fieldName, siErrors.get(fieldName));
                    }
                }
                // we should 'shift' the names here, tbh 02/2010
                // need: total number of rows, manual rows, all row names
                // plus: error names
                Iterator iter2 = errors.keySet().iterator();
                while (iter2.hasNext()) {
                    String fieldName = iter2.next().toString();
                    logger.debug("found error " + fieldName);
                }
                //                for (int i = 0; i < allItems.size(); i++) {
                //                    DisplayItemWithGroupBean diwb = allItems.get(i);
                //
                //                    if (diwb.isInGroup()) {
                //                        List<DisplayItemGroupBean> dgbs = diwb.getItemGroups();
                //                        logger.debug("found manual rows " + getManualRows(dgbs) + " and total rows " + dgbs.size() + " from ordinal " + diwb.getOrdinal());
                //                    }
                //                }

                errors = reshuffleErrorGroupNamesKK(errors, allItems);
                // reset manual rows, so that we can catch errors correctly
                // but it needs to be set per block of repeating items?  what if there are two or more?

                /*
                int manualRows = 0; // getManualRows(formGroups);
                for (int i = 0; i < allItems.size(); i++) {
                    DisplayItemWithGroupBean diwb = allItems.get(i);

                    if (diwb.isInGroup()) {
                        List<DisplayItemGroupBean> dgbs = diwb.getItemGroups();
                        manualRows = getManualRows(dgbs);
                    }
                }
                */
                //request.setAttribute("manualRows", new Integer(manualRows));
                Iterator iter3 = errors.keySet().iterator();
                while (iter3.hasNext()) {
                    String fieldName = iter3.next().toString();
                    logger.debug("found error after shuffle " + fieldName);
                }
                // << tbh, 02/2010
                // YW >>
                // copied
                request.setAttribute(BEAN_DISPLAY, section);
                request.setAttribute(BEAN_ANNOTATIONS, fp.getString(INPUT_ANNOTATIONS));
                setInputMessages(errors);
                addPageMessage(respage.getString("errors_in_submission_see_below"));
                request.setAttribute("hasError", "true");
                // addPageMessage("To override these errors and keep the data as
                // you
                // entered it, click one of the \"Confirm\" buttons. ");
                // if (section.isCheckInputs()) {
                // addPageMessage("Please notice that you must enter data for
                // the
                // <b>required</b> entries.");
                // }
                // we do not save any DNs if we get here, so we have to set it back into session...
                session.setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);
                // << tbh 01/2010
                setUpPanel(section);
                forwardPage(getJSPPage());
            } else {
                logger.debug("Do we hit this in save ?????");
                ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
                boolean success = true;
                boolean temp = true;

                // save interviewer name and date into DB
                ecb.setInterviewerName(fp.getString(INPUT_INTERVIEWER));
                if (!StringUtil.isBlank(fp.getString(INPUT_INTERVIEW_DATE))) {
                    ecb.setDateInterviewed(fp.getDate(INPUT_INTERVIEW_DATE));
                } else {
                    ecb.setDateInterviewed(null);
                }

                if (ecdao == null) {
                    ecdao = new EventCRFDAO(sm.getDataSource());
                }
                // set validator id for DDE
                DataEntryStage stage = ecb.getStage();
                if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE) || stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
                    ecb.setValidatorId(ub.getId());

                }
                /*
                 * if(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus .SIGNED)){ if(edcBean.isDoubleEntry()){
                 * ecb.setStage(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE); }else{ ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE); } }
                 */

                // for Administrative editing
                if (studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.SIGNED) && changedItemsList.size() > 0) {
                    studyEventBean.setSubjectEventStatus(SubjectEventStatus.COMPLETED);
                    studyEventBean.setUpdater(ub);
                    studyEventBean.setUpdatedDate(new Date());
                    seDao.update(studyEventBean);
                }

                // If the Study Subject's Satus is signed and we save a section
                // , change status to available
                logger.debug("Status of Study Subject {}", ssb.getStatus().getName());
                if (ssb.getStatus() == Status.SIGNED && changedItemsList.size() > 0) {
                    logger.debug("Status of Study Subject is Signed we are updating");
                    StudySubjectDAO studySubjectDao = new StudySubjectDAO(sm.getDataSource());
                    ssb.setStatus(Status.AVAILABLE);
                    ssb.setUpdater(ub);
                    ssb.setUpdatedDate(new Date());
                    studySubjectDao.update(ssb);
                }
                if (ecb.isSdvStatus() && changedItemsList.size() > 0) {
                    logger.debug("Status of Study Subject is SDV we are updating");
                    StudySubjectDAO studySubjectDao = new StudySubjectDAO(sm.getDataSource());
                    ssb.setStatus(Status.AVAILABLE);
                    ssb.setUpdater(ub);
                    ssb.setUpdatedDate(new Date());
                    studySubjectDao.update(ssb);
                    ecb.setSdvStatus(false);
                }

                ecb = (EventCRFBean) ecdao.update(ecb);

                // save discrepancy notes into DB
                FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) session.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
                dndao = new DiscrepancyNoteDAO(sm.getDataSource());

                AddNewSubjectServlet.saveFieldNotes(INPUT_INTERVIEWER, fdn, dndao, ecb.getId(), "EventCRF", currentStudy);
                AddNewSubjectServlet.saveFieldNotes(INPUT_INTERVIEW_DATE, fdn, dndao, ecb.getId(), "EventCRF", currentStudy);

                // items = section.getItems();
                allItems = section.getDisplayItemGroups();

                logger.debug("all items before saving into DB" + allItems.size());
                this.output(allItems);

                
                for (int i = 0; i < allItems.size(); i++) {
                    DisplayItemWithGroupBean diwb = allItems.get(i);

                    // we don't write success = success && writeToDB here
                    // since the short-circuit mechanism may prevent Java
                    // from executing writeToDB.
                    if (diwb.isInGroup()) {

                        List<DisplayItemGroupBean> dgbs = diwb.getItemGroups();
                        // using the above gets us the correct number of manual groups, tbh 01/2010
                        List<DisplayItemGroupBean> dbGroups = diwb.getDbItemGroups();
                        logger.debug("item group size: " + dgbs.size());
                        logger.debug("item db-group size: " + dbGroups.size());
                        for (int j = 0; j < dgbs.size(); j++) {
                            DisplayItemGroupBean displayGroup = dgbs.get(j);
                            List<DisplayItemBean> items = displayGroup.getItems();
                            // this ordinal will only useful to create a new
                            // item data
                            // update an item data won't touch its ordinal
                            int nextOrdinal = iddao.getMaxOrdinalForGroup(ecb, sb, displayGroup.getItemGroupBean()) + 1;

                            for (DisplayItemBean displayItem : items) {
                                String fileName = this.addAttachedFilePath(displayItem, attachedFilePath);
                                displayItem.setEditFlag(displayGroup.getEditFlag());
                                logger.debug("group item value: " + displayItem.getData().getValue());
                                if ("add".equalsIgnoreCase(displayItem.getEditFlag()) && fileName.length() > 0 && !newUploadedFiles.containsKey(fileName)) {
                                    displayItem.getData().setValue("");
                                }
                                temp = writeToDB(displayItem, iddao, nextOrdinal);
                                logger.debug("just executed writeToDB - 1");
                                logger.debug("next ordinal: " + nextOrdinal);
                                if (temp && newUploadedFiles.containsKey(fileName)) {
                                    newUploadedFiles.remove(fileName);
                                }
                                // maybe put ordinal in the place of j? maybe subtract max rows from next ordinal if j is gt
                                // next ordinal?
                                String inputName = getGroupItemInputName(displayGroup, j, displayItem);
                                // String inputName2 = getGroupItemManualInputName(displayGroup, j, displayItem);
                                if (!displayGroup.isAuto()) {
                                    logger.trace("not auto");
                                    inputName = this.getGroupItemManualInputName(displayGroup, j, displayItem);

                                }
                                if (j == dgbs.size() - 1) {
                                    // LAST ONE
                                    logger.trace("last one");
                                    int ordinal = j - this.getManualRows(dgbs);
                                    logger.info("+++ found manual rows from line 1326: " + ordinal);
                                    inputName = getGroupItemInputName(displayGroup, ordinal, displayItem);
                                }
                                // logger.trace("&&& we get previous looking at input name: " + inputName + " " + inputName2);
                                logger.trace("&&& we get previous looking at input name: " + inputName);
                                // input name 2 removed from below
                                AddNewSubjectServlet.saveFieldNotes(inputName, fdn, dndao, displayItem.getData().getId(), "itemData", currentStudy);
                                success = success && temp;
                            }
                        }
                        for (int j = 0; j < dbGroups.size(); j++) {
                            DisplayItemGroupBean displayGroup = dbGroups.get(j);
                            if ("remove".equalsIgnoreCase(displayGroup.getEditFlag())) {
                                List<DisplayItemBean> items = displayGroup.getItems();
                                for (DisplayItemBean displayItem : items) {
                                    String fileName = this.addAttachedFilePath(displayItem, attachedFilePath);
                                    displayItem.setEditFlag(displayGroup.getEditFlag());
                                    logger.debug("group item value: " + displayItem.getData().getValue());
                                    if ("add".equalsIgnoreCase(displayItem.getEditFlag()) && fileName.length() > 0 && !newUploadedFiles.containsKey(fileName)) {
                                        displayItem.getData().setValue("");
                                    }
                                    temp = writeToDB(displayItem, iddao, 0);
                                    logger.debug("just executed writeToDB - 2");
                                    if (temp && newUploadedFiles.containsKey(fileName)) {
                                        newUploadedFiles.remove(fileName);
                                    }
                                    // just use 0 here since update doesn't
                                    // touch ordinal
                                    success = success && temp;
                                }
                            }
                        }

                    } else {
                        DisplayItemBean dib = diwb.getSingleItem();
                        // TODO work on this line

                        this.addAttachedFilePath(dib, attachedFilePath);
                        if(dib.getMetadata().isConditionalDisplayItem()){
                            temp=this.writeConditionalDisplayItemToDB(dib,iddao,1,section.getShowSCDItemIds().contains(dib.getMetadata().getItemId()));
                        } else {
                            temp = writeToDB(dib, iddao, 1);
                        }
                        logger.debug("just executed writeToDB - 3");
                        if (temp && newUploadedFiles.containsKey(dib.getItem().getId() + "")) {
                            // so newUploadedFiles will contain only failed file
                            // items;
                            newUploadedFiles.remove(dib.getItem().getId() + "");
                        }

                        String inputName = getInputName(dib);
                        logger.trace("3 - found input name: " + inputName);
                        AddNewSubjectServlet.saveFieldNotes(inputName, fdn, dndao, dib.getData().getId(), "itemData", currentStudy);

                        success = success && temp;

                        ArrayList childItems = dib.getChildren();
                        for (int j = 0; j < childItems.size(); j++) {
                            DisplayItemBean child = (DisplayItemBean) childItems.get(j);
                            this.addAttachedFilePath(child, attachedFilePath);
                            if(child.getMetadata().isConditionalDisplayItem()){
                                temp=this.writeConditionalDisplayItemToDB(child,iddao,1,section.getShowSCDItemIds().contains(child.getMetadata().getItemId()));
                            } else {
                                temp = writeToDB(child, iddao, 1);
                            }
                            logger.debug("just executed writeToDB - 4");
                            if (temp && newUploadedFiles.containsKey(child.getItem().getId() + "")) {
                                // so newUploadedFiles will contain only failed
                                // file items;
                                newUploadedFiles.remove(child.getItem().getId() + "");
                            }
                            inputName = getInputName(child);
                            AddNewSubjectServlet.saveFieldNotes(inputName, fdn, dndao, child.getData().getId(), "itemData", currentStudy);
                            success = success && temp;
                        }
                    }
                }
                System.out.println("running rules: " + phase2.name());

                HashMap<String, ArrayList<String>> rulesPostDryRun = runRules(allItems, ruleSets, false, shouldRunRules(), MessageType.WARNING, phase2,ecb);
                System.out.println("found rules post dry run: " + rulesPostDryRun.toString());
                HashMap<String, ArrayList<String>> errorsPostDryRun = new HashMap<String, ArrayList<String>>();
                // additional step needed, run rules and see if any items are 'shown' AFTER saving data
                boolean inSameSection = false;
                if (!rulesPostDryRun.isEmpty()) {
                    // in same section?

                    // iterate through the OIDs and see if any of them belong to this section
                    Iterator iter3 = rulesPostDryRun.keySet().iterator();
                    while (iter3.hasNext()) {
                        String fieldName = iter3.next().toString();
                        logger.debug("found oid after post dry run " + fieldName);
                        // set up a listing of OIDs in the section
                        // BUT: Oids can have the group name in them.
                        String newFieldName = fieldName;
                        String[] fieldNames = fieldName.split("\\.");
                        if (fieldNames.length == 2) {
                            newFieldName = fieldNames[1];
                            // check items in item groups here?
                        }
                        List<DisplayItemWithGroupBean> displayGroupsWithItems = section.getDisplayItemGroups();
                        //ArrayList<DisplayItemBean> displayItems = section.getItems();
                        for (int i = 0; i < displayGroupsWithItems.size(); i++) {
                            DisplayItemWithGroupBean itemWithGroup = displayGroupsWithItems.get(i);
                            if (itemWithGroup.isInGroup()) {
                                logger.debug("found group: " + fieldNames[0]);
                                // do something there
                                List<DisplayItemGroupBean> digbs = itemWithGroup.getItemGroups();
                                logger.debug("digbs size: " + digbs.size());
                                for (int j = 0; j < digbs.size(); j++) {
                                    DisplayItemGroupBean displayGroup = digbs.get(j);
                                    if (displayGroup.getItemGroupBean().getOid().equals(fieldNames[0])) {
                                        List<DisplayItemBean> items = displayGroup.getItems();

                                        for (int k = 0; k < items.size(); k++) {
                                            DisplayItemBean dib = items.get(k);
                                            if (dib.getItem().getOid().equals(newFieldName)) {
                                                //inSameSection = true;

                                                if (!dib.getMetadata().isShowItem()) {
                                                    logger.debug("found item in group " + this.getGroupItemInputName(displayGroup, j, dib) + " vs. "
                                                        + fieldName + " and is show item: " + dib.getMetadata().isShowItem());
                                                    dib.getMetadata().setShowItem(true);
                                                    inSameSection = true;
                                                    errorsPostDryRun.put(this.getGroupItemInputName(displayGroup, j, dib), rulesPostDryRun.get(fieldName));
                                                }
                                            }
                                            items.set(k, dib);
                                        }
                                        displayGroup.setItems(items);
                                        digbs.set(j, displayGroup);
                                    }
                                }
                                itemWithGroup.setItemGroups(digbs);
                            } else {
                                DisplayItemBean displayItemBean = itemWithGroup.getSingleItem();
                                ItemBean itemBean = displayItemBean.getItem();
                                if (newFieldName.equals(itemBean.getOid())) {
                                    System.out.println("is show item for " + displayItemBean.getItem().getId() + ": " + displayItemBean.getMetadata().isShowItem());
                                    System.out.println("check run dynamics item check " + runDynamicsItemCheck(displayItemBean).getMetadata().isShowItem());
                                    if (!displayItemBean.getMetadata().isShowItem() && !runDynamicsItemCheck(displayItemBean).getMetadata().isShowItem()) {
                                        // double check there?
                                        inSameSection = true;
                                        logger.debug("found item " + this.getInputName(displayItemBean) + " vs. " + fieldName + " and is show item: "
                                            + displayItemBean.getMetadata().isShowItem());
                                        // if is repeating, use the other input name? no

                                        errorsPostDryRun.put(this.getInputName(displayItemBean), rulesPostDryRun.get(fieldName));

                                        displayItemBean.getMetadata().setShowItem(true);
                                    }
                                }
                                itemWithGroup.setSingleItem(displayItemBean);
                            }
                            displayGroupsWithItems.set(i, itemWithGroup);
                        }

                        // check groups
                        List<DisplayItemGroupBean> itemGroups = new ArrayList<DisplayItemGroupBean>();
                        itemGroups = section.getDisplayFormGroups();
                        // List<DisplayItemGroupBean> newItemGroups = new ArrayList<DisplayItemGroupBean>();
                        for (DisplayItemGroupBean displayGroup : itemGroups) {
                            if (fieldName.equals(displayGroup.getItemGroupBean().getOid())) {
                                if (!displayGroup.getGroupMetaBean().isShowGroup()) {
                                    inSameSection = true;
                                    logger.debug("found itemgroup " + displayGroup.getItemGroupBean().getOid() + " vs. " + fieldName + " and is show item: "
                                        + displayGroup.getGroupMetaBean().isShowGroup());
                                    // hmmm how to set highlighting for a group?
                                    errorsPostDryRun.put(displayGroup.getItemGroupBean().getOid(), rulesPostDryRun.get(fieldName));
                                    displayGroup.getGroupMetaBean().setShowGroup(true);
                                    // add necessary rows to the display group here????
                                    // we have to set the items in the itemGroup for the displayGroup

                                }
                            }
                            // newItemGroups.add(displayGroup);
                        }
                        // trying to reset the display form groups here, tbh

                        // section.setItems(displayItems);
                        section.setDisplayItemGroups(displayGroupsWithItems);

                        // section.setDisplayFormGroups(newDisplayBean.getDisplayFormGroups());

                    }
                    // we need the following for repeating groups, tbh
                    // >> tbh 06/2010
                    // List<DisplayItemWithGroupBean> displayItemWithGroups2 = createItemWithGroups(section, hasGroup, eventDefinitionCRFId);

                    // section.setDisplayItemGroups(displayItemWithGroups2);

                    // if so, stay at this section
                    logger.debug(" in same section: " + inSameSection);
                    System.out.println(" in same section: " + inSameSection);
                    if (inSameSection) {
                        // copy of one line from early on around line 400, forcing a re-show of the items
                        // section = getDisplayBean(hasGroup, true);// include all items, tbh
                        // below a copy of three lines from the if errors = true line, tbh 03/2010
                        String[] textFields = { INPUT_INTERVIEWER, INPUT_INTERVIEW_DATE };
                        fp.setCurrentStringValuesAsPreset(textFields);
                        setPresetValues(fp.getPresetValues());
                        // below essetially a copy except for rulesPostDryRun
                        request.setAttribute(BEAN_DISPLAY, section);
                        request.setAttribute(BEAN_ANNOTATIONS, fp.getString(INPUT_ANNOTATIONS));
                        setInputMessages(errorsPostDryRun);
                        addPageMessage(respage.getString("your_answers_activated_hidden_items"));
                        request.setAttribute("hasError", "true");
                        request.setAttribute("hasShown", "true");

                        session.setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);
                        setUpPanel(section);
                        forwardPage(getJSPPage());
                    }
                }

                if (!inSameSection) {// else if not in same section, progress as usual

                    // can we just forward page or do we actually need an ELSE here?
                    // yes, we do. tbh 05/03/2010

                    ArrayList<String> updateFailedItems = sc.redoCalculations(scoreItems, scoreItemdata, changedItems, itemOrdinals, sb.getId());
                    success = updateFailedItems.size() > 0 ? false : true;

                    // now check if CRF is marked complete
                    boolean markComplete = fp.getString(INPUT_MARK_COMPLETE).equals(VALUE_YES);
                    boolean markSuccessfully = false; // if the CRF was marked
                    // complete
                    // successfully
                    if (markComplete) {
                        logger.debug("need to mark CRF as complete");
                        markSuccessfully = markCRFComplete();
                        logger.debug("...marked CRF as complete: " + markSuccessfully);
                        if (!markSuccessfully) {
                            request.setAttribute(BEAN_DISPLAY, section);
                            request.setAttribute(BEAN_ANNOTATIONS, fp.getString(INPUT_ANNOTATIONS));
                            setUpPanel(section);
                            forwardPage(getJSPPage());
                            return;
                        }

                    }

                    // now write the event crf bean to the database
                    String annotations = fp.getString(INPUT_ANNOTATIONS);
                    setEventCRFAnnotations(annotations);
                    Date now = new Date();
                    ecb.setUpdatedDate(now);
                    ecb.setUpdater(ub);
                    ecb = (EventCRFBean) ecdao.update(ecb);
                    success = success && ecb.isActive();

                    StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
                    StudyEventBean seb = (StudyEventBean) sedao.findByPK(ecb.getStudyEventId());
                    seb.setUpdatedDate(now);
                    seb.setUpdater(ub);
                    seb = (StudyEventBean) sedao.update(seb);
                    success = success && seb.isActive();

                    request.setAttribute(INPUT_IGNORE_PARAMETERS, Boolean.TRUE);

                    if (newUploadedFiles.size() > 0) {
                        if (this.unloadFiles(newUploadedFiles)) {

                        } else {
                            String missed = "";
                            Iterator iter = newUploadedFiles.keySet().iterator();
                            while (iter.hasNext()) {
                                missed += " " + newUploadedFiles.get(iter.next());
                            }
                            addPageMessage(respage.getString("uploaded_files_not_deleted_or_not_exist") + ": " + missed);
                        }
                    }
                    if (!success) {
                        // YW, 3-6-2008 <<
                        if (updateFailedItems.size() > 0) {
                            String mess = "";
                            for (String ss : updateFailedItems) {
                                mess += ss + ", ";
                            }
                            mess = mess.substring(0, mess.length() - 2);
                            addPageMessage(resexception.getString("item_save_failed_because_database_error") + mess);
                        } else {
                            // YW>>
                            addPageMessage(resexception.getString("database_error"));
                        }
                        request.setAttribute(BEAN_DISPLAY, section);
                        session.removeAttribute(GROUP_HAS_DATA);
                        session.removeAttribute(HAS_DATA_FLAG);
                        session.removeAttribute(DDE_PROGESS);
                        session.removeAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
                        logger.debug("try to remove to_create_crf");
                        session.removeAttribute("to_create_crf");

                        // forwardPage(Page.SUBMIT_DATA_SERVLET);
                        forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET);
                        // >> changed tbh, 06/2009
                    } else {
                        boolean forwardingSucceeded = false;

                        if (!fp.getString(GO_PREVIOUS).equals("")) {
                            if (previousSec.isActive()) {
                                forwardingSucceeded = true;
                                request.setAttribute(INPUT_EVENT_CRF, ecb);
                                request.setAttribute(INPUT_SECTION, previousSec);
                                int tabNum = 0;
                                if (fp.getString("tab") == null) {
                                    tabNum = 1;
                                } else {
                                    tabNum = fp.getInt("tab");
                                }
                                request.setAttribute("tab", new Integer(tabNum - 1).toString());
                                forwardPage(getServletPage());
                            }
                        } else if (!fp.getString(GO_NEXT).equals("")) {
                            if (nextSec.isActive()) {
                                forwardingSucceeded = true;
                                request.setAttribute(INPUT_EVENT_CRF, ecb);
                                request.setAttribute(INPUT_SECTION, nextSec);
                                int tabNum = 0;
                                if (fp.getString("tab") == null) {
                                    tabNum = 1;
                                } else {
                                    tabNum = fp.getInt("tab");
                                }
                                request.setAttribute("tab", new Integer(tabNum + 1).toString());
                                forwardPage(getServletPage());
                            }
                        }

                        if (!forwardingSucceeded) {
                            // request.setAttribute(TableOfContentsServlet.
                            // INPUT_EVENT_CRF_BEAN,
                            // ecb);
                            if (markSuccessfully) {
                                addPageMessage(respage.getString("data_saved_CRF_marked_complete"));
                                session.removeAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
                                session.removeAttribute(GROUP_HAS_DATA);
                                session.removeAttribute(HAS_DATA_FLAG);
                                session.removeAttribute(DDE_PROGESS);
                                session.removeAttribute("to_create_crf");

                                request.setAttribute("eventId", new Integer(ecb.getStudyEventId()).toString());
                                forwardPage(Page.ENTER_DATA_FOR_STUDY_EVENT_SERVLET);
                            } else {
                                // use clicked 'save'
                                addPageMessage(respage.getString("data_saved_continue_entering_edit_later"));
                                request.setAttribute(INPUT_EVENT_CRF, ecb);
                                request.setAttribute(INPUT_EVENT_CRF_ID, new Integer(ecb.getId()).toString());
                                // forward to the next section if the previous one
                                // is not the last section
                                if (!section.isLastSection()) {
                                    request.setAttribute(INPUT_SECTION, nextSec);
                                    request.setAttribute(INPUT_SECTION_ID, new Integer(nextSec.getId()).toString());
                                    session.removeAttribute("mayProcessUploading");
                                } else {
                                    // already the last section, should go back to
                                    // view event page
                                    session.removeAttribute(GROUP_HAS_DATA);
                                    session.removeAttribute(HAS_DATA_FLAG);
                                    session.removeAttribute(DDE_PROGESS);
                                    session.removeAttribute("to_create_crf");
                                    session.removeAttribute("mayProcessUploading");

                                    request.setAttribute("eventId", new Integer(ecb.getStudyEventId()).toString());
                                    if(fromViewNotes != null && "1".equals(fromViewNotes)) {
                                        String viewNotesPageFileName = (String)session.getAttribute("viewNotesPageFileName");
                                        session.removeAttribute("viewNotesPageFileName");
                                        session.removeAttribute("viewNotesURL");
                                        if(viewNotesPageFileName!=null && viewNotesPageFileName.length()>0) {
                                            forwardPage(Page.setNewPage(viewNotesPageFileName, "View Notes"));
                                        }
                                    }
                                    forwardPage(Page.ENTER_DATA_FOR_STUDY_EVENT_SERVLET);
                                    return;

                                }
                                int tabNum = 0;
                                if (fp.getString("tab") == null) {
                                    tabNum = 1;
                                } else {
                                    tabNum = fp.getInt("tab");
                                }
                                if (!section.isLastSection()) {
                                    request.setAttribute("tab", new Integer(tabNum + 1).toString());
                                }

                                forwardPage(getServletPage());

                            }
                            // session.removeAttribute(AddNewSubjectServlet.
                            // FORM_DISCREPANCY_NOTES_NAME);
                            // forwardPage(Page.SUBMIT_DATA_SERVLET);
                        }
                    }
                }// end of if-block for dynamic rules not in same section, tbh 05/2010
            }// end of save
        }
    }

    /**
     * Changed by thickerson 05/2010
     * @param idb
     * @param formName
     * @param error
     */
    protected void setReasonForChangeError(ItemDataBean idb, String formName, String error) {
        DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());
        FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) session.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
        HashMap idNotes = fdn.getIdNotes();
        int existingNotes = dndao.findNumExistingNotesForItem(idb.getId());
        if (existingNotes > 0) {

            logger.debug("has a note in db " + formName);

            /*
             * Having existing notes is not enough to let it pass through after changing data. There has to be a DiscrepancyNote for the latest changed data
             */
            HashMap<Integer, Boolean> noteSubmitted = (HashMap<Integer, Boolean>) session.getAttribute(DataEntryServlet.NOTE_SUBMITTED);
            if (noteSubmitted == null || noteSubmitted.get(idb.getId()) == null || !(Boolean) noteSubmitted.get(idb.getId())) {
                boolean hasRfcAlready = false;
                ArrayList<DiscrepancyNoteBean> notes = dndao.findExistingNotesForItemData(idb.getId());
                for (DiscrepancyNoteBean note : notes) {
                    if (note.getDiscrepancyNoteTypeId() == DiscrepancyNoteType.REASON_FOR_CHANGE.getId()) {
                        hasRfcAlready = true;
                        logger.debug("has Rfc already: " + formName + " note id " + note.getId());
                    }
                }
                if (!hasRfcAlready) {
                    errors.put(formName, error);
                }
            } else {
                logger.debug("found note in session");
                logger.debug("has a note in db: entered an error here: " + formName + ": " + errors.toString());
            }

            // session.removeAttribute(DataEntryServlet.NOTE_SUBMITTED);
        } else if (idNotes.containsKey(idb.getId())) {
            logger.debug("has note in session");
        } else {
            // none, which means the error is thrown
            // nonforcedChanges++;
            logger.debug("setting an error for " + formName);
            errors.put(formName, error);
        }
    }

    /**
     * Get the input beans - the EventCRFBean and the SectionBean. For both beans, look first in the request attributes to see if the bean has been stored
     * there. If not, look in the parameters for the bean id, and then retrieve the bean from the database. The beans are stored as protected class members.
     */
    protected void getInputBeans() throws InsufficientPermissionException {

        fp = new FormProcessor(request);
        ecdao = new EventCRFDAO(sm.getDataSource());
        sdao = new SectionDAO(sm.getDataSource());
        // BWP >>we should have the correct crfVersionId, in order to acquire
        // the correct
        // section IDs
        ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
        if (ecb == null) {
            int eventCRFId = fp.getInt(INPUT_EVENT_CRF_ID, true);
            logger.debug("found event crf id: " + eventCRFId);
            if (eventCRFId > 0) {
                logger.debug("***NOTE*** that we didnt have to create an event crf because we already have one: " + eventCRFId);
                // there is an event CRF already, only need to update
                ecb = (EventCRFBean) ecdao.findByPK(eventCRFId);
                // ecb.setUpdatedDate(new Date());
                // ecb.setUpdater(ub);
                // ecb = (EventCRFBean) ecdao.update(ecb);
                // logger.trace("found an event crf id "+eventCRFId);

                // YW 11-12-2007 << if interviewer or/and interview date
                // has/have been updated for study/site from "blank" to
                // "pre-populated"
                // But at this point, this update only shows on web page and
                // will not be updated to database.
                int studyEventId = fp.getInt(INPUT_STUDY_EVENT_ID);
                if (studyEventId > 0) {
                    StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
                    StudyEventBean sEvent = (StudyEventBean) sedao.findByPK(studyEventId);
                    ecb = updateECB(sEvent);
                }
                request.setAttribute(INPUT_EVENT_CRF, ecb);
                // YW >>
            } else {
                // CRF id <=0, so we need to create a new CRF
                // use toCreateCRF as a flag to prevent user to submit event CRF
                // more than once
                // for example, user reloads the page
                String toCreateCRF = (String) session.getAttribute("to_create_crf");
                if (StringUtil.isBlank(toCreateCRF) || "0".equals(toCreateCRF)) {
                    session.setAttribute("to_create_crf", "1");
                }
                try {
                    // if (ecb.getInterviewerName() != null) {
                    logger.debug("Initial: to create an event CRF.");
                    String toCreateCRF1 = (String) session.getAttribute("to_create_crf");
                    if (!StringUtil.isBlank(toCreateCRF1) && "1".equals(toCreateCRF1)) {
                        ecb = createEventCRF();
                        session.setAttribute("ecb", ecb);
                        request.setAttribute(INPUT_EVENT_CRF, ecb);
                        session.setAttribute("to_create_crf", "0");
                    } else {
                        ecb = (EventCRFBean) session.getAttribute("ecb");
                    }
                    // }
                } catch (InconsistentStateException ie) {
                    ie.printStackTrace();
                    addPageMessage(ie.getOpenClinicaMessage());
                    throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS_SERVLET, ie.getOpenClinicaMessage(), "1");
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                    addPageMessage(ne.getMessage());
                    throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS_SERVLET, ne.getMessage(), "1");
                }
            }
        }
        // added to allow sections shown on this page
        DisplayTableOfContentsBean displayBean = new DisplayTableOfContentsBean();
        displayBean = TableOfContentsServlet.getDisplayBean(ecb, sm.getDataSource(), currentStudy);
        request.setAttribute(TOC_DISPLAY, displayBean);

        int sectionId = fp.getInt(INPUT_SECTION_ID, true);
        ArrayList sections;
        if (sectionId <= 0) {
            StudyEventDAO studyEventDao = new StudyEventDAO(sm.getDataSource());
            int maximumSampleOrdinal = studyEventDao.getMaxSampleOrdinal(displayBean.getStudyEventDefinition(), displayBean.getStudySubject());
            request.setAttribute("maximumSampleOrdinal", maximumSampleOrdinal);

            sections = sdao.findAllByCRFVersionId(ecb.getCRFVersionId());

            for (int i = 0; i < sections.size(); i++) {
                SectionBean sb = (SectionBean) sections.get(i);
                sectionId = sb.getId();// find the first section of this CRF
                break;
            }
        }
        sb = new SectionBean();
        if (sectionId > 0) {
            // int sectionId = fp.getInt(INPUT_SECTION_ID, true);
            sb = (SectionBean) sdao.findByPK(sectionId);
        }

        int tabId = fp.getInt("tab", true);
        if (tabId <= 0) {
            tabId = 1;
        }
        request.setAttribute(INPUT_TAB, new Integer(tabId));

    }

    /**
     * Tries to check if a seciton has item groups
     *
     * @return
     */
    protected boolean checkGroups() {
        int sectionId = fp.getInt(INPUT_SECTION_ID, true);
        if (sectionId <= 0) {
            ArrayList sections = sdao.findAllByCRFVersionId(ecb.getCRFVersionId());

            for (int i = 0; i < sections.size(); i++) {
                SectionBean sb = (SectionBean) sections.get(i);
                sectionId = sb.getId();// find the first section of this CRF
                break;
            }
        }

        // we will look into db to see if any repeating items for this CRF
        // section
        ItemGroupDAO igdao = new ItemGroupDAO(sm.getDataSource());
        // find any item group which doesn't equal to 'Ungrouped'
        List<ItemGroupBean> itemGroups = igdao.findLegitGroupBySectionId(sectionId);
        if (!itemGroups.isEmpty()) {
            logger.trace("This section has group");
            return true;
        }
        return false;

    }

    /**
     * Creates a new Event CRF or update the exsiting one, that is, an event CRF can be created but not item data yet, in this case, still consider it is not
     * started(called uncompleted before)
     *
     * @return
     * @throws Exception
     */
    private EventCRFBean createEventCRF() throws InconsistentStateException {
        locale = request.getLocale();
        // < resmessage =
        // ResourceBundle.getBundle("org.akaza.openclinica.i18n.page_messages",
        // locale);
        // < restext =
        // ResourceBundle.getBundle("org.akaza.openclinica.i18n.notes",locale);
        // <
        // resexception=ResourceBundle.getBundle(
        // "org.akaza.openclinica.i18n.exceptions",locale);

        EventCRFBean ecb;
        ecdao = new EventCRFDAO(sm.getDataSource());

        int crfVersionId = fp.getInt(INPUT_CRF_VERSION_ID);

        logger.trace("***FOUND*** crfversionid: " + crfVersionId);
        int studyEventId = fp.getInt(INPUT_STUDY_EVENT_ID);
        int eventDefinitionCRFId = fp.getInt(INPUT_EVENT_DEFINITION_CRF_ID);
        int subjectId = fp.getInt(INPUT_SUBJECT_ID);
        int eventCRFId = fp.getInt(INPUT_EVENT_CRF_ID);

        logger.trace("look specifically wrt event crf id: " + eventCRFId);

        logger.trace("Creating event CRF.  Study id: " + currentStudy.getId() + "; CRF Version id: " + crfVersionId + "; Study Event id: " + studyEventId
            + "; Event Definition CRF id: " + eventDefinitionCRFId + "; Subject: " + subjectId);

        StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
        StudySubjectBean ssb = ssdao.findBySubjectIdAndStudy(subjectId, currentStudy);

        if (ssb.getId() <= 0) {
            logger.trace("throwing ISE with study subject bean id of " + ssb.getId());
            // addPageMessage(resexception.getString(
            // "begin_data_entry_without_event_but_subject"));
            throw new InconsistentStateException(Page.LIST_STUDY_SUBJECTS_SERVLET, resexception.getString("begin_data_entry_without_event_but_subject"));
        }

        StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
        StudyEventDefinitionBean sedb = seddao.findByEventDefinitionCRFId(eventDefinitionCRFId);
        // logger.trace("study event definition:" + sedb.getId());
        if (sedb.getId() <= 0) {
            addPageMessage(resexception.getString("begin_data_entry_without_event_but_study"));
            throw new InconsistentStateException(Page.LIST_STUDY_SUBJECTS_SERVLET, resexception.getString("begin_data_entry_without_event_but_study"));
        }

        CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
        EntityBean eb = cvdao.findByPK(crfVersionId);

        if (eb.getId() <= 0) {
            //addPageMessage(resexception.getString("begin_data_entry_without_event_but_CRF"));
            throw new InconsistentStateException(Page.LIST_STUDY_SUBJECTS_SERVLET, resexception.getString("begin_data_entry_without_event_but_CRF"));
        }

        StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
        StudyEventBean sEvent = (StudyEventBean) sedao.findByPK(studyEventId);

        StudyBean studyWithSED = currentStudy;
        if (currentStudy.getParentStudyId() > 0) {
            studyWithSED = new StudyBean();
            studyWithSED.setId(currentStudy.getParentStudyId());
        }

        AuditableEntityBean aeb = sedao.findByPKAndStudy(studyEventId, studyWithSED);

        if (aeb.getId() <= 0) {
            addPageMessage(resexception.getString("begin_data_entry_without_event_but_especified_event"));
            throw new InconsistentStateException(Page.LIST_STUDY_SUBJECTS_SERVLET, resexception
                    .getString("begin_data_entry_without_event_but_especified_event"));
        }

        ecb = new EventCRFBean();
        if (eventCRFId == 0) {// no event CRF created yet
            ArrayList ecList = ecdao.findByEventSubjectVersion(sEvent, ssb, (CRFVersionBean) eb);
            if (ecList.size() > 0) {
                ecb = (EventCRFBean) ecList.get(0);
            } else {
                ecb.setAnnotations("");
                ecb.setCreatedDate(new Date());
                ecb.setCRFVersionId(crfVersionId);

                if (currentStudy.getStudyParameterConfig().getInterviewerNameDefault().equals("blank")) {
                    ecb.setInterviewerName("");
                } else {
                    // default will be event's owner name
                    ecb.setInterviewerName(sEvent.getOwner().getName());

                }
                if (!currentStudy.getStudyParameterConfig().getInterviewDateDefault().equals("blank")) {
                    if (sEvent.getDateStarted() != null) {
                        ecb.setDateInterviewed(sEvent.getDateStarted());// default
                        // date
                    } else {
                        // logger.trace("evnet start date is null, so date
                        // interviewed is null");
                        ecb.setDateInterviewed(null);
                    }
                } else {
                    ecb.setDateInterviewed(null);
                    // logger.trace("date interviewed is
                    // null,getInterviewDateDefault() is blank");
                }
                // ecb.setOwnerId(ub.getId());
                // above depreciated, try without it, tbh
                ecb.setOwner(ub);
                ecb.setStatus(Status.AVAILABLE);
                ecb.setCompletionStatusId(1);
                ecb.setStudySubjectId(ssb.getId());
                ecb.setStudyEventId(studyEventId);
                ecb.setValidateString("");
                ecb.setValidatorAnnotations("");

                ecb = (EventCRFBean) ecdao.create(ecb);
                logger.debug("*********CREATED EVENT CRF");
            }
        } else {
            // there is an event CRF already, only need to update
            ecb = (EventCRFBean) ecdao.findByPK(eventCRFId);
            ecb.setCRFVersionId(crfVersionId);
            ecb.setUpdatedDate(new Date());
            ecb.setUpdater(ub);
            ecb = updateECB(sEvent);
            ecb = (EventCRFBean) ecdao.update(ecb);
        }

        if (ecb.getId() <= 0) {
            addPageMessage(resexception.getString("new_event_CRF_not_created"));
            throw new InconsistentStateException(Page.LIST_STUDY_SUBJECTS_SERVLET, resexception.getString("new_event_CRF_not_created"));
        } else {
            if (sEvent.getSubjectEventStatus().equals(SubjectEventStatus.SIGNED)) {
                sEvent.setSubjectEventStatus(SubjectEventStatus.COMPLETED);
                sEvent.setUpdater(ub);
                sEvent.setUpdatedDate(new Date());
                sedao.update(sEvent);
            } else {
                sEvent.setSubjectEventStatus(SubjectEventStatus.DATA_ENTRY_STARTED);
                sEvent.setUpdater(ub);
                sEvent.setUpdatedDate(new Date());
                sedao.update(sEvent);
            }
        }

        return ecb;
    }

    /**
     * Read in form values and write them to a display item bean. Note that this results in the form value being written to both the response set bean and the
     * item data bean. The ResponseSetBean is used to display preset values on the form in the event of error, and the ItemDataBean is used to send values to
     * the database.
     *
     * @param dib
     *            The DisplayItemBean to write data into.
     * @return The DisplayItemBean, with form data loaded.
     */
    protected DisplayItemBean loadFormValue(DisplayItemBean dib) {
        String inputName = getInputName(dib);
        org.akaza.openclinica.bean.core.ResponseType rt = dib.getMetadata().getResponseSet().getResponseType();
        if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CHECKBOX) || rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECTMULTI)) {
            dib.loadFormValue(fp.getStringArray(inputName));
            // YW, 2-4-2008 << calculation result has been written in dib-data
        } else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CALCULATION)
            || rt.equals(org.akaza.openclinica.bean.core.ResponseType.GROUP_CALCULATION)) {
            dib.loadFormValue(dib.getData().getValue());
            ResponseOptionBean rob = (ResponseOptionBean) dib.getMetadata().getResponseSet().getOptions().get(0);
            logger.trace("test print of options for coding: " + rob.getValue());
            // YW >>
        } else {
            logger.trace("test print: " + inputName + ": " + fp.getString(inputName));
            dib.loadFormValue(fp.getString(inputName));
        }

        return dib;
    }

    /**
     * This methods will create an array of DisplayItemGroupBean, which contains multiple rows for an item group on the data entry form.
     *
     * @param digb
     *            The Item group which has multiple data rows
     * @param dbGroups
     *            The original array got from DB which contains multiple data rows
     * @param formGroups
     *            The new array got from front end which contains multiple data rows
     * @return new constructed formGroups, compare to dbGroups, some rows are update, some new ones are added and some are removed
     */
    protected List<DisplayItemGroupBean> loadFormValueForItemGroup(DisplayItemGroupBean digb, List<DisplayItemGroupBean> dbGroups,
            List<DisplayItemGroupBean> formGroups, int eventDefCRFId) {

        int repeatMax = digb.getGroupMetaBean().getRepeatMax();

        List<ItemBean> itBeans = idao.findAllItemsByGroupId(digb.getItemGroupBean().getId(), sb.getCRFVersionId());
        logger.debug("+++ starting to review groups: " + repeatMax);
        long timeCheck = System.currentTimeMillis();

        // adding this code from below, since we want to pass a null values list
        // in all cases of getDisplayBeansFromItems().
        // For adding null values to display items
        FormBeanUtil formBeanUtil = new FormBeanUtil();
        List<String> nullValuesList = new ArrayList<String>();
        // BWP>> Get a List<String> of any null values such as NA or NI
        // method returns null values as a List<String>
        nullValuesList = formBeanUtil.getNullValuesByEventCRFDefId(eventDefCRFId, sm.getDataSource());
        // >>BWP
        // logger.trace("+++ starting to review groups 2: " + repeatMax);
        long two = System.currentTimeMillis() - timeCheck;
        // logger.trace("time 2: " + two + "ms");
        // >>TBH below dual for loops need a breaker to avoid a performance hit
        int firstLoopBreak = 0;
        int secondLoopBreak = 0;
        for (int i = 0; i < repeatMax; i++) {
            DisplayItemGroupBean formGroup = new DisplayItemGroupBean();
            formGroup.setItemGroupBean(digb.getItemGroupBean());
            formGroup.setGroupMetaBean(runDynamicsCheck(digb.getGroupMetaBean()));

            ItemGroupBean igb = digb.getItemGroupBean();

            // want to do deep copy here, so always get a fresh copy for items,
            // may use other better way to do, like clone
            List<DisplayItemBean> dibs = FormBeanUtil.getDisplayBeansFromItems(itBeans, sm.getDataSource(), ecb, sb.getId(), nullValuesList, context);

            // get the values from the manually created rows first- not from the
            // rep model
            // second half of the if line is here to protect against looping tbh 01/2010
            // split the if loop into two parts, so that we can get what's existing first
            // and then get newly created rows later, tbh 01/2010
            if (fp.getStartsWith(igb.getOid() + "_manual" + i + "input")) {
                formGroup.setOrdinal(i);
                formGroup.setFormInputOrdinal(i);
                formGroup.setAuto(false);
                formGroup.setInputId(igb.getOid() + "_manual" + i);
                logger.debug("1: set auto to false for " + igb.getOid() + " " + i);

                dibs = processInputForGroupItem(fp, dibs, i, digb, false);

                formGroup.setItems(dibs);
                formGroups.add(formGroup);
            } else if (!StringUtil.isBlank(fp.getString(igb.getOid() + "_manual" + i + ".newRow"))) {
                // ||
                // (fp.getStartsWith(igb.getOid() + "_manual" + i + "input"))) {
                // the ordinal is the number got from [ ] and submitted by
                // repetition javascript
                formGroup.setOrdinal(i);
                formGroup.setFormInputOrdinal(i);
                formGroup.setInputId(igb.getOid() + "_manual" + i + ".newRow");

                formGroup.setAuto(false);

                logger.debug("2: set auto to false for " + igb.getOid() + " " + i);

                dibs = processInputForGroupItem(fp, dibs, i, digb, false);

                formGroup.setItems(dibs);
                formGroups.add(formGroup);

            } else {
                firstLoopBreak++;
            }
            if (firstLoopBreak > 14) {
                logger.debug("break first loop");
                break;
            }
        }// end of for (int i = 0; i < repeatMax; i++)
        // >>TBH remove the above eventually, repeat some work here?

        logger.trace("+++ starting to review groups 3: " + repeatMax);
        two = System.currentTimeMillis() - timeCheck;
        logger.trace("time 3: " + two + "ms");
        // >>TBH taking the nullvalues list out of the for loop, since it should
        // be the same for all display beans
        // nullValuesList = formBeanUtil.getNullValuesByEventCRFDefId(
        // eventDefCRFId,
        // sm.getDataSource());
        /*
         * logger.trace("+++ count for null values list: " + nullValuesList.size()); logger.trace(nullValuesList.toString() + " found with " + eventDefCRFId);
         */

        // had the call to form bean utils here, tbh
        for (int i = 0; i < repeatMax; i++) {
            DisplayItemGroupBean formGroup = new DisplayItemGroupBean();
            formGroup.setItemGroupBean(digb.getItemGroupBean());

            //            try {
            //                // set isShown here, tbh 04/2010
            //                boolean showGroup = getItemMetadataService().isGroupShown(digb.getGroupMetaBean().getId(), ecb);
            //                logger.debug("found show group for group meta bean " + digb.getGroupMetaBean().getId() + ": " + showGroup);
            //                if (showGroup) {
            //                    digb.getGroupMetaBean().setShowGroup(showGroup);
            //                    // we are only hiding, not showing (for now) tbh
            //                }
            //                // << tbh 04/2010
            //            } catch (OpenClinicaException oce) {
            //                // do nothing for right now, just store the bean
            //                logger.debug("throws an OCE for " + digb.getGroupMetaBean().getId());
            //            }
            formGroup.setGroupMetaBean(runDynamicsCheck(digb.getGroupMetaBean()));
            ItemGroupBean igb = digb.getItemGroupBean();
            // adding this code from below, since we want to pass a null values
            // list
            // in all cases of getDisplayBeansFromItems().
            // For adding null values to display items
            // FormBeanUtil formBeanUtil = new FormBeanUtil();
            // List<String> nullValuesList = new ArrayList<String>();
            // BWP>> Get a List<String> of any null values such as NA or NI
            // method returns null values as a List<String>

            // >>BWP
            // want to do deep copy here, so always get a fresh copy for items,
            // may use other better way to do, like clone

            // moved it back down here to fix another bug, tbh 12-3-2007
            List<DisplayItemBean> dibs = FormBeanUtil.getDisplayBeansFromItems(itBeans, sm.getDataSource(), ecb, sb.getId(), nullValuesList, context);
            logger.trace("+++count for dibs after deep copy: " + dibs.size());
            two = System.currentTimeMillis() - timeCheck;
            // logger.trace("time 3.dibs: " + two + "ms");
            // >>tbh

            // let's get the values from the rep model, which includes the first
            // row and any new rows
            // created by rep model(add button)
            if (fp.getStartsWith(igb.getOid() + "_" + i + "input")) {
                formGroup.setInputId(igb.getOid() + "_" + i);
                if (i == 0) {
                    formGroup.setOrdinal(i);// ordinal that will be saved into
                    // DB
                } else {
                    formGroup.setFormInputOrdinal(i);// from 0 again, the
                    // ordinal from front
                    // end page, needs to be
                    // reprocessed
                }
                formGroup.setAuto(true);
                logger.debug("1: set auto to TRUE for " + igb.getOid() + " " + i);
                dibs = processInputForGroupItem(fp, dibs, i, digb, true);
                logger.trace("+++ group ordinal: " + i + " igb name " + igb.getName());

                formGroup.setItems(dibs);
                formGroups.add(formGroup);
            } else if (!StringUtil.isBlank(fp.getString(igb.getOid() + "_" + i + ".newRow"))) {
                // || (fp.getStartsWith(igb.getOid() + "_" + i + "input"))) {
                // the ordinal is the number got from [ ] and submitted by
                // repetition javascript
                formGroup.setInputId(igb.getOid() + "_" + i);
                if (i == 0) {
                    formGroup.setOrdinal(i);// ordinal that will be saved into
                    // DB
                } else {
                    formGroup.setFormInputOrdinal(i);// from 0 again, the
                    // ordinal from front
                    // end page, needs to be
                    // reprocessed
                }
                // String fieldName = igb.getOid() + "_" + i + this.getInputName(dib);
                // if (!StringUtil.isBlank(fp.getString(fieldName))) {
                // if (i != repeatMax) {
                // formGroup.setAuto(false);
                // logger.debug("set auto to false for " + igb.getOid() + " " + i);
                // } else {
                formGroup.setAuto(true);

                logger.debug("2: set auto to TRUE for " + igb.getOid() + " " + i);

                dibs = processInputForGroupItem(fp, dibs, i, digb, true);
                logger.trace("+++ group ordinal: " + i + " igb name " + igb.getName());

                formGroup.setItems(dibs);
                formGroups.add(formGroup);
            } else {
                secondLoopBreak++;
            }
            if (secondLoopBreak > 14) {
                logger.debug("break second loop");
                break;
            }

        } // end of for (int i = 0; i < repeatMax; i++)
        logger.debug("first loop: " + firstLoopBreak);
        logger.debug("second loop: " + secondLoopBreak);
        logger.trace("+++ starting to review groups 4: " + repeatMax);
        two = System.currentTimeMillis() - timeCheck;
        logger.trace("time 4: " + two + "ms");
        // checks how many rows are manually created, not added by repetition
        // model

        int manualRows = getManualRows(formGroups);
        // for (int j = 0; j < formGroups.size(); j++) {
        // DisplayItemGroupBean formItemGroup = formGroups.get(j);
        // // logger.trace("begin formGroup Ordinal:" +
        // // formItemGroup.getOrdinal());
        // if (formItemGroup.isAuto() == false) {
        // manualRows = manualRows + 1;
        // }
        // }
        logger.debug(" manual rows " + manualRows + " formGroup size " + formGroups.size());

        request.setAttribute("manualRows", new Integer(manualRows));
        // reset ordinal for the auto-created rows except for the first row
        for (int j = 0; j < formGroups.size(); j++) {
            DisplayItemGroupBean formItemGroup = formGroups.get(j);
            if (formItemGroup.isAuto() && formItemGroup.getFormInputOrdinal() > 0) {
                logger.trace("+++ formInputOrdinal() " + formItemGroup.getFormInputOrdinal());
                // rows included in the model: first row, last existing row and
                // new rows
                // the rows in between are manually added

                // set the correct ordinal that will be saved into DB
                // the rows generated by model starts from 0, need to add the
                // number of manual rows to
                // get the correct ordinals, otherwise, we will have duplicate
                // ordinals like two ordinal 0
                formItemGroup.setOrdinal(formItemGroup.getFormInputOrdinal() + manualRows);
            }
        }
        logger.trace("+++ starting to review groups 5: " + repeatMax);
        two = System.currentTimeMillis() - timeCheck;
        logger.trace("time 5: " + two + "ms");
        Collections.sort(formGroups);// sort all the rows by ordinal

        logger.trace("group row size:" + formGroups.size());
        // suppose we have 3 rows of data from db, the orginal order is 0,1,2,
        // repetition model will submit row number in [ ] like the following:
        // 0,1,2.. consecutive numbers, means no row removed in between
        // 0,1,3,4.. the 2rd row is removed, 3 and 4 are new rows
        int previous = -1;
        for (int j = 0; j < formGroups.size(); j++) {
            DisplayItemGroupBean formItemGroup = formGroups.get(j);
            logger.trace("formGroup Ordinal:" + formItemGroup.getOrdinal());
            // logger.debug("=== formGroup Ordinal:" + formItemGroup.getOrdinal());
            // the below if loop addresses a specific problem with the repeating model
            // if we cut a row out of a long list, the repeater returns double ordinals of another row
            // and a second row gets deleted by mistake.
            // tbh 08/2009
            if (formItemGroup.getOrdinal() == previous) {
                logger.debug("found a match btw previous and ordinal");
                formItemGroup.setEditFlag("edit");
                formItemGroup.setOrdinal(previous + 1);
                // dbGroups.get(j).setEditFlag("edit");
            }
            // << tbh 08/2009
            if (formItemGroup.getOrdinal() > dbGroups.size() - 1) {
                formItemGroup.setEditFlag("add");
            } else {
                for (int i = 0; i < dbGroups.size(); i++) {
                    DisplayItemGroupBean dbItemGroup = dbGroups.get(i);
                    if (formItemGroup.getOrdinal() == i) {
                        // the first row is different, it could be blank on page
                        // just for
                        // display, so need to insert this row, not update
                        if ("initial".equalsIgnoreCase(dbItemGroup.getEditFlag())) {
                            formItemGroup.setEditFlag("add");
                        } else {
                            dbItemGroup.setEditFlag("edit");
                            // need to set up item data id in order to update
                            for (DisplayItemBean dib : dbItemGroup.getItems()) {
                                ItemDataBean data = dib.getData();
                                for (DisplayItemBean formDib : formItemGroup.getItems()) {
                                    if (formDib.getItem().getId() == dib.getItem().getId()) {
                                        formDib.getData().setId(data.getId());
                                        // this will save the data from IDE
                                        // complete, used only for DDE
                                        formDib.setDbData(dib.getData());
                                        // tbh removed below line so as not to
                                        // log so much, 112007
                                        logger.debug("+++ +++ form dib get data set id " + data.getId());
                                        break;
                                    }
                                }
                            }

                            formItemGroup.setEditFlag("edit");
                        }// else
                        break;
                    }
                }
            } // else
            previous = formItemGroup.getOrdinal();

        }

        logger.trace("+++ === DB group row:" + dbGroups.size());

        logger.trace("+++ === DB group contents: " + dbGroups.toString());

        // why do we need to remove this one row below?
        // For the existing rows in dbGroups,if cannot get the edit flag or
        // initial flag for a row,
        // it means the row is removed on the front-end, so the back-end servlet
        // cannot get it.-jxu
        for (int i = 0; i < dbGroups.size(); i++) {
            DisplayItemGroupBean dbItemGroup = dbGroups.get(i);
            logger.trace("+++ found edit flag of " + dbItemGroup.getEditFlag() + " for #" + dbItemGroup.getOrdinal());
            // logger.debug("+++ found edit flag of " + dbItemGroup.getEditFlag() + " for #" + dbItemGroup.getOrdinal() + ": " + i);
            if (!"edit".equalsIgnoreCase(dbItemGroup.getEditFlag()) && !"initial".equalsIgnoreCase(dbItemGroup.getEditFlag())) {
                // && !"".equalsIgnoreCase(dbItemGroup.getEditFlag())) {
                // >> tbh if the group is not shown, we should not touch it 05/2010
                if (dbItemGroup.getGroupMetaBean().isShowGroup()) {
                    logger.trace("+++ one row removed, edit flag was " + dbItemGroup.getEditFlag());
                    logger.debug("+++ one row removed, edit flag was " + dbItemGroup.getEditFlag());
                    dbItemGroup.setEditFlag("remove");
                }
                // << tbh
            }

        }
        logger.debug("+++ about to return form groups: " + formGroups.toString());
        for (int j = 0; j < formGroups.size(); j++) {
            DisplayItemGroupBean formGroup = formGroups.get(j);
            formGroup.setIndex(j);
        }
        return formGroups;
    }

    /**
     * @return <code>true</code> if processRequest should validate inputs when the user clicks the "Save" button, <code>false</code> otherwise.
     */
    protected abstract boolean validateInputOnFirstRound();

    /**
     * Validate the input from the form corresponding to the provided item. Implementing methods should load data from the form into the bean before validating.
     * The loadFormValue method should be used for this purpose.
     * <p/>
     * validateDisplayItemBeanText, validateDisplayItemBeanSingleCV, and validateDisplayItemBeanMultipleCV are provided to make implementing this method easy.
     *
     * @param v
     *            The Validator to add validations to.
     * @param dib
     *            The DisplayItemBean to validate.
     * @return The DisplayItemBean which is validated and has form values loaded into it.
     */
    protected abstract DisplayItemBean validateDisplayItemBean(DiscrepancyValidator v, DisplayItemBean dib, String inputName);

    protected void validateSCDItemBean(DiscrepancyValidator v, DisplayItemBean dib) {
        ItemFormMetadataBean ibMeta = dib.getMetadata();
        ItemDataBean idb = dib.getData();
        if (StringUtil.isBlank(idb.getValue())) {
            //if (ibMeta.isRequired() && showSCDItemIds.contains(ibMeta.getItemId())) {
            if (ibMeta.isRequired() && dib.getIsSCDtoBeShown()) {
                v.addValidation(this.getInputName(dib), Validator.IS_REQUIRED);
                /*if(dib.getIsSCDtoBeShown()) {
                    v.addValidation(this.getInputName(dib), Validator.IS_REQUIRED);
                } else {
                    validateShownSCDToBeHiddenSingle(v,dib);
                }*/
            }
        } else {
            validateShownSCDToBeHiddenSingle(v,dib);
        }
    }

    protected DisplayItemBean validateDisplayItemBean(DiscrepancyValidator v, DisplayItemBean dib, String inputName, RuleValidator rv,
            HashMap<String, ArrayList<String>> groupOrdinalPLusItemOid, Boolean fireRuleValidation, ArrayList<String> messages) {
        return dib;
    }

    protected abstract List<DisplayItemGroupBean> validateDisplayItemGroupBean(DiscrepancyValidator v, DisplayItemGroupBean dib,
            List<DisplayItemGroupBean> digbs, List<DisplayItemGroupBean> formGroups);

    protected List<DisplayItemGroupBean> validateDisplayItemGroupBean(DiscrepancyValidator v, DisplayItemGroupBean dib, List<DisplayItemGroupBean> digbs,
            List<DisplayItemGroupBean> formGroups, RuleValidator rv, HashMap<String, ArrayList<String>> groupOrdinalPLusItemOid) {
        return digbs;
    }

    /*
     * function written out here to return itemMetadataGroupBeans after they have been checked for show/hide via dynamics.
     * author: tbh 04/2010
     *
     */
    private ItemGroupMetadataBean runDynamicsCheck(ItemGroupMetadataBean metadataBean) {
        try {
            if (!metadataBean.isShowGroup()) {
                // set isShown here, tbh 04/2010
                boolean showGroup = getItemMetadataService().isGroupShown(metadataBean.getId(), ecb);
                // logger.debug("found show group for group meta bean " + metadataBean.getId() + ": " + 
                //        metadataBean.getItemGroupId() + 
                //        ": " + ecb.getId() + ": " + showGroup);

                if (getServletPage().equals(Page.DOUBLE_DATA_ENTRY_SERVLET)) {
                    showGroup = getItemMetadataService().hasGroupPassedDDE(metadataBean.getId(), ecb.getId());
                }
                metadataBean.setShowGroup(showGroup);
                // what about the items which should be shown?
                // if (getServletPage().equals(Page.ADMIN_EDIT_SERVLET) && metadataBean.isShowGroup()) {
                //    metadataBean.setHighlighted(true);
                // }
                // sets highlighting for AE, tbh 05/2010
                // unset highlighting for admin editing, tbh 06/2010
            }
            // << tbh 04/2010
        } catch (OpenClinicaException oce) {
            // do nothing for right now, just store the bean
            logger.debug("throws an OCE for " + metadataBean.getId());
        }
        return metadataBean;
    }

    private DisplayItemBean runDynamicsItemCheck(DisplayItemBean dib) {
        try {
            // System.out.println("trying run dynamics item check: item id " + dib.getItem().getId() + " item data id " + dib.getData().getId());
            if (!dib.getMetadata().isShowItem()) {
                boolean showItem = getItemMetadataService().isShown(dib.getItem().getId(), ecb, dib.getData());
                dib.getMetadata().setShowItem(showItem);
                // System.out.println("returning " + showItem);
            }
        } catch (NullPointerException npe) {
            logger.debug("found NPE! item id " + dib.getItem().getId());
        }

        return dib;

    }

    /*
     * Perform validation for calculation and group-calculation type. <br> Pre-condition: passed DisplayItemBean parameter has been loaded with value. @param sv
     * @param dib @param inputName @return dib @author ywang (Feb.,2008)
     */
    protected DisplayItemBean validateCalcTypeDisplayItemBean(ScoreItemValidator sv, DisplayItemBean dib, String inputName) {

        dib = validateDisplayItemBeanText(sv, dib, inputName);

        return dib;
    }

    /**
     * Peform validation on a item which has a TEXT or TEXTAREA response type. If the item has a null value, it's automatically validated. Otherwise, it's
     * checked against its data type.
     *
     * @param v
     *            The Validator to add validations to.
     * @param dib
     *            The DisplayItemBean to validate.
     * @return The DisplayItemBean which is validated.
     */
    protected DisplayItemBean validateDisplayItemBeanText(DiscrepancyValidator v, DisplayItemBean dib, String inputName) {

        if (StringUtil.isBlank(inputName)) {// for single items
            inputName = getInputName(dib);
        }
        ItemBean ib = dib.getItem();
        ItemFormMetadataBean ibMeta = dib.getMetadata();
        ItemDataType idt = ib.getDataType();
        ItemDataBean idb = dib.getData();

        boolean isNull = false;
        ArrayList nullValues = edcb.getNullValuesList();
        for (int i = 0; i < nullValues.size(); i++) {
            NullValue nv = (NullValue) nullValues.get(i);
            if (nv.getName().equals(fp.getString(inputName))) {
                isNull = true;
            }
        }

        if (!isNull) {
            if (StringUtil.isBlank(idb.getValue())) {
                // check required first
                if (ibMeta.isRequired() && ibMeta.isShowItem()) {
                    v.addValidation(inputName, Validator.IS_REQUIRED);
                }
            } else {

                if (idt.equals(ItemDataType.ST)) {
                    // a string's size could be more than 255, which is more
                    // than
                    // the db field length
                    v.addValidation(inputName, Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);

                } else if (idt.equals(ItemDataType.INTEGER)) {
                    v.addValidation(inputName, Validator.IS_AN_INTEGER);
                    v.alwaysExecuteLastValidation(inputName);

                } else if (idt.equals(ItemDataType.REAL)) {

                    v.addValidation(inputName, Validator.IS_A_NUMBER);
                    v.alwaysExecuteLastValidation(inputName);
                } else if (idt.equals(ItemDataType.BL)) {
                    // there is no validation here since this data type is
                    // explicitly
                    // allowed to be null
                    // if the string input for this field parses to a non-zero
                    // number, the
                    // value will be true; otherwise, 0
                } else if (idt.equals(ItemDataType.BN)) {

                } else if (idt.equals(ItemDataType.SET)) {
                    // v.addValidation(inputName, Validator.NO_BLANKS_SET);
                    v.addValidation(inputName, Validator.IN_RESPONSE_SET_SINGLE_VALUE, dib.getMetadata().getResponseSet());
                } else if (idt.equals(ItemDataType.DATE)) {
                    v.addValidation(inputName, Validator.IS_A_DATE);
                    v.alwaysExecuteLastValidation(inputName);
                } else if (idt.equals(ItemDataType.PDATE)) {
                    v.addValidation(inputName, Validator.IS_PARTIAL_DATE);
                    v.alwaysExecuteLastValidation(inputName);
                }
                if (ibMeta.getWidthDecimal().length() > 0) {
                    ArrayList<String> params = new ArrayList<String>();
                    params.add(0, idt.getName());
                    params.add(1, ibMeta.getWidthDecimal());
                    v.addValidation(inputName, Validator.IS_VALID_WIDTH_DECIMAL, params);
                    v.alwaysExecuteLastValidation(inputName);
                }
                // >> tbh 4/30/2010 #4963 removing custom validations firing during AE

                customValidation(v, dib, inputName);

                /*
                 * if (!StringUtil.isBlank(customValidationString)) { Validation customValidation = null; if (customValidationString.startsWith("func:")) { try
                 * { customValidation = Validator.processCRFValidationFunction(customValidationString ); } catch (Exception e) { e.printStackTrace(); } } else
                 * if (customValidationString.startsWith("regexp:")) { try { customValidation = Validator.processCRFValidationRegex(customValidationString); }
                 * catch (Exception e) { } } if (customValidation != null) { customValidation .setErrorMessage(dib.getMetadata().getRegexpErrorMsg());
                 * v.addValidation(inputName, customValidation); } }
                 */
            }
        }
        return dib;
    }

    protected DisplayItemBean validateDisplayItemBeanSingleCV(RuleValidator v, DisplayItemBean dib, String inputName, ArrayList<String> messages) {
        if (StringUtil.isBlank(inputName)) {
            inputName = getInputName(dib);
        }
        ItemFormMetadataBean ibMeta = dib.getMetadata();
        ItemDataBean idb = dib.getData();
        if (StringUtil.isBlank(idb.getValue())) {
            if (ibMeta.isRequired() && ibMeta.isShowItem()) {
                v.addValidation(inputName, Validator.IS_REQUIRED);
            }
            v.addValidation(inputName, Validator.IS_AN_RULE, messages);
        } else {
            v.addValidation(inputName, Validator.IS_AN_RULE, messages);
        }
        // customValidation(v, dib, inputName);
        return dib;
    }

    /**
     * Peform validation on a item which has a RADIO or SINGLESELECTresponse type. This function checks that the input isn't blank, and that its value comes
     * from the controlled vocabulary (ResponseSetBean) in the DisplayItemBean.
     *
     * @param v
     *            The Validator to add validations to.
     * @param dib
     *            The DisplayItemBean to validate.
     * @return The DisplayItemBean which is validated.
     */
    protected DisplayItemBean validateDisplayItemBeanSingleCV(DiscrepancyValidator v, DisplayItemBean dib, String inputName) {
        if (StringUtil.isBlank(inputName)) {
            inputName = getInputName(dib);
        }
        ItemFormMetadataBean ibMeta = dib.getMetadata();
        ItemDataBean idb = dib.getData();
        if (StringUtil.isBlank(idb.getValue())) {
            if (ibMeta.isRequired() && ibMeta.isShowItem()) {
                v.addValidation(inputName, Validator.IS_REQUIRED);
            }
        } else {
            v.addValidation(inputName, Validator.IN_RESPONSE_SET_SINGLE_VALUE, dib.getMetadata().getResponseSet());
        }
        customValidation(v, dib, inputName);
        return dib;
    }

    //validate simple-conditional-display item to be changed from shown to hidden
    protected void validateShownSCDToBeHiddenSingle(DiscrepancyValidator v, DisplayItemBean dib) {
        ItemFormMetadataBean ifmb = dib.getMetadata();
        boolean hasDN = dib.getDiscrepancyNotes() != null && dib.getDiscrepancyNotes().size()>0 ? true : false;
        if(SimpleConditionalDisplayPair.isCurrentShownItem(dib.getData()) && !dib.getIsSCDtoBeShown() && !hasDN) {
            String message = ifmb.getConditionalDisplay().replaceAll("\\\\,", "##").split(",")[2].trim();
            message = message.replaceAll("##", "\\,");
            Validation vl = new Validation(Validator.TO_HIDE_CONDITIONAL_DISPLAY);
            vl.setErrorMessage(message);
            v.addValidation(getInputName(dib), vl);
        }
    }

    /**
     * Peform validation on a item which has a RADIO or SINGLESELECT response type. This function checks that the input isn't blank, and that its value comes
     * from the controlled vocabulary (ResponseSetBean) in the DisplayItemBean.
     *
     * @param v
     *            The Validator to add validations to.
     * @param dib
     *            The DisplayItemBean to validate.
     * @return The DisplayItemBean which is validated.
     */
    protected DisplayItemBean validateDisplayItemBeanMultipleCV(DiscrepancyValidator v, DisplayItemBean dib, String inputName) {
        if (StringUtil.isBlank(inputName)) {
            inputName = getInputName(dib);
        }
        ItemFormMetadataBean ibMeta = dib.getMetadata();
        ItemDataBean idb = dib.getData();
        if (StringUtil.isBlank(idb.getValue())) {
            if (ibMeta.isRequired() && ibMeta.isShowItem()) {
                v.addValidation(inputName, Validator.IS_REQUIRED);
            }
        } else {
            v.addValidation(inputName, Validator.IN_RESPONSE_SET, dib.getMetadata().getResponseSet());
        }
        customValidation(v, dib, inputName);
        return dib;
    }

    /**
     * @param dib
     *            A DisplayItemBean representing an input on the CRF.
     * @return The name of the input in the HTML form.
     */
    public final String getInputName(DisplayItemBean dib) {
        ItemBean ib = dib.getItem();
        String inputName = "input" + ib.getId();
        return inputName;
    }

    /**
     * Creates an input name for an item data entry in an item group
     *
     * @param digb
     * @param ordinal
     * @param dib
     * @return
     */
    public final String getGroupItemInputName(DisplayItemGroupBean digb, int rowCount, int manualRows, DisplayItemBean dib) {
        int ordinal = rowCount - manualRows;
        String inputName = digb.getItemGroupBean().getOid() + "_" + ordinal + getInputName(dib);
        logger.debug("===returning: " + inputName);
        return inputName;
    }

    public final String getGroupItemInputName(DisplayItemGroupBean digb, int ordinal, DisplayItemBean dib) {
        String inputName = digb.getItemGroupBean().getOid() + "_" + ordinal + getInputName(dib);
        logger.debug("+++returning: " + inputName);
        return inputName;
    }

    /**
     * Writes data from the DisplayItemBean to the database. Note that if the bean contains an inactive ItemDataBean, the ItemDataBean is created; otherwise,
     * the ItemDataBean is updated.
     *
     * @param dib
     *            The DisplayItemBean from which to write data.
     * @param iddao
     *            The DAO to use to access the database.
     * @return <code>true</code> if the query succeeded, <code>false</code> otherwise.
     */
    protected boolean writeToDB(DisplayItemBean dib, ItemDataDAO iddao, int ordinal) {
        ItemDataBean idb = dib.getData();
        if (getServletPage().equals(Page.DOUBLE_DATA_ENTRY_SERVLET)) {
        	if (!dib.getMetadata().isShowItem() &&
        			idb.getValue().equals("") &&
        			!getItemMetadataService().hasPassedDDE(dib.getMetadata(), ecb, idb)) {//(dib.getItem().getId(), ecb, idb)) {// && !getItemMetadataService().isShown(dib.getItem().getId(), ecb, dib.getData())) {
        		logger.debug("*** not shown - not writing for idb id " + dib.getData().getId() + " and item id " + dib.getItem().getId());
        		return true;
        	}
        } else {
        	if (!dib.getMetadata().isShowItem() &&
            		idb.getValue().equals("") &&
            		!getItemMetadataService().isShown(dib.getItem().getId(), ecb, dib.getData())) {
                logger.debug("*** not shown - not writing for idb id " + dib.getData().getId() + " and item id " + dib.getItem().getId());
                return true;
            }
        }

        if (idb.getValue().equals("")) {
            idb.setStatus(getBlankItemStatus());
        } else {
            idb.setStatus(getNonBlankItemStatus());
        }
        return writeToDB(idb,dib,iddao,ordinal);
    }

    protected boolean writeConditionalDisplayItemToDB(DisplayItemBean dib, ItemDataDAO iddao, int ordinal, boolean isDisplay) {
        ItemDataBean idb = dib.getData();
        ItemDataBean dbdata = dib.getDbData();
        boolean display = isDisplay ? true : false;
        if(!display && idb.getId()>0) {
            Status s = dbdata.getStatus();
            int id = s == null ? -1 : s.getId();
            display = id>0 && (id!=5 || id!=7)? dbdata.getValue().length()>0?true:dib.getNumDiscrepancyNotes()>0?true:false : false;
        }
        if(display) {
            if (idb.getValue().equals("")) {
                idb.setStatus(getBlankItemStatus());
            } else {
                idb.setStatus(getNonBlankItemStatus());
            }
            return this.writeToDB(idb, dib, iddao, ordinal);
        } else if(idb.getId()>0){
            idb.setStatus(Status.DELETED);
            idb.setUpdater(ub);
            iddao.updateValue(idb);
            return idb.isActive();
        }
        //if no need written to database, back with true
        return true;
    }

    protected boolean writeToDB(ItemDataBean itemData, DisplayItemBean dib, ItemDataDAO iddao, int ordinal) {
        ItemDataBean idb = itemData;

        idb.setItemId(dib.getItem().getId());
        idb.setEventCRFId(ecb.getId());

        if (StringUtil.isBlank(dib.getEditFlag())) {

            if (!idb.isActive()) {
                // will this need to change for double data entry?
                idb.setOrdinal(ordinal);
                idb.setCreatedDate(new Date());
                idb.setOwner(ub);
                idb = (ItemDataBean) iddao.create(idb);
            } else {
                idb.setUpdater(ub);
                // tbh 5990: should we update the logic here for nonrepeats?
                // System.out.println("string util is blank: update an item data " + idb.getId() + " :" + idb.getValue());
                logger.info("update item update_id " + idb.getUpdater().getId());
                idb = (ItemDataBean) iddao.updateValue(idb);
            }
        } else {
            // for the items in group, they have editFlag
            if ("add".equalsIgnoreCase(dib.getEditFlag())) {
                idb.setOrdinal(ordinal);
                idb.setCreatedDate(new Date());
                idb.setOwner(ub);
                logger.debug("create a new item data" + idb.getItemId() + idb.getValue());
                // idb = (ItemDataBean) iddao.create(idb);
                // >>tbh 08/2008
                idb.setUpdater(ub);
                // idb = (ItemDataBean) iddao.updateValue(idb);
                // instead of two hits to the db, we perform an 'upsert'
                // combining them into one
                idb = (ItemDataBean) iddao.upsert(idb);
                // <<tbh
            } else if ("edit".equalsIgnoreCase(dib.getEditFlag())) {
  				idb.setUpdater(ub);
                
                // System.out.println("update an item data - running update value " + idb.getId() + " :" + idb.getValue());
                logger.info("update item update_id " + idb.getUpdater().getId());
                // update tbh #5999, #5998; if an item_data was not included in 
                // an import data, it won't exist; we need to check on item_data_id
                // to make sure we are running the correct command on the db
                if (idb.getId() != 0) {
                    idb.setUpdatedDate(new Date());
                    idb = (ItemDataBean) iddao.updateValue(idb);
                } else {
                    idb.setCreatedDate(new Date());
                    idb.setOrdinal(ordinal);
                    idb.setOwner(ub);
                    idb = (ItemDataBean) iddao.upsert(idb);
                    logger.debug("just ran upsert! " + idb.getId());
                }

            } else if ("remove".equalsIgnoreCase(dib.getEditFlag())) {
                logger.debug("REMOVE an item data" + idb.getItemId() + idb.getValue());
                idb.setUpdater(ub);
                idb.setStatus(Status.DELETED);
                idb = (ItemDataBean) iddao.updateValue(idb);

                DiscrepancyNoteDAO dnDao = new DiscrepancyNoteDAO(sm.getDataSource());
                List dnNotesOfRemovedItem = dnDao.findExistingNotesForItemData(idb.getId());
                if (!dnNotesOfRemovedItem.isEmpty()) {
                    DiscrepancyNoteBean itemParentNote = null;
                    for (Object obj : dnNotesOfRemovedItem) {
                        if (((DiscrepancyNoteBean) obj).getParentDnId() == 0) {
                            itemParentNote = (DiscrepancyNoteBean) obj;
                        }
                    }
                    DiscrepancyNoteBean dnb = new DiscrepancyNoteBean();
                    if (itemParentNote != null) {
                        dnb.setParentDnId(itemParentNote.getId());
                        dnb.setDiscrepancyNoteTypeId(itemParentNote.getDiscrepancyNoteTypeId());
                    }
                    dnb.setResolutionStatusId(ResolutionStatus.CLOSED.getId());
                    dnb.setStudyId(currentStudy.getId());
                    dnb.setAssignedUserId(ub.getId());
                    dnb.setOwner(ub);
                    dnb.setEntityType(DiscrepancyNoteBean.ITEM_DATA);
                    dnb.setEntityId(idb.getId());
                    dnb.setCreatedDate(new Date());
                    dnb.setColumn("value");
                    dnb.setDescription("The item has been removed, this Discrepancy Note has been Closed.");
                    dnDao.create(dnb);
                    dnDao.createMapping(dnb);
                    itemParentNote.setResolutionStatusId(ResolutionStatus.CLOSED.getId());
                    dnDao.update(itemParentNote);
                }
            }

        }

        return idb.isActive();
    }

    protected String addAttachedFilePath(DisplayItemBean dib, String attachedFilePath) {
        String fileName = "";
        ItemDataBean idb = dib.getData();
        String itemDataValue = idb.getValue();
        String dbValue = dib.getDbData().getValue();
        ResponseSetBean rsb = dib.getMetadata().getResponseSet();
        org.akaza.openclinica.bean.core.ResponseType rt = rsb.getResponseType();
        if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.FILE) && itemDataValue.length() > 0) {
            File file = new File(itemDataValue);
            fileName = file.getName();
            if (itemDataValue.length() > fileName.length()) {
                // itemDataValue includes file path
                idb.setValue(itemDataValue);
            } else {
                File f = new File(dbValue);
                fileName = f.getName();
                if (fileName.equals(itemDataValue) && dbValue.length()>itemDataValue.length()) {
                // since filename is unique by timestamp, re-upload will
                // append
                // another timestamp to the same filename
                    idb.setValue(dbValue);
                } else {
                    idb.setValue(attachedFilePath + itemDataValue);
                    fileName = itemDataValue;
                }
            }
        }
        return fileName;
    }

    /**
     * Retrieve the status which should be assigned to ItemDataBeans which have blank values for this data entry servlet.
     */
    protected abstract Status getBlankItemStatus();

    // unavailable in admin. editing

    /**
     * Retrieve the status which should be assigned to ItemDataBeans which have non-blank values for this data entry servlet.
     */
    protected abstract Status getNonBlankItemStatus();

    // unavailable in admin. editing

    /**
     * Get the eventCRF's annotations as appropriate for this data entry servlet.
     */
    protected abstract String getEventCRFAnnotations();

    /**
     * Set the eventCRF's annotations properties as appropriate for this data entry servlet.
     */
    protected abstract void setEventCRFAnnotations(String annotations);

    /**
     * Retrieve the DisplaySectionBean which will be used to display the Event CRF Section on the JSP, and also is used to controll processRequest.
     */
    protected DisplaySectionBean getDisplayBean(boolean hasGroup, boolean includeUngroupedItems) throws Exception {
        DisplaySectionBean section = new DisplaySectionBean();
        FormProcessor fp = new FormProcessor(request);
        StudyBean study = (StudyBean) session.getAttribute("study");

        // Find out whether there are ungrouped items in this section
        boolean hasUngroupedItems = false;
        int eventDefinitionCRFId = fp.getInt("eventDefinitionCRFId");

        if (eventDefinitionCRFId <= 0) { // TODO: this block of code repeats
            // many times, need to clean up
            edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
            EventDefinitionCRFBean edcBean = edcdao.findByStudyEventIdAndCRFVersionId(study, ecb.getStudyEventId(), ecb.getCRFVersionId());
            eventDefinitionCRFId = edcBean.getId();
        }
        logger.trace("eventDefinitionCRFId " + eventDefinitionCRFId);
        // Use this class to find out whether there are ungrouped items in this
        // section
        FormBeanUtil formBeanUtil = new FormBeanUtil();
        List<DisplayItemGroupBean> itemGroups = new ArrayList<DisplayItemGroupBean>();
        if (hasGroup) {
            DisplaySectionBean newDisplayBean = new DisplaySectionBean();
            if (includeUngroupedItems) {
                // Null values: this method adds null values to the
                // displayitembeans
                newDisplayBean =
                    formBeanUtil.createDisplaySectionBWithFormGroups(sb.getId(), ecb.getCRFVersionId(), sm.getDataSource(), eventDefinitionCRFId, ecb, context);
            } else {
                newDisplayBean =
                    formBeanUtil.createDisplaySectionWithItemGroups(study, sb.getId(), ecb, ecb.getStudyEventId(), sm, eventDefinitionCRFId, context);
            }
            itemGroups = newDisplayBean.getDisplayFormGroups();
            // setDataForDisplayItemGroups(itemGroups, sb,ecb,sm);
            logger.trace("found item group size: " + itemGroups.size() + " and to string: " + itemGroups.toString());
            section.setDisplayFormGroups(itemGroups);

        }

        // Find out whether any display items are *not* grouped; see issue 1689
        hasUngroupedItems = formBeanUtil.sectionHasUngroupedItems(sm.getDataSource(), sb.getId(), itemGroups);
        sdao = new SectionDAO(sm.getDataSource());
        sb.setHasSCDItem(hasUngroupedItems?this.sdao.hasSCDItem(sb.getId()):false);

        section.setEventCRF(ecb);

        if (sb.getParentId() > 0) {
            SectionBean parent = (SectionBean) sdao.findByPK(sb.getParentId());
            sb.setParent(parent);
        }

        section.setSection(sb);

        CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
        CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(ecb.getCRFVersionId());
        section.setCrfVersion(cvb);

        CRFDAO cdao = new CRFDAO(sm.getDataSource());
        CRFBean cb = (CRFBean) cdao.findByPK(cvb.getCrfId());
        section.setCrf(cb);

        edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
        // EventDefinitionCRFBean edcb =
        // edcdao.findByStudyEventIdAndCRFVersionId(study,
        // ecb.getStudyEventId(), cvb.getId());
        section.setEventDefinitionCRF(edcb);

        // setup DAO's here to avoid creating too many objects
        idao = new ItemDAO(sm.getDataSource());
        ifmdao = new ItemFormMetadataDAO(sm.getDataSource());
        iddao = new ItemDataDAO(sm.getDataSource());

        // Use itemGroups to determine if there are any ungrouped items

        // get all the parent display item beans not in group
        ArrayList displayItems = getParentDisplayItems(hasGroup, sb, edcb, idao, ifmdao, iddao, hasUngroupedItems);
        logger.debug("just ran get parent display, has group " + hasGroup + " has ungrouped " + hasUngroupedItems);
        // now sort them by ordinal
        Collections.sort(displayItems);

        // now get the child DisplayItemBeans
        for (int i = 0; i < displayItems.size(); i++) {
            DisplayItemBean dib = (DisplayItemBean) displayItems.get(i);
            dib.setChildren(getChildrenDisplayItems(dib, edcb));

            // TODO use the setData command here to make sure we get a value?
            //On Submition of the Admin Editing form the loadDBValue does not required
            //
            if (ecb.getStage() == DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE || ecb.getStage() == DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE) {
                if (shouldLoadDBValues(dib) && !isSubmitted) {
                    dib.loadDBValue();
                }
            } else {
                if (shouldLoadDBValues(dib)) {
                    logger.trace("should load db values is true, set value");
                    dib.loadDBValue();
                    logger.trace("just got data loaded: " + dib.getData().getValue());
                }
            }

            displayItems.set(i, dib);
        }

        if(sb.hasSCDItem()) {
            Map<String,DisplayItemBean> displayItemMap = new HashMap<String,DisplayItemBean>();
            for(int i=0; i<displayItems.size(); ++i) {
                displayItemMap.put(((DisplayItemBean)displayItems.get(i)).getItem().getName(),(DisplayItemBean)displayItems.get(i));
            }
            List<ItemFormMetadataBean> sectionSCDMeta = ifmdao.findSCDItemsBySectionId(sb.getId());
            Map<String,String> childParentNameMap = idao.mapAllChildAndParentNameInSection(sb.getId());
            displayItems = this.gainConditionalDisplayPairs(displayItemMap, sectionSCDMeta, childParentNameMap);

        }

        section.setItems(displayItems);

        return section;
    }

    /**
     * Retrieve the DisplaySectionBean which will be used to display the Event CRF Section on the JSP, and also is used to controll processRequest.
     */
    protected ArrayList getAllDisplayBeans() throws Exception {

        ArrayList sections = new ArrayList();
        StudyBean study = (StudyBean) session.getAttribute("study");

        for (int j = 0; j < allSectionBeans.size(); j++) {

            SectionBean sb = allSectionBeans.get(j);

            DisplaySectionBean section = new DisplaySectionBean();
            section.setEventCRF(ecb);

            if (sb.getParentId() > 0) {
                SectionBean parent = (SectionBean) sdao.findByPK(sb.getParentId());
                sb.setParent(parent);
            }

            section.setSection(sb);

            CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
            CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(ecb.getCRFVersionId());
            section.setCrfVersion(cvb);

            CRFDAO cdao = new CRFDAO(sm.getDataSource());
            CRFBean cb = (CRFBean) cdao.findByPK(cvb.getCrfId());
            section.setCrf(cb);

            edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
            edcb = edcdao.findByStudyEventIdAndCRFVersionId(study, ecb.getStudyEventId(), cvb.getId());
            section.setEventDefinitionCRF(edcb);

            // setup DAO's here to avoid creating too many objects
            idao = new ItemDAO(sm.getDataSource());
            ifmdao = new ItemFormMetadataDAO(sm.getDataSource());
            iddao = new ItemDataDAO(sm.getDataSource());

            // get all the display item beans
            ArrayList displayItems = getParentDisplayItems(false, sb, edcb, idao, ifmdao, iddao, false);

            logger.debug("222 just ran get parent display, has group " + " FALSE has ungrouped FALSE");
            // now sort them by ordinal
            Collections.sort(displayItems);

            // now get the child DisplayItemBeans
            for (int i = 0; i < displayItems.size(); i++) {
                DisplayItemBean dib = (DisplayItemBean) displayItems.get(i);
                dib.setChildren(getChildrenDisplayItems(dib, edcb));

                if (shouldLoadDBValues(dib)) {
                    logger.trace("should load db values is true, set value");
                    dib.loadDBValue();
                }

                displayItems.set(i, dib);
            }

            section.setItems(displayItems);
            sections.add(section);

        }
        return sections;
    }

    /**
     * For each single item in this section which is a parent, get a DisplayItemBean corresponding to that item. Note that an item is a parent iff its parentId
     * == 0.
     *
     * @param sb
     *            The section whose items we are retrieving.
     * @param hasUngroupedItems
     * @return An array of DisplayItemBean objects, one per parent item in the section. Note that there is no guarantee on the ordering of the objects.
     * @throws Exception
     */
    private ArrayList getParentDisplayItems(boolean hasGroup, SectionBean sb, EventDefinitionCRFBean edcb, ItemDAO idao, ItemFormMetadataDAO ifmdao,
            ItemDataDAO iddao, boolean hasUngroupedItems) throws Exception {

        ArrayList answer = new ArrayList();

        // DisplayItemBean objects are composed of an ItemBean, ItemDataBean and
        // ItemFormDataBean.
        // However the DAOs only provide methods to retrieve one type of bean at
        // a
        // time (per section)
        // the displayItems hashmap allows us to compose these beans into
        // DisplayItemBean objects,
        // while hitting the database only three times
        HashMap displayItems = new HashMap();

        // ArrayList items = idao.findAllParentsBySectionId(sb.getId());

        ArrayList items = new ArrayList();
        if (hasGroup) {
            // issue 1689: this method causes problems with items that have
            // been defined as grouped, then redefined as ungrouped; thus it
            // is "checked twice" with hasUngroupedItems.
            // See: FormBeanUtil.sectionHasUngroupedItems.
            if (hasUngroupedItems) {
                items = idao.findAllUngroupedParentsBySectionId(sb.getId(), sb.getCRFVersionId());
            }
            // however, if we have true:true, we exclude all grouped items
            //            items.addAll(
            //            		idao.findAllGroupedParentsBySectionId(
            //            				sb.getId(), sb.getCRFVersionId()));
        } else {
            logger.trace("no item groups");
            items = idao.findAllParentsBySectionId(sb.getId());
        }
        logger.debug("items size" + items.size());
        for (int i = 0; i < items.size(); i++) {
            DisplayItemBean dib = new DisplayItemBean();
            dib.setEventDefinitionCRF(edcb);
            ItemBean ib = (ItemBean) items.get(i);
            dib.setItem(ib);
            displayItems.put(new Integer(dib.getItem().getId()), dib);
        }

        ArrayList data = iddao.findAllBySectionIdAndEventCRFId(sb.getId(), ecb.getId());
        for (int i = 0; i < data.size(); i++) {
            ItemDataBean idb = (ItemDataBean) data.get(i);
            DisplayItemBean dib = (DisplayItemBean) displayItems.get(new Integer(idb.getItemId()));
            if (dib != null) {
                dib.setData(idb);
                displayItems.put(new Integer(idb.getItemId()), dib);
            }
        }

        ArrayList metadata = ifmdao.findAllBySectionId(sb.getId());
        for (int i = 0; i < metadata.size(); i++) {
            ItemFormMetadataBean ifmb = (ItemFormMetadataBean) metadata.get(i);
            DisplayItemBean dib = (DisplayItemBean) displayItems.get(new Integer(ifmb.getItemId()));
            if (dib != null) {
                // boolean showItem = false;
                boolean needsHighlighting = !ifmb.isShowItem();
                boolean showItem = getItemMetadataService().isShown(ifmb.getItemId(), ecb, dib.getData());
                if (getServletPage().equals(Page.DOUBLE_DATA_ENTRY_SERVLET)) {
                    showItem = getItemMetadataService().hasPassedDDE(ifmb, ecb, dib.getData());
                }
                // is the above needed for children items too?
                boolean passedDDE = getItemMetadataService().hasPassedDDE(ifmb, ecb, dib.getData());
                if (showItem) { // we are only showing, not hiding
                    logger.debug("set show item " + ifmb.getItemId() + " idb " + dib.getData().getId() + " show item " + showItem + " passed dde " + passedDDE);
                    // ifmb.setShowItem(showItem);
                    ifmb.setShowItem(true);
                } else {
                    logger.debug("DID NOT set show item " + ifmb.getItemId() + " idb " + dib.getData().getId() + " show item " + showItem + " passed dde "
                        + passedDDE + " value " + dib.getData().getValue());
                }
                // now set highlighting for admin entry only
                //                if (getServletPage().equals(Page.ADMIN_EDIT_SERVLET)) {
                //                    if (needsHighlighting && ifmb.isShowItem()) {
                //                        // that is, if it was not shown but now is shown ...
                //                        ifmb.setHighlighted(true);
                //                        logger.debug("set highlighted to true");
                //                    }
                //                }
                // << tbh 06/2010
                // TODO child items
                // logger.debug("did not catch NPE 1");
                dib.setMetadata(ifmb);
                displayItems.put(new Integer(ifmb.getItemId()), dib);
            }
        }

        Iterator hmIt = displayItems.keySet().iterator();
        while (hmIt.hasNext()) {
            Integer key = (Integer) hmIt.next();
            DisplayItemBean dib = (DisplayItemBean) displayItems.get(key);
            answer.add(dib);
            logger.debug("*** getting with key: " + key + " display item bean with value: " + dib.getData().getValue());
        }
        logger.debug("*** test of the display items: " + displayItems.toString());

        return answer;
    }

    /**
     * Get the DisplayItemBean objects corresponding to the items which are children of the specified parent.
     *
     * @param parent
     *            The item whose children are to be retrieved.
     * @return An array of DisplayItemBean objects corresponding to the items which are children of parent, and are sorted by column number (ascending), then
     *         ordinal (ascending).
     */
    private ArrayList getChildrenDisplayItems(DisplayItemBean parent, EventDefinitionCRFBean edcb) {
        ArrayList answer = new ArrayList();

        int parentId = parent.getItem().getId();
        ArrayList childItemBeans = idao.findAllByParentIdAndCRFVersionId(parentId, ecb.getCRFVersionId());

        for (int i = 0; i < childItemBeans.size(); i++) {
            ItemBean child = (ItemBean) childItemBeans.get(i);
            ItemDataBean data = iddao.findByItemIdAndEventCRFId(child.getId(), ecb.getId());
            ItemFormMetadataBean metadata = ifmdao.findByItemIdAndCRFVersionId(child.getId(), ecb.getCRFVersionId());

            DisplayItemBean dib = new DisplayItemBean();
            dib.setEventDefinitionCRF(edcb);
            dib.setItem(child);
            // tbh
            if (!getServletPage().equals(Page.DOUBLE_DATA_ENTRY_SERVLET)) {
                dib.setData(data);
            }
            // <<tbh 07/2009, bug #3883
            // ItemDataBean dbData = iddao.findByItemIdAndEventCRFIdAndOrdinal(itemId, eventCRFId, ordinal)
            dib.setDbData(data);
            boolean showItem = getItemMetadataService().isShown(metadata.getItemId(), ecb, data);
            if (getServletPage().equals(Page.DOUBLE_DATA_ENTRY_SERVLET)) {
                showItem = getItemMetadataService().hasPassedDDE(metadata, ecb, data);
            } //else {
            //                showItem = getItemMetadataService().isShown(metadata.getItemId(), ecb, dib.getDbData());
            //            }
            // boolean passedDDE = getItemMetadataService().hasPassedDDE(data);
            if (showItem) {
                logger.debug("set show item: " + metadata.getItemId() + " data " + data.getId());
                // metadata.setShowItem(showItem);
                metadata.setShowItem(true);
            }
            // logger.debug("did not catch NPE");

            dib.setMetadata(metadata);

            if (shouldLoadDBValues(dib)) {
                logger.trace("should load db values is true, set value");
                dib.loadDBValue();
                logger.trace("just loaded the child value: " + dib.getData().getValue());
            }

            answer.add(dib);
        }

        // this is a pretty slow and memory intensive way to sort... see if we
        // can
        // have the db do this instead
        Collections.sort(answer);

        return answer;
    }

    /**
     * gets the available dynamics service
     */
    public DynamicsMetadataService getItemMetadataService() {
        itemMetadataService =
            this.itemMetadataService != null ? itemMetadataService : (DynamicsMetadataService) SpringServletAccess.getApplicationContext(context).getBean(
                    "dynamicsMetadataService");
        return itemMetadataService;
    }

    /**
     * @return The Page object which represents this servlet's JSP.
     */
    protected abstract Page getJSPPage();

    /**
     * @return The Page object which represents this servlet.
     */
    protected abstract Page getServletPage();

    protected abstract boolean shouldLoadDBValues(DisplayItemBean dib);

    protected void setUpPanel(DisplaySectionBean section) {
        resetPanel();
        panel.setStudyInfoShown(false);
        panel.setOrderedData(true);

    }

    /*
     * change to explicitly re-set the section bean after reviewing the disc note counts, tbh 01/2010
     */

    protected DisplaySectionBean populateNotesWithDBNoteCounts(FormDiscrepancyNotes discNotes, DisplaySectionBean section) {
        dndao = new DiscrepancyNoteDAO(sm.getDataSource());
       // ArrayList items = section.getItems();
        ArrayList<DiscrepancyNoteBean> ecNotes = dndao.findEventCRFDNotesFromEventCRF(ecb);
        ArrayList<DiscrepancyNoteBean> existingNameNotes = new ArrayList(),nameNotes = new ArrayList();
        ArrayList<DiscrepancyNoteBean> existingIntrvDateNotes = new ArrayList(),dateNotes = new ArrayList();

        int intNew = 0,intRes = 0,intUpdated=0,intClosed=0,intNA = 0;
        int dateNew = 0,dateRes = 0,dateUpdated=0,dateClosed=0,dateNA=0 ;
        boolean hasMoreThreads = false;
        for (int i = 0; i < ecNotes.size(); i++) {
            DiscrepancyNoteBean dn = ecNotes.get(i);
            if (INTERVIEWER_NAME.equalsIgnoreCase(dn.getColumn())) {
                discNotes.setNumExistingFieldNotes(INPUT_INTERVIEWER, 1);
                request.setAttribute("hasNameNote", "yes");
                request.setAttribute(INTERVIEWER_NAME_NOTE, dn);
               if(dn.getParentDnId()==0)
                existingNameNotes.add(dn);

            }

            if (DATE_INTERVIEWED.equalsIgnoreCase(dn.getColumn())) {
                discNotes.setNumExistingFieldNotes(INPUT_INTERVIEW_DATE, 1);
                request.setAttribute("hasDateNote", "yes");
                request.setAttribute(INTERVIEWER_DATE_NOTE, dn);
             if(dn.getParentDnId()==0)
                existingIntrvDateNotes.add(dn);
            }
        }
        setToolTipEventNotes();
        request.setAttribute("nameNoteResStatus", getDiscrepancyNoteResolutionStatus(existingNameNotes));
        request.setAttribute("IntrvDateNoteResStatus", getDiscrepancyNoteResolutionStatus(existingIntrvDateNotes));

        request.setAttribute("existingNameNotes", existingNameNotes);



        request.setAttribute("existingIntrvDateNotes", existingIntrvDateNotes);

        //sendJSONNames(existingNameNotes);
///===add here...
        List<DisplayItemWithGroupBean> allItems = section.getDisplayItemGroups();
        logger.debug("start to populate notes: " + section.getDisplayItemGroups().size());
        this.output(allItems);
        for (int k = 0; k < allItems.size(); k++) {
            DisplayItemWithGroupBean itemWithGroup = allItems.get(k);

            if (itemWithGroup.isInGroup()) {
                logger.debug("group item DNote...");
                List<DisplayItemGroupBean> digbs = itemWithGroup.getItemGroups();
                logger.trace("digbs size: " + digbs.size());
                for (int i = 0; i < digbs.size(); i++) {
                    DisplayItemGroupBean displayGroup = digbs.get(i);
                    List<DisplayItemBean> items = displayGroup.getItems();
                    // for (DisplayItemBean dib : items) {
                    for (int j = 0; j < items.size(); j++) {
                        DisplayItemBean dib = items.get(j);
                        int itemDataId = dib.getData().getId();
                        int numNotes = dndao.findNumExistingNotesForItem(itemDataId);

                        // logger.debug("itemDataId:" + itemDataId);
                        // logger.debug("numNotes:" + numNotes);

                        // String inputName =
                        // displayGroup.getItemGroupBean().getName() + "_" + i +
                        // "." + getInputName(dib);
                        int ordinal = this.getManualRows(digbs);
                        String inputName = getGroupItemInputName(displayGroup, displayGroup.getFormInputOrdinal(), dib);
                        if (!displayGroup.isAuto()) {
                            inputName = getGroupItemManualInputName(displayGroup, i, dib);
                        }

                        // String inputName = getGroupItemInputName(displayGroup, i, getManualRows(digbs), dib);
                        // logger.trace("inputName: " + inputName);
                        discNotes.setNumExistingFieldNotes(inputName, numNotes);
                        ArrayList notes = discNotes.getNotes(inputName);
                        // we need to also set the notes for the manual input name, tbh 01/2010
                        //                        String inputName2 = this.getGroupItemManualInputName(displayGroup, i, dib);
                        //                        logger.trace("inputName 2: " + inputName2);
                        //                        ArrayList notes2 = discNotes.getNotes(inputName2);
                        //                        discNotes.setNumExistingFieldNotes(inputName2, numNotes);
                        //                        if (numNotes > 0) {
                        //                            logger.debug("itemDataId:" + itemDataId);
                        //                            logger.debug("numNotes:" + numNotes);
                        //                            logger.debug("inputName: " + inputName);
                        //                            logger.debug("inputName 2: " + inputName2);
                        //                        }
                        dib.setNumDiscrepancyNotes(numNotes + notes.size());// + notes2.size());
                        dib.setDiscrepancyNoteStatus(getDiscrepancyNoteResolutionStatus(itemDataId, notes));
                        dib =  setTotals(dib,itemDataId,notes);
                        logger.debug("dib note size:" + dib.getNumDiscrepancyNotes() + " " + dib.getData().getId() + " " + inputName);
                        items.set(j, dib);
                    }
                    displayGroup.setItems(items);
                    digbs.set(i, displayGroup);
                }
                itemWithGroup.setItemGroups(digbs);

            } else {
                logger.trace("single item db note");
                DisplayItemBean dib = itemWithGroup.getSingleItem();
                try {
                    ResponseOptionBean rob = (ResponseOptionBean) dib.getMetadata().getResponseSet().getOptions().get(0);
                    logger.trace("test print of options for coding: " + rob.getValue());
                } catch (NullPointerException e) {
                    // TODO Auto-generated catch block
                    // e.printStackTrace();
                }
                int itemDataId = dib.getData().getId();
                int itemId = dib.getItem().getId();
                int numNotes = dndao.findNumExistingNotesForItem(itemDataId);
                String inputFieldName = "input" + itemId;

                discNotes.setNumExistingFieldNotes(inputFieldName, numNotes);
                dib.setNumDiscrepancyNotes(numNotes + discNotes.getNotes(inputFieldName).size());
                dib.setDiscrepancyNoteStatus(getDiscrepancyNoteResolutionStatus(itemDataId, discNotes.getNotes(inputFieldName)));
               dib =  setTotals(dib,itemDataId,discNotes.getNotes(inputFieldName));

                ArrayList childItems = dib.getChildren();
                for (int j = 0; j < childItems.size(); j++) {
                    DisplayItemBean child = (DisplayItemBean) childItems.get(j);
                    int childItemDataId = child.getData().getId();
                    int childItemId = child.getItem().getId();
                    int childNumNotes = dndao.findNumExistingNotesForItem(childItemDataId);
                    String childInputFieldName = "input" + childItemId;

                    logger.debug("*** setting " + childInputFieldName);
                    discNotes.setNumExistingFieldNotes(childInputFieldName, childNumNotes);
                    child.setNumDiscrepancyNotes(childNumNotes + discNotes.getNotes(childInputFieldName).size());
                    child.setDiscrepancyNoteStatus(getDiscrepancyNoteResolutionStatus(childItemDataId, discNotes.getNotes(childInputFieldName)));
                    child = setTotals(child,childItemDataId,discNotes.getNotes(childInputFieldName));
                    childItems.set(j, child);
                }
                dib.setChildren(childItems);
                itemWithGroup.setSingleItem(runDynamicsItemCheck(dib));
            }
            // missing piece of the puzzle - reset the itemgroup into all items?
            allItems.set(k, itemWithGroup);
        }

        section.setDisplayItemGroups(allItems);
        return section;
    }

    private void setToolTipEventNotes() {


        ArrayList<DiscrepancyNoteBean> ecNotes = dndao.findEventCRFDNotesToolTips(ecb);
        ArrayList<DiscrepancyNoteBean> nameNotes = new ArrayList();
        ArrayList<DiscrepancyNoteBean> dateNotes = new ArrayList();
        for (int i = 0; i < ecNotes.size(); i++) {
            DiscrepancyNoteBean dn = ecNotes.get(i);
            if (INTERVIEWER_NAME.equalsIgnoreCase(dn.getColumn())) {


                	nameNotes.add(dn);


            }

            if (DATE_INTERVIEWED.equalsIgnoreCase(dn.getColumn())) {
                             	dateNotes.add(dn);
            }

        }
        request.setAttribute("nameNotes", nameNotes);
        request.setAttribute("intrvDates", dateNotes);
	}

	/*@RequestMapping(value="/{nameNotes}", method=RequestMethod.GET)
    public @ResponseBody ArrayList<DiscrepancyNoteBean> sendJSONNames(ArrayList<DiscrepancyNoteBean> existingNameNotes) {
		return existingNameNotes;
	}*/



	/**
     * To set the totals of each resolution status on the DisplayItemBean for each item.
     * @param dib
     * @param notes
     */
    private DisplayItemBean setTotals(DisplayItemBean dib,int itemDataId,ArrayList<DiscrepancyNoteBean> notes)
    {
    	int resolutionStatus;
    	int totNew = 0,totRes = 0,totClosed = 0,totUpdated =0,totNA = 0;
    	boolean hasOtherThread = false;
    	 ArrayList<DiscrepancyNoteBean> existingNotes = dndao.findExistingNotesForToolTip(itemDataId);
    	 dib.setDiscrepancyNotes(existingNotes);

         for (DiscrepancyNoteBean obj : dib.getDiscrepancyNotes()) {
             DiscrepancyNoteBean note =  obj;


             if (note.getParentDnId() == 0) {
                	 resolutionStatus = note.getResolutionStatusId();
                 totNew++;//using totNew to show the total parent threads
                 /* if(resolutionStatus==ResolutionStatus.UPDATED.getId())totUpdated++;
                 else if(resolutionStatus==ResolutionStatus.RESOLVED.getId())totRes++;//Resolution proposed count
                 else if(resolutionStatus==ResolutionStatus.CLOSED.getId())totClosed++;
                 else if(resolutionStatus==ResolutionStatus.NOT_APPLICABLE.getId())totNA++;*/// not needed any more
             }
         }

        ArrayList parentNotes = dndao.findExistingNotesForItemData(itemDataId);
        //Adding this to show the value of only parent threads on discrepancy notes tool tip
        for (Object obj : parentNotes) {
            DiscrepancyNoteBean note = (DiscrepancyNoteBean) obj;
            /*We would only take the resolution status of the parent note of any note thread. If there
            * are more than one note thread, the thread with the worst resolution status will be taken.*/

            if(note.getParentDnId()==0)
            {
                 if(hasOtherThread)
                 {
                     totNew++;
                 }
                 hasOtherThread = true;
             }
        }

        AuditDAO adao = new AuditDAO(sm.getDataSource());
        ArrayList itemAuditEvents = adao.checkItemAuditEventsExist(dib.getItem().getId(), "item_data");
        if (itemAuditEvents.size() > 0) {
            dib.getData().setAuditLog(true);    
        }
        dib.setTotNew(totNew);//totNew is used for parent thread count
    	dib.setTotRes(totRes);
    	dib.setTotUpdated(totUpdated);
    	dib.setTotClosed(totClosed);
    	dib.setTotNA(totNA);
    	return dib;
    }

    /**
     * The following methods are for 'mark CRF complete'
     *
     * @return
     */

    protected boolean markCRFComplete() throws Exception {
        locale = request.getLocale();
        // < respage =
        // ResourceBundle.getBundle("org.akaza.openclinica.i18n.page_messages",
        // locale);
        // < restext =
        // ResourceBundle.getBundle("org.akaza.openclinica.i18n.notes",locale);
        // <
        // resexception=ResourceBundle.getBundle(
        // "org.akaza.openclinica.i18n.exceptions",locale);
        getEventCRFBean();
        getEventDefinitionCRFBean();
        DataEntryStage stage = ecb.getStage();

        // request.setAttribute(TableOfContentsServlet.INPUT_EVENT_CRF_BEAN,
        // ecb);
        // request.setAttribute(INPUT_EVENT_CRF_ID, new Integer(ecb.getId()));
        logger.trace("inout_event_crf_id:" + ecb.getId());

        if (stage.equals(DataEntryStage.UNCOMPLETED) || stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE) || stage.equals(DataEntryStage.LOCKED)) {
            addPageMessage(respage.getString("not_mark_CRF_complete1"));
            return false;
        }

        if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE) || stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {

            if (!edcb.isDoubleEntry()) {
                addPageMessage(respage.getString("not_mark_CRF_complete2"));
                return false;
            }
        }

        /*
         * if (!isEachSectionReviewedOnce()) { addPageMessage("You may not mark this Event CRF complete, because there are some sections which have not been
         * reviewed once."); return false; }
         */

        if (isEachRequiredFieldFillout() == false) {
            addPageMessage(respage.getString("not_mark_CRF_complete4"));
            return false;
        }

        /*
         * if (ecb.getInterviewerName().trim().equals("")) { throw new InconsistentStateException(errorPage, "You may not mark this Event CRF complete, because
         * the interviewer name is blank."); }
         */

        Status newStatus = ecb.getStatus();
        boolean ide = true;
        if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY) && edcb.isDoubleEntry()) {
            newStatus = Status.PENDING;
            ecb.setUpdaterId(ub.getId());
            ecb.setUpdater(ub);
            ecb.setUpdatedDate(new Date());
            ecb.setDateCompleted(new Date());
        } else if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY) && !edcb.isDoubleEntry()) {
            newStatus = Status.UNAVAILABLE;
            ecb.setUpdaterId(ub.getId());
            ecb.setUpdater(ub);
            ecb.setUpdatedDate(new Date());
            ecb.setDateCompleted(new Date());
            ecb.setDateValidateCompleted(new Date());
        } else if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE) || stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
            newStatus = Status.UNAVAILABLE;
            ecb.setDateValidateCompleted(new Date());
            ide = false;
        }

        // for the non-reviewed sections, no item data in DB yet, need to
        // create them
        if (!isEachSectionReviewedOnce()) {
            boolean canSave = saveItemsToMarkComplete(newStatus);
            if (canSave == false) {
                addPageMessage(respage.getString("not_mark_CRF_complete3"));
                return false;
            }
        }
        ecb.setStatus(newStatus);
        /*
         * Marking the data entry as signed if the corresponding EventDefinitionCRF is being enabled for electronic signature.
         */
        if (edcb.isElectronicSignature()) {
            ecb.setElectronicSignatureStatus(true);
        }
        ecb = (EventCRFBean) ecdao.update(ecb);
        // note the below statement only updates the DATES, not the STATUS
        ecdao.markComplete(ecb, ide);

        // update all the items' status to complete
        iddao.updateStatusByEventCRF(ecb, newStatus);

        // change status for study event
        StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
        StudyEventBean seb = (StudyEventBean) sedao.findByPK(ecb.getStudyEventId());
        seb.setUpdatedDate(new Date());
        seb.setUpdater(ub);

        EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
        ArrayList allCRFs = ecdao.findAllByStudyEvent(seb);
        StudyBean study = (StudyBean) session.getAttribute("study");
        ArrayList allEDCs = (ArrayList) edcdao.findAllActiveByEventDefinitionId(study, seb.getStudyEventDefinitionId());

        boolean eventCompleted = true;
        boolean allRequired = true;
        int allEDCsize = allEDCs.size();
        ArrayList nonRequiredCrfIds = new ArrayList();
        // go through the list and find out if all are required, tbh
        for (int ii = 0; ii < allEDCs.size(); ii++) {
            EventDefinitionCRFBean edcBean = (EventDefinitionCRFBean) allEDCs.get(ii);
            if (!edcBean.isRequiredCRF()) {
                logger.trace("found one non required CRF: " + edcBean.getCrfName() + " " + edcBean.getCrfId() + " " + edcBean.getDefaultVersionName());
                allRequired = false;
                nonRequiredCrfIds.add(new Integer(edcBean.getCrfId()));
                allEDCsize--;
            }
        }
        logger.trace("non required crf ids: " + nonRequiredCrfIds.toString());
        // go through all the crfs and check their status
        // add an additional check to see if it is required or not, tbh
        for (int i = 0; i < allCRFs.size(); i++) {
            EventCRFBean ec = (EventCRFBean) allCRFs.get(i);
            logger.trace("-- looking at a CRF: " + ec.getName() + " " + ec.getCrf().getName() + " " + ec.getCrf().getId());
            // if clause kind of not right since none of the above fields are
            // set in the dao, tbh
            if (!ec.getStatus().equals(Status.UNAVAILABLE) && ec.getDateInterviewed() != null) { // &&
                // (!nonRequiredCrfIds.contains(new
                // Integer(ec.getCrf().getId())))) {
                eventCompleted = false;
                logger.trace("just rejected eventCompleted looking at a CRF: " + ec.getName());
                break;
            }
        }

        if (!allRequired) {
            logger.trace("SEB contains some nonrequired CRFs: " + allEDCsize + " vs " + allEDCs.size());
        }

        if (eventCompleted && allCRFs.size() >= allEDCsize) {// was
            // allEDCs.size(),
            // tbh
            if (!allRequired && allEDCsize != 0) {// what if there are no
                // required CRFs, and all
                // CRFs have been finished?
                addPageMessage(respage.getString("CRF_completed"));
            } else if (!edcb.isDoubleEntry()){
                logger.trace("just set subj event status to -- COMPLETED --");
                seb.setSubjectEventStatus(SubjectEventStatus.COMPLETED);
            }
        }

        seb = (StudyEventBean) sedao.update(seb);

        return true;
    }

    private void getEventCRFBean() {
        fp = new FormProcessor(request);
        int eventCRFId = fp.getInt(INPUT_EVENT_CRF_ID);

        ecdao = new EventCRFDAO(sm.getDataSource());
        ecb = (EventCRFBean) ecdao.findByPK(eventCRFId);
    }

    protected boolean isEachRequiredFieldFillout() {
        // need to update this method to accomodate dynamics, tbh
        ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
        ItemDAO idao = new ItemDAO(sm.getDataSource());
        ItemFormMetadataDAO itemFormMetadataDao = new ItemFormMetadataDAO(sm.getDataSource());
        int allRequiredNum = idao.findAllRequiredByCRFVersionId(ecb.getCRFVersionId());
        int allRequiredFilledOut = iddao.findAllRequiredByEventCRFId(ecb);
        int allRequiredButHidden = itemFormMetadataDao.findCountAllHiddenByCRFVersionId(ecb.getCRFVersionId());
        int allHiddenButShown = itemFormMetadataDao.findCountAllHiddenButShownByEventCRFId(ecb.getId());
        // add all hidden items minus all hidden but now shown items to the allRequiredFilledOut variable

        if (allRequiredNum > allRequiredFilledOut + allRequiredButHidden - allHiddenButShown) {
            logger.debug("using crf version number: " + ecb.getCRFVersionId());
            logger.debug("allRequiredNum > allRequiredFilledOut:" + allRequiredNum + " " + allRequiredFilledOut + " plus " + allRequiredButHidden + " minus "
                + allHiddenButShown);
            return false;
        }
        // had to change the query below to allow for hidden items here, tbh 04/2010
        ArrayList allFilled = iddao.findAllBlankRequiredByEventCRFId(ecb.getId(), ecb.getCRFVersionId());
        int numNotes = 0;
        if (!allFilled.isEmpty()) {
            logger.trace("allFilled is not empty");
            FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) session.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
            HashMap idNotes = fdn.getIdNotes();
            for (int i = 0; i < allFilled.size(); i++) {
                ItemDataBean idb = (ItemDataBean) allFilled.get(i);
                int exsitingNotes = dndao.findNumExistingNotesForItem(idb.getId());
                if (exsitingNotes > 0) {
                    logger.trace("has existing note");
                    numNotes++;
                } else if (idNotes.containsKey(idb.getId())) {
                    logger.trace("has note in session");
                    numNotes++;
                }
            }
            logger.trace("numNotes allFilled.size:" + numNotes + " " + allFilled.size());
            if (numNotes >= allFilled.size()) {
                logger.trace("all required are filled out");
                return true;
            } else {
                logger.debug("numNotes < allFilled.size() " + numNotes + ": " + allFilled.size());
                return false;
            }
        }
        return true;
    }

    /**
     * 06/13/2007- jxu Since we don't require users to review each section before mark a CRF as complete, we need to create item data in the database because
     * items will not be created unless the section which contains the items is reviewed by users
     */
    private boolean saveItemsToMarkComplete(Status completeStatus) throws Exception {
        ArrayList sections = sdao.findAllByCRFVersionId(ecb.getCRFVersionId());
        for (int i = 0; i < sections.size(); i++) {
            SectionBean sb = (SectionBean) sections.get(i);
            if (!isSectionReviewedOnce(sb)) {
                // ArrayList requiredItems =
                // idao.findAllRequiredBySectionId(sb.getId());
                // if (!requiredItems.isEmpty()) {
                // return false;
                // }
                ArrayList items = idao.findAllBySectionId(sb.getId());
                for (int j = 0; j < items.size(); j++) {
                    ItemBean item = (ItemBean) items.get(j);
                    ItemDataBean idb = new ItemDataBean();
                    idb.setItemId(item.getId());
                    idb.setEventCRFId(ecb.getId());
                    idb.setCreatedDate(new Date());
                    idb.setOrdinal(1);
                    idb.setOwner(ub);
                    if (completeStatus != null) {// to avoid null exception
                        idb.setStatus(completeStatus);
                    } else {
                        idb.setStatus(Status.UNAVAILABLE);
                    }
                    idb.setValue("");
                    iddao.create(idb);
                }
            }
        }

        return true;
    }

    /**
     * Checks if a section is reviewed at least once by user
     *
     * @param sb
     * @return
     */
    protected boolean isSectionReviewedOnce(SectionBean sb) {
        SectionDAO sdao = new SectionDAO(sm.getDataSource());

        DataEntryStage stage = ecb.getStage();

        HashMap numItemsHM = sdao.getNumItemsBySectionId();
        HashMap numItemsPendingHM = sdao.getNumItemsPendingBySectionId(ecb);
        HashMap numItemsCompletedHM = sdao.getNumItemsCompletedBySectionId(ecb);

        Integer key = new Integer(sb.getId());

        int numItems = TableOfContentsServlet.getIntById(numItemsHM, key);
        int numItemsPending = TableOfContentsServlet.getIntById(numItemsPendingHM, key);
        int numItemsCompleted = TableOfContentsServlet.getIntById(numItemsCompletedHM, key);

        if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY) && edcb.isDoubleEntry()) {
            if (numItemsPending == 0 && numItems > 0) {
                return false;
            }
        } else {
            if (numItemsCompleted == 0 && numItems > 0) {
                return false;
            }
        }

        return true;

    }

    /**
     * Checks if all the sections in an event crf are reviewed once
     *
     * @return
     */
    protected boolean isEachSectionReviewedOnce() {
        SectionDAO sdao = new SectionDAO(sm.getDataSource());

        DataEntryStage stage = ecb.getStage();

        ArrayList sections = sdao.findAllByCRFVersionId(ecb.getCRFVersionId());
        HashMap numItemsHM = sdao.getNumItemsBySectionId();
        HashMap numItemsPendingHM = sdao.getNumItemsPendingBySectionId(ecb);
        HashMap numItemsCompletedHM = sdao.getNumItemsCompletedBySectionId(ecb);

        for (int i = 0; i < sections.size(); i++) {
            SectionBean sb = (SectionBean) sections.get(i);
            Integer key = new Integer(sb.getId());

            int numItems = TableOfContentsServlet.getIntById(numItemsHM, key);
            int numItemsPending = TableOfContentsServlet.getIntById(numItemsPendingHM, key);
            int numItemsCompleted = TableOfContentsServlet.getIntById(numItemsCompletedHM, key);

            if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY) && edcb.isDoubleEntry()) {
                if (numItemsPending == 0 && numItems > 0) {
                    return false;
                }
            } else {
                if (numItemsCompleted == 0 && numItems > 0) {
                    return false;
                }
            }
        }

        return true;
    }

    protected void getEventDefinitionCRFBean() {
        edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
        StudyBean study = (StudyBean) session.getAttribute("study");
        edcb = edcdao.findByStudyEventIdAndCRFVersionId(study, ecb.getStudyEventId(), ecb.getCRFVersionId());
    }

    /**
     * Constructs a list of DisplayItemWithGroupBean, which is used for display a section of items on the UI
     *
     * @param dsb
     * @param hasItemGroup
     * @return
     */
    protected List<DisplayItemWithGroupBean> createItemWithGroups(DisplaySectionBean dsb, boolean hasItemGroup, int eventCRFDefId) {

        List<DisplayItemWithGroupBean> displayItemWithGroups = new ArrayList<DisplayItemWithGroupBean>();

        // For adding null values to display items
        FormBeanUtil formBeanUtil = new FormBeanUtil();
        List<String> nullValuesList = new ArrayList<String>();
        // BWP>> Get a List<String> of any null values such as NA or NI
        // method returns null values as a List<String>
        nullValuesList = formBeanUtil.getNullValuesByEventCRFDefId(eventCRFDefId, sm.getDataSource());
        // >>BWP
        ArrayList items = dsb.getItems();
        logger.trace("single items size: " + items.size());
        for (int i = 0; i < items.size(); i++) {
            DisplayItemBean item = (DisplayItemBean) items.get(i);
            DisplayItemWithGroupBean newOne = new DisplayItemWithGroupBean();
            newOne.setSingleItem(runDynamicsItemCheck(item));
            newOne.setOrdinal(item.getMetadata().getOrdinal());
            newOne.setInGroup(false);
            newOne.setPageNumberLabel(item.getMetadata().getPageNumberLabel());
            displayItemWithGroups.add(newOne);
            // logger.trace("just added on line 1979:
            // "+newOne.getSingleItem().getData().getValue());
        }

        if (hasItemGroup) {
            ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
            ArrayList data = iddao.findAllActiveBySectionIdAndEventCRFId(sb.getId(), ecb.getId());
            // BWP 12/2/07>> set a flag signaling to data entry JSPs that data
            // is involved
            // for the purposes of displaying or not displaying default values
            if (data != null && data.size() > 0) {
                session.setAttribute(HAS_DATA_FLAG, true);
            }
            // logger.trace("how many groups:" +
            // dsb.getDisplayFormGroups().size());
            // logger.trace("just got data using section id " + sb.getId() + "
            // and event crf id " + ecb.getId());
            logger.trace("found data: " + data.size());
            logger.trace("data.toString: " + data.toString());

            for (DisplayItemGroupBean itemGroup : dsb.getDisplayFormGroups()) {
                logger.debug("found one itemGroup");
                DisplayItemWithGroupBean newOne = new DisplayItemWithGroupBean();
                // to arrange item groups and other single items, the ordinal of
                // a item group will be the ordinal of the first item in this
                // group
                DisplayItemBean firstItem = itemGroup.getItems().get(0);
                DisplayItemBean checkItem = firstItem;
                // does not work if there is not any data in the first item of the group
                // i.e. imports.
                // does it make a difference if we take a last item? 
                boolean noNeedToSwitch = false;
                for (int i = 0; i < data.size(); i++) {
                    ItemDataBean idb = (ItemDataBean) data.get(i);
                    if (idb.getItemId() == firstItem.getItem().getId()) {
                        noNeedToSwitch = true;
                    }
                }
                if (!noNeedToSwitch) {
                    checkItem = itemGroup.getItems().get(itemGroup.getItems().size() - 1);
                }
                // so we are either checking the first or the last item, BUT ONLY ONCE
                newOne.setPageNumberLabel(firstItem.getMetadata().getPageNumberLabel());

                newOne.setItemGroup(itemGroup);
                newOne.setInGroup(true);
                newOne.setOrdinal(itemGroup.getGroupMetaBean().getOrdinal());

                List<ItemBean> itBeans = idao.findAllItemsByGroupId(itemGroup.getItemGroupBean().getId(), sb.getCRFVersionId());

                boolean hasData = false;
                int checkAllColumns = 0;
                // if a group has repetitions, the number of data of
                // first item should be same as the row number
                for (int i = 0; i < data.size(); i++) {
                    ItemDataBean idb = (ItemDataBean) data.get(i);

                    logger.debug("check all columns: " + checkAllColumns);
                    if (idb.getItemId() == checkItem.getItem().getId()) {
                        hasData = true;
                        logger.debug("set has data to --TRUE--");
                        checkAllColumns = 0;
                        // so that we only fire once a row
                        logger.debug("has data set to true");
                        DisplayItemGroupBean digb = new DisplayItemGroupBean();
                        // always get a fresh copy for items, may use other
                        // better way to
                        // do deep copy, like clone
                        List<DisplayItemBean> dibs = FormBeanUtil.getDisplayBeansFromItems(itBeans, sm.getDataSource(), ecb, sb.getId(), edcb, 0, context);

                        digb.setItems(dibs);
                        logger.trace("set with dibs list of : " + dibs.size());
                        digb.setGroupMetaBean(runDynamicsCheck(itemGroup.getGroupMetaBean()));
                        digb.setItemGroupBean(itemGroup.getItemGroupBean());
                        newOne.getItemGroups().add(digb);
                        newOne.getDbItemGroups().add(digb);
                    }
                }

                List<DisplayItemGroupBean> groupRows = newOne.getItemGroups();
                logger.trace("how many group rows:" + groupRows.size());
                logger.trace("how big is the data:" + data.size());
                if (hasData) {
                    session.setAttribute(GROUP_HAS_DATA, Boolean.TRUE);
                    // iterate through the group rows, set data for each item in
                    // the group
                    for (int i = 0; i < groupRows.size(); i++) {
                        DisplayItemGroupBean displayGroup = groupRows.get(i);
                        for (DisplayItemBean dib : displayGroup.getItems()) {
                            for (int j = 0; j < data.size(); j++) {
                                ItemDataBean idb = (ItemDataBean) data.get(j);
                                if (idb.getItemId() == dib.getItem().getId() && !idb.isSelected()) {
                                    idb.setSelected(true);
                                    dib.setData(idb);
                                    logger.debug("--> set data " + idb.getId() + ": " + idb.getValue());

                                    if (shouldLoadDBValues(dib)) {
                                        logger.debug("+++should load db values is true, set value");
                                        dib.loadDBValue();
                                        logger.debug("+++data loaded: " + idb.getName() + ": " + idb.getOrdinal() + " " + idb.getValue());
                                        logger.debug("+++try dib OID: " + dib.getItem().getOid());
                                    }
                                    break;
                                }
                            }
                        }

                    }
                } else {
                    session.setAttribute(GROUP_HAS_DATA, Boolean.FALSE);
                    // no data, still add a blank row for displaying
                    DisplayItemGroupBean digb2 = new DisplayItemGroupBean();
                    List<DisplayItemBean> dibs = FormBeanUtil.getDisplayBeansFromItems(itBeans, sm.getDataSource(), ecb, sb.getId(), nullValuesList, context);
                    digb2.setItems(dibs);
                    logger.trace("set with nullValuesList of : " + nullValuesList);
                    digb2.setEditFlag("initial");
                    digb2.setGroupMetaBean(itemGroup.getGroupMetaBean());
                    digb2.setItemGroupBean(itemGroup.getItemGroupBean());
                    newOne.getItemGroups().add(digb2);
                    newOne.getDbItemGroups().add(digb2);

                }

                displayItemWithGroups.add(newOne);
            }

        }// if hasItemGroup
        Collections.sort(displayItemWithGroups);

        // add null values to displayitems in the itemGroups of
        // DisplayItemWithGroupBeans;
        // These item groups are used by the data entry screens
        /*
         * if(nullValuesList != null && (! nullValuesList.isEmpty())) { formBeanUtil.addNullValuesToDisplayItemWithGroupBeans( displayItemWithGroups,
         * nullValuesList); }
         */
        return displayItemWithGroups;
    }

    /**
     * @param fp
     * @param dibs
     * @param i
     * @param digb
     * @return
     */
    private List<DisplayItemBean> processInputForGroupItem(FormProcessor fp, List<DisplayItemBean> dibs, int i, DisplayItemGroupBean digb, boolean isAuto) {
        for (int j = 0; j < dibs.size(); j++) {
            DisplayItemBean displayItem = dibs.get(j);
            String inputName = "";
            org.akaza.openclinica.bean.core.ResponseType rt = displayItem.getMetadata().getResponseSet().getResponseType();
            if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CHECKBOX) || rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECTMULTI)) {

                if (isAuto) {
                    inputName = getGroupItemInputName(digb, i, displayItem);
                } else {
                    inputName = getGroupItemManualInputName(digb, i, displayItem);
                }
                ArrayList valueArray = fp.getStringArray(inputName);
                displayItem.loadFormValue(valueArray);

            } else {
                if (isAuto) {
                    inputName = getGroupItemInputName(digb, i, displayItem);
                } else {
                    inputName = getGroupItemManualInputName(digb, i, displayItem);
                }
                displayItem.loadFormValue(fp.getString(inputName));
                // BWP issue 3257 << This seems to be an apt place to check any
                // single-select options as "selected"
                // if the data in the displayItem matches the options text;
                // although this code is very
                // confusing and murky, ; refactoring of form handling is
                // necessary here
                if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECT)) {
                    ensureSelectedOption(displayItem);
                }
                // BWP>>
            }

        }
        return dibs;
    }

    /**
     * @param digb
     * @param ordinal
     * @param dib
     * @return
     */
    public final String getGroupItemManualInputName(DisplayItemGroupBean digb, int ordinal, DisplayItemBean dib) {
        String inputName = digb.getItemGroupBean().getOid() + "_manual" + ordinal + getInputName(dib);
        logger.info("+++ returning manual: " + inputName);
        return inputName;
    }

    // YW 11-12-2007
    private EventCRFBean updateECB(StudyEventBean sEvent) {
        if (!currentStudy.getStudyParameterConfig().getInterviewerNameDefault().equals("blank")
            && ("".equals(ecb.getInterviewerName()) || ecb.getInterviewerName() == null)) {
            // default will be event's owner name
            ecb.setInterviewerName(sEvent.getOwner().getName());
        }

        if (!currentStudy.getStudyParameterConfig().getInterviewDateDefault().equals("blank")
            && ("".equals(ecb.getDateInterviewed()) || ecb.getDateInterviewed() == null)) {
            if (sEvent.getDateStarted() != null) {
                ecb.setDateInterviewed(sEvent.getDateStarted());// default date
            } else {
                // logger.trace("evnet start date is null, so date interviewed is
                // null");
                ecb.setDateInterviewed(null);
            }
        }

        return ecb;
    }

    protected HashMap<String, ItemBean> prepareScoreItems() {
        HashMap<String, ItemBean> scoreItems = new HashMap<String, ItemBean>();
        ArrayList<ItemBean> ibs = idao.findAllItemsByVersionId(ecb.getCRFVersionId());
        for (ItemBean ib : ibs) {
            scoreItems.put(ib.getName(), ib);
        }

        return scoreItems;
    }

    protected HashMap<String, String> prepareScoreItemdata() {
        HashMap<String, String> scoreItemdata = new HashMap<String, String>();
        ArrayList<SectionBean> sbs = sdao.findAllByCRFVersionId(ecb.getCRFVersionId());
        for (SectionBean section : sbs) {
            HashMap<String, String> data = prepareSectionItemDataBeans(section.getId());
            if (data != null && data.size() > 0) {
                scoreItemdata.putAll(data);
            }
        }
        return scoreItemdata;
    }

    protected HashMap<String, String> prepareSectionItemDataBeans(int sectionId) {
        HashMap<String, String> scoreItemdata = new HashMap<String, String>();
        ArrayList<ItemDataBean> idbs = iddao.findAllActiveBySectionIdAndEventCRFId(sectionId, ecb.getId());
        for (ItemDataBean idb : idbs) {
            if (idb.getId() > 0) {
                int ordinal = idb.getOrdinal();
                ordinal = ordinal > 0 ? ordinal : 1;
                scoreItemdata.put(idb.getItemId() + "_" + ordinal, idb.getValue().length() > 0 ? idb.getValue() : "");
            }
        }
        return scoreItemdata;
    }

    protected HashMap<Integer, TreeSet<Integer>> prepareItemdataOrdinals() {
        HashMap<Integer, TreeSet<Integer>> ordinals = new HashMap<Integer, TreeSet<Integer>>();
        ArrayList<SectionBean> sbs = sdao.findAllByCRFVersionId(ecb.getCRFVersionId());
        for (SectionBean section : sbs) {
            ArrayList<ItemDataBean> idbs = iddao.findAllActiveBySectionIdAndEventCRFId(section.getId(), ecb.getId());
            if (idbs != null && idbs.size() > 0) {
                for (ItemDataBean idb : idbs) {
                    int itemId = idb.getItemId();
                    TreeSet<Integer> os = new TreeSet<Integer>();
                    if (ordinals == null) {
                        os.add(idb.getOrdinal());
                        ordinals.put(itemId, os);
                    } else if (ordinals.containsKey(itemId)) {
                        os = ordinals.get(itemId);
                        os.add(idb.getOrdinal());
                        ordinals.put(itemId, os);
                    } else {
                        os.add(idb.getOrdinal());
                        ordinals.put(itemId, os);
                    }
                }
            }
        }
        return ordinals;
    }

    protected HashMap<Integer, Integer> prepareGroupSizes(HashMap<String, ItemBean> scoreItems) {
        HashMap<Integer, Integer> groupSizes = new HashMap<Integer, Integer>();

        Iterator iter = scoreItems.keySet().iterator();
        while (iter.hasNext()) {
            int itemId = scoreItems.get(iter.next().toString()).getId();
            groupSizes.put(itemId, 1);
        }

        ArrayList<SectionBean> sbs = sdao.findAllByCRFVersionId(ecb.getCRFVersionId());
        for (SectionBean section : sbs) {
            ArrayList<ItemDataBean> idbs = iddao.findAllActiveBySectionIdAndEventCRFId(section.getId(), ecb.getId());
            for (ItemDataBean idb : idbs) {
                int itemId = idb.getItemId();
                if (groupSizes != null && groupSizes.containsKey(itemId)) {
                    int groupsize = iddao.getGroupSize(itemId, ecb.getId());
                    groupsize = groupsize > 0 ? groupsize : 1;
                    groupSizes.put(itemId, groupsize);
                }
            }
        }
        return groupSizes;
    }

    protected HashMap<Integer, String> prepareSectionItemdata(int sectionId) {
        HashMap<Integer, String> itemdata = new HashMap<Integer, String>();
        ArrayList<ItemDataBean> idbs = iddao.findAllActiveBySectionIdAndEventCRFId(sectionId, ecb.getId());
        for (ItemDataBean idb : idbs) {
            itemdata.put(idb.getId(), idb.getValue());
        }
        return itemdata;
    }

    protected boolean isChanged(ItemDataBean idb, HashMap<Integer, String> oldItemdata) {
        String value = idb.getValue();
        if (!oldItemdata.containsKey(idb.getId()))
            return true;
        else {
            String oldValue = oldItemdata.get(idb.getId());
            if (oldValue != null) {
                if (value == null)
                    return true;
                else if (!oldValue.equals(value))
                    return true;
            } else if (value != null)
                return true;
        }
        return false;
    }
    
    protected boolean isChanged(DisplayItemBean dib, HashMap<Integer, String> oldItemdata, String attachedFilePath) {
        ItemDataBean idb = dib.getData();
        String value = idb.getValue();
        // if(value != null && value.length()>0 && dib.getItem().getDataType().getId()==11) {
        // value = attachedFilePath + value;
        // }
        if (!oldItemdata.containsKey(idb.getId()))
            return true;
        else {
            String oldValue = oldItemdata.get(idb.getId());
            if (oldValue != null) {
                if (value == null)
                    return true;
                else if (dib.getItem().getDataType().getId() == 11) {
                    String theOldValue = oldValue.split("(/|\\\\)")[oldValue.split("(/|\\\\)").length - 1].trim();
                    return !value.equals(theOldValue);
                } else if (!oldValue.equals(value))
                    return true;
            } else if (value != null)
                return true;
        }
        return false;
    }

    protected boolean isChanged(ItemDataBean idb, HashMap<Integer, String> oldItemdata, DisplayItemBean dib,String attachedFilePath) {
        if(dib.getMetadata().isConditionalDisplayItem() && !dib.getIsSCDtoBeShown()) {
            return false;
        } else {
            return isChanged(dib, oldItemdata, attachedFilePath);
        }
    }
    /**
     * Output, just logs all contents of the allItems list. tbh, 01/2010
     *
     * @param displayItemWithGroups
     */
    protected void output(List<DisplayItemWithGroupBean> displayItemWithGroups) {
        for (int i = 0; i < displayItemWithGroups.size(); ++i) {
            DisplayItemWithGroupBean diwb = displayItemWithGroups.get(i);

            if (diwb.isInGroup()) {
                List<DisplayItemGroupBean> dbGroups = diwb.getDbItemGroups();
                logger.trace("+++++++ DB ITEM GROUPS ++++++++");
                for (int j = 0; j < dbGroups.size(); j++) {
                    DisplayItemGroupBean displayGroup = dbGroups.get(j);
                    List<DisplayItemBean> items = displayGroup.getItems();
                    for (DisplayItemBean displayItem : items) {
                        int itemId = displayItem.getItem().getId();
                        int ordinal = displayItem.getData().getOrdinal();
                        if ("initial".equalsIgnoreCase(displayGroup.getEditFlag())) {
                            // nextOrdinals.put(itemId, 1);
                            logger.trace("* found initial: " + itemId + " " + ordinal);
                        } else {
                            logger.trace("** found NOT initial: " + itemId + " " + ordinal);
                        }
                        // editFlags.put(displayItem.getData().getId(), displayGroup.getEditFlag());
                    }
                }

                List<DisplayItemGroupBean> dgbs = diwb.getItemGroups();
                logger.trace("+++++++++ ITEM GROUPS ++++++++++");
                int nextOrdinal = 0;
                for (int j = 0; j < dgbs.size(); j++) {
                    DisplayItemGroupBean displayGroup = dgbs.get(j);
                    List<DisplayItemBean> oItems = displayGroup.getItems();
                    String editFlag = displayGroup.getEditFlag();
                    for (DisplayItemBean displayItem : oItems) {
                        int itemId = displayItem.getItem().getId();
                        // nextOrdinal = nextOrdinals.get(itemId);
                        int ordinal = 0;
                        // String editflag = "add".equalsIgnoreCase(editFlag) ? editFlag : editFlags.get(displayItem.getData().getId());
                        // if (editflag.length() > 0) {
                        // logger.trace("*** found: edit flag for " + itemId + ": " + editflag);
                        logger.trace("*** found edit Flag " + itemId + ": " + editFlag);
                        // }
                    }
                }
            }
        }
    }

    protected void updateDataOrdinals(List<DisplayItemWithGroupBean> displayItemWithGroups) {
        for (int i = 0; i < displayItemWithGroups.size(); ++i) {
            DisplayItemWithGroupBean diwb = displayItemWithGroups.get(i);
            HashMap<Integer, String> editFlags = new HashMap<Integer, String>();
            HashMap<Integer, Integer> nextOrdinals = new HashMap<Integer, Integer>();
            if (diwb.isInGroup()) {
                List<DisplayItemGroupBean> dbGroups = diwb.getDbItemGroups();
                for (int j = 0; j < dbGroups.size(); j++) {
                    DisplayItemGroupBean displayGroup = dbGroups.get(j);
                    List<DisplayItemBean> items = displayGroup.getItems();
                    for (DisplayItemBean displayItem : items) {
                        int itemId = displayItem.getItem().getId();
                        int ordinal = displayItem.getData().getOrdinal();
                        if ("initial".equalsIgnoreCase(displayGroup.getEditFlag())) {
                            nextOrdinals.put(itemId, 1);
                        } else {
                            if (nextOrdinals.containsKey(itemId)) {
                                int max = nextOrdinals.get(itemId);
                                nextOrdinals.put(itemId, ordinal > max ? ordinal + 1 : max);
                            } else {
                                nextOrdinals.put(itemId, ordinal + 1);
                            }
                        }
                        editFlags.put(displayItem.getData().getId(), displayGroup.getEditFlag());
                    }
                }

                List<DisplayItemGroupBean> dgbs = diwb.getItemGroups();
                int nextOrdinal = 0;
                for (int j = 0; j < dgbs.size(); j++) {
                    DisplayItemGroupBean displayGroup = dgbs.get(j);
                    List<DisplayItemBean> oItems = displayGroup.getItems();
                    String editFlag = displayGroup.getEditFlag();
                    for (DisplayItemBean displayItem : oItems) {
                        int itemId = displayItem.getItem().getId();
                        nextOrdinal = nextOrdinals.get(itemId);
                        int ordinal = 0;
                        String editflag = "add".equalsIgnoreCase(editFlag) ? editFlag : editFlags.get(displayItem.getData().getId());
                        if (editflag.length() > 0) {
                            if ("add".equalsIgnoreCase(editflag)) {
                                ordinal = nextOrdinals.get(itemId);
                                displayItem.getData().setOrdinal(ordinal);
                                nextOrdinals.put(itemId, nextOrdinal + 1);
                            } else if ("edit".equalsIgnoreCase(editflag)) {
                                displayItem.getData().setOrdinal(displayItem.getDbData().getOrdinal());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Customized validation for item input
     *
     * @param v
     * @param dib
     * @param inputName
     */
    private void customValidation(DiscrepancyValidator v, DisplayItemBean dib, String inputName) {
        String customValidationString = dib.getMetadata().getRegexp();
        if (!StringUtil.isBlank(customValidationString)) {
            Validation customValidation = null;

            if (customValidationString.startsWith("func:")) {
                try {
                    customValidation = Validator.processCRFValidationFunction(customValidationString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (customValidationString.startsWith("regexp:")) {
                try {
                    customValidation = Validator.processCRFValidationRegex(customValidationString);
                } catch (Exception e) {
                }
            }

            if (customValidation != null) {
                customValidation.setErrorMessage(dib.getMetadata().getRegexpErrorMsg());
                v.addValidation(inputName, customValidation);
            }
        }// custom validation
    }

    private String ifValueIsDate(ItemBean itemBean, String value, boolean dryRun) {

        String dateFormat = ResourceBundleProvider.getFormatBundle().getString("date_format_string");
        String dateRegexp = ResourceBundleProvider.getFormatBundle().getString("date_regexp");

        // isValidDateMMddyyyy

        String theFinalValue = value;
        if (value != null && itemBean.getDataType() == ItemDataType.DATE && dryRun) {
            theFinalValue = ExpressionTreeHelper.ifValidDateFormatAsyyyyMMdd(value, dateFormat, dateRegexp);
        } else {
            theFinalValue = ExpressionTreeHelper.isValidDateMMddyyyy(value);
        }
        return theFinalValue;
    }

    /**
     * This method will populate grouped and variableAndValue HashMaps grouped : Used to correctly populate group ordinals variableAndValue : Holds itemOID ,
     * value (in Form ) pairs passed in to rule processor
     *
     * @param allItems
     */
    private Container populateRuleSpecificHashMaps(List<DisplayItemWithGroupBean> allItems, Container c, Boolean dryRun) {

        for (DisplayItemWithGroupBean displayItemWithGroupBean : allItems) {
            // Items in Non Repeating group also known as UNGROUPED
            if (displayItemWithGroupBean.getSingleItem() != null) {
                if (displayItemWithGroupBean.getSingleItem().getItem().getOid() != null) {
                    c.grouped.put(displayItemWithGroupBean.getSingleItem().getItem().getOid(), 1);
                    c.variableAndValue.put(displayItemWithGroupBean.getSingleItem().getItem().getOid(), ifValueIsDate(displayItemWithGroupBean.getSingleItem()
                            .getItem(), displayItemWithGroupBean.getSingleItem().getData().getValue(), dryRun));
                    logger.debug("Type : " + displayItemWithGroupBean.getSingleItem().getItem().getItemDataTypeId());
                    for (Object displayItemBean : displayItemWithGroupBean.getSingleItem().getChildren()) {
                        String oid = ((DisplayItemBean) displayItemBean).getItem().getOid();
                        String value =
                            ifValueIsDate(((DisplayItemBean) displayItemBean).getItem(), ((DisplayItemBean) displayItemBean).getData().getValue(), dryRun);
                        logger.debug("Type : " + ((DisplayItemBean) displayItemBean).getItem().getItemDataTypeId());
                        c.grouped.put(oid, 1);
                        c.variableAndValue.put(oid, value);

                    }
                }
                logger.debug("Item Name : {} , Item Value : {} , Item Data Ordinal : {} , Item OID : {} ", new Object[] {
                    displayItemWithGroupBean.getSingleItem().getItem().getName(), displayItemWithGroupBean.getSingleItem().getData().getValue(),
                    displayItemWithGroupBean.getSingleItem().getData().getOrdinal(), displayItemWithGroupBean.getSingleItem().getItem().getOid() });
            }
            // Items in repeating groups
            for (DisplayItemGroupBean itemGroupBean : displayItemWithGroupBean.getItemGroups()) {
                logger.debug("Item Group Name : {} , Item Group OID : {} , Ordinal : {} ", new Object[] { itemGroupBean.getItemGroupBean().getName(),
                    itemGroupBean.getItemGroupBean().getOid(), itemGroupBean.getIndex() });
                for (DisplayItemBean displayItemBean : itemGroupBean.getItems()) {
                    String key1 = itemGroupBean.getItemGroupBean().getOid() + "[" + (itemGroupBean.getIndex() + 1) + "]." + displayItemBean.getItem().getOid();
                    String key = itemGroupBean.getItemGroupBean().getOid() + "." + displayItemBean.getItem().getOid();
                    c.variableAndValue.put(key1, ifValueIsDate(displayItemBean.getItem(), displayItemBean.getData().getValue(), dryRun));
                    if (c.grouped.containsKey(key)) {
                        c.grouped.put(key, c.grouped.get(key) + 1);
                    } else {
                        c.grouped.put(key, 1);
                    }
                    logger.debug("Item Name : {} , Item Value : {} , Item OID : {} ", new Object[] { displayItemBean.getItem().getName(),
                        displayItemBean.getData().getValue(), displayItemBean.getItem().getOid() });
                }
            }
        }
        if (logger.isDebugEnabled()) {
            for (String key : c.grouped.keySet()) {
                logger.debug("key : {} , value : {}", key, c.grouped.get(key));
            }
            for (String key : c.variableAndValue.keySet()) {
                logger.debug("key : {} , value : {}", key, c.variableAndValue.get(key));
            }
        }
        return c;
    }

    private List<RuleSetBean> createAndInitializeRuleSet(StudyBean currentStudy,
            StudyEventDefinitionBean studyEventDefinition,
            CRFVersionBean crfVersionBean,
            StudyEventBean studyEventBean,
            EventCRFBean eventCrfBean,
            Boolean shouldRunRules) {
        if (shouldRunRules) {
            List<RuleSetBean> ruleSets = getRuleSetService().getRuleSetsByCrfStudyAndStudyEventDefinition(currentStudy, studyEventDefinition, crfVersionBean);
            ruleSets = getRuleSetService().filterByStatusEqualsAvailable(ruleSets);
            ruleSets = getRuleSetService().filterRuleSetsByStudyEventOrdinal(ruleSets, studyEventBean, crfVersionBean, studyEventDefinition);
            // place next line here, tbh
            ruleSets = getRuleSetService().filterRuleSetsByHiddenItems(ruleSets, eventCrfBean, crfVersionBean);
            return ruleSets;
        } else
            return new ArrayList<RuleSetBean>();
    }

    private HashMap<String, ArrayList<String>> runRules(List<DisplayItemWithGroupBean> allItems, List<RuleSetBean> ruleSets, Boolean dryRun,
            Boolean shouldRunRules, MessageType mt, Phase phase,EventCRFBean ecb) {
        if (shouldRunRules) {
            Container c = new Container();
            try {
                c = populateRuleSpecificHashMaps(allItems, c, dryRun);
                ruleSets = getRuleSetService().filterRuleSetsBySectionAndGroupOrdinal(ruleSets, c.grouped);
                ruleSets = getRuleSetService().solidifyGroupOrdinalsUsingFormProperties(ruleSets, c.grouped);
                // next line here ?
            } catch (NullPointerException npe) {
                logger.debug("found NPE " + npe.getMessage());
                npe.printStackTrace();
            }
            // above throws NPE?
            // return getRuleSetService().runRules(ruleSets, dryRun,
            // currentStudy, c.variableAndValue, ub);
            logger.debug("running rules ... rule sets size is " + ruleSets.size());
            return getRuleSetService().runRulesInDataEntry(ruleSets, dryRun, currentStudy, ub, c.variableAndValue, phase,ecb).getByMessageType(mt);
        } else {
            return new HashMap<String, ArrayList<String>>();
        }

    }

    protected abstract boolean shouldRunRules();

    protected abstract boolean isAdministrativeEditing();

    protected abstract boolean isAdminForcedReasonForChange();

    private RuleSetServiceInterface getRuleSetService() {
        ruleSetService =
            this.ruleSetService != null ? ruleSetService : (RuleSetServiceInterface) SpringServletAccess.getApplicationContext(context).getBean(
                    "ruleSetService");
        ruleSetService.setContextPath(getContextPath());
        ruleSetService.setMailSender((JavaMailSenderImpl) SpringServletAccess.getApplicationContext(context).getBean("mailSender"));
        ruleSetService.setRequestURLMinusServletPath(getRequestURLMinusServletPath());
        return ruleSetService;
    }

    private void ensureSelectedOption(DisplayItemBean displayItemBean) {
        if (displayItemBean == null || displayItemBean.getData() == null) {
            return;
        }
        ItemDataBean itemDataBean = displayItemBean.getData();
        String dataName = itemDataBean.getName();
        String dataValue = itemDataBean.getValue();
        if ("".equalsIgnoreCase(dataValue)) {
            return;
        }

        List<ResponseOptionBean> responseOptionBeans = new ArrayList<ResponseOptionBean>();
        ResponseSetBean responseSetBean = displayItemBean.getMetadata().getResponseSet();
        if (responseSetBean == null) {
            return;
        }
        responseOptionBeans = responseSetBean.getOptions();
        String tempVal = "";
        for (ResponseOptionBean responseOptionBean : responseOptionBeans) {
            tempVal = responseOptionBean.getValue();
            if (tempVal != null && tempVal.equalsIgnoreCase(dataValue)) {
                responseOptionBean.setSelected(true);
            }
        }
    }

    protected boolean unloadFiles(HashMap<String, String> newUploadedFiles) {
        boolean success = true;
        Iterator iter = newUploadedFiles.keySet().iterator();
        while (iter.hasNext()) {
            String itemId = (String) iter.next();
            String filename = newUploadedFiles.get(itemId);
            File f = new File(filename);
            if (f.exists()) {
                if (f.delete()) {
                    newUploadedFiles.remove("filename");
                } else {
                    success = false;
                }
            } else {
                newUploadedFiles.remove("filename");
                // success = false;
            }
        }
        return success;
    }

    class Container {
        HashMap<String, Integer> grouped;
        HashMap<String, String> variableAndValue;

        public Container() {
            super();
            this.grouped = new HashMap<String, Integer>();
            this.variableAndValue = new HashMap<String, String>();
        }
    }

    public int getManualRows(List<DisplayItemGroupBean> formGroups) {
        int manualRows = 0;
        for (int j = 0; j < formGroups.size(); j++) {
            DisplayItemGroupBean formItemGroup = formGroups.get(j);
            logger.debug("begin formGroup Ordinal:" + formItemGroup.getOrdinal());

            if (formItemGroup.isAuto() == false) {
                manualRows = manualRows + 1;
            }

        }
        logger.debug("+++ returning manual rows: " + manualRows + " from a form group size of " + formGroups.size());
        // logger.debug("+++ returning manual rows: " + manualRows + " from a form group size of " + formGroups.size());
        return manualRows;
    }

    private HashMap reshuffleErrorGroupNamesKK(HashMap errors, List<DisplayItemWithGroupBean> allItems) {
        int manualRows = 0;
        for (int i = 0; i < allItems.size(); i++) {
            DisplayItemWithGroupBean diwb = allItems.get(i);

            if (diwb.isInGroup()) {
                List<DisplayItemGroupBean> dgbs = diwb.getItemGroups();
                for (int j = 0; j < dgbs.size(); j++) {
                    DisplayItemGroupBean digb = dgbs.get(j);

                    ItemGroupBean igb = digb.getItemGroupBean();
                    List<DisplayItemBean> dibs = digb.getItems();

                    if (j == 0) { // first repeat
                        for (DisplayItemBean dib : dibs) {
                            String intendedKey = digb.getInputId() + getInputName(dib);
                            String replacementKey = digb.getItemGroupBean().getOid() + "_" + j + getInputName(dib);
                            if (!replacementKey.equals(intendedKey) && errors.containsKey(intendedKey)) {
                                // String errorMessage = (String)errors.get(intendedKey);
                                errors.put(replacementKey, errors.get(intendedKey));
                                errors.remove(intendedKey);
                                logger.debug("removing: " + intendedKey + " and replacing it with " + replacementKey);
                            }
                        }
                    } else if (j == dgbs.size() - 1) { // last repeat
                        for (DisplayItemBean dib : dibs) {
                            String intendedKey = digb.getInputId() + getInputName(dib);
                            String replacementKey = digb.getItemGroupBean().getOid() + "_1" + getInputName(dib);
                            if (!replacementKey.equals(intendedKey) && errors.containsKey(intendedKey)) {
                                // String errorMessage = (String)errors.get(intendedKey);
                                errors.put(replacementKey, errors.get(intendedKey));
                                errors.remove(intendedKey);
                                logger.debug("removing: " + intendedKey + " and replacing it with " + replacementKey);
                            }
                        }
                    } else { // everything in between
                        manualRows++;
                        for (DisplayItemBean dib : dibs) {
                            String intendedKey = digb.getInputId() + getInputName(dib);
                            String replacementKey = digb.getItemGroupBean().getOid() + "_manual" + j + getInputName(dib);
                            if (!replacementKey.equals(intendedKey) && errors.containsKey(intendedKey)) {
                                // String errorMessage = (String)errors.get(intendedKey);
                                errors.put(replacementKey, errors.get(intendedKey));
                                errors.remove(intendedKey);
                                logger.debug("removing: " + intendedKey + " and replacing it with " + replacementKey);
                            }
                        }
                    }
                }
            }
        }
        request.setAttribute("manualRows", new Integer(manualRows));
        return errors;
    }

    private HashMap reshuffleErrorGroupNames(HashMap errors, List<DisplayItemWithGroupBean> allItems) {
        for (int i = 0; i < allItems.size(); i++) {
            DisplayItemWithGroupBean diwb = allItems.get(i);

            if (diwb.isInGroup()) {
                List<DisplayItemGroupBean> dgbs = diwb.getItemGroups();
                int manualRows = getManualRows(dgbs);
                int totalRows = dgbs.size();
                logger.debug("found manual rows " + manualRows + " and total rows " + totalRows + " from ordinal " + diwb.getOrdinal());
                for (DisplayItemGroupBean digb : dgbs) {
                    ItemGroupBean igb = digb.getItemGroupBean();
                    List<DisplayItemBean> dibs = digb.getItems();
                    int placeHolder = 1;
                    while (totalRows - manualRows > 2) {

                        for (DisplayItemBean dib : dibs) {
                            // needs to be 2, 3, 4 ... in the place of 4, 3, 2...
                            String intendedKey = getGroupItemInputName(digb, placeHolder, dib);
                            String replacementKey = getGroupItemManualInputName(digb, manualRows + 1, dib);
                            if (errors.containsKey(intendedKey)) {
                                // String errorMessage = (String)errors.get(intendedKey);
                                errors.put(replacementKey, errors.get(intendedKey));
                                errors.remove(intendedKey);
                                logger.debug("removing: " + intendedKey + " and replacing it with " + replacementKey);
                            }
                        }
                        placeHolder++;
                        manualRows++;
                    }
                    // but what about the last row? use the placeholder
                    for (DisplayItemBean dib : dibs) {
                        String lastIntendedKey = getGroupItemInputName(digb, placeHolder, dib);
                        String lastReplacementKey = getGroupItemInputName(digb, 1, dib);
                        if (!lastIntendedKey.equals(lastReplacementKey) && errors.containsKey(lastIntendedKey)) {
                            // String errorMessage = (String)errors.get(intendedKey);
                            errors.put(lastReplacementKey, errors.get(lastIntendedKey));
                            errors.remove(lastIntendedKey);
                            logger.debug("removing: " + lastIntendedKey + " and replacing it with " + lastReplacementKey);
                        }
                    }
                }
            }
        }
        return errors;
    }

    /*Determining the resolution status that will be shown in color flag for an item.*/
    private int getDiscrepancyNoteResolutionStatus(int itemDataId, ArrayList formNotes) {
        int resolutionStatus = 0;
        boolean hasOtherThread = false;
        int parentNotesNum = 0;
        ArrayList existingNotes = dndao.findExistingNotesForItemData(itemDataId);
        for (Object obj : existingNotes) {
            DiscrepancyNoteBean note = (DiscrepancyNoteBean) obj;
            /*We would only take the resolution status of the parent note of any note thread. If there
            * are more than one note thread, the thread with the worst resolution status will be taken.*/
            if (note.getParentDnId() == 0) {
                if (hasOtherThread) {
                    if (resolutionStatus > note.getResolutionStatusId()) {
                        resolutionStatus = note.getResolutionStatusId();
                    }
                } else {
                    resolutionStatus = note.getResolutionStatusId();
                }
                hasOtherThread = true;
            }
        }

        if (formNotes == null || formNotes.isEmpty()) {
            return resolutionStatus;
        }

        for (Object obj : formNotes) {
            DiscrepancyNoteBean note = (DiscrepancyNoteBean) obj;
            if (note.getParentDnId() == 0) {
                if (hasOtherThread) {
                    if (resolutionStatus > note.getResolutionStatusId()) {
                        resolutionStatus = note.getResolutionStatusId();
                    }
                } else {
                    resolutionStatus = note.getResolutionStatusId();
                }
                hasOtherThread = true;
            }
        }


        return resolutionStatus;
    }

    private int getDiscrepancyNoteResolutionStatus(List existingNotes) {
        int resolutionStatus = 0;
        boolean hasOtherThread = false;
        for (Object obj : existingNotes) {
            DiscrepancyNoteBean note = (DiscrepancyNoteBean) obj;
            /*We would only take the resolution status of the parent note of any note thread. If there
            * are more than one note thread, the thread with the worst resolution status will be taken.*/
            if (note.getParentDnId() == 0) {
                if (hasOtherThread) {
                    if (resolutionStatus > note.getResolutionStatusId()) {
                        resolutionStatus = note.getResolutionStatusId();
                    }
                } else {
                    resolutionStatus = note.getResolutionStatusId();
                }
                hasOtherThread = true;
            }
        }
        return resolutionStatus;
    }

    private HashMap<String, DisplayItemBean> gainConditionalDisplayPairs(HashMap<String, DisplayItemBean> displayItems,
            ArrayList<ItemFormMetadataBean> conditionalDisplayMeta, HashMap<String,Integer>sectionItemNameIdMap) {
        HashMap<String, DisplayItemBean> displayitems = displayItems;
        for(ItemFormMetadataBean ifmb : conditionalDisplayMeta) {
            Integer cdId = ifmb.getItemId();
            String[] ss = ifmb.getConditionalDisplay().replaceAll("\\\\,", "##").split(",");
            DisplayItemBean displayItem = displayitems.get(sectionItemNameIdMap.get(ss[0].trim()));
            SimpleConditionalDisplayPair cd = new SimpleConditionalDisplayPair();
            cd.setSCDItemId(cdId);
            cd.setControlItemId(displayItem.getItem().getId());
            cd.setControlItemName(displayItem.getItem().getName());
            cd.setOptionValue(ss[1].trim());
            cd.setMessage(ss[2].replaceAll("##", "\\\\,"));
            DisplayItemBean dib = displayItems.get(new Integer(ifmb.getItemId()));
            cd.setIsCurrentShown(SimpleConditionalDisplayPair.isCurrentShownItem(dib.getData()));
            displayItem.getSCDPairs().add(cd);
        }

        return displayitems;
    }

    private ArrayList<DisplayItemBean> gainConditionalDisplayPairs(Map<String, DisplayItemBean> sectionDisplayItemMap,
            List<ItemFormMetadataBean> sectionSCDMeta, Map<String,String> childParentNameMap) {
        for(ItemFormMetadataBean ifmb : sectionSCDMeta) {
            Integer cdId = ifmb.getItemId();
            String[] ss = ifmb.getConditionalDisplay().replaceAll("\\\\,", "##").split(",");

            SimpleConditionalDisplayPair cd = new SimpleConditionalDisplayPair();
            cd.setSCDItemId(cdId);
            cd.setOptionValue(ss[1].trim());
            cd.setMessage(ss[2].replaceAll("##", "\\\\,"));

            DisplayItemBean controlItem = new DisplayItemBean();
            if(childParentNameMap.containsKey(ss[0].trim())) {
                //among child items
                DisplayItemBean pItem = sectionDisplayItemMap.get(childParentNameMap.get(ss[0]).trim());
                ArrayList<DisplayItemBean> children = pItem.getChildren();
                for(int i=0; i<children.size(); ++i) {
                    controlItem = children.get(i);
                    if(ss[0].trim().equalsIgnoreCase(controlItem.getItem().getName())) {
                        ItemBean item = controlItem.getItem();
                        cd.setControlItemId(item.getId());
                        cd.setControlItemName(item.getName());
                        controlItem.getSCDPairs().add(cd);
                    }
                }
            } else {
                //among parent items
                controlItem = sectionDisplayItemMap.get(ss[0].trim());
                ItemBean item = controlItem.getItem();
                cd.setControlItemId(item.getId());
                cd.setControlItemName(item.getName());
                controlItem.getSCDPairs().add(cd);
            }
        }
        ArrayList<DisplayItemBean> displayitems = new ArrayList<DisplayItemBean>();
        displayitems.addAll(sectionDisplayItemMap.values());
        return displayitems;
    }

    protected Set<Integer> conditionalDisplayToBeShown (DisplayItemBean dib, Set<Integer> showConditionalDisplaySet) {
        Set<Integer> showCDSet = showConditionalDisplaySet;
        //a conditional display item will be always after its control item.
        ArrayList<SimpleConditionalDisplayPair> cds = dib.getSCDPairs();
        if(cds.size()>0) {
            String chosenOption = dib.getData().getValue();
            if(chosenOption != null && chosenOption.length()>0) {
                chosenOption = chosenOption.replaceAll("\\\\,", "##");
                if(chosenOption.contains(",")) {
                    String[] ss = chosenOption.split(",");
                    for(SimpleConditionalDisplayPair cd : cds) {
                        for(int i=0; i<ss.length; ++i) {
                            if(ss[i].replaceAll("##", "\\\\,").trim().equalsIgnoreCase(cd.getOptionValue())) {
                                showCDSet.add(cd.getSCDItemId());
                            }
                        }
                    }
                } else {
                    chosenOption.replaceAll("##", "\\,");
                    for(SimpleConditionalDisplayPair cd : cds) {
                        if(chosenOption.equalsIgnoreCase(cd.getOptionValue())) {
                            showCDSet.add(cd.getSCDItemId());
                        }
                    }
                }
            }
        }
        return showCDSet;
    }

    protected boolean shouldSCDBeShown(DisplayItemBean dib) {
        boolean shown = dib.getIsSCDtoBeShown();
        if(!shown) {
            if(dib.getNumDiscrepancyNotes()>0) {
                shown = true;
            } else {
                ItemDataBean dbdata = dib.getDbData();
                Status status = dbdata.isActive()?dbdata.getStatus():null;
                int sid = status==null?0:status.getId();
                shown = sid>0&&(sid!=5||sid!=7)?(dbdata.getValue().length()>0?true:false):false;
            }
        }
        return shown;
    }
}
