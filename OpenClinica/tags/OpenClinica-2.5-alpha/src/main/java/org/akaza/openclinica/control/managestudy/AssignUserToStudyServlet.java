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
import org.akaza.openclinica.bean.login.UserAccountRow;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

/**
 * Processes request to assign a user to a study
 *
 * @author jxu
 *
 */
public class AssignUserToStudyServlet extends SecureController {

    /**
     *
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {
        if (ub.isSysAdmin()) {
            return;
        }

        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");

    }

    @Override
    public void processRequest() throws Exception {

        String action = request.getParameter("action");
        ArrayList users = findUsers();

        if (StringUtil.isBlank(action)) {
            FormProcessor fp = new FormProcessor(request);
            EntityBeanTable table = fp.getEntityBeanTable();
            ArrayList allRows = UserAccountRow.generateRowsFromBeans(users);

            String[] columns =
                { resword.getString("user_name"), resword.getString("first_name"), resword.getString("last_name"), resword.getString("role"),
                    resword.getString("selected"), resword.getString("notes") };
            table.setColumns(new ArrayList(Arrays.asList(columns)));
            table.hideColumnLink(3);
            table.hideColumnLink(4);
            table.hideColumnLink(5);
            table.setQuery("AssignUserToStudy", new HashMap());
            table.setRows(allRows);
            table.computeDisplay();

            request.setAttribute("table", table);
            // request.setAttribute("studyUsers", users);
            ArrayList roles = Role.toArrayList();
            roles.remove(Role.ADMIN); // admin is not a user role, only used
            // for
            // tomcat
            request.setAttribute("roles", roles);
            forwardPage(Page.STUDY_USER_LIST);
        } else {
            if ("submit".equalsIgnoreCase(action)) {
                addUser(users);
            }
        }
    }

    private void addUser(ArrayList users) throws Exception {
        String pageMass = "";
        UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
        FormProcessor fp = new FormProcessor(request);
        for (int i = 0; i < users.size(); i++) {
            int id = fp.getInt("id" + i);
            String firstName = fp.getString("firstName" + i);
            String lastName = fp.getString("lastName" + i);
            String name = fp.getString("name" + i);
            String email = fp.getString("email" + i);
            int roleId = fp.getInt("activeStudyRoleId" + i);
            String checked = fp.getString("selected" + i);
            // logger.info("selected:" + checked);
            if (!StringUtil.isBlank(checked) && "yes".equalsIgnoreCase(checked.trim())) {
                logger.info("one user selected");
                UserAccountBean u = new UserAccountBean();
                u.setId(id);
                u.setLastName(lastName);
                u.setFirstName(firstName);
                u.setName(name);
                u.setEmail(email);
                u.setActiveStudyId(ub.getActiveStudyId());
                u.setOwnerId(id);

                StudyUserRoleBean sub = new StudyUserRoleBean();
                sub.setRoleName(Role.get(roleId).getName());
                sub.setStudyId(currentStudy.getId());
                sub.setStatus(Status.AVAILABLE);
                sub.setOwner(ub);
                udao.createStudyUserRole(u, sub);
                logger.info("one user added");

                pageMass = pageMass + sendEmail(u, sub);

            }
        }
        if ("".equals(pageMass)) {
            addPageMessage(respage.getString("no_new_user_assigned_to_study"));
        } else {
            addPageMessage(pageMass);
        }
        forwardPage(Page.LIST_USER_IN_STUDY_SERVLET);

    }

    /**
     * Find all users in the system
     *
     * @return
     */
    private ArrayList findUsers() {
        UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
        ArrayList userList = (ArrayList) udao.findAll();
        ArrayList userAvailable = new ArrayList();
        for (int i = 0; i < userList.size(); i++) {
            UserAccountBean u = (UserAccountBean) userList.get(i);
            int activeStudyId = currentStudy.getId();
            StudyUserRoleBean sub = udao.findRoleByUserNameAndStudyId(u.getName(), activeStudyId);
            if (!sub.isActive()) {// doesn't have a role in the current study
                sub.setRole(Role.RESEARCHASSISTANT);
                sub.setStudyId(activeStudyId);
                u.setActiveStudyId(activeStudyId);
                u.addRole(sub);
                u.setStatus(Status.AVAILABLE);
                // logger.info("\n activeStudyRole:" +
                // u.getActiveStudyRole().getName());

                // try to find whether this user has role in site or parent
                if (currentStudy.getParentStudyId() > 0) {// this is a site
                    StudyUserRoleBean subParent = udao.findRoleByUserNameAndStudyId(u.getName(), currentStudy.getParentStudyId());
                    if (subParent.isActive()) {
                        u.setNotes(subParent.getRole().getDescription() + " " + respage.getString("in_parent_study"));
                    }

                } else {
                    // find all the sites for this top study
                    StudyDAO sdao = new StudyDAO(sm.getDataSource());
                    ArrayList sites = (ArrayList) sdao.findAllByParent(currentStudy.getId());
                    String notes = "";
                    for (int j = 0; j < sites.size(); j++) {
                        StudyBean site = (StudyBean) sites.get(j);
                        StudyUserRoleBean subSite = udao.findRoleByUserNameAndStudyId(u.getName(), site.getId());
                        if (subSite.isActive()) {
                            notes = notes + subSite.getRole().getDescription() + respage.getString("in_site") + ":" + site.getName() + "; ";
                        }
                    }
                    u.setNotes(notes);
                }

            } else {
                // already have a role in the current study
                sub.setStudyId(activeStudyId);
                u.setActiveStudyId(activeStudyId);
                u.addRole(sub);
                u.setStatus(Status.UNAVAILABLE);
            }
            userAvailable.add(u);
        }
        return userAvailable;

    }

    /**
     * Send email to the user, director and administrator
     *
     * @param request
     * @param response
     */
    private String sendEmail(UserAccountBean u, StudyUserRoleBean sub) throws Exception {

        logger.info("Sending email...");
        String body =
            u.getFirstName() + " " + u.getLastName() + "(" + resword.getString("username") + ": " + u.getName() + ") "
                + respage.getString("has_been_assigned_to_the_study_site") + ((StudyBean) session.getAttribute("study")).getName() + " "
                + resword.getString("as") + sub.getRoleName() + ". ";

        try {
            EmailEngine em = new EmailEngine(EmailEngine.getSMTPHost());
            // to user
            em.process(u.getEmail().trim(), EmailEngine.getAdminEmail(), respage.getString("new_user_added_to_study"), body);

            em = null;// otherwise keep sending emails??
            em = new EmailEngine(EmailEngine.getSMTPHost());
            // to study director
            em.process(ub.getEmail().trim(), EmailEngine.getAdminEmail(), respage.getString("new_user_added_to_study"), body);

            em = null;
            em = new EmailEngine(EmailEngine.getSMTPHost());
            // to admin
            em.process(EmailEngine.getAdminEmail(), EmailEngine.getAdminEmail(), respage.getString("new_user_added_to_study"), body);
        } catch (SendFailedException se) {
            addPageMessage(respage.getString("mail_cannot_be_sent_for_some_reasons"));
        } catch (MessagingException me) {
            addPageMessage(respage.getString("mail_cannot_be_sent_to_admin"));
        }
        return body;

    }

}
