/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 * 
 * Created on Jul 11, 2005
 */
package org.akaza.openclinica.bean.extract;

import java.util.ArrayList;

/**
 * @author ssachs
 */
public abstract class ReportBean {
	protected ArrayList metadata; // header block - includes SED CRF codes
	protected ArrayList data;
	protected ArrayList items; //items in the database
	private boolean metadataClosed;
	protected ArrayList currentRow;
	
	public ReportBean() {
		metadata = new ArrayList();
		data = new ArrayList();
		currentRow = new ArrayList();
		metadataClosed = false;
		items= new ArrayList();
	}
	
	public abstract String toString();
	
	public void nextCell(String value) {
		currentRow.add(value);
	}
	
	public void nextRow() {
		System.out.println("*** current row count: " + currentRow.size());
		if (!metadataClosed) {
			metadata.add(currentRow);
		}
		else {
			data.add(currentRow);
		}
		
		currentRow = new ArrayList();
	}
	
	public void closeMetadata() {
		if (currentRow.size() > 0) {
			metadata.add(currentRow);
		}
		currentRow = new ArrayList();
		metadataClosed = true;
	}
	
	protected String getDataColumnEntry(int col, int rowNum) {
		if (data.size() > rowNum) {
			ArrayList row = (ArrayList) data.get(rowNum);
			
			if ((row != null) && (row.size() > col)) {
				String s = (String) row.get(col);
				
				if (s != null) {
					return s;
				}
			}
		}
		
		return "";
	}
	
	

  /**
   * @return Returns the items.
   */
  public ArrayList getItems() {
    return items;
  }
  
  /**
   * @param items The items to set.
   */
  public void setItems(ArrayList items) {
    this.items = items;
  }
}
