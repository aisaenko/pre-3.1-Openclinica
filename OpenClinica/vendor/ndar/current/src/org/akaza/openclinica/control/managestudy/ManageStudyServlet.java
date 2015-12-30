/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.dao.admin.AuditEventDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * Generates the index page of manage study module
 * 
 * @author ssachs
 */
public class ManageStudyServlet extends SecureController {

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.core.SecureController#processRequest()
   */
  protected void processRequest() throws Exception {
    //find last 5 modifed sites
    StudyDAO sdao = new StudyDAO(sm.getDataSource());    
    ArrayList sites = (ArrayList) sdao.findAllByParentAndLimit(currentStudy.getId(),true);
    ArrayList allSites = (ArrayList) sdao.findAllByParent(currentStudy.getId());
    request.setAttribute("sites", sites);
    request.setAttribute("sitesCount", new Integer(sites.size()));
    request.setAttribute("allSitesCount", new Integer(allSites.size()));
    
    StudyEventDefinitionDAO edao = new StudyEventDefinitionDAO(sm.getDataSource());
    ArrayList seds = (ArrayList) edao.findAllByStudyAndLimit(currentStudy.getId());
    ArrayList allSeds = (ArrayList) edao.findAllByStudy(currentStudy);
    request.setAttribute("seds", seds);
    request.setAttribute("sedsCount", new Integer(seds.size()));
    request.setAttribute("allSedsCount", new Integer(allSeds.size()));
    
    UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
    ArrayList users = (ArrayList) udao.findAllUsersByStudyIdAndLimit(currentStudy.getId(),true);
    ArrayList allUsers = (ArrayList) udao.findAllUsersByStudy(currentStudy.getId());
    request.setAttribute("users", users);
    request.setAttribute("usersCount", new Integer(users.size()));
    request.setAttribute("allUsersCount", new Integer(allUsers.size()));
    
    StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
    ArrayList subjects = (ArrayList) ssdao.findAllByStudyIdAndLimit(currentStudy.getId(),true);
    ArrayList allSubjects = (ArrayList) ssdao.findAllByStudyId(currentStudy.getId());
    request.setAttribute("subs",subjects);
    request.setAttribute("subsCount", new Integer(subjects.size()));
    request.setAttribute("allSubsCount", new Integer(allSubjects.size()));
    
    AuditEventDAO aedao = new AuditEventDAO(sm.getDataSource());
    ArrayList audits = (ArrayList) aedao.findAllByStudyIdAndLimit(currentStudy.getId());
    ArrayList allAudits = (ArrayList) aedao.findAllByStudyId(currentStudy.getId());
    request.setAttribute("audits", audits);
    request.setAttribute("auditsCount", new Integer(audits.size()));
    request.setAttribute("allAuditsCount", new Integer(allAudits.size()));
    resetPanel();
		
    if (allSubjects.size()>0){
      setToPanel("Subjects", new Integer(allSubjects.size()).toString());
    }
    if (allUsers.size()>0) {
      setToPanel("Users", new Integer(allUsers.size()).toString());
    }
    if (allSites.size()>0){
      setToPanel("Sites", new Integer(allSites.size()).toString());
    }
    if (allSeds.size()>0){
      setToPanel("Event Definitions", new Integer(allSeds.size()).toString());
    }
    
    
    forwardPage(Page.MANAGE_STUDY);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
   */
  /**
   * Checks whether the user has the correct privilege
   */
  public void mayProceed() throws InsufficientPermissionException {
  	if (ub.isSysAdmin()) {
      return;
    }

	Role r = currentRole.getRole();
    if (r.equals(Role.STUDYDIRECTOR) || r.equals(Role.COORDINATOR)) {
    	return ;
    }
    
    addPageMessage("You don't have correct privilege in your current active study. "
    		+ "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.MENU_SERVLET, "not director", "1");
  }

}