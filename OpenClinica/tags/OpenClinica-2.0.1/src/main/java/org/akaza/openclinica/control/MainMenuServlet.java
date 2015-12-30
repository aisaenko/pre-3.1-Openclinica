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

/**
 * 
 * The main controller servlet for all the work behind study sites for OpenClinica.
 * 
 * @author jxu
 *  
 */
public class MainMenuServlet extends SecureController {
  
  public void mayProceed() throws InsufficientPermissionException {

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

      if (days > pwdExpireDay) {//password expired, need to be changed
        studies = (ArrayList) sdao.findAllByUser(ub.getName());
        request.setAttribute("studies", studies);
        session.setAttribute("userBean1", ub);
        addPageMessage("Your password has expired, please change your password for security reasons.");
        
        if (pwdChangeRequired ==1) {
          forwardPage(Page.UPDATE_PROFILE);
        } else {
          forwardPage(Page.MENU);
        }
      } else {
       if (ub.getNumVisitsToMainMenu() <= 1) {
         addPageMessage("Welcome to OpenClinica, " + ub.getFirstName() + " " + ub.getLastName()+ ". " +
        "You last logged in on " + sdf.format(ub.getLastVisitDate()) + ".");
         addPageMessage("In order to use OpenClinica properly, Javascript must be turned on and all popup blockers must be disabled for this site.");
       }
        forwardPage(Page.MENU);
      }

    } else {//a new user's first log in
      studies = (ArrayList) sdao.findAllByUser(ub.getName());
      request.setAttribute("studies", studies);
      session.setAttribute("userBean1", ub);
      request.setAttribute("mustChangePass", "yes");
      addPageMessage("Welcome to OpenClinica, " + ub.getFirstName() + " " + 
          ub.getLastName()+ ". " + 
          "Your current password is set by system. For greater security, please change your password in your <a href=\"UpdateProfile\">User Profile</a>.");
      
      if (pwdChangeRequired ==1) {        
        forwardPage(Page.UPDATE_PROFILE);
      } else {
        forwardPage(Page.MENU);
      }
    }

  }

}