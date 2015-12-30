package org.akaza.openclinica.control.managestudy;

/**
 *
 */

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
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
import org.akaza.openclinica.bean.submit.EventCRFBean;
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ListDiscNotesForCRFServlet extends SecureController {

    public static final String DISCREPANCY_NOTE_TYPE = "discrepancyNoteType";
    public static final String RESOLUTION_STATUS = "resolutionStatus";
    public static final String FILTER_SUMMARY = "filterSummary";
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
        if (r.equals(Role.STUDYDIRECTOR) || r.equals(Role.COORDINATOR) || r.equals(Role.INVESTIGATOR) || r.equals(Role.RESEARCHASSISTANT) || r.equals(Role.MONITOR)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
    }

    @Override
    public void processRequest() throws Exception {

        FormProcessor fp = new FormProcessor(request);
        //Determine whether to limit the displayed DN's to a certain DN type
        int resolutionStatus = 0;
        try {
            resolutionStatus = Integer.parseInt(request.getParameter("resolutionStatus"));
        } catch(NumberFormatException nfe){
            //Show all DN's
            resolutionStatus=-1;
        }
        // request.setAttribute(RESOLUTION_STATUS,resolutionStatus);

        //Determine whether we already have a collection of resolutionStatus Ids, and if not
        //create a new attribute. If there is no resolution status, then the Set object should be cleared,
        //because we do not have to save a set of filter IDs.
        boolean hasAResolutionStatus =
          resolutionStatus >= 1 && resolutionStatus <= 5;
        Set<Integer>  resolutionStatusIds = (HashSet) session.getAttribute(RESOLUTION_STATUS);
        //remove the session if there is no resolution status
        if(! hasAResolutionStatus && resolutionStatusIds != null) {
            session.removeAttribute(RESOLUTION_STATUS);
            resolutionStatusIds = null;
        }
        if(hasAResolutionStatus) {
            if(resolutionStatusIds == null) {
                resolutionStatusIds = new HashSet<Integer>();
            }
            resolutionStatusIds.add(resolutionStatus);
            session.setAttribute(RESOLUTION_STATUS,resolutionStatusIds);
        }
        int discNoteType = 0;
        try {
            discNoteType = Integer.parseInt(request.getParameter("type"));
        } catch(NumberFormatException nfe){
            //Show all DN's
            discNoteType=-1;
        }
        request.setAttribute(DISCREPANCY_NOTE_TYPE,discNoteType);


        DiscrepancyNoteUtil discNoteUtil = new DiscrepancyNoteUtil();
        //Generate a summary of how we are filtering;
        Map<String,List<String>> filterSummary =
          discNoteUtil.generateFilterSummary(discNoteType,
            resolutionStatusIds);

        if(! filterSummary.isEmpty()) {
            request.setAttribute(FILTER_SUMMARY,filterSummary);
        }
        // checks which module the requests are from
        String module = fp.getString(MODULE);
        request.setAttribute(MODULE, module);

        int definitionId = fp.getInt("defId");
        int tabId = fp.getInt("tab");
        if (definitionId <= 0) {
            addPageMessage(respage.getString("please_choose_an_ED_ta_to_vies_details"));
            forwardPage(Page.LIST_SUBJECT_DISC_NOTE_SERVLET);
            return;
        }

        request.setAttribute("eventDefinitionId",definitionId);
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



        ArrayList<DisplayStudySubjectBean> displayStudySubs = new ArrayList<DisplayStudySubjectBean>();
        StudyBean sbean = (StudyBean) session.getAttribute("study");

        //Get all discrepancy notes for the study, optionally filtering on resolution status
        //and disc note type
        List<DiscrepancyNoteBean> allDiscNotes = discNoteUtil.getThreadedDNotesForStudy(sbean,
          resolutionStatusIds,sm.getDataSource(), discNoteType, true);

        //Don't loop through the subjects if there aren't any notes in the study
        if(! (allDiscNotes == null || allDiscNotes.isEmpty())) {
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
                    DisplayStudyEventBean dseb =
                      ListStudySubjectServlet.getDisplayStudyEventsForStudySubject(studySub, seb, sm.getDataSource(), ub, currentRole);

                    ArrayList<EventCRFBean> eventCRFs = ecdao.findAllByStudyEvent(seb);
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
                            // logger.info("eventCRF" +
                            // decb.getEventCRF().getId() +
                            // decb.getStage().getName() );
                            if (decb.getEventCRF().getCrf().getId() == crfId) {
                                dseb.getAllEventCRFs().add(decb);
                                // logger.info("hasCRf" + crfId +
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

                            // logger.info("noCRf" + crfId);

                        }

                    }

                    logger.info("size of all event crfs" + dseb.getAllEventCRFs().size());
                }

                DisplayStudySubjectBean dssb = new DisplayStudySubjectBean();

                //Associate discrepancy notes with the proper StudyEvent
                //Inside the JSP

                discNoteUtil.injectDiscNotesIntoDisplayStudyEvents(displayEvents,
                  resolutionStatusIds,sm.getDataSource(), discNoteType);

                //Create a request attribute indicating whether or not there are any disc notes for the study
                /*allDiscNotes =
               discNoteUtil.getDNotesForStudy(this.currentStudy,
               resolutionStatusIds,sm.getDataSource(), discNoteType);*/
                boolean studyHasDiscNotes = allDiscNotes != null && ! allDiscNotes.isEmpty();
                request.setAttribute("studyHasDiscNotes",studyHasDiscNotes);

                dssb.setStudySubject(studySub);
                dssb.setStudyGroups(subGClasses);
                dssb.setStudyEvents(displayEvents);
                if (definitionId > 0) {
                    dssb.setSedId(definitionId);
                } else {
                    dssb.setSedId(-1);
                }
                  //if there the studysubject bean does not have any DN's then do not add it to this list.
                if(discNoteUtil.studyEventsHaveDiscNotes(null,displayEvents)) {
                    displayStudySubs.add(dssb);
                }
            }
        }

        EntityBeanTable table = fp.getEntityBeanTable();
        ArrayList allStudyRows = DisplayStudySubjectRow.
          generateRowsFromBeans(displayStudySubs);

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
        String actions = resword.getString("actions");
        columnArray.add(actions);
        String columns[] = new String[columnArray.size()];
        columnArray.toArray(columns);

        table.setColumns(new ArrayList(Arrays.asList(columns)));
       // table.setQuery("ListDiscNotesForCRFServlet?tab=" + tabId, new HashMap());
        table.setQuery("ListDiscNotesForCRFServlet", new HashMap());
        table.hideColumnLink(columnArray.size() - 1);

        //table.addLink(resword.getString("add_new_subject"), "AddNewSubject");
        table.setRows(allStudyRows);
        table.computeDisplay();

        request.setAttribute("table", table);

        forwardPage(Page.LIST_DNOTES_FOR_CRF);
    }

}

