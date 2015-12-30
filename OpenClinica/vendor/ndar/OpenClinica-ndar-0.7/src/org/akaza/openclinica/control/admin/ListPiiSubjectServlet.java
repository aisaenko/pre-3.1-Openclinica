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
import java.util.List;

import org.akaza.openclinica.bean.admin.DisplayPiiSubjectRow;
import org.akaza.openclinica.bean.login.PiiPrivilege;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.subject.Person;
import org.akaza.openclinica.bean.subject.SubjectEntryLabelBean;
import org.akaza.openclinica.bean.subject.SubjectLabelMapBean;
import org.akaza.openclinica.bean.submit.DisplaySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.subject.LdapServer;
import org.akaza.openclinica.dao.subject.SubjectEntryLabelDAO;
import org.akaza.openclinica.dao.subject.SubjectLabelMapDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * Processes user request and generate subject list
 * 
 * @author jxu 
 */
public class ListPiiSubjectServlet extends SecureController {
  /**
   *  
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.getPiiPrivilege().equals(PiiPrivilege.EDIT) ||
        ub.getPiiPrivilege().equals(PiiPrivilege.VIEW)) {
      return;
    }

    addPageMessage("You don't have correct privilege in your current active study. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.MENU_SERVLET, "no pii privilege", "1");

  }

  /**
   * Finds all the crfs   
   */
  public void processRequest() throws Exception {

    SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
    StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
    StudyDAO stdao = new StudyDAO(sm.getDataSource());
    SubjectLabelMapDAO slmdao = new SubjectLabelMapDAO(sm.getDataSource());
    
    ArrayList dsbs = new ArrayList();
//    ArrayList subjects = (ArrayList) sdao.findAll();

    FormProcessor fp = new FormProcessor(request);
    String action = request.getParameter("action");
    List<Person> subjects = null;
    LdapServer server = LdapServer.getInstance();
    if(!server.isConnected()){
        addPageMessage("The connection with LDAP server was unable to be created, please contact your administrator.");
        resetPanel();
        panel.setStudyInfoShown(false);
        panel.setOrderedData(true);
        setToPanel("You currently have", "");
        setToPanel("Subjects: ", new Integer(0).toString());
        forwardPage(Page.PII_SUBJECT_LIST);
        return;
    }
    if(action != null && action.equals("ListSubjectsByLabel")){
        subjects = new ArrayList();
        int subjectEntryLabelId = fp.getInt("subjectEntryLabelId");
        List<SubjectLabelMapBean> slmbs = slmdao.findAllBySubjectEntryLabelId(subjectEntryLabelId);
        for(SubjectLabelMapBean slmb : slmbs){
            SubjectBean sb = (SubjectBean)sdao.findByPK(slmb.getSubjectId());
            subjects.addAll(server.query(LdapServer.QUERY_BY_UID, sb.getUniqueIdentifier()));
        }
    }else{
        subjects = server.query(0, "");
    }
    if (subjects==null) {
    	return;
    }
    Person person = null;
    SubjectEntryLabelDAO seldao = new SubjectEntryLabelDAO(sm.getDataSource());
    for (int i = 0; i < subjects.size(); i++) {
      person = subjects.get(i);
      SubjectBean subject = sdao.findByUniqueIdentifier(person.getPersonId());
      String protocolSubjectIds = "";
      String labelIds = "";
      if (subject!=null) {
          ArrayList studySubs = ssdao.findAllBySubjectId(subject.getId());
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
          List<SubjectLabelMapBean> labels = slmdao.findAllBySubjectId(subject.getId());
          for (int k = 0; k < labels.size(); k++) {
              SubjectLabelMapBean labelMap = labels.get(k);
              int labelId = labelMap.getSubjectEntryLabelId();
              SubjectEntryLabelBean label = (SubjectEntryLabelBean) seldao.findByPK(labelId);
              if (k == (labels.size() - 1)) {
                labelIds += label.getName();
              } else {
                labelIds += label.getName() + ", ";
              }
		}
      }
      DisplaySubjectBean dsb = new DisplaySubjectBean();
      dsb.setSubject(subject);
      dsb.setStudySubjectIds(protocolSubjectIds);
      dsb.setLabelIds(labelIds);
      dsb.setPerson(person);
      dsbs.add(dsb);

    }

    EntityBeanTable table = fp.getEntityBeanTable();
    ArrayList allRows = DisplayPiiSubjectRow.generateRowsFromBeans(dsbs);

    String[] columns = { "Person ID", "Person Name", "Protocol-SubjectIDs", "Gender", "Entry Labels", "Date Created",
        "Owner", "Date Updated", "Last Updated By", "Status", "Actions" };
    table.setColumns(new ArrayList(Arrays.asList(columns)));
//    table.hideColumnLink(2); //Protocol-SubjectIDs
//    table.hideColumnLink(5); //Labels
    table.hideColumnLink(10);
	table.addLink("Add a new subject", "AddNewPiiSubject");
    table.setQuery("ListPiiSubject", new HashMap());
    table.setRows(allRows);
    table.computeDisplay();

    request.setAttribute("table", table);

    resetPanel();
    panel.setStudyInfoShown(false);
    panel.setOrderedData(true);
    if (subjects.size() > 0) {
	setToPanel("Subjects: ", new Integer(subjects.size()).toString());
    }else{
	setToPanel("Subjects: ", new Integer(0).toString());
    }
    forwardPage(Page.PII_SUBJECT_LIST);
  }

  protected String getAdminServlet() {
    if (ub.isSysAdmin()) {
      return SecureController.ADMIN_SERVLET_CODE;
    } else {
      return "";
    }
  }

}