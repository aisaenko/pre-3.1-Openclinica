/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.util.ArrayList;
import java.util.Date;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;



/**
 * Removes a crf
 * 
 * @author jxu
 */
public class RemoveCRFServlet extends SecureController {
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

    CRFDAO cdao = new CRFDAO(sm.getDataSource());
    CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
    FormProcessor fp = new FormProcessor(request);
    int crfId = fp.getInt("id",true);    

    String action = request.getParameter("action");
    if (crfId==0) {
      addPageMessage("Please choose a CRF to remove.");
      forwardPage(Page.CRF_LIST_SERVLET);
    } else {
      CRFBean crf = (CRFBean)cdao.findByPK(crfId);
      ArrayList versions = cvdao.findAllByCRFId(crfId);
      crf.setVersions(versions);
      EventDefinitionCRFDAO  edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
      ArrayList edcs = (ArrayList)edcdao.findAllByCRF(crfId);
      
      SectionDAO secdao = new SectionDAO(sm.getDataSource());
           
      
      EventCRFDAO evdao = new EventCRFDAO(sm.getDataSource());
      ArrayList eventCRFs = evdao.findAllByCRF(crfId);
      if ("confirm".equalsIgnoreCase(action)) {
        request.setAttribute("crfToRemove", crf);
        request.setAttribute("eventCRFs", eventCRFs);        
        forwardPage(Page.REMOVE_CRF);
      } else {
        logger.info("submit to remove the crf");  
        crf.setStatus(Status.DELETED);
        crf.setUpdater(ub);
        crf.setUpdatedDate(new Date());
        cdao.update(crf);
        
        for (int i=0; i<versions.size();i++){
          CRFVersionBean version = (CRFVersionBean)versions.get(i);
          version.setStatus(Status.DELETED);
          version.setUpdater(ub);
          version.setUpdatedDate(new Date());
          cvdao.update(version);
          
          ArrayList sections = secdao.findAllByCRFVersionId(version.getId());
          for (int j=0; j<sections.size(); j++) {
            SectionBean section = (SectionBean)sections.get(j);
            section.setStatus(Status.DELETED);
            section.setUpdater(ub);
            section.setUpdatedDate(new Date());
            secdao.update(section);
          }
        }
        
        for (int i=0; i<edcs.size();i++){
          EventDefinitionCRFBean edc = (EventDefinitionCRFBean)edcs.get(i);
          edc.setStatus(Status.DELETED);
          edc.setUpdater(ub);
          edc.setUpdatedDate(new Date());
          edcdao.update(edc);
        }
        
        ItemDataDAO idao = new ItemDataDAO(sm.getDataSource());
        for (int i=0; i<eventCRFs.size();i++){
          EventCRFBean eventCRF= (EventCRFBean)eventCRFs.get(i);
          eventCRF.setStatus(Status.DELETED);
          eventCRF.setUpdater(ub);
          eventCRF.setUpdatedDate(new Date());
          evdao.update(eventCRF);
          
          ArrayList items = idao.findAllByEventCRFId(eventCRF.getId());
          for (int j=0; j<items.size();j++){
            ItemDataBean item =(ItemDataBean)items.get(j);
            item.setStatus(Status.DELETED);
            item.setUpdater(ub);
            item.setUpdatedDate(new Date());
            idao.update(item);
          }          
        }
        
        addPageMessage("The CRF " + crf.getName() + "  has been removed successfully.");
        forwardPage(Page.CRF_LIST_SERVLET);

      }
    }

  }
  
  protected String getAdminServlet() {
	return SecureController.ADMIN_SERVLET_CODE;
  }

}
