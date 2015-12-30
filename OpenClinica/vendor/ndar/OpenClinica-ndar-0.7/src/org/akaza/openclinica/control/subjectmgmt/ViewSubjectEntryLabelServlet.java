package org.akaza.openclinica.control.subjectmgmt;

import java.util.ArrayList;

import org.akaza.openclinica.bean.login.PiiPrivilege;
import org.akaza.openclinica.bean.subject.SubjectEntryLabelBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.subject.SubjectEntryLabelDAO;
import org.akaza.openclinica.exception.InconsistentStateException;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

public class ViewSubjectEntryLabelServlet extends SecureController {
    public static final String PATH = "ViewSubjectEntryLabel";

    public static final String ARG_SUBJECT_ENTRY_LABEL_ID = "subjectEntryLabelId";

    public static String getLink(int subjectEntryLabelId) {
        return PATH + '?' + ARG_SUBJECT_ENTRY_LABEL_ID + '=' + subjectEntryLabelId;
    }

    /* (non-Javadoc)
     * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
     */
    protected void mayProceed() throws InsufficientPermissionException {
      if (ub.getPiiPrivilege().equals(PiiPrivilege.EDIT) ||
          ub.getPiiPrivilege().equals(PiiPrivilege.VIEW)) {
        return;
      }
      addPageMessage("You don't have correct privilege to view subject entry labels, please contact your system admin.");
      throw new InsufficientPermissionException(Page.MENU,
          "You may not perform subject management functions", "1");
    }

    protected void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);
        int subjectEntryLabelId = fp.getInt(ARG_SUBJECT_ENTRY_LABEL_ID, true);
        SubjectEntryLabelDAO seldao = new SubjectEntryLabelDAO(sm.getDataSource());

        SubjectEntryLabelBean selb = (SubjectEntryLabelBean)seldao.findByPK(subjectEntryLabelId);


        ArrayList allSubjectEntryLabels = ListSubjectEntryLabelsServlet.getAllSubjectEntryLabels(seldao);

        resetPanel();
        panel.setStudyInfoShown(false);
        panel.setOrderedData(true);
        if (allSubjectEntryLabels.size()>0){
            setToPanel("Subject Entry Labels", new Integer(allSubjectEntryLabels.size()).toString());
          }else{
              setToPanel("Subject Entry Labels", "0");            
          }

        if (selb != null) {
            request.setAttribute("subjectEntryLabel", selb);
        } else {
            throw new InconsistentStateException(Page.LIST_SUBJECT_ENTRY_LABELS,
                    "The subject entry label you are attempting to view does not exist.");
        }

        forwardPage(Page.VIEW_SUBJECT_ENTRY_LABEL);
    }
}