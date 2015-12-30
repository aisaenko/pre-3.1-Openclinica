/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.submit.AddNewSubjectServlet;
import org.akaza.openclinica.core.form.DiscrepancyValidator;
import org.akaza.openclinica.core.form.FormDiscrepancyNotes;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * Performs updating study event action
 */
public class UpdateStudyEventServlet extends SecureController {
  public static final String EVENT_ID = "event_id";

  public static final String STUDY_SUBJECT_ID = "ss_id";

  public static final String EVENT_BEAN = "event";

  public static final String EVENT_DEFINITION_BEAN = "eventDefinition";

  public static final String SUBJECT_EVENT_STATUS_ID = "statusId";

  public static final String INPUT_STARTDATE_PREFIX = "start";

  public static final String INPUT_LOCATION = "location";

  public void mayProceed() throws InsufficientPermissionException {
  }

  public void processRequest() throws Exception {
  	FormDiscrepancyNotes discNotes= null;
    FormProcessor fp = new FormProcessor(request);
    int studyEventId = fp.getInt(EVENT_ID, true);
    int studySubjectId = fp.getInt(STUDY_SUBJECT_ID, true);

    if (studyEventId == 0 || studySubjectId == 0) {
      addPageMessage("Please choose a study event to edit.");
      request.setAttribute("id", new Integer(studySubjectId).toString());
      forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
      return;
    }
    request.setAttribute(STUDY_SUBJECT_ID, new Integer(studySubjectId).toString());
    StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());

    StudyEventBean studyEvent = (StudyEventBean) sedao.findByPK(studyEventId);

    //only owner, admins, and study director/coordinator can update
    if (ub.getId() != studyEvent.getOwnerId()) {
      if (!ub.isSysAdmin() && !currentRole.getRole().equals(Role.STUDYDIRECTOR)
          && !currentRole.getRole().equals(Role.COORDINATOR)) {
        addPageMessage("You don't have correct privilege in your current active study. "
            + "Please change your active study or contact your sysadmin.");
        request.setAttribute("id", new Integer(studySubjectId).toString());
        forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
        return;
      }
    }

    ArrayList statuses = SubjectEventStatus.toArrayList();
    statuses.remove(SubjectEventStatus.COMPLETED);
    statuses.remove(SubjectEventStatus.DATA_ENTRY_STARTED);
    request.setAttribute("statuses", statuses);

    String action = fp.getString("action");
    StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
    StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(studyEvent
        .getStudyEventDefinitionId());      
    request.setAttribute(EVENT_DEFINITION_BEAN, sed);
    if (action.equalsIgnoreCase("submit")) {
      discNotes = (FormDiscrepancyNotes)session.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
      DiscrepancyValidator v = new DiscrepancyValidator(request, discNotes);
      SubjectEventStatus ses = SubjectEventStatus.get(fp.getInt(SUBJECT_EVENT_STATUS_ID));
      studyEvent.setSubjectEventStatus(ses);
      if (ses.equals(SubjectEventStatus.SKIPPED) || ses.equals(SubjectEventStatus.STOPPED)) {
        studyEvent.setStatus(Status.UNAVAILABLE);
        EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
        ArrayList eventCRFs = ecdao.findAllByStudyEvent(studyEvent);
        for (int i = 0; i < eventCRFs.size(); i++) {
          EventCRFBean ecb = (EventCRFBean) eventCRFs.get(i);
          ecb.setStatus(Status.UNAVAILABLE);
          ecb.setUpdater(ub);
          ecb.setUpdatedDate(new Date());
          ecdao.update(ecb);
        }
      }

      v.addValidation(INPUT_STARTDATE_PREFIX, Validator.IS_DATE_TIME);
      v.alwaysExecuteLastValidation(INPUT_STARTDATE_PREFIX);
      v.addValidation(INPUT_LOCATION, Validator.NO_BLANKS);
      HashMap errors = v.validate();
      if (!errors.isEmpty()) {
        setInputMessages(errors);
        String prefixes[] = { INPUT_STARTDATE_PREFIX };
        fp.setCurrentDateTimeValuesAsPreset(prefixes);
        setPresetValues(fp.getPresetValues());

        studyEvent.setLocation(fp.getString(INPUT_LOCATION));

        request.setAttribute("changeDate", fp.getString("changeDate"));
        request.setAttribute(EVENT_BEAN, studyEvent);
        forwardPage(Page.UPDATE_STUDY_EVENT);

      } else {
        logger.info("no validation error");
        Date start = fp.getDateTime(INPUT_STARTDATE_PREFIX);
        studyEvent.setDateStarted(start);
        studyEvent.setDateEnded(start);
        studyEvent.setLocation(fp.getString(INPUT_LOCATION));

      }
      logger.info("update study event...");
      studyEvent.setUpdater(ub);
      studyEvent.setUpdatedDate(new Date());
      sedao.update(studyEvent);
      
//    save discrepancy notes into DB
      FormDiscrepancyNotes fdn = (FormDiscrepancyNotes)session.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
      DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());
      
      AddNewSubjectServlet.saveFieldNotes(INPUT_LOCATION, fdn, dndao, studyEvent.getId(), "studyEvent", currentStudy);
      AddNewSubjectServlet.saveFieldNotes(INPUT_STARTDATE_PREFIX, fdn, dndao, studyEvent.getId(), "studyEvent", currentStudy);
      
      
      addPageMessage("The study event was updated successfully.");
      request.setAttribute("id", new Integer(studySubjectId).toString());
      session.removeAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
      forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);

    } else {
      logger.info("no action, go to update page");     

      HashMap presetValues = new HashMap();
      presetValues.put(INPUT_STARTDATE_PREFIX + "Hour", new Integer(12));

      String dateValue = sdf.format(studyEvent.getDateStarted());
      presetValues.put(INPUT_STARTDATE_PREFIX + "Date", dateValue);
      setPresetValues(presetValues);

      request.setAttribute(EVENT_BEAN, studyEvent);
      discNotes = new FormDiscrepancyNotes();
      session.setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);

      forwardPage(Page.UPDATE_STUDY_EVENT);
    }//else

  }

  protected String getAdminServlet() {
    if (ub.isSysAdmin()) {
      return SecureController.ADMIN_SERVLET_CODE;
    } else {
      return "";
    }
  }

}