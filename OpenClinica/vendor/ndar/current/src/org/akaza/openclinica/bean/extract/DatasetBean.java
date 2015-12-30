/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.extract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.EntityDAO;
import org.akaza.openclinica.dao.core.SQLFactory;

/**
 * @author thickerson
 *
 * 
 */
public class DatasetBean extends AuditableEntityBean{
	private int studyId;
	private String description;
	private String SQLStatement;
	private int numRuns = 0;
	private int runTime = 0;
	private java.util.Date dateLastRun;
	private java.util.Date dateStart;
	private java.util.Date dateEnd;
	private int approverId = 0;
	/*private ArrayList versionIds = new ArrayList();
	private ArrayList versionNames = new ArrayList();
	
	private ArrayList eventNames = new ArrayList();
	*/
	private ArrayList eventIds = new ArrayList();
	private ArrayList itemIds = new ArrayList();
	private HashMap itemMap = new HashMap();
	
	private boolean showEventLocation = false;	
	private boolean showEventStart = false;
	private boolean showEventEnd = false;
	private boolean showSubjectDob = false;
	private boolean showSubjectGender = false;
	private ArrayList itemDefCrf = new ArrayList();// map items with definition and CRF
	
	private String VIEW_NAME = "test_table_three";
	//put up here since we know it's going to be changed, tbh
	public DatasetBean() {
	}

	/**
	 * @return Returns the dateLastRun.
	 */
	public java.util.Date getDateLastRun() {
		return dateLastRun;
	}
	/**
	 * @param dateLastRun The dateLastRun to set.
	 */
	public void setDateLastRun(java.util.Date dateLastRun) {
		this.dateLastRun = dateLastRun;
	}
	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return Returns the numRuns.
	 */
	public int getNumRuns() {
		return numRuns;
	}
	/**
	 * @param numRuns The numRuns to set.
	 */
	public void setNumRuns(int numRuns) {
		this.numRuns = numRuns;
	}
	
	/**
	 * @return Returns the runTime.
	 */
	public int getRunTime() {
		return runTime;
	}
	/**
	 * @param runTime The runTime to set.
	 */
	public void setRunTime(int runTime) {
		this.runTime = runTime;
	}
	
	/**
	 * @return Returns the sQLStatement.
	 */
	public String getSQLStatement() {
		return SQLStatement;
	}
	/**
	 * @param statement The sQLStatement to set.
	 */
	public void setSQLStatement(String statement) {
		SQLStatement = statement;
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
	 * @return Returns the approverId.
	 */
	public int getApproverId() {
		return approverId;
	}
	/**
	 * @param approverId The approverId to set.
	 */
	public void setApproverId(int approverId) {
		this.approverId = approverId;
	}
	/**
	 * @return Returns the dateEnd.
	 */
	public java.util.Date getDateEnd() {
		return dateEnd;
	}
	/**
	 * @param dateEnd The dateEnd to set.
	 */
	public void setDateEnd(java.util.Date dateEnd) {
		this.dateEnd = dateEnd;
	}
	/**
	 * @return Returns the dateStart.
	 */
	public java.util.Date getDateStart() {
		return dateStart;
	}
	/**
	 * @param dateStart The dateStart to set.
	 */
	public void setDateStart(java.util.Date dateStart) {
		this.dateStart = dateStart;
	}
	
	
	/**
	 * takes the dataset bean information and generates a query; this will
	 * changes if the database changes.  This will also change when we apply
	 * filters. 
	 * @return string in SQL, to elicit information.
	 */
	public String generateQuery() {
		DAODigester digester = SQLFactory.getInstance().getDigester(SQLFactory.getInstance().DAO_FILTER);
		StringBuffer sb = new StringBuffer();
		sb.append(digester.getQuery("generateQuerySelectFromTable3Where")+" ");
		List params = new ArrayList();
		
		if (!this.getEventIds().isEmpty()) {
			params.add(this.getEventIds());
			sb.append(digester.getQuery("generateQueryWhereStudyEventDefInAnd")+" ");
		}
		if (!this.getItemIds().isEmpty()) {
			params.add(this.getItemIds());
			sb.append(digester.getQuery("generateQueryWhereItemIdInAnd")+" ");
		}
		
		params.add(this.dateStart);
		params.add(this.dateEnd);
		sb.append(digester.getQuery("generateQueryWhereDateCreatedBetween"));
		
        return EntityDAO.createStatement(sb.toString(), params);
	}
  
  /**
   * @return Returns the itemIds.
   */
  public ArrayList getItemIds() {
    return itemIds;
  }
  /**
   * @param itemIds The itemIds to set.
   */
  public void setItemIds(ArrayList itemIds) {
    this.itemIds = itemIds;
  }
   
  /**
   * @return Returns the itemMap.
   */
  public HashMap getItemMap() {
    return itemMap;
  }
  /**
   * @param itemMap The itemMap to set.
   */
  public void setItemMap(HashMap itemMap) {
    this.itemMap = itemMap;
  }
  /**
   * @return Returns the eventIds.
   */
  public ArrayList getEventIds() {
    return eventIds;
  }
  /**
   * @param eventIds The eventIds to set.
   */
  public void setEventIds(ArrayList eventIds) {
    this.eventIds = eventIds;
  }
  
  
  /**
   * @return Returns the showEventEnd.
   */
  public boolean isShowEventEnd() {
    return showEventEnd;
  }
  /**
   * @param showEventEnd The showEventEnd to set.
   */
  public void setShowEventEnd(boolean showEventEnd) {
    this.showEventEnd = showEventEnd;
  }
  /**
   * @return Returns the showEventLocation.
   */
  public boolean isShowEventLocation() {
    return showEventLocation;
  }
  /**
   * @param showEventLocation The showEventLocation to set.
   */
  public void setShowEventLocation(boolean showEventLocation) {
    this.showEventLocation = showEventLocation;
  }
  /**
   * @return Returns the showEventStart.
   */
  public boolean isShowEventStart() {
    return showEventStart;
  }
  /**
   * @param showEventStart The showEventStart to set.
   */
  public void setShowEventStart(boolean showEventStart) {
    this.showEventStart = showEventStart;
  }
  /**
   * @return Returns the showSubjectDob.
   */
  public boolean isShowSubjectDob() {
    return showSubjectDob;
  }
  /**
   * @param showSubjectDob The showSubjectDob to set.
   */
  public void setShowSubjectDob(boolean showSubjectDob) {
    this.showSubjectDob = showSubjectDob;
  }
  /**
   * @return Returns the showSubjectGender.
   */
  public boolean isShowSubjectGender() {
    return showSubjectGender;
  }
  /**
   * @param showSubjectGender The showSubjectGender to set.
   */
  public void setShowSubjectGender(boolean showSubjectGender) {
    this.showSubjectGender = showSubjectGender;
  }
  
  
  /**
   * @return Returns the itemDefCrf.
   */
  public ArrayList getItemDefCrf() {
    return itemDefCrf;
  }
  /**
   * @param itemDefCrf The itemDefCrf to set.
   */
  public void setItemDefCrf(ArrayList itemDefCrf) {
    this.itemDefCrf = itemDefCrf;
  }
}
