/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.bean.service.StudyParamsConfig;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * @version CVS: $Id: InitUpdateSubStudyServlet.java 8556 2006-10-02 01:02:55Z jxu $
 */
public class InitUpdateSubStudyServlet extends SecureController {
  
  /**
   *  
   */
  public void mayProceed() throws InsufficientPermissionException {
  	if (ub.isSysAdmin()) {
  		return ;
  	}
  	if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
         || currentRole.getRole().equals(Role.COORDINATOR)) {
       return;
     }

    addPageMessage("You don't have correct privilege in your current active study. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.MENU_SERVLET, "not study director", "1");

  }
  

  public void processRequest()throws Exception {

    String userName = request.getRemoteUser();    
    StudyDAO sdao = new StudyDAO(sm.getDataSource());
    String idString = request.getParameter("id");
    logger.info("study id:" + idString);
    if (StringUtil.isBlank(idString)) {
      addPageMessage("Please choose a study to edit.");
      forwardPage(Page.STUDY_LIST_SERVLET);
    } else {
      int studyId = Integer.valueOf(idString.trim()).intValue();
      StudyBean study = (StudyBean) sdao.findByPK(studyId);
      String parentStudyName = "";
      if (study.getParentStudyId() >0){
        StudyBean parent = (StudyBean) sdao.findByPK(study.getParentStudyId());
        parentStudyName = parent.getName();
      }
      
      if (currentStudy.getId() != study.getId()) {
        ArrayList parentConfigs = currentStudy.getStudyParameters();
         //logger.info("parentConfigs size:" + parentConfigs.size());
        ArrayList configs = new ArrayList();
        StudyParameterValueDAO spvdao = new StudyParameterValueDAO(sm.getDataSource());
        for (int i = 0; i < parentConfigs.size(); i++) {
          StudyParamsConfig scg = (StudyParamsConfig) parentConfigs.get(i);
          if (scg != null) {
            // find the one that sub study can change
            if (scg.getValue().getId() > 0 && scg.getParameter().isOverridable()) {
               //logger.info("parameter:" + scg.getParameter().getHandle());
               //logger.info("value:" + scg.getValue().getValue());
               StudyParameterValueBean spvb = spvdao.findByHandleAndStudy(study.getId(),scg.getParameter().getHandle());
               if (spvb.getId()>0) {
                // the sub study itself has the parameter 
                 scg.setValue(spvb);
               }
              configs.add(scg);
            }
          }

        }
        
       
        study.setStudyParameters(configs);
      }
      
      session.setAttribute("parentName", parentStudyName);
      session.setAttribute("newStudy", study);
      request.setAttribute("facRecruitStatusMap", CreateStudyServlet.facRecruitStatusMap);
      request.setAttribute("statuses", Status.toArrayList());
      
      FormProcessor fp = new FormProcessor(request);
      logger.info("start date:" + study.getDatePlannedEnd());
      if (study.getDatePlannedEnd() != null){
        fp.addPresetValue(UpdateSubStudyServlet.INPUT_END_DATE, sdf.format(study.getDatePlannedEnd()));
      } 
      if (study.getDatePlannedStart() != null){
        fp.addPresetValue(UpdateSubStudyServlet.INPUT_START_DATE, sdf.format(study.getDatePlannedStart()));
      } 
      setPresetValues(fp.getPresetValues());
      forwardPage(Page.UPDATE_SUB_STUDY);
    }

  }
  
  protected String getAdminServlet() {
    if (ub.isSysAdmin()) {
		return SecureController.ADMIN_SERVLET_CODE;
	}
	else {
		return "";
	}
  }

}