/*
 * Created on Jan 21, 2005
 */
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.managestudy.ListStudySubjectServlet;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author ssachs
 */
public class ListStudySubjectsSubmitServlet extends ListStudySubjectServlet {

	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.managestudy.ListStudySubjectServlet#getJSP()
	 */
	protected Page getJSP() {
		return Page.SUBMIT_DATA;
	}

	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
	 */
	protected void mayProceed() throws InsufficientPermissionException {
		if (ub.isSysAdmin()) {
			return;
		}
		
		Role r = currentRole.getRole(); 
		if (r.equals(Role.STUDYDIRECTOR)
				|| r.equals(Role.COORDINATOR)
				|| r.equals(Role.INVESTIGATOR)
				|| r.equals(Role.RESEARCHASSISTANT)) {
			return;
		}
		
		addPageMessage("You don't have correct privilege in your current active study. "
				+ "Please change your active study or contact your sysadmin.");
		throw new InsufficientPermissionException(Page.MENU_SERVLET,
				"may not submit data", "1");
	}

	
	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.managestudy.ListStudySubjectServlet#getBaseURL()
	 */
	protected String getBaseURL() {
		return "ListStudySubjectsSubmit";
	}
}
