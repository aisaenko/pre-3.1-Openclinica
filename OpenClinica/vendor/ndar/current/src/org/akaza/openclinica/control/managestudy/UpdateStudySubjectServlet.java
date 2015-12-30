/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.core.SummaryDataEntryStatus;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.bean.managestudy.StudyTemplateBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.managestudy.StudyTemplateEventDefBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.submit.AddNewSubjectServlet;
import org.akaza.openclinica.core.form.DiscrepancyValidator;
import org.akaza.openclinica.core.form.FormDiscrepancyNotes;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyTemplateDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.managestudy.StudyTemplateEventDefDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu Processes request to update a study subject
 */
public class UpdateStudySubjectServlet extends SecureController {
  /**
   * Checks whether the user has the right permission to proceed function
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }

    if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
        || currentRole.getRole().equals(Role.COORDINATOR)) {
      return;
    }

    addPageMessage("You don't have correct privilege in your current active study. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.LIST_USER_IN_STUDY_SERVLET,
        "not study director", "1");
  }

  public void processRequest() throws Exception {
    FormDiscrepancyNotes discNotes = null;
    SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
    StudySubjectDAO subdao = new StudySubjectDAO(sm.getDataSource());
    FormProcessor fp = new FormProcessor(request);
    int studySubId = fp.getInt("id", true);// studySubjectId

    if (studySubId == 0) {
      addPageMessage("Please choose a study subject to edit.");
      forwardPage(Page.LIST_STUDY_SUBJECT_SERVLET);
    } else {
      String action = fp.getString("action", true);
      if (StringUtil.isBlank(action)) {
        addPageMessage("No action specified.");
        forwardPage(Page.LIST_STUDY_SUBJECT_SERVLET);
        return;
      }

      StudySubjectBean sub = (StudySubjectBean) subdao.findByPK(studySubId);

      StudyTemplateDAO stdao = new StudyTemplateDAO(sm.getDataSource());
      StudyGroupDAO sgdao = new StudyGroupDAO(sm.getDataSource());
      SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(sm.getDataSource());
      ArrayList groupMaps = (ArrayList) sgmdao.findAllByStudySubject(studySubId);

      HashMap gMaps = new HashMap();
      for (int i = 0; i < groupMaps.size(); i++) {
        SubjectGroupMapBean groupMap = (SubjectGroupMapBean) groupMaps.get(i);
        gMaps.put(new Integer(groupMap.getStudyGroupClassId()), groupMap);

      }
      if (!"submit".equalsIgnoreCase(action)) {
        ArrayList classes = (ArrayList) stdao.findAllActiveByStudy(currentStudy);
        for (int i = 0; i < classes.size(); i++) {
          StudyTemplateBean template = (StudyTemplateBean) classes.get(i);
          ArrayList studyGroups = sgdao.findAllByGroupClass(template);
          template.setStudyGroups(studyGroups);
          SubjectGroupMapBean gMap = (SubjectGroupMapBean) gMaps.get(new Integer(template.getId()));
          if (gMap != null) {
            template.setStudyGroupId(gMap.getStudyGroupId());
            template.setGroupNotes(gMap.getNotes());
            template.setStudyTemplateStartDate(gMap.getStudyTemplateStartDate());
          }
          
          setEditableForTemplate(template, studySubId);
        }

        session.setAttribute("groups", classes);
      }

      if ("show".equalsIgnoreCase(action)) {

        session.setAttribute("studySub", sub);
        discNotes = new FormDiscrepancyNotes();
        session.setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);

        forwardPage(Page.UPDATE_STUDY_SUBJECT);
      } else if ("confirm".equalsIgnoreCase(action)) {
        confirm(sgdao);

      } else if ("submit".equalsIgnoreCase(action)) {// submit to DB
        StudySubjectBean subject = (StudySubjectBean) session.getAttribute("studySub");
        subject.setUpdater(ub);
        subdao.update(subject);

        // save discrepancy notes into DB
        FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) session
            .getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
        DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());
        AddNewSubjectServlet.saveFieldNotes("enrollmentDate", fdn, dndao, subject.getId(),
            "studySub", currentStudy);

        ArrayList groups = (ArrayList) session.getAttribute("groups");
        if (!groups.isEmpty()) {
          for (int i = 0; i < groups.size(); i++) {
            StudyTemplateBean stb = (StudyTemplateBean) groups.get(i);
            SubjectGroupMapBean sgm = new SubjectGroupMapBean();
            // the original subject group map bean from DB
            SubjectGroupMapBean gMap = (SubjectGroupMapBean) gMaps.get(new Integer(stb.getId()));

            sgm.setStudyGroupId(stb.getStudyGroupId());
            sgm.setNotes(stb.getGroupNotes());
            sgm.setStudyGroupClassId(stb.getId());
            sgm.setStudySubjectId(subject.getId());
            sgm.setStatus(Status.AVAILABLE);      
            //logger.info("template start date:" + stb.getStudyTemplateStartDate());
            sgm.setStudyTemplateStartDate(stb.getStudyTemplateStartDate());

            if (sgm.getStudyGroupId() > 0) {
              if (gMap != null && gMap.getId() > 0 ) {
                
                if (gMap.getStatus().equals(Status.AVAILABLE)) {
                    sgm.setUpdater(ub);
                    sgm.setUpdatedDate(new java.util.Date());
                    sgm.setId(gMap.getId());
                    sgmdao.update(sgm);

                  
                if ((gMap.getStudyTemplateStartDate() != null)
                    && (stb.getStudyTemplateStartDate() != null)
                    && (!stb.getStudyTemplateStartDate().equals(gMap.getStudyTemplateStartDate()))) {
                    // user changed template start date, need to see if the rescheduling will happen
                    logger.info("need to update the existing subject group map");
                  
                   rescheduleEvents(gMap, subject,stb);                  
                 
                  } else if ((gMap.getStudyTemplateStartDate() ==null) 
                      &&(stb.getStudyTemplateStartDate() != null)) {
                    //user added a start date to an existing template, so need to
                    //create events based on the tempalte and the start date
                    logger.info("need to create new events");
                    scheduleEvents (stb, subject);
                  }
                } 

              } //if gMap != null && gMap.getId() > 0
              else {
                logger.info("need to create new subject group map");
                sgm.setOwner(ub);
                sgm.setCreatedDate(new java.util.Date());
                sgmdao.create(sgm);

                // subject is assigned into a new group template, so
                // need to create prosposed events for this subject
                if (stb.getStudyTemplateStartDate() != null) {
                  logger.info("template start date is not null");
                  scheduleEvents (stb, subject) ;
                  
                }// end if stb.getStudyTemplateStartDate() != null

              }
            } //if sgm.getStudyGroupId() > 0
            else {
              if (gMap != null && gMap.getId() > 0) {
              //need to remove this group template
                sgm.setStatus(Status.DELETED);  
                sgm.setUpdater(ub);
                sgm.setUpdatedDate(new java.util.Date());
                sgm.setId(gMap.getId());
                sgmdao.update(sgm);
             
              }
            }

          }
        }

        addPageMessage("The study subject has been updated successfully.");
        session.removeAttribute("studySub");
        session.removeAttribute("groups");
        session.removeAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
        request.setAttribute("id", new Integer(studySubId).toString());

        forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
      } else {
        addPageMessage("No action specified.");
        forwardPage(Page.LIST_STUDY_SUBJECT_SERVLET);
        return;
      }

    }
  }

  /**
   * Processes 'confirm' request, validate the study subject object
   * 
   * @param sub
   * @throws Exception
   */
  private void confirm(StudyGroupDAO sgdao) throws Exception {
    ArrayList classes = (ArrayList) session.getAttribute("groups");
    StudySubjectBean sub = (StudySubjectBean) session.getAttribute("studySub");
    SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
    
    
    
    FormDiscrepancyNotes discNotes = (FormDiscrepancyNotes) session
        .getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
    DiscrepancyValidator v = new DiscrepancyValidator(request, discNotes);
    FormProcessor fp = new FormProcessor(request);

    v.addValidation("label", Validator.NO_BLANKS);

    String eDateString = fp.getString("enrollmentDate");
    if (!StringUtil.isBlank(eDateString)) {
      v.addValidation("enrollmentDate", Validator.IS_A_DATE);
      v.alwaysExecuteLastValidation("enrollmentDate");      
    }   
    

    if (!classes.isEmpty()) {
      for (int i = 0; i < classes.size(); i++) {
        StudyTemplateBean stb = (StudyTemplateBean) classes.get(i);
        int groupId = fp.getInt("studyGroupId" + i);
        String notes = fp.getString("notes" + i);

        v.addValidation("notes" + i, Validator.LENGTH_NUMERIC_COMPARISON,
            NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);

        String studyTemplateStartDateStr = fp.getString("studyTemplateStartDate" + i);
        
        if (stb.getStudyTemplateStartDate() != null) {
          //if the template start date was not null before, user cannot remove it
          v.addValidation("studyTemplateStartDate" + i, Validator.IS_A_DATE);
        }
        
        if (!StringUtil.isBlank(studyTemplateStartDateStr)) {
          v.addValidation("studyGroupId" + i, Validator.IS_REQUIRED);
          v.addValidation("studyTemplateStartDate" + i, Validator.IS_A_DATE);
          stb.setStudyTemplateStartDate(fp.getDate("studyTemplateStartDate" + i));
        } else {
          stb.setStudyTemplateStartDate(null);
        }
        stb.setStudyGroupId(groupId);
        stb.setGroupNotes(notes);
        if (groupId > 0) {
          StudyGroupBean sgb = (StudyGroupBean) sgdao.findByPK(groupId);
          stb.setStudyGroupName(sgb.getName());
        }
      }
    }
    session.setAttribute("groups", classes);
    errors = v.validate();

    if (!StringUtil.isBlank(fp.getString("label"))) {
      StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());

      StudySubjectBean sub1 = (StudySubjectBean) ssdao.findAnotherBySameLabel(fp.getString("label")
          .trim(), currentStudy.getId(), sub.getId());

      if (sub1.getId() > 0) {
        Validator
            .addError(errors, "label",
                "Study Subject ID has been used by another subject in current study, please choose a unique one.");
      }
    }

    sub.setLabel(fp.getString("label"));
    sub.setSecondaryLabel(fp.getString("secondaryLabel"));

    java.util.Date enrollDate = null;
    try {
      sdf.setLenient(false);
      if (!StringUtil.isBlank(eDateString)) {
        enrollDate = sdf.parse(eDateString);
        SubjectBean subject = (SubjectBean)sdao.findByPK(sub.getSubjectId());
        
        Date birthDate = subject.getDateOfBirth();
        int year =0;
        int yearEnroll =0;
        if (!subject.isDobCollected()) {
          if (birthDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(birthDate);
            year = cal.get(Calendar.YEAR);
            
            cal.setTime(enrollDate);
            yearEnroll = cal.get(Calendar.YEAR);
            
            if (yearEnroll < year) {
              Validator
              .addError(errors, "enrollmentDate",
                  "Enrollment date should be after the subject's date of birth.");
       
            }
            
          } 
        } else {
          if (!enrollDate.after(birthDate)) {
            Validator
            .addError(errors, "enrollmentDate",
                "Enrollment date should be after the subject's date of birth.");
     
          }
          
        }
      }
    } catch (ParseException fe) {

    }
    sub.setEnrollmentDate(enrollDate);
    session.setAttribute("studySub", sub);
    if (!errors.isEmpty()) {
      logger.info("has errors");
      if (StringUtil.isBlank(sub.getLabel())) {
        addPageMessage("You must enter a Study Subject ID for identifying the subject. "
            + "This may be an external ID number containing letters, numbers, and dashes, "
            + "or you may simply enter the Study Subject ID listed below. "
            + "The Study Subject ID should not contain Protected Health Information such as name or SSN.");
      } else {
        StudySubjectDAO subdao = new StudySubjectDAO(sm.getDataSource());
        StudySubjectBean sub1 = (StudySubjectBean) subdao.findAnotherBySameLabel(sub.getLabel(),
            sub.getStudyId(), sub.getId());
        if (sub1.getId() > 0) {
          addPageMessage("Study Subject ID has been used by another subject in current study, please choose a unique one.");
        }
      }

      request.setAttribute("formMessages", errors);
      forwardPage(Page.UPDATE_STUDY_SUBJECT);

    } else {
      forwardPage(Page.UPDATE_STUDY_SUBJECT_CONFIRM);
    }

  } 
  
  /**
   * Sets the editable property for a template
   * if there is an event created by this template is no longer proposed (not first event)
   * or no longer proposed or scheduled, user should not be able to change the template start date
   * or remove the user from this template
   * @param template
   * @param studySubjectId
   */
  private void setEditableForTemplate(StudyTemplateBean template, int studySubjectId) {
    StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
    ArrayList events = sedao.findAllByTemplateAndStudySubject(template.getId(), studySubjectId);
    int pos = 0;
    for (int i=0; i< events.size(); i++) {
      StudyEventBean event = (StudyEventBean)events.get(i);
      pos = i;
      if (pos > 0 && event.getSubjectEventStatus().getId() > 1 ) {
        template.setEditable(false);
        break;
      }
      else if ((pos == 0) && event.getSubjectEventStatus().getId() > 2)  {
        template.setEditable(false);
        break;
      }
    }
    //logger.info("template editable:" + template.isEditable());
 
    
  }
  
  /**
   * Creates the events based on a template for a study subject
   * @param stb
   * @param subject
   */
  private void scheduleEvents (StudyTemplateBean stb, StudySubjectBean subject) {
    StudyTemplateEventDefDAO steddao = new StudyTemplateEventDefDAO(sm
        .getDataSource());
    ArrayList eventSequences = steddao.findAllActiveByStudyTemplate(stb.getId());
    StudyEventBean previous = new StudyEventBean();
    for (int k = 0; k < eventSequences.size(); k++) {
      StudyTemplateEventDefBean sted = (StudyTemplateEventDefBean) eventSequences
          .get(k);
     
      logger.info("will create proposed events for the new subject");

      StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
      StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
      StudyEventBean se = new StudyEventBean();

      se.setStudyTemplateEventDefId(sted.getId());

      // find the default location
      se.setLocation(currentStudy.getFacilityCity());

      // calculate the start date
      Calendar cal = Calendar.getInstance();
      if (k == 0) {// the very first event in the template
        se.setSubjectEventStatus(SubjectEventStatus.SCHEDULED);
        se.setDateStarted(stb.getStudyTemplateStartDate());
      } else {
        cal.setTime(previous.getDateStarted());
        StudyTemplateEventDefBean prevSted = (StudyTemplateEventDefBean) eventSequences
            .get(k - 1);
        cal.add(Calendar.DATE, new Float((prevSted.getEventDuration()==null ? 0 : prevSted.getEventDuration().floatValue())
            + (prevSted.getIdealTimeToNextEvent()==null ? 0 : prevSted.getIdealTimeToNextEvent())).intValue());
        se.setDateStarted(cal.getTime());
        se.setSubjectEventStatus(SubjectEventStatus.PROPOSED);
      }

      // calculate the end date
      cal.setTime(se.getDateStarted());
      cal.add(Calendar.DATE, (sted.getEventDuration()==null ? new Float(0) : sted.getEventDuration()).intValue());
      se.setDateEnded(cal.getTime());

      se.setOwner(ub);
      se.setStudyEventDefinitionId(sted.getStudyEventDefinitionId());
      se.setStatus(Status.AVAILABLE);
      se.setStudySubjectId(subject.getId());
      
      se.setSummaryDataEntryStatus(SummaryDataEntryStatus.NOT_STARTED);
      StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(sted
          .getStudyEventDefinitionId());
      se.setSampleOrdinal(sedao.getMaxSampleOrdinal(sed, subject) + 1);
      sedao.create(se);
      previous = se;

    }
  }
  
  
  /**
   * Reschedules the events based on the template info in a subject group map object
   * @param gMap
   * @param subject
   * @param stb
   */
  private void rescheduleEvents(SubjectGroupMapBean gMap, StudySubjectBean subject,
      StudyTemplateBean stb) {
    StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
    ArrayList events = sedao.findAllByTemplateAndStudySubject(gMap.getStudyGroupClassId(), subject
        .getId());

    StudyTemplateEventDefDAO steddao = new StudyTemplateEventDefDAO(sm.getDataSource());
    ArrayList templates = steddao.findAllActiveByStudyTemplate(stb.getId());

    if (events.size() > 1) {// more than one event
      StudyEventBean first = (StudyEventBean) events.get(0);
      Date originalDate = first.getDateStarted();

      boolean isFirstStarted = false;
      if (first.getSubjectEventStatus().getId() > 2) {
        // no need to do recheduling
        isFirstStarted = true;
      } else {
        logger.info("need to update the first event");
        long diffMillis = first.getDateStarted().getTime() - first.getDateEnded().getTime();
        // Get difference in days
        long diffDays = Math.abs(diffMillis / (24 * 60 * 60 * 1000));

        logger.info("use diff as duration" + diffDays);
        // the first event 's start date is changed
        first.setDateStarted(stb.getStudyTemplateStartDate());

        Calendar cal = Calendar.getInstance();
        // calculate the new end date
        cal.setTime(first.getDateStarted());
        cal.add(Calendar.DATE, new Float(diffDays).intValue());
        first.setDateEnded(cal.getTime());

        first.setUpdater(ub);
        first.setUpdatedDate(new java.util.Date());
        sedao.update(first);
      }

      if (isFirstStarted == false) {
        // after, find the first proposed event in the subsequent events
        ArrayList proposedEvents = UpdateStudyEventServlet.findProposedAfterCurrent(first, events);
        if (proposedEvents.size() > 0) {

          StudyEventBean firstProposed = (StudyEventBean) proposedEvents.get(0);
          logger.info("first proposed's start date:" + firstProposed.getDateStarted());

          if (currentStudy.getStudyParameterConfig().getReschedule().equals(
              "reschedule based on previous event")) {
            boolean isOutOfWindow = UpdateStudyEventServlet.isEventOutOfWindowOfPrevious(first,
                firstProposed, events, templates);

            if (isOutOfWindow) {
              boolean needReschedule = true;

              int pos = UpdateStudyEventServlet.findFirstNotProposedAfterCurrent(first, events);

              if (pos == 0) {
                // there is a non-proposed event next to the current
                // or the current is the last event
                needReschedule = false;
              } else {
                needReschedule = true;                

              }

              if (needReschedule) {
                logger.info("rescheduling study events based on previous...");
                long diffMillis = first.getDateStarted().getTime() - originalDate.getTime();
                // Get difference in days
                long diffDays = diffMillis / (24 * 60 * 60 * 1000);

                Calendar cal = Calendar.getInstance();

                ArrayList toBeRescheduled = proposedEvents;
                if (pos == -1) {
                  // all the events after current are proposed
                } else if (pos > 0) {
                  toBeRescheduled = UpdateStudyEventServlet.findProposedInBetween(first, pos,
                      events);

                }

                for (int k = 0; k < toBeRescheduled.size(); k++) {
                  StudyEventBean pro = (StudyEventBean) proposedEvents.get(k);
                  logger.info("old start" + pro.getDateStarted());

                  long millis = pro.getDateStarted().getTime() - pro.getDateEnded().getTime();
                  // Get difference in days between end and start
                  long days = Math.abs(millis / (24 * 60 * 60 * 1000));

                  cal.setTime(pro.getDateStarted());
                  cal.add(Calendar.DATE, new Float(diffDays).intValue());
                  pro.setDateStarted(cal.getTime());

                  logger.info("new start" + pro.getDateStarted());

                  cal.setTime(pro.getDateStarted());
                  cal.add(Calendar.DATE, new Float(days).intValue());
                  pro.setDateEnded(cal.getTime()); // new end date

                  pro.setUpdater(ub);
                  pro.setUpdatedDate(new Date());
                  sedao.update(pro);

                }
              }

            }
        
        
      } else {// reschedule based on first event
        logger.info("reschedule based on the first event");
        boolean isOutOfWindow = UpdateStudyEventServlet.isEventOutOfWindowOfFirst(firstProposed, first, events, templates);
        
        if (isOutOfWindow) {
          logger.info("rescheduling study events based on first...");
          for (int k= 0; k<proposedEvents.size(); k++) {         
            StudyEventBean proposedEvent = (StudyEventBean)proposedEvents.get(k);                  
            proposedEvent= UpdateStudyEventServlet.rescheduleBasedOnFirst(first, proposedEvent,templates);                   
            proposedEvent.setUpdater(ub);
            proposedEvent.setUpdatedDate(new Date());
            sedao.update(proposedEvent);
            
          }
        }               
                        
      }                
      
      
      
     } // if proposedEvents.size()>0
    }

    } //if isFirstStarted== false
     
  }

}