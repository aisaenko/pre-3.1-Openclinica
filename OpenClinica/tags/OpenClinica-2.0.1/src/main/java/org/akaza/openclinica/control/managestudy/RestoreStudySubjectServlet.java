/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Date;

import javax.mail.MessagingException;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * Restores a removed subject to a study
 */
public class RestoreStudySubjectServlet extends SecureController {
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
    throw new InsufficientPermissionException(Page.LIST_DEFINITION_SERVLET, "not study director",
        "1");

  }

  public void processRequest() throws Exception {
    String studySubIdString = request.getParameter("id");//studySubjectId
    String subIdString = request.getParameter("subjectId");
    String studyIdString = request.getParameter("studyId");

    SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
    StudySubjectDAO subdao = new StudySubjectDAO(sm.getDataSource());

    if (StringUtil.isBlank(studySubIdString) || StringUtil.isBlank(subIdString)
        || StringUtil.isBlank(studyIdString)) {
      addPageMessage("Please choose a study subject to restore.");
      forwardPage(Page.LIST_STUDY_SUBJECT_SERVLET);
    } else {
      int studyId = (Integer.valueOf(studyIdString.trim())).intValue();
      int studySubId = (Integer.valueOf(studySubIdString.trim())).intValue();
      int subjectId = (Integer.valueOf(subIdString.trim())).intValue();

      SubjectBean subject = (SubjectBean) sdao.findByPK(subjectId);

      StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);

      StudyDAO studydao = new StudyDAO(sm.getDataSource());
      StudyBean study = (StudyBean) studydao.findByPK(studyId);

      //find study events
      StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
      ArrayList events = (ArrayList) sedao.findAllByStudyAndStudySubjectId(study, studySubId);

      String action = request.getParameter("action");
      if ("confirm".equalsIgnoreCase(action)) {
        if (studySub.getStatus().equals(Status.AVAILABLE)) {
          addPageMessage("This subject is already available for this study."
              + " Please contact the System Administrator for more information.");
          forwardPage(Page.LIST_STUDY_SUBJECT_SERVLET);
          return;
        }

        request.setAttribute("subject", subject);
        request.setAttribute("study", study);
        request.setAttribute("studySub", studySub);
        request.setAttribute("events", events);

        forwardPage(Page.RESTORE_STUDY_SUBJECT);
      } else {
        logger.info("submit to restore the subject from study");
        //restore subject from study
        studySub.setStatus(Status.AVAILABLE);
        studySub.setUpdater(ub);
        studySub.setUpdatedDate(new Date());
        subdao.update(studySub);

        //restore all study events
        //restore all event crfs
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
            EventCRFBean eventCRF = (EventCRFBean) eventCRFs.get(k);
            eventCRF.setStatus(Status.AVAILABLE);
            eventCRF.setUpdater(ub);
            eventCRF.setUpdatedDate(new Date());
            ecdao.update(eventCRF);
            //remove all the item data
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

        String emailBody = "The Subject " + subject.getName() + " has been restored to the Study "
            + study.getName() + ".";

        addPageMessage(emailBody);
        sendEmail(emailBody);
        forwardPage(Page.LIST_STUDY_SUBJECT_SERVLET);
      }
    }
  }

  /**
   * Send email to director and administrator
   * 
   * @param request
   * @param response
   */
  private void sendEmail(String emailBody) throws Exception {

    logger.info("Sending email...");
    try {
      EmailEngine em = new EmailEngine(EmailEngine.getSMTPHost());
      //to study director
      em.process(ub.getEmail().trim(), EmailEngine.getAdminEmail(), "Restore Subject to Study",
          emailBody);

      em = null;
      em = new EmailEngine(EmailEngine.getSMTPHost());
      //to admin
      em.process(EmailEngine.getAdminEmail(), EmailEngine.getAdminEmail(),
          "Restore Subject to Study", emailBody);
      logger.info("Sending email done..");
    } catch (MessagingException me) {
      addPageMessage(" Mail cannot be sent to Administrator due to mail server connection problem.");
    }
  }

}