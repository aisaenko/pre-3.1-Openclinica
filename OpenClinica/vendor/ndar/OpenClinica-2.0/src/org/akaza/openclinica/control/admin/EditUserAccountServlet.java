/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.util.logging.Level;

import org.akaza.openclinica.bean.login.*;
import org.akaza.openclinica.control.core.*;
import org.akaza.openclinica.view.*;
import org.akaza.openclinica.exception.*;

import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.core.SessionManager;

import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.core.*;

import org.akaza.openclinica.core.form.*;
import org.akaza.openclinica.bean.core.*;

import java.util.*;

/**
 * @author ssachs
 * 
 * Servlet for creating a user account.
 */
public class EditUserAccountServlet extends SecureController {
  public static final String INPUT_FIRST_NAME = "firstName";

  public static final String INPUT_LAST_NAME = "lastName";

  public static final String INPUT_EMAIL = "email";

  public static final String INPUT_INSTITUTION = "institutionalAffiliation";

  public static final String INPUT_RESET_PASSWORD = "resetPassword";

  public static final String INPUT_USER_TYPE = "userType";

  public static final String INPUT_CONFIRM_BUTTON = "submit";

  public static final String INPUT_DISPLAY_PWD = "displayPwd";

  public static final String PATH = "EditUserAccount";

  public static final String ARG_USERID = "userId";

  public static final String ARG_STEPNUM = "stepNum";

  // possible values of ARG_STEPNUM
  public static final int EDIT_STEP = 1;

  public static final int CONFIRM_STEP = 2;

  // possible values of INPUT_CONFIRM_BUTTON
  public static final String BUTTON_CONFIRM_VALUE = "Confirm";

  public static final String BUTTON_BACK_VALUE = "Back";

  private ArrayList getAllStudies() {
    StudyDAO sdao = new StudyDAO(sm.getDataSource());
    return (ArrayList) sdao.findAll();
  }

  public static String getLink(int userId) {
    return PATH + '?' + ARG_USERID + '=' + userId;
  }

  protected void mayProceed() throws InsufficientPermissionException {
    if (!ub.isSysAdmin()) {
      throw new InsufficientPermissionException(Page.MENU,
          "You may not perform administrative functions", "1");
    }

    return;
  }

  protected void processRequest() throws Exception {
    FormProcessor fp = new FormProcessor(request);

    // because we need to use this in the confirmation and error parts too
    ArrayList studies = getAllStudies();
    request.setAttribute("studies", studies);

    int userId = fp.getInt(ARG_USERID);
    UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
    UserAccountBean user = (UserAccountBean) udao.findByPK(userId);

    int stepNum = fp.getInt(ARG_STEPNUM);

    if (!fp.isSubmitted()) {
      addEntityList("userTypes", getUserTypes(),
    	          "The user could not be edited because there are no user types available.",
    	          Page.ADMIN_SYSTEM);
      loadPresetValuesFromBean(fp, user);
      fp.addPresetValue(ARG_STEPNUM, EDIT_STEP);
      setPresetValues(fp.getPresetValues());

      
//      addEntityList("userTypes", getUserTypes(),
//          "The user could not be edited because there are no user types available.",
//          Page.ADMIN_SYSTEM);
      forwardPage(Page.EDIT_ACCOUNT);
    } else if (stepNum == EDIT_STEP) {
      Validator v = new Validator(request);

      v.addValidation(INPUT_FIRST_NAME, Validator.NO_BLANKS);
      v.addValidation(INPUT_LAST_NAME, Validator.NO_BLANKS);
      
      v.addValidation(INPUT_FIRST_NAME, Validator.LENGTH_NUMERIC_COMPARISON,
          NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 50);
	  v.addValidation(INPUT_LAST_NAME, Validator.LENGTH_NUMERIC_COMPARISON, 
	      NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 50);

      v.addValidation(INPUT_EMAIL, Validator.NO_BLANKS);
      v.addValidation(INPUT_EMAIL, Validator.LENGTH_NUMERIC_COMPARISON,
          NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 120);
      v.addValidation(INPUT_EMAIL, Validator.IS_A_EMAIL);

      v.addValidation(INPUT_INSTITUTION, Validator.NO_BLANKS);
      v.addValidation(INPUT_INSTITUTION, Validator.LENGTH_NUMERIC_COMPARISON, 
          NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);

      HashMap errors = v.validate();

      if (errors.isEmpty()) {
        loadPresetValuesFromForm(fp);
        fp.addPresetValue(ARG_STEPNUM, CONFIRM_STEP);

        setPresetValues(fp.getPresetValues());
        forwardPage(Page.EDIT_ACCOUNT_CONFIRM);

      } else {
        loadPresetValuesFromForm(fp);
        fp.addPresetValue(ARG_STEPNUM, EDIT_STEP);
        setInputMessages(errors);

        setPresetValues(fp.getPresetValues());
        addEntityList("userTypes", getUserTypes(),
            "The user could not be edited because there are no user types available.",
            Page.ADMIN_SYSTEM);

        addPageMessage("There were some errors in your submission.  See below for details.");
        forwardPage(Page.EDIT_ACCOUNT);
      }
    } else if (stepNum == CONFIRM_STEP) {
      String button = fp.getString(INPUT_CONFIRM_BUTTON);

      if (button.equals(BUTTON_BACK_VALUE)) {
        loadPresetValuesFromForm(fp);
        fp.addPresetValue(ARG_STEPNUM, EDIT_STEP);

        addEntityList("userTypes", getUserTypes(),
            "The user could not be edited because there are no user types available.",
            Page.ADMIN_SYSTEM);
        setPresetValues(fp.getPresetValues());
        forwardPage(Page.EDIT_ACCOUNT);
      } else if (button.equals(BUTTON_CONFIRM_VALUE)) {
        user.setFirstName(fp.getString(INPUT_FIRST_NAME));
        user.setLastName(fp.getString(INPUT_LAST_NAME));
        user.setEmail(fp.getString(INPUT_EMAIL));
        user.setInstitutionalAffiliation(fp.getString(INPUT_INSTITUTION));
        user.setUpdater(ub);

        UserType ut = UserType.get(fp.getInt(INPUT_USER_TYPE));
        if (ut.equals(UserType.SYSADMIN)) {
          user.addUserType(ut);
        }
        else if (ut.equals(UserType.TECHADMIN)) {
          user.addUserType(ut);
        } else {
          user.addUserType(UserType.USER);
        }

        if (fp.getBoolean(INPUT_RESET_PASSWORD)) {
          SecurityManager sm = SecurityManager.getInstance();
          String password = sm.genPassword();
          String passwordHash = sm.encrytPassword(password);

          user.setPasswd(passwordHash);
          user.setPasswdTimestamp(null);

          udao.update(user);
          if ("no".equalsIgnoreCase(fp.getString(INPUT_DISPLAY_PWD))) {
            logger.info("displayPwd is no");
            try {
              sendResetPasswordEmail(user, password);
            } catch (Exception e) {
              addPageMessage("There was an error sending the reset-password email.  Please try to reset the password again.");
            }
          } else {
            addPageMessage("New User Password: " + password + " Please write down the password and provide it directly to the user.");
          }
        } else {
          udao.update(user);
        }

        addPageMessage(" The user account \"" + user.getName() + "\" was updated successfully.");
        forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET);
      } else {
        throw new InconsistentStateException(Page.ADMIN_SYSTEM,
            "An invalid submit button was clicked while editing a user.");
      }
    } else {
      throw new InconsistentStateException(Page.ADMIN_SYSTEM,
          "An invalid step was specified while editing a user.");
    }
  }

  //	public void processRequest(HttpServletRequest request, HttpServletResponse
  // response)
  //	throws OpenClinicaException {
  //		session = request.getSession();
  //		session.setMaxInactiveInterval(60 * 60 * 3);
  //		logger.setLevel(Level.ALL);
  //		UserAccountBean ub = (UserAccountBean) session.getAttribute("userBean");
  //		try {
  //			String userName = request.getRemoteUser();
  //			
  //			sm = new SessionManager(ub, userName);
  //			ub = sm.getUserBean();
  //			if (logger.isLoggable(Level.INFO)) {
  //				logger.info("user bean from DB" + ub.getName());
  //			}
  //			
  //			SQLFactory factory = SQLFactory.getInstance();
  //			UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
  //			
  //			HashMap presetValues;
  //		} catch (Exception e) {
  //			e.printStackTrace();
  //			logger.warning("OpenClinicaException:: OpenClinica.control.editUserAccount:
  // " + e.getMessage());
  //			
  //			forwardPage(Page.ERROR, request, response);
  //		}
  //	}

  private void loadPresetValuesFromBean(FormProcessor fp, UserAccountBean user) {
    fp.addPresetValue(INPUT_FIRST_NAME, user.getFirstName());
    fp.addPresetValue(INPUT_LAST_NAME, user.getLastName());
    fp.addPresetValue(INPUT_EMAIL, user.getEmail());
    fp.addPresetValue(INPUT_INSTITUTION, user.getInstitutionalAffiliation());
    int userTypeId = UserType.USER.getId();
    if (user.isTechAdmin()) {
    	userTypeId = UserType.TECHADMIN.getId();
    } else if (user.isSysAdmin()) {
    	userTypeId = UserType.SYSADMIN.getId();
    }
    //int userTypeId = user.isSysAdmin() ? UserType.SYSADMIN.getId() : UserType.USER.getId();
    fp.addPresetValue(INPUT_USER_TYPE, userTypeId);
    fp.addPresetValue(ARG_USERID, user.getId());
  }

  private void loadPresetValuesFromForm(FormProcessor fp) {
    fp.clearPresetValues();

    String textFields[] = { ARG_USERID, INPUT_FIRST_NAME, INPUT_LAST_NAME, INPUT_EMAIL,
        INPUT_INSTITUTION, INPUT_DISPLAY_PWD };
    fp.setCurrentStringValuesAsPreset(textFields);

    String ddlbFields[] = { INPUT_USER_TYPE, INPUT_RESET_PASSWORD };
    fp.setCurrentIntValuesAsPreset(ddlbFields);

    //		String chkFields[] = { };
    //		fp.setCurrentBoolValuesAsPreset(chkFields);
  }

  private ArrayList getUserTypes() {
    
    ArrayList types = UserType.toArrayList();
    types.remove(UserType.INVALID);
	if (!ub.isTechAdmin()) {
		types.remove(UserType.TECHADMIN);
	}
	return types;
  }
  

  private void sendResetPasswordEmail(UserAccountBean user, String password) throws Exception {
    logger.info("Sending password reset notification to " + user.getName());
    EmailEngine em = new EmailEngine(EmailEngine.getSMTPHost());

    String body = "Dear " + user.getFirstName() + " " + user.getLastName() + ",\n";
    body += "Your password has been reset on the OpenClinica system.  Your login information follows:\n\n";
    body += "Username: " + user.getName() + "\n";
    body += "Password: " + password + "\n\n";
    body += "Please test your login information and let us know if you have any problems.\n";
    body += "OpenClinica System Administrator";

    logger.info("Sending email...begin");
    em.process(user.getEmail().trim(), EmailEngine.getAdminEmail(),
        "Your OpenClinica account password has been reset", body);
    logger.info("Sending email...done");
  }

  protected String getAdminServlet() {
    return SecureController.ADMIN_SERVLET_CODE;
  }
}