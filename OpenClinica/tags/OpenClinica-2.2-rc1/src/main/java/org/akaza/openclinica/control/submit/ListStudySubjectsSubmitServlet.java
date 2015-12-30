/*
 * Created on Jan 21, 2005
 */
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.managestudy.ListStudySubjectServlet;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author ssachs
 */
public class ListStudySubjectsSubmitServlet extends ListStudySubjectServlet {
	
	Locale locale;
	//<  ResourceBundleresexception,respage;

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
		
		  locale = request.getLocale();
		  //< resexception=ResourceBundle.getBundle("org.akaza.openclinica.i18n.exceptions",locale);  
		  //< respage = ResourceBundle.getBundle("org.akaza.openclinica.i18n.page_messages",locale);
		
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
		
		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU_SERVLET,
				resexception.getString("may_not_submit_data"), "1");
	}

	
	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.managestudy.ListStudySubjectServlet#getBaseURL()
	 */
	protected String getBaseURL() {
		return "ListStudySubjectsSubmit";
	}
}
