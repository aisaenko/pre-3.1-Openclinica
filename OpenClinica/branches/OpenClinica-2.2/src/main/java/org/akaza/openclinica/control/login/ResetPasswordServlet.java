/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.login;

import java.util.ArrayList;
import java.util.Date;

import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * Reset expired password
 * 
 * @author ywang
 */
public class ResetPasswordServlet extends SecureController {

	public void mayProceed() throws InsufficientPermissionException {
	}

	/**
	 * Tasks include:
	 * <ol> 
	 * <li>Validation:
	 * <ol>
	 *  <li>1. old password match database record
	 *  <li>2. new password is different from old password
	 *  <li>3. new password satisfy required length and patterns
	 *  <li>4. two times entered passwords are same
	 *  <li>5. all required fields are filled
	 * </ol>
	 * <li>Update ub - UserAccountBean - in session and database
	 * </ol>
	 */
	public void processRequest() throws Exception {
		logger.info("Change expired password");
		
		UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
		Validator v = new Validator(request);
		errors.clear();
		FormProcessor fp = new FormProcessor(request);
		String mustChangePwd = request.getParameter("mustChangePwd");
		String newPwd = fp.getString("passwd");

		if("yes".equalsIgnoreCase(mustChangePwd)) {
			addPageMessage("Your password has expired. You must change your password to login.");
		}else {
        	addPageMessage("Your password has expired, please change your password for security reasons. If you do not want to change password, you can leave new password blank.");
		}
		request.setAttribute("mustChangePass", mustChangePwd);
		
		String oldPwd = fp.getString("oldPasswd").trim();
		String oldDigestPass = SecurityManager.getInstance().encrytPassword(oldPwd);
		if (!(oldDigestPass).equals(ub.getPasswd())) {
			Validator.addError(errors, "oldPasswd", "Wrong old password.");
			request.setAttribute("formMessages", errors);
			forwardPage(Page.RESET_PASSWORD);
		} else {
			if(mustChangePwd.equalsIgnoreCase("yes")){
				v.addValidation("passwd", Validator.NO_BLANKS);
				v.addValidation("passwd1", Validator.NO_BLANKS);
				v.addValidation("passwd", Validator.CHECK_DIFFERENT, "oldPasswd");
			}
			if (!StringUtil.isBlank(newPwd)) {
				v.addValidation("passwd", Validator.IS_A_PASSWORD);
				v.addValidation("passwd1", Validator.CHECK_SAME, "passwd");
			}
			errors = v.validate();
		
			if (!errors.isEmpty()) {
				logger.info("ResetPassword page has validation errors");
				request.setAttribute("formMessages", errors);
				forwardPage(Page.RESET_PASSWORD);
			} else {
				logger.info("ResetPassword page has no errors");
	
				if (!StringUtil.isBlank(newPwd)) {
					String newDigestPass = SecurityManager.getInstance().encrytPassword(newPwd);
					ub.setPasswd(newDigestPass);
					ub.setPasswdTimestamp(new Date());
				} else if("no".equalsIgnoreCase(mustChangePwd)){
					ub.setPasswdTimestamp(new Date());
				}
                ub.setOwner(ub);
                ub.setUpdater(ub);// when update ub, updator id is required
				udao.update(ub);
				
				ArrayList<String> pageMessages = new ArrayList<String>();
				request.setAttribute(PAGE_MESSAGE, pageMessages);
				addPageMessage("Your expired password has been reset successfully.");
				ub.incNumVisitsToMainMenu();
				forwardPage(Page.MENU);
			}
		}

	}

}