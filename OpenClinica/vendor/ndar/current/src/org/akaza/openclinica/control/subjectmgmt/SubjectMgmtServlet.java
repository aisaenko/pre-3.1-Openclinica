/*
 *
 */
package org.akaza.openclinica.control.subjectmgmt;
//
//import java.util.ArrayList;

import java.util.ArrayList;
import java.util.List;

import org.akaza.openclinica.bean.login.PiiPrivilege;
import org.akaza.openclinica.bean.subject.Person;
import org.akaza.openclinica.bean.submit.DisplaySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.dao.subject.LdapServer;
import org.akaza.openclinica.dao.subject.SubjectEntryLabelDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author thickerson
 *
 * 
 */
public class SubjectMgmtServlet extends SecureController {

	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.core.SecureController#processRequest()
	 */
	protected void processRequest() throws Exception {
	   	    
	    SubjectEntryLabelDAO seldao = new SubjectEntryLabelDAO(sm.getDataSource());
	    ArrayList labels = (ArrayList) seldao.findAll();
	    request.setAttribute("labels", labels);
	    //request.setAttribute("allUserNumber", new Integer(allUsers.size()));
	    
	    SubjectDAO subdao = new SubjectDAO(sm.getDataSource());
	    ArrayList allSubjects = (ArrayList) subdao.findAll();
        List<DisplaySubjectBean> displaySubjects = new ArrayList<DisplaySubjectBean>(10);
        LdapServer server = LdapServer.getInstance();
        if(!server.isConnected()){
            addPageMessage("The connection with LDAP server was unable to be created, please contact your administrator.");
	    resetPanel();		
	    
	    panel.setStudyInfoShown(false);
	    panel.setOrderedData(true);
	    setToPanel("You currently have","");
	    setToPanel("Subjects: ", new Integer(0).toString());
	    if (labels.size()>0) {
	      setToPanel("Subject Entry Labels: ", new Integer(labels.size()).toString());
	    }else{
		setToPanel("Subject Entry Labels: ", new Integer(0).toString());
	    }
            forwardPage(Page.SUBJECT_MGMT);
            return;
        }
        for(int i = 0; i < allSubjects.size(); i++){
            SubjectBean sb = (SubjectBean)allSubjects.get(i);
            List<Person> persons = server.query(LdapServer.QUERY_BY_UID, sb.getUniqueIdentifier());
            if(persons == null || persons.size() == 0){
                continue;
            }
            DisplaySubjectBean dsb = new DisplaySubjectBean();
            dsb.setSubject(sb);
            dsb.setPerson(persons.get(0));
            displaySubjects.add(dsb);
        }
	    request.setAttribute("subs", displaySubjects);
	    resetPanel();		
	    
	    panel.setStudyInfoShown(false);
	    panel.setOrderedData(true);
	    setToPanel("You currently have","");
	    if (allSubjects.size()>0){
		setToPanel("Subjects: ", new Integer(allSubjects.size()).toString());
	    }else{
		setToPanel("Subjects: ", new Integer(0).toString());
	    }
	    if (labels.size()>0) {
		setToPanel("Subject Entry Labels: ", new Integer(labels.size()).toString());
	    }else{
		setToPanel("Subject Entry Labels: ", new Integer(0).toString());
	    }
		forwardPage(Page.SUBJECT_MGMT);
	}

	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
	 */
	protected void mayProceed() throws InsufficientPermissionException {
		if (ub.getPiiPrivilege().equals(PiiPrivilege.EDIT) ||
            ub.getPiiPrivilege().equals(PiiPrivilege.VIEW)) {
			return;
		}
        addPageMessage("You don't have correct privilege to perform subject management functions, please contact your system admin.");
        throw new InsufficientPermissionException(Page.MENU_SERVLET, "You may not perform subject management functions", "1");
		
	}
	
	protected String getAdminServlet() {
		return SecureController.ADMIN_SERVLET_CODE;
	}
}

