/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.login;

import java.util.ArrayList;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * Processes the request of changing current study
 */
public class ChangeStudyServlet extends SecureController {
  /**
   * Checks whether the user has the correct privilege
   */
  public void mayProceed() throws InsufficientPermissionException {

  }

  public void processRequest() throws Exception {

    String action = request.getParameter("action");//action sent by user
    UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
    StudyDAO sdao = new StudyDAO(sm.getDataSource());

    ArrayList studies = udao.findStudyByUser(ub.getName(), (ArrayList) sdao.findAll());

    if (StringUtil.isBlank(action)) {
      request.setAttribute("studies", studies);

      forwardPage(Page.CHANGE_STUDY);
    } else {
      if ("confirm".equalsIgnoreCase(action)) {
        logger.info("confirm");
        confirmChangeStudy(studies);

      } else if ("submit".equalsIgnoreCase(action)) {
        logger.info("submit");
        changeStudy();
      }
    }

  }

  private void confirmChangeStudy(ArrayList studies) throws Exception {
    Validator v = new Validator(request);
    FormProcessor fp = new FormProcessor(request);
    v.addValidation("studyId", Validator.IS_AN_INTEGER);

    errors = v.validate();

    if (!errors.isEmpty()) {
      request.setAttribute("studies", studies);
      forwardPage(Page.CHANGE_STUDY);
    } else {
      int studyId = fp.getInt("studyId");
      logger.info("new study id:" + studyId);
      for (int i = 0; i < studies.size(); i++) {
        StudyUserRoleBean studyWithRole = (StudyUserRoleBean) studies.get(i);
        if (studyWithRole.getStudyId() == studyId) {
          request.setAttribute("studyId", new Integer(studyId));
          session.setAttribute("studyWithRole", studyWithRole);
          forwardPage(Page.CHANGE_STUDY_CONFIRM);
          return;
        }
      }
      addPageMessage("No study selected.");
      forwardPage(Page.CHANGE_STUDY);
    }
  }

  private void changeStudy() throws Exception {
    Validator v = new Validator(request);
    FormProcessor fp = new FormProcessor(request);
    int studyId = fp.getInt("studyId");

    StudyDAO sdao = new StudyDAO(sm.getDataSource());
    StudyBean current = (StudyBean) sdao.findByPK(studyId);
    StudyParameterValueDAO spvdao = new StudyParameterValueDAO(sm.getDataSource());
    
    ArrayList studyParameters = spvdao.findParamConfigByStudy(current);
    
    current.setStudyParameters(studyParameters);
    
    StudyConfigService scs = new StudyConfigService(sm.getDataSource());
    if (current.getParentStudyId() <=0) {//top study           
      scs.setParametersForStudy(current);           
      
    } else {              
      scs.setParametersForSite(current);                 
       
    }
    if (current.getStatus().equals(Status.DELETED)) {
      session.removeAttribute("studyWithRole");
      addPageMessage("The study you chose has been removed, please restore it first if you have correct privilege or contact your System Administrator.");
    } else {
      session.setAttribute("study", current);
      currentStudy = current;
      //change user's active study id
      UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
      ub.setActiveStudyId(current.getId());
      ub.setUpdater(ub);
      ub.setUpdatedDate(new java.util.Date());
      udao.update(ub);
      
      session.setAttribute("userRole", session.getAttribute("studyWithRole"));
      currentRole=(StudyUserRoleBean)session.getAttribute("studyWithRole");
      session.removeAttribute("studyWithRole");
      addPageMessage("Your current active study has been changed successfully.");
    }
    ub.incNumVisitsToMainMenu();
    forwardPage(Page.MENU);

  }

}