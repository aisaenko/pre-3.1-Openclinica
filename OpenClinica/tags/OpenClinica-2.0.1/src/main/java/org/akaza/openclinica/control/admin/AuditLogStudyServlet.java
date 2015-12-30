/*
 * Created on Sep 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.akaza.openclinica.control.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.akaza.openclinica.bean.admin.AuditEventStudyRow;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.admin.AuditEventDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author thickerson
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AuditLogStudyServlet extends SecureController {
	  
	  public static String getLink(int userId) {
		return "AuditLogStudy";
	  }
	/*
	 *  (non-Javadoc)
	 * Assume that we get the user id automatically.
	 * We will jump from the edit user page if the user is an admin, they
	 * can get to see the users' log
	 * @see org.akaza.openclinica.control.core.SecureController#processRequest()
	 */
	protected void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		
		AuditEventDAO aeDAO = new AuditEventDAO(sm.getDataSource());
		ArrayList al = aeDAO.findAllByStudyId(currentStudy.getId());
		
		EntityBeanTable table = fp.getEntityBeanTable();
	    ArrayList allRows = AuditEventStudyRow.generateRowsFromBeans(al);
	    
//	    String[] columns = { "Date and Time", "Action", "Entity/Operation", "Record ID", "Changes and Additions","Other Info" };
//		table.setColumns(new ArrayList(Arrays.asList(columns)));
//		table.hideColumnLink(4);
//		table.hideColumnLink(1);
//		table.hideColumnLink(5);
//		table.setQuery("AuditLogUser?userLogId="+userId, new HashMap());
		String[] columns = {"Date and Time","Action/Message","Entity/Operation",
				"Updated by","Subject Unique ID","Changes and Additions",
				//"Other Info",
				"Actions"};
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.setAscendingSort(false);
		table.hideColumnLink(1);
		table.hideColumnLink(5);
		table.hideColumnLink(6);
		//table.hideColumnLink(7);
		table.setQuery("AuditLogStudy", new HashMap());
		table.setRows(allRows);
		table.computeDisplay();

		
		request.setAttribute("table", table);

		logger.warning("*** found servlet, sending to page ***");
		forwardPage(Page.AUDIT_LOG_STUDY);

	}
	
	/*
	 *  (non-Javadoc)
	 * Since access to this servlet is admin-only, restricts user to 
	 * see logs of specific users only
	 * @author thickerson
	 * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
	 */
	protected void mayProceed() throws InsufficientPermissionException {
		if (ub.isSysAdmin()) {
		      return;
		    }

			Role r = currentRole.getRole();
		    if (r.equals(Role.STUDYDIRECTOR) || r.equals(Role.COORDINATOR)) {
		    	return ;
		    }
		    
		    addPageMessage("You don't have correct privilege in your current active study. "
		    		+ "Please change your active study or contact your sysadmin.");
		    throw new InsufficientPermissionException(Page.MENU_SERVLET, "not director", "1");
		  }
	
//	protected String getAdminServlet() {
//		return SecureController.ADMIN_SERVLET_CODE;
//	}

}
