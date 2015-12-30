/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.submit.AddNewSubjectServlet;
import org.akaza.openclinica.core.form.DiscrepancyValidator;
import org.akaza.openclinica.core.form.FormDiscrepancyNotes;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * @author jxu
 *
 * Performs updating study event action
 */
public class UpdateStudyEventServlet extends SecureController {
    public static final String EVENT_ID = "event_id";

    public static final String STUDY_SUBJECT_ID = "ss_id";

    public static final String EVENT_BEAN = "event";

    public static final String EVENT_DEFINITION_BEAN = "eventDefinition";

    public static final String SUBJECT_EVENT_STATUS_ID = "statusId";

    public static final String INPUT_STARTDATE_PREFIX = "start";

    // YW, 3-12-2008, for 2220 fix <<
    public static final String INPUT_ENDDATE_PREFIX = "end";
    // YW >>

    public static final String INPUT_LOCATION = "location";

    @Override
    public void mayProceed() throws InsufficientPermissionException {
    }

    @Override
    public void processRequest() throws Exception {
        FormDiscrepancyNotes discNotes = null;
        FormProcessor fp = new FormProcessor(request);
        int studyEventId = fp.getInt(EVENT_ID, true);
        int studySubjectId = fp.getInt(STUDY_SUBJECT_ID, true);

        String module = fp.getString(MODULE);
        request.setAttribute(MODULE, module);

        if (studyEventId == 0 || studySubjectId == 0) {
            addPageMessage(respage.getString("choose_a_study_event_to_edit"));
            request.setAttribute("id", new Integer(studySubjectId).toString());
            forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
            return;
        }

        StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
        StudySubjectBean ssub = null;
        if (studySubjectId > 0) {
            ssub = (StudySubjectBean) ssdao.findByPK(studySubjectId);
            request.setAttribute("studySubject", ssub);
            request.setAttribute("id", studySubjectId + "");// for the workflow
            // box, so it can
            // link back to view
            // study subject
        }

        // YW 11-07-2007, a study event could not be updated if its study
        // subject has been removed
        // Status s = ((StudySubjectBean)new
        // StudySubjectDAO(sm.getDataSource()).findByPK(studySubjectId)).getStatus();
        Status s = ssub.getStatus();
        if ("removed".equalsIgnoreCase(s.getName()) || "auto-removed".equalsIgnoreCase(s.getName())) {
            addPageMessage(resword.getString("study_event") + resterm.getString("could_not_be") + resterm.getString("updated") + "."
                + respage.getString("study_subject_has_been_deleted"));
            request.setAttribute("id", new Integer(studySubjectId).toString());
            forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
        }
        // YW

        request.setAttribute(STUDY_SUBJECT_ID, new Integer(studySubjectId).toString());
        StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
        EventCRFDAO ecrfdao = new EventCRFDAO(sm.getDataSource());

        StudyEventBean studyEvent = (StudyEventBean) sedao.findByPK(studyEventId);
        studyEvent.setEventCRFs(ecrfdao.findAllByStudyEvent(studyEvent));

        // only owner, admins, and study director/coordinator can update
        // if (ub.getId() != studyEvent.getOwnerId()) {
        // if (!ub.isSysAdmin() &&
        // !currentRole.getRole().equals(Role.STUDYDIRECTOR)
        // && !currentRole.getRole().equals(Role.COORDINATOR)) {
        // addPageMessage(respage.getString("no_have_correct_privilege_current_study")
        // + respage.getString("change_study_contact_sysadmin"));
        // request.setAttribute("id", new Integer(studySubjectId).toString());
        // forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
        // return;
        // }
        // }
        // above removed tbh 11162007

        ArrayList statuses = SubjectEventStatus.toArrayList();
        // remove more statuses here, tbh, 092007
        // ### updates to status setting, below added tbh 102007
        // following pieces of logic to be added:
        /*
         * REMOVED can happen at any step, COMPLETED can happen if the Subject
         * Event is already complete, COMPLETED can also happen if all required
         * CRFs in the Subject Event are completed, LOCKED can occur when all
         * Event CRFs are completed, or not started, or removed, LOCKED/REMOVED
         * are only options, however, when the user is study director or study
         * coordinator SKIPPED/STOPPED?
         *
         * Additional rules spelled out on Nov 16 2007: STOPPED should only be
         * in the list of choices after IDE has been started, i.e. not when
         * SCHEDULED SKIPPED should only be in the list before IDE has been
         * started, i.e. when SCHEDULED reminder about LOCKED happening only
         * when CRFs are completed (not as in the above...) if a status is
         * LOCKED already, it should allow a user to set the event back to
         * COMPLETED
         */

        if (!studyEvent.getSubjectEventStatus().equals(SubjectEventStatus.DATA_ENTRY_STARTED)) {
            statuses.remove(SubjectEventStatus.DATA_ENTRY_STARTED);
            statuses.remove(SubjectEventStatus.SKIPPED);
            // per new rule 11-2007
        }
        if (!studyEvent.getSubjectEventStatus().equals(SubjectEventStatus.NOT_SCHEDULED)) {
            statuses.remove(SubjectEventStatus.NOT_SCHEDULED);
        }
        if (!studyEvent.getSubjectEventStatus().equals(SubjectEventStatus.SCHEDULED)) {
            // can't lock a non-completed CRF, but removed above
            statuses.remove(SubjectEventStatus.SCHEDULED);
            statuses.remove(SubjectEventStatus.SKIPPED);
            // addl rule: skipped should only be present before data starts
            // being entered
        }

        ArrayList getECRFs = studyEvent.getEventCRFs();
        // above removed tbh 102007, require to get all definitions, no matter
        // if they are filled in or now
        EventDefinitionCRFDAO edefcrfdao = new EventDefinitionCRFDAO(sm.getDataSource());
        ArrayList getAllECRFs = (ArrayList) edefcrfdao.findAllByDefinition(studyEvent.getStudyEventDefinitionId());
        // does the study event have all complete CRFs which are required?
        logger.info("found number of ecrfs: " + getAllECRFs.size());
        // may not be populated, only entered crfs seem to ping the list
        for (int u = 0; u < getAllECRFs.size(); u++) {
            EventDefinitionCRFBean ecrfBean = (EventDefinitionCRFBean) getAllECRFs.get(u);

            //
            logger.info("found number of existing ecrfs: " + getECRFs.size());
            if (getECRFs.size() == 0) {
                statuses.remove(SubjectEventStatus.COMPLETED);
                statuses.remove(SubjectEventStatus.LOCKED);

            }// otherwise...
            for (int uv = 0; uv < getECRFs.size(); uv++) {
                EventCRFBean existingBean = (EventCRFBean) getECRFs.get(uv);
                logger.info("***** found: " + existingBean.getCRFVersionId() + " " + existingBean.getCrf().getId() + " "
                    + existingBean.getCrfVersion().getName() + " " + existingBean.getStatus().getName() + " " + existingBean.getStage().getName());

                logger.info("***** comparing above to ecrfBean.DefaultVersionID: " + ecrfBean.getDefaultVersionId());

                // if (existingBean.getCRFVersionId() ==
                // ecrfBean.getDefaultVersionId()) {
                // OK. this only works if we go ahead and remove the drop down
                // will this match up? Do we need to pull it out of
                // studyEvent.getEventCRFs()?
                // only case that this will screw up is if there are no crfs
                // whatsoever
                // this is addressed in the if-clause above
                if (!existingBean.getStatus().equals(Status.UNAVAILABLE)
                    && edefcrfdao.isRequiredInDefinition(existingBean.getCRFVersionId(), studyEvent.getId())) {

                    logger.info("found that " + existingBean.getCrfVersion().getName() + " is required...");
                    // that is, it's not completed but required to complete
                    statuses.remove(SubjectEventStatus.COMPLETED);
                    statuses.remove(SubjectEventStatus.LOCKED);
                    // per new rule above 11-16-2007
                }
                // }
            }
        }
        // below added 092007, tbh, task #1390
        if (!ub.isSysAdmin() && !currentRole.getRole().equals(Role.STUDYDIRECTOR) && !currentRole.getRole().equals(Role.COORDINATOR)) {
            statuses.remove(SubjectEventStatus.LOCKED);
        }

        // also, if data entry is started, can't move back to scheduled or not
        // scheduled
        if (studyEvent.getSubjectEventStatus().equals(SubjectEventStatus.DATA_ENTRY_STARTED)) {
            statuses.remove(SubjectEventStatus.NOT_SCHEDULED);
            statuses.remove(SubjectEventStatus.SCHEDULED);
        }

        // ### tbh, above modified 102007
        request.setAttribute("statuses", statuses);

        String action = fp.getString("action");
        StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
        StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(studyEvent.getStudyEventDefinitionId());
        request.setAttribute(EVENT_DEFINITION_BEAN, sed);
        if (action.equalsIgnoreCase("submit")) {
            discNotes = (FormDiscrepancyNotes) session.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
            DiscrepancyValidator v = new DiscrepancyValidator(request, discNotes);
            SubjectEventStatus ses = SubjectEventStatus.get(fp.getInt(SUBJECT_EVENT_STATUS_ID));
            studyEvent.setSubjectEventStatus(ses);
            if (ses.equals(SubjectEventStatus.SKIPPED) || ses.equals(SubjectEventStatus.STOPPED)) {
                studyEvent.setStatus(Status.UNAVAILABLE);
                EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
                ArrayList eventCRFs = ecdao.findAllByStudyEvent(studyEvent);
                for (int i = 0; i < eventCRFs.size(); i++) {
                    EventCRFBean ecb = (EventCRFBean) eventCRFs.get(i);
                    ecb.setStatus(Status.UNAVAILABLE);
                    ecb.setUpdater(ub);
                    ecb.setUpdatedDate(new Date());
                    ecdao.update(ecb);
                }
            }
            // YW 3-12-2008, 2220 fix
            String strEnd = fp.getDateTimeInputString(INPUT_ENDDATE_PREFIX);
            String strEndScheduled = fp.getDateTimeInputString(INPUT_ENDDATE_PREFIX);
            Date start = fp.getDateTime(INPUT_STARTDATE_PREFIX);
            Date end = null;
            v.addValidation(INPUT_STARTDATE_PREFIX, Validator.IS_DATE_TIME);
            v.alwaysExecuteLastValidation(INPUT_STARTDATE_PREFIX);
            if (!strEndScheduled.equals("")) {
                v.addValidation(INPUT_ENDDATE_PREFIX, Validator.IS_DATE_TIME);
                v.alwaysExecuteLastValidation(INPUT_ENDDATE_PREFIX);
            }
            v.addValidation(INPUT_LOCATION, Validator.NO_BLANKS);
            HashMap errors = v.validate();
            // YW, 3-12-2008, 2220 fix <<
            if (!strEnd.equals("") && !errors.containsKey(INPUT_STARTDATE_PREFIX) && !errors.containsKey(INPUT_ENDDATE_PREFIX)) {
                end = fp.getDateTime(INPUT_ENDDATE_PREFIX);
                if (!fp.getString(INPUT_STARTDATE_PREFIX + "Date").equals(fp.getString(INPUT_ENDDATE_PREFIX + "Date"))) {
                    if (end.before(start)) {
                        v.addError(errors, INPUT_ENDDATE_PREFIX, resexception.getString("input_provided_not_occure_after_previous_start_date_time"));
                    }
                } else {
                    // if in same date, only check when both had time entered
                    if (fp.timeEntered(INPUT_STARTDATE_PREFIX) && fp.timeEntered(INPUT_ENDDATE_PREFIX)) {
                        if (end.before(start) || end.equals(start)) {
                            v.addError(errors, INPUT_ENDDATE_PREFIX, resexception.getString("input_provided_not_occure_after_previous_start_date_time"));
                        }
                    }
                }
            }
            // YW >>

            if (!errors.isEmpty()) {
                setInputMessages(errors);
                String prefixes[] = { INPUT_STARTDATE_PREFIX, INPUT_ENDDATE_PREFIX };
                fp.setCurrentDateTimeValuesAsPreset(prefixes);
                setPresetValues(fp.getPresetValues());

                studyEvent.setLocation(fp.getString(INPUT_LOCATION));

                request.setAttribute("changeDate", fp.getString("changeDate"));
                request.setAttribute(EVENT_BEAN, studyEvent);
                forwardPage(Page.UPDATE_STUDY_EVENT);
                // return;
            } else {
                logger.info("no validation error");
                // YW 08-17-2007 << update start_time_flag column
                if (fp.getString(INPUT_STARTDATE_PREFIX + "Hour").equals("-1") && fp.getString(INPUT_STARTDATE_PREFIX + "Minute").equals("-1")
                    && fp.getString(INPUT_STARTDATE_PREFIX + "Half").equals("")) {
                    studyEvent.setStartTimeFlag(false);
                } else {
                    studyEvent.setStartTimeFlag(true);
                }
                // YW >>
                studyEvent.setDateStarted(start);
                // YW, 3-12-2008, 2220 fix which adding End datetime <<
                if (!strEnd.equals("")) {
                    studyEvent.setDateEnded(end);
                    if (fp.getString(INPUT_ENDDATE_PREFIX + "Hour").equals("-1") && fp.getString(INPUT_ENDDATE_PREFIX + "Minute").equals("-1")
                        && fp.getString(INPUT_ENDDATE_PREFIX + "Half").equals("")) {
                        studyEvent.setEndTimeFlag(false);
                    } else {
                        studyEvent.setEndTimeFlag(true);
                    }
                }
                // YW >>
                studyEvent.setLocation(fp.getString(INPUT_LOCATION));

                logger.info("update study event...");
                studyEvent.setUpdater(ub);
                studyEvent.setUpdatedDate(new Date());
                sedao.update(studyEvent);

                // save discrepancy notes into DB
                FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) session.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
                DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());

                AddNewSubjectServlet.saveFieldNotes(INPUT_LOCATION, fdn, dndao, studyEvent.getId(), "studyEvent", currentStudy);
                AddNewSubjectServlet.saveFieldNotes(INPUT_STARTDATE_PREFIX, fdn, dndao, studyEvent.getId(), "studyEvent", currentStudy);
                AddNewSubjectServlet.saveFieldNotes(INPUT_ENDDATE_PREFIX, fdn, dndao, studyEvent.getId(), "studyEvent", currentStudy);

                addPageMessage(respage.getString("study_event_updated"));
                request.setAttribute("id", new Integer(studySubjectId).toString());
                session.removeAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
                forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
            }
        } else {
            logger.info("no action, go to update page");

            HashMap presetValues = new HashMap();
            // YW 08-17-2007 <<
            if (studyEvent.getStartTimeFlag() == true) {
                Calendar c = new GregorianCalendar();
                c.setTime(studyEvent.getDateStarted());
                presetValues.put(INPUT_STARTDATE_PREFIX + "Hour", new Integer(c.get(Calendar.HOUR)));
                presetValues.put(INPUT_STARTDATE_PREFIX + "Minute", new Integer(c.get(Calendar.MINUTE)));
                // Later it could be put to somewhere as a static method if
                // necessary.
                switch (c.get(Calendar.AM_PM)) {
                case 0:
                    presetValues.put(INPUT_STARTDATE_PREFIX + "Half", "am");
                    break;
                case 1:
                    presetValues.put(INPUT_STARTDATE_PREFIX + "Half", "pm");
                    break;
                default:
                    presetValues.put(INPUT_STARTDATE_PREFIX + "Half", "");
                    break;
                }
            } else {
                presetValues.put(INPUT_STARTDATE_PREFIX + "Hour", new Integer(-1));
                presetValues.put(INPUT_STARTDATE_PREFIX + "Minute", new Integer(-1));
                presetValues.put(INPUT_STARTDATE_PREFIX + "Half", "");
            }
            // YW >>

            String dateValue = local_df.format(studyEvent.getDateStarted());
            presetValues.put(INPUT_STARTDATE_PREFIX + "Date", dateValue);

            // YW 3-12-2008, add end datetime for 2220 fix<<
            presetValues.put(INPUT_ENDDATE_PREFIX + "Hour", new Integer(-1));
            presetValues.put(INPUT_ENDDATE_PREFIX + "Minute", new Integer(-1));
            presetValues.put(INPUT_ENDDATE_PREFIX + "Half", "");
            if (studyEvent.getDateEnded() != null) {
                if (studyEvent.getEndTimeFlag() == true) {
                    Calendar c = new GregorianCalendar();
                    c.setTime(studyEvent.getDateEnded());
                    presetValues.put(INPUT_ENDDATE_PREFIX + "Hour", new Integer(c.get(Calendar.HOUR)));
                    presetValues.put(INPUT_ENDDATE_PREFIX + "Minute", new Integer(c.get(Calendar.MINUTE)));
                    // Later it could be put to somewhere as a static method if
                    // necessary.
                    switch (c.get(Calendar.AM_PM)) {
                    case 0:
                        presetValues.put(INPUT_ENDDATE_PREFIX + "Half", "am");
                        break;
                    case 1:
                        presetValues.put(INPUT_ENDDATE_PREFIX + "Half", "pm");
                        break;
                    default:
                        presetValues.put(INPUT_ENDDATE_PREFIX + "Half", "");
                        break;
                    }
                }
                presetValues.put(INPUT_ENDDATE_PREFIX + "Date", local_df.format(studyEvent.getDateEnded()));
            }
            // YW >>

            setPresetValues(presetValues);

            request.setAttribute(EVENT_BEAN, studyEvent);
            discNotes = new FormDiscrepancyNotes();
            session.setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);

            forwardPage(Page.UPDATE_STUDY_EVENT);
        }// else

    }

    @Override
    protected String getAdminServlet() {
        if (ub.isSysAdmin()) {
            return SecureController.ADMIN_SERVLET_CODE;
        } else {
            return "";
        }
    }

}
