package org.akaza.openclinica.domain.role;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import javax.persistence.Table;


import java.util.List;
import java.util.Set;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.akaza.openclinica.domain.Status;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
/**
 * 
 * @author jnyayapathi
 * The mapping bean between user_account and role. 
 */
@Entity
@Table(name = "user_role_access")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "user_role_access_id_seq") })

public class UserRoleAccess extends AbstractMutableDomainObject {

    private Integer user_id;
    private Integer role_id;
    private Integer group_id;
    public Integer getUser_id() {
        return user_id;
    }
    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }
    public Integer getRole_id() {
        return role_id;
    }
    public void setRole_id(Integer role_id) {
        this.role_id = role_id;
    }
    public Integer getGroup_id() {
        return group_id;
    }
    public void setGroup_id(Integer group_id) {
        this.group_id = group_id;
    }
    Status status;
  
    @Type(type = "status")
    @Column(name = "status_id")
    public Status getStatus() {
        if (status != null) {
            return status;
        } else
            return Status.AVAILABLE;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }
   /* private UserAccount userAccounts;

    @ManyToOne
    @JoinColumn(name="user_id")
   // @JoinTable(name = "user_role_access", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = { @JoinColumn(name = "user_id") })
    public UserAccount getUserAccount() {
        return userAccounts;
    }

    public void setUserAccount(UserAccount userAccounts) {
        this.userAccounts = userAccounts;
    }
    */
    private Role role;
    @OneToOne(cascade={CascadeType.ALL})
    @JoinColumn(name="role_id",insertable=false,updatable=false)
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    
    private GroupAuthDefinition groupAuthDef;
    @OneToOne(cascade={CascadeType.ALL})
    @JoinColumn(name="group_id",insertable=false,updatable=false)
   // @JoinTable(name = "user_role_access", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = { @JoinColumn(name = "user_id") })
    public GroupAuthDefinition getGroupAuthDef() {
        return groupAuthDef;
    }

    public void setGroupAuthDef(GroupAuthDefinition groupAuthDef) {
        this.groupAuthDef = groupAuthDef;
    }
   
}
