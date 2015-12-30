/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.managestudy;

import java.util.*;

import org.akaza.openclinica.bean.core.AuditableEntityBean;

/**
 * @author jxu
 */
public class DisplayStudyEventBean extends AuditableEntityBean {
  
  private StudyEventBean studyEvent;
  private ArrayList displayEventCRFs;
  private ArrayList uncompletedCRFs;
  private ArrayList allEventCRFs; //includes uncompleted and completed, order by ordinal
  private StudySubjectBean studySubject; // added on 07/26/06
   

  /**
   * 
   */
  public DisplayStudyEventBean() {
    displayEventCRFs=new ArrayList();
    uncompletedCRFs=new ArrayList();
    allEventCRFs = new ArrayList();
   
  }
 
  
  /**
   * @return Returns the allEventCRFs.
   */
  public ArrayList getAllEventCRFs() {
    return allEventCRFs;
  }


  /**
   * @param allEventCRFs The allEventCRFs to set.
   */
  public void setAllEventCRFs(ArrayList allEventCRFs) {
    this.allEventCRFs = allEventCRFs;
  }


  /**
   * @return Returns the displayEventCRFs.
   */
  public ArrayList getDisplayEventCRFs() {
    return displayEventCRFs;
  }
  /**
   * @param displayEventCRFs The displayEventCRFs to set.
   */
  public void setDisplayEventCRFs(ArrayList displayEventCRFs) {
    this.displayEventCRFs = displayEventCRFs;
  }
  /**
   * @return Returns the studyEvent.
   */
  public StudyEventBean getStudyEvent() {
    return studyEvent;
  }
  /**
   * @param studyEvent The studyEvent to set.
   */
  public void setStudyEvent(StudyEventBean studyEvent) {
    this.studyEvent = studyEvent;
  }
  /**
   * @return Returns the uncompletedCRFs.
   */
  public ArrayList getUncompletedCRFs() {
    return uncompletedCRFs;
  }
  /**
   * @param uncompletedCRFs The uncompletedCRFs to set.
   */
  public void setUncompletedCRFs(ArrayList uncompletedCRFs) {
    this.uncompletedCRFs = uncompletedCRFs;
  }
  
  public static ArrayList generateBeansFromRows(ArrayList rows) {
    ArrayList answer = new ArrayList();

    for (int i = 0; i < rows.size(); i++) {
      try {
        DisplayStudyEventRow row =  (DisplayStudyEventRow) rows.get(i);
        answer.add((DisplayStudyEventBean)  row.getBean());
      } catch (Exception e) {
      }
    }

    return answer;
  }

  /**
   * @return Returns the studySubject.
   */
  public StudySubjectBean getStudySubject() {
    return studySubject;
  }

  /**
   * @param studySubject The studySubject to set.
   */
  public void setStudySubject(StudySubjectBean studySubject) {
    this.studySubject = studySubject;
  }
  
  
}
