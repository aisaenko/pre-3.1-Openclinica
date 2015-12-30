/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.TemplateType;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.bean.managestudy.StudyTemplateBean;
import org.akaza.openclinica.bean.managestudy.StudyTemplateEventDefBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudyTemplateDAO;
import org.akaza.openclinica.dao.managestudy.StudyTemplateEventDefDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * Servlet to create a new study template
 */
public class CreateSubjectGroupClassServlet extends SecureController {
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
    throw new InsufficientPermissionException(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET,
        "not study director", "1");

  }

  public void processRequest() throws Exception {
    String action = request.getParameter("action");
    // find all the active definitions in current study,
    //if current study is a site, then find all the definitions from the parent study
    StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
    ArrayList templateDefinitions = new ArrayList();
    ArrayList defs = new ArrayList();
    if (currentStudy.getParentStudyId() == 0) {
    defs = seddao.findAllActiveByStudy(currentStudy);
    } else {
      StudyDAO sdao = new StudyDAO(sm.getDataSource());
      StudyBean parent = (StudyBean)sdao.findByPK(currentStudy.getParentStudyId());
      defs = seddao.findAllActiveByStudy(parent);
    }
    
    for (int i=0; i<defs.size(); i++) {
      StudyEventDefinitionBean sed = (StudyEventDefinitionBean)defs.get(i);
      StudyTemplateEventDefBean sted = new StudyTemplateEventDefBean();
      sted.setStudyEventDefinitionId(sed.getId());
      sted.setStudyEventDefinitionName(sed.getName()); 
      templateDefinitions.add(sted);    
    }
    if (StringUtil.isBlank(action)) {
      ArrayList studyGroups = new ArrayList();
      for (int i = 0; i < 10; i++) {
        studyGroups.add(new StudyGroupBean());
      }
      StudyTemplateBean group = new StudyTemplateBean();     
       
      request.setAttribute("templateDefs", templateDefinitions);
      request.setAttribute("groupTypes", TemplateType.toArrayList());
      session.setAttribute("group", group);
      session.setAttribute("studyGroups", studyGroups);
      forwardPage(Page.CREATE_SUBJECT_GROUP_CLASS);

    } else {
      if ("confirm".equalsIgnoreCase(action)) {
        confirmTemplate(templateDefinitions);

      } else if ("submit".equalsIgnoreCase(action)) {
        submitTemplate();

      }
    }

  }

  /**
   * Validates the first section of study template inputs and save it 
   * into study template bean
   * 
   * @param request
   * @param response
   * @throws Exception
   */
  private void confirmTemplate(ArrayList templateDefinitions) throws Exception {
    Validator v = new Validator(request);
    FormProcessor fp = new FormProcessor(request);

    v.addValidation("name", Validator.NO_BLANKS);    
    
    v.addValidation("subjectAssignment", Validator.NO_BLANKS);

    v.addValidation("name", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 30);
    v.addValidation("subjectAssignment", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 30);

    errors = v.validate();
    
    // process the study event definitions selected by user to create the study template
    ArrayList selectedDef = new ArrayList();
    for ( int i=0; i< templateDefinitions.size();i++) {
      StudyTemplateEventDefBean td = (StudyTemplateEventDefBean)templateDefinitions.get(i);
      String selected = fp.getString("selected" +i);
     
      if (!StringUtil.isBlank("selected") && selected.equalsIgnoreCase("yes")) {
        td.setSelected("yes");
        
        int definitionId = fp.getInt("definitionId" + i);        
        td.setStudyEventDefinitionId(definitionId);          
        
        float duration = fp.getFloat("eventDuration"+i);
        td.setEventDurationSt(fp.getString("eventDuration"+i));
        if (duration<=0) {
          Validator.addError(errors, "eventDuration"+i,
          "Event Duration is required and must be a positive number.");
        }
        
        float idealTime = fp.getFloat("idealTime" +i);
        td.setIdealTimeToNextEventSt(fp.getString("idealTime" +i));  
        
        
        if (idealTime<=0) {
          Validator.addError(errors, "idealTime"+i,
          "Ideal Time is required and must be a positive number.");
        }
        if (!StringUtil.isBlank(fp.getString("minTime" +i))) {
         
          float minTime = fp.getFloat("minTime"+i);
          if (minTime<=0 || minTime > idealTime) {
            Validator.addError(errors, "minTime"+i,
            "Min Time must be a positive number and not longer than ideal time.");
          }          
        }
        td.setMinTimeToNextEventSt(fp.getString("minTime"+i));
        
        if (!StringUtil.isBlank(fp.getString("maxTime" +i))) {
         
          float maxTime = fp.getFloat("maxTime"+i);
          if (maxTime<=0 || (maxTime>0 && maxTime < idealTime)) {
            Validator.addError(errors, "maxTime"+i,
            "Max Time must be a positive number and not less than ideal time.");
          }             
        }    
        td.setMaxTimeToNextEventSt(fp.getString("maxTime"+i));
       
        selectedDef.add(td);       
        
      }      
    }
    
    
    ArrayList studyGroups = new ArrayList();
    for (int i = 0; i < 10; i++) {
      String name = fp.getString("studyGroup" + i);
      String description = fp.getString("studyGroupDescription" + i);
      if (!StringUtil.isBlank(name)) {
        StudyGroupBean group = new StudyGroupBean();
        group.setName(name);
        group.setDescription(description);
        studyGroups.add(group);
        if (name.length() > 255) {
          request.setAttribute("studyGroupError", "Group Name cannot be more than 255 characters.");
          break;
        }
        if (description.length() > 1000) {
          request.setAttribute("studyGroupError", "Group Description cannot be more than 1000 characters.");
          break;
        }
      }

    }
    
    
    if (fp.getInt("groupClassTypeId")==0) {
      Validator.addError(errors, "groupClassTypeId",
      "Group Template Type is required.");
    }
       
    StudyTemplateBean template = new StudyTemplateBean();
    template.setStudyTemplateEventDefs(selectedDef);
    template.setName(fp.getString("name"));
    template.setStudyTemplateTypeId(fp.getInt("groupClassTypeId"));
    template.setSubjectAssignment(fp.getString("subjectAssignment"));

    request.setAttribute("templateDefs", templateDefinitions);
    session.setAttribute("group", template);
    session.setAttribute("studyGroups", studyGroups);

    if (errors.isEmpty()) {
      logger.info("no errors in the first section");
      template.setStudyTemplateTypeName((TemplateType.get(template.getStudyTemplateTypeId()).getName()));

      forwardPage(Page.CREATE_SUBJECT_GROUP_CLASS_CONFIRM);

    } else {
      logger.info("has validation errors in the first section");
      request.setAttribute("formMessages", errors);
      request.setAttribute("groupTypes", TemplateType.toArrayList());

      forwardPage(Page.CREATE_SUBJECT_GROUP_CLASS);
    }

  }

  /**
   * Saves study template information into database
   * @throws OpenClinicaException
   */
  private void submitTemplate() throws OpenClinicaException {
    StudyTemplateBean template = (StudyTemplateBean) session.getAttribute("group");
    ArrayList studyGroups = (ArrayList) session.getAttribute("studyGroups");
    StudyTemplateDAO stdao = new StudyTemplateDAO(sm.getDataSource());
    template.setStudyId(currentStudy.getId());
    template.setOwner(ub);
    template.setStatus(Status.AVAILABLE);
    template = (StudyTemplateBean) stdao.create(template);
    logger.info("template id:" + template.getId());
    if (!template.isActive()) {
      addPageMessage("The group template could not be created in the database.");
    } else {
      StudyGroupDAO sgdao = new StudyGroupDAO(sm.getDataSource());
      for (int i = 0; i < studyGroups.size(); i++) {       
        StudyGroupBean sg = (StudyGroupBean)studyGroups.get(i);       
        sg.setStudyGroupClassId(template.getId());
        sg.setOwner(ub);
        sg.setStatus(Status.AVAILABLE);
        sgdao.create(sg);

      }
      
      StudyTemplateEventDefDAO steddao = new StudyTemplateEventDefDAO(sm.getDataSource());
      if (!template.getStudyTemplateEventDefs().isEmpty()) {
        for (int i=0; i<template.getStudyTemplateEventDefs().size(); i++) {
          StudyTemplateEventDefBean sted =
            (StudyTemplateEventDefBean)template.getStudyTemplateEventDefs().get(i);
          sted.setStudyTemplateId(template.getId());
          
          if (StringUtil.isBlank(sted.getEventDurationSt())){
            sted.setEventDuration(null);
          } else {
            sted.setEventDuration(Float.valueOf(sted.getEventDurationSt()));
          }
          
          if (StringUtil.isBlank(sted.getIdealTimeToNextEventSt())) {
            sted.setIdealTimeToNextEvent(null);
          } else {
            sted.setIdealTimeToNextEvent(Float.valueOf(sted.getIdealTimeToNextEventSt()));
          }
          
          if (StringUtil.isBlank(sted.getMaxTimeToNextEventSt())) {
            sted.setMaxTimeToNextEvent(null);
          } else {
            sted.setMaxTimeToNextEvent(Float.valueOf(sted.getMaxTimeToNextEventSt()));
            
          }
          if (StringUtil.isBlank(sted.getMinTimeToNextEventSt())) {
            sted.setMinTimeToNextEvent(null);
          } else {
            sted.setMinTimeToNextEvent(Float.valueOf(sted.getMinTimeToNextEventSt()));
            
          }
          sted.setOwner(ub);;
          sted.setStatus(Status.AVAILABLE);
          steddao.create(sted);
        }
      }
      session.removeAttribute("group");
      session.removeAttribute("studyGroups");
      addPageMessage("The group template was created successfully.");
    }
    forwardPage(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET);

  }

}