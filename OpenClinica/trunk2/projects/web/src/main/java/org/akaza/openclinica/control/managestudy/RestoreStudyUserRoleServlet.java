/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.Date;

/**
 * @author jxu
 *
 * Restores a removed study user role
 */
public class RestoreStudyUserRoleServlet extends SecureController {
    @Override
    public void processRequest() throws Exception {

        UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
        String name = request.getParameter("name");
        String studyIdString = request.getParameter("studyId");
        if (StringUtil.isBlank(name) || StringUtil.isBlank(studyIdString)) {
            addPageMessage(respage.getString("please_choose_a_user_to_restore_his_role"));
            forwardPage(Page.LIST_USER_IN_STUDY_SERVLET);
        } else {
            String action = request.getParameter("action");
            UserAccountBean user = (UserAccountBean) udao.findByUserName(name);
            if ("confirm".equalsIgnoreCase(action)) {
                int studyId = Integer.valueOf(studyIdString.trim()).intValue();

                request.setAttribute("user", user);

                StudyUserRoleBean uRole = udao.findRoleByUserNameAndStudyId(name, studyId);
                request.setAttribute("uRole", uRole);

                StudyDAO sdao = new StudyDAO(sm.getDataSource());
                StudyBean study = (StudyBean) sdao.findByPK(studyId);
                request.setAttribute("uStudy", study);
                forwardPage(Page.RESTORE_USER_ROLE_IN_STUDY);
            } else {
                // restore role
                FormProcessor fp = new FormProcessor(request);
                String userName = fp.getString("name");
                int studyId = fp.getInt("studyId");
                int roleId = fp.getInt("roleId");
                StudyUserRoleBean sur = new StudyUserRoleBean();
                sur.setName(userName);
                sur.setRole(Role.get(roleId));
                sur.setStudyId(studyId);
                sur.setStatus(Status.AVAILABLE);
                sur.setUpdater(ub);
                sur.setUpdatedDate(new Date());
                udao.updateStudyUserRole(sur, userName);
                addPageMessage(sendEmail(user, sur));
                forwardPage(Page.LIST_USER_IN_STUDY_SERVLET);

            }

        }
    }

    /**
     * Send email to the user, director and administrator
     *
     * @param request
     * @param response
     */
    private String sendEmail(UserAccountBean u, StudyUserRoleBean sub) throws Exception {

        StudyDAO sdao = new StudyDAO(sm.getDataSource());
        StudyBean study = (StudyBean) sdao.findByPK(sub.getStudyId());
        logger.info("Sending email...");
        String body =
            u.getFirstName() + " " + u.getLastName() + "(" + resword.getString("username") + ": " + u.getName() + ") "
                + respage.getString("has_been_restored_to_the_study_site") + " " + study.getName() + respage.getString("with_the_role") + " "
                + sub.getRoleName() + ". " + respage.getString("the_user_is_now_be_able_to_access_study");

        sendEmail(u.getEmail().trim(), respage.getString("restore_user_role"), body, false);
        sendEmail(ub.getEmail().trim(), respage.getString("restore_user_role"), body, false);
        sendEmail(EmailEngine.getAdminEmail(), respage.getString("restore_user_role"), body, false);

        return body;

    }

}
