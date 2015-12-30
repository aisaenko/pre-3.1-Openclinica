/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.managestudy;

import java.util.ArrayList;
import java.util.Date;

import org.akaza.openclinica.bean.core.AuditableEntityBean;

/**
 * @author jxu
 *
 * Object for study group class
 */
public class StudyTemplateBean extends AuditableEntityBean {
  //STUDY_GROUP_ID   NAME  STUDY_ID  OWNER_ID
  //DATE_CREATED  STUDY_TEMPLATE_TYPE_ID  STATUS_ID  DATE_UPDATED
  //UPDATE_ID subject_assignment
  private int studyId=0;
  private String studyName=""; //not in db
  private int studyTemplateTypeId=0;
  private String studyTemplateTypeName=""; //not in db
  private String subjectAssignment="";  
  
  private ArrayList studyGroups = new ArrayList();//not in DB
  private int studyGroupId=0;//not in DB, indicates which group a subject is in
  private String groupNotes ="";//not in DB
  private Date studyTemplateStartDate=null;//not in DB
  private String studyGroupName ="";//not in DB
  
  private ArrayList studyTemplateEventDefs = new ArrayList(); //not in DB
  
  private boolean editable = true;//not in DB
  
  

  /**
   * @return Returns the studyTemplateEventDefs.
   */
  public ArrayList getStudyTemplateEventDefs() {
    return studyTemplateEventDefs;
  }
  /**
   * @param studyTemplateEventDefs The studyTemplateEventDefs to set.
   */
  public void setStudyTemplateEventDefs(ArrayList studyTemplateEventDefs) {
    this.studyTemplateEventDefs = studyTemplateEventDefs;
  }
  /**
   * @return Returns the studyName.
   */
  public String getStudyName() {
    return studyName;
  }
  /**
   * @param studyName The studyName to set.
   */
  public void setStudyName(String studyName) {
    this.studyName = studyName;
  }
  
  /**
   * @return Returns the subjectAssignment.
   */
  public String getSubjectAssignment() {
    return subjectAssignment;
  }
  /**
   * @param subjectAssignment The subjectAssignment to set.
   */
  public void setSubjectAssignment(String subjectAssignment) {
    this.subjectAssignment = subjectAssignment;
  }
   
  
  /**
   * @return Returns the studyId.
   */
  public int getStudyId() {
    return studyId;
  }
  
  /**
   * @param studyId The studyId to set.
   */
  public void setStudyId(int studyId) {
    this.studyId = studyId;
  }
  
  

  /**
   * @return Returns the studyGroups.
   */
  public ArrayList getStudyGroups() {
    return studyGroups;
  }
  /**
   * @param studyGroups The studyGroups to set.
   */
  public void setStudyGroups(ArrayList studyGroups) {
    this.studyGroups = studyGroups;
  }
  
  
  /**
   * @return Returns the studyGroupId.
   */
  public int getStudyGroupId() {
    return studyGroupId;
  }
  /**
   * @param studyGroupId The studyGroupId to set.
   */
  public void setStudyGroupId(int studyGroupId) {
    this.studyGroupId = studyGroupId;
  }
  
  
  /**
   * @return Returns the groupNotes.
   */
  public String getGroupNotes() {
    return groupNotes;
  }
  /**
   * @param groupNotes The groupNotes to set.
   */
  public void setGroupNotes(String groupNotes) {
    this.groupNotes = groupNotes;
  }
  
  
  /**
   * @return Returns the studyGroupName.
   */
  public String getStudyGroupName() {
    return studyGroupName;
  }
  /**
   * @param studyGroupName The studyGroupName to set.
   */
  public void setStudyGroupName(String studyGroupName) {
    this.studyGroupName = studyGroupName;
  }
  /**
   * @return the studyTemplateStartDate
   */
  public Date getStudyTemplateStartDate() {
	return studyTemplateStartDate;
  }
  /**
   * @param studyTemplateStartDate the studyTemplateStartDate to set
   */
  public void setStudyTemplateStartDate(Date studyTemplateStartDate) {
	this.studyTemplateStartDate = studyTemplateStartDate;
  }
  /**
   * @return Returns the studyTemplateTypeId.
   */
  public int getStudyTemplateTypeId() {
    return studyTemplateTypeId;
  }
  /**
   * @param studyTemplateTypeId The studyTemplateTypeId to set.
   */
  public void setStudyTemplateTypeId(int studyTemplateTypeId) {
    this.studyTemplateTypeId = studyTemplateTypeId;
  }
  /**
   * @return Returns the studyTemplateTypeName.
   */
  public String getStudyTemplateTypeName() {
    return studyTemplateTypeName;
  }
  /**
   * @param studyTemplateTypeName The studyTemplateTypeName to set.
   */
  public void setStudyTemplateTypeName(String studyTemplateTypeName) {
    this.studyTemplateTypeName = studyTemplateTypeName;
  }
  /**
   * @return Returns the editable.
   */
  public boolean isEditable() {
    return editable;
  }
  /**
   * @param editable The editable to set.
   */
  public void setEditable(boolean editable) {
    this.editable = editable;
  }
  
  
  
}
