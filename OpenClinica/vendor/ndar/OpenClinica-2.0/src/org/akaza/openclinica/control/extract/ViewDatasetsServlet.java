/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.extract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.extract.DatasetRow;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**ViewDatasetsServlet.java, the view datasets function
 * accessed from the extract datasets main page.
 * @author thickerson
 *
 * 
 * 
 */
public class ViewDatasetsServlet extends SecureController {
	public static String getLink(int dsId) {
		return "ViewDatasets?action=details&datasetId=" + dsId;
	}
	
	public void processRequest() throws Exception {
		DatasetDAO dsdao = new DatasetDAO(sm.getDataSource());
		String action = request.getParameter("action");
		resetPanel();			
		request.setAttribute(STUDY_INFO_PANEL, panel);
	    if (StringUtil.isBlank(action)) {
	    	FormProcessor fp = new FormProcessor(request);
			
			EntityBeanTable table = fp.getEntityBeanTable();
			ArrayList datasets = new ArrayList();
			if (ub.isSysAdmin()) {
				datasets = (ArrayList)dsdao.findAllByStudyIdAdmin(currentStudy.getId());
			} else {
				datasets = (ArrayList)dsdao.findAllByStudyId(currentStudy.getId());
			}
			
			
			ArrayList datasetRows = DatasetRow.generateRowsFromBeans(datasets);

			String[] columns = { "Dataset Name", "Description", "Created By", "Created Date", "Status", "Actions" };
			table.setColumns(new ArrayList(Arrays.asList(columns)));
			table.hideColumnLink(5);
			table.addLink("Show only My Datasets", 
					"ViewDatasets?action=owner&ownerId="+ub.getId());
			table.addLink("Create Dataset", "CreateDataset");
			table.setQuery("ViewDatasets", new HashMap());
			table.setRows(datasetRows);
			table.computeDisplay();

			request.setAttribute("table", table);
	    	//this is the old code that the tabling code replaced:
			//ArrayList datasets = (ArrayList)dsdao.findAll();
			//request.setAttribute("datasets", datasets);
		    forwardPage(Page.VIEW_DATASETS);
	    } else {
	     if ("owner".equalsIgnoreCase(action)) {
	     	FormProcessor fp = new FormProcessor(request);
	     	int ownerId = fp.getInt("ownerId");
	     	EntityBeanTable table = fp.getEntityBeanTable();

			ArrayList datasets = (ArrayList)dsdao.findByOwnerId(ownerId, currentStudy.getId());
			
			/*if (datasets.isEmpty()) {
				forwardPage(Page.VIEW_EMPTY_DATASETS);
			} else {*/
			
				ArrayList datasetRows = DatasetRow.generateRowsFromBeans(datasets);
				String[] columns = { "Dataset Name", "Description", "Created By", "Created Date", "Status", "Actions" };
				table.setColumns(new ArrayList(Arrays.asList(columns)));
				table.hideColumnLink(5);
				table.addLink("Show All Datasets", "ViewDatasets");
				table.addLink("Create Dataset", "CreateDataset");
				table.setQuery("ViewDatasets?action=owner&ownerId="+ub.getId(), new HashMap());
				table.setRows(datasetRows);
				table.computeDisplay();
				request.setAttribute("table", table);
	     	//this is the old code:
	     	
	     	//ArrayList datasets = (ArrayList)dsdao.findByOwnerId(ownerId);
			//request.setAttribute("datasets", datasets);
				forwardPage(Page.VIEW_DATASETS);
			//}
	     } else if ("details".equalsIgnoreCase(action)) {
	     	FormProcessor fp = new FormProcessor(request);
	     	int datasetId = fp.getInt("datasetId");
	     	DatasetBean db = (DatasetBean)dsdao.findByPK(datasetId);
	     	/*EntityBeanTable table = fp.getEntityBeanTable();
	     	ArrayList datasetRows = DatasetRow.generateRowFromBean(db);
			String[] columns = { "Dataset Name", "Description", "Created By", "Created Date", "Status", "Actions" };
			table.setColumns(new ArrayList(Arrays.asList(columns)));
			table.hideColumnLink(5);
			table.setQuery("ViewDatasets", new HashMap());
			table.setRows(datasetRows);
			table.computeDisplay();
			request.setAttribute("table", table);*/
	     	request.setAttribute("dataset",db);
	     	
	     	forwardPage(Page.VIEW_DATASET_DETAILS);
	     }
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
}
