/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 
 * The main controller servlet for all the work behind study sites for OpenClinica.
 * 
 * @author jxu
 *  
 */
public class MainMenuServlet extends SecureController {
	Locale locale;
	//<  ResourceBundle respage;
	
  
  public void mayProceed() throws InsufficientPermissionException {
		locale = request.getLocale();
		//< respage = ResourceBundle.getBundle("org.akaza.openclinica.i18n.page_messages",locale);
  }

  public void processRequest() throws Exception {
    ub.incNumVisitsToMainMenu();
    session.setAttribute(USER_BEAN_NAME, ub);

    if (ub == null || ub.getId()==0) {//in case database connection is broken
      forwardPage(Page.MENU, false);
      return;
    }
    
    StudyDAO sdao = new StudyDAO(sm.getDataSource());
    ArrayList studies = null;    
    
    long pwdExpireDay = new Long(SQLInitServlet.getField("passwd_expiration_time")).longValue();
    Date lastPwdChangeDate = ub.getPasswdTimestamp();
    
    //a flag tells whether users are required to change pwd upon the first time log in or pwd expired
    int pwdChangeRequired = new Integer(SQLInitServlet.getField("change_passwd_required")).intValue();
    //update last visit date to current date
    UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
    UserAccountBean ub1 = (UserAccountBean)udao.findByPK(ub.getId());
    ub1.setLastVisitDate(new Date(System.currentTimeMillis()));
    //have to actually set the above to a timestamp? tbh
    udao.update(ub1);
    
    if (lastPwdChangeDate != null) {//not a new user
      Calendar cal = Calendar.getInstance();
      //compute difference between current date and lastPwdChangeDate
      long difference = Math.abs(cal.getTime().getTime() - lastPwdChangeDate.getTime());
      long days = difference / (1000 * 60 * 60 * 24);
      session.setAttribute("passwordExpired", "no");
      
      if (days > pwdExpireDay) {//password expired, need to be changed
        studies = (ArrayList) sdao.findAllByUser(ub.getName());
        request.setAttribute("studies", studies);
        session.setAttribute("userBean1", ub);
	addPageMessage(respage.getString("password_expired"));
        //YW 06-25-2007 << add the feature that if password is expired, have to go through /ResetPassword page
        session.setAttribute("passwordExpired", "yes");
        if (pwdChangeRequired ==1) {
        	request.setAttribute("mustChangePass", "yes");
        	addPageMessage("Your password has expired. You must change your password to login.");
        } else {
        	request.setAttribute("mustChangePass", "no");
        	addPageMessage("Your password has expired, please change your password for security reasons. If you do not want to change password, you can leave new password blank.");
        }
        forwardPage(Page.RESET_PASSWORD);
        //YW >>
      } else {
       if (ub.getNumVisitsToMainMenu() <= 1) {
         addPageMessage(respage.getString("welcome") + " " + ub.getFirstName() + " " + ub.getLastName()+ ". " +
        respage.getString("last_logged")+ " " +
        local_df.format(ub.getLastVisitDate()) + ". ");
       }
       
        forwardPage(Page.MENU);
      }

    } else {//a new user's first log in
      studies = (ArrayList) sdao.findAllByUser(ub.getName());
      request.setAttribute("studies", studies);
      session.setAttribute("userBean1", ub);
      addPageMessage(respage.getString("welcome") + " " + ub.getFirstName() + " " + 
          ub.getLastName()+ ". " + 
          respage.getString("password_set")+ " " +
          "<a href=\"UpdateProfile\">" +
          respage.getString("user_profile") + 
          " </a>");
      
      if (pwdChangeRequired ==1) { 
        request.setAttribute("mustChangePass", "yes");       
        forwardPage(Page.RESET_PASSWORD);
      } else {
        forwardPage(Page.MENU);
      }
    }

  }

}
