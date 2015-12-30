package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;
/**
 * 
 * @author thickerson
 *
 */
public class CreateJobImportServlet extends SecureController {

	@Override
	protected void mayProceed() throws InsufficientPermissionException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void processRequest() throws Exception {
		// TODO multi stage servlet to generate import jobs
		// validate form, create job and return to view jobs servlet
		request.setAttribute("filePath", SQLInitServlet.getField("filePath"));
		forwardPage(Page.CREATE_JOB_IMPORT);
		// TODO make the above move to the correct page
	}

}
