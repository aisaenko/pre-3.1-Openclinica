/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.extract;

import java.util.ArrayList;
import java.util.HashMap;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * Views selected items for creating dataset, aslo allow user to de-select or
 * select all items in a study
 */
public class ViewSelectedServlet extends SecureController {
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

    DatasetBean db = (DatasetBean) session.getAttribute("newDataset");
    HashMap events = (HashMap) session.getAttribute(CreateDatasetServlet.EVENTS_FOR_CREATE_DATASET);
    if (events == null) {
      events = new HashMap();
    }
    request.setAttribute("eventlist", events);
    
    CRFDAO crfdao = new CRFDAO(sm.getDataSource());
    ItemDAO idao = new ItemDAO(sm.getDataSource());
    ItemFormMetadataDAO imfdao = new ItemFormMetadataDAO(sm.getDataSource());
    ArrayList ids = new ArrayList();
    ArrayList allItemsInStudy = EditSelectedServlet.selectAll(events,crfdao,idao);
    for (int j = 0; j < allItemsInStudy.size(); j++) {
      ItemBean item = (ItemBean) allItemsInStudy.get(j);     
      Integer itemId = new Integer(item.getId());
      if (!ids.contains(itemId)) {
        ids.add(itemId);
      }
    }
    session.setAttribute("numberOfStudyItems", new Integer(ids.size()).toString());
    
    ArrayList items = new ArrayList();
    if (db == null || db.getItemIds().size() == 0) {
      session.setAttribute("allSelectedItems", items);
      forwardPage(Page.CREATE_DATASET_VIEW_SELECTED);
      return;
    }
   

    items = getAllSelected(db, idao, imfdao);

    session.setAttribute("allSelectedItems", items);   
    
    FormProcessor fp = new FormProcessor(request);
    String status = fp.getString("status");
    if (!StringUtil.isBlank(status) && "html".equalsIgnoreCase(status)){
      forwardPage(Page.CREATE_DATASET_VIEW_SELECTED_HTML);
    } else {
    
      forwardPage(Page.CREATE_DATASET_VIEW_SELECTED);
    }

  }

  public static ArrayList getAllSelected(DatasetBean db, ItemDAO idao, ItemFormMetadataDAO imfdao)
      throws Exception {
    ArrayList items = new ArrayList();
    //ArrayList itemIds = db.getItemIds();
    ArrayList itemDefCrfs = db.getItemDefCrf();

    for (int i = 0; i < itemDefCrfs.size(); i++) {      
      ItemBean item = (ItemBean)itemDefCrfs.get(i);
      item.setSelected(true);
      ArrayList metas = imfdao.findAllByItemId(item.getId());
      item.setItemMetas(metas);
      items.add(item);
    }

    return items;

  }

}