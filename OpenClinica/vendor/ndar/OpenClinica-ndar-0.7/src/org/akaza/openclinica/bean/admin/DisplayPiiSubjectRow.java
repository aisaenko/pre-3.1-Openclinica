/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.admin;

import java.util.ArrayList;

import org.akaza.openclinica.bean.submit.DisplaySubjectBean;
import org.akaza.openclinica.core.EntityBeanRow;

/**
 * @author jxu
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DisplayPiiSubjectRow extends EntityBeanRow {
//columns:
  public static final int COL_GLOBAL_ID = 0;
  
  public static final int COL_PERSON_NAMES = 1;
  
  public static final int COL_SUBJECT_IDS = 2;

  public static final int COL_GENDER = 3;
  
  public static final int COL_SUBJECT_LABELS = 4;
  
  public static final int COL_DATE_CREATED = 5;
  
  public static final int COL_OWNER = 6;
  
  public static final int COL_DATE_UPDATED = 7;
  
  public static final int COL_UPDATER = 8;

  public static final int COL_STATUS = 9;

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.core.EntityBeanRow#compareColumn(java.lang.Object,
   *      int)
   */
  protected int compareColumn(Object row, int sortingColumn) {
    if (!row.getClass().equals(DisplayPiiSubjectRow.class)) {
      return 0;
    }

    DisplaySubjectBean thisSubject = (DisplaySubjectBean) bean;
    DisplaySubjectBean argSubject = (DisplaySubjectBean) ((DisplayPiiSubjectRow) row).bean;

    int answer = 0;
    switch (sortingColumn) {
    case COL_GLOBAL_ID:
      if (thisSubject.getPerson()==null || argSubject.getPerson()==null) {
    	answer = (thisSubject.getPerson() == null ? (argSubject.getPerson() == null ? 0 : -1) : 1);
    	break;
      }
      answer = thisSubject.getPerson().getPersonId().compareToIgnoreCase(argSubject.getPerson().getPersonId());
      break; 
    case COL_PERSON_NAMES:
      if (thisSubject.getPerson()==null || argSubject.getPerson()==null) {
        answer = (thisSubject.getPerson() == null ? (argSubject.getPerson() == null ? 0 : -1) : 1);
        break;
      }
      if (thisSubject.getPerson().getSurname()==null || argSubject.getPerson().getSurname()==null) {
      	answer = (thisSubject.getPerson().getSurname() == null ? (argSubject.getPerson().getSurname() == null ? 0 : -1) : 1);
      } else {
        answer = thisSubject.getPerson().getSurname().compareToIgnoreCase(argSubject.getPerson().getSurname());
      }
      if (answer==0) {
        if (thisSubject.getPerson().getGivenName()==null || argSubject.getPerson().getGivenName()==null) {
          answer = (thisSubject.getPerson().getGivenName() == null ? (argSubject.getPerson().getGivenName() == null ? 0 : -1) : 1);
        } else {
          answer = thisSubject.getPerson().getGivenName().compareToIgnoreCase(argSubject.getPerson().getGivenName());
        }
      }
      break; 
    case COL_SUBJECT_IDS:
      if (thisSubject.getSubject()==null || argSubject.getSubject()==null) {
        answer = (thisSubject.getSubject() == null ? (argSubject.getSubject() == null ? 0 : -1) : 1);
        break;
      }
      answer = thisSubject.getStudySubjectIds().compareToIgnoreCase(argSubject.getStudySubjectIds());
      break;      
    case COL_GENDER:
        if (thisSubject.getSubject()==null || argSubject.getSubject()==null) {
            answer = (thisSubject.getSubject() == null ? (argSubject.getSubject() == null ? 0 : -1) : 1);
            break;
          }
      answer = (thisSubject.getSubject().getGender()+"").compareTo(argSubject.getSubject().getGender()+"");
      break;  
    case COL_DATE_CREATED:
        if (thisSubject.getSubject()==null || argSubject.getSubject()==null) {
            answer = (thisSubject.getSubject() == null ? (argSubject.getSubject() == null ? 0 : -1) : 1);
            break;
          }
      answer = compareDate(thisSubject.getSubject().getCreatedDate(),argSubject.getSubject().getCreatedDate());
      break;
    case COL_OWNER:
        if (thisSubject.getSubject()==null || argSubject.getSubject()==null) {
            answer = (thisSubject.getSubject() == null ? (argSubject.getSubject() == null ? 0 : -1) : 1);
            break;
          }
      answer = thisSubject.getSubject().getOwner().getName().compareToIgnoreCase(argSubject.getSubject().getOwner().getName());
      break;
    case COL_SUBJECT_LABELS:
        answer = thisSubject.getLabelIds().compareToIgnoreCase(argSubject.getLabelIds());
        break;      
    case COL_DATE_UPDATED:
        if (thisSubject.getSubject()==null || argSubject.getSubject()==null) {
            answer = (thisSubject.getSubject() == null ? (argSubject.getSubject() == null ? 0 : -1) : 1);
            break;
          }
      answer = compareDate(thisSubject.getSubject().getUpdatedDate(),argSubject.getSubject().getUpdatedDate());
      break;
    case COL_UPDATER:
        if (thisSubject.getSubject()==null || argSubject.getSubject()==null) {
            answer = (thisSubject.getSubject() == null ? (argSubject.getSubject() == null ? 0 : -1) : 1);
            break;
          }
      answer = thisSubject.getSubject().getUpdater().getName().compareToIgnoreCase(argSubject.getSubject().getUpdater().getName());
      break;      
    case COL_STATUS:
      if (thisSubject.getSubject()==null || argSubject.getSubject()==null) {
        answer = (thisSubject.getSubject() == null ? (argSubject.getSubject() == null ? 0 : -1) : 1);
        break;
      }
      answer = (thisSubject.getSubject().getStatus()==null ? (argSubject.getSubject().getStatus() == null ? 0 : 1) : thisSubject.getSubject().getStatus().compareTo(argSubject.getSubject().getStatus()));
      break;
    }

    return answer;
  }

  public String getSearchString() {
    DisplaySubjectBean thisSubject = (DisplaySubjectBean) bean;
    return thisSubject.getPerson().getSurname() + " " + thisSubject.getPerson().getGivenName() + " " + thisSubject.getPerson().getPersonId() + " " + (thisSubject.getSubject() == null ? "" : thisSubject.getSubject().getName()) + " " + thisSubject.getStudySubjectIds();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.core.EntityBeanRow#generatRowsFromBeans(java.util.ArrayList)
   */
  public ArrayList generatRowsFromBeans(ArrayList beans) {
    return DisplayPiiSubjectRow.generateRowsFromBeans(beans);
  }

  public static ArrayList generateRowsFromBeans(ArrayList beans) {
    ArrayList answer = new ArrayList();

    Class[] parameters = null;
    Object[] arguments = null;

    for (int i = 0; i < beans.size(); i++) {
      try {
        DisplayPiiSubjectRow row = new DisplayPiiSubjectRow();
        row.setBean((DisplaySubjectBean) beans.get(i));
        answer.add(row);
      } catch (Exception e) {
      }
    }

    return answer;
  }


}
