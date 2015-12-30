/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.managestudy;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyType;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.DAOTestBase;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

/**
 * @author thickerson
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class StudyDAOTest extends DAOTestBase {

    private StudyDAO sdao;
    private DAODigester digester = new DAODigester();

    public StudyDAOTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(StudyDAOTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        digester.setInputStream(new FileInputStream(SQL_DIR + "study_dao.xml"));
        // digester.setInputStream(new FileInputStream(SQL_DIR +
        // "oracle_study_dao.xml"));
        try {
            digester.run();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        }

        super.ds = setupTestDataSource();
        Locale locale = ResourceBundleProvider.getLocale();

        // super.ds = setupOraDataSource();
        // super.ds = setupDataSource();
        sdao = new StudyDAO(super.ds, digester, locale);
    }

    @Override
    public void testFindAll() throws Exception {
        Collection col = sdao.findAll();
        assertNotNull("findAll", col);
    }

    public void testFindByPKSuccess() throws Exception {
        Properties properties = DAOTestBase.getTestProperties();
        int studyId = Integer.parseInt(properties.getProperty("studyId"));
        StudyBean sb = (StudyBean) sdao.findByPK(studyId);
        assertNotNull("findbypk", sb);
        assertTrue("The id is set", sb.isActive());
        assertEquals(sb.getId(), studyId);
        // assertEquals("check the name","Diabetes II",sb.getName());
    }

    public void testFindByPKFailure() throws Exception {
        StudyBean sb = (StudyBean) sdao.findByPK(-1);
        assertNotNull("findbypk", sb);
        assertTrue("id not set", !sb.isActive());
    }

    /*
     * public void testFindNextKey() throws Exception { int key =
     * sdao.findNextKey(); System.out.print("found an int: "+key);
     * assertEquals("find next key",0,0); }
     */
    public void testCreate() throws Exception {
        StudyBean sb = new StudyBean();
        sb.setId(sdao.findNextKey());
        sb.setName("Unit Test Top Study");
        sb.setIdentifier("Unit Test Study 001");
        sb.setParentStudyId(0);
        sb.setPrincipalInvestigator("Joe Sumbuddy");
        sb.setSummary("summary goes here");
        // sb.setDatePlannedStart(new Date());
        sb.setDatePlannedEnd(new Date());
        sb.setProtocolDateVerification(new Date());
        sb.setFacilityName("Fname");
        sb.setFacilityCity("Fcity");
        sb.setFacilityState("");
        sb.setFacilityZip("");
        sb.setFacilityCountry("Fcountry");
        sb.setFacilityRecruitmentStatus("");
        sb.setFacilityContactName("");
        sb.setFacilityContactEmail("");
        sb.setFacilityContactPhone("");
        sb.setFacilityContactDegree("");
        sb.setStatus(Status.AVAILABLE);

        // StudyType t = StudyType.get(StudyType.GENETIC.getId());
        sb.setType(StudyType.get(StudyType.GENETIC.getId()));

        sb.setProtocolType("interventional");
        sb.setOwnerId(1);
        sb.setGender("male");// illegal argument exception if not set?

        // need to rename column 'facility_contact_degree' in table, tbh

        StudyBean sb2 = (StudyBean) sdao.create(sb);
        assertNotNull("simple create test", sb2);
        sdao.deleteTestOnly("Unit Test Top Study");
    }

    public void testUpdateSuccess() throws Exception {
        StudyBean sb = new StudyBean();
        sb = (StudyBean) sdao.findByPK(1);
        sb.setName("TEST01");
        sb.setIdentifier("Unit Test Study 001");
        sb.setPrincipalInvestigator("Joe Sumbuddy");
        sb.setSummary("objective goes here");
        sb.setDatePlannedStart(new Date());
        sb.setDatePlannedEnd(new Date());
        sb.setProtocolDateVerification(new Date());
        sb.setFacilityName("Fname");
        sb.setFacilityCity("Fcity");
        sb.setStatus(Status.AVAILABLE);
        sb.setUpdaterId(1);
        sb.setCreatedDate(new Date());
        sb.setUpdatedDate(new Date());

        StudyBean sb2 = (StudyBean) sdao.update(sb);
        assertNotNull("simple create test", sb2);
    }

    public void testUpdateFail() throws Exception {
        StudyBean sb = new StudyBean();
        sb = (StudyBean) sdao.findByPK(9999);
        sb.setName("TEST01");
        sb.setIdentifier("Unit Test Study 001");
        sb.setPrincipalInvestigator("Joe Sumbuddy");
        sb.setSummary("objective goes here");
        sb.setDatePlannedStart(new Date());
        sb.setDatePlannedEnd(new Date());
        sb.setProtocolDateVerification(new Date());
        sb.setType(StudyType.GENETIC);
        sb.setFacilityName("Fname");
        sb.setFacilityCity("Fcity");
        sb.setStatus(Status.AVAILABLE);
        sb.setUpdaterId(1);
        sb.setCreatedDate(new Date());
        sb.setUpdatedDate(new Date());

        StudyBean sb2 = (StudyBean) sdao.update(sb);
        assertNotNull("simple create test", sb2);
        assertEquals("test primary key", sb2.getId(), 0);
    }
}
