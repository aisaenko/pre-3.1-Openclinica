/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.login;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.admin.EventStatusStatisticsTableFactory;
import org.akaza.openclinica.control.admin.SiteStatisticsTableFactory;
import org.akaza.openclinica.control.admin.StudyStatisticsTableFactory;
import org.akaza.openclinica.control.admin.StudySubjectStatusStatisticsTableFactory;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.control.submit.ListStudySubjectTableFactory;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.hibernate.UserAccountDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.domain.role.GroupAuthDefinition;
import org.akaza.openclinica.domain.role.UserAccount;
import org.akaza.openclinica.domain.role.UserRoleAccess;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.filter.OpenClinicaGroupVoter;
import org.akaza.openclinica.web.filter.OpenClinicaURLVoter;
import org.akaza.openclinica.web.table.sdv.SDVUtil;
import org.akaza.openclinica.web.util.PageMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @author jxu
 *
 * Processes the request of changing current study
 */
public class ChangeStudyServlet extends SecureController {
    /**
     * Checks whether the user has the correct privilege
     */

    Locale locale;
    private StudyEventDefinitionDAO studyEventDefinitionDAO;
    private SubjectDAO subjectDAO;
    private StudySubjectDAO studySubjectDAO;
    private StudyEventDAO studyEventDAO;
    private StudyGroupClassDAO studyGroupClassDAO;
    private SubjectGroupMapDAO subjectGroupMapDAO;
    private StudyDAO studyDAO;
    private EventCRFDAO eventCRFDAO;
    private EventDefinitionCRFDAO eventDefintionCRFDAO;
    private StudyGroupDAO studyGroupDAO;
    private DiscrepancyNoteDAO discrepancyNoteDAO;

    // < ResourceBundlerestext;

    @Override
    public void processRequest() throws Exception {

        String action = request.getParameter("action");// action sent by user
        UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
        
        
        
        StudyDAO sdao = new StudyDAO(sm.getDataSource());
        UserAccount userAccnt =(UserAccount) request.getSession().getAttribute(OpenClinicaURLVoter.USER_BEAN_NAME);
        //List of valid studies...from groups
        ArrayList<StudyBean> studies =   getRequiredStudies((List<String>)request.getAttribute(OpenClinicaGroupVoter.ALLOWED_GROUP_OIDS),sdao);
        ArrayList<GroupAuthDefinition> groups = (ArrayList<GroupAuthDefinition>)request.getAttribute(OpenClinicaGroupVoter.GROUPS);
  
        List<UserRoleAccess> userStudyRoles = userAccnt.getUserRoleAccess();
      
        LinkedHashMap<StudyBean,org.akaza.openclinica.domain.role.UserRoleAccess> userGroupRoles = new LinkedHashMap<StudyBean,org.akaza.openclinica.domain.role.UserRoleAccess>();
        Integer groupId = null,roleId = null;
        UserRoleAccess temp = null,tempSite = null;
        String group_name = null;
        for(StudyBean studyBean:studies)
        {
        	group_name =groups.get(studies.indexOf(studyBean)).getGroup_name();
           if( group_name!= null && group_name.equalsIgnoreCase("study"))
           {
               Iterator<UserRoleAccess> it = userStudyRoles.iterator();
               while(it.hasNext())
               {
                   temp = it.next();
                   if(temp.getGroup_id()!=null){
                   if(temp.getGroup_id().equals(groups.get(studies.indexOf(studyBean)).getId())){
                   userGroupRoles.put(studyBean, temp);
                  
                   //get user name and assigned
                   }
                   }
               }
           }
       
            else if(group_name != null && group_name.equalsIgnoreCase("Site"))
            {
                Iterator<UserRoleAccess> it = userStudyRoles.iterator();
                boolean flag = true;
                while(it.hasNext() && flag){
                    tempSite = it.next();
                    if(tempSite.getGroup_id()!=null){
                    if(tempSite.getGroup_id().equals(groups.get(studies.indexOf(studyBean)).getId())){
                        flag = false;
                    }
                    }
                }
                if(flag == true)tempSite = temp;
                userGroupRoles.put(studyBean,tempSite);
            }
         }
        
        
        if (StringUtil.isBlank(action)) {
            request.setAttribute("studies", userGroupRoles);

            forwardPage(Page.CHANGE_STUDY);
        } else {
            if ("confirm".equalsIgnoreCase(action)) {
                logger.info("confirm");
                confirmChangeStudy(studies, userGroupRoles, sdao);

            } else if ("submit".equalsIgnoreCase(action)) {
                logger.info("submit");
                changeStudy();
            }
        }

    }
/**
 * 
 * @param groupOids
 * @param studydao
 * @return
 */
    //JN:not the best way of doing it, the prepared statements dont allow array as parameters for IN clause and hence left with no choice.
    private ArrayList<StudyBean> getRequiredStudies(List<String> groupOids,StudyDAO studydao) {
       ArrayList<StudyBean> studyBeans = new ArrayList<StudyBean>();
        for(String groupOID:groupOids){
            studyBeans.add(studydao.findByOid(groupOID));
        }
        return studyBeans;
    }

    private void confirmChangeStudy(ArrayList studies, LinkedHashMap userGroupRoles, StudyDAO sdao) throws Exception {
        Validator v = new Validator(request);
        FormProcessor fp = new FormProcessor(request);
        v.addValidation("studyId", Validator.IS_AN_INTEGER);
        
        errors = v.validate();

        if (!errors.isEmpty()) {
            request.setAttribute("studies", studies);
            forwardPage(Page.CHANGE_STUDY);
        } else {
            int studyId = fp.getInt("studyId");
            logger.info("new study id:" + studyId);
            
            
               
                    request.setAttribute("studyId", new Integer(studyId));
                    session.setAttribute("studyWithRole",  userGroupRoles.get(studies.indexOf(sdao.findByPK(studyId))));
                    request.setAttribute("currentStudy", currentStudy);
                    forwardPage(Page.CHANGE_STUDY_CONFIRM);
                    return;
                    
              
        
        }
    }

    private void changeStudy() throws Exception {
        Validator v = new Validator(request);
        FormProcessor fp = new FormProcessor(request);
        int studyId = fp.getInt("studyId");
        int prevStudyId = currentStudy.getId();

        StudyDAO sdao = new StudyDAO(sm.getDataSource());
        StudyBean current = (StudyBean) sdao.findByPK(studyId);

        // reset study parameters -jxu 02/09/2007
        StudyParameterValueDAO spvdao = new StudyParameterValueDAO(sm.getDataSource());

        ArrayList studyParameters = spvdao.findParamConfigByStudy(current);
        current.setStudyParameters(studyParameters);
        int parentStudyId = currentStudy.getParentStudyId()>0?currentStudy.getParentStudyId():currentStudy.getId();
        StudyParameterValueBean parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "subjectIdGeneration");
        current.getStudyParameterConfig().setSubjectIdGeneration(parentSPV.getValue());
        String idSetting = current.getStudyParameterConfig().getSubjectIdGeneration();
        if (idSetting.equals("auto editable") || idSetting.equals("auto non-editable")) {
            int nextLabel = this.getStudySubjectDAO().findTheGreatestLabel() + 1;
            request.setAttribute("label", new Integer(nextLabel).toString());
        }

        StudyConfigService scs = new StudyConfigService(sm.getDataSource());
        if (current.getParentStudyId() <= 0) {// top study
            scs.setParametersForStudy(current);

        } else {
            // YW <<
            if (current.getParentStudyId() > 0) {
                current.setParentStudyName(((StudyBean) sdao.findByPK(current.getParentStudyId())).getName());

            }
            // YW 06-12-2007>>
            scs.setParametersForSite(current);

        }
        if (current.getStatus().equals(Status.DELETED) || current.getStatus().equals(Status.AUTO_DELETED)) {
            session.removeAttribute("studyWithRole");
            addPageMessage(restext.getString("study_choosed_removed_restore_first"));
        } else {
            session.setAttribute("study", current);
            
            currentStudy = current;
            
            UserAccountDao userDao = (UserAccountDao)SpringServletAccess.getApplicationContext(context).getBean("userAccountDAO");
            UserAccount userAccnt =(UserAccount) request.getSession().getAttribute(OpenClinicaURLVoter.USER_BEAN_NAME);
            userAccnt.setActiveStudy(current.getId());
            userAccnt.setUpdateId(userAccnt.getId());
            session.setAttribute(OpenClinicaURLVoter.USER_BEAN_NAME, userAccnt);
           // userDao.update(userAccnt);
            //update current role
            //session.setAttribute(OpenClinicaURLVoter.USER_BEAN_NAME, userDao);
           
            

            currentRole = (StudyUserRoleBean) session.getAttribute("studyWithRole");
            session.setAttribute("userRole", currentRole);
            session.removeAttribute("studyWithRole");
            addPageMessage(restext.getString("current_study_changed_succesfully"));
        }
        ub.incNumVisitsToMainMenu();
        // YW 2-18-2008, if study has been really changed <<
        if (prevStudyId != studyId) {
            session.removeAttribute("eventsForCreateDataset");
            session.setAttribute("tableFacadeRestore", "false");
        }
        request.setAttribute("studyJustChanged", "yes");
        // YW >>

        //Integer assignedDiscreRpancies = getDiscrepancyNoteDAO().countAllItemDataByStudyAndUser(currentStudy, ub);
        Integer assignedDiscrepancies = getDiscrepancyNoteDAO().getViewNotesCountWithFilter(" AND dn.assigned_user_id ="
                + ub.getId() + " AND (dn.resolution_status_id=1 OR dn.resolution_status_id=2 OR dn.resolution_status_id=3)", currentStudy);
        request.setAttribute("assignedDiscrepancies", assignedDiscrepancies == null ? 0 : assignedDiscrepancies);
        request.getSession().setAttribute(PageMenu.PAGE_MENU_RECALCULATE,"true");
        response.sendRedirect(request.getContextPath()+Page.MENU_SERVLET.getFileName());

    }

    private void setupSubjectSDVTable() {

        request.setAttribute("studyId", currentStudy.getId());
        String sdvMatrix = getSDVUtil().renderEventCRFTableWithLimit(request, currentStudy.getId(), "");
        request.setAttribute("sdvMatrix", sdvMatrix);
    }

    private void setupStudySubjectStatusStatisticsTable() {

        StudySubjectStatusStatisticsTableFactory factory = new StudySubjectStatusStatisticsTableFactory();
        factory.setStudySubjectDao(getStudySubjectDAO());
        factory.setCurrentStudy(currentStudy);
        factory.setStudyDao(getStudyDAO());
        String studySubjectStatusStatistics = factory.createTable(request, response).render();
        request.setAttribute("studySubjectStatusStatistics", studySubjectStatusStatistics);
    }

    private void setupSubjectEventStatusStatisticsTable() {

        EventStatusStatisticsTableFactory factory = new EventStatusStatisticsTableFactory();
        factory.setStudySubjectDao(getStudySubjectDAO());
        factory.setCurrentStudy(currentStudy);
        factory.setStudyEventDao(getStudyEventDAO());
        factory.setStudyDao(getStudyDAO());
        String subjectEventStatusStatistics = factory.createTable(request, response).render();
        request.setAttribute("subjectEventStatusStatistics", subjectEventStatusStatistics);
    }

    private void setupStudySiteStatisticsTable() {

        SiteStatisticsTableFactory factory = new SiteStatisticsTableFactory();
        factory.setStudySubjectDao(getStudySubjectDAO());
        factory.setCurrentStudy(currentStudy);
        factory.setStudyDao(getStudyDAO());
        String studySiteStatistics = factory.createTable(request, response).render();
        request.setAttribute("studySiteStatistics", studySiteStatistics);

    }

    private void setupStudyStatisticsTable() {

        StudyStatisticsTableFactory factory = new StudyStatisticsTableFactory();
        factory.setStudySubjectDao(getStudySubjectDAO());
        factory.setCurrentStudy(currentStudy);
        factory.setStudyDao(getStudyDAO());
        String studyStatistics = factory.createTable(request, response).render();
        request.setAttribute("studyStatistics", studyStatistics);

    }

    private void setupListStudySubjectTable() {

        ListStudySubjectTableFactory factory = new ListStudySubjectTableFactory(true);
        factory.setStudyEventDefinitionDao(getStudyEventDefinitionDao());
        factory.setSubjectDAO(getSubjectDAO());
        factory.setStudySubjectDAO(getStudySubjectDAO());
        factory.setStudyEventDAO(getStudyEventDAO());
        factory.setStudyBean(currentStudy);
        factory.setStudyGroupClassDAO(getStudyGroupClassDAO());
        factory.setSubjectGroupMapDAO(getSubjectGroupMapDAO());
        factory.setStudyDAO(getStudyDAO());
        factory.setCurrentRole(currentRole);
        factory.setCurrentUser(ub);
        factory.setEventCRFDAO(getEventCRFDAO());
        factory.setEventDefintionCRFDAO(getEventDefinitionCRFDAO());
        factory.setStudyGroupDAO(getStudyGroupDAO());
        String findSubjectsHtml = factory.createTable(request, response).render();
        request.setAttribute("findSubjectsHtml", findSubjectsHtml);
    }

    public StudyEventDefinitionDAO getStudyEventDefinitionDao() {
        studyEventDefinitionDAO = studyEventDefinitionDAO == null ? new StudyEventDefinitionDAO(sm.getDataSource()) : studyEventDefinitionDAO;
        return studyEventDefinitionDAO;
    }

    public SubjectDAO getSubjectDAO() {
        subjectDAO = this.subjectDAO == null ? new SubjectDAO(sm.getDataSource()) : subjectDAO;
        return subjectDAO;
    }

    public StudySubjectDAO getStudySubjectDAO() {
        studySubjectDAO = this.studySubjectDAO == null ? new StudySubjectDAO(sm.getDataSource()) : studySubjectDAO;
        return studySubjectDAO;
    }

    public StudyGroupClassDAO getStudyGroupClassDAO() {
        studyGroupClassDAO = this.studyGroupClassDAO == null ? new StudyGroupClassDAO(sm.getDataSource()) : studyGroupClassDAO;
        return studyGroupClassDAO;
    }

    public SubjectGroupMapDAO getSubjectGroupMapDAO() {
        subjectGroupMapDAO = this.subjectGroupMapDAO == null ? new SubjectGroupMapDAO(sm.getDataSource()) : subjectGroupMapDAO;
        return subjectGroupMapDAO;
    }

    public StudyEventDAO getStudyEventDAO() {
        studyEventDAO = this.studyEventDAO == null ? new StudyEventDAO(sm.getDataSource()) : studyEventDAO;
        return studyEventDAO;
    }

    public StudyDAO getStudyDAO() {
        studyDAO = this.studyDAO == null ? new StudyDAO(sm.getDataSource()) : studyDAO;
        return studyDAO;
    }

    public EventCRFDAO getEventCRFDAO() {
        eventCRFDAO = this.eventCRFDAO == null ? new EventCRFDAO(sm.getDataSource()) : eventCRFDAO;
        return eventCRFDAO;
    }

    public EventDefinitionCRFDAO getEventDefinitionCRFDAO() {
        eventDefintionCRFDAO = this.eventDefintionCRFDAO == null ? new EventDefinitionCRFDAO(sm.getDataSource()) : eventDefintionCRFDAO;
        return eventDefintionCRFDAO;
    }

    public StudyGroupDAO getStudyGroupDAO() {
        studyGroupDAO = this.studyGroupDAO == null ? new StudyGroupDAO(sm.getDataSource()) : studyGroupDAO;
        return studyGroupDAO;
    }

    public DiscrepancyNoteDAO getDiscrepancyNoteDAO() {
        discrepancyNoteDAO = this.discrepancyNoteDAO == null ? new DiscrepancyNoteDAO(sm.getDataSource()) : discrepancyNoteDAO;
        return discrepancyNoteDAO;
    }

    public SDVUtil getSDVUtil() {
        return (SDVUtil) SpringServletAccess.getApplicationContext(context).getBean("sdvUtil");
    }

}
