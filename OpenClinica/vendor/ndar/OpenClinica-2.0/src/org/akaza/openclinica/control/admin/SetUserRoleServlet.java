/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.util.ArrayList;
import java.util.Date;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SetUserRoleServlet extends SecureController {
  /**
   *  
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }

    addPageMessage("You don't have correct privilege in your current active study. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.LIST_USER_ACCOUNTS_SERVLET, "not admin", "1");

  }

  public void processRequest() throws Exception {
    UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
    StudyDAO sdao = new StudyDAO(sm.getDataSource());
    FormProcessor fp = new FormProcessor(request);
    int userId = fp.getInt("userId");
    if (userId == 0) {
      addPageMessage("Please choose a user to set role for.");
      forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET);
    } else {
      String action = request.getParameter("action");
      UserAccountBean user = (UserAccountBean) udao.findByPK(userId);
      ArrayList studies = (ArrayList) sdao.findAll();
      ArrayList studiesHaveRole = (ArrayList) sdao.findAllByUser(user.getName());
      studies.removeAll(studiesHaveRole);
      ArrayList studiesNotHaveRole = new ArrayList();
      for (int i=0; i< studies.size();i++){
       StudyBean study1= (StudyBean)studies.get(i);
       
       //TODO: implement equal() according to id
       boolean hasStudy = false;
       for (int j=0;j<studiesHaveRole.size();j++){
         StudyBean study2= (StudyBean)studiesHaveRole.get(j);
         if (study2.getId() == study1.getId()) {
           hasStudy = true;
           break;
         }         
       }
       if (!hasStudy) {
         studiesNotHaveRole.add(study1);
       }
      }
      if ("confirm".equalsIgnoreCase(action)) {

        request.setAttribute("user", user);
        request.setAttribute("studies", studiesNotHaveRole);
        StudyUserRoleBean uRole = new StudyUserRoleBean();
        uRole.setFirstName(user.getFirstName());
        uRole.setLastName(user.getLastName());
        uRole.setUserName(user.getName());
        request.setAttribute("uRole", uRole);

        ArrayList roles = Role.toArrayList();
        roles.remove(Role.ADMIN); //admin is not a user role, only used for
        // tomcat
        request.setAttribute("roles", roles);

        forwardPage(Page.SET_USER_ROLE);
      } else {
        //set role
        String userName = fp.getString("name");
        int studyId = fp.getInt("studyId");
        StudyBean userStudy = (StudyBean) sdao.findByPK(studyId);
        int roleId = fp.getInt("roleId");
        //new user role
        StudyUserRoleBean sur = new StudyUserRoleBean();
        sur.setName(userName);
        sur.setRole(Role.get(roleId));
        sur.setStudyId(studyId);
        sur.setStudyName(userStudy.getName());
        sur.setStatus(Status.AVAILABLE);
        sur.setOwner(ub);
        sur.setCreatedDate(new Date());
       
        udao.createStudyUserRole(user, sur);
        
        addPageMessage(user.getFirstName() + " " + user.getLastName() + "(username: "
            + user.getName() + " ) has been granted the role " + sur.getRole().getName()
            + " in the Study/Site " + userStudy.getName() + ".");

        forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET);

      }

    }
  }

  protected String getAdminServlet() {
    return SecureController.ADMIN_SERVLET_CODE;
  }

}