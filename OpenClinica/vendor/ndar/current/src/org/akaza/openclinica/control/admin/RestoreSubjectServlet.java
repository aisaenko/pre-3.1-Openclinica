/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.admin;

import java.util.ArrayList;
import java.util.Date;

import javax.mail.MessagingException;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * Restores a subject to system, also restore all the related data
 */
public class RestoreSubjectServlet extends SecureController {
  /**
   *  
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }

    addPageMessage("You don't have correct privilege. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.SUBJECT_LIST_SERVLET, "not admin", "1");

  }

  public void processRequest() throws Exception {

    SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
    FormProcessor fp = new FormProcessor(request);
    int subjectId = fp.getInt("id");

    String action = fp.getString("action");
    if (subjectId == 0 || StringUtil.isBlank(action)) {
      addPageMessage("Please choose a subject to restore.");
      forwardPage(Page.SUBJECT_LIST_SERVLET);
    } else {

      SubjectBean subject = (SubjectBean) sdao.findByPK(subjectId);

      //find all study subjects
      StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
      ArrayList studySubs = ssdao.findAllBySubjectId(subjectId);

      // find study events
      StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
      ArrayList events = (ArrayList) sedao.findAllBySubjectId(subjectId);
      if ("confirm".equalsIgnoreCase(action)) {
        request.setAttribute("subjectToRestore", subject);
        request.setAttribute("studySubs", studySubs);
        request.setAttribute("events", events);
        forwardPage(Page.RESTORE_SUBJECT);
      } else {
        logger.info("submit to restore the subject");
        //change all statuses to AVAILABLE
        subject.setStatus(Status.AVAILABLE);
        subject.setUpdater(ub);
        subject.setUpdatedDate(new Date());
        EntityBean sb = sdao.update(subject);

		if (!sdao.isQuerySuccessful() || !sb.isActive()) {
		    throw new OpenClinicaException("Could not restore subject.", "3");
		}

        //remove subject references from study
        for (int i = 0; i < studySubs.size(); i++) {
          StudySubjectBean studySub = (StudySubjectBean) studySubs.get(i);
          studySub.setStatus(Status.AVAILABLE);
          studySub.setUpdater(ub);
          studySub.setUpdatedDate(new Date());
          ssdao.update(studySub);
        }

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
            //restore all the item data
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

        String emailBody = "The Subject " + subject.getName() + " has been restored successfully.";

        addPageMessage(emailBody);
        sendEmail(emailBody);

        forwardPage(Page.SUBJECT_LIST_SERVLET);

      }
    }

  }

  /**
   * Send email to administrator
   * 
   * @param request
   * @param response
   */
  private void sendEmail(String emailBody) throws Exception {

    logger.info("Sending email...");

    EmailEngine em = new EmailEngine(EmailEngine.getSMTPHost());
    try{
    //to admin
    em.process(ub.getEmail().trim(), EmailEngine.getAdminEmail(), "Restore Subject to System",
        emailBody);
    } catch (MessagingException me){
      addPageMessage(" Mail cannot be sent to Administrator due to mail server connection problem.");
    } 
    logger.info("Sending email done..");
  }
  
  protected String getAdminServlet() {
    if (ub.isSysAdmin()) {
      return SecureController.ADMIN_SERVLET_CODE;
    } else {
      return "";
    }
  }

}