package org.akaza.openclinica.ws.logic;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.ws.SubjectTransferDAO;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.ws.SubjectTransferBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseCctsService extends CctsService {

    @Override
    public boolean isSubjectInQueue(SubjectTransferBean subjectTransferBean) {
        SubjectTransferDAO stdao = new SubjectTransferDAO(getDataSource());

        return stdao.isFoundByGridId(subjectTransferBean.getGridId());
    }

    @Override
    public boolean doesSubjectExist(SubjectTransferBean subjectTransferBean) {
        // TODO: Implement this
        StudySubjectDAO ssdao = new StudySubjectDAO(getDataSource());
        StudyDAO studyDao = new StudyDAO(getDataSource());
        StudyBean studyBean = studyDao.findByUniqueIdentifier(subjectTransferBean.getStudyOid());
        StudySubjectBean ssbean = ssdao.findByLabelAndStudy(subjectTransferBean.getStudySubjectId(), studyBean);
        return ssbean.getId() > 0 ? true : false;
    }

    @Override
    public boolean createSubject(SubjectTransferBean subjectTransfer) {
        // TODO Auto-generated method stub
        StudyBean study = new StudyBean();
        // study.setOid(subjectTransfer.getStudyOid());
        study.setIdentifier(subjectTransfer.getStudyOid());
        SubjectBean subject = new SubjectBean();
        subject.setUniqueIdentifier(subjectTransfer.getPersonId());
        subject.setLabel(subjectTransfer.getStudySubjectId());
        subject.setDateOfBirth(subjectTransfer.getDateOfBirth());
        subject.setGender(subjectTransfer.getGender());
        if (subjectTransfer.getOwner() != null) {
            subject.setOwner(subjectTransfer.getOwner());
        }
        subject.setCreatedDate(new Date(subjectTransfer.getDatetimeReceived().getTime()));
        return "yes".equals(this.getSubjectService().createSubject(subject, study, subjectTransfer.getEnrollmentDate())) ? true : false;
    }

    @Override
    public void createSubjectTransfer(SubjectTransferBean subjectTransfer) {
        SubjectTransferDAO stdao = new SubjectTransferDAO(getDataSource());
        try {
            stdao.create(subjectTransfer);
        } catch (OpenClinicaException e) {
            logger.info("createSubjectTransfer failed.");
            e.printStackTrace();
        }
    }

    @Override
    public List<SubjectTransferBean> getAllSubjectsInQueue() {
        // TODO Auto-generated method stub
        return new ArrayList<SubjectTransferBean>();
    }

    @Override
    public void removeSubjectFromQueue(SubjectTransferBean subjectTransfer) {
        // TODO Auto-generated method stub
        SubjectTransferDAO stdao = new SubjectTransferDAO(this.getDataSource());
        try {
            stdao.delete(subjectTransfer.getGridId());
        } catch (OpenClinicaException e) {
            logger.info("deleteSubjectTransfer failed.");
            e.printStackTrace();
        }
    }

}
