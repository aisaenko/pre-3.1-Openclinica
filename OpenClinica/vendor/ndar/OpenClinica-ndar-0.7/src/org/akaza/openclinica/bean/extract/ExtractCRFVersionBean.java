/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 * 
 * Created on Feb 23, 2005
 */
package org.akaza.openclinica.bean.extract;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.submit.ItemBean;

/**
 * @author ssachs
 */
public class ExtractCRFVersionBean extends EntityBean {
	private String code;
	private String crfName;

	OrderedEntityBeansSet items = new OrderedEntityBeansSet(new ItemBean());
	
	public ItemBean addItem(Integer itemId, String itemName) {
		if ((itemId == null) || (itemName == null)) {
System.out.println("item null!");
			return new ItemBean();
		}
		
		ItemBean ib = new ItemBean();
		ib.setId(itemId.intValue());
		ib.setName(itemName);
if (!items.contains(ib)) {
//System.out.println("adding item: " + itemId.intValue() + "-" + itemName);
}
		return (ItemBean) items.add(ib);
	}
	
	
	/**
	 * @return Returns the code.
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code The code to set.
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return Returns the crfName.
	 */
	public String getCrfName() {
		return crfName;
	}
	/**
	 * @param crfName The crfName to set.
	 */
	public void setCrfName(String crfName) {
		this.crfName = crfName;
	}
	
	public OrderedEntityBeansSet getItems() {
		return items;
	}
	
	public static String getCode(int ind) {
		return "" + ind;
	}
}
