/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * Assigns a study subject to another study
 */
public class AssignStudySubjectServlet extends SecureController {
  /**
   *  
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
    throw new InsufficientPermissionException(Page.MENU_SERVLET, "not study director", "1");

  }

  public void processRequest() throws Exception {
    String action = request.getParameter("action");
    StudyDAO sdao = new StudyDAO(sm.getDataSource());
    StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
    SubjectDAO subdao = new SubjectDAO(sm.getDataSource());
    FormProcessor fp = new FormProcessor(request);

    int studySubId = fp.getInt("id");
    if (studySubId == 0) {
      addPageMessage("Please choose a subject to assign.");
      forwardPage(Page.LIST_STUDY_SUBJECT_SERVLET);
      return;
    }
    StudySubjectBean studySub = (StudySubjectBean) ssdao.findByPK(studySubId);
    int subjectId = studySub.getSubjectId();
    request.setAttribute("studySub", studySub);
    SubjectBean subject = (SubjectBean) subdao.findByPK(subjectId);
    request.setAttribute("subject", subject);

    if (StringUtil.isBlank(action)) {
      //create a list of existing studies for the subject
      ArrayList<StudyBean> assignedStudies = sdao.findAllStudiesBySubjectId(subjectId);
      Set<Integer> inParentStudyIds = new HashSet<Integer>();
      for (StudyBean sb : assignedStudies) {
        Integer stId=new Integer(sb.getParentStudyId()>0 ? sb.getParentStudyId() : sb.getId());
        if (!inParentStudyIds.contains(stId)) {
          inParentStudyIds.add(stId);
        }
      }

      ArrayList studies=new ArrayList();
      UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
      ArrayList<StudyUserRoleBean> userStudies = (ArrayList<StudyUserRoleBean>) udao.findStudyByUser(ub.getName(), (ArrayList) sdao.findAllByStatus(Status.AVAILABLE));
      for (StudyUserRoleBean studyUserRole : userStudies) {
        StudyBean study= (StudyBean) sdao.findByPK(studyUserRole.getStudyId());

        //check permission
        //TODO: need to check which role to see if permission to enroll

        //check to see if subject is already assigned to study/site
        if (inParentStudyIds.contains(study.getParentStudyId()>0 ? study.getParentStudyId() : study.getId())) {
          continue;
        }
            
        studies.add(studyUserRole);
      }
      //request.setAttribute("studies", studies);
      request.setAttribute("studies", studies);
      forwardPage(Page.ASSIGN_STUDY_SUBJECT);
    } else {
      int studyId = fp.getInt("studyId");

      if (studyId == 0) {
        addPageMessage("Please choose a study/site to assign the subject.");
        forwardPage(Page.ASSIGN_STUDY_SUBJECT);
        return;
      }

      StudyParameterValueDAO spvdao = new StudyParameterValueDAO(sm.getDataSource());
      StudyBean st = (StudyBean) sdao.findByPK(studyId);
      
      ArrayList studyParameters = spvdao.findParamConfigByStudy(st);
      
      st.setStudyParameters(studyParameters);
      
      StudyConfigService scs = new StudyConfigService(sm.getDataSource());
      if (st.getParentStudyId() <=0) {//top study           
        scs.setParametersForStudy(st);           
      } else {              
        scs.setParametersForSite(st);                 
      }

      if ("confirm".equalsIgnoreCase(action)) {
        request.setAttribute("newStudy", st);
        forwardPage(Page.ASSIGN_STUDY_SUBJECT_CONFIRM);
      } else {
        logger.info("submit to assign the subject");
        StudySubjectBean studySubject = new StudySubjectBean();
        // enroll the subject in the active study
        String idSetting = st.getStudyParameterConfig().getSubjectIdGeneration();
        //set up auto study subject id
        if (idSetting!=null && (idSetting.equals("auto editable") || idSetting.equals("auto non-editable")) ) {
          int nextLabel = ssdao.findTheGreatestLabel()+1;
          studySubject.setLabel(""+nextLabel);
        } else {
        	String currentStudyIdentifier = (currentStudy.getIdentifier().length() > 13 ? currentStudy.getIdentifier().substring(0, 10)+"..." : currentStudy.getIdentifier());
        	String prefix = "Added from "+currentStudyIdentifier+" #";
            int nextLabel = ssdao.findTheGreatestLabel(prefix)+1;
            studySubject.setLabel(prefix+nextLabel);
        }
        studySubject.setSubjectId(subject.getId());
        studySubject.setStudyId(studyId);
        studySubject.setEnrollmentDate(new Date());
        studySubject.setSecondaryLabel(studySub.getSecondaryLabel());
        studySubject.setStatus(Status.AVAILABLE);
        studySubject.setOwner(ub);

        studySubject = ssdao.createWithoutGroup(studySubject);

        addPageMessage("The subject " + studySub.getLabel() + " has been assigned to " + 
            "study/site " + st.getName() + " with Study-Subject: " + studySubject.getLabel() + ".");
        forwardPage(Page.LIST_STUDY_SUBJECT_SERVLET);
      }
    }
  }
}
