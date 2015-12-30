/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.util.ArrayList;
import java.util.Date;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DeleteEventCRFServlet extends SecureController {
  public static String STUDY_SUB_ID = "ssId";

  public static String EVENT_CRF_ID = "ecId";

  /**
   *  
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }
    addPageMessage("You don't have correct privilege. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECT_SERVLET, "not admin", "1");

  }

  public void processRequest() throws Exception {
    FormProcessor fp = new FormProcessor(request);
    int studySubId = fp.getInt(STUDY_SUB_ID, true);
    int eventCRFId = fp.getInt(EVENT_CRF_ID);

    String action = request.getParameter("action");

    StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
    StudySubjectDAO subdao = new StudySubjectDAO(sm.getDataSource());
    EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());

    if (eventCRFId == 0) {
      addPageMessage("Please choose an Event CRF to delete.");
      request.setAttribute("id", new Integer(studySubId).toString());
      forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
    } else {
      EventCRFBean eventCRF = (EventCRFBean) ecdao.findByPK(eventCRFId);

      StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);
      request.setAttribute("studySub", studySub);

      //construct info needed on view event crf page
      CRFDAO cdao = new CRFDAO(sm.getDataSource());
      CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());

      int crfVersionId = eventCRF.getCRFVersionId();
      CRFBean cb = cdao.findByVersionId(crfVersionId);
      eventCRF.setCrf(cb);

      CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(crfVersionId);
      eventCRF.setCrfVersion(cvb);

      // then get the definition so we can call DisplayEventCRFBean.setFlags
      int studyEventId = eventCRF.getStudyEventId();

      StudyEventBean event = (StudyEventBean) sedao.findByPK(studyEventId);

      int studyEventDefinitionId = sedao.getDefinitionIdFromStudyEventId(studyEventId);
      StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
      StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao
          .findByPK(studyEventDefinitionId);
      event.setStudyEventDefinition(sed);
      request.setAttribute("event", event);

      EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());

      EventDefinitionCRFBean edc = (EventDefinitionCRFBean) edcdao
          .findByStudyEventDefinitionIdAndCRFId(studyEventDefinitionId, cb.getId());

      DisplayEventCRFBean dec = new DisplayEventCRFBean();
      dec.setEventCRF(eventCRF);
      dec.setFlags(eventCRF, ub, currentRole, edc.isDoubleEntry());

      //find all item data
      ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());

      ArrayList itemData = iddao.findAllByEventCRFId(eventCRF.getId());

      request.setAttribute("items", itemData);

      if ("confirm".equalsIgnoreCase(action)) {

        request.setAttribute("displayEventCRF", dec);

        forwardPage(Page.DELETE_EVENT_CRF);
      } else {
        logger.info("submit to delete the event CRF from event");
        //delete all the item data first
        for (int a = 0; a < itemData.size(); a++) {
          ItemDataBean item = (ItemDataBean) itemData.get(a);
          iddao.delete(item.getId());
        }
        //delete event crf
        ecdao.delete(eventCRF.getId());

       
        String emailBody = "The Event CRF " + cb.getName() + " has been deleted from the Event "
            + event.getStudyEventDefinition().getName() + ".";

        addPageMessage(emailBody);
        //sendEmail(emailBody);
        request.setAttribute("id", new Integer(studySubId).toString());
        forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
      }

    }
  }
}