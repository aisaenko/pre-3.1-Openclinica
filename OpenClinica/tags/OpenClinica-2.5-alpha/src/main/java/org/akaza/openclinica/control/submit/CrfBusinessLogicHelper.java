package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

/*
 * Helper methods will be placed in this class - DRY
 */
public class CrfBusinessLogicHelper {

    SessionManager sm;
    protected final Logger logger = Logger.getLogger(getClass().getName());

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

    /**
     * The following methods are for 'mark CRF complete'
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

        // request.setAttribute(TableOfContentsServlet.INPUT_EVENT_CRF_BEAN,
        // ecb);
        // request.setAttribute(INPUT_EVENT_CRF_ID, new Integer(ecb.getId()));
        logger.info("inout_event_crf_id:" + ecb.getId());
        // Page errorPage = getJSPPage();

        if (stage.equals(DataEntryStage.UNCOMPLETED) || stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE) || stage.equals(DataEntryStage.LOCKED)) {
            logger.info("addPageMessage(respage.getString(\"not_mark_CRF_complete1\"))");
            return false;
        }

        if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE) || stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {

            /*
             * if (!edcb.isDoubleEntry()) {
             * logger.info("addPageMessage(respage.getString(\"not_mark_CRF_complete2\"))");
             * return false; }
             * 
             */
        }

        /*
         * if (!isEachSectionReviewedOnce()) { addPageMessage("You may not mark
         * this Event CRF complete, because there are some sections which have
         * not been reviewed once."); return false; }
         */

        if (!isEachRequiredFieldFillout(ecb)) {
            logger.info("addPageMessage(respage.getString(\"not_mark_CRF_complete4\"))");
            return false;
        }

        /*
         * if (ecb.getInterviewerName().trim().equals("")) { throw new
         * InconsistentStateException(errorPage, "You may not mark this Event
         * CRF complete, because the interviewer name is blank."); }
         */

        Status newStatus = ecb.getStatus();
        boolean ide = true;
        // currently we are setting it all to complete, so this block is all to
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
        // } else if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE)
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
        ecb = (EventCRFBean) eventCrfDao.update(ecb);
        // note the below statement only updates the DATES, not the STATUS
        eventCrfDao.markComplete(ecb, ide);

        // update all the items' status to complete
        itemDataDao.updateStatusByEventCRF(ecb, newStatus);

        // change status for study event
        StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
        StudyEventBean seb = (StudyEventBean) sedao.findByPK(ecb.getStudyEventId());
        seb.setUpdatedDate(new Date());
        seb.setUpdater(ub);

        EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
        ArrayList allCRFs = eventCrfDao.findAllByStudyEvent(seb);
        ArrayList allEDCs = edcdao.findAllActiveByEventDefinitionId(seb.getStudyEventDefinitionId());
        boolean eventCompleted = true;
        boolean allRequired = true;
        int allEDCsize = allEDCs.size();
        ArrayList nonRequiredCrfIds = new ArrayList();
        // go through the list and find out if all are required, tbh
        for (int ii = 0; ii < allEDCs.size(); ii++) {
            EventDefinitionCRFBean edcBean = (EventDefinitionCRFBean) allEDCs.get(ii);
            if (!edcBean.isRequiredCRF()) {
                logger.info("found one non required CRF: " + edcBean.getCrfName() + " " + edcBean.getCrfId() + " " + edcBean.getDefaultVersionName());
                allRequired = false;
                nonRequiredCrfIds.add(new Integer(edcBean.getCrfId()));
                allEDCsize--;
            }
        }
        logger.info("non required crf ids: " + nonRequiredCrfIds.toString());
        // go through all the crfs and check their status
        // add an additional check to see if it is required or not, tbh
        for (int i = 0; i < allCRFs.size(); i++) {
            EventCRFBean ec = (EventCRFBean) allCRFs.get(i);
            logger.info("-- looking at a CRF: " + ec.getName() + " " + ec.getCrf().getName() + " " + ec.getCrf().getId());
            // if clause kind of not right since none of the above fields are
            // set in the dao, tbh
            if (!ec.getStatus().equals(Status.UNAVAILABLE) && ec.getDateInterviewed() != null) { // &&
                // (!nonRequiredCrfIds.contains(new
                // Integer(ec.getCrf().getId())))) {
                eventCompleted = false;
                logger.info("just rejected eventCompleted looking at a CRF: " + ec.getName());
                break;
            }
        }

        if (!allRequired) {
            logger.info("SEB contains some nonrequired CRFs: " + allEDCsize + " vs " + allEDCs.size());
        }

        if (eventCompleted && allCRFs.size() >= allEDCsize) {// was
            // allEDCs.size(),
            // tbh
            if (!allRequired) {
                /*
                 * addPageMessage("All Required CRFs have been completed. "+
                 * "You can update the Study Event's status to 'complete'"+ " by
                 * following the 'Edit Study Event' link.");
                 */
            } else {
                logger.info("just set subj event status to -- COMPLETED --");
                seb.setSubjectEventStatus(SubjectEventStatus.COMPLETED);
            }
        }

        seb = (StudyEventBean) sedao.update(seb);

        return true;
    }
}
