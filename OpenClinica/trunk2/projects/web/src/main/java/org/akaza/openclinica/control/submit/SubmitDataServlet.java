/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.Locale;

/**
 * @author ssachs
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SubmitDataServlet extends SecureController {

    Locale locale;

    // < ResourceBundleresexception,respage;

    /*
     * (non-Javadoc)
     *
     * @see org.akaza.openclinica.control.core.SecureController#processRequest()
     */
    @Override
    protected void processRequest() throws Exception {
        forwardPage(Page.SUBMIT_DATA);
    }

    public static boolean mayViewData(UserAccountBean ub, StudyUserRoleBean currentRole) {
        if (currentRole != null) {
            Role r = currentRole.getRole();
            if (r != null && (r.equals(Role.COORDINATOR) || r.equals(Role.STUDYDIRECTOR) ||
                    r.equals(Role.INVESTIGATOR) || r.equals(Role.RESEARCHASSISTANT) ||r.equals(Role.MONITOR) )) {
                return true;
            }
        }

        return false;
    }
    
    
    public static boolean maySubmitData(UserAccountBean ub, StudyUserRoleBean currentRole) {
        if (currentRole != null) {
            Role r = currentRole.getRole();
            if (r != null && (r.equals(Role.COORDINATOR) || r.equals(Role.STUDYDIRECTOR) ||
                    r.equals(Role.INVESTIGATOR) || r.equals(Role.RESEARCHASSISTANT))) {
                return true;
            }
        }

        return false;
    }

}
