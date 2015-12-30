/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.util.ArrayList;
import java.util.Date;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 *
 * Processes the request of removing a top level study, all the data
 * assoicated with this study will be removed
 */
public class RemoveStudyServlet extends SecureController {
  /**
   *  
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }

    addPageMessage("You don't have correct privilege. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.STUDY_LIST_SERVLET, "not admin", "1");

  }

  public void processRequest() throws Exception {

    StudyDAO sdao = new StudyDAO(sm.getDataSource());
    FormProcessor fp = new FormProcessor(request);
    int studyId = fp.getInt("id");

   
    StudyBean study = (StudyBean) sdao.findByPK(studyId);
    //find all sites
    ArrayList sites = (ArrayList)sdao.findAllByParent(studyId);

    //find all user and roles in the study, include ones in sites
    UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
    ArrayList userRoles = udao.findAllByStudyId(studyId);

    //find all subjects in the study, include ones in sites
    StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
    ArrayList subjects = ssdao.findAllByStudy(study);

    //find all events in the study, include ones in sites
    StudyEventDefinitionDAO sefdao = new StudyEventDefinitionDAO(sm.getDataSource());
    ArrayList definitions = sefdao.findAllByStudy(study);

    String action = request.getParameter("action");
    if (studyId==0) {
      addPageMessage("Please choose a study to remove.");
      forwardPage(Page.STUDY_LIST_SERVLET);
    } else {
      if ("confirm".equalsIgnoreCase(action)) {
        request.setAttribute("studyToRemove", study);
        
        request.setAttribute("sitesToRemove", sites);

        request.setAttribute("userRolesToRemove", userRoles);

        request.setAttribute("subjectsToRemove", subjects);

        request.setAttribute("definitionsToRemove", definitions);
        forwardPage(Page.REMOVE_STUDY);
      } else {
        logger.info("submit to remove the study");
        //change all statuses to unavailable
        StudyDAO studao = new StudyDAO(sm.getDataSource());
        study.setStatus(Status.DELETED);
        study.setUpdater(ub);
        study.setUpdatedDate(new Date());
        studao.update(study);
        
        //remove all sites
        for (int i = 0; i < sites.size(); i++) {
          StudyBean site = (StudyBean) sites.get(i);
          site.setStatus(Status.DELETED);
          site.setUpdater(ub);
          site.setUpdatedDate(new Date());
          sdao.update(site);
        }
        

        //remove all users and roles
        for (int i = 0; i < userRoles.size(); i++) {        
          StudyUserRoleBean role = (StudyUserRoleBean) userRoles.get(i);
          logger.info("remove user role" + role.getName()) ;
          role.setStatus(Status.DELETED);
          role.setUpdater(ub);
          role.setUpdatedDate(new Date());
          udao.updateStudyUserRole(role, role.getUserName());
        }

        //remove all subjects
        for (int i = 0; i < subjects.size(); i++) {
          StudySubjectBean subject = (StudySubjectBean) subjects.get(i);
          subject.setStatus(Status.DELETED);
          subject.setUpdater(ub);
          subject.setUpdatedDate(new Date());
          ssdao.update(subject);
        }

        //remove all study_group_class 
        //changed by jxu on 08-31-06, to fix the problem of no study_id in study_group table
        StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(sm.getDataSource());
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
        
        ArrayList groupClasses = sgcdao.findAllActiveByStudy(study);
        for (int i = 0; i < groupClasses.size(); i++) {
          StudyGroupClassBean gc = (StudyGroupClassBean) groupClasses.get(i);
          gc.setStatus(Status.DELETED);
          gc.setUpdater(ub);
          gc.setUpdatedDate(new Date());
          sgcdao.update(gc);
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
        
        addPageMessage("This study has been removed successfully.");
        forwardPage(Page.STUDY_LIST_SERVLET);

      }
    }

  }
  
  protected String getAdminServlet() {
	return SecureController.ADMIN_SERVLET_CODE;
  }

}
