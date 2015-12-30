package org.akaza.openclinica.web.filter;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.domain.role.GroupAuthDefinition;
import org.akaza.openclinica.domain.role.Role;
import org.akaza.openclinica.domain.role.UserAccount;
import org.akaza.openclinica.domain.role.UserRoleAccess;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

public class OpenClinicaGroupVoter implements AccessDecisionVoter  {
    public static final String ALLOWED_GROUPS = "ALLOWED_GROUPS";
    public static final String ALLOWED_GROUP_OIDS = "ALLOWED_GROUP_OIDS";
    public static final String GROUPS = "GROUPS";
    @Override
    public boolean supports(ConfigAttribute arg0) {
        return true;
    }

    @Override
    public boolean supports(Class<?> arg0) {
        return FilterInvocation.class.isAssignableFrom(arg0);
    }

    @Override
    public int vote(Authentication authentication, Object arg1, Collection<ConfigAttribute> arg2) {
        String path,authority;
        List<String> groupOids = new ArrayList<String>();
        FilterInvocation invocation = (FilterInvocation) arg1;
       
      //   userAcnt = (UserAccount) invocation.getHttpRequest().getAttribute(OpenClinicaURLVoter.USER_BEAN_NAME);
        // UserAccount userAcnt = ((CustomUser)authentication.getPrincipal()).getUserAccount();
         UserAccount userAcnt = (UserAccount) invocation.getHttpRequest().getSession().getAttribute(OpenClinicaURLVoter.USER_BEAN_NAME);
         
        HttpServletRequest request = invocation.getHttpRequest();
        String url = invocation.getRequestUrl();
        HttpServletRequest req = invocation.getHttpRequest();
        if (req.getPathInfo() != null) {
            path = req.getServletPath() + req.getPathInfo();
        } else {
            path = req.getServletPath();
        }
        
        OpenClinicaAuthDetails details = (OpenClinicaAuthDetails) authentication.getDetails();
    
        
        if(path.equals("/ChangeStudy")){
          //  req.getSession().setAttribute("userBeanURL", userAcnt);
            List<GroupAuthDefinition>groupAuths =  userAcnt.getGroupAuthDefinition();
            List<Role>roles = userAcnt.getRole();
            List<UserRoleAccess> userRoles = userAcnt.getUserRoleAccess();
            Set<GroupAuthDefinition>childernGroups;
            Iterator<GroupAuthDefinition> grpAuthItr;
            String tempStudyOID,tempSiteOID;

            List<GroupAuthDefinition> groups = new ArrayList<GroupAuthDefinition>();
            HashMap<GroupAuthDefinition,UserRoleAccess> roleGrpMap = new HashMap<GroupAuthDefinition,UserRoleAccess>();
            int index = 0;
            for(GroupAuthDefinition groupAuthDef:groupAuths){
              
                tempStudyOID = groupAuthDef.getOc_oid_reference_list();
                groupOids.add(tempStudyOID);
                childernGroups  = groupAuthDef.getChildernGroupList();
                groups.add(groupAuthDef);
                grpAuthItr = childernGroups.iterator();
                roleGrpMap.put(groupAuthDef,userRoles.get(index));
              index++;
                while(grpAuthItr.hasNext())
                {
                    GroupAuthDefinition childGroup = grpAuthItr.next();
                    tempSiteOID = childGroup.getOc_oid_reference_list();
                    groupOids.add( tempSiteOID);
                    groups.add(childGroup);
                   
                }
                  
                
            }
            
            request.setAttribute(ALLOWED_GROUP_OIDS, groupOids);
            request.setAttribute(GROUPS,groups);
            request.setAttribute(ALLOWED_GROUPS,roleGrpMap);
            
            
            /* if (!detailsChecker(url, details, request))
            {
                return AccessDecisionVoter.ACCESS_GRANTED;
            }
            else return AccessDecisionVoter.ACCESS_DENIED;*/
        }
        
        
        return AccessDecisionVoter.ACCESS_GRANTED;
    }
    private boolean detailsChecker(String url, OpenClinicaAuthDetails details, HttpServletRequest request) {
        FormProcessor fp = new FormProcessor(request);
        boolean restricted = false;
        
        if (url.equals("/ChangeStudy")) {

            int studyId = fp.getInt("studyId");
            if (studyId != 0) {
                StudyDAO studyDao = new StudyDAO(getDataSource());
                StudyBean studyBean = (StudyBean) studyDao.findByPK(studyId);
                String oid = studyBean.getOid();
                restricted = genericChecker(oid, details,restricted);
            }
        }
        return restricted;
    }

    private boolean genericChecker(String oid, OpenClinicaAuthDetails details,boolean restricted) {
         restricted = true;

        for (GroupAuthDefinition groupDef : details.getGroupAuthDef()) {
            String listOids = groupDef.getOc_oid_reference_list();
            StringTokenizer st = new StringTokenizer(listOids, ",");
            while (st.hasMoreTokens() && !restricted) {
                if (oid.equals(st.nextElement())) {
                    restricted = false;
                }
            }

        }

        return restricted;

    }
    private DataSource dataSource;
    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
}
