/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.bean.service.StudyParamsConfig;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.bean.core.Role;

import org.akaza.openclinica.control.core.SecureController;

/**
 * @author jxu
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ViewSiteServlet extends SecureController {
  /**
   * Checks whether the user has the correct privilege
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }
    if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
        || currentRole.getRole().equals(Role.COORDINATOR)
        || currentRole.getRole().equals(Role.INVESTIGATOR)
		|| currentRole.getRole().equals(Role.RESEARCHASSISTANT)) {
      return;
    }
    
    
    addPageMessage("You don't have correct privilege in your current active study. "
         + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.MENU_SERVLET, "not director", "1");
   
  }
  
  public void processRequest() throws Exception {     
    
    StudyDAO sdao = new StudyDAO(sm.getDataSource());
    String idString = request.getParameter("id");
    logger.info("site id:" + idString);
    if (StringUtil.isBlank(idString)) {
      addPageMessage("Please choose a site to edit.");
      forwardPage(Page.SITE_LIST_SERVLET);
    } else {     
      int siteId = Integer.valueOf(idString.trim()).intValue();
      StudyBean study = (StudyBean) sdao.findByPK(siteId);
      
      if (currentStudy.getId() != study.getId()) {
       
        ArrayList configs = new ArrayList();
        StudyParameterValueDAO spvdao = new StudyParameterValueDAO(sm.getDataSource());
        configs = spvdao.findParamConfigByStudy(study);
        study.setStudyParameters(configs);      
        
      }
      
      String parentStudyName = "";
      if (study.getParentStudyId() >0){
        StudyBean parent = (StudyBean) sdao.findByPK(study.getParentStudyId());
        parentStudyName = parent.getName();
      }
      request.setAttribute("parentName", parentStudyName);
      request.setAttribute("siteToView", study);
      forwardPage(Page.VIEW_SITE);
    }
  }

}