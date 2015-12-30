/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import java.util.*;
import java.text.NumberFormat;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.*;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.control.core.Utils;
import org.akaza.openclinica.control.submit.DataEntryServlet;
import org.akaza.openclinica.core.form.DiscrepancyValidator;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.form.FormBeanUtil;

/**
 * @author Bruce W. Perry
 *
 * Preview a CRF version section data entry.
 * This class is based almost entirely on ViewSectionDataEntryServlet;
 * Except that it's designed to provide a preview of a crf before
 * the crfversion is inserted into the database.
 */
public class ViewSectionDataEntryPreview extends DataEntryServlet {
  public static String SECTION_TITLE = "section_title";
  public static String SECTION_LABEL = "section_label";
  public static String SECTION_SUBTITLE = "subtitle";
   public static String INSTRUCTIONS = "instructions";
  /**
   * Checks whether the user has the correct privilege. This is from ViewSectionDataEntryServlet.
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }
    if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
      || currentRole.getRole().equals(Role.COORDINATOR)
      || currentRole.getRole().equals(Role.INVESTIGATOR)
      || currentRole.getRole().equals(Role.RESEARCHASSISTANT)) {
      return;
    }

    addPageMessage("You don't have correct privilege in your current active study. "
      + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.MENU_SERVLET, "not director", "1");

  }

  public void processRequest() throws Exception {
    FormProcessor fp = new FormProcessor(request);
    //These numbers will be zero if the
    //params are not present in the URL
    int crfid =  fp.getInt("crfId");
    int tabNum =  fp.getInt("tabId");

    request.setAttribute("crfId", crfid);
    String crfName ="";
    String verNumber = "";
    //All the data on the uploaded Excel file
    //see org.akaza.openclinica.control.admin.SpreadsheetPreview
    //createCrfMetaObject() method
    Map<String,Map> crfMap = (Map) session.getAttribute("preview_crf");
    if(crfMap == null)  {
      //addPageMessage
      String msg = "The Preview data has timed out and expired. "+
      "You may start the CRF version creation process again, if this is the task that you are focusing on.";
      this.addPageMessage(msg);
      logger.info("The session attribute \"preview_crf\" has expired or gone out of scope in: "+
        this.getClass().getName());
      this.forwardPage(Page.CRF_LIST_SERVLET );
    }

    Map<String,String> crfIdnameInfo = null;
    if(crfMap != null){
      crfIdnameInfo = crfMap.get("crf_info");
    }
    //Get the CRF name and version String
    if(crfIdnameInfo != null){
      Map.Entry mapEnt = null;
      for(Iterator iter = crfIdnameInfo.entrySet().iterator();iter.hasNext();) {
        mapEnt = (Map.Entry) iter.next();
        if(((String)mapEnt.getKey()).equalsIgnoreCase("crf_name")){
          crfName = (String) mapEnt.getValue();
        }
        if(((String)mapEnt.getKey()).equalsIgnoreCase("version")){
          verNumber = (String) mapEnt.getValue();
        }
      }
    }


    //Set up the beans that DisplaySectionBean and the preview
    //depend on
    EventCRFBean  ebean = new  EventCRFBean();
    CRFVersionBean crfverBean = new CRFVersionBean();
    crfverBean.setName(verNumber);
    CRFBean crfbean = new CRFBean();
    crfbean.setId(crfid);
    crfbean.setName(crfName);
    ebean.setCrf(crfbean);

    //This happens in ViewSectionDataEntry
    //It's an assumption that it has to happen here as well
    super.ecb = ebean;

    //All the groups data, if it's present in the CRF
    Map<Integer,Map<String,String>> groupsMap = null;
    if(crfMap != null) groupsMap = crfMap.get("groups");
    //Find out whether this CRF involves groups
    //At least one group is involved if the groups Map is not null or
    //empty, and the first group entry (there may be only one) has a
    //valid group label
    boolean hasGroups=false;
/*    if(groupsMap != null &&  (! groupsMap.isEmpty())  &&
      groupsMap.get(1).get("group_label").length() > 0)   hasGroups = true;*/

    //A SortedMap containing the row number as the key, and the
    //section headers/values (contained in a Map) as the value
    Map<Integer,Map<String,String>> sectionsMap = null;
    if(crfMap != null) sectionsMap = crfMap.get("sections");
    //The itemsMap contains the spreadsheet table items row number as a key,
    //followed by a map of the column names/values; it contains values for display
    //such as 'left item text'
    Map<Integer,Map<String,String>> itemsMap =  null;
    if(crfMap != null) itemsMap= crfMap.get("items");

    //Create a list of FormGroupBeans from Maps of groups,
    //items, and sections
    BeanFactory beanFactory = new BeanFactory();
    //FormBeanUtil formUtil = new FormBeanUtil();

    //Set up sections for the preview
    Map.Entry me = null;
    SectionBean secbean = null;
    ArrayList<SectionBean> allSectionBeans = new ArrayList<SectionBean>();
    String name_str = "";
    String pageNum="";
    Map secMap = null;
    //SpreadsheetPreviewNw returns doubles (via the
    // HSSFCell API, which parses Excel files)
    // as Strings (such as "1.0") for "1" in a spreadsheet cell,
    // so make sure only "1" is displayed using
    //this NumberFormat object
    NumberFormat numFormatter = NumberFormat.getInstance();
    numFormatter.setMaximumFractionDigits(0);
    if(sectionsMap != null)    {
      for(Iterator iter=sectionsMap.entrySet().iterator();
          iter.hasNext();){
        secbean = new SectionBean();
        me = (Map.Entry)  iter.next();
        secMap = (Map)me.getValue();
        name_str = (String) secMap.get("section_label");
        secbean.setName(name_str);
        secbean.setTitle((String) secMap.get("section_title"));
        secbean.setInstructions((String) secMap.get("instructions"));
        secbean.setSubtitle((String) secMap.get("subtitle"));
        pageNum = (String) secMap.get("page_number");
        //ensure pageNum is an actual number; the user is not required to
        //type a number in that Spreadsheet cell
        try{
          pageNum = numFormatter.format(Double.parseDouble(pageNum));
        } catch(NumberFormatException nfe)  {
          pageNum = "";
        }

        secbean.setPageNumberLabel(pageNum);
        //Sift through the items to see if their section label matches
        //the section's section_label column
        secbean.setNumItems(this.getNumberOfItems(itemsMap,
          secbean.getName()));
        allSectionBeans.add(secbean);
      }
    }
    DisplayTableOfContentsBean dtocBean = new
      DisplayTableOfContentsBean();
    //Methods should just take Lists, the interface, not
    //ArrayList only!
    dtocBean.setSections(allSectionBeans);

    request.setAttribute("toc",dtocBean);
    request.setAttribute("sectionNum", allSectionBeans.size()+ "");

    //Assuming that the super class' SectionBean sb variable must be initialized,
    //since it happens in ViewSectionDataEntryServlet. TODO: verify this
    super.sb = allSectionBeans.get(0);
    // This is the StudySubjectBean
    //Not sure if this is needed for a Preview, but leaving
    //it in for safety/consisitency reasons
    setupStudyBean();
    //Create a DisplaySectionBean for the SectionBean specified by the
    //tab number.
    tabNum = (tabNum == 0 ? 1: tabNum);
    String sectionTitle = getSectionColumnBySecNum(sectionsMap,tabNum,
      SECTION_TITLE);
    String secLabel = getSectionColumnBySecNum(sectionsMap,tabNum,
      SECTION_LABEL);
    String secSubtitle = getSectionColumnBySecNum(sectionsMap,tabNum,
      SECTION_SUBTITLE);
    String instructions = getSectionColumnBySecNum(sectionsMap,tabNum,
      INSTRUCTIONS);

    DisplaySectionBean displaySection = beanFactory.createDisplaySectionBean
      (itemsMap,
        sectionTitle,
        secLabel,
        secSubtitle,
        instructions,
        crfName);

      //
    // the variable hasGroups should only be true if the group appears in this section
    List<DisplayItemBean> disBeans = displaySection.getItems();
    ItemFormMetadataBean metaBean;
    String groupLabel;
    hasGroups=false;
    for(DisplayItemBean diBean : disBeans) {
       metaBean=diBean.getMetadata();
       groupLabel=metaBean.getGroupLabel();
       if(groupLabel != null &&  groupLabel.length() > 0){
           hasGroups=true;
           break;
       }

    }
    //Create groups associated with this section
    List<DisplayItemGroupBean> disFormGroupBeans = null;

    if(hasGroups)  {
      disFormGroupBeans = beanFactory.createGroupBeans(itemsMap,
        groupsMap,
        secLabel,
        crfName);
      displaySection.setDisplayFormGroups(disFormGroupBeans);
    }

    /*DisplaySectionBean displaySection = beanFactory.createDisplaySectionBean
      (itemsMap,
        sectionTitle,
        secLabel,
        secSubtitle,
        instructions,
        crfName);*/
    displaySection.setCrfVersion(crfverBean);
    displaySection.setCrf(crfbean);
    displaySection.setEventCRF(ebean);
    //Not sure if this is needed? The JSPs pull it out
    //as a request attribute
    SectionBean aSecBean = new SectionBean();

    request.setAttribute(BEAN_DISPLAY, displaySection);
    //TODO: verify these attributes, from the original servlet, are necessary
    request.setAttribute("sec", aSecBean);
    request.setAttribute("EventCRFBean", ebean);
    try{
      request.setAttribute("tabId", Integer.toString(tabNum));
    }   catch(NumberFormatException nfe) {
      request.setAttribute("tabId", new Integer("1"));
    }
    if(hasGroups){
      logger.info("has group, new_table is true");
      request.setAttribute("new_table",true);
    }
    //YW 07-23-2007 << for issue 0000937
    forwardPage(Page.CREATE_CRF_VERSION_CONFIRM);
    //YW >>

  }
  //Get a Section's title by its key number in the sectionsMap; i.e., what is the title
  //of the first section in the CRF?  
    private String getSectionColumnBySecNum(Map sectionsMap, int sectionNum,
                                            String sectionColumn){
    if(sectionsMap==null || (sectionColumn == null || sectionColumn.length() < 1)){
      return "";
    }
    Map innerMap =  (Map)sectionsMap.get(sectionNum);
    return (String)innerMap.get(sectionColumn);
  }

/*  private String getSectionTitleBySecNum(Map sectionsMap, int sectionNum){
    if(sectionsMap==null)  return "";
    Map innerMap =  (Map)sectionsMap.get(new Integer(sectionNum));
    return (String)innerMap.get("section_title");
  }
  private String getSectionLabelBySecNum(Map sectionsMap, int sectionNum){
    if(sectionsMap==null)  return "";
    Map innerMap =  (Map)sectionsMap.get(new Integer(sectionNum));
    return (String)innerMap.get("section_label");
  }
   private String getSectionSubtitleBySecNum(Map sectionsMap,
                                             int sectionNum){
    if(sectionsMap==null)  return "";
    Map innerMap =  (Map)sectionsMap.get(new Integer(sectionNum));
    return (String)innerMap.get("subtitle");
  }
  private String getInstructionsBySecNum(Map sectionsMap,
                                             int sectionNum){
    if(sectionsMap==null)  return "";
    Map innerMap =  (Map)sectionsMap.get(new Integer(sectionNum));
    return (String)innerMap.get("instructions");
  }*/
  //Determine the number of items associated with this section
  //by checking the page number value of each item, and comparing it
  //with the page number of the section
  private int getNumberOfItems(Map itemsMap,String sectionLabel) {
    if(itemsMap==null)  return 0;
    int itemCount=0;
    Map itemVals = null;
    Map.Entry me = null;
    Map.Entry me2 = null;
    String columnName = "";
    String val = "";
    for(Iterator iter = itemsMap.entrySet().iterator();iter.hasNext();) {
      me = (Map.Entry) iter.next();
      itemVals = (Map) me.getValue();
      //each Map member is a key/value pair representing an
      //item column/value
      for(Iterator iter2 = itemVals.entrySet().iterator();iter2.hasNext();) {
        me2 = (Map.Entry) iter2.next();
        columnName = (String) me2.getKey();
        val = (String) me2.getValue();
        if(columnName.equalsIgnoreCase("section_label")) {
          if(val.equalsIgnoreCase(sectionLabel))  itemCount++;
        }
      }
    }
    return itemCount;
  }

  private void setupStudyBean(){
    String age = "";
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
      //YW 11-16-2007 enrollment-date is used for calculating age
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
}