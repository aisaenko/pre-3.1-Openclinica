/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class RestoreCRFFromDefinitionServlet extends SecureController {
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

    addPageMessage("You do not have permission to update a Study Event Definition. "
        + "Please change your active study or contact the System Administrator.");
    throw new InsufficientPermissionException(Page.STUDY_EVENT_DEFINITION_LIST, "not study director", "1");

  }

  public void processRequest() throws Exception {
    ArrayList edcs = (ArrayList) session.getAttribute("eventDefinitionCRFs");
    String crfName = "";

    String idString = request.getParameter("id");
    logger.info("crf id:" + idString);
    if (StringUtil.isBlank(idString)) {
      addPageMessage("Please choose a crf to restore.");
      forwardPage(Page.UPDATE_EVENT_DEFINITION1);
    } else {
      //event crf definition id
      int id = Integer.valueOf(idString.trim()).intValue();
      for (int i = 0; i < edcs.size(); i++) {
        EventDefinitionCRFBean edc = (EventDefinitionCRFBean) edcs.get(i);
        if (edc.getCrfId() == id) {
          edc.setStatus(Status.AVAILABLE);
          crfName = edc.getCrfName();
        }
        
      }
      session.setAttribute("eventDefinitionCRFs", edcs);
      addPageMessage(crfName + " has been restored.");
      forwardPage(Page.UPDATE_EVENT_DEFINITION1);
    }

  }
}

