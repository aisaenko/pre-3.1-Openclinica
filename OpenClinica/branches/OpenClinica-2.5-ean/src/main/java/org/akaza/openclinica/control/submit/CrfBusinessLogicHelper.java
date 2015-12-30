package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;

import java.util.ArrayList;
import java.util.Date;

/*
 * Helper methods will be placed in this class - DRY
 */
public class CrfBusinessLogicHelper {

    SessionManager sm;
    protected final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(getClass().getName());

    public CrfBusinessLogicHelper(SessionManager sm) {
        this.sm = sm;
    }

    private EventDefinitionCRFBean getEventDefinitionCrfByStudyEventAndCrfVersion(EventCRFBean eventCrf) {

        EventDefinitionCRFDAO eventDefinitionCrfDao = new EventDefinitionCRFDAO(sm.getDataSource());
        // TODO we have to get that id before we can continue
        eventDefinitionCrfDao = new EventDefinitionCRFDAO(sm.getDataSource());
        EventDefinitionCRFBean eventDefinitionCrf =
            eventDefinitionCrfDao.findByStudyEventIdAndCRFVersionId(eventCrf.getStudyEventId(), eventCrf.getCRFVersionId());
        return eventDefinitionCrf;
    }

    protected boolean isEachRequiredFieldFillout(EventCRFBean ecb) {
        ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
        ItemDAO idao = new ItemDAO(sm.getDataSource());
        int allRequiredNum = idao.findAllRequiredByCRFVersionId(ecb.getCRFVersionId());
        int allRequiredFilledOut = iddao.findAllRequiredByEventCRFId(ecb);
        if (allRequiredNum > allRequiredFilledOut) {
            return false;
        }

        ArrayList allFilled = iddao.findAllBlankRequiredByEventCRFId(ecb.getId(), ecb.getCRFVersionId());
        if (!allFilled.isEmpty()) {
            return false;
        }
        return true;
    }

    protected boolean areAllCompleted(StudyEventBean seBean) {
        EventDefinitionCRFDAO edcDao = new EventDefinitionCRFDAO(sm.getDataSource());
        EventCRFDAO eventCrfDao = new EventCRFDAO(sm.getDataSource());
        ArrayList allCRFs = eventCrfDao.findAllByStudyEvent(seBean);
        ArrayList allEDCs = edcDao.findAllActiveByEventDefinitionId(seBean.getStudyEventDefinitionId());
        boolean eventCompleted = true;
        logger.info("found all crfs: " + allCRFs.size());
        logger.info("found all edcs: " + allEDCs.size());
        for (int i = 0; i < allCRFs.size(); i++) {
            EventCRFBean ec = (EventCRFBean) allCRFs.get(i);
            // logger.info("found a crf name from event crf bean: " +
            // ec.getCrf().getName());
            logger.info("found a event name from event crf bean: " + ec.getEventName() + " crf version id: " + ec.getCRFVersionId());
            if (!ec.getStatus().equals(Status.UNAVAILABLE) || allCRFs.size() < allEDCs.size()) {
                eventCompleted = false;
                break;
            }
        }
        logger.info("returning for are all completed: " + eventCompleted);
        return eventCompleted;
    }

    protected boolean areAllRequiredCompleted(StudyEventBean seBean) {
        EventDefinitionCRFDAO edcDao = new EventDefinitionCRFDAO(sm.getDataSource());
        EventCRFDAO eventCrfDao = new EventCRFDAO(sm.getDataSource());
        ArrayList allCRFs = eventCrfDao.findAllByStudyEvent(seBean);
        ArrayList allEDCs = edcDao.findAllActiveByEventDefinitionId(seBean.getStudyEventDefinitionId());
        boolean eventRequiredCompleted = true;
        // keep in mind that allCRFs return only existing CRFs,
        // while allEDCs return all event defs in an event
        for (int i = 0; i < allCRFs.size(); i++) {
            EventCRFBean ec = (EventCRFBean) allCRFs.get(i);
            EventDefinitionCRFBean edcBean = edcDao.findByStudyEventIdAndCRFVersionId(seBean.getId(), ec.getCRFVersionId());
            if (!ec.getStatus().equals(Status.UNAVAILABLE) && edcBean.isRequiredCRF()) {
                // if it's not done but required, return FALSE
                eventRequiredCompleted = false;
                logger.info("it is not done but required, returning FALSE");
                break;
            }
        }
        if (allCRFs.size() < allEDCs.size()) {
            // the above means that we didnt find all the edcs yet
            // iterate through edcs now, if we find a required one return FALSE,
            // since we found one that we didn't catch before
            for (int i = 0; i < allEDCs.size(); i++) {
                EventDefinitionCRFBean ec = (EventDefinitionCRFBean) allEDCs.get(i);
                if (ec.isRequiredCRF() && allCRFsDoesNotContainEventDefinition(allCRFs, seBean, ec)) {
                    eventRequiredCompleted = false;
                    logger.info("fail out at review of EDCs, returning FALSE");
                    break;
                }
            }
        }
        logger.info("returning for event required completed: " + eventRequiredCompleted);
        return eventRequiredCompleted;
    }

    /*
     * checking to see if this does not already contain the event def in the
     * filled-out or completed CRFs, tbh 07/2008
     */
    protected boolean allCRFsDoesNotContainEventDefinition(ArrayList allCRFs, StudyEventBean seBean, EventDefinitionCRFBean ecbean) {
        boolean doesNotContain = true;
        EventDefinitionCRFDAO edcDao = new EventDefinitionCRFDAO(sm.getDataSource());

        for (int i = 0; i < allCRFs.size(); i++) {
            EventCRFBean ec = (EventCRFBean) allCRFs.get(i);
            EventDefinitionCRFBean edcBean = edcDao.findByStudyEventIdAndCRFVersionId(seBean.getId(), ec.getCRFVersionId());
            if (edcBean.getId() == ecbean.getId()) {
                doesNotContain = false;
                break;
            }
        }
        return doesNotContain;
    }

    protected boolean noneAreRequired(StudyEventBean seBean) {
        boolean noneAreRequired = true;
        EventDefinitionCRFDAO edcDao = new EventDefinitionCRFDAO(sm.getDataSource());
        ArrayList allEDCs = edcDao.findAllActiveByEventDefinitionId(seBean.getStudyEventDefinitionId());
        logger.info("found all EDCs: " + allEDCs.size());
        for (int i = 0; i < allEDCs.size(); i++) {
            EventDefinitionCRFBean ec = (EventDefinitionCRFBean) allEDCs.get(i);
            logger.info("found crf name: " + ec.getCrfName());
            if (ec.isRequiredCRF()) {
                noneAreRequired = false;
                break;
            }
        }
        logger.info("returning for none are required: " + noneAreRequired);
        return noneAreRequired;
    }

    protected boolean areAllRequired(StudyEventBean seBean) {
        EventDefinitionCRFDAO edcDao = new EventDefinitionCRFDAO(sm.getDataSource());
        // EventCRFDAO eventCrfDao = new EventCRFDAO(sm.getDataSource());
        // ArrayList allCRFs = eventCrfDao.findAllByStudyEvent(seBean);
        ArrayList allEDCs = edcDao.findAllActiveByEventDefinitionId(seBean.getStudyEventDefinitionId());
        boolean areAllRequired = true;
        for (int i = 0; i < allEDCs.size(); i++) {
            EventDefinitionCRFBean ec = (EventDefinitionCRFBean) allEDCs.get(i);
            if (!ec.isRequiredCRF()) {
                areAllRequired = false;
                break;
            }
        }
        logger.info("returning for are all required: " + areAllRequired);
        return areAllRequired;
    }

    /**
     * The following methods are for 'mark CRF complete' Note that we will also
     * wrap Study Event status changes in this code, possibly split out in a
     * later release, tbh 06/2008
     * 
     * @return
     */
    protected boolean markCRFComplete(EventCRFBean ecb, UserAccountBean ub) throws Exception {
        // locale = request.getLocale();
        // < respage =
        // ResourceBundle.getBundle("org.akaza.openclinica.i18n.page_messages",locale);
        // < restext =
        // ResourceBundle.getBundle("org.akaza.openclinica.i18n.notes",locale);
        // <
        // resexception=ResourceBundle.getBundle("org.akaza.openclinica.i18n.exceptions",locale);
        // getEventCRFBean();
        // getEventDefinitionCRFBean();
        DataEntryStage stage = ecb.getStage();
        EventCRFDAO eventCrfDao = new EventCRFDAO(sm.getDataSource());
        ItemDataDAO itemDataDao = new ItemDataDAO(sm.getDataSource());
        EventDefinitionCRFBean edcb = getEventDefinitionCrfByStudyEventAndCrfVersion(ecb);

        // StudyEventDAO studyEventDao = new StudyEventDAO(sm.getDataSource());
        // StudyEventBean studyEventBean = (StudyEventBean)
        // studyEventDao.findByPK(ecb.getStudyEventId());
        // Status studyEventStatus = studyEventBean.getStatus();

        StudyEventDefinitionDAO studyEventDefinitionDao = new StudyEventDefinitionDAO(sm.getDataSource());
        StudyEventDefinitionBean sedBean = (StudyEventDefinitionBean) studyEventDefinitionDao.findByPK(edcb.getStudyEventDefinitionId());
        CRFDAO crfDao = new CRFDAO(sm.getDataSource());
        ArrayList crfs = (ArrayList) crfDao.findAllActiveByDefinition(sedBean);
        sedBean.setCrfs(crfs);
        // request.setAttribute(TableOfContentsServlet.INPUT_EVENT_CRF_BEAN,
        // ecb);
        // request.setAttribute(INPUT_EVENT_CRF_ID, new
        // Integer(ecb.getId()));
        logger.debug("inout_event_crf_id:" + ecb.getId());
        logger.debug("inout_study_event_def_id:" + sedBean.getId());

        // below bit is from DataEntryServlet, is more appropriate for filling
        // in by hand than by automatic
        // removing this in favor of the more streamlined effect below, tbh
        // 06/2008
        // Page errorPage = getJSPPage();

        // if (stage.equals(DataEntryStage.UNCOMPLETED) ||
        // stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE) ||
        // stage.equals(DataEntryStage.LOCKED)) {
        // logger.info("addPageMessage(respage.getString(\"not_mark_CRF_complete1\"))");
        // return false;
        // }
        //
        // if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE) ||
        // stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
        //
        // /*
        // * if (!edcb.isDoubleEntry()) {
        // *
        // logger.info("addPageMessage(respage.getString(\"not_mark_CRF_complete2\"))");
        // * return false; }
        // *
        // */
        // }
        //
        // /*
        // * if (!isEachSectionReviewedOnce()) { addPageMessage("You may not
        // mark
        // * this Event CRF complete, because there are some sections which have
        // * not been reviewed once."); return false; }
        // */
        //
        // if (!isEachRequiredFieldFillout(ecb)) {
        // logger.info("addPageMessage(respage.getString(\"not_mark_CRF_complete4\"))");
        // return false;
        // }
        //
        // /*
        // * if (ecb.getInterviewerName().trim().equals("")) { throw new
        // * InconsistentStateException(errorPage, "You may not mark this Event
        // * CRF complete, because the interviewer name is blank."); }
        // */

        Status newStatus = ecb.getStatus();
        DataEntryStage newStage = ecb.getStage();
        boolean ide = true;

        // currently we are setting the event crf status to complete, so this
        // block is all to
        // complete, tbh

        // if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY) &&

        // edcb.isDoubleEntry()) {
        // newStatus = Status.PENDING;
        // ecb.setUpdaterId(ub.getId());
        // ecb.setUpdater(ub);
        // ecb.setUpdatedDate(new Date());
        // ecb.setDateCompleted(new Date());
        // } else if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY) &&
        // !edcb.isDoubleEntry()) {
        // newStatus = Status.UNAVAILABLE;
        // ecb.setUpdaterId(ub.getId());
        // ecb.setUpdater(ub);
        // ecb.setUpdatedDate(new Date());
        // ecb.setDateCompleted(new Date());
        // ecb.setDateValidateCompleted(new Date());
        // } else if
        // (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE)
        // || stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
        // newStatus = Status.UNAVAILABLE;
        // ecb.setDateValidateCompleted(new Date());
        // ide = false;
        // }

        newStatus = Status.UNAVAILABLE;
        // ecb.setUpdaterId(ub.getId());
        ecb.setUpdater(ub);
        ecb.setUpdatedDate(new Date());
        ecb.setDateCompleted(new Date());
        ecb.setDateValidateCompleted(new Date());

        /*
         * //for the non-reviewed sections, no item data in DB yet, need to
         * //create them if (!isEachSectionReviewedOnce()) { boolean canSave =
         * saveItemsToMarkComplete(newStatus); if (canSave == false){
         * addPageMessage("You may not mark this Event CRF complete, because
         * there are some required entries which have not been filled out.");
         * return false; } }
         */
        ecb.setStatus(newStatus);
        ecb.setStage(newStage);
        ecb = (EventCRFBean) eventCrfDao.update(ecb);
        logger.debug("just updated event crf id: " + ecb.getId());
        // note the below statement only updates the DATES, not the STATUS
        eventCrfDao.markComplete(ecb, ide);

        // update all the items' status to complete
        itemDataDao.updateStatusByEventCRF(ecb, newStatus);

        // change status for study event
        StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
        StudyEventBean seb = (StudyEventBean) sedao.findByPK(ecb.getStudyEventId());
        seb.setUpdatedDate(new Date());
        seb.setUpdater(ub);

        /*
         * EventDefinitionCRFDAO edcdao = new
         * EventDefinitionCRFDAO(sm.getDataSource()); ArrayList allCRFs =
         * eventCrfDao.findAllByStudyEvent(seb); ArrayList allEDCs =
         * edcdao.findAllActiveByEventDefinitionId(seb.getStudyEventDefinitionId());
         * boolean eventCompleted = true; boolean allRequired = true; int
         * allEDCsize = allEDCs.size(); ArrayList nonRequiredCrfIds = new
         * ArrayList(); // go through the list and find out if all are required,
         * tbh for (int ii = 0; ii < allEDCs.size(); ii++) {
         * EventDefinitionCRFBean edcBean = (EventDefinitionCRFBean)
         * allEDCs.get(ii); if (!edcBean.isRequiredCRF()) { logger.info("found
         * one non required CRF: " + edcBean.getCrfName() + " " +
         * edcBean.getCrfId() + " " + edcBean.getDefaultVersionName());
         * allRequired = false; nonRequiredCrfIds.add(new
         * Integer(edcBean.getCrfId())); allEDCsize--; } } logger.info("non
         * required crf ids: " + nonRequiredCrfIds.toString()); // go through
         * all the crfs and check their status // add an additional check to see
         * if it is required or not, tbh for (int i = 0; i < allCRFs.size();
         * i++) { EventCRFBean ec = (EventCRFBean) allCRFs.get(i);
         * logger.info("-- looking at a CRF: " + ec.getName() + " " +
         * ec.getCrf().getName() + " " + ec.getCrf().getId()); // if clause kind
         * of not right since none of the above fields are // set in the dao,
         * tbh if (!ec.getStatus().equals(Status.UNAVAILABLE) &&
         * ec.getDateInterviewed() != null) { // && //
         * (!nonRequiredCrfIds.contains(new // Integer(ec.getCrf().getId())))) {
         * eventCompleted = false; logger.info("just rejected eventCompleted
         * looking at a CRF: " + ec.getName()); break; } }
         * 
         * if (!allRequired) { logger.info("SEB contains some nonrequired CRFs: " +
         * allEDCsize + " vs " + allEDCs.size()); }
         * 
         * if (eventCompleted && allCRFs.size() >= allEDCsize) {// was //
         * allEDCs.size(), // tbh if (!allRequired) { addPageMessage("All
         * Required CRFs have been completed. "+ "You can update the Study
         * Event's status to 'complete'"+ " by following the 'Edit Study Event'
         * link."); } else { logger.info("just set subj event status to --
         * COMPLETED --");
         * seb.setSubjectEventStatus(SubjectEventStatus.COMPLETED); } }
         */

        // updates with Pauls observation from bug:2488:
        // 1. If there is only one CRF in the event (whether the CRF was
        // required or not), and data was imported for it, the status of the
        // event should be Completed.
        //
        logger.debug("sed bean get crfs get size: " + sedBean.getCrfs().size());
        logger.debug("edcb get crf id: " + edcb.getCrfId() + " version size? " + edcb.getVersions().size());
        logger.debug("ecb get crf id: " + ecb.getCrf().getId());
        logger.debug("ecb get crf version id: " + ecb.getCRFVersionId());

        if (sedBean.getCrfs().size() == 1) { // && edcb.getCrfId() ==
            // ecb.getCrf().getId()) {

            seb.setSubjectEventStatus(SubjectEventStatus.COMPLETED);
            logger.info("just set subj event status to -- COMPLETED --");
        }
        // 2. More than one CRF in the event, all of them being required. If
        // data is imported into only one CRF, the status of the event
        // should be
        // Data Entry Started.
        //
        // removing sedBean.getCrfs().size() > 1 &&
        else if (areAllRequired(seb) && !areAllCompleted(seb)) {

            seb.setSubjectEventStatus(SubjectEventStatus.DATA_ENTRY_STARTED);
            logger.info("just set subj event status to -- DATAENTRYSTARTED --");
        }
        // 3. More than one CRF in the event, one is required, the other is
        // not.
        // If data is imported into the Required CRF, the status of the
        // event
        // should be Completed.
        //
        // 4. More than one CRF in the event, one is required, the other is
        // not.
        // If data is imported into the non-required CRF, the status of the
        // event should be Data Entry Started.
        // tbh -- below case covers both
        // removing sedBean.getCrfs().size() > 1 &&
        else if (!areAllRequired(seb)) {
            if (areAllRequiredCompleted(seb)) {
                seb.setSubjectEventStatus(SubjectEventStatus.COMPLETED);
                logger.info("just set subj event status to -- 3completed3 --");
            } else {
                seb.setSubjectEventStatus(SubjectEventStatus.DATA_ENTRY_STARTED);
                logger.info("just set subj event status to -- DATAENTRYSTARTED --");
            }
        }

        // 5. More than one CRF in the event and none of them are required.
        // If
        // Data is imported into all the CRFs, the status of the event will
        // be
        // Completed. If the data is imported in some of the CRFs, the
        // status
        // will be completed as well.
        // removing sedBean.getCrfs().size() > 1 &&

        else if (noneAreRequired(seb)) {
            seb.setSubjectEventStatus(SubjectEventStatus.COMPLETED);
            logger.info("just set subj event status to -- 5completed5 --");
        }
        logger.debug("just set subj event status, final status is " + seb.getSubjectEventStatus().getName());
        logger.debug("final overall status is " + seb.getStatus().getName());
        seb = (StudyEventBean) sedao.update(seb);

        return true;
    }
}
