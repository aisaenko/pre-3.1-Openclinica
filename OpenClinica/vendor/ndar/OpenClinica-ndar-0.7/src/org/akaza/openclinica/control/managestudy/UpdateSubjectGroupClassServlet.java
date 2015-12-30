/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Date;

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
 * Servlet to update an existing study template
 */
public class UpdateSubjectGroupClassServlet extends SecureController {
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
    FormProcessor fp = new FormProcessor(request);
    int classId = fp.getInt("id");

    if (classId == 0) {

      addPageMessage("Please choose a group template to edit.");
      forwardPage(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET);
    } else {
      // find all the active definitions in current study,
      //if current study is a site, then find all the definitions from the parent study
      StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
      ArrayList defs = new ArrayList();
      if (currentStudy.getParentStudyId() == 0) {
        defs = seddao.findAllActiveByStudy(currentStudy);
      } else {
        StudyDAO sdao = new StudyDAO(sm.getDataSource());
        StudyBean parent = (StudyBean) sdao.findByPK(currentStudy.getParentStudyId());
        defs = seddao.findAllActiveByStudy(parent);
      }

      StudyTemplateDAO stdao = new StudyTemplateDAO(sm.getDataSource());
      StudyGroupDAO sgdao = new StudyGroupDAO(sm.getDataSource());
      StudyTemplateEventDefDAO steddao = new StudyTemplateEventDefDAO(sm.getDataSource());

      // constructs an arraylist which contains the definition already in the
      // template and other definitions
      // available in the current study
      ArrayList availableTempDefs = new ArrayList();
      StudyTemplateBean stb = (StudyTemplateBean) stdao.findAllByPK(classId);

      ArrayList templateDefs = steddao.findAllByStudyTemplate(stb.getId());
      
      for (int i = 0; i < defs.size(); i++) {
        StudyEventDefinitionBean sed = (StudyEventDefinitionBean) defs.get(i);
        boolean isSelected = false;
        for (int j = 0; j < templateDefs.size(); j++) {
          StudyTemplateEventDefBean sted = (StudyTemplateEventDefBean) templateDefs.get(j);
          if (sed.getId() == sted.getStudyEventDefinitionId()) {
            if (sted.getStatus().equals(Status.AVAILABLE)) {
              sted.setSelected("yes");
            } 
            availableTempDefs.add(sted);// event already in the template
            isSelected = true;
            break;
          }
        }
        if (isSelected == false) {
          StudyTemplateEventDefBean newSted = new StudyTemplateEventDefBean();
          newSted.setStudyEventDefinitionId(sed.getId());
          newSted.setStudyEventDefinitionName(sed.getName());
          newSted.setStudyTemplateId(stb.getId());
          newSted.setStatus(Status.UNAVAILABLE);
          availableTempDefs.add(newSted);
        }
      }

      request.setAttribute("availableTempDefs", availableTempDefs);
      if (!fp.isSubmitted()) {

        ArrayList groups = sgdao.findAllByGroupClass(stb);
        request.setAttribute("groupTypes", TemplateType.toArrayList());
        session.setAttribute("group", stb);
        session.setAttribute("studyGroups", groups);
        forwardPage(Page.UPDATE_SUBJECT_GROUP_CLASS);
      } else {
        if (action.equalsIgnoreCase("confirm")) {
          confirmGroup(availableTempDefs);
        } else if (action.equalsIgnoreCase("submit")) {
          submitGroup(availableTempDefs);
        } else {
          addPageMessage("No action specified.");
          forwardPage(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET);
        }
      }

    }

  }

  /**
   * Validates the first section of study template and save it into study
   * template bean
   * 
   * @param request
   * @param response
   * @throws Exception
   */
  private void confirmGroup(ArrayList availableTempDefs) throws Exception {
    Validator v = new Validator(request);
    FormProcessor fp = new FormProcessor(request);

    v.addValidation("name", Validator.NO_BLANKS);

    v.addValidation("subjectAssignment", Validator.NO_BLANKS);

    v.addValidation("name", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 30);
    v.addValidation("subjectAssignment", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 30);

    errors = v.validate();

    // process the study event definitions selected by user to create
    // the study template
    ArrayList selectedDef = new ArrayList();
    for (int i = 0; i < availableTempDefs.size(); i++) {
      StudyTemplateEventDefBean td = (StudyTemplateEventDefBean) availableTempDefs.get(i);
      String selected = fp.getString("selected" + i);

      if (!StringUtil.isBlank("selected") && selected.equalsIgnoreCase("yes")) {
        td.setSelected("yes");

        int definitionId = fp.getInt("definitionId" + i);
        td.setStudyEventDefinitionId(definitionId);

        float duration = fp.getFloat("eventDuration" + i);
        td.setEventDurationSt(fp.getString("eventDuration"+i));
        if (duration <= 0) {
          Validator.addError(errors, "eventDuration" + i,
              "Event Duration is required and must be a positive number.");
        }

        float idealTime = fp.getFloat("idealTime" + i);
        td.setIdealTimeToNextEventSt(fp.getString("idealTime" +i));

        if (idealTime <= 0) {
          Validator.addError(errors, "idealTime" + i,
              "Ideal Time is required and must be a positive number.");
        }
        if (!StringUtil.isBlank(fp.getString("minTime" + i))) {

          float minTime = fp.getFloat("minTime" + i);
          if (minTime < 0 || minTime > idealTime) {
            Validator.addError(errors, "minTime" + i,
                "Min Time must be a positive number and not longer than ideal time.");
          }         
        }
        td.setMinTimeToNextEventSt(fp.getString("minTime"+i));

        if (!StringUtil.isBlank(fp.getString("maxTime" + i))) {

          float maxTime = fp.getFloat("maxTime" + i);
          if (maxTime < 0 || (maxTime>0 && maxTime < idealTime)) {
            Validator.addError(errors, "maxTime" + i,
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
      int studyGroupId = fp.getInt("studyGroupId" + i);
      String description = fp.getString("studyGroupDescription" + i);
      if (!StringUtil.isBlank(name)) {
        StudyGroupBean g = new StudyGroupBean();
        g.setName(name);
        g.setDescription(description);
        g.setId(studyGroupId);
        studyGroups.add(g);
        if (name.length() > 255) {
          request.setAttribute("studyGroupError", "Group Name cannot be more than 255 characters.");
          break;
        }
        if (description.length() > 1000) {
          request.setAttribute("studyGroupError",
              "Group Description cannot be more than 1000 characters.");
          break;
        }
      }

    }

    if (fp.getInt("groupClassTypeId") == 0) {
      Validator.addError(errors, "groupClassTypeId", "Group Template Type is required.");
    }

    StudyTemplateBean template = (StudyTemplateBean) session.getAttribute("group");
    template.setName(fp.getString("name"));
    template.setStudyTemplateEventDefs(selectedDef);
    template.setStudyTemplateTypeId(fp.getInt("groupClassTypeId"));
    template.setSubjectAssignment(fp.getString("subjectAssignment"));

    session.setAttribute("group", template);
    request.setAttribute("availableTempDefs", availableTempDefs);
    session.setAttribute("studyGroups", studyGroups);

    if (errors.isEmpty()) {
      logger.info("no errors in the first section");
      template.setStudyTemplateTypeName((TemplateType.get(template.getStudyTemplateTypeId()).getName()));

      forwardPage(Page.UPDATE_SUBJECT_GROUP_CLASS_CONFIRM);

    } else {
      logger.info("has validation errors in the first section");
      request.setAttribute("formMessages", errors);
      request.setAttribute("groupTypes", TemplateType.toArrayList());

      forwardPage(Page.UPDATE_SUBJECT_GROUP_CLASS);
    }

  }

  private void submitGroup(ArrayList availableTempDefs) throws OpenClinicaException {
    StudyTemplateBean template = (StudyTemplateBean) session.getAttribute("group");
    ArrayList studyGroups = (ArrayList) session.getAttribute("studyGroups");
    ArrayList newStudyGroups = (ArrayList) session.getAttribute("newStudyGroups");
    StudyTemplateDAO stdao = new StudyTemplateDAO(sm.getDataSource());
    template.setUpdater(ub);
    template.setUpdatedDate(new Date());
    template = (StudyTemplateBean) stdao.update(template);

    if (!template.isActive()) {
      addPageMessage("The group template could not be updated in the database.");
    } else {

      StudyGroupDAO sgdao = new StudyGroupDAO(sm.getDataSource());
      for (int i = 0; i < studyGroups.size(); i++) {
        StudyGroupBean sg = (StudyGroupBean) studyGroups.get(i);
        sg.setStudyGroupClassId(template.getId());
        if (sg.getId() == 0) {
          sg.setOwner(ub);
          sg.setStatus(Status.AVAILABLE);
          sgdao.create(sg);
        } else {

          sg.setUpdater(ub);
          sg.setStatus(Status.AVAILABLE);
          sgdao.update(sg);
        }

      }

      StudyTemplateEventDefDAO steddao = new StudyTemplateEventDefDAO(sm.getDataSource());

      for (int j = 0; j < availableTempDefs.size(); j++) {
        StudyTemplateEventDefBean sted1 = (StudyTemplateEventDefBean) availableTempDefs.get(j);
        if (sted1.getId() > 0) {
          boolean isSelected = false;
          for (int i = 0; i < template.getStudyTemplateEventDefs().size(); i++) {
            StudyTemplateEventDefBean sted = (StudyTemplateEventDefBean) template
                .getStudyTemplateEventDefs().get(i);
            
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
            
            if (sted1.getId() == sted.getId()) {
              isSelected = true;
              break;
            } 
          }
          if (isSelected == false) {
            // find the ones are unselected by user
            sted1.setUpdater(ub);
            sted1.setStatus(Status.DELETED);
            steddao.update(sted1);            
          }
        }// end if sted1.getId()>0

      }

      if (!template.getStudyTemplateEventDefs().isEmpty()) {
        for (int i = 0; i < template.getStudyTemplateEventDefs().size(); i++) {
          StudyTemplateEventDefBean sted = (StudyTemplateEventDefBean) template
              .getStudyTemplateEventDefs().get(i);
          sted.setStudyTemplateId(template.getId());
          
          sted.setStatus(Status.AVAILABLE);
          if (sted.getId() == 0) {
            sted.setOwner(ub);
            steddao.create(sted);
          } else {           
            sted.setUpdater(ub);
            steddao.update(sted);
          }

        }
      }

      addPageMessage("The group template was updated successfully.");
    }
    forwardPage(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET);

  }

}