/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.admin;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.DAOTestBase;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * @author thickerson
 *
 *
 */
public class CRFDAOTest extends DAOTestBase {
    private CRFDAO cdao;

    private DAODigester digester = new DAODigester();

    public CRFDAOTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(CRFDAOTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        digester.setInputStream(new FileInputStream(SQL_DIR + "crf_dao.xml"));

        try {
            digester.run();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        }

        super.ds = setupTestDataSource();

        // filterdao = new FilterDAO(super.ds, getDigester("filter_dao.xml"));
        cdao = new CRFDAO(super.ds, digester);
    }

    @Override
    public void testFindAll() throws Exception {
        ArrayList col = (ArrayList) cdao.findAll();
        for (int i = 0; i < col.size(); i++) {
            CRFBean eb = (CRFBean) col.get(i);
            System.out.println(eb.getName());
        }
        assertNotNull("findAll", col);
    }

    public void testStuff() throws Exception {
        ArrayList col = (ArrayList) cdao.findAll();
        for (int i = 0; i < col.size(); i++) {
            CRFBean eb = (CRFBean) col.get(i);
            System.out.println(eb.getName());
        }
        assertNotNull("findAll", col);
    }

    // testFindByPK
    public void testFindByPKSuccess() throws Exception {
        CRFBean sb = (CRFBean) cdao.findByPK(1);
        assertNotNull("findbypk", sb);
        assertTrue("active object", sb.isActive());
        assertEquals("correct object", sb.getId(), 1);
    }

    public void testFindByPKFail() throws Exception {
        CRFBean sb = (CRFBean) cdao.findByPK(-1);
        assertNotNull("findbypk", sb);
        assertTrue("inactive object", !sb.isActive());
    }

    // testUpdate
    public void testUpdateSuccess() throws Exception {
        CRFBean sb = (CRFBean) cdao.findByPK(1);
        String keepName = sb.getName();
        sb.setName("new name");
        UserAccountBean ub = new UserAccountBean();
        ub.setId(1);
        sb.setUpdater(ub);

        sb.setStatus(Status.AVAILABLE);

        sb = (CRFBean) cdao.update(sb);
        sb.setName(keepName);
        CRFBean sbs = (CRFBean) cdao.update(sb);
        assertNotNull("test update", sbs);
    }

    public void testUpdateFail() throws Exception {
        CRFBean sb = (CRFBean) cdao.findByPK(1000);
        String keepName = sb.getName();
        sb.setName("new name");
        UserAccountBean ub = new UserAccountBean();
        ub.setId(2);
        sb.setUpdater(ub);

        sb.setStatus(Status.AVAILABLE);

        sb = (CRFBean) cdao.update(sb);
        sb.setName(keepName);
        CRFBean sbs = (CRFBean) cdao.update(sb);
        assertNotNull("test update", sbs);
        assertEquals("check primarykey", sb.getId(), 0);
    }

    // testInsert
    public void testInsertSuccess() throws Exception {
        CRFBean cb = new CRFBean();
        cb.setName("test CRF -- please delete");
        cb.setStatus(Status.AVAILABLE);
        cb.setDescription("test CRF -- please delete");
        UserAccountBean ub = new UserAccountBean();
        ub.setId(1);
        cb.setOwner(ub);
        CRFBean cbc = (CRFBean) cdao.create(cb);
        assertNotNull("test insert", cbc);
    }

    public void testInsertFail() throws Exception {
        CRFBean cb = new CRFBean();
        cb.setName("test CRF -- please delete");
        cb.setStatus(Status.AVAILABLE);
        cb.setDescription("test CRF -- please delete");
        cb.setOwner(new UserAccountBean());
        CRFBean cbc = (CRFBean) cdao.create(cb);
        assertNotNull("test insert", cbc);

        assertEquals("check primarykey", cb.getId(), 0);
    }

    public void testFindByVersionIdSuccess() throws Exception {
        int crfVersionId = 1;
        int crfId = 1;

        CRFBean cb = cdao.findByVersionId(crfVersionId);

        assertNotNull("bean valid", cb);
        assertTrue("bean id set", cb.isActive());
        assertEquals("correct bean found", cb.getId(), crfId);
    }

    public void testFindByVersionIdFail() throws Exception {
        int crfVersionId = -1;
        int crfId = 1;

        CRFBean cb = cdao.findByVersionId(crfVersionId);

        assertNotNull("bean valid", cb);
        assertTrue("bean inactive", !cb.isActive());
    }

    public void testReplaceString() throws Exception {
        String test = "aaa, bb\\,b, ccc";
        String olds = "\\,";
        String news = "##";

        String finals1 = test.replaceAll("\\\\,", "\\,");
        System.out.println("finals1:" + finals1);

    }
}
