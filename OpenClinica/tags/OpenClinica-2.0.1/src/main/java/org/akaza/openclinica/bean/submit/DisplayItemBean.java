/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.submit;

import java.util.ArrayList;
import org.akaza.openclinica.bean.managestudy.*;
import org.akaza.openclinica.bean.core.*;

/**
 * @author ssachs
 */
public class DisplayItemBean implements Comparable {
	private ItemDataBean data;
	private ItemBean item;
	private ItemFormMetadataBean metadata;

	// not in the database
	
	/**
	 * Not a database column.
	 * The elements are DisplayItemBeans whose metadata.parentId property
	 * equals the item.id property of this bean.
	 * Furthermore, they must be ordered in increasing value of their
	 * metadata.columnNumber property.
	 */
	private ArrayList children;
	
	
	/**
	 * Not a database column.
	 * Always equal to children.size().  Is primarily used when displaying the data entry form.
	 */
	private int numChildren;

	/**
	 * Not a database column.
	 * Always equal to the metadata.columnNumber property of the last element
	 * of children.  Set to 0 by default.
	 */
	private int numColumns;
	
	/**
	 * Not a database column.
	 * The number of discrepancy notes related to this item in the discrepancy note tables.
	 */
	private int numDiscrepancyNotes = 0;

	/**
	 * The event definition CRF which defines the event CRF
	 * within which this bean's item form matadata resides.
	 * Used to determine which, if any, null values should
	 * be added to the item's response set.
	 */
	private EventDefinitionCRFBean eventDefinitionCRF;

	private void setProperties() {
		data = new ItemDataBean();
		item = new ItemBean();
		metadata = new ItemFormMetadataBean();
		children = new ArrayList();
		numChildren = 0;
		numColumns = 0;		
	}
	
	public DisplayItemBean() {
		this.eventDefinitionCRF = new EventDefinitionCRFBean();
		setProperties();
	}
	
//	public DisplayItemBean(EventDefinitionCRFBean eventDefinitionCRF) {
//		this.eventDefinitionCRF = eventDefinitionCRF;
//		setProperties();
//	}
	
	/**
	 * @return Returns the data.
	 */
	public ItemDataBean getData() {
		return data;
	}
	/**
	 * @param data The data to set.
	 */
	public void setData(ItemDataBean data) {
		this.data = data;
	}
	/**
	 * @return Returns the item.
	 */
	public ItemBean getItem() {
		return item;
	}
	/**
	 * @param item The item to set.
	 */
	public void setItem(ItemBean item) {
		this.item = item;
	}
	/**
	 * @return Returns the metadata.
	 */
	public ItemFormMetadataBean getMetadata() {
		return metadata;
	}
	/**
	 * @param metadata The metadata to set.
	 */
	public void setMetadata(ItemFormMetadataBean metadata) {
		this.metadata = metadata;
		
		ResponseSetBean rsb = metadata.getResponseSet();
		org.akaza.openclinica.bean.core.ResponseType rt = rsb.getResponseType();
		
		if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CHECKBOX)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.RADIO)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECT)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECTMULTI)) {
			
			ArrayList nullValues = eventDefinitionCRF.getNullValuesList();
			for (int i = 0; i < nullValues.size(); i++) {
				NullValue nv = (NullValue) nullValues.get(i);
				ResponseOptionBean ro = new ResponseOptionBean();
				
				ro.setValue(nv.getName());
				ro.setText(nv.getDescription());
				rsb.addOption(ro);
			}
		}
				
		
		metadata.setResponseSet(rsb);
	}
	
	
	/**
	 * @return Returns the children.
	 */
	public ArrayList getChildren() {
		return children;
	}
	/**
	 * Assumes the children are ordered by getMetadata().getColumnNumber() in ascending order.
	 * @param children The children to set.
	 */
	public void setChildren(ArrayList children) {
		this.children = children;
		numChildren = children.size();
		
		if (numChildren > 0) {
			DisplayItemBean dib = (DisplayItemBean) children.get(numChildren - 1); 
			numColumns = dib.getMetadata().getColumnNumber();
		}
		else {
			numColumns = 1;
		}
	}
	
	/**
	 * @return Returns the numChildren.
	 */
	public int getNumChildren() {
		return numChildren;
	}
	/**
	 * @return Returns the numColumns.
	 */
	public int getNumColumns() {
		return numColumns;
	}

	/**
	 * Allows DisplayItemBean objects to be sorted by their metadata's ordinal value. 
	 * @param o The object this bean is being compared to.
	 * @return A negative number if o is a DisplayItemBean with a greater ordinal than this DisplayItemBean's
	 * 0 if o is not a DisplayItemBean or is a DisplayItemBean with an ordinal equal to this DisplayItemBean's
	 * A positive number if o is a DisplayItemBean with a lesser ordinal than this DisplayItemBean's
	 */
	public int compareTo(Object o) {
		if (!o.getClass().equals(this.getClass())) {
			return 0;
		}
		
		DisplayItemBean arg = (DisplayItemBean) o;
		return metadata.getOrdinal() - arg.metadata.getOrdinal();
	}
	
	/**
	 * Loads a set of values from the form into the bean.
	 * This means that the selected property of the ResponseOptionBean objects metadata.responseSet.opresponseOption value
	 * is set properly, and
	 * @param values
	 */
	public void loadFormValue(ArrayList values) {
		ResponseSetBean rsb = metadata.getResponseSet(); 

		String valueForDB = "";
		String glue = "";
		
		for (int i = 0; i < values.size(); i++) {
			String value = (String) values.get(i);
			
			if ((value == null) || value.equals("")) {
				continue;
			}
			
			rsb.setSelected(value.trim(), true);
			
			valueForDB += glue + value;
			glue = ",";
		}

		metadata.setResponseSet(rsb);
		data.setValue(valueForDB);
	}
	
	public void loadFormValue(String value) {
		ResponseSetBean rsb = metadata.getResponseSet(); 
		org.akaza.openclinica.bean.core.ResponseType rt = rsb.getResponseType();
		
		if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXT) ||
				rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXTAREA)) {
			rsb.setValue(value);
		}
		else {
			if (value != null) {
				rsb.setSelected(value.trim(), true);
			}
		}

		metadata.setResponseSet(rsb);
		data.setValue(value);
	}
	
	public void loadDBValue() {
		ResponseSetBean rsb = metadata.getResponseSet(); 
		org.akaza.openclinica.bean.core.ResponseType rt = rsb.getResponseType();
		String dbValue = data.getValue();

		if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CHECKBOX)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECTMULTI)) {
			String dbValues[] = dbValue.split(",");
			
			if (dbValues != null) {
				for (int i = 0; i < dbValues.length; i++) {
					if (dbValues[i] != null) {
						rsb.setSelected(dbValues[i].trim(), true);
					}
				}
			}
		}
        else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXT)
                  || rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXTAREA))  {
			rsb.setValue(dbValue);
		}
		else {
			if (dbValue != null) {
				dbValue = dbValue.trim();
			}
			rsb.setSelected(dbValue, true);
		}

		metadata.setResponseSet(rsb);
	}
	
	
	/**
	 * @return Returns the eventDefinitionCRF.
	 */
	public EventDefinitionCRFBean getEventDefinitionCRF() {
		return eventDefinitionCRF;
	}
	/**
	 * @param eventDefinitionCRF The eventDefinitionCRF to set.
	 */
	public void setEventDefinitionCRF(EventDefinitionCRFBean eventDefinitionCRF) {
		this.eventDefinitionCRF = eventDefinitionCRF;
	}
	
	
	/**
	 * @return Returns the numDiscrepancyNotes.
	 */
	public int getNumDiscrepancyNotes() {
		return numDiscrepancyNotes;
	}
	/**
	 * @param numDiscrepancyNotes The numDiscrepancyNotes to set.
	 */
	public void setNumDiscrepancyNotes(int numDiscrepancyNotes) {
		this.numDiscrepancyNotes = numDiscrepancyNotes;
	}
}
