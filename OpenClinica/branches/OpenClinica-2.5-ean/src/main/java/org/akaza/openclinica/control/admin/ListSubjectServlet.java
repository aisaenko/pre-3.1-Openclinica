/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.DisplaySubjectBean;
import org.akaza.openclinica.bean.submit.DisplaySubjectRow;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.dao.core.EntityDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Processes user request and generate subject list
 *
 * @author jxu
 */
public class ListSubjectServlet extends SecureController {
    Locale locale;

    // < ResourceBundleresexception,respage,resword;

    /**
     *
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {

        locale = request.getLocale();
        // < resword =
        // ResourceBundle.getBundle("org.akaza.openclinica.i18n.words",locale);
        // <
        // resexception=ResourceBundle.getBundle("org.akaza.openclinica.i18n.exceptions",locale);
        // < respage =
        // ResourceBundle.getBundle("org.akaza.openclinica.i18n.page_messages",locale);

        if (ub.isSysAdmin()) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.ADMIN_SYSTEM_SERVLET, resexception.getString("not_admin"), "1");

    }

    /**
     * Finds all the crfs
     */
    @Override
    public void processRequest() throws Exception {
        SubjectDAO sdao = new SubjectDAO(sm.getDataSource());

        ArrayList allSubjects = sdao.findAllSubjectsAndStudies();
        ArrayList dsbs = new ArrayList();   
        SubjectBean lastSubject = null;
        String protocolSubjectIds = "";

        // Used for the count of subjects...look into this
        ArrayList subjects = (ArrayList) sdao.findAll();	
        
        for (int i = 0; i < allSubjects.size(); i++) {
            // Extract the subject from the hash map
            SubjectBean subject = (SubjectBean) allSubjects.get(i);
            
            // Create protocolSubjectIds
            if (lastSubject != null) {

                if (subject.getUniqueIdentifier() != lastSubject.getUniqueIdentifier()) {		    		   
                    // Last subject is complete
                    DisplaySubjectBean dsb = new DisplaySubjectBean();
                    dsb.setSubject(lastSubject);
                    dsb.setStudySubjectIds(protocolSubjectIds);
                    dsbs.add(dsb);		    

                    // New subject
                    protocolSubjectIds = subject.getStudyIdentifier() + "-" + subject.getLabel();
                } else {
                    // Append this protocalSubjectId
                    protocolSubjectIds += ", " + subject.getStudyIdentifier() + "-" + subject.getLabel();
                }

            } 

            lastSubject = subject;
        }

        // Last subject is complete
        DisplaySubjectBean dsb = new DisplaySubjectBean();
        dsb.setSubject(lastSubject);
        dsb.setStudySubjectIds(protocolSubjectIds);
        dsbs.add(dsb);		    
    /*
        
        SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
        StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
        StudyDAO stdao = new StudyDAO(sm.getDataSource());
        ArrayList dsbs = new ArrayList();
        ArrayList subjects = (ArrayList) sdao.findAll();
        for (int i = 0; i < subjects.size(); i++) {
            SubjectBean subject = (SubjectBean) subjects.get(i);
            ArrayList studySubs = ssdao.findAllBySubjectId(subject.getId());
            String protocolSubjectIds = "";
            for (int j = 0; j < studySubs.size(); j++) {
                StudySubjectBean studySub = (StudySubjectBean) studySubs.get(j);
                int studyId = studySub.getStudyId();
                StudyBean stu = (StudyBean) stdao.findByPK(studyId);
                String protocolId = stu.getIdentifier();
                if (j == studySubs.size() - 1) {
                    protocolSubjectIds += protocolId + "-" + studySub.getLabel();
                } else {
                    protocolSubjectIds += protocolId + "-" + studySub.getLabel() + ", ";
                }
            }
            DisplaySubjectBean dsb = new DisplaySubjectBean();
            dsb.setSubject(subject);
            dsb.setStudySubjectIds(protocolSubjectIds);
            dsbs.add(dsb);

        }        
*/
        FormProcessor fp = new FormProcessor(request);
        EntityBeanTable table = fp.getEntityBeanTable();
        ArrayList allRows = DisplaySubjectRow.generateRowsFromBeans(dsbs);

        String[] columns =
        { resword.getString("person_ID"), resword.getString("Protocol_Study_subject_IDs"), resword.getString("gender"), resword.getString("date_created"),
            resword.getString("owner"), resword.getString("date_updated"), resword.getString("last_updated_by"), resword.getString("status"),
            resword.getString("actions") };
        table.setColumns(new ArrayList(Arrays.asList(columns)));
        table.hideColumnLink(1);
        table.hideColumnLink(8);
        table.setQuery("ListSubject", new HashMap());
        table.setRows(allRows);
        table.computeDisplay();

        request.setAttribute("table", table);

        resetPanel();
        panel.setStudyInfoShown(false);
        panel.setOrderedData(true);
        if (subjects.size() > 0) {
            setToPanel("Subjects", new Integer(subjects.size()).toString());
        }
        forwardPage(Page.SUBJECT_LIST);
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
