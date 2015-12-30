/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import java.util.*;
import java.text.SimpleDateFormat;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.*;
import org.akaza.openclinica.bean.submit.*;
import org.akaza.openclinica.control.core.Utils;
import org.akaza.openclinica.control.submit.DataEntryServlet;
import org.akaza.openclinica.control.submit.TableOfContentsServlet;
import org.akaza.openclinica.core.form.DiscrepancyValidator;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.*;
import org.akaza.openclinica.dao.submit.*;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.form.FormBeanUtil;

/**
 * @author jxu
 *
 * View a CRF version section data entry
 */
public class ViewSectionDataEntryServlet extends DataEntryServlet {

  Locale locale;
  //<  ResourceBundleresexception,respage,resword;

  /**
   * Checks whether the user has the correct privilege
   */
  public void mayProceed() throws InsufficientPermissionException {
    mayAccess();

    locale = request.getLocale();
    //< resexception=ResourceBundle.getBundle("org.akaza.openclinica.i18n.exceptions",locale);
    //< respage = ResourceBundle.getBundle("org.akaza.openclinica.i18n.page_messages",locale);
    //< resword = ResourceBundle.getBundle("org.akaza.openclinica.i18n.words",locale);

    if (ub.isSysAdmin()) {
      return;
    }
    if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
      || currentRole.getRole().equals(Role.COORDINATOR)
      || currentRole.getRole().equals(Role.INVESTIGATOR)
      || currentRole.getRole().equals(Role.RESEARCHASSISTANT)) {
      return;
    }

    addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " "
      + respage.getString("change_study_contact_sysadmin"));
    throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_director"), "1");

  }

  public void processRequest() throws Exception {
    FormProcessor fp = new FormProcessor(request);
    int crfVersionId = fp.getInt("crfVersionId");
    int sectionId = fp.getInt("sectionId");
    int eventCRFId = fp.getInt("ecId");
    //YW <<
    int sedId = fp.getInt("sedId");
    request.setAttribute("sedId", sedId + "");
    int crfId = fp.getInt("crfId");
    //BWP>> ... try to get crfId from crfVersionId
    if(crfId == 0 && crfVersionId > 0)  {
      CRFVersionDAO crfVDao = new CRFVersionDAO(sm.getDataSource());
      CRFVersionBean crvVBean  = (CRFVersionBean) crfVDao.findByPK(crfVersionId);
      if(crvVBean != null) {
        crfId=crvVBean.getCrfId();
      }
    }

    //YW >>
    int eventDefinitionCRFId = fp.getInt("eventDefinitionCRFId");
    if(crfId == 0 && eventDefinitionCRFId > 0)  {
      EventDefinitionCRFDAO eventCrfDao = new EventDefinitionCRFDAO(sm.getDataSource());
      //try to get crfId from eventDefinitionCRFId
      EventDefinitionCRFBean eventDefBean = (EventDefinitionCRFBean) eventCrfDao.findByPK(eventDefinitionCRFId);
      if(eventDefBean != null) {
        crfId= eventDefBean.getCrfId();
      }
    }
    request.setAttribute("crfId", crfId + "");
    String printVersion = fp.getString("print");
    //BWP>> this has to be removed for CRFs that do not display an interviewdate
    //for a particular event
    session.removeAttribute("presetValues");

    EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
    SectionDAO sdao = new SectionDAO(sm.getDataSource());
    String age = "";
    if (sectionId == 0 && crfVersionId == 0 & eventCRFId ==0) {
      addPageMessage(respage.getString("please_choose_a_CRF_to_view"));
      forwardPage(Page.SUBMIT_DATA_SERVLET);
      return;
    }
    if (crfVersionId > 0) {//for viewing blank CRF
      DisplayTableOfContentsBean displayBean = ViewTableOfContentServlet.
        getDisplayBean(sm.getDataSource(), crfVersionId);
      request.setAttribute("toc",displayBean);
      ArrayList sections = displayBean.getSections();

      request.setAttribute("sectionNum", sections.size() + "");
      if (!sections.isEmpty()) {
        if (sectionId == 0) {
          SectionBean firstSec = (SectionBean) sections.get(0);
          sectionId = firstSec.getId();
        }
      } else {
        addPageMessage(respage.getString("there_are_no_sections_ins_this_CRF_version"));
        if (eventCRFId == 0) {
          forwardPage(Page.CRF_LIST_SERVLET);
        } else {
          forwardPage(Page.SUBMIT_DATA_SERVLET);
        }
        return;
      }

    } else if (eventCRFId >0){
      //for event crf, the input crfVersionId from url =0  
      super.ecb = (EventCRFBean) ecdao.findByPK(eventCRFId);

      DisplayTableOfContentsBean displayBean = TableOfContentsServlet.
        getDisplayBean(super.ecb,sm.getDataSource(),currentStudy);
      //Make sure that the interviewDate in the eventCRF is properly formatted
      //for viewSectionDataEntry.jsp --> interviewer.jsp
     // int studyEventId = (Integer)request.getAttribute("studyEvent");
      SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
      //TODO l10n dates?
      Date tmpDate = displayBean.getEventCRF().getDateInterviewed();
      String formattedInterviewerDate;
      try {
        formattedInterviewerDate = formatter.format(tmpDate);
      } catch (Exception e) {
        formattedInterviewerDate="";
      }
      HashMap presetVals = (HashMap)session.getAttribute("presetValues");
      if(presetVals == null) {
        presetVals = new HashMap();
        session.setAttribute("presetValues",presetVals);
      }
      presetVals.put("interviewDate",formattedInterviewerDate);
      request.setAttribute("toc",displayBean);


      ArrayList sections = displayBean.getSections();

      request.setAttribute("sectionNum", sections.size() + "");
      if (!sections.isEmpty()) {
        if (sectionId == 0) {
          SectionBean firstSec = (SectionBean) sections.get(0);
          sectionId = firstSec.getId();
        }
      } else {
        addPageMessage(respage.getString("there_are_no_sections_ins_this_CRF"));
        forwardPage(Page.SUBMIT_DATA_SERVLET);
        return;
      }
    }

    super.sb = (SectionBean) sdao.findByPK(sectionId);
    if (eventCRFId == 0) {
      super.ecb = new EventCRFBean();
      super.ecb.setCRFVersionId(sb.getCRFVersionId());
    } else {
      super.ecb = (EventCRFBean) ecdao.findByPK(eventCRFId);

      // This is the StudySubjectBean
      StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
      StudySubjectBean sub = (StudySubjectBean) ssdao.findByPK(super.ecb.getStudySubjectId());
      // This is the SubjectBean
      SubjectDAO subjectDao = new SubjectDAO(sm.getDataSource());
      int subjectId = sub.getSubjectId();
      int studyId = sub.getStudyId();
      SubjectBean subject = (SubjectBean) subjectDao.findByPK(subjectId);
      // Let us process the age
      if (currentStudy.getStudyParameterConfig().getCollectDob().equals("1")) {
        StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
        StudyEventBean se = (StudyEventBean)sedao.findByPK(super.ecb.getStudyEventId());
        StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
        StudyEventDefinitionBean sed =(StudyEventDefinitionBean)seddao.findByPK(se.getStudyEventDefinitionId());
        se.setStudyEventDefinition(sed);
        request.setAttribute("studyEvent",se);

        //YW 11-16-2007 enrollment-date is used for computing age
        age = Utils.getInstacne().processAge(sub.getEnrollmentDate(), subject.getDateOfBirth());
      }
      // Get the study then the parent study
      StudyDAO studydao = new StudyDAO(sm.getDataSource());
      StudyBean study = (StudyBean) studydao.findByPK(studyId);

      if (study.getParentStudyId() > 0) {
        // this is a site,find parent
        StudyBean parentStudy = (StudyBean) studydao.findByPK(study.getParentStudyId());
        request.setAttribute("studyTitle", parentStudy.getName() + " - " + study.getName());
      } else {
        request.setAttribute("studyTitle", study.getName());
      }

      request.setAttribute("studySubject", sub);
      request.setAttribute("subject", subject);
      request.setAttribute("age", age);



    }
    FormBeanUtil formUtil = new FormBeanUtil();
    // DisplaySectionBean newDisplayBean = new DisplaySectionBean();

    boolean hasItemGroup=false;
    //we will look into db to see if any repeating items for this CRF section
    ItemGroupDAO igdao = new ItemGroupDAO(sm.getDataSource());
    List<ItemGroupBean> itemGroups = igdao.findGroupBySectionId(sectionId);
    if (!itemGroups.isEmpty()) {
      hasItemGroup = true;
      /*  newDisplayBean = formUtil.
        createDisplaySectionBWithFormGroups(sectionId,crfVersionId, sm );*/
    }



    //if the List of DisplayFormGroups is empty, then the servlet defers to the prior method
    //of generating a DisplaySectionBean for the application

    DisplaySectionBean dsb;
    //want to get displayBean with grouped and ungrouped items
    dsb = super.getDisplayBean(hasItemGroup,true);
    if(hasItemGroup)   {
      //  dsb.setDisplayItemGroups(newDisplayBean.getDisplayItemGroups());
      request.setAttribute("new_table",true);
    }
    //If the Horizontal type table will be used, then  set the DisplaySectionBean's
    //DisplayFormGroups List to the ones we have just generated
    List<DisplayItemWithGroupBean> displayItemWithGroups =
      super.createItemWithGroups(dsb, hasItemGroup,eventDefinitionCRFId);
    dsb.setDisplayItemGroups(displayItemWithGroups);

    request.setAttribute(BEAN_DISPLAY, dsb);
    request.setAttribute(BEAN_ANNOTATIONS, ecb.getAnnotations());
    request.setAttribute("sec", sb);
    request.setAttribute("EventCRFBean", super.ecb);




    int tabNum = 0;
    if (fp.getString("tabId") == null) {
      tabNum = 1;
    } else {
      tabNum = fp.getInt("tabId");
    }
    request.setAttribute("tabId", (new Integer(tabNum)).toString());

    if ("yes".equalsIgnoreCase(printVersion)) {
      forwardPage(Page.VIEW_SECTION_DATA_ENTRY_PRINT);
    } else {
      forwardPage(Page.VIEW_SECTION_DATA_ENTRY);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#getBlankItemStatus()
   */
  protected Status getBlankItemStatus() {
    return Status.AVAILABLE;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#getNonBlankItemStatus()
   */
  protected Status getNonBlankItemStatus() {
    return edcb.isDoubleEntry() ? Status.PENDING : Status.UNAVAILABLE;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#getEventCRFAnnotations()
   */
  protected String getEventCRFAnnotations() {
    return ecb.getAnnotations();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#setEventCRFAnnotations(java.lang.String)
   */
  protected void setEventCRFAnnotations(String annotations) {
    ecb.setAnnotations(annotations);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#getJSPPage()
   */
  protected Page getJSPPage() {
    return Page.VIEW_SECTION_DATA_ENTRY;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#getServletPage()
   */
  protected Page getServletPage() {
    return Page.VIEW_SECTION_DATA_ENTRY_SERVLET;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#validateInputOnFirstRound()
   */
  protected boolean validateInputOnFirstRound() {
    return true;
  }

  /* (non-Javadoc)
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#validateDisplayItemBean(org.akaza.openclinica.core.form.Validator, org.akaza.openclinica.bean.submit.DisplayItemBean)
   */
  protected DisplayItemBean validateDisplayItemBean(DiscrepancyValidator v,
                                                    DisplayItemBean dib, String inputName) {
    ItemBean ib = dib.getItem();
    org.akaza.openclinica.bean.core.ResponseType rt = dib.getMetadata().getResponseSet().getResponseType();


    // note that this step sets us up both for
    // displaying the data on the form again, in the event of an error
    // and sending the data to the database, in the event of no error
    dib = loadFormValue(dib);

    // types TEL and ED are not supported yet
    if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXT) ||
      rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXTAREA)) {
      dib = validateDisplayItemBeanText(v, dib, inputName);
    }
    else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.RADIO) ||
      rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECT)) {
      dib = validateDisplayItemBeanSingleCV(v, dib,inputName);
    }
    else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CHECKBOX) ||
      rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECTMULTI)) {
      dib = validateDisplayItemBeanMultipleCV(v, dib,inputName);
    }

    return dib;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#validateDisplayItemGroupBean()
   */
  protected List<DisplayItemGroupBean> validateDisplayItemGroupBean(DiscrepancyValidator v,
                                                                    DisplayItemGroupBean digb,List<DisplayItemGroupBean>digbs,List<DisplayItemGroupBean>formGroups){

    return formGroups;

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#loadDBValues()
   */
  protected boolean shouldLoadDBValues(DisplayItemBean dib) {
    return true;
  }

  /**
   * Current User may access a requested event CRF in the current user's studies
   *
   * @author ywang 10-18-2007
   */
  public void mayAccess() throws InsufficientPermissionException {
    FormProcessor fp = new FormProcessor(request);
    EventCRFDAO edao = new EventCRFDAO(sm.getDataSource());
    int eventCRFId = fp.getInt("ecId", true);

    if(eventCRFId > 0) {
      if(!entityIncluded(eventCRFId, ub.getName(), edao, sm.getDataSource())) {
        addPageMessage("Required " + resword.getString("event_CRF") + respage.getString("not_belong_to_studies"));
        throw new InsufficientPermissionException(Page.MENU,
          resexception.getString("entity_not_belong_studies"), "1");
      }
    }
  }

  private Date formatInterviewDate(Date datetoBeFormatted) {
    Date tmpDate = new Date();
    if(datetoBeFormatted == null) { return null; }
    String tmpStr = datetoBeFormatted.toString().replaceAll("-","/");
    try {
      tmpDate = new SimpleDateFormat("MM/dd/yyyy").parse(tmpStr);
    }  catch (java.text.ParseException pe) {
      return datetoBeFormatted;
    }
    return tmpDate;
  }

}
