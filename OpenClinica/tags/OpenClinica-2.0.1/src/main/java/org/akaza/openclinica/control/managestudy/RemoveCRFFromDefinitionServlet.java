/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.managestudy;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.*;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

import java.util.*;

/**
 * Remove the reference to a CRF from a study event definition
 * 
 * @author jxu 
 */
public class RemoveCRFFromDefinitionServlet extends SecureController {
  
  /**
   * Checks whether the user has the correct privilege
   */
  public void mayProceed() throws InsufficientPermissionException {
  	if (ub.isSysAdmin()) {
  		return ;
  	}
  	if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
         || currentRole.getRole().equals(Role.COORDINATOR)) {
       return;
    }

    addPageMessage("You do not have permission to update a Study Event Definition. "
        + "Please change your active study or contact the System Administrator.");
    throw new InsufficientPermissionException(Page.LIST_DEFINITION_SERVLET, "not study director", "1");


  }

  public void processRequest() throws Exception {   
    ArrayList edcs = (ArrayList)session.getAttribute("eventDefinitionCRFs");
    ArrayList updatedEdcs = new ArrayList();
    String crfName = "";
    if (edcs != null && edcs.size()>1) {
      String idString = request.getParameter("id");
      logger.info("crf id:" + idString);
      if (StringUtil.isBlank(idString)) {
        addPageMessage("Please choose a crf to remove.");
        forwardPage(Page.UPDATE_EVENT_DEFINITION1);
      } else {
        //crf id        
        int id = Integer.valueOf(idString.trim()).intValue();
        for (int i =0; i< edcs.size(); i++){
          EventDefinitionCRFBean edc = (EventDefinitionCRFBean)edcs.get(i);
          if (edc.getCrfId() == id) {
            edc.setStatus(Status.DELETED);
            crfName=edc.getCrfName();
          }  
          if (edc.getId()>0 || !edc.getStatus().equals(Status.DELETED)){
            updatedEdcs.add(edc);
            System.out.println("\nversion:" + edc.getDefaultVersionId());
          }
        }     
        session.setAttribute("eventDefinitionCRFs", updatedEdcs);
        addPageMessage(crfName + " has been removed.");
        forwardPage(Page.UPDATE_EVENT_DEFINITION1);
      }
      
    } else {
      addPageMessage("An event definition needs to have at least one CRF.");
      forwardPage(Page.UPDATE_EVENT_DEFINITION1);
    }
  }
}
      