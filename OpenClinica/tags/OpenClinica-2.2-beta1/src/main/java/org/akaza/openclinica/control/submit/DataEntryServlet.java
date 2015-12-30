/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 
 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.NullValue;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
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
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.core.Utils;
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
import org.akaza.openclinica.dao.submit.ItemGroupDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.exception.InconsistentStateException;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.form.FormBeanUtil;

/**
 * @author ssachs
 */
public abstract class DataEntryServlet extends SecureController {

  Locale locale;
  //<  ResourceBundleresmessage,restext,resexception,respage;


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
  
  public static final String GROUP_HAS_DATA="groupHasData";

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

  protected boolean hasGroup = false;

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
   */
  protected abstract void mayProceed() throws InsufficientPermissionException;

  /*locale = request.getLocale();
     //< resmessage = ResourceBundle.getBundle("org.akaza.openclinica.i18n.page_messages",locale);
     //< restext = ResourceBundle.getBundle("org.akaza.openclinica.i18n.notes",locale);
     //< resexception=ResourceBundle.getBundle("org.akaza.openclinica.i18n.exceptions",locale);*/


  /*
  * (non-Javadoc)
  *
  * @see org.akaza.openclinica.control.core.SecureController#processRequest()
  */
  protected void processRequest() throws Exception {

    locale = request.getLocale();
    //< resmessage = ResourceBundle.getBundle("org.akaza.openclinica.i18n.page_messages",locale);
    //< restext = ResourceBundle.getBundle("org.akaza.openclinica.i18n.notes",locale);
    //< resexception=ResourceBundle.getBundle("org.akaza.openclinica.i18n.exceptions",locale);
    //< respage = ResourceBundle.getBundle("org.akaza.openclinica.i18n.page_messages",locale);

    FormDiscrepancyNotes discNotes;
    panel.setStudyInfoShown(false);
    String age ="";



    if (!fp.getString(GO_EXIT).equals("")) {
      session.removeAttribute(GROUP_HAS_DATA);
      addPageMessage(respage.getString("exit_without_saving"));
      //addPageMessage("You chose to exit the data entry page.");
      //changed bu jxu 03/06/2007- we should use redirection to go to another servlet
      response.sendRedirect(response.encodeRedirectURL("ListStudySubjectsSubmit"));
      //forwardPage(Page.SUBMIT_DATA_SERVLET);
      return;
    }

    try {
      //checks if the section has items in item group
      // for repeating items     


      hasGroup = getInputBeans();

    } catch (InsufficientPermissionException ie) {
      addPageMessage(ie.getOpenClinicaMessage());
      forwardPage(Page.SUBMIT_DATA_SERVLET);
      return;
    }
    if (!ecb.isActive()) {
      throw new InconsistentStateException(Page.SUBMIT_DATA_SERVLET,
        resexception.getString("event_not_exists"));
    }

    Boolean b = (Boolean) request.getAttribute(INPUT_IGNORE_PARAMETERS);
    isSubmitted = fp.isSubmitted() && b == null;
    //variable is used for fetching any null values like "not applicable"
    int eventDefinitionCRFId = 0;
    if(fp != null) {
      eventDefinitionCRFId=fp.getInt("eventDefinitionCRFId");
    }
    DisplaySectionBean section = getDisplayBean(hasGroup, false);

    //constructs the list of items used on UI
    List<DisplayItemWithGroupBean> displayItemWithGroups
      = createItemWithGroups(section, hasGroup,eventDefinitionCRFId);

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
      //SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
      String idateFormatted = local_df.format(ecb.getDateInterviewed());
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
      //ArrayList items = section.getItems();

      //all items- inlcude items in item groups and other single items
      List<DisplayItemWithGroupBean> allItems = section.getDisplayItemGroups();


      discNotes = (FormDiscrepancyNotes) session
        .getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
      if (discNotes == null) {
        discNotes = new FormDiscrepancyNotes();
      }
      populateNotesWithDBNoteCounts(discNotes, section);

      DiscrepancyValidator v = new DiscrepancyValidator(request, discNotes);


      for (int i = 0; i < allItems.size(); i++) {
        //logger.info("===itering through items: "+i);
        DisplayItemWithGroupBean diwg = allItems.get(i);
        if (diwg.isInGroup()) {
          //for the items in groups
          DisplayItemGroupBean dgb = diwg.getItemGroup();
          List<DisplayItemGroupBean> dbGroups = diwg.getDbItemGroups();
          logger.info("got db item group size" + dbGroups.size());
          List<DisplayItemGroupBean> formGroups = new ArrayList<DisplayItemGroupBean>();
          if (validate) {
            //logger.info("===got to this part in the validation loop");
            formGroups = validateDisplayItemGroupBean(v, dgb, dbGroups, formGroups);
            logger.info("form group size after validation" + formGroups.size());
          } else {
            formGroups = loadFormValueForItemGroup(
              dgb, dbGroups, formGroups
            );
          }

          diwg.setItemGroup(dgb);
          diwg.setItemGroups(formGroups);

          allItems.set(i, diwg);

        } else {// other single items
          DisplayItemBean dib = diwg.getSingleItem();
          //dib = (DisplayItemBean) allItems.get(i);
          if (validate) {
            //generate input name here?
            //DisplayItemGroupBean dgb = diwg.getItemGroup();
            //String itemName = getInputName(dib);
            //no Item group for single item, so just use blank string as parameter for inputName
            dib = validateDisplayItemBean(v, dib, "");//this should be used, otherwise, DDE not working-jxu
            
            //logger.info("&&& found name: "+itemName);
            //logger.info("input " + itemName + ": " + fp.getString(itemName));
            //dib.loadFormValue(fp.getString(itemName));
           
          } else {
            //String itemName = getInputName(dib);
            //dib.loadFormValue(itemName);
            dib = loadFormValue(dib);
            //  String itemName = getInputName(dib);
            //  dib = loadFormValue(itemName);
          }

          ArrayList children = dib.getChildren();
          for (int j = 0; j < children.size(); j++) {
            DisplayItemBean child = (DisplayItemBean) children.get(j);
            //DisplayItemGroupBean dgb = diwg.getItemGroup();
            String itemName = getInputName(child);
            child.loadFormValue(fp.getString(itemName));
            if (validate) {
              child = validateDisplayItemBean(v, child, itemName);
              //was null changed value 092007 tbh
            } else {
              //child.loadFormValue(itemName);
              child = loadFormValue(child);
            }
            //logger.info("Checking child value: "+child.getData().getValue());
            children.set(j, child);
          }

          dib.setChildren(children);
          diwg.setSingleItem(dib);
          //logger.info("just set single item on line 447: "+dib.getData().getValue());
          // items.set(i, dib);
          allItems.set(i, diwg);

        }
      }



      // we have to do this since we loaded all the form values into the display
      // item beans above
      //section.setItems(items);
      section.setDisplayItemGroups(allItems);

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

        String[] textFields = { INPUT_INTERVIEWER, INPUT_INTERVIEW_DATE };
        fp.setCurrentStringValuesAsPreset(textFields);
        setPresetValues(fp.getPresetValues());

        request.setAttribute(BEAN_DISPLAY, section);
        request.setAttribute(BEAN_ANNOTATIONS, fp.getString(INPUT_ANNOTATIONS));
        setInputMessages(errors);
        addPageMessage(respage.getString("errors_in_submission_see_below"));
        request.setAttribute("hasError", "true");
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

        //items = section.getItems();
        allItems = section.getDisplayItemGroups();

        for (int i = 0; i < allItems.size(); i++) {
          DisplayItemWithGroupBean diwb = (DisplayItemWithGroupBean) allItems.get(i);

          // we don't write success = success && writeToDB here
          // since the short-circuit mechanism may prevent Java
          // from executing writeToDB.
          if (diwb.isInGroup()) {

            List<DisplayItemGroupBean> dgbs = diwb.getItemGroups();
            List<DisplayItemGroupBean> dbGroups = diwb.getDbItemGroups();
            //logger.info("dgbs size: " + dgbs.size());
            for (int j=0; j<dgbs.size(); j++) {
              DisplayItemGroupBean displayGroup= dgbs.get(j);
              List<DisplayItemBean> items = displayGroup.getItems();
              //this ordinal will only useful to create a new item data
              //update an item data won't touch its ordinal
              int nextOrdinal = iddao.getMaxOrdinalForGroup(ecb, sb, displayGroup.getItemGroupBean()) + 1;
              for(DisplayItemBean displayItem : items){
                displayItem.setEditFlag(displayGroup.getEditFlag());
                logger.info("group item value:" + displayItem.getData().getValue());
                writeToDB(displayItem,iddao, nextOrdinal);
                logger.info("just executed writeToDB - 1");
                String inputName= getGroupItemInputName(displayGroup, j,displayItem );
                AddNewSubjectServlet.saveFieldNotes(inputName, fdn, dndao, displayItem.getData().getId(),
                  "ItemData", currentStudy);
                success = success && temp;
              }
            }

            for (int j=0; j<dbGroups.size(); j++) {
              DisplayItemGroupBean displayGroup= dbGroups.get(j);
              if ("remove".equalsIgnoreCase(displayGroup.getEditFlag())){
                List<DisplayItemBean> items = displayGroup.getItems();
                for(DisplayItemBean displayItem : items){
                  displayItem.setEditFlag(displayGroup.getEditFlag());
                  writeToDB(displayItem,iddao, 0);
                  logger.info("just executed writeToDB - 2");
                  //just use 0 here since update doesn't touch ordinal   
                  success = success && temp;
                }
              }
            }

          } else {
            DisplayItemBean dib = diwb.getSingleItem();
            //TODO work on this line

            temp = writeToDB(dib, iddao, 1);
            //logger.info("just executed writeToDB - 3");

            String inputName = getInputName(dib);
            AddNewSubjectServlet.saveFieldNotes(inputName, fdn, dndao, dib.getData().getId(),
              "ItemData", currentStudy);

            success = success && temp;

            ArrayList childItems = dib.getChildren();
            for (int j = 0; j < childItems.size(); j++) {
              DisplayItemBean child = (DisplayItemBean) childItems.get(j);
              temp = writeToDB(child, iddao,1);
              //logger.info("just executed writeToDB - 4");
              inputName = getInputName(child);
              AddNewSubjectServlet.saveFieldNotes(inputName, fdn, dndao, dib.getData().getId(),
                "ItemData", currentStudy);
              success = success && temp;
            }
          }
        }


        // now check if CRF is marked complete
        boolean markComplete = fp.getString(INPUT_MARK_COMPLETE).equals(VALUE_YES);
        boolean markSuccessfully = false; // if the CRF was marked complete
        // successfully
        if (markComplete) {
          logger.info("need to mark CRF as complete");
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
          addPageMessage(resexception.getString("database_error"));
          request.setAttribute(BEAN_DISPLAY, section);
          session.removeAttribute(GROUP_HAS_DATA);
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
              addPageMessage(respage.getString("data_saved_CRF_marked_complete"));
              session.removeAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
              session.removeAttribute(GROUP_HAS_DATA);
              request.setAttribute("eventId", new Integer(ecb.getStudyEventId()).toString());
              forwardPage(Page.ENTER_DATA_FOR_STUDY_EVENT_SERVLET);
            } else {
              //use clicked 'save'
              addPageMessage(respage.getString("data_saved_continue_entering_edit_later"));
              request.setAttribute(INPUT_EVENT_CRF, ecb);
              request.setAttribute(INPUT_EVENT_CRF_ID, new Integer(ecb.getId()).toString());
              //forward to the next section if the previous one is not the last section
              if (!section.isLastSection()) {
                request.setAttribute(INPUT_SECTION, nextSec);
                request.setAttribute(INPUT_SECTION_ID, new Integer(nextSec.getId()).toString());
              } else {
                //already the last section, should go back to view event page
                session.removeAttribute(GROUP_HAS_DATA);
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
                request.setAttribute("tab", (new Integer(tabNum +1)).toString());
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
  protected boolean getInputBeans() throws InsufficientPermissionException {

    fp = new FormProcessor(request);
    ecdao = new EventCRFDAO(sm.getDataSource());
    sdao = new SectionDAO(sm.getDataSource());
    //BWP >>we should have the correct crfVersionId, in order to acquire the correct
    //section IDs
    ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
    if (ecb == null) {
      int eventCRFId = fp.getInt(INPUT_EVENT_CRF_ID, true);
      if (eventCRFId > 0) {
        // there is an event CRF already, only need to update
        ecb = (EventCRFBean) ecdao.findByPK(eventCRFId);
        // ecb.setUpdatedDate(new Date());
        // ecb.setUpdater(ub);
        // ecb = (EventCRFBean) ecdao.update(ecb);
        //logger.info("found an event crf id "+eventCRFId);
      } else {
        try {
          //if (ecb.getInterviewerName() != null) {
          ecb = createEventCRF();
          // below added tbh, 092007
          //logger.info("did not find an event crf id at line 731, created an ecb id "
          //  + ecb.getId());
          request.setAttribute(INPUT_EVENT_CRF, ecb);
          //}
        } catch (InconsistentStateException ie) {
          addPageMessage(ie.getOpenClinicaMessage());
          throw new InsufficientPermissionException(Page.SUBMIT_DATA_SERVLET, ie.getOpenClinicaMessage(), "1");
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

    //  we will look into db to see if any repeating items for this CRF section
    ItemGroupDAO igdao = new ItemGroupDAO(sm.getDataSource());
    //find any item group which doesn't equal to 'Ungrouped'
    List<ItemGroupBean> itemGroups = igdao.findGroupBySectionId(sectionId);
    if (!itemGroups.isEmpty()) {
      logger.info("This section has group");
      return true;
    }
    return false;

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
    locale = request.getLocale();
    //< resmessage = ResourceBundle.getBundle("org.akaza.openclinica.i18n.page_messages",locale);
    //< restext = ResourceBundle.getBundle("org.akaza.openclinica.i18n.notes",locale);
    //< resexception=ResourceBundle.getBundle("org.akaza.openclinica.i18n.exceptions",locale);

    EventCRFBean ecb;
    ecdao = new EventCRFDAO(sm.getDataSource());

    int crfVersionId = fp.getInt(INPUT_CRF_VERSION_ID);

    //logger.info("look specifically wrt crfversionid: "+crfVersionId);
    int studyEventId = fp.getInt(INPUT_STUDY_EVENT_ID);
    int eventDefinitionCRFId = fp.getInt(INPUT_EVENT_DEFINITION_CRF_ID);
    int subjectId = fp.getInt(INPUT_SUBJECT_ID);
    int eventCRFId = fp.getInt(INPUT_EVENT_CRF_ID);

    //logger.info("look specifically wrt event crf id: "+eventCRFId);

    //logger.info("Creating event CRF.  Study id: " + currentStudy.getId()
    // + "; CRF Version id: " + crfVersionId + "; Study Event id: " + studyEventId
    // + "; Event Definition CRF id: " + eventDefinitionCRFId + "; Subject: " + subjectId);

    StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
    StudySubjectBean ssb = ssdao.findBySubjectIdAndStudy(subjectId, currentStudy);

    if (ssb.getId()<=0) {
      addPageMessage(resexception.getString("begin_data_entry_without_event_but_subject"));
      throw new InconsistentStateException(
        Page.SUBMIT_DATA_SERVLET,
        resexception.getString("begin_data_entry_without_event_but_subject"));
    }

    StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
    StudyEventDefinitionBean sedb = seddao.findByEventDefinitionCRFId(eventDefinitionCRFId);
    //logger.info("study event definition:" + sedb.getId());
    if (sedb.getId()<=0) {
      addPageMessage(resexception.getString("begin_data_entry_without_event_but_study"));
      throw new InconsistentStateException(
        Page.SUBMIT_DATA_SERVLET,
        resexception.getString("begin_data_entry_without_event_but_study"));
    }

    CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
    EntityBean eb = cvdao.findByPK(crfVersionId);

    if (eb.getId()<=0) {
      addPageMessage(resexception.getString("begin_data_entry_without_event_but_CRF"));
      throw new InconsistentStateException(
        Page.SUBMIT_DATA_SERVLET,
        resexception.getString("begin_data_entry_without_event_but_CRF"));
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
      addPageMessage(resexception.getString("begin_data_entry_without_event_but_especified_event"));
      throw new InconsistentStateException(
        Page.SUBMIT_DATA_SERVLET,
        resexception.getString("begin_data_entry_without_event_but_especified_event"));
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
      //ecb.setOwnerId(ub.getId());
      //above depreciated, try without it, tbh
      ecb.setOwner(ub);
      ecb.setStatus(Status.AVAILABLE);
      ecb.setCompletionStatusId(1);
      ecb.setStudySubjectId(ssb.getId());
      ecb.setStudyEventId(studyEventId);
      ecb.setValidateString("");
      ecb.setValidatorAnnotations("");

      ecb = (EventCRFBean) ecdao.create(ecb);
      //logger.info("CREATED EVENT CRF");
    } else {
      // there is an event CRF already, only need to update
      ecb = (EventCRFBean) ecdao.findByPK(eventCRFId);
      ecb.setCRFVersionId(crfVersionId);
      ecb.setUpdatedDate(new Date());
      ecb.setUpdater(ub);
      ecb = (EventCRFBean) ecdao.update(ecb);

    }

    if (ecb.getId()<=0) {
      addPageMessage(resexception.getString("new_event_CRF_not_created"));
      throw new InconsistentStateException(Page.SUBMIT_DATA_SERVLET,
        resexception.getString("new_event_CRF_not_created"));
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
   * This methods will create an array of DisplayItemGroupBean, which contains multiple
   * rows for an item group on the data entry form.
   * @param digb The Item group which has multiple data rows
   * @param dbGroups The original array got from DB which contains multiple data rows
   * @param formGroups The new array got from front end which contains multiple data rows
   * @return new constructed formGroups, compare to dbGroups, some rows are update, some new ones are
   * added and some are removed
   */
  protected List<DisplayItemGroupBean> loadFormValueForItemGroup(
    DisplayItemGroupBean digb,
    List<DisplayItemGroupBean> dbGroups,
    List<DisplayItemGroupBean> formGroups) {

    int repeatMax = digb.getGroupMetaBean().getRepeatMax();
    //temp code to allow us to truncate
    if (repeatMax > 40 || repeatMax < 0) {
    	repeatMax = 40;
    }
    List<ItemBean> itBeans = idao.findAllItemsByGroupId(digb.getItemGroupBean().getId());
    logger.info("+++ starting to review groups: "+repeatMax);
    for (int i = 0; i < repeatMax; i++) {
      DisplayItemGroupBean formGroup = new DisplayItemGroupBean();
      formGroup.setItemGroupBean(digb.getItemGroupBean());
      formGroup.setGroupMetaBean(digb.getGroupMetaBean());

      ItemGroupBean igb = digb.getItemGroupBean();
      //want to do deep copy here, so always get a fresh copy for items, 
      //may use other better way to do, like clone
      List<DisplayItemBean> dibs =
        FormBeanUtil.getDisplayBeansFromItems(itBeans,
          sm.getDataSource(),
          ecb.getCRFVersionId(),
          sb.getId(), null);

      if (!StringUtil.isBlank(fp.getString(igb.getName() + "_" + i + ".newRow"))) {
        // the ordinal is the number got from [ ] and submitted by repetition javascript
        formGroup.setOrdinal(i);
        logger.info("+++ group ordinal: " + i + " igb name "+igb.getName());
        for (int j = 0; j < dibs.size(); j++) {
          DisplayItemBean displayItem = dibs.get(j);

          org.akaza.openclinica.bean.core.ResponseType rt = displayItem.getMetadata()
            .getResponseSet().getResponseType();
          if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CHECKBOX)
            || rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECTMULTI)) {

            //String inputName = igb.getName() + "_" + i + getInputName(displayItem);
            String inputName= getGroupItemInputName(digb, i, displayItem );
            ArrayList valueArray = fp.getStringArray(inputName);            
              displayItem.loadFormValue(valueArray);
            
          } else {
            //String inputName = igb.getName() + "_" + i + getInputName(displayItem);;
            String inputName= getGroupItemInputName(digb, i, displayItem );
            logger.info("input " + inputName + ": " + fp.getString(inputName));
            displayItem.loadFormValue(fp.getString(inputName));
           

          }

        }
        formGroup.setItems(dibs);
        formGroups.add(formGroup);

      }

    } //end of for (int i = 0; i < repeatMax; i++) 
     
    //logger.info("group row size:" + formGroups.size());
    // suppose we have 3 rows of data from db, the orginal order is 0,1,2,
    // repetition model will submit row number in [ ] like the following:
    // 0,1,2.. consecutive numbers, means no row removed in between
    // 0,1,3,4.. the 2rd row is removed, 3 and 4 are new rows
    for (int j = 0; j < formGroups.size(); j++) {
      DisplayItemGroupBean formItemGroup = (DisplayItemGroupBean) formGroups.get(j);
      // formItemGroup.setEditFlag("remove");
      if (formItemGroup.getOrdinal() > dbGroups.size() - 1) {
        formItemGroup.setEditFlag("add");
      } else {
        for (int i = 0; i < dbGroups.size(); i++) {
          DisplayItemGroupBean dbItemGroup = (DisplayItemGroupBean) dbGroups.get(i);
          if (formItemGroup.getOrdinal() == i) {
            // the first row is different, it could be blank on page just for
            // display, so need to insert this row not update
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
                    //this will save the data from IDE complete, used only for DDE
                    formDib.setDbData(dib.getData());                   
                    
                    logger.info("+++ form dib get data set id "+data.getId());
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

    }

    logger.info("+++ DB group row:" + dbGroups.size());

    for (int i = 0; i < dbGroups.size(); i++) {
      DisplayItemGroupBean dbItemGroup = (DisplayItemGroupBean) dbGroups.get(i);
      if (!"edit".equalsIgnoreCase(dbItemGroup.getEditFlag()) &&
          !"initial".equalsIgnoreCase(dbItemGroup.getEditFlag())) {
        logger.info("+++ one row removed");
        dbItemGroup.setEditFlag("remove");
      }

    }

    return formGroups;
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
                                                             DisplayItemBean dib, String inputName);


  protected abstract List<DisplayItemGroupBean> validateDisplayItemGroupBean(DiscrepancyValidator v,
                                                                             DisplayItemGroupBean dib, List<DisplayItemGroupBean> digbs,List<DisplayItemGroupBean> formGroups);
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
  protected DisplayItemBean validateDisplayItemBeanText(
    DiscrepancyValidator v,
    DisplayItemBean dib,
    String inputName) {

    if (StringUtil.isBlank(inputName)){// for single items
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
        if (ibMeta.isRequired()) {
          v.addValidation(inputName, Validator.IS_REQUIRED);
        }
      } else {

        if (idt.equals(ItemDataType.ST)) {
          //a string's size could be more than 255, which is more than
          //the db field length
          v.addValidation(inputName, Validator.LENGTH_NUMERIC_COMPARISON,
            NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);

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
                                                            DisplayItemBean dib, String inputName) {
    if (StringUtil.isBlank(inputName)){
      inputName = getInputName(dib);
    }
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
                                                              DisplayItemBean dib, String inputName) {
    if (StringUtil.isBlank(inputName)){
      inputName = getInputName(dib);
    }
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
   * Creates an input name for an item data entry in an item group
   * @param digb
   * @param ordinal
   * @param dib
   * @return
   */
  public final String getGroupItemInputName(
    DisplayItemGroupBean digb,
    int ordinal,
    DisplayItemBean dib) {
    String inputName = digb.getItemGroupBean().getName() + "_" + ordinal + getInputName(dib);

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
  protected boolean writeToDB(DisplayItemBean dib, ItemDataDAO iddao, int ordinal) {
    ItemDataBean idb = dib.getData();

    idb.setItemId(dib.getItem().getId());
    idb.setEventCRFId(ecb.getId());

    if (idb.getValue().equals("")) {
      idb.setStatus(getBlankItemStatus());
    } else {
      idb.setStatus(getNonBlankItemStatus());
    }
    if(StringUtil.isBlank(dib.getEditFlag())) {

      if (!idb.isActive()) {
        // will this need to change for double data entry?
        idb.setOrdinal(ordinal);
        idb.setCreatedDate(new Date());
        idb.setOwner(ub);
        idb = (ItemDataBean) iddao.create(idb);
      } else {
        idb.setUpdater(ub);
        idb = (ItemDataBean) iddao.updateValue(idb);
      }
    } else {
      //for the items in group, they have editFlag
      if ("add".equalsIgnoreCase(dib.getEditFlag())) {
        idb.setOrdinal(ordinal);
        idb.setCreatedDate(new Date());
        idb.setOwner(ub);
        logger.info("create a new item data" + idb.getItemId() + idb.getValue());
        idb = (ItemDataBean) iddao.create(idb);
      } else if ("edit".equalsIgnoreCase(dib.getEditFlag())) {
        idb.setUpdater(ub);
        logger.info("update an item data" + idb.getId()+ ":" + idb.getValue());
        idb = (ItemDataBean) iddao.updateValue(idb);



      } else if ("remove".equalsIgnoreCase(dib.getEditFlag())) {
        logger.info("remove an item data" + idb.getItemId() + idb.getValue());
        idb.setUpdater(ub);
        idb.setStatus(Status.DELETED);
        idb = (ItemDataBean) iddao.updateValue(idb);
      }

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
  protected DisplaySectionBean getDisplayBean(boolean hasGroup,
                                              boolean includeUngroupedItems) throws Exception {
    DisplaySectionBean section = new DisplaySectionBean();
    FormProcessor fp = new FormProcessor(request);

    //Find out whether there are ungrouped items in this section
    boolean hasUngroupedItems=false;
    int eventDefinitionCRFId = fp.getInt("eventDefinitionCRFId");
    //Use this class to find out whether there are ungrouped items in this section
    FormBeanUtil formBeanUtil = new  FormBeanUtil();
    List<DisplayItemGroupBean> itemGroups=new ArrayList<DisplayItemGroupBean>();
    if (hasGroup) {
      DisplaySectionBean newDisplayBean = new DisplaySectionBean();
      if(includeUngroupedItems){
        //Null values: this method adds null values to the displayitembeans
        newDisplayBean = formBeanUtil.createDisplaySectionBWithFormGroups(
          sb.getId(), ecb
          .getCRFVersionId(), sm,eventDefinitionCRFId);
      } else {
        newDisplayBean = formBeanUtil.createDisplaySectionWithItemGroups(
          sb.getId(), ecb
          .getCRFVersionId(), sm,eventDefinitionCRFId);
      }
      itemGroups = newDisplayBean.getDisplayFormGroups();
      logger.info("how many groups:" + itemGroups.size());

      //setDataForDisplayItemGroups(itemGroups, sb,ecb,sm);

      section.setDisplayFormGroups(itemGroups);

    }

    //Find out whether any display items are *not* grouped; see issue 1689
    hasUngroupedItems= formBeanUtil.sectionHasUngroupedItems(
      sm.getDataSource(),
      sb.getId(),itemGroups);


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

    //Use itemGroups to determine if there are any ungrouped items

    // get all the parent display item beans not in group
    ArrayList displayItems= getParentDisplayItems(
      hasGroup,sb, edcb, idao, ifmdao, iddao, hasUngroupedItems);


    // now sort them by ordinal
    Collections.sort(displayItems);

    // now get the child DisplayItemBeans
    for (int i = 0; i < displayItems.size(); i++) {
      DisplayItemBean dib = (DisplayItemBean) displayItems.get(i);
      dib.setChildren(getChildrenDisplayItems(dib, edcb));

      //TODO use the setData command here to make sure we get a value?
      if (shouldLoadDBValues(dib)) {
        //logger.info("should load db values is true, set value");
        dib.loadDBValue();
        //logger.info("just got data loaded: "+dib.getData().getValue());
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
      ArrayList displayItems = getParentDisplayItems(false,sb, edcb, idao,
        ifmdao, iddao, false);

      // now sort them by ordinal
      Collections.sort(displayItems);

      // now get the child DisplayItemBeans
      for (int i = 0; i < displayItems.size(); i++) {
        DisplayItemBean dib = (DisplayItemBean) displayItems.get(i);
        dib.setChildren(getChildrenDisplayItems(dib, edcb));

        if (shouldLoadDBValues(dib)) {
          logger.info("should load db values is true, set value");
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
   * For each single item in this section which is a parent, get a DisplayItemBean
   * corresponding to that item. Note that an item is a parent iff its parentId ==
   * 0.
   *
   * @param sb
   *            The section whose items we are retrieving.
   * @param hasUngroupedItems
   * @return An array of DisplayItemBean objects, one per parent item in the
   *         section. Note that there is no guarantee on the ordering of the
   *         objects.
   * @throws Exception
   */
  private ArrayList getParentDisplayItems(boolean hasGroup,
                                          SectionBean sb, EventDefinitionCRFBean edcb,
                                          ItemDAO idao, ItemFormMetadataDAO ifmdao,
                                          ItemDataDAO iddao, boolean hasUngroupedItems) throws Exception {
    ArrayList answer = new ArrayList();

    // DisplayItemBean objects are composed of an ItemBean, ItemDataBean and
    // ItemFormDataBean.
    // However the DAOs only provide methods to retrieve one type of bean at a
    // time (per section)
    // the displayItems hashmap allows us to compose these beans into
    // DisplayItemBean objects,
    // while hitting the database only three times
    HashMap displayItems = new HashMap();

    //ArrayList items = idao.findAllParentsBySectionId(sb.getId());

    ArrayList items = new ArrayList();
    if (hasGroup) {
      //issue 1689: this method causes problems with items that have
      //been defined as grouped, then redefined as ungrouped; thus it
      // is "checked twice" with hasUngroupedItems. 
      // See: FormBeanUtil.sectionHasUngroupedItems.
      if(hasUngroupedItems)  {
        items = idao.findAllUngroupedParentsBySectionId(sb.getId());
      }
    } else {
      logger.info("no item groups");
      items = idao.findAllParentsBySectionId(sb.getId());
    }
    //logger.info("items size" + items.size());
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
        logger.info("should load db values is true, set value");
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

  protected void populateNotesWithDBNoteCounts(
    FormDiscrepancyNotes discNotes,
    DisplaySectionBean section) {
    DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());

    //ArrayList items = section.getItems();

    List<DisplayItemWithGroupBean> allItems = section.getDisplayItemGroups();

    for (DisplayItemWithGroupBean itemWithGroup:allItems) {
      if (itemWithGroup.isInGroup()) {
        DisplayItemGroupBean dgb = itemWithGroup.getItemGroup();
        List<DisplayItemGroupBean> digbs = itemWithGroup.getItemGroups();

        for (int i=0; i<digbs.size(); i++) {
          DisplayItemGroupBean displayGroup= digbs.get(i);
          List<DisplayItemBean> items = displayGroup.getItems();
          for(DisplayItemBean dib : items){
            int itemDataId = dib.getData().getId();
            int numNotes = dndao.findNumExistingNotesForItem(itemDataId);
            //String inputName = displayGroup.getItemGroupBean().getName() + "_" + i + "." + getInputName(dib); 
            String inputName= getGroupItemInputName(displayGroup, i,dib);
            discNotes.setNumExistingFieldNotes(inputName, numNotes);
            dib.setNumDiscrepancyNotes(numNotes + discNotes.getNotes(inputName).size());

          }
        }
      }
      else {
        DisplayItemBean dib = itemWithGroup.getSingleItem();
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


      }
    }

    section.setDisplayItemGroups(allItems);
  }

  /**
   * The following methods are for 'mark CRF complete'
   *
   * @return
   */

  protected boolean markCRFComplete() throws Exception {
    locale = request.getLocale();
    //< respage = ResourceBundle.getBundle("org.akaza.openclinica.i18n.page_messages",locale);
    //< restext = ResourceBundle.getBundle("org.akaza.openclinica.i18n.notes",locale);
    //< resexception=ResourceBundle.getBundle("org.akaza.openclinica.i18n.exceptions",locale);
    getEventCRFBean();
    getEventDefinitionCRFBean();
    DataEntryStage stage = ecb.getStage();

    // request.setAttribute(TableOfContentsServlet.INPUT_EVENT_CRF_BEAN, ecb);
    // request.setAttribute(INPUT_EVENT_CRF_ID, new Integer(ecb.getId()));
    logger.info("inout_event_crf_id:" +ecb.getId());
    Page errorPage = getJSPPage();

    if (stage.equals(DataEntryStage.UNCOMPLETED)
      || stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE)
      || stage.equals(DataEntryStage.LOCKED)) {
      addPageMessage(respage.getString("not_mark_CRF_complete1"));
      return false;
    }

    if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE)
      || stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {

      if (!edcb.isDoubleEntry()) {
        addPageMessage(respage.getString("not_mark_CRF_complete2"));
        return false;
      }
    }

    /*if (!isEachSectionReviewedOnce()) {
      addPageMessage("You may not mark this Event CRF complete, because there are some sections which have not been reviewed once.");
      return false;
    } */

    if (!isEachRequiredFieldFillout()) {
      addPageMessage(respage.getString("not_mark_CRF_complete4"));
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
    } else if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE)
      || stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
      newStatus = Status.UNAVAILABLE;
      ecb.setDateValidateCompleted(new Date());
      ide = false;
    }
    ecb.setStatus(newStatus);
    ecb = (EventCRFBean) ecdao.update(ecb);
    //note the below statement only updates the DATES, not the STATUS
    ecdao.markComplete(ecb, ide);

    // for the non-reviewed sections, no item data in DB yet, need to
    //create them
    if (!isEachSectionReviewedOnce()) {
      saveItemsToMarkComplete();
    }
    iddao.updateStatusByEventCRF(ecb, newStatus);

    // change status for study event
    StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
    StudyEventBean seb = (StudyEventBean) sedao.findByPK(ecb.getStudyEventId());
    seb.setUpdatedDate(new Date());
    seb.setUpdater(ub);

    EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
    ArrayList allCRFs = (ArrayList) ecdao.findAllByStudyEvent(seb);
    ArrayList allEDCs = (ArrayList) edcdao.findAllActiveByEventDefinitionId(seb
      .getStudyEventDefinitionId());
    boolean eventCompleted = true;
    boolean allRequired = true;
    int allEDCsize = allEDCs.size();
    //go through all the crfs and check their status

    for (int i = 0; i < allCRFs.size(); i++) {
      EventCRFBean ec = (EventCRFBean) allCRFs.get(i);
      if (!ec.getStatus().equals(Status.UNAVAILABLE) && (ec.getDateInterviewed() != null)) {
        eventCompleted = false;
        break;
      }
    }

    for (int ii = 0; ii < allEDCs.size(); ii++) {
      EventDefinitionCRFBean edcBean = (EventDefinitionCRFBean)allEDCs.get(ii);
      if (!edcBean.isRequiredCRF()) {
        allRequired = false;
        allEDCsize--;
      }
    }

    if (!allRequired) {
      logger.info("SEB contains some nonrequired CRFs: "+allEDCsize+" vs "+allEDCs.size());
    }

    if (eventCompleted && (allCRFs.size() >= allEDCs.size())) {
      seb.setSubjectEventStatus(SubjectEventStatus.COMPLETED);
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
    ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
    ItemDAO idao = new ItemDAO(sm.getDataSource());
    int allRequiredNum = idao.findAllRequiredByCRFVersionId(ecb.getCRFVersionId());
    int allRequiredFilledOut = iddao.findAllRequiredByEventCRFId(ecb);
    if (allRequiredNum > allRequiredFilledOut){
      return false;
    }

    ArrayList allFilled = iddao.findAllBlankRequiredByEventCRFId(ecb.getId(), ecb.getCRFVersionId());
    if (!allFilled.isEmpty()) {
      return false;
    }
    return true;
  }

  /**
   * 06/13/2007- jxu
   * Since we don't require users to review each section before
   * mark a CRF as complete, we need to create item data in the database
   * because items will not be created unless the section which contains the items is 
   * reviewed by users
   *
   */
  private void saveItemsToMarkComplete() throws Exception {
    ArrayList sections = sdao.findAllByCRFVersionId(ecb.getCRFVersionId());
    CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
    for (int i = 0; i < sections.size(); i++) {
      SectionBean sb = (SectionBean) sections.get(i);
      if (!isSectionReviewedOnce(sb)) {
        ArrayList items = idao.findAllBySectionId(sb.getId());
        for (int j=0;j< items.size();j++) {
          ItemBean item = (ItemBean)items.get(j);
          ItemDataBean idb = new ItemDataBean();
          idb.setItemId(item.getId());
          idb.setEventCRFId(ecb.getId());
          idb.setCreatedDate(new Date());
          idb.setOrdinal(1);
          idb.setOwner(ub);
          idb.setStatus(Status.UNAVAILABLE);
          idb.setValue("");
          iddao.create(idb);
        }
      }
    }


  }

  /** Checks if a section is reviewed at least once by user
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
      if ((numItemsPending == 0) && (numItems > 0)) {
        return false;
      }
    } else {
      if ((numItemsCompleted == 0) && (numItems > 0)) {
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

  /**
   * Constructs a list of DisplayItemWithGroupBean, 
   * which is used for display a section of items on the UI
   * @param dsb
   * @param hasItemGroup
   * @return
   */
  protected List<DisplayItemWithGroupBean> createItemWithGroups(
    DisplaySectionBean dsb,
    boolean hasItemGroup,int eventCRFDefId) {

    List<DisplayItemWithGroupBean> displayItemWithGroups =
      new ArrayList<DisplayItemWithGroupBean>();

    //For adding null values to display items
    FormBeanUtil formBeanUtil = new FormBeanUtil();
    List<String> nullValuesList=new ArrayList<String>();
    //BWP>> Get a List<String> of any null values such as NA or NI
    //method returns null values as a List<String>
    nullValuesList=formBeanUtil.getNullValuesByEventCRFDefId(eventCRFDefId,
      sm.getDataSource());
    //>>BWP

    ArrayList items = dsb.getItems();
    logger.info("single items size: " + items.size());
    for (int i=0; i<items.size();i++) {
      DisplayItemBean item = (DisplayItemBean)items.get(i);
      DisplayItemWithGroupBean newOne = new DisplayItemWithGroupBean();
      newOne.setSingleItem(item);
      newOne.setOrdinal(item.getMetadata().getOrdinal());
      newOne.setInGroup(false);
      newOne.setPageNumberLabel(item.getMetadata().getPageNumberLabel());
      displayItemWithGroups.add(newOne);
      //logger.info("just added on line 1979: "+newOne.getSingleItem().getData().getValue());
    }
    if (hasItemGroup) {
      ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
      ArrayList data = iddao.findAllActiveBySectionIdAndEventCRFId(sb.getId(), ecb.getId());
      logger.info("how many groups:" + dsb.getDisplayFormGroups().size());

      for(DisplayItemGroupBean itemGroup : dsb.getDisplayFormGroups()) {
        DisplayItemWithGroupBean newOne = new DisplayItemWithGroupBean();
        //to arrange item groups and other single items, the ordinal of 
        //a item group will be the ordinal of the first item in this group
        DisplayItemBean firstItem = (DisplayItemBean)itemGroup.getItems().get(0);
        newOne.setPageNumberLabel(firstItem.getMetadata().getPageNumberLabel());

        newOne.setItemGroup(itemGroup);
        newOne.setInGroup(true);
        newOne.setOrdinal(itemGroup.getGroupMetaBean().getOrdinal());

        List<ItemBean> itBeans = idao.findAllItemsByGroupId(itemGroup.getItemGroupBean().getId());

        boolean hasData = false;
        //if a group has repetitions, the number of data of 
        //first item should be same as the row number
        for (int i=0; i<data.size();i++) {
          ItemDataBean idb = (ItemDataBean)data.get(i);
          if(idb.getItemId() == firstItem.getItem().getId()) {
            hasData =true;
            DisplayItemGroupBean digb = new DisplayItemGroupBean();
            //always get a fresh copy for items, may use other better way to
            //do deep copy, like clone
            List<DisplayItemBean> dibs =
              FormBeanUtil.getDisplayBeansFromItems(itBeans,
                sm.getDataSource(),
                ecb.getCRFVersionId(),
                sb.getId(), nullValuesList);

            digb.setItems(dibs);
            digb.setGroupMetaBean(itemGroup.getGroupMetaBean());
            digb.setItemGroupBean(itemGroup.getItemGroupBean());
            newOne.getItemGroups().add(digb);
            newOne.getDbItemGroups().add(digb);

          }
        }

        List<DisplayItemGroupBean> groupRows = newOne.getItemGroups();
        logger.info("how many group rows:" + groupRows.size());
        if (hasData) {
          session.setAttribute(GROUP_HAS_DATA,Boolean.TRUE);
          //iterate through the group rows, set data for each item in the group  
          for (int i = 0; i < groupRows.size(); i++) {
            DisplayItemGroupBean displayGroup = groupRows.get(i);
            for (DisplayItemBean dib : displayGroup.getItems()) {
              for (int j = 0; j < data.size(); j++) {
                ItemDataBean idb = (ItemDataBean) data.get(j);
                if ((idb.getItemId() == dib.getItem().getId()) && !idb.isSelected()) {
                  idb.setSelected(true);
                  dib.setData(idb);
                  if (shouldLoadDBValues(dib)) {
                    //logger.info("should load db values is true, set value");
                    dib.loadDBValue();
                  }
                  break;
                }
              }
            }

          }
        }
        else {
          session.setAttribute(GROUP_HAS_DATA,Boolean.FALSE);
          //no data, still add a blank row for displaying
          DisplayItemGroupBean digb = new DisplayItemGroupBean();
          List<DisplayItemBean> dibs =
            FormBeanUtil.getDisplayBeansFromItems(itBeans,
              sm.getDataSource(),
              ecb.getCRFVersionId(),
              sb.getId(), nullValuesList);
          digb.setItems(dibs);
          digb.setEditFlag("initial");
          digb.setGroupMetaBean(itemGroup.getGroupMetaBean());
          digb.setItemGroupBean(itemGroup.getItemGroupBean());
          newOne.getItemGroups().add(digb);
          newOne.getDbItemGroups().add(digb);

        }



        displayItemWithGroups.add(newOne);
      }

    }//if hasItemGroup
    Collections.sort(displayItemWithGroups);

    //add null values to displayitems in the itemGroups of
    // DisplayItemWithGroupBeans;
    //These item groups are used by the data entry screens
  /*  if(nullValuesList != null && (! nullValuesList.isEmpty())) {
      formBeanUtil.addNullValuesToDisplayItemWithGroupBeans(
        displayItemWithGroups,
        nullValuesList);
    }*/
    return displayItemWithGroups;
  }

}
