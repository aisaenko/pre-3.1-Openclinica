package org.akaza.openclinica.control.subjectmgmt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.akaza.openclinica.bean.login.PiiPrivilege;
import org.akaza.openclinica.bean.subject.SubjectEntryLabelBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.subject.SubjectEntryLabelDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.view.Page;

public class CreateSubjectEntryLabelServlet extends SecureController {
    public static final String SUBJECT_ENTRY_LABEL_LABEL = "subjectEntryLabelLabel";

    public static final String SUBJECT_ENTRY_LABEL_DESCRIPTION = "subjectEntryLabelDescription";

    public static final String SUBMIT_BACK_BUTTON = "submitBack";

    public static final String SUBMIT_CREATE_BUTTON = "submitCreate";

    public static final String SUBMIT_DONE_BUTTON = "submitDone";

    @Override
    protected void mayProceed() throws InsufficientPermissionException {
        if (ub.getPiiPrivilege().equals(PiiPrivilege.EDIT)){
           return;
           
        }
        addPageMessage("You don't have correct privilege to create a subject entry label, please contact your system admin. ");
         throw new InsufficientPermissionException(Page.MENU,
             "You may not perform subject management functions", "1");
    }

    @Override
    protected void processRequest() throws Exception {
        SubjectEntryLabelDAO seldao = new SubjectEntryLabelDAO(sm
                .getDataSource());

        panel.setStudyInfoShown(false);
        FormProcessor fp = new FormProcessor(request);

        if (!fp.isSubmitted()) {
            ArrayList allSubjectEntryLabels = ListSubjectEntryLabelsServlet
                    .getAllSubjectEntryLabels(seldao);
            resetPanel();
            panel.setOrderedData(true);
            if (allSubjectEntryLabels.size() > 0) {
                setToPanel("Subject Entry Labels", new Integer(
                        allSubjectEntryLabels.size()).toString());
            } else {
                setToPanel("Subject Entry Labels", "0");
            }
            forwardPage(Page.CREATE_SUBJECT_ENTRY_LABEL);
        } else {
            Validator v = new Validator(request);
            HashMap errors = v.validate();

            String label = fp.getString(SUBJECT_ENTRY_LABEL_LABEL);
            if (label == null || label.length() == 0) {
                Validator.addError(errors, SUBJECT_ENTRY_LABEL_LABEL,
                        "This field can not be empty and has to be unique!");
             }
            String description = fp.getString(SUBJECT_ENTRY_LABEL_DESCRIPTION);

            if (!errors.isEmpty()) {
                addPageMessage("There were some errors in your submission.");
                setInputMessages(errors);
                fp.addPresetValue(SUBJECT_ENTRY_LABEL_LABEL, fp
                        .getString(SUBJECT_ENTRY_LABEL_LABEL));
                fp.addPresetValue(SUBJECT_ENTRY_LABEL_DESCRIPTION, fp
                        .getString(SUBJECT_ENTRY_LABEL_DESCRIPTION));
                setPresetValues(fp.getPresetValues());
                forwardPage(Page.CREATE_SUBJECT_ENTRY_LABEL);
            } else {

                SubjectEntryLabelBean selb = new SubjectEntryLabelBean();
                selb.setName(label);
                selb.setDescription(description);
                selb.setOwner(ub);
                seldao.create(selb);
                if(!seldao.isQuerySuccessful()){
                    Validator.addError(errors, SUBJECT_ENTRY_LABEL_LABEL, "The label you want to create already existed in the system.");
                    addPageMessage("There were some errors in your submission.");
                    setInputMessages(errors);
                    fp.addPresetValue(SUBJECT_ENTRY_LABEL_LABEL, fp
                            .getString(SUBJECT_ENTRY_LABEL_LABEL));
                    fp.addPresetValue(SUBJECT_ENTRY_LABEL_DESCRIPTION, fp
                            .getString(SUBJECT_ENTRY_LABEL_DESCRIPTION));
                    setPresetValues(fp.getPresetValues());
                    forwardPage(Page.CREATE_SUBJECT_ENTRY_LABEL);
                } else {
                    request.removeAttribute(FormProcessor.FIELD_SUBMITTED);
                    request.setAttribute(FormProcessor.FIELD_SUBMITTED, "0");

                    addPageMessage("The subject entry label with unique identifier '"
                            + selb.getName() + "' was created.");
                    String submitBack = fp.getString(SUBMIT_BACK_BUTTON);
                    String submitCreate = fp.getString(SUBMIT_CREATE_BUTTON);
                    String submitDone = fp.getString(SUBMIT_DONE_BUTTON);

                    if (!StringUtil.isBlank(submitBack)) {
                        forwardPage(Page.LIST_SUBJECT_ENTRY_LABELS_SERVLET);
                    } else if (!StringUtil.isBlank(submitCreate)) {
                        forwardPage(Page.CREATE_SUBJECT_ENTRY_LABEL_SERVLET);
                    } else {
                        forwardPage(Page.LIST_SUBJECT_ENTRY_LABELS_SERVLET);
                    }
                }
            }
        }
    }

}
