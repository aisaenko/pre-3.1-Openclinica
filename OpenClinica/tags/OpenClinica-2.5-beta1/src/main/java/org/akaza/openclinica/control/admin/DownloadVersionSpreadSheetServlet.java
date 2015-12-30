/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

import java.io.File;

/**
 * @author jxu
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DownloadVersionSpreadSheetServlet extends SecureController {
    public static String CRF_ID = "crfId";

    public static String CRF_VERSION_NAME = "crfVersionName";

    // public static String CRF_VERSION_TEMPLATE = "CRF_Design_Template.xls";

    // YW 09-05-2007
    public static String CRF_VERSION_TEMPLATE = "CRF_Design_Template_v2.xls";

    /**
     *
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {
        if (ub.isSysAdmin()) {
            return;
        }

        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MANAGE_STUDY_SERVLET, resexception.getString("not_study_director"), "1");

    }

    @Override
    public void processRequest() throws Exception {
        String dir = SQLInitServlet.getField("filePath") + "crf" + File.separator + "new" + File.separator;
        // YW 09-10-2007 << Now CRF_Design_Template_v2.xls is located at
        // $CATALINA_HOME/webapps/OpenClinica-instanceName/properties
        String templateDir = SQLInitServlet.PROPERTY_DIR;
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
        if (!excelFile.exists() || excelFile.length() <= 0) {
            addPageMessage(respage.getString("the_excel_is_not_available_on_server_contact"));
            forwardPage(Page.CRF_LIST_SERVLET);
        } else {
            response.setContentType("application/excel");
            response.setHeader("Content-disposition", "attachment; filename=\"" + excelFileName + "\";");

            response.setHeader("Pragma", "public");

            request.setAttribute("generate", dir + excelFileName);
            response.setHeader("Pragma", "public");
            Page finalTarget = Page.EXPORT_DATA_CUSTOM;
            finalTarget.setFileName("/WEB-INF/jsp/extract/generatedExcelDataset.jsp");
            forwardPage(finalTarget);
        }

    }

}