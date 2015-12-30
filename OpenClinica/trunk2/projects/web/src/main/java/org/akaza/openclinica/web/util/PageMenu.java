package org.akaza.openclinica.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.web.util.TaskMenuSection.SECTION_TYPE;

public class PageMenu {
	
	public static final String PAGE_MENU_UI_CONSTANT="pageMenu";
	public static final String PAGE_MENU_RECALCULATE="recalculatePageMenu";
	
	private ArrayList<MenuItem> m_navBar;
	private ArrayList<TaskMenuSection> m_taskBar;
	/**
	 * @param m_navBar the m_navBar to set
	 */
	public PageMenu(){
		 m_taskBar = new ArrayList<TaskMenuSection>();
		 m_navBar = new ArrayList<MenuItem> (4);
	}
	public void setNavBar(ArrayList<MenuItem> m_navBar) {
		this.m_navBar = m_navBar;
	}
	/**
	 * @return the m_navBar
	 */
	public ArrayList<MenuItem> getNavBar() {
		return m_navBar;
	}
	/**
	 * @param m_tastBar the m_tastBar to set
	 */
	public void setTaskBar(ArrayList<TaskMenuSection> m_tastBar) {
		this.m_taskBar = m_tastBar;
	}
	/**
	 * @return the m_tastBar
	 */
	public ArrayList<TaskMenuSection> getTaskBar() {
		return m_taskBar;
	}
	
	
	public void buildPageMenu(HttpServletRequest request,  String menu_params, 
			List<String> allPermissionsURL, StudyBean study){
		  //brrr how get params
        buildNavBar(menu_params, study);
        buildTaskBar( request,   allPermissionsURL, study);
		
	}
	public void buildNavBar(String url_params,  StudyBean study){
		//parse per items
		String[] items = url_params.split(";");
			//parse items
		MenuItem navItem; 
		boolean isFirstItem=true;
		String url=null;
		String[] item_data ;
		for (String item: items){
			item_data = item.split(",");
			if (item_data.length!=2) continue;
			url = (isFirstItem)? "MainMenu" : item_data[0].substring(1);
			navItem = new MenuItem(url,item_data[1], true, study);
			if (isFirstItem){
				navItem.setHomePageURL(item_data[0].substring(1));
				isFirstItem = !isFirstItem;
			}
			if ( navItem.getItemUrl()!= null && navItem.getItemKey()!= null){
				m_navBar.add(navItem);
			}
		}
		//build item
	}
	public void buildTaskBar(HttpServletRequest request,  List<String> allPermissionsURL,  StudyBean study){
		 Locale locale = request.getLocale();
        ResourceBundleProvider.updateLocale(locale);
        ResourceBundle  resword=org.akaza.openclinica.i18n.util.ResourceBundleProvider.getWordsBundle(locale);
        String section_name;TaskMenuSection curSection;
        
        // put permissions in hash for easy access
        HashMap<String,String> allPermitURL = new HashMap<String,String>(allPermissionsURL.size());
        for(String url : allPermissionsURL){
        	allPermitURL.put(url, url);
        }
        
        section_name= resword.getString("nav_submit_data");
        curSection = new TaskMenuSection(section_name,   allPermitURL, resword, SECTION_TYPE.SUBMIT_SECTION, study);
       if ( !(curSection.getSectionMenuItems()== null || curSection.getSectionMenuItems().size()==0)){
       		m_taskBar.add(curSection);
       }
       
       section_name= resword.getString("nav_monitor_and_manage_data");
       curSection = new TaskMenuSection(section_name,   allPermitURL, resword,  
    		   SECTION_TYPE.MONITOR_MANAGEDATA_SECTION, study);
      if ( !(curSection.getSectionMenuItems()== null || curSection.getSectionMenuItems().size()==0)){
      	m_taskBar.add(curSection);
      }
       
         section_name= resword.getString("nav_extract_data");
         curSection = new TaskMenuSection(section_name,   allPermitURL, resword, 
        		 SECTION_TYPE.EXTRACT_SECTION, study);
        if ( !(curSection.getSectionMenuItems()== null || curSection.getSectionMenuItems().size()==0)){
        	m_taskBar.add(curSection);
        }
        
      
        section_name= resword.getString("nav_study_setup");
        curSection = new TaskMenuSection(section_name,   allPermitURL, resword, 
        		SECTION_TYPE.BUILD_STUDY_SECTION, study);
       if ( !(curSection.getSectionMenuItems()== null || curSection.getSectionMenuItems().size()==0)){
       	m_taskBar.add(curSection);
       }
	     //build all sections for task menu
         section_name= resword.getString("nav_administration");
         curSection = new TaskMenuSection(section_name,   allPermitURL, resword, SECTION_TYPE.ADMIN_SECTION, study);
        if ( !(curSection.getSectionMenuItems()== null || curSection.getSectionMenuItems().size()==0)){
        	m_taskBar.add(curSection);
        }
        
         section_name= resword.getString("nav_other");
         curSection = new TaskMenuSection(section_name,   allPermitURL, resword, SECTION_TYPE.OTHER_SECTION, study);
         m_taskBar.add(curSection);
        
        
	}

}
