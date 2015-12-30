/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.managestudy.*;
import org.akaza.openclinica.bean.submit.*;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.*;
import org.akaza.openclinica.dao.submit.*;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;
import java.util.*;

/**
 * @author jxu
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ViewCRFVersionServlet extends SecureController {
  /**
   * Checks whether the user has the right permission to proceed function
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
    throw new InsufficientPermissionException(Page.MENU_SERVLET, "not director", "1");


  }

  public void processRequest() throws Exception {

    CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
    ItemDAO idao = new ItemDAO(sm.getDataSource());
    ItemFormMetadataDAO ifmdao = new ItemFormMetadataDAO(sm.getDataSource());
    FormProcessor fp = new FormProcessor(request);
    int crfVersionId = fp.getInt("id");

    if (crfVersionId == 0) {
      addPageMessage("Please choose a crf to view details.");
      forwardPage(Page.CRF_LIST_SERVLET);
    } else {
      CRFVersionBean version = (CRFVersionBean) cvdao.findByPK(crfVersionId);
      SectionDAO sdao = new SectionDAO(sm.getDataSource());
      ArrayList sections = (ArrayList) sdao.findByVersionId(version.getId());
      HashMap versionMap = new HashMap();
      for (int i = 0; i < sections.size(); i++) {
        SectionBean section = (SectionBean) sections.get(i);
        versionMap.put(new Integer(section.getId()), section.getItems());
      }
      ArrayList items = idao.findAllItemsByVersionId(version.getId());
      for (int i = 0; i < items.size(); i++) {
        ItemBean item = (ItemBean) items.get(i);
        ItemFormMetadataBean ifm = ifmdao
            .findByItemIdAndCRFVersionId(item.getId(), version.getId());

        item.setItemMeta(ifm);
        //System.out.println("option******" + ifm.getResponseSet().getOptions().size());
        ArrayList its = (ArrayList) versionMap.get(new Integer(ifm.getSectionId()));
        its.add(item);
      }

      for (int i = 0; i < sections.size(); i++) {
        SectionBean section = (SectionBean) sections.get(i);
        section.setItems((ArrayList) versionMap.get(new Integer(section.getId())));
      }
      request.setAttribute("sections", sections);
      request.setAttribute("version", version);
      forwardPage(Page.VIEW_CRF_VERSION);

    }
  }

}