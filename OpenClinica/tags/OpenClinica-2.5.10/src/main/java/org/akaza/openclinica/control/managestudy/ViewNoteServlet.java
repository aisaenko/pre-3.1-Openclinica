/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.submit.SubmitDataServlet;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * @author jxu
 *
 * View a single discrepancy note from note list page
 */
public class ViewNoteServlet extends SecureController {

    public static final String NOTE_ID = "id";

    public static final String DIS_NOTE = "singleNote";

    /*
     * (non-Javadoc)
     *
     * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
     */
    @Override
    protected void mayProceed() throws InsufficientPermissionException {
        /*if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
            return;
        }*/
        if (SubmitDataServlet.mayViewData(ub, currentRole)) {
            return;
        }

        addPageMessage(respage.getString("no_permission_to_view_discrepancies") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director_or_study_cordinator"), "1");

    }

    @Override
    protected void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);

        Locale locale = request.getLocale();
        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);

        DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());
        dndao.setFetchMapping(true);
        int noteId = fp.getInt(NOTE_ID, true);

        DiscrepancyNoteBean note = (DiscrepancyNoteBean) dndao.findByPK(noteId);

        String entityType = note.getEntityType();

        if (note.getEntityId() > 0 && !entityType.equals("")) {

            if (!StringUtil.isBlank(entityType)) {
                if ("itemData".equalsIgnoreCase(entityType)) {
                    ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
                    ItemDataBean itemData = (ItemDataBean) iddao.findByPK(note.getEntityId());

                    ItemDAO idao = new ItemDAO(sm.getDataSource());
                    ItemBean item = (ItemBean) idao.findByPK(itemData.getItemId());

                    note.setEntityValue(itemData.getValue());
                    note.setEntityName(item.getName());

                    EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
                    EventCRFBean ec = (EventCRFBean) ecdao.findByPK(itemData.getEventCRFId());

                    StudyEventDAO sed = new StudyEventDAO(sm.getDataSource());
                    StudyEventBean se = (StudyEventBean) sed.findByPK(ec.getStudyEventId());

                    StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
                    StudySubjectBean ssub = (StudySubjectBean) ssdao.findByPK(se.getStudySubjectId());

                    note.setStudySub(ssub);

                    StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
                    StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) seddao.findByPK(se.getStudyEventDefinitionId());

                    se.setName(sedb.getName());
                    note.setEvent(se);

                    CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
                    CRFVersionBean cv = (CRFVersionBean) cvdao.findByPK(ec.getCRFVersionId());

                    CRFDAO cdao = new CRFDAO(sm.getDataSource());
                    CRFBean crf = (CRFBean) cdao.findByPK(cv.getCrfId());
                    note.setCrfName(crf.getName());

                } else if ("studySub".equalsIgnoreCase(entityType)) {
                    StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
                    StudySubjectBean ssub = (StudySubjectBean) ssdao.findByPK(note.getEntityId());

                    note.setStudySub(ssub);
                    //System.out.println("column" + note.getColumn());
                    SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
                    SubjectBean sub = (SubjectBean) sdao.findByPK(ssub.getSubjectId());

                    if (!StringUtil.isBlank(note.getColumn())) {
                        if ("enrollment_date".equalsIgnoreCase(note.getColumn())) {
                            if (ssub.getEnrollmentDate() != null) {
                                note.setEntityValue(dateFormatter.format(ssub.getEnrollmentDate()));
                            }
                            note.setEntityName(resword.getString("enrollment_date"));
                        } else if ("gender".equalsIgnoreCase(note.getColumn())) {
                            note.setEntityValue(sub.getGender() + "");
                            note.setEntityName(resword.getString("gender"));
                        } else if ("date_of_birth".equalsIgnoreCase(note.getColumn())) {
                            if (sub.getDateOfBirth() != null) {
                                note.setEntityValue(dateFormatter.format(sub.getDateOfBirth()));
                            }
                            note.setEntityName(resword.getString("date_of_birth"));
                        }
                    }

                } else if ("subject".equalsIgnoreCase(entityType)) {

                    SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
                    SubjectBean sub = (SubjectBean) sdao.findByPK(note.getEntityId());

                    if (!StringUtil.isBlank(note.getColumn())) {
                        if ("gender".equalsIgnoreCase(note.getColumn())) {
                            note.setEntityValue(sub.getGender() + "");
                            note.setEntityName(resword.getString("gender"));
                        } else if ("date_of_birth".equalsIgnoreCase(note.getColumn())) {
                            if (sub.getDateOfBirth() != null) {
                                note.setEntityValue(dateFormatter.format(sub.getDateOfBirth()));
                            }
                            note.setEntityName(resword.getString("date_of_birth"));
                        }
                    }

                } else if ("studyEvent".equalsIgnoreCase(entityType)) {

                    StudyEventDAO sed = new StudyEventDAO(sm.getDataSource());
                    StudyEventBean se = (StudyEventBean) sed.findByPK(note.getEntityId());

                    StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
                    StudySubjectBean ssub = (StudySubjectBean) ssdao.findByPK(note.getEntityId());

                    note.setStudySub(ssub);

                    StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
                    StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) seddao.findByPK(se.getStudyEventDefinitionId());

                    se.setName(sedb.getName());
                    note.setEvent(se);

                    if (!StringUtil.isBlank(note.getColumn())) {
                        if ("location".equalsIgnoreCase(note.getColumn())) {
                            request.setAttribute("entityValue", se.getLocation());
                            request.setAttribute("entityName", resword.getString("location"));
                        } else if ("date_start".equalsIgnoreCase(note.getColumn())) {
                            if (se.getDateStarted() != null) {
                                note.setEntityValue(dateFormatter.format(se.getDateStarted()));
                            }
                            note.setEntityName(resword.getString("start_date"));

                        } else if ("date_end".equalsIgnoreCase(note.getColumn())) {
                            if (se.getDateEnded() != null) {
                                note.setEntityValue(dateFormatter.format(se.getDateEnded()));
                            }
                            note.setEntityName(resword.getString("end_date"));

                        }
                    }

                } else if ("eventCrf".equalsIgnoreCase(entityType)) {
                    EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
                    EventCRFBean ec = (EventCRFBean) ecdao.findByPK(note.getEntityId());
                    if (!StringUtil.isBlank(note.getColumn())) {
                        if ("date_interviewed".equals(note.getColumn())) {
                            if (ec.getDateInterviewed() != null) {
                                note.setEntityValue(dateFormatter.format(ec.getDateInterviewed()));
                            }
                            note.setEntityName(resword.getString("date_interviewed"));
                        } else if ("interviewer_name".equals(note.getColumn())) {
                            note.setEntityValue(ec.getInterviewerName());
                            note.setEntityName(resword.getString("interviewer_name"));
                        }
                    }

                }

            }

        }
        UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());

        ArrayList notes = dndao.findAllEntityByPK(note.getEntityType(), noteId);

        Date lastUpdatedDate = note.getCreatedDate();
        UserAccountBean lastUpdator = (UserAccountBean) udao.findByPK(note.getOwnerId());

        /*for (int i = 0; i < notes.size(); i++) {
            DiscrepancyNoteBean n = (DiscrepancyNoteBean) notes.get(i);
            int pId = n.getParentDnId();
            if (pId == 0) {
                note = n;
                note.setLastUpdator((UserAccountBean) udao.findByPK(n.getOwnerId()));
                note.setLastDateUpdated(n.getCreatedDate());
                lastUpdatedDate = note.getLastDateUpdated();
                lastUpdator = note.getLastUpdator();
            }
        }*/

        for (int i = 0; i < notes.size(); i++) {
            DiscrepancyNoteBean n = (DiscrepancyNoteBean) notes.get(i);
            int pId = n.getParentDnId();
            if (pId > 0) {
                note.getChildren().add(n);
                if (!n.getCreatedDate().before(lastUpdatedDate)) {
                    lastUpdatedDate = n.getCreatedDate();
                    lastUpdator = (UserAccountBean) udao.findByPK(n.getOwnerId());
                    note.setLastUpdator(lastUpdator);
                    note.setLastDateUpdated(lastUpdatedDate);
                    //note.setResolutionStatusId(n.getResolutionStatusId());
                    //note.setResStatus(ResolutionStatus.get(n.getResolutionStatusId()));
                }
            }
        }
        note.setNumChildren(note.getChildren().size());
        note.setDisType(DiscrepancyNoteType.get(note.getDiscrepancyNoteTypeId()));
        logger.info("Just set Note: " + note.getCrfName() + " column " + note.getColumn());
        request.setAttribute(DIS_NOTE, note);
        forwardPage(Page.VIEW_SINGLE_NOTE);
    }

}
