/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.util.Locale;
import java.util.ResourceBundle;
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
	
	//<  ResourceBundle restext;
	Locale locale;
	
	public static final String PATH = "DeleteUser";
	public static final String ARG_USERID = "userId";
	public static final String ARG_ACTION = "action";
	
	public static String getLink(UserAccountBean u, EntityAction action) {
		return PATH + "?" + ARG_USERID + "=" + u.getId() + "&" + "&" + ARG_ACTION + "=" + action.getId();
	}
	
	protected void mayProceed() throws InsufficientPermissionException {
		
		locale = request.getLocale();
		//< restext = ResourceBundle.getBundle("org.akaza.openclinica.i18n.notes",locale);
		
		if (!ub.isSysAdmin()) {
			throw new InsufficientPermissionException(Page.MENU, resexception.getString("you_may_not_perform_administrative_functions"), "1");
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
			message = respage.getString("the_specified_user_not_exits");
		}
		else if (!EntityAction.contains(action)) {
			message = respage.getString("the_specified_action_on_the_user_is_invalid");
		}
		else if (!EntityAction.get(action).equals(EntityAction.DELETE)
				&& !EntityAction.get(action).equals(EntityAction.RESTORE)) {
			message = respage.getString("the_specified_action_is_not_allowed");
		}
		else {
			EntityAction desiredAction = EntityAction.get(action);
			u.setUpdater(ub);
			
			if (desiredAction.equals(EntityAction.DELETE)) {
				udao.delete(u);
				
				if (udao.isQuerySuccessful()) {
					message = respage.getString("the_user_has_been_deleted");
					//YW 07-31-2007 << for feature that deletion doesn't need email the deleted user. 
					/*
					//YW 07-26-2007 << catch exception (eg. timeout) and inform users.
					try { 
						sendDeleteEmail(u); 
					} catch (Exception e) { 
						message += "  However, there has been an error sending the user an email regarding this deletion.";
					}
					*/
					//YW >>
				}
				else {
					message = respage.getString("the_user_could_not_be_deleted_due_database_error");
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
					message = respage.getString("the_user_has_been_restored");

					try {
						sendRestoreEmail(u, password);
					}
					catch (Exception e) {
						message += respage.getString("however_was_error_sending_user_email_regarding");
					}
				}
				else {
					message = respage.getString("the_user_could_not_be_deleted_due_database_error");
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

	    String body = resword.getString("dear") + u.getFirstName() + " " + u.getLastName() + ",\n";
	    body += restext.getString("your_account_has_been_restored_and_password_reset")+ ":\n\n";
	    body += resword.getString("user_name") + u.getName() + "\n";
	    body += resword.getString("password") + password + "\n\n";
	    body += restext.getString("please_test_your_login_information_and_let")+"\n";
	    body += restext.getString("openclinica_system_administrator");
	    
	    logger.info("Sending email...begin");
	    em.process(u.getEmail().trim(), EmailEngine.getAdminEmail(), restext.getString("your_new_openclinica_account_has_been_restored"), body);
	    logger.info("Sending email...done");
	}

	protected String getAdminServlet() {
		return SecureController.ADMIN_SERVLET_CODE;
	}
}
