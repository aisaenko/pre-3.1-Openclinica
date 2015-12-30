/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.managestudy;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.DAOTestBase;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author jxu
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class StudyEventDAOTest extends DAOTestBase {
    private StudyEventDAO sdao;

    private DAODigester digester = new DAODigester();

    public StudyEventDAOTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(StudySubjectDAOTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        digester.setInputStream(new FileInputStream(SQL_DIR + "study_event_dao.xml"));

        try {
            digester.run();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        }

        super.ds = setupTestDataSource();
        Locale locale = ResourceBundleProvider.getLocale();

        // super.ds = setupOraDataSource();
        // super.ds = setupDataSource();
        sdao = new StudyEventDAO(super.ds, digester, locale);
    }

    @Override
    public void testFindAll() throws Exception {
        Collection col = sdao.findAll();
        assertNotNull("findAll", col);
    }

    public void testFindAllByDefinition() throws Exception {
        Collection col = sdao.findAllByDefinition(1);
        assertNotNull("findAllByDefinition", col);
    }

    public void testFindAllWithSubjectLabelByDefinition() throws Exception {
        Collection col = sdao.findAllWithSubjectLabelByDefinition(5);
        assertNotNull("findAllWithSubjectLabelByDefinition", col);
    }

    public void testUpdate() throws Exception {
        StudyEventBean sb = new StudyEventBean();
        Properties properties = DAOTestBase.getTestProperties();
        int studyEventId = Integer.parseInt(properties.getProperty("studyeventId"));
        sb = (StudyEventBean) sdao.findByPK(studyEventId);
        sb.setLocation("this is the updated location");
        sb.setStatus(Status.AVAILABLE);
        UserAccountBean ub = new UserAccountBean();
        ub.setId(1);
        sb.setUpdater(ub);

        StudyEventBean sb2 = (StudyEventBean) sdao.update(sb);
        assertNotNull("simple update test", sb2);
    }

    public void testFindByPK() throws Exception {
        Properties properties = DAOTestBase.getTestProperties();
        int studyEventId = Integer.parseInt(properties.getProperty("studyeventId"));
        StudyEventBean sb = (StudyEventBean) sdao.findByPK(studyEventId);
        assertNotNull("findbypk", sb);
        assertEquals("check the location", "this is the updated location", sb.getLocation());
    }

    public void testCreate() throws Exception {
        StudyEventBean sb = new StudyEventBean();
        sb.setName("Study Event 1");
        sb.setDateStarted(new Date());
        sb.setDateEnded(new Date());
        sb.setSampleOrdinal(2);
        sb.setStudyEventDefinitionId(1);
        sb.setStudySubjectId(4);
        sb.setLocation("this is the location");

        sb.setStatus(Status.AVAILABLE);
        UserAccountBean ub = new UserAccountBean();
        ub.setId(1);
        sb.setOwner(ub);

        StudyEventBean sb2 = (StudyEventBean) sdao.create(sb);
        assertNotNull("simple create test", sb2);
    }

    public void testGetDefinitionIdFromStudyEventId() throws Exception {
        Properties properties = DAOTestBase.getTestProperties();
        int studyEventId = Integer.parseInt(properties.getProperty("studyeventId"));
        int studyEventDefId = Integer.parseInt(properties.getProperty("studyEventDefId"));
        int id = sdao.getDefinitionIdFromStudyEventId(studyEventId);
        assertEquals("The correct id is returned.", id, studyEventDefId);
    }

    public void testFindAllByStudyAndStudySubjectId() throws Exception {
        Properties properties = DAOTestBase.getTestProperties();
        int studyId = Integer.parseInt(properties.getProperty("studyId"));
        int studySubjectId = Integer.parseInt(properties.getProperty("studySubjectId"));
        StudyBean sb = new StudyBean();
        sb.setId(studyId);

        ArrayList rows = sdao.findAllByStudyAndStudySubjectId(sb, studySubjectId);

        assertNotNull("The ArrayList is valid.", rows);
        assertTrue("at least one row", rows.size() > 0);

        StudyEventBean seb = (StudyEventBean) rows.get(0);
        assertNotNull("The returned StudyEventBean is not null", seb);
        assertTrue("The StudyEventBean is active.", seb.isActive());
    }

    // thickerson
    // dev4?
    /*
     * public void testFindCRFsByStudyEvent() throws Exception { StudyEventBean
     * seb = new StudyEventBean(); Collection col = sdao.findAll(); Iterator it =
     * col.iterator(); if (it.hasNext()) { seb = (StudyEventBean)it.next(); }
     * //seb = (StudyEventBean)sdao.findByPK(1); HashMap hm =
     * sdao.findCRFsByStudyEvent(seb); Set se = hm.entrySet(); for (Iterator
     * iter = se.iterator(); iter.hasNext(); ) { Map.Entry newMap =
     * (Map.Entry)iter.next(); CRFBean cb = (CRFBean)newMap.getKey();
     * System.out.println("crf name: "+cb.getName()); } assertNotNull("found a
     * bean", seb); }
     */

    public void testFindCRFsByStudy() throws Exception {
        StudyBean sb = new StudyBean();
        Properties properties = DAOTestBase.getTestProperties();
        int studyId = Integer.parseInt(properties.getProperty("studyId"));
        sb.setId(studyId);
        HashMap hm = sdao.findCRFsByStudy(sb);
        Set se = hm.entrySet();
        for (Iterator iter = se.iterator(); iter.hasNext();) {
            Map.Entry newMap = (Map.Entry) iter.next();
            EntityBean cb = (EntityBean) newMap.getKey();
            ArrayList<EntityBean> listofEntityBeans = (ArrayList) newMap.getValue();
            System.out.println("event name: " + cb.getName());
            System.out.println("crf name: " + listofEntityBeans.get(0).getName());
        }
        assertNotNull("The test found a valid bean", sb);
    }
}
