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
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.managestudy.InterventionBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * Processes request to create a new study
 * 
 * @author jxu
 * @version CVS: $Id: CreateStudyServlet.java,v 1.33 2006/11/01 23:38:19 jxu Exp $
 *  
 */
public class CreateStudyServlet extends SecureController {
  public static final String INPUT_START_DATE = "startDate";

  public static final String INPUT_END_DATE = "endDate";

  public static final String INPUT_VER_DATE = "protocolDateVerification";

  public static final String FAC_NAME = "FacName";

  public static final String FAC_CITY = "FacCity";

  public static final String FAC_STATE = "FacState";

  public static final String FAC_ZIP = "FacZIP";

  public static final String FAC_COUNTRY = "FacCountry";

  public static final String FAC_CONTACT_NAME = "FacContactName";

  public static final String FAC_CONTACT_DEGREE = "FacContactDegree";

  public static final String FAC_CONTACT_PHONE = "FacContactPhone";

  public static final String FAC_CONTACT_EMAIL = "FacContactEmail";

  static HashMap facRecruitStatusMap = new LinkedHashMap();

  static HashMap studyPhaseMap = new LinkedHashMap();

  static HashMap interPurposeMap = new LinkedHashMap();

  static HashMap allocationMap = new LinkedHashMap();

  static HashMap maskingMap = new LinkedHashMap();

  static HashMap controlMap = new LinkedHashMap();

  static HashMap assignmentMap = new LinkedHashMap();

  static HashMap endpointMap = new LinkedHashMap();

  static HashMap interTypeMap = new LinkedHashMap();

  static HashMap obserPurposeMap = new LinkedHashMap();

  static HashMap selectionMap = new LinkedHashMap();

  static HashMap timingMap = new LinkedHashMap();

  static {
    facRecruitStatusMap.put("Not yet recruiting", "Not yet recruiting");
    facRecruitStatusMap.put("Recruiting", "Recruiting");
    facRecruitStatusMap.put("No longer Recruiting", "No longer Recruiting");
    facRecruitStatusMap.put("Completed", "Completed");
    facRecruitStatusMap.put("Suspended", "Suspended");
    facRecruitStatusMap.put("Terminated", "Terminated");

    studyPhaseMap.put("N/A", "N/A");
    studyPhaseMap.put("Phase I", "Phase I");
    studyPhaseMap.put("Phase I/Phase II", "Phase I/Phase II");
    studyPhaseMap.put("Phase II", "Phase II");
    studyPhaseMap.put("Phase II/Phase III", "Phase II/Phase III");
    studyPhaseMap.put("Phase III", "Phase III");
    studyPhaseMap.put("Phase III/Phase IV", "Phase III/Phase IV");
    studyPhaseMap.put("Phase IV", "Phase IV");

    interPurposeMap.put("Treatment", "Treatment");
    interPurposeMap.put("Prevention", "Prevention");
    interPurposeMap.put("Diagnosis", "Diagnosis");
    interPurposeMap.put("Educational/Counseling/Training", "Educational/Counseling/Training");

    allocationMap.put("Randomized Clinical Trial", "Randomized Clinical Trial");
    allocationMap.put("Nonrandomized Trial", "Nonrandomized Trial");

    maskingMap.put("Open", "Open");
    maskingMap.put("Single Blind", "Single Blind");
    maskingMap.put("Double Blind", "Double Blind");

    controlMap.put("Placebo", "Placebo");
    controlMap.put("Active", "Active");
    controlMap.put("Uncontrolled", "Uncontrolled");
    controlMap.put("Historical", "Historical");
    controlMap.put("Dose Comparison", "Dose Comparison");

    assignmentMap.put("Single Group", "Single Group");
    assignmentMap.put("Parallel", "Parallel");
    assignmentMap.put("Cross-over", "Cross-over");
    assignmentMap.put("Factorial", "Factorial");
    assignmentMap.put("Expanded Access", "Expanded Access");

    endpointMap.put("Safety", "Safety");
    endpointMap.put("Efficacy", "Efficacy");
    endpointMap.put("Safety/Efficacy", "Safety/Efficacy");
    endpointMap.put("Bio-equivalence", "Bio-equivalence");
    endpointMap.put("Bio-availability", "Bio-availability");
    endpointMap.put("Pharmacokinetics", "Pharmacokinetics");
    endpointMap.put("Pharmacodynamics", "Pharmacodynamics");
    endpointMap.put("Pharmacokinetics/Pharmacodynamics", "Pharmacokinetics/Pharmacodynamics");

    interTypeMap.put("Drug", "Drug");
    interTypeMap.put("Gene Transfer", "Gene Transfer");
    interTypeMap.put("Vaccine", "Vaccine");
    interTypeMap.put("Behavior", "Behavior");
    interTypeMap.put("Device", "Device");
    interTypeMap.put("Procedure", "Procedure");
    interTypeMap.put("Other", "Other");

    obserPurposeMap.put("Natural History", "Natural History");
    obserPurposeMap.put("Screening", "Screening");
    obserPurposeMap.put("Psychosocial", "Psychosocial");

    selectionMap.put("Convenience Sample", "Convenience Sample");
    selectionMap.put("Defined Population", "Defined Population");
    selectionMap.put("Random Sample", "Random Sample");
    selectionMap.put("Case Control", "Case Control");

    timingMap.put("Retrospective", "Retrospective");
    timingMap.put("Prospective", "Prospective");
    timingMap.put("Both", "Both");

  }

  /**
   *  
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }

    addPageMessage("You don't have correct privilege in your current active study. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.STUDY_LIST_SERVLET, "not admin", "1");

  }

  /**
   * Processes user request
   */
  public void processRequest() throws Exception {

    String action = request.getParameter("action");
    resetPanel();
    panel.setStudyInfoShown(false);    
    panel.setOrderedData(true);
    panel.setExtractData(false);
    panel.setSubmitDataModule(false);
    panel.setCreateDataset(false);       
    panel.setIconInfoShown(true);
    panel.setManageSubject(false);
    
    if (StringUtil.isBlank(action)) {
      session.setAttribute("newStudy", new StudyBean());

      //request.setAttribute("facRecruitStatusMap", facRecruitStatusMap);
      //request.setAttribute("statuses", Status.toArrayList());
      forwardPage(Page.CREATE_STUDY1);
    } else {
      if ("confirm".equalsIgnoreCase(action)) {
        confirmWholeStudy();

      } else if ("submit".equalsIgnoreCase(action)) {
	    if (!submitStudy()) {
	        addPageMessage("There was a database error - the study was not created successfully.");
	        forwardPage(Page.CREATE_STUDY1);
	        return;
	    }

        session.removeAttribute("interventions");
        session.removeAttribute("isInterventionalFlag");
        session.removeAttribute("interventionArray");

        //swith user to the newly created study

        session.setAttribute("study", session.getAttribute("newStudy"));
        session.removeAttribute("newStudy");
        currentStudy = (StudyBean) session.getAttribute("study");

        UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());

        StudyUserRoleBean sub = new StudyUserRoleBean();
        sub.setRole(Role.COORDINATOR);
        sub.setStudyId(currentStudy.getId());
        sub.setStatus(Status.AVAILABLE);
        sub.setOwner(ub);
        udao.createStudyUserRole(ub, sub);
        currentRole = sub;
        session.setAttribute("userRole", sub);

        addPageMessage("The new study has been created successfully and is now your current Active Study.");
        forwardPage(Page.STUDY_LIST_SERVLET);

      } else if ("next".equalsIgnoreCase(action)) {
        Integer pageNumber = Integer.valueOf(request.getParameter("pageNum"));
        if (pageNumber != null) {
          if (pageNumber.intValue() == 6) {
            confirmStudy6();
          }else if (pageNumber.intValue() == 5) {
            confirmStudy5();
          } else if (pageNumber.intValue() == 4) {
            confirmStudy4();
          } else if (pageNumber.intValue() == 3) {
            confirmStudy3();
          } else if (pageNumber.intValue() == 2) {
            confirmStudy2();
          } else {
            logger.info("confirm study 1" + pageNumber.intValue());
            confirmStudy1();
          }
        } else {
          if (session.getAttribute("newStudy") == null) {
            session.setAttribute("newStudy", new StudyBean());
          }

          forwardPage(Page.CREATE_STUDY1);
        }
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
  private void confirmStudy1() throws Exception {
    Validator v = new Validator(request);
    FormProcessor fp = new FormProcessor(request);

    v.addValidation("name", Validator.NO_BLANKS);
    v.addValidation("name", Validator.LENGTH_NUMERIC_COMPARISON,
            NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 60);
    v.addValidation("uniqueProId", Validator.NO_BLANKS);
    v.addValidation("description", Validator.NO_BLANKS);
    v.addValidation("prinInvestigator", Validator.NO_BLANKS);
    v.addValidation("sponsor", Validator.NO_BLANKS);

    v.addValidation("secondProId", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
    v.addValidation("collaborators", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 1000);
    v.addValidation("protocolDescription", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 1000);

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
    if (fp.getString("sponsor").trim().length() > 255) {
      Validator.addError(errors, "sponsor", "The maximum length of Sponsor is 255 characters.");
    }
    if (fp.getString("officialTitle").trim().length() > 255) {
      Validator.addError(errors, "officialTitle",
          "The maximum length of Official Title is 255 characters.");
    }

    session.setAttribute("newStudy", createStudyBean());

    if (errors.isEmpty()) {
      logger.info("no errors in the first section");
      request.setAttribute("studyPhaseMap", studyPhaseMap);
      request.setAttribute("statuses", Status.toArrayList());

      forwardPage(Page.CREATE_STUDY2);

    } else {
      logger.info("has validation errors in the first section");
      request.setAttribute("formMessages", errors);
      //request.setAttribute("facRecruitStatusMap", facRecruitStatusMap);

      forwardPage(Page.CREATE_STUDY1);
    }

  }

  /**
   * Validates the second section of study info inputs
   * @throws Exception
   */
  private void confirmStudy2() throws Exception {
    Validator v = new Validator(request);
    FormProcessor fp = new FormProcessor(request);

    v.addValidation(INPUT_START_DATE, Validator.IS_A_DATE);
    if (!StringUtil.isBlank(fp.getString(INPUT_END_DATE))) {
      v.addValidation(INPUT_END_DATE, Validator.IS_A_DATE);
    }

    v.addValidation("protocolType", Validator.NO_BLANKS);
    v.addValidation(INPUT_VER_DATE, Validator.IS_A_DATE);

    errors = v.validate();
    
    if(!StringUtil.isBlank(fp.getString(INPUT_START_DATE)) &&
        !StringUtil.isBlank(fp.getString(INPUT_END_DATE))) {
      if (fp.getDate(INPUT_START_DATE).after(fp.getDate(INPUT_END_DATE))) {
        Validator.addError(errors, INPUT_END_DATE,
        "The study completion date must be equal or after the start date.");
      }
      
    }
    
    boolean isInterventional = updateStudy2();
    session.setAttribute("isInterventionalFlag", new Boolean(isInterventional));

    if (errors.isEmpty()) {
      logger.info("no errors");
      setMaps(isInterventional);
      if (isInterventional) {
        forwardPage(Page.CREATE_STUDY3);
      } else {
        forwardPage(Page.CREATE_STUDY4);
      }

    } else {
      logger.info("has validation errors");
      try {
        sdf.parse(fp.getString(INPUT_START_DATE));
        fp.addPresetValue(INPUT_START_DATE, sdf.format(fp.getDate(INPUT_START_DATE)));
      } catch (ParseException pe) {
        fp.addPresetValue(INPUT_START_DATE, fp.getString(INPUT_START_DATE));
      }
      try {
        sdf.parse(fp.getString(INPUT_VER_DATE));
        fp.addPresetValue(INPUT_VER_DATE, sdf.format(fp.getDate(INPUT_VER_DATE)));
      } catch (ParseException pe) {
        fp.addPresetValue(INPUT_VER_DATE, fp.getString(INPUT_VER_DATE));
      }
      try {
        sdf.parse(fp.getString(INPUT_END_DATE));
        fp.addPresetValue(INPUT_END_DATE, sdf.format(fp.getDate(INPUT_END_DATE)));
      } catch (ParseException pe) {
        fp.addPresetValue(INPUT_END_DATE, fp.getString(INPUT_END_DATE));
      }
      setPresetValues(fp.getPresetValues());
      request.setAttribute("formMessages", errors);
      request.setAttribute("studyPhaseMap", studyPhaseMap);
      request.setAttribute("statuses", Status.toArrayList());
      //request.setAttribute("studyTypes", StudyType.toArrayList());
      forwardPage(Page.CREATE_STUDY2);
    }

  }

  /**
   * Confirms the third section of study info inputs
   * 
   * @throws Exception
   */
  private void confirmStudy3() throws Exception {
    Validator v = new Validator(request);
    FormProcessor fp = new FormProcessor(request);

    v.addValidation("purpose", Validator.NO_BLANKS);
    for (int i = 0; i < 10; i++) {
      String type = fp.getString("interType" + i);
      String name = fp.getString("interName" + i);
      if (!StringUtil.isBlank(type) && StringUtil.isBlank(name)) {
        v.addValidation("interName", Validator.NO_BLANKS);
        request.setAttribute("interventionError", "Name cannot be blank if you input Type.");
        break;
      }
      if (!StringUtil.isBlank(name) && StringUtil.isBlank(type)) {
        v.addValidation("interType", Validator.NO_BLANKS);
        request.setAttribute("interventionError", "Type cannot be blank if you input Name.");
        break;
      }
    }

    errors = v.validate();

    boolean isInterventional = true;
    if (session.getAttribute("isInterventionalFlag") != null) {
      isInterventional = ((Boolean) session.getAttribute("isInterventionalFlag")).booleanValue();
    }
    updateStudy3(isInterventional);

    if (errors.isEmpty()) {
      logger.info("no errors");
      if (session.getAttribute("interventionArray") == null) {
        request.setAttribute("interventions", new ArrayList());
      } else {
        request.setAttribute("interventions", session.getAttribute("interventionArray"));
      }
      forwardPage(Page.CREATE_STUDY5);

    } else {
      logger.info("has validation errors");

      request.setAttribute("formMessages", errors);
      setMaps(isInterventional);
      if (isInterventional) {
        forwardPage(Page.CREATE_STUDY3);
      } else {
        forwardPage(Page.CREATE_STUDY4);
      }
    }

  }

  /**
   * Validates the forth section of study and save it into study bean
   * 
   * @param request
   * @param response
   * @throws Exception
   */
  private void confirmStudy4() throws Exception {
    Validator v = new Validator(request);
    FormProcessor fp = new FormProcessor(request);

    //v.addValidation("expectedTotalEnrollment", Validator.IS_A_NUMBER);
    v.addValidation("conditions", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 500);
    v.addValidation("keywords", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
    v.addValidation("eligibility", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 500);

    logger.info("expectedTotalEnrollment:" + fp.getInt("expectedTotalEnrollment"));
    errors = v.validate();
    
    if (!StringUtil.isBlank(fp.getString("ageMax"))){
      if (fp.getInt("ageMax") <= 0) {
        Validator.addError(errors, "ageMax",
          "Maximum age must be a positive number.");
    }
    }
    
    if (!StringUtil.isBlank(fp.getString("ageMin"))){
      if (fp.getInt("ageMin") <= 0) {
        Validator.addError(errors, "ageMin",
          "Minimum age must be a positive number.");
      }
    }
    
    if (!StringUtil.isBlank(fp.getString("ageMax")) &&
        !StringUtil.isBlank(fp.getString("ageMin"))){
      if (fp.getInt("ageMin")> fp.getInt("ageMax")){
        Validator.addError(errors, "ageMax",
        "Maximum age cannot be less than Minimum age.");
      }
    }
   
    if (fp.getInt("expectedTotalEnrollment") <= 0) {
      Validator.addError(errors, "expectedTotalEnrollment",
          "Expected Total Enrollment must be a positive number.");
    }
   
    StudyBean newStudy = (StudyBean) session.getAttribute("newStudy");
    newStudy.setConditions(fp.getString("conditions"));
    newStudy.setKeywords(fp.getString("keywords"));
    newStudy.setEligibility(fp.getString("eligibility"));
    newStudy.setGender((fp.getString("gender")));

    newStudy.setAgeMax(fp.getString("ageMax"));
    newStudy.setAgeMin(fp.getString("ageMin"));
    newStudy.setHealthyVolunteerAccepted(fp.getBoolean("healthyVolunteerAccepted"));
    newStudy.setExpectedTotalEnrollment(fp.getInt("expectedTotalEnrollment"));
    session.setAttribute("newStudy", newStudy);
    request.setAttribute("facRecruitStatusMap", facRecruitStatusMap);
    if (errors.isEmpty()) {
      //get default facility info from property file
      newStudy.setFacilityName(SQLInitServlet.getField(FAC_NAME, true));
      newStudy.setFacilityCity(SQLInitServlet.getField(FAC_CITY, true));
      newStudy.setFacilityState(SQLInitServlet.getField(FAC_STATE, true));
      newStudy.setFacilityCountry(SQLInitServlet.getField(FAC_COUNTRY, true));
      newStudy.setFacilityContactDegree(SQLInitServlet.getField(FAC_CONTACT_DEGREE, true));
      newStudy.setFacilityContactEmail(SQLInitServlet.getField(FAC_CONTACT_EMAIL, true));
      newStudy.setFacilityContactName(SQLInitServlet.getField(FAC_CONTACT_NAME, true));
      newStudy.setFacilityContactPhone(SQLInitServlet.getField(FAC_CONTACT_PHONE, true));
      newStudy.setFacilityZip(SQLInitServlet.getField(FAC_ZIP, true));

      session.setAttribute("newStudy", newStudy);
      forwardPage(Page.CREATE_STUDY6);

    } else {
      request.setAttribute("formMessages", errors);
      forwardPage(Page.CREATE_STUDY5);
    }
  }

  /**
   * Validates the forth section of study and save it into study bean
   * 
   * @param request
   * @param response
   * @throws Exception
   */
  private void confirmStudy5() throws Exception {
    FormProcessor fp = new FormProcessor(request);
    Validator v = new Validator(request);
    if (!StringUtil.isBlank(fp.getString("facConEmail"))) {
      v.addValidation("facConEmail", Validator.IS_A_EMAIL);
    }
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

    StudyBean newStudy = (StudyBean) session.getAttribute("newStudy");

    newStudy.setFacilityCity(fp.getString("facCity"));
    newStudy.setFacilityContactDegree(fp.getString("facConDrgree"));
    newStudy.setFacilityName(fp.getString("facName"));
    newStudy.setFacilityContactEmail(fp.getString("facConEmail"));
    newStudy.setFacilityContactPhone(fp.getString("facConPhone"));
    newStudy.setFacilityContactName(fp.getString("facConName"));
    newStudy.setFacilityCountry(fp.getString("facCountry"));
    newStudy.setFacilityContactDegree(fp.getString("facConDegree"));
    //newStudy.setFacilityRecruitmentStatus(fp.getString("facRecStatus"));
    newStudy.setFacilityState(fp.getString("facState"));
    newStudy.setFacilityZip(fp.getString("facZip"));

    session.setAttribute("newStudy", newStudy);
    if (errors.isEmpty()) {
      forwardPage(Page.CREATE_STUDY7);
    } else {
      request.setAttribute("formMessages", errors);
      request.setAttribute("facRecruitStatusMap", facRecruitStatusMap);
      forwardPage(Page.CREATE_STUDY6);
    }

  }

  /**
   * Lets user confirm all the study info entries input
   * 
   * @throws Exception
   */
  private void confirmStudy6() throws Exception {

    FormProcessor fp = new FormProcessor(request);
    Validator v = new Validator(request);
    v.addValidation("medlineIdentifier", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
    v.addValidation("url", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
    v.addValidation("urlDescription", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);

    errors = v.validate();

    StudyBean newStudy = (StudyBean) session.getAttribute("newStudy");
    newStudy.setMedlineIdentifier(fp.getString("medlineIdentifier"));
    newStudy.setResultsReference(fp.getBoolean("resultsReference"));
    newStudy.setUrl(fp.getString("url"));
    newStudy.setUrlDescription(fp.getString("urlDescription"));
    
    session.setAttribute("newStudy", newStudy);
    
    if (errors.isEmpty()) { 
     
      forwardPage(Page.CREATE_STUDY8);
    } else {
      request.setAttribute("formMessages", errors);
      forwardPage(Page.CREATE_STUDY7);
    }

  }
  
  /**
   * Lets user confirm all the study info entries input
   * 
   * @throws Exception
   */
  private void confirmWholeStudy() throws Exception {

    FormProcessor fp = new FormProcessor(request);
    Validator v = new Validator(request);
    errors = v.validate();

    StudyBean newStudy = (StudyBean) session.getAttribute("newStudy");
    newStudy.getStudyParameterConfig().setCollectDob(fp.getString("collectDob"));
    newStudy.getStudyParameterConfig().setDiscrepancyManagement(fp.getString("discrepancyManagement"));
    newStudy.getStudyParameterConfig().setGenderRequired(fp.getString("genderRequired"));
    
    newStudy.getStudyParameterConfig().setInterviewerNameRequired(fp.getString("interviewerNameRequired"));
    newStudy.getStudyParameterConfig().setInterviewerNameDefault(fp.getString("interviewerNameDefault"));    
    newStudy.getStudyParameterConfig().setInterviewDateEditable(fp.getString("interviewDateEditable"));
    newStudy.getStudyParameterConfig().setInterviewDateRequired(fp.getString("interviewDateRequired"));
    newStudy.getStudyParameterConfig().setInterviewerNameEditable(fp.getString("interviewerNameEditable"));
    newStudy.getStudyParameterConfig().setInterviewDateDefault(fp.getString("interviewDateDefault"));
    
    newStudy.getStudyParameterConfig().setSubjectIdGeneration(fp.getString("subjectIdGeneration"));
    newStudy.getStudyParameterConfig().setSubjectPersonIdRequired(fp.getString("subjectPersonIdRequired"));
    newStudy.getStudyParameterConfig().setSubjectIdPrefixSuffix(fp.getString("subjectIdPrefixSuffix"));
    newStudy.getStudyParameterConfig().setPersonIdShownOnCRF(fp.getString("personIdShownOnCRF"));  
    newStudy.getStudyParameterConfig().setReschedule(fp.getString("reschedule"));  
    
    
    session.setAttribute("newStudy", newStudy);
    
    if (errors.isEmpty()) {    
      if (session.getAttribute("interventionArray") == null) {
        request.setAttribute("interventions", new ArrayList());
      } else {
        request.setAttribute("interventions", session.getAttribute("interventionArray"));
      }
      forwardPage(Page.STUDY_CREATE_CONFIRM);
     
    } else {
      request.setAttribute("formMessages", errors);
      forwardPage(Page.CREATE_STUDY8);
    }

  }
  
  

  /**
   * Inserts the new study into database
   *  
   */
  private boolean submitStudy() {
    StudyDAO sdao = new StudyDAO(sm.getDataSource());
    StudyParameterValueDAO spvdao = new StudyParameterValueDAO(sm.getDataSource());
    StudyBean newStudy = (StudyBean) session.getAttribute("newStudy");

    logger.info("study bean to be created:" + newStudy.getName()
        + newStudy.getProtocolDateVerification());
    //newStudy.setType(StudyType.NONGENETIC);//need to refine in the future
    newStudy.setOwner(ub);
    newStudy.setCreatedDate(new Date());
    //newStudy.setStatus(Status.AVAILABLE);
    StudyBean finalStudy = (StudyBean)sdao.create(newStudy);
    if (!sdao.isQuerySuccessful()) {
    	logger.warning("study could not be saved: "+sdao.getFailureDetails().getMessage());
    	return false;
    }
    
    logger.info("new study created");
    StudyParameterValueBean spv = new StudyParameterValueBean();  
    spv.setStudyId(finalStudy.getId());
    spv.setParameter("collectDob");
    spv.setValue(newStudy.getStudyParameterConfig().getCollectDob());
    spvdao.create(spv);
        
    spv.setParameter("discrepancyManagement");
    spv.setValue(newStudy.getStudyParameterConfig().getDiscrepancyManagement());
    spvdao.create(spv);
    
    spv.setParameter("genderRequired");
    spv.setValue(newStudy.getStudyParameterConfig().getGenderRequired());
    spvdao.create(spv);    
    
    spv.setParameter("subjectPersonIdRequired");
    spv.setValue(newStudy.getStudyParameterConfig().getSubjectPersonIdRequired());
    spvdao.create(spv);
    
   
    spv.setParameter("interviewerNameRequired");
    spv.setValue(newStudy.getStudyParameterConfig().getInterviewerNameRequired());
    spvdao.create(spv);
    
   
    spv.setParameter("interviewerNameDefault");
    spv.setValue(newStudy.getStudyParameterConfig().getInterviewerNameDefault());
    spvdao.create(spv);
    
    spv.setParameter("interviewerNameEditable");
    spv.setValue(newStudy.getStudyParameterConfig().getInterviewerNameEditable());
    spvdao.create(spv);
    
    spv.setParameter("interviewDateRequired");
    spv.setValue(newStudy.getStudyParameterConfig().getInterviewDateRequired());
    spvdao.create(spv);
    
    spv.setParameter("interviewDateDefault");
    spv.setValue(newStudy.getStudyParameterConfig().getInterviewDateDefault());
    spvdao.create(spv);
    
    spv.setParameter("interviewDateEditable");
    spv.setValue(newStudy.getStudyParameterConfig().getInterviewDateEditable());
    spvdao.create(spv);
    
    spv.setParameter("subjectIdGeneration");
    spv.setValue(newStudy.getStudyParameterConfig().getSubjectIdGeneration());
    spvdao.create(spv);
    
    spv.setParameter("subjectIdPrefixSuffix");
    spv.setValue(newStudy.getStudyParameterConfig().getSubjectIdPrefixSuffix());
    spvdao.create(spv);    
    
    spv.setParameter("personIdShownOnCRF");
    spv.setValue(newStudy.getStudyParameterConfig().getPersonIdShownOnCRF());
    spvdao.create(spv); 
    
    spv.setParameter("reschedule");
    spv.setValue(newStudy.getStudyParameterConfig().getReschedule());
    spvdao.create(spv); 
    
    logger.info("study parameters created done");
    return true;
  }

  /**
   * Constructs study bean from the first section
   * 
   * @param request
   * @return
   */
  private StudyBean createStudyBean() {
    FormProcessor fp = new FormProcessor(request);
    StudyBean newStudy = new StudyBean();
    newStudy.setName(fp.getString("name"));
    newStudy.setOfficialTitle(fp.getString("officialTitle"));
    newStudy.setIdentifier(fp.getString("uniqueProId"));
    newStudy.setSecondaryIdentifier(fp.getString("secondProId"));
    newStudy.setPrincipalInvestigator(fp.getString("prinInvestigator"));

    newStudy.setSummary(fp.getString("description"));
    newStudy.setProtocolDescription(fp.getString("protocolDescription"));

    newStudy.setSponsor(fp.getString("sponsor"));
    newStudy.setCollaborators(fp.getString("collaborators"));

    return newStudy;

  }

  /**
   * Updates the study bean with inputs from the second section
   * 
   * @param request
   * @return true if study type is Interventional, otherwise false
   */
  private boolean updateStudy2() {
    FormProcessor fp = new FormProcessor(request);
    StudyBean newStudy = (StudyBean) session.getAttribute("newStudy");
    newStudy.setProtocolType(fp.getString("protocolType"));//protocolType

    //this is not fully supported yet, because the system will not handle studies which are pending
    // or private...
    newStudy.setStatus(Status.get(fp.getInt("statusId")));
    
    newStudy.setProtocolDateVerification(fp.getDate(INPUT_VER_DATE));

    newStudy.setDatePlannedStart(fp.getDate(INPUT_START_DATE));

    if (StringUtil.isBlank(fp.getString(INPUT_END_DATE))) {
      newStudy.setDatePlannedEnd(null);
    } else {
      newStudy.setDatePlannedEnd(fp.getDate(INPUT_END_DATE));
    }

    newStudy.setPhase(fp.getString("phase"));
    if (fp.getInt("genetic") == 1) {
      newStudy.setGenetic(true);
    } else {
      newStudy.setGenetic(false);
    }

    
    session.setAttribute("newStudy", newStudy);
    return "interventional".equalsIgnoreCase(newStudy.getProtocolType());

  }

  /**
   * Updates the study bean with inputs from the third section
   * 
   * @param isInterventional if the study type is internventional
   */
  private void updateStudy3(boolean isInterventional) {
    FormProcessor fp = new FormProcessor(request);
    StudyBean newStudy = (StudyBean) session.getAttribute("newStudy");
    newStudy.setPurpose(fp.getString("purpose"));
    if (isInterventional) {
      newStudy.setAllocation(fp.getString("allocation"));
      newStudy.setMasking(fp.getString("masking"));
      newStudy.setControl(fp.getString("control"));
      newStudy.setAssignment(fp.getString("assignment"));
      newStudy.setEndpoint(fp.getString("endpoint"));

      //Handle Interventions-type and name
      //repeat 10 times for each pair on the web page
      StringBuffer interventions = new StringBuffer();

      ArrayList interventionArray = new ArrayList();

      for (int i = 0; i < 10; i++) {
        String type = fp.getString("interType" + i);
        String name = fp.getString("interName" + i);
        if (!StringUtil.isBlank(type) && !StringUtil.isBlank(name)) {
          InterventionBean ib = new InterventionBean(fp.getString("interType" + i), fp
              .getString("interName" + i));
          interventionArray.add(ib);
          interventions.append(ib.toString()).append(",");
        }
      }
      newStudy.setInterventions(interventions.toString());
      session.setAttribute("interventionArray", interventionArray);

    } else {// type = observational
      newStudy.setDuration(fp.getString("duration"));
      newStudy.setSelection(fp.getString("selection"));
      newStudy.setTiming(fp.getString("timing"));
    }
    session.setAttribute("newStudy", newStudy);

  }

  /**
   * Sets map in request for different JSP pages
   * 
   * @param request
   * @param isInterventional
   */
  private void setMaps(boolean isInterventional) {
    if (isInterventional) {
      request.setAttribute("interPurposeMap", interPurposeMap);
      request.setAttribute("allocationMap", allocationMap);
      request.setAttribute("maskingMap", maskingMap);
      request.setAttribute("controlMap", controlMap);
      request.setAttribute("assignmentMap", assignmentMap);
      request.setAttribute("endpointMap", endpointMap);
      request.setAttribute("interTypeMap", interTypeMap);
      if (session.getAttribute("interventionArray") == null) {
        session.setAttribute("interventions", new ArrayList());
      } else {
        session.setAttribute("interventions", session.getAttribute("interventionArray"));
      }
    } else {
      request.setAttribute("obserPurposeMap", obserPurposeMap);
      request.setAttribute("selectionMap", selectionMap);
      request.setAttribute("timingMap", timingMap);
    }

  }

  protected String getAdminServlet() {
    return SecureController.ADMIN_SERVLET_CODE;
  }

}