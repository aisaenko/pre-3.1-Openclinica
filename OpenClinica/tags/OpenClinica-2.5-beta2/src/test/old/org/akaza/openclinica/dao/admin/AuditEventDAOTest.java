/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.admin;

import org.akaza.openclinica.bean.admin.AuditEventBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.DAOTestBase;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

/**
 * @author jxu
 *
 *
 */
public class AuditEventDAOTest extends DAOTestBase {
    private AuditEventDAO sdao;

    private DAODigester digester = new DAODigester();

    public AuditEventDAOTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(AuditEventDAOTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        digester.setInputStream(new FileInputStream(SQL_DIR + "audit_event_dao.xml"));

        try {
            digester.run();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        }

        super.ds = setupTestDataSource();

        sdao = new AuditEventDAO(super.ds, digester);
    }

    @Override
    public void testFindAll() throws Exception {
        Collection col = sdao.findAll();
        assertNotNull("findAll", col);
    }

    public void testCreate() throws Exception {
        AuditEventBean sb = new AuditEventBean();
        sb.setAuditDate(new Date());
        sb.setAuditTable("study");
        sb.setEntityId(3);
        sb.setReasonForChange("for testing purpose");
        sb.setUserId(1);

        sb.setStatus(Status.AVAILABLE);
        UserAccountBean ub = new UserAccountBean();
        ub.setId(1);
        sb.setOwner(ub);

        AuditEventBean sb2 = (AuditEventBean) sdao.create(sb);
        assertNotNull("simple create test", sb2);
    }

    public void testFindAggregatesByTableName() throws Exception {

        ArrayList col = (ArrayList) sdao.findAggregatesByTableName("USER_ACCOUNT");
        Iterator it = col.iterator();
        if (it.hasNext()) {
            AuditEventBean sb = (AuditEventBean) col.get(0);
            System.out.println("study column:" + sb.getColumnName());
            sb = (AuditEventBean) col.get(1);
            System.out.println("study column:" + sb.getColumnName());
            assertNotNull("FindAggregatesByTableName", sb);

        } else {

            assertNull("no information in table", col);
        }

    }

    /*
     * public void testFindByPK() throws Exception { AuditEventBean sb =
     * (AuditEventBean) sdao.findByPK(257);
     *
     * assertNotNull("findbypk", sb); assertEquals("check the reason", "for
     * testing purpose", sb.getReasonForChange()); }
     */

}
