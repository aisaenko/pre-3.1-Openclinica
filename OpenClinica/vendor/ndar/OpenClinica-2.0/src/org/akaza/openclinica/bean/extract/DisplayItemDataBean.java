/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.extract;

import java.util.ArrayList;

/**
 * @author jxu
 *
 * To display dataset->item row on dataset html browser page
 */
public class DisplayItemDataBean {
  private String subjectName;
  private String studyLabel;
  private String subjectDob;
  private String subjectGender;
  private ArrayList eventValues = new ArrayList();
  private ArrayList itemValues = new ArrayList();
  
  
  

  /**
   * @return Returns the eventValues.
   */
  public ArrayList getEventValues() {
    return eventValues;
  }
  /**
   * @param eventValues The eventValues to set.
   */
  public void setEventValues(ArrayList eventValues) {
    this.eventValues = eventValues;
  }
  /**
   * @return Returns the subjectDob.
   */
  public String getSubjectDob() {
    return subjectDob;
  }
  /**
   * @param subjectDob The subjectDob to set.
   */
  public void setSubjectDob(String subjectDob) {
    this.subjectDob = subjectDob;
  }
  /**
   * @return Returns the subjectGender.
   */
  public String getSubjectGender() {
    return subjectGender;
  }
  /**
   * @param subjectGender The subjectGender to set.
   */
  public void setSubjectGender(String subjectGender) {
    this.subjectGender = subjectGender;
  }
  /**
   * @return Returns the itemValues.
   */
  public ArrayList getItemValues() {
    return itemValues;
  }
  /**
   * @param itemValues The itemValues to set.
   */
  public void setItemValues(ArrayList itemValues) {
    this.itemValues = itemValues;
  }
  /**
   * @return Returns the studyLabel.
   */
  public String getStudyLabel() {
    return studyLabel;
  }
  /**
   * @param studyLabel The studyLabel to set.
   */
  public void setStudyLabel(String studyLabel) {
    this.studyLabel = studyLabel;
  }
  /**
   * @return Returns the subjectName.
   */
  public String getSubjectName() {
    return subjectName;
  }
  /**
   * @param subjectName The subjectName to set.
   */
  public void setSubjectName(String subjectName) {
    this.subjectName = subjectName;
  }
}
