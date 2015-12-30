/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.techadmin;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Enumeration;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

import com.oreilly.servlet.MultipartRequest;

public class DataImportingServlet extends SecureController {
  protected void processRequest() throws Exception {

    FormProcessor fp = new FormProcessor(request);
    int crfId = fp.getInt("crfId");
    int versionId = fp.getInt("versionId");
    if (!fp.isSubmitted()) {
      request.setAttribute("crfId", new Integer(crfId));
      request.setAttribute("versionId", new Integer(versionId));
      forwardPage(Page.UPLOAD_DATA_FILE);
    } else {
      logger.info("submitted");
      CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
      CRFVersionBean version = (CRFVersionBean) cvdao.findByPK(versionId);
      CRFDAO cdao = new CRFDAO(sm.getDataSource());
      CRFBean crf = (CRFBean) cdao.findByPK(crfId);
      if (uploadFile(crf, version) == null) {
        forwardPage(Page.UPLOAD_DATA_FILE);
      } else {

        addPageMessage("The data file was imported successfully.");
        forwardPage(Page.TECH_ADMIN_SYSTEM);
      }

    }

  }

  protected void mayProceed() throws InsufficientPermissionException {
    if (!ub.isTechAdmin()) {
      throw new InsufficientPermissionException(Page.MENU,
          "You may not perform technical administrative functions", "1");
    }

    return;
  }

  /**
   * Uploads the excel version file
   * 
   * @param version
   * @throws Exception
   */
  public String uploadFile(CRFBean crf, CRFVersionBean version) throws Exception {
    String dir = SQLInitServlet.getField("filePath");

    // DefaultFileRenamePolicy rename = new DefaultFileRenamePolicy();
    // All the uploaded files will be saved in filePath/crf/original/
    File rootDir = new File(dir);
    // test whether the filePath defined is valid
    if (!rootDir.exists()) {
      logger.info("The filePath in datainfo.properties is invalid " + dir);
      addPageMessage("The filePath you defined in datainfo.properties does not seem to be a valid path, please check it.");
      session.setAttribute("version", version);
      return null;
    }

    String theDir = dir + File.separator;
    File theDirFile = new File(theDir);

    if (theDirFile.isDirectory()) {
      logger.info("Found the directory " + theDir);
    } else {
      theDirFile.mkdirs();
      logger.info("Made the directory " + theDir);
    }
    MultipartRequest multi = new MultipartRequest(request, theDir, 50 * 1024 * 1024);
    Enumeration params = multi.getParameterNames();
    Enumeration files = multi.getFileNames();

    String tempFile = null;
    while (files.hasMoreElements()) {
      String name = (String) files.nextElement();
      String filename = multi.getFilesystemName(name);
      String type = multi.getContentType(name);
      File f = multi.getFile(name);
      System.out.println("file name:" + f.getName());
      if (f == null || (f.getName() == null)
          || ((f.getName().indexOf(".xls") < 0) && (f.getName().indexOf(".XLS") < 0))) {
        addPageMessage("The file you uploaded does not seem to be an Excel spreadsheet.");
        return tempFile;

      } else {
        tempFile = f.getName();
        //whether user will create definition? this is just for testing
        StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
        StudyEventDefinitionBean def = (StudyEventDefinitionBean)seddao.findByNameAndStudy("ADOS - Autism Diagnostic Observation Schedule", currentStudy.getId());
        logger.info("found definition:" + def.getId());
        
        DataFileParser dtab = new DataFileParser(new FileInputStream(theDir + tempFile), ub, crf,
            version, sm.getDataSource(),currentStudy, def);
        dtab.parseData();
        ArrayList<String> errors = dtab.getErrors();

        if (!errors.isEmpty()) {
          addPageMessage("There are some errors when parsing the data file.");
          request.setAttribute("errors", errors);
          return null;
        } else {
          logger.info("no parsing errors");
           dtab.insertToDB();
           ArrayList<String> sqlErrors = dtab.getSqlErrors();
           if (!sqlErrors.isEmpty()) {
             addPageMessage("There are some errors when importing data into database.");
             request.setAttribute("errors", sqlErrors);
             return null;
           }
        }

      }

    }
    return tempFile;

  }

  protected String getAdminServlet() {
    return SecureController.ADMIN_SERVLET_CODE;
  }

}
