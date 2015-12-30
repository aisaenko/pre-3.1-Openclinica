package org.akaza.openclinica.bean.extract;

import java.util.ArrayList;

/**
 * @author	ywang
 */
public class XMLReportBean extends ReportBean {
	private ArrayList dataLines;
	
	public XMLReportBean(ArrayList xml) {
		dataLines = new ArrayList(xml);
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for(int i=0; i<dataLines.size(); ++i) {
			System.out.println(dataLines.get(i));
			buffer.append(dataLines.get(i));
			buffer.append("\n");
		}
		return buffer.toString();
	}
	
	public ArrayList getXML() {
		return dataLines;
	}
	
	public void setXML(ArrayList xml) {
		dataLines = xml;
	}
}