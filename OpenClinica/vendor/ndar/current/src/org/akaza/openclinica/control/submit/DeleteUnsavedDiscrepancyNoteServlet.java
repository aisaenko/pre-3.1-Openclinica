/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 
 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormDiscrepancyNotes;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * Create a discrepancy note for a data entity
 * 
 * @author jxu
 *  
 */
public class DeleteUnsavedDiscrepancyNoteServlet extends SecureController {
	public static final String DIS_TYPES = "discrepancyTypes";
	
	public static final String RES_STATUSES = "resolutionStatuses";
	
	public static final String ID = "id";
	public static final String ENTITY_ID = "id";
	
	public static final String PARENT_ID = "parentId";//parent note id
	
	public static final String ENTITY_TYPE = "name";
	
	public static final String ENTITY_COLUMN = "column";
	
	public static final String ENTITY_FIELD = "field";
	
	public static final String FORM_DISCREPANCY_NOTES_NAME = "fdnotes";
	
	public static final String DIS_NOTE = "discrepancyNote";
	
	public static final String WRITE_TO_DB = "writeToDB";
	
	public static final String PRESET_RES_STATUS = "strResStatus";
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
	 */
	protected void mayProceed() throws InsufficientPermissionException {
		String exceptionName = "no permission to delete a discrepancy note";
		String noAccessMessage = "You may not delete a discrepancy note.  "
			+ "Please change your active study or contact the System Administrator.";
		
		if (SubmitDataServlet.maySubmitData(ub, currentRole)) {
			return;
		}
		
		addPageMessage(noAccessMessage);
		throw new InsufficientPermissionException(Page.MENU, exceptionName, "1");
	}
	
	protected void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());
		request.setAttribute(DIS_TYPES, DiscrepancyNoteType.toArrayList());
		request.setAttribute(RES_STATUSES, ResolutionStatus.toArrayList());

		int id = (new Integer(request.getParameter("id"))).intValue();
		int entityId = fp.getInt(ENTITY_ID);
		String entityType = fp.getString(ENTITY_TYPE);
		String field = fp.getString(ENTITY_FIELD);
		String column = fp.getString(ENTITY_COLUMN);
		int parentId = fp.getInt(PARENT_ID);
		String strResStatus = fp.getString(PRESET_RES_STATUS);
		if (!strResStatus.equals("")) {
			request.setAttribute(PRESET_RES_STATUS, strResStatus);
		}

		DiscrepancyNoteBean parent = new DiscrepancyNoteBean();
		if (parentId > 0) {
			dndao.setFetchMapping(true);
			parent = (DiscrepancyNoteBean) dndao.findByPK(parentId);
			if (parent.isActive()) {
				request.setAttribute("parent", parent);
			}
			dndao.setFetchMapping(false);
		}

		ArrayList notes = (ArrayList) dndao.findAllByEntityAndColumn(entityType, entityId, column);
		FormDiscrepancyNotes newNotes = (FormDiscrepancyNotes) session.getAttribute(FORM_DISCREPANCY_NOTES_NAME);
		
		if (newNotes == null) {
			newNotes = new FormDiscrepancyNotes();
		}
		 
			FormDiscrepancyNotes noteTree = (FormDiscrepancyNotes) session.getAttribute(FORM_DISCREPANCY_NOTES_NAME);
			
			String description = fp.getString("description");
			int typeId = fp.getInt("typeId");
			int resStatusId = fp.getInt("resStatusId");
			String detailedDes = fp.getString("detailedDes");
			DiscrepancyNoteBean note = new DiscrepancyNoteBean();
			
			note.setId(id);
			note.setDescription(description);
			note.setDetailedNotes(detailedDes);
			note.setOwner(ub);
			note.setCreatedDate(new Date());
			note.setResolutionStatusId(resStatusId);
			note.setDiscrepancyNoteTypeId(typeId);
			note.setParentDnId(parentId);
			note.setField(field);	
			
			
			if (!parent.isActive()) {
				note.setEntityId(entityId);
				note.setEntityType(entityType);
				note.setColumn(column);      	
			}
			else {
				note.setEntityId(parent.getEntityId());
				note.setEntityType(parent.getEntityType());
				note.setColumn(parent.getColumn());
			}
			
			note.setStudyId(currentStudy.getId());
			
			getNoteInfo(note);//populate note infos
			
			request.setAttribute(DIS_NOTE, note);
			
			  
					logger.info("delete note from session");
					if (!noteTree.deleteUnsavedNote(field, note)) {
						setInputMessages(errors);
						forwardPage(Page.ADD_DISCREPANCY_NOTE);
						return;
					}
				
					if (!notes.isEmpty() || !newNotes.getNotes(field).isEmpty()) {
						request.setAttribute("hasNotes", "yes");
					} else {
						request.setAttribute("hasNotes", "no");
					}
				//addPageMessage("Your discrepancy note has been deleted.");
				forwardPage(Page.DELETE_DISCREPANCY_NOTE_DONE);
			
		
	}
	
    /**
     * Constructs a url for creating new note on 'view note list' page
     * @param note
     * @param preset
     * @return
     */
	public static String getAddChildURL(DiscrepancyNoteBean note, ResolutionStatus preset) {
		ArrayList arguments = new ArrayList();
		
		arguments.add(ENTITY_TYPE + "=" + note.getEntityType());
		arguments.add(ENTITY_ID + "=" + note.getEntityId());
		arguments.add(PARENT_ID + "=" + note.getId());
		arguments.add(WRITE_TO_DB + "=" + "1");
		
		if (preset.isActive()) {
			arguments.add(PRESET_RES_STATUS + "=" + String.valueOf(preset.getId()));
		}
		
		String queryString = StringUtil.join("&", arguments);
		
		return "CreateDiscrepancyNote?" + queryString;
	}
	
	
    /**
     * Pulls the note related information from database according to note type
     * @param note
     */
    private void getNoteInfo(DiscrepancyNoteBean note) {
	  StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
	  if ("itemData".equalsIgnoreCase(note.getEntityType())) {
	    int itemDataId = note.getEntityId();
	    ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
	    ItemDataBean itemData = (ItemDataBean)iddao.findByPK(itemDataId);
	    ItemDAO idao = new ItemDAO(sm.getDataSource());
	    ItemBean item = (ItemBean)idao.findByPK(itemData.getItemId());
	    note.setEntityName(item.getName());
	    EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
	    StudyEventDAO svdao = new StudyEventDAO(sm.getDataSource());
	   
	    EventCRFBean ec = (EventCRFBean)ecdao.findByPK(itemData.getEventCRFId());
	    StudyEventBean event = (StudyEventBean)svdao.findByPK(ec.getStudyEventId());
	    
	    StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
	    StudyEventDefinitionBean sed = (StudyEventDefinitionBean)seddao.findByPK(event.getStudyEventDefinitionId());
	    note.setEventName(sed.getName());
	    note.setEventStart(event.getDateStarted());	
	    			    
	    CRFDAO cdao = new CRFDAO(sm.getDataSource());
	    CRFBean crf = (CRFBean)cdao.findByVersionId(ec.getCRFVersionId());
	    note.setCrfName(crf.getName());
	    StudySubjectBean ss = (StudySubjectBean)ssdao.findByPK(ec.getStudySubjectId());
	    note.setSubjectName(ss.getName());			    
	    
	  } else if ("eventCrf".equalsIgnoreCase(note.getEntityType())) {
	    int eventCRFId = note.getEntityId();
	    EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
	    StudyEventDAO svdao = new StudyEventDAO(sm.getDataSource());
		   
		EventCRFBean ec = (EventCRFBean)ecdao.findByPK(eventCRFId);
		StudyEventBean event = (StudyEventBean)svdao.findByPK(ec.getStudyEventId());
		    
		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
		StudyEventDefinitionBean sed = (StudyEventDefinitionBean)seddao.findByPK(event.getStudyEventDefinitionId());
		note.setEventName(sed.getName());
		note.setEventStart(event.getDateStarted());	
		    			    
		CRFDAO cdao = new CRFDAO(sm.getDataSource());
		CRFBean crf = (CRFBean)cdao.findByVersionId(ec.getCRFVersionId());
		note.setCrfName(crf.getName());
		StudySubjectBean ss = (StudySubjectBean)ssdao.findByPK(ec.getStudySubjectId());
		note.setSubjectName(ss.getName());	
	    
	    
	  } else if ("studyEvent".equalsIgnoreCase(note.getEntityType())) {
	    int eventId = note.getEntityId();
	    StudyEventDAO svdao = new StudyEventDAO(sm.getDataSource());
	    StudyEventBean event = (StudyEventBean)svdao.findByPK(eventId);
	    
	    StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
	    StudyEventDefinitionBean sed = (StudyEventDefinitionBean)seddao.findByPK(event.getStudyEventDefinitionId());
	    note.setEventName(sed.getName());
	    note.setEventStart(event.getDateStarted());	
	    
	    StudySubjectBean ss = (StudySubjectBean)ssdao.findByPK(event.getStudySubjectId());
	    note.setSubjectName(ss.getName());	
	    
	    
	  } else if ("studySub".equalsIgnoreCase(note.getEntityType())) {
	    int studySubjectId = note.getEntityId();
	    StudySubjectBean ss = (StudySubjectBean)ssdao.findByPK(studySubjectId);
	    note.setSubjectName(ss.getName());	
	    
	    
	  } else if ("Subject".equalsIgnoreCase(note.getEntityType())){
	    int subjectId = note.getEntityId();			   
	    StudySubjectBean ss = ssdao.findBySubjectIdAndStudy(subjectId, currentStudy);
	    note.setSubjectName(ss.getName());	
	  }
	}
}