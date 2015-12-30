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
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionRow;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * Processes user reuqest to generate study event definition list
 * 
 * @author jxu 
 * 
 */
public class ListEventDefinitionServlet extends SecureController {
  /**
   * Checks whether the user has the correct privilege
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
   * Processes the request
   */
  public void processRequest() throws Exception {

    StudyEventDefinitionDAO edao = new StudyEventDefinitionDAO(sm.getDataSource());
    UserAccountDAO sdao = new UserAccountDAO(sm.getDataSource());
    ArrayList seds = (ArrayList) edao.findAllByStudy(currentStudy);

    //request.setAttribute("seds", seds);
    
    StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
    EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
    ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
    for (int i = 0; i < seds.size(); i++) {
      StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seds.get(i);
      if (sed.getUpdater().getId()==0){
        sed.setUpdater(sed.getOwner());
        sed.setUpdatedDate(sed.getCreatedDate());
      }
      if (isLockable(sed,sedao,ecdao,iddao)) {
        sed.setLockable(true);
      } 
      if (isPopulated(sed,sedao)) {
        sed.setPopulated(true);
      } 
    }
    
    FormProcessor fp = new FormProcessor(request);
    EntityBeanTable table = fp.getEntityBeanTable();
    ArrayList allStudyRows = StudyEventDefinitionRow.generateRowsFromBeans(seds);
    
    String[] columns = {"Order","Name", "Repeating", "Type", "Category", "Populated", "Date Updated","Actions" };
	table.setColumns(new ArrayList(Arrays.asList(columns)));
	table.hideColumnLink(5);
	table.hideColumnLink(7);
	table.setQuery("ListEventDefinition", new HashMap());
	table.addLink("Create a new study event definition","DefineStudyEvent");
	
	table.setRows(allStudyRows);
	table.computeDisplay();

	
	request.setAttribute("table", table);
	request.setAttribute("defSize", new Integer(seds.size()));

    forwardPage(Page.STUDY_EVENT_DEFINITION_LIST);
  }
  
  
  /**
   * Checked whether a definition is available to be locked
   * @param sed
   * @return
   */
  private boolean isPopulated(StudyEventDefinitionBean sed, StudyEventDAO sedao) {
      
      
    //checks study event
    ArrayList events = (ArrayList) sedao.findAllByDefinition(sed.getId());
    for (int j = 0; j < events.size(); j++) {
      StudyEventBean event = (StudyEventBean) events.get(j);
      if (!(event.getStatus().equals(Status.DELETED))) {
         return true;
      }     
    }
    return false;
  }

  /**
   * Checked whether a definition is available to be locked
   * @param sed
   * @return
   */
  private boolean isLockable(StudyEventDefinitionBean sed, StudyEventDAO sedao,
      EventCRFDAO ecdao,ItemDataDAO iddao) {
      
      
    //checks study event
    ArrayList events = (ArrayList) sedao.findAllByDefinition(sed.getId());
    for (int j = 0; j < events.size(); j++) {
      StudyEventBean event = (StudyEventBean) events.get(j);
      if (!(event.getStatus().equals((Status.AVAILABLE)) || event.getStatus().equals(
          (Status.DELETED)))) {
         return false;
      }     
      
      
      ArrayList eventCRFs = ecdao.findAllByStudyEvent(event);
    
      for (int k = 0; k < eventCRFs.size(); k++) {
        EventCRFBean eventCRF = (EventCRFBean) eventCRFs.get(k);
        if (!(eventCRF.getStatus().equals((Status.UNAVAILABLE))||
            eventCRF.getStatus().equals((Status.DELETED)))){
          return false;
        }
       

        ArrayList itemDatas = iddao.findAllByEventCRFId(eventCRF.getId());
        for (int a = 0; a < itemDatas.size(); a++) {
          ItemDataBean item = (ItemDataBean) itemDatas.get(a);
          if (!(item.getStatus().equals((Status.UNAVAILABLE))||
              item.getStatus().equals((Status.DELETED)))){
            return false;
          }
         
        }
      }
    }
    
    return true;
  }

}