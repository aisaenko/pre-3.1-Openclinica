/*
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 * 
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 * 
 * Created on Jul 7, 2005
 */
package org.akaza.openclinica.bean.extract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;

public class ExtractBean {
  public static final int SAS_FORMAT = 1;

  public static final int SPSS_FORMAT = 2;

  public static final int CSV_FORMAT = 3;

  public static final int PDF_FORMAT = 4;

  public static final int XLS_FORMAT = 5;

  public static final int TXT_FORMAT = 6;

  java.text.SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

  private int format = 1;

  private StudyBean parentStudy;

  private StudyBean study;

  private DatasetBean dataset;

  private DataSource ds;

  private Date dateCreated;

  // an array of StudyEventDefinitionBean objects
  private ArrayList studyEvents;

  private HashMap eventData;

  //an array of subjects
  private ArrayList subjects;

  // a hashmap indicating which subjects have already been added
  // key is subjectId as Integer, value is Boolean.TRUE
  private HashMap subjectsAdded;

  // keys are studySubjectId-studyEventDefinitionId-sampleOrdinal-crfId-ItemID
  // strings
  // values are the corresponding values in the item_data table
  private HashMap data;

  // keys are studyEventDefinitionId Integer
  // values are the maximum sample ordinal for that sed
  private HashMap maxOrdinals;

  // keys are itemId Integer
  // values are Boolean.TRUE
  // if an item has its id in the keySet for this HashMap,
  // that means the user has chosen to display this item in the report
  private HashMap selectedItems;

  private HashMap selectedSEDs;

  private HashMap selectedSEDCRFs;
  
  private HashMap eventDescriptions;//for spss only

  private ArrayList eventHeaders; // for displaying dataset in HTML view,event

  // header

  private ArrayList itemNames;// for displaying dataset in HTML view,item header

  private ArrayList rowValues; //for displaying dataset in html view
  

  private StudySubjectBean currentSubject;

  private int subjIndex = -1;  
  
  private CRFBean currentCRF;

  private int crfIndex = -1;
  

  private StudyEventDefinitionBean currentDef;

  private int sedIndex = -1;
  
  private ItemBean currentItem;
  
  private int itemIndex = -1;
  
  public ExtractBean(DataSource ds) {
    this.ds = ds;
    study = new StudyBean();
    parentStudy = new StudyBean();
    studyEvents = new ArrayList();

    data = new HashMap();
    maxOrdinals = new HashMap();
    subjects = new ArrayList();
    subjectsAdded = new HashMap();
    selectedItems = new HashMap();
    selectedSEDs = new HashMap();
    selectedSEDCRFs = new HashMap();
    itemNames = new ArrayList();
    rowValues = new ArrayList();
    eventHeaders = new ArrayList();
    eventDescriptions= new HashMap();
  } 
  

  /**
   * @return Returns the eventDescriptions.
   */
  public HashMap getEventDescriptions() {
    return eventDescriptions;
  }
  /**
   * @param eventDescriptions The eventDescriptions to set.
   */
  public void setEventDescriptions(HashMap eventDescriptions) {
    this.eventDescriptions = eventDescriptions;
  }
  public void computeReportMetadata(ReportBean answer) {
    /////////////////////
    //                 //
    //      HEADER //
    //                 //
    /////////////////////
    answer.nextCell("Database Export Header Metadata");
    answer.nextRow();

    answer.nextCell("Dataset Name");
    answer.nextCell(dataset.getName());
    answer.nextRow();

    answer.nextCell("Date");

    answer.nextCell(sdf.format(new Date(System.currentTimeMillis())));
    answer.nextRow();

    answer.nextCell("Protocol ID");
    answer.nextCell(getParentProtocolId());
    answer.nextRow();

    answer.nextCell("Study Name");
    answer.nextCell(getParentStudyName());
    answer.nextRow();

    String siteName = getSiteName();
    if (!siteName.equals("")) {
      answer.nextCell("Site Name");
      answer.nextCell(siteName);
      answer.nextRow();
    }

    answer.nextCell("Subjects");
    answer.nextCell(Integer.toString(getNumSubjects()));
    answer.nextRow();

    int numSEDs = getNumSEDs();
    answer.nextCell("Study Event Definitions");
    answer.nextCell(String.valueOf(numSEDs));
    answer.nextRow();

    for (int i = 1; i <= numSEDs; i++) {
      String repeating = getSEDIsRepeating(i) ? " (Repeating) " : "";
      answer.nextCell("Study Event Definition " + i + repeating);
      answer.nextCell(getSEDName(i));
      answer.nextRow();

      int numSEDCRFs = getSEDNumCRFs(i);
      for (int j = 1; j <= numSEDCRFs; j++) {
        answer.nextCell("CRF ");
        answer.nextCell(getSEDCRFName(i, j));
        answer.nextCell(getSEDCRFCode(i, j));
        answer.nextRow();
      }
    }    
  }
  
  public void computeReportData(ReportBean answer) {
    answer.nextCell("Subject Event Item Values (Item-CRF-Ordinal)");
    answer.nextRow();

    /////////////////////
    //                 //
    //     COLUMNS //
    //                 //
    /////////////////////
    answer.nextCell("SubjID");
    answer.nextCell("ProtocolID");

    // subject column labels
    if (dataset.isShowSubjectDob()) {
      if (study.getStudyParameterConfig().getCollectDob().equals("2")) {
        answer.nextCell("YOB");
      } else {
        answer.nextCell("DOB");
      }
    }
    if (dataset.isShowSubjectGender()) {
      answer.nextCell("Gender");
    }

    // sed column labels
    int numSEDs = getNumSEDs();
    for (int i = 1; i <= numSEDs; i++) {
      int numSamples = getSEDNumSamples(i);

      for (int j = 1; j <= numSamples; j++) {
        if (dataset.isShowEventLocation()) {
          String location = getColumnLabel(i, j, "Location", numSamples);
          String description = 
            getColumnDescription(i, j, "Location For ", currentDef.getName(),numSamples);
          answer.nextCell(location);
          eventHeaders.add(location);
          eventDescriptions.put(location, description);
        }
        if (dataset.isShowEventStart()) {
          String start = getColumnLabel(i, j, "StartDate", numSamples);
          String description = 
            getColumnDescription(i, j, "Start Date For ", currentDef.getName(),numSamples);
          answer.nextCell(start);
          eventHeaders.add(start);
          eventDescriptions.put(start, description);
          

        }
        if (dataset.isShowEventEnd()) {
          String end = getColumnLabel(i, j, "EndDate", numSamples);
          String description = 
            getColumnDescription(i, j, "End Date For ", currentDef.getName(),numSamples);
          answer.nextCell(end);
          eventHeaders.add(end);
          eventDescriptions.put(end, description);
        }
      }
    }

    // item-crf-ordinal column labels
//System.out.println("numseds: " + numSEDs);
    for (int i = 1; i <= numSEDs; i++) {
      int numSamples = getSEDNumSamples(i);
//System.out.println("SED " + i + "; numsamples: " + numSamples);
      for (int j = 1; j <= numSamples; j++) {
        
        int numSEDCRFs = getSEDNumCRFs(i);
//System.out.println("SED " + i + "; sample " + j +": numsedcrfs: " + numSEDCRFs);
        for (int k = 1; k <= numSEDCRFs; k++) {
          
          int numItems = getNumItems(i, k);
//System.out.println("*SED " + i + "; sample " + j +": sedcrf " + k + "; numitems: " + numItems);
          for (int l = 1; l <= numItems; l++) {
            answer.nextCell(getColumnItemLabel(i, j, k, l, numSamples));
            DisplayItemHeaderBean dih = new DisplayItemHeaderBean();
            dih.setItemHeaderName(getColumnItemLabel(i, j, k, l, numSamples));
            ItemBean item = new ItemBean();
            dih.setItem(currentItem);
            itemNames.add(dih);
          }
        }
      }
    }

    answer.nextRow();

    //////////////////
    //              //
    //     DATA //
    //              //
    //////////////////
    for (int h = 1; h <= getNumSubjects(); h++) {
      DisplayItemDataBean didb = new DisplayItemDataBean();
      String label = getSubjectStudyLabel(h);
      answer.nextCell(label);
      didb.setSubjectName(label);

      String protocolId = getParentProtocolId();
      answer.nextCell(protocolId);
      didb.setStudyLabel(protocolId);

      // subject column labels
      if (dataset.isShowSubjectDob()) {
        if (study.getStudyParameterConfig().getCollectDob().equals("2")) {
          String yob = getSubjectYearOfBirth(h);
          answer.nextCell(yob);
          didb.setSubjectDob(yob);
        } else {          
          String dob = getSubjectDateOfBirth(h);
          answer.nextCell(dob);
          didb.setSubjectDob(dob);
        }
      }
      if (dataset.isShowSubjectGender()) {
        String gender = getSubjectGender(h);
        answer.nextCell(gender);
        didb.setSubjectGender(gender);
      }

      // sed column values
      for (int i = 1; i <= numSEDs; i++) {
        int numSamples = getSEDNumSamples(i);

        for (int j = 1; j <= numSamples; j++) {
          if (dataset.isShowEventLocation()) {
            String location = getEventLocation(h, i, j);
            answer.nextCell(location);
            didb.getEventValues().add(location);

          }
          if (dataset.isShowEventStart()) {
            String start = getEventStart(h, i, j);
            answer.nextCell(start);
            didb.getEventValues().add(start);
          }
          if (dataset.isShowEventEnd()) {
            String end = getEventEnd(h, i, j);
            answer.nextCell(end);
            didb.getEventValues().add(end);
          }

        }
      }

      // item-crf-ordinal column labels
      for (int i = 1; i <= numSEDs; i++) {
        int numSamples = getSEDNumSamples(i);

        for (int j = 1; j <= numSamples; j++) {

          int numSEDCRFs = getSEDNumCRFs(i);
          for (int k = 1; k <= numSEDCRFs; k++) {

            int numItems = getNumItems(i, k);
            for (int l = 1; l <= numItems; l++) {
              String data = getDataByIndex(h, i, j, k, l);
              answer.nextCell(data);
              didb.getItemValues().add(data);
            }
          }
        }
      }
      rowValues.add(didb);
      answer.nextRow();
    }  	
  }
  
  public void computeReport(ReportBean answer) {
    computeReportMetadata(answer);
    answer.closeMetadata();
    computeReportData(answer);
  }

  private HashMap displayed = new HashMap();

  // keys are Strings returned by getColumnKeys, values are ArrayLists of
  // ItemBean objects in order of their display in the SED/CRF
  private HashMap sedCrfColumns = new HashMap();
  private HashMap sedCrfItemFormMetadataBeans = new HashMap();

  /**
   * Implements the Column algorithm in "Dataset Export Algorithms" Must be
   * called after DatasetDAO.getDatasetData();
   */
  public void getMetadata() {
    StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(ds);
    CRFDAO cdao = new CRFDAO(ds);
    CRFVersionDAO cvdao = new CRFVersionDAO(ds);
    ItemDAO idao = new ItemDAO(ds);
    ItemFormMetadataDAO ifmDAO = new ItemFormMetadataDAO(this.ds);
    sedCrfColumns = new HashMap();
    displayed = new HashMap();
    sedCrfItemFormMetadataBeans = new HashMap();
    
    studyEvents = seddao.findAllByStudy(study);
    ArrayList finalStudyEvents = new ArrayList();
    for (int i = 0; i < studyEvents.size(); i++) {
      StudyEventDefinitionBean sed = (StudyEventDefinitionBean) studyEvents.get(i);

      if (!selectedSED(sed)) {
        continue;
      }
//System.out.println("selected! " + sed.getId() + "; name: " + sed.getName()); 
      ArrayList CRFs = (ArrayList) cdao.findAllActiveByDefinition(sed);
      ArrayList CRFsDisplayedInThisSED = new ArrayList();
      
      for (int j = 0; j < CRFs.size(); j++) {
        CRFBean cb = (CRFBean) CRFs.get(j);

        if (!selectedSEDCRF(sed, cb)) {
//System.out.println("sedcrf not selected! " + sed.getId() + "; name: " + sed.getName() + "; crfid: " + cb.getId() + "; crfname: " + cb.getName());
          continue;
        } else {
//System.out.println("sedcrf selected! " + sed.getId() + "; name: " + sed.getName() + "; crfid: " + cb.getId() + "; crfname: " + cb.getName());

        CRFsDisplayedInThisSED.add(cb);

        ArrayList CRFVersions = cvdao.findAllByCRFId(cb.getId());
        for (int k = 0; k < CRFVersions.size(); k++) {
          CRFVersionBean cvb = (CRFVersionBean) CRFVersions.get(k);

          ArrayList Items = idao.findAllItemsByVersionId(cvb.getId());
          //sort by ordinal/name
          Collections.sort(Items);
          for (int l = 0; l < Items.size(); l++) {
            ItemBean ib = (ItemBean) Items.get(l);
            if (selected(ib) && !getDisplayed(sed, cb, ib)) {
             // System.out.println("selected " + ib.getId() + "; name: " + ib.getName() + "? " + selected(ib));
            	ItemFormMetadataBean ifmb = ifmDAO.findByItemIdAndCRFVersionId(ib.getId(), cvb.getId());
            	addColumn(sed, cb, ib);
            	addItemFormMetadataBeans(sed, cb, ifmb);
            	markDisplayed(sed, cb, ib);
            }
          }
        }
      }//else
      }//for 

      sed.setCrfs(CRFsDisplayedInThisSED);
      finalStudyEvents.add(sed); // make the setCrfs call "stick"
    }
    this.studyEvents = finalStudyEvents;
  }

  protected boolean selected(ItemBean ib) {
    return selectedItems.containsKey(new Integer(ib.getId()));
  }

  protected boolean selectedSEDCRF(StudyEventDefinitionBean sed, CRFBean cb) {
    return selectedSEDCRFs.containsKey(sed.getId() + "_" + cb.getId());
  }

  protected boolean selectedSED(StudyEventDefinitionBean sed) {
    return selectedSEDs.containsKey(new Integer(sed.getId()));
  }

  private void markDisplayed(StudyEventDefinitionBean sed, CRFBean cb, ItemBean ib) {
    displayed.put(getDisplayedKey(sed, cb, ib), Boolean.TRUE);
  }

  private boolean getDisplayed(StudyEventDefinitionBean sed, CRFBean cb, ItemBean ib) {
    return displayed.containsKey(getDisplayedKey(sed, cb, ib));
  }

  private void addColumn(StudyEventDefinitionBean sed, CRFBean cb, ItemBean ib) {
    String key = getColumnsKey(sed, cb);
    ArrayList columns = (ArrayList) sedCrfColumns.get(key);

    if (columns == null) {
      columns = new ArrayList();
    }

    columns.add(ib);
    sedCrfColumns.put(key, columns);
  }

  
  public ArrayList getColumns(StudyEventDefinitionBean sed, CRFBean cb) {
    String key = getColumnsKey(sed, cb);   
    ArrayList columns = (ArrayList) sedCrfColumns.get(key);

    if (columns == null) {
      columns = new ArrayList();
    }

    return columns;
  }
  
  private void addItemFormMetadataBeans(StudyEventDefinitionBean sed, CRFBean cb, ItemFormMetadataBean ifmb) {
	    String key = sed.getId() + "_" + cb.getId();
	    ArrayList columns = (ArrayList) sedCrfItemFormMetadataBeans.get(key);

	    if (columns == null) {
	      columns = new ArrayList();
	    }

	    columns.add(ifmb);
	    sedCrfItemFormMetadataBeans.put(key, columns);
  }

  public ArrayList getItemFormMetadataBeans(StudyEventDefinitionBean sed, CRFBean cb) {
    String key = sed.getId() + "_" + cb.getId();   
    ArrayList columns = (ArrayList) sedCrfItemFormMetadataBeans.get(key);

    if (columns == null) {
      columns = new ArrayList();
    }

    return columns;
  }

  public void addStudySubjectData(Integer studySubjectId, String studySubjectLabel,
      Date dateOfBirth, String gender) {
    if (!subjectsAdded.containsKey(studySubjectId)) {
      StudySubjectBean sub = new StudySubjectBean();
      sub.setId(studySubjectId.intValue());
      sub.setLabel(studySubjectLabel);
      
      sub.setDateOfBirth(dateOfBirth);
      //System.out.println("gender:" + gender);
      if (gender != null && gender.length()>0){
        sub.setGender(gender.charAt(0));
      } else {
        sub.setGender(' ');
      }
      subjects.add(sub);
      subjectsAdded.put(studySubjectId, Boolean.TRUE);
    }
  }

  public void addStudyEventData(Integer studySubjectId, String studyEventDefinitionName, 
      Integer studyEventDefinitionId,Integer sampleOrdinal, String studyEventLocation, 
      Date studyEventStart, Date studyEventEnd) {
    if ((studySubjectId == null) || (studyEventDefinitionId == null) || (sampleOrdinal == null)
        || (studyEventLocation == null) || (studyEventStart == null) || (studyEventEnd == null)) {
      return;
    }

    if ((studyEventDefinitionId.intValue() <= 0) || (studySubjectId.intValue() <= 0)
        || (sampleOrdinal.intValue() <= 0)) {
      return;
    }
    //System.out.println("here.....OK........111111");
    StudyEventBean event = new StudyEventBean();
    event.setName(studyEventDefinitionName);
    event.setDateStarted(studyEventStart);
    event.setDateEnded(studyEventEnd);
    event.setLocation(studyEventLocation);
    event.setSampleOrdinal(sampleOrdinal.intValue());
    event.setStudyEventDefinitionId(studyEventDefinitionId.intValue());
    event.setStudySubjectId(studySubjectId.intValue());
    String key = getStudyEventDataKey(studySubjectId.intValue(), studyEventDefinitionId.intValue(),
        sampleOrdinal.intValue());
    //System.out.println("here.....OK........2222222222");
    if (eventData == null) {
      eventData = new HashMap();
    }
    eventData.put(key, event);

  }

  public void addItemData(Integer studySubjectId, Integer studyEventDefinitionId,
      Integer sampleOrdinal, Integer crfId, Integer itemId, String itemValue) {
    if ((studyEventDefinitionId == null) || (studySubjectId == null) || (crfId == null)
        || (itemId == null) || (sampleOrdinal == null) || (itemValue == null)) {
      return;
    }

    if ((studyEventDefinitionId.intValue() <= 0) || (studySubjectId.intValue() <= 0)
        || (crfId.intValue() <= 0) || (itemId.intValue() <= 0) || (sampleOrdinal.intValue() <= 0)) {
      return;
    }

    String key = getDataKey(studySubjectId.intValue(), studyEventDefinitionId.intValue(),
        sampleOrdinal.intValue(), crfId.intValue(), itemId.intValue());
    //System.out.println("data key" + key);
    data.put(key, itemValue);

    int maxOrdinal = getMaxOrdinal(studyEventDefinitionId.intValue());
    if (maxOrdinal < sampleOrdinal.intValue()) {
      setMaxOrdinal(studyEventDefinitionId.intValue(), sampleOrdinal.intValue());
    }
//System.out.println("adding to selecteditems: " + itemId);
    selectedItems.put(itemId, Boolean.TRUE);
//System.out.println("adding to selected sed crf: " + studyEventDefinitionId + "/" + crfId);
    selectedSEDCRFs.put(studyEventDefinitionId.intValue() + "_" + crfId.intValue(), Boolean.TRUE);
//System.out.println("adding to selectedseds: " + studyEventDefinitionId);
    selectedSEDs.put(studyEventDefinitionId, Boolean.TRUE);

    return;
  }

  protected String getDataByIndex(int subjectInd, int sedInd, int sampleOrdinal, int crfInd,
      int itemInd) {
    syncSubjectIndex(subjectInd);
    syncItemIndex(sedInd, crfInd, itemInd);
    String key = getDataKey(currentSubject.getId(), currentDef.getId(), sampleOrdinal, currentCRF
        .getId(), currentItem.getId());
    String itemValue = (String) data.get(key);

    if (itemValue == null) {
      itemValue = "";
    }

    return itemValue;
  }

  ////////////////////////////
  //  Max Ordinals Section //
  ////////////////////////////

  private Integer getMaxOrdinalsKey(int studySubjectId) {
    return new Integer(studySubjectId);
  }

  private int getMaxOrdinal(int studyEventDefinitionId) {
    Integer key = getMaxOrdinalsKey(studyEventDefinitionId);
    try {
      if (maxOrdinals.containsKey(key)) {
        Integer maxOrdinal = (Integer) maxOrdinals.get(key);
        if (maxOrdinal != null) {
          return maxOrdinal.intValue();
        }
      }
    } catch (Exception e) {
    }

    return 0;
  }

  private void setMaxOrdinal(int studyEventDefinitionId, int sampleOrdinal) {
    Integer key = getMaxOrdinalsKey(studyEventDefinitionId);
    maxOrdinals.put(key, new Integer(sampleOrdinal));
  }

  ///////////////////////////////////////////////
  //                                           //
  //    RETRIEVE METADATA AND DATA SECTION //
  //                                           //
  ///////////////////////////////////////////////

  public String getParentProtocolId() {
    if (!parentStudy.isActive()) {
      return study.getIdentifier();
    } else {
      return parentStudy.getIdentifier();
    }
  }

  public String getParentStudyName() {
    if (!parentStudy.isActive()) {
      return study.getName();
    } else {
      return parentStudy.getName();
    }
  }
  
  public String getParentStudySummary() {
	  if (!parentStudy.isActive()) {
	      return study.getSummary();
	    } else {
	      return parentStudy.getSummary();
	    }
	  }

  private String getSiteName() {
    if (parentStudy.isActive()) {
      return study.getName();
    } else {
      return "";
    }
  }

  public int getNumSubjects() {
    if (subjects != null) {
      return subjects.size();
    } else {
      return 0;
    }
  }

  protected int getNumSEDs() {
    return studyEvents.size();
  }

  private void syncSubjectIndex(int ind) {
    if (subjIndex != ind) {
      currentSubject = (StudySubjectBean) subjects.get(ind - 1);
      subjIndex = ind;
    }
  }

  private String getSubjectStudyLabel(int h) {
    syncSubjectIndex(h);
    return currentSubject.getLabel();
  }

  private String getSubjectDateOfBirth(int h) {
    syncSubjectIndex(h);
    Date dob = currentSubject.getDateOfBirth();
    return dob == null ? "" : sdf.format(dob);
  }

  private String getSubjectYearOfBirth(int h) {
    syncSubjectIndex(h);
    Date dob = currentSubject.getDateOfBirth();

    if (dob == null) {
      return "";
    }

    Calendar cal = Calendar.getInstance();
    cal.setTime(dob);
    int year = cal.get(Calendar.YEAR);

    return (year + "");
  }

  private String getSubjectGender(int h) {
    syncSubjectIndex(h);
    return String.valueOf(currentSubject.getGender());
  }


  private void syncSEDIndex(int ind) {
    if (sedIndex != ind) {
      currentDef = (StudyEventDefinitionBean) studyEvents.get(ind - 1);
      sedIndex = ind;     
    }
    //System.out.println("sedIndex : " + sedIndex + " currentDef:" + currentDef.getName());
  }

  private boolean getSEDIsRepeating(int ind) {
    syncSEDIndex(ind);
    return currentDef.isRepeating();
  }

  private String getSEDName(int ind) {
    syncSEDIndex(ind);
    return currentDef.getName();
  }

  protected int getSEDNumCRFs(int ind) {
    syncSEDIndex(ind);
    return currentDef.getCrfs().size();
  }

  

  private void syncCRFIndex(int sedInd, int crfInd) {
    syncSEDIndex(sedInd);
   
      currentCRF = (CRFBean) currentDef.getCrfs().get(crfInd - 1);
      crfIndex = crfInd;
     
    //System.out.println("crfIndex" + crfIndex + " currentCRF:" + currentCRF.getName());
  }

  private String getSEDCRFName(int sedInd, int crfInd) {
    syncCRFIndex(sedInd, crfInd);
    return currentCRF.getName();
  }

  protected int getNumItems(int sedInd, int crfInd) {
    syncCRFIndex(sedInd, crfInd);   
    ArrayList items = getColumns(currentDef, currentCRF);  
    return items.size();
  }

  

  private void syncItemIndex(int sedInd, int crfInd, int itemInd) {
    syncCRFIndex(sedInd, crfInd);
    
       
      ArrayList items = getColumns(currentDef, currentCRF);   
      currentItem = (ItemBean) items.get(itemInd - 1);
      itemIndex = itemInd;
   
  }

  private String getItemName(int sedInd, int crfInd, int itemInd) {
    syncItemIndex(sedInd, crfInd, itemInd);
    return currentItem.getName();
  }

  ////////////////////
  //                //
  //  HASHMAP KEYS //
  //                //
  ////////////////////

  private String getDataKey(int studySubjectId, int studyEventDefinitionId, int sampleOrdinal,
      int crfId, int itemId) {
    return studySubjectId + "_" + studyEventDefinitionId + "_" + sampleOrdinal + "_" + crfId + "_"
        + itemId;
  }

  private String getDisplayedKey(StudyEventDefinitionBean sed, CRFBean cb, ItemBean ib) {
    return sed.getId() + "_" + cb.getId() + "_" + ib.getId();
  }

  private String getColumnsKey(StudyEventDefinitionBean sed, CRFBean cb) {
    return sed.getId() + "_" + cb.getId();
  }

  private String getStudyEventDataKey(int studySubjectId, int studyEventDefinitionId,
      int sampleOrdinal) {
    String key = studySubjectId + "_" + studyEventDefinitionId + "_" + sampleOrdinal;
    return key;
  }

  ///////////////////////////////
  //                           //
  //  CODES AND COLUMN LABELS //
  //                           //
  ///////////////////////////////

  public static String getSEDCode(int sedInd) {
    sedInd--;
    if (sedInd > 26) {
      int digit1 = sedInd / 26;
      int digit2 = sedInd % 26;

      char letter1 = (char) ('A' + digit1);
      char letter2 = (char) ('A' + digit2);

      return "" + letter1 + letter2;
    } else {
      char letter = (char) ('A' + sedInd);

      return "" + letter;
    }
  }

  public static String getSEDCRFCode(int sedInd, int crfInd) {
    return getSEDCode(sedInd) + crfInd;
  }

  private String getSampleCode(int ordinal, int numSamples) {
    return numSamples > 1 ? "_" + ordinal : "";
  }

  private String getColumnLabel(int sedInd, int ordinal, String labelType, int numSamples) {
    return labelType + "_" + getSEDCode(sedInd) + getSampleCode(ordinal, numSamples);
  }
  
  private String getColumnDescription(int sedInd, int ordinal, String labelType, String defName, int numSamples) {
    return labelType + defName + "(" + getSEDCode(sedInd) + getSampleCode(ordinal, numSamples) + ")";
  }

  private String getColumnItemLabel(int sedInd, int ordinal, int crfInd, int itemInd, int numSamples) {
    return getItemName(sedInd, crfInd, itemInd) + "_" + getSEDCRFCode(sedInd, crfInd)
        + getSampleCode(ordinal, numSamples);
  }

  //////////////////////////////
  //                          //
  //   GETTERS AND SETTERS //
  //                          //
  //////////////////////////////

  /**
   * @return Returns the study.
   */
  public StudyBean getStudy() {
    return study;
  }

  /**
   * @param study
   *          The study to set.
   */
  public void setStudy(StudyBean study) {
    this.study = study;
  }

  /**
   * @return Returns the parentStudy.
   */
  public StudyBean getParentStudy() {
    return parentStudy;
  }

  /**
   * @param parentStudy
   *          The parentStudy to set.
   */
  public void setParentStudy(StudyBean parentStudy) {
    this.parentStudy = parentStudy;
  }

  /**
   * @return Returns the format.
   */
  public int getFormat() {
    return format;
  }

  /**
   * @param format
   *          The format to set.
   */
  public void setFormat(int format) {
    this.format = format;
  }

  /**
   * @return Returns the dataset.
   */
  public DatasetBean getDataset() {
    return dataset;
  }

  /**
   * @param dataset
   *          The dataset to set.
   */
  public void setDataset(DatasetBean dataset) {
    this.dataset = dataset;
  }

  /**
   * @return Returns the studyEvents.
   */
  public ArrayList getStudyEvents() {
    return studyEvents;
  }

  /**
   * The maximum over all ordinals over all study events for the provided SED.
   * 
   * @param i
   *          An index into the studyEvents list for the SED whose max ordinal
   *          we want.
   * @return The maximum number of samples for the i-th SED.
   */
  public int getSEDNumSamples(int i) {
    syncSEDIndex(i);
    int sedId = currentDef.getId();
    return getMaxOrdinal(sedId);
  }

  /**
   * Get the event correspodning to the provided study subject, SED and sample
   * ordinal.
   * 
   * @param h
   *          An index into the array of subjects.
   * @param i
   *          An index into the array of SEDs.
   * @param j
   *          The sample ordinal.
   * @return The event correspodning to the provided study subject, SED and
   *         sample ordinal.
   */
  private StudyEventBean getEvent(int h, int i, int j) {
    syncSubjectIndex(h);
    syncSEDIndex(i);

    String key = getStudyEventDataKey(currentSubject.getId(), currentDef.getId(), j);
    StudyEventBean seb = (StudyEventBean) eventData.get(key);

    if (seb == null) {
      return new StudyEventBean();
    } else {
      return seb;
    }
  }

  private String getEventLocation(int h, int i, int j) {
    return getEvent(h, i, j).getLocation();
  }  
  

  private String getEventStart(int h, int i, int j) {
    StudyEventBean seb = getEvent(h, i, j);
    Date start = seb.getDateStarted();
    return (start != null ? sdf.format(start) : "");
  }

  private String getEventEnd(int h, int i, int j) {
    StudyEventBean seb = getEvent(h, i, j);
    Date end = seb.getDateEnded();
    return (end != null ? sdf.format(end) : "");
  }
  
  protected ArrayList getSubjects() {
    return this.subjects;
  }

  //  public String getItemValue(int h, int i, int j, int k, int l) {
  //    StudySubjectBean sub = (StudySubjectBean) subjects.get(h);
  //    int studyEventDefinitionId = ((StudyEventDefinitionBean)
  // studyEvents.get(i)).getId();
  //    ArrayList events = (ArrayList) eventData.get(new
  // Integer(studyEventDefinitionId));
  //    int sampleOrdinal = ((StudyEventBean) events.get(j)).getSampleOrdinal();
  //    ArrayList items = getColumns(currentDef, currentCRF);
  //
  //    String key = getDataKey(sub.getId(), studyEventDefinitionId, sampleOrdinal,
  // currentCRF.getId(),
  //        ((ItemBean) items.get(l)).getId());
  //    return (String) data.get(key);
  //
  //  }

  /**
   * @return Returns the dateCreated.
   */
  public Date getDateCreated() {
    return dateCreated;
  }

  /**
   * @param dateCreated
   *          The dateCreated to set.
   */
  public void setDateCreated(Date dateCreated) {
    this.dateCreated = dateCreated;
  }

  /**
   * @return Returns the itemNames.
   */
  public ArrayList getItemNames() {
    return itemNames;
  }

  /**
   * @param itemNames
   *          The itemNames to set.
   */
  public void setItemNames(ArrayList itemNames) {
    this.itemNames = itemNames;
  }

  /**
   * @return Returns the rowValues.
   */
  public ArrayList getRowValues() {
    return rowValues;
  }

  /**
   * @param rowValues
   *          The rowValues to set.
   */
  public void setRowValues(ArrayList rowValues) {
    this.rowValues = rowValues;
  }

  /**
   * @return Returns the eventHeaders.
   */
  public ArrayList getEventHeaders() {
    return eventHeaders;
  }

  /**
   * @param eventHeaders
   *          The eventHeaders to set.
   */
  public void setEventHeaders(ArrayList eventHeaders) {
    this.eventHeaders = eventHeaders;
  }

 
}