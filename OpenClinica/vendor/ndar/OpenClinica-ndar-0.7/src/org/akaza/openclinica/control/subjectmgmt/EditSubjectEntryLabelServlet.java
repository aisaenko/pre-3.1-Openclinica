package org.akaza.openclinica.control.subjectmgmt;

import java.util.ArrayList;
import java.util.HashMap;

import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.PiiPrivilege;
import org.akaza.openclinica.bean.subject.SubjectEntryLabelBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.subject.SubjectEntryLabelDAO;
import org.akaza.openclinica.exception.InconsistentStateException;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

public class EditSubjectEntryLabelServlet extends SecureController {
    public static final String INPUT_SUBJECT_ENTRY_LABEL_LABEL = "subjectEntryLabelLabel";

    public static final String INPUT_SUBJECT_ENTRY_LABEL_DESCRIPTION = "subjectEntryLabelDescription";

    public static final String INPUT_CONFIRM_BUTTON = "submit";

    public static final String PATH = "EditSubjectEntryLabel";

    public static final String ARG_SUBJECT_ENTRY_LABEL_ID = "subjectEntryLabelId";

    public static final String ARG_STEPNUM = "stepNum";

    // possible values of ARG_STEPNUM
    public static final int EDIT_STEP = 1;

    public static final int CONFIRM_STEP = 2;

    // possible values of INPUT_CONFIRM_BUTTON
    public static final String BUTTON_CONFIRM_VALUE = "Confirm";

    public static final String BUTTON_BACK_VALUE = "Back";

    private ArrayList getAllStudies() {
        StudyDAO sdao = new StudyDAO(sm.getDataSource());
        return (ArrayList) sdao.findAll();
    }

    public static String getLink(int subjectEntryLabelId) {
        return PATH + '?' + ARG_SUBJECT_ENTRY_LABEL_ID + '=' + subjectEntryLabelId;
    }

    protected void mayProceed() throws InsufficientPermissionException {
        if (!ub.getPiiPrivilege().equals(PiiPrivilege.EDIT)) {
            throw new InsufficientPermissionException(Page.MENU,
                    "You may not perform subject management functions", "1");
        }

        return;
    }

    protected void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);

        // because we need to use this in the confirmation and error parts too
        ArrayList studies = getAllStudies();
        request.setAttribute("studies", studies);

        int subjectEntryLabelId = fp.getInt(ARG_SUBJECT_ENTRY_LABEL_ID);
        SubjectEntryLabelDAO seldao = new SubjectEntryLabelDAO(sm.getDataSource());
        SubjectEntryLabelBean selb = (SubjectEntryLabelBean) seldao.findByPK(subjectEntryLabelId);

        ArrayList allSubjectEntryLabels = ListSubjectEntryLabelsServlet.getAllSubjectEntryLabels(seldao);

        resetPanel();
        panel.setStudyInfoShown(false);
        panel.setOrderedData(true);
        if (allSubjectEntryLabels.size()>0){
          setToPanel("Subject Entry Labels", new Integer(allSubjectEntryLabels.size()).toString());
        }else{
            setToPanel("Subject Entry Labels", "0");            
        }

        int stepNum = fp.getInt(ARG_STEPNUM);

        if (!fp.isSubmitted()) {
            loadPresetValuesFromBean(fp, selb);
            fp.addPresetValue(ARG_STEPNUM, EDIT_STEP);
            setPresetValues(fp.getPresetValues());
            forwardPage(Page.EDIT_SUBJECT_ENTRY_LABEL);
        } else if (stepNum == EDIT_STEP) {
            Validator v = new Validator(request);

            v.addValidation(INPUT_SUBJECT_ENTRY_LABEL_LABEL, Validator.NO_BLANKS);

            HashMap errors = v.validate();

            if (errors.isEmpty()) {
                loadPresetValuesFromForm(fp);
                fp.addPresetValue(ARG_STEPNUM, CONFIRM_STEP);

                setPresetValues(fp.getPresetValues());
                forwardPage(Page.EDIT_SUBJECT_ENTRY_LABEL_CONFIRM);

            } else {
                loadPresetValuesFromForm(fp);
                fp.addPresetValue(ARG_STEPNUM, EDIT_STEP);
                setInputMessages(errors);

                setPresetValues(fp.getPresetValues());

                addPageMessage("There were some errors in your submission.  See below for details.");
                forwardPage(Page.EDIT_SUBJECT_ENTRY_LABEL);
            }
        } else if (stepNum == CONFIRM_STEP) {
            String button = fp.getString(INPUT_CONFIRM_BUTTON);

            if (button.equals(BUTTON_BACK_VALUE)) {
                loadPresetValuesFromForm(fp);
                fp.addPresetValue(ARG_STEPNUM, EDIT_STEP);
                setPresetValues(fp.getPresetValues());
                forwardPage(Page.EDIT_SUBJECT_ENTRY_LABEL);
            } else if (button.equals(BUTTON_CONFIRM_VALUE)) {
                selb.setName(fp.getString(INPUT_SUBJECT_ENTRY_LABEL_LABEL));
                selb.setDescription(fp.getString(INPUT_SUBJECT_ENTRY_LABEL_DESCRIPTION));
                selb.setUpdater(ub);
                seldao.update(selb);
                addPageMessage(" The subject entry label \"" + selb.getName()
                        + "\" was updated successfully.");
                forwardPage(Page.LIST_SUBJECT_ENTRY_LABELS_SERVLET);
            } else {
                throw new InconsistentStateException(Page.ADMIN_SYSTEM,
                        "An invalid submit button was clicked while editing a user.");
            }
        } else {
            throw new InconsistentStateException(Page.ADMIN_SYSTEM,
                    "An invalid step was specified while editing a user.");
        }
    }

    private void loadPresetValuesFromBean(FormProcessor fp, SubjectEntryLabelBean selb) {
        fp.addPresetValue(ARG_SUBJECT_ENTRY_LABEL_ID, selb.getId());
        fp.addPresetValue(INPUT_SUBJECT_ENTRY_LABEL_LABEL, selb.getName());
        fp.addPresetValue(INPUT_SUBJECT_ENTRY_LABEL_DESCRIPTION, selb.getDescription());
    }

    private void loadPresetValuesFromForm(FormProcessor fp) {
        fp.clearPresetValues();

        String textFields[] = { ARG_SUBJECT_ENTRY_LABEL_ID, INPUT_SUBJECT_ENTRY_LABEL_LABEL, INPUT_SUBJECT_ENTRY_LABEL_DESCRIPTION };
        fp.setCurrentStringValuesAsPreset(textFields);
    }

    private ArrayList getUserTypes() {

        ArrayList types = UserType.toArrayList();
        types.remove(UserType.INVALID);
        if (!ub.isTechAdmin()) {
            types.remove(UserType.TECHADMIN);
        }
        return types;
    }

    protected String getAdminServlet() {
        return SecureController.ADMIN_SERVLET_CODE;
    }
}