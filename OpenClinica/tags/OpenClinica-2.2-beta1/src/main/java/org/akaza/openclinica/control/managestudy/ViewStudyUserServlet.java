/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ViewStudyUserServlet extends SecureController {
  /**
   *  
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }

    if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
        || currentRole.getRole().equals(Role.COORDINATOR)) {
      return;
    }

    addPageMessage(respage.getString("no_have_correct_privilege_current_study")+ " " 
            + respage.getString("change_study_contact_sysadmin"));
    throw new InsufficientPermissionException(Page.LIST_USER_IN_STUDY_SERVLET, resexception.getString("not_study_director"), "1");

  }
  
  public void processRequest() throws Exception {
    UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
    String name = request.getParameter("name");
    String studyIdString = request.getParameter("studyId");
    if (StringUtil.isBlank(name) || StringUtil.isBlank(studyIdString)) {
      addPageMessage(respage.getString("please_choose_a_user_to_view"));
      forwardPage(Page.LIST_USER_IN_STUDY_SERVLET);
    } else {    
      int studyId = (Integer.valueOf(studyIdString.trim())).intValue();
      UserAccountBean user = (UserAccountBean) udao.findByUserName(name);
      
      request.setAttribute("user",user);
      
      StudyUserRoleBean uRole = (StudyUserRoleBean)udao.findRoleByUserNameAndStudyId(name,studyId);
      request.setAttribute("uRole",uRole);
      
      StudyDAO sdao = new StudyDAO(sm.getDataSource());
      StudyBean study = (StudyBean)sdao.findByPK(studyId);
      request.setAttribute("uStudy",study);
      forwardPage(Page.VIEW_USER_IN_STUDY);
      
    }
  }

}
