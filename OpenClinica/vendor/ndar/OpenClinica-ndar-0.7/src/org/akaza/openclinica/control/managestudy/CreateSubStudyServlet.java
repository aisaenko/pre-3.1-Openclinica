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
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * Creates a sub study of user's current active study
 */
public class CreateSubStudyServlet extends SecureController {
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
    addPageMessage("You don't have correct privilege in your current active study.\n"
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.SITE_LIST_SERVLET, "not study director", "1");

  }

  public void processRequest() throws Exception {

    String action = request.getParameter("action");

    if (StringUtil.isBlank(action)) {
      if (currentStudy.getParentStudyId() > 0) {
        addPageMessage("You cannot create a site from a study that "
            + "itself is a site. Please change your active study to "
            + "a top-level study or contact your sysadmin.");

        forwardPage(Page.SITE_LIST_SERVLET);
      } else {
        StudyBean newStudy = new StudyBean();
        newStudy.setParentStudyId(currentStudy.getId());
        //get default facility info from property file
        newStudy.setFacilityName(SQLInitServlet.getField(CreateStudyServlet.FAC_NAME,true));
        newStudy.setFacilityCity(SQLInitServlet.getField(CreateStudyServlet.FAC_CITY,true));
        newStudy.setFacilityState(SQLInitServlet.getField(CreateStudyServlet.FAC_STATE,true));
        newStudy.setFacilityCountry(SQLInitServlet.getField(CreateStudyServlet.FAC_COUNTRY,true));
        newStudy.setFacilityContactDegree(SQLInitServlet.getField(CreateStudyServlet.FAC_CONTACT_DEGREE,true));
        newStudy.setFacilityContactEmail(SQLInitServlet.getField(CreateStudyServlet.FAC_CONTACT_EMAIL,true));
        newStudy.setFacilityContactName(SQLInitServlet.getField(CreateStudyServlet.FAC_CONTACT_NAME,true));
        newStudy.setFacilityContactPhone(SQLInitServlet.getField(CreateStudyServlet.FAC_CONTACT_PHONE,true));
        newStudy.setFacilityZip(SQLInitServlet.getField(CreateStudyServlet.FAC_ZIP,true));
        
        
        //StudyConfigService scs = new StudyConfigService(sm.getDataSource());
        //newStudy = scs.setParametersForSite(newStudy);
       
        ArrayList parentConfigs = currentStudy.getStudyParameters();
        logger.info("current study:" + currentStudy.getName());
        logger.info("parentConfigs size:" + parentConfigs.size());
        ArrayList configs = new ArrayList();
        
        for (int i = 0; i<parentConfigs.size(); i++) {
          StudyParamsConfig scg= (StudyParamsConfig)parentConfigs.get(i);
          if (scg != null) {
            //find the one that sub study can change
            if (scg.getValue().getId() >0 && scg.getParameter().isOverridable()) {
              //logger.info("parameter:" + scg.getParameter().getHandle());
              //logger.info("value:" + scg.getValue().getValue());
              configs.add(scg);
            }
          }
          
        }
        newStudy.setStudyParameters(configs);       
        
        
        session.setAttribute("newStudy", newStudy);
        request.setAttribute("facRecruitStatusMap", CreateStudyServlet.facRecruitStatusMap);
        request.setAttribute("statuses", Status.toArrayList());        
     
        forwardPage(Page.CREATE_SUB_STUDY);
      }

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
    //v.addValidation("statusId", Validator.IS_VALID_TERM);
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
      
    if(!StringUtil.isBlank(fp.getString(INPUT_START_DATE)) &&
        !StringUtil.isBlank(fp.getString(INPUT_END_DATE))) {
      if (fp.getDate(INPUT_START_DATE).after(fp.getDate(INPUT_END_DATE))) {
        Validator.addError(errors, INPUT_END_DATE,
        "The site completion date must be equal or after the start date.");
      }
      
    }

    session.setAttribute("newStudy", createStudyBean());

    if (errors.isEmpty()) {
      logger.info("no errors");
      forwardPage(Page.CONFIRM_CREATE_SUB_STUDY);

    } else {
      try {
        sdf.parse(fp.getString(INPUT_START_DATE));
        fp.addPresetValue(INPUT_START_DATE, sdf.format(fp.getDate(INPUT_START_DATE)));
      } catch (ParseException pe){
        fp.addPresetValue(INPUT_START_DATE, fp.getString(INPUT_START_DATE));
      }
      try {
        sdf.parse(fp.getString(INPUT_END_DATE));
        fp.addPresetValue(INPUT_END_DATE, sdf.format(fp.getDate(INPUT_END_DATE)));
      } catch (ParseException pe){
        fp.addPresetValue(INPUT_END_DATE, fp.getString(INPUT_END_DATE));
      }
      setPresetValues(fp.getPresetValues());
      logger.info("has validation errors");
      request.setAttribute("formMessages", errors);
      //request.setAttribute("facRecruitStatusMap",
      // CreateStudyServlet.facRecruitStatusMap);
      request.setAttribute("statuses", Status.toArrayList());
      forwardPage(Page.CREATE_SUB_STUDY);
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
    //study.setFacilityRecruitmentStatus(fp.getString("facRecStatus"));
    study.setFacilityState(fp.getString("facState"));
    study.setFacilityZip(fp.getString("facZip"));
    study.setStatus(Status.get(fp.getInt("statusId")));
    
    
    ArrayList parameters = study.getStudyParameters();
    
    for (int i=0; i<parameters.size(); i++) {
      StudyParamsConfig scg= (StudyParamsConfig)parameters.get(i);
      String value = fp.getString(scg.getParameter().getHandle());
      logger.info("get value:" + value);
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
    logger.info("study bean to be created:\n");
    logger.info(study.getName() + "\n" + study.getIdentifier() + "\n" + study.getParentStudyId()
        + "\n" + study.getSummary() + "\n" + study.getPrincipalInvestigator() + "\n"
        + study.getDatePlannedStart() + "\n" + study.getDatePlannedEnd() + "\n"
        + study.getFacilityName() + "\n" + study.getFacilityCity() + "\n"
        + study.getFacilityState() + "\n" + study.getFacilityZip() + "\n"
        + study.getFacilityCountry() + "\n" + study.getFacilityRecruitmentStatus() + "\n"
        + study.getFacilityContactName() + "\n" + study.getFacilityContactEmail() + "\n"
        + study.getFacilityContactPhone() + "\n" + study.getFacilityContactDegree());

    study.setOwner(ub);
    study.setCreatedDate(new Date());
    StudyBean parent = (StudyBean) sdao.findByPK(study.getParentStudyId());
    study.setType(parent.getType());
    if (study.getStatus().getId()==Status.INVALID.getId()) {
        study.setStatus(Status.AVAILABLE);
    }
   
    study.setGenetic(parent.isGenetic());
    study = (StudyBean)sdao.create(study);
    
    StudyParameterValueDAO spvdao = new StudyParameterValueDAO(sm.getDataSource());
    for (int i=0; i<parameters.size(); i++) {
      StudyParamsConfig config = (StudyParamsConfig)parameters.get(i);
      StudyParameterValueBean spv = config.getValue();
      spv.setStudyId(study.getId());      
      spv= (StudyParameterValueBean)spvdao.create(config.getValue());
      
    }

    //switch user to the newly created site
    session.setAttribute("study", session.getAttribute("newStudy"));
    currentStudy = (StudyBean) session.getAttribute("study");

    session.removeAttribute("newStudy");
    addPageMessage("The new site has been created successfully and is now your current Active Study.");
    forwardPage(Page.SITE_LIST_SERVLET);

  }

  protected String getAdminServlet() {
    if (ub.isSysAdmin()) {
      return SecureController.ADMIN_SERVLET_CODE;
    } else {
      return "";
    }
  }

}