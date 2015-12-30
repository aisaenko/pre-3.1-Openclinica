/*
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 * 
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 */
package org.akaza.openclinica.control.submit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.DiscrepancyValidator;
import org.akaza.openclinica.core.form.FormDiscrepancyNotes;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.view.Page;

// TODO: support YYYY-MM-DD HH:MM time formats

public class CreateNewStudyEventServlet extends SecureController {
	
	Locale locale;
	//<  ResourceBundlerestext,respage,resexception;

  public static final String INPUT_STUDY_EVENT_DEFINITION = "studyEventDefinition";

  public static final String INPUT_STUDY_EVENT_DEFINITION_SCHEDULED = "studyEventDefinitionScheduled";

  public static final String INPUT_STUDY_SUBJECT = "studySubject";

  public static final String INPUT_STUDY_SUBJECT_ID_FROM_VIEWSUBJECT = "studySubjectId";
  
  public static final String INPUT_EVENT_DEF_ID_FROM_VIEWSUBJECT = "eventDefId";

  public static final String INPUT_STARTDATE_PREFIX = "start";

  public static final String INPUT_ENDDATE_PREFIX = "end";

  public static final String INPUT_STARTDATE_PREFIX_SCHEDULED = "startScheduled";

  public static final String INPUT_REQUEST_STUDY_SUBJECT = "requestStudySubject";

  public static final String INPUT_LOCATION = "location";

  public static final String INPUT_SCHEDULED_LOCATION = "locationScheduled";

  private FormProcessor fp;

  protected void processRequest() throws Exception {
    panel.setStudyInfoShown(false);
    fp = new FormProcessor(request);
    FormDiscrepancyNotes discNotes = null;

    // TODO: make this sensitive to permissions
    StudySubjectDAO sdao = new StudySubjectDAO(sm.getDataSource());
    ArrayList subjects = (ArrayList) sdao.findAllActiveByStudyOrderByLabel(currentStudy);

    // TODO: make this sensitive to permissions
    StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());

    StudyBean studyWithEventDefinitions = currentStudy;
    if (currentStudy.getParentStudyId() > 0) {
      studyWithEventDefinitions = new StudyBean();
      studyWithEventDefinitions.setId(currentStudy.getParentStudyId());
    }
    //find all active definitions with CRFs
    ArrayList eventDefinitions = (ArrayList) seddao.findAllActiveByStudy(studyWithEventDefinitions);
    EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
    ArrayList definitionsWithCRF = new ArrayList();
    for (int i=0; i<eventDefinitions.size(); i++) {
      StudyEventDefinitionBean sed = (StudyEventDefinitionBean)eventDefinitions.get(i);
      ArrayList edcs = edcdao.findAllByEventDefinitionId(sed.getId());
      if (!edcs.isEmpty()) {
        definitionsWithCRF.add(sed);
      }
    }
   
    Collections.sort(definitionsWithCRF);

    /*
     * ArrayList eventDefinitionsScheduled = new ArrayList(); 
     * for (int i = 0; i < eventDefinitions.size(); i++) {
     *  StudyEventDefinitionBean sed =
     * (StudyEventDefinitionBean) eventDefinitions.get(i);
     *  if (sed.getType().equalsIgnoreCase("scheduled")) {
     * eventDefinitionsScheduled.add(sed); 
     * } 
     * }
     */
    //all definitions will appear in scheduled event creation box-11/26/05
    ArrayList eventDefinitionsScheduled = definitionsWithCRF;

    if (!fp.isSubmitted()) {
      //			StudyEventDAO sed = new StudyEventDAO(sm.getDataSource());
      //			sed.updateSampleOrdinals_v092();

      HashMap presetValues = new HashMap();

      //YW 08-16-2007 << set default as blank for time
      presetValues.put(INPUT_STARTDATE_PREFIX + "Hour", new Integer(-1));
      presetValues.put(INPUT_STARTDATE_PREFIX + "Minute", new Integer(-1));
      presetValues.put(INPUT_STARTDATE_PREFIX + "Half", new String(""));
      presetValues.put(INPUT_ENDDATE_PREFIX + "Hour", new Integer(-1));
      presetValues.put(INPUT_ENDDATE_PREFIX + "Minute", new Integer(-1));
      presetValues.put(INPUT_ENDDATE_PREFIX + "Half", new String(""));
      presetValues.put(INPUT_STARTDATE_PREFIX_SCHEDULED + "Hour", new Integer(-1));
      presetValues.put(INPUT_STARTDATE_PREFIX_SCHEDULED + "Minute", new Integer(-1));
      presetValues.put(INPUT_STARTDATE_PREFIX_SCHEDULED + "Half", new String(""));

      //SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
      String dateValue = local_df.format(new Date(System.currentTimeMillis()));
      presetValues.put(INPUT_STARTDATE_PREFIX + "Date", dateValue);
      presetValues.put(INPUT_STARTDATE_PREFIX_SCHEDULED + "Date", dateValue);
      presetValues.put(INPUT_LOCATION, currentStudy.getFacilityCity());//defualt
      // location
      presetValues.put(INPUT_SCHEDULED_LOCATION, currentStudy.getFacilityCity());

      StudySubjectBean ssb;
      int studySubjectId = fp.getInt(INPUT_STUDY_SUBJECT_ID_FROM_VIEWSUBJECT);
      if (studySubjectId <= 0) {
        ssb = (StudySubjectBean) request.getAttribute(INPUT_STUDY_SUBJECT);
      } else {
        ssb = (StudySubjectBean) sdao.findByPK(studySubjectId);
        request.setAttribute(INPUT_REQUEST_STUDY_SUBJECT, "no");
      }

      if ((ssb != null) && ssb.isActive()) {
        presetValues.put(INPUT_STUDY_SUBJECT, ssb);

        String requestStudySubject = (String) request.getAttribute(INPUT_REQUEST_STUDY_SUBJECT);
        if (requestStudySubject != null) {
          presetValues.put(INPUT_REQUEST_STUDY_SUBJECT, requestStudySubject);

          dateValue = local_df.format(ssb.getEnrollmentDate());
          presetValues.put(INPUT_STARTDATE_PREFIX + "Date", dateValue);
        }
      }
      
      //input from manage subject matrix, user has specified definition id
      int studyEventDefinitionId = fp.getInt(INPUT_STUDY_EVENT_DEFINITION);
      if (studyEventDefinitionId>0){
        StudyEventDefinitionBean sed= (StudyEventDefinitionBean)seddao.findByPK(studyEventDefinitionId);
        presetValues.put(INPUT_STUDY_EVENT_DEFINITION, sed);
      }
        
      //tbh
      logger.info("set preset values: "+presetValues.toString());
      logger.info("found def.w.CRF list, size "+definitionsWithCRF.size());
      //tbh
      setPresetValues(presetValues);

      setupBeans(subjects, definitionsWithCRF);

      discNotes = new FormDiscrepancyNotes();
      session.setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);

      request.setAttribute("eventDefinitionsScheduled", eventDefinitionsScheduled);
      setInputMessages(new HashMap());
      forwardPage(Page.CREATE_NEW_STUDY_EVENT);
    } else {
    	//tbh
//    	String dateCheck = (String)request.getAttribute("startDate");
//    	String endCheck = (String)request.getAttribute("endDate");
//    	logger.info(dateCheck+"; "+endCheck);
    	
    	String dateCheck2 = (String)request.getParameter("startDate");
    	String endCheck2 = (String)request.getParameter("endDate");
    	logger.info(dateCheck2+"; "+endCheck2);
    	
//    	for (java.util.Enumeration enu = request.getAttributeNames(); enu.hasMoreElements() ;) {
//            System.out.println(">>> found "+enu.nextElement().toString());
//        }
    	//tbh
      discNotes = (FormDiscrepancyNotes) session
          .getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
      if (discNotes == null) {
        discNotes = new FormDiscrepancyNotes();
        session.setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);
      }
      DiscrepancyValidator v = new DiscrepancyValidator(request, discNotes);

      v.addValidation(INPUT_STARTDATE_PREFIX, Validator.IS_DATE_TIME);
      v.alwaysExecuteLastValidation(INPUT_STARTDATE_PREFIX);
      if (!fp.getString(INPUT_ENDDATE_PREFIX + "Date").equals("")) {
        v.addValidation(INPUT_ENDDATE_PREFIX, Validator.IS_DATE_TIME);
        v.alwaysExecuteLastValidation(INPUT_ENDDATE_PREFIX);
      }

      v.addValidation(INPUT_STUDY_EVENT_DEFINITION, Validator.ENTITY_EXISTS_IN_STUDY, seddao,
          studyWithEventDefinitions);
      v.addValidation(INPUT_STUDY_SUBJECT, Validator.ENTITY_EXISTS_IN_STUDY, sdao, currentStudy);

      v.addValidation(INPUT_LOCATION, Validator.NO_BLANKS);
      v.addValidation(INPUT_LOCATION, Validator.LENGTH_NUMERIC_COMPARISON,
          NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 2000);
      v.alwaysExecuteLastValidation(INPUT_LOCATION);

      boolean hasScheduledEvent = false;
      if (!StringUtil.isBlank(fp.getString(INPUT_STUDY_EVENT_DEFINITION_SCHEDULED))) {
        //System.out.println("has scheduled definition******");
        v.addValidation(INPUT_STUDY_EVENT_DEFINITION_SCHEDULED, Validator.ENTITY_EXISTS_IN_STUDY,
            seddao, studyWithEventDefinitions);
        v.addValidation(INPUT_SCHEDULED_LOCATION, Validator.NO_BLANKS);
        v.addValidation(INPUT_SCHEDULED_LOCATION, Validator.LENGTH_NUMERIC_COMPARISON,
            NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 2000);
        v.alwaysExecuteLastValidation(INPUT_SCHEDULED_LOCATION);
        v.addValidation(INPUT_STARTDATE_PREFIX_SCHEDULED, Validator.IS_DATE_TIME);
        v.alwaysExecuteLastValidation(INPUT_STARTDATE_PREFIX_SCHEDULED);
        hasScheduledEvent = true;

      }

      HashMap errors = v.validate();
      //System.out.println("v is not null *****");

      StudyEventDefinitionBean definition = (StudyEventDefinitionBean) seddao.findByPK(fp
          .getInt(INPUT_STUDY_EVENT_DEFINITION));

      StudyEventDefinitionBean definitionScheduled = null;
      if (hasScheduledEvent) {
        definitionScheduled = (StudyEventDefinitionBean) seddao.findByPK(fp
            .getInt(INPUT_STUDY_EVENT_DEFINITION_SCHEDULED));
      }
      StudySubjectBean studySubject = (StudySubjectBean) sdao.findByPK(fp
          .getInt(INPUT_STUDY_SUBJECT));

      if (!subjectMayReceiveStudyEvent(sm.getDataSource(), definition, studySubject)) {
        Validator
            .addError(
                errors,
                INPUT_STUDY_EVENT_DEFINITION,
                restext.getString("not_added_since_event_not_repeating"));
      }

      if (hasScheduledEvent && !subjectMayReceiveStudyEvent(sm.getDataSource(), definitionScheduled, studySubject)) {
        Validator
            .addError(
                errors,
                INPUT_STUDY_EVENT_DEFINITION_SCHEDULED,
                restext.getString("not_added_since_event_not_repeating"));
      }

      if (!errors.containsKey(INPUT_STARTDATE_PREFIX) && !errors.containsKey(INPUT_ENDDATE_PREFIX)) {

        Date start = getInputStartDate();
        Date end = getInputEndDate();
        v.addValidation(INPUT_ENDDATE_PREFIX, Validator.DATE_IS_AFTER_OR_EQUAL,
            INPUT_STARTDATE_PREFIX);

        /*
         * if (end.compareTo(start) < 0) { Validator.addError(errors,
         * INPUT_ENDDATE_PREFIX, "The end date/time must be later or the same as
         * the start date/time."); }
         */
      }
      //System.out.println("here ok 11111111111111");
      if (hasScheduledEvent && !errors.containsKey(INPUT_STARTDATE_PREFIX_SCHEDULED)) {

        Date start = getInputStartDate();
        //tbh
        logger.info("found start date in verify: "+local_df.format(start));
        Date startScheduled = getInputStartDateScheduled();
        logger.info("found input start date scheduled in verify: "+local_df.format(startScheduled));
        //tbh, added to test 1389
        v.addValidation(INPUT_STARTDATE_PREFIX_SCHEDULED, Validator.DATE_IS_AFTER_OR_EQUAL,
            INPUT_STARTDATE_PREFIX);
        /*
         * if (startScheduled.compareTo(start) < 0) { Validator
         * .addError(errors, INPUT_STARTDATE_PREFIX_SCHEDULED, "The scheduled
         * start date/time must be after or the same as the current start
         * date/time."); }
         */
      }
      //System.out.println("here ok 222222222222");
      if (!errors.isEmpty()) {
        addPageMessage(respage.getString("errors_in_submission_see_below"));
        setInputMessages(errors);

        fp.addPresetValue(INPUT_STUDY_EVENT_DEFINITION, definition);
        fp.addPresetValue(INPUT_STUDY_SUBJECT, studySubject);
        fp.addPresetValue(INPUT_REQUEST_STUDY_SUBJECT, fp.getString(INPUT_REQUEST_STUDY_SUBJECT));
        fp.addPresetValue(INPUT_LOCATION, fp.getString(INPUT_LOCATION));

        fp.addPresetValue(INPUT_SCHEDULED_LOCATION, fp.getString(INPUT_SCHEDULED_LOCATION));
        String prefixes[] = { INPUT_STARTDATE_PREFIX, INPUT_ENDDATE_PREFIX,
            INPUT_STARTDATE_PREFIX_SCHEDULED };
        fp.setCurrentDateTimeValuesAsPreset(prefixes);

        if (hasScheduledEvent) {
          fp.addPresetValue(INPUT_STUDY_EVENT_DEFINITION_SCHEDULED, definitionScheduled);
        }

        setPresetValues(fp.getPresetValues());
        setupBeans(subjects, definitionsWithCRF);
        request.setAttribute("eventDefinitionsScheduled", eventDefinitionsScheduled);
        forwardPage(Page.CREATE_NEW_STUDY_EVENT);
      } else {
        StudyEventDAO sed = new StudyEventDAO(sm.getDataSource());

        StudyEventBean studyEvent = new StudyEventBean();
        studyEvent.setStudyEventDefinitionId(definition.getId());
        studyEvent.setStudySubjectId(studySubject.getId());

        Date start = getInputStartDate();
        Date end = getInputEndDate();

        //YW 08-17-2007 <<
        if(("-1").equals(getInputStartHour()) && ("-1").equals(getInputStartMinute()) && ("").equals(getInputStartHalf())) {
        	studyEvent.setStartTimeFlag(false);
        } else {
        	studyEvent.setStartTimeFlag(true);
        }
        if(!fp.getString(INPUT_ENDDATE_PREFIX + "Date").equals("")) {
        	if(("-1").equals(getInputEndHour()) && ("-1").equals(getInputEndMinute()) && ("").equals(getInputEndHalf())) {
            	studyEvent.setEndTimeFlag(false);
            } else {
            	studyEvent.setEndTimeFlag(true);
            }
        }
        if(("-1").equals(fp.getDateTime(INPUT_STARTDATE_PREFIX_SCHEDULED + "Hour")) && 
        		("-1").equals(fp.getDateTime(INPUT_STARTDATE_PREFIX_SCHEDULED + "Minute")) && 
        		("").equals(fp.getDateTime(INPUT_STARTDATE_PREFIX_SCHEDULED + "Half"))) {
        	studyEvent.setStartTimeFlag(false);
        } else {
        	studyEvent.setStartTimeFlag(true);
        }
        //YW >>
        studyEvent.setDateStarted(start);
        //comment to find bug 1389, tbh
        logger.info("found start date: "+local_df.format(start));
        Date startScheduled2 = getInputStartDateScheduled();
        logger.info("found input start date scheduled: "+local_df.format(startScheduled2));
        //tbh
        studyEvent.setDateEnded(end);
        studyEvent.setOwner(ub);
        studyEvent.setStatus(Status.AVAILABLE);
        studyEvent.setLocation(fp.getString(INPUT_LOCATION));
        studyEvent.setSubjectEventStatus(SubjectEventStatus.SCHEDULED);

        ArrayList subjectsExistingEvents = sed.findAllByStudyAndStudySubjectId(currentStudy,
            studySubject.getId());
        studyEvent.setSampleOrdinal(sed.getMaxSampleOrdinal(definition, studySubject) + 1);

        studyEvent = (StudyEventBean) sed.create(studyEvent);

        if (!studyEvent.isActive()) {
          throw new OpenClinicaException(restext.getString("event_not_created_in_database"),
              "2");
        }
        addPageMessage(restext.getString("X_event_wiht_definition") + definition.getName()
            + restext.getString("X_and_subject") + studySubject.getName() + respage.getString("X_was_created_succesfully"));

        //save discrepancy notes into DB
        FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) session
            .getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
        DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());
        String[] eventFields = { INPUT_LOCATION, INPUT_STARTDATE_PREFIX, INPUT_ENDDATE_PREFIX };
        for (int i = 0; i < eventFields.length; i++) {
          AddNewSubjectServlet.saveFieldNotes(eventFields[i], fdn, dndao, studyEvent.getId(),
              "studyEvent", currentStudy);
        }
        //System.out.println("here ok 3333333333333333");
        if (hasScheduledEvent) {
          System.out.println(restext.getString("has_scheduled_event"));
          if (subjectMayReceiveStudyEvent(sm.getDataSource(), definitionScheduled, studySubject)) {

            StudyEventBean studyEventScheduled = new StudyEventBean();
            studyEventScheduled.setStudyEventDefinitionId(definitionScheduled.getId());
            studyEventScheduled.setStudySubjectId(studySubject.getId());

            Date startScheduled = getInputStartDateScheduled();

            studyEventScheduled.setDateStarted(startScheduled);
            studyEventScheduled.setDateEnded(startScheduled);
            studyEventScheduled.setOwner(ub);
            studyEventScheduled.setStatus(Status.AVAILABLE);
            studyEventScheduled.setLocation(fp.getString(INPUT_SCHEDULED_LOCATION));
            studyEvent.setSubjectEventStatus(SubjectEventStatus.SCHEDULED);

            
            subjectsExistingEvents = sed.findAllByStudyAndStudySubjectId(currentStudy, studySubject
                .getId());
            studyEventScheduled.setSampleOrdinal(sed.getMaxSampleOrdinal(definitionScheduled, studySubject) + 1);
           

            studyEventScheduled = (StudyEventBean) sed.create(studyEventScheduled);
            if (!studyEventScheduled.isActive()) {
              throw new OpenClinicaException(
            		  restext.getString("scheduled_event_not_created_in_database"), "2");
            }

            AddNewSubjectServlet.saveFieldNotes(INPUT_SCHEDULED_LOCATION, fdn, dndao,
                studyEventScheduled.getId(), "studyEvent", currentStudy);

          } else {
            addPageMessage(restext.getString("scheduled_event_definition")
                + definitionScheduled.getName() + restext.getString("X_and_subject") + studySubject.getName()
                + restext.getString("not_created_since_event_not_repeating")
                + restext.getString("event_type_already_exists"));
          }

        } //if

        session.removeAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
        request.setAttribute(EnterDataForStudyEventServlet.INPUT_EVENT_ID, String
            .valueOf(studyEvent.getId()));

        forwardPage(Page.ENTER_DATA_FOR_STUDY_EVENT_SERVLET);
      }
    }
  }

  protected void mayProceed() throws InsufficientPermissionException {
	  
	locale = request.getLocale();
	//< restext = ResourceBundle.getBundle("org.akaza.openclinica.i18n.notes",locale);
	//< respage = ResourceBundle.getBundle("org.akaza.openclinica.i18n.page_messages",locale);
	//< resexception=ResourceBundle.getBundle("org.akaza.openclinica.i18n.exceptions",locale);
	  
    String exceptionName = resexception.getString("no_permission_to_add_new_study_event");
    String noAccessMessage = respage.getString("not_create_new_event")+ (" ") + respage.getString("change_study_contact_sysadmin");

    if (SubmitDataServlet.maySubmitData(ub, currentRole)) {
      return;
    }

    addPageMessage(noAccessMessage);
    throw new InsufficientPermissionException(Page.MENU_SERVLET, exceptionName, "1");
  }

  /**
   * Determines whether a subject may receive an additional study event. This is
   * true if:
   * <ul>
   * <li>The study event definition is repeating; or
   * <li>The subject does not yet have a study event for the given study event
   * definition
   * </ul>
   * 
   * @param studyEventDefinition
   *          The definition of the study event which is to be added for the
   *          subject.
   * @param studySubject
   *          The subject for which the study event is to be added.
   * @return <code>true</code> if the subject may receive an additional study
   *         event, <code>false</code> otherwise.
   */
  public static boolean subjectMayReceiveStudyEvent(DataSource ds,
      StudyEventDefinitionBean studyEventDefinition, StudySubjectBean studySubject) {

    if (studyEventDefinition.isRepeating()) {
      return true;
    }

    StudyEventDAO sedao = new StudyEventDAO(ds);
    ArrayList allEvents = sedao.findAllByDefinitionAndSubject(studyEventDefinition, studySubject);

    if (allEvents.size() > 0) {
      return false;
    }

    return true;
  }

  private void setupBeans(ArrayList subjects, ArrayList eventDefinitions) throws Exception {
    addEntityList(
        "subjects",
        subjects,
        restext.getString("cannot_create_event_because_no_subjects"),
        Page.SUBMIT_DATA_SERVLET);
    addEntityList(
        "eventDefinitions",
        eventDefinitions,
        restext.getString("cannot_create_event_because_no_event_definitions"),
        Page.SUBMIT_DATA_SERVLET);

  }

  private Date getInputStartDate() {
    return fp.getDateTime(INPUT_STARTDATE_PREFIX);
  }

  private Date getInputStartDateScheduled() {
    return fp.getDateTime(INPUT_STARTDATE_PREFIX_SCHEDULED);
  }

  private Date getInputEndDate() {
    if (fp.getString(INPUT_ENDDATE_PREFIX + "Date").equals("")) {
      return fp.getDateTime(INPUT_STARTDATE_PREFIX);
    } else {
      return fp.getDateTime(INPUT_ENDDATE_PREFIX);
    }
  }
  
  //YW 08-17-2007
  private String getInputStartHour() {
	  return fp.getString(INPUT_STARTDATE_PREFIX + "Hour");
  }
  private String getInputStartMinute() {
	  return fp.getString(INPUT_STARTDATE_PREFIX + "Minute");
  }
  private String getInputStartHalf() {
	  return fp.getString(INPUT_STARTDATE_PREFIX + "Half");
  }
  private String getInputEndHour() {
	  return fp.getString(INPUT_ENDDATE_PREFIX + "Hour");
  }
  private String getInputEndMinute() {
	  return fp.getString(INPUT_ENDDATE_PREFIX + "Minute");
  }
  private String getInputEndHalf() {
	  return fp.getString(INPUT_ENDDATE_PREFIX + "Half");
  }
  //YW >>
}
