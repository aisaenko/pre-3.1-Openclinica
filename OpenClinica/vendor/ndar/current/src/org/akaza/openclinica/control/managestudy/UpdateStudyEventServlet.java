/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyTemplateEventDefBean;
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
import org.akaza.openclinica.dao.managestudy.StudyTemplateEventDefDAO;
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
    
    Date originalDate = studyEvent.getDateStarted();
    //request.setAttribute("isStartDateEditable", true);
    int templateEventDefId = studyEvent.getStudyTemplateEventDefId();
       
       
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
    request.setAttribute("statuses", statuses);
    //TODO: setAttribute for "dataEntryStatus"? --AJF

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
      if (ses.equals(SubjectEventStatus.NO_SHOW_NO_RESCHEDULE) || ses.equals(SubjectEventStatus.CANCELED_NO_RESCHEDULE)
    		  || ses.equals(SubjectEventStatus.NO_SHOW_RESCHEDULE) || ses.equals(SubjectEventStatus.CANCELED_RESCHEDULE)) {
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
      logger.info("studyEvent.getSubjectEventStatus().getId():" +studyEvent.getSubjectEventStatus().getId());
      if (studyEvent.getSubjectEventStatus().getId()<3) {
       v.addValidation(INPUT_STARTDATE_PREFIX, Validator.IS_DATE_TIME);
       v.alwaysExecuteLastValidation(INPUT_STARTDATE_PREFIX);
      }
      
      v.addValidation(INPUT_LOCATION, Validator.NO_BLANKS);
      
      HashMap errors = v.validate();
      
      if (!errors.isEmpty()) {
        logger.info("there are validation errors");
        setInputMessages(errors);
        String prefixes[] = { INPUT_STARTDATE_PREFIX };
        fp.setCurrentDateTimeValuesAsPreset(prefixes);
        setPresetValues(fp.getPresetValues());

        studyEvent.setLocation(fp.getString(INPUT_LOCATION));

        request.setAttribute("changeDate", fp.getString("changeDate"));
        request.setAttribute(EVENT_BEAN, studyEvent);
        forwardPage(Page.UPDATE_STUDY_EVENT); 
        return;

      } else {
        logger.info("no validation error");
        if (studyEvent.getSubjectEventStatus().getId()<3) {
          Date start = fp.getDateTime(INPUT_STARTDATE_PREFIX);
          studyEvent.setDateStarted(start);        
        }
        
        studyEvent.setLocation(fp.getString(INPUT_LOCATION));        
              
        // if the start date is changed, and this event was created based on
        // group template, we need to update the other events which are still proposed
        //and happen after this event (only apply to the first event in template)
        
        if (templateEventDefId ==0) {//this event was not created by a template
          studyEvent.setDateEnded(studyEvent.getDateStarted());
        } 
        else {
          StudyTemplateEventDefDAO steddao = new StudyTemplateEventDefDAO(sm
              .getDataSource());
          StudyTemplateEventDefBean sted = (StudyTemplateEventDefBean)steddao.findByPK(templateEventDefId);
          ArrayList events = sedao.findAllByTemplateAndStudySubject(sted.getStudyTemplateId(), studySubjectId);
          ArrayList templates = steddao.findAllActiveByStudyTemplate(sted.getStudyTemplateId());
          
          if (events.size()>1) {//more than one event
            
            long diffMillis = studyEvent.getDateEnded().getTime()- originalDate.getTime();
            //Get difference in days
            long diffDays = Math.abs(diffMillis/(24*60*60*1000));
            
            logger.info("use diff as duration:" + diffDays);
            
            Calendar cal = Calendar.getInstance();
            
            cal.setTime(studyEvent.getDateStarted());
            cal.add(Calendar.DATE, new Float(diffDays).intValue());
            studyEvent.setDateEnded(cal.getTime());
            
            
            StudyEventBean first = (StudyEventBean) events.get(0);
            
            if (first.getId() == studyEvent.getId()) {
              logger.info("the first event's start date is changed");
              first.setDateStarted(studyEvent.getDateStarted());
            }
            
              //after studyEvent, find the first proposed event in the subsequent events
              ArrayList proposedEvents = findProposedAfterCurrent(studyEvent, events);
              if (proposedEvents.size()>0) {
                
              StudyEventBean firstProposed = (StudyEventBean)proposedEvents.get(0);
              logger.info("first proposed's start date:" + firstProposed.getDateStarted());
              
              if (currentStudy.getStudyParameterConfig().
                  getReschedule().equals("reschedule based on previous event")){
                boolean isOutOfWindow = isEventOutOfWindowOfPrevious(studyEvent,firstProposed, events, templates);
               
                if (isOutOfWindow) {
                  boolean needReschedule = true;
                  
                  int pos = findFirstNotProposedAfterCurrent(studyEvent, events);
                  
                  if ( pos == 0) { 
                    //there is a non-proposed event next to the current
                    //or the current is the last event
                    needReschedule = false;
                  } else {                    
                    needReschedule = true;;
                    
                  }
                                    
                  if (needReschedule) {
                    logger.info("rescheduling study events based on previous...");
                    diffMillis = studyEvent.getDateStarted().getTime()- originalDate.getTime();
                    //Get difference in days
                    diffDays = diffMillis/(24*60*60*1000);                     

                    logger.info("diffDays" + diffDays);
                    
                    ArrayList toBeRescheduled = proposedEvents;
                    
                    //pos is the position of the first non-proposed event after current 
                    //if pos= -1, all the events after current are proposed                                         
                     
                    if (pos >0) {                      
                      toBeRescheduled = findProposedInBetween(studyEvent, pos, events);                      
                    }
                    
                    for (int i= 0; i< toBeRescheduled.size(); i++) { 
                      StudyEventBean pro =(StudyEventBean) proposedEvents.get(i);
                      logger.info("old start" + pro.getDateStarted());
                      
                      long millis = pro.getDateStarted().getTime()- pro.getDateEnded().getTime();
                      //Get difference in days between end and start
                      long days = Math.abs(millis/(24*60*60*1000)); 
                      
                      cal.setTime(pro.getDateStarted());
                      cal.add(Calendar.DATE, new Float(diffDays).intValue());
                      pro.setDateStarted(cal.getTime());
                    
                      logger.info("new start" + pro.getDateStarted());           
                       
                      
                      cal.setTime(pro.getDateStarted());
                      cal.add(Calendar.DATE, new Float(days).intValue());
                      pro.setDateEnded(cal.getTime());     // new end date                 
                   
                      pro.setUpdater(ub);
                      pro.setUpdatedDate(new Date());
                      sedao.update(pro);
                    
                     }
                  }
                  
                  
                }
                
                
              } else {//reschedule based on first event
                logger.info("reschedule based on the first event");
                boolean isOutOfWindow = isEventOutOfWindowOfFirst(firstProposed, first, events, templates);
                
                if (isOutOfWindow) {
                  logger.info("rescheduling study events based on first...");
                  for (int i= 0; i<proposedEvents.size(); i++) {         
                    StudyEventBean proposedEvent = (StudyEventBean)proposedEvents.get(i);                  
                    proposedEvent= rescheduleBasedOnFirst(first, proposedEvent,templates);                   
                    proposedEvent.setUpdater(ub);
                    proposedEvent.setUpdatedDate(new Date());
                    sedao.update(proposedEvent);
                    
                  }
                }               
                                
              }
              
              
              
              
            } // if proposedEvents.size()>0
           
          }// if more than one event
          
        }
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
  
 public static int findFirstNotProposedAfterCurrent(StudyEventBean current, ArrayList events) {
    
    int currentPos = 0;
    for (int i=0; i<events.size();i++) {
      StudyEventBean event = (StudyEventBean)events.get(i);
      if (event.getId() == current.getId()){
        currentPos =i;
        break;
     }
    }
    
    if (currentPos == events.size()-1) {
      return 0;
    }
    if (currentPos < events.size()-1) {
      for (int i= currentPos + 1; i<events.size();i++) {
        StudyEventBean event = (StudyEventBean)events.get(i);
        if (!event.getSubjectEventStatus().equals(SubjectEventStatus.PROPOSED)){
          if (i == currentPos + 1) {// there is a non-proposed event next to the current
            return 0;
          }
          return i;
        }
      }
    }
    return -1;
  }
  
  /**
   * Finds all the proposed events between current event and the first non-proposed event after current
   * @param events
   * @return the arraylist of proposed events in between
   */
  public static ArrayList findProposedInBetween(StudyEventBean current, int end, ArrayList events) {
    ArrayList proposedEvents = new ArrayList();
    
    int start = 0;
     if (!events.isEmpty()) {
       for (int i=0; i<events.size(); i++) {
         StudyEventBean event = (StudyEventBean)events.get(i);
         if (event.getId()== current.getId()){
         start = i;
         break;
         }
       }  
       if ((start < events.size()-1) && (end < events.size()-1)) {
         for (int i=start+1; i< end ; i++) {
           StudyEventBean event = (StudyEventBean)events.get(i);
            if (event.getSubjectEventStatus().equals(SubjectEventStatus.PROPOSED)){
              proposedEvents.add(event);
           }
         }      
       }
     }
     return proposedEvents;
    
  }
  
  /**
   * Finds all the proposed events after current event
   * @param events
   * @return
   */
  public static ArrayList findProposedAfterCurrent(StudyEventBean current, ArrayList events) {
    ArrayList proposedEvents = new ArrayList();
    
    int pos = 0;
     if (!events.isEmpty()) {
       for (int i=0; i<events.size(); i++) {
         StudyEventBean event = (StudyEventBean)events.get(i);
         if (event.getId()== current.getId()){
         pos = i;
         break;
         }
       }  
       if (pos < events.size()-1) {
         for (int i=pos+1; i<events.size(); i++) {
           StudyEventBean event = (StudyEventBean)events.get(i);
            if (event.getSubjectEventStatus().equals(SubjectEventStatus.PROPOSED)){
              proposedEvents.add(event);
           }
         }      
       }
     }
     return proposedEvents;
    
  }
  
  
  /**
   * Checks if an event is in good window to the previous event
   * @param event the event to be checked
   * @param changed the event whose started was changed
   * @param events
   * @param templates
   * @return  true if this event is out of window
   *          false otherwise
   */
  public static  boolean isEventOutOfWindowOfPrevious(StudyEventBean changed, StudyEventBean event, ArrayList events, ArrayList templates) {
    StudyEventBean previous = null;      
    
    long diffDays=0;
    if (!events.isEmpty()) {     
      
      for (int i = 0; i<events.size(); i++) {
        StudyEventBean ev = (StudyEventBean)events.get(i);
        if (event.getId()==ev.getId()){
          if (i > 0) {
            previous = (StudyEventBean)events.get(i-1);
            
            if (previous.getId() == changed.getId()) {
              previous.setDateStarted(changed.getDateStarted());
            }
            
            if (previous.getDateStarted().after(event.getDateStarted())) {
              return true;      
           
           } 
            long diffMillis = Math.abs(event.getDateStarted().getTime()-previous.getDateStarted().getTime());
            //Get difference in days
            diffDays = diffMillis/(24*60*60*1000);            
          }
        }
      }          
    }
    //logger.info("previous start date:" + previous.getDateStarted());
    float previousDuration = 0;
    float previousIdealTime =0;
    float previousMinTime =0;
    float previousMaxTime =0;
    float currentDuration = 0;
    
    for (int m = 0; m < templates.size(); m++) {
      StudyTemplateEventDefBean stedb = (StudyTemplateEventDefBean) templates.get(m);
      if (stedb.getStudyEventDefinitionId() == previous.getStudyEventDefinitionId()) {
        previousDuration = stedb.getEventDuration();
        previousIdealTime = stedb.getIdealTimeToNextEvent();
        
        previousMinTime = previousIdealTime;
        previousMaxTime = previousIdealTime;
        
        if (stedb.getMinTimeToNextEvent() != null) {
          previousMinTime = stedb.getMinTimeToNextEvent();  
        }
        if (stedb.getMaxTimeToNextEvent() != null) {
          previousMaxTime = stedb.getMaxTimeToNextEvent();  
        }
       } else if (stedb.getStudyEventDefinitionId() == event.getStudyEventDefinitionId()) {
         currentDuration = stedb.getEventDuration();
         float idealTime = stedb.getIdealTimeToNextEvent();
         
         
      }
    }
    
    
    long goodMax=0;
    long goodMin=0;
    
    goodMax = new Float(previousDuration + previousMaxTime).longValue();
    goodMin = new Float(previousDuration + previousMinTime).longValue();
    if ((diffDays >= goodMin) && (diffDays <= goodMax)) {         
      return false;
    }    
        
    
    
    return true;
    
  }
  
  /**
   * Checks if an event is in good window to the first event
   * @param current
   * @param first
   * @param events
   * @param templates
   * @return true if this event is out of window, false otherwise
   */
  public static boolean isEventOutOfWindowOfFirst(StudyEventBean current, StudyEventBean first, ArrayList events, ArrayList templates) {
    
    //logger.info("current : first" + current.getDateStarted() + " " + first.getDateStarted());
    if (current.getDateStarted().before(first.getDateStarted())) {
      return true;
    }
    
    long diffMillis = Math.abs(current.getDateStarted().getTime()-first.getDateStarted().getTime());
    //Get difference in days
    long diffDays = diffMillis/(24*60*60*1000);    
    //logger.info("diffDays:" + diffDays);
    
    int currentPos =0;
    for (int m = 0; m < templates.size(); m++) {
      StudyTemplateEventDefBean stedb = (StudyTemplateEventDefBean) templates.get(m);
      if (stedb.getStudyEventDefinitionId() == current.getStudyEventDefinitionId()) {
         currentPos=m;
         break;      
      }
    }
    float goodMax =0;
    float goodMin =0;
    if (currentPos >0) {
      if (currentPos ==1) {
        StudyTemplateEventDefBean stedb = (StudyTemplateEventDefBean) templates.get(0);
        goodMax = stedb.getEventDuration() + stedb.getMaxTimeToNextEvent();
        goodMin = stedb.getEventDuration() + stedb.getMinTimeToNextEvent();
      } 
      else {//currentPos>1
       for (int i=0; i< currentPos-1; i++) {
         StudyTemplateEventDefBean stedb = (StudyTemplateEventDefBean) templates.get(i);
         float duration = stedb.getEventDuration();
         float idealTime = stedb.getIdealTimeToNextEvent();            
         goodMax = goodMax + duration + idealTime;
         goodMin = goodMin + duration + idealTime;
        }
       StudyTemplateEventDefBean stedb1 = (StudyTemplateEventDefBean) templates.get(currentPos-1);
       goodMax = goodMax + stedb1.getEventDuration() + stedb1.getMaxTimeToNextEvent();
       goodMin = goodMin + stedb1.getEventDuration() + stedb1.getMinTimeToNextEvent();
      }
     
     if ((diffDays >= goodMin) && (diffDays <= goodMax)) {         
        return false;
     } 
    }
    return true;
  }

  /**
   * Reschedules all the proposed events after the first event
   * @param first
   * @param proposedAfterFirst
   * @param events
   * @param templates
   * @return
   */
  public static StudyEventBean rescheduleBasedOnFirst (StudyEventBean first, StudyEventBean proposedAfterFirst, ArrayList templates) {
    
    //logger.info("reschedule Based On First");
    int gapToFirst =0;//the event count between proposedAfterFirst and first 
    float currentDuration =0;
    
    for (int i= 0; i< templates.size(); i++) {
      StudyTemplateEventDefBean st = (StudyTemplateEventDefBean) templates.get(i);
      if( st.getStudyEventDefinitionId()== proposedAfterFirst.getStudyEventDefinitionId()) {
        gapToFirst = i;
        currentDuration = st.getEventDuration();
        break;
      }
    }
    float gap =0; //the ideal time gap between proposedAfterFirst and first 
    
    //logger.info("gapTpFirst:" + gapToFirst);
    if (gapToFirst >0) {
      for (int i= 0; i < gapToFirst; i++) {
        StudyTemplateEventDefBean st = (StudyTemplateEventDefBean) templates.get(i);
        gap = gap + st.getEventDuration() + st.getIdealTimeToNextEvent();
      }
    }
    //logger.info("gap:" + gap);
    Calendar cal = Calendar.getInstance();
    cal.setTime(first.getDateStarted());
    cal.add(Calendar.DATE, new Float(gap).intValue());
    proposedAfterFirst.setDateStarted(cal.getTime());
    
    cal.add(Calendar.DATE, new Float(currentDuration).intValue());
    proposedAfterFirst.setDateEnded(cal.getTime());  
    
    return proposedAfterFirst;
    
    
  }
}