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
import org.akaza.openclinica.bean.managestudy.StudyRow;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * @version CVS: $Id: ListSiteServlet.java 7544 2005-10-03 18:26:07Z jxu $ 
 */
public class ListSiteServlet extends SecureController {
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
    throw new InsufficientPermissionException(Page.MANAGE_STUDY_SERVLET,
        "not study director", "1");

  }

  /**
   * Finds all the studies, processes the request
   */
  public void processRequest() throws Exception {
    FormProcessor fp = new FormProcessor(request);
    if (currentStudy.getParentStudyId() > 0) {
      addPageMessage("No sites available because your current study "
            + "itself is a site.");
      forwardPage(Page.MENU_SERVLET);
    } else {

    StudyDAO sdao = new StudyDAO(sm.getDataSource());
    ArrayList studies = (ArrayList) sdao.findAllByParent(currentStudy.getId());
    
    EntityBeanTable table = fp.getEntityBeanTable();
    ArrayList allStudyRows = StudyRow.generateRowsFromBeans(studies);
    
    String[] columns = { "Name", "Unique Identifier", "Principal Investigator", "Facility Name", "Date Created", "Status", "Actions" };
	table.setColumns(new ArrayList(Arrays.asList(columns)));
	table.hideColumnLink(6);
	table.setQuery("ListSite", new HashMap());
	table.addLink("Create a new site", "CreateSubStudy");
	table.setRows(allStudyRows);
	table.computeDisplay();

	
	request.setAttribute("table", table);
    //request.setAttribute("studies", studies);
    session.setAttribute("fromListSite", "yes");
    forwardPage(Page.SITE_LIST);
    }

  }

}