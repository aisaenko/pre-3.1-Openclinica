/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.managestudy;

import java.util.ArrayList;

import org.akaza.openclinica.core.EntityBeanRow;

/**
 * @author jxu
 *  
 */
public class StudyEventDefinitionRow extends EntityBeanRow {
  //columns:
  //YW << Currently, for URL .../ListEventDefinition, the following collumn match is wrong 
  //      and not all of them are used.
  //      Change has been made and no trouble has been found to couple this this change.
  //YW >>
  public static final int COL_ORDINAL = 0;
  
  public static final int COL_NAME = 1;

  public static final int COL_REPEATING = 2;

  public static final int COL_TYPE = 3;

  public static final int COL_CATEGORY = 4;
  
  public static final int COL_POPULATED = 5;

  public static final int COL_DATE_CREATED = 8;//6;  -- not been used?
  
  public static final int COL_OWNER = 7; // -- not been used?
  
  public static final int COL_DATE_UPDATED = 6;//8;
  
  public static final int COL_UPDATER = 9; // -- not been used?

  public static final int COL_STATUS = 10; // -- not been used?

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.core.EntityBeanRow#compareColumn(java.lang.Object,
   *      int)
   */
  protected int compareColumn(Object row, int sortingColumn) {
    if (!row.getClass().equals(StudyEventDefinitionRow.class)) {
      return 0;
    }

    StudyEventDefinitionBean thisDefinition = (StudyEventDefinitionBean) bean;
    StudyEventDefinitionBean argDefinition = (StudyEventDefinitionBean) ((StudyEventDefinitionRow) row).bean;

    int answer = 0;
    switch (sortingColumn) {
    case COL_ORDINAL:
      if (thisDefinition.getOrdinal()>argDefinition.getOrdinal()) {
        answer = 1;
      } else if (thisDefinition.getOrdinal()==argDefinition.getOrdinal()){
        answer = 0;
      } else {
        answer = -1;
      }
      break;
    case COL_NAME:
      answer = thisDefinition.getName().toLowerCase().compareTo(argDefinition.getName().toLowerCase());
      break;
    case COL_REPEATING:      
      if (thisDefinition.isRepeating() && !argDefinition.isRepeating()){
        answer = 1;
      }
      else if (!thisDefinition.isRepeating() && argDefinition.isRepeating()){
        answer = -1;
      } else {
        answer=0;
      }           
      break;
    case COL_TYPE:
      answer = thisDefinition.getType().toLowerCase().compareTo(
          argDefinition.getType().toLowerCase());
      break;
    case COL_CATEGORY:
      answer = thisDefinition.getCategory().toLowerCase().compareTo(
          argDefinition.getCategory().toLowerCase());
      break;
    case COL_POPULATED:      
      if (thisDefinition.isPopulated() && !argDefinition.isPopulated()){
        answer = 1;
      }
      else if (!thisDefinition.isPopulated() && argDefinition.isPopulated()){
        answer = -1;
      } else {
        answer=0;
      }           
      break;  
    case COL_DATE_CREATED:
      answer = compareDate(thisDefinition.getCreatedDate(),argDefinition.getCreatedDate());
      break;
    case COL_OWNER:
      answer = thisDefinition.getOwner().getName().toLowerCase().compareTo(argDefinition.getOwner().getName().toLowerCase());
      break;
    case COL_DATE_UPDATED:
      answer = compareDate(thisDefinition.getUpdatedDate(),argDefinition.getUpdatedDate());
      break;
    case COL_UPDATER:
      answer = thisDefinition.getUpdater().getName().toLowerCase().compareTo(argDefinition.getUpdater().getName().toLowerCase());
      break;      
    case COL_STATUS:
      answer = thisDefinition.getStatus().compareTo(argDefinition.getStatus());
      break;
    }

    return answer;
  }

  public String getSearchString() {
    StudyEventDefinitionBean thisDefinition = (StudyEventDefinitionBean) bean;
    return thisDefinition.getName() + " " + thisDefinition.getType() + " "
        + thisDefinition.getCategory()+ " " + thisDefinition.getOwner().getName()+ " " + thisDefinition.getUpdater().getName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.core.EntityBeanRow#generatRowsFromBeans(java.util.ArrayList)
   */
  public ArrayList generatRowsFromBeans(ArrayList beans) {
    return StudyEventDefinitionRow.generateRowsFromBeans(beans);
  }

  public static ArrayList generateRowsFromBeans(ArrayList beans) {
    ArrayList answer = new ArrayList();

    Class[] parameters = null;
    Object[] arguments = null;

    for (int i = 0; i < beans.size(); i++) {
      try {
        StudyEventDefinitionRow row = new StudyEventDefinitionRow();
        row.setBean((StudyEventDefinitionBean) beans.get(i));
        answer.add(row);
      } catch (Exception e) {
      }
    }

    return answer;
  }

}