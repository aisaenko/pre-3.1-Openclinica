/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.submit;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.DAOTestBase;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

/**
 * @author thickerson
 *
 *
 */
public class SectionDAOTest extends DAOTestBase {
    private SectionDAO secdao;
    private DAODigester digester = new DAODigester();

    public SectionDAOTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(SectionDAOTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        digester.setInputStream(new FileInputStream(SQL_DIR + "section_dao.xml"));
        try {
            digester.run();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        }

        super.ds = setupTestDataSource();
        Locale locale = ResourceBundleProvider.getLocale();

        secdao = new SectionDAO(super.ds, digester, locale);
    }

    @Override
    public void testFindAll() throws Exception {
        Collection col = secdao.findAll();
        assertNotNull("findAll", col);
    }

    public void testCreate() throws Exception {
        Properties properties = DAOTestBase.getTestProperties();
        int crfversionId = Integer.parseInt(properties.getProperty("crfversionId"));
        int ownerId = Integer.parseInt(properties.getProperty("ownerId"));
        SectionBean sb = new SectionBean();
        sb.setCRFVersionId(crfversionId);
        sb.setStatus(Status.AVAILABLE);
        sb.setLabel("Deleteme");
        sb.setTitle("Delete Me, Please");
        sb.setInstructions("test section to fill space only, please delete");
        sb.setSubtitle("delete");
        sb.setPageNumberLabel("A.1");
        sb.setOrdinal(1);
        sb.setParentId(0);
        sb.setOwnerId(ownerId);
        sb = (SectionBean) secdao.create(sb);
        assertNotNull("test insert", sb);

        secdao.deleteTestSection("Deleteme");
    }

    /*
     * Don't update any existing sections:
     *
     * public void testUpdate() throws Exception { Collection col =
     * secdao.findAll(); Iterator it = col.iterator(); int pkey = 0; if
     * (it.hasNext()) { SectionBean testMe = (SectionBean)it.next(); pkey =
     * testMe.getId(); } SectionBean ecb = (SectionBean)secdao.findByPK(pkey);
     * System.out.println("Found by primary key "+pkey+" desc: "+
     * ecb.getInstructions());
     *
     * String keepDesc = ecb.getInstructions(); ecb.setInstructions("Test Desc
     * Deleteme"); ecb.setUpdaterId(1); ecb = (SectionBean)secdao.update(ecb);
     *
     * ecb.setInstructions(keepDesc); SectionBean ecb2 =
     * (SectionBean)secdao.update(ecb); assertNotNull("test update", ecb2); }
     */

    public void testFindAllByCRFVersionIdSuccess() {
        Properties properties = DAOTestBase.getTestProperties();
        int crfversionId = Integer.parseInt(properties.getProperty("crfversionId"));
        ArrayList answer = secdao.findAllByCRFVersionId(crfversionId);

        assertNotNull("wellformed answer", answer);
        // Dependent on db configuration :assertEquals("correct number of rows",
        // answer.size(), 11);

        SectionBean sb = (SectionBean) answer.get(0);
        assertNotNull("wellformed bean", sb);
        assertTrue("valid bean", sb.isActive());
        assertEquals("correct crf version", sb.getCRFVersionId(), crfversionId);
    }

    public void testFindAllByCRFVersionIdFailure() {
        ArrayList answer = secdao.findAllByCRFVersionId(-1);

        assertNotNull("wellformed answer", answer);
        assertEquals("correct number of rows", answer.size(), 0);
    }

    public void testFindByPKSuccess() {
        Properties properties = DAOTestBase.getTestProperties();
        int sectionId = Integer.parseInt(properties.getProperty("sectionId"));
        SectionBean sb = (SectionBean) secdao.findByPK(sectionId);
        assertNotNull("wellformed bean", sb);
        assertTrue("valid bean", sb.isActive());
        assertEquals("correct bean", sb.getId(), sectionId);
    }

    public void testFindByPKFailure() {
        SectionBean sb = (SectionBean) secdao.findByPK(-1);
        assertNotNull("wellformed bean", sb);
        assertTrue("invalid bean", !sb.isActive());
    }

    /*
     * These tests are based on a specific database and section configuration,
     * which can change, thus failing the test. public void
     * testFindNextSuccess() { EventCRFBean ecb = new EventCRFBean();
     * SectionBean current = new SectionBean();
     *
     * ecb.setId(4); ecb.setCRFVersionId(101); current.setId(201);
     * current.setOrdinal(1);
     *
     * SectionBean sb = (SectionBean) secdao.findNext(ecb, current);
     * assertNotNull("wellformed bean", sb); assertTrue("valid bean",
     * sb.isActive()); assertEquals("correct bean", sb.getId(), 202); }
     *
     * public void testFindNextFailureWrongECBId() { EventCRFBean ecb = new
     * EventCRFBean(); SectionBean current = new SectionBean();
     *
     * ecb.setId(-1); ecb.setCRFVersionId(-1); current.setId(201);
     * current.setOrdinal(1);
     *
     * SectionBean sb = (SectionBean) secdao.findNext(ecb, current);
     * assertNotNull("wellformed bean", sb); assertTrue("invalid bean",
     * !sb.isActive()); }
     *
     * public void testFindNextFailureWrongSectionId() { Properties properties =
     * DAOTestBase.getTestProperties(); int crfversionId =
     * Integer.parseInt(properties.getProperty("crfversionId")); int eventCRFId =
     * Integer.parseInt(properties.getProperty("eventCRFId")); EventCRFBean ecb =
     * new EventCRFBean(); SectionBean current = new SectionBean();
     *
     * ecb.setId(eventCRFId); ecb.setCRFVersionId(crfversionId);
     * current.setId(-1); current.setOrdinal(-1);
     *
     * SectionBean sb = (SectionBean) secdao.findNext(ecb, current);
     * assertNotNull("wellformed bean", sb); assertTrue("invalid bean",
     * !sb.isActive()); }
     *
     * public void testFindNextFailureNoNextSection() { Properties properties =
     * DAOTestBase.getTestProperties(); int crfversionId =
     * Integer.parseInt(properties.getProperty("crfversionId")); int eventCRFId =
     * Integer.parseInt(properties.getProperty("eventCRFId")); EventCRFBean ecb =
     * new EventCRFBean(); SectionBean current = new SectionBean();
     *
     * ecb.setId(eventCRFId); ecb.setCRFVersionId(crfversionId);
     * current.setId(211); current.setOrdinal(11);
     *
     * SectionBean sb = (SectionBean) secdao.findNext(ecb, current);
     * assertNotNull("wellformed bean", sb); assertTrue("invalid bean",
     * !sb.isActive()); }
     *
     * public void testFindPreviousSuccess() { EventCRFBean ecb = new
     * EventCRFBean(); SectionBean current = new SectionBean();
     *
     * ecb.setId(4); ecb.setCRFVersionId(101); current.setId(211);
     * current.setOrdinal(11);
     *
     * SectionBean sb = (SectionBean) secdao.findPrevious(ecb, current);
     * assertNotNull("wellformed bean", sb); assertTrue("valid bean",
     * sb.isActive()); assertEquals("correct bean", sb.getId(), 210); }
     *
     * public void testFindPreviousFailureWrongECBId() { EventCRFBean ecb = new
     * EventCRFBean(); SectionBean current = new SectionBean();
     *
     * ecb.setId(-1); ecb.setCRFVersionId(-1); current.setId(211);
     * current.setOrdinal(11);
     *
     * SectionBean sb = (SectionBean) secdao.findPrevious(ecb, current);
     * assertNotNull("wellformed bean", sb); assertTrue("invalid bean",
     * !sb.isActive()); }
     *
     * public void testFindPreviousFailureWrongSectionId() { EventCRFBean ecb =
     * new EventCRFBean(); SectionBean current = new SectionBean();
     *
     * ecb.setId(4); ecb.setCRFVersionId(101); current.setId(-1);
     * current.setOrdinal(-1);
     *
     * SectionBean sb = (SectionBean) secdao.findPrevious(ecb, current);
     * assertNotNull("wellformed bean", sb); assertTrue("invalid bean",
     * !sb.isActive()); }
     *
     * public void testFindPreviousFailureNoPreviousSection() { EventCRFBean ecb =
     * new EventCRFBean(); SectionBean current = new SectionBean();
     *
     * ecb.setId(4); ecb.setCRFVersionId(101); current.setId(201);
     * current.setOrdinal(1);
     *
     * SectionBean sb = (SectionBean) secdao.findPrevious(ecb, current);
     * assertNotNull("wellformed bean", sb); assertTrue("invalid bean",
     * !sb.isActive()); }
     */

    public void testGetNumItemsBySectionId() {
        Properties properties = DAOTestBase.getTestProperties();
        int sectionId = Integer.parseInt(properties.getProperty("sectionId"));
        HashMap answer = secdao.getNumItemsBySectionId();

        assertNotNull("well formed answer", answer);
        assertTrue("answer not empty", !answer.isEmpty());

        Integer num = (Integer) answer.get(sectionId);
        assertNotNull("well formed value", num);

        assertTrue("invalid section not in answer", !answer.containsKey(new Integer(-1)));
    }

    public void testGetNumItemsCompletedBySectionId() {
        Properties properties = DAOTestBase.getTestProperties();
        int eventCRFId = Integer.parseInt(properties.getProperty("eventCRFId"));
        int sectionId = Integer.parseInt(properties.getProperty("sectionId"));
        EventCRFBean ecb = new EventCRFBean();
        ecb.setId(eventCRFId);

        HashMap answer = secdao.getNumItemsCompletedBySectionId(ecb);

        assertNotNull("well formed answer", answer);

        assertTrue("invalid section not in answer", !answer.containsKey(new Integer(-1)));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.akaza.openclinica.dao.core.DAOTestBase#shutDown()
     */
    @Override
    protected void shutDown() throws Exception {
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
