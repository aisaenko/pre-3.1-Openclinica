/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.managestudy;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author jxu
 *
 */
public class DiscrepancyNoteBean extends AuditableEntityBean {
    // discrepancy_note_id serial NOT NULL,
    // description varchar(255),
    // discrepancy_note_type_id numeric,
    // resolution_status_id numeric,
    // detailed_notes varchar(1000),
    // parent_dn_id numeric,
    private String description = "";
    private int discrepancyNoteTypeId;
    private int resolutionStatusId;

    private DiscrepancyNoteType disType;
    private ResolutionStatus resStatus;

    private String detailedNotes = "";
    private int parentDnId = 0;
    private String entityType = "";
    private String column = "";// not in DB
    private int entityId = 0;
    private String field = "";
    private ArrayList children = new ArrayList();// not in DB
    private int studyId = 0;
    //added by BWP; 5/09/08;
    private int eventCRFId = 0;

    private UserAccountBean lastUpdator;
    private Date lastDateUpdated;
    private String subjectName = "";
    private String eventName = "";
    private Date eventStart;
    private String crfName = "";
    private int numChildren = 0;
    private String entityName = "";
    private String entityValue = "";
    private boolean isSaved = true;
    // indicates whether a note is in the db or
                                    // not
    // YW << if entity is ItemData, stageId = 5 means the crf of this ItemData
    // has been marked complete
    private int stageId = 0;
    private int itemId = 0;

    public int getEventCRFId() {
        return eventCRFId;
    }

    public void setEventCRFId(int eventCRFId) {
        this.eventCRFId = eventCRFId;
    }

    public int getStageId() {
        return this.stageId;
    }

    public void setStageId(int stageid) {
        this.stageId = stageid;
    }

    public int getItemId() {
        return this.itemId;
    }

    public void setItemId(int itemid) {
        this.itemId = itemid;
    }

    // YW >>

    public DiscrepancyNoteBean() {
        disType = DiscrepancyNoteType.INCOMPLETE;
        resStatus = ResolutionStatus.OPEN;
        children = new ArrayList();
        lastUpdator = new UserAccountBean();
    }

    /**
     * @return Returns the disType.
     */
    public DiscrepancyNoteType getDisType() {
        return disType;
    }

    /**
     * @param disType
     *            The disType to set.
     */
    public void setDisType(DiscrepancyNoteType disType) {
        this.disType = disType;
    }

    /**
     * @return Returns the resStatus.
     */
    public ResolutionStatus getResStatus() {
        return resStatus;
    }

    /**
     * @param resStatus
     *            The resStatus to set.
     */
    public void setResStatus(ResolutionStatus resStatus) {
        this.resStatus = resStatus;
    }

    /**
     * @return Returns the field.
     */
    public String getField() {
        return field;
    }

    /**
     * @param field
     *            The field to set.
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * @return Returns the entityId.
     */
    public int getEntityId() {
        return entityId;
    }

    /**
     * @param entityId
     *            The entityId to set.
     */
    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    /**
     * @return Returns the column.
     */
    public String getColumn() {
        return column;
    }

    /**
     * @param column
     *            The column to set.
     */
    public void setColumn(String column) {
        this.column = column;
    }

    /**
     * @return Returns the children.
     */
    public ArrayList getChildren() {
        return children;
    }

    /**
     * @param children
     *            The children to set.
     */
    public void setChildren(ArrayList children) {
        this.children = children;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return Returns the detailedNotes.
     */
    public String getDetailedNotes() {
        return detailedNotes;
    }

    /**
     * @param detailedNotes
     *            The detailedNotes to set.
     */
    public void setDetailedNotes(String detailedNotes) {
        this.detailedNotes = detailedNotes;
    }

    /**
     * @return Returns the discrepancyNoteTypeId.
     */
    public int getDiscrepancyNoteTypeId() {
        return discrepancyNoteTypeId;
    }

    /**
     * @param discrepancyNoteTypeId
     *            The discrepancyNoteTypeId to set.
     */
    public void setDiscrepancyNoteTypeId(int discrepancyNoteTypeId) {
        this.discrepancyNoteTypeId = discrepancyNoteTypeId;
    }

    /**
     * @return Returns the parentDnId.
     */
    public int getParentDnId() {
        return parentDnId;
    }

    /**
     * @param parentDnId
     *            The parentDnId to set.
     */
    public void setParentDnId(int parentDnId) {
        this.parentDnId = parentDnId;
    }

    /**
     * @return Returns the resolutionStatusId.
     */
    public int getResolutionStatusId() {
        return resolutionStatusId;
    }

    /**
     * @param resolutionStatusId
     *            The resolutionStatusId to set.
     */
    public void setResolutionStatusId(int resolutionStatusId) {
        this.resolutionStatusId = resolutionStatusId;
    }

    /**
     * @return Returns the entityType.
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * @param entityType
     *            The entityType to set.
     */
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    /**
     * @return Returns the studyId.
     */
    public int getStudyId() {
        return studyId;
    }

    /**
     * @param studyId
     *            The studyId to set.
     */
    public void setStudyId(int studyId) {
        this.studyId = studyId;
    }

    /**
     * @return Returns the eventName.
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * @param eventName
     *            The eventName to set.
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * @return Returns the eventStart.
     */
    public Date getEventStart() {
        return eventStart;
    }

    /**
     * @param eventStart
     *            The eventStart to set.
     */
    public void setEventStart(Date eventStart) {
        this.eventStart = eventStart;
    }

    /**
     * @return Returns the lastDateUpdated.
     */
    public Date getLastDateUpdated() {
        return lastDateUpdated;
    }

    /**
     * @param lastDateUpdated
     *            The lastDateUpdated to set.
     */
    public void setLastDateUpdated(Date lastDateUpdated) {
        this.lastDateUpdated = lastDateUpdated;
    }

    /**
     * @return Returns the lastUpdator.
     */
    public UserAccountBean getLastUpdator() {
        return lastUpdator;
    }

    /**
     * @param lastUpdator
     *            The lastUpdator to set.
     */
    public void setLastUpdator(UserAccountBean lastUpdator) {
        this.lastUpdator = lastUpdator;
    }

    /**
     * @return Returns the subjectName.
     */
    public String getSubjectName() {
        return subjectName;
    }

    /**
     * @param subjectName
     *            The subjectName to set.
     */
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    /**
     * @return Returns the crfName.
     */
    public String getCrfName() {
        return crfName;
    }

    /**
     * @param crfName
     *            The crfName to set.
     */
    public void setCrfName(String crfName) {
        this.crfName = crfName;
    }

    /**
     * @return Returns the numChildren.
     */
    public int getNumChildren() {
        return numChildren;
    }

    /**
     * @param numChildren
     *            The numChildren to set.
     */
    public void setNumChildren(int numChildren) {
        this.numChildren = numChildren;
    }

    /**
     * @return Returns the entityName.
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @param entityName
     *            The entityName to set.
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * @return Returns the entityValue.
     */
    public String getEntityValue() {
        return entityValue;
    }

    /**
     * @param entityValue
     *            The entityValue to set.
     */
    public void setEntityValue(String entityValue) {
        this.entityValue = entityValue;
    }

    /**
     * @return Returns the isSaved.
     */
    public boolean isSaved() {
        return isSaved;
    }

    /**
     * @param isSaved
     *            The isSaved to set.
     */
    public void setSaved(boolean isSaved) {
        this.isSaved = isSaved;
    }
}
