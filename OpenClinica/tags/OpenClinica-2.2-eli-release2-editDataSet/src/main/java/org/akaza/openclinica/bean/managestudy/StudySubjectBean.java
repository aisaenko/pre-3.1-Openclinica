/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.managestudy;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import java.util.*;

/**
 * @author jxu
 *  
 */
public class StudySubjectBean extends AuditableEntityBean {
	//STUDY_SUBJECT_ID, LABEL, SUBJECT_ID, STUDY_ID
	//STATUS_ID, DATE_CREATED, OWNER_ID,
	//DATE_UPDATED, UPDATE_ID,secondary_label
	private String label = "";
	
	private int subjectId;
	
	private int studyId;
	
	//private int studyGroupId;
	
	private Date enrollmentDate;
	
	private String secondaryLabel = "";
	
	private String uniqueIdentifier = "";//not in the table, for display purpose
	
	private String studyName = "";//not in the table, for display purpose
	
	private char gender = 'm';//not in the table, for display purpose
	
	private Date dateOfBirth;//not in the db
	
	/**
	 * An array of the groups this subject belongs to.
	 * Each element is a StudyGroupMapBean object.
	 * Not in the database.
	 */
	private ArrayList studyGroupMaps;
	
	public StudySubjectBean() {
		studyGroupMaps = new ArrayList();
	}
	
	/**
	 * @return Returns the uniqueIndentifier.
	 */
	public String getUniqueIdentifier() {
		return uniqueIdentifier;
	}
	
	/**
	 * @param uniqueIdentifier
	 *          The uniqueIdentifier to set.
	 */
	public void setUniqueIdentifier(String uniqueIdentifier) {
		this.uniqueIdentifier = uniqueIdentifier;
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
	 * @return Returns the gender.
	 */
	public char getGender() {
		return gender;
	}
	
	/**
	 * @param gender
	 *          The gender to set.
	 */
	public void setGender(char gender) {
		this.gender = gender;
	}
	
	/**
	 * @return Returns the label.
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * @param label
	 *          The label to set.
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * @return Returns the secondaryLabel.
	 */
	public String getSecondaryLabel() {
		return secondaryLabel;
	}
	
	/**
	 * @param secondaryLabel
	 *          The secondaryLabel to set.
	 */
	public void setSecondaryLabel(String secondaryLabel) {
		this.secondaryLabel = secondaryLabel;
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
	
	/**
	 * @return Returns the subjectId.
	 */
	public int getSubjectId() {
		return subjectId;
	}
	
	/**
	 * @param subjectId
	 *          The subjectId to set.
	 */
	public void setSubjectId(int subjectId) {
		this.subjectId = subjectId;
	}
	
	/**
	 * @return Returns the enrollmentDate.
	 */
	public Date getEnrollmentDate() {
		return enrollmentDate;
	}
	
	/**
	 * @param enrollmentDate
	 *          The enrollmentDate to set.
	 */
	public void setEnrollmentDate(Date enrollmentDate) {
		this.enrollmentDate = enrollmentDate;
	}
	
	// disambiguate the meaning of "name" in this context
	public String getName() {
		return getLabel();
	}
	
	public void setName(String name) {
		setLabel(name);
	}
	
	
	/**
	 * @return Returns the studyGroupMaps.
	 */
	public ArrayList getStudyGroupMaps() {
		return studyGroupMaps;
	}
	/**
	 * @param studyGroupMaps The studyGroupMaps to set.
	 */
	public void setStudyGroupMaps(ArrayList studyGroupMaps) {
		this.studyGroupMaps = studyGroupMaps;
	}
	
	
  /**
   * @return Returns the dateOfBirth.
   */
  public Date getDateOfBirth() {
    return dateOfBirth;
  }
  /**
   * @param dateOfBirth The dateOfBirth to set.
   */
  public void setDateOfBirth(Date dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }
  
  
}