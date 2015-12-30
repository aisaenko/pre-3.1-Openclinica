package org.akaza.openclinica.service.rule.expression;

import org.akaza.openclinica.OcDbTestCase;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;

public class SampleTest extends OcDbTestCase {

    public SampleTest() {
        super();
    }

    public void testStatement() {
        StudyDAO studyDao = new StudyDAO(getDataSource());
        StudyBean study = (StudyBean) studyDao.findByPK(1);
        assertNotNull(study);
    }
}