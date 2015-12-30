/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.submit;

import org.akaza.openclinica.bean.core.AuditableEntityBean;

/**
 * 
 */
public class ScoreUsageCountBean extends AuditableEntityBean {
	private int CRFVersionId = 0;
	private int scoreUsageCountId = 0;
	private int countPurchased = 0;
	private int countUsed = 0;

	public ScoreUsageCountBean() {
	}

	/**
	 * @return the countPurchased
	 */
	public int getCountPurchased() {
		return countPurchased;
	}

	/**
	 * @param countPurchased the countPurchased to set
	 */
	public void setCountPurchased(int countPurchased) {
		this.countPurchased = countPurchased;
	}

	/**
	 * @return the countUsed
	 */
	public int getCountUsed() {
		return countUsed;
	}

	/**
	 * @param countUsed the countUsed to set
	 */
	public void setCountUsed(int countUsed) {
		this.countUsed = countUsed;
	}

	/**
	 * @return the cRFVersionId
	 */
	public int getCRFVersionId() {
		return CRFVersionId;
	}

	/**
	 * @param versionId the cRFVersionId to set
	 */
	public void setCRFVersionId(int versionId) {
		CRFVersionId = versionId;
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
}
