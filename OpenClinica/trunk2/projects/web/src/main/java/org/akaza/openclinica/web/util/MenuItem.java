package org.akaza.openclinica.web.util;

import org.akaza.openclinica.bean.managestudy.StudyBean;


public class MenuItem {
	private String m_itemUrl;
	private String m_homePageURL;
	private String m_itemKey;
	private boolean m_isStudyStatusDependent;
//	private int[] m_studyStatusForMenuItem;
//	private boolean m_isStudyIdDependent;
//	private boolean m_isStudyLevelOnly;
	
	/**
	 * @param m_itemUrl the m_itemUrl to set
	 */
	public void setItemUrl(String m_itemUrl) {
		this.m_itemUrl = m_itemUrl;
	}
	/**
	 * @return the m_itemUrl
	 */
	public String getItemUrl() {
		return m_itemUrl;
	}
	/**
	 * @param m_itemKey the m_itemKey to set
	 */
	public void setItemKey(String m_itemKey) {
		this.m_itemKey = m_itemKey;
	}
	/**
	 * @return the m_itemKey
	 */
	public String getItemKey() {
		return m_itemKey;
	}
	
	
	
	/**
	 * @param m_isStudyStatusDependent the m_isStudyStatusDependent to set
	 */
	public void setIsStudyStatusDependent(boolean m_isStudyStatusDependent) {
		this.m_isStudyStatusDependent = m_isStudyStatusDependent;
	}
	/**
	 * @return the m_isStudyStatusDependent
	 */
	public boolean getIsStudyStatusDependent() {
		return m_isStudyStatusDependent;
	}

	
	public MenuItem(String url, String key){
		 this( url,  key,  false, null);
		 
		 
	}
	public MenuItem(String url, String key,  boolean isAddParam,  StudyBean study){
		//m_isStudyIdDependent = m_isStudyStatusDependent = m_isStudyLevelOnly =false;
	// for url that requier additional params
		if ( isAddParam){
			if (url.equals("ViewNotes") || url.equals("ListCRF")){
				url+="?module=submit";
			}
			else if ( url.equals("ListCRF")){
				url+="?module=manage";
			}
			else if ( url.equals("ViewRuleAssignment") || url.equals("ListSubjectGroupClass")){
				url+="?read=true";
			}else if(url.equals("ViewStudy")){
				url+="?viewFull=yes;&studyId="+study.getId();
			//	m_isStudyIdDependent=true;
				
			}
			if (url.contains("viewAllSubjectSDVtmp") ||
					url.contains("reviewCodes") ){
				//m_isStudyIdDependent=true;
				url+="?studyId="+study.getId();
			}
		}
		//set params for url that depend on study status
		if (url.equals("AddNewSubject") && study.getStatus().getId()!=1 ){
//			m_isStudyStatusDependent=true;
//			m_studyStatusForMenuItem = new int[1];
//			m_studyStatusForMenuItem[0]=1;
			return ;
		}
		if ( url.equals("CreateNewStudyEvent") && !( study.getStatus().getId()==1 || study.getStatus().getId()==4 )){
//			m_isStudyStatusDependent=true;
//			m_studyStatusForMenuItem = new int[2];
//			m_studyStatusForMenuItem[0]=1;m_studyStatusForMenuItem[0]=4;
			return;
		}
		m_itemUrl = url;
		if (key.contains("&amp;")){
			String[] temp = key.split("&amp;");
			key = temp[0]+"&"+temp[1];
			
		}
		m_itemKey =key;
	}
//	/**
//	 * @param m_isStudyIdDependent the m_isStudyIdDependent to set
//	 */
//	public void setIsStudyIdDependent(boolean m_isStudyIdDependent) {
//		this.m_isStudyIdDependent = m_isStudyIdDependent;
//	}
//	/**
//	 * @return the m_isStudyIdDependent
//	 */
//	public boolean getIsStudyIdDependent() {
//		return m_isStudyIdDependent;
//	}
//	/**
//	 * @param m_studyStatusForMenuItem the m_studyStatusForMenuItem to set
//	 */
//	public void setStudyStatusForMenuItem(int[] m_studyStatusForMenuItem) {
//		this.m_studyStatusForMenuItem = m_studyStatusForMenuItem;
//	}
//	/**
//	 * @return the m_studyStatusForMenuItem
//	 */
//	public int[] getStudyStatusForMenuItem() {
//		return m_studyStatusForMenuItem;
//	}
	/**
	 * @param m_homePageURL the m_homePageURL to set
	 */
	public void setHomePageURL(String m_homePageURL) {
		this.m_homePageURL = m_homePageURL;
	}
	/**
	 * @return the m_homePageURL
	 */
	public String getHomePageURL() {
		return m_homePageURL;
	}
//	/**
//	 * @param m_isStudyLevelOnly the m_isStudyLevelOnly to set
//	 */
//	public void setIsStudyLevelOnly(boolean m_isStudyLevelOnly) {
//		this.m_isStudyLevelOnly = m_isStudyLevelOnly;
//	}
//	/**
//	 * @return the m_isStudyLevelOnly
//	 */
//	public boolean getIsStudyLevelOnly() {
//		return m_isStudyLevelOnly;
//	}
}
