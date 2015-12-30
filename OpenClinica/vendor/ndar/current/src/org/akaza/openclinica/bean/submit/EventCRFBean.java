/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.submit;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.*;
import java.util.Date;
import org.akaza.openclinica.control.submit.*;

/**
 * <P>EventCRFBean, the object that collects data on a subject while
 * filling out a CRF.  Previous equivalent was individualInstrumentBean in
 * version v.1.
 * @author thickerson
 */
public class EventCRFBean extends AuditableEntityBean {
	// constants for the different completion status values
	public static final int COMPLETION_STATUS_NULL = 0;
	public static final int COMPLETION_STATUS_DEFAULT = 1;
	
	private int studyEventId = 0;
	private int CRFVersionId = 0;
	private Date dateInterviewed;
	private String interviewerName = "";
	private int completionStatusId = COMPLETION_STATUS_NULL;
	// private int statusId =1;
	private Status status;
	private String annotations = "";
	private Date dateCompleted;
	private int validatorId = 0;
	private Date dateValidate;
	private Date dateValidateCompleted;
	private String validatorAnnotations = "";
	private String validateString = "";
	private int studySubjectId = 0;
	private int formUsageCountId = 0;
	
	// the following properties are not in the table; they are meant for convenience
	private CRFBean crf = new CRFBean();
	private CRFVersionBean crfVersion = new CRFVersionBean();
	private DataEntryStage stage;

	public EventCRFBean() {
		stage = DataEntryStage.INVALID;
		status = Status.INVALID;
	}
	
	/**
	 * @return Returns the annotations.
	 */
	public String getAnnotations() {
		return annotations;
	}
	/**
	 * @param annotations The annotations to set.
	 */
	public void setAnnotations(String annotations) {
		this.annotations = annotations;
	}
	/**
	 * @return Returns the completionStatusId.
	 */
	public int getCompletionStatusId() {
		return completionStatusId;
	}
	/**
	 * @param completionStatusId The completionStatusId to set.
	 */
	public void setCompletionStatusId(int completionStatusId) {
		this.completionStatusId = completionStatusId;
	}
	/**
	 * @return Returns the cRFVersionId.
	 */
	public int getCRFVersionId() {
		return CRFVersionId;
	}
	/**
	 * @param versionId The cRFVersionId to set.
	 */
	public void setCRFVersionId(int versionId) {
		CRFVersionId = versionId;
	}
	/**
	 * @return Returns the dateCompleted.
	 */
	public Date getDateCompleted() {
		return dateCompleted;
	}
	/**
	 * @param dateCompleted The dateCompleted to set.
	 */
	public void setDateCompleted(Date dateCompleted) {
		this.dateCompleted = dateCompleted;
	}
	/**
	 * @return Returns the dateInterviewed.
	 */
	public Date getDateInterviewed() {
		return dateInterviewed;
	}
	/**
	 * @param dateInterviewed The dateInterviewed to set.
	 */
	public void setDateInterviewed(Date dateInterviewed) {
		this.dateInterviewed = dateInterviewed;
	}
	/**
	 * @return Returns the dateValidate.
	 */
	public Date getDateValidate() {
		return dateValidate;
	}
	/**
	 * @param dateValidate The dateValidate to set.
	 */
	public void setDateValidate(Date dateValidate) {
		this.dateValidate = dateValidate;
	}
	/**
	 * @return Returns the dateValidateCompleted.
	 */
	public Date getDateValidateCompleted() {
		return dateValidateCompleted;
	}
	/**
	 * @param dateValidateCompleted The dateValidateCompleted to set.
	 */
	public void setDateValidateCompleted(Date dateValidateCompleted) {
		this.dateValidateCompleted = dateValidateCompleted;
	}
	/**
	 * @return Returns the interviewerName.
	 */
	public String getInterviewerName() {
		return interviewerName;
	}
	/**
	 * @param interviewerName The interviewerName to set.
	 */
	public void setInterviewerName(String interviewerName) {
		this.interviewerName = interviewerName;
	}
	
	/**
	 * @return Returns the status.
	 */
	public Status getStatus() {
		return status;
	}
	
	/**
	 * @deprecated
	 * @return Returns the statusId.
	 */
	public int getStatusId() {
		return status.getId();
	}
	
	/**
	 * @param status The status to set.
	 */
	public void setStatus(Status status) {
		this.status = status;
	}
	
	/**
	 * @deprecated
	 * @param statusId The statusId to set.
	 */
	public void setStatusId(int statusId) {
		status = Status.get(statusId);
	}
	
	/**
	 * @return Returns the studyEventId.
	 */
	public int getStudyEventId() {
		return studyEventId;
	}
	/**
	 * @param studyEventId The studyEventId to set.
	 */
	public void setStudyEventId(int studyEventId) {
		this.studyEventId = studyEventId;
	}

	/**
	 * @return Returns the studySubjectId.
	 */
	public int getStudySubjectId() {
		return studySubjectId;
	}
	/**
	 * @param studySubjectId The studySubjectId to set.
	 */
	public void setStudySubjectId(int studySubjectId) {
		this.studySubjectId = studySubjectId;
	}
	/**
	 * @return Returns the validateString.
	 */
	public String getValidateString() {
		return validateString;
	}
	/**
	 * @param validateString The validateString to set.
	 */
	public void setValidateString(String validateString) {
		this.validateString = validateString;
	}
	/**
	 * @return Returns the validatorAnnotations.
	 */
	public String getValidatorAnnotations() {
		return validatorAnnotations;
	}
	/**
	 * @param validatorAnnotations The validatorAnnotations to set.
	 */
	public void setValidatorAnnotations(String validatorAnnotations) {
		this.validatorAnnotations = validatorAnnotations;
	}
	/**
	 * @return Returns the validatorId.
	 */
	public int getValidatorId() {
		return validatorId;
	}
	/**
	 * @param validatorId The validatorId to set.
	 */
	public void setValidatorId(int validatorId) {
		this.validatorId = validatorId;
	}
	
	/**
	 * Uses the status and created/updated dates to determine which stage the
	 * Event CRF is in.
	 * @return The Event CRF's data entry stage.
	 */
	public DataEntryStage getStage() {
	  if (stage != null) {
	    if (!stage.equals(DataEntryStage.INVALID)) {
	      return stage;
	    }
	  }

	  if (!active || !status.isActive()) {
	    stage = DataEntryStage.UNCOMPLETED;
	  }
	 
	  if (status.equals(Status.AVAILABLE)) {
	    stage = DataEntryStage.INITIAL_DATA_ENTRY;
	  }
	 
	  if (status.equals(Status.PENDING)) {
	    if (validatorId != 0) {
	      stage = DataEntryStage.DOUBLE_DATA_ENTRY;
	    }
	    else {
	      stage = DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE;
	    }
	  }
	  
	  if (status.equals(Status.UNAVAILABLE)) {
	    stage = DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE;
	  }
	  
	  if (status.equals(Status.LOCKED)) {
	    stage = DataEntryStage.LOCKED;
	  }
	 
	  return stage;
	}	
	
	public void setStage(DataEntryStage stage) {
	  this.stage=stage;
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
	 * @return the formUsageCountId
	 */
	public int getFormUsageCountId() {
		return formUsageCountId;
	}

	/**
	 * @param formUsageCountId the formUsageCountId to set
	 */
	public void setFormUsageCountId(int formUsageCountId) {
		this.formUsageCountId = formUsageCountId;
	}
}
