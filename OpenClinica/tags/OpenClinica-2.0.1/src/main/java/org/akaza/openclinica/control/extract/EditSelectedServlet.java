/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.extract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
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
public class EditSelectedServlet extends SecureController {
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
    boolean selectAll = fp.getBoolean("all");
    HashMap events = (HashMap) session.getAttribute(CreateDatasetServlet.EVENTS_FOR_CREATE_DATASET);
    if (events == null) {
      events = new HashMap();
    }
    request.setAttribute("eventlist", events);

    ItemDAO idao = new ItemDAO(sm.getDataSource());
    CRFDAO crfdao = new CRFDAO(sm.getDataSource());
    ItemFormMetadataDAO imfdao = new ItemFormMetadataDAO(sm.getDataSource());   
    
    DatasetBean db = (DatasetBean) session.getAttribute("newDataset");
    if (db == null) {
      db = new DatasetBean();
      session.setAttribute("newDataset", db);
    }
    
    if (selectAll) {
      System.out.println("select all..........");      
      ArrayList allItems = selectAll(events, crfdao, idao);
      Iterator it = events.keySet().iterator();
      while (it.hasNext()){
        StudyEventDefinitionBean sed = (StudyEventDefinitionBean)it.next();
        if (!db.getEventIds().contains(new Integer(sed.getId()))){
          db.getEventIds().add(new Integer(sed.getId()));
        }        
      }
      
      for (int j = 0; j < allItems.size(); j++) {
        ItemBean item = (ItemBean) allItems.get(j);
        ArrayList ids = db.getItemIds();
        ArrayList itemDefCrfs = db.getItemDefCrf();
        Integer itemId = new Integer(item.getId());
        if (!ids.contains(itemId)) {
          ids.add(itemId);  
          itemDefCrfs.add(item);
        }
      }
      addPageMessage("You choose to include all items in current study for the dataset, " +
          db.getItemIds().size() + " items totally.");
    }//end of if selectAll

    session.setAttribute("newDataset", db);
    
    ArrayList allSelectItems = ViewSelectedServlet.getAllSelected(db,idao,imfdao);
    session.setAttribute("allSelectedItems", allSelectItems);
    forwardPage(Page.CREATE_DATASET_VIEW_SELECTED);

  }

  /**
   * Finds all the items in a study giving all events in the study
   * 
   * @param events
   * @return
   */
  public static ArrayList selectAll(HashMap events, CRFDAO crfdao, ItemDAO idao) {    

    ArrayList allItems = new ArrayList();

    Iterator it = events.keySet().iterator();
    while (it.hasNext()) {
      StudyEventDefinitionBean sed = (StudyEventDefinitionBean) it.next();
      ArrayList crfs = (ArrayList) crfdao.findAllActiveByDefinition(sed);
      for (int i = 0; i < crfs.size(); i++) {
        CRFBean crf = (CRFBean) crfs.get(i);
        ArrayList items = idao.findAllActiveByCRF(crf);
        for (int j=0; j<items.size();j++) {
          ItemBean item = (ItemBean)items.get(j);
          item.setCrfName(crf.getName());
          item.setDefName(sed.getName());
        }
        allItems.addAll(items);
      }
    }
    return allItems;

  }

}