/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.admin.CRFRow;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * Processes request to add new CRFs info study event definition
 * 
 * @author jxu 
 */
public class AddCRFToDefinitionServlet extends SecureController {

  /**
   * Checks whether the user has the correct privilege
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }
    if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
        || currentRole.getRole().equals(Role.COORDINATOR)) {
      return;
    }

    addPageMessage("You do not have permission to update a Study Event Definition. "
        + "Please change your active study or contact the System Administrator.");
    throw new InsufficientPermissionException(Page.MENU_SERVLET, "not study director", "1");

  }

  public void processRequest() throws Exception {

    CRFDAO cdao = new CRFDAO(sm.getDataSource());
    ArrayList crfs = (ArrayList) cdao.findAllByStatus(Status.AVAILABLE);
    ArrayList edcs = (ArrayList)session.getAttribute("eventDefinitionCRFs");
    if (edcs == null) {
      edcs = new ArrayList();
    }
    HashMap crfIds = new HashMap();
    for (int i=0; i<edcs.size();i++){
      EventDefinitionCRFBean edc = (EventDefinitionCRFBean)edcs.get(i);
      Integer crfId = new Integer(edc.getCrfId());
      crfIds.put(crfId,edc);
    }
    for (int i=0; i<crfs.size(); i++){
      CRFBean crf = (CRFBean) crfs.get(i);
      if (crfIds.containsKey(new Integer(crf.getId()))){
        crf.setSelected(true);
      }
    }

    String action = request.getParameter("action");
    if (StringUtil.isBlank(action)) {
      //request.setAttribute("crfs", crfs);
      FormProcessor fp = new FormProcessor(request);
      EntityBeanTable table = fp.getEntityBeanTable();
      ArrayList allRows = CRFRow.generateRowsFromBeans(crfs);
      String[] columns = { "CRF Name", "Date Created", "Owner", "Date Updated", "Last Updated By",
          "Selected" };
      table.setColumns(new ArrayList(Arrays.asList(columns)));
      table.hideColumnLink(5);
      table.setQuery("AddCRFToDefinition", new HashMap());
      table.setRows(allRows);
      table.computeDisplay();

      request.setAttribute("table", table);
      forwardPage(Page.UPDATE_EVENT_DEFINITION2);
    } else {
      addCRF(crfs);
    }
  }

  private void addCRF(ArrayList crfs) throws Exception {
    boolean hasCrf = false;
    ArrayList edcs = (ArrayList) session.getAttribute("eventDefinitionCRFs");
    StudyEventDefinitionBean sed = (StudyEventDefinitionBean) session.getAttribute("definition");
    Validator v = new Validator(request);
    FormProcessor fp = new FormProcessor(request);
    CRFVersionDAO vdao = new CRFVersionDAO(sm.getDataSource());
    String crfNames = "";
    boolean isCRFSelected = false;
    int ordinalForNewCRF = edcs.size();
    for (int i = 0; i < crfs.size(); i++) {

      int id = fp.getInt("id" + i);
      String name = fp.getString("name" + i);
      //String label = fp.getString("label" + i);
      String selected = fp.getString("selected" + i);
      logger.info("selected:" + selected);
      if (!StringUtil.isBlank(selected) && "yes".equalsIgnoreCase(selected.trim())) {
        logger.info("one crf selected");
        isCRFSelected = true;
        EventDefinitionCRFBean edcBean = new EventDefinitionCRFBean();

        edcBean.setCrfId(id);
        edcBean.setCrfName(name);

        edcBean.setStudyId(ub.getActiveStudyId());
        edcBean.setStatus(Status.AVAILABLE);
        edcBean.setStudyEventDefinitionId(sed.getId());
        edcBean.setStudyId(ub.getActiveStudyId());

        ArrayList versions = (ArrayList) vdao.findAllActiveByCRF(edcBean.getCrfId());
        edcBean.setVersions(versions);
        for (int j = 0; j < edcs.size(); j++) {
          EventDefinitionCRFBean edcBean1 = (EventDefinitionCRFBean) edcs.get(j);
          if (edcBean1.getCrfId() == edcBean.getCrfId()) {
            hasCrf = true;
            crfNames = crfNames + name + " ";
            break;
          }
        }
        if (hasCrf == false) {
          edcBean.setOrdinal(ordinalForNewCRF);
          ordinalForNewCRF++;
          edcs.add(edcBean);          
          crfNames = crfNames + name + " ";
        }
      }
    }

    session.setAttribute("eventDefinitionCRFs", edcs);
    if (isCRFSelected) {
      if (hasCrf == false) {
        addPageMessage(crfNames + "has(have) been added.");
      } else {
        addPageMessage(crfNames + "has(have) been added already.");
      }
    } else {
      addPageMessage("No new CRF added.");
    }
    forwardPage(Page.UPDATE_EVENT_DEFINITION1);
  }

}