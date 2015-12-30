/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.managestudy;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.DAOTestBase;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;

/**
 * @author ssachs
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class StudyEventDefinitionDAOTest extends DAOTestBase {
    private StudyEventDefinitionDAO sdao;

    private DAODigester digester = new DAODigester();

    public StudyEventDefinitionDAOTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(StudySubjectDAOTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        digester.setInputStream(new FileInputStream(SQL_DIR + "studyeventdefinition_dao.xml"));

        try {
            digester.run();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        }

        super.ds = setupTestDataSource();
        Locale locale = ResourceBundleProvider.getLocale();
        // super.ds = setupOraDataSource();
        // super.ds = setupDataSource();
        sdao = new StudyEventDefinitionDAO(super.ds, digester, locale);
    }

    public void testFindAllByStudySuccess() {
        StudyBean studyBean = new StudyBean();
        Properties properties = DAOTestBase.getTestProperties();
        int studyId = Integer.parseInt(properties.getProperty("studyId"));
        studyBean.setId(studyId);

        ArrayList rows = sdao.findAllByStudy(studyBean);

        assertNotNull("The ArrayList is not null", rows);
        assertTrue("There is at least one row", rows.size() > 0);

        StudyEventDefinitionBean seb = (StudyEventDefinitionBean) rows.get(0);
        assertNotNull("The StudyEventDefinitionBean is not null.", seb);
        assertTrue("The StudyEventDefinitionBean is active", seb.isActive());
    }

    public void testFindAllByStudyFailure() {
        // BWP>> this method was unchanged
        StudyBean sb = new StudyBean();
        sb.setId(-1);

        ArrayList rows = sdao.findAllByStudy(sb);

        assertNotNull("rows valid", rows);
        assertEquals("no rows", rows.size(), 0);
    }

    public void testFindByPKAndStudy() {
        Properties properties = DAOTestBase.getTestProperties();
        int pk = Integer.parseInt(properties.getProperty("studyEventDefId"));
        int studyId = Integer.parseInt(properties.getProperty("studyId"));
        StudyBean sb = new StudyBean();
        sb.setId(studyId);

        StudyEventDefinitionBean seb = (StudyEventDefinitionBean) sdao.findByPKAndStudy(pk, sb);

        assertNotNull("The beans are not null.", seb);
        assertTrue("The returned bean is active", seb.isActive());
        assertEquals("The returned bean is the correct one.", seb.getId(), pk);
    }

    public void testFindAllWithStudyEventSuccess() {
        Properties properties = DAOTestBase.getTestProperties();
        int studyId = Integer.parseInt(properties.getProperty("studyId"));
        StudyBean sb = new StudyBean();
        sb.setId(studyId);

        ArrayList rows = sdao.findAllWithStudyEvent(sb);
        StudyEventDefinitionBean seb = (StudyEventDefinitionBean) rows.get(0);

        assertNotNull("rows valid", rows);
        assertTrue("at least one row", rows.size() > 0);
        assertNotNull("well-formed beans", seb);
        assertTrue("bean id set", seb.isActive());
        assertEquals("bean from correct study", seb.getStudyId(), studyId);
    }

    public void testFindAllWithStudyEventFailure() {
        // BWP>> this method was unchanged
        StudyBean sb = new StudyBean();
        sb.setId(-1);

        ArrayList rows = sdao.findAllWithStudyEvent(sb);

        assertNotNull("rows valid", rows);
        assertEquals("no row", rows.size(), 0);
    }

    @Override
    public void testFindAll() {
        // BWP>> this method was unchanged
        ArrayList rows = (ArrayList) sdao.findAll();
        StudyEventDefinitionBean seb = (StudyEventDefinitionBean) rows.get(0);

        assertNotNull("rows valid", rows);
        assertTrue("at least one row", rows.size() > 0);
        assertNotNull("well-formed beans", seb);
        assertTrue("bean id set", seb.isActive());
    }

    public void testFindByPKSuccess() {
        Properties properties = DAOTestBase.getTestProperties();
        int pk = Integer.parseInt(properties.getProperty("studyEventDefId"));
        StudyEventDefinitionBean seb = (StudyEventDefinitionBean) sdao.findByPK(pk);

        assertNotNull("well-formed beans", seb);
        assertTrue("bean id set", seb.isActive());
        assertEquals("got right bean", seb.getId(), pk);
    }

    public void testFindByPKFailure() {
        // BWP>> this method was unchanged

        StudyEventDefinitionBean seb = (StudyEventDefinitionBean) sdao.findByPK(-1);

        assertNotNull("well-formed beans", seb);
        assertTrue("bean id not set", !seb.isActive());
    }

    /*
     * These methods shouldn't be run unless test or fake studies and events are
     * set up in the database. public void testCreate() throws Exception {
     * Properties properties = DAOTestBase.getTestProperties(); int studyId =
     * Integer.parseInt(properties.getProperty("studyId"));
     * StudyEventDefinitionBean sb = new StudyEventDefinitionBean();
     *
     * sb.setCategory("test category"); sb.setDescription("test description");
     * sb.setName("test definition"); sb.setRepeating(true);
     * sb.setStudyId(studyId); sb.setType("common");
     *
     * sb.setStatus(Status.AVAILABLE); UserAccountBean ub = new
     * UserAccountBean(); ub.setId(1); sb.setOwner(ub);
     *
     * StudyEventDefinitionBean sb2 = (StudyEventDefinitionBean)
     * sdao.create(sb); assertNotNull("simple create test", sb2);
     * assertTrue("got id back", sb2.isActive());
     *  }
     */

    /*
     * public void testUpdate() throws Exception { StudyEventDefinitionBean sb =
     * new StudyEventDefinitionBean(); Properties properties =
     * DAOTestBase.getTestProperties(); int pk =
     * Integer.parseInt(properties.getProperty("studyEventDefId")); sb =
     * (StudyEventDefinitionBean) sdao.findByPK(pk); //Don't change an existing
     * description String descrip = sb.getDescription(); sb.setDescription("test
     * description 2"); sb.setStatus(Status.AVAILABLE); UserAccountBean ub = new
     * UserAccountBean(); ub.setId(1); sb.setUpdater(ub);
     *
     * StudyEventDefinitionBean sb2 = (StudyEventDefinitionBean)
     * sdao.update(sb); assertNotNull("simple update test", sb2); }
     */

    public void testFindByEventDefinitionCRFIdSuccess() throws Exception {
        Properties properties = DAOTestBase.getTestProperties();
        int eventDefCRFId = Integer.parseInt(properties.getProperty("eventDefCRFId"));
        int studyEventDefId = Integer.parseInt(properties.getProperty("studyEventDefId"));
        StudyEventDefinitionBean seb = sdao.findByEventDefinitionCRFId(eventDefCRFId);
        assertNotNull("well-formed beans", seb);
        assertTrue("bean id set", seb.isActive());
        assertEquals("got right bean", seb.getId(), studyEventDefId);
    }

    public void testFindByEventDefinitionCRFIdFailure() throws Exception {
        StudyEventDefinitionBean seb = sdao.findByEventDefinitionCRFId(-1);
        assertNotNull("well-formed beans", seb);
        assertTrue("bean id not set", !seb.isActive());
    }
}
