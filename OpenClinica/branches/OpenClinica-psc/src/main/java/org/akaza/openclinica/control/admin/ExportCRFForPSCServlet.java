/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import org.akaza.openclinica.bean.admin.CRFActivityBean;
import org.akaza.openclinica.bean.extract.XMLReportBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

public class ExportCRFForPSCServlet extends SecureController {
    public static final String ACTIVITY_FILE_NAME = "OpenClinica_CRF.xml";

    private static final String ACTIVITY_DIR = SQLInitServlet.getField("filePath") + "psc"
            + File.separator;

    protected void mayProceed() throws InsufficientPermissionException {
        if (!ub.isSysAdmin()) {
            throw new InsufficientPermissionException(Page.MENU, resexception
                    .getString("you_may_not_perform_administrative_functions"), "1");
        }

        return;
    }

    protected void processRequest() throws Exception {
        // checks which module the requests are from, manage or admin
        FormProcessor fp = new FormProcessor(request);
        String module = fp.getString(MODULE);
        request.setAttribute(MODULE, module);
        
        CRFActivityBean cab = new CRFActivityBean(sm.getDataSource());
        cab.createActivityXML();
        ArrayList xmlfile = cab.getXmlOutput();
        XMLReportBean xrb = new XMLReportBean(xmlfile);
        this.createFile(ACTIVITY_FILE_NAME, ACTIVITY_DIR, xrb.toString());

    }

    public void createFile(String name, String dir, String content) {

        try {
            File complete = new File(dir);
            if (!complete.isDirectory()) {
                complete.mkdirs();
            }
            File newFile = new File(complete, name);
            newFile.setLastModified(System.currentTimeMillis());

            BufferedWriter w = new BufferedWriter(new FileWriter(newFile));
            w.write(content);
            w.close();
            logger.info("finished writing the text file...");
            addPageMessage("CRF List was exported as Activities successfully.");
            request.setAttribute("module", "admin");
            forwardPage(Page.CRF_LIST_SERVLET);

        } catch (Exception e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
            request.setAttribute("module", "admin");
            addPageMessage("CRF List was exported with error. Please contact your system admin.");
            forwardPage(Page.CRF_LIST_SERVLET);
        }

    }

    @Override
    protected String getAdminServlet() {
        return SecureController.ADMIN_SERVLET_CODE;
    }

}
