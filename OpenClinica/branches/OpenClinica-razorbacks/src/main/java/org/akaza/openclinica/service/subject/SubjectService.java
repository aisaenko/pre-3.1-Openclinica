package org.akaza.openclinica.service.subject;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.ws.SubjectTransferBean;

import java.util.Date;

import javax.sql.DataSource;

public class SubjectService implements SubjectServiceInterface {

    SubjectDAO subjectDao;
    StudySubjectDAO studySubjectDao;
    UserAccountDAO userAccountDao;
    StudyDAO studyDao;
    DataSource dataSource;

    public SubjectService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public SubjectService(SessionManager sessionManager) {
        this.dataSource = sessionManager.getDataSource();
    }

    /*
     * (non-Javadoc)
     * @see org.akaza.openclinica.service.subject.SubjectServiceInterface#createSubject(org.akaza.openclinica.bean.submit.SubjectBean,
     * org.akaza.openclinica.bean.managestudy.StudyBean)
     */
    public String createSubject(SubjectBean subjectBean, StudyBean studyBean, Date enrollmentDate) {
        studyBean = getStudyDao().findByUniqueIdentifier(studyBean.getIdentifier()); // Need to
        // do
        // validation
        // in case
        // Study not
        // present
        subjectBean.setStatus(Status.AVAILABLE);
        // subjectBean.setOwnerId(1); // This is definitely a hack to be resolved
        // with authenticating web services
        // subjectBean.setOwner(getUserAccount()); // inconsistencies
        subjectBean = getSubjectDao().create(subjectBean);
        StudySubjectBean studySubject = createStudySubject(subjectBean, studyBean, enrollmentDate);
        getStudySubjectDao().createWithoutGroup(studySubject);
        return "yes";
    }

    private StudySubjectBean createStudySubject(SubjectBean subject, StudyBean studyBean, Date enrollmentDate) {
        StudySubjectBean studySubject = new StudySubjectBean();
        studySubject.setOwner(getUserAccount());
        studySubject.setLabel(subject.getLabel());
        subject.setLabel(null);
        studySubject.setSubjectId(subject.getId());
        studySubject.setStudyId(studyBean.getId());
        studySubject.setEnrollmentDate(enrollmentDate);
        studySubject.setStatus(Status.AVAILABLE);
        return studySubject;

    }

    public void validateSubjectTransfer(SubjectTransferBean subjectTransferBean) {
        // TODO: Validate here
    }

    /**
     * Getting the first user account from the database. This would be replaced by an authenticated user who is doing the SOAP requests .
     * 
     * @return UserAccountBean
     */
    private UserAccountBean getUserAccount() {

        UserAccountBean user = new UserAccountBean();
        user.setId(1);
        return user;
    }

    /**
     * @return the subjectDao
     */
    public SubjectDAO getSubjectDao() {
        subjectDao = subjectDao != null ? subjectDao : new SubjectDAO(dataSource);
        return subjectDao;
    }

    /**
     * @return the subjectDao
     */
    public StudyDAO getStudyDao() {
        studyDao = studyDao != null ? studyDao : new StudyDAO(dataSource);
        return studyDao;
    }

    /**
     * @return the subjectDao
     */
    public StudySubjectDAO getStudySubjectDao() {
        studySubjectDao = studySubjectDao != null ? studySubjectDao : new StudySubjectDAO(dataSource);
        return studySubjectDao;
    }

    /**
     * @return the UserAccountDao
     */
    public UserAccountDAO getUserAccountDao() {
        userAccountDao = userAccountDao != null ? userAccountDao : new UserAccountDAO(dataSource);
        return userAccountDao;
    }

    /**
     * @return the datasource
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * @param datasource
     *            the datasource to set
     */
    public void setDatasource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

}
