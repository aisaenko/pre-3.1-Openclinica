package org.akaza.openclinica.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.akaza.openclinica.bean.managestudy.StudyBean;


public class TaskMenuSection {
	private String m_sectionName;
	private ArrayList<MenuItem> m_sectionItems;
	//private int m_itemsInColumn;
	
	enum SECTION_TYPE{
		ADMIN_SECTION,
		OTHER_SECTION,
		EXTRACT_SECTION,
		SUBMIT_SECTION,
		BUILD_STUDY_SECTION,
		MONITOR_MANAGEDATA_SECTION,
		STUDY_SETUP;
	}
	/**
	 * @param m_sectionItems the m_sectionItems to set
	 */
	public void setSectionMenuItems(ArrayList<MenuItem> m_sectionItems) {
		this.m_sectionItems = m_sectionItems;
	}
	/**
	 * @return the m_sectionItems
	 */
	public ArrayList<MenuItem> getSectionMenuItems() {
		return m_sectionItems;
	}
	/**
	 * @param m_sectionName the m_sectionName to set
	 */
	public void setSectionName(String m_sectionName) {
		this.m_sectionName = m_sectionName;
	}
	/**
	 * @return the m_sectionName
	 */
	public String getSectionName() {
		return m_sectionName;
	}
	
	public TaskMenuSection(String sectionName, HashMap<String,String> allPermitURL,  
			ResourceBundle  resword, SECTION_TYPE section_type,  StudyBean study){
		m_sectionName = sectionName;
		m_sectionItems = new  ArrayList<MenuItem>();
		switch(section_type){
		case ADMIN_SECTION: {
				 buildAdminSection(  this, allPermitURL,    resword);
				 break;
		}
		case  OTHER_SECTION:{
			buildOtherSection(  this, allPermitURL,    resword);
			break;
		}
		case EXTRACT_SECTION:{
			buildExtractDataSection(  this, allPermitURL,    resword);
			break;
		}
		case SUBMIT_SECTION:{
			buildSubmitDataSection(  this, allPermitURL,    resword, study);
			break;
		}
		case BUILD_STUDY_SECTION:{
			buildStudySetupSection(  this, allPermitURL,    resword, study);
			break;
		}
		case MONITOR_MANAGEDATA_SECTION:{
			buildMonitorMangeSection(  this, allPermitURL,    resword, study);
			break;
		}
		}
	}
	
	private void buildAdminSection(TaskMenuSection curSection, HashMap<String,String> allPermitURL, 
			 ResourceBundle  resword){
		MenuItem menuItem = null;
		String key;String permission;
		if (allPermitURL.get("/ListStudy")!= null){
			//strip / is needed
			key = resword.getString("nav_studies");
			menuItem = new MenuItem("ListStudy", key);
			addMenuItem(menuItem);
			
		}
		if (allPermitURL.get("/ViewAllJobs")!= null){
			//strip / is needed
			key = resword.getString("nav_jobs");
			menuItem = new MenuItem("ViewAllJobs", key);
			addMenuItem(menuItem);
			
		}
		 if (allPermitURL.get("/ListUserAccounts")!= null){
			//strip / is needed
			key = resword.getString("nav_users");
			menuItem = new MenuItem("ListUserAccounts", key);
			addMenuItem(menuItem);
			
		}
		 if (allPermitURL.get("/ListSubject")!= null){
				//strip / is needed
				key = resword.getString("nav_subjects");
				menuItem = new MenuItem("ListSubject", key);
				addMenuItem(menuItem);
				
			}
		 if (allPermitURL.get("/ListCRF")!= null){
			//strip / is needed
			key = resword.getString("nav_crfs");
			permission="ListCRF?module=admin";
			menuItem = new MenuItem(permission, key);
			addMenuItem(menuItem);
			
		}
		 
//		 
//		 if (allPermitURL.get("/pages/reviewCodes")!= null){
//			//strip / is needed
//			key = resword.getString("nav_coding_review");
//			permission="pages/reviewCodes?studyId=";
//			menuItem = new MenuItem(permission, key);
//			menuItem.setIsStudyIdDependent(true);
//			addMenuItem(menuItem);
//			
//		}
		
	}
	private void buildOtherSection(TaskMenuSection curSection, HashMap<String,String> allPermitURL,  ResourceBundle  resword){
		MenuItem menuItem = null;
		String key = resword.getString("nav_update_profile");
		menuItem = new MenuItem("UpdateProfile", key);
		addMenuItem(menuItem);
		key = resword.getString("nav_log_out");
		menuItem = new MenuItem("j_spring_security_logout", key);
		addMenuItem(menuItem);
		
	}
	private void buildExtractDataSection(TaskMenuSection curSection, HashMap<String,String> allPermitURL,  ResourceBundle  resword){
		MenuItem menuItem = null;String key=null;
		
			if (allPermitURL.get("/ViewDatasets") != null ){
				 key = resword.getString("nav_view_datasets");
				menuItem = new MenuItem("ViewDatasets", key);
				addMenuItem(menuItem);
			}
			if (allPermitURL.get("/CreateDataset")  != null){
				 key = resword.getString("nav_create_dataset");
					menuItem = new MenuItem("CreateDataset", key);
					addMenuItem(menuItem);
			}
		
		//m_itemsInColumn=1;
	}
	
	private void buildSubmitDataSection(TaskMenuSection curSection, HashMap<String,String> allPermitURL,
			ResourceBundle  resword, StudyBean study){
		MenuItem menuItem = null;String key;
		String permission;
			if (allPermitURL.get("/ListStudySubjects")!= null  ){
				key = resword.getString("nav_subject_matrix");
				menuItem = new MenuItem("ListStudySubjects", key);
				addMenuItem(menuItem);
			}
//change here to include ref to study status (!study.status.frozen && !study.status.locked)
			
			if (allPermitURL.get("/CreateNewStudyEvent")!= null  ){
				key = resword.getString("nav_schedule_event");
				menuItem = new MenuItem("CreateNewStudyEvent", key, false, study);
				addMenuItem(menuItem);
			}
			
			
			//change here to include ref to study status (available)
			if (allPermitURL.get("/AddNewSubject")  != null   ){
				//check study status, should be study.status.available
				key = resword.getString("nav_add_subject");
				menuItem = new MenuItem("AddNewSubject", key, false,study);
				addMenuItem(menuItem);
			}
			if (allPermitURL.get("/ViewStudyEvents")  != null){
				key = resword.getString("nav_view_events");
				menuItem = new MenuItem("ViewStudyEvents", key);
				addMenuItem(menuItem);
			}
			
			if (allPermitURL.get("/ViewNotes")  != null   ){
				//check study status, should be study.status.available
				key = resword.getString("nav_notes_and_discrepancies");
				menuItem = new MenuItem("ViewNotes", key);
				addMenuItem(menuItem);
			}
			if (allPermitURL.get("/ImportCRFData")  != null){
				key = resword.getString("nav_import_data");
				menuItem = new MenuItem("ImportCRFData", key);
				addMenuItem(menuItem);
			}
		//	m_itemsInColumn= 3;
			
		}
	
		
	private void buildMonitorMangeSection(TaskMenuSection curSection, HashMap<String,String> allPermitURL, 
			ResourceBundle  resword,  StudyBean study){
		MenuItem menuItem = null;String key;String permission;
		 	
			if (allPermitURL.get("/pages/viewAllSubjectSDVtmp")!= null  && study.getParentStudyId()>0){
				key = resword.getString("nav_source_data_verification");
				permission="pages/viewAllSubjectSDVtmp?studyId=";
				menuItem = new MenuItem(permission, key);
				//menuItem.setIsStudyIdDependent(true);
				addMenuItem(menuItem);
			}
			if (allPermitURL.get("/ListSubjectGroupClass")!= null  && study.getParentStudyId()<=0){ //&& //condition study.parentStudyId <= 0 ){
				key = resword.getString("nav_groups");
				permission="ListSubjectGroupClass?read=true";
				menuItem = new MenuItem(permission, key);
			//	menuItem.setIsStudyLevelOnly(true);
				addMenuItem(menuItem);
			}
			if (allPermitURL.get("/StudyAuditLog")!= null  ){
				key = resword.getString("nav_study_audit_log");
				menuItem = new MenuItem("StudyAuditLog", key);
				addMenuItem(menuItem);
			}
			if (allPermitURL.get("/ListCRF") != null  && study.getParentStudyId()<=0){ //&& //condition study.parentStudyId <= 0 ){
				key = resword.getString("nav_crfs");
				permission="ListCRF?module=manage";
				menuItem = new MenuItem(permission, key);
			//	menuItem.setIsStudyLevelOnly(true);
				addMenuItem(menuItem);
				
			}
			if (allPermitURL.get("/ViewRuleAssignment")!=null  && study.getParentStudyId()<=0) {//&& //condition study.parentStudyId <= 0 ){
				key = resword.getString("nav_rules");
				permission="ViewRuleAssignment?read=true";
				menuItem = new MenuItem(permission, key);
				//menuItem.setIsStudyLevelOnly(true);
				addMenuItem(menuItem);
			}
			 if (allPermitURL.get("/pages/reviewCodes")!= null  && study.getParentStudyId()<=0){
					//strip / is needed
					key = resword.getString("nav_coding_review");
					permission="pages/reviewCodes?studyId="+study.getId();
					menuItem = new MenuItem(permission, key);
					//menuItem.setIsStudyIdDependent(true);
					addMenuItem(menuItem);
					
				}
			//m_itemsInColumn= 3;
			
		}

	
	  private void buildStudySetupSection(TaskMenuSection curSection, HashMap<String,String> allPermitURL, 
			  ResourceBundle  resword,  StudyBean study){
			  MenuItem menuItem = null;String key;String permission;
			  	if (allPermitURL.get("/ViewStudy") != null  ){
					key = resword.getString("nav_view_study");
					permission="ViewStudy?viewFull=yes&studyId="+study.getId();
					menuItem = new MenuItem(permission, key);
					//menuItem.setIsStudyIdDependent(true);
					addMenuItem(menuItem);
				}
			  	if (allPermitURL.get("/ListStudyUser") != null ){
						
						key = resword.getString("nav_users");
						menuItem = new MenuItem("ListStudyUser", key);
						addMenuItem(menuItem);
					}
				if (allPermitURL.get("/pages/studymodule")!= null  && study.getParentStudyId()<=0 ){
					//condition here study.parentStudyId > 0
					key = resword.getString("nav_build_study");
					menuItem = new MenuItem("pages/studymodule", key);
					//menuItem.setIsStudyLevelOnly(true);
					addMenuItem(menuItem);
				}
				
				// m_itemsInColumn= (int) (this.m_sectionItems.size() )/2;
					
	  }
  
	 private void addMenuItem(MenuItem menuItem){
		 if ( menuItem != null && menuItem.getItemUrl()!= null && menuItem.getItemKey()!= null){
			 m_sectionItems.add(menuItem);
		 }
	 }
	
	/**
	 * @param m_itemsInColumn the m_itemsInColumn to set
	 */
//	public void setItemsInColumn(int m_itemsInColumn) {
//		this.m_itemsInColumn = m_itemsInColumn;
//	}
	/**
	 * @return the m_itemsInColumn
	 */
//	public int getItemsInColumn() {
//		return m_itemsInColumn;
//	}
//	
}
