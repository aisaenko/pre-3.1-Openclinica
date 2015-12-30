/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 *
 * Views details of a Subject Group Class
 */
public class ViewSubjectGroupClassServlet  extends SecureController {
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }
    if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
        || currentRole.getRole().equals(Role.COORDINATOR)) {
      return;
    }
    addPageMessage("You don't have correct privilege in your current active study.\n"
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET,
        "not study director", "1");

  }
  
  
  public void processRequest() throws Exception {
    String action = request.getParameter("action");
    FormProcessor fp = new FormProcessor(request);
    int classId = fp.getInt("id");

    if (classId == 0) {

      addPageMessage("Please choose a subject group class to view.");
      forwardPage(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET);
    } else {
      StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(sm.getDataSource());
      StudyGroupDAO sgdao = new StudyGroupDAO(sm.getDataSource());
      SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(sm.getDataSource());
      
      StudyGroupClassBean sgcb = (StudyGroupClassBean) sgcdao.findByPK(classId);

      ArrayList groups = sgdao.findAllByGroupClass(sgcb);
	  ArrayList studyGroups = new ArrayList();
      
      for ( int i= 0; i<groups.size(); i++) {
        StudyGroupBean sg = (StudyGroupBean)groups.get(i);
        ArrayList subjectMaps =
          sgmdao.findAllByStudyGroupClassAndGroup(sgcb.getId(),sg.getId());
        sg.setSubjectMaps(subjectMaps);
        //YW<<
        studyGroups.add(sg);
        //YW>>
      }
      
     
      request.setAttribute("group", sgcb);
      //request.setAttribute("studyGroups", groups);
      request.setAttribute("studyGroups", studyGroups);
      forwardPage(Page.VIEW_SUBJECT_GROUP_CLASS);
    }
  }

}
