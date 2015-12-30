/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.util.ArrayList;

import org.akaza.openclinica.bean.admin.NewCRFBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DeleteCRFVersionServlet extends SecureController {
  public static final String VERSION_ID = "verId";

  public static final String VERSION_TO_DELETE = "version";

  /**
   *  
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }

    addPageMessage("You don't have correct privilege. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.CRF_LIST_SERVLET, "not admin", "1");

  }

  public void processRequest() throws Exception {
    FormProcessor fp = new FormProcessor(request);
    int versionId = fp.getInt(VERSION_ID, true);
    String action = request.getParameter("action");
    if (versionId == 0) {
      addPageMessage("Please choose a CRF version to delete.");
      forwardPage(Page.CRF_LIST_SERVLET);
    } else {
      CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
      CRFVersionBean version = (CRFVersionBean) cvdao.findByPK(versionId);
      EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
      //find definitions using this version
      ArrayList definitions = (ArrayList) edcdao.findByDefaultVersion(version.getId());
      
      //find event crfs using this version
      EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
      ArrayList eventCRFs = ecdao.findAllByCRFVersion(versionId);  
      boolean canDelete = true;
      if (!definitions.isEmpty()) {//used in definition
        canDelete = false;
        request.setAttribute("definitions", definitions);
        addPageMessage("This CRF version " + version.getName() + " has associated study event definitions and cannot be deleted." );
        
      } else if (!eventCRFs.isEmpty()) {
        canDelete = false;
        request.setAttribute("eventsForVersion", eventCRFs);
        addPageMessage("This CRF version " + version.getName() + " has associated study events and cannot be deleted." );
      }
      if ("confirm".equalsIgnoreCase(action)) {       
        request.setAttribute(VERSION_TO_DELETE, version);
        forwardPage(Page.DELETE_CRF_VERSION);
      } else {
        //submit
         if (canDelete) {
           ArrayList items = (ArrayList) cvdao.findNotSharedItemsByVersion(versionId);
           NewCRFBean nib = new NewCRFBean(sm.getDataSource(),version.getCrfId());
           nib.setDeleteQueries(cvdao.generateDeleteQueries(versionId, items));
           nib.deleteFromDB();
           addPageMessage("The CRF version has been deleted successfully.");
         } else {
           addPageMessage("The CRF version cannot be deleted.");
         }
         forwardPage(Page.CRF_LIST_SERVLET);
      }

    }

  }

}