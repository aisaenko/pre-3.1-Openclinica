/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.exception.*;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.bean.core.*;
import org.akaza.openclinica.bean.login.*;

/**
 * @author ssachs
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SubmitDataServlet extends SecureController {
	
	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.core.SecureController#processRequest()
	 */
	protected void processRequest() throws Exception {
		forwardPage(Page.SUBMIT_DATA);
	}

	public static boolean maySubmitData(UserAccountBean ub, StudyUserRoleBean currentRole) {
		if (currentRole != null) {
			Role r = currentRole.getRole();
			if ((r != null) &&
					(r.equals(Role.COORDINATOR)
							|| r.equals(Role.STUDYDIRECTOR)
							|| r.equals(Role.INVESTIGATOR)
							|| r.equals(Role.RESEARCHASSISTANT))) {
				return true;
			}
		}

		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
	 */
	protected void mayProceed() throws InsufficientPermissionException {
		String exceptionName = "no permission to submit data";
		String noAccessMessage = "You may not submit data for this study.  Please change your active study or contact the System Administrator.";
		
		if (maySubmitData(ub, currentRole)) {
			return ;
		}
		
		addPageMessage(noAccessMessage);
		throw new InsufficientPermissionException(Page.MENU, exceptionName, "1");
	}
	
}