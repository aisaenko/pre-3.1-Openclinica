/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * Prepares to process request of updating a study object
 * 
 * @author jxu 
 * @version CVS: $Id: InitUpdateStudyServlet.java,v 1.7 2006/11/01 23:38:19 jxu Exp $
 */
public class InitUpdateStudyServlet extends SecureController {
  
  /**
   *  
   */
  public void mayProceed() throws InsufficientPermissionException {
  	if (ub.isSysAdmin()) {
  		return ;
  	}
   

    addPageMessage("You don't have correct privilege in your current active study. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.STUDY_LIST_SERVLET, "not admin", "1");

  }

  /**
   * Processes the request
   */
  public void processRequest()throws Exception {
   
    StudyDAO sdao = new StudyDAO(sm.getDataSource());
    String idString = request.getParameter("id");
    logger.info("study id:" + idString);
    if (StringUtil.isBlank(idString)) {
      addPageMessage("Please choose a study to edit.");
      forwardPage(Page.STUDY_LIST_SERVLET);
    } else {
      int studyId = Integer.valueOf(idString.trim()).intValue();
      StudyBean study = (StudyBean) sdao.findByPK(studyId);
      StudyConfigService scs = new StudyConfigService(sm.getDataSource());
      study = scs.setParametersForStudy(study);
      
      logger.info("date created:" + study.getCreatedDate());
      logger.info("protocol Type:" + study.getProtocolType());

      session.setAttribute("newStudy", study);
      request.setAttribute("facRecruitStatusMap", CreateStudyServlet.facRecruitStatusMap);
      request.setAttribute("statuses", Status.toArrayList());
      
      resetPanel();
      panel.setStudyInfoShown(false);    
      panel.setOrderedData(true);
      panel.setExtractData(false);
      panel.setSubmitDataModule(false);
      panel.setCreateDataset(false);       
      panel.setIconInfoShown(true);
      panel.setManageSubject(false);
  	  
      forwardPage(Page.UPDATE_STUDY1);
    }

  }
  
  protected String getAdminServlet() {
    return SecureController.ADMIN_SERVLET_CODE;
  }

}