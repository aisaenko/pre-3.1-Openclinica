/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2009 Akaza Research
 */
package org.akaza.openclinica.ws;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.login.UserAccountBean;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Krikor Krumlian
 */
public class SubjectTransferBean extends EntityBean {

    private static final long serialVersionUID = 2270466335721404526L;
    private String gridId;
    private String personId;
    private String studySubjectId;
    private String secondaryId;
    private Date enrollmentDate;
    private Date dateOfBirth;
    private char gender;
    private String studyOid;
    private int ownerId;
    private Timestamp datetimeReceived;

    UserAccountBean owner;

    public SubjectTransferBean() {
        super();
    }

    public SubjectTransferBean(String personId, String studySubjectId, Date dateOfBirth, char gender, String studyOid) {
        super();
        this.personId = personId;
        this.studySubjectId = studySubjectId;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.studyOid = studyOid;
        this.datetimeReceived = new Timestamp(System.currentTimeMillis());
    }

    public String getGridId() {
        return gridId;
    }

    public void setGridId(String gridId) {
        this.gridId = gridId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getStudySubjectId() {
        return studySubjectId;
    }

    public void setStudySubjectId(String studySubjectId) {
        this.studySubjectId = studySubjectId;
    }

    public String getSecondaryId() {
        return this.secondaryId;
    }

    public void setSecondaryId(String secondaryId) {
        this.secondaryId = secondaryId;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public String getStudyOid() {
        return studyOid;
    }

    public void setStudyOid(String studyOid) {
        this.studyOid = studyOid;
    }

    public int getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public Timestamp getDatetimeReceived() {
        return datetimeReceived;
    }

    public void setDatetimeReceived(Timestamp datetimeReceived) {
        this.datetimeReceived = datetimeReceived;
    }

    public UserAccountBean getOwner() {
        return owner;
    }

    public void setOwner(UserAccountBean owner) {
        this.owner = owner;
    }

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(Date enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

}