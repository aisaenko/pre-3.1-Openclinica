/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.extract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SelectItemsServlet extends SecureController {
  public static String CURRENT_DEF_ID = "currentDefId";

  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }
    if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
        || currentRole.getRole().equals(Role.COORDINATOR)
        || currentRole.getRole().equals(Role.INVESTIGATOR)) {
      return;
    }

    addPageMessage("You don't have the correct privilege in your current active study. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.MENU,
        "not allowed to access extract data servlet", "1");

  }

  public void processRequest() throws Exception {
    FormProcessor fp = new FormProcessor(request);
    int crfId = fp.getInt("crfId");
    int defId = fp.getInt("defId");
    int eventAttr = fp.getInt("eventAttr");
    int subAttr = fp.getInt("subAttr");
    CRFDAO crfdao = new CRFDAO(sm.getDataSource());
    ItemDAO idao = new ItemDAO(sm.getDataSource());
    ItemFormMetadataDAO imfdao = new ItemFormMetadataDAO(sm.getDataSource());
    StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
    
    HashMap events = (HashMap) session.getAttribute(CreateDatasetServlet.EVENTS_FOR_CREATE_DATASET);
    if (events == null) {
      events = new HashMap();
    }
    request.setAttribute("eventlist", events);
    if (crfId == 0) {// no crf selected
      if (eventAttr == 0 && subAttr == 0) {

        forwardPage(Page.CREATE_DATASET_2);
      } else if (eventAttr > 0) {
        forwardPage(Page.CREATE_DATASET_EVENT_ATTR);
      } else if (subAttr > 0) {
        forwardPage(Page.CREATE_DATASET_SUB_ATTR);
      } else {
        forwardPage(Page.CREATE_DATASET_2);
      }
      return;
    }

    CRFBean crf = (CRFBean) crfdao.findByPK(crfId);
    StudyEventDefinitionBean sed = (StudyEventDefinitionBean)seddao.findByPK(defId);
        
    session.setAttribute("crf", crf);
    session.setAttribute("definition", sed);

    DatasetBean db = (DatasetBean) session.getAttribute("newDataset");
    if (db == null) {
      db = new DatasetBean();
    }   

    session.setAttribute("newDataset", db);
    //save current def id in the seesion to avoid duplicated def id in dataset
    // bean
    // session.setAttribute(CURRENT_DEF_ID, new Integer(defId));
   
      ArrayList items = idao.findAllActiveByCRF(crf);
      for (int i = 0; i < items.size(); i++) {
        ItemBean item = (ItemBean) items.get(i);
        ItemFormMetadataBean meta = imfdao.findByItemIdAndCRFVersionId(item.getId(), item
            .getItemMeta().getCrfVersionId());
        meta.setCrfVersionName(item.getItemMeta().getCrfVersionName());
        //System.out.println("crf versionname" + meta.getCrfVersionName());
        item.getItemMetas().add(meta);
      }
      HashMap itemMap = new HashMap();
      for (int i = 0; i < items.size(); i++) {
        ItemBean item = (ItemBean) items.get(i);
        
        if (!itemMap.containsKey(defId + "_" + item.getId())) { 
          if (db.getItemMap().containsKey(defId + "_" + item.getId())){
            item.setSelected(true);
            //System.out.println("Item got selected already11");
          }
          //itemMap.put(new Integer(item.getId()), item);
          itemMap.put(defId + "_" + item.getId(), item);
        } else {
          //same item,combine the metadata
          ItemBean uniqueItem = (ItemBean) itemMap.get(defId + "_" + item.getId());
          uniqueItem.getItemMetas().add(item.getItemMetas().get(0));
          if (db.getItemMap().containsKey(defId + "_" + uniqueItem.getId())){
            uniqueItem.setSelected(true);
            //System.out.println("Item got selected already22");
          }
        }
        
      }
      ArrayList itemArray = new ArrayList(itemMap.values());
       //now sort them by ordinal/name
	  Collections.sort(itemArray);

      session.setAttribute("allItems", itemArray);
   
    forwardPage(Page.CREATE_DATASET_2);
  }

}