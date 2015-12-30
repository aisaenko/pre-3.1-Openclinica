package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

public class ViewAllJobsServlet extends SecureController {

    @Override
    protected void processRequest() throws Exception {
        // TODO Auto-generated method stub
        forwardPage(Page.VIEW_ALL_JOBS);
    }

}
