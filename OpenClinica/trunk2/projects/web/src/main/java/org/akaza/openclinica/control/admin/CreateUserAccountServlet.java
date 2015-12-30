/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.util.*;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.TermType;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.dao.hibernate.AuthoritiesDao;
import org.akaza.openclinica.dao.hibernate.GroupAuthenticationDao;
import org.akaza.openclinica.dao.hibernate.RoleDao;
import org.akaza.openclinica.dao.hibernate.UserAccountDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.domain.role.GroupAuthDefinition;
import org.akaza.openclinica.domain.role.UserAccount;
import org.akaza.openclinica.domain.role.UserRoleAccess;
import org.akaza.openclinica.domain.user.AuthoritiesBean;
import org.akaza.openclinica.domain.user.LdapUser;
import org.akaza.openclinica.service.user.LdapUserService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;

/**
 * Servlet for creating a user account.
 * 
 * @author ssachs
 */
public class CreateUserAccountServlet extends SecureController {

    // < ResourceBundle restext;
    Locale locale;

    public static final String INPUT_USER_SOURCE = "userSource";
    public static final String INPUT_USERNAME = "userName";
    public static final String INPUT_FIRST_NAME = "firstName";
    public static final String INPUT_LAST_NAME = "lastName";
    public static final String INPUT_EMAIL = "email";
    public static final String INPUT_INSTITUTION = "institutionalAffiliation";
    public static final String INPUT_STUDY = "activeStudy";
    public static final String INPUT_ROLE = "role";
    public static final String INPUT_TYPE = "type";
    public static final String INPUT_DISPLAY_PWD = "displayPwd";
    public static final String INPUT_RUN_WEBSERVICES = "runWebServices";
    public static final String USER_ACCOUNT_NOTIFICATION = "notifyPassword";


    @Override
    protected void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);
        List<UserRoleAccess> userRoleAccess = new ArrayList<UserRoleAccess>();
        StudyDAO sdao = new StudyDAO(sm.getDataSource());
        // YW 11-28-2007 << list sites under their studies
        ArrayList<StudyBean> all = (ArrayList<StudyBean>) sdao.findAll();
        ArrayList<StudyBean> finalList = new ArrayList<StudyBean>();
        for (StudyBean sb : all) {
            if (!(sb.getParentStudyId() > 0)) {
                finalList.add(sb);
                finalList.addAll(sdao.findAllByParent(sb.getId()));
            }
        }
        addEntityList("studies", finalList, respage.getString("a_user_cannot_be_created_no_study_as_active"), Page.ADMIN_SYSTEM);
      
        Map roleMap = new LinkedHashMap();
        for (Iterator it = getRoles().iterator(); it.hasNext();) {
            org.akaza.openclinica.domain.role.Role role = ( org.akaza.openclinica.domain.role.Role) it.next();
            roleMap.put(role.getId(), role.getRoleName());
        }

        // addEntityList("roles", getRoles(), respage.getString("a_user_cannot_be_created_no_roles_as_role"), Page.ADMIN_SYSTEM);
        request.setAttribute("roles", roleMap);

        ArrayList types = UserType.toArrayList();
        types.remove(UserType.INVALID);
        if (!ub.isTechAdmin()) {
            types.remove(UserType.TECHADMIN);
        }
        addEntityList("types", types, respage.getString("a_user_cannot_be_created_no_user_types_for"), Page.ADMIN_SYSTEM);

        Boolean changeRoles = request.getParameter("changeRoles") == null ? false : Boolean.parseBoolean(request.getParameter("changeRoles"));
        boolean isSite = false;//JN: added for the new user role mgmt stuff
        int activeStudy = fp.getInt(INPUT_STUDY);
   
        request.setAttribute("ldapEnabled", isLdapEnabled());
        request.setAttribute("activeStudy", activeStudy);
        if (!fp.isSubmitted() || changeRoles) {
            String textFields[] = { INPUT_USER_SOURCE, INPUT_USERNAME, INPUT_FIRST_NAME, INPUT_LAST_NAME, INPUT_EMAIL,
                    INPUT_INSTITUTION, INPUT_DISPLAY_PWD };
            fp.setCurrentStringValuesAsPreset(textFields);

            String ddlbFields[] = { INPUT_STUDY, INPUT_ROLE, INPUT_TYPE, INPUT_RUN_WEBSERVICES };
            fp.setCurrentIntValuesAsPreset(ddlbFields);

            HashMap presetValues = fp.getPresetValues();
            // Mantis Issue 6058.
            String sendPwd = SQLInitServlet.getField("user_account_notification");
            fp.addPresetValue(USER_ACCOUNT_NOTIFICATION, sendPwd);
            // 
            setPresetValues(presetValues);
            forwardPage(Page.CREATE_ACCOUNT);
        } else {
            UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
            Validator v = new Validator(request);

            // username must not be blank,
            // must be in the format specified by Validator.USERNAME,
            // and must be unique
            v.addValidation(INPUT_USERNAME, Validator.NO_BLANKS);
            v.addValidation(INPUT_USERNAME, Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 64);
            v.addValidation(INPUT_USERNAME, Validator.IS_A_USERNAME);

            v.addValidation(INPUT_USERNAME, Validator.USERNAME_UNIQUE, udao);

            v.addValidation(INPUT_FIRST_NAME, Validator.NO_BLANKS);
            v.addValidation(INPUT_LAST_NAME, Validator.NO_BLANKS);
            v.addValidation(INPUT_FIRST_NAME, Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 50);
            v.addValidation(INPUT_LAST_NAME, Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 50);

            v.addValidation(INPUT_EMAIL, Validator.NO_BLANKS);
            v.addValidation(INPUT_EMAIL, Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 120);
            v.addValidation(INPUT_EMAIL, Validator.IS_A_EMAIL);

            v.addValidation(INPUT_INSTITUTION, Validator.NO_BLANKS);
            v.addValidation(INPUT_INSTITUTION, Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);

            v.addValidation(INPUT_STUDY, Validator.ENTITY_EXISTS, sdao);
       //     v.addValidation(INPUT_ROLE, Validator.IS_VALID_TERM, TermType.ROLE);

            HashMap errors = v.validate();

            if (errors.isEmpty()) {
                boolean isLdap = fp.getString(INPUT_USER_SOURCE).equals("ldap");
                String password = null;
                String passwordHash = UserAccountBean.LDAP_PASSWORD;
                if (!isLdap){
                    SecurityManager secm = ((SecurityManager) SpringServletAccess.getApplicationContext(context).getBean("securityManager"));
                    password = secm.genPassword();
                    passwordHash = secm.encrytPassword(password, getUserDetails());
                }

                Set< org.akaza.openclinica.domain.role.Role> roles = new HashSet<org.akaza.openclinica.domain.role.Role>();
                org.akaza.openclinica.domain.role.Role role1 = new  org.akaza.openclinica.domain.role.Role();
                role1.setId(fp.getInt(INPUT_ROLE));
                
                RoleDao roledao = (RoleDao)SpringServletAccess.getApplicationContext(context).getBean("roleDAO");
                UserAccount userAccount  = new UserAccount();
                UserType type = UserType.get(fp.getInt("type"));
                if(fp.getInt("type")==1){
                    org.akaza.openclinica.domain.role.Role role2 = new  org.akaza.openclinica.domain.role.Role();
                    
                    role2.setId(1);
                    roles.add(role2);
                    userAccount.addToRoles(role2);
                    role2 = roledao.findById(1);
                        }
                     //   roles.add(role1);
                        
                    
                     role1 = roledao.findById(fp.getInt(INPUT_ROLE));
                     
                userAccount.setUserName(fp.getString(INPUT_USERNAME));
                userAccount.setFirstName(fp.getString(INPUT_FIRST_NAME));
                userAccount.setLastName(fp.getString(INPUT_LAST_NAME));
              //  userAccount.setRole(roles);
          
                userAccount.setEmail(fp.getString(INPUT_EMAIL));
                userAccount.setInstitutionalAffiliation(fp.getString(INPUT_INSTITUTION));
                userAccount.setPasswd(passwordHash);
                userAccount.setPasswdTimestamp(null);
                userAccount.setDateLastvisit(null);
              //  userAccount.setStatus_id(org.akaza.openclinica.domain.Status.AVAILABLE);
                //TODO: add status to userAccount
                userAccount.setStatusId(1);
                userAccount.setPasswdChallengeAnswer("");
                userAccount.setPasswdChallengeQuestion("");
                userAccount.setPhone("");
                //TODO:Look into this...
                userAccount.setOwnerId(ub.getId());
                userAccount.setRunWebservices(fp.getBoolean(INPUT_RUN_WEBSERVICES));
                int studyId = fp.getInt(INPUT_STUDY);
               //userAccount =  getStudyGroupId(studyId,userAccount);
           //     createdUserAccountBean = addActiveStudyRole(createdUserAccountBean, studyId, r);
              
                logger.warn("*** found type: " + fp.getInt("type"));
                logger.warn("*** setting type: " + type.getDescription());
          //      createdUserAccountBean.addUserType(type);
               
                userAccount.setUserTypeId(type.getId());
                userAccount.setActiveStudy(new Integer(studyId));
                userAccount.setEnabled(true);
                userAccount.setAccountNonLocked(true);
                userAccount.setLockCounter(new Integer(0));
                //userAccount.addToRoles(role1);
                //GroupAuthDefinition groupAuthDef =  getStudyGroupId(studyId,userAccount);
                UserRoleAccess uAxs = new UserRoleAccess();
                uAxs.setGroup_id(getStudyGroupId(studyId,userAccount).getId());
               uAxs.setRole_id(role1.getId()); 
               uAxs.setStatus(org.akaza.openclinica.domain.Status.AVAILABLE);
               userRoleAccess.add(uAxs);
               // userAccount.setGroupAuthDefinition(getStudyGroupId(studyId,userAccount));
               userAccount.setUserRoleAccess(userRoleAccess);
               UserAccountDao uaDao = (UserAccountDao)SpringServletAccess.getApplicationContext(context).getBean("userAccountDAO");
               
               UserAccount createdUserAccountBean =   uaDao.saveOrUpdate(userAccount);

           
               
               
               
              // uaDao.runUpdateGroupInfo(userAccount, groupAuthDef.getId(), role1.getId(), userAccount.getId());
               
                AuthoritiesDao authoritiesDao = (AuthoritiesDao) SpringServletAccess.getApplicationContext(context).getBean("authoritiesDao");
                authoritiesDao.saveOrUpdate(new AuthoritiesBean(userAccount.getUserName()));
                String displayPwd = fp.getString(INPUT_DISPLAY_PWD);

                if (createdUserAccountBean.getUserId()>0) {
                    addPageMessage(respage.getString("the_user_account") + "\"" + createdUserAccountBean.getUserName() + "\""
                        + respage.getString("was_created_succesfully"));
                    if (!isLdap) {
                    if ("no".equalsIgnoreCase(displayPwd)) {
                        try {
                            sendNewAccountEmail(createdUserAccountBean, password);
                        } catch (Exception e) {
                            addPageMessage(respage.getString("there_was_an_error_sending_account_creating_mail"));
                        }
                    } else {
                        addPageMessage(respage.getString("user_password") + ":<br/>" + password + "<br/> "
                            + respage.getString("please_write_down_the_password_and_provide"));
                    }
                    }
                } else {
                    addPageMessage(respage.getString("the_user_account") + "\"" + createdUserAccountBean.getUserName() + "\""
                        + respage.getString("could_not_created_due_database_error"));
                }
                if (createdUserAccountBean.getUserId()>0) {
                    request.setAttribute(ViewUserAccountServlet.ARG_USER_ID, new Integer(createdUserAccountBean.getUserId()).toString());
                    forwardPage(Page.VIEW_USER_ACCOUNT_SERVLET);
                } else {
                    forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET);
                }
            } else {
                String textFields[] = { INPUT_USERNAME, INPUT_FIRST_NAME, INPUT_LAST_NAME, INPUT_EMAIL, INPUT_INSTITUTION, INPUT_DISPLAY_PWD };
                fp.setCurrentStringValuesAsPreset(textFields);

                String ddlbFields[] = { INPUT_STUDY, INPUT_ROLE, INPUT_TYPE, INPUT_RUN_WEBSERVICES };
                fp.setCurrentIntValuesAsPreset(ddlbFields);

                HashMap presetValues = fp.getPresetValues();
                setPresetValues(presetValues);

                setInputMessages(errors);
                addPageMessage(respage.getString("there_were_some_errors_submission") + respage.getString("see_below_for_details"));

                forwardPage(Page.CREATE_ACCOUNT);
            }
        }
    }

    protected GroupAuthDefinition getStudyGroupId(int studyId,UserAccount userAccount){
       ArrayList<GroupAuthDefinition> grpAuth = new ArrayList<GroupAuthDefinition>();
        StudyDAO studyDAO = new StudyDAO(sm.getDataSource());
        StudyBean studyBean =  (StudyBean)studyDAO.findByPK(studyId);
        GroupAuthenticationDao groupAuthDao = (GroupAuthenticationDao)SpringServletAccess.getApplicationContext(context).getBean("groupAuthenticationDefinitionDAO");
        GroupAuthDefinition groupDef = new GroupAuthDefinition();
        groupDef = groupAuthDao.findByColumnName(studyBean.getOid(), "oc_oid_reference_list");
        //userAccount.addToGroupDefinitions(groupDef);
    //   System.out.println(groupDef.getParentGroup());
        grpAuth.add(groupDef);
        return groupDef;

    }

    protected boolean isLdapEnabled() {
        LdapUserService ldapUserService = SpringServletAccess.getApplicationContext(context).getBean(LdapUserService.class);
        return ldapUserService.isLdapServerConfigured();
    }

    /**
     * Reusing the <code>setPresetValues</code> method to process a <code>ldapUser</code> which was previously stored
     * in the session scope.
     * @param presetValues
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected void setPresetValues(HashMap presetValues) {
        HashMap map = presetValues;
        if (isLdapEnabled()) {
            LdapUser ldapUser = (LdapUser) session.getAttribute("ldapUser");
            if (ldapUser != null) {
                session.removeAttribute("ldapUser");
                if (map == null) {
                    map = new HashMap();
                }
                map.put("userName", ldapUser.getUsername());
                map.put("firstName", ldapUser.getFirstName());
                map.put("lastName", ldapUser.getLastName());
                map.put("email", ldapUser.getEmail());
                map.put("institutionalAffiliation", ldapUser.getOrganization());
            }
        }
        super.setPresetValues(map);
    }

    protected int getNewRoleIds(String roleName,boolean isSite){
        Role coordinator = Role.COORDINATOR;
        Role studyDirector = Role.STUDYDIRECTOR;
        //Role dataSpecialist = Role.;
        Role monitor = Role.MONITOR;
        Role ra = Role.RESEARCHASSISTANT;
        Role investigator = Role.INVESTIGATOR;
        int roleId = 1;
      if(roleName.equalsIgnoreCase(coordinator.getName())){
          roleId = 3;
      }
      else if(roleName.equalsIgnoreCase(studyDirector.getName())){
          roleId = 4;
      }
      else if(roleName.equalsIgnoreCase(ra.getName())&&isSite){
          roleId = 9;
          
      }
      else if(roleName.equalsIgnoreCase(ra.getName())&&!isSite){
          roleId = 6;
          
      }
      else if(roleName.equalsIgnoreCase(monitor.getName())){
          roleId = 7;
      }
      else if(roleName.equalsIgnoreCase(investigator.getName())&& isSite){
          roleId = 8;
      }
      else if(roleName.equalsIgnoreCase(investigator.getName())&&!isSite){
          roleId=5;
      }
      return roleId;
    }
    
    protected ArrayList getRoles() {

        //org.akaza.openclinica.domain.role.
        RoleDao roleDao = (RoleDao)SpringServletAccess.getApplicationContext(context).getBean("roleDAO");
        
        //ArrayList roles = org.akaza.openclinica.domain.role.Role.toArrayList();
       // roles.remove(Role.ADMIN);

        return (ArrayList) roleDao.findAll();
    }

    protected UserAccountBean addActiveStudyRole(UserAccountBean createdUserAccountBean, int studyId, Role r) {
        createdUserAccountBean.setActiveStudyId(studyId);

        StudyUserRoleBean activeStudyRole = new StudyUserRoleBean();

        activeStudyRole.setStudyId(studyId);
        activeStudyRole.setRoleName(r.getName());
        activeStudyRole.setStatus(Status.AVAILABLE);
        activeStudyRole.setOwner(ub);

        createdUserAccountBean.addRole(activeStudyRole);

        return createdUserAccountBean;
    }

    /**
     * @deprecated Use {@link #sendNewAccountEmail(UserAccount,String)} instead
     */
    private void sendNewAccountEmail(UserAccountBean createdUserAccountBean, String password) throws Exception {
        sendNewAccountEmail(createdUserAccountBean, password);
    }

    private void sendNewAccountEmail(UserAccount createdUserAccountBean, String password) throws Exception {
        logger.info("Sending account creation notification to " + createdUserAccountBean.getUserName());

        String body = resword.getString("dear") + " " + createdUserAccountBean.getFirstName() + " " + createdUserAccountBean.getLastName() + ",\n";
        body += restext.getString("a_new_user_account_has_been_created_for_you") + "\n\n";
        body += resword.getString("user_name") + ": " + createdUserAccountBean.getUserName() + "\n";
        body += resword.getString("password") + ": " + password + "\n\n";
        body += restext.getString("please_test_your_login_information_and_let") + "\n";
        body += SQLInitServlet.getField("sysURL");
        body += " . ";
        // body += restext.getString("openclinica_system_administrator");

        sendEmail(createdUserAccountBean.getEmail().trim(), restext.getString("your_new_openclinica_account"), body, false);
    }

    @Override
    protected String getAdminServlet() {
        return SecureController.ADMIN_SERVLET_CODE;
    }
}
