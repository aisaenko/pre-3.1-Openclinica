/**
 * 
 */
package org.akaza.openclinica.bean.coding;

/**
 * @author pgawade
 *
 */
public class CodeItemData {
	private Integer studySubjectId;
	private String eventName;
	private Integer eventOrdinal;
	private String crfName;
	private String crfVersionName;
	private String groupName;
	private Integer groupOrdinal;
	private String verbatimTermItemName;
	private String verbatimTermItemValue;
	private String codeItemName;
	private String codeItemValue;
	private String responseOptionValues;
	
	private Integer crfId;
	private Integer eventCrfId;
//	private Integer statusId;
	private String status;
	private String codeRootPath;
	
	public CodeItemData(){
		
	}
	
	public CodeItemData(Integer studySubjectId, String eventName, Integer eventOrdinal, String crfName, 
			String crfVersionName, String groupName, Integer groupOrdinal,String codeItemName, String codeItemValue, 
			String responseOptionValues, Integer crfId, Integer eventCrfId/*, Integer statusId*/){
		this.studySubjectId = studySubjectId;
		this.eventName = eventName;
		this.eventOrdinal = eventOrdinal;
		this.crfName = crfName;
		this.crfVersionName = crfVersionName;
		this.groupName = groupName;
		this.groupOrdinal = groupOrdinal;
		this.verbatimTermItemName = verbatimTermItemName;
		this.verbatimTermItemValue = verbatimTermItemValue;
		this.codeItemName = codeItemName;
		this.codeItemValue = codeItemValue;
		this.responseOptionValues = responseOptionValues;
		this.crfId = crfId;
		this.eventCrfId = eventCrfId;
//		this.statusId = statusId;
	}

	public Integer getStudySubjectId() {
		return studySubjectId;
	}
	public void setStudySubjectId(Integer studySubjectId) {
		this.studySubjectId = studySubjectId;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public Integer getEventOrdinal() {
		return eventOrdinal;
	}
	public void setEventOrdinal(Integer eventOrdinal) {
		this.eventOrdinal = eventOrdinal;
	}
	public String getCrfName() {
		return crfName;
	}
	public void setCrfName(String crfName) {
		this.crfName = crfName;
	}
	public String getCrfVersionName() {
		return crfVersionName;
	}
	public void setCrfVersionName(String crfVersionName) {
		this.crfVersionName = crfVersionName;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public Integer getGroupOrdinal() {
		return groupOrdinal;
	}
	public void setGroupOrdinal(Integer groupOrdinal) {
		this.groupOrdinal = groupOrdinal;
	}
	public String getVerbatimTermItemName() {
		return verbatimTermItemName;
	}
	public void setVerbatimTermItemName(String verbatimTermItemName) {
		this.verbatimTermItemName = verbatimTermItemName;
	}
	public String getVerbatimTermItemValue() {
		return verbatimTermItemValue;
	}
	public void setVerbatimTermItemValue(String verbatimTermItemValue) {
		this.verbatimTermItemValue = verbatimTermItemValue;
	}
	public String getCodeItemName() {
		return codeItemName;
	}
	public void setCodeItemName(String codeItemName) {
		this.codeItemName = codeItemName;
	}
	public String getCodeItemValue() {
		return codeItemValue;
	}
	public void setCodeItemValue(String codeItemValue) {
		this.codeItemValue = codeItemValue;
	}

	public String getResponseOptionValues() {
		return responseOptionValues;
	}

	public void setResponseOptionValues(String responseOptionValues) {
		this.responseOptionValues = responseOptionValues;
	}

	public Integer getCrfId() {
		return crfId;
	}

	public void setCrfId(Integer crfId) {
		this.crfId = crfId;
	}

	public Integer getEventCrfId() {
		return eventCrfId;
	}

	public void setEventCrfId(Integer eventCrfId) {
		this.eventCrfId = eventCrfId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCodeRootPath() {
		return codeRootPath;
	}

	public void setCodeRootPath(String codeRootPath) {
		this.codeRootPath = codeRootPath;
	}

//	public Integer getStatusId() {
//		return statusId;
//	}
//
//	public void setStatusId(Integer statusId) {
//		this.statusId = statusId;
//	}	
	
}
