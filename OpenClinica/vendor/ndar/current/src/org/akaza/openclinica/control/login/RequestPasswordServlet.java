/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.login;

import java.util.Calendar;
import java.util.Date;

import javax.mail.MessagingException;

import org.akaza.openclinica.bean.login.PwdChallengeQuestion;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * @version CVS: $Id: RequestPasswordServlet.java,v 1.4 2005/02/21 17:54:32 jxu Exp $
 * 
 * Servlet of requesting password
 */
public class RequestPasswordServlet extends SecureController {
  
  public void mayProceed() throws InsufficientPermissionException {

  }

  public void processRequest() throws Exception {

    String action = request.getParameter("action");  
    session.setAttribute("challengeQuestions", PwdChallengeQuestion.toArrayList());

    if (StringUtil.isBlank(action)) {      
      request.setAttribute("userBean1", new UserAccountBean());
      forwardPage(Page.REQUEST_PWD);
    } else {
      if ("confirm".equalsIgnoreCase(action)) {
        confirmPassword();

      } else {
        request.setAttribute("userBean1", new UserAccountBean());
        forwardPage(Page.REQUEST_PWD);
      }
    }

  }

  /**
   * 
   * @param request
   * @param response
   */
  private void confirmPassword()  throws Exception {
    Validator v = new Validator(request);
    FormProcessor fp = new FormProcessor(request);
    v.addValidation("name", Validator.NO_BLANKS);  
    v.addValidation("email", Validator.IS_A_EMAIL);
    v.addValidation("passwdChallengeQuestion", Validator.NO_BLANKS);
    v.addValidation("passwdChallengeAnswer", Validator.NO_BLANKS);

    errors = v.validate();

    UserAccountBean ubForm = new UserAccountBean(); //user bean from web form
    ubForm.setName(fp.getString("name"));
    ubForm.setEmail(fp.getString("email"));
    ubForm.setPasswdChallengeQuestion(fp.getString("passwdChallengeQuestion"));
    ubForm.setPasswdChallengeAnswer(fp.getString("passwdChallengeAnswer")); 

    sm = new SessionManager(null, ubForm.getName());

    UserAccountDAO uDAO = new UserAccountDAO(sm.getDataSource());
    //see whether this user in the DB
    UserAccountBean ubDB = (UserAccountBean) uDAO.findByUserName(ubForm.getName());

    UserAccountBean updater = ubDB;

    request.setAttribute("userBean1", ubForm);
    if (!errors.isEmpty()) {
      logger.info("after processing form,has errors");
      request.setAttribute("formMessages", errors);
      forwardPage(Page.REQUEST_PWD);
    } else {
      logger.info("after processing form,no errors");
      //whether this user's email is in the DB
      if ((ubDB.getEmail() != null) && (ubDB.getEmail().equalsIgnoreCase(ubForm.getEmail()))) {
        logger.info("ubDB.getPasswdChallengeQuestion()" + ubDB.getPasswdChallengeQuestion());
        logger.info("ubForm.getPasswdChallengeQuestion()" + ubForm.getPasswdChallengeQuestion());
        logger.info("ubDB.getPasswdChallengeAnswer()" + ubDB.getPasswdChallengeAnswer());
        logger.info("ubForm.getPasswdChallengeAnswer()" + ubForm.getPasswdChallengeAnswer());

        //if this user's password challenge can be verified
        if (ubDB.getPasswdChallengeQuestion().equals(ubForm.getPasswdChallengeQuestion())
            && (ubDB.getPasswdChallengeAnswer().equalsIgnoreCase(ubForm.getPasswdChallengeAnswer()))) {

          String newPass = SecurityManager.getInstance().genPassword();
          String newDigestPass = SecurityManager.getInstance().encrytPassword(newPass);
          ubDB.setPasswd(newDigestPass);

          //passwdtimestamp should be null ,fix PrepareStatementFactory        
          Calendar cal = Calendar.getInstance();

          Date date = sdf.parse("01/01/1900");
          cal.setTime(date);
          ubDB.setPasswdTimestamp(cal.getTime());
          ubDB.setUpdater(updater);
          ubDB.setLastVisitDate(new Date());

          logger.info("user bean to be updated:" + ubDB.getId() + ubDB.getName()
              + ubDB.getActiveStudyId());

          uDAO.update(ubDB);        
          sendPassword(newPass,ubDB);
        } else {
          addPageMessage("Your Password Challenge could not be verified. "
              + "Please try again or contact the Administrative Contact.");
          forwardPage(Page.REQUEST_PWD);
        }

      } else {
        addPageMessage("Your email address could not be found in our database."
                + " Please try again or contact the Administrative Contact.");
        forwardPage(Page.REQUEST_PWD);
      }

    }

  }

  /**
   * Gets user basic info and set email to the administrator
   * 
   * @param request
   * @param response
   */
  private void sendPassword(String passwd,UserAccountBean ubDB) throws Exception {

    logger.info("Sending email...");
    EmailEngine em = new EmailEngine(EmailEngine.getSMTPHost());
    StringBuffer email = new StringBuffer("Hi " + ubDB.getFirstName() + ", <br>");
    String url = SQLInitServlet.getField("sysURL");
    if (url==null || "".equals(url)) {
    	boolean secureRequest=request.isSecure();
    	url=(secureRequest ? "https://" : "http://")+request.getServerName()+(((!secureRequest && request.getServerPort()==80) || (secureRequest && request.getServerPort()==443)) ? "" : ":"+request.getServerPort())+request.getContextPath()+"/MainMenu";
    }
    email.append(" This email is from OpenClinica admin.<br>").append(
        "Your password has been reset as:" + passwd).append(
        "<br>You will be required to change this password the next ").append(
        "time you login to the system. ").append(
          "Use the following link to log into OpenClinica:<br>").append(
        		url);
    String emailBody = email.toString();
    try {
    em.process(ubDB.getEmail().trim(), EmailEngine.getAdminEmail(), "your OpenClinica password",
        emailBody);
    addPageMessage("Your password has been reset and a new temporary "
        + "password has been emailed to the address on file. "
        + "You will be required to change this password the "
        + "next time you login to the system.");
  } catch (MessagingException me){
    addPageMessage("Your password cannot be sent due to mail server connection problem, please try again later.");
  }    
  
    session.removeAttribute("challengeQuestions");
    forwardPage(Page.LOGIN);

  }
}