/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.akaza.openclinica.web.bean.StudyGroupClassRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

/**
 * Lists all the subject group classes in a study
 * 
 * @author jxu
 * 
 */
public class ListSubjectGroupClassServlet extends SecureController {

    Locale locale;

    // < ResourceBundleresexception,respage,resword;

    @Override
    public void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);
        StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(sm.getDataSource());
        // YW <<
        StudyDAO stdao = new StudyDAO(sm.getDataSource());
        int parentStudyId = currentStudy.getParentStudyId();
        ArrayList groups = new ArrayList();
        if (parentStudyId > 0) {
            StudyBean parentStudy = (StudyBean) stdao.findByPK(parentStudyId);
            groups = sgcdao.findAllByStudy(parentStudy);
        } else {
            groups = sgcdao.findAllByStudy(currentStudy);
        }
        // YW >>
        String isReadOnly = request.getParameter("read");

        StudyGroupDAO sgdao = new StudyGroupDAO(sm.getDataSource());
        for (int i = 0; i < groups.size(); i++) {
            StudyGroupClassBean group = (StudyGroupClassBean) groups.get(i);
            ArrayList studyGroups = sgdao.findAllByGroupClass(group);
            group.setStudyGroups(studyGroups);

        }
        EntityBeanTable table = fp.getEntityBeanTable();
        ArrayList allGroupRows = StudyGroupClassRow.generateRowsFromBeans(groups);
        boolean isParentStudy = currentStudy.getParentStudyId() > 0 ? false : true;
        request.setAttribute("isParentStudy", isParentStudy);

        String[] columns =
            { resword.getString("subject_group_class"), resword.getString("type"), resword.getString("subject_assignment"), resword.getString("study_name"),
                resword.getString("subject_groups"), resword.getString("status"), resword.getString("actions") };
        table.setColumns(new ArrayList(Arrays.asList(columns)));
        table.hideColumnLink(4);
        table.hideColumnLink(6);
        table.setQuery("ListSubjectGroupClass", new HashMap());
        // if (isParentStudy && (!currentStudy.getStatus().isLocked())) {
        // table.addLink(resword.getString("create_a_subject_group_class"),
        // "CreateSubjectGroupClass");
        // }
        table.setRows(allGroupRows);
        table.computeDisplay();

        request.setAttribute("table", table);
        if (request.getParameter("read") != null && request.getParameter("read").equals("true")) {
            request.setAttribute("readOnly", true);
        }
        forwardPage(Page.SUBJECT_GROUP_CLASS_LIST);

    }

}
