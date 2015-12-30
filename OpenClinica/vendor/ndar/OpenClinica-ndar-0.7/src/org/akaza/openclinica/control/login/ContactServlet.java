/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.login;

import javax.mail.MessagingException;

import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * Sends user message to the administrator
 */
public class ContactServlet extends SecureController {
  public void mayProceed() throws InsufficientPermissionException {

  }

  public void processRequest() throws Exception {
    String action = request.getParameter("action");

    if (StringUtil.isBlank(action)) {
      if (ub != null && ub.getId() > 0) {
        request.setAttribute("name", ub.getName());
        request.setAttribute("email", ub.getEmail());
      }
      forwardPage(Page.CONTACT);
    } else {
      if ("submit".equalsIgnoreCase(action)) {
        Validator v = new Validator(request);
        v.addValidation("name", Validator.NO_BLANKS);
        v.addValidation("email", Validator.IS_A_EMAIL);
        v.addValidation("subject", Validator.NO_BLANKS);
        v.addValidation("message", Validator.NO_BLANKS);

        errors = v.validate();

        FormProcessor fp = new FormProcessor(request);
        if (!errors.isEmpty()) {
          request.setAttribute("name", fp.getString("name"));
          request.setAttribute("email", fp.getString("email"));
          request.setAttribute("subject", fp.getString("subject"));
          request.setAttribute("message", fp.getString("message"));
          request.setAttribute("formMessages", errors);
          forwardPage(Page.CONTACT);
        } else {
          sendEmail();
        }
      } else {
        if (ub != null && ub.getId() > 0) {
          request.setAttribute("name", ub.getName());
          request.setAttribute("email", ub.getEmail());
        }
        forwardPage(Page.CONTACT);
      }

    }

  }

  private void sendEmail() throws Exception {
    FormProcessor fp = new FormProcessor(request);
    String name = fp.getString("name");
    String email = fp.getString("email");
    String subject = fp.getString("subject");
    String message = fp.getString("message");

    logger.info("Sending email...");
    try {
      EmailEngine em = new EmailEngine(EmailEngine.getSMTPHost());
      StringBuffer emailBody = new StringBuffer("Dear OpenClinica Administrator, <br>");
      emailBody.append(name + " sent you the following message.<br>");
      emailBody.append("<br>Email: " + email);
      emailBody.append("<br>Subject:" + subject);
      emailBody.append("<br>Message:" + message);

      String emailBodyString = email.toString();

      em.process(EmailEngine.getAdminEmail(), email.trim(), "User Message", emailBodyString);

      addPageMessage("Your message has been sent successfully.");
    } catch (MessagingException me) {
      addPageMessage("Your message cannot be sent due to mail server connection problem, please try again later.");
    }
    if (ub != null && ub.getId() > 0) {
      forwardPage(Page.MENU_SERVLET);
    } else {
      forwardPage(Page.LOGIN);
    }
  }

}