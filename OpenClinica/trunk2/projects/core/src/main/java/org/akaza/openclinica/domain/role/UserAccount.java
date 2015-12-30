package org.akaza.openclinica.domain.role;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.akaza.openclinica.domain.MutableDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
@Entity
@Table(name = "user_account")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "user_account_user_id_seq") })
/**
 * @author jnyayapathi  
 */
public class UserAccount implements MutableDomainObject{

    
    private Integer user_id;
    private String userName;
    private String firstName;
    private String lastName;
    private Boolean accountNonLocked;
    private String passwd;
    private String email;
    private Integer activeStudy;
    private String institutionalAffiliation;
    private Integer statusId;
    private Integer ownerId;
    private String passwdChallengeQuestion;
    private String passwdChallengeAnswer;
    private String phone;
    private Integer userTypeId;
    private Integer updateId;
    private Boolean enabled;
    private Integer lockCounter;
    private Boolean runWebservices;
    private Date passwdTimestamp;
    private Date dateUpdated;
    private Date dateLastvisit;
    @Transient
    private Role currentRole;
    HashSet<UserAccount> accounts = new HashSet<UserAccount>();
 
 
    
    
    public UserAccount(){
        this.accounts.add(this);
    }
    
    public UserAccount(String user_name,List<Role> role){
        this.setUserName(userName);
        this.setRole(role);
    }
  
    public UserAccount(List<Role> role){
        this.setRole(role);
    }
    
    //JN:'roles' as a parameter in this is a misnomer, it actually represents only one role.
    @SuppressWarnings("unchecked")
    public void addToRoles(Role roles){
      
      // roles.setUserAccount(this.accounts); 
       getRole().add(roles);
       
        
    }
    
    //JN:''
    public void addToGroupDefinitions(GroupAuthDefinition groupAuthDefinitions){
     
      // groupAuthDefinitions.setUserAccount(accounts);
       getGroupAuthDefinition().add(groupAuthDefinitions);
        
        
        
    }
        
    
    public void addToRolesGroups(Role roles,GroupAuthDefinition groupAuthDefinitions){
        accounts.add(this);
        roles.setUserAccount(accounts);
        groupAuthDefinitions.setUserAccount(accounts);
        getRole().add(roles);
        
        getGroupAuthDefinition().add(groupAuthDefinitions);
        
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    @Column(name="active_study")
    public Integer getActiveStudy() {
        return activeStudy;
    }
    public void setActiveStudy(Integer active_study) {
        this.activeStudy = active_study;
    }
    @Column(name="institutional_affiliation")
    
    public String getInstitutionalAffiliation() {
        return institutionalAffiliation;
    }
    public void setInstitutionalAffiliation(String institutional_affiliation) {
        this.institutionalAffiliation = institutional_affiliation;
    }
    @Column(name="status_id")
    
    public Integer getStatusId() {
        return statusId;
    }
    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }
    @Column(name="owner_id")
    
    public Integer getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(Integer owner_id) {
        this.ownerId = owner_id;
    }
    @Column(name="passwd_challenge_question")
    public String getPasswdChallengeQuestion() {
        return passwdChallengeQuestion;
    }
    public void setPasswdChallengeQuestion(String passwd_challenge_question) {
        this.passwdChallengeQuestion = passwd_challenge_question;
    }
    @Column(name="passwd_challenge_answer")
    
    public String getPasswdChallengeAnswer() {
        return passwdChallengeAnswer;
    }
    public void setPasswdChallengeAnswer(String passwd_challenge_answer) {
        this.passwdChallengeAnswer = passwd_challenge_answer;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    @Column(name="user_type_id")
    
    public Integer getUserTypeId() {
        return userTypeId;
    }
    public void setUserTypeId(Integer user_type_id) {
        this.userTypeId = user_type_id;
    }
    @Column(name="update_id")
    
    public Integer getUpdateId() {
        return updateId;
    }
    public void setUpdateId(Integer update_id) {
        this.updateId = update_id;
    }
    public Boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    @Column(name="lock_counter")
    
    public Integer getLockCounter() {
        return lockCounter;
    }
    public void setLockCounter(Integer lock_counter) {
        this.lockCounter = lock_counter;
    }
    
    @Column(name="run_webservices")
    
    public Boolean getRunWebservices() {
        return runWebservices;
    }
    public void setRunWebservices(Boolean run_webservices) {
        this.runWebservices = run_webservices;
    }
    public String getPasswd() {
        return passwd;
    }
    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
    @Column(name="account_non_locked")
    
    public Boolean getAccountNonLocked() {
        return accountNonLocked;
    }
    public void setAccountNonLocked(Boolean account_non_locked) {
        this.accountNonLocked = account_non_locked;
    }
 
    @Id
    @GeneratedValue(generator = "id-generator")
    @Column(name="user_id")
    public Integer getUserId() {
        return user_id;
    }
    public void setUserId(Integer user_id) {
        this.user_id = user_id;
    }
    @Column(name="user_name")
    public String getUserName() {
        return userName;
    }
    public void setUserName(String user_name) {
        this.userName = user_name;
    }
    @Column(name="first_name")
    
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String first_name) {
        this.firstName = first_name;
    }
    @Column(name="last_name")
    
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String last_name) {
        this.lastName = last_name;
    }


    @Transient
    public Integer getId() {
        // TODO Auto-generated method stub
        return null;
    }
    public void setId(Integer id) {
        // TODO Auto-generated method stub
        
    }
    @Transient
    public Integer getVersion() {
        // TODO Auto-generated method stub
        return null;
    }
    public void setVersion(Integer version) {
        // TODO Auto-generated method stub
        
    }
   private List<Role> role = new ArrayList<Role>();
  
    @ManyToMany(targetEntity=org.akaza.openclinica.domain.role.Role.class, fetch=FetchType.EAGER,cascade={CascadeType.PERSIST,CascadeType.MERGE}) 
    @JoinTable(name="user_role_access", joinColumns= {  @JoinColumn(name="user_id") },inverseJoinColumns = { @JoinColumn(name = "role_id")})
    public List<Role> getRole() {
        return role;
    }
    public void setRole(List<Role> role) {
        this.role = role;
    }
    private List<GroupAuthDefinition> groupAuthDefinition = new ArrayList<GroupAuthDefinition>();
    
    @ManyToMany(targetEntity=org.akaza.openclinica.domain.role.GroupAuthDefinition.class, fetch=FetchType.EAGER,cascade={CascadeType.PERSIST,CascadeType.MERGE}) 
    @JoinTable(name="user_role_access",joinColumns= {  @JoinColumn(name="user_id") },inverseJoinColumns = { @JoinColumn(name = "group_id")})
    public List<GroupAuthDefinition> getGroupAuthDefinition() {
        return groupAuthDefinition;
    }
    public void setGroupAuthDefinition(List<GroupAuthDefinition> groupAuthDefinition) {
        this.groupAuthDefinition = groupAuthDefinition;
    }
    
    
    
    public void setPasswdTimestamp(Date passwd_timestamp) {
        this.passwdTimestamp = passwd_timestamp;
    }
    @Column(name="passwd_timestamp")
    
    public Date getPasswdTimestamp() {
        return passwdTimestamp;
    }
    public void setDateUpdated(Date date_updated) {
        this.dateUpdated = date_updated;
    }
    @Column(name="date_updated")
    
    public Date getDateUpdated() {
        return dateUpdated;
    }
    @Column(name="date_lastvisit")
    
    public void setDateLastvisit(Date date_lastvisit) {
        this.dateLastvisit = date_lastvisit;
    }
    public Date getDateLastvisit() {
        return dateLastvisit;
    }
    
    private List<UserRoleAccess> userRoleAccess = new ArrayList<UserRoleAccess>();
    
    @OneToMany(fetch=FetchType.EAGER,cascade={CascadeType.ALL})
    @JoinColumn(name = "user_id")
    public List<UserRoleAccess> getUserRoleAccess() {
        return userRoleAccess;
    }
    public void setUserRoleAccess(List<UserRoleAccess> userRoleAccess) {
        this.userRoleAccess = userRoleAccess;
    }

	/**
	 * @param currentRole the currentRole to set
	 */
    public void setCurrentRole(Role currentRole) {
		this.currentRole = currentRole;
	}

	/**
	 * @return the currentRole
	 */
	@Transient
	public Role getCurrentRole() {
		return currentRole;
	}
    
}
