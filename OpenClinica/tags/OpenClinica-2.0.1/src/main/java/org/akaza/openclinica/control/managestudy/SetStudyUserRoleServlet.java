/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.core.*;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.core.form.FormProcessor;

import java.util.ArrayList;
import java.util.Date;

import javax.mail.MessagingException;

/**
 * @author jxu
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SetStudyUserRoleServlet extends SecureController {
  /**
   *  
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }

    if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
        || currentRole.getRole().equals(Role.COORDINATOR)) {
      return;
    }

    addPageMessage("You don't have correct privilege in your current active study. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.LIST_USER_IN_STUDY_SERVLET,
        "not study director", "1");

  }

  public void processRequest() throws Exception {
    UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
    StudyDAO sdao = new StudyDAO(sm.getDataSource());
    String name = request.getParameter("name");
    String studyIdString = request.getParameter("studyId");
    if (StringUtil.isBlank(name) || StringUtil.isBlank(studyIdString)) {
      addPageMessage("Please choose a user to set role for.");
      forwardPage(Page.LIST_USER_IN_STUDY_SERVLET);
    } else {
      String action = request.getParameter("action");
      FormProcessor fp = new FormProcessor(request);
      UserAccountBean user = (UserAccountBean) udao.findByUserName(name);
      StudyBean userStudy = (StudyBean)sdao.findByPK(fp.getInt("studyId"));
      if ("confirm".equalsIgnoreCase(action)) {
        int studyId = (Integer.valueOf(studyIdString.trim())).intValue();

        request.setAttribute("user", user);

        StudyUserRoleBean uRole = (StudyUserRoleBean) udao.findRoleByUserNameAndStudyId(name,
            studyId);
        uRole.setStudyName(userStudy.getName());
        request.setAttribute("uRole", uRole);

        ArrayList roles = Role.toArrayList();
        roles.remove(Role.ADMIN); //admin is not a user role, only used for
                                  // tomcat
        request.setAttribute("roles", roles);

        forwardPage(Page.SET_USER_ROLE_IN_STUDY);
      } else {
        //set role
        
        String userName = fp.getString("name");
        int studyId = fp.getInt("studyId");
        int roleId = fp.getInt("roleId");
        StudyUserRoleBean sur = new StudyUserRoleBean();
        sur.setName(userName);
        sur.setRole(Role.get(roleId));
        sur.setStudyId(studyId);
        sur.setStudyName(userStudy.getName());
        sur.setStatus(Status.AVAILABLE);
        sur.setUpdater(ub);
        sur.setUpdatedDate(new Date());
        udao.updateStudyUserRole(sur, userName);
        addPageMessage(sendEmail(user, sur));
        forwardPage(Page.LIST_USER_IN_STUDY_SERVLET);

      }

    }
  }

  /**
   * Send email to the user, director and administrator
   * 
   * @param request
   * @param response
   */
  private String sendEmail(UserAccountBean u, StudyUserRoleBean sub) throws Exception {

    StudyDAO sdao = new StudyDAO(sm.getDataSource());
    StudyBean study = (StudyBean) sdao.findByPK(sub.getStudyId());
    logger.info("Sending email...");
    String body = u.getFirstName() + " " + u.getLastName() + "(username: " + u.getName()
        + " ) has been granted the role " + sub.getRoleName() + " in the Study/Site "
        + study.getName() + ".";

    try {
      EmailEngine em = new EmailEngine(EmailEngine.getSMTPHost());
      //to user
      em.process(u.getEmail().trim(), EmailEngine.getAdminEmail(), "set user role", body);

      em = null;//otherwise keep sending emails??
      em = new EmailEngine(EmailEngine.getSMTPHost());
      //to study director
      em.process(ub.getEmail().trim(), EmailEngine.getAdminEmail(), "set user role", body);

      em = null;
      em = new EmailEngine(EmailEngine.getSMTPHost());
      //to admin
      em.process(EmailEngine.getAdminEmail(), EmailEngine.getAdminEmail(), "set user role", body);
    } catch (MessagingException me) {
      addPageMessage(" Mail cannot be sent to Administrator due to mail server connection problem.");
    }
    return body;

  }

}