/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2007 Akaza Research
 */


package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.admin.AuditDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jsampson
 * 
 */

public class ViewStudySubjectAuditLogServlet extends SecureController {
	
	 /**
	   * Checks whether the user has the right permission to proceed function
	   */
	  public void mayProceed() throws InsufficientPermissionException {
	    if (ub.isSysAdmin()) {
	      return;
	    }

	    if (currentRole != null) {
			Role r = currentRole.getRole();
			if ((r != null) &&
					(r.equals(Role.COORDINATOR)
							|| r.equals(Role.STUDYDIRECTOR)
							|| r.equals(Role.INVESTIGATOR)
							|| r.equals(Role.RESEARCHASSISTANT))) {
				return;
			}
		}


	    addPageMessage("You don't have correct privilege in your current active study. "
	        + "Please change your active study or contact your sysadmin.");
	    throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECT_SERVLET,
	        "not study director", "1");

	  }
		    
		  public void processRequest() throws Exception {
		    StudySubjectDAO subdao = new StudySubjectDAO(sm.getDataSource());
		    SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
		    AuditDAO adao = new AuditDAO(sm.getDataSource());
		    FormProcessor fp = new FormProcessor(request);
			StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
			StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
			EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
			EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
			StudyDAO studydao = new StudyDAO(sm.getDataSource());
			CRFDAO cdao = new CRFDAO(sm.getDataSource());
			CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
		    ArrayList studySubjectAudits = new ArrayList();
		    ArrayList eventCRFAudits = new ArrayList();


		    int studySubId = fp.getInt("id", true);//studySubjectId		    
		    
		    if (studySubId == 0) {
				addPageMessage("Please choose a subject to view.");
				forwardPage(Page.LIST_STUDY_SUBJECT_SERVLET);
		    } else {
		    	StudySubjectBean studySubject = (StudySubjectBean) subdao.findByPK(studySubId);      
			    request.setAttribute("studySub", studySubject);
				SubjectBean subject = (SubjectBean) sdao.findByPK(studySubject.getSubjectId());		       
				request.setAttribute("subject", subject);
				StudyBean study = (StudyBean) studydao.findByPK(studySubject.getStudyId());
				request.setAttribute("study", study);
				
				//Show both study subject and subject audit events together
			    studySubjectAudits.addAll(adao.findStudySubjectAuditEvents(studySubject.getId())); //Study subject value changed
			    studySubjectAudits.addAll(adao.findSubjectAuditEvents(subject.getId())); //Global subject value changed
			    request.setAttribute("studySubjectAudits", studySubjectAudits);
			    
			    //Get the list of events
			    ArrayList events = (ArrayList) sedao.findAllByStudySubject(studySubject);
			    for (int i = 0; i < events.size(); i++) {
			    	//Link study event definitions
				    StudyEventBean studyEvent = (StudyEventBean) events.get(i);
				    studyEvent.setStudyEventDefinition((StudyEventDefinitionBean) seddao.findByPK(studyEvent.getStudyEventDefinitionId()));

				    //Link event CRFs
				    studyEvent.setEventCRFs(ecdao.findAllByStudyEvent(studyEvent));
			    }
			    
			    for (int i = 0; i < events.size(); i++) {
				    StudyEventBean studyEvent = (StudyEventBean) events.get(i);
			    	ArrayList eventCRFs = studyEvent.getEventCRFs();
			    	for (i = 0; i < eventCRFs.size(); i++) {
				    	//Link CRF and CRF Versions
				    	EventCRFBean eventCRF = (EventCRFBean) eventCRFs.get(i);
				    	eventCRF.setCrfVersion((CRFVersionBean) cvdao.findByPK(eventCRF.getCRFVersionId()));
				    	eventCRF.setCrf((CRFBean) cdao.findByVersionId(eventCRF.getCRFVersionId()));
					    //Get the event crf audits
					    eventCRFAudits.addAll(adao.findEventCRFAuditEvents(eventCRF.getId()));
			    	}
			    }
			    request.setAttribute("events", events);			    
			    request.setAttribute("eventCRFAudits", eventCRFAudits);

		    forwardPage(Page.VIEW_STUDY_SUBJECT_AUDIT);

		    }
		  }
		  
		  protected String getAdminServlet() {
		    if (ub.isSysAdmin()) {
		      return SecureController.ADMIN_SERVLET_CODE;
		    } else {
		      return "";
		    }
		  }


}
