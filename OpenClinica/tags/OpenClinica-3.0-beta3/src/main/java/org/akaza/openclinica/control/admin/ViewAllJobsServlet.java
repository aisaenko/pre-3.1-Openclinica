package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

public class ViewAllJobsServlet extends SecureController {

    @Override
    protected void mayProceed() throws InsufficientPermissionException {
        // TODO Auto-generated method stub
        if (ub.isSysAdmin()) {
            return;
        }
        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {// ?

            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU, resexception.getString("not_allowed_access_extract_data_servlet"), "1");// TODO

    }

    @Override
    protected void processRequest() throws Exception {
        // TODO Auto-generated method stub
        forwardPage(Page.VIEW_ALL_JOBS);
    }

}
