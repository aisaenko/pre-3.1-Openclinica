package org.akaza.openclinica.controller.helper.table;

/**
 * tableFacade.setColumnProperties("studySubjectId","personId","secondaryId",
 "eventName",
 "eventDate","enrollmentDate","subjectStatus","crfNameVersion","crfStatus",
 "lastUpdatedDate",
 "lastUpdatedBy","sdvStatusActions");
 */
public class SubjectSDVContainer {

    private String studySubjectId;
    private String personId;
    private String secondaryId;
    private String eventName;
    private String eventDate;
    private String enrollmentDate;
    private String studySubjectStatus;
    private String crfNameVersion;
    private String crfStatus;
    private String lastUpdatedDate;
    private String lastUpdatedBy;
    private String sdvStatusActions;
    private String numberOfCRFsSDV;
    private String percentageOfCRFsSDV;
    private String group;


    public SubjectSDVContainer() {
        studySubjectId="";
        personId="";
        secondaryId="";
        eventName="";
        eventDate="";
        enrollmentDate="";
        studySubjectStatus ="";
        crfNameVersion="";
        crfStatus="";
        lastUpdatedDate="";
        lastUpdatedBy="";
        sdvStatusActions="";
        numberOfCRFsSDV="";
        percentageOfCRFsSDV="";
        group="";
    }

    public String getNumberOfCRFsSDV() {
        return numberOfCRFsSDV;
    }

    public void setNumberOfCRFsSDV(String numberOfCRFsSDV) {
        this.numberOfCRFsSDV = numberOfCRFsSDV;
    }

    public String getPercentageOfCRFsSDV() {
        return percentageOfCRFsSDV;
    }

    public void setPercentageOfCRFsSDV(String percentageOfCRFsSDV) {
        this.percentageOfCRFsSDV = percentageOfCRFsSDV;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getCrfNameVersion() {
        return crfNameVersion;
    }

    public void setCrfNameVersion(String crfNameVersion) {
        this.crfNameVersion = crfNameVersion;
    }

    public String getStudySubjectId() {
        return studySubjectId;
    }

    public void setStudySubjectId(String studySubjectId) {
        this.studySubjectId = studySubjectId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getSecondaryId() {
        return secondaryId;
    }

    public void setSecondaryId(String secondaryId) {
        this.secondaryId = secondaryId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getStudySubjectStatus() {
        return studySubjectStatus;
    }

    public void setStudySubjectStatus(String studySubjectStatus) {
        this.studySubjectStatus = studySubjectStatus;
    }

    public String getCrfStatus() {
        return crfStatus;
    }

    public void setCrfStatus(String crfStatus) {
        this.crfStatus = crfStatus;
    }

    public String getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(String lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getSdvStatusActions() {
        return sdvStatusActions;
    }

    public void setSdvStatusActions(String sdvStatusActions) {
        this.sdvStatusActions = sdvStatusActions;
    }
}
