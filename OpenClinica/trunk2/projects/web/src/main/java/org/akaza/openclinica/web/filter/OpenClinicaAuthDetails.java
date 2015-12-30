package org.akaza.openclinica.web.filter;

import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Set;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.dao.hibernate.UserAccountDao;
import org.akaza.openclinica.domain.role.GroupAuthDefinition;
import org.akaza.openclinica.domain.role.UserAccount;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class OpenClinicaAuthDetails extends WebAuthenticationDetails {

    /**
     * 
     */

    public static final String USER_BEAN_NAME = "userBean";
    private static final long serialVersionUID = 1L;
    private String requestURL= new String();
    public String getRequestURL() {
        return requestURL;
    }
    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }
    public OpenClinicaAuthDetails(HttpServletRequest request) {
        super(request);
      
    }
    @Override
    public void doPopulateAdditionalInformation(HttpServletRequest request){
        UserAccountBean ub = (UserAccountBean) request.getSession().getAttribute(USER_BEAN_NAME);
        
        UserAccountDao userAccountDao = (UserAccountDao)SpringServletAccess.getApplicationContext(request.getSession().getServletContext()).getBean(
        "userAccountDAO");
        
        UserAccount userAccount = (UserAccount)userAccountDao.findByColumnName((String)request.getAttribute("userNameForAuth"), "userName");
       setGroupAuthDef( userAccount.getGroupAuthDefinition());
       request.getSession().setAttribute(OpenClinicaURLVoter.USER_BEAN_NAME, userAccount);
       
    }
    private List<GroupAuthDefinition> groupAuthDef;
    
    public List<GroupAuthDefinition> getGroupAuthDef() {
        return groupAuthDef;
    }
    public void setGroupAuthDef(List<GroupAuthDefinition> groupAuthDefinition) {
     this.groupAuthDef = groupAuthDefinition;
        
    }
}
