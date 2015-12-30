/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import java.util.*;

import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudySubjectBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudySubjectRow;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.submit.AddNewSubjectServlet;
import org.akaza.openclinica.control.submit.SubmitDataServlet;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormDiscrepancyNotes;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author Bruce W. Perry, 5/1/08
 */
public class ListDiscNotesSubjectServlet extends SecureController {
    public static final String RESOLUTION_STATUS = "resolutionStatus";
    //Include extra path info on the URL, which generates a file name hint in some
    //browser's "save as..." dialog boxes
    public static final String EXTRA_PATH_INFO = "discrepancyNoteReport";
    public static final String DISCREPANCY_NOTE_TYPE = "discrepancyNoteType";
    public static final String FILTER_SUMMARY = "filterSummary";
    Locale locale;


    // < ResourceBundleresexception,respage;
    @Override
    protected void processRequest() throws Exception {

        String module = request.getParameter("module");
        if(module != null) {
            request.setAttribute("module",module);
        }
        //Determine whether to limit the displayed DN's to a certain DN type
        int resolutionStatus = 0;
        try {
            resolutionStatus = Integer.parseInt(request.getParameter("resolutionStatus"));
        } catch(NumberFormatException nfe){
            //Show all DN's
            resolutionStatus=-1;
        }
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
        locale = request.getLocale();
        // < resword =
        // ResourceBundle.getBundle("org.akaza.openclinica.i18n.words",locale);

        StudyDAO stdao = new StudyDAO(sm.getDataSource());
        StudySubjectDAO sdao = new StudySubjectDAO(sm.getDataSource());
        StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
        StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
        SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(sm.getDataSource());
        StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(sm.getDataSource());
        StudyGroupDAO sgdao = new StudyGroupDAO(sm.getDataSource());
        StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());

        int parentStudyId = currentStudy.getParentStudyId();
        ArrayList studyGroupClasses = new ArrayList();
        ArrayList allDefs = new ArrayList();
        // allDefs holds the list of study event definitions used in the table,
        // tbh
        if (parentStudyId > 0) {
            StudyBean parentStudy = (StudyBean) stdao.findByPK(parentStudyId);
            studyGroupClasses = sgcdao.findAllActiveByStudy(parentStudy);
            allDefs = seddao.findAllActiveByStudy(parentStudy);
        } else {
            parentStudyId = currentStudy.getId();
            studyGroupClasses = sgcdao.findAllActiveByStudy(currentStudy);
            allDefs = seddao.findAllActiveByStudy(currentStudy);
        }
        StudyParameterValueDAO spvdao = new StudyParameterValueDAO(sm.getDataSource());
        StudyParameterValueBean parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "collectDob");
        currentStudy.getStudyParameterConfig().setCollectDob(parentSPV.getValue());
        parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "genderRequired");
        currentStudy.getStudyParameterConfig().setGenderRequired(parentSPV.getValue());
        parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "subjectPersonIdRequired");
        currentStudy.getStudyParameterConfig().setSubjectPersonIdRequired(parentSPV.getValue());
        parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "subjectIdGeneration");
        currentStudy.getStudyParameterConfig().setSubjectIdGeneration(parentSPV.getValue());
        parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "subjectIdPrefixSuffix");
        currentStudy.getStudyParameterConfig().setSubjectIdPrefixSuffix(parentSPV.getValue());

        // YW >>

        // for all the study groups for each group class
        for (int i = 0; i < studyGroupClasses.size(); i++) {
            StudyGroupClassBean sgc = (StudyGroupClassBean) studyGroupClasses.get(i);
            ArrayList groups = sgdao.findAllByGroupClass(sgc);
            sgc.setStudyGroups(groups);
        }
        request.setAttribute("studyGroupClasses", studyGroupClasses);

        // information for the event tabs
        session.setAttribute("allDefsArray", allDefs);
        session.setAttribute("allDefsNumber", new Integer(allDefs.size()));
        session.setAttribute("groupSize", new Integer(studyGroupClasses.size()));

        // find all the subjects in current study
        ArrayList subjects = sdao.findAllByStudyId(currentStudy.getId());

        ArrayList<DisplayStudySubjectBean> displayStudySubs = new ArrayList<DisplayStudySubjectBean>();

        StudyBean sbean = (StudyBean) session.getAttribute("study");

        //Get all discrepancy notes for the study, optionally filtering on resolution status
        //and disc note type  - getDNotesForStudy
        List<DiscrepancyNoteBean> allDiscNotes = discNoteUtil.getThreadedDNotesForStudy(
          sbean,
          resolutionStatusIds,sm.getDataSource(), discNoteType);

        Map stats = discNoteUtil.generateDiscNoteSummary(allDiscNotes);
        request.setAttribute("summaryMap",stats);
        Set mapKeys = stats.keySet();
        request.setAttribute("mapKeys",mapKeys);

        //Don't loop through the subjects if there aren't any notes in the study
        if(! (allDiscNotes == null || allDiscNotes.isEmpty())) {
            // BEGIN LOOPING THROUGH SUBJECTS
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

                ArrayList subEvents = new ArrayList();
                // find all events order by definition ordinal
                ArrayList events = sedao.findAllByStudySubject(studySub);

                for (int j = 0; j < allDefs.size(); j++) {
                    StudyEventDefinitionBean sed = (StudyEventDefinitionBean) allDefs.get(j);
                    boolean hasDef = false;
                    // StudyEventBean first = (StudyEventBean) events.get(0);
                    // whack idea to try and set things right with the code
                    // below, tbh
                    // below needs to change, we need to reach into the list and
                    // pick the next one, tbh
                    // int blankid = first.getId();
                    // logger.info("...set blank to "+blankid);
                    for (int k = 0; k < events.size(); k++) {
                        StudyEventBean se = (StudyEventBean) events.get(k);
                        if (se.getStudyEventDefinitionId() == sed.getId()) {
                            se.setStudyEventDefinition(sed);

                            // logger.info(">>>found assigned id "+sed.getId()+" sed
                            // name: "+sed.getName()+" "+se.getId());
                            subEvents.add(se);
                            hasDef = true;
                        }

                    }
                    if (!hasDef) {
                        StudyEventBean blank = new StudyEventBean();
                        blank.setSubjectEventStatus(SubjectEventStatus.NOT_SCHEDULED);
                        blank.setStudyEventDefinitionId(sed.getId());

                        blank.setStudyEventDefinition(sed);

                        subEvents.add(blank);
                    }

                }

                // logger.info("subevents size after all adds: "+subEvents.size());
                // reorganize the events and find the repeating ones
                // subEvents:[aa bbb cc d ee]
                // finalEvents:[a(2) b(3) c(2) d e(2)]
                int prevDefId = 0;
                int currDefId = 0;
                ArrayList finalEvents = new ArrayList();
                int repeatingNum = 1;
                int count = 0;
                StudyEventBean event = new StudyEventBean();

                // begin looping through subject events
                for (int j = 0; j < subEvents.size(); j++) {
                    StudyEventBean se = (StudyEventBean) subEvents.get(j);
                    currDefId = se.getStudyEventDefinitionId();
                    if (currDefId != prevDefId) {// find a new event
                        if (repeatingNum > 1) {
                            event.setRepeatingNum(repeatingNum);
                            repeatingNum = 1;
                        }
                        finalEvents.add(se); // add current event to final
                        event = se;
                        count++;
                        // logger.info("event id? "+event.getId());
                    } else {// repeating event
                        repeatingNum++;
                        event.getRepeatEvents().add(se);
                        // logger.info("repeating size:" +
                        // event.getStudySubjectId()+
                        // event.getRepeatEvents().size());
                        if (j == subEvents.size() - 1) {
                            event.setRepeatingNum(repeatingNum);
                            repeatingNum = 1;

                        }
                    }
                    prevDefId = currDefId;
                }
                // end looping through subject events

                DisplayStudySubjectBean dssb = new DisplayStudySubjectBean();

                //Associate discrepancy notes with the proper StudyEvent
                //the allDiscNotes List of discrepancy notes are all the notes associated with
                //this study, and optionally filtered for resolution status and type.
                discNoteUtil.injectDiscNotesIntoStudyEvents(finalEvents,
                  allDiscNotes,sm.getDataSource());

                //Create a request attribute indicating whether or not there are any disc notes for the study
                boolean studyHasDiscNotes = (allDiscNotes != null && (! allDiscNotes.isEmpty()));
                request.setAttribute("studyHasDiscNotes",studyHasDiscNotes);

                dssb.setStudyEvents(finalEvents);
                dssb.setStudySubject(studySub);
                dssb.setStudyGroups(subGClasses);
                //if there the studysubject bean does not have any DN's then do not add it to this list.
                if(discNoteUtil.studyEventsHaveDiscNotes(finalEvents,null)) {
                    displayStudySubs.add(dssb);
                }
            }
            // END LOOPING THROUGH SUBJECTS
        }

        FormProcessor fp = new FormProcessor(request);
        EntityBeanTable table = fp.getEntityBeanTable();
        ArrayList allStudyRows =
          DisplayStudySubjectRow.generateRowsFromBeans(displayStudySubs);



        ArrayList columnArray = new ArrayList();

        columnArray.add(resword.getString("study_subject_ID"));
        columnArray.add(resword.getString("subject_status"));
        /*    columnArray.add(resword.getString("OID"));
        columnArray.add(resword.getString("gender"));*/
        /*  for (int i = 0; i < studyGroupClasses.size(); i++) {
            StudyGroupClassBean sgc = (StudyGroupClassBean) studyGroupClasses.get(i);
            columnArray.add(sgc.getName());
        }*/
        for (int i = 0; i < allDefs.size(); i++) {
            StudyEventDefinitionBean sed = (StudyEventDefinitionBean) allDefs.get(i);
            columnArray.add(sed.getName());
        }
        columnArray.add(resword.getString("actions"));
        String columns[] = new String[columnArray.size()];
        columnArray.toArray(columns);

        // String[] columns = {"ID", "Subject Status", "Gender", "Enrollment
        // Date",
        // "Study Events", "Actions" };
        table.setColumns(new ArrayList(Arrays.asList(columns)));
        table.setQuery(getBaseURL(), new HashMap());

        table.hideColumnLink(columnArray.size() - 1);

        // table.addLink("Enroll a new subject",
        // "javascript:leftnavExpand('addSubjectRowExpress');");
        table.setRows(allStudyRows);
        table.computeDisplay();

        request.setAttribute("table", table);
        // request.setAttribute("subjects", subjects);

        String idSetting = currentStudy.getStudyParameterConfig().getSubjectIdGeneration();
        // logger.info("subject id setting :" + idSetting);
        // set up auto study subject id
        if (idSetting.equals("auto editable") || idSetting.equals("auto non-editable")) {
            int nextLabel = ssdao.findTheGreatestLabel() + 1;
            request.setAttribute("label", new Integer(nextLabel).toString());
        }

        FormDiscrepancyNotes discNotes = new FormDiscrepancyNotes();
        session.setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);

        forwardPage(getJSP());
    }



    /**
     * Checks whether the user has the right permission to proceed function
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {

        locale = request.getLocale();
        // <
        // resexception=ResourceBundle.getBundle("org.akaza.openclinica.i18n.exceptions",locale);
        // < respage =
        // ResourceBundle.getBundle("org.akaza.openclinica.i18n.page_messages",locale);

        if (ub.isSysAdmin()) {
            return;
        }

        if(SubmitDataServlet.mayViewData(ub,currentRole)){
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");

    }



    protected Page getJSP() {
        return Page.LIST_SUBJECT_DISC_NOTE;
    }

    protected String getBaseURL() {
        return "ListDiscNotesSubjectServlet";
    }

}
