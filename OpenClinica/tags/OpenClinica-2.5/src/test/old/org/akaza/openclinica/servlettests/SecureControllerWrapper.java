package org.akaza.openclinica.servlettests;

import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

/**
 * A wrapper class designed to allow the implementation of an "out of container"
 * JUnit test case for abstract SecureController.
 *
 * @author Bruce W. Perry 01/2008
 * @see test.org.akaza.openclinica.servlettests.SecureControllerServletTest
 * @see org.akaza.openclinica.control.core.SecureController.SecureControllerTestDelegate
 */
public class SecureControllerWrapper extends SecureController {

    @Override
    protected void processRequest() throws Exception {

    }

    @Override
    protected void mayProceed() throws InsufficientPermissionException {

    }

    // For out of container testing...
    public void setRequest(HttpServletRequest _request) {
        this.request = _request;
    }

    public void setResponse(HttpServletResponse _response) {
        this.response = _response;
    }

    public void setSessionManager(SessionManager manager) {
        this.sm = manager;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    public void forwardPageWrapper(Page jspPage, boolean checkTrail) {
        super.forwardPage(jspPage, checkTrail);
    }

    public boolean entityIncludedWrapper(int entityId, String userName, AuditableEntityDAO adao, DataSource ds) {
        return super.entityIncluded(entityId, userName, adao, ds);
    }

}
