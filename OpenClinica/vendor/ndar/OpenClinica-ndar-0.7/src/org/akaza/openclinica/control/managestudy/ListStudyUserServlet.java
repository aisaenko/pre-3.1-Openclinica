/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.managestudy.*;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * Lists all the users in a study 
 * 
 * @author jxu
 * 
 * @version CVS: $Id: ListStudyUserServlet.java,v 1.6 2005/10/12 18:51:53 jxu Exp $
 *
 */
public class ListStudyUserServlet extends SecureController {
  
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

    addPageMessage("You don't have correct privilege in your current active study. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.MANAGE_STUDY_SERVLET, "not study director", "1");

  }
  
  public void processRequest() throws Exception {
    FormProcessor fp = new FormProcessor(request);
    UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
    ArrayList users = (ArrayList) udao.findAllUsersByStudy(currentStudy.getId());
    
    EntityBeanTable table = fp.getEntityBeanTable();
    ArrayList allStudyUserRows = StudyUserRoleRow.generateRowsFromBeans(users);
    
    String[] columns = { "User Name", "First Name", "Last Name", "Role", "Study Name", "Status", "Actions" };
	table.setColumns(new ArrayList(Arrays.asList(columns)));
	table.hideColumnLink(6);
	table.setQuery("ListStudyUser", new HashMap());
	table.addLink("Assign a new user to current study","AssignUserToStudy");
	table.setRows(allStudyUserRows);
	table.computeDisplay();

	
	request.setAttribute("table", table);
    //request.setAttribute("users", users);
    forwardPage(Page.LIST_USER_IN_STUDY);
   
  }

}
