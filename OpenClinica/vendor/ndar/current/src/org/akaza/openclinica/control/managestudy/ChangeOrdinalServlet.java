/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * A super class of ChangeDefinitionOrdinal and Change DefinitionCRFOrdinal Servlets
 * 
 * @author jxu 
 */
public abstract class ChangeOrdinalServlet extends SecureController {
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
    throw new InsufficientPermissionException(Page.MENU_SERVLET,
        "not study director", "1");

  }   
 
  


}
