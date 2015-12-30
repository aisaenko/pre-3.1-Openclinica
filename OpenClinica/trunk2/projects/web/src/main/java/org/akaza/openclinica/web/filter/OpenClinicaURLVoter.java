package org.akaza.openclinica.web.filter;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.hibernate.LocalePropertyValueDao;
import org.akaza.openclinica.dao.hibernate.PermissionDao;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.domain.role.GroupAuthDefinition;
import org.akaza.openclinica.domain.role.LocalePropertyValue;
import org.akaza.openclinica.domain.role.Permissions;
import org.akaza.openclinica.domain.role.UserAccount;
import org.akaza.openclinica.domain.role.UserRoleAccess;
import org.akaza.openclinica.web.util.PageMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.web.servlet.support.RequestContextUtils;

public class OpenClinicaURLVoter implements AccessDecisionVoter {
    public static final String USER_BEAN_NAME = "userBeanURL";
    private static final String PERMIT_DISCREPANCIES="permitDiscrepancies";
    private static final String EVENT_STATUS="eventStatus";
    private LocalePropertyValueDao localePropertyValueDao;
    
    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

    public boolean supports(ConfigAttribute arg0) {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean supports(Class<?> arg0) {
        return FilterInvocation.class.isAssignableFrom(arg0);
    }

    private DataSource dataSource;
    private PermissionDao permissionDao;

    public int vote(Authentication authentication, Object arg1, Collection<ConfigAttribute> arg2) {
        String path,authority;
        
        logMe("is AuthenticationDetails null?" + authentication.getDetails());

        FilterInvocation invocation = (FilterInvocation) arg1;
        HttpServletRequest request = invocation.getHttpRequest();
        String url = invocation.getRequestUrl();
       
        if (request.getPathInfo() != null) {
            path = request.getServletPath() + request.getPathInfo();
        } else {
            path = request.getServletPath();
        }

        logMe("access for url being requested::" + url);
        logMe("length of grantedAuthorities:::::" + authentication.getAuthorities().size());
        UserAccount userAcnt = (UserAccount) request.getSession().getAttribute(USER_BEAN_NAME);
        for (GrantedAuthority ga : authentication.getAuthorities()) {
            logMe("each authrority is?" + ga.getAuthority());
            
            authority = ga.getAuthority();
            if (!path.isEmpty() & url.indexOf("?") != -1)
				url.substring(url.indexOf("?"));
			else {
			}
            
            
            if (authority.equals(path)) {
              
                
                //OpenClinicaAuthDetails details = (OpenClinicaAuthDetails) authentication.getDetails();

               // if (!detailsChecker(url, details, request))
                //{
            		boolean isFirstLogin = (request.getSession().getAttribute(PageMenu.PAGE_MENU_UI_CONSTANT)== null);
             	    boolean isRecalculateUIParams= ( (path.equals("/MainMenu") || path.equals("/pages/studymodule")) && request.getSession().getAttribute(PageMenu.PAGE_MENU_RECALCULATE) != null );
            	    if (isRecalculateUIParams)	{request.getSession().removeAttribute(PageMenu.PAGE_MENU_RECALCULATE);}
            		if (isFirstLogin){isRecalculateUIParams=true;}
            		
                    populateStatusAndDoNotAccessList(request, authentication.getAuthorities(), path,
                    		isRecalculateUIParams , authentication);
                   // ApplicationContext context=RequestContextUtils.getWebApplicationContext(request);
          	       //get locale role descriptions for  user and set current role
                    if ( userAcnt != null && isFirstLogin ){ setupUserRoleDescriptions(request, userAcnt);}
                    if ( userAcnt != null && isRecalculateUIParams ){ 
                    	setupUserCurrentRole(request, userAcnt, authentication);}
                    	
                  
                    request.getSession().setAttribute(USER_BEAN_NAME, userAcnt);
                    return AccessDecisionVoter.ACCESS_GRANTED;
                //}
                //else
                  //  return AccessDecisionVoter.ACCESS_DENIED;
            }
            
            
        }

        return AccessDecisionVoter.ACCESS_DENIED;
    }

    /**
     * This is  a pretty strict format on the database for accessUrls and works only for that format, exceptions would occur if not used properly.
     * The format followed on the database side is :
     * type=query,fvc;status=new,updated;
     * @param allPermissions
     */
    private void populateDiscrepancyNoteStatus(List<Permissions>allPermissions,HttpServletRequest request,String matchUrl,String param){
   
        HashMap<String,List<String>> permitDiscrepancies = new HashMap<String,List<String>>();
        String tempType,tempStatus;
        StringTokenizer st,stTypes;
        String[] arry = new String[2];
        String[] discStatus = new String[2];
      
       for(Permissions permissions:allPermissions){
           if(permissions.getAccess_url().startsWith(matchUrl) && permissions.getAccess_parameters()!=null ){
           
               st = new StringTokenizer( permissions.getAccess_parameters().trim(),";");
           
           if(st.hasMoreTokens()){
               
                   tempType = st.nextToken();
                   arry = tempType.split("=");
                   tempStatus = st.nextToken();
                 
                      // discType = arry[0].split("=");
                       stTypes = new StringTokenizer(arry[1],",");
                       discStatus = tempStatus.split("=");
                       while(stTypes.hasMoreTokens()){
                           List<String> temp = new ArrayList<String>(); 
                           temp.addAll(Arrays.asList(discStatus[1].split(",")));
                           permitDiscrepancies.put(stTypes.nextToken(), temp);
                         //  temp.removeAll(Arrays.asList(discStatus[1].split(",")));
                       }
                       
                   //permitDiscrepancies.put(arry[0],Arrays.asList(tempStatus.split(",")));
                 
           }      
           }
       }
        request.getSession().setAttribute(param,permitDiscrepancies);
    }
    
    
    private void populateEventStatus(List<Permissions>allPermissions,HttpServletRequest request,String matchUrl,String param){
        
       // HashMap<String,List<String>> permitDiscrepancies = new HashMap<String,List<String>>();
        ArrayList<String> eventStatusList = new ArrayList<String>() ;
        String tempType;
        StringTokenizer st;
        String[] arry = new String[2];
        for(Permissions permissions:allPermissions){
           if(permissions.getAccess_url().startsWith(matchUrl) && permissions.getAccess_parameters()!=null ){
           
               st = new StringTokenizer( permissions.getAccess_parameters().trim(),";");
           
           if(st.hasMoreTokens()){
               
                   tempType = st.nextToken();
                   arry = tempType.split("=");
                  
                   StringTokenizer st2 = new StringTokenizer(arry[1],",");
                   while(st2.hasMoreTokens())
                   eventStatusList.add(st2.nextToken());
           }      
           }
       }
        request.setAttribute(param,eventStatusList);
    }
    
    /**
     * The links which are displayed on each page will check with this populated list to make sure that they have permission.
     * @param request
     * @param grantedPermissions
     * @param path TODO
     */
    private void populateStatusAndDoNotAccessList(HttpServletRequest request,
    		Collection<GrantedAuthority> grantedPermissions, String path, 
    		boolean isRecalculateUIParams, Authentication authentication){
      
        //TODO:cache the permissionDAO to avoid calling several times, putting in session might increase the foot print so cache it as read_only
        List<org.akaza.openclinica.domain.role.Permissions> allPermissions = getPermissionDao().findAll();
        List<String> allUrls = getUrls(allPermissions);
        logMe("allUrls size?"+allUrls.size());
        List<String> allPermitUrls = getPermitUrls(grantedPermissions);
        List<Permissions> permits = getPermissionObjects(allPermitUrls);
        if(path.contains("/ViewDiscrepancyNote")|| path.contains("/CreateDiscrepancyNote"))
        {
        populateDiscrepancyNoteStatus(permits,request,"/DiscrepancyNoteTypeStatus1",PERMIT_DISCREPANCIES);
        }
        else if(path.contains("UpdateStudyEvent")){
            populateEventStatus(permits,request,"/EventStatusUpdate",EVENT_STATUS);
        }
      //build page menu struc and put it in session this will be rebuild for login/change study/change study status
        /*ChangeStudy         */
        if (isRecalculateUIParams){
        	String menu_params=null;
        	for(Permissions permission:permits){
           		if(permission.getAccess_url().startsWith("/MenuSetting") && permission.getAccess_parameters()!=null ){
           			menu_params= permission.getAccess_parameters();
           			break;
           		}
        	}
        	PageMenu pageMenu = new PageMenu();
        	StudyBean study = (StudyBean)request.getSession().getAttribute("study");
            if ( study == null) {
            	 study = ((CustomUser)authentication.getPrincipal()).getStudyBean();
            } 
   			pageMenu.buildPageMenu(request, menu_params, allPermitUrls, study);
   			request.getSession().setAttribute(PageMenu.PAGE_MENU_UI_CONSTANT, pageMenu);
 	    }
	    
        
        boolean removed = allUrls.removeAll(allPermitUrls);
        logMe("removed:"+removed);
        logMe("allUrls size now::"+allUrls.size());
        request.getSession().setAttribute("notGrantedPermissions", allUrls);
        
        
	        
    }
    
    private void setupUserRoleDescriptions(HttpServletRequest request, UserAccount userAcnt){
    	//get locale role descriptions for  user and set current role
        Locale locale = request.getLocale();
        String role_i18n_description = null;
         
        for ( org.akaza.openclinica.domain.role.Role role : userAcnt.getRole()){
        	role_i18n_description = getLocalePropertyValueDao().getPropertyDescription(role.getRoleName(),  locale);
        	role.setI18nDescription(role_i18n_description);
        }
    }        	 
    private void setupUserCurrentRole(HttpServletRequest request, UserAccount userAcnt, Authentication authentication){        
        //set current role for user bean
        StudyBean study = (StudyBean)request.getSession().getAttribute("study");
        if ( study == null) {
        	 study = ((CustomUser)authentication.getPrincipal()).getStudyBean();
        }
        
        for ( GroupAuthDefinition usra : userAcnt.getGroupAuthDefinition()){
        	if (usra.getGroup_name()!= null && (usra.getGroup_name().equals("study") || usra.getGroup_name().equals("site"))){
        		if ( usra.getOc_oid_reference_list().equals(study.getOid() ))
        		{
        			for ( UserRoleAccess us : userAcnt.getUserRoleAccess()){
        				if (us.getGroup_id()!= null && us.getGroup_id().intValue()==usra.getId().intValue()){
        					userAcnt.setCurrentRole(us.getRole());
        					break;
        				}
        			}
        		}
        	}
        }
    }
    
    private List<Permissions> getPermissionObjects(List<String> allPermitUrls){
        List<Permissions> permit = new ArrayList<Permissions>();
        for(String temp:allPermitUrls){
          permit.addAll(  getPermissionDao().findAllByColumnName(temp, "access_url"));
        }
        return permit;
    }
    
    private List<String> getPermitUrls(Collection<GrantedAuthority> grantedPermissions){
        List<String> grantedUrls = new ArrayList<String>();
        String authUrl;
        for(GrantedAuthority ga:grantedPermissions){
            authUrl = ga.getAuthority();
            grantedUrls.add(authUrl);
            
        }
        return grantedUrls;
    }
    private List<String> getUrls(List<Permissions> allPermissions){
        List<String> allUrls = new ArrayList<String>();
        String permissionUrl;
        for(Permissions permission:allPermissions){
            permissionUrl = permission.getAccess_url();
            allUrls.add(permissionUrl);
        }
        return allUrls;
    }
    
    
    
    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
   

    public PermissionDao getPermissionDao() {
        return permissionDao;
    }

    public void setPermissionDao(PermissionDao permissionDao) {
        this.permissionDao = permissionDao;
    }
    
    
    private void logMe(String msg) {
        logger.debug(msg);
      //  System.out.println(msg);
    }

	/**
	 * @param localePropertyValueDao the localePropertyValueDao to set
	 */
	public void setLocalePropertyValueDao(LocalePropertyValueDao localePropertyValueDao) {
		this.localePropertyValueDao = localePropertyValueDao;
	}

	/**
	 * @return the localePropertyValueDao
	 */
	public LocalePropertyValueDao getLocalePropertyValueDao() {
		return localePropertyValueDao;
	}
}
