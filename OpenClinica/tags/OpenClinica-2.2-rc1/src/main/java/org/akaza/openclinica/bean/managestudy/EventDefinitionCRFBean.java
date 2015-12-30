/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.managestudy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.AuditableEntityBean;

/**
 * @author jxu
 */
public class EventDefinitionCRFBean extends AuditableEntityBean implements Comparable {
  private int studyEventDefinitionId = 0;

  private int studyId = 0;

  private int crfId = 0;

  private boolean requiredCRF = true;

  private boolean doubleEntry = false;

  private boolean requireAllTextFilled = false;//not on page for now

  private boolean decisionCondition = true;

  private int defaultVersionId = 0;
  /**
   * @return Returns the ordinal.
   */
  public int getOrdinal() {
    return ordinal;
  }
  /**
   * @param ordinal The ordinal to set.
   */
  public void setOrdinal(int ordinal) {
    this.ordinal = ordinal;
  }
  private int ordinal = 1;

  /**
   * A comma-separated list of null values allowed.
   * Getting this value is deprecated.  Use nullValuesList instead.
   */
  private String nullValues = "";
  
  /**
   * An array of null values allowed.  Each element is a NullValue object.
   * This property is set in setNullValues.
   */
  private ArrayList nullValuesList = new ArrayList();

  private String crfName = ""; //not in table

  private ArrayList versions = new ArrayList();//not in table

  private CRFBean crf = new CRFBean(); // not in table

  private HashMap nullFlags = new LinkedHashMap(); //not in table
  
  private String defaultVersionName = "";// not in DB

  /**
   * @return Returns the crfId.
   */
  public int getCrfId() {
    return crfId;
  }

  /**
   * @param crfId
   *          The crfId to set.
   */
  public void setCrfId(int crfId) {
    this.crfId = crfId;
  }

  /**
   * @return Returns the crfName.
   */
  public String getCrfName() {
    return crfName;
  }

  /**
   * @param crfName
   *          The crfName to set.
   */
  public void setCrfName(String crfName) {
    this.crfName = crfName;
  }

  /**
   * @return Returns the decisionCondition.
   */
  public boolean isDecisionCondition() {
    return decisionCondition;
  }

  /**
   * @param decisionCondition
   *          The decisionCondition to set.
   */
  public void setDecisionCondition(boolean decisionCondition) {
    this.decisionCondition = decisionCondition;
  }

  /**
   * @return Returns the defaultVersionId.
   */
  public int getDefaultVersionId() {
    return defaultVersionId;
  }

  /**
   * @param defaultVersionId
   *          The defaultVersionId to set.
   */
  public void setDefaultVersionId(int defaultVersionId) {
    this.defaultVersionId = defaultVersionId;
  }

  /**
   * @return Returns the doubleEntry.
   */
  public boolean isDoubleEntry() {
    return doubleEntry;
  }

  /**
   * @param doubleEntry
   *          The doubleEntry to set.
   */
  public void setDoubleEntry(boolean doubleEntry) {
    this.doubleEntry = doubleEntry;
  }

  /**
   * @return Returns the requireAllTextFilled.
   */
  public boolean isRequireAllTextFilled() {
    return requireAllTextFilled;
  }

  /**
   * @param requireAllTextFilled
   *          The requireAllTextFilled to set.
   */
  public void setRequireAllTextFilled(boolean requireAllTextFilled) {
    this.requireAllTextFilled = requireAllTextFilled;
  }

  /**
   * @return Returns the requiredCRF.
   */
  public boolean isRequiredCRF() {
    return requiredCRF;
  }

  /**
   * @param requiredCRF
   *          The requiredCRF to set.
   */
  public void setRequiredCRF(boolean requiredCRF) {
    this.requiredCRF = requiredCRF;
  }

  /**
   * @return Returns the studyEventDefinitionId.
   */
  public int getStudyEventDefinitionId() {
    return studyEventDefinitionId;
  }

  /**
   * @param studyEventDefinitionId
   *          The studyEventDefinitionId to set.
   */
  public void setStudyEventDefinitionId(int studyEventDefinitionId) {
    this.studyEventDefinitionId = studyEventDefinitionId;
  }

  /**
   * @return Returns the studyId.
   */
  public int getStudyId() {
    return studyId;
  }

  /**
   * @param studyId
   *          The studyId to set.
   */
  public void setStudyId(int studyId) {
    this.studyId = studyId;
  }

  /**
   * @deprecated
   * @return Returns the nullValues.
   */
  public String getNullValues() {
    return nullValues;
  }

  /**
   * @param nullValues
   *          The nullValues to set.
   */
  public void setNullValues(String nullValues) {
    this.nullValues = nullValues;
    String[] nullValuesSeparated = nullValues.split(",");
    
    nullValuesList = new ArrayList();
    if (nullValuesSeparated != null) {
    	for (int i = 0; i < nullValuesSeparated.length; i++) {
    		String val = nullValuesSeparated[i];
    		org.akaza.openclinica.bean.core.NullValue nv = org.akaza.openclinica.bean.core.NullValue.getByName(val);
    		if (nv.isActive()) {
    			nullValuesList.add(nv);
    		}
    	}
    }
  }

  /**
   * @return Returns the versions.
   */
  public ArrayList getVersions() {
    return versions;
  }

  /**
   * @param versions
   *          The versions to set.
   */
  public void setVersions(ArrayList versions) {
    this.versions = versions;
  }

  /**
   * @return Returns the crf.
   */
  public CRFBean getCrf() {
    return crf;
  }

  /**
   * @param crf
   *          The crf to set.
   */
  public void setCrf(CRFBean crf) {
    this.crf = crf;
  }

  /**
   * @return Returns the nullFlags.
   */
  public HashMap getNullFlags() {
    if (nullFlags.size() == 0) {
      nullFlags.put("NI", "0");
      nullFlags.put("NA", "0");
      nullFlags.put("UNK", "0");
      nullFlags.put("NASK", "0");
      nullFlags.put("NAV", "0");
      nullFlags.put("ASKU", "0");
      nullFlags.put("NAV", "0");
      nullFlags.put("OTH", "0");
      nullFlags.put("PINF", "0");
      nullFlags.put("NINF", "0");
      nullFlags.put("MSK", "0");
      nullFlags.put("NP", "0");

    }
    return nullFlags;
  }

  /**
   * @param nullFlags
   *          The nullFlags to set.
   */
  public void setNullFlags(HashMap nullFlags) {
    this.nullFlags = nullFlags;
  }

  
  /**
   * @return Returns the nullValuesList.
   */
  public ArrayList getNullValuesList() {
  	return nullValuesList;
  }
  /**
   * @param nullValuesList The nullValuesList to set.
   */
  public void setNullValuesList(ArrayList nullValuesList) {
  	this.nullValuesList = nullValuesList;
  }
  /**
   * @return Returns the defaultVersionName.
   */
  public String getDefaultVersionName() {
    return defaultVersionName;
  }
  /**
   * @param defaultVersionName The defaultVersionName to set.
   */
  public void setDefaultVersionName(String defaultVersionName) {
    this.defaultVersionName = defaultVersionName;
  }
  
  
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if ((o == null) || !o.getClass().equals(this.getClass())) {
			return 0;
		}

		EventDefinitionCRFBean edcb = (EventDefinitionCRFBean) o; 
		return this.ordinal - edcb.ordinal;
	}
}