/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.util.ArrayList;
import java.util.HashMap;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.TermType;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * Servlet for creating a user account.
 * 
 * @author ssachs
 */
public class CreateUserAccountServlet extends SecureController {
	public static final String INPUT_USERNAME = "userName";
	public static final String INPUT_FIRST_NAME = "firstName";
	public static final String INPUT_LAST_NAME = "lastName";
	public static final String INPUT_EMAIL = "email";
	public static final String INPUT_INSTITUTION = "institutionalAffiliation";
	public static final String INPUT_STUDY = "activeStudy";
	public static final String INPUT_ROLE = "role";
	public static final String INPUT_TYPE = "type";
	public static final String INPUT_DISPLAY_PWD = "displayPwd";
	
	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
	 */
	protected void mayProceed() throws InsufficientPermissionException {
		if (!ub.isSysAdmin()) {
			throw new InsufficientPermissionException(Page.MENU, "You may not perform administrative functions", "1");
		}
		
		return;
	}
	
	protected void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);

		StudyDAO sdao = new StudyDAO(sm.getDataSource());
		addEntityList("studies", sdao.findAll(), "A user cannot be created, because there is no study to set as an active study for the user.", Page.ADMIN_SYSTEM);
		addEntityList("roles", getRoles(), "A user cannot be created, because there are no roles to set as a role within the active study for the user.", Page.ADMIN_SYSTEM);
		
		ArrayList types=UserType.toArrayList();
		types.remove(UserType.INVALID);
		if (!ub.isTechAdmin()) {
			types.remove(UserType.TECHADMIN);
		}
		addEntityList("types", types, "A user cannot be created, because there are no user types within the active study for the user.", Page.ADMIN_SYSTEM);
		
		if (!fp.isSubmitted()) {		  
		  forwardPage(Page.CREATE_ACCOUNT);
		}
		else {
			UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
			Validator v = new Validator(request);
			
			// username must not be blank,
			// must be in the format specified by Validator.USERNAME,
			// and must be unique
			v.addValidation(INPUT_USERNAME, Validator.NO_BLANKS);
			v.addValidation(INPUT_USERNAME, Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 64);
			v.addValidation(INPUT_USERNAME, Validator.IS_A_USERNAME);
			
			v.addValidation(INPUT_USERNAME, Validator.USERNAME_UNIQUE, udao);
			
			v.addValidation(INPUT_FIRST_NAME, Validator.NO_BLANKS);
			v.addValidation(INPUT_LAST_NAME, Validator.NO_BLANKS);
			v.addValidation(INPUT_FIRST_NAME, Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 50);
			v.addValidation(INPUT_LAST_NAME, Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 50);
			
			v.addValidation(INPUT_EMAIL, Validator.NO_BLANKS);
			v.addValidation(INPUT_EMAIL, Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 120);
			v.addValidation(INPUT_EMAIL, Validator.IS_A_EMAIL);
			
			v.addValidation(INPUT_INSTITUTION, Validator.NO_BLANKS);
			v.addValidation(INPUT_INSTITUTION, Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
			
			v.addValidation(INPUT_STUDY, Validator.ENTITY_EXISTS, sdao);
			v.addValidation(INPUT_ROLE, Validator.IS_VALID_TERM, TermType.ROLE);
			
			HashMap errors = v.validate();
			
			if (errors.isEmpty()) {
				UserAccountBean createdUserAccountBean = new UserAccountBean();
				createdUserAccountBean.setName(fp.getString(INPUT_USERNAME));
				createdUserAccountBean.setFirstName(fp.getString(INPUT_FIRST_NAME));
				createdUserAccountBean.setLastName(fp.getString(INPUT_LAST_NAME));
				createdUserAccountBean.setEmail(fp.getString(INPUT_EMAIL));
				createdUserAccountBean.setInstitutionalAffiliation(fp.getString(INPUT_INSTITUTION));
				
				SecurityManager secm = SecurityManager.getInstance();
				String password = secm.genPassword();
				String passwordHash = secm.encrytPassword(password);
				
				createdUserAccountBean.setPasswd(passwordHash);
				
				createdUserAccountBean.setPasswdTimestamp(null);
				createdUserAccountBean.setLastVisitDate(null);
				
				createdUserAccountBean.setStatus(Status.AVAILABLE);
				createdUserAccountBean.setPasswdChallengeQuestion("");
				createdUserAccountBean.setPasswdChallengeAnswer("");
				createdUserAccountBean.setPhone("");
				createdUserAccountBean.setOwner(ub);
				
				int studyId = fp.getInt(INPUT_STUDY);
				Role r = Role.get(fp.getInt(INPUT_ROLE));
				createdUserAccountBean = addActiveStudyRole(createdUserAccountBean, studyId, r);
				UserType type = UserType.get(fp.getInt("type"));
				logger.warning("*** found type: "+fp.getInt("type"));
				logger.warning("*** setting type: "+type.getDescription());
				createdUserAccountBean.addUserType(type);
				createdUserAccountBean = (UserAccountBean) udao.create(createdUserAccountBean);
				String displayPwd = fp.getString(INPUT_DISPLAY_PWD);
				
				if (createdUserAccountBean.isActive()) {
					addPageMessage("The user account \"" + createdUserAccountBean.getName() + "\" was created successfully. ");
					if ("no".equalsIgnoreCase(displayPwd)){
					  try {
						sendNewAccountEmail(createdUserAccountBean, password);
					  }
					  catch (Exception e) {
						addPageMessage("There was an error sending the account-creating email.  Please contact the user directly regarding account creation.  You may reset the user's password by editing the user's account.");
					  }
					} else {
					  addPageMessage("User Password: " + password+ ". Please write down the password and provide it directly to the user.");
					}
				}
				else {
					addPageMessage("The user account \"" + createdUserAccountBean.getName() + "\" could not be created due to a database error.");
				}
				if (createdUserAccountBean.isActive()) {
				  request.setAttribute(ViewUserAccountServlet.ARG_USER_ID, new Integer(createdUserAccountBean.getId()).toString());
				  forwardPage(Page.VIEW_USER_ACCOUNT_SERVLET);
				} else {
				 forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET);
				}
			}
			else {
				String textFields[] = { INPUT_USERNAME, INPUT_FIRST_NAME, INPUT_LAST_NAME, INPUT_EMAIL, INPUT_INSTITUTION,INPUT_DISPLAY_PWD };
				fp.setCurrentStringValuesAsPreset(textFields);
				
				String ddlbFields[] = { INPUT_STUDY, INPUT_ROLE,INPUT_TYPE };
				fp.setCurrentIntValuesAsPreset(ddlbFields);
				
				HashMap presetValues = fp.getPresetValues();
				setPresetValues(presetValues);
				
				setInputMessages(errors);
				addPageMessage("There were some errors in your submission.  See below for details.");
				
				forwardPage(Page.CREATE_ACCOUNT);
			}
		}
	}
	
	private ArrayList getRoles() {
		
		ArrayList roles = Role.toArrayList();
		roles.remove(Role.ADMIN);
		
		return roles;
	}
	
	private UserAccountBean addActiveStudyRole(UserAccountBean createdUserAccountBean, int studyId, Role r) {
		createdUserAccountBean.setActiveStudyId(studyId);
		
		StudyUserRoleBean activeStudyRole = new StudyUserRoleBean();
		
		activeStudyRole.setStudyId(studyId);
		activeStudyRole.setRoleName(r.getName());
		activeStudyRole.setStatus(Status.AVAILABLE);
		activeStudyRole.setOwner(ub);
		
		createdUserAccountBean.addRole(activeStudyRole);

		return createdUserAccountBean;
	}

	private void sendNewAccountEmail(UserAccountBean createdUserAccountBean, String password) throws Exception {
		logger.info("Sending account creation notification to " + createdUserAccountBean.getName());
		EmailEngine em = new EmailEngine(EmailEngine.getSMTPHost());
		
		String body = "Dear " + createdUserAccountBean.getFirstName() + " " + createdUserAccountBean.getLastName() + ",\n\n";
		body += "A new user account has been created for you on the OpenClinica system. \n\n";
		body += "Your login information follows:\n\n";
		body += "Username: " + createdUserAccountBean.getName() + "\n";
		body += "Password: " + password + "\n\n";
		//body += "Please test your login information and let us know if you have any problems.\n";
		//body += "OpenClinica System Administrator";
		body += "Please go to the URL below to login. You may need to change your password upon login.\n\n";
		body += SQLInitServlet.getField("sysURL") + "\n\n";
		body += "Regards,\n";
		body += "OpenClinica System Administrator";
		
		logger.info("Sending email...begin");
		em.process(createdUserAccountBean.getEmail().trim(), EmailEngine.getAdminEmail(), "Your New OpenClinica Account", body);
		logger.info("Sending email...done");
	}
	
	protected String getAdminServlet() {
		return SecureController.ADMIN_SERVLET_CODE;
	}
}