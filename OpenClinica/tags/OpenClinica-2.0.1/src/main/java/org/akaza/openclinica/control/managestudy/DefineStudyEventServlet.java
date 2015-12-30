/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.admin.CRFRow;
import org.akaza.openclinica.bean.core.NullValue;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * Defines a new study event
 */
public class DefineStudyEventServlet extends SecureController {

  /**
   * Checks whether the user has the correct privilege
   */
  public void mayProceed() throws InsufficientPermissionException {

    if (currentStudy.getParentStudyId() > 0) {
      addPageMessage("Study Event Definitions may only be added to top-level Studies. "
          + "Please contact the System Administrator if you have questions.");
      throw new InsufficientPermissionException(Page.STUDY_EVENT_DEFINITION_LIST, "not top study",
          "1");
    }

    if (ub.isSysAdmin()) {
      return;
    }

    if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
        || currentRole.getRole().equals(Role.COORDINATOR)) {
      return;
    }

    addPageMessage("You do not have permission to add a Study Event Definition"
        + " to this study. "
        + "Please change your active study or contact the System Administrator.");
    throw new InsufficientPermissionException(Page.STUDY_EVENT_DEFINITION_LIST,
        "not study director", "1");

  }

  /**
   * Processes the 'define study event' request
   * 
   */
  public void processRequest() throws Exception {
    FormProcessor fpr = new FormProcessor(request);
    // System.out.println("action*******" + fpr.getString("action"));
    // System.out.println("pageNum*******" + fpr.getString("pageNum"));

    String action = request.getParameter("action");
    ArrayList crfsWithVersion = (ArrayList) session.getAttribute("crfsWithVersion");
    if (crfsWithVersion == null) {
      crfsWithVersion = new ArrayList();
      CRFDAO cdao = new CRFDAO(sm.getDataSource());
      CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
      ArrayList crfs = (ArrayList) cdao.findAllByStatus(Status.AVAILABLE);

      for (int i = 0; i < crfs.size(); i++) {
        CRFBean crf = (CRFBean) crfs.get(i);
        ArrayList versions = cvdao.findAllByCRFId(crf.getId());
        if (!versions.isEmpty()) {
          crfsWithVersion.add(crf);
        }

      }
      session.setAttribute("crfsWithVersion", crfsWithVersion);
    }
    if (StringUtil.isBlank(action)) {
      StudyEventDefinitionBean sed = new StudyEventDefinitionBean();
      sed.setStudyId(currentStudy.getId());
      session.setAttribute("definition", sed);

      forwardPage(Page.DEFINE_STUDY_EVENT1);
    } else {
      if ("confirm".equalsIgnoreCase(action)) {
        confirmWholeDefinition();

      } else if ("submit".equalsIgnoreCase(action)) {
        Integer nextAction = Integer.valueOf(request.getParameter("nextAction"));
        if (nextAction != null) {
          if (nextAction.intValue() == 1) {
            session.removeAttribute("definition");
            addPageMessage("The new event definition creation is cancelled.");
            forwardPage(Page.LIST_DEFINITION_SERVLET);
          } else if (nextAction.intValue() == 2) {
            submitDefinition();
            forwardPage(Page.LIST_DEFINITION_SERVLET);
          } else {
            logger.info("action =3");
            submitDefinition();
            StudyEventDefinitionBean sed = new StudyEventDefinitionBean();
            sed.setStudyId(currentStudy.getId());
            session.setAttribute("definition", sed);
            forwardPage(Page.DEFINE_STUDY_EVENT1);
          }
        }

      } else if ("next".equalsIgnoreCase(action)) {
        Integer pageNumber = Integer.valueOf(request.getParameter("pageNum"));
        if (pageNumber != null) {
          if (pageNumber.intValue() == 2) {
            confirmDefinition2();
          } else {
            confirmDefinition1();
          }
        } else {
          if (session.getAttribute("definition") == null) {
            StudyEventDefinitionBean sed = new StudyEventDefinitionBean();
            sed.setStudyId(currentStudy.getId());
            session.setAttribute("definition", sed);
          }
          forwardPage(Page.DEFINE_STUDY_EVENT1);
        }
      }
    }
  }

  /**
   * Validates the first section of definition inputs
   * @throws Exception
   */
  private void confirmDefinition1() throws Exception {
    Validator v = new Validator(request);
    FormProcessor fp = new FormProcessor(request);

    v.addValidation("name", Validator.NO_BLANKS);
    v.addValidation("name", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 2000);
    v.addValidation("description", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 2000);
    v.addValidation("category", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 2000);

    errors = v.validate();

    session.setAttribute("definition", createStudyEventDefinition());

    if (errors.isEmpty()) {
      logger.info("no errors in the first section");
      // System.out.println("action*******" + fp.getString("action"));
      // System.out.println("pageNum*******" + fp.getString("pageNum"));
      EntityBeanTable table = fp.getEntityBeanTable();
      ArrayList crfsWithVersion = (ArrayList) session.getAttribute("crfsWithVersion");
      ArrayList allRows = CRFRow.generateRowsFromBeans(crfsWithVersion);
      String[] columns = { "CRF Name", "Date Created", "Owner", "Date Updated", "Last Updated By",
          "Selected" };
      table.setColumns(new ArrayList(Arrays.asList(columns)));
      table.hideColumnLink(5);
      StudyEventDefinitionBean def1 = (StudyEventDefinitionBean) session.getAttribute("definition");
      HashMap args = new HashMap();
      args.put("action", "next");
      args.put("pageNum", "1");
      args.put("name", def1.getName());
      args.put("repeating", new Boolean(def1.isRepeating()).toString());
      args.put("category", def1.getCategory());
      args.put("description", def1.getDescription());
      args.put("type", def1.getType());
      table.setQuery("DefineStudyEvent", args);
      table.setRows(allRows);
      table.computeDisplay();

      request.setAttribute("table", table);
      // request.setAttribute("crfs", crfs);

      //YW << 
      forwardPage(Page.DEFINE_STUDY_EVENT2);

    } else {
      logger.info("has validation errors in the first section");
      request.setAttribute("formMessages", errors);
      forwardPage(Page.DEFINE_STUDY_EVENT1);
    }
  }

  /**
   * Validates the entire definition 
   * @throws Exception
   */
  private void confirmWholeDefinition() throws Exception {
    Validator v = new Validator(request);
    FormProcessor fp = new FormProcessor(request);
    StudyEventDefinitionBean sed = (StudyEventDefinitionBean) session.getAttribute("definition");
    ArrayList eventDefinitionCRFs = new ArrayList();
    CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
    for (int i = 0; i < sed.getCrfs().size(); i++) {
      EventDefinitionCRFBean edcBean = new EventDefinitionCRFBean();
      int crfId = fp.getInt("crfId" + i);
      int defaultVersionId = fp.getInt("defaultVersionId" + i);
      edcBean.setCrfId(crfId);
      edcBean.setDefaultVersionId(defaultVersionId);
      CRFVersionBean defaultVersion = (CRFVersionBean) cvdao
          .findByPK(edcBean.getDefaultVersionId());
      edcBean.setDefaultVersionName(defaultVersion.getName());

      String crfName = fp.getString("crfName" + i);
      // String crfLabel = fp.getString("crfLabel" + i);
      edcBean.setCrfName(crfName);
      // edcBean.setCrfLabel(crfLabel);

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
      // process null values
      ArrayList nulls = NullValue.toArrayList();
      for (int a = 0; a < nulls.size(); a++) {
        NullValue n = (NullValue) nulls.get(a);
        String myNull = fp.getString(n.getName().toLowerCase() + i);
        if (!StringUtil.isBlank(myNull) && "yes".equalsIgnoreCase(myNull.trim())) {
          nullString = nullString + n.getName().toUpperCase() + ",";
        }

      }

      edcBean.setNullValues(nullString);
      edcBean.setStudyId(ub.getActiveStudyId());
      eventDefinitionCRFs.add(edcBean);
    }

    request.setAttribute("eventDefinitionCRFs", eventDefinitionCRFs);
    session.setAttribute("edCRFs", eventDefinitionCRFs);// not used on page
    forwardPage(Page.DEFINE_STUDY_EVENT_CONFIRM);

  }

  /**
   * Constructs study bean from request-first section
   * 
   * @param request
   * @return
   */
  private StudyEventDefinitionBean createStudyEventDefinition() {
    FormProcessor fp = new FormProcessor(request);
    StudyEventDefinitionBean sed = (StudyEventDefinitionBean) session.getAttribute("definition");
    sed.setName(fp.getString("name"));
    //YW <<
    String temp = fp.getString("repeating");
    if("true".equalsIgnoreCase(temp) || "1".equals(temp)) {
    	sed.setRepeating(true);
    }else if("false".equalsIgnoreCase(temp) || "0".equals(temp)){
    	sed.setRepeating(false);
    }
    //YW >>
    sed.setCategory(fp.getString("category"));
    sed.setDescription(fp.getString("description"));
    sed.setType(fp.getString("type"));
    return sed;

  }

  private void confirmDefinition2() throws Exception {

    FormProcessor fp = new FormProcessor(request);
    CRFVersionDAO vdao = new CRFVersionDAO(sm.getDataSource());
    ArrayList crfArray = new ArrayList();
    ArrayList crfsWithVersion = (ArrayList)session.getAttribute("crfsWithVersion");
    for (int i = 0; i < crfsWithVersion.size(); i++) {
      int id = fp.getInt("id" + i);
      String name = fp.getString("name" + i);
      // String label = fp.getString("label" + i);
      String selected = fp.getString("selected" + i);
      // logger.info("selected:" + selected);
      if (!StringUtil.isBlank(selected) && "yes".equalsIgnoreCase(selected.trim())) {
        logger.info("one crf selected");
        CRFBean cb = new CRFBean();
        cb.setId(id);
        cb.setName(name);

        // only find active verions
        ArrayList versions = (ArrayList) vdao.findAllActiveByCRF(cb.getId());
        cb.setVersions(versions);

        crfArray.add(cb);

      }
    }
    if (crfArray.size() == 0) {// no crf seleted
      // addPageMessage("At least one CRF must be selected.");
      // request.setAttribute("crfs", crfs);
      addPageMessage("No CRF is selected for this Definition this time, you may add CRFs later.");
      StudyEventDefinitionBean sed = (StudyEventDefinitionBean) session.getAttribute("definition");
      sed.setCrfs(new ArrayList());
      session.setAttribute("definition", sed);
      request.setAttribute("eventDefinitionCRFs", new ArrayList());
      session.setAttribute("edCRFs", new ArrayList());// not used on page
      forwardPage(Page.DEFINE_STUDY_EVENT_CONFIRM);
      // forwardPage(Page.DEFINE_STUDY_EVENT2);

    } else {
      StudyEventDefinitionBean sed = (StudyEventDefinitionBean) session.getAttribute("definition");
      sed.setCrfs(crfArray);// crfs selected by user
      session.setAttribute("definition", sed);

      forwardPage(Page.DEFINE_STUDY_EVENT3);
    }
  }

  /**
   * Inserts the new study into database
   * 
   */
  private void submitDefinition() {
    StudyEventDefinitionDAO edao = new StudyEventDefinitionDAO(sm.getDataSource());
    StudyEventDefinitionBean sed = (StudyEventDefinitionBean) session.getAttribute("definition");
    logger.info("Definition bean to be created:" + sed.getName() + sed.getStudyId());

    // fine the last one's ordinal
    ArrayList defs = (ArrayList) edao.findAllByStudy(currentStudy);
    if (defs == null || defs.isEmpty()) {
      sed.setOrdinal(1);
    } else {
      int lastCount = defs.size() - 1;
      StudyEventDefinitionBean last = (StudyEventDefinitionBean) defs.get(lastCount);
      sed.setOrdinal(last.getOrdinal() + 1);
    }
    sed.setOwner(ub);
    sed.setCreatedDate(new Date());
    sed.setStatus(Status.AVAILABLE);
    StudyEventDefinitionBean sed1 = (StudyEventDefinitionBean) edao.create(sed);

    EventDefinitionCRFDAO cdao = new EventDefinitionCRFDAO(sm.getDataSource());
    ArrayList eventDefinitionCRFs = new ArrayList();
    if (session.getAttribute("edCRFs") != null) {
      eventDefinitionCRFs = (ArrayList) session.getAttribute("edCRFs");
    }
    for (int i = 0; i < eventDefinitionCRFs.size(); i++) {
      EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
      edc.setOwner(ub);
      edc.setCreatedDate(new Date());
      edc.setStatus(Status.AVAILABLE);
      edc.setStudyEventDefinitionId(sed1.getId());
      edc.setOrdinal(i + 1);
      cdao.create(edc);
    }

    session.removeAttribute("definition");
    session.removeAttribute("edCRFs");
    session.removeAttribute("crfsWithVersion");

    addPageMessage("The new event definition has been created successfully.");

  }

}