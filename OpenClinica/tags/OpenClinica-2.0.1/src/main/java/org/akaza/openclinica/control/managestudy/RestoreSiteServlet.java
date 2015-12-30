/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Date;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * Restores a removed site and all its data, including users. roles, study groups, definitions,
 * events and items
 * 
 * @author jxu
 * 
 */
public class RestoreSiteServlet extends SecureController {
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
      addPageMessage("Please choose a site to restore.");
      forwardPage(Page.SITE_LIST_SERVLET);
    } else {
      if ("confirm".equalsIgnoreCase(action)) {
        request.setAttribute("siteToRestore", study);

        request.setAttribute("userRolesToRestore", userRoles);

        request.setAttribute("subjectsToRestore", subjects);

        request.setAttribute("definitionsToRestore", definitions);
        forwardPage(Page.RESTORE_SITE);
      } else {
        logger.info("submit to restore the site");
        //change all statuses to unavailable
        StudyDAO studao = new StudyDAO(sm.getDataSource());
        study.setStatus(Status.AVAILABLE);
        study.setUpdater(ub);
        study.setUpdatedDate(new Date());
        studao.update(study);

        //remove all users and roles
        for (int i = 0; i < userRoles.size(); i++) {
          StudyUserRoleBean role = (StudyUserRoleBean) userRoles.get(i);
          role.setStatus(Status.AVAILABLE);
          role.setUpdater(ub);
          role.setUpdatedDate(new Date());
          udao.updateStudyUserRole(role, role.getName());
        }

        //remove all subjects
        for (int i = 0; i < subjects.size(); i++) {
          StudySubjectBean subject = (StudySubjectBean) subjects.get(i);
          subject.setStatus(Status.AVAILABLE);
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
          group.setStatus(Status.AVAILABLE);
          group.setUpdater(ub);
          group.setUpdatedDate(new Date());
          sgdao.update(group);
          //all subject_group_map
          ArrayList subjectGroupMaps = sgmdao.findAllByStudyGroupId(group.getId());
          for (int j = 0; j < subjectGroupMaps.size(); j++) {
            SubjectGroupMapBean sgMap = (SubjectGroupMapBean) subjectGroupMaps.get(j);
            sgMap.setStatus(Status.AVAILABLE);
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
          definition.setStatus(Status.AVAILABLE);
          definition.setUpdater(ub);
          definition.setUpdatedDate(new Date());
          sefdao.update(definition);
          ArrayList edcs = (ArrayList) edcdao.findAllByDefinition(definition.getId());
          for (int j = 0; j < edcs.size(); j++) {
            EventDefinitionCRFBean edc = (EventDefinitionCRFBean) edcs.get(j);
            edc.setStatus(Status.AVAILABLE);
            edc.setUpdater(ub);
            edc.setUpdatedDate(new Date());
            edcdao.update(edc);
          }

          ArrayList events = (ArrayList) sedao.findAllByDefinition(definition.getId());
          EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());

          for (int j = 0; j < events.size(); j++) {
            StudyEventBean event = (StudyEventBean) events.get(j);
            event.setStatus(Status.AVAILABLE);
            event.setUpdater(ub);
            event.setUpdatedDate(new Date());
            sedao.update(event);

            ArrayList eventCRFs = ecdao.findAllByStudyEvent(event);

            ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
            for (int k = 0; k < eventCRFs.size(); k++) {
              EventCRFBean eventCRF = (EventCRFBean) edcs.get(k);
              eventCRF.setStatus(Status.AVAILABLE);
              eventCRF.setUpdater(ub);
              eventCRF.setUpdatedDate(new Date());
              ecdao.update(eventCRF);

              ArrayList itemDatas = iddao.findAllByEventCRFId(eventCRF.getId());
              for (int a = 0; a < itemDatas.size(); a++) {
                ItemDataBean item = (ItemDataBean) itemDatas.get(a);
                item.setStatus(Status.AVAILABLE);
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
          data.setStatus(Status.AVAILABLE);
          data.setUpdater(ub);
          data.setUpdatedDate(new Date());
          datadao.update(data);
        }

        addPageMessage("This site has been restored successfully.");
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