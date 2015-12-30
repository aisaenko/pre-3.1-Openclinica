/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.exception.InsufficientPermissionException;

/**
 * Checks user's password with the one int the session
 *
 * @author shamim
 *
 */
public class MatchPasswordServlet extends SecureController {
    @Override
    protected void processRequest() throws Exception {
        String password = request.getParameter("password");
        logger.info("password [" + password + "]");
        if (password != null && !password.equals("")) {
            String encodedUserPass = org.akaza.openclinica.core.SecurityManager.getInstance().encrytPassword(password);
            UserAccountBean ub = (UserAccountBean) session.getAttribute("userBean");
            logger.info("session pass[" + ub.getPasswd() + "]");
            logger.info("user pass[" + encodedUserPass + "]");
            if (ub.getPasswd().equals(encodedUserPass)) {
                response.getWriter().print("true");
            } else {
                response.getWriter().print("false");
            }
            return;
        }
    }

    @Override
    protected void mayProceed() throws InsufficientPermissionException {
        return;
    }
}
