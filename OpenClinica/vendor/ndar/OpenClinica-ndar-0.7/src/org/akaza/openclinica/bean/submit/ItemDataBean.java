/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.submit;

import org.akaza.openclinica.bean.core.*;
/**<P>ItemDataBean.java, the object that represents an actual answer, 
 * or a point of data, in the database.
 * @author thickerson
 *
 * 
 */
public class ItemDataBean extends AuditableEntityBean {
	private int eventCRFId;
	private int itemId;
	private String value;//name will be null
	
	public ItemDataBean() {
		eventCRFId = 0;
		itemId = 0;
		value = "";
	}
	
	/**
	 * @return Returns the itemId.
	 */
	public int getItemId() {
		return itemId;
	}
	/**
	 * @param itemId The itemId to set.
	 */
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	/**
	 * @return Returns the eventCRFId.
	 */
	public int getEventCRFId() {
		return eventCRFId;
	}
	/**
	 * @param eventCRFId The eventCRFId to set.
	 */
	public void setEventCRFId(int eventCRFId) {
		this.eventCRFId = eventCRFId;
	}
	/**
	 * @return Returns the value.
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
