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
import java.util.List;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.TermType;
import org.akaza.openclinica.bean.extract.FilterBean;
import org.akaza.openclinica.bean.extract.FilterRow;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.extract.FilterDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;
/**
 * <P>The servlet class that will be for editing and validating
 * the filter object only, kept small for performance purposes.
 * 
 * <P>This affects the jsp pages editFilter.jsp and 
 * validateEditFilter.jsp.
 * 
 * @author thickerson
 */
public class EditFilterServlet extends SecureController {
	
	public static String getLink(int filterId) {
		return "EditFilter?filterId=" + filterId;
	}
	
	public void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		String action = request.getParameter("action");
		if ("validate".equalsIgnoreCase(action)) {
			//check name, description, status for right now
			Validator v = new Validator(request);
			
			v.addValidation("fName", Validator.NO_BLANKS);
			v.addValidation("fDesc", Validator.NO_BLANKS);
			v.addValidation("fStatusId", Validator.IS_VALID_TERM, TermType.STATUS);
			
			HashMap errors = v.validate();
			if (!errors.isEmpty()) {
				String fieldNames[] = {"fName", "fDesc"}; 
				fp.setCurrentStringValuesAsPreset(fieldNames);
				fp.addPresetValue("fStatusId", fp.getInt("fStatusId"));
				
				addPageMessage("There were some errors in your update."+
						"  See below for details.");
				setInputMessages(errors);
				setPresetValues(fp.getPresetValues());

				//TODO determine if this is necessary
				int filterId = fp.getInt("filterId");
				FilterDAO fDAO = new FilterDAO(sm.getDataSource());
				FilterBean showFilter = 
					(FilterBean)fDAO.findByPK(filterId);
				request.setAttribute("filter", showFilter);
				//maybe just set the above to the session?
				
				request.setAttribute("statuses", getStatuses());
				forwardPage(Page.EDIT_FILTER);
			} else {
				int filterId = fp.getInt("filterId");
				FilterDAO fDAO = new FilterDAO(sm.getDataSource());
				FilterBean filter = 
					(FilterBean)fDAO.findByPK(filterId);
				filter.setName(fp.getString("fName"));
				filter.setDescription(fp.getString("fDesc"));
				filter.setStatus(Status.get(fp.getInt("fStatusId")));
				fDAO.update(filter);
				addPageMessage("The Filter was successfully updated.");
				
				//Collection filters = fDAO.findAll();
				//TODO make findAllByProject?
				//FormProcessor fp = new FormProcessor(request);
				FilterDAO fdao = new FilterDAO(sm.getDataSource());
				EntityBeanTable table = fp.getEntityBeanTable();

				ArrayList filters = (ArrayList)fdao.findAll();//TODO make findAllByProject
				ArrayList filterRows = FilterRow.generateRowsFromBeans(filters);
				
				String[] columns = { "Filter Name", "Description", "Created By", "Created Date", "Status", "Actions" };
				table.setColumns(new ArrayList(Arrays.asList(columns)));
				table.hideColumnLink(5);
				table.setQuery("CreateFiltersOne", new HashMap());
				table.setRows(filterRows);
				table.computeDisplay();

				request.setAttribute("table", table);
				
				forwardPage(Page.CREATE_FILTER_SCREEN_1);
				//forwardPage(Page.VALIDATE_EDIT_FILTER);
			}
		} else {
			int filterId = fp.getInt("filterId");
			FilterDAO fDAO = new FilterDAO(sm.getDataSource());
			FilterBean showFilter = 
				(FilterBean)fDAO.findByPK(filterId);
			request.setAttribute("filter", showFilter);
			request.setAttribute("statuses", getStatuses());
			forwardPage(Page.EDIT_FILTER);
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

	  	//TODO add a limit so that the owner can edit, no one else?
	}
	
	private ArrayList getStatuses() {
		Status statusesArray[] = {Status.AVAILABLE, Status.PENDING, Status.PRIVATE, Status.UNAVAILABLE};
		List statuses = Arrays.asList(statusesArray);
		return new ArrayList(statuses);
	}

}
