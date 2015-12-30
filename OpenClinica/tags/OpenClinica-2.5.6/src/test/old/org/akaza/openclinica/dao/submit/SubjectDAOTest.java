/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.submit;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.DAOTestBase;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

/**
 * @author jxu
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SubjectDAOTest extends DAOTestBase {
    private SubjectDAO sdao;

    private DAODigester digester = new DAODigester();

    public SubjectDAOTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(SubjectDAOTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        digester.setInputStream(new FileInputStream(SQL_DIR + "subject_dao.xml"));

        try {
            digester.run();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        }

        super.ds = setupTestDataSource();
        Locale locale = ResourceBundleProvider.getLocale();

        // super.ds = setupOraDataSource();
        // super.ds = setupDataSource();
        sdao = new SubjectDAO(super.ds, digester, locale);
    }

    @Override
    public void testFindAll() throws Exception {
        Collection col = sdao.findAll();
        assertNotNull("findAll", col);
    }

    public void testFindByPKSuccess() throws Exception {
        Properties properties = DAOTestBase.getTestProperties();
        int subjectId = Integer.parseInt(properties.getProperty("subjectId"));
        SubjectBean sb = (SubjectBean) sdao.findByPK(subjectId);
        assertNotNull("findbypk", sb);
        assertTrue("bean valid", sb.isActive());
        assertEquals("correct bean", sb.getId(), subjectId);
    }

    public void testFindByPKFailure() throws Exception {
        SubjectBean sb = (SubjectBean) sdao.findByPK(-1);
        assertNotNull("findbypk", sb);
        assertTrue("bean not valid", !sb.isActive());
    }

    public void testCreate() throws Exception {
        SubjectBean sb = new SubjectBean();
        sb.setFatherId(1);
        sb.setMotherId(3);
        sb.setDateOfBirth(new Date());
        sb.setGender('m');
        sb.setUniqueIdentifier("TestSubject1");

        sb.setStatus(Status.AVAILABLE);
        UserAccountBean ub = new UserAccountBean();
        ub.setId(1);
        sb.setOwner(ub);

        SubjectBean sb2 = sdao.create(sb);
        assertNotNull("simple create test", sb2);
        sdao.deleteTestSubject("TestSubject1");
    }

    public void testCreateWithParents() throws Exception {
        SubjectBean sb = new SubjectBean();
        sb.setFatherId(1);
        sb.setMotherId(3);
        sb.setDateOfBirth(new Date());
        sb.setGender('m');
        sb.setUniqueIdentifier("subjectWithParents");

        sb.setStatus(Status.AVAILABLE);
        UserAccountBean ub = new UserAccountBean();
        ub.setId(1);
        sb.setOwner(ub);

        SubjectBean sb2 = sdao.createWithParents(sb);
        assertNotNull("simple create test", sb2);
        assertTrue("insert successful", sb2.isActive());
        sdao.deleteTestSubject("subjectWithParents");
    }

    public void testCreateWithoutParents() throws Exception {
        SubjectBean sb = new SubjectBean();
        sb.setDateOfBirth(new Date());
        sb.setGender('m');
        sb.setUniqueIdentifier("subjectWithoutParents");

        sb.setStatus(Status.AVAILABLE);
        UserAccountBean ub = new UserAccountBean();
        ub.setId(1);
        sb.setOwner(ub);

        SubjectBean sb2 = sdao.createWithoutParents(sb);
        assertNotNull("simple create test", sb2);
        assertTrue("insert successful", sb2.isActive());
        sdao.deleteTestSubject("subjectWithoutParents");
    }

    /*
     * Don't update existing subjects unless a 'fake' subject has been added to
     * the DB.
     *
     * public void testUpdate() throws Exception { SubjectBean sb = new
     * SubjectBean(); sb = (SubjectBean) sdao.findByPK(4);
     * sb.setUniqueIdentifier("subject-1"); sb.setStatus(Status.AVAILABLE);
     *
     * UserAccountBean ub = new UserAccountBean(); ub.setId(1);
     * sb.setUpdater(ub);
     *
     * SubjectBean sb2 = (SubjectBean) sdao.update(sb); assertNotNull("simple
     * update test", sb2); }
     */

    public void testFindAllMales() {
        ArrayList males = sdao.findAllMales();
        assertNotNull("result well formed", males);
        assertTrue("at least one male", males.size() > 0);

        SubjectBean ssb = (SubjectBean) males.get(0);
        assertNotNull("bean well formed", ssb);
        assertTrue("bean id set", ssb.isActive());
        assertEquals("correct gender chosen", ssb.getGender(), 'm');
    }

    public void testFindAllFemales() {
        ArrayList females = sdao.findAllFemales();
        assertNotNull("result well formed", females);
        assertTrue("at least one female", females.size() > 0);

        SubjectBean ssb = (SubjectBean) females.get(0);
        assertNotNull("bean well formed", ssb);
        assertTrue("bean id set", ssb.isActive());
        assertEquals("correct gender chosen", ssb.getGender(), 'f');
    }

    /*
     * public void testFindAllChildrenByPK() { int subjectId = 1; ArrayList
     * children = (ArrayList)sdao.findAllChildrenByPK(subjectId);
     * assertTrue("children size", children.size()>0);
     *  }
     */

    // public void testGetCurrentPrimaryKey() throws Exception {
    // int pk = sdao.getCurrentPK();
    //
    // assertTrue("pk valid", pk > 0);
    //
    // testCreate();
    // int pk2 = sdao.getCurrentPK();
    // assertEquals("pk sequence correct", pk + 1, pk2);
    // }
    public void testFindByUniqueIdentifierSuccess() throws Exception {
        Properties properties = DAOTestBase.getTestProperties();
        String uniqueIdentifier = properties.getProperty("uniqueIdentifier");
        int subjectId = Integer.parseInt(properties.getProperty("subjectId"));
        SubjectBean sb = sdao.findByUniqueIdentifier(uniqueIdentifier);
        assertNotNull("bean well formed", sb);
        assertTrue("bean id set", sb.isActive());
        assertEquals("correct bean chosen", sb.getId(), subjectId);
    }

    public void testFindByUniqueIdentifierFailure() throws Exception {
        SubjectBean sb = sdao.findByUniqueIdentifier("invalid identifier");
        assertNotNull("bean well formed", sb);
        assertTrue("bean id not set", !sb.isActive());
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
}