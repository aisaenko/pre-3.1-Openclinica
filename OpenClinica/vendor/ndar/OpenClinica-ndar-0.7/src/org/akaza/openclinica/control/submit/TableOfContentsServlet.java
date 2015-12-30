/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.core.SummaryDataEntryStatus;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.akaza.openclinica.bean.submit.DisplayTableOfContentsBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.FormUsageCountBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.managestudy.ViewStudySubjectServlet;
import org.akaza.openclinica.core.form.DiscrepancyValidator;
import org.akaza.openclinica.core.form.FormDiscrepancyNotes;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.FormUsageCountDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.exception.InconsistentStateException;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author ssachs
 */

// TODO: make it possible to input an event crf bean to this servlet rather than
// an int
public class TableOfContentsServlet extends SecureController {
  public static final String BEAN_DISPLAY = "toc";

  // these inputs are used when you get here from a jsp page
  // e.g. TableOfContents?action=ide_c&id=123
  public static final String INPUT_ACTION = "action";

  public static final String INPUT_ID = "ecid";

  // these inputs are used when another servlet sends you here
  // such as mark crf complete, initial data entry, etc
  public static final String INPUT_EVENT_CRF_BEAN = "eventCRF";

  // these are only for use with ACTION_START_INITIAL_DATA_ENTRY
  public static final String INPUT_EVENT_DEFINITION_CRF_ID = "eventDefinitionCRFId";

  public static final String INPUT_CRF_VERSION_ID = "crfVersionId";

  public static final String INPUT_STUDY_EVENT_ID = "studyEventId";

  public static final String INPUT_SUBJECT_ID = "subjectId";

  public static final String INPUT_EVENT_CRF_ID = "eventCRFId";

  // these inputs are displayed on the table of contents and
  // are used to edit Event CRF properties
  public static final String INPUT_INTERVIEWER = "interviewer";

  public static final String INPUT_INTERVIEW_DATE = "interviewDate";

  public static final String ACTION_START_INITIAL_DATA_ENTRY = "ide_s";

  public static final String ACTION_CONTINUE_INITIAL_DATA_ENTRY = "ide_c";

  public static final String ACTION_START_DOUBLE_DATA_ENTRY = "dde_s";

  public static final String ACTION_CONTINUE_DOUBLE_DATA_ENTRY = "dde_c";

  public static final String ACTION_ADMINISTRATIVE_EDITING = "ae";

  public static final String[] ACTIONS = { ACTION_START_INITIAL_DATA_ENTRY,
      ACTION_CONTINUE_INITIAL_DATA_ENTRY, ACTION_START_DOUBLE_DATA_ENTRY,
      ACTION_CONTINUE_DOUBLE_DATA_ENTRY, ACTION_ADMINISTRATIVE_EDITING };

  private FormProcessor fp;

  private EventCRFDAO ecdao;

  private EventCRFBean ecb;

  private String action;

  private void getEventCRFAndAction() {
    ecdao = new EventCRFDAO(sm.getDataSource());

    ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF_BEAN);

    if (ecb == null) {
      int ecid = fp.getInt(INPUT_ID, true);
      AuditableEntityBean aeb = ecdao.findByPKAndStudy(ecid, currentStudy);

      if (!aeb.isActive()) {
        ecb = new EventCRFBean();
      } else {
        ecb = (EventCRFBean) aeb;
      }

      action = fp.getString(INPUT_ACTION, true);
    } else {
      action = getActionForStage(ecb.getStage());
    }
  }

  /**
   * Determines if the action requested is a valid action.
   * 
   * @param action
   *          The action requested.
   * @return <code>true</code> if the action is valid, <code>false</code>
   *         otherwise.
   */
  private boolean invalidAction(String action) {
    ArrayList validActions = new ArrayList(Arrays.asList(ACTIONS));
    return !validActions.contains(action);
  }

  /**
   * Determines if the action requested is consistent with the specified Event
   * CRF's data entry stage.
   * 
   * @param action
   *          The action requested.
   * @param ecb
   *          The Event CRF whose data entry stage is being checked for
   *          consistency with the action.
   * @return <code>true</code> if the action is consistent with the Event
   *         CRF's stage, <code>false</code> otherwise.
   */
  private boolean isConsistentAction(String action, EventCRFBean ecb) {
    DataEntryStage stage = ecb.getStage();

    boolean isConsistent = true;
    if (action.equals(ACTION_START_INITIAL_DATA_ENTRY) && !stage.equals(DataEntryStage.UNCOMPLETED)) {
      isConsistent = false;
    } else if (action.equals(ACTION_CONTINUE_INITIAL_DATA_ENTRY)
        && !stage.equals(DataEntryStage.INITIAL_DATA_ENTRY)) {
      isConsistent = false;
    } else if (action.equals(ACTION_START_DOUBLE_DATA_ENTRY)
        && !stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE)) {
      isConsistent = false;
    } else if (action.equals(ACTION_CONTINUE_DOUBLE_DATA_ENTRY)
        && !stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
      isConsistent = false;
    } else if (action.equals(ACTION_ADMINISTRATIVE_EDITING)
        && !stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE)) {
      isConsistent = false;
    }

    return isConsistent;
  }

  /**
   * Creates a new Event CRF or update the exsiting one, that is, an event CRF
   * can be created but not item data yet, in this case, still consider it is
   * not started(called uncompleted before)
   * 
   * @return @throws
   *         Exception
   */
  private EventCRFBean createEventCRF() throws Exception {
    EventCRFBean newEcb;
    ecdao = new EventCRFDAO(sm.getDataSource());

    int crfVersionId = fp.getInt(INPUT_CRF_VERSION_ID);
    int studyEventId = fp.getInt(INPUT_STUDY_EVENT_ID);
    int eventDefinitionCRFId = fp.getInt(INPUT_EVENT_DEFINITION_CRF_ID);
    int subjectId = fp.getInt(INPUT_SUBJECT_ID);
    int eventCRFId = fp.getInt(INPUT_EVENT_CRF_ID);

    logger.info("Creating event CRF within Table of Contents.  Study id: " + currentStudy.getId()
        + "; CRF Version id: " + crfVersionId + "; Study Event id: " + studyEventId
        + "; Event Definition CRF id: " + eventDefinitionCRFId + "; Subject: " + subjectId);

    StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
    StudySubjectBean ssb = ssdao.findBySubjectIdAndStudy(subjectId, currentStudy);

    if (!ssb.isActive()) {
      throw new InconsistentStateException(
          Page.SUBMIT_DATA_SERVLET,
          "You are trying to begin data entry for a CRF within a Study Event, but the subject does not exist within the study.");
    }

    StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
    StudyEventDefinitionBean sedb = seddao.findByEventDefinitionCRFId(eventDefinitionCRFId);

    if (!ssb.isActive() || !sedb.isActive()) {
      throw new InconsistentStateException(
          Page.SUBMIT_DATA_SERVLET,
          "You are trying to begin data entry for a CRF within a Study Event, but the study event definition for this study event does not exist within the study.");
    }

    CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
    EntityBean eb = cvdao.findByPK(crfVersionId);

    if (!eb.isActive()) {
      throw new InconsistentStateException(
          Page.SUBMIT_DATA_SERVLET,
          "You are trying to begin data entry for a CRF within a Study Event, but the specified CRF version does not exist.");
    }

    StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
    StudyEventBean sEvent = (StudyEventBean) sedao.findByPK(studyEventId);

    StudyBean studyWithSED = currentStudy;
    if (currentStudy.getParentStudyId() > 0) {
      studyWithSED = new StudyBean();
      studyWithSED.setId(currentStudy.getParentStudyId());
    }

    AuditableEntityBean aeb = sedao.findByPKAndStudy(studyEventId, studyWithSED);

    if (!aeb.isActive()) {
      throw new InconsistentStateException(
          Page.SUBMIT_DATA_SERVLET,
          "You are trying to begin data entry for a CRF within a Study Event, but the specified study event definition does not exist within the study.");
    }

    newEcb = new EventCRFBean();
    if (eventCRFId == 0) {//no event CRF created yet
      newEcb.setAnnotations("");
      newEcb.setCreatedDate(new Date());
      newEcb.setCRFVersionId(crfVersionId);
      newEcb.setInterviewerName("");
      if (sEvent.getDateStarted() != null) {
        newEcb.setDateInterviewed(sEvent.getDateStarted());// default date
      } else {
        newEcb.setDateInterviewed(null);
      }
      newEcb.setOwner(ub);
      newEcb.setStatus(Status.AVAILABLE);
      newEcb.setCompletionStatusId(EventCRFBean.COMPLETION_STATUS_DEFAULT);
      newEcb.setStudySubjectId(ssb.getId());
      newEcb.setStudyEventId(studyEventId);
      newEcb.setValidateString("");
      newEcb.setValidatorAnnotations("");

      int formUsageCountId=0;
      FormUsageCountDAO fucdao = new FormUsageCountDAO(sm.getDataSource());
      FormUsageCountBean fuc = (FormUsageCountBean)fucdao.findByCRFVersion(crfVersionId);
      if (fuc==null) {
    	  fuc = new FormUsageCountBean();
      }
      if (!fuc.isActive()) {
    	  fuc.setCRFVersionId(crfVersionId);
    	  fuc = (FormUsageCountBean)fucdao.create(fuc);
      }
      formUsageCountId=fuc.getId();
      if (!fucdao.incrementCountUsageByCrfVersion(fuc)) {
    	  logger.warning("Exceeded number of forms purchased (purchased="+fuc.getCountPurchased()+", used="+fuc.getCountUsed()+") for crfVersionId="+crfVersionId);
      }
      newEcb.setFormUsageCountId(formUsageCountId);
      
      newEcb = (EventCRFBean) ecdao.create(newEcb);
    } else {
      //there is an event CRF already, only need to update
      newEcb = (EventCRFBean) ecdao.findByPK(eventCRFId);
      newEcb.setCRFVersionId(crfVersionId);
      newEcb.setUpdatedDate(new Date());
      newEcb.setUpdater(ub);
      newEcb = (EventCRFBean) ecdao.update(newEcb);

    }

    if (!newEcb.isActive()) {
      throw new InconsistentStateException(Page.SUBMIT_DATA_SERVLET,
          "A new Event CRF could not be created due to a database error.");
    }
//  TODO: Change subject event status too? --AJF
	sEvent.setSummaryDataEntryStatus(SummaryDataEntryStatus.DATA_ENTRY_STARTED);
    sEvent.setUpdater(ub);
    sEvent.setUpdatedDate(new Date());
    sedao.update(sEvent);

    return newEcb;
  }

  private void validateEventCRFAndAction() throws Exception {
    if (invalidAction(action)) {
      throw new InconsistentStateException(Page.SUBMIT_DATA_SERVLET,
          "No action was specified, or an invalid action was specified.");
    }

    if (!isConsistentAction(action, ecb)) {
      HashMap verbs = new HashMap();
      verbs.put(ACTION_START_INITIAL_DATA_ENTRY, "start initial data entry");
      verbs.put(ACTION_CONTINUE_INITIAL_DATA_ENTRY, "continue initial data entry");
      verbs.put(ACTION_START_DOUBLE_DATA_ENTRY, "start double data entry");
      verbs.put(ACTION_CONTINUE_DOUBLE_DATA_ENTRY, "continue double data entry");
      verbs.put(ACTION_ADMINISTRATIVE_EDITING, "perform administrative editing");
      String verb = (String) verbs.get(action);

      if (verb == null) {
        verb = "start initial data entry";
      }

      throw new InconsistentStateException(Page.SUBMIT_DATA_SERVLET, "You are trying to " + verb
          + " on an Event CRF, but that is an inappropriate action for that Event CRF.");
    }

    if (action.equals(ACTION_START_DOUBLE_DATA_ENTRY)) {
      ecb.setValidatorId(ub.getId());
      ecb.setDateValidate(new Date());

      ecb.setUpdater(ub);
      ecb.setUpdatedDate(new Date());
      ecb = (EventCRFBean) ecdao.update(ecb);
    }
  }

  private void updatePresetValues(EventCRFBean ecb) {
    fp.addPresetValue(INPUT_INTERVIEWER, ecb.getInterviewerName());
    if (ecb.getDateInterviewed() != null) {
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
      String idateFormatted = sdf.format(ecb.getDateInterviewed());
      fp.addPresetValue(INPUT_INTERVIEW_DATE, idateFormatted);
    } else {
      fp.addPresetValue(INPUT_INTERVIEW_DATE, "");
    }
    setPresetValues(fp.getPresetValues());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.core.SecureController#processRequest()
   */
  protected void processRequest() throws Exception {
    FormDiscrepancyNotes discNotes;
    if (action.equals(ACTION_START_INITIAL_DATA_ENTRY)) {
      ecb = createEventCRF();
    } else {
      validateEventCRFAndAction();
    }

    updatePresetValues(ecb);

    Boolean b = (Boolean) request.getAttribute(DataEntryServlet.INPUT_IGNORE_PARAMETERS);

    if (fp.isSubmitted() && (b == null)) {
      discNotes = (FormDiscrepancyNotes) session
          .getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
      if (discNotes == null) {
        discNotes = new FormDiscrepancyNotes();
        session.setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);

      }
      DiscrepancyValidator v = new DiscrepancyValidator(request, discNotes);

      v.addValidation(INPUT_INTERVIEWER, Validator.NO_BLANKS);
      v.addValidation(INPUT_INTERVIEW_DATE, Validator.IS_A_DATE);
      v.alwaysExecuteLastValidation(INPUT_INTERVIEW_DATE);

      errors = v.validate();

      if (errors.isEmpty()) {
        
        ecb.setInterviewerName(fp.getString(INPUT_INTERVIEWER));
        ecb.setDateInterviewed(fp.getDate(INPUT_INTERVIEW_DATE));

        if (ecdao == null) {
          ecdao = new EventCRFDAO(sm.getDataSource());
        }

        ecb.setUpdater(ub);
        ecb.setUpdatedDate(new Date());
        ecb = (EventCRFBean) ecdao.update(ecb);
        
        //save discrepancy notes into DB
        FormDiscrepancyNotes fdn = (FormDiscrepancyNotes)session.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
        DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());
       
        AddNewSubjectServlet.saveFieldNotes(INPUT_INTERVIEWER, fdn, dndao, ecb.getId(), "EventCRF", currentStudy);
        AddNewSubjectServlet.saveFieldNotes(INPUT_INTERVIEW_DATE, fdn, dndao, ecb.getId(), "EventCRF", currentStudy);

        if (ecdao.isQuerySuccessful()) {
          updatePresetValues(ecb);
          if (!fp.getBoolean("editInterview", true)) {
            //editing completed
            addPageMessage("The interviewer name and interview date were successfully updated.");
          }
        } else {
          addPageMessage("There was a database error - the interviewer name and interview date were not updated successfully.");
        }
        
      } else {
        String[] textFields = { INPUT_INTERVIEWER, INPUT_INTERVIEW_DATE };
        fp.setCurrentStringValuesAsPreset(textFields);

        setInputMessages(errors);
        setPresetValues(fp.getPresetValues());
      }
    } else {
      discNotes = new FormDiscrepancyNotes();
      session.setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);

    }

    DisplayTableOfContentsBean displayBean = getDisplayBean(ecb, sm.getDataSource(), currentStudy);

    //  this is for generating side info panel
    StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
    StudySubjectBean ssb = (StudySubjectBean) ssdao.findByPK(ecb.getStudySubjectId());
    ArrayList beans = ViewStudySubjectServlet.getDisplayStudyEventsForStudySubject(ssb, sm
        .getDataSource(), ub, currentRole);
    request.setAttribute("studySubject", ssb);
    request.setAttribute("beans", beans);
    request.setAttribute("eventCRF", ecb);

    request.setAttribute(BEAN_DISPLAY, displayBean);
    
    boolean allowEnterData = true;
    if (StringUtil.isBlank(ecb.getInterviewerName())) {
      if ((discNotes == null) || discNotes.getNotes(TableOfContentsServlet.INPUT_INTERVIEWER).isEmpty()){
        allowEnterData = false;
      }
    }
    
    if (ecb.getDateInterviewed() == null) {
      if ((discNotes == null) || (discNotes.getNotes(TableOfContentsServlet.INPUT_INTERVIEW_DATE).isEmpty()) ) {
        allowEnterData = false;
      }
    }
    
    if (!allowEnterData) {
      request.setAttribute("allowEnterData", "no");
      forwardPage(Page.INTERVIEWER);
    } else {
     
    if (fp.getBoolean("editInterview", true)) {
      //user wants to edit interview info
      request.setAttribute("allowEnterData", "yes");
      forwardPage(Page.INTERVIEWER);
    } else {
      if (fp.isSubmitted() && (!errors.isEmpty())) {
        //interview form submitted, but has blank field or validation error
        request.setAttribute("allowEnterData", "no");
        forwardPage(Page.INTERVIEWER);
      } else {
        request.setAttribute("allowEnterData", "yes");
        forwardPage(Page.TABLE_OF_CONTENTS);
      }
    }
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
   */
  protected void mayProceed() throws InsufficientPermissionException {
    fp = new FormProcessor(request);
    getEventCRFAndAction();

    Role r = currentRole.getRole();
    boolean isSuper = DisplayEventCRFBean.isSuper(ub, r);

    if (!SubmitDataServlet.maySubmitData(ub, currentRole)) {
      String exceptionName = "no permission to perform data entry";
      String noAccessMessage = "You may not perform data entry on a CRF in this study.  Please change your active study or contact the Study Coordinator.";

      addPageMessage(noAccessMessage);
      throw new InsufficientPermissionException(Page.MENU, exceptionName, "1");
    }

    // we're creating an event crf
    if (action.equals(ACTION_START_INITIAL_DATA_ENTRY)) {
      return;
    }
    // we're editing an existing event crf
    else {
      if (!ecb.isActive()) {
        addPageMessage("The Event CRF for which you are trying to resume initial data entry does not exist in the current study.  Please contact your Study Coordinator.");
        throw new InsufficientPermissionException(Page.SUBMIT_DATA_SERVLET,
            "Event CRF does not belong to current study", "1");
      }

      if (action.equals(ACTION_CONTINUE_INITIAL_DATA_ENTRY)) {
        if ((ecb.getOwnerId() == ub.getId()) || isSuper) {
          return;
        } else {
          addPageMessage("You did not begin data entry on this Event CRF, so you may not resume data entry on it.");
          throw new InsufficientPermissionException(Page.SUBMIT_DATA_SERVLET,
              "Event CRF does not belong to current user", "1");
        }
      } else if (action.equals(ACTION_START_DOUBLE_DATA_ENTRY)) {
        if (ecb.getOwnerId() != ub.getId()) {
          return;
        } else {
          if (!DisplayEventCRFBean.initialDataEntryCompletedMoreThanTwelveHoursAgo(ecb) && !isSuper) {
            addPageMessage("You began data entry on this Event CRF, and it was marked complete less than 12 hours ago.  Therefore, you may not begin double data entry on it.");
            throw new InsufficientPermissionException(Page.SUBMIT_DATA_SERVLET,
                "owner attempting to start double data entry before 12 hours have passed", "1");
          } else {
            return;
          }
        }
      } else if (action.equals(ACTION_CONTINUE_INITIAL_DATA_ENTRY)) {
        if ((ecb.getValidatorId() == ub.getId()) || isSuper) {
          return;
        } else {
          addPageMessage("You did not begin double data entry on this Event CRF, so you may not resume data entry on it.");
          throw new InsufficientPermissionException(Page.SUBMIT_DATA_SERVLET,
              "Validation on Event CRF was not begun by current user", "1");
        }
      } else if (action.equals(ACTION_ADMINISTRATIVE_EDITING)) {
        if (isSuper) {
          return;
        } else {
          addPageMessage("You may not perform administrative editing on an Event CRF in this study.  Please change your active study or contact the Study Coordinator.");
          throw new InsufficientPermissionException(Page.SUBMIT_DATA_SERVLET,
              "no permission to perform administrative editing", "1");
        }
      } // end else if (action.equals(ACTION_ADMINISTRATIVE_EDITING))
    } // end else (for actions other than ACTION_START_INITIAL_DATA_ENTRY
  } // end mayProceed

  public static int getIntById(HashMap h, Integer key) {
    Integer value = (Integer) h.get(key);
    if (value == null) {
      return 0;
    } else {
      return value.intValue();
    }
  }

  /**
   * Assumes the Event CRF's data entry stage is not Uncompleted.
   * 
   * @param ecb
   *          An Event CRF which should be displayed in the table of contents.
   * @return A text link to the Table of Contents servlet for the bean.
   */
  public static String getLink(EventCRFBean ecb) {
    DataEntryStage stage = ecb.getStage();
    String answer = Page.TABLE_OF_CONTENTS_SERVLET.getFileName();

    answer = Page.TABLE_OF_CONTENTS_SERVLET.getFileName();
    answer += "?action=" + getActionForStage(ecb.getStage());
    answer += "&" + INPUT_ID + "=" + ecb.getId();

    return answer;
  }

  public static String getActionForStage(DataEntryStage stage) {
    if (stage.equals(DataEntryStage.UNCOMPLETED)) {
    }

    else if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY)) {
      return ACTION_CONTINUE_INITIAL_DATA_ENTRY;
    }

    else if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE)) {
      return ACTION_START_DOUBLE_DATA_ENTRY;
    }

    else if (stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
      return ACTION_CONTINUE_DOUBLE_DATA_ENTRY;
    }

    else if (stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE)) {
      return ACTION_ADMINISTRATIVE_EDITING;
    }

    return "";
  }

  public static ArrayList getSections(EventCRFBean ecb, DataSource ds) {
    SectionDAO sdao = new SectionDAO(ds);

    HashMap numItemsBySectionId = sdao.getNumItemsBySectionId();
    HashMap numItemsCompletedBySectionId = sdao.getNumItemsCompletedBySectionId(ecb);
    HashMap numItemsPendingBySectionId = sdao.getNumItemsPendingBySectionId(ecb);

    ArrayList sections = sdao.findAllByCRFVersionId(ecb.getCRFVersionId());

    for (int i = 0; i < sections.size(); i++) {
      SectionBean sb = (SectionBean) sections.get(i);

      int sectionId = sb.getId();
      Integer key = new Integer(sectionId);
      sb.setNumItems(getIntById(numItemsBySectionId, key));
      sb.setNumItemsCompleted(getIntById(numItemsCompletedBySectionId, key));
      sb.setNumItemsNeedingValidation(getIntById(numItemsPendingBySectionId, key));
      sections.set(i, sb);
    }

    return sections;
  }

  public static DisplayTableOfContentsBean getDisplayBean(EventCRFBean ecb, DataSource ds,
      StudyBean currentStudy) {
    DisplayTableOfContentsBean answer = new DisplayTableOfContentsBean();

    answer.setEventCRF(ecb);

    // get data
    StudySubjectDAO ssdao = new StudySubjectDAO(ds);
    StudySubjectBean ssb = (StudySubjectBean) ssdao.findByPK(ecb.getStudySubjectId());
    answer.setStudySubject(ssb);

    StudyEventDAO sedao = new StudyEventDAO(ds);
    StudyEventBean seb = (StudyEventBean) sedao.findByPK(ecb.getStudyEventId());
    answer.setStudyEvent(seb);

    SectionDAO sdao = new SectionDAO(ds);
    ArrayList sections = getSections(ecb, ds);
    answer.setSections(sections);

    // get metadata
    StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(ds);
    StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) seddao.findByPK(seb
        .getStudyEventDefinitionId());
    answer.setStudyEventDefinition(sedb);

    CRFVersionDAO cvdao = new CRFVersionDAO(ds);
    CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(ecb.getCRFVersionId());
    answer.setCrfVersion(cvb);

    CRFDAO cdao = new CRFDAO(ds);
    CRFBean cb = (CRFBean) cdao.findByPK(cvb.getCrfId());
    answer.setCrf(cb);

    EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(ds);
    EventDefinitionCRFBean edcb = edcdao.findByStudyEventDefinitionIdAndCRFId(sedb.getId(), cb
        .getId());
    answer.setEventDefinitionCRF(edcb);

    answer.setAction(getActionForStage(ecb.getStage()));

    return answer;
  }
}