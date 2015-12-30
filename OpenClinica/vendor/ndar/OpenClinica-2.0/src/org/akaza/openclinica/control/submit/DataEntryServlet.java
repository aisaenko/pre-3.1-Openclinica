/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 
 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.NullValue;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplaySectionBean;
import org.akaza.openclinica.bean.submit.DisplayTableOfContentsBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.Utils;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.managestudy.ViewStudySubjectServlet;
import org.akaza.openclinica.core.form.DiscrepancyValidator;
import org.akaza.openclinica.core.form.FormDiscrepancyNotes;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validation;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.admin.CRFDAO;
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
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.exception.InconsistentStateException;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author ssachs
 */
public abstract class DataEntryServlet extends SecureController {
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
   * A bean used to indicate that servlets to which this servlet forwards should
   * ignore any parameters, in particular the "submitted" parameter which
   * controls FormProcessor.isSubmitted. If an attribute with this name is set
   * in the request, the servlet to which this servlet forwards should consider
   * fp.isSubmitted to always return false.
   */
  public static final String INPUT_IGNORE_PARAMETERS = "ignore";

  /**
   * A bean used to indicate that we are not validating inputs, that is, that
   * the user is "confirming" values which did not validate properly the first
   * time. If an attribute with this name is set in the request, this servlet
   * should not perform any validation on the form inputs.
   */
  public static final String INPUT_CHECK_INPUTS = "checkInputs";

  /**
   * The name of the form input on which users write annotations.
   */
  public static final String INPUT_ANNOTATIONS = "annotations";

  /**
   * The name of the attribute in the request which hold the preset annotations
   * form value.
   */
  public static final String BEAN_ANNOTATIONS = "annotations";

  // names of submit buttons in the JSP
  public static final String RESUME_LATER = "submittedResume";

  public static final String GO_PREVIOUS = "submittedPrev";

  public static final String GO_NEXT = "submittedNext";

  public static final String BEAN_DISPLAY = "section";

  public static final String TOC_DISPLAY = "toc"; // from TableOfContentServlet

  // these inputs are displayed on the table of contents and
  // are used to edit Event CRF properties
  public static final String INPUT_INTERVIEWER = "interviewer";

  public static final String INPUT_INTERVIEW_DATE = "interviewDate";

  public static final String INPUT_TAB = "tabId";

  public static final String INPUT_MARK_COMPLETE = "markComplete";

  public static final String VALUE_YES = "Yes";

  // these are only for use with ACTION_START_INITIAL_DATA_ENTRY
  public static final String INPUT_EVENT_DEFINITION_CRF_ID = "eventDefinitionCRFId";

  public static final String INPUT_CRF_VERSION_ID = "crfVersionId";

  public static final String INPUT_STUDY_EVENT_ID = "studyEventId";

  public static final String INPUT_SUBJECT_ID = "subjectId";
  
  public static final String GO_EXIT = "submittedExit";

  protected FormProcessor fp;

  // the input beans
  protected EventCRFBean ecb;

  protected SectionBean sb;
  
  protected ArrayList allSectionBeans;

  /**
   * The event definition CRF bean which governs the event CRF bean into which
   * we are entering data.
   */
  protected EventDefinitionCRFBean edcb;

  // DAOs used throughout the c;ass
  protected EventCRFDAO ecdao;

  protected EventDefinitionCRFDAO edcdao;

  protected SectionDAO sdao;

  protected ItemDAO idao;

  protected ItemFormMetadataDAO ifmdao;

  protected ItemDataDAO iddao;

  /**
   * Determines whether the form was submitted. Calculated once in
   * processRequest. The reason we don't use the normal means to determine if
   * the form was submitted (ie FormProcessor.isSubmitted) is because when we
   * use forwardPage, Java confuses the inputs from the just-processed form with
   * the inputs for the forwarded-to page. This is a problem since frequently
   * we're forwarding from one (submitted) section to the next (unsubmitted)
   * section. If we use the normal means, Java will always think that the
   * unsubmitted section is, in fact, submitted. This member is guaranteed to be
   * calculated before shouldLoadDBValues() is called.
   */
  protected boolean isSubmitted = false;

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
   */
  protected abstract void mayProceed() throws InsufficientPermissionException;

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.core.SecureController#processRequest()
   */
  protected void processRequest() throws Exception {
    FormDiscrepancyNotes discNotes;
    panel.setStudyInfoShown(false);
    String age ="";
    
    if (!fp.getString(GO_EXIT).equals("")) {
      addPageMessage("You chose to exit without saving your data.");
      forwardPage(Page.SUBMIT_DATA_SERVLET);
      return;
    }
    
    try {
    getInputBeans();

    } catch (InsufficientPermissionException ie) {
      addPageMessage(ie.getOpenClinicaMessage());
      forwardPage(Page.SUBMIT_DATA_SERVLET);
      return;
    }
    if (!ecb.isActive()) {
      throw new InconsistentStateException(Page.SUBMIT_DATA_SERVLET,
          "The event CRF you are attempting to enter data on does not exist.");
    }

    Boolean b = (Boolean) request.getAttribute(INPUT_IGNORE_PARAMETERS);
    isSubmitted = fp.isSubmitted() && b == null;

    DisplaySectionBean section = getDisplayBean();

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
    StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
    SubjectDAO subjectDao = new SubjectDAO(sm.getDataSource());
    StudyDAO studydao = new StudyDAO(sm.getDataSource());
    StudySubjectBean ssb = (StudySubjectBean) ssdao.findByPK(ecb.getStudySubjectId());
    SubjectBean subject = (SubjectBean) subjectDao.findByPK(ssb.getSubjectId());
    
//  Get the study then the parent study
    
    StudyBean study = (StudyBean)session.getAttribute("study");
    
    if (study.getParentStudyId() > 0){ 
     // this is a site,find parent
	   StudyBean parentStudy = (StudyBean) studydao.findByPK(study.getParentStudyId());
	   request.setAttribute("studyTitle", parentStudy.getName()
						+ " - " + study.getName());
	  } else {
	   request.setAttribute("studyTitle", study.getName());
	  }
    
    
    // Let us process the age
    if (currentStudy.getStudyParameterConfig().getCollectDob().equals("1")){
    	Date eventStartDate = ((DisplayTableOfContentsBean)request.getAttribute(TOC_DISPLAY)).getStudyEvent().getDateStarted();
  	    age = Utils.getInstacne().processAge(eventStartDate,subject.getDateOfBirth());
    }
    ArrayList beans = ViewStudySubjectServlet.getDisplayStudyEventsForStudySubject(ssb, sm
        .getDataSource(), ub, currentRole);
    request.setAttribute("studySubject", ssb);
    request.setAttribute("subject", subject);
    request.setAttribute("beans", beans);
    request.setAttribute("eventCRF", ecb);
    request.setAttribute("age", age);

    // set up interviewer name and date
    fp.addPresetValue(INPUT_INTERVIEWER, ecb.getInterviewerName());

    if (ecb.getDateInterviewed() != null) {
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
      String idateFormatted = sdf.format(ecb.getDateInterviewed());
      fp.addPresetValue(INPUT_INTERVIEW_DATE, idateFormatted);
    } else {
      fp.addPresetValue(INPUT_INTERVIEW_DATE, "");
    }
    setPresetValues(fp.getPresetValues());

    if (!isSubmitted) {
      // TODO: prevent data enterer from seeing results of first round of data
      // entry, if this is second round
      request.setAttribute(BEAN_DISPLAY, section);
      request.setAttribute(BEAN_ANNOTATIONS, getEventCRFAnnotations());

      discNotes = new FormDiscrepancyNotes();
      populateNotesWithDBNoteCounts(discNotes, section);

      session.setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);

      setUpPanel(section);
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

      boolean validate =
      // did the user click a "Save" button?
      fp.getBoolean(INPUT_CHECK_INPUTS)

      // is validation required in this type of servlet when the user clicks
          // "Save"?
          && validateInputOnFirstRound();

      // section.setCheckInputs(fp.getBoolean(INPUT_CHECK_INPUTS));

      errors = new HashMap();
      ArrayList items = section.getItems();

      discNotes = (FormDiscrepancyNotes) session
          .getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
      if (discNotes == null) {
        discNotes = new FormDiscrepancyNotes();
      }
      populateNotesWithDBNoteCounts(discNotes, section);

      DiscrepancyValidator v = new DiscrepancyValidator(request, discNotes);

      for (int i = 0; i < items.size(); i++) {
        DisplayItemBean dib = (DisplayItemBean) items.get(i);

        if (validate) {
          dib = validateDisplayItemBean(v, dib);
        } else {
          dib = loadFormValue(dib);
        }

        ArrayList children = dib.getChildren();
        for (int j = 0; j < children.size(); j++) {
          DisplayItemBean child = (DisplayItemBean) children.get(j);
          if (validate) {
            child = validateDisplayItemBean(v, child);
          } else {
            child = loadFormValue(child);
          }

          children.set(j, child);
        }

        dib.setChildren(children);
        items.set(i, dib);
      }

      // we have to do this since we loaded all the form values into the display
      // item beans above
      section.setItems(items);
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

      if (!errors.isEmpty()) {
        // // force the servlet to accept whatever data is entered next go-round
        // section.setCheckInputs(false);

        // items = section.getItems();
        // for (int i = 0; i < items.size(); i++) {
        // DisplayItemBean dib = (DisplayItemBean) items.get(i);
        // String inputName = getInputName(dib);
        // //logger.info("required:" + dib.getMetadata().isRequired());
        // if (dib.getMetadata().isRequired()) {
        // //logger.info("inputname:" + inputName);
        // //logger.info("inputname value:" + fp.getString(inputName));
        // if (StringUtil.isBlank(fp.getString(inputName))) {
        // section.setCheckInputs(true);
        //
        // logger.info("required field is blank");
        // break;
        // }
        // }
        // }

        request.setAttribute(BEAN_DISPLAY, section);
        request.setAttribute(BEAN_ANNOTATIONS, fp.getString(INPUT_ANNOTATIONS));
        setInputMessages(errors);
        addPageMessage("There were some errors in your submission.  See below for details.");
        // addPageMessage("To override these errors and keep the data as you
        // entered it, click one of the \"Confirm\" buttons. ");
        // if (section.isCheckInputs()) {
        // addPageMessage("Please notice that you must enter data for the
        // <b>required</b> entries.");
        // }

        setUpPanel(section);
        forwardPage(getJSPPage());
      } else {
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

        ecb = (EventCRFBean) ecdao.update(ecb);

        // save discrepancy notes into DB
        FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) session
            .getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
        DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());

        AddNewSubjectServlet.saveFieldNotes(INPUT_INTERVIEWER, fdn, dndao, ecb.getId(), "EventCRF",
            currentStudy);
        AddNewSubjectServlet.saveFieldNotes(INPUT_INTERVIEW_DATE, fdn, dndao, ecb.getId(),
            "EventCRF", currentStudy);

        items = section.getItems();
        for (int i = 0; i < items.size(); i++) {
          DisplayItemBean dib = (DisplayItemBean) items.get(i);

          // we don't write success = success && writeToDB here
          // since the short-circuit mechanism may prevent Java
          // from executing writeToDB.
          temp = writeToDB(dib, iddao);

          String inputName = getInputName(dib);
          AddNewSubjectServlet.saveFieldNotes(inputName, fdn, dndao, dib.getData().getId(),
              "ItemData", currentStudy);

          success = success && temp;

          ArrayList childItems = dib.getChildren();
          for (int j = 0; j < childItems.size(); j++) {
            DisplayItemBean child = (DisplayItemBean) childItems.get(j);
            temp = writeToDB(child, iddao);
            inputName = getInputName(child);
            AddNewSubjectServlet.saveFieldNotes(inputName, fdn, dndao, dib.getData().getId(),
                "ItemData", currentStudy);
            success = success && temp;
          }
        }

        // now check if CRF is marked complete
        boolean markComplete = fp.getString(INPUT_MARK_COMPLETE).equals(VALUE_YES);
        boolean markSuccessfully = false; // if the CRF was marked complete
                                          // successfully
        if (markComplete) {
          markSuccessfully = markCRFComplete();
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
        if (!success) {
          addPageMessage("There was a database error during data entry.  Please try again.");
          request.setAttribute(BEAN_DISPLAY, section);
          session.removeAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
          forwardPage(Page.SUBMIT_DATA_SERVLET);
        } else {
          boolean forwardingSucceeded = false;

          if (!fp.getString(GO_PREVIOUS).equals("")) {
            if (previousSec.isActive()) {
              forwardingSucceeded = true;
              request.setAttribute(INPUT_EVENT_CRF, ecb);              
              request.setAttribute(INPUT_SECTION, previousSec);
              int tabNum =0;
              if (fp.getString("tab")==null ) {
                tabNum=1;
              } else {
                tabNum = fp.getInt("tab");
              }
              request.setAttribute("tab", new Integer(tabNum -1).toString());
              forwardPage(getServletPage());
            }
          } else if (!fp.getString(GO_NEXT).equals("")) {
            if (nextSec.isActive()) {
              forwardingSucceeded = true;
              request.setAttribute(INPUT_EVENT_CRF, ecb);
              request.setAttribute(INPUT_SECTION, nextSec);
              int tabNum =0;
              if (fp.getString("tab")==null ) {
                tabNum=1;
              } else {
                tabNum = fp.getInt("tab");
              }
              request.setAttribute("tab", new Integer(tabNum +1).toString());
              forwardPage(getServletPage());
            }
          }

          if (!forwardingSucceeded) {
            //request.setAttribute(TableOfContentsServlet.INPUT_EVENT_CRF_BEAN, ecb);
            if (markSuccessfully) {
              addPageMessage("Your data has been saved and the CRF was marked complete.");
              session.removeAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
              request.setAttribute("eventId", new Integer(ecb.getStudyEventId()).toString());             
              forwardPage(Page.ENTER_DATA_FOR_STUDY_EVENT_SERVLET);
            } else {
              //use clicked 'save'
              addPageMessage("Your data has been saved.  You may continue entering/editing data now or return at a later time.");
              request.setAttribute(INPUT_EVENT_CRF, ecb);
              request.setAttribute(INPUT_EVENT_CRF_ID, new Integer(ecb.getId()).toString());
              //forward to the next section if the previous one is not the last section
              if (!section.isLastSection()) {
                request.setAttribute(INPUT_SECTION, nextSec);
                request.setAttribute(INPUT_SECTION_ID, new Integer(nextSec.getId()).toString());
              } else {
                //already the last section, should go back to view event page
                request.setAttribute("eventId", new Integer(ecb.getStudyEventId()).toString());
                forwardPage(Page.ENTER_DATA_FOR_STUDY_EVENT_SERVLET);
                return;
             
              }
              int tabNum =0;
              if (fp.getString("tab")==null ) {
                tabNum=1;
              } else {
                tabNum = fp.getInt("tab");
              }
              if (!section.isLastSection()) {
              request.setAttribute("tab", new Integer(tabNum +1).toString());
              } 
              
              forwardPage(getServletPage());
             
            }
            //session.removeAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
            //forwardPage(Page.SUBMIT_DATA_SERVLET);
            
          }
        }
      }
    }
  }

  /**
   * Get the input beans - the EventCRFBean and the SectionBean. For both beans,
   * look first in the request attributes to see if the bean has been stored
   * there. If not, look in the parameters for the bean id, and then retrieve
   * the bean from the database. The beans are stored as protected class
   * members.
   */
  protected void getInputBeans() throws InsufficientPermissionException {
    fp = new FormProcessor(request);
    ecdao = new EventCRFDAO(sm.getDataSource());
    sdao = new SectionDAO(sm.getDataSource());

    ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
    if (ecb == null) {
      int eventCRFId = fp.getInt(INPUT_EVENT_CRF_ID, true);
      if (eventCRFId > 0) {
        // there is an event CRF already, only need to update
        ecb = (EventCRFBean) ecdao.findByPK(eventCRFId);
        // ecb.setUpdatedDate(new Date());
        // ecb.setUpdater(ub);
        // ecb = (EventCRFBean) ecdao.update(ecb);
      } else {
        try {
          ecb = createEventCRF();
        } catch (InconsistentStateException ie) {
          throw new InsufficientPermissionException(Page.SUBMIT_DATA_SERVLET, ie.getOpenClinicaMessage(), "1");
        }
      }

    }

    // added to allow sections shown on this page
    DisplayTableOfContentsBean displayBean = new DisplayTableOfContentsBean();
    displayBean = TableOfContentsServlet.getDisplayBean(ecb, sm.getDataSource(), currentStudy);
    request.setAttribute(TOC_DISPLAY, displayBean);
    
    int sectionId = fp.getInt(INPUT_SECTION_ID, true);
    if (sectionId <= 0) {
      ArrayList sections = sdao.findAllByCRFVersionId(ecb.getCRFVersionId());
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

    return;
  }

  /**
   * Creates a new Event CRF or update the exsiting one, that is, an event CRF
   * can be created but not item data yet, in this case, still consider it is
   * not started(called uncompleted before)
   * 
   * @return
   * @throws Exception
   */
  private EventCRFBean createEventCRF() throws InconsistentStateException {
    EventCRFBean ecb;
    ecdao = new EventCRFDAO(sm.getDataSource());

    int crfVersionId = fp.getInt(INPUT_CRF_VERSION_ID);
    int studyEventId = fp.getInt(INPUT_STUDY_EVENT_ID);
    int eventDefinitionCRFId = fp.getInt(INPUT_EVENT_DEFINITION_CRF_ID);
    int subjectId = fp.getInt(INPUT_SUBJECT_ID);
    int eventCRFId = fp.getInt(INPUT_EVENT_CRF_ID);

    logger.info("Creating event CRF.  Study id: " + currentStudy.getId()
        + "; CRF Version id: " + crfVersionId + "; Study Event id: " + studyEventId
        + "; Event Definition CRF id: " + eventDefinitionCRFId + "; Subject: " + subjectId);

    StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
    StudySubjectBean ssb = ssdao.findBySubjectIdAndStudy(subjectId, currentStudy);

    if (ssb.getId()<=0) {
      throw new InconsistentStateException(
          Page.SUBMIT_DATA_SERVLET,
          "You are trying to begin data entry for a CRF within a Study Event, but the subject does not exist within the study.");
    }

    StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
    StudyEventDefinitionBean sedb = seddao.findByEventDefinitionCRFId(eventDefinitionCRFId);
    logger.info("study event definition:" + sedb.getId());
    if (sedb.getId()<=0) {
      throw new InconsistentStateException(
          Page.SUBMIT_DATA_SERVLET,
          "You are trying to begin data entry for a CRF within a Study Event, but the study event definition for this study event does not exist within the study.");
    }

    CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
    EntityBean eb = cvdao.findByPK(crfVersionId);

    if (eb.getId()<=0) {
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

    if (aeb.getId()<=0) {
      throw new InconsistentStateException(
          Page.SUBMIT_DATA_SERVLET,
          "You are trying to begin data entry for a CRF within a Study Event, but the specified study event definition does not exist within the study.");
    }

    ecb = new EventCRFBean();
    if (eventCRFId == 0) {// no event CRF created yet
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
          ecb.setDateInterviewed(sEvent.getDateStarted());// default date
        } else {
          //logger.info("evnet start date is null, so date interviewed is null");
          ecb.setDateInterviewed(null);
        }
      } else {
        ecb.setDateInterviewed(null);
        //logger.info("date interviewed is null,getInterviewDateDefault() is blank");
      }
      ecb.setOwnerId(ub.getId());
      ecb.setOwner(ub);
      ecb.setStatus(Status.AVAILABLE);
      ecb.setCompletionStatusId(1);
      ecb.setStudySubjectId(ssb.getId());
      ecb.setStudyEventId(studyEventId);
      ecb.setValidateString("");
      ecb.setValidatorAnnotations("");

      ecb = (EventCRFBean) ecdao.create(ecb);
    } else {
      // there is an event CRF already, only need to update
      ecb = (EventCRFBean) ecdao.findByPK(eventCRFId);
      ecb.setCRFVersionId(crfVersionId);
      ecb.setUpdatedDate(new Date());
      ecb.setUpdater(ub);
      ecb = (EventCRFBean) ecdao.update(ecb);

    }

    if (ecb.getId()<=0) {
      throw new InconsistentStateException(Page.SUBMIT_DATA_SERVLET,
          "A new Event CRF could not be created due to a database error.");
    } else {
      sEvent.setSubjectEventStatus(SubjectEventStatus.DATA_ENTRY_STARTED);
      sEvent.setUpdater(ub);
      sEvent.setUpdatedDate(new Date());
      sedao.update(sEvent);

    }

    return ecb;
  }

  /**
   * Read in form values and write them to a display item bean. Note that this
   * results in the form value being written to both the response set bean and
   * the item data bean. The ResponseSetBean is used to display preset values on
   * the form in the event of error, and the ItemDataBean is used to send values
   * to the database.
   * 
   * @param dib
   *          The DisplayItemBean to write data into.
   * @return The DisplayItemBean, with form data loaded.
   */
  protected DisplayItemBean loadFormValue(DisplayItemBean dib) {
    String inputName = getInputName(dib);
    org.akaza.openclinica.bean.core.ResponseType rt = dib.getMetadata().getResponseSet()
        .getResponseType();
    if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CHECKBOX)
        || rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECTMULTI)) {
      dib.loadFormValue(fp.getStringArray(inputName));
    } else {
      dib.loadFormValue(fp.getString(inputName));
    }

    return dib;
  }

  /**
   * @return <code>true</code> if processRequest should validate inputs when
   *         the user clicks the "Save" button, <code>false</code> otherwise.
   */
  protected abstract boolean validateInputOnFirstRound();

  /**
   * Validate the input from the form corresponding to the provided item.
   * Implementing methods should load data from the form into the bean before
   * validating. The loadFormValue method should be used for this purpose.
   * 
   * validateDisplayItemBeanText, validateDisplayItemBeanSingleCV, and
   * validateDisplayItemBeanMultipleCV are provided to make implementing this
   * method easy.
   * 
   * @param v
   *          The Validator to add validations to.
   * @param dib
   *          The DisplayItemBean to validate.
   * @return The DisplayItemBean which is validated and has form values loaded
   *         into it.
   */
  protected abstract DisplayItemBean validateDisplayItemBean(DiscrepancyValidator v,
      DisplayItemBean dib);

  /**
   * Peform validation on a item which has a TEXT or TEXTAREA response type. If
   * the item has a null value, it's automatically validated. Otherwise, it's
   * checked against its data type.
   * 
   * @param v
   *          The Validator to add validations to.
   * @param dib
   *          The DisplayItemBean to validate.
   * @return The DisplayItemBean which is validated.
   */
  protected DisplayItemBean validateDisplayItemBeanText(DiscrepancyValidator v, DisplayItemBean dib) {
    String inputName = getInputName(dib);
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
        if (ibMeta.isRequired()) {
          v.addValidation(inputName, Validator.IS_REQUIRED);
        }
      } else {

        if (idt.equals(ItemDataType.ST)) {

        } else if (idt.equals(ItemDataType.INTEGER)) {
          v.addValidation(inputName, Validator.IS_AN_INTEGER);
          v.alwaysExecuteLastValidation(inputName);

        } else if (idt.equals(ItemDataType.REAL)) {

          v.addValidation(inputName, Validator.IS_A_NUMBER);
          v.alwaysExecuteLastValidation(inputName);
        } else if (idt.equals(ItemDataType.BL)) {
          // there is no validation here since this data type is explicitly
          // allowed to be null
          // if the string input for this field parses to a non-zero number, the
          // value will be true; otherwise, 0
        } else if (idt.equals(ItemDataType.BN)) {

        } else if (idt.equals(ItemDataType.SET)) {
          // v.addValidation(inputName, Validator.NO_BLANKS_SET);
          v.addValidation(inputName, Validator.IN_RESPONSE_SET_SINGLE_VALUE, dib.getMetadata()
              .getResponseSet());
        }

        else if (idt.equals(ItemDataType.DATE)) {
          v.addValidation(inputName, Validator.IS_A_DATE);
          v.alwaysExecuteLastValidation(inputName);
        }

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
        }
      }
    }
    return dib;
  }

  /**
   * Peform validation on a item which has a RADIO or SINGLESELECTresponse type.
   * This function checks that the input isn't blank, and that its value comes
   * from the controlled vocabulary (ResponseSetBean) in the DisplayItemBean.
   * 
   * @param v
   *          The Validator to add validations to.
   * @param dib
   *          The DisplayItemBean to validate.
   * @return The DisplayItemBean which is validated.
   */
  protected DisplayItemBean validateDisplayItemBeanSingleCV(DiscrepancyValidator v,
      DisplayItemBean dib) {
    String inputName = getInputName(dib);
    ItemFormMetadataBean ibMeta = dib.getMetadata();
    ItemDataBean idb = dib.getData();
    if (StringUtil.isBlank(idb.getValue())) {
      if (ibMeta.isRequired()) {
        v.addValidation(inputName, Validator.IS_REQUIRED);
      }
    } else {
      v.addValidation(inputName, Validator.IN_RESPONSE_SET_SINGLE_VALUE, dib.getMetadata()
          .getResponseSet());
    }

    return dib;
  }

  /**
   * Peform validation on a item which has a RADIO or SINGLESELECTresponse type.
   * This function checks that the input isn't blank, and that its value comes
   * from the controlled vocabulary (ResponseSetBean) in the DisplayItemBean.
   * 
   * @param v
   *          The Validator to add validations to.
   * @param dib
   *          The DisplayItemBean to validate.
   * @return The DisplayItemBean which is validated.
   */
  protected DisplayItemBean validateDisplayItemBeanMultipleCV(DiscrepancyValidator v,
      DisplayItemBean dib) {
    String inputName = getInputName(dib);
    ItemFormMetadataBean ibMeta = dib.getMetadata();
    ItemDataBean idb = dib.getData();
    if (StringUtil.isBlank(idb.getValue())) {
      if (ibMeta.isRequired()) {
        v.addValidation(inputName, Validator.IS_REQUIRED);
      }
    } else {
      v.addValidation(inputName, Validator.IN_RESPONSE_SET, dib.getMetadata().getResponseSet());
    }
    return dib;
  }

  /**
   * @param dib
   *          A DisplayItemBean representing an input on the CRF.
   * @return The name of the input in the HTML form.
   */
  public final String getInputName(DisplayItemBean dib) {
    ItemBean ib = dib.getItem();
    String inputName = "input" + ib.getId();
    return inputName;
  }

  /**
   * Writes data from the DisplayItemBean to the database. Note that if the bean
   * contains an inactive ItemDataBean, the ItemDataBean is created; otherwise,
   * the ItemDataBean is updated.
   * 
   * @param dib
   *          The DisplayItemBean from which to write data.
   * @param iddao
   *          The DAO to use to access the database.
   * @return <code>true</code> if the query succeeded, <code>false</code>
   *         otherwise.
   */
  protected boolean writeToDB(DisplayItemBean dib, ItemDataDAO iddao) {
    ItemDataBean idb = dib.getData();

    if (idb.getValue().equals("")) {
      idb.setStatus(getBlankItemStatus());
    } else {
      idb.setStatus(getNonBlankItemStatus());
    }

    if (!idb.isActive()) {
      // will this need to change for double data entry?
      idb.setCreatedDate(new Date());
      idb.setOwner(ub);
      idb.setItemId(dib.getItem().getId());
      idb.setEventCRFId(ecb.getId());

      idb = (ItemDataBean) iddao.create(idb);
    } else {
      idb = (ItemDataBean) iddao.update(idb);
    }

    return idb.isActive();
  }

  /**
   * Retrieve the status which should be assigned to ItemDataBeans which have
   * blank values for this data entry servlet.
   */
  protected abstract Status getBlankItemStatus();

  // unavailable in admin. editing

  /**
   * Retrieve the status which should be assigned to ItemDataBeans which have
   * non-blank values for this data entry servlet.
   */
  protected abstract Status getNonBlankItemStatus();

  // unavailable in admin. editing

  /**
   * Get the eventCRF's annotations as appropriate for this data entry servlet.
   */
  protected abstract String getEventCRFAnnotations();

  /**
   * Set the eventCRF's annotations properties as appropriate for this data
   * entry servlet.
   */
  protected abstract void setEventCRFAnnotations(String annotations);

  /**
   * Retrieve the DisplaySectionBean which will be used to display the Event CRF
   * Section on the JSP, and also is used to controll processRequest.
   */
  protected DisplaySectionBean getDisplayBean() throws Exception {
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
    edcb = edcdao.findByStudyEventIdAndCRFVersionId(ecb.getStudyEventId(), cvb.getId());
    section.setEventDefinitionCRF(edcb);

    // setup DAO's here to avoid creating too many objects
    idao = new ItemDAO(sm.getDataSource());
    ifmdao = new ItemFormMetadataDAO(sm.getDataSource());
    iddao = new ItemDataDAO(sm.getDataSource());

    // get all the display item beans
    ArrayList displayItems = getParentDisplayItems(sb, edcb, idao, ifmdao, iddao);

    // now sort them by ordinal
    Collections.sort(displayItems);

    // now get the child DisplayItemBeans
    for (int i = 0; i < displayItems.size(); i++) {
      DisplayItemBean dib = (DisplayItemBean) displayItems.get(i);
      dib.setChildren(getChildrenDisplayItems(dib, edcb));

      if (shouldLoadDBValues(dib)) {
        dib.loadDBValue();
      }

      displayItems.set(i, dib);
    }

    section.setItems(displayItems);

    return section;
  }
  
  
  /**
   * Retrieve the DisplaySectionBean which will be used to display the Event CRF
   * Section on the JSP, and also is used to controll processRequest.
   */
  protected ArrayList getAllDisplayBeans() throws Exception {

		ArrayList sections = new ArrayList();

		for (int j = 0; j < allSectionBeans.size(); j++) {

			SectionBean sb = (SectionBean) allSectionBeans.get(j);

			DisplaySectionBean section = new DisplaySectionBean();
			section.setEventCRF(ecb);

			if (sb.getParentId() > 0) {
				SectionBean parent = (SectionBean) sdao.findByPK(sb
						.getParentId());
				sb.setParent(parent);
			}

			section.setSection(sb);

			CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
			CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(ecb
					.getCRFVersionId());
			section.setCrfVersion(cvb);

			CRFDAO cdao = new CRFDAO(sm.getDataSource());
			CRFBean cb = (CRFBean) cdao.findByPK(cvb.getCrfId());
			section.setCrf(cb);

			edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
			edcb = edcdao.findByStudyEventIdAndCRFVersionId(ecb
					.getStudyEventId(), cvb.getId());
			section.setEventDefinitionCRF(edcb);

			// setup DAO's here to avoid creating too many objects
			idao = new ItemDAO(sm.getDataSource());
			ifmdao = new ItemFormMetadataDAO(sm.getDataSource());
			iddao = new ItemDataDAO(sm.getDataSource());

			// get all the display item beans
			ArrayList displayItems = getParentDisplayItems(sb, edcb, idao,
					ifmdao, iddao);

			// now sort them by ordinal
			Collections.sort(displayItems);

			// now get the child DisplayItemBeans
			for (int i = 0; i < displayItems.size(); i++) {
				DisplayItemBean dib = (DisplayItemBean) displayItems.get(i);
				dib.setChildren(getChildrenDisplayItems(dib, edcb));

				if (shouldLoadDBValues(dib)) {
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
	 * For each item in this section which is a parent, get a DisplayItemBean
     * corresponding to that item. Note that an item is a parent iff its parentId ==
     * 0.
	 * 
	 * @param sb
	 *            The section whose items we are retrieving.
	 * @return An array of DisplayItemBean objects, one per parent item in the
	 *         section. Note that there is no guarantee on the ordering of the
	 *         objects.
	 * @throws Exception
	 */
  private ArrayList getParentDisplayItems(SectionBean sb, EventDefinitionCRFBean edcb,
      ItemDAO idao, ItemFormMetadataDAO ifmdao, ItemDataDAO iddao) throws Exception {
    ArrayList answer = new ArrayList();

    // DisplayItemBean objects are composed of an ItemBean, ItemDataBean and
    // ItemFormDataBean.
    // However the DAOs only provide methods to retrieve one type of bean at a
    // time (per section)
    // the displayItems hashmap allows us to compose these beans into
    // DisplayItemBean objects,
    // while hitting the database only three times
    HashMap displayItems = new HashMap();

    ArrayList items = idao.findAllParentsBySectionId(sb.getId());
    for (int i = 0; i < items.size(); i++) {
      DisplayItemBean dib = new DisplayItemBean();
      dib.setEventDefinitionCRF(edcb);
      ItemBean ib = (ItemBean) items.get(i);
      dib.setItem(ib);
      displayItems.put(new Integer(dib.getItem().getId()), dib);
    }

    ArrayList metadata = ifmdao.findAllBySectionId(sb.getId());
    for (int i = 0; i < metadata.size(); i++) {
      ItemFormMetadataBean ifmb = (ItemFormMetadataBean) metadata.get(i);
      DisplayItemBean dib = (DisplayItemBean) displayItems.get(new Integer(ifmb.getItemId()));
      if (dib != null) {
        dib.setMetadata(ifmb);
        displayItems.put(new Integer(ifmb.getItemId()), dib);
      }
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

    Iterator hmIt = displayItems.keySet().iterator();
    while (hmIt.hasNext()) {
      Integer key = (Integer) hmIt.next();
      DisplayItemBean dib = (DisplayItemBean) displayItems.get(key);
      answer.add(dib);
    }

    return answer;
  }

  /**
   * Get the DisplayItemBean objects corresponding to the items which are
   * children of the specified parent.
   * 
   * @param parent
   *          The item whose children are to be retrieved.
   * @return An array of DisplayItemBean objects corresponding to the items
   *         which are children of parent, and are sorted by column number
   *         (ascending), then ordinal (ascending).
   */
  private ArrayList getChildrenDisplayItems(DisplayItemBean parent, EventDefinitionCRFBean edcb) {
    ArrayList answer = new ArrayList();

    int parentId = parent.getItem().getId();
    ArrayList childItemBeans = idao.findAllByParentIdAndCRFVersionId(parentId, ecb
        .getCRFVersionId());

    for (int i = 0; i < childItemBeans.size(); i++) {
      ItemBean child = (ItemBean) childItemBeans.get(i);
      ItemDataBean data = iddao.findByItemIdAndEventCRFId(child.getId(), ecb.getId());
      ItemFormMetadataBean metadata = (ItemFormMetadataBean) ifmdao.findByItemIdAndCRFVersionId(
          child.getId(), ecb.getCRFVersionId());

      DisplayItemBean dib = new DisplayItemBean();
      dib.setEventDefinitionCRF(edcb);
      dib.setItem(child);
      dib.setData(data);
      dib.setMetadata(metadata);

      if (shouldLoadDBValues(dib)) {
        dib.loadDBValue();
      }

      answer.add(dib);
    }

    // this is a pretty slow and memory intensive way to sort... see if we can
    // have the db do this instead
    Collections.sort(answer);

    return answer;
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

  protected void populateNotesWithDBNoteCounts(FormDiscrepancyNotes discNotes,
      DisplaySectionBean section) {
    DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());

    ArrayList items = section.getItems();
    for (int i = 0; i < items.size(); i++) {
      DisplayItemBean dib = (DisplayItemBean) items.get(i);
      int itemDataId = dib.getData().getId();
      int itemId = dib.getItem().getId();
      int numNotes = dndao.findNumExistingNotesForItem(itemDataId);
      String inputFieldName = "input" + itemId;

      discNotes.setNumExistingFieldNotes(inputFieldName, numNotes);
      dib.setNumDiscrepancyNotes(numNotes + discNotes.getNotes(inputFieldName).size());

      ArrayList childItems = dib.getChildren();
      for (int j = 0; j < childItems.size(); j++) {
        DisplayItemBean child = (DisplayItemBean) childItems.get(j);
        int childItemDataId = child.getData().getId();
        int childItemId = child.getItem().getId();
        int childNumNotes = dndao.findNumExistingNotesForItem(childItemDataId);
        String childInputFieldName = "input" + childItemId;

        discNotes.setNumExistingFieldNotes(childInputFieldName, childNumNotes);
        child
            .setNumDiscrepancyNotes(childNumNotes + discNotes.getNotes(childInputFieldName).size());
        childItems.set(j, child);
      }
      dib.setChildren(childItems);
      items.set(i, dib);
    }
    section.setItems(items);
  }

  /**
   * The following methods are for 'mark CRF complete'
   * 
   * @return
   */

  protected boolean markCRFComplete() throws Exception {
    getEventCRFBean();
    getEventDefinitionCRFBean();
    DataEntryStage stage = ecb.getStage();

    // request.setAttribute(TableOfContentsServlet.INPUT_EVENT_CRF_BEAN, ecb);
    // request.setAttribute(INPUT_EVENT_CRF_ID, new Integer(ecb.getId()));
    // logger.info("inout_event_crf_id:" +ecb.getId());
    Page errorPage = getJSPPage();

    if (stage.equals(DataEntryStage.UNCOMPLETED)
        || stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE)
        || stage.equals(DataEntryStage.LOCKED)) {
      addPageMessage("You may not mark this Event CRF complete, because it is not in the initial data entry or validation stage.");
      return false;
    }

    if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE)
        || stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {

      if (!edcb.isDoubleEntry()) {
        addPageMessage("You may not mark this Event CRF complete, because it has already been marked complete for data entry, and validation is not allowed for this Event Definition CRF.");
        return false;
      }
    }

    if (!isEachSectionReviewedOnce()) {
      addPageMessage("You may not mark this Event CRF complete, because there are some sections which have not been reviewed once.");
      return false;
    }

    if (!isEachRequiredFieldFillout()) {
      addPageMessage("You may not mark this Event CRF complete, because there are some required entries which have not been filled out.");
      return false;
    }

    /*
     * if (ecb.getInterviewerName().trim().equals("")) { throw new
     * InconsistentStateException(errorPage, "You may not mark this Event CRF
     * complete, because the interviewer name is blank."); }
     */

    Status newStatus = ecb.getStatus();
    boolean ide = true;
    if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY) && edcb.isDoubleEntry()) {
      newStatus = Status.PENDING;
      ecb.setUpdaterId(ub.getId());
      ecb.setUpdatedDate(new Date());
      ecb.setDateCompleted(new Date());
    } else if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY) && !edcb.isDoubleEntry()) {
      newStatus = Status.UNAVAILABLE;
      ecb.setUpdaterId(ub.getId());
      ecb.setUpdatedDate(new Date());
      ecb.setDateCompleted(new Date());
      ecb.setDateValidateCompleted(new Date());
    } else if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE)
        || stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
      newStatus = Status.UNAVAILABLE;
      ecb.setDateValidateCompleted(new Date());
      ide = false;
    }
    ecb.setStatus(newStatus);
    ecb = (EventCRFBean) ecdao.update(ecb);
    ecdao.markComplete(ecb, ide);

    ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
    iddao.updateStatusByEventCRF(ecb, newStatus);

    // change status for event
    StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
    StudyEventBean seb = (StudyEventBean) sedao.findByPK(ecb.getStudyEventId());
    seb.setUpdatedDate(new Date());
    seb.setUpdater(ub);

    EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
    ArrayList allCRFs = (ArrayList) ecdao.findAllByStudyEvent(seb);
    ArrayList allEDCs = (ArrayList) edcdao.findAllActiveByEventDefinitionId(seb
        .getStudyEventDefinitionId());
    boolean eventCompleted = true;
    for (int i = 0; i < allCRFs.size(); i++) {
      EventCRFBean ec = (EventCRFBean) allCRFs.get(i);
      if (!ec.getStatus().equals(Status.UNAVAILABLE)) {
        eventCompleted = false;
        break;
      }
    }
    if (eventCompleted && (allCRFs.size() >= allEDCs.size())) {
      seb.setSubjectEventStatus(SubjectEventStatus.COMPLETED);
    }

    seb = (StudyEventBean) sedao.update(seb);

    // addPageMessage("The Event CRF data was saved and was marked complete.");
    // request.setAttribute(EnterDataForStudyEventServlet.INPUT_EVENT_ID,
    // String.valueOf(ecb
    // .getStudyEventId()));
    // forwardPage(Page.ENTER_DATA_FOR_STUDY_EVENT_SERVLET);
    return true;
  }

  private void getEventCRFBean() {
    fp = new FormProcessor(request);
    int eventCRFId = fp.getInt(INPUT_EVENT_CRF_ID);

    ecdao = new EventCRFDAO(sm.getDataSource());
    ecb = (EventCRFBean) ecdao.findByPK(eventCRFId);
  }

  protected boolean isEachRequiredFieldFillout() {
    ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
    ArrayList dataList = iddao.findAllBlankRequiredByEventCRFId(ecb.getId(), ecb.getCRFVersionId());
    // empty means all required fields got filled out,return true-jxu
    return dataList.isEmpty();
  }

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
        if ((numItemsPending == 0) && (numItems > 0)) {
          return false;
        }
      } else {
        if ((numItemsCompleted == 0) && (numItems > 0)) {
          return false;
        }
      }
    }

    return true;
  }

  protected void getEventDefinitionCRFBean() {
    edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
    edcb = edcdao.findByStudyEventIdAndCRFVersionId(ecb.getStudyEventId(), ecb.getCRFVersionId());
  }

}