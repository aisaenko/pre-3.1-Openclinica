/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.admin;

import java.util.ArrayList;
import java.util.List;

import org.akaza.openclinica.bean.subject.Person;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.subject.LdapServer;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ViewPiiSubjectServlet extends SecureController {
  /**
   *  
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }

    addPageMessage("You don't have correct privilege. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.PII_SUBJECT_LIST_SERVLET, "not admin", "1");

  }

  public void processRequest() throws Exception {

    SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
    FormProcessor fp = new FormProcessor(request);
    String subjectId = fp.getString("id");

    if (subjectId == null || subjectId.equals("")) {
      addPageMessage("Please choose a subject to view.");
      forwardPage(Page.PII_SUBJECT_LIST_SERVLET);
    } else {
      LdapServer server = LdapServer.getInstance();
      List<Person> personList = server.query(LdapServer.QUERY_BY_UID, subjectId);
      Person person = null;
      if (personList!=null && !personList.isEmpty()) {
    	  person=personList.iterator().next();
      }

      SubjectBean subject = sdao.findByUniqueIdentifier(subjectId);
      ArrayList studySubs = new ArrayList();
      if (subject!=null) {
          //find all study subjects
          StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
          studySubs = ssdao.findAllBySubjectId(subject.getId());
      }

      request.setAttribute("person", person);
      request.setAttribute("subject", subject);
      request.setAttribute("studySubs", studySubs);
      forwardPage(Page.VIEW_PII_SUBJECT);

    }

  }
  
  protected String getAdminServlet() {
    if (ub.isSysAdmin()) {
      return SecureController.ADMIN_SERVLET_CODE;
    }
    return "";
  }

}