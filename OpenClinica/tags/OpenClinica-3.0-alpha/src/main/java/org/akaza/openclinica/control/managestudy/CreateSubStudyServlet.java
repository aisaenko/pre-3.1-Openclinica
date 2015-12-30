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
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author jxu
 *
 * Creates a sub study of user's current active study
 *
 * Modified by ywang: [10-10-2007], enable setting overidable study parameters
 * of a sub study.
 */
public class CreateSubStudyServlet extends SecureController {
    public static final String INPUT_START_DATE = "startDate";
    public static final String INPUT_END_DATE = "endDate";

    /**
     *
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {
        checkStudyLocked(Page.SITE_LIST_SERVLET, respage.getString("current_study_locked"));
        //checkStudyFrozen(Page.SITE_LIST_SERVLET, respage.getString("current_study_frozen"));
        if (ub.isSysAdmin()) {
            return;
        }
        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
            return;
        }
        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + "\n" + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.SITE_LIST_SERVLET, resexception.getString("not_study_director"), "1");

    }

    @Override
    public void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);
        String action = request.getParameter("action");

        if (StringUtil.isBlank(action)) {
            if (currentStudy.getParentStudyId() > 0) {
                addPageMessage(respage.getString("you_cannot_create_site_itself_site"));

                forwardPage(Page.SITE_LIST_SERVLET);
            }           
            else {
                StudyBean newStudy = new StudyBean();
                newStudy.setParentStudyId(currentStudy.getId());
                // get default facility info from property file
                newStudy.setFacilityName(SQLInitServlet.getField(CreateStudyServlet.FAC_NAME));
                newStudy.setFacilityCity(SQLInitServlet.getField(CreateStudyServlet.FAC_CITY));
                newStudy.setFacilityState(SQLInitServlet.getField(CreateStudyServlet.FAC_STATE));
                newStudy.setFacilityCountry(SQLInitServlet.getField(CreateStudyServlet.FAC_COUNTRY));
                newStudy.setFacilityContactDegree(SQLInitServlet.getField(CreateStudyServlet.FAC_CONTACT_DEGREE));
                newStudy.setFacilityContactEmail(SQLInitServlet.getField(CreateStudyServlet.FAC_CONTACT_EMAIL));
                newStudy.setFacilityContactName(SQLInitServlet.getField(CreateStudyServlet.FAC_CONTACT_NAME));
                newStudy.setFacilityContactPhone(SQLInitServlet.getField(CreateStudyServlet.FAC_CONTACT_PHONE));
                newStudy.setFacilityZip(SQLInitServlet.getField(CreateStudyServlet.FAC_ZIP));

                List<StudyParamsConfig> parentConfigs =
                  currentStudy.getStudyParameters();
                // logger.info("parentConfigs size:" + parentConfigs.size());
                ArrayList configs = new ArrayList();

                for (StudyParamsConfig scg : parentConfigs) {
                   // StudyParamsConfig scg = (StudyParamsConfig) parentConfigs.get(i);
                    if (scg != null) {
                        // find the one that sub study can change
                        if (scg.getValue().getId() > 0 && scg.getParameter().isOverridable()) {
                            logger.info("parameter:" + scg.getParameter().getHandle());
                            logger.info("value:" + scg.getValue().getValue());
                            // YW 10-12-2007, set overridable study parameters
                            // for a site
                            if (scg.getParameter().getHandle().equalsIgnoreCase("interviewerNameRequired")) {
                                scg.getValue().setValue(fp.getString("interviewerNameRequired"));
                            } else if (scg.getParameter().getHandle().equalsIgnoreCase("interviewerNameDefault")) {
                                scg.getValue().setValue(fp.getString("interviewerNameDefault"));
                            } else if (scg.getParameter().getHandle().equalsIgnoreCase("interviewDateRequired")) {
                                scg.getValue().setValue(fp.getString("interviewDateRequired"));
                            } else if (scg.getParameter().getHandle().equalsIgnoreCase("interviewDateDefault")) {
                                scg.getValue().setValue(fp.getString("interviewDateDefault"));
                            }
                            // YW >>
                            configs.add(scg);
                        }
                    }

                }
                newStudy.setStudyParameters(configs);

                // YW 10-12-2007 <<
                newStudy.getStudyParameterConfig().setInterviewerNameRequired(fp.getString("interviewerNameRequired"));
                newStudy.getStudyParameterConfig().setInterviewerNameDefault(fp.getString("interviewerNameDefault"));
                newStudy.getStudyParameterConfig().setInterviewDateRequired(fp.getString("interviewDateRequired"));
                newStudy.getStudyParameterConfig().setInterviewDateDefault(fp.getString("interviewDateDefault"));
                // YW >>

                //BWP 3169 1-12-2008 <<
                 newStudy.getStudyParameterConfig().
                   setInterviewerNameEditable(currentStudy.getStudyParameterConfig().getInterviewerNameEditable());
                newStudy.getStudyParameterConfig().setInterviewDateEditable(currentStudy.getStudyParameterConfig().getInterviewDateEditable());
                //>>

                session.setAttribute("newStudy", newStudy);
                request.setAttribute("facRecruitStatusMap", CreateStudyServlet.facRecruitStatusMap);
                request.setAttribute("statuses", Status.toActiveArrayList());

                forwardPage(Page.CREATE_SUB_STUDY);
            }

        } else {
            if ("confirm".equalsIgnoreCase(action)) {
                confirmStudy();

            }else if ("back".equalsIgnoreCase(action)) {
                request.setAttribute("facRecruitStatusMap", CreateStudyServlet.facRecruitStatusMap);
                request.setAttribute("statuses", Status.toActiveArrayList());

                forwardPage(Page.CREATE_SUB_STUDY);
            }
            else if ("submit".equalsIgnoreCase(action)) {
                submitStudy();

            }
        }
    }

    /**
     * Validates the first section of study and save it into study bean
     *
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

        session.setAttribute("newStudy", createStudyBean());

        if (errors.isEmpty()) {
            logger.info("no errors");
            forwardPage(Page.CONFIRM_CREATE_SUB_STUDY);

        } else {
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
            logger.info("has validation errors");
            request.setAttribute("formMessages", errors);
            // request.setAttribute("facRecruitStatusMap",
            // CreateStudyServlet.facRecruitStatusMap);
            request.setAttribute("statuses", Status.toActiveArrayList());
            forwardPage(Page.CREATE_SUB_STUDY);
        }

    }

    /**
     * Constructs study bean from request
     *
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
        // study.setFacilityRecruitmentStatus(fp.getString("facRecStatus"));
        study.setFacilityState(fp.getString("facState"));
        study.setFacilityZip(fp.getString("facZip"));
        study.setStatus(Status.get(fp.getInt("statusId")));

        ArrayList parameters = study.getStudyParameters();

        for (int i = 0; i < parameters.size(); i++) {
            StudyParamsConfig scg = (StudyParamsConfig) parameters.get(i);
            String value = fp.getString(scg.getParameter().getHandle());
            logger.info("get value:" + value);
            scg.getValue().setParameter(scg.getParameter().getHandle());
            scg.getValue().setValue(value);
        }

        // YW 10-12-2007 <<
        study.getStudyParameterConfig().setInterviewerNameRequired(fp.getString("interviewerNameRequired"));
        study.getStudyParameterConfig().setInterviewerNameDefault(fp.getString("interviewerNameDefault"));
        study.getStudyParameterConfig().setInterviewDateRequired(fp.getString("interviewDateRequired"));
        study.getStudyParameterConfig().setInterviewDateDefault(fp.getString("interviewDateDefault"));
        // YW >>

        return study;

    }

    /**
     * Inserts the new study into database
     *
     */
    private void submitStudy() {
        FormProcessor fp = new FormProcessor(request);
        StudyDAO sdao = new StudyDAO(sm.getDataSource());
        StudyBean study = (StudyBean) session.getAttribute("newStudy");

        ArrayList parameters = study.getStudyParameters();
        logger.info("study bean to be created:\n");
        logger.info(study.getName() + "\n" + study.getIdentifier() + "\n" + study.getParentStudyId() + "\n" + study.getSummary() + "\n"
            + study.getPrincipalInvestigator() + "\n" + study.getDatePlannedStart() + "\n" + study.getDatePlannedEnd() + "\n" + study.getFacilityName() + "\n"
            + study.getFacilityCity() + "\n" + study.getFacilityState() + "\n" + study.getFacilityZip() + "\n" + study.getFacilityCountry() + "\n"
            + study.getFacilityRecruitmentStatus() + "\n" + study.getFacilityContactName() + "\n" + study.getFacilityContactEmail() + "\n"
            + study.getFacilityContactPhone() + "\n" + study.getFacilityContactDegree());

        study.setOwner(ub);
        study.setCreatedDate(new Date());
        StudyBean parent = (StudyBean) sdao.findByPK(study.getParentStudyId());
        study.setType(parent.getType());
        // YW 10-10-2007, enable setting site status
        study.setStatus(study.getStatus());
        // YW >>

        study.setGenetic(parent.isGenetic());
        study = (StudyBean) sdao.create(study);

        StudyParameterValueDAO spvdao = new StudyParameterValueDAO(sm.getDataSource());
        for (int i = 0; i < parameters.size(); i++) {
            StudyParamsConfig config = (StudyParamsConfig) parameters.get(i);
            StudyParameterValueBean spv = config.getValue();
            spv.setStudyId(study.getId());
            spv = (StudyParameterValueBean) spvdao.create(config.getValue());
        }

        // YW << here only "collectDob" and "genderRequired" have been corrected
        // for sites.
        StudyParameterValueBean spv = new StudyParameterValueBean();
        StudyParameterValueBean parentSPV = spvdao.findByHandleAndStudy(parent.getId(), "collectDob");
        spv.setStudyId(study.getId());
        spv.setParameter("collectDob");
        spv.setValue(parentSPV.getValue());
        spvdao.create(spv);

        parentSPV = spvdao.findByHandleAndStudy(parent.getId(), "genderRequired");
        spv.setParameter("genderRequired");
        spv.setValue(parentSPV.getValue());
        spvdao.create(spv);
        // YW >>

        // switch user to the newly created site
        session.setAttribute("study", session.getAttribute("newStudy"));
        currentStudy = (StudyBean) session.getAttribute("study");

        session.removeAttribute("newStudy");
        addPageMessage(respage.getString("the_new_site_created_succesfully_current"));
        forwardPage(Page.SITE_LIST_SERVLET);

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
