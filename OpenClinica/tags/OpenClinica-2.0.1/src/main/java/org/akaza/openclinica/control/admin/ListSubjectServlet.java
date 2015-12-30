/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * Processes user request and generate subject list
 * 
 * @author jxu 
 */
public class ListSubjectServlet extends SecureController {
  /**
   *  
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }

    addPageMessage("You don't have correct privilege in your current active study. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.ADMIN_SYSTEM_SERVLET, "not admin", "1");

  }

  /**
   * Finds all the crfs   
   */
  public void processRequest() throws Exception {

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
        if (j == (studySubs.size() - 1)) {
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

    FormProcessor fp = new FormProcessor(request);
    EntityBeanTable table = fp.getEntityBeanTable();
    ArrayList allRows = DisplaySubjectRow.generateRowsFromBeans(dsbs);

    String[] columns = { "Person ID", "Protocol-Study Subject IDs ", "Gender", "Date Created",
        "Owner", "Date Updated", "Last Updated By", "Status", "Actions" };
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

  protected String getAdminServlet() {
    if (ub.isSysAdmin()) {
      return SecureController.ADMIN_SERVLET_CODE;
    } else {
      return "";
    }
  }

}