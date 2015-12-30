/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.login;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;

/**
 * @author thickerson
 */

/**
 * @author ssachs
 * 
 * The superclass id field is the role id. The superclass name field is the role
 * name.
 */

public class StudyUserRoleBean extends AuditableEntityBean {
  /*
   * this class will hold the following fields: username, rolename, studyid,
   * updateid, datecreated, dateupdated, ownerid, statusid in the context of
   * entitybean, name->username
   */

  private Role role;

  private int studyId;

  // not in the database, and not guaranteed to correspond to studyId; studyId
  // is authoritative
  // this is only provided as a convenience
  private String studyName = "";

  // not in the database, and not guaranteed to correspond to studyId; studyId
  // is authoritative
  // this is only provided as a convenience
  private int parentStudyId = 0;

  private String lastName = ""; //not in the DB,not guaranteed to have a value

  private String firstName = "";//not in the DB,not guaranteed to have a value
  
  private String userName = ""; //name here is role.name, this is different from name,not guaranteed to have a value

  public StudyUserRoleBean() {
    role = Role.INVALID;
    studyId = 0;
    setRole(role);
    status = Status.AVAILABLE;
  }

  /**
   * @return Returns the role.
   */
  public Role getRole() {
    return role;
  }

  /**
   * @param role
   *          The role to set.
   */
  public void setRole(Role role) {
    this.role = role;
    super.setId(role.getId());
    super.setName(role.getName());
  }

  /**
   * @return Returns the roleName.
   */
  public String getRoleName() {
    return role.getName();
  }

  /**
   * @param roleName
   *          The roleName to set.
   */
  public void setRoleName(String roleName) {
    Role role = Role.getByName(roleName);
    setRole(role);
  }

  /**
   * @return Returns the studyId.
   */
  public int getStudyId() {
    return studyId;
  }

  /**
   * @param studyId
   *          The studyId to set.
   */
  public void setStudyId(int studyId) {
    this.studyId = studyId;
  }

  //this is different from the meaning of "name"
  public String getUserName() {
    return userName;
  }
  
  public void setUserName(String userName) {
  	this.userName = userName;
  }

  /**
   * @return Returns the studyName.
   */
  public String getStudyName() {
    return studyName;
  }

  /**
   * @param studyName
   *          The studyName to set.
   */
  public void setStudyName(String studyName) {
    this.studyName = studyName;
  }

  /**
   * @return Returns the parentStudyId.
   */
  public int getParentStudyId() {
    return parentStudyId;
  }

  /**
   * @param parentStudyId
   *          The parentStudyId to set.
   */
  public void setParentStudyId(int parentStudyId) {
    this.parentStudyId = parentStudyId;
  }

  public String getName() {
    if (role != null) {
      return role.getName();
    }
    return "";
  }

  public void setName(String name) {
    setRoleName(name);
  }

  public int getId() {
    if (role != null) {
      return role.getId();
    }
    return 0;
  }

  public void setId(int id) {
    setRole(Role.get(id));
  }
  
  public String getLastName(){
    return lastName;
  }
  
  public void setLastName(String lastName){
    this.lastName =lastName;
  }
  
  public String getFirstName(){
    return firstName;
  }
  
  public void setFirstName(String firstName){
    this.firstName =firstName;
  }
}