/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 * 
 * Created on Sep 23, 2005
 */
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.core.Role;
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
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * 
 * View a list of all discrepancy notes in current study
 * 
 * @author ssachs
 * @author jxu
 */
public class ViewNotesServlet extends SecureController {
  public static final String PRINT = "print";

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.core.SecureController#processRequest()
   */
  protected void processRequest() throws Exception {
    FormProcessor fp = new FormProcessor(request);

    DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());
    dndao.setFetchMapping(true);
    //ArrayList notes = dndao.findAllParentsByStudy(currentStudy);
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
          dn.setEntityName("Date Of Birth");
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
          dn.setEntityName("Enrollment Date");
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
          dn.setEntityName("Start Date");
        } else if ("date_end".equals(column)) {
          if (se.getDateEnded() != null) {
            dn.setEntityValue(se.getDateEnded().toString());
          }
          dn.setEntityName("End Date");
        } else if ("location".equals(column)){
          dn.setEntityValue(se.getLocation());
          dn.setEntityName("Location");          
        }

      }

    }

    ArrayList eventCRFNotes = dndao.findAllEventCRFByStudy(currentStudy);
    //EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
    for (int i = 0; i < eventCRFNotes.size(); i++) {
      DiscrepancyNoteBean dn = (DiscrepancyNoteBean) eventCRFNotes.get(i);
      EventCRFBean ec = (EventCRFBean) ecdao.findByPK(dn.getEntityId());
      String column = dn.getColumn().trim();
      if (!StringUtil.isBlank(column)) {
        if ("date_interviewed".equals(column)) {
          if (ec.getDateInterviewed() != null) {
            dn.setEntityValue(ec.getDateInterviewed().toString());
          }
          dn.setEntityName("Date Interviewed");
        } else if ("interviewer_name".equals(column)) {
          dn.setEntityValue(ec.getInterviewerName());
          dn.setEntityName("Interviewer Name");
        }
      }

    }

    
    notes.addAll(itemDataNotes);
    notes.addAll(subjectNotes);
    notes.addAll(studySubjectNotes);
    notes.addAll(studyEventNotes);
    notes.addAll(eventCRFNotes);

    EntityBeanTable table = fp.getEntityBeanTable();
    ArrayList noteRows = DiscrepancyNoteRow.generateRowsFromBeans(notes);

    String[] columns = { "Resolution Status", "Type", "Study Subject ID", "Event", "Event Date", "CRF",
        "Entity Type", "Entity Name", "Entity Value", "Description", "Date Created", "Created By",
        "Detailed Notes", "# of Notes", "Actions" };

    table.setColumns(new ArrayList(Arrays.asList(columns)));
    table.hideColumnLink(DiscrepancyNoteRow.COL_ACTIONS);
    table.setQuery("ViewNotes", new HashMap());
    populateRowsWithAttachedData(noteRows); 
    table.setRows(noteRows);
    table.computeDisplay();
   /* 
    for(int i=0; i<noteRows.size(); ++i) {
    	DiscrepancyNoteBean dnb = (DiscrepancyNoteBean) noteRows.get(i);
    	System.out.println("~~~~StageId=" + dnb.getStageId());
    }*/

    request.setAttribute("table", table);
    
    if ("yes".equalsIgnoreCase(fp.getString(PRINT))) {
      request.setAttribute("allNotes", DiscrepancyNoteRow.generateBeansFromRows(noteRows));
      forwardPage(Page.VIEW_DISCREPANCY_NOTES_IN_STUDY_PRINT);
    } else {
      forwardPage(Page.VIEW_DISCREPANCY_NOTES_IN_STUDY);
    }
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
      
      if ((dnb.getEntityId() > 0) && !entityType.equals("")) {
        AuditableEntityBean aeb = dndao.findEntity(dnb);
        dnr.setEntityName(aeb.getName());
        if (entityType.equalsIgnoreCase("eventCRF")) {
          EventCRFBean ecb = (EventCRFBean) aeb;
          CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
          CRFDAO cdao = new CRFDAO(sm.getDataSource());
          CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(ecb.getCRFVersionId());
          CRFBean cb = (CRFBean) cdao.findByPK(cvb.getCrfId());
          //YW << save states of EventCRFBean for hiding resolving icon if a CRF hasn't been 
          //      marked complete (actually, DOUBLE_DATA_ENTRY_COMPLETE)  
          dnb.setStageId(ecb.getStage().getId());
          //YW >>
          dnr.setEntityName(cb.getName() + " (" + cvb.getName() + ")");
        }

        else if (entityType.equalsIgnoreCase("studyEvent")) {
          StudyEventBean seb = (StudyEventBean) aeb;

          StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
          StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) seddao.findByPK(seb
              .getStudyEventDefinitionId());

          dnr.setEntityName(sedb.getName());
        }

        else if (entityType.equalsIgnoreCase("itemData")) {
          ItemDataBean idb = (ItemDataBean) aeb;

          ItemDAO idao = new ItemDAO(sm.getDataSource());
          ItemBean ib = (ItemBean) idao.findByPK(idb.getItemId());
          
          //YW << save states of EventCRFBean for hiding resolving icon if a CRF hasn't been 
          //      marked complete (actually, DOUBLE_DATA_ENTRY_COMPLETE)  
          EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
          EventCRFBean ec = (EventCRFBean) ecdao.findByPK(idb.getEventCRFId());
          dnb.setStageId(ec.getStage().getId());
          //YW >>
          
  		  
          dnr.setEntityName(ib.getName());
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
   */
  protected void mayProceed() throws InsufficientPermissionException {
    if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
        || currentRole.getRole().equals(Role.COORDINATOR)) {
      return;
    }

    addPageMessage("You do not have permission to view discrepancies. "
        + "Please change your active study or contact the System Administrator.");
    throw new InsufficientPermissionException(Page.MENU_SERVLET,
        "not study director or study coordinator", "1");
  }
}