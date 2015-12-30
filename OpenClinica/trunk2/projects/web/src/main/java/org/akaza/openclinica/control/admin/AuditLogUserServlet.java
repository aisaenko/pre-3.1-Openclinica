/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.admin.AuditEventDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.AuditEventRow;
import org.akaza.openclinica.web.bean.EntityBeanTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author thickerson
 *
 *
 */
public class AuditLogUserServlet extends SecureController {

    Locale locale;
    // < ResourceBundleresword,resexception;

    public static final String ARG_USERID = "userLogId";

    public static String getLink(int userId) {
        return "AuditLogUser?userLogId=" + userId;
    }

    /*
     * (non-Javadoc) Assume that we get the user id automatically. We will jump
     * from the edit user page if the user is an admin, they can get to see the
     * users' log
     *
     * @see org.akaza.openclinica.control.core.SecureController#processRequest()
     */
    @Override
    protected void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);
        int userId = fp.getInt(ARG_USERID);
        if (userId == 0) {
            Integer userIntId = (Integer) session.getAttribute(ARG_USERID);
            userId = userIntId.intValue();
        } else {
            session.setAttribute(ARG_USERID, new Integer(userId));
        }
        AuditEventDAO aeDAO = new AuditEventDAO(sm.getDataSource());
        ArrayList al = aeDAO.findAllByUserId(userId);

        EntityBeanTable table = fp.getEntityBeanTable();
        ArrayList allRows = AuditEventRow.generateRowsFromBeans(al);

        // String[] columns = { "Date and Time", "Action", "Entity/Operation",
        // "Record ID", "Changes and Additions","Other Info" };
        // table.setColumns(new ArrayList(Arrays.asList(columns)));
        // table.hideColumnLink(4);
        // table.hideColumnLink(1);
        // table.hideColumnLink(5);
        // table.setQuery("AuditLogUser?userLogId="+userId, new HashMap());
        String[] columns =
            { resword.getString("date_and_time"), resword.getString("action_message"), resword.getString("entity_operation"), resword.getString("study_site"),
                resword.getString("study_subject_ID"), resword.getString("changes_and_additions"),
                // "Other Info",
                resword.getString("actions") };
        table.setColumns(new ArrayList(Arrays.asList(columns)));
        table.setAscendingSort(false);
        table.hideColumnLink(1);
        table.hideColumnLink(5);
        table.hideColumnLink(6);
        // table.hideColumnLink(7);
        table.setQuery("AuditLogUser?userLogId=" + userId, new HashMap());
        table.setRows(allRows);

        table.computeDisplay();

        request.setAttribute("table", table);
        UserAccountDAO uadao = new UserAccountDAO(sm.getDataSource());
        UserAccountBean uabean = (UserAccountBean) uadao.findByPK(userId);
        request.setAttribute("auditUserBean", uabean);
        forwardPage(Page.AUDIT_LOG_USER);
    }

    @Override
    protected String getAdminServlet() {
        return SecureController.ADMIN_SERVLET_CODE;
    }

}
