/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.login;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.DAOTestBase;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import junit.swingui.TestRunner;

/**
 * <P>
 * Purpose of this class is to test against the older model, UserAccountDAOOld,
 * and to time its responses.
 * <P>
 * Note that this is now testing the Oracle database: in the postgres database,
 * we were looking for a user id of 1, now we are looking for a user id of 3. In
 * postgres, we were looking for a user name of tomh2, now we are looking for a
 * user name of tomh.
 *
 * @author thickerson
 */
public class UserAccountDAOTest extends DAOTestBase {

    private UserAccountDAO uadao;
    private StudyDAO sdao;

    private UserAccountDAOOld uadao_old;

    private DAODigester digester = new DAODigester();
    private DAODigester studyDigester = new DAODigester();

    public UserAccountDAOTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(UserAccountDAOTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // digester.setInputStream(new FileInputStream(SQL_DIR +
        // "useraccount_dao.xml"));
        digester.setInputStream(new FileInputStream(SQL_DIR + "useraccount_dao.xml"));
        studyDigester.setInputStream(new FileInputStream(SQL_DIR + "study_dao.xml"));
        try {
            digester.run();
            studyDigester.run();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        }

        // super.ds = setupPostgresDataSource();
        super.ds = setupTestDataSource();
        uadao = new UserAccountDAO(super.ds, digester);
        sdao = new StudyDAO(super.ds, studyDigester);
        uadao_old = new UserAccountDAOOld(super.ds);
    }

    @Override
    public void testFindAll() throws Exception {
        Collection col = uadao.findAll();
        assertNotNull("findAll", col);
    }

    public void testFindByPK() throws Exception {
        UserAccountBean uab = (UserAccountBean) uadao.findByUserName("tomh");
        UserAccountBean uab1 = (UserAccountBean) uadao.findByPK(uab.getId());
        assertNotNull("findByPK", uab);
    }

    public void testFindByUserName() throws Exception {
        UserAccountBean uab = (UserAccountBean) uadao.findByUserName("user1");
        assertNotNull("findByUserName", uab);
    }

    public void testFindByPKFirstName() throws Exception {
        UserAccountBean uab = (UserAccountBean) uadao.findByUserName("user1");
        UserAccountBean uab1 = (UserAccountBean) uadao.findByPK(uab.getId());
        assertEquals("Tom", uab1.getFirstName());
    }

    public void testFindByPKUserName() throws Exception {
        UserAccountBean uab = (UserAccountBean) uadao.findByPK(1);
        assertEquals("user1", uab.getName());
    }

    public void testTimedFindByPrimaryKey() throws Exception {
        long start = System.currentTimeMillis();
        UserAccountBean uab = (UserAccountBean) uadao.findByPK(1);
        long end = System.currentTimeMillis();
        long interval = end - start;
        System.out.println("new execution time FindByPrimaryKey:" + interval + "ms");
        assertNotNull("timed find by pk", uab);
    }

    public void testTimedFindByPrimaryKeyOld() throws Exception {
        long start = System.currentTimeMillis();
        UserAccountBean uab = uadao_old.findByPrimaryKey(1);
        long end = System.currentTimeMillis();
        long interval = end - start;
        System.out.println("old execution time FindByPrimaryKey:" + interval + "ms");
        assertNotNull("timed find by pk", uab);
    }

    public void testTimedFindAll() throws Exception {
        long start = System.currentTimeMillis();
        Collection col = uadao.findAll();
        long end = System.currentTimeMillis();
        long interval = end - start;
        System.out.println("new execution time FindAll:" + interval + "ms");
        assertNotNull("findAll", col);
    }

    public void testTimedFindAllOld() throws Exception {
        long start = System.currentTimeMillis();
        Collection col = uadao_old.findAll();
        long end = System.currentTimeMillis();
        long interval = end - start;
        System.out.println("old execution time FindAll:" + interval + "ms");
        assertNotNull("findAll", col);
    }

    public void testInsert() throws Exception {
        UserAccountBean uab = new UserAccountBean();
        uab.setName("testme");
        uab.setFirstName("Test");
        uab.setLastName("Me");
        uab.setStatus(Status.AVAILABLE);
        uab.setActiveStudyId(3);
        uab.setPasswd("testme222");
        uab.setEmail("me@me.com");
        uab.setPasswdChallengeQuestion("xxx");
        uab.setPasswdChallengeAnswer("yyy");
        uab.setInstitutionalAffiliation("AKAZA");
        uab.setPhone("617 611 1111");
        uab.setOwnerId(1);

        long start = System.currentTimeMillis();
        UserAccountBean uab2 = (UserAccountBean) uadao.create(uab);
        long end = System.currentTimeMillis();
        long interval = end - start;
        System.out.println("new execution time insert: " + interval + "ms");
        // need to delete row
        uadao.deleteTestOnly("testme");
        assertNotNull("insert", uab2);
        assertEquals("insert same name", "testme", uab2.getName());

    }

    public void testUpdate() throws Exception {
        // inserts and updates, then deletes
        UserAccountBean uab = new UserAccountBean();
        uab.setName("testme");
        uab.setFirstName("Test");
        uab.setLastName("Me");
        uab.setStatus(Status.AVAILABLE);
        uab.setActiveStudyId(2);
        uab.setPasswd("testme222");
        uab.setEmail("me@me.com");
        uab.setPasswdChallengeQuestion("xxx");
        uab.setPasswdChallengeAnswer("yyy");
        uab.setInstitutionalAffiliation("AKAZA");
        uab.setPhone("617 611 1111");
        uab.setOwnerId(1);
        long start = System.currentTimeMillis();
        UserAccountBean uab2 = (UserAccountBean) uadao.create(uab);
        long end = System.currentTimeMillis();
        long interval = end - start;
        System.out.println("new execution time insert: " + interval + "ms");
        // need to update row
        uab2.setFirstName("Test2");
        UserAccountBean updater = new UserAccountBean();
        uab2.setUpdater(updater);
        uab2.setLastVisitDate(new Date());
        uab2.setPasswdTimestamp(new Date());
        start = System.currentTimeMillis();
        UserAccountBean uab3 = (UserAccountBean) uadao.update(uab2);
        end = System.currentTimeMillis();
        interval = end - start;
        System.out.println("new execution time update: " + interval + "ms");
        uadao.deleteTestOnly("testme");
        assertNotNull("update", uab3);
        assertEquals("update same first name", "Test2", uab3.getFirstName());
    }

    public void testSingleUpdate() throws Exception {
        // created to assist in filling in fields, tbh
        UserAccountBean uab = (UserAccountBean) uadao.findByUserName("user1");
        uab.setPasswdChallengeQuestion("yyyydude!");
        uab.setPhone("515 444 2222");
        uab.setActiveStudyId(1);
        uab.setInstitutionalAffiliation("Akaza");
        UserAccountBean updater = new UserAccountBean();
        uab.setUpdater(updater);
        uab.setLastVisitDate(new Date());
        uab.setPasswdTimestamp(new Date());
        long start = System.currentTimeMillis();
        UserAccountBean uab3 = (UserAccountBean) uadao.update(uab);
        long end = System.currentTimeMillis();
        long interval = end - start;
        System.out.println("new execution time update: " + interval + "ms");
        assertNotNull("another update", uab3);
    }

    public void testPrivs() throws Exception {
        Collection col = uadao.findPrivilegesByRole(1);

        assertNotNull("testPrivs", col);
    }

    public void testRoles() throws Exception {
        Collection col = uadao.findAllRolesByUserName("tomh2");
        assertNotNull("testRoles", col);
    }

    // public void testGrabbingPrivs() throws Exception {
    // UserAccountBean uab = (UserAccountBean)uadao.findByUserName("tomh");
    // assertEquals("equal to 1", "1", uab.getPrivilege("run_query"));
    // assertEquals("equal to 1", "1", uab.getPrivilege("run_dataset"));
    //
    // }

    public void testAuditInserts() throws Exception {
        UserAccountBean uab = (UserAccountBean) uadao.findByUserName("user1");
        uab.setFirstName("BigDude");
        uab.setPhone("515 444 2222");
        uab.setInstitutionalAffiliation("Akaza");
        UserAccountBean updater = new UserAccountBean();
        uab.setUpdater(updater);
        uab.setLastVisitDate(new Date());
        uab.setPasswdTimestamp(new Date());
        long start = System.currentTimeMillis();
        UserAccountBean uab3 = (UserAccountBean) uadao.update(uab);
        long end = System.currentTimeMillis();
        long interval = end - start;
        System.out.println("new execution time update with audits: " + interval + "ms");
        uab3.setFirstName("Tom");
        start = System.currentTimeMillis();
        UserAccountBean uab4 = (UserAccountBean) uadao.update(uab3);
        end = System.currentTimeMillis();
        interval = end - start;
        System.out.println("new execution time update with audits: " + interval + "ms");
        assertNotNull("another update audit insert", uab4);
    }

    public void testStudyRolesSetForSysAdmin() throws Exception {
        int pk = 113;
        UserAccountBean uab = (UserAccountBean) uadao.findByPK(pk);

        assertNotNull("bean valid", uab);
        assertTrue("bean id set", uab.isActive());
        assertEquals("got right bean", uab.getId(), pk);

        StudyUserRoleBean surb = uab.getRoleByStudy(109);
        assertNotNull("role valid", surb);
        assertTrue("role id set", surb.isActive());
        assertEquals("study id correct", surb.getStudyId(), 109);
        assertTrue("found correct role", surb.getRoleName().equals("director"));
    }

    public void testFindStudyByUser() throws Exception {
        String userName = "jun_director";

        ArrayList answers = uadao.findStudyByUser(userName, (ArrayList) sdao.findAll());
        for (int i = 0; i < answers.size(); i++) {
            StudyUserRoleBean sb = (StudyUserRoleBean) answers.get(i);
            if (sb.getParentStudyId() > 0) {
                System.out.print("---");
            }
            System.out.println(sb.getStudyName() + " " + sb.getRole().getName());
        }
    }

}