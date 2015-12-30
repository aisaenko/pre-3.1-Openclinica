/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import java.util.ArrayList;
import java.util.HashMap;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.managestudy.DisplayEventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.managestudy.ViewStudySubjectServlet;
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
 * @author ssachs
 */
public class EnterDataForStudyEventServlet extends SecureController {

  public static final String INPUT_EVENT_ID = "eventId";

  public static final String BEAN_STUDY_EVENT = "studyEvent";

  public static final String BEAN_STUDY_SUBJECT = "studySubject";

  public static final String BEAN_UNCOMPLETED_EVENTDEFINITIONCRFS = "uncompletedEventDefinitionCRFs";

  public static final String BEAN_DISPLAY_EVENT_CRFS = "displayEventCRFs";

  private StudyEventBean getStudyEvent(int eventId) throws Exception {
    StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());

    StudyBean studyWithSED = currentStudy;
    if (currentStudy.getParentStudyId() > 0) {
      studyWithSED = new StudyBean();
      studyWithSED.setId(currentStudy.getParentStudyId());
    }

    AuditableEntityBean aeb = sedao.findByPKAndStudy(eventId, studyWithSED);

    if (!aeb.isActive()) {
      addPageMessage("The study event for which you are attempting to enter data does not belong to the current study.");
      throw new InsufficientPermissionException(Page.SUBMIT_DATA_SERVLET,
          "study event does not belong to the current study", "1");
    }

    StudyEventBean seb = (StudyEventBean) aeb;

    StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
    StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) seddao.findByPK(seb
        .getStudyEventDefinitionId());
    seb.setStudyEventDefinition(sedb);
    return seb;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.core.SecureController#processRequest()
   */
  protected void processRequest() throws Exception {
    FormProcessor fp = new FormProcessor(request);

    int eventId = fp.getInt(INPUT_EVENT_ID, true);
    //System.out.println("333333***************" + eventId);
    // so we can display the event for which we're entering data
    StudyEventBean seb = getStudyEvent(eventId);

    // so we can display the subject's label
    StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
    StudySubjectBean ssb = (StudySubjectBean) ssdao.findByPK(seb.getStudySubjectId());

    // prepare to figure out what the display should look like
    EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
    ArrayList eventCRFs = ecdao.findAllByStudyEvent(seb);

    EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
    ArrayList eventDefinitionCRFs = edcdao.findAllActiveByEventDefinitionId(seb
        .getStudyEventDefinitionId());

    // get the event definition CRFs for which no event CRF exists
    // the event definition CRFs must be populated with versions so we can
    // let the user choose which version he will enter data for
    ArrayList uncompletedEventDefinitionCRFs = getUncompletedCRFs(eventDefinitionCRFs, eventCRFs);
    populateUncompletedCRFsWithCRFAndVersions(uncompletedEventDefinitionCRFs);

    // for the event definition CRFs for which event CRFs exist, get
    // DisplayEventCRFBeans, which the JSP will use to determine what
    // the user will see for each event CRF
    ArrayList displayEventCRFs = getDisplayEventCRFs(eventCRFs, eventDefinitionCRFs);

    request.setAttribute(BEAN_STUDY_EVENT, seb);
    request.setAttribute(BEAN_STUDY_SUBJECT, ssb);
    request.setAttribute(BEAN_UNCOMPLETED_EVENTDEFINITIONCRFS, uncompletedEventDefinitionCRFs);
    request.setAttribute(BEAN_DISPLAY_EVENT_CRFS, displayEventCRFs);
    
    //this is for generating side info panel
    ArrayList beans = ViewStudySubjectServlet.getDisplayStudyEventsForStudySubject(ssb,sm.getDataSource(),ub,currentRole);
    request.setAttribute("beans", beans);
    EventCRFBean ecb = new EventCRFBean();
    ecb.setStudyEventId(eventId);
    request.setAttribute("eventCRF", ecb); 
    
    forwardPage(Page.ENTER_DATA_FOR_STUDY_EVENT);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
   */
  protected void mayProceed() throws InsufficientPermissionException {
    String exceptionName = "no permission to submit data";
    String noAccessMessage = "You may not enter data for this study event.";

    if (SubmitDataServlet.maySubmitData(ub, currentRole)) {
      return;
    }

    addPageMessage(noAccessMessage);
    throw new InsufficientPermissionException(Page.SUBMIT_DATA_SERVLET, exceptionName, "1");
  }

  /**
   * Finds all the event definitions for which no event CRF exists - which is
   * the list of event definitions with uncompleted event CRFs.
   * 
   * @param eventDefinitionCRFs
   *          All of the event definition CRFs for this study event.
   * @param eventCRFs
   *          All of the event CRFs for this study event.
   * @return The list of event definitions for which no event CRF exists.
   */
  private ArrayList getUncompletedCRFs(ArrayList eventDefinitionCRFs, ArrayList eventCRFs) {
    int i;
    HashMap completed = new HashMap();
    HashMap startedButIncompleted = new HashMap();
    ArrayList answer = new ArrayList();

    /**
     * A somewhat non-standard algorithm is used here: let answer = empty;
     * foreach event definition ED, set isCompleted(ED) = false foreach event
     * crf EC, set isCompleted(EC.getEventDefinition()) = true foreach event
     * definition ED, if (!isCompleted(ED)) { answer += ED; } return answer;
     * This algorithm is guaranteed to find all the event definitions for which
     * no event CRF exists.
     * 
     * The motivation for using this algorithm is reducing the number of
     * database hits.
     * 
     * -jun-we have to add more CRFs here: the event CRF which dones't have item
     * data yet
     */

    for (i = 0; i < eventDefinitionCRFs.size(); i++) {
      EventDefinitionCRFBean edcrf = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
      completed.put(new Integer(edcrf.getCrfId()), Boolean.FALSE);
      startedButIncompleted.put(new Integer(edcrf.getCrfId()), new EventCRFBean());
    }

    CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
    ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
    for (i = 0; i < eventCRFs.size(); i++) {
      EventCRFBean ecrf = (EventCRFBean) eventCRFs.get(i);
      int crfId = cvdao.getCRFIdFromCRFVersionId(ecrf.getCRFVersionId());
      ArrayList idata = iddao.findAllByEventCRFId(ecrf.getId());
      if (!idata.isEmpty()) {//this crf has data already
        completed.put(new Integer(crfId), Boolean.TRUE);
      } else {// event crf got created, but no data entered
        startedButIncompleted.put(new Integer(crfId), ecrf);
      }
    }

    for (i = 0; i < eventDefinitionCRFs.size(); i++) {
      DisplayEventDefinitionCRFBean dedc = new DisplayEventDefinitionCRFBean();
      EventDefinitionCRFBean edcrf = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
      dedc.setEdc(edcrf);
      Boolean b = (Boolean) completed.get(new Integer(edcrf.getCrfId()));
      EventCRFBean ev = (EventCRFBean) startedButIncompleted.get(new Integer(edcrf.getCrfId()));
      if ((b == null) || !b.booleanValue()) {
        dedc.setEventCRF(ev);
        answer.add(dedc);
      }
    }

    return answer;
  }

  private void populateUncompletedCRFsWithCRFAndVersions(ArrayList uncompletedEventDefinitionCRFs) {
    CRFDAO cdao = new CRFDAO(sm.getDataSource());
    CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());

    int size = uncompletedEventDefinitionCRFs.size();
    for (int i = 0; i < size; i++) {
      DisplayEventDefinitionCRFBean dedcrf = (DisplayEventDefinitionCRFBean) uncompletedEventDefinitionCRFs
          .get(i);
      CRFBean cb = (CRFBean) cdao.findByPK(dedcrf.getEdc().getCrfId());
      dedcrf.getEdc().setCrf(cb);

      ArrayList versions = (ArrayList) cvdao.findAllActiveByCRF(dedcrf.getEdc().getCrfId());
      dedcrf.getEdc().setVersions(versions);
      uncompletedEventDefinitionCRFs.set(i, dedcrf);
    }
  }

  /**
   * Each of the event CRFs with its corresponding CRFBean. Then generates a
   * list of DisplayEventCRFBeans, one for each event CRF.
   * 
   * @param eventCRFs
   *          The list of event CRFs for this study event.
   * @param eventDefinitionCRFs
   *          The list of event definition CRFs for this study event.
   * @return The list of DisplayEventCRFBeans for this study event.
   */
  private ArrayList getDisplayEventCRFs(ArrayList eventCRFs, ArrayList eventDefinitionCRFs) {
    ArrayList answer = new ArrayList();

    HashMap definitionsByCRFId = new HashMap();
    int i;
    for (i = 0; i < eventDefinitionCRFs.size(); i++) {
      EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
      definitionsByCRFId.put(new Integer(edc.getCrfId()), edc);
    }

    CRFDAO cdao = new CRFDAO(sm.getDataSource());
    CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
    ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());

    for (i = 0; i < eventCRFs.size(); i++) {
      EventCRFBean ecb = (EventCRFBean) eventCRFs.get(i);

      // populate the event CRF with its crf bean
      int crfVersionId = ecb.getCRFVersionId();
      CRFBean cb = cdao.findByVersionId(crfVersionId);
      ecb.setCrf(cb);

      CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(crfVersionId);
      ecb.setCrfVersion(cvb);

      EventDefinitionCRFBean edcb = (EventDefinitionCRFBean) definitionsByCRFId.get(new Integer(cb
          .getId()));
      DisplayEventCRFBean dec = new DisplayEventCRFBean();

      dec.setFlags(ecb, ub, currentRole, edcb.isDoubleEntry());
      ArrayList idata = iddao.findAllByEventCRFId(ecb.getId());
      if (!idata.isEmpty()) {
	    //consider an event crf started only if item data get created
        answer.add(dec);
      }
    }

    return answer;
  }

  /**
   * Generate a list of DisplayEventCRFBean objects for a study event. Some of
   * the DisplayEventCRFBeans will represent uncompleted Event CRFs; others will
   * represent Event CRFs which are in initial data entry, have completed
   * initial data entry, are in double data entry, or have completed double data
   * entry.
   * 
   * The list is sorted using the DisplayEventCRFBean's compareTo method (that
   * is, using the event definition crf bean's ordinal value.) Also, the
   * setFlags method of each DisplayEventCRFBean object will have been called
   * once.
   * 
   * @param studyEvent
   *          The study event for which we want the Event CRFs.
   * @param ecdao
   *          An EventCRFDAO from which to grab the study event's Event CRFs.
   * @param edcdao
   *          An EventDefinitionCRFDAO from which to grab the Event CRF
   *          Definitions which apply to the study event.
   * @return A list of DisplayEventCRFBean objects releated to the study event,
   *         ordered by the EventDefinitionCRF ordinal property, and with flags
   *         already set.
   */
/*  public static ArrayList getDisplayEventCRFs(StudyEventBean studyEvent, EventCRFDAO ecdao,
      EventDefinitionCRFDAO edcdao, CRFVersionDAO crfvdao, UserAccountBean user,
      StudyUserRoleBean surb) {
    ArrayList answer = new ArrayList();
    HashMap indexByCRFId = new HashMap();

    ArrayList eventCRFs = ecdao.findAllByStudyEvent(studyEvent);
    ArrayList eventDefinitionCRFs = edcdao.findAllByEventDefinitionId(studyEvent
        .getStudyEventDefinitionId());

    // TODO: map this out to another function
    ArrayList crfVersions = (ArrayList) crfvdao.findAll();
    HashMap crfIdByCRFVersionId = new HashMap();
    for (int i = 0; i < crfVersions.size(); i++) {
      CRFVersionBean cvb = (CRFVersionBean) crfVersions.get(i);
      crfIdByCRFVersionId.put(new Integer(cvb.getId()), new Integer(cvb.getCrfId()));
    }

    // put the event definition crfs inside DisplayEventCRFs
    for (int i = 0; i < eventDefinitionCRFs.size(); i++) {
      EventDefinitionCRFBean edcb = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
      DisplayEventCRFBean decb = new DisplayEventCRFBean();
      decb.setEventDefinitionCRF(edcb);

      answer.add(decb);
      indexByCRFId.put(new Integer(edcb.getCrfId()), new Integer(answer.size() - 1));
    }

    // attach EventCRFs to the DisplayEventCRFs
    for (int i = 0; i < eventCRFs.size(); i++) {
      EventCRFBean ecb = (EventCRFBean) eventCRFs.get(i);

      Integer crfVersionId = new Integer(ecb.getCRFVersionId());
      if (crfIdByCRFVersionId.containsKey(crfVersionId)) {
        Integer crfId = (Integer) crfIdByCRFVersionId.get(crfVersionId);

        if ((crfId != null) && indexByCRFId.containsKey(crfId)) {
          Integer indexObj = (Integer) indexByCRFId.get(crfId);

          if (indexObj != null) {
            int index = indexObj.intValue();
            if ((index > 0) && (index < answer.size())) {
              DisplayEventCRFBean decb = (DisplayEventCRFBean) answer.get(index);
              decb.setEventCRF(ecb);
              answer.set(index, decb);
            }
          }
        }
      }
    }

    for (int i = 0; i < answer.size(); i++) {
      DisplayEventCRFBean decb = (DisplayEventCRFBean) answer.get(i);
      decb.setFlags(decb.getEventCRF(), user, surb, decb.getEventDefinitionCRF().isDoubleEntry());
      answer.set(i, decb);
    }

    // TODO: attach crf versions to the DisplayEventCRFs

    return answer;
  }
*/
}