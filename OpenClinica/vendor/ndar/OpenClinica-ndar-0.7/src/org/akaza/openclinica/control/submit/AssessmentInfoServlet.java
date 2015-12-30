/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SummaryDataEntryStatus;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.DisplayTableOfContentsBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.FormUsageCountBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.DiscrepancyValidator;
import org.akaza.openclinica.core.form.FormDiscrepancyNotes;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.FormUsageCountDAO;
import org.akaza.openclinica.exception.InconsistentStateException;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

public class AssessmentInfoServlet extends SecureController {
  // public static final String ACTION_START_INITIAL_DATA_ENTRY = "ide_s";

  // public static final String ACTION_CONTINUE_INITIAL_DATA_ENTRY = "ide_c";

  public static final String INPUT_INTERVIEWER = "interviewer";

  public static final String INPUT_INTERVIEW_DATE = "assessmentDate";

  public static final String INPUT_EVENT_CRF = "eventCRF";

  public static final String INPUT_EVENT = "studyEvent";

  // these are only for use with ACTION_START_INITIAL_DATA_ENTRY
  public static final String INPUT_EVENT_CRF_ID = "eventCRFId";

  public static final String INPUT_EVENT_DEFINITION_CRF_ID = "eventDefinitionCRFId";

  public static final String INPUT_CRF_VERSION_ID = "crfVersionId";

  public static final String INPUT_STUDY_EVENT_ID = "studyEventId";

  public static final String INPUT_SUBJECT_ID = "subjectId";
  
  private EventCRFBean ecb;

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
   */
  protected void mayProceed() throws InsufficientPermissionException {

    Role r = currentRole.getRole();

    if (!SubmitDataServlet.maySubmitData(ub, currentRole)) {
      String exceptionName = "no permission to perform data entry";
      String noAccessMessage = "You may not perform data entry on a CRF in this study.  Please change your active study or contact the Study Coordinator.";

      addPageMessage(noAccessMessage);
      throw new InsufficientPermissionException(Page.MENU, exceptionName, "1");
    }

  } // end mayProceed

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.core.SecureController#processRequest()
   */
  protected void processRequest() throws Exception {
    FormDiscrepancyNotes discNotes;
    FormProcessor fp = new FormProcessor(request);
    int eventCRFId = fp.getInt(INPUT_EVENT_CRF_ID, true);
    

    EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
   
    if (fp.isSubmitted()) {
      discNotes = (FormDiscrepancyNotes) session
          .getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
      if (discNotes == null) {
        discNotes = new FormDiscrepancyNotes();
        session.setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);

      }
      DiscrepancyValidator v = new DiscrepancyValidator(request, discNotes);

      if (currentStudy.getStudyParameterConfig().getInterviewerNameRequired().equals("true")) {

        v.addValidation(INPUT_INTERVIEWER, Validator.NO_BLANKS);
      }

     if (currentStudy.getStudyParameterConfig().getInterviewDateRequired().equals("true")) {
        v.addValidation(INPUT_INTERVIEW_DATE, Validator.NO_BLANKS);
     } 
     if (!StringUtil.isBlank(fp.getString(INPUT_INTERVIEW_DATE))) {
       v.addValidation(INPUT_INTERVIEW_DATE, Validator.IS_A_DATE);
       v.alwaysExecuteLastValidation(INPUT_INTERVIEW_DATE);
     }

     errors = v.validate();

     if (errors.isEmpty()) {
        logger.info("error is empty!");
        //logger.info("input_interviewer" + fp.getString(INPUT_INTERVIEWER));
        ecb.setInterviewerName(fp.getString(INPUT_INTERVIEWER));
        if (!StringUtil.isBlank(fp.getString(INPUT_INTERVIEW_DATE))) {
          ecb.setDateInterviewed(fp.getDate(INPUT_INTERVIEW_DATE));
        } else {
          ecb.setDateInterviewed(null);
        }

        if (ecdao == null) {
          ecdao = new EventCRFDAO(sm.getDataSource());
        }

        ecb.setUpdater(ub);
        ecb.setUpdatedDate(new Date());
        ecb = (EventCRFBean) ecdao.update(ecb);
        
        DisplayTableOfContentsBean toc = TableOfContentsServlet.getDisplayBean(ecb, sm.getDataSource(),
            currentStudy);
        request.setAttribute("toc", toc);

        // save discrepancy notes into DB
        FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) session
            .getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
        DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());

        AddNewSubjectServlet.saveFieldNotes(INPUT_INTERVIEWER, fdn, dndao, ecb.getId(), "EventCRF",
            currentStudy);
        AddNewSubjectServlet.saveFieldNotes(INPUT_INTERVIEW_DATE, fdn, dndao, ecb.getId(),
            "EventCRF", currentStudy);

        if (!ecdao.isQuerySuccessful()) {
            throw new InconsistentStateException(Page.SUBMIT_DATA_SERVLET,
            "A new Event CRF could not be updated due to a database error.");
        }
        logger.info("assessment info ok, go to initial data entry");
        request.setAttribute(INPUT_EVENT_CRF, ecb);
        request.setAttribute("submitted","0");
        Page target = Page.INITIAL_DATA_ENTRY_SERVLET.cloneWithNewFileName(Page.INITIAL_DATA_ENTRY_SERVLET.getFileName()
            + "?eventCRFId=" + ecb.getId());
        forwardPage(target);
        return;
      }

      String[] textFields = { INPUT_INTERVIEWER, INPUT_INTERVIEW_DATE };
      fp.setCurrentStringValuesAsPreset(textFields);

      setInputMessages(errors);
      setPresetValues(fp.getPresetValues());
      
      DisplayTableOfContentsBean toc = TableOfContentsServlet.getDisplayBean(ecb, sm.getDataSource(),
          currentStudy);
      request.setAttribute("toc", toc);
      forwardPage(Page.ASSESSMENT_INFO);
      return;
    }

    discNotes = new FormDiscrepancyNotes();
    session.setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);

    if (eventCRFId <= 0) {
      ecb = createEventCRF(ecdao);
      updatePresetValues(ecb);

      if (!ecb.isActive()) {
        throw new InconsistentStateException(Page.SUBMIT_DATA_SERVLET,
            "A new Event CRF could not be created due to a database error.");
      }

      StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
      StudyEventBean sEvent = (StudyEventBean) sedao.findByPK(ecb.getStudyEventId());
//TODO: Should event status be changed if SCHEDULED or PROPOSED? --AJF
      sEvent.setSummaryDataEntryStatus(SummaryDataEntryStatus.DATA_ENTRY_STARTED);
      sEvent.setUpdater(ub);
      sEvent.setUpdatedDate(new Date());
      sedao.update(sEvent);
      request.setAttribute(INPUT_EVENT, sEvent);
      
      DisplayTableOfContentsBean toc = TableOfContentsServlet.getDisplayBean(ecb, sm.getDataSource(),
          currentStudy);
      request.setAttribute("toc", toc);


      request.setAttribute(INPUT_EVENT_CRF, ecb);
      forwardPage(Page.ASSESSMENT_INFO);
    }
  }

  private void updatePresetValues(EventCRFBean ecb) {
    FormProcessor fp = new FormProcessor(request);
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

  /**
   * Creates a new Event CRF or update the exsiting one, that is, an event CRF
   * can be created but not item data yet, in this case, still consider it is
   * not started(called uncompleted before)
   * 
   * @return
   * @throws Exception
   */
  private EventCRFBean createEventCRF(EventCRFDAO ecdao) throws Exception {
    EventCRFBean newEcb;
    FormProcessor fp = new FormProcessor(request);
    // EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());

    int crfVersionId = fp.getInt(INPUT_CRF_VERSION_ID);
    int studyEventId = fp.getInt(INPUT_STUDY_EVENT_ID);
    int eventDefinitionCRFId = fp.getInt(INPUT_EVENT_DEFINITION_CRF_ID);
    int subjectId = fp.getInt(INPUT_SUBJECT_ID);
    int eventCRFId = fp.getInt(INPUT_EVENT_CRF_ID);

    logger.info("Creating event CRF.  Study id: " + currentStudy.getId() + "; CRF Version id: "
        + crfVersionId + "; Study Event id: " + studyEventId + "; Event Definition CRF id: "
        + eventDefinitionCRFId + "; Subject: " + subjectId);

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
    if (eventCRFId == 0) {// no event CRF created yet
      newEcb.setAnnotations("");
      newEcb.setCreatedDate(new Date());
      newEcb.setCRFVersionId(crfVersionId);
      newEcb.setInterviewerName("");
      if (currentStudy.getStudyParameterConfig().getInterviewerNameDefault().equals("blank")) {
        newEcb.setInterviewerName("");
      } else {
        // default will be event's owner name
        newEcb.setInterviewerName(sEvent.getOwner().getName());

      }
      if (!currentStudy.getStudyParameterConfig().getInterviewDateDefault().equals("blank")) {
        if (sEvent.getDateStarted() != null) {
          newEcb.setDateInterviewed(sEvent.getDateStarted());// default date
        } else {
          // logger.info("evnet start date is null, so date interviewed is
          // null");
          newEcb.setDateInterviewed(null);
        }
      } else {
        newEcb.setDateInterviewed(null);
        // logger.info("date interviewed is null,getInterviewDateDefault() is
        // blank");
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
    }

    logger.info("return the created event crf bean");
    return newEcb;
  }

}
