/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class InitUpdateCRFServlet extends SecureController {
 
  private static String CRF_ID = "crfId";

  private static String CRF = "crf";

  /**
   *  
   */
  public void mayProceed() throws InsufficientPermissionException {
  	if (ub.isSysAdmin()) {
  		return ;
  	}
  	
    boolean isStudyDirectorInParent = false;
    if (currentStudy.getParentStudyId() > 0) {
      logger.info("2222");
      Role r = ub.getRoleByStudy(currentStudy.getParentStudyId()).getRole();
      if (r.equals(Role.STUDYDIRECTOR) || r.equals(Role.ADMIN)) {
        isStudyDirectorInParent = true;
      }
    }

    //get current studyid
    int studyId = currentStudy.getId();

    if (ub.hasRoleInStudy(studyId)) {
      Role r = ub.getRoleByStudy(studyId).getRole();
      if (isStudyDirectorInParent || r.equals(Role.STUDYDIRECTOR) || r.equals(Role.ADMIN)) {
        return;
      }
    }

    addPageMessage("You do not have permission to update a CRF. "
        + "Please change your active study or contact the System Administrator.");
    throw new InsufficientPermissionException(Page.CRF_LIST_SERVLET, "not study director", "1");

  }

  public void processRequest() throws Exception {
    
    resetPanel();
	panel.setStudyInfoShown(false);
	panel.setOrderedData(true);	
   
	setToPanel("Create CRF","<br>Create a new CRF by entering a name and description.");
	
	setToPanel("Create CRF Version","<br>Create a new CRF version by uploading an excel spreadsheet " +
			"defining the CRF's data elements and layout.");
	setToPanel("Revise CRF Version" ,"<br>If you are the owner of a CRF version, and the CRF version " +
			"has not been used in a study, you can overwrite " +
			"the CRF version by uploading a new excel spreadsheet with same version name. In this case, " +
			"system will ask you whether you want to delete the " +
			"previous contents and upload a new version.");
    setToPanel("CRF Spreadsheet <br>Template","<br>Download a blank CRF Excel spreadsheet " +
    		"template <a href=\"DownloadVersionSpreadSheet?template=1\"><b>here</b></a>.");
	setToPanel("Example CRF <br>Spreadsheets","<br>Download example CRFs and instructions from the" +
			" <a href=\"http://www.openclinica.org/entities/entity_details.php?eid=151\"><b>OpenClinica.org portal</b></a> " +
			"(OpenClinica.org user account required).");
    

    FormProcessor fp = new FormProcessor(request);
    int crfId = fp.getInt(CRF_ID);
    if (crfId == 0) {
      addPageMessage("Please choose a CRF to update.");
      forwardPage(Page.CRF_LIST_SERVLET);
    } else {
      CRFDAO cdao = new CRFDAO(sm.getDataSource());
      CRFBean crf = (CRFBean) cdao.findByPK(crfId);
      session.setAttribute(CRF, crf);
      forwardPage(Page.UPDATE_CRF);

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