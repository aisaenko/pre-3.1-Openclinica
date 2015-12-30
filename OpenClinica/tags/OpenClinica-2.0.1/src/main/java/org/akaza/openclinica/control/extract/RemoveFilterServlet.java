/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.extract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.extract.FilterBean;
import org.akaza.openclinica.bean.extract.FilterRow;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.extract.FilterDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/** 
 * <P>The goal here is to provide a small servlet which will 
 * change a status from 'available' to 'unavailable' so that
 * it cannot be accessed.
 * 
 * <P>TODO define who can or can't remove a filter; creator only?
 * anyone in the project?
 * 
 * @author thickerson
 * 
 */
public class RemoveFilterServlet extends SecureController {
	public static final String PATH = "RemoveFilter";
	public static final String ARG_FILTER_ID = "filterId";
	
	public static String getLink(int filterId) {
		return PATH + '?' + ARG_FILTER_ID + '=' + filterId;
	}
	public void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		int filterId = fp.getInt("filterId");
		FilterDAO fDAO = new FilterDAO(sm.getDataSource());
		FilterBean filter = 
			(FilterBean)fDAO.findByPK(filterId);
		
		String action = request.getParameter("action");
		if ("Remove this Filter".equalsIgnoreCase(action)) {
			filter.setStatus(Status.DELETED);
			fDAO.update(filter);
			addPageMessage("The Filter has been successfully removed.  "+
					"System Administrators can access "+
					"and reverse this change if necessary.");
			EntityBeanTable table = getFilterTable();
			request.setAttribute("table",table);
			
			forwardPage(Page.CREATE_FILTER_SCREEN_1);
		} else if ("Cancel".equalsIgnoreCase(action)) {
			EntityBeanTable table = getFilterTable();
			request.setAttribute("table",table);
			
			forwardPage(Page.CREATE_FILTER_SCREEN_1);
		} else {
			request.setAttribute("filter",filter);
			forwardPage(Page.REMOVE_FILTER);
		}
	}
	
	public void mayProceed() throws InsufficientPermissionException {
	  	if (ub.isSysAdmin()) {
	  		return ;
	  	}
	  	if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
	  	        || currentRole.getRole().equals(Role.COORDINATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR)) {
	  	      return;
	  	    }

	  	    addPageMessage("You don't have the correct privilege in your current active study. "
	  	        + "Please change your active study or contact your sysadmin.");
	  	    throw new InsufficientPermissionException(Page.MENU, "not allowed to access extract data servlet", "1");

	}
	
	private EntityBeanTable getFilterTable() {
		FormProcessor fp = new FormProcessor(request);
		FilterDAO fdao = new FilterDAO(sm.getDataSource());
		EntityBeanTable table = fp.getEntityBeanTable();

		ArrayList filters = (ArrayList)fdao.findAll();
		//TODO make findAllByProject
		ArrayList filterRows = FilterRow.generateRowsFromBeans(filters);
		
		String[] columns = { "Filter Name", "Description", "Created By", "Created Date", "Status", "Actions" };
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(5);
		table.setQuery("CreateFiltersOne", new HashMap());
		table.setRows(filterRows);
		table.computeDisplay();
		return table;
	}

}
