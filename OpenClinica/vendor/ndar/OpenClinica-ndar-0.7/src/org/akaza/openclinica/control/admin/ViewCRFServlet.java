/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.util.ArrayList;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ViewCRFServlet extends SecureController {

  private static String CRF_ID = "crfId";

  private static String CRF = "crf";

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
    resetPanel();
    panel.setStudyInfoShown(false);
    panel.setOrderedData(true);
    panel.setSubmitDataModule(false);
    panel.setExtractData(false);
    panel.setCreateDataset(false);

    setToPanel("Create CRF", "<br>Create a new CRF by entering a name and description.");

    setToPanel("Create CRF Version",
        "<br>Create a new CRF version by uploading an excel spreadsheet "
            + "defining the CRF's data elements and layout.");
    setToPanel(
        "Revise CRF Version",
        "<br>If you are the owner of a CRF version, and the CRF version "
            + "has not been used in a study, you can overwrite "
            + "the CRF version by uploading a new excel spreadsheet with same version name. In this case, "
            + "system will ask you whether you want to delete the "
            + "previous contents and upload a new version.");
    setToPanel("CRF Spreadsheet <br>Template", "<br>Download a blank CRF Excel spreadsheet "
        + "template <a href=\"DownloadVersionSpreadSheet?template=1\"><b>here</b></a>.");
    setToPanel(
        "Example CRF <br>Spreadsheets",
        "<br>Download example CRFs and instructions from the"
            + " <a href=\"http://www.openclinica.org/entities/entity_details.php?eid=151\"><b>OpenClinica.org portal</b></a> "
            + "(OpenClinica.org user account required).");

    FormProcessor fp = new FormProcessor(request);
    int crfId = fp.getInt(CRF_ID);
    if (crfId == 0) {
      addPageMessage("Please choose a CRF to view.");
      forwardPage(Page.CRF_LIST);
    } else {
      CRFDAO cdao = new CRFDAO(sm.getDataSource());
      CRFVersionDAO vdao = new CRFVersionDAO(sm.getDataSource());
      CRFBean crf = (CRFBean) cdao.findByPK(crfId);
      ArrayList versions = (ArrayList) vdao.findAllByCRF(crfId);
      crf.setVersions(versions);
      request.setAttribute(CRF, crf);
      forwardPage(Page.VIEW_CRF);

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