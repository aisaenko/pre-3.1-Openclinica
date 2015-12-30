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
import org.akaza.openclinica.bean.extract.DatasetRow;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;


/**
 * <P>The main page for the extract datasets use case.  
 * Show five last datasets and offers links for viewing all 
 * and viewing users' datasets, together with a link for extracting 
 * datasets.</P>
 * 
 * @author thickerson
 * 
 */
public class ExtractDatasetsMainServlet extends SecureController {
	
	public static final String PATH = "ExtractDatasetsMain";
	public static final String ARG_USER_ID = "userId";
	
	public static String getLink(int userId) {
		return PATH + '?' + ARG_USER_ID + '=' + userId;
	}
	public void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		DatasetDAO dsdao = new DatasetDAO(sm.getDataSource());
		EntityBeanTable table = fp.getEntityBeanTable();

		ArrayList datasets = (ArrayList)dsdao.findTopFive(currentStudy);
		ArrayList datasetRows = DatasetRow.generateRowsFromBeans(datasets);

		String[] columns = { "Dataset Name", "Description", "Created By", "Created Date", "Status", "Actions" };
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(5);
		
		table.addLink("View All","ViewDatasets");
		table.addLink("View My Datasets", "ViewDatasets?action=owner&ownerId="+ub.getId());
		table.addLink("Create Dataset", "CreateDataset");
		table.setQuery("ExtractDatasetsMain", new HashMap());
		table.setRows(datasetRows);
		table.computeDisplay();

		request.setAttribute("table", table);
		//the code above replaces the following lines:
		//DatasetDAO dsdao = new DatasetDAO(sm.getDataSource());
		//ArrayList datasets = (ArrayList)dsdao.findTopFive();
		//request.setAttribute("datasets", datasets);
		resetPanel();
		
		request.setAttribute(STUDY_INFO_PANEL, panel);
		 
		
	    forwardPage(Page.EXTRACT_DATASETS_MAIN);
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
