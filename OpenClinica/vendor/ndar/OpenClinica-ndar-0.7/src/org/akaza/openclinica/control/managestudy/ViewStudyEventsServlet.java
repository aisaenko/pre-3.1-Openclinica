/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.core.SummaryDataEntryStatus;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyEventRow;
import org.akaza.openclinica.bean.managestudy.ViewEventDefinitionBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * Handles user request of "view study events"
 */
public class ViewStudyEventsServlet extends SecureController {
  public static final String INPUT_STARTDATE = "startDate";

  public static final String INPUT_ENDDATE = "endDate";

  public static final String INPUT_DEF_ID = "definitionId";

  public static final String INPUT_STATUS_ID = "statusId";

  public static final String STATUS_MAP = "statuses";

  public static final String DEFINITION_MAP = "definitions";
  
  public static final String PRINT = "print";
  
  public static final String SUMMARY_STATUS_MAP = "summaryStatuses";

  /**
   * Checks whether the user has the right permission to proceed function
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }

    if (currentRole != null) {
      Role r = currentRole.getRole();
      if ((r != null)
          && (r.equals(Role.COORDINATOR) || r.equals(Role.STUDYDIRECTOR)
              || r.equals(Role.INVESTIGATOR) || r.equals(Role.RESEARCHASSISTANT))) {
        return;
      }
    }

    addPageMessage("You don't have correct privilege in your current active study. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.MENU_SERVLET, "not correct role", "1");
  }

  public void processRequest() throws Exception {
    FormProcessor fp = new FormProcessor(request);
    int sedId = fp.getInt("sedId");
    int statusId = fp.getInt(INPUT_STATUS_ID);
    int definitionId = fp.getInt(INPUT_DEF_ID);
    Date startDate = fp.getDate(INPUT_STARTDATE);
    Date endDate = fp.getDate(INPUT_ENDDATE);
    
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    int month = cal.get(Calendar.MONTH)+1;
    int year = cal.get(Calendar.YEAR);
    String defaultStartDateString = month + "/01/" + year;
    Date defaultStartDate = new Date();
    try {
      defaultStartDate = sdf.parse(defaultStartDateString);
    } catch (ParseException pe) {
     
    }
    cal.setTime(defaultStartDate);
    cal.add(Calendar.DATE,30);
    Date defaultEndDate = cal.getTime();
    
    if (!fp.isSubmitted()) {
      logger.info("not submitted");
      HashMap presetValues = new HashMap();
     
      presetValues.put(INPUT_STARTDATE, sdf.format(defaultStartDate));     
      presetValues.put(INPUT_ENDDATE, sdf.format(defaultEndDate));     
      startDate = defaultStartDate;
      endDate = defaultEndDate;
      setPresetValues(presetValues);
    } else {     
        Validator v = new Validator(request);
        v.addValidation(INPUT_STARTDATE, Validator.IS_A_DATE);
        v.addValidation(INPUT_ENDDATE, Validator.IS_A_DATE);
        errors=v.validate();
        if (!errors.isEmpty()){
          setInputMessages(errors);  
          startDate = defaultStartDate;
          endDate = defaultEndDate;
        }
        fp.addPresetValue(INPUT_STARTDATE, fp.getString(INPUT_STARTDATE));     
        fp.addPresetValue(INPUT_ENDDATE, fp.getString(INPUT_ENDDATE));  
        fp.addPresetValue(INPUT_DEF_ID, fp.getInt(INPUT_DEF_ID)); 
        fp.addPresetValue(INPUT_STATUS_ID, fp.getInt(INPUT_STATUS_ID)); 
        setPresetValues(fp.getPresetValues());
    }

    request.setAttribute(STATUS_MAP, SubjectEventStatus.toArrayList());
    request.setAttribute(SUMMARY_STATUS_MAP, SummaryDataEntryStatus.toArrayList());
    StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
    ArrayList definitions = (ArrayList) seddao.findAllByStudy(currentStudy);
    request.setAttribute(DEFINITION_MAP, definitions);

    ArrayList allEvents = new ArrayList();  
    allEvents = genTables(fp, definitions, startDate, endDate, sedId, definitionId, statusId);

   

    request.setAttribute("allEvents", allEvents);
   
    //for print version
    String queryUrl = INPUT_STARTDATE + "=" + sdf.format(startDate) + "&"+
                       INPUT_ENDDATE + "=" + sdf.format(endDate) + "&"+
                       INPUT_DEF_ID + "=" +definitionId + "&"+
                       INPUT_STATUS_ID+ "=" + statusId + "&"+
                       "sedId=" + sedId + "&submitted=" + fp.getInt("submitted") ;
    request.setAttribute("queryUrl", queryUrl);
    if ("yes".equalsIgnoreCase(fp.getString(PRINT))){
      allEvents = genEventsForPrint(fp, definitions, startDate, endDate, sedId, definitionId, statusId);
      request.setAttribute("allEvents", allEvents);
      forwardPage(Page.VIEW_STUDY_EVENTS_PRINT);
    } else {
      forwardPage(Page.VIEW_STUDY_EVENTS);
    }
    

  }
  
  /**
   * 
   * @param fp
   * @param definitions
   * @param startDate
   * @param endDate
   * @param sedId
   * @param definitionId
   * @param statusId
   * @return
   */
  private ArrayList genTables(FormProcessor fp, ArrayList definitions, 
      Date startDate, Date endDate, int sedId, int definitionId, int statusId){
    StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
    EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
    ArrayList allEvents = new ArrayList();
    definitions = findDefinitionById(definitions, definitionId);
    for (int i = 0; i < definitions.size(); i++) {
      ViewEventDefinitionBean ved = new ViewEventDefinitionBean();
      StudyEventDefinitionBean sed = (StudyEventDefinitionBean) definitions.get(i);
     
      ved.setDefinition(sed);
      
      ArrayList events = (ArrayList) sedao.findAllWithSubjectLabelByDefinition(sed.getId());
      events = findEventByStatusAndDate(events,statusId,startDate,endDate);

      setViewEventDefBean(ved, events);

      EntityBeanTable table;
      if (sedId == sed.getId()) {//apply finding function or ordering function
                                 // to a specific table
        table = fp.getEntityBeanTable();
      } else {
        table = new EntityBeanTable();
      }
      table.setSortingIfNotExplicitlySet(1, false);//sort by event start date,
                                                   // desc
      ArrayList allEventRows = StudyEventRow.generateRowsFromBeans(events);

      String[] columns = { "Study Subject ID", "Event Date Started",
          "Subject Event Status", "Data Entry Status", "Actions" };
      table.setColumns(new ArrayList(Arrays.asList(columns)));
      table.hideColumnLink(4);
      HashMap args = new HashMap();
      args.put("sedId", new Integer(sed.getId()).toString());
      args.put("definitionId", new Integer(definitionId).toString());
      args.put("statusId", new Integer(statusId).toString());
      args.put("startDate",sdf.format(startDate));
      args.put("endDate",sdf.format(endDate));
      table.setQuery("ViewStudyEvents", args);
      table.setRows(allEventRows);
      table.computeDisplay();
      ved.setStudyEventTable(table);
      if (!events.isEmpty()) {
        allEvents.add(ved);
      }
    }
    return allEvents;
  }

  /**
   * Generates an arraylist of study events for printing
   * @param fp
   * @param definitions
   * @param startDate
   * @param endDate
   * @param sedId
   * @param definitionId
   * @param statusId
   * @return
   */
  private ArrayList genEventsForPrint(FormProcessor fp, ArrayList definitions, 
      Date startDate, Date endDate, int sedId, int definitionId, int statusId){
    StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
    EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
    ArrayList allEvents = new ArrayList();
    definitions = findDefinitionById(definitions, definitionId);
    for (int i = 0; i < definitions.size(); i++) {
      ViewEventDefinitionBean ved = new ViewEventDefinitionBean();
      StudyEventDefinitionBean sed = (StudyEventDefinitionBean) definitions.get(i);
     
      ved.setDefinition(sed);
      
      ArrayList events = (ArrayList) sedao.findAllWithSubjectLabelByDefinition(sed.getId());
      events = findEventByStatusAndDate(events,statusId,startDate,endDate);

      setViewEventDefBean(ved, events);

      ved.setStudyEvents(events);
     
      if (!events.isEmpty()) {
        allEvents.add(ved);
      }
    }
    return allEvents;
  }
  
  
  /**
   * @param ved
   * @param events
   */
  private void setViewEventDefBean(ViewEventDefinitionBean ved, ArrayList events) {
    int subjectScheduled = 0;
    int subjectCompleted = 0;
    int subjectDiscontinued = 0;
      
    Date firstStartDateForScheduled = null;
    Date lastCompletionDate = null;
    Date now = new Date();
    for (StudyEventBean se: (ArrayList<StudyEventBean>)events) {
      if (se.getSubjectEventStatus().equals(SubjectEventStatus.SCHEDULED) || se.getSubjectEventStatus().equals(SubjectEventStatus.PROPOSED)) {
        subjectScheduled++;
        if (se.getDateStarted().before(now)) {
          se.setScheduledDatePast(true);
        }
        //find the first firstStartDateForScheduled (or Proposed)
        if (firstStartDateForScheduled== null || se.getDateStarted().before(firstStartDateForScheduled)) {
          firstStartDateForScheduled = se.getDateStarted();
        }
      } else if (se.getSubjectEventStatus().equals(SubjectEventStatus.COMPLETED)) {
        subjectCompleted++;
        //find the first lastCompletionDate
        if (lastCompletionDate == null || se.getDateEnded().after(lastCompletionDate)){
          lastCompletionDate = se.getDateEnded();
        }
      } else if (se.getSubjectEventStatus().equals(SubjectEventStatus.CANCELED_NO_RESCHEDULE) ||
                 se.getSubjectEventStatus().equals(SubjectEventStatus.NO_SHOW_NO_RESCHEDULE)) {
        // dropped out/stopped/skipped/relapse
        subjectDiscontinued++;
      }
    }
    ved.setSubjectCompleted(subjectCompleted);
    ved.setSubjectScheduled(subjectScheduled);
    ved.setSubjectDiscontinued(subjectDiscontinued);
    ved.setFirstScheduledStartDate(firstStartDateForScheduled);
    ved.setLastCompletionDate(lastCompletionDate);
  }
  
  /**
   * 
   * @param definitions
   * @param definitionId
   * @return
   */
  private ArrayList findDefinitionById(ArrayList definitions, int definitionId){
    if (definitionId >0){
      for (int i=0; i<definitions.size(); i++){
        StudyEventDefinitionBean sed = (StudyEventDefinitionBean) definitions.get(i);
        if (sed.getId()==definitionId){
          ArrayList a = new ArrayList();
          a.add(sed);
          return a;
        }
      }
    }
    return definitions;
  }
  
  /**
   * 
   * @param events
   * @param statusId
   * @param startDate
   * @param endDate
   * @return
   */
  private ArrayList findEventByStatusAndDate(ArrayList events, int statusId,Date startDate, Date endDate){    
    ArrayList a = new ArrayList();
    for (int i=0; i<events.size(); i++){
      StudyEventBean se = (StudyEventBean)events.get(i);
      if (!se.getDateStarted().before(startDate) && ! se.getDateStarted().after(endDate)){
        a.add(se);
      }
    }
    ArrayList b = new ArrayList();
    if (statusId >0){          
      for (int i=0; i<a.size(); i++){
        StudyEventBean se = (StudyEventBean)a.get(i);
        if (se.getSubjectEventStatus().getId()==statusId){         
          b.add(se);          
        }
      }
      return b;
    }
    return a;
  }
}