/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.submit;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.DAOTestBase;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Properties;

/**
 * @author jxu
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SubjectGroupMapDAOTest extends DAOTestBase {
    private SubjectGroupMapDAO sdao;

    private DAODigester digester = new DAODigester();

    public SubjectGroupMapDAOTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(SubjectGroupMapDAOTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        digester.setInputStream(new FileInputStream(SQL_DIR + "subject_group_map_dao.xml"));

        try {
            digester.run();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        }

        super.ds = setupTestDataSource();
        Locale locale = ResourceBundleProvider.getLocale();
        // super.ds = setupOraDataSource();
        // super.ds = setupDataSource();
        sdao = new SubjectGroupMapDAO(super.ds, digester, locale);
    }

    @Override
    public void testFindAll() throws Exception {
        Collection col = sdao.findAll();
        assertNotNull("findAll", col);
    }

    public void testCreate() throws Exception {
        SubjectGroupMapBean sb = new SubjectGroupMapBean();
        Properties properties = DAOTestBase.getTestProperties();
        int subjectId = Integer.parseInt(properties.getProperty("subjectId"));
        sb.setStudyGroupClassId(1);
        sb.setStudySubjectId(subjectId);
        sb.setStudyGroupId(1);
        sb.setStatus(Status.AVAILABLE);
        UserAccountBean ub = new UserAccountBean();
        ub.setId(1);
        sb.setOwner(ub);

        SubjectGroupMapBean sb2 = (SubjectGroupMapBean) sdao.create(sb);
        assertNotNull("simple create test", sb2);
        sdao.deleteTestGroupMap(sb2.getId());
    }

    // commented out because there are no rows in subject_group_map in test2
    // ssachs, 10 dec 04
    // public void testFindByPK() throws Exception {
    // SubjectGroupMapBean sb = (SubjectGroupMapBean) sdao.findByPK(3);
    // assertNotNull("findbypk", sb);
    // System.out.println("the Group Id"+ sb.getStudySubjectId()+
    // sb.getStudyGroupId());
    // assertEquals("check the Group Id", 1, sb.getStudyGroupId());
    // }

    public void testUpdate() throws Exception {
        SubjectGroupMapBean sb = new SubjectGroupMapBean();
        Properties properties = DAOTestBase.getTestProperties();
        int subjectId = Integer.parseInt(properties.getProperty("subjectId"));
        sb.setStudyGroupClassId(1);
        sb.setStudySubjectId(subjectId);
        sb.setStudyGroupId(1);
        sb.setStatus(Status.AVAILABLE);
        UserAccountBean ub = new UserAccountBean();
        ub.setId(1);
        sb.setOwner(ub);

        SubjectGroupMapBean sb2 = (SubjectGroupMapBean) sdao.create(sb);

        sb2.setStatus(Status.AVAILABLE);
        sb2.setUpdater(ub);

        SubjectGroupMapBean sb3 = (SubjectGroupMapBean) sdao.update(sb2);
        assertNotNull("simple update test", sb3);
        sdao.deleteTestGroupMap(sb2.getId());
    }

    public void testFindAllByStudySubject() throws Exception {
        Properties properties = DAOTestBase.getTestProperties();
        int subjectId = Integer.parseInt(properties.getProperty("subjectId"));
        ArrayList groups = (ArrayList) sdao.findAllByStudySubject(subjectId);
        assertNotNull("the groups are not null.", groups);
        assertTrue("FindAllByStudySubject test", groups.size() > 0);

    }

    public void testFindAllByStudyGroupClassAndGroup() throws Exception {
        Properties properties = DAOTestBase.getTestProperties();
        int studyGroupId = Integer.parseInt(properties.getProperty("studyGroupId"));
        int studyGroupClassId = Integer.parseInt(properties.getProperty("studyGroupClassId"));

        ArrayList groups = sdao.findAllByStudyGroupClassAndGroup(studyGroupClassId, studyGroupId);
        assertTrue("FindAllByStudyGroupClassAndGroup test", groups.size() > 0);

    }

}
