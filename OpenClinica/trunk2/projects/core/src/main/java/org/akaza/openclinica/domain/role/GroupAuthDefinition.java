package org.akaza.openclinica.domain.role;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import java.util.HashSet;
import java.util.Set;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
/**
 * GroupAuthDefinition, defines authorization based upon restricted access to the data values, such as study, CRF etc. 
 * @author jnyayapathi
 *
 */
@Entity
@Table(name = "group_auth_definition")


@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "group_auth_definition_id_seq") })
public class GroupAuthDefinition extends AbstractMutableDomainObject{
private String oc_oid_reference_list;

public String getOc_oid_reference_list() {
    return oc_oid_reference_list;
}

public void setOc_oid_reference_list(String oc_oid_reference_list) {
    this.oc_oid_reference_list = oc_oid_reference_list;
}

private String group_name;

public String getGroup_name() {
    return group_name;
}

public void setGroup_name(String group_name) {
    this.group_name = group_name;
}
private Set<UserAccount> userAccounts;

@ManyToMany(targetEntity=org.akaza.openclinica.domain.role.UserAccount.class,cascade={CascadeType.PERSIST,CascadeType.MERGE},fetch=FetchType.EAGER) 
@JoinTable(name = "user_role_access", joinColumns = { @JoinColumn(name = "group_id") }, inverseJoinColumns = { @JoinColumn(name = "user_id") })
public Set<UserAccount> getUserAccount() {
    return userAccounts;
}

public void setUserAccount(Set<UserAccount> userAccounts) {
    this.userAccounts = userAccounts;
}

public void setParentGroup(GroupAuthDefinition parentGroup) {
 
    this.parentGroup = parentGroup;
    /*if (this.parentGroup != null) {
        this.parentGroup.removeChildernGroupList(this);            
    }
    this.parentGroup = parentGroup;
    this.parentGroup.addChildernGroupList(this);
    */
}
@ManyToOne(targetEntity = GroupAuthDefinition.class, optional=true,fetch=FetchType.EAGER)
@JoinColumn(name="grp_parent_id")
public GroupAuthDefinition getParentGroup() {
    return parentGroup;
}




private GroupAuthDefinition parentGroup = null;



private Set<GroupAuthDefinition> childernGroupList = new HashSet<GroupAuthDefinition>();

private void addChildernGroupList(GroupAuthDefinition childGroup) {
    childernGroupList.add(childGroup);
}
private void removeChildernGroupList(GroupAuthDefinition childGroup) {
    childernGroupList.remove(childGroup);
}
public void setChildernGroupList(Set<GroupAuthDefinition> childernGroupList) {
    this.childernGroupList = childernGroupList;
}

@OneToMany(mappedBy="parentGroup",fetch=FetchType.EAGER)
//@JoinColumns(  { @JoinColumn (name="grp_parent_id",referencedColumnName="id")})
public Set<GroupAuthDefinition> getChildernGroupList() {
    return childernGroupList;
}

private UserRoleAccess userRoleAccess; 

@OneToOne(targetEntity=org.akaza.openclinica.domain.role.UserRoleAccess.class)  
@JoinColumn(name="id",insertable=false,updatable=false)
  public UserRoleAccess getUserRoleAccess() {
      return userRoleAccess;
  }
  public void setUserRoleAccess(UserRoleAccess userRoleAccess) {
      this.userRoleAccess = userRoleAccess;
  }
}