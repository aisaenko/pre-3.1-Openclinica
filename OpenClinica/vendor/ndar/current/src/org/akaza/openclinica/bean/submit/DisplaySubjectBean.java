/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.submit;

import java.util.ArrayList;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.subject.Person;

/**
 * @author jxu
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DisplaySubjectBean extends EntityBean {
  private SubjectBean subject;
  private ArrayList studySubjects;
  private String studySubjectIds;
  private Person person;
  private String labelIds;
  
  /**
   * @return Returns the studySubjectIds.
   */
  public String getStudySubjectIds() {
    return studySubjectIds;
  }
  /**
   * @param studySubjectIds The studySubjectIds to set.
   */
  public void setStudySubjectIds(String studySubjectIds) {
    this.studySubjectIds = studySubjectIds;
  }
  /**
   * @return Returns the studySubjects.
   */
  public ArrayList getStudySubjects() {
    return studySubjects;
  }
  /**
   * @param studySubjects The studySubjects to set.
   */
  public void setStudySubjects(ArrayList studySubjects) {
    this.studySubjects = studySubjects;
  }
  /**
   * @return Returns the subject.
   */
  public SubjectBean getSubject() {
    return subject;
  }
  /**
   * @param subject The subject to set.
   */
  public void setSubject(SubjectBean subject) {
    this.subject = subject;
  }
  /**
   * @return the person
   */
  public Person getPerson() {
    return person;
  }
  /**
   * @param person the person to set
   */
  public void setPerson(Person person) {
    this.person = person;
  }
  /**
   * @return the labelIds
   */
  public String getLabelIds() {
	return labelIds;
  }
  /**
   * @param labelIds the labelIds to set
   */
  public void setLabelIds(String labelIds) {
	this.labelIds = labelIds;
  }
}
