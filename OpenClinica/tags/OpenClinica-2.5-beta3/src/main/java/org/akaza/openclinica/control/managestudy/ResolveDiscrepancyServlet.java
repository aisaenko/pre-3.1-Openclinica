/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 *
 * Created on Sep 22, 2005
 */
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.submit.CreateDiscrepancyNoteServlet;
import org.akaza.openclinica.control.submit.DataEntryServlet;
import org.akaza.openclinica.control.submit.TableOfContentsServlet;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.exception.InconsistentStateException;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author ssachs
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ResolveDiscrepancyServlet extends SecureController {

    private static final String INPUT_NOTE_ID = "noteId";
    private static final String CAN_ADMIN_EDIT = "canAdminEdit";
    private static final String EVENT_CRF_ID ="ecId";
    private static final String STUDY_SUB_ID ="studySubjectId";
    
    private static final String RESOLVING_NOTE = "resolving_note";
    

    public Page getPageForForwarding(DiscrepancyNoteBean note, boolean isCompleted) {
        String entityType = note.getEntityType().toLowerCase();
        request.setAttribute("fromResolvingNotes", "yes");
        
        if ("subject".equalsIgnoreCase(entityType)) {
            return Page.UPDATE_SUBJECT_SERVLET;
            // UpdateSubject?id=8&studySubId=8&action=show
        } else if ("studysub".equalsIgnoreCase(entityType)) {
            return Page.UPDATE_STUDY_SUBJECT_SERVLET;
            // UpdateStudySubject?id=8&action=show
        } else if ("eventcrf".equalsIgnoreCase(entityType)) {
            return Page.TABLE_OF_CONTENTS_SERVLET;
            // TableOfContents?action=ae&ecid=51&submitted=1&editInterview=1&interviewer=abc&interviewDate=12/04/2003
        } else if ("studyevent".equalsIgnoreCase(entityType)) {
            return Page.UPDATE_STUDY_EVENT_SERVLET;
            // UpdateStudyEvent?event_id=12&ss_id=12
        } else if ("itemdata".equalsIgnoreCase(entityType)) {
            if (currentRole.getRole().equals(Role.MONITOR) || !isCompleted) {
                //System.out.println("not completed");
                return Page.VIEW_SECTION_DATA_ENTRY_SERVLET; 
                //ViewSectionDataEntry?eventDefinitionCRFId=&ecId=1&tabId=1&studySubjectId=1
            } else {
              return Page.ADMIN_EDIT_SERVLET;
            }
            // eventCRFId=51&sectionId=14
        }
        return null;
    }

    public boolean prepareRequestForResolution(HttpServletRequest request, 
            DataSource ds, StudyBean currentStudy, DiscrepancyNoteBean note, boolean isCompleted) {
        String entityType = note.getEntityType().toLowerCase();
        int id = note.getEntityId();
        if ("subject".equalsIgnoreCase(entityType)) {
            StudySubjectDAO ssdao = new StudySubjectDAO(ds);
            StudySubjectBean ssb = ssdao.findBySubjectIdAndStudy(id, currentStudy);

            request.setAttribute("action", "show");
            request.setAttribute("id", String.valueOf(note.getEntityId()));
            request.setAttribute("studySubId", String.valueOf(ssb.getId()));
        } else if ("studysub".equalsIgnoreCase(entityType)) {
            request.setAttribute("action", "show");
            request.setAttribute("id", String.valueOf(note.getEntityId()));
        } else if ("eventcrf".equalsIgnoreCase(entityType)) {
            request.setAttribute("editInterview", "1");

            EventCRFDAO ecdao = new EventCRFDAO(ds);
            EventCRFBean ecb = (EventCRFBean) ecdao.findByPK(id);
            request.setAttribute(TableOfContentsServlet.INPUT_EVENT_CRF_BEAN, ecb);
        } else if ("studyevent".equalsIgnoreCase(entityType)) {
            StudyEventDAO sedao = new StudyEventDAO(ds);
            StudyEventBean seb = (StudyEventBean) sedao.findByPK(id);
            request.setAttribute(UpdateStudyEventServlet.EVENT_ID, String.valueOf(id));
            request.setAttribute(UpdateStudyEventServlet.STUDY_SUBJECT_ID, String.valueOf(seb.getStudySubjectId()));
        }

        // this is for item data
        else if ("itemdata".equalsIgnoreCase(entityType)) {
            ItemDataDAO iddao = new ItemDataDAO(ds);
            ItemDataBean idb = (ItemDataBean) iddao.findByPK(id);

            EventCRFDAO ecdao = new EventCRFDAO(ds);            
           
            EventCRFBean ecb = (EventCRFBean) ecdao.findByPK(idb.getEventCRFId());

            StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
            
            StudySubjectBean ssb = (StudySubjectBean)ssdao.findByPK(ecb.getStudySubjectId());
            
            ItemFormMetadataDAO ifmdao = new ItemFormMetadataDAO(ds);
            ItemFormMetadataBean ifmb = ifmdao.findByItemIdAndCRFVersionId(idb.getItemId(), ecb.getCRFVersionId());

            
            if (currentRole.getRole().equals(Role.MONITOR) || !isCompleted) {
                StudyEventDAO sedao = new StudyEventDAO(ds);
                StudyEventBean seb = (StudyEventBean) sedao.findByPK(id);
                request.setAttribute(EVENT_CRF_ID, String.valueOf(idb.getEventCRFId()));
                request.setAttribute(STUDY_SUB_ID, String.valueOf(seb.getStudySubjectId()));
                
            } else {
                request.setAttribute(DataEntryServlet.INPUT_EVENT_CRF_ID, String.valueOf(idb.getEventCRFId()));
                request.setAttribute(DataEntryServlet.INPUT_SECTION_ID, String.valueOf(ifmb.getSectionId()));
                
            }
            DataEntryStage stage = ecb.getStage();

            //if (!stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE)) {
            //    return false;
            //}
        }

        return true;

    }

    /*
     * (non-Javadoc)
     *
     * @see org.akaza.openclinica.control.core.SecureController#processRequest()
     */
    @Override
    protected void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);
        int noteId = fp.getInt(INPUT_NOTE_ID);

        DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());
        dndao.setFetchMapping(true);

        // check that the note exists
        DiscrepancyNoteBean note = (DiscrepancyNoteBean) dndao.findByPK(noteId);

        if (!note.isActive()) {
            throw new InconsistentStateException(Page.MANAGE_STUDY_SERVLET, resexception.getString("you_are_trying_resolve_discrepancy_not_exist"));
        }

        // check that the note has not already been closed
        ArrayList children = dndao.findAllByParent(note);
        note.setChildren(children);
        if (noteIsClosed(note)) {
            throw new InconsistentStateException(Page.VIEW_DISCREPANCY_NOTES_IN_STUDY_SERVLET, resexception
                    .getString("the_discrepancy_choose_has_been_closed_resolved_create_new"));
        }

        // all clear, send the user to the resolved screen
        String entityType = note.getEntityType().toLowerCase();
        note.setResStatus(ResolutionStatus.get(note.getResolutionStatusId()));
        note.setDisType(DiscrepancyNoteType.get(note.getDiscrepancyNoteTypeId()));
        boolean toView = false;
        boolean isCompleted = false;
        if ("itemdata".equalsIgnoreCase(entityType)) {
            ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
            ItemDataBean idb = (ItemDataBean) iddao.findByPK(note.getEntityId());

            EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());            
           
            EventCRFBean ecb = (EventCRFBean) ecdao.findByPK(idb.getEventCRFId());

            StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
            
            StudySubjectBean ssb = (StudySubjectBean)ssdao.findByPK(ecb.getStudySubjectId());
            
            note.setSubjectId(ssb.getId());
            note.setItemId(idb.getItemId());
            
            if (ecb.getStatus().equals(Status.UNAVAILABLE)) {
                isCompleted = true;
            }
            
            toView = true;// we want to go to view note page if the note is for item data
        }
        String createNoteURL = CreateDiscrepancyNoteServlet.getAddChildURL(note, ResolutionStatus.CLOSED, toView);
        setPopUpURL(createNoteURL);
        //logger.info("set up pop up url: " + createNoteURL);
        boolean goNext = prepareRequestForResolution(request, sm.getDataSource(), currentStudy, note, isCompleted);

        Page p = getPageForForwarding(note, isCompleted);

        //logger.info("found page for forwarding: " + p.getFileName());
        if (p == null) {
            throw new InconsistentStateException(Page.VIEW_DISCREPANCY_NOTES_IN_STUDY_SERVLET, resexception
                    .getString("the_discrepancy_note_triying_resolve_has_invalid_type"));
        }

        if (!goNext) {
            setPopUpURL("");
            addPageMessage(respage.getString("you_may_not_perform_admin_edit_on_CRF_not_completed_by_user"));
            p = Page.VIEW_DISCREPANCY_NOTES_IN_STUDY_SERVLET;
            logger.info("found self here; Not Go Next");
        }
       
        forwardPage(p);
    }

    /**
     * Determines if a discrepancy note is closed or not. The note is closed if
     * it has status closed, or any of its children have closed status.
     *
     * @param note
     *            The discrepancy note. The children should already be set.
     * @return <code>true</code> if the note is closed, <code>false</code>
     *         otherwise.
     */
    public static boolean noteIsClosed(DiscrepancyNoteBean note) {
        if (note.getResolutionStatusId() == ResolutionStatus.CLOSED.getId()) {
            return true;
        }

        ArrayList children = note.getChildren();
        for (int i = 0; i < children.size(); i++) {
            DiscrepancyNoteBean child = (DiscrepancyNoteBean) children.get(i);
            if (child.getResolutionStatusId() == ResolutionStatus.CLOSED.getId()) {
                return true;
            }
        }

        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
     */
    @Override
    protected void mayProceed() throws InsufficientPermissionException {
        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || 
                currentRole.getRole().equals(Role.COORDINATOR) || currentRole.getRole().equals(Role.INVESTIGATOR) ||
                currentRole.getRole().equals(Role.RESEARCHASSISTANT) || currentRole.getRole().equals(Role.MONITOR)) {
            return;
        }

        addPageMessage(respage.getString("no_have_permission_to_resolve_discrepancy") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director_or_study_coordinator"), "1");
    }

}
