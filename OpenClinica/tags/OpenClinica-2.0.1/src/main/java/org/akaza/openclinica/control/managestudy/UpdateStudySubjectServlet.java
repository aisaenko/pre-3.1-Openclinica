/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.submit.AddNewSubjectServlet;
import org.akaza.openclinica.core.form.DiscrepancyValidator;
import org.akaza.openclinica.core.form.FormDiscrepancyNotes;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * Processes request to update a study subject
 */
public class UpdateStudySubjectServlet extends SecureController {
  /**
   * Checks whether the user has the right permission to proceed function
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }

    if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
        || currentRole.getRole().equals(Role.COORDINATOR)) {
      return;
    }

    addPageMessage("You don't have correct privilege in your current active study. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.LIST_USER_IN_STUDY_SERVLET,
        "not study director", "1");
  }

  public void processRequest() throws Exception {
    FormDiscrepancyNotes discNotes = null;
    SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
    StudySubjectDAO subdao = new StudySubjectDAO(sm.getDataSource());
    FormProcessor fp = new FormProcessor(request);
    int studySubId = fp.getInt("id", true);//studySubjectId

    if (studySubId == 0) {
      addPageMessage("Please choose a study subject to edit.");
      forwardPage(Page.LIST_STUDY_SUBJECT_SERVLET);
    } else {
      String action = fp.getString("action", true);
      if (StringUtil.isBlank(action)) {
        addPageMessage("No action specified.");
        forwardPage(Page.LIST_STUDY_SUBJECT_SERVLET);
        return;
      }

      StudySubjectBean sub = (StudySubjectBean) subdao.findByPK(studySubId);

      StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(sm.getDataSource());
      StudyGroupDAO sgdao = new StudyGroupDAO(sm.getDataSource());
      SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(sm.getDataSource());
      ArrayList groupMaps = (ArrayList) sgmdao.findAllByStudySubject(studySubId);

      HashMap gMaps = new HashMap();
      for (int i = 0; i < groupMaps.size(); i++) {
        SubjectGroupMapBean groupMap = (SubjectGroupMapBean) groupMaps.get(i);
        gMaps.put(new Integer(groupMap.getStudyGroupClassId()), groupMap);

      }
      
      StudyDAO stdao = new StudyDAO(sm.getDataSource());
  	  ArrayList classes = new ArrayList();
      if (!"submit".equalsIgnoreCase(action)) {
    	//YW <<
    	int parentStudyId = currentStudy.getParentStudyId();
    	if(parentStudyId > 0) {
    		StudyBean parentStudy = (StudyBean)stdao.findByPK(parentStudyId);
    		classes = (ArrayList) sgcdao.findAllActiveByStudy(parentStudy);
    	} else {
    		classes = (ArrayList) sgcdao.findAllActiveByStudy(currentStudy);
    	}
    	//YW >>
        for (int i = 0; i < classes.size(); i++) {
          StudyGroupClassBean group = (StudyGroupClassBean) classes.get(i);
          ArrayList studyGroups = sgdao.findAllByGroupClass(group);
          group.setStudyGroups(studyGroups);
          SubjectGroupMapBean gMap = (SubjectGroupMapBean) gMaps.get(new Integer(group.getId()));
          if (gMap != null) {
            group.setStudyGroupId(gMap.getStudyGroupId());
            group.setGroupNotes(gMap.getNotes());
          }
        }

        session.setAttribute("groups", classes);
      }

      if ("show".equalsIgnoreCase(action)) {
    	 
        session.setAttribute("studySub", sub);
        discNotes = new FormDiscrepancyNotes();
        session.setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);

        forwardPage(Page.UPDATE_STUDY_SUBJECT);
      } else if ("confirm".equalsIgnoreCase(action)) {
        confirm(sgdao);

      } else if ("submit".equalsIgnoreCase(action)) {//submit to DB
        StudySubjectBean subject = (StudySubjectBean) session.getAttribute("studySub");
        subject.setUpdater(ub);
        subdao.update(subject);
        
       //save discrepancy notes into DB
        FormDiscrepancyNotes fdn = (FormDiscrepancyNotes)session.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
        DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());
        AddNewSubjectServlet.saveFieldNotes("enrollmentDate", fdn, dndao, subject.getId(), "studySub", currentStudy);
        
        ArrayList groups = (ArrayList) session.getAttribute("groups"); 
        if (!groups.isEmpty()) {
          for (int i = 0; i < groups.size(); i++) {
            StudyGroupClassBean sgc = (StudyGroupClassBean) groups.get(i);
            SubjectGroupMapBean sgm = new SubjectGroupMapBean();
            SubjectGroupMapBean gMap = (SubjectGroupMapBean) gMaps.get(new Integer(sgc.getId()));
            sgm.setStudyGroupId(sgc.getStudyGroupId());
            sgm.setNotes(sgc.getGroupNotes());
            sgm.setStudyGroupClassId(sgc.getId());
            sgm.setStudySubjectId(subject.getId());
            sgm.setStatus(Status.AVAILABLE);
            if (sgm.getStudyGroupId() > 0) {
              if (gMap != null && gMap.getId()>0) {
                sgm.setUpdater(ub);
                sgm.setId(gMap.getId());
                sgmdao.update(sgm);
              } else {
                sgm.setOwner(ub);             
                sgmdao.create(sgm);
              }
            }

          }
        }

        addPageMessage("The study subject has been updated successfully.");
        session.removeAttribute("studySub");
        session.removeAttribute("groups");
        session.removeAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
        request.setAttribute("id", new Integer(studySubId).toString());
        
        forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
      } else {
        addPageMessage("No action specified.");
        forwardPage(Page.LIST_STUDY_SUBJECT_SERVLET);
        return;
      }

    }
  }

  /**
   * Processes 'confirm' request, validate the study subject object
   * 
   * @param sub
   * @throws Exception
   */
  private void confirm(StudyGroupDAO sgdao) throws Exception {
    ArrayList classes = (ArrayList) session.getAttribute("groups");
    StudySubjectBean sub = (StudySubjectBean) session.getAttribute("studySub");
    FormDiscrepancyNotes discNotes = (FormDiscrepancyNotes)session.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
    DiscrepancyValidator v = new DiscrepancyValidator(request, discNotes);
    FormProcessor fp = new FormProcessor(request);

    v.addValidation("label", Validator.NO_BLANKS);

    String eDateString = fp.getString("enrollmentDate");
    if (!StringUtil.isBlank(eDateString)) {
      v.addValidation("enrollmentDate", Validator.IS_A_DATE);
      v.alwaysExecuteLastValidation("enrollmentDate");
    }

    if (!classes.isEmpty()) {
      for (int i = 0; i < classes.size(); i++) {
        StudyGroupClassBean sgc = (StudyGroupClassBean) classes.get(i);
        int groupId = fp.getInt("studyGroupId" + i);
        String notes = fp.getString("notes" + i);
        v.addValidation("notes" + i, Validator.LENGTH_NUMERIC_COMPARISON,
            NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
        sgc.setStudyGroupId(groupId);
        sgc.setGroupNotes(notes);
        if (groupId > 0) {
          StudyGroupBean sgb = (StudyGroupBean) sgdao.findByPK(groupId);
          sgc.setStudyGroupName(sgb.getName());
        }
      }
    }
    session.setAttribute("groups", classes);
    errors = v.validate();
    
    if (!StringUtil.isBlank(fp.getString("label"))) {
      StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());      
     
      StudySubjectBean sub1 = (StudySubjectBean) ssdao.findAnotherBySameLabel(fp.getString("label").trim(),currentStudy.getId(),sub.getId());
      
      //JRWS>> Also look for labels in the child studies
      if (sub1.getId() == 0) {
    	  sub1 = (StudySubjectBean) ssdao.findAnotherBySameLabelInSites(fp.getString("label").trim(),currentStudy.getId(),sub.getId());
      }
      
      if (sub1.getId() > 0) {
        Validator.addError(errors, "label", "Study Subject ID has been used by another subject in current study, please choose a unique one.");
      }
    }


    sub.setLabel(fp.getString("label"));
    sub.setSecondaryLabel(fp.getString("secondaryLabel"));

    java.util.Date enrollDate = null;
    try {
      sdf.setLenient(false);
      if (!StringUtil.isBlank(eDateString)) {
        enrollDate = sdf.parse(eDateString);
      }
    } catch (ParseException fe) {

    }
    sub.setEnrollmentDate(enrollDate);
    session.setAttribute("studySub", sub);
    if (!errors.isEmpty()) {
      logger.info("has errors");
      if (StringUtil.isBlank(sub.getLabel())) {
        addPageMessage("You must enter a Study Subject ID for identifying the subject. "
            + "This may be an external ID number containing letters, numbers, and dashes, "
            + "or you may simply enter the Study Subject ID listed below. "
            + "The Study Subject ID should not contain Protected Health Information such as name or SSN.");
      } else {
        StudySubjectDAO subdao = new StudySubjectDAO(sm.getDataSource());
        StudySubjectBean sub1 = (StudySubjectBean) subdao.findAnotherBySameLabel(sub.getLabel(),
            sub.getStudyId(), sub.getId());
        if (sub1.getId() > 0) {
          addPageMessage("Study Subject ID has been used by another subject in current study, please choose a unique one.");
        }
      }

      request.setAttribute("formMessages", errors);
      forwardPage(Page.UPDATE_STUDY_SUBJECT);

    } else {
      forwardPage(Page.UPDATE_STUDY_SUBJECT_CONFIRM);
    }

  }

}