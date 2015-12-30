/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.util.*;
import org.akaza.openclinica.bean.login.*;
import org.akaza.openclinica.bean.managestudy.*;
import org.akaza.openclinica.dao.managestudy.*;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.*;
import org.akaza.openclinica.core.form.*;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.*;

public class ListUserAccountsServlet extends SecureController {
	public static final String PATH = "ListUserAccounts";
	public static final String ARG_MESSAGE = "message";

	protected void mayProceed() throws InsufficientPermissionException {
		if (!ub.isSysAdmin()) {
			addPageMessage("You may not perform administrative functions.");
			throw new InsufficientPermissionException(Page.ADMIN_SYSTEM_SERVLET, "You may not perform administrative functions.", "1");
		}
		
		return;
	}

	protected void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);

		UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
		EntityBeanTable table = fp.getEntityBeanTable();
//		table.setSortingIfNotExplicitlySet(1, false);

		ArrayList allUsers = getAllUsers(udao);
		setStudyNamesInStudyUserRoles(allUsers);
		ArrayList allUserRows = UserAccountRow.generateRowsFromBeans(allUsers);

		String[] columns = { "User Name", "First Name", "Last Name", "Status", "Actions" };
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(4);
		table.setQuery("ListUserAccounts", new HashMap());
		table.addLink("Create a new user", "CreateUserAccount");

		table.setRows(allUserRows);
		table.computeDisplay();

		request.setAttribute("table", table);
		
		String message = fp.getString(ARG_MESSAGE, true);
		request.setAttribute(ARG_MESSAGE, message);
		
		resetPanel();
		panel.setStudyInfoShown(false);
		panel.setOrderedData(true);
		if (allUsers.size()>0){
	      setToPanel("Users", new Integer(allUsers.size()).toString());
	    }  

		forwardPage(Page.LIST_USER_ACCOUNTS);
	}
	
	private ArrayList getAllUsers(UserAccountDAO udao) {
		ArrayList result = (ArrayList) udao.findAll();
		return result;
	}

	/**
	 * For each user, for each study user role, set the study user role's studyName property.  
	 * @param users The users to display in the table of users.  Each element is a UserAccountBean.
	 */
	private void setStudyNamesInStudyUserRoles(ArrayList users) {
		StudyDAO sdao = new StudyDAO(sm.getDataSource());
		ArrayList allStudies = (ArrayList) sdao.findAll();
		HashMap studiesById = new HashMap();
		
		int i;
		for (i = 0; i < allStudies.size(); i++) {
			StudyBean sb = (StudyBean) allStudies.get(i);
			studiesById.put(new Integer(sb.getId()), sb);
		}
		
		for (i = 0; i < users.size(); i++) {
			UserAccountBean u = (UserAccountBean) users.get(i);
			ArrayList roles = u.getRoles();
			
			for (int j = 0; j < roles.size(); j++) {
				StudyUserRoleBean surb = (StudyUserRoleBean) roles.get(j);
				StudyBean sb = (StudyBean) studiesById.get(new Integer(surb.getStudyId()));
				if (sb != null) {
					surb.setStudyName(sb.getName());
				}
				roles.set(j, surb);
			}
			u.setRoles(roles);
			users.set(i, u);
		}
		
		return ;
	}

	protected String getAdminServlet() {
		return SecureController.ADMIN_SERVLET_CODE;
	}
}