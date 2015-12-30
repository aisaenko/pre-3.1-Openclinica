/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.admin.CRFRow;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * Lists all the CRF and their CRF versions
 * 
 * @author jxu 
 */
public class ListCRFServlet extends SecureController {

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
    throw new InsufficientPermissionException(Page.MANAGE_STUDY_SERVLET,
        "not study director", "1");


  }

  /**
   * Finds all the crfs
   *  
   */
  public void processRequest() throws Exception {
    String dir = SQLInitServlet.getField("filePath")+ "crf" + File.separator + "new" + File.separator;// for crf version spreadsheet

    CRFDAO cdao = new CRFDAO(sm.getDataSource());  
	CRFVersionDAO vdao = new CRFVersionDAO(sm.getDataSource());
    ArrayList crfs = (ArrayList) cdao.findAll();
    for (int i = 0; i < crfs.size();i++) {
      CRFBean eb = (CRFBean)crfs.get(i);  
      //logger.info("crf id:" + eb.getId());
      ArrayList versions = (ArrayList)vdao.findAllByCRF(eb.getId());
      
      //check whether the speadsheet is available on the server
      for (int j=0; j<versions.size();j++) {
        CRFVersionBean cv = (CRFVersionBean) versions.get(j);
        File file = new File(dir + eb.getId() + cv.getName()+ ".xls");
        if (file.exists()) {
          cv.setDownloadable(true);
        }
        
      }
      eb.setVersions(versions);
     
    }
    //request.setAttribute("crfs", crfs);
    FormProcessor fp = new FormProcessor(request);
    EntityBeanTable table = fp.getEntityBeanTable();
    ArrayList allRows = CRFRow.generateRowsFromBeans(crfs);
    
    String[] columns = { "CRF Name", "Date Updated", "Last Updated By", "Versions", "Date Created","Owner", "Status", "Download", "Actions" };
	table.setColumns(new ArrayList(Arrays.asList(columns)));
	table.hideColumnLink(3);
	table.hideColumnLink(7);
	table.hideColumnLink(8);
	table.setQuery("ListCRF", new HashMap());
	table.addLink("Create a new CRF", "CreateCRF");
	table.setRows(allRows);
	table.computeDisplay();

	
	request.setAttribute("table", table);
	
	resetPanel();
	panel.setStudyInfoShown(false);
	panel.setOrderedData(true);
    panel.setSubmitDataModule(false);
    panel.setExtractData(false);
    panel.setCreateDataset(false);
    
	if (crfs.size()>0){
      setToPanel("CRFs", new Integer(crfs.size()).toString());
    }    
   
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
    forwardPage(Page.CRF_LIST);
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