package org.akaza.openclinica.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.BindingResult;
import org.akaza.openclinica.dao.hibernate.StudyModuleStatusDao;
import org.akaza.openclinica.dao.hibernate.RuleDao;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.dao.rule.RuleDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.domain.managestudy.StudyModuleStatus;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.apache.commons.dbcp.BasicDataSource;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: sshamim
 * Date: Jan 22, 2009
 * Time: 6:52:16 PM
 * Will manage the Study creation process
 */
@Controller("studyModuleController")
@RequestMapping("/studymodule")
@SessionAttributes("studyModuleStatus")
public class StudyModuleController {
    @Autowired
    @Qualifier("sidebarInit")
    private SidebarInit sidebarInit;

    @Autowired
    @Qualifier("studyModuleStatusDao")
    private StudyModuleStatusDao studyModuleStatusDao;

    @Autowired
    @Qualifier("ruleDao")
    private RuleDao ruleDao;

    @Autowired
    @Qualifier("dataSource")
    private BasicDataSource dataSource;

    private EventDefinitionCRFDAO eventDefinitionCRFDao;
    private CRFDAO crfDao;
    private SubjectGroupMapDAO subjectGroupDao;
    private StudyDAO studyDao;
    private UserAccountDAO userDao;

    public StudyModuleController() {

    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelMap handleMainPage(HttpServletRequest request) {
        ModelMap map = new ModelMap();
        setUpSidebar(request);
        StudyBean currentStudy = (StudyBean) request.getSession().getAttribute("study");

        eventDefinitionCRFDao = new EventDefinitionCRFDAO(dataSource);
        crfDao = new CRFDAO(dataSource);
        subjectGroupDao = new SubjectGroupMapDAO(dataSource);
        studyDao = new StudyDAO(dataSource);
        userDao = new UserAccountDAO(dataSource);

        StudyModuleStatus sms = studyModuleStatusDao.findById(1);
        int crfCount = crfDao.findAll().size();
        int eventDefinitionCount = eventDefinitionCRFDao.findAllActiveByStudy(currentStudy).size();
        int subjectGroupCount = subjectGroupDao.findAll().size();
        int ruleCount = ruleDao.count().intValue();
        int siteCount = studyDao.findAllSiteIdsByStudy(currentStudy).size();
        int userCount = userDao.findAll().size();

        if (sms.getCrf() != 3 && crfCount > 0) {
            sms.setCrf(StudyModuleStatus.IN_PROGRESS);
        }
        if (sms.getEventDefinition() != 3 && eventDefinitionCount > 0) {
            sms.setEventDefinition(StudyModuleStatus.IN_PROGRESS);
        }
        if (sms.getSubjectGroup() != 3 && subjectGroupCount > 0) {
            sms.setSubjectGroup(StudyModuleStatus.IN_PROGRESS);
        }
        if (sms.getRule() != 3 && ruleCount > 0) {
            sms.setRule(StudyModuleStatus.IN_PROGRESS);
        }
        if (sms.getSite() != 3 && siteCount > 0) {
            sms.setSite(StudyModuleStatus.IN_PROGRESS);
        }
        if (sms.getUsers() != 3 && userCount > 0) {
            sms.setUsers(StudyModuleStatus.IN_PROGRESS);
        }

        map.addObject(sms);
        map.addAttribute("crfCount", crfCount);
        map.addAttribute("eventDefinitionCount", eventDefinitionCount);
        map.addAttribute("subjectGroupCount", subjectGroupCount);
        map.addAttribute("ruleCount", ruleCount);
        map.addAttribute("siteCount", siteCount);
        map.addAttribute("userCount", userCount);

        return map;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(@ModelAttribute("studyModuleStatus") StudyModuleStatus studyModuleStatus,
                                BindingResult result, SessionStatus status, HttpServletRequest request) {
        int study = studyModuleStatus.getStudy();
        studyModuleStatusDao.saveOrUpdate(studyModuleStatus);
        status.setComplete();
        
        return "redirect:studymodule?decorator=mydecorator";
    }


    private void setUpSidebar(HttpServletRequest request){
        if(sidebarInit.getAlertsBoxSetup() ==
          SidebarEnumConstants.OPENALERTS){
            request.setAttribute("alertsBoxSetup",true);
        }

        if(sidebarInit.getInfoBoxSetup() == SidebarEnumConstants.OPENINFO){
            request.setAttribute("infoBoxSetup",true);
        }
        if(sidebarInit.getInstructionsBoxSetup() == SidebarEnumConstants.OPENINSTRUCTIONS){
            request.setAttribute("instructionsBoxSetup",true);
        }

        if(! (sidebarInit.getEnableIconsBoxSetup() ==
          SidebarEnumConstants.DISABLEICONS)){
            request.setAttribute("enableIconsBoxSetup",true);
        }
    }

    public SidebarInit getSidebarInit() {
        return sidebarInit;
    }

    public void setSidebarInit(SidebarInit sidebarInit) {
        this.sidebarInit = sidebarInit;
    }

    public StudyModuleStatusDao getStudyModuleStatusDao() {
        return studyModuleStatusDao;
    }

    public BasicDataSource getDataSource() {
        return dataSource;
    }
}
