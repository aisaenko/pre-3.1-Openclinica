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

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * Unlocks a locked study event definition
 * 
 * @author jxu
 *  
 */
public class UnlockEventDefinitionServlet extends SecureController {
  /**
   *  
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }

    addPageMessage(respage.getString("no_have_correct_privilege_current_study")+ " "
			+ respage.getString("change_study_contact_sysadmin"));
    throw new InsufficientPermissionException(Page.LIST_DEFINITION_SERVLET, resexception.getString("not_admin"), "1");

  }

  public void processRequest() throws Exception {
    String idString = request.getParameter("id");

    int defId = Integer.valueOf(idString.trim()).intValue();
    StudyEventDefinitionDAO sdao = new StudyEventDefinitionDAO(sm.getDataSource());
    StudyEventDefinitionBean sed = (StudyEventDefinitionBean) sdao.findByPK(defId);
    //find all CRFs
    EventDefinitionCRFDAO edao = new EventDefinitionCRFDAO(sm.getDataSource());
    ArrayList eventDefinitionCRFs = (ArrayList) edao.findAllByDefinition(defId);

    CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
    CRFDAO cdao = new CRFDAO(sm.getDataSource());
    for (int i = 0; i < eventDefinitionCRFs.size(); i++) {
      EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
      ArrayList versions = (ArrayList) cvdao.findAllByCRF(edc.getCrfId());
      edc.setVersions(versions);
      CRFBean crf = (CRFBean) cdao.findByPK(edc.getCrfId());
      edc.setCrfName(crf.getName());
    }

    //finds all events
    StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
    ArrayList events = (ArrayList) sedao.findAllByDefinition(sed.getId());

    String action = request.getParameter("action");
    if (StringUtil.isBlank(idString)) {
      addPageMessage(respage.getString("please_choose_a_SED_to_unlock"));
      forwardPage(Page.LIST_DEFINITION_SERVLET);
    } else {
      if ("confirm".equalsIgnoreCase(action)) {
        if (!sed.getStatus().equals(Status.LOCKED)) {
          addPageMessage(respage.getString("this_SED_cannot_be_unlocked")
              + " "+ respage.getString("please_contact_sysadmin_for_more_information"));
          forwardPage(Page.LIST_DEFINITION_SERVLET);
          return;
        }

        request.setAttribute("definitionToUnlock", sed);
        request.setAttribute("eventDefinitionCRFs", eventDefinitionCRFs);
        request.setAttribute("events", events);
        forwardPage(Page.UNLOCK_DEFINITION);
      } else {
        logger.info("submit to lock the definition");
        //unlock definition
        sed.setStatus(Status.AVAILABLE);
        sed.setUpdater(ub);
        sed.setUpdatedDate(new Date());
        sdao.update(sed);

        //lock all crfs
        for (int j = 0; j < eventDefinitionCRFs.size(); j++) {
          EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFs.get(j);
          edc.setStatus(Status.AVAILABLE);
          edc.setUpdater(ub);
          edc.setUpdatedDate(new Date());
          edao.update(edc);
        }
        //unlock all events

        EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());

        for (int j = 0; j < events.size(); j++) {
          StudyEventBean event = (StudyEventBean) events.get(j);
          event.setStatus(Status.AVAILABLE);
          event.setUpdater(ub);
          event.setUpdatedDate(new Date());
          sedao.update(event);

          ArrayList eventCRFs = ecdao.findAllByStudyEvent(event);
          //unlock all the item data
          ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
          for (int k = 0; k < eventCRFs.size(); k++) {
            EventCRFBean eventCRF = (EventCRFBean) eventCRFs.get(k);
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

        String emailBody = respage.getString("the_SED") + " " + sed.getName()
            + respage.getString("has_been_unlocked_for_the_study") + " " + currentStudy.getName()
            + ". " + respage.getString("subject_event_data_is_as_it_was_before");

        addPageMessage(emailBody);
        sendEmail(emailBody);
        forwardPage(Page.LIST_DEFINITION_SERVLET);
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
      em.process(ub.getEmail().trim(), EmailEngine.getAdminEmail(),
    		  respage.getString("unlock_SED"), emailBody);

      em = null;
      em = new EmailEngine(EmailEngine.getSMTPHost());
      //to admin
      em.process(EmailEngine.getAdminEmail(), EmailEngine.getAdminEmail(),
    		  respage.getString("unlock_SED"), emailBody);
      logger.info("Sending email done..");
    } catch (MessagingException me) {
      addPageMessage(respage.getString("mail_cannot_be_sent_to_admin"));
    }
  }

}
