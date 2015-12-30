/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * Prepares to creat a new CRF Version
 * 
 * @author jxu
 */
public class InitCreateCRFVersionServlet extends SecureController {
  /**
   *  
   */
  public void mayProceed() throws InsufficientPermissionException {
  	if (ub.isSysAdmin()) {
  		return ;
  	}
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
    
    String idString = request.getParameter("crfId");
    String name = request.getParameter("name");
    logger.info("crf id:" + idString);
    if (StringUtil.isBlank(idString) || StringUtil.isBlank(name)) {
      addPageMessage("Please choose a CRF to add new version for.");
      forwardPage(Page.CRF_LIST);
    } else {
      //crf id
      int crfId = Integer.valueOf(idString.trim()).intValue();
      CRFVersionBean version = new CRFVersionBean();
      version.setCrfId(crfId);
      session.setAttribute("version", version);
      session.setAttribute("crfName",name);
      forwardPage(Page.CREATE_CRF_VERSION);
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