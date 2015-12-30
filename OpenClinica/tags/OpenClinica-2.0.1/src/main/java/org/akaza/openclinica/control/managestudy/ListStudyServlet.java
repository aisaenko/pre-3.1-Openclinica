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

import org.akaza.openclinica.bean.admin.DisplayStudyBean;
import org.akaza.openclinica.bean.admin.DisplayStudyRow;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * @version CVS: $Id: ListStudyServlet.java 7731 2005-10-10 16:52:28Z thickerson $
 */
public class ListStudyServlet extends SecureController {

  /**
   *  
   */
  public void mayProceed() throws InsufficientPermissionException {    
  	if (ub.isSysAdmin()) {
  		return ;
  	}
    
    addPageMessage("You don't have correct privilege in your current active study. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.ADMIN_SYSTEM_SERVLET, "not admin", "1");


  }

  /**
   * Finds all the studies
   *    
   */
  public void processRequest() throws Exception {

    StudyDAO sdao = new StudyDAO(sm.getDataSource());
    ArrayList studies = (ArrayList) sdao.findAll();
    //find all parent studies
    ArrayList parents = (ArrayList) sdao.findAllParents();
    ArrayList displayStudies = new ArrayList();
    
    for (int i=0; i<parents.size(); i++){
      StudyBean parent= (StudyBean) parents.get(i);
      ArrayList children = (ArrayList) sdao.findAllByParent(parent.getId());
      DisplayStudyBean displayStudy = new DisplayStudyBean();
      displayStudy.setParent(parent);
      displayStudy.setChildren(children);
      displayStudies.add(displayStudy);
      
    }
    
    FormProcessor fp = new FormProcessor(request);
    EntityBeanTable table = fp.getEntityBeanTable();
    ArrayList allStudyRows = DisplayStudyRow.generateRowsFromBeans(displayStudies);
    
    String[] columns = { "Name", "Unique Identifier", "Principal Investigator", "Facility Name", "Date Created", "Status", "Actions" };
	table.setColumns(new ArrayList(Arrays.asList(columns)));
	table.hideColumnLink(6);
	table.setQuery("ListStudy", new HashMap());
	table.addLink("Create a New Study", "CreateStudy");
	table.setRows(allStudyRows);
	table.computeDisplay();

	
	request.setAttribute("table", table);
    //request.setAttribute("studies", studies);
    session.setAttribute("fromListSite", "no");
    
    resetPanel();
	panel.setStudyInfoShown(false);
	panel.setOrderedData(true);
	setToPanel("In the application, you currently have","");
	if (parents.size()>0){
      setToPanel("Studies", new Integer(parents.size()).toString());
    }  
	if (studies.size()>0){
      setToPanel("Sites", new Integer(studies.size()-parents.size()).toString());
    } 
    forwardPage(Page.STUDY_LIST);

  }
  
  protected String getAdminServlet() {
	return SecureController.ADMIN_SERVLET_CODE;
}

}