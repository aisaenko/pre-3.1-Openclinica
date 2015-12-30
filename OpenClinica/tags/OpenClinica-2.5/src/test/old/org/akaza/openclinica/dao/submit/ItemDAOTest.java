/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.submit;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.DAOTestBase;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;

/**
 * @author thickerson
 */
public class ItemDAOTest extends DAOTestBase {
    private ItemDAO idao;

    private DAODigester digester = new DAODigester();

    public ItemDAOTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(ItemDAOTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        digester.setInputStream(new FileInputStream(SQL_DIR + "item_dao.xml"));

        try {
            digester.run();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        }

        super.ds = setupTestDataSource();
        Locale locale = ResourceBundleProvider.getLocale();

        // super.ds = setupOraDataSource();
        // super.ds = setupDataSource();
        idao = new ItemDAO(super.ds, digester, locale);
    }

    @Override
    public void testFindAll() throws Exception {
        Collection col = idao.findAll();
        assertNotNull("findAll", col);
    }

    public void testFindByPK() throws Exception {
        Collection col = idao.findAll();
        Iterator it = col.iterator();
        int pkey = 0;
        if (it.hasNext()) {
            ItemBean testMe = (ItemBean) it.next();
            pkey = testMe.getId();
        }
        ItemBean ib = (ItemBean) idao.findByPK(pkey);
        System.out.println("Found by primary key " + pkey + " name: " + ib.getName());
        assertNotNull("findByPK", ib);
    }

    public void testUpdate() throws Exception {
        Collection col = idao.findAll();
        Iterator it = col.iterator();
        int pkey = 0;
        if (it.hasNext()) {
            ItemBean testMe = (ItemBean) it.next();
            pkey = testMe.getId();
        }
        ItemBean ib = (ItemBean) idao.findByPK(pkey);
        System.out.println("Found by primary key " + pkey + " name: " + ib.getName());
        String keepDesc = ib.getDescription();
        ib.setDescription("foo fighters deluxe");
        ib = (ItemBean) idao.update(ib);
        ib.setDescription(keepDesc);
        ib = (ItemBean) idao.update(ib);
        int equals = 0;

        assertEquals("test Update", keepDesc, ib.getDescription());
    }

    public void testInsert() throws Exception {
        ItemBean ib = new ItemBean();
        ib.setName("Delete Me Later");
        ib.setDescription("item created to test use case only");
        ib.setUnits("mg");
        ib.setStatusId(1);
        ib.setStatus(Status.get(ib.getStatusId()));
        ib.setItemDataTypeId(1);
        ib.setItemReferenceTypeId(1);
        ib.setOwnerId(1);
        ib = (ItemBean) idao.create(ib);
        assertNotNull("test Insert", ib);
    }

    /*
     * Commented out because it's too hard to find specific IDs that make this
     * method pass.
     *
     * public void testFindAllByParentIdAndCRFVersionIdSuccess() throws
     * Exception { ArrayList answer = idao.findAllByParentIdAndCRFVersionId(651,
     * 101);
     *
     * assertNotNull("well-formed answer", answer); assertEquals("correct number
     * of rows", answer.size(), 2);
     *
     * ItemBean ib = (ItemBean) answer.get(0); assertNotNull("well formed bean",
     * ib); assertTrue("valid bean", ib.isActive()); assertTrue("correct bean",
     * (ib.getId() == 402) || (ib.getId() == 152)); }
     */

    public void testFindAllByParentIdAndCRFVersionIdFailureWrongPID() throws Exception {
        ArrayList answer = idao.findAllByParentIdAndCRFVersionId(-1, 101);

        assertNotNull("well-formed answer", answer);
        assertEquals("correct number of rows", answer.size(), 0);
    }

    public void testFindAllByParentIdAndCRFVersionIdFailureWrongCRFVID() throws Exception {
        ArrayList answer = idao.findAllByParentIdAndCRFVersionId(651, -1);

        assertNotNull("well-formed answer", answer);
        assertEquals("correct number of rows", answer.size(), 0);
    }

    /*
     * This test case is too bound to a particular DB configuration.
     *
     * public void testFindAllParentsBySectionIdSuccess() throws Exception {
     * ArrayList answer = idao.findAllParentsBySectionId(203);
     *
     * assertNotNull("well formed answer", answer); assertEquals("correct number
     * of rows", answer.size(), 36);
     *
     * ItemBean ib = (ItemBean) answer.get(0); assertTrue("valid bean",
     * ib.isActive()); assertEquals("correct bean", ib.getId(), 272); }
     */

    public void testFindAllParentsBySectionIdFailure() throws Exception {
        ArrayList answer = idao.findAllParentsBySectionId(-1);

        assertNotNull("well formed answer", answer);
        assertEquals("correct number of rows", answer.size(), 0);
    }

    public void testFindAllActiveByCRF() {
        Properties properties = DAOTestBase.getTestProperties();
        int crfId = Integer.parseInt(properties.getProperty("crfId"));
        CRFBean crf = new CRFBean();
        crf.setId(crfId);
        ArrayList items = idao.findAllActiveByCRF(crf);
    }
}