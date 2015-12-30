/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.akaza.openclinica.web.bean.StudyUserRoleRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Lists all the users in a study
 * 
 * @author jxu
 * 
 * @version CVS: $Id: ListStudyUserServlet.java 9771 2007-08-28 15:26:26Z
 *          thickerson $
 * 
 */
public class ListStudyUserServlet extends SecureController {

    @Override
    public void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);
        UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
        ArrayList users = udao.findAllUsersByStudy(currentStudy.getId());

        EntityBeanTable table = fp.getEntityBeanTable();
        ArrayList allStudyUserRows = StudyUserRoleRow.generateRowsFromBeans(users);

        String[] columns =
            { resword.getString("user_name"), resword.getString("first_name"), resword.getString("last_name"), resword.getString("role"),
                resword.getString("study_name"), resword.getString("status"), resword.getString("actions") };
        table.setColumns(new ArrayList(Arrays.asList(columns)));
        table.hideColumnLink(6);
        table.setQuery("ListStudyUser", new HashMap());
        table.addLink(restext.getString("assign_new_user_to_current_study"), "AssignUserToStudy");
        table.setRows(allStudyUserRows);
        table.computeDisplay();

        request.setAttribute("table", table);
        request.setAttribute("siteRoleMap", Role.siteRoleMap);
        request.setAttribute("studyRoleMap", Role.studyRoleMap);
        request.setAttribute("study", currentStudy);
        // request.setAttribute("users", users);
        forwardPage(Page.LIST_USER_IN_STUDY);

    }

}
