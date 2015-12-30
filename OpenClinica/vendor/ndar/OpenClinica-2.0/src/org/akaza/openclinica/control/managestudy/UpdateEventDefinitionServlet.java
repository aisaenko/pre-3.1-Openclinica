/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Date;

import org.akaza.openclinica.bean.core.NullValue;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class UpdateEventDefinitionServlet extends SecureController {

  /**
   *  
   */
  public void mayProceed() throws InsufficientPermissionException {
  	if (ub.isSysAdmin()) {
  		return ;
  	}
  	if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
         || currentRole.getRole().equals(Role.COORDINATOR)) {
       return;
    }
    addPageMessage("You do not have permission to update a Study Event Definition." + "<br>"
        + "Please change your active study or contact the System Administrator.");
    throw new InsufficientPermissionException(Page.LIST_DEFINITION_SERVLET, "not study director", "1");
  }

  public void processRequest() throws Exception {
    String action = request.getParameter("action");
    if (StringUtil.isBlank(action)) {
      forwardPage(Page.UPDATE_EVENT_DEFINITION1);
    } else {
      if ("confirm".equalsIgnoreCase(action)) {
        confirmDefinition();

      } else if ("submit".equalsIgnoreCase(action)) {
        submitDefinition();
      } else {
        addPageMessage("Updating event definition is cancelled.");
        forwardPage(Page.LIST_DEFINITION_SERVLET);
      }
    }

  }

  /**
   * 
   * @throws Exception
   */
  private void confirmDefinition() throws Exception {
    Validator v = new Validator(request);
    FormProcessor fp = new FormProcessor(request);

    StudyEventDefinitionBean sed = (StudyEventDefinitionBean) session.getAttribute("definition");

    v.addValidation("name", Validator.NO_BLANKS);
    v.addValidation("name", Validator.LENGTH_NUMERIC_COMPARISON, 
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 2000);
    v.addValidation("description", Validator.LENGTH_NUMERIC_COMPARISON, 
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 2000);
    v.addValidation("category", Validator.LENGTH_NUMERIC_COMPARISON, 
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 2000);

    errors = v.validate();

    if (!errors.isEmpty()) {
      logger.info("has errors");
      request.setAttribute("formMessages", errors);
      forwardPage(Page.UPDATE_EVENT_DEFINITION1);

    } else {
      logger.info("no errors");

      sed.setName(fp.getString("name"));
      sed.setRepeating(fp.getBoolean("repeating"));
      sed.setCategory(fp.getString("category"));
      sed.setDescription(fp.getString("description"));
      sed.setType(fp.getString("type"));

      session.setAttribute("definition", sed);
      ArrayList edcs = (ArrayList) session.getAttribute("eventDefinitionCRFs");
      CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
      for (int i = 0; i < edcs.size(); i++) {
        EventDefinitionCRFBean edcBean = (EventDefinitionCRFBean) edcs.get(i);
        if (!edcBean.getStatus().equals(Status.DELETED)) {
          //only get inputs from web page if AVAILABLE
          int defaultVersionId = fp.getInt("defaultVersionId" + i);
          edcBean.setDefaultVersionId(defaultVersionId);
          CRFVersionBean defaultVersion = (CRFVersionBean)cvdao.findByPK(edcBean.getDefaultVersionId());
          edcBean.setDefaultVersionName(defaultVersion.getName());  

          String requiredCRF = fp.getString("requiredCRF" + i);
          String doubleEntry = fp.getString("doubleEntry" + i);
          String decisionCondition = fp.getString("decisionCondition" + i);

          if (!StringUtil.isBlank(requiredCRF) && "yes".equalsIgnoreCase(requiredCRF.trim())) {
            edcBean.setRequiredCRF(true);
          } else {
            edcBean.setRequiredCRF(false);
          }
          if (!StringUtil.isBlank(doubleEntry) && "yes".equalsIgnoreCase(doubleEntry.trim())) {
            edcBean.setDoubleEntry(true);
          } else {
            edcBean.setDoubleEntry(false);
          }          
          if (!StringUtil.isBlank(decisionCondition)
              && "yes".equalsIgnoreCase(decisionCondition.trim())) {
            edcBean.setDecisionCondition(true);
          } else {
            edcBean.setDecisionCondition(false);
          }
          String nullString = "";
          //process null values
          ArrayList nulls = NullValue.toArrayList();
          for (int a = 0; a < nulls.size(); a++){
            NullValue n = (NullValue)nulls.get(a);
            String myNull = fp.getString(n.getName().toLowerCase() + i);
            if (!StringUtil.isBlank(myNull) && "yes".equalsIgnoreCase(myNull.trim())) {
              nullString = nullString + n.getName().toUpperCase() + ",";
            } 
            
          }
          
          edcBean.setNullValues(nullString);
        }

      }

      session.setAttribute("eventDefinitionCRFs", edcs);
      forwardPage(Page.UPDATE_EVENT_DEFINITION_CONFIRM);
    }

  }

  /**
   * Updates the definition
   *  
   */
  private void submitDefinition() {
    ArrayList edcs = (ArrayList) session.getAttribute("eventDefinitionCRFs");
    StudyEventDefinitionBean sed = (StudyEventDefinitionBean) session.getAttribute("definition");
    StudyEventDefinitionDAO edao = new StudyEventDefinitionDAO(sm.getDataSource());
    logger.info("Definition bean to be updated:" + sed.getName() + sed.getCategory());

    sed.setUpdater(ub);
    sed.setUpdatedDate(new Date());
    sed.setStatus(Status.AVAILABLE);
    edao.update(sed);

    EventDefinitionCRFDAO cdao = new EventDefinitionCRFDAO(sm.getDataSource());
    for (int i = 0; i < edcs.size(); i++) {
      EventDefinitionCRFBean edc = (EventDefinitionCRFBean) edcs.get(i);
      if (edc.getId() > 0) {//need to do update
        edc.setUpdater(ub);
        edc.setUpdatedDate(new Date());
        logger.info("Status:" + edc.getStatus().getName());
        logger.info("version:" + edc.getDefaultVersionId());
        cdao.update(edc);
      } else { //to insert
        edc.setOwner(ub);
        edc.setCreatedDate(new Date());
        edc.setStatus(Status.AVAILABLE);
        cdao.create(edc);

      }
    }

    session.removeAttribute("definition");
    session.removeAttribute("eventDefinitionCRFs");
    addPageMessage("The event definition has been updated successfully.");
    forwardPage(Page.LIST_DEFINITION_SERVLET);
  }

}