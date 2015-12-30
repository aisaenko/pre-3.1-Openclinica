/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.io.File;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DownloadVersionSpreadSheetServlet extends SecureController {
  public static String CRF_ID = "crfId";

  public static String CRF_VERSION_NAME = "crfVersionName";

  public static String CRF_VERSION_TEMPLATE = "CRF_Design_Template.xls";

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
    throw new InsufficientPermissionException(Page.MANAGE_STUDY_SERVLET, "not study director", "1");

  }

  public void processRequest() throws Exception {
    String dir = SQLInitServlet.getField("filePath") + "crf" + File.separator + "new" + File.separator;
    String templateDir = SQLInitServlet.getField("filePath") + "crf" + File.separator;
    FormProcessor fp = new FormProcessor(request);

    String crfIdString = fp.getString(CRF_ID);
    String crfVersionName = fp.getString(CRF_VERSION_NAME);

    boolean isTemplate = fp.getBoolean("template");

    String excelFileName = crfIdString + crfVersionName + ".xls";

    if (isTemplate) {
      excelFileName = CRF_VERSION_TEMPLATE;
      dir = templateDir;
    }   

    File excelFile = new File(dir + excelFileName);
    if (!excelFile.exists() || excelFile.length()<=0) {
      addPageMessage("The excel spreadsheet is not available on the server, please contact your admin for help.");
      forwardPage(Page.CRF_LIST_SERVLET);
    } else {
      response.setContentType("application/excel");
      response.setHeader("Content-disposition", "attachment; filename=\"" + excelFileName + "\";");

      response.setHeader("Pragma", "public");

      request.setAttribute("generate", dir + excelFileName);
      response.setHeader("Pragma", "public");
      Page finalTarget = Page.EXPORT_DATA_CUSTOM;
      finalTarget.setFileName("/WEB-INF/jsp/extract/generatedFileDataset.jsp");
      forwardPage(finalTarget);
    }

  }

}