/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.extract;

import java.util.ArrayList;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 *
 * View all related metadata for an item
 */
public class ViewItemDetailServlet extends SecureController {
	  public static String ITEM_ID ="itemId";
	  public static String ITEM_BEAN ="item";
	  public static String VERSION_ITEMS ="versionItems";
	  public void mayProceed() throws InsufficientPermissionException {
	  
	  }
	  
	  public void processRequest() throws Exception {
	    FormProcessor fp = new FormProcessor(request);
	    int itemId = fp.getInt(ITEM_ID);
	    ItemDAO idao = new ItemDAO(sm.getDataSource());
	    ItemFormMetadataDAO ifmdao = new ItemFormMetadataDAO(sm.getDataSource());
	    CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
	    CRFDAO cdao = new CRFDAO(sm.getDataSource());
	    
	    if (itemId==0){
	      addPageMessage("Please choose an item first.");
	      forwardPage(Page.ITEM_DETAIL);
	      return;
	    }
	    ItemBean item = (ItemBean)idao.findByPK(itemId);
	    ArrayList versions = idao.findAllVersionsByItemId(item.getId());
	    ArrayList versionItems = new ArrayList();
	    //finds each item metadata for each version
	    for (int i=0; i<versions.size();i++){      
	      Integer versionId = (Integer)versions.get(i);
	      CRFVersionBean version = (CRFVersionBean)cvdao.findByPK(versionId.intValue());
	      if (versionId!= null && versionId.intValue()>0){
	        ItemFormMetadataBean imfBean = 
	          ifmdao.findByItemIdAndCRFVersionId(item.getId(),versionId.intValue());
	        imfBean.setCrfVersionName(version.getName());   
	        CRFBean crf = (CRFBean)cdao.findByPK(version.getCrfId());
	        imfBean.setCrfName(crf.getName());
	        versionItems.add(imfBean);
	      }
	      
	    }
	    request.setAttribute(VERSION_ITEMS,versionItems);
	    request.setAttribute(ITEM_BEAN,item);
	    forwardPage(Page.ITEM_DETAIL);
	        
	    
	  }
}
