package org.akaza.openclinica.control.subjectmgmt;

import java.util.List;

import org.akaza.openclinica.bean.core.EntityAction;
import org.akaza.openclinica.bean.login.PiiPrivilege;
import org.akaza.openclinica.bean.subject.SubjectEntryLabelBean;
import org.akaza.openclinica.bean.subject.SubjectLabelMapBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.subject.SubjectEntryLabelDAO;
import org.akaza.openclinica.dao.subject.SubjectLabelMapDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

public class DeleteSubjectEntryLabelServlet extends SecureController {
    public static final String PATH = "DeleteSubjectEntryLabel";

    public static final String ARG_SUBJECT_ENTRY_LABEL_ID = "subjectEntryLabelId";

    public static String getLink(SubjectEntryLabelBean selb, EntityAction action) {
        return PATH + "?" + ARG_SUBJECT_ENTRY_LABEL_ID + "=" + selb.getId();
    }

    protected void mayProceed() throws InsufficientPermissionException {
        if (ub.getPiiPrivilege().equals(PiiPrivilege.EDIT)) {
            return;
        }
        addPageMessage("You don't have correct privilege to remove a subject entry label, please contact your system admin. ");
        throw new InsufficientPermissionException(Page.MENU,
            "You may not perform subject management functions", "1");
    }

    protected void processRequest() throws Exception {
        SubjectEntryLabelDAO seldao = new SubjectEntryLabelDAO(sm.getDataSource());
        SubjectLabelMapDAO slmdao = new SubjectLabelMapDAO(sm.getDataSource());
        
        FormProcessor fp = new FormProcessor(request);
        int subjectEntryLabelId = fp.getInt(ARG_SUBJECT_ENTRY_LABEL_ID);

        SubjectEntryLabelBean selb = (SubjectEntryLabelBean) seldao.findByPK(subjectEntryLabelId);
        List<SubjectLabelMapBean> slmrs = slmdao.findAllBySubjectEntryLabelId(subjectEntryLabelId);
        
        if(slmrs != null && slmrs.size() > 0){
            addPageMessage("The subject entry label (" + selb.getName() + ") can't be deleted, because it was already used!");
            forwardPage(Page.LIST_SUBJECT_ENTRY_LABELS_SERVLET);
            return ;
        }
        seldao.delete(selb);

        String message = "";
        if(seldao.isQuerySuccessful()){
            message = "The subject entry label(" + selb.getName() + ") has been deleted.";
        }else{
            message = seldao.getFailureDetails().getMessage();
        }

        addPageMessage(message);
        forwardPage(Page.LIST_SUBJECT_ENTRY_LABELS_SERVLET);
    }

    protected String getAdminServlet() {
        return SecureController.ADMIN_SERVLET_CODE;
    }
}