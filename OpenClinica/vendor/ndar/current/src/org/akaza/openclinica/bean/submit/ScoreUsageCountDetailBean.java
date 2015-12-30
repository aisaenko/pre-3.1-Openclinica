/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.submit;

import java.util.Date;

import org.akaza.openclinica.bean.core.AuditableEntityBean;

/**
 * 
 */
public class ScoreUsageCountDetailBean extends AuditableEntityBean {
	private int scoreUsageCountDetailId = 0;
	private int scoreUsageCountId = 0;
	private int userId = 0;
	private Date dateScored = null;
	private int eventCRFId = 0;

	public ScoreUsageCountDetailBean() {
	}

	/**
	 * @return the scoreUsageCountDetailId
	 */
	public int getScoreUsageCountDetailId() {
		return scoreUsageCountDetailId;
	}

	/**
	 * @param scoreUsageCountDetailId the scoreUsageCountDetailId to set
	 */
	public void setScoreUsageCountDetailId(int scoreUsageCountDetailId) {
		this.scoreUsageCountDetailId = scoreUsageCountDetailId;
	}

	/**
	 * @return the scoreUsageCountId
	 */
	public int getScoreUsageCountId() {
		return scoreUsageCountId;
	}

	/**
	 * @param scoreUsageCountId the scoreUsageCountId to set
	 */
	public void setScoreUsageCountId(int scoreUsageCountId) {
		this.scoreUsageCountId = scoreUsageCountId;
	}

	/**
	 * @return the dateScored
	 */
	public Date getDateScored() {
		return dateScored;
	}

	/**
	 * @param dateScored the dateScored to set
	 */
	public void setDateScored(Date dateScored) {
		this.dateScored = dateScored;
	}

	/**
	 * @return the eventCRFId
	 */
	public int getEventCRFId() {
		return eventCRFId;
	}

	/**
	 * @param eventCRFId the eventCRFId to set
	 */
	public void setEventCRFId(int eventCRFId) {
		this.eventCRFId = eventCRFId;
	}

	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}
}
