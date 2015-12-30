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
import org.akaza.openclinica.bean.managestudy.StudyTemplateBean;
import org.akaza.openclinica.bean.managestudy.StudyTemplateRow;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyTemplateDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * Lists all the subject group classes in a study
 * 
 * @author jxu
 * 
 */
public class ListSubjectGroupClassServlet  extends SecureController {
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
    throw new InsufficientPermissionException(Page.MANAGE_STUDY_SERVLET, "not study director", "1");

  }
  
  public void processRequest() throws Exception {
    FormProcessor fp = new FormProcessor(request);
    StudyTemplateDAO stdao = new StudyTemplateDAO(sm.getDataSource());
    ArrayList groups = (ArrayList)stdao.findAllByStudy(currentStudy);
    
    StudyGroupDAO sgdao = new StudyGroupDAO(sm.getDataSource());
    for (int i=0; i<groups.size();i++) {
      StudyTemplateBean group = (StudyTemplateBean)groups.get(i);
      ArrayList studyGroups = sgdao.findAllByGroupClass(group);
      group.setStudyGroups(studyGroups);
      
    }
    EntityBeanTable table = fp.getEntityBeanTable();
    ArrayList allGroupRows = StudyTemplateRow.generateRowsFromBeans(groups);
    
    String[] columns = {"Group Template", "Type", "Subject Assignment", "Study Name", "Study Groups", "Status", "Actions"};
	table.setColumns(new ArrayList(Arrays.asList(columns)));
	table.hideColumnLink(4);
	table.hideColumnLink(6);
	table.setQuery("ListSubjectGroupClass", new HashMap());
	table.addLink("Create a Group Template","CreateSubjectGroupClass");
	table.setRows(allGroupRows);
	table.computeDisplay();

	
	request.setAttribute("table", table);
    forwardPage(Page.SUBJECT_GROUP_CLASS_LIST);
   
  }


}
