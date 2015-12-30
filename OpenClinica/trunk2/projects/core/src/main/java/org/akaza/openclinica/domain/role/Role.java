package org.akaza.openclinica.domain.role;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.util.Set;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * 
 * @author jnyayapathi The domain Object for persisting to the Domain Table.
 */
@Entity
@Table(name = "role")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "role_id_seq") })
public class Role extends AbstractMutableDomainObject {

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((permissions == null) ? 0 : permissions.hashCode());
        result = prime * result + ((roleName == null) ? 0 : roleName.hashCode());
        result = prime * result + ((userAccounts == null) ? 0 : userAccounts.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Role other = (Role) obj;
        if (permissions == null) {
            if (other.permissions != null)
                return false;
        } else if (!permissions.equals(other.permissions))
            return false;
        if (roleName == null) {
            if (other.roleName != null)
                return false;
        } else if (!roleName.equals(other.roleName))
            return false;
        if (userAccounts == null) {
            if (other.userAccounts != null)
                return false;
        } else if (!userAccounts.equals(other.userAccounts))
            return false;
        return true;
    }
    private String roleName;
    @Transient
    private String i18n_description;
    

    /*
     * private Set<UserRoleAccess> userRoleAccess;
     * 
     * @ManyToMany(targetEntity=org.akaza.openclinica.domain.role.UserRoleAccess.
     * class) public Set<UserRoleAccess> getUserRoleAccess() { return
     * userRoleAccess; }
     * 
     * public void setUserRoleAccess(Set<UserRoleAccess> userRoleAccess) {
     * this.userRoleAccess = userRoleAccess; }
     */

    private Set<UserAccount> userAccounts;

    @ManyToMany(targetEntity=org.akaza.openclinica.domain.role.UserAccount.class,cascade={CascadeType.PERSIST,CascadeType.MERGE}) 
    @JoinTable(name = "user_role_access", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = { @JoinColumn(name = "user_id") })
    public Set<UserAccount> getUserAccount() {
        return userAccounts;
    }

    public void setUserAccount(Set<UserAccount> userAccount) {
        this.userAccounts = userAccount;
    }

    @Column(name = "role_name")
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    private Set<Permissions> permissions;
    
    @ManyToMany(targetEntity=org.akaza.openclinica.domain.role.Permissions.class,fetch=FetchType.EAGER,cascade=CascadeType.ALL) 
    @JoinTable(name = "role_permission", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = { @JoinColumn(name = "permission_id") })
        public Set<Permissions> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permissions> permissions) {
        this.permissions = permissions;
    }

	/**
	 * @param i18n_description the i18n_description to set
	 */
	public void setI18nDescription(String i18n_description) {
		this.i18n_description = i18n_description;
	}

	/**
	 * @return the i18n_description
	 */
	@Transient
	public String getI18nDescription() {
		return i18n_description;
	}
    
    
}
