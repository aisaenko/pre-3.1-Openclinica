/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.bean.service.StudyParamsConfig;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author jxu
 *
 * @version CVS: $Id: UpdateSubStudyServlet.java,v 1.7 2005/07/05 21:55:58 jxu
 *          Exp $
 */
public class UpdateSubStudyServlet extends SecureController {
    public static final String INPUT_START_DATE = "startDate";

    public static final String INPUT_END_DATE = "endDate";
    public static StudyBean parentStudy;

    /**
     *
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {
        checkStudyLocked(Page.SITE_LIST_SERVLET, respage.getString("current_study_locked"));
        if (ub.isSysAdmin()) {
            return;
        }
        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.STUDY_LIST, resexception.getString("not_study_director"), "1");

    }

    @Override
    public void processRequest() throws Exception {

        StudyDAO sdao = new StudyDAO(sm.getDataSource());
        StudyBean study = (StudyBean) session.getAttribute("newStudy");

        parentStudy = (StudyBean)sdao.findByPK(study.getParentStudyId());

        logger.info("study from session:" + study.getName() + "\n" + study.getCreatedDate() + "\n");
        String action = request.getParameter("action");

        if (StringUtil.isBlank(action)) {
            request.setAttribute("facRecruitStatusMap", CreateStudyServlet.facRecruitStatusMap);
            request.setAttribute("statuses", Status.toStudyUpdateMembersList());
            FormProcessor fp = new FormProcessor(request);
            logger.info("start date:" + study.getDatePlannedEnd());
            if (study.getDatePlannedEnd() != null) {
                fp.addPresetValue(INPUT_END_DATE, local_df.format(study.getDatePlannedEnd()));
            }
            if (study.getDatePlannedStart() != null) {
                fp.addPresetValue(INPUT_START_DATE, local_df.format(study.getDatePlannedStart()));
            }

            setPresetValues(fp.getPresetValues());
            forwardPage(Page.UPDATE_SUB_STUDY);
        } else {
            if ("confirm".equalsIgnoreCase(action)) {
                confirmStudy();

            } else if ("submit".equalsIgnoreCase(action)) {
                submitStudy();

            }
        }
    }

    /**
     * Validates the first section of study and save it into study bean
     *
     * @param request
     * @param response
     * @throws Exception
     */
    private void confirmStudy() throws Exception {
        Validator v = new Validator(request);
        FormProcessor fp = new FormProcessor(request);
        v.addValidation("name", Validator.NO_BLANKS);
        v.addValidation("uniqueProId", Validator.NO_BLANKS);
        v.addValidation("description", Validator.NO_BLANKS);
        v.addValidation("prinInvestigator", Validator.NO_BLANKS);
        if (!StringUtil.isBlank(fp.getString(INPUT_START_DATE))) {
            v.addValidation(INPUT_START_DATE, Validator.IS_A_DATE);
        }
        if (!StringUtil.isBlank(fp.getString(INPUT_END_DATE))) {
            v.addValidation(INPUT_END_DATE, Validator.IS_A_DATE);
        }
        if (!StringUtil.isBlank(fp.getString("facConEmail"))) {
            v.addValidation("facConEmail", Validator.IS_A_EMAIL);
        }
        // v.addValidation("statusId", Validator.IS_VALID_TERM);
        v.addValidation("secondProId", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
        v.addValidation("facName", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
        v.addValidation("facCity", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
        v.addValidation("facState", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 20);
        v.addValidation("facZip", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 64);
        v.addValidation("facCountry", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 64);
        v.addValidation("facConName", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
        v.addValidation("facConDegree", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
        v.addValidation("facConPhone", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
        v.addValidation("facConEmail", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);

        errors = v.validate();
        if (fp.getString("name").trim().length() > 100) {
            Validator.addError(errors, "name", resexception.getString("maximum_lenght_name_100"));
        }
        if (fp.getString("uniqueProId").trim().length() > 30) {
            Validator.addError(errors, "uniqueProId", resexception.getString("maximum_lenght_unique_protocol_30"));
        }
        if (fp.getString("description").trim().length() > 255) {
            Validator.addError(errors, "description", resexception.getString("maximum_lenght_brief_summary_255"));
        }
        if (fp.getString("prinInvestigator").trim().length() > 255) {
            Validator.addError(errors, "prinInvestigator", resexception.getString("maximum_lenght_principal_investigator_255"));
        }
        if (fp.getInt("expectedTotalEnrollment") <= 0) {
            Validator.addError(errors, "expectedTotalEnrollment", respage.getString("expected_total_enrollment_must_be_a_positive_number"));
        }

        if(parentStudy.getStatus().equals(Status.LOCKED)){
            if(fp.getInt("statusId")!= Status.LOCKED.getId()){
                Validator.addError(errors, "statusId", respage.getString("study_locked_site_status_locked"));
            }
        }else if(parentStudy.getStatus().equals(Status.FROZEN)){
            if(fp.getInt("statusId")!= Status.AVAILABLE.getId()){
                Validator.addError(errors, "statusId", respage.getString("study_locked_site_status_frozen"));
            }
        }

        session.setAttribute("newStudy", createStudyBean());

        if (errors.isEmpty()) {
            logger.info("no errors");
            forwardPage(Page.CONFIRM_UPDATE_SUB_STUDY);

        } else {
            logger.info("has validation errors");
            try {
                local_df.parse(fp.getString(INPUT_START_DATE));
                fp.addPresetValue(INPUT_START_DATE, local_df.format(fp.getDate(INPUT_START_DATE)));
            } catch (ParseException pe) {
                fp.addPresetValue(INPUT_START_DATE, fp.getString(INPUT_START_DATE));
            }
            try {
                local_df.parse(fp.getString(INPUT_END_DATE));
                fp.addPresetValue(INPUT_END_DATE, local_df.format(fp.getDate(INPUT_END_DATE)));
            } catch (ParseException pe) {
                fp.addPresetValue(INPUT_END_DATE, fp.getString(INPUT_END_DATE));
            }
            setPresetValues(fp.getPresetValues());
            request.setAttribute("formMessages", errors);
            request.setAttribute("facRecruitStatusMap", CreateStudyServlet.facRecruitStatusMap);
            request.setAttribute("statuses", Status.toStudyUpdateMembersList());
            forwardPage(Page.UPDATE_SUB_STUDY);
        }

    }

    /**
     * Constructs study bean from request
     *
     * @param request
     * @return
     */
    private StudyBean createStudyBean() {
        FormProcessor fp = new FormProcessor(request);
        StudyBean study = (StudyBean) session.getAttribute("newStudy");
        study.setName(fp.getString("name"));
        study.setIdentifier(fp.getString("uniqueProId"));
        study.setSecondaryIdentifier(fp.getString("secondProId"));
        study.setSummary(fp.getString("description"));
        study.setPrincipalInvestigator(fp.getString("prinInvestigator"));
        study.setExpectedTotalEnrollment(fp.getInt("expectedTotalEnrollment"));

        java.util.Date startDate = null;
        java.util.Date endDate = null;
        try {
            local_df.setLenient(false);
            startDate = local_df.parse(fp.getString("startDate"));

        } catch (ParseException fe) {
            startDate = study.getDatePlannedStart();
            logger.info(fe.getMessage());
        }
        study.setDatePlannedStart(startDate);

        try {
            local_df.setLenient(false);
            endDate = local_df.parse(fp.getString("endDate"));

        } catch (ParseException fe) {
            endDate = study.getDatePlannedEnd();
        }
        study.setDatePlannedEnd(endDate);

        study.setFacilityCity(fp.getString("facCity"));
        study.setFacilityContactDegree(fp.getString("facConDrgree"));
        study.setFacilityName(fp.getString("facName"));
        study.setFacilityContactEmail(fp.getString("facConEmail"));
        study.setFacilityContactPhone(fp.getString("facConPhone"));
        study.setFacilityContactName(fp.getString("facConName"));
        study.setFacilityContactDegree(fp.getString("facConDegree"));
        study.setFacilityCountry(fp.getString("facCountry"));
        study.setFacilityRecruitmentStatus(fp.getString("facRecStatus"));
        study.setFacilityState(fp.getString("facState"));
        study.setFacilityZip(fp.getString("facZip"));
        // study.setStatusId(fp.getInt("statusId"));
        study.setStatus(Status.get(fp.getInt("statusId")));
        // YW 10-12-2007 <<
        study.getStudyParameterConfig().setInterviewerNameRequired(fp.getString("interviewerNameRequired"));
        study.getStudyParameterConfig().setInterviewerNameDefault(fp.getString("interviewerNameDefault"));
        study.getStudyParameterConfig().setInterviewDateRequired(fp.getString("interviewDateRequired"));
        study.getStudyParameterConfig().setInterviewDateDefault(fp.getString("interviewDateDefault"));
        // YW >>

        ArrayList parameters = study.getStudyParameters();

        for (int i = 0; i < parameters.size(); i++) {
            StudyParamsConfig scg = (StudyParamsConfig) parameters.get(i);
            String value = fp.getString(scg.getParameter().getHandle());
            logger.info("get value:" + value);
            scg.getValue().setStudyId(study.getId());
            scg.getValue().setParameter(scg.getParameter().getHandle());
            scg.getValue().setValue(value);
        }

        return study;

    }

    /**
     * Inserts the new study into database
     *
     */
    private void submitStudy() {
        StudyDAO sdao = new StudyDAO(sm.getDataSource());
        StudyBean study = (StudyBean) session.getAttribute("newStudy");
        ArrayList parameters = study.getStudyParameters();
        /*
         * logger.info("study bean to be updated:\n");
         * logger.info(study.getName()+ "\n" + study.getCreatedDate() + "\n" +
         * study.getIdentifier() + "\n" + study.getParentStudyId()+ "\n" +
         * study.getSummary()+ "\n" + study.getPrincipalInvestigator()+ "\n" +
         * study.getDatePlannedStart()+ "\n" + study.getDatePlannedEnd()+ "\n" +
         * study.getFacilityName()+ "\n" + study.getFacilityCity()+ "\n" +
         * study.getFacilityState()+ "\n" + study.getFacilityZip()+ "\n" +
         * study.getFacilityCountry()+ "\n" +
         * study.getFacilityRecruitmentStatus()+ "\n" +
         * study.getFacilityContactName()+ "\n" +
         * study.getFacilityContactEmail()+ "\n" +
         * study.getFacilityContactPhone()+ "\n" +
         * study.getFacilityContactDegree());
         */

        // study.setCreatedDate(new Date());
        study.setUpdatedDate(new Date());
        study.setUpdater(ub);
        sdao.update(study);

        StudyParameterValueDAO spvdao = new StudyParameterValueDAO(sm.getDataSource());
        for (int i = 0; i < parameters.size(); i++) {
            StudyParamsConfig config = (StudyParamsConfig) parameters.get(i);
            StudyParameterValueBean spv = config.getValue();

            StudyParameterValueBean spv1 = spvdao.findByHandleAndStudy(spv.getStudyId(), spv.getParameter());
            if (spv1.getId() > 0) {
                spv = (StudyParameterValueBean) spvdao.update(spv);
            } else {
                spv = (StudyParameterValueBean) spvdao.create(spv);
            }
            // spv = (StudyParameterValueBean)spvdao.update(config.getValue());

        }
        session.removeAttribute("newStudy");
        session.removeAttribute("parentName");
        addPageMessage(respage.getString("the_site_has_been_updated_succesfully"));
        String fromListSite = (String) session.getAttribute("fromListSite");
        if (fromListSite != null && fromListSite.equals("yes") && currentRole.getRole().equals(Role.STUDYDIRECTOR)) {
            session.removeAttribute("fromListSite");
            forwardPage(Page.SITE_LIST_SERVLET);
        } else {
            session.removeAttribute("fromListSite");
            forwardPage(Page.STUDY_LIST_SERVLET);
        }

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
