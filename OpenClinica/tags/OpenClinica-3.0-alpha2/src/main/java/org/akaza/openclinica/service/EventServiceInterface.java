package org.akaza.openclinica.service;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.exception.OpenClinicaSystemException;

import java.util.Date;

public interface EventServiceInterface {

    public Integer validateAndSchedule(String studySubjectId, String studyUniqueId, String eventDefinitionOID, String location, Date startDateTime,
            Date endDateTime, UserAccountBean user) throws OpenClinicaSystemException;

}