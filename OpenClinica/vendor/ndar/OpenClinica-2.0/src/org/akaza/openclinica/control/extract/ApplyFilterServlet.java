/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.extract;

import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

import org.akaza.openclinica.dao.extract.FilterDAO;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.extract.FilterBean;
import org.akaza.openclinica.bean.extract.FilterRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * <P>Meant to be the gateway between creating a dataset and
 * applying (or creating) a filter to go on top of the dataset.
 * <P>Ideally, will only consist of a couple of steps; and then
 * will send on to a different servlet to either a) create the
 * filter, or b) add a selected filter and go back to the dataset
 * creation process.<P>
 * 
 * @author thickerson
 *
 */
public class ApplyFilterServlet extends SecureController {

	public static final String BEAN_YEARS = "years"; 
	public static final String BEAN_MONTHS = "months";
	public static final String BEAN_FILTER = "filter";
	public static final String DETAILS_URL = "ApplyFilter?action=details";
	public static final String ARG_FILTER_ID = "filterId";
	//the above mean to be here to send back to create dataset, tbh
	
	public static String getLink(int filterId) {
		return DETAILS_URL + '&' + ARG_FILTER_ID + '=' + filterId;
	}
	
	public void processRequest() throws Exception {
		String action = request.getParameter("action");
		//if blank -- move to apply filter page
		//if validate -- check to make sure one is chosen
		//if return -- return to the create dataset workflow
		//if details == show details with an option to return to the list page
		if (StringUtil.isBlank(action)) {
			
			EntityBeanTable table = getFilterTable();
			request.setAttribute("table", table);
			
			forwardPage(Page.APPLY_FILTER);
		} else if ("validate".equalsIgnoreCase(action)) {
			FormProcessor fp = new FormProcessor(request);
			HashMap errors = new HashMap();
			if (fp.getString("submit").equalsIgnoreCase("Apply Filter")) {
				if (fp.getInt("filterId") > 0) {
					FilterDAO fdao = new FilterDAO(sm.getDataSource());
					FilterBean fb = (FilterBean) fdao.findByPK(fp.getInt("filterId"));
					session.setAttribute("newFilter",fb);
					
				} else {
					Validator.addError(errors, "all", "No filter was chosen.");
				}
				if (!errors.isEmpty()) {
					EntityBeanTable table = getFilterTable();
					request.setAttribute("table", table);
					
					addPageMessage("There were some errors in your submission.  See below for details.");
					setInputMessages(errors);
					
					forwardPage(Page.APPLY_FILTER);
				} else {
					//move on to the next page in create dataset
					request.setAttribute("statuses", getStatuses());
					forwardPage(Page.CREATE_DATASET_4);
				}
			} else if (fp.getString("submit").equalsIgnoreCase("Create New Filter")) {
				//forward on to the rules, already on screen 2
				forwardPage(Page.CREATE_FILTER_SCREEN_2);
				
			} else if (fp.getString("submit").equalsIgnoreCase("Skip Apply Filter and Save")) {
				//send back to creating a dataset: 
				//TODO set the longitudinal dates?
				//or back a screen before/after that?
				String fieldNames[] = {"firstmonth", "firstyear", "lastmonth", "lastyear"};
				fp.setCurrentIntValuesAsPreset(fieldNames);
				
				setPresetValues(fp.getPresetValues());
				
				request.setAttribute(BEAN_MONTHS, getMonths());
				request.setAttribute(BEAN_YEARS, getYears());
				
				forwardPage(Page.CREATE_DATASET_3);
			} else {
				//throw an error, you shouldn't get here
			}
			
		} else if ("return".equalsIgnoreCase(action)) {
			//TODO figure out if we need this? tbh
		} else if ("details".equalsIgnoreCase(action)) {
			FormProcessor fp = new FormProcessor(request);
			int filterId = fp.getInt("filterId");
			FilterDAO fDAO = new FilterDAO(sm.getDataSource());
			FilterBean showFilter = 
				(FilterBean)fDAO.findByPK(filterId);
			request.setAttribute(BEAN_FILTER, showFilter);
			forwardPage(Page.VIEW_FILTER_DETAILS);
		} else {
			//throw an error, you can't get here
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
	private ArrayList getMonths() {
		ArrayList answer = new ArrayList();
		
		answer.add("January");
		answer.add("February");
		answer.add("March");
		answer.add("April");
		answer.add("May");
		answer.add("June");
		answer.add("July");
		answer.add("August");
		answer.add("September");
		answer.add("October");
		answer.add("November");
		answer.add("December");
		
		return answer;
	}
	
	private ArrayList getYears() {
		ArrayList answer = new ArrayList();
		
		Calendar currTime = Calendar.getInstance();
		int currYear = currTime.get(Calendar.YEAR);
		
		for (int i = 1980; i <= currYear; i++) {
			answer.add(String.valueOf(i));
		}
		
		return answer;
	}
	
	/*
	 * might be worth adding this to the core servlets?
	 */
	private ArrayList getStatuses() {
		Status statusesArray[] = {Status.AVAILABLE, Status.PENDING, Status.PRIVATE, Status.UNAVAILABLE};
		List statuses = Arrays.asList(statusesArray);
		return new ArrayList(statuses);
	}
	
	private EntityBeanTable getFilterTable() {
		FormProcessor fp = new FormProcessor(request);
		FilterDAO fdao = new FilterDAO(sm.getDataSource());
		EntityBeanTable table = fp.getEntityBeanTable();

		ArrayList filters = new ArrayList();
		if (ub.isSysAdmin()) {
			filters = (ArrayList)fdao.findAllAdmin();
		} else {
			filters = (ArrayList)fdao.findAll();
		}
		//TODO make findAllByProject????
		ArrayList filterRows = FilterRow.generateRowsFromBeans(filters);
		
		String[] columns = { "Filter Name", "Description", "Created By", "Created Date", "Status", "Actions" };
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(5);
		table.setQuery("ApplyFilter", new HashMap());
		table.setRows(filterRows);
		table.computeDisplay();
		return table;
	}

}
