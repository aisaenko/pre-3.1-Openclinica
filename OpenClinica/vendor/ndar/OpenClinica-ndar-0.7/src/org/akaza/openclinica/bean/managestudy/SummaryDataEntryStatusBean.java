/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.managestudy;

import org.akaza.openclinica.bean.core.AuditableEntityBean;

public class SummaryDataEntryStatusBean extends AuditableEntityBean {
  private String summaryDataEntryStatusId;
  private String name;
  private String description;
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
   * @return Returns the summaryDataEntryStatusId.
   */
  public String getSummaryDataEntryStatusId() {
    return summaryDataEntryStatusId;
  }
  /**
   * @param summaryDataEntryStatusId The summaryDataEntryStatusId to set.
   */
  public void setSummaryDataEntryStatusId(String summaryDataEntryStatusId) {
    this.summaryDataEntryStatusId = summaryDataEntryStatusId;
  }
  /**
   * @return the name
   */
  public String getName() {
	return name;
  }
  /**
   * @param name the name to set
   */
  public void setName(String name) {
	this.name = name;
  }
}
