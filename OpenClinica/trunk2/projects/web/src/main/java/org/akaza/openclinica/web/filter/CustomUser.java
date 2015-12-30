package org.akaza.openclinica.web.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.domain.role.GroupAuthDefinition;
import org.akaza.openclinica.domain.role.Permissions;
import org.akaza.openclinica.domain.role.Role;
import org.akaza.openclinica.domain.role.RolePermission;
import org.akaza.openclinica.domain.role.UserAccount;
import org.akaza.openclinica.domain.role.UserRoleAccess;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
/**
 * The userDetails for the custom user role configuration, extended to use permissions instead of roles to grant authority.
 * @author jnyayapathi
 *
 */
public class CustomUser implements Serializable, UserDetails {

    
    private String access_url;
    private StudyBean study_bean;
    
    public String getAccess_url() {
        return access_url;
    }

    public void setAccess_url(String access_url) {
        this.access_url = access_url;
    }

    //JN: the trick here is to map the permissions to the grantedAuthorities.
    public Collection<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority>  list = new ArrayList();
        StudyBean studyBean = (StudyBean)getStudyDAO().findByPK(userAccount.getActiveStudy());
        setStudyBean(studyBean);
        UserRoleAccess usrRoleAccess = getRoleAccess(studyBean,userAccount.getUserRoleAccess());
       
            Role role = usrRoleAccess.getRole();
            for(Permissions permission:role.getPermissions())
            {   
            list.add(new GrantedAuthorityImpl(permission.getAccess_url()));
            }
        
         
            for(Role allRoles:userAccount.getRole()){
               if(!role.equals(allRoles))
                for(Permissions permit:allRoles.getPermissions()){
                    if(permit.getIsAdmin())
                    {
                        list.add(new GrantedAuthorityImpl(permit.getAccess_url()));
                    }
                }
            }
       
        return list;
    }

  
    private UserRoleAccess getRoleAccess(StudyBean studyBean,List<UserRoleAccess> usrRoleAccess){
        UserRoleAccess returnGrp = new UserRoleAccess();
        for(UserRoleAccess usrRoleAxs:usrRoleAccess){
         if(usrRoleAxs.getGroupAuthDef()!=null)
            if(studyBean.getOid().equals(usrRoleAxs.getGroupAuthDef().getOc_oid_reference_list())){
                returnGrp = usrRoleAxs;
            }
        }
        return returnGrp; 
    }
    public String getPassword() {
        return userAccount.getPasswd();
    }

    public String getUsername() {
        return userAccount.getUserName();
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return userAccount.getAccountNonLocked();
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }
    
    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Set<Permissions> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permissions> permissions) {
        this.permissions = permissions;
    }
    public Set<UserRoleAccess> getUserRoleAccess() {
        return userRoleAccess;
    }

    public void setUserRoleAccess(Set<UserRoleAccess> userRoleAccess) {
        this.userRoleAccess = userRoleAccess;
    }

    public Set<RolePermission> getRolePermissions() {
        return rolePermissions;
    }

    public void setRolePermissions(Set<RolePermission> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }
    private List<Role> roles;
    private Set<Permissions> permissions;
    private Set<UserRoleAccess> userRoleAccess;
    private Set<RolePermission> rolePermissions;
    
    private UserAccount userAccount;

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }
    
    
    private StudyDAO studyDAO;

    public StudyDAO getStudyDAO() {
        return studyDAO;
    }

    public void setStudyDAO(StudyDAO studyDAO) {
        this.studyDAO = studyDAO;
    }

	/**
	 * @param study_bean the study_bean to set
	 */
	public void setStudyBean(StudyBean study_bean) {
		this.study_bean = study_bean;
	}

	/**
	 * @return the study_bean
	 */
	public StudyBean getStudyBean() {
		return study_bean;
	}
    
    

}
