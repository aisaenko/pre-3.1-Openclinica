package org.akaza.openclinica.control.subjectmgmt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.akaza.openclinica.bean.login.PiiPrivilege;
import org.akaza.openclinica.bean.subject.SubjectEntryLabelBean;
import org.akaza.openclinica.bean.subject.SubjectEntryLabelRow;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.subject.SubjectEntryLabelDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

public class ListSubjectEntryLabelsServlet extends SecureController {
    public static final String PATH = "ListSubjectEntryLabels";
    public static final String ARG_MESSAGE = "message";

    protected void mayProceed() throws InsufficientPermissionException {
        if (ub.getPiiPrivilege().equals(PiiPrivilege.EDIT) ||
            ub.getPiiPrivilege().equals(PiiPrivilege.VIEW)) {
           return; 
        }
        
        addPageMessage("You don't have correct privilege to view subject entry labels, please contact your system admin.");
        throw new InsufficientPermissionException(Page.MENU_SERVLET, "You may not perform subject management functions.", "1");
    }

    protected void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);

        SubjectEntryLabelDAO seldao = new SubjectEntryLabelDAO(sm.getDataSource());
        EntityBeanTable table = fp.getEntityBeanTable();

        ArrayList allSubjectEntryLabels = getAllSubjectEntryLabels(seldao);
        ArrayList allSubjectEntryLabelRows = generateRowsFromBeans(allSubjectEntryLabels);

        String[] columns = { "Label", "Description", "Creator", "Create Date", "Actions"};
        table.setColumns(new ArrayList(Arrays.asList(columns)));
        table.hideColumnLink(4);
        table.setQuery("ListSubjectEntryLabels", new HashMap());
        table.addLink("Create a subject entry label", "CreateSubjectEntryLabel");

        table.setRows(allSubjectEntryLabelRows);
        table.computeDisplay();

        request.setAttribute("table", table);
        
        String message = fp.getString(ARG_MESSAGE, true);
        request.setAttribute(ARG_MESSAGE, message);
        
        resetPanel();
        panel.setStudyInfoShown(false);
        panel.setOrderedData(true);
        if (allSubjectEntryLabelRows.size()>0){
          setToPanel("Subject Entry Labels", new Integer(allSubjectEntryLabelRows.size()).toString());
        }else{
            setToPanel("Subject Entry Labels", "0");            
        }

        forwardPage(Page.LIST_SUBJECT_ENTRY_LABELS);
    }
    
    public static ArrayList getAllSubjectEntryLabels(SubjectEntryLabelDAO seldao) {
        ArrayList result = (ArrayList) seldao.findAll();
        return result;
    }


    protected String getAdminServlet() {
        return SecureController.ADMIN_SERVLET_CODE;
    }

    public ArrayList generateRowsFromBeans(ArrayList beans) {
        ArrayList answer = new ArrayList();
        
        for (int i = 0; i < beans.size(); i++) {
            try {
                SubjectEntryLabelRow row = new SubjectEntryLabelRow();
                row.setBean((SubjectEntryLabelBean) beans.get(i));
                answer.add(row);
            } catch (Exception e) { 
//                logger.severe(e.getMessage());
            }
        }
        
        return answer;
    }

}