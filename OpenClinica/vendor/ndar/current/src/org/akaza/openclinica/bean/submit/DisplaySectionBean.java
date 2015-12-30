/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.submit;

import java.util.ArrayList;
import java.util.List;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;

/**
 * @author ssachs
 */
public class DisplaySectionBean {
	private CRFBean crf;
	private CRFVersionBean crfVersion;
	private EventCRFBean eventCRF;
	private EventDefinitionCRFBean eventDefinitionCRF;
	private SectionBean section;
	private ArrayList items; // an array of DisplayItemBeans which belong to this section
	private List<DisplaySkipRuleBean> skipRules; // an array of Skip Rules that apply to this section
	private boolean checkInputs;
	private boolean firstSection;
	private boolean lastSection;
	
	public DisplaySectionBean() {
		crf = new CRFBean();
		crfVersion = new CRFVersionBean();
		eventCRF = new EventCRFBean();
		section = new SectionBean();
		items = new ArrayList();
		skipRules = new ArrayList<DisplaySkipRuleBean>();
		checkInputs = true;
		firstSection = false;
		lastSection = false;
	}
	
	/**
	 * @return Returns the crf.
	 */
	public CRFBean getCrf() {
		return crf;
	}
	/**
	 * @param crf The crf to set.
	 */
	public void setCrf(CRFBean crf) {
		this.crf = crf;
	}
	/**
	 * @return Returns the crfVersion.
	 */
	public CRFVersionBean getCrfVersion() {
		return crfVersion;
	}
	/**
	 * @param crfVersion The crfVersion to set.
	 */
	public void setCrfVersion(CRFVersionBean crfVersion) {
		this.crfVersion = crfVersion;
	}
	/**
	 * @return Returns the eventCRF.
	 */
	public EventCRFBean getEventCRF() {
		return eventCRF;
	}
	/**
	 * @param eventCRF The eventCRF to set.
	 */
	public void setEventCRF(EventCRFBean eventCRF) {
		this.eventCRF = eventCRF;
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
	/**
	 * @return Returns the section.
	 */
	public SectionBean getSection() {
		return section;
	}
	/**
	 * @param section The section to set.
	 */
	public void setSection(SectionBean section) {
		this.section = section;
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
	 * @return Returns the checkInputs.
	 */
	public boolean isCheckInputs() {
		return checkInputs;
	}
	
//	/**
//	 * @param checkInputs The checkInputs to set.
//	 */
//	public void setCheckInputs(boolean checkInputs) {
//		this.checkInputs = checkInputs;
//	}
		
	/**
	 * @return Returns the firstSection.
	 */
	public boolean isFirstSection() {
		return firstSection;
	}
	/**
	 * @param firstSection The firstSection to set.
	 */
	public void setFirstSection(boolean firstSection) {
		this.firstSection = firstSection;
	}
	/**
	 * @return Returns the lastSection.
	 */
	public boolean isLastSection() {
		return lastSection;
	}
	/**
	 * @param lastSection The lastSection to set.
	 */
	public void setLastSection(boolean lastSection) {
		this.lastSection = lastSection;
	}

	/**
	 * @return the skipRules
	 */
	public List<DisplaySkipRuleBean> getSkipRules() {
		return skipRules;
	}

	/**
	 * @param skipRules the skipRules to set
	 */
	public void setSkipRules(List<DisplaySkipRuleBean> skipRules) {
		this.skipRules = skipRules;
	}
}
