package org.akaza.openclinica.service.subject;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.ws.SubjectTransferBean;

import java.util.Date;

public interface SubjectServiceInterface {

    public abstract String createSubject(SubjectBean subjectBean, StudyBean studyBean, Date enrollmentDate);

    public abstract boolean validate(SubjectTransferBean subjectTransferBean);

}