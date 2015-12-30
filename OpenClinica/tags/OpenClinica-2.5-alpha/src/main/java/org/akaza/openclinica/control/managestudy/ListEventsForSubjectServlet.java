/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudySubjectBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudySubjectRow;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author jxu
 */
public class ListEventsForSubjectServlet extends SecureController {

    Locale locale;

    // < ResourceBundleresword;
    /*
     * (non-Javadoc)
     *
     * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
     */
    @Override
    protected void mayProceed() throws InsufficientPermissionException {

        locale = request.getLocale();
        // < resword =
        // ResourceBundle.getBundle("org.akaza.openclinica.i18n.words",locale);

        if (ub.isSysAdmin()) {
            return;
        }

        Role r = currentRole.getRole();
        if (r.equals(Role.STUDYDIRECTOR) || r.equals(Role.COORDINATOR) || r.equals(Role.INVESTIGATOR) || r.equals(Role.RESEARCHASSISTANT)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
    }

    @Override
    public void processRequest() throws Exception {

        FormProcessor fp = new FormProcessor(request);
        // checks which module the requests are from
        String module = fp.getString(MODULE);
        request.setAttribute(MODULE, module);

        int definitionId = fp.getInt("defId");
        int tabId = fp.getInt("tab");
        if (definitionId <= 0) {
            addPageMessage(respage.getString("please_choose_an_ED_ta_to_vies_details"));
            forwardPage(Page.LIST_STUDY_SUBJECT_SERVLET);
            return;
        }

        StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
        StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(definitionId);

        StudySubjectDAO sdao = new StudySubjectDAO(sm.getDataSource());
        StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());

        SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(sm.getDataSource());
        StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(sm.getDataSource());

        EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
        EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
        CRFDAO crfdao = new CRFDAO(sm.getDataSource());

        // find all the groups in the current study
        ArrayList studyGroupClasses = sgcdao.findAllActiveByStudy(currentStudy);

        // information for the event tabs
        ArrayList allDefs = seddao.findAllActiveByStudy(currentStudy);
        if (currentStudy.getParentStudyId() > 0) {
            StudyDAO stdao = new StudyDAO(sm.getDataSource());
            StudyBean parent = (StudyBean) stdao.findByPK(currentStudy.getParentStudyId());
            allDefs = seddao.findAllActiveByStudy(parent);
        }

        ArrayList eventDefinitionCRFs = edcdao.findAllActiveByEventDefinitionId(definitionId);

        for (int i = 0; i < eventDefinitionCRFs.size(); i++) {
            EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
            CRFBean crf = (CRFBean) crfdao.findByPK(edc.getCrfId());
            edc.setCrf(crf);

        }
        request.setAttribute("allDefsArray", allDefs);
        request.setAttribute("allDefsNumber", new Integer(allDefs.size()));
        request.setAttribute("groupSize", new Integer(studyGroupClasses.size()));
        request.setAttribute("eventDefCRFSize", new Integer(eventDefinitionCRFs.size()));
        request.setAttribute("tabId", new Integer(tabId));
        request.setAttribute("studyEventDef", sed);
        request.setAttribute("eventDefCRFs", eventDefinitionCRFs);

        // find all the subjects in current study
        ArrayList subjects = sdao.findAllByStudyId(currentStudy.getId());

        ArrayList displayStudySubs = new ArrayList();
        for (int i = 0; i < subjects.size(); i++) {
            StudySubjectBean studySub = (StudySubjectBean) subjects.get(i);

            ArrayList groups = (ArrayList) sgmdao.findAllByStudySubject(studySub.getId());

            ArrayList subGClasses = new ArrayList();
            for (int j = 0; j < studyGroupClasses.size(); j++) {
                StudyGroupClassBean sgc = (StudyGroupClassBean) studyGroupClasses.get(j);
                boolean hasClass = false;
                for (int k = 0; k < groups.size(); k++) {
                    SubjectGroupMapBean sgmb = (SubjectGroupMapBean) groups.get(k);
                    if (sgmb.getGroupClassName().equalsIgnoreCase(sgc.getName())) {
                        subGClasses.add(sgmb);
                        hasClass = true;
                        break;
                    }

                }
                if (!hasClass) {
                    subGClasses.add(new SubjectGroupMapBean());
                }

            }

            // find all eventcrfs for each event, for each event tab
            ArrayList displaySubjectEvents = new ArrayList();

            ArrayList displayEvents = new ArrayList();
            ArrayList events = sedao.findAllByStudySubjectAndDefinition(studySub, sed);

            for (int k = 0; k < events.size(); k++) {
                StudyEventBean seb = (StudyEventBean) events.get(k);
                DisplayStudyEventBean dseb = ListStudySubjectServlet.getDisplayStudyEventsForStudySubject(studySub, seb, sm.getDataSource(), ub, currentRole);

                // ArrayList eventCRFs = ecdao.findAllByStudyEvent(seb);
                // ArrayList al =
                // ViewStudySubjectServlet.getUncompletedCRFs(sm.getDataSource(),
                // eventDefinitionCRFs, eventCRFs);
                // dseb.getUncompletedCRFs().add(al);
                displayEvents.add(dseb);

            }

            ArrayList al = new ArrayList();
            for (int k = 0; k < displayEvents.size(); k++) {
                DisplayStudyEventBean dseb = (DisplayStudyEventBean) displayEvents.get(k);
                ArrayList eventCRFs = dseb.getDisplayEventCRFs();
                // ArrayList uncompletedCRFs = dseb.getUncompletedCRFs();

                for (int a = 0; a < eventDefinitionCRFs.size(); a++) {
                    EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFs.get(a);
                    int crfId = edc.getCrfId();
                    boolean hasCRF = false;
                    for (int b = 0; b < eventCRFs.size(); b++) {
                        DisplayEventCRFBean decb = (DisplayEventCRFBean) eventCRFs.get(b);
                        // System.out.println("eventCRF" +
                        // decb.getEventCRF().getId() +
                        // decb.getStage().getName() );
                        if (decb.getEventCRF().getCrf().getId() == crfId) {
                            dseb.getAllEventCRFs().add(decb);
                            // System.out.println("hasCRf" + crfId +
                            // decb.getEventCRF().getCrf().getName());

                            hasCRF = true;
                            break;
                        }
                    }
                    if (hasCRF == false) {
                        DisplayEventCRFBean db = new DisplayEventCRFBean();
                        db.setEventDefinitionCRF(edc);
                        db.getEventDefinitionCRF().setCrf(edc.getCrf());
                        dseb.getAllEventCRFs().add(db);

                        // System.out.println("noCRf" + crfId);

                    }

                }

                System.out.println("size of all event crfs" + dseb.getAllEventCRFs().size());
            }

            DisplayStudySubjectBean dssb = new DisplayStudySubjectBean();

            dssb.setStudySubject(studySub);
            dssb.setStudyGroups(subGClasses);
            dssb.setStudyEvents(displayEvents);
            if (definitionId > 0) {
                dssb.setSedId(definitionId);
            } else {
                dssb.setSedId(-1);
            }
            displayStudySubs.add(dssb);
        }

        EntityBeanTable table = fp.getEntityBeanTable();
        ArrayList allStudyRows = DisplayStudySubjectRow.generateRowsFromBeans(displayStudySubs);

        ArrayList columnArray = new ArrayList();

        columnArray.add(resword.getString("ID"));
        columnArray.add(resword.getString("subject_status"));
        columnArray.add(resword.getString("gender"));
        for (int i = 0; i < studyGroupClasses.size(); i++) {
            StudyGroupClassBean sgc = (StudyGroupClassBean) studyGroupClasses.get(i);
            columnArray.add(sgc.getName());
        }

        // columnArray.add("Event Sequence");
        columnArray.add(resword.getString("event_status"));
        columnArray.add(resword.getString("event_date"));

        for (int i = 0; i < eventDefinitionCRFs.size(); i++) {
            EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
            columnArray.add(edc.getCrf().getName());

        }
        columnArray.add(resword.getString("actions"));
        String columns[] = new String[columnArray.size()];
        columnArray.toArray(columns);

        table.setColumns(new ArrayList(Arrays.asList(columns)));
        table.setQuery("ListEventsForSubject?module=" + module + "&defId=" + definitionId + "&tab=" + tabId, new HashMap());
        table.hideColumnLink(columnArray.size() - 1);

        table.addLink(resword.getString("add_new_subject"), "AddNewSubject");
        table.setRows(allStudyRows);
        table.computeDisplay();

        request.setAttribute("table", table);

        forwardPage(Page.LIST_EVENTS_FOR_SUBJECT);
    }

}
