/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author ssachs
 */
public class ListStudySubjectsManageServlet extends ListStudySubjectServlet {
	
	/**
	 * Checks whether the user has the right permission to proceed function
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
	
	
	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.managestudy.ListStudySubjectServlet#getJSP()
	 */
	protected Page getJSP() {
		return Page.LIST_STUDY_SUBJECT;
	}
	
	
	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.managestudy.ListStudySubjectServlet#getBaseURL()
	 */
	protected String getBaseURL() {
		return "ListStudySubject";
	}
}
