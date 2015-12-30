/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.login;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * @author jxu
 *
 * Processes the request of changing current study
 */
public class ChangeStudyServlet extends SecureController {
    /**
     * Checks whether the user has the correct privilege
     */

    Locale locale;

    // < ResourceBundlerestext;

    @Override
    public void mayProceed() throws InsufficientPermissionException {

        locale = request.getLocale();
        // < restext =
        // ResourceBundle.getBundle("org.akaza.openclinica.i18n.notes",locale);

    }

    @Override
    public void processRequest() throws Exception {

        String action = request.getParameter("action");// action sent by user
        UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
        StudyDAO sdao = new StudyDAO(sm.getDataSource());

        ArrayList studies = udao.findStudyByUser(ub.getName(), (ArrayList) sdao.findAll());

        ArrayList validStudies = new ArrayList();
        for (int i=0; i<studies.size();i++) {
            StudyUserRoleBean sr = (StudyUserRoleBean)studies.get(i);
            StudyBean s = (StudyBean) sdao.findByPK(sr.getStudyId());
            if (s != null && s.getStatus().equals(Status.AVAILABLE)) {
                validStudies.add(sr);
            }
            
        }
        if (StringUtil.isBlank(action)) {
            request.setAttribute("studies", validStudies);

            forwardPage(Page.CHANGE_STUDY);
        } else {
            if ("confirm".equalsIgnoreCase(action)) {
                logger.info("confirm");
                confirmChangeStudy(studies);

            } else if ("submit".equalsIgnoreCase(action)) {
                logger.info("submit");
                changeStudy();
            }
        }

    }

    private void confirmChangeStudy(ArrayList studies) throws Exception {
        Validator v = new Validator(request);
        FormProcessor fp = new FormProcessor(request);
        v.addValidation("studyId", Validator.IS_AN_INTEGER);

        errors = v.validate();

        if (!errors.isEmpty()) {
            request.setAttribute("studies", studies);
            forwardPage(Page.CHANGE_STUDY);
        } else {
            int studyId = fp.getInt("studyId");
            logger.info("new study id:" + studyId);
            for (int i = 0; i < studies.size(); i++) {
                StudyUserRoleBean studyWithRole = (StudyUserRoleBean) studies.get(i);
                if (studyWithRole.getStudyId() == studyId) {
                    request.setAttribute("studyId", new Integer(studyId));
                    session.setAttribute("studyWithRole", studyWithRole);
                    forwardPage(Page.CHANGE_STUDY_CONFIRM);
                    return;
                }
            }
            addPageMessage(restext.getString("no_study_selected"));
            forwardPage(Page.CHANGE_STUDY);
        }
    }

    private void changeStudy() throws Exception {
        Validator v = new Validator(request);
        FormProcessor fp = new FormProcessor(request);
        int studyId = fp.getInt("studyId");
        int prevStudyId = currentStudy.getId();

        StudyDAO sdao = new StudyDAO(sm.getDataSource());
        StudyBean current = (StudyBean) sdao.findByPK(studyId);

        // reset study parameters -jxu 02/09/2007
        StudyParameterValueDAO spvdao = new StudyParameterValueDAO(sm.getDataSource());

        ArrayList studyParameters = spvdao.findParamConfigByStudy(current);

        current.setStudyParameters(studyParameters);

        StudyConfigService scs = new StudyConfigService(sm.getDataSource());
        if (current.getParentStudyId() <= 0) {// top study
            scs.setParametersForStudy(current);

        } else {
            // YW <<
            if (current.getParentStudyId() > 0) {
                current.setParentStudyName(((StudyBean) sdao.findByPK(current.getParentStudyId())).getName());
                
            }
            // YW 06-12-2007>>
            scs.setParametersForSite(current);

        }
        if (current.getStatus().equals(Status.DELETED) || current.getStatus().equals(Status.AUTO_DELETED)) {
            session.removeAttribute("studyWithRole");
            addPageMessage(restext.getString("study_choosed_removed_restore_first"));
        } else {
            session.setAttribute("study", current);
            currentStudy = current;
            // change user's active study id
            UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
            ub.setActiveStudyId(current.getId());
            ub.setUpdater(ub);
            ub.setUpdatedDate(new java.util.Date());
            udao.update(ub);

            if (current.getParentStudyId() > 0) {
                /*The Role decription will be set depending on whether the user logged in at
                study lever or site level. issue-2422*/
                List roles = Role.toArrayList();
                for (Iterator it = roles.iterator(); it.hasNext();) {
                    Role role = (Role) it.next();
                    switch (role.getId()) {
                        case 2: role.setDescription("site_Study_Coordinator");
                            break;
                        case 3: role.setDescription("site_Study_Director");
                            break;
                        case 4: role.setDescription("site_investigator");
                            break;
                        case 5: role.setDescription("site_Data_Entry_Person");
                            break;
                        case 6: role.setDescription("site_monitor");
                            break;
                        default: logger.info("No role matched when setting role description");
                    }
                }
            } else {
                /*If the current study is a site, we will change the role description. issue-2422*/
                List roles = Role.toArrayList();
                for (Iterator it = roles.iterator(); it.hasNext();) {
                    Role role = (Role) it.next();
                    switch (role.getId()) {
                        case 2: role.setDescription("Study_Coordinator");
                            break;
                        case 3: role.setDescription("Study_Director");
                            break;
                        case 4: role.setDescription("investigator");
                            break;
                        case 5: role.setDescription("Data_Entry_Person");
                            break;
                        case 6: role.setDescription("monitor");
                            break;
                        default: logger.info("No role matched when setting role description");
                    }
                }
            }

            session.setAttribute("userRole", session.getAttribute("studyWithRole"));
            currentRole = (StudyUserRoleBean) session.getAttribute("studyWithRole");
            session.removeAttribute("studyWithRole");
            addPageMessage(restext.getString("current_study_changed_succesfully"));
        }
        ub.incNumVisitsToMainMenu();
        // YW 2-18-2008, if study has been really changed <<
        if (prevStudyId != studyId) {
            session.removeAttribute("eventsForCreateDataset");
        }
        // YW >>
        forwardPage(Page.MENU);

    }

}
