/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.bean.service.StudyParamsConfig;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * @version CVS: $Id: UpdateSubStudyServlet.java,v 1.7 2005/07/05 21:55:58 jxu
 *          Exp $
 */
public class UpdateSubStudyServlet extends SecureController {
  public static final String INPUT_START_DATE = "startDate";

  public static final String INPUT_END_DATE = "endDate";

  /**
   * 
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
    throw new InsufficientPermissionException(Page.STUDY_LIST, "not study director", "1");

  }

  public void processRequest() throws Exception {

    StudyDAO sdao = new StudyDAO(sm.getDataSource());

    StudyBean study = (StudyBean) session.getAttribute("newStudy");
    logger.info("study from session:" + study.getName() + "\n" + study.getCreatedDate() + "\n");
    String action = request.getParameter("action");

    if (StringUtil.isBlank(action)) {
      request.setAttribute("facRecruitStatusMap", CreateStudyServlet.facRecruitStatusMap);
      request.setAttribute("statuses", Status.toArrayList());
      FormProcessor fp = new FormProcessor(request);
      logger.info("start date:" + study.getDatePlannedEnd());
      if (study.getDatePlannedEnd() != null) {
        fp.addPresetValue(INPUT_END_DATE, sdf.format(study.getDatePlannedEnd()));
      }
      if (study.getDatePlannedStart() != null) {
        fp.addPresetValue(INPUT_START_DATE, sdf.format(study.getDatePlannedStart()));
      }

     
      
      setPresetValues(fp.getPresetValues());
      forwardPage(Page.UPDATE_SUB_STUDY);
    } else {
      if ("confirm".equalsIgnoreCase(action)) {
        confirmStudy();

      } else if ("submit".equalsIgnoreCase(action)) {
        submitStudy();

      }
    }
  }

  /**
   * Validates the first section of study and save it into study bean
   * 
   * @param request
   * @param response
   * @throws Exception
   */
  private void confirmStudy() throws Exception {
    Validator v = new Validator(request);
    FormProcessor fp = new FormProcessor(request);
    v.addValidation("name", Validator.NO_BLANKS);
    v.addValidation("uniqueProId", Validator.NO_BLANKS);
    v.addValidation("description", Validator.NO_BLANKS);
    v.addValidation("prinInvestigator", Validator.NO_BLANKS);
    if (!StringUtil.isBlank(fp.getString(INPUT_START_DATE))) {
      v.addValidation(INPUT_START_DATE, Validator.IS_A_DATE);
    }
    if (!StringUtil.isBlank(fp.getString(INPUT_END_DATE))) {
      v.addValidation(INPUT_END_DATE, Validator.IS_A_DATE);
    }
    if (!StringUtil.isBlank(fp.getString("facConEmail"))) {
      v.addValidation("facConEmail", Validator.IS_A_EMAIL);
    }
    // v.addValidation("statusId", Validator.IS_VALID_TERM);
    v.addValidation("secondProId", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
    v.addValidation("facName", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
    v.addValidation("facCity", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
    v.addValidation("facState", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 20);
    v.addValidation("facZip", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 64);
    v.addValidation("facCountry", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 64);
    v.addValidation("facConName", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
    v.addValidation("facConDegree", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
    v.addValidation("facConPhone", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
    v.addValidation("facConEmail", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);

    errors = v.validate();
    if (fp.getString("name").trim().length() > 100) {
      Validator.addError(errors, "name", "The maximum length of Name is 100 characters.");
    }
    if (fp.getString("uniqueProId").trim().length() > 30) {
      Validator.addError(errors, "uniqueProId",
          "The maximum length of Unique Protocol ID is 30 characters.");
    }
    if (fp.getString("description").trim().length() > 255) {
      Validator.addError(errors, "description",
          "The maximum length of Brief Summary is 255 characters.");
    }
    if (fp.getString("prinInvestigator").trim().length() > 255) {
      Validator.addError(errors, "prinInvestigator",
          "The maximum length of Principal Investigator is 255 characters.");
    }

    session.setAttribute("newStudy", createStudyBean());

    if (errors.isEmpty()) {
      logger.info("no errors");
      forwardPage(Page.CONFIRM_UPDATE_SUB_STUDY);

    } else {
      logger.info("has validation errors");
      try {
        sdf.parse(fp.getString(INPUT_START_DATE));
        fp.addPresetValue(INPUT_START_DATE, sdf.format(fp.getDate(INPUT_START_DATE)));
      } catch (ParseException pe) {
        fp.addPresetValue(INPUT_START_DATE, fp.getString(INPUT_START_DATE));
      }
      try {
        sdf.parse(fp.getString(INPUT_END_DATE));
        fp.addPresetValue(INPUT_END_DATE, sdf.format(fp.getDate(INPUT_END_DATE)));
      } catch (ParseException pe) {
        fp.addPresetValue(INPUT_END_DATE, fp.getString(INPUT_END_DATE));
      }
      setPresetValues(fp.getPresetValues());
      request.setAttribute("formMessages", errors);
      request.setAttribute("facRecruitStatusMap", CreateStudyServlet.facRecruitStatusMap);
      request.setAttribute("statuses", Status.toArrayList());
      forwardPage(Page.UPDATE_SUB_STUDY);
    }

  }

  /**
   * Constructs study bean from request
   * 
   * @param request
   * @return
   */
  private StudyBean createStudyBean() {
    FormProcessor fp = new FormProcessor(request);
    StudyBean study = (StudyBean) session.getAttribute("newStudy");
    study.setName(fp.getString("name"));
    study.setIdentifier(fp.getString("uniqueProId"));
    study.setSecondaryIdentifier(fp.getString("secondProId"));
    study.setSummary(fp.getString("description"));
    study.setPrincipalInvestigator(fp.getString("prinInvestigator"));

    java.util.Date startDate = null;
    java.util.Date endDate = null;
    try {
      sdf.setLenient(false);
      startDate = sdf.parse(fp.getString("startDate"));

    } catch (ParseException fe) {
      startDate = study.getDatePlannedStart();
      logger.info(fe.getMessage());
    }
    study.setDatePlannedStart(startDate);

    try {
      sdf.setLenient(false);
      endDate = sdf.parse(fp.getString("endDate"));

    } catch (ParseException fe) {
      endDate = study.getDatePlannedEnd();
    }
    study.setDatePlannedEnd(endDate);

    study.setFacilityCity(fp.getString("facCity"));
    study.setFacilityContactDegree(fp.getString("facConDrgree"));
    study.setFacilityName(fp.getString("facName"));
    study.setFacilityContactEmail(fp.getString("facConEmail"));
    study.setFacilityContactPhone(fp.getString("facConPhone"));
    study.setFacilityContactName(fp.getString("facConName"));
    study.setFacilityContactDegree(fp.getString("facConDegree"));
    study.setFacilityCountry(fp.getString("facCountry"));
    study.setFacilityRecruitmentStatus(fp.getString("facRecStatus"));
    study.setFacilityState(fp.getString("facState"));
    study.setFacilityZip(fp.getString("facZip"));
    // study.setStatusId(fp.getInt("statusId"));
    study.setStatus(Status.get(fp.getInt("statusId")));
    
    ArrayList parameters = study.getStudyParameters();
    
    for (int i=0; i<parameters.size(); i++) {
      StudyParamsConfig scg= (StudyParamsConfig)parameters.get(i);
      String value = fp.getString(scg.getParameter().getHandle());
      logger.info("get value:" + value);
      scg.getValue().setStudyId(study.getId());
      scg.getValue().setParameter(scg.getParameter().getHandle());
      scg.getValue().setValue(value);
    }
    
    return study;

  }

  /**
   * Inserts the new study into database
   * 
   */
  private void submitStudy() {
    StudyDAO sdao = new StudyDAO(sm.getDataSource());
    StudyBean study = (StudyBean) session.getAttribute("newStudy");
    ArrayList parameters = study.getStudyParameters();
    /*
     * logger.info("study bean to be updated:\n"); logger.info(study.getName()+
     * "\n" + study.getCreatedDate() + "\n" + study.getIdentifier() + "\n" +
     * study.getParentStudyId()+ "\n" + study.getSummary()+ "\n" +
     * study.getPrincipalInvestigator()+ "\n" + study.getDatePlannedStart()+
     * "\n" + study.getDatePlannedEnd()+ "\n" + study.getFacilityName()+ "\n" +
     * study.getFacilityCity()+ "\n" + study.getFacilityState()+ "\n" +
     * study.getFacilityZip()+ "\n" + study.getFacilityCountry()+ "\n" +
     * study.getFacilityRecruitmentStatus()+ "\n" +
     * study.getFacilityContactName()+ "\n" + study.getFacilityContactEmail()+
     * "\n" + study.getFacilityContactPhone()+ "\n" +
     * study.getFacilityContactDegree());
     */

    // study.setCreatedDate(new Date());
    study.setUpdatedDate(new Date());
    study.setUpdater(ub);
    sdao.update(study);
    
    StudyParameterValueDAO spvdao = new StudyParameterValueDAO(sm.getDataSource());
    for (int i=0; i<parameters.size(); i++) {
      StudyParamsConfig config = (StudyParamsConfig)parameters.get(i);
      StudyParameterValueBean spv = config.getValue();  
      
      StudyParameterValueBean spv1= spvdao.findByHandleAndStudy(spv.getStudyId(),spv.getParameter());
      if (spv1.getId() >0) {
        spv = (StudyParameterValueBean)spvdao.update(spv);
      } else {
        spv = (StudyParameterValueBean)spvdao.create(spv);
      }
      //spv = (StudyParameterValueBean)spvdao.update(config.getValue());
      
    }
    session.removeAttribute("newStudy");
    session.removeAttribute("parentName");
    addPageMessage("The site has been updated successfully.");
    String fromListSite = (String) session.getAttribute("fromListSite");
    if (fromListSite != null && fromListSite.equals("yes")
        && currentRole.getRole().getName().equalsIgnoreCase("director")) {
      session.removeAttribute("fromListSite");
      forwardPage(Page.SITE_LIST_SERVLET);
    } else {
      session.removeAttribute("fromListSite");
      forwardPage(Page.STUDY_LIST_SERVLET);
    }

  }

  protected String getAdminServlet() {
    if (ub.isSysAdmin()) {
      return SecureController.ADMIN_SERVLET_CODE;
    } else {
      return "";
    }
  }

}
