/*
 * Created on Sep 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.admin.AuditDAO;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.Locale;

/**
 * @author thickerson
 * 
 * 
 */
public class StudyAuditLogServlet extends SecureController {

    Locale locale;

    // <ResourceBundle resword,resexception,respage;

    public static String getLink(int userId) {
        return "AuditLogStudy";
    }

    /*
     * (non-Javadoc) Assume that we get the user id automatically. We will jump
     * from the edit user page if the user is an admin, they can get to see the
     * users' log
     * @see org.akaza.openclinica.control.core.SecureController#processRequest()
     */

    /*
     * (non-Javadoc) redo this servlet to run the audits per study subject for
     * the study; need to add a studyId param and then use the
     * StudySubjectDAO.findAllByStudyOrderByLabel() method to grab a lot of
     * study subject beans and then return them much like in
     * ViewStudySubjectAuditLogServet.process() currentStudy instead of studyId?
     */
    @Override
    protected void processRequest() throws Exception {
        int studyId = currentStudy.getId();

        StudySubjectDAO subdao = new StudySubjectDAO(sm.getDataSource());
        SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
        AuditDAO adao = new AuditDAO(sm.getDataSource());
        UserAccountDAO uadao = new UserAccountDAO(sm.getDataSource());

        FormProcessor fp = new FormProcessor(request);

        StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
        StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
        EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
        EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
        StudyDAO studydao = new StudyDAO(sm.getDataSource());
        CRFDAO cdao = new CRFDAO(sm.getDataSource());
        CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());

        StudyAuditLogTableFactory factory = new StudyAuditLogTableFactory();
        factory.setSubjectDao(sdao);
        factory.setStudySubjectDao(subdao);
        factory.setUserAccountDao(uadao);
        factory.setCurrentStudy(currentStudy);

        String auditLogsHtml = factory.createTable(request, response).render();
        request.setAttribute("auditLogsHtml", auditLogsHtml);

        forwardPage(Page.AUDIT_LOGS_STUDY);

    }

}
