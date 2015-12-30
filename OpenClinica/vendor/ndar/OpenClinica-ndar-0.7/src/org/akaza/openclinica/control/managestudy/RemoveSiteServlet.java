/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.*;
import org.akaza.openclinica.bean.login.*;
import org.akaza.openclinica.bean.submit.*;
import org.akaza.openclinica.bean.extract.*;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.*;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.submit.*;
import org.akaza.openclinica.dao.extract.*;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

import java.util.*;

/**
 * @author jxu
 * 
 * Removes a site from a study
 */
public class RemoveSiteServlet extends SecureController {

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
    throw new InsufficientPermissionException(Page.SITE_LIST_SERVLET, "not study director", "1");

  }

  public void processRequest() throws Exception {

    StudyDAO sdao = new StudyDAO(sm.getDataSource());
    String idString = request.getParameter("id");
    logger.info("site id:" + idString);

    int siteId = Integer.valueOf(idString.trim()).intValue();
    StudyBean study = (StudyBean) sdao.findByPK(siteId);

    //find all user and roles
    UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
    ArrayList userRoles = udao.findAllByStudyId(siteId);

    //find all subjects
    StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
    ArrayList subjects = ssdao.findAllByStudy(study);

    //find all events
    StudyEventDefinitionDAO sefdao = new StudyEventDefinitionDAO(sm.getDataSource());
    ArrayList definitions = sefdao.findAllByStudy(study);

    String action = request.getParameter("action");
    if (StringUtil.isBlank(idString)) {
      addPageMessage("Please choose a site to remove.");
      forwardPage(Page.SITE_LIST_SERVLET);
    } else {
      if ("confirm".equalsIgnoreCase(action)) {
        request.setAttribute("siteToRemove", study);

        request.setAttribute("userRolesToRemove", userRoles);

        request.setAttribute("subjectsToRemove", subjects);

        request.setAttribute("definitionsToRemove", definitions);
        forwardPage(Page.REMOVE_SITE);
      } else {
        logger.info("submit to remove the site");
        //change all statuses to unavailable
        StudyDAO studao = new StudyDAO(sm.getDataSource());
        study.setStatus(Status.DELETED);
        study.setUpdater(ub);
        study.setUpdatedDate(new Date());
        studao.update(study);

        //remove all users and roles
        for (int i = 0; i < userRoles.size(); i++) {
          StudyUserRoleBean role = (StudyUserRoleBean) userRoles.get(i);
          role.setStatus(Status.DELETED);
          role.setUpdater(ub);
          role.setUpdatedDate(new Date());
          udao.updateStudyUserRole(role, role.getName());
        }

        //remove all subjects
        for (int i = 0; i < subjects.size(); i++) {
          StudySubjectBean subject = (StudySubjectBean) subjects.get(i);
          subject.setStatus(Status.DELETED);
          subject.setUpdater(ub);
          subject.setUpdatedDate(new Date());
          ssdao.update(subject);
        }

        //remove all study_group
        StudyGroupDAO sgdao = new StudyGroupDAO(sm.getDataSource());
        SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(sm.getDataSource());
        ArrayList groups = sgdao.findAllByStudy(study);
        for (int i = 0; i < groups.size(); i++) {
          StudyGroupBean group = (StudyGroupBean) groups.get(i);
          group.setStatus(Status.DELETED);
          group.setUpdater(ub);
          group.setUpdatedDate(new Date());
          sgdao.update(group);
          //all subject_group_map
          ArrayList subjectGroupMaps = sgmdao.findAllByStudyGroupId(group.getId());
          for (int j = 0; j < subjectGroupMaps.size(); j++) {
            SubjectGroupMapBean sgMap = (SubjectGroupMapBean) subjectGroupMaps.get(j);
            sgMap.setStatus(Status.DELETED);
            sgMap.setUpdater(ub);
            sgMap.setUpdatedDate(new Date());
            sgmdao.update(sgMap);
          }
        }

        //remove all event definitions and event
        EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
        StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
        for (int i = 0; i < definitions.size(); i++) {
          StudyEventDefinitionBean definition = (StudyEventDefinitionBean) definitions.get(i);
          definition.setStatus(Status.DELETED);
          definition.setUpdater(ub);
          definition.setUpdatedDate(new Date());
          sefdao.update(definition);
          ArrayList edcs = (ArrayList) edcdao.findAllByDefinition(definition.getId());
          for (int j = 0; j < edcs.size(); j++) {
            EventDefinitionCRFBean edc = (EventDefinitionCRFBean) edcs.get(j);
            edc.setStatus(Status.DELETED);
            edc.setUpdater(ub);
            edc.setUpdatedDate(new Date());
            edcdao.update(edc);
          }

          ArrayList events = (ArrayList) sedao.findAllByDefinition(definition.getId());
          EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());

          for (int j = 0; j < events.size(); j++) {
            StudyEventBean event = (StudyEventBean) events.get(j);
            event.setStatus(Status.DELETED);
            event.setUpdater(ub);
            event.setUpdatedDate(new Date());
            sedao.update(event);

            ArrayList eventCRFs = ecdao.findAllByStudyEvent(event);

            ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
            for (int k = 0; k < eventCRFs.size(); k++) {
              EventCRFBean eventCRF = (EventCRFBean)eventCRFs.get(k);
              eventCRF.setStatus(Status.DELETED);
              eventCRF.setUpdater(ub);
              eventCRF.setUpdatedDate(new Date());
              ecdao.update(eventCRF);

              ArrayList itemDatas = iddao.findAllByEventCRFId(eventCRF.getId());
              for (int a = 0; a < itemDatas.size(); a++) {
                ItemDataBean item = (ItemDataBean) itemDatas.get(a);
                item.setStatus(Status.DELETED);
                item.setUpdater(ub);
                item.setUpdatedDate(new Date());
                iddao.update(item);
              }
            }
          }
        }//for definitions

        DatasetDAO datadao = new DatasetDAO(sm.getDataSource());
        ArrayList dataset = datadao.findAllByStudyId(study.getId());
        for (int i = 0; i < dataset.size(); i++) {
          DatasetBean data = (DatasetBean) dataset.get(i);
          data.setStatus(Status.DELETED);
          data.setUpdater(ub);
          data.setUpdatedDate(new Date());
          datadao.update(data);
        }
        
        addPageMessage("This site has been removed successfully.");
        
        String fromListSite = (String)session.getAttribute("fromListSite");
        if (fromListSite != null && fromListSite.equals("yes") &&
            currentRole.getRole().getName().equalsIgnoreCase("director")) {
          session.removeAttribute("fromListSite");
          forwardPage(Page.SITE_LIST_SERVLET);
        } else {
          session.removeAttribute("fromListSite");
          forwardPage(Page.STUDY_LIST_SERVLET);
        }

      }
    }

  }

}