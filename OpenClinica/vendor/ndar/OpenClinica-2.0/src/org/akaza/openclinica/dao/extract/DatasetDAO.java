/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.dao.extract;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.extract.ExtractBean;
import org.akaza.openclinica.bean.extract.ExtractCRFVersionBean;
import org.akaza.openclinica.bean.extract.ExtractStudyEventDefinitionBean;
import org.akaza.openclinica.bean.extract.ExtractStudySubjectBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.submit.ResponseSetBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/** 
 * The data access object for datasets; also generates datasets based on their
 * query and criteria set; also generates the extract bean, which holds dataset
 * information.
 * 
 * @author thickerson
 * 
 *  
 */
public class DatasetDAO extends AuditableEntityDAO {

  //private DataSource ds;
  // private DAODigester digester;

  protected void setDigesterName() {
    digesterName = SQLFactory.getInstance().DAO_DATASET;
  }

  protected void setQueryNames() {
    getCurrentPKName = "getCurrentPK";
  }

  /**
   * Creates a DatasetDAO object, for use in the application only.
   * 
   * @param ds
   */
  public DatasetDAO(DataSource ds) {
    super(ds);
    this.setQueryNames();
  }

  /**
   * Creates a DatasetDAO object suitable for testing purposes only.
   * 
   * @param ds
   * @param digester
   */
  public DatasetDAO(DataSource ds, DAODigester digester) {
    super(ds);
    this.digester = digester;
    this.setQueryNames();
  }

  public void setTypesExpected() {
    this.unsetTypeExpected();
    this.setTypeExpected(1, TypeNames.INT);
    this.setTypeExpected(2, TypeNames.INT);
    this.setTypeExpected(3, TypeNames.INT);
    this.setTypeExpected(4, TypeNames.STRING);//name
    this.setTypeExpected(5, TypeNames.STRING);//desc
    this.setTypeExpected(6, TypeNames.STRING);//sql
    this.setTypeExpected(7, TypeNames.INT);//num runs
    this.setTypeExpected(8, TypeNames.DATE);//date start
    this.setTypeExpected(9, TypeNames.DATE);//date end
    this.setTypeExpected(10, TypeNames.DATE);//created
    this.setTypeExpected(11, TypeNames.DATE);//updated
    this.setTypeExpected(12, TypeNames.DATE);//last run
    this.setTypeExpected(13, TypeNames.INT);//owner id
    this.setTypeExpected(14, TypeNames.INT);//approver id
    this.setTypeExpected(15, TypeNames.INT);//update id
    this.setTypeExpected(16, TypeNames.BOOL);//show_event_location
    this.setTypeExpected(17, TypeNames.BOOL);//show_event_start
    this.setTypeExpected(18, TypeNames.BOOL);//show_event_end
    this.setTypeExpected(19, TypeNames.BOOL);//show_subject_dob
    this.setTypeExpected(20, TypeNames.BOOL);//show_subject_gender
    
  }

  public void setExtractTypesExpected() {
    this.unsetTypeExpected();
    this.setTypeExpected(1, TypeNames.INT);//subj id
    this.setTypeExpected(2, TypeNames.STRING);//subj identifier
    this.setTypeExpected(3, TypeNames.INT);//study id
    this.setTypeExpected(4, TypeNames.STRING);//study ident
    this.setTypeExpected(5, TypeNames.INT);//event def crf id
    this.setTypeExpected(6, TypeNames.INT);//crf id
    this.setTypeExpected(7, TypeNames.STRING);//crf label
    this.setTypeExpected(8, TypeNames.STRING);//crf name
    this.setTypeExpected(9, TypeNames.INT);//version id
    this.setTypeExpected(10, TypeNames.STRING);//version label
    this.setTypeExpected(11, TypeNames.STRING);//version name
    this.setTypeExpected(12, TypeNames.INT);//study event id
    this.setTypeExpected(13, TypeNames.INT);//event crf id
    this.setTypeExpected(14, TypeNames.INT);//item data id
    this.setTypeExpected(15, TypeNames.STRING);//value
    //oops added three more here
    this.setTypeExpected(16, TypeNames.STRING);//sed.name
    this.setTypeExpected(17, TypeNames.BOOL);//repeating
    this.setTypeExpected(18, TypeNames.INT);//sample ordinal
    this.setTypeExpected(19, TypeNames.INT);//item id
    this.setTypeExpected(20, TypeNames.STRING);//item name
    this.setTypeExpected(21, TypeNames.STRING);//item desc
    this.setTypeExpected(22, TypeNames.STRING);//item units
    this.setTypeExpected(23, TypeNames.DATE);//date created for item data
    this.setTypeExpected(24, TypeNames.INT);//study event definition id
    this.setTypeExpected(25, TypeNames.STRING);//option stings
    this.setTypeExpected(26, TypeNames.STRING);//option values
    this.setTypeExpected(27, TypeNames.INT);//response type id
    this.setTypeExpected(28, TypeNames.STRING);//gender
    this.setTypeExpected(29, TypeNames.DATE);//dob
    this.setTypeExpected(30, TypeNames.STRING);//location
    this.setTypeExpected(31, TypeNames.DATE);//date start
    this.setTypeExpected(32, TypeNames.DATE);//date end
  }

  public EntityBean update(EntityBean eb) {
    DatasetBean db = (DatasetBean) eb;
    HashMap variables = new HashMap();
    HashMap nullVars = new HashMap();
    variables.put(new Integer(1), new Integer(db.getStudyId()));
    variables.put(new Integer(2), new Integer(db.getStatus().getId()));
    variables.put(new Integer(3), db.getName());
    variables.put(new Integer(4), db.getDescription());
    variables.put(new Integer(5), db.getSQLStatement());
    variables.put(new Integer(6), db.getDateLastRun());
    variables.put(new Integer(7), new Integer(db.getNumRuns()));

    variables.put(new Integer(8), new Integer(db.getUpdaterId()));
    if (db.getApproverId() <= 0) {
      //nullVars.put(new Integer(9), null);
      //ABOVE IS WRONG; follow the example below:
    	nullVars.put(new Integer(9), new Integer(Types.NUMERIC));
        variables.put(new Integer(9), null);
    } else {
      variables.put(new Integer(9), new Integer(db.getApproverId()));
    }

    variables.put(new Integer(10), db.getDateStart());
    variables.put(new Integer(11), db.getDateEnd());
    variables.put(new Integer(12), new Integer(db.getId()));
    this.execute(digester.getQuery("update"), variables, nullVars);
    return eb;
  }

  
 
  public EntityBean create(EntityBean eb) {
    /*
     *   INSERT INTO DATASET (STUDY_ID, STATUS_ID, NAME, DESCRIPTION, 
     *		  SQL_STATEMENT, OWNER_ID, DATE_CREATED, DATE_LAST_RUN, 
  		  NUM_RUNS, DATE_START, DATE_END,
  		  SHOW_EVENT_LOCATION,SHOW_EVENT_START,SHOW_EVENT_END,
  		  SHOW_SUBJECT_DOB,SHOW_SUBJECT_GENDER) 
  		  VALUES (?,?,?,?,?,?,NOW(),NOW(),?,NOW(),'2005-11-15',
  		  ?,?,?,?,?)
     */
    DatasetBean db = (DatasetBean) eb;
    HashMap variables = new HashMap();
    HashMap nullVars = new HashMap();
    variables.put(new Integer(1), new Integer(db.getStudyId()));
    variables.put(new Integer(2), new Integer(db.getStatus().getId()));
    variables.put(new Integer(3), db.getName());
    variables.put(new Integer(4), db.getDescription());
    variables.put(new Integer(5), db.getSQLStatement());
    variables.put(new Integer(6), new Integer(db.getOwnerId()));
    variables.put(new Integer(7), new Integer(db.getNumRuns()));
    variables.put(new Integer(8), new Boolean(db.isShowEventLocation()));
    variables.put(new Integer(9), new Boolean(db.isShowEventStart()));
    variables.put(new Integer(10), new Boolean(db.isShowEventEnd()));
    variables.put(new Integer(11), new Boolean(db.isShowSubjectDob()));
    variables.put(new Integer(12), new Boolean(db.isShowSubjectGender()));    
   

    this.execute(digester.getQuery("create"), variables, nullVars);

    eb.setId(getCurrentPK());

    return eb;
  }

  public Object getEntityFromHashMap(HashMap hm) {
    DatasetBean eb = new DatasetBean();
    this.setEntityAuditInformation(eb, hm);
    eb.setDescription((String) hm.get("description"));
    eb.setStudyId(((Integer) hm.get("study_id")).intValue());
    eb.setName((String) hm.get("name"));
    eb.setId(((Integer) hm.get("dataset_id")).intValue());
    eb.setSQLStatement((String) hm.get("sql_statement"));
    eb.setNumRuns(((Integer) hm.get("num_runs")).intValue());
    eb.setDateStart((Date) hm.get("date_start"));
    eb.setDateEnd((Date) hm.get("date_end"));
    eb.setApproverId(((Integer) hm.get("approver_id")).intValue());
    eb.setDateLastRun((Date) hm.get("date_last_run"));
    eb.setShowEventEnd(((Boolean) hm.get("show_event_end")).booleanValue());
    eb.setShowEventStart(((Boolean) hm.get("show_event_start")).booleanValue());
    eb.setShowEventLocation(((Boolean) hm.get("show_event_location")).booleanValue());
    eb.setShowSubjectDob(((Boolean) hm.get("show_subject_dob")).booleanValue());
    eb.setShowSubjectGender(((Boolean) hm.get("show_subject_gender")).booleanValue());
    return eb;
  }

  public Collection findAll() {
    this.setTypesExpected();
    ArrayList alist = this.select(digester.getQuery("findAll"));
    ArrayList al = new ArrayList();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      DatasetBean eb = (DatasetBean) this.getEntityFromHashMap((HashMap) it.next());
      al.add(eb);
    }
    return al;
  }

  public Collection findTopFive(StudyBean currentStudy) {
  	int studyId = currentStudy.getId();
    this.setTypesExpected();
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(studyId));
    variables.put(new Integer(2), new Integer(studyId));
    ArrayList alist = this.select(digester.getQuery("findTopFive"),variables);
    ArrayList al = new ArrayList();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      DatasetBean eb = (DatasetBean) this.getEntityFromHashMap((HashMap) it.next());
      al.add(eb);
    }
    return al;
  }

  /**
   * find by owner id, reports a list of datasets by user account id.
   * 
   * @param ownerId studyId 
   */

  public Collection findByOwnerId(int ownerId, int studyId) {
  	//TODO add an findbyadminownerid?
    this.setTypesExpected();

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(studyId));
    variables.put(new Integer(2), new Integer(studyId));
    variables.put(new Integer(3), new Integer(ownerId));

    ArrayList alist = this.select(digester.getQuery("findByOwnerId"), variables);
    ArrayList al = new ArrayList();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      DatasetBean eb = (DatasetBean) this.getEntityFromHashMap((HashMap) it.next());
      al.add(eb);
    }
    return al;
  }

  public Collection findAll(String strOrderByColumn, boolean blnAscendingSort,
      String strSearchPhrase) {
    ArrayList al = new ArrayList();

    return al;
  }

  public EntityBean findByPK(int ID) {
    DatasetBean eb = new DatasetBean();
    this.setTypesExpected();

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(ID));

    String sql = digester.getQuery("findByPK");
    ArrayList alist = this.select(sql, variables);
    Iterator it = alist.iterator();

    if (it.hasNext()) {
      eb = (DatasetBean) this.getEntityFromHashMap((HashMap) it.next());
    } else {
    	logger.warning("found no object: "+sql+" "+ID);
    }
    return eb;
  }
  
  /**
   * 
   * @param name
   * @return
   */
  public EntityBean findByNameAndStudy(String name,StudyBean study) {
    DatasetBean eb = new DatasetBean();
    this.setTypesExpected();

    HashMap variables = new HashMap();
    variables.put(new Integer(1), name);
    variables.put(new Integer(2), new Integer(study.getId()));
    String sql = digester.getQuery("findByNameAndStudy");
    ArrayList alist = this.select(sql, variables);
    Iterator it = alist.iterator();

    if (it.hasNext()) {
      eb = (DatasetBean) this.getEntityFromHashMap((HashMap) it.next());
    } else {
    	logger.warning("found no object: "+sql+" "+name);
    }
    return eb;
  }

//  /**
//   * 
//   * @param db
//   * @param format
//   * @param currentStudy The current study.
//   * @param parentStudy The parent of the current study, if the current study has a parent; or an inactive StudyBean, otherwise.
//   * @return
//   */
//  public String generateDataset(DatasetBean db, int format, StudyBean currentStudy, StudyBean parentStudy) {
//    //set up the types first
//  	
//    this.setExtractTypesExpected();
//
//    StringBuffer returnText = new StringBuffer("");
//    ArrayList alist = this.select(db.getSQLStatement());
//    //currently hits the view, returns the data
//
//    ExtractBean exbean = new ExtractBean(ds);
//    
//    exbean = this.getExtractBeanFromArrayList(alist, currentStudy);
//
//    exbean.setDataset(db);
//    if (!parentStudy.isActive()) {
//    	exbean.setParentProtocolId(currentStudy.getIdentifier());
//    	exbean.setParentStudyName(currentStudy.getName());
//    }
//    else {
//    	exbean.setParentProtocolId(parentStudy.getIdentifier());
//    	exbean.setParentStudyName(parentStudy.getName());
//    	exbean.setSiteName(currentStudy.getName());
//    }
//    
//    exbean.setFormat(format);
//    
//    returnText.append(exbean.export());
//    return returnText.toString();
//  }
//  
////<<<<<<< DatasetDAO.java
//  public ArrayList generateSPSSDatasets(DatasetBean db,
//  		int format, 
//		StudyBean currentStudy,
//		StudyBean parentStudy) {
//  	ArrayList files = new ArrayList();
//  	//repeat the first few steps, including getting the data from the view
//  	this.setExtractTypesExpected();
//    //StringBuffer ddlFile = new StringBuffer("");
//    //StringBuffer dataFile = new StringBuffer("");
//    ArrayList alist = this.select(db.getSQLStatement());
//    ExtractBean exbean = new ExtractBean(ds);
//    
//    exbean = this.getExtractBeanFromArrayList(alist, currentStudy);
//    exbean.setDataset(db);
//    if (!parentStudy.isActive()) {
//    	exbean.setParentProtocolId(currentStudy.getIdentifier());
//    	exbean.setParentStudyName(currentStudy.getName());
//    }
//    else {
//    	exbean.setParentProtocolId(parentStudy.getIdentifier());
//    	exbean.setParentStudyName(parentStudy.getName());
//    	exbean.setSiteName(currentStudy.getName());
//    }
//    
//    try {
//		//exbean.setFormat(format);
//		files = exbean.exportSPSS();
//		//TODO trying to look at the hashmap's values here, remove
//	} catch (NullPointerException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//		if (files.isEmpty()) {
//			logger.warning("*** empty list");
//		}
//		Iterator it = files.iterator();
//		while (it.hasNext()) {
//			String file = (String)it.next();
//			logger.warning("NPE thrown with this: "+file);
//		}
//	}
//    
//  	return files;
//  }
  
//=======
//  /**
//   * 
//   * @param db
//   * @param format
//   * @param currentStudy The current study.
//   * @param parentStudy The parent of the current study, if the current study has a parent; or an inactive StudyBean, otherwise.
//   * @return
//   */
//  public ExtractBean generateDataset(DatasetBean db, StudyBean currentStudy, StudyBean parentStudy) {
//    //set up the types first
//  	
//    this.setExtractTypesExpected();
//    StringBuffer returnText = new StringBuffer("");
//    ArrayList alist = this.select(db.getSQLStatement());
//    //currently hits the view, returns the data
//    ExtractBean exbean = new ExtractBean(ds);
//    
//    exbean = this.getExtractBeanFromArrayList(alist, currentStudy);
//
//    exbean.setDataset(db);
//    if (!parentStudy.isActive()) {
//    	exbean.setParentProtocolId(currentStudy.getIdentifier());
//    	exbean.setParentStudyName(currentStudy.getName());
//    }
//    else {
//    	exbean.setParentProtocolId(parentStudy.getIdentifier());
//    	exbean.setParentStudyName(parentStudy.getName());
//    	exbean.setSiteName(currentStudy.getName());
//    }
//    return exbean;
//  }
//  
//  public HSSFWorkbook generateExcelDataset(DatasetBean db, 
//  		int format, 
//		StudyBean currentStudy, 
//		StudyBean parentStudy) {
//  	
//  	HSSFWorkbook wb = new HSSFWorkbook();
//  	this.setExtractTypesExpected();
//    
//    ArrayList alist = this.select(db.getSQLStatement());
//   
//    ExtractBean exbean = new ExtractBean(ds);
//    
//    exbean = this.getExtractBeanFromArrayList(alist, currentStudy);
//
//    exbean.setDataset(db);
//    if (!parentStudy.isActive()) {
//    	exbean.setParentProtocolId(currentStudy.getIdentifier());
//    	exbean.setParentStudyName(currentStudy.getName());
//    }
//    else {
//    	exbean.setParentProtocolId(parentStudy.getIdentifier());
//    	exbean.setParentStudyName(parentStudy.getName());
//    	exbean.setSiteName(currentStudy.getName());
//    }
//    
//    exbean.setFormat(format);
//  	
//  	wb = exbean.exportExcel();
//  	return wb;
//  }

  /**
   * Implements the Data Algorithm described in Dataset Export Algorithms,
   * stores output in the returned ExtractBean.
   * @param eb The ExtractBean containing the dataset and study for which data is being retrieved.
   * @return An ExtractBean containing structured data stored by subject,
   * study event definition, ordinal, CRF and item, as well as the maximum
   * ordinal per study event definition.
   */
  public ExtractBean getDatasetData(ExtractBean eb) {
  	String sql = eb.getDataset().getSQLStatement();

  	setExtractTypesExpected();
  	ArrayList viewRows = select(sql);
  	Iterator it = viewRows.iterator();

  	while (it.hasNext()) {
  		HashMap row = (HashMap) it.next();

  		// study subject properties
  		Integer studySubjectId = (Integer) row.get("subject_id");
		String studySubjectLabel = (String) row.get("subject_identifier");
		Date dateOfBirth = (Date)row.get("date_of_birth");  			
		String gender = (String)row.get("gender");
  		
  		// study event properties
  		Integer studyEventDefinitionId = (Integer) row.get("study_event_definition_id");
  		String studyEventDefinitionName = (String) row.get("study_event_definition_name");
  		Boolean repeating = (Boolean) row.get("study_event_definition_repeating");
  		Integer sampleOrdinal = (Integer) row.get("sample_ordinal");
  		
  		// event start, end, location triple
  		String studyEventLocation = (String) row.get("location");
  		Date studyEventStart = (Date) row.get("date_start");
  		Date studyEventEnd = (Date) row.get("date_end");
  		
  		// item data
  		Integer crfId = (Integer) row.get("crf_id");
  		Integer itemId = (Integer) row.get("item_id");
  		String itemValue = (String) row.get("value");

  		eb.addStudySubjectData(studySubjectId, studySubjectLabel, dateOfBirth, gender);  		
  		eb.addStudyEventData(studySubjectId, studyEventDefinitionName,
  		    studyEventDefinitionId, sampleOrdinal, studyEventLocation, studyEventStart, studyEventEnd);
  		eb.addItemData(studySubjectId, studyEventDefinitionId, sampleOrdinal, crfId, itemId, itemValue);
  		
  	}
  

  	return eb;
  }
  
 
//  public ExtractBean getExtractBeanFromArrayListOld(ArrayList viewRows, 
//  		StudyBean currentStudy) {
//  	ExtractBean exbean = new ExtractBean();
//  	
//  	Iterator it = viewRows.iterator();
//  	//TODO have to add a guard clause in there to make sure we 
//  	//properly regulate active-study subjects vs. all subjects for admins
//  	while (it.hasNext()) {
//  		
//  		HashMap row = (HashMap) it.next();
//  		Integer studyId = (Integer) row.get("study_id");
//  		logger.warning("before guard clause:");
//  		if (studyId.intValue()==currentStudy.getId()) {//or you are an admin
//  			//start guard clause
//  			logger.warning("inside guard clause:");
//  			Integer studyEventDefinitionId = (Integer) row.get("study_event_definition_id");
//  			String studyEventDefinitionName = (String) row.get("study_event_definition_name");
//  			Boolean studyEventDefinitionRepeating = (Boolean) row.get("study_event_definition_repeating");
//  			
//  			ExtractStudyEventDefinitionBean sedb = 
//  				exbean.addStudyEventDefinition(studyEventDefinitionId, studyEventDefinitionName, studyEventDefinitionRepeating);
//  			
//  			Integer crfVersionId = (Integer) row.get("crf_version_id");
//  			String crfName = (String) row.get("crf_name");
//  			String crfVersionName = (String) row.get("crf_version_name");
//  			
//  			ExtractCRFVersionBean cvb = sedb.addCRFVersion(crfVersionId, crfName, crfVersionName);
//  			
//  			// note: these do NOT correspond to table names and columns!!
//  			Integer studySubjectId = (Integer) row.get("subject_id");
//  			String studySubjectLabel = (String) row.get("subject_identifier");
//  			//TODO check max length of V1 here, for SPSS purposes
//  			//exbean.updateSPSSColLengths("Subject Unique ID",studySubjectLabel.length());
//  			String protocolId = (String) row.get("study_identifier");
//  			//TODO check the max length of protocol Id here for part of V2, for SPSS purposes
//  			//need to add the siteUniqueIdentifier to it to determine the full length of V2
//  			//exbean.updateSPSSColLengths("Protocol-ID-Site ID",protocolId.length());
//  			Date dateOfBirth = (Date)row.get("date_of_birth");  			
//  			String gender = (String)row.get("gender");
//  			
//  			
//  			ExtractStudySubjectBean ssb =
//  			  exbean.addStudySubject(studySubjectId, studySubjectLabel, protocolId,dateOfBirth,gender);
//  			
//  			Integer dbOrdinal = (Integer) row.get("sample_ordinal");
//  			Integer studyEventId = (Integer) row.get("study_event_id");
//  			String studyEventLocation = (String) row.get("location");
//  			Date studyEventStart = (Date) row.get("date_start");
//  			Date studyEventEnd = (Date) row.get("date_end");
//  			ssb.addStudyEvent(sedb, studyEventId, studyEventLocation, studyEventStart, studyEventEnd, dbOrdinal);
//  			
//  			Integer itemId = (Integer) row.get("item_id");
//  			String itemName = (String) row.get("item_name");
//  			String itemValue = (String) row.get("value");
//  			
//  			cvb.addItem(itemId, itemName);
//  			ssb.addValue(sedb, dbOrdinal, crfVersionId, itemId, itemValue);
//  			//TODO measure max length of column VX here for SPSS generation purposes
//  			//exbean.updateSPSSColLengths(itemName,itemValue.length());
//  			logger.warning("entered "+itemValue.length()+" for item "+itemName);
//  			sedb.updateCRFVersion(cvb);
//  			exbean.updateStudyEventDefinition(sedb);
//  			exbean.updateStudySubject(ssb);
//  			
//  			ResponseSetBean rsb = new ResponseSetBean();
//  			String optionsText = (String) row.get("options_text");
//  		    String optionsValues = (String) row.get("options_values");
//  		    //we only want to set options that are numbers,
//  		    //characters are rejected by SPSS
//  		    Pattern catchText = Pattern.compile("[[0-9],]+");
//  		    java.util.regex.Matcher matchText = 
//  		    	catchText.matcher(optionsValues);
//  		    boolean b = matchText.matches();
//  		    //logger.warning("found options: "+optionsValues+", returning: "+b);
//  		    rsb.setOptions(optionsText, optionsValues);
//	    	Integer responseTypeId = (Integer) row.get("response_type_id");
//  		    if (b) {
//  	  		    rsb.setResponseTypeId(responseTypeId.intValue());    
//  		    } else {
//  		    	rsb.setResponseTypeId(1);
//  		    }
//  		    exbean.addResponseSet(itemName,rsb);
//  			exbean.updateMaxSamples(sedb, ssb.getNumSamples(sedb.getId()));
//  			//end guard clause
//  		}
//  	}
//    
//  	return exbean;
//    
//  }  

//  public ExtractBean getExtractBeanFromArrayList(ArrayList viewRows) {
//    ExtractBean exbean = new ExtractBean();
//
//    ArrayList rows = new ArrayList(); // contains subject identifiers
//    ArrayList columns = new ArrayList();
//    HashMap content = new HashMap();
//    
//    columns.add("Subject");
//    
//    // tells us if a row for a subject has been added
//    // key is a String of format "0001" where "0001" is the subject_identifier
//    // for a given subject, the key is present if the subject has been added, not present otherwise
//    HashMap subjectAdded = new HashMap();
//    
//    // tells us if a column for the crfVersion-itemId combination has been added
//    // key is a String of format "1-2" where "1" is the crfVersionId and "2" is the itemId
//    // for a given crfVersion/item combination, the key is present if the combination has been added, not present otherwise
//    // the value will be the itemNum of the crfVersion/itemId combination
//    HashMap crfVersionItemIdAdded = new HashMap();
//    
//    int itemNum = 0;
//    
//    Iterator it = viewRows.iterator();
//
//	while (it.hasNext()) {
//	  HashMap row = (HashMap) it.next();
//	  String subjectIdentifier = (String) row.get("subject_identifier");
//	  
//	  if (!subjectAdded.containsKey(subjectIdentifier)) {
//	    subjectAdded.put(subjectIdentifier, Boolean.TRUE);
//	    rows.add(subjectIdentifier);
//	  }
//    
//	  String CRFName = (String) row.get("crf_name");
//	  String CRFVersionName = (String) row.get("crf_version_name");
//	  String itemName = (String) row.get("item_name");
//	  String contentValue = (String) row.get("value");
//	  
//	  Integer crfVersionId = (Integer) row.get("crf_version_id");
//	  Integer itemId = (Integer) row.get("item_id");
//	  
//	  if ((crfVersionId == null) || (itemId == null)) {
//	    logger.warning("found null crfversionid/itemid");
//	    continue;
//	  }
//	  
//	  String key = crfVersionId.intValue() + "-" + itemId.intValue();
//	  if (!crfVersionItemIdAdded.containsKey(key)) {
//	    itemNum++;
//	    crfVersionItemIdAdded.put(key, new Integer(itemNum));
//
//	    columns.add("CRF Name " + itemNum);
//	    content.put("CRF Name " + itemNum, CRFName);
//
//	    columns.add("CRF Version " + itemNum);
//	    content.put("CRF Version " + itemNum, CRFVersionName);
//
//	    columns.add(itemName + " " + itemNum);
//	    String contentKey = subjectIdentifier + " " + itemName + " " + itemNum;
//	    content.put(contentKey, contentValue);
//	  }
//	  else {
//	    Integer itemNumInteger = (Integer) crfVersionItemIdAdded.get(key); 
//	    String contentKey = subjectIdentifier + " " + itemName + " " + itemNumInteger.intValue();
//	    content.put(contentKey, contentValue);	    
//	  }
//	}
//    
////    int maxCRFs = 0;
////    ArrayList subjects = new ArrayList();
////    ArrayList listCRFs = new ArrayList();
////    HashMap content = new HashMap();
////    ArrayList columnHeaders = new ArrayList();
////    Iterator it = alist.iterator();
////    columnHeaders.add("Subject");
////    while (it.hasNext()) {
////      HashMap hm = (HashMap) it.next();
////      String subjectIdentifier = (String) hm.get("subject_identifier");
////      //logger.warning("** captured subject identifier: "+subjectIdentifier);
////      if (!subjects.contains(subjectIdentifier)) {
////        subjects.add(subjectIdentifier);
////        logger.warning("** ADDED subject identifier: "+subjectIdentifier);
////      } else {
////      	//logger.warning("** DID NOT ADD subject identifier");
////      }
////      String CRFName = (String) hm.get("crf_name");
////      String CRFVersionName = (String) hm.get("crf_version_name");
////      String checkCRF = CRFName + " " + CRFVersionName;
////      if (!listCRFs.contains(checkCRF)) {
////        maxCRFs++;
////        columnHeaders.add("CRF Name " + maxCRFs);
////        columnHeaders.add("CRF Version " + maxCRFs);
////        content.put("CRF Name " + maxCRFs, CRFName);
////        content.put("CRF Version " + maxCRFs, CRFVersionName);
////        //so this means that either row+column or just column names
////        //can be filled in the export dataset functions
////      }
////      String itemName = (String) hm.get("item_name");
////      String contentValue = (String) hm.get("value");
////      String orderedItemName = itemName + " " + maxCRFs;
////      String contentKey = subjectIdentifier + " " + orderedItemName;
////
////if (itemName.equals("2_B1")) {
////      System.out.println("** ADDED column header for item values: " + orderedItemName + "; CRF: " + CRFName + " " + CRFVersionName + "; column num: " + maxCRFs + "; subject: " + subjectIdentifier);
////      System.out.println("key is " + contentKey + " and value is " + contentValue);
////}      
////
////      columnHeaders.add(orderedItemName);
////      content.put(contentKey, contentValue);
////    }
////    
////    // three types of keys in content:
////    // CRF Name x - the name of a CRF for the x-th three-column entry
////    // CRF Version x - the name of a CRF version for the x-th three-column entry
////    // 001 2_B1 3 - the value of the item with item name "2_B1", for subject with identifier "001", in the 3d three column entry
////    //		("001", "2_B1", and "3" are just examples)
//    
//    exbean.setColNames(columns);
//    exbean.setRowNames(rows);
//    exbean.setContent(content);
//    return exbean;
//  }

  public Collection findAllByPermission(Object objCurrentUser, int intActionType,
      String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
    ArrayList al = new ArrayList();

    return al;
  }

  public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
    ArrayList al = new ArrayList();

    return al;
  }

  public ArrayList findAllByStudyId(int studyId) {
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(studyId));
    variables.put(new Integer(2), new Integer(studyId));

    return executeFindAllQuery("findAllByStudyId", variables);
  }
  
  public ArrayList findAllByStudyIdAdmin(int studyId) {
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(studyId));
    variables.put(new Integer(2), new Integer(studyId));

    return executeFindAllQuery("findAllByStudyIdAdmin", variables);
  }

}
