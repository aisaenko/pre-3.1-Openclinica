/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;

import javax.mail.MessagingException;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.TermType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * @version CVS: $Id: RequestAccountServlet.java 9018 2007-01-23 19:33:47Z ywang $
 * 
 * Processes request of 'request a user account'
 */
public class RequestAccountServlet extends SecureController {
  //private UserAccountBean ubForm = new UserAccountBean();
  
  public void mayProceed() throws InsufficientPermissionException {

  }

  public void processRequest() throws Exception {
   
    String action = request.getParameter("action");   
      
    StudyDAO sdao = new StudyDAO(sm.getDataSource());
    ArrayList studies  = (ArrayList)sdao.findAll(); 
    ArrayList roles = Role.toArrayList();
    roles.remove(Role.ADMIN); //admin is not a user role, only used for tomcat
    
    request.setAttribute("roles", roles);
    request.setAttribute("studies",studies);

    if (StringUtil.isBlank(action)) {   
   
      session.setAttribute("newUserBean", new UserAccountBean());
     
     
      forwardPage(Page.REQUEST_ACCOUNT);
    } else {
      if ("confirm".equalsIgnoreCase(action)) {
        confirmAccount();

      } else if ("submit".equalsIgnoreCase(action)) {
        submitAccount();
      } else {       
        logger.info("here...");
        forwardPage(Page.REQUEST_ACCOUNT);
      }
    }

  }

  /**
   * 
   * @param request
   * @param response
   */
  private void confirmAccount() throws Exception {
    Validator v = new Validator(request);
    v.addValidation("name", Validator.NO_BLANKS);
    v.addValidation("firstName", Validator.NO_BLANKS);
    v.addValidation("lastName", Validator.NO_BLANKS);
    v.addValidation("email", Validator.IS_A_EMAIL);
    v.addValidation("email2", Validator.CHECK_SAME, "email");
    v.addValidation("institutionalAffiliation", Validator.NO_BLANKS);
    v.addValidation("activeStudyId", Validator.IS_AN_INTEGER);
    v.addValidation("activeStudyRole", Validator.IS_VALID_TERM, TermType.ROLE);

    HashMap errors = v.validate();

    FormProcessor fp = new FormProcessor(request);
   
    UserAccountBean ubForm = getUserBean();
    request.setAttribute("otherStudy", fp.getString("otherStudy"));
    session.setAttribute("newUserBean", ubForm);

    if (!errors.isEmpty()) {
      logger.info("after processing form,error is not empty");
      request.setAttribute("formMessages", errors);
      forwardPage(Page.REQUEST_ACCOUNT);

    } else {
      logger.info("after processing form,no errors");
      
      sm = new SessionManager(null, ubForm.getName());
      //see whether this user already in the DB
      UserAccountBean ubDB = sm.getUserBean();
     
      if (StringUtil.isBlank(ubDB.getName())) {
        StudyDAO sdao= new StudyDAO(sm.getDataSource());
        StudyBean study = (StudyBean)sdao.findByPK(ubForm.getActiveStudyId());
        String studyName = study.getName();
        request.setAttribute("studyName", studyName);
        forwardPage(Page.REQUEST_ACCOUNT_CONFIRM);
      } else {

        addPageMessage("Your user name has been used by other users. Please try another name.");
        forwardPage(Page.REQUEST_ACCOUNT);
      }

    }

  }

  /**
   * Gets user basic info and set email to the administrator
   * 
   * @param request
   * @param response
   */
  private void submitAccount() throws Exception { 
    String otherStudy = (String) request.getParameter("otherStudy");
    String studyName = (String) request.getParameter("studyName");
    UserAccountBean ubForm = (UserAccountBean)session.getAttribute("newUserBean");
    logger.info("Sending email..." );
    EmailEngine em = new EmailEngine(EmailEngine.getSMTPHost());
    //YW << <<
    StringBuffer email = new StringBuffer("From: " + ubForm.getEmail() + "\n");
    email.append("Sent: " + new Date() + "\n");
    email.append("To: " + SQLInitServlet.getField("adminEmail") + "\n");
    email.append("Subject: Request Account\n\n\n");
    email.append("Dear Admin, \n\n");
    email.append(ubForm.getFirstName() + " is requesting for an account on the OpenClinica system running at " 
    		     + SQLInitServlet.getField("sysURL") + ". \n\n");
    email.append("His/her information is shown as follows: \n\n");
    email.append("Name: " + ubForm.getFirstName() + " " + ubForm.getLastName() + "\n");
    email.append("UserName: " + ubForm.getName()+ "\n");
    email.append("Email: " + ubForm.getEmail()+ "\n");
    email.append("Institutional Affiliation: " + ubForm.getInstitutionalAffiliation()+ "\n\n");
    email.append("Default Active Study: " + studyName + ". ID: " + ubForm.getActiveStudyId() );
    email.append("\nUser Role Requested: " + ubForm.getActiveStudyRoleName()+ "\n\n");
    email.append("Other study: " + otherStudy);
    String emailBody = email.toString();
    //YW >>
    logger.info("Sending email...begin" + emailBody);
    try {
    em.process(EmailEngine.getAdminEmail(), ubForm.getEmail().trim(), "request account",
        emailBody);
    logger.info("Sending email...done");

    addPageMessage("Your request has been sent successfully.");
    } catch (MessagingException me){
      addPageMessage("Your request cannot be sent due to mail server connection problem, please try again later.");
    }    
    session.removeAttribute("newUserBean");
    forwardPage(Page.LOGIN);
  }

  /**
   * Constructs userbean from request
   * 
   * @param request
   * @return
   */
  private UserAccountBean getUserBean() {
    FormProcessor fp = new FormProcessor(request);

    UserAccountBean ubForm = new UserAccountBean();
    ubForm.setName(fp.getString("name"));
    ubForm.setFirstName(fp.getString("firstName"));
    ubForm.setLastName(fp.getString("lastName"));
    ubForm.setEmail(fp.getString("email"));
    ubForm.setInstitutionalAffiliation(fp.getString("institutionalAffiliation"));
    ubForm.setActiveStudyId(fp.getInt("activeStudyId"));
    StudyUserRoleBean uRole = new StudyUserRoleBean();
    uRole.setStudyId(fp.getInt("activeStudyId"));
    uRole.setRole(Role.get(fp.getInt("activeStudyRole")));
    ubForm.addRole(uRole);
    return ubForm;

  }

}