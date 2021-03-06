/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.bean.managestudy.StudyTemplateBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyTemplateDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 *
 * Restores a removed subject group class
 */
public class RestoreSubjectGroupClassServlet extends SecureController {
  /**
   *  
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }

    if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
        || currentRole.getRole().equals(Role.COORDINATOR)) {
      return;
    }

    addPageMessage("You don't have correct privilege in your current active study. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET,
        "not study director", "1");

  }
  
  public void processRequest() throws Exception {
    String action = request.getParameter("action");
    FormProcessor fp = new FormProcessor(request);
    int classId = fp.getInt("id");

    if (classId == 0) {

      addPageMessage("Please choose a subject group class to restore.");
      forwardPage(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET);
    } else {
      StudyTemplateDAO stdao = new StudyTemplateDAO(sm.getDataSource());
      StudyGroupDAO sgdao = new StudyGroupDAO(sm.getDataSource());
      SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(sm.getDataSource());       

      if (action.equalsIgnoreCase("confirm")) {
        StudyTemplateBean stb = (StudyTemplateBean) stdao.findAllByPK(classId);
        if (stb.getStatus().equals(Status.AVAILABLE)) {
          addPageMessage("This subject group class is available already and cannot be restored.");
          forwardPage(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET);
          return;
        }
        
        ArrayList groups = sgdao.findAllByGroupClass(stb);

        for (int i = 0; i < groups.size(); i++) {
          StudyGroupBean sg = (StudyGroupBean) groups.get(i);
          ArrayList subjectMaps = sgmdao.findAllByStudyGroupClassAndGroup(stb.getId(), sg.getId());
          sg.setSubjectMaps(subjectMaps);

        }

        session.setAttribute("group", stb);
        session.setAttribute("studyGroups", groups);
        forwardPage(Page.RESTORE_SUBJECT_GROUP_CLASS);

      } else if (action.equalsIgnoreCase("submit")) {
        StudyTemplateBean group = (StudyTemplateBean)session.getAttribute("group");
        group.setStatus(Status.AVAILABLE);
        group.setUpdater(ub);
        stdao.update(group);   
        
        ArrayList subjectMaps = sgmdao.findAllByStudyGroupClassId(group.getId());
        for (int i=0; i < subjectMaps.size(); i++){
          SubjectGroupMapBean sgmb = (SubjectGroupMapBean)subjectMaps.get(i);
          sgmb.setStatus(Status.AVAILABLE);
          sgmb.setUpdater(ub);
          sgmdao.update(sgmb);
        }
        addPageMessage("This subject group class was restored successfully.");
        forwardPage(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET);
      } else {
        addPageMessage("No action specified.");
        forwardPage(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET);
      }

    }
  }

  
}
