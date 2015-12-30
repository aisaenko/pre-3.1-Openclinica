package org.akaza.openclinica.ws.logic;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.ws.SubjectTransferDAO;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.service.subject.SubjectService;
import org.akaza.openclinica.ws.SubjectTransferBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.sql.DataSource;

public abstract class CctsService {

    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
    private SubjectService subjectService;
    private String waitBeforeCommit;
    private SessionManager sessionManager;

    public boolean validate(SubjectTransferBean subjectTransferBean) {
        // assign owner
        UserAccountDAO userAccountDao = new UserAccountDAO(getDataSource());
        UserAccountBean userAccount = (UserAccountBean) userAccountDao.findByUserName("cctsoc");
        subjectTransferBean.setOwner(userAccount);
        subjectTransferBean.setOwnerId(userAccount.getId());
        // validate
        if (subjectTransferBean.getGridId().length() < 1) {
            logger.info("GridId is required.");
            return false;
        }
        String studySubjectId = subjectTransferBean.getStudySubjectId();
        if (studySubjectId == null || studySubjectId.length() < 1) {
            logger.info("studySubjectId is required.");
            return false;
        } else if (studySubjectId.length() > 30) {
            logger.info("studySubjectId should not be longer than 30.");
            return false;
        }
        StudyDAO stdao = new StudyDAO(this.getDataSource());
        StudyBean study = stdao.findByUniqueIdentifier(subjectTransferBean.getStudyOid());
        if (study == null) {
            throw new OpenClinicaSystemException("No study exists with uniqueIdentifier you provided");
        }
        int handleStudyId = study.getParentStudyId() > 0 ? study.getParentStudyId() : study.getId();
        org.akaza.openclinica.dao.service.StudyParameterValueDAO spvdao = new StudyParameterValueDAO(this.getDataSource());
        StudyParameterValueBean studyParameter = spvdao.findByHandleAndStudy(handleStudyId, "subjectPersonIdRequired");
        String personId = subjectTransferBean.getPersonId();
        if ("required".equals(studyParameter.getValue()) && (personId == null || personId.length() < 1)) {
            logger.info("personId is required for the study: " + study.getName());
            return false;
        }
        if (doesSubjectExist(subjectTransferBean)) {
            logger.info("studySubject already exists");
            throw new OpenClinicaSystemException("Duplicate studySubjectId");
        }
        if (personId != null && personId.length() > 255) {
            logger.info("personId should not be longer than 255.");
            return false;
        }
        String secondaryId = subjectTransferBean.getSecondaryId();
        if (secondaryId != null && secondaryId.length() > 30) {
            logger.info("secondaryId should not be longer than 30.");
            return false;
        }
        String gender = subjectTransferBean.getGender() + "";
        studyParameter = spvdao.findByHandleAndStudy(handleStudyId, "genderRequired");
        if ("true".equals(studyParameter.getValue()) && (gender == null || gender.length() < 1)) {
            logger.info("gender is required for the study:" + study.getName());
            return false;
        }
        Date enrollmentDate = subjectTransferBean.getEnrollmentDate();
        if (enrollmentDate == null) {
            logger.info("enrollmentDate is required.");
            return false;
        } else {
            if ((new Date()).compareTo(enrollmentDate) < 0) {
                logger.info("enrollmentDate should be in the past.");
                return false;
            }
        }

        return true;
    }

    public void commit(SubjectTransferBean subjectTransferBean) {
        boolean isSubjectInQueue = isSubjectInQueue(subjectTransferBean);
        boolean isSubjectInMain = doesSubjectExist(subjectTransferBean);

        if (isSubjectInMain) {
            // TODO : either return something or throw exception or don't do
            // anything
            logger.debug("SubjectInMain");
        } else if (isSubjectInQueue) {
            logger.debug("SubjectInQueue");
            if (createSubject(subjectTransferBean)) {
                this.removeSubjectFromQueue(subjectTransferBean);
            }
        } else {
            logger.debug("creating subject transfer");
            createSubjectTransfer(subjectTransferBean);
        }
    }

    public void rollback(SubjectTransferBean subjectTransfer) {
        boolean isSubjectInQueue = isSubjectInQueue(subjectTransfer); // subjectAlreadyExistInTemporaryLocation();
        boolean isSubjectInMain = doesSubjectExist(subjectTransfer); // jectExistInDatabase();

        if (isSubjectInMain) {
            // TODO : remove subject
            logger.debug("rollbackSubjectInMain.");
        }
        if (isSubjectInQueue) {
            // TODO : Remove from Queue
            logger.debug("rollbackSubjectInQueue, removing from subjectTransfer.");
            this.removeSubjectFromQueue(subjectTransfer);
        } else {
            // TODO : Do nothing
            logger.debug("rollback Subject doesn't exist in Queue.");
        }
    }

    public void autoCommit() {
        ResourceBundleProvider.updateLocale(new Locale("en_US"));
        SubjectTransferDAO tfdao = new SubjectTransferDAO(this.getDataSource());
        ArrayList<SubjectTransferBean> subjectTransferBeans = new ArrayList<SubjectTransferBean>();
        try {
            subjectTransferBeans = (ArrayList<SubjectTransferBean>) tfdao.findAll();
        } catch (OpenClinicaException e) {
            logger.info("Fetching all SubjectTransfer records failed.");
            e.printStackTrace();
        }
        logger.debug("autoCommit, subjectTransferBeans size=" + subjectTransferBeans.size());
        for (SubjectTransferBean tf : subjectTransferBeans) {
            boolean isSubjectInMain = doesSubjectExist(tf);
            if (isSubjectInMain) {
            } else {
                if (this.isSubjectInQueueForMoreThanXSeconds(tf)) {
                    if (createSubject(tf)) {
                        this.removeSubjectFromQueue(tf);
                    }
                }
            }
        }
    }

    public abstract boolean isSubjectInQueue(SubjectTransferBean subjectTransferBean);

    public abstract boolean doesSubjectExist(SubjectTransferBean subjectTransferBean);

    public abstract void createSubjectTransfer(SubjectTransferBean subjectTransfer);

    public abstract boolean createSubject(SubjectTransferBean subjectTransfer);

    public abstract List<SubjectTransferBean> getAllSubjectsInQueue();

    public abstract void removeSubjectFromQueue(SubjectTransferBean subjectTransfer);

    private boolean isSubjectInQueueForMoreThanXSeconds(SubjectTransferBean subjectTransferBean) {
        // TODO: 1.Implement Method
        long waitTime = System.currentTimeMillis() - subjectTransferBean.getDatetimeReceived().getTime();
        return waitTime >= Long.parseLong(waitBeforeCommit) ? true : false;
    }

    public SubjectService getSubjectService() {
        return subjectService;
    }

    public void setSubjectService(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    public String getWaitBeforeCommit() {
        return waitBeforeCommit;
    }

    public void setWaitBeforeCommit(String waitBeforeCommit) {
        this.waitBeforeCommit = waitBeforeCommit;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public DataSource getDataSource() {
        return sessionManager.getDataSource();
    }

}
