/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.submit;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.DAOTestBase;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;

/**
 * @author thickerson
 *
 *
 */
public class EventCRFDAOTest extends DAOTestBase {
    private EventCRFDAO ecdao;

    private DAODigester digester = new DAODigester();

    public EventCRFDAOTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(ItemDAOTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        digester.setInputStream(new FileInputStream(SQL_DIR + "eventcrf_dao.xml"));

        try {
            digester.run();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        }

        super.ds = setupTestDataSource();
        Locale locale = ResourceBundleProvider.getLocale();

        ecdao = new EventCRFDAO(super.ds, digester, locale);
    }

    @Override
    public void testFindAll() throws Exception {
        Collection col = ecdao.findAll();
        assertNotNull("findAll", col);
    }

    public void testFindByPKSuccess() throws Exception {
        Collection col = ecdao.findAll();
        Iterator it = col.iterator();
        int pkey = 0;
        if (it.hasNext()) {
            EventCRFBean testMe = (EventCRFBean) it.next();
            pkey = testMe.getId();
        }
        EventCRFBean ecb = (EventCRFBean) ecdao.findByPK(pkey);
        assertNotNull("findByPK", ecb);
        assertTrue("valid bean", ecb.isActive());
        assertEquals("correct bean", ecb.getId(), pkey);
    }

    public void testFindTimestamp() throws Exception {
        Properties properties = DAOTestBase.getTestProperties();
        int eventCRFId = Integer.parseInt(properties.getProperty("eventCRFId"));
        EventCRFBean ecb = (EventCRFBean) ecdao.findByPK(eventCRFId);
        assertNotNull("findByPK", ecb);
        assertTrue("valid bean", ecb.isActive());

    }

    public void testFindByPKFailure() throws Exception {
        EventCRFBean ecb = (EventCRFBean) ecdao.findByPK(-1);
        assertNotNull("findByPK", ecb);
        assertTrue("invalid bean", !ecb.isActive());
    }

    public void testFindByPKAndStudySuccess() throws Exception {
        Properties properties = DAOTestBase.getTestProperties();
        int eventCRFId = Integer.parseInt(properties.getProperty("eventCRFId"));
        int studyId = Integer.parseInt(properties.getProperty("studyId"));
        StudyBean sb = new StudyBean();
        sb.setId(studyId);
        AuditableEntityBean ecb = ecdao.findByPKAndStudy(eventCRFId, sb);

        assertNotNull("findByPK", ecb);
        assertEquals("correct bean", ecb.getId(), eventCRFId);
    }

    public void testFindByPKAndStudyFailureBadPK() throws Exception {
        int pkey = -1;
        int studyId = 1;
        StudyBean sb = new StudyBean();
        sb.setId(studyId);

        AuditableEntityBean ecb = ecdao.findByPKAndStudy(pkey, sb);
        assertNotNull("findByPK", ecb);
        assertTrue("invalid bean", !ecb.isActive());
    }

    public void testFindByPKAndStudyFailureBadStudy() throws Exception {
        Properties properties = DAOTestBase.getTestProperties();
        int eventCRFId = Integer.parseInt(properties.getProperty("eventCRFId"));
        int studyId = -2;
        StudyBean sb = new StudyBean();
        sb.setId(studyId);

        AuditableEntityBean ecb = ecdao.findByPKAndStudy(eventCRFId, sb);
        assertNotNull("findByPK", ecb);
        assertTrue("invalid bean", !ecb.isActive());
    }

    /*
     * public void testUpdate() throws Exception { Properties properties =
     * DAOTestBase.getTestProperties(); int eventCRFId =
     * Integer.parseInt(properties.getProperty("eventCRFId")); int ownerId =
     * Integer.parseInt(properties.getProperty("ownerId")); Collection col =
     * ecdao.findAll(); Iterator it = col.iterator(); int pkey = 0; if
     * (it.hasNext()) { EventCRFBean testMe = (EventCRFBean)it.next(); pkey =
     * testMe.getId(); } EventCRFBean ecb = (EventCRFBean)ecdao.findByPK(pkey);
     * System.out.println("Found by primary key "+pkey+" annotation:
     * "+ecb.getAnnotations()); String keepAnnot = ecb.getAnnotations();
     * ecb.setAnnotations("changed annotations"); ecb.setStatus(Status.DELETED);
     * //ecb.setUpdaterId(1); UserAccountBean updater = new UserAccountBean();
     * updater.setId(ownerId); ecb.setUpdater(updater); ecb.setUpdatedDate(new
     * Date(System.currentTimeMillis())); ecb = (EventCRFBean)ecdao.update(ecb);
     * ecb.setAnnotations(keepAnnot); ecb.setStatus(Status.AVAILABLE); ecb =
     * (EventCRFBean)ecdao.update(ecb); assertNotNull(ecb); assertEquals("test
     * Update", keepAnnot, ecb.getAnnotations());
     *  }
     */

    public void testInsert() throws Exception {
        Properties properties = DAOTestBase.getTestProperties();
        int crfversionId = Integer.parseInt(properties.getProperty("crfversionId"));
        int studyeventId = Integer.parseInt(properties.getProperty("studyeventId"));
        int studySubjectId = Integer.parseInt(properties.getProperty("studySubjectId"));
        int ownerId = Integer.parseInt(properties.getProperty("ownerId"));
        EventCRFBean ecb = new EventCRFBean();
        Date thisDate = new Date(System.currentTimeMillis());
        ecb.setAnnotations("test inserting, delete me later " + thisDate.toString());
        // ecb.setOwnerId(1);
        UserAccountBean owner = new UserAccountBean();
        owner.setId(ownerId);
        ecb.setOwner(owner);
        // ecb.setStatusId(1);
        ecb.setStatus(Status.get(1));
        ecb.setCompletionStatusId(1);
        ecb.setInterviewerName("interviewer a1");
        ecb.setDateInterviewed(thisDate);
        ecb.setStudyEventId(studyeventId);
        ecb.setCRFVersionId(crfversionId);
        ecb.setStudySubjectId(studySubjectId);
        ecb = (EventCRFBean) ecdao.create(ecb);

        assertNotNull("Test Insert", ecb);
        assertEquals("Test Insert 2", ecb.getInterviewerName(), "interviewer a1");
    }

    public void testFindAllByStudyEventSuccess() throws Exception {
        Properties properties = DAOTestBase.getTestProperties();
        int studyeventId = Integer.parseInt(properties.getProperty("studyeventId"));
        StudyEventBean seb = new StudyEventBean();
        seb.setId(studyeventId);

        ArrayList rows = ecdao.findAllByStudyEvent(seb);

        assertNotNull("rows valid", rows);
        assertTrue("at least one row", rows.size() > 0);

        EventCRFBean ecb = (EventCRFBean) rows.get(0);
        assertNotNull("well-formed beans", ecb);
        assertTrue("bean id set", ecb.isActive());

    }

    public void testFindAllByStudyEventFailure() throws Exception {
        StudyEventBean seb = new StudyEventBean();
        seb.setId(-1);

        ArrayList rows = ecdao.findAllByStudyEvent(seb);

        assertNotNull("rows valid", rows);
        assertEquals("no rows", rows.size(), 0);
    }
}
