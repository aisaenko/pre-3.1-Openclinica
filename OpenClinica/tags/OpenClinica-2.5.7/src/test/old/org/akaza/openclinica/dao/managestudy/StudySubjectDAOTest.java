/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.managestudy;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.DAOTestBase;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * @author jxu
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class StudySubjectDAOTest extends DAOTestBase {
    private StudySubjectDAO sdao;
    private StudyDAO studyDao;

    private DAODigester digester = new DAODigester();
    private DAODigester studyDigester = new DAODigester();

    public StudySubjectDAOTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(StudySubjectDAOTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        digester.setInputStream(new FileInputStream(SQL_DIR + "study_subject_dao.xml"));
        studyDigester.setInputStream(new FileInputStream(SQL_DIR + "study_dao.xml"));

        try {
            digester.run();
            studyDigester.run();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        }

        super.ds = setupTestDataSource();
        // super.ds = setupOraDataSource();
        // super.ds = setupDataSource();
        sdao = new StudySubjectDAO(super.ds, digester);
        studyDao = new StudyDAO(super.ds, studyDigester);
    }

    @Override
    public void testFindAll() throws Exception {
        Collection col = sdao.findAll();
        assertNotNull("findAll", col);
    }

    public void testCreate() throws Exception {
        StudySubjectBean sb = new StudySubjectBean();
        sb.setName("Unit Test Study Subject");
        sb.setLabel("this is label");
        sb.setStudyId(1);
        // sb.setStudyGroupId(9);
        sb.setSubjectId(1);
        sb.setStatus(Status.AVAILABLE);
        UserAccountBean ub = new UserAccountBean();
        ub.setId(1);
        sb.setOwner(ub);

        StudySubjectBean sb2 = (StudySubjectBean) sdao.create(sb);
        assertNotNull("simple create test", sb2);
    }

    public void testCreateWithGroup() throws Exception {
        StudySubjectBean sb = new StudySubjectBean();
        sb.setName("Unit Test Study Subject");
        sb.setLabel("studySubjectWithGroup");
        sb.setStudyId(1);
        // sb.setStudyGroupId(9);
        sb.setSubjectId(1);
        sb.setStatus(Status.AVAILABLE);
        sb.setEnrollmentDate(new Date(0));
        UserAccountBean ub = new UserAccountBean();
        ub.setId(1);
        sb.setOwner(ub);

        StudySubjectBean sb2 = sdao.createWithGroup(sb);
        assertNotNull("simple create test", sb2);
    }

    public void testCreateWithoutGroup() throws Exception {
        StudySubjectBean sb = new StudySubjectBean();
        sb.setName("Unit Test Study Subject");
        sb.setLabel("studySubjectWithoutGroup");
        sb.setStudyId(1);
        sb.setSubjectId(1);
        sb.setStatus(Status.AVAILABLE);
        sb.setEnrollmentDate(new Date(0));
        UserAccountBean ub = new UserAccountBean();
        ub.setId(1);
        sb.setOwner(ub);

        StudySubjectBean sb2 = sdao.createWithoutGroup(sb);
        assertNotNull("simple create test", sb2);
    }

    public void testFindByPKSuccess() throws Exception {
        int pk = 14;
        StudySubjectBean sb = (StudySubjectBean) sdao.findByPK(pk);
        assertNotNull("findbypk", sb);
        assertTrue("id set", sb.isActive());
        assertEquals("correct bean", sb.getId(), pk);
    }

    public void testFindByPKFailure() throws Exception {
        int pk = -1;
        StudySubjectBean sb = (StudySubjectBean) sdao.findByPK(pk);
        assertNotNull("findbypk", sb);
        assertTrue("id not set", !sb.isActive());
    }

    public void testUpdate() throws Exception {
        StudySubjectBean sb = new StudySubjectBean();
        sb = (StudySubjectBean) sdao.findByPK(1);
        sb.setLabel("this is label updated");
        sb.setStatus(Status.AVAILABLE);
        UserAccountBean ub = new UserAccountBean();
        ub.setId(1);
        sb.setUpdater(ub);

        StudySubjectBean sb2 = (StudySubjectBean) sdao.update(sb);
        assertNotNull("simple create test", sb2);
    }

    public void testFindBySubjectIdAndStudySuccess() throws Exception {
        int id = 1;
        StudyBean sb = new StudyBean();
        sb.setId(5);

        StudySubjectBean ssb = sdao.findBySubjectIdAndStudy(id, sb);

        assertNotNull("bean valid", ssb);
        assertTrue("bean id set", ssb.isActive());
        assertEquals("correct bean found", ssb.getId(), 21);
    }

    public void testFindBySubjectIdAndStudyFailure() throws Exception {
        int id = -1;
        StudyBean sb = new StudyBean();
        sb.setId(1);

        StudySubjectBean ssb = sdao.findBySubjectIdAndStudy(id, sb);

        assertNotNull("bean valid", ssb);
        assertTrue("bean id not set", !ssb.isActive());
    }

    public void testFindByLabelAndStudySuccess() throws Exception {
        String label = "study1subject2";
        StudyBean sb = new StudyBean();
        sb.setId(1);

        StudySubjectBean ssb = sdao.findByLabelAndStudy(label, sb);

        assertNotNull("bean valid", ssb);
        assertTrue("bean id set", ssb.isActive());
        assertEquals("correct bean found", ssb.getId(), 2);
    }

    public void testFindByLabelAndStudyFailure() throws Exception {
        String label = "invalid label";
        StudyBean sb = new StudyBean();
        sb.setId(1);

        StudySubjectBean ssb = sdao.findByLabelAndStudy(label, sb);

        assertNotNull("bean valid", ssb);
        assertTrue("bean id not set", !ssb.isActive());
    }

    public void testFindAllWithStudyEventSuccess() throws Exception {
        // StudyDAO studyDAO = new StudyDAO(this.ds);
        StudyBean sb = new StudyBean();
        // sb.setId(1);
        ArrayList col = (ArrayList) studyDao.findAll();
        sb = (StudyBean) col.get(0);
        ArrayList rows = sdao.findAllWithStudyEvent(sb);
        StudySubjectBean ssb = rows.size() > 0 ? (StudySubjectBean) rows.get(0) : null;
        // StudySubjectBean ssb = (StudySubjectBean) rows.get(0);

        assertNotNull("rows valid", rows);
        assertTrue("at least one row", rows.size() > 0);
        assertNotNull("well-formed beans", ssb);
        assertTrue("bean id set", ssb.isActive());
    }

    public void testFindAllWithStudyEventFailure() throws Exception {
        StudyBean sb = new StudyBean();
        sb.setId(-1);

        ArrayList rows = sdao.findAllWithStudyEvent(sb);

        assertNotNull("rows valid", rows);
        assertTrue("no rows", rows.size() == 0);
    }

    public void testFindBySubjectIdAndStudy() throws Exception {
        int subjectId = 384;
        StudyBean study = new StudyBean();
        study.setId(102);

        StudySubjectBean ssub = sdao.findBySubjectIdAndStudy(subjectId, study);
        // assertTrue("study subject bean id valid", ssub.getId()> 0);

    }

    /*
     * The query does not order by label - KK 10/31/2006 public void
     * testFindAllByStudyOrderByLabelSuccess() throws Exception { StudyBean sb =
     * new StudyBean(); sb.setId(5); ArrayList answer = sdao.findAllByStudy(sb);
     *
     * assertNotNull("rows valid", answer); assertTrue("correct num rows",
     * answer.size() >= 5);
     *
     * StudySubjectBean ssb = (StudySubjectBean) answer.get(0);
     * assertNotNull("bean wellformed", ssb); assertTrue("bean valid",
     * ssb.isActive());
     *
     * String firstLabel = ssb.getLabel();
     *
     * StudySubjectBean ssb2 = (StudySubjectBean) answer.get(1); String
     * secondLabel = ssb2.getLabel();
     *
     * assertTrue("ordered properly", firstLabel.compareTo(secondLabel) <= 0); }
     */

    public void testFindAllByStudyOrderByLabelFailure() throws Exception {
        StudyBean sb = new StudyBean();
        sb.setId(-1);
        ArrayList answer = sdao.findAllByStudy(sb);

        assertNotNull("rows valid", answer);
        assertEquals("correct num rows", answer.size(), 0);
    }

    public void testFindAllByStudySuccess() throws Exception {
        StudyBean sb = new StudyBean();
        sb.setId(5);
        ArrayList answer = sdao.findAllByStudy(sb);

        assertNotNull("rows valid", answer);
        assertTrue("correct num rows", answer.size() >= 5);

        StudySubjectBean ssb = (StudySubjectBean) answer.get(0);
        assertNotNull("bean wellformed", ssb);
        assertTrue("bean valid", ssb.isActive());
    }

    public void testFindAllByStudyFailure() throws Exception {
        StudyBean sb = new StudyBean();
        sb.setId(-1);
        ArrayList answer = sdao.findAllByStudy(sb);

        assertNotNull("rows valid", answer);
        assertEquals("correct num rows", answer.size(), 0);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.akaza.openclinica.dao.core.DAOTestBase#shutDown()
     */
    @Override
    protected void shutDown() throws Exception {
        // TODO Auto-generated method stub
        super.shutDown();
    }

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        shutdownDataSource(ds);
        super.tearDown();
    }

    public void testFindAllByStudyIdAndLimit() throws Exception {
        int studyId = 102;
        ArrayList al = sdao.findAllByStudyIdAndLimit(102, false);

    }
}
