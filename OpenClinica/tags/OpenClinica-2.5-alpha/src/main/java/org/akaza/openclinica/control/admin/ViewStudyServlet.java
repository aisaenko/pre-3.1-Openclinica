/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.managestudy.DisplayStudySubjectBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

import java.util.ArrayList;

/**
 * @author jxu
 *
 * Processes the reuqest of 'view study details'
 */
public class ViewStudyServlet extends SecureController {
    /**
     * Checks whether the user has the correct privilege
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {
        if (ub.isSysAdmin()) {
            return;
        }

        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)
            || currentRole.getRole().equals(Role.INVESTIGATOR) || currentRole.getRole().equals(Role.RESEARCHASSISTANT)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_admin"), "1");

    }

    @Override
    public void processRequest() throws Exception {

        StudyDAO sdao = new StudyDAO(sm.getDataSource());
        FormProcessor fp = new FormProcessor(request);
        int studyId = fp.getInt("id");
        if (studyId == 0) {
            addPageMessage(respage.getString("please_choose_a_study_to_view"));
            forwardPage(Page.STUDY_LIST_SERVLET);
        } else {
            String viewFullRecords = fp.getString("viewFull");
            StudyBean study = (StudyBean) sdao.findByPK(studyId);

            StudyConfigService scs = new StudyConfigService(sm.getDataSource());
            study = scs.setParametersForStudy(study);

            request.setAttribute("studyToView", study);
            if ("yes".equalsIgnoreCase(viewFullRecords)) {
                // find all sites
                ArrayList sites = (ArrayList) sdao.findAllByParent(studyId);

                // find all user and roles in the study, include ones in sites
                UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
                ArrayList userRoles = udao.findAllUsersByStudy(currentStudy.getId());

                // find all subjects in the study, include ones in sites
                StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
                EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
                StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
                StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
                ArrayList subjects = ssdao.findAllByStudy(study);

                ArrayList displayStudySubs = new ArrayList();
                for (int i = 0; i < subjects.size(); i++) {
                    StudySubjectBean studySub = (StudySubjectBean) subjects.get(i);
                    // find all events
                    ArrayList events = sedao.findAllByStudySubject(studySub);

                    // find all eventcrfs for each event
                    EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());

                    ArrayList displayEvents = new ArrayList();
                    for (int j = 0; j < events.size(); j++) {
                        StudyEventBean event = (StudyEventBean) events.get(j);

                        StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(event.getStudyEventDefinitionId());

                        // find all crfs in the definition
                        ArrayList eventDefinitionCRFs = edcdao.findAllByEventDefinitionId(sed.getId());

                    }

                    DisplayStudySubjectBean dssb = new DisplayStudySubjectBean();
                    dssb.setStudyEvents(events);
                    dssb.setStudySubject(studySub);
                    displayStudySubs.add(dssb);
                }

                // find all events in the study, include ones in sites
                ArrayList definitions = seddao.findAllByStudy(study);

                for (int i = 0; i < definitions.size(); i++) {
                    StudyEventDefinitionBean def = (StudyEventDefinitionBean) definitions.get(i);
                    ArrayList crfs = edcdao.findAllActiveByEventDefinitionId(def.getId());
                    def.setCrfNum(crfs.size());

                }

                request.setAttribute("sitesToView", sites);
                request.setAttribute("siteNum", sites.size() + "");

                request.setAttribute("userRolesToView", userRoles);
                request.setAttribute("userNum", userRoles.size() + "");

                request.setAttribute("subjectsToView", displayStudySubs);
                request.setAttribute("subjectNum", subjects.size() + "");

                request.setAttribute("definitionsToView", definitions);
                request.setAttribute("defNum", definitions.size() + "");
                forwardPage(Page.VIEW_FULL_STUDY);

            } else {
                forwardPage(Page.VIEW_STUDY);
            }
        }
    }

    @Override
    protected String getAdminServlet() {
        if (ub.isSysAdmin()) {
            return SecureController.ADMIN_SERVLET_CODE;
        } else {
            return "";
        }
    }

}
