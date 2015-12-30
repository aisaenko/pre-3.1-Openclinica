/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.util.*;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * Creates a new CRF 
 * 
 * @author jxu
 */
public class CreateCRFServlet extends SecureController {
  
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

    addPageMessage("You do not have permission to create a CRF."
        + " Please change your active study or contact the System Administrator.");

    throw new InsufficientPermissionException(Page.CRF_LIST_SERVLET, "not study director", "1");

  }

  public void processRequest() throws Exception {
    CRFDAO cdao = new CRFDAO(sm.getDataSource());
    String action = request.getParameter("action");
    //add the list here so that users can tell about crf creation 
    //process together with workflow, tbh
    
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
    
    
    if (StringUtil.isBlank(action)) {
      session.setAttribute("crf", new CRFBean());
      forwardPage(Page.CREATE_CRF);
    } else {
      if ("confirm".equalsIgnoreCase(action)) {
        FormProcessor fp = new FormProcessor(request);
        Validator v = new Validator(request);

        v.addValidation("name", Validator.NO_BLANKS);
        v.addValidation("description", Validator.NO_BLANKS);
        String name = fp.getString("name").trim();
        String description = fp.getString("description");
        CRFBean crf = new CRFBean();
        crf.setName(name);
        crf.setDescription(description.trim());
        session.setAttribute("crf", crf);
        errors = v.validate();
        if (fp.getString("name").trim().length() > 255) {
          Validator.addError(errors, "name", "The maximum length of Name is 255 characters.");
        }
        if (fp.getString("description").trim().length() > 2048) {
          Validator.addError(errors, "description", "The maximum length of Description is 2048 characters.");
        }
        if (!errors.isEmpty()) {
          logger.info("has validation errors in the first section");
          request.setAttribute("formMessages", errors);
          forwardPage(Page.CREATE_CRF);

        } else {

          CRFBean crf1 = (CRFBean) cdao.findByName(name);
          if (crf1.getId() > 0) {
            Validator.addError(errors, "name",
                "CRF name has been used, please choose a unique name.");
            request.setAttribute("formMessages", errors);
            forwardPage(Page.CREATE_CRF);
          } else {
            forwardPage(Page.CREATE_CRF_CONFIRM);
          }
        }

      } else {//submit this crf to DB
        CRFBean crf = (CRFBean) session.getAttribute("crf");
        logger.info("The crf to be saved:" + crf.getName());
        crf.setOwner(ub);
        crf.setCreatedDate(new Date());
        crf.setStatus(Status.AVAILABLE);
        cdao.create(crf);

        crf = (CRFBean) cdao.findByName(crf.getName());
        CRFVersionBean version = new CRFVersionBean();
        version.setCrfId(crf.getId());
        session.setAttribute("version", version);
        session.setAttribute("crfName", crf.getName());
        session.removeAttribute("crf");
        forwardPage(Page.CREATE_CRF_VERSION);

      }
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