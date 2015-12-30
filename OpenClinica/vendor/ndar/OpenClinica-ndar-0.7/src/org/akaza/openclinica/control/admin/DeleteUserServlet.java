/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.*;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.*;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.exception.*;
import org.akaza.openclinica.view.Page;

// allows both deletion and restoration of a study user role

public class DeleteUserServlet extends SecureController {
	public static final String PATH = "DeleteUser";
	public static final String ARG_USERID = "userId";
	public static final String ARG_ACTION = "action";
	
	public static String getLink(UserAccountBean u, EntityAction action) {
		return PATH + "?" + ARG_USERID + "=" + u.getId() + "&" + "&" + ARG_ACTION + "=" + action.getId();
	}
	
	protected void mayProceed() throws InsufficientPermissionException {
		if (!ub.isSysAdmin()) {
			throw new InsufficientPermissionException(Page.MENU, "You may not perform administrative functions", "1");
		}
		
		return;
	}
	
	protected void processRequest() throws Exception {
		UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
		
		FormProcessor fp = new FormProcessor(request);
		int userId = fp.getInt(ARG_USERID);
		int action = fp.getInt(ARG_ACTION);
		
		UserAccountBean u = (UserAccountBean) udao.findByPK(userId);
		
		String message;
		if (!u.isActive()) {
			message = "The specified user does not exist.";
		}
		else if (!EntityAction.contains(action)) {
			message = "The specified action on the user is invalid.";
		}
		else if (!EntityAction.get(action).equals(EntityAction.DELETE)
				&& !EntityAction.get(action).equals(EntityAction.RESTORE)) {
			message = "The specified action is not allowed.";
		}
		else {
			EntityAction desiredAction = EntityAction.get(action);
			u.setUpdater(ub);
			
			if (desiredAction.equals(EntityAction.DELETE)) {
				udao.delete(u);
				
				if (udao.isQuerySuccessful()) {
					try { sendDeleteEmail(u); } catch (Exception e) { }
					message = "The user has been deleted.";
				}
				else {
					message = "The user could not be deleted due to a database error.";
				}
			}
			else {
				SecurityManager sm = SecurityManager.getInstance();
				String password = sm.genPassword();
				String passwordHash = sm.encrytPassword(password);

				u.setPasswd(passwordHash);
				u.setPasswdTimestamp(null);

				udao.restore(u);
				
				if (udao.isQuerySuccessful()) {
					message = "The user has been restored.";

					try {
						sendRestoreEmail(u, password);
					}
					catch (Exception e) {
						message += "  However, there has been an error sending the user an email regarding the user's new password.  Please reset the user's password by editing the user's account.";
					}
				}
				else {
					message = "The user could not be restored due to a database error.";
				}
			}
		}
		
		addPageMessage(message);
		forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET);
	}	
	
	
	private void sendDeleteEmail(UserAccountBean u) throws Exception {
	    logger.info("Sending delete notification to " + u.getName());
	    EmailEngine em = new EmailEngine(EmailEngine.getSMTPHost());

	    String body = "Dear " + u.getFirstName() + " " + u.getLastName() + ",\n";
	    body += "Your account has been deleted from the OpenClinica system.  Please contact the system administrator if this was a mistake.\n\n";
	    body += "OpenClinica System Administrator";
	    
	    logger.info("Sending email...begin");
	    em.process(u.getEmail().trim(), EmailEngine.getAdminEmail(), "Your OpenClinica account has been deleted", body);
	    logger.info("Sending email...done");
	}
	
	private void sendRestoreEmail(UserAccountBean u, String password) throws Exception {
	    logger.info("Sending restore and password reset notification to " + u.getName());
	    EmailEngine em = new EmailEngine(EmailEngine.getSMTPHost());

	    String body = "Dear " + u.getFirstName() + " " + u.getLastName() + ",\n";
	    body += "Your account on the OpenClinica system has been restored, and your password has been reset.  Your login information follows:\n\n";
	    body += "Username: " + u.getName() + "\n";
	    body += "Password: " + password + "\n\n";
	    body += "Please test your login information and let us know if you have any problems.\n";
	    body += "OpenClinica System Administrator";
	    
	    logger.info("Sending email...begin");
	    em.process(u.getEmail().trim(), EmailEngine.getAdminEmail(), "Your OpenClinica account has been restored", body);
	    logger.info("Sending email...done");
	}

	protected String getAdminServlet() {
		return SecureController.ADMIN_SERVLET_CODE;
	}
}