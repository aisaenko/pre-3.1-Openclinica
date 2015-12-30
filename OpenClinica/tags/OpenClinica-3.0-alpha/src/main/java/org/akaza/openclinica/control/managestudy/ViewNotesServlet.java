/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 *
 * Created on Sep 23, 2005
 */
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteRow;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.submit.SubmitDataServlet;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * View a list of all discrepancy notes in current study
 * 
 * @author ssachs
 * @author jxu
 */
public class ViewNotesServlet extends SecureController {
    public static final String PRINT = "print";
    public static final String RESOLUTION_STATUS = "resolutionStatus";
    public static final String TYPE = "discNoteType";
    public static final String WIN_LOCATION = "window_location";
    public static final String NOTES_TABLE = "notesTable";

    /*
     * public static final Map<Integer,String> TYPES = new
     * HashMap<Integer,String>(); static{
     * TYPES.put(1,"Failed Validation Check"); TYPES.put(2,"Incomplete");
     * TYPES.put(3,"Unclear/Unreadable"); TYPES.put(4,"Annotation");
     * TYPES.put(5,"Other"); TYPES.put(6,"Query");
     * TYPES.put(7,"Reason for Change"); }
     */
    /*
     * (non-Javadoc)
     * 
     * @see org.akaza.openclinica.control.core.SecureController#processRequest()
     */
    @Override
    protected void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);
        int oneSubjectId = fp.getInt("id");
        // BWP 11/03/2008 3029: This session attribute in removed in
        // ResolveDiscrepancyServlet.mayProceed() >>
        session.setAttribute("subjectId", oneSubjectId);
        // >>

        int resolutionStatusSubj = fp.getInt(RESOLUTION_STATUS);
        int discNoteType = fp.getInt(TYPE);

        boolean removeSession = fp.getBoolean("removeSession");

        String module = fp.getString("module");
        request.setAttribute("module", module);

        // BWP 11/03/2008 3029: This session attribute in removed in
        // ResolveDiscrepancyServlet.mayProceed() >>
        session.setAttribute("module", module);
        // >>

        // Do we only want to view the notes for 1 subject?
        String viewForOne = fp.getString("viewForOne");
        boolean isForOneSubjectsNotes = ("y".equalsIgnoreCase(viewForOne));

        DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());
        dndao.setFetchMapping(true);
        // refactoring needed...
        ArrayList notes = new ArrayList();

        EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
        ArrayList itemDataNotes = dndao.findAllItemDataByStudy(currentStudy);

        ArrayList subjectNotes = dndao.findAllSubjectByStudy(currentStudy);
        SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
        for (int i = 0; i < subjectNotes.size(); i++) {
            DiscrepancyNoteBean dn = (DiscrepancyNoteBean) subjectNotes.get(i);
            int subjectId = dn.getEntityId();
            SubjectBean sub = (SubjectBean) sdao.findByPK(subjectId);
            String column = dn.getColumn().trim();
            if (!StringUtil.isBlank(column)) {
                if ("gender".equalsIgnoreCase(column)) {
                    dn.setEntityValue(sub.getGender() + "");
                    dn.setEntityName("gender");

                } else if ("date_of_birth".equals(column)) {
                    if (sub.getDateOfBirth() != null) {
                        dn.setEntityValue(sub.getDateOfBirth().toString());

                    }
                    dn.setEntityName(resword.getString("date_of_birth"));

                }
            }
        }
        ArrayList studySubjectNotes = dndao.findAllStudySubjectByStudy(currentStudy);

        StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
        for (int i = 0; i < studySubjectNotes.size(); i++) {
            DiscrepancyNoteBean dn = (DiscrepancyNoteBean) studySubjectNotes.get(i);
            int studySubId = dn.getEntityId();
            StudySubjectBean ss = (StudySubjectBean) ssdao.findByPK(studySubId);
            String column = dn.getColumn().trim();
            if (!StringUtil.isBlank(column)) {
                if ("enrollment_date".equals(column)) {
                    if (ss.getEnrollmentDate() != null) {
                        dn.setEntityValue(ss.getEnrollmentDate().toString());

                    }
                    dn.setEntityName(resword.getString("enrollment_date"));

                }
            }

        }
        ArrayList studyEventNotes = dndao.findAllStudyEventByStudy(currentStudy);
        StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
        for (int i = 0; i < studyEventNotes.size(); i++) {
            DiscrepancyNoteBean dn = (DiscrepancyNoteBean) studyEventNotes.get(i);
            StudyEventBean se = (StudyEventBean) sedao.findByPK(dn.getEntityId());
            String column = dn.getColumn().trim();
            if (!StringUtil.isBlank(column)) {
                if ("date_start".equals(column)) {
                    if (se.getDateStarted() != null) {
                        dn.setEntityValue(se.getDateStarted().toString());

                    }
                    dn.setEntityName(resword.getString("start_date"));

                } else if ("date_end".equals(column)) {
                    if (se.getDateEnded() != null) {
                        dn.setEntityValue(se.getDateEnded().toString());

                    }
                    dn.setEntityName(resword.getString("end_date"));

                } else if ("location".equals(column)) {
                    dn.setEntityValue(se.getLocation());
                    dn.setEntityName(resword.getString("location"));

                }

            }

        }

        ArrayList eventCRFNotes = dndao.findAllEventCRFByStudy(currentStudy);
        // EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
        for (int i = 0; i < eventCRFNotes.size(); i++) {
            DiscrepancyNoteBean dn = (DiscrepancyNoteBean) eventCRFNotes.get(i);
            EventCRFBean ec = (EventCRFBean) ecdao.findByPK(dn.getEntityId());
            String column = dn.getColumn().trim();
            if (!StringUtil.isBlank(column)) {
                if ("date_interviewed".equals(column)) {
                    if (ec.getDateInterviewed() != null) {
                        dn.setEntityValue(ec.getDateInterviewed().toString());

                    }
                    dn.setEntityName(resword.getString("date_interviewed"));
                } else if ("interviewer_name".equals(column)) {
                    dn.setEntityValue(ec.getInterviewerName());
                    dn.setEntityName(resword.getString("interviewer_name"));
                }
            }

        }
        // issue 2795: We have to add an updated Date to itemDataNotes
        // issue 3014: & eventCRFNotes
        // In general, all notes need these dates
        this.addUpdatedDateToNotes(itemDataNotes);
        this.addUpdatedDateToNotes(eventCRFNotes);
        this.addUpdatedDateToNotes(studyEventNotes);
        this.addUpdatedDateToNotes(studySubjectNotes);
        this.addUpdatedDateToNotes(subjectNotes);

        notes.addAll(itemDataNotes);
        notes.addAll(subjectNotes);
        notes.addAll(studySubjectNotes);
        notes.addAll(studyEventNotes);
        notes.addAll(eventCRFNotes);

        if (isForOneSubjectsNotes) {
            notes = filterForOneSubject(notes, oneSubjectId, resolutionStatusSubj);
        }
        DiscrepancyNoteUtil dNoteUtil = new DiscrepancyNoteUtil();
        if (discNoteType > 0) {
            notes = (ArrayList) dNoteUtil.filterforDiscNoteType(notes, discNoteType);
        }

        // The notes have not been filtered for resolutionStatus,
        // if they are not yet filtered by subject ID
        if ((!isForOneSubjectsNotes) && (resolutionStatusSubj > 0 && resolutionStatusSubj <= 4)) {
            notes = (ArrayList) dNoteUtil.filterDiscNotes(notes, resolutionStatusSubj);
        }

        EntityBeanTable table = fp.getEntityBeanTable();
        table.setSortingIfNotExplicitlySet(2, false);// sort by event date,
        // desc
        ArrayList noteRows = new ArrayList();

        noteRows = DiscrepancyNoteRow.generateRowsFromBeans(notes);

        String resolutionStatus = fp.getString(RESOLUTION_STATUS);

        String ofnotes = resword.getString("of_notes");
        // String[] columns = { "Study Subject ID", "Date Created","Event
        // Date","Event", "CRF",
        // "Entity Name", "Entity Value", "Description", "Detailed Notes",
        // "# of Notes", "Resolution Status","Type", "Entity Type", "Created
        // By", "Actions" };

        String[] columns =
            { resword.getString("study_subject_ID"), resword.getString("date_created"), resword.getString("date_updated"), resword.getString("event_date"),
                resword.getString("event"), resword.getString("CRF"), resword.getString("entity_name"), resword.getString("entity_value"),
                resword.getString("description"), resword.getString("detailed_notes"), ofnotes, resword.getString("assigned_user"),
                resword.getString("resolution_status"), resword.getString("type"), resword.getString("entity_type"), resword.getString("created_by"),
                resword.getString("actions") };

        table.setColumns(new ArrayList(Arrays.asList(columns)));
        table.hideColumnLink(DiscrepancyNoteRow.COL_ACTIONS);
        HashMap hm = new HashMap();
        hm.put(RESOLUTION_STATUS, fp.getString(RESOLUTION_STATUS));
        table.setQuery("ViewNotes", hm);
        // table.setSortingColumnInd(2);
        // table.setAscendingSort(false);
        populateRowsWithAttachedData(noteRows);

        // skip this section if we are only viewing rows for one subject
        // We've already filtered for resolution status...
        /*
         * if(! isForOneSubjectsNotes) { if
         * (!StringUtil.isBlank(resolutionStatus)) { //
         * logger.info("resolution status:" + resolutionStatus);
         * ArrayList<DiscrepancyNoteRow> filteredRows = new
         * ArrayList<DiscrepancyNoteRow>(); if
         * (resolutionStatus.equalsIgnoreCase("open")) { for (int i = 0; i <
         * noteRows.size(); i++) { DiscrepancyNoteRow dnr = (DiscrepancyNoteRow)
         * noteRows.get(i); DiscrepancyNoteBean dnb = (DiscrepancyNoteBean)
         * dnr.getBean(); if (dnb.getResStatus().equals(ResolutionStatus.OPEN))
         * { filteredRows.add(dnr); } }
         * 
         * } else if (resolutionStatus.equalsIgnoreCase("closed")) { for (int i
         * = 0; i < noteRows.size(); i++) { DiscrepancyNoteRow dnr =
         * (DiscrepancyNoteRow) noteRows.get(i); DiscrepancyNoteBean dnb =
         * (DiscrepancyNoteBean) dnr.getBean(); if
         * (dnb.getResStatus().equals(ResolutionStatus.CLOSED)) {
         * filteredRows.add(dnr); } } }
         * 
         * table.setRows(filteredRows); } else { table.setRows(noteRows); } }
         */
        // if(isForOneSubjectsNotes) table.setRows(noteRows);
        table.setRows(noteRows);
        table.computeDisplay();
        /*
         * for(int i=0; i<noteRows.size(); ++i) { DiscrepancyNoteBean dnb =
         * (DiscrepancyNoteBean) noteRows.get(i); logger.info("~~~~StageId=" +
         * dnb.getStageId()); }
         */

        // if removeSession =1, user is back from resolving a note, so let's
        // go back to where he is from
        if (removeSession && session.getAttribute(NOTES_TABLE) != null) {
            request.setAttribute("table", session.getAttribute(NOTES_TABLE));
        } else {
            request.setAttribute("table", table);
        }

        if (removeSession) {
            session.removeAttribute(WIN_LOCATION);
            session.removeAttribute(NOTES_TABLE);
        }

        // after resolving a note, user wants to go back to view notes page, we
        // save the current URL
        // so we can go back later
        session.setAttribute(WIN_LOCATION, "ViewNotes?viewForOne=" + viewForOne + "&id=" + oneSubjectId + "&module=" + module + " &removeSession=1");
        session.setAttribute(NOTES_TABLE, table);

        if ("yes".equalsIgnoreCase(fp.getString(PRINT))) {
            request.setAttribute("allNotes", DiscrepancyNoteRow.generateBeansFromRows(noteRows));
            forwardPage(Page.VIEW_DISCREPANCY_NOTES_IN_STUDY_PRINT);
        } else {
            forwardPage(Page.VIEW_DISCREPANCY_NOTES_IN_STUDY);
        }
    }

    public ArrayList<DiscrepancyNoteBean> filterForOneSubject(ArrayList<DiscrepancyNoteBean> allNotes, int subjectId, int resolutionStatus) {

        if (allNotes == null || allNotes.isEmpty() || subjectId == 0)
            return allNotes;
        // Are the D Notes filtered by resolution?
        boolean filterByRes = (resolutionStatus >= 1 && resolutionStatus <= 5);

        ArrayList<DiscrepancyNoteBean> filteredNotes = new ArrayList<DiscrepancyNoteBean>();
        StudySubjectDAO subjectDao = new StudySubjectDAO(sm.getDataSource());
        StudySubjectBean studySubjBean = (StudySubjectBean) subjectDao.findByPK(subjectId);

        for (DiscrepancyNoteBean discBean : allNotes) {
            if (discBean.getSubjectName().equalsIgnoreCase(studySubjBean.getLabel())) {
                if (!filterByRes) {
                    filteredNotes.add(discBean);
                } else {
                    if (discBean.getResolutionStatusId() == resolutionStatus) {
                        filteredNotes.add(discBean);
                    }
                }
            }
        }

        return filteredNotes;
    }

    private void populateRowsWithAttachedData(ArrayList noteRows) {
        DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());

        for (int i = 0; i < noteRows.size(); i++) {
            DiscrepancyNoteRow dnr = (DiscrepancyNoteRow) noteRows.get(i);
            DiscrepancyNoteBean dnb = (DiscrepancyNoteBean) dnr.getBean();

            // get study properties
            dnr.setPartOfSite(dnb.getStudyId() == currentStudy.getId());
            if (dnr.isPartOfSite()) {
                StudyDAO sdao = new StudyDAO(sm.getDataSource());
                StudyBean sb = (StudyBean) sdao.findByPK(dnb.getStudyId());
            }

            if (dnb.getParentDnId() == 0) {
                ArrayList children = dndao.findAllByStudyAndParent(currentStudy, dnb.getId());
                dnr.setNumChildren(children.size());
                dnb.setNumChildren(children.size());

                for (int j = 0; j < children.size(); j++) {
                    DiscrepancyNoteBean child = (DiscrepancyNoteBean) children.get(j);

                    if (child.getResolutionStatusId() > dnb.getResolutionStatusId()) {
                        dnr.setStatus(ResolutionStatus.get(child.getResolutionStatusId()));
                        dnb.setResStatus(ResolutionStatus.get(child.getResolutionStatusId()));
                    }
                }
            }

            String entityType = dnb.getEntityType();

            if (dnb.getEntityId() > 0 && !entityType.equals("")) {
                AuditableEntityBean aeb = dndao.findEntity(dnb);
                dnr.setEntityName(aeb.getName());
                if (entityType.equalsIgnoreCase("eventCRF")) {
                    EventCRFBean ecb = (EventCRFBean) aeb;
                    CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
                    CRFDAO cdao = new CRFDAO(sm.getDataSource());
                    CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(ecb.getCRFVersionId());
                    CRFBean cb = (CRFBean) cdao.findByPK(cvb.getCrfId());
                    // YW << save states of EventCRFBean for hiding resolving
                    // icon if a CRF hasn't been
                    // marked complete (actually, DOUBLE_DATA_ENTRY_COMPLETE)
                    dnb.setStageId(ecb.getStage().getId());
                    // YW >>
                    dnr.setEntityName(cb.getName() + " (" + cvb.getName() + ")");
                }

                else if (entityType.equalsIgnoreCase("studyEvent")) {
                    StudyEventDAO sed = new StudyEventDAO(sm.getDataSource());
                    StudyEventBean se = (StudyEventBean) sed.findByPK(dnb.getEntityId());

                    StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
                    StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) seddao.findByPK(se.getStudyEventDefinitionId());

                    dnr.setEntityName(sedb.getName());
                }

                else if (entityType.equalsIgnoreCase("itemData")) {
                    // ItemDataBean idb = (ItemDataBean) aeb;
                    ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
                    ItemDataBean idb = (ItemDataBean) iddao.findByPK(dnb.getEntityId());

                    // System.out.println("item data bean" + idb.getItemId());

                    ItemDAO idao = new ItemDAO(sm.getDataSource());
                    ItemBean ib = (ItemBean) idao.findByPK(idb.getItemId());

                    // YW << save states of EventCRFBean for hiding resolving
                    // icon if a CRF hasn't been
                    // marked complete (actually, DOUBLE_DATA_ENTRY_COMPLETE)
                    EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
                    EventCRFBean ec = (EventCRFBean) ecdao.findByPK(idb.getEventCRFId());
                    dnb.setStageId(ec.getStage().getId());
                    // YW >>

                    dnr.setEntityName(ib.getName());

                    StudyEventDAO sed = new StudyEventDAO(sm.getDataSource());
                    StudyEventBean se = (StudyEventBean) sed.findByPK(ec.getStudyEventId());

                    StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
                    StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) seddao.findByPK(se.getStudyEventDefinitionId());

                    se.setName(sedb.getName());

                    StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
                    StudySubjectBean ssub = (StudySubjectBean) ssdao.findByPK(ec.getStudySubjectId());

                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
     */
    @Override
    protected void mayProceed() throws InsufficientPermissionException {
        /*
         * if (currentRole.getRole().equals(Role.STUDYDIRECTOR) ||
         * currentRole.getRole().equals(Role.COORDINATOR)) { return; }
         */
        if (SubmitDataServlet.mayViewData(ub, currentRole)) {
            return;
        }

        addPageMessage(respage.getString("no_permission_to_view_discrepancies") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director_or_study_cordinator"), "1");
    }

    private void addUpdatedDateToNotes(List<DiscrepancyNoteBean> discrepancyNoteBeans) {
        if (discrepancyNoteBeans == null || discrepancyNoteBeans.isEmpty()) {
            return;
        }

        DiscrepancyNoteDAO discrepancyNoteDAO = new DiscrepancyNoteDAO(sm.getDataSource());

        // The updated date is the creation date for notes with no parents or
        // children
        for (DiscrepancyNoteBean dnBean : discrepancyNoteBeans) {
            Date createDate = dnBean.getCreatedDate();
            if (isUnaffiliatedDNote(dnBean, discrepancyNoteDAO) && (createDate != null)) {
                dnBean.setUpdatedDate(createDate);
                continue;
            }

            // The bean is part of a thread, either as a parent or child.
            // First check whether it's a parent, and if so, get the creation
            // date of the newest child
            List<DiscrepancyNoteBean> childNoteBeans = discrepancyNoteDAO.findAllByParent(dnBean);

            DiscrepancyNoteBean tempBean;
            int listSize;

            if (!childNoteBeans.isEmpty()) {
                listSize = childNoteBeans.size();
                // get the last child's CreatedDate
                tempBean = childNoteBeans.get(listSize - 1);
                dnBean.setUpdatedDate(tempBean.getCreatedDate());
                continue;
            }

            // The disc bean must be a child of a parent bean. Get the
            // parent bean, then the last child's CreatedDate
            DiscrepancyNoteBean parentBean;
            int parentId = dnBean.getParentDnId();
            if (parentId == 0) {
                // There is no reason to move on if there is not a valid parent
                // id
                continue;
            }
            parentBean = (DiscrepancyNoteBean) discrepancyNoteDAO.findByPK(parentId);

            if (parentBean != null) {
                childNoteBeans = discrepancyNoteDAO.findAllByParent(dnBean);

                if (!childNoteBeans.isEmpty()) {
                    listSize = childNoteBeans.size();
                    // get the last child's CreatedDate
                    tempBean = childNoteBeans.get(listSize - 1);
                    dnBean.setUpdatedDate(tempBean.getCreatedDate());
                }

            }
        }

    }

    private boolean isUnaffiliatedDNote(DiscrepancyNoteBean dnBean, DiscrepancyNoteDAO discrepancyNoteDAO) {

        if (dnBean == null || discrepancyNoteDAO == null)
            return true;

        List<DiscrepancyNoteBean> discrepancyNoteBeans = discrepancyNoteDAO.findAllByParent(dnBean);

        boolean isChildless = discrepancyNoteBeans.isEmpty();

        boolean isOrphan = (dnBean.getParentDnId() == 0);

        if (isChildless && isOrphan)
            return true;

        return false;
    }
}
