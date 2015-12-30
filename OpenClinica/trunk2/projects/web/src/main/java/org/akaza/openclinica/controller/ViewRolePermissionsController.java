package org.akaza.openclinica.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.hibernate.LocalePropertyValueDao;
import org.akaza.openclinica.dao.hibernate.PermissionDao;
import org.akaza.openclinica.dao.hibernate.RoleDao;
import org.akaza.openclinica.dao.hibernate.RuleSetRuleDao;
import org.akaza.openclinica.domain.role.LocalePropertyValue;
import org.akaza.openclinica.domain.role.Permissions;
import  org.akaza.openclinica.domain.role.Role ;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

/**
 * Implement the functionality for displaying a table of Role permissions.
 *  This is an autowired, multiaction Controller.
 */
@Controller("viewRolePermissionsController")
public class ViewRolePermissionsController {
	public final static String VIEW_ROLE_PERMISSION_TABLE_CONTENT = "vrpTableContent";
	public final static String VIEW_ROLE_PERMISSION_TABLE_HEADER = "vrpTableHeader";
	public final static String DATATABLE_LANGUAGE_FILE = "dataTableLanguageFile";
	   
	/*
	@Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;
    */
	


    //Autowire the class that handles the sidebar structure with a configured
    //bean named "sidebarInit"
    @Autowired
    @Qualifier("sidebarInit")
    private SidebarInit sidebarInit;
    
    ViewRolePermissionsController(){
    	
    }
    @RequestMapping(value="/admin/viewRolePermissions", method = RequestMethod.GET)
    public ModelMap viewRolePermissions(HttpServletRequest request, HttpServletResponse response) {

    		
    
        //For the messages that appear in the left column of the results page
        ArrayList<String> pageMessages = new ArrayList<String>();
        ArrayList<String[]> rolePermissionsTableContent = new ArrayList<String[]> ();
        ArrayList<String> rolePermissionsTableHeader = new ArrayList<String> ();
        Locale locale = request.getLocale();
        ResourceBundleProvider.updateLocale(locale);
        ResourceBundle  resword=org.akaza.openclinica.i18n.util.ResourceBundleProvider.getWordsBundle(locale);
        
        try{
	        ApplicationContext context=RequestContextUtils.getWebApplicationContext(request);
	        RoleDao roledao = (RoleDao)context.getBean("roleDAO");
	        List<Role> roles = roledao.findAllSortByColumn("id");
	        PermissionDao permissionDao = (PermissionDao)context.getBean("permissionDAO");
	        List<Permissions> permissions = permissionDao.findAllSortByColumn("access_url");
	        LocalePropertyValueDao lpvdao= (LocalePropertyValueDao)context.getBean("localePropertyValueDAO");
	    	
	        rolePermissionsTableHeader.add(resword.getString("url"));
	        HashMap<Integer, Integer> role_ids = new HashMap<Integer,Integer>(roles.size());
	        int role_count=0;
	        
	        for (Role role : roles){
	        //	rolePermissionsTableHeader.add( getRoleDescription(role.getRoleName(),locale));
	        	rolePermissionsTableHeader.add(getRoleDescription(role,locale,lpvdao));
	     	   role_ids.put(role.getId(), new Integer(role_count));
	     	   role_count++;
	        	
	        }
	        
	       
	       String[] row ; String temp_url ;
	       String yes_permission = resword.getString("yes_permission");
	       String no_permission= resword.getString("no_permission");
	       for ( org.akaza.openclinica.domain.role.Permissions prc : permissions){
	    	   row =  new String[rolePermissionsTableHeader.size()];
	    	   for (int count = 1; count <row.length; count++){   	row[count]  =no_permission; 	}
	    	   temp_url = prc.getAccess_url();
	    	   temp_url = temp_url.replace("/pages", "");
	    	   temp_url = temp_url.substring(1);
	    	   
	    	   row[0]=temp_url;
	    	   String params = prc.getAccess_parameters();
	    	   
	    	 
	    	   if ( prc.getAccess_parameters() != null && !prc.getAccess_parameters().equals("")){
	    	       params = params.replace(",", ",<br/> ");
	               params = params.replace(";", ";<br/> ");
	    	       row[0]=row[0]+ "<br/> ("+params+")";
	    	   }
	     	   for (org.akaza.openclinica.domain.role.Role rr1: prc.getRole()){
	    		     row[role_ids.get(rr1.getId()).intValue()+1]=yes_permission;
	     		    //ArrayList<String> table_row = new ArrayList<String>(rolePermissionsTableHeader.size());
	     		   }
	       	      rolePermissionsTableContent.add(row);
	        }
	       
        }catch (Exception e){
        	pageMessages.add("There was a problem with getting data for the view");
        }
       
       //get language file for dataTable
        ResourceBundle  resformat=org.akaza.openclinica.i18n.util.ResourceBundleProvider.getFormatBundle(locale);
        String dataTableLFile = resformat.getString("dataTableLanguageFile");
       
        ModelMap map = new ModelMap();
        map.addAttribute(DATATABLE_LANGUAGE_FILE, dataTableLFile);
        map.addAttribute(VIEW_ROLE_PERMISSION_TABLE_CONTENT, rolePermissionsTableContent);
       // map.addAttribute(VIEW_ROLE_PERMISSION_TABLE_CONTENT, rolePermissionsTableContent);
        map.addAttribute(VIEW_ROLE_PERMISSION_TABLE_HEADER, rolePermissionsTableHeader);
     	request.setAttribute("pageMessages", pageMessages);
     	return map;   
        

    }

    
    private String   getRoleDescription(Role role, Locale locale, LocalePropertyValueDao lpvdao){
    	Locale default_locale = new Locale("en_US");
    	List<LocalePropertyValue> results = 
    		lpvdao.findByLocaleAndPropertyName(role.getRoleName(), locale.getLanguage());
    	String result="";
    	for ( LocalePropertyValue lvalue:  results){
    		if( lvalue.getLocaleName().equals(locale.getLanguage())){
    			return lvalue.getLocaleDescription();
    		}
    		if (lvalue.getLocaleName().equals(default_locale.getLanguage())){
    			result = lvalue.getLocaleDescription();
    		}
    	}
    	return result;
    }
    
    private String   getRoleDescription(String role_name, Locale locale){
    	ResourceBundle resterm = org.akaza.openclinica.i18n.util.ResourceBundleProvider.getTermsBundle(locale);

        String str =    resterm.getString(role_name).trim();
        return (str==null)? role_name: str;
    }
    
   
   
}
