/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.managestudy;

import java.util.*;
import java.text.*;

import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.core.EntityBeanRow;

/**
 * @author jxu
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DisplayStudySubjectRow extends EntityBeanRow {

  // columns:
  
  public static final int COL_SUBJECT_LABEL = 0; 
  
  public static final int COL_STATUS = 1;
  
  public static final int COL_GENDER = 2;
  
  public static final int COL_STUDYGROUP = 3;
  
  public static final int COL_STUDYEVENT = 4;
  


  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.core.EntityBeanRow#compareColumn(java.lang.Object,
   *      int)
   */
  protected int compareColumn(Object row, int sortingColumn) {
    if (!row.getClass().equals(DisplayStudySubjectRow.class)) {
      return 0;
    }

    DisplayStudySubjectBean thisStudy = (DisplayStudySubjectBean) bean;
    DisplayStudySubjectBean argStudy = (DisplayStudySubjectBean) ((DisplayStudySubjectRow) row).bean;

    int answer = 0;
    
    int groupSize = thisStudy.getStudyGroups().size();
    int code;
    if(sortingColumn > 2+groupSize ) {
        code = COL_STUDYEVENT;
    } else if(sortingColumn > 2 && sortingColumn <= 2+groupSize) {
        code = COL_STUDYGROUP;
    }else {
        code = sortingColumn;
    }
    switch (code) {
    
    case COL_SUBJECT_LABEL:
      answer = thisStudy.getStudySubject().getLabel().toLowerCase().compareTo(
          argStudy.getStudySubject().getLabel().toLowerCase());
      break;
    case COL_GENDER:
      answer = (thisStudy.getStudySubject().getGender()+"") .compareTo(
          argStudy.getStudySubject().getGender() +"");
      break;
    case COL_STUDYGROUP:
    answer = ((SubjectGroupMapBean)(thisStudy.getStudyGroups().get(sortingColumn-3))).getStudyGroupName().toLowerCase().compareTo(
            ((SubjectGroupMapBean)(argStudy.getStudyGroups().get(sortingColumn-3))).getStudyGroupName().toLowerCase());
    break;
    case COL_STUDYEVENT:
    //studyEvent subject event status comparison
    answer = ((StudyEventBean)(thisStudy.getStudyEvents().get(sortingColumn-3-groupSize))).getSubjectEventStatus().compareTo(
            ((StudyEventBean)argStudy.getStudyEvents().get(sortingColumn-3-groupSize)).getSubjectEventStatus());
    break;
     
    case COL_STATUS:
      answer = thisStudy.getStudySubject().getStatus().compareTo(argStudy.getStudySubject().getStatus());
      break;
    }

    return answer;
  }

  public String getSearchString() {
    DisplayStudySubjectBean thisStudy = (DisplayStudySubjectBean) bean;
    
    String toStr = "";
    Date enrDate = thisStudy.getStudySubject().getEnrollmentDate();
    if (enrDate != null) {
    	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    	toStr = sdf.format(enrDate);
    }

    return thisStudy.getStudySubject().getLabel() + " "
        + thisStudy.getStudySubject().getGender() + " " + toStr;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.core.EntityBeanRow#generatRowsFromBeans(java.util.ArrayList)
   */
  public ArrayList generatRowsFromBeans(ArrayList beans) {
    return DisplayStudySubjectRow.generateRowsFromBeans(beans);
  }

  public static ArrayList generateRowsFromBeans(ArrayList beans) {
    ArrayList answer = new ArrayList();

    Class[] parameters = null;
    Object[] arguments = null;

    for (int i = 0; i < beans.size(); i++) {
      try {
        DisplayStudySubjectRow row = new DisplayStudySubjectRow();
        row.setBean((DisplayStudySubjectBean) beans.get(i));
        answer.add(row);
      } catch (Exception e) {
      }
    }

    return answer;
  }

}