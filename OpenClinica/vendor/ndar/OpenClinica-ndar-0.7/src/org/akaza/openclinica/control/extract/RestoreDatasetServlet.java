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

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.extract.DatasetRow;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author thickerson
 *
 */
public class RestoreDatasetServlet extends SecureController {
	public static String getLink(int dsId) {
		return "RestoreDataset?dsId=" + dsId;
	}
	
	public void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		int dsId = fp.getInt("dsId");
		DatasetDAO dsDAO = new DatasetDAO(sm.getDataSource());
		DatasetBean dataset = 
			(DatasetBean)dsDAO.findByPK(dsId);
		
		String action = request.getParameter("action");
		if ("Restore this Dataset".equalsIgnoreCase(action)) {
			dataset.setStatus(Status.AVAILABLE);
			dsDAO.update(dataset);
			addPageMessage("The Dataset has been successfully reinstated.");
			request.setAttribute("table", getDatasetTable());
			forwardPage(Page.VIEW_DATASETS);
		} else if ("Cancel".equalsIgnoreCase(action)) {
			
			request.setAttribute("table", getDatasetTable());
			forwardPage(Page.VIEW_DATASETS);
		} else {
			request.setAttribute("dataset",dataset);
			forwardPage(Page.RESTORE_DATASET);
		}
	}
	
	public void mayProceed() throws InsufficientPermissionException {
	  	if (ub.isSysAdmin()) {
	  		return ;//TODO limit to owner only?
	  	}
	  	if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
	  	        || currentRole.getRole().equals(Role.COORDINATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR)) {
	  	      return;
	  	    }

	  	    addPageMessage("You don't have the correct privilege in your current active study. "
	  	        + "Please change your active study or contact your sysadmin.");
	  	    throw new InsufficientPermissionException(Page.MENU, "not allowed to access restore dataset servlet", "1");

	}
	
	private EntityBeanTable getDatasetTable() {
		FormProcessor fp = new FormProcessor(request);
		
		EntityBeanTable table = fp.getEntityBeanTable();
		DatasetDAO dsdao = new DatasetDAO(sm.getDataSource());
		ArrayList datasets = new ArrayList();
		//if (ub.isSysAdmin()) {
			//datasets = (ArrayList)dsdao.findAllByStudyIdAdmin(currentStudy.getId());
		//} else {
			datasets = (ArrayList)dsdao.findAllByStudyId(currentStudy.getId());
		//}
		
		ArrayList datasetRows = DatasetRow.generateRowsFromBeans(datasets);

		String[] columns = { "Dataset Name", "Description", "Created By", "Created Date", "Status", "Actions" };
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(5);
		table.setQuery("ViewDatasets", new HashMap());
		table.setRows(datasetRows);
		table.computeDisplay();
		return table;
	}


}

