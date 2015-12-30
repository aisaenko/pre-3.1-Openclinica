/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.submit;

import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.DAOTestBase;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

/**
 * @author thickerson
 */
public class ItemDataDAOTest extends DAOTestBase {
    private ItemDataDAO iddao;

    private DAODigester digester = new DAODigester();

    public ItemDataDAOTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(ItemDataDAOTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        digester.setInputStream(new FileInputStream(SQL_DIR + "itemdata_dao.xml"));

        try {
            digester.run();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        }

        super.ds = setupTestDataSource();
        Locale locale = ResourceBundleProvider.getLocale();

        iddao = new ItemDataDAO(super.ds, digester, locale);
    }

    @Override
    public void testFindAll() throws Exception {
        Collection col = iddao.findAll();
        assertNotNull("findAll", col);
    }

    public void testFindByPK() throws Exception {
        Collection col = iddao.findAll();
        Iterator it = col.iterator();
        int pkey = 0;
        if (it.hasNext()) {
            ItemDataBean testMe = (ItemDataBean) it.next();
            pkey = testMe.getId();
        }
        ItemDataBean ecb = (ItemDataBean) iddao.findByPK(pkey);
        System.out.println("Found by primary key " + pkey + " value: " + ecb.getValue());
        assertNotNull("findByPK", ecb);
    }

    /*
     * Avoid updating or creating any data, unless the DB is set-up for fake
     * data.
     *
     * public void testUpdate() throws Exception { Collection col =
     * iddao.findAll(); Iterator it = col.iterator(); int pkey = 0; if
     * (it.hasNext()) { ItemDataBean testMe = (ItemDataBean)it.next(); pkey =
     * testMe.getId(); } ItemDataBean ecb = (ItemDataBean)iddao.findByPK(pkey);
     * String keepVal = ecb.getValue(); ecb.setValue("no"); ecb.setUpdaterId(1);
     * ecb = (ItemDataBean)iddao.update(ecb); ecb.setValue(keepVal);
     * ItemDataBean ecb2 = (ItemDataBean)iddao.update(ecb); assertNotNull("test
     * update", ecb2); assertEquals("test update too", keepVal,
     * ecb2.getValue()); }
     *
     * public void testCreate() throws Exception { Properties properties =
     * DAOTestBase.getTestProperties(); int eventCRFId =
     * Integer.parseInt(properties.getProperty("eventCRFId")); int itemId =
     * Integer.parseInt(properties.getProperty("itemId")); ItemDataBean idb =
     * new ItemDataBean(); DAODigester itemdigester = new DAODigester();
     * itemdigester.setInputStream(new FileInputStream(SQL_DIR +
     * "item_dao.xml"));
     *
     * try { itemdigester.run(); } catch (SAXException saxe) {
     * saxe.printStackTrace(); } Locale locale =
     * ResourceBundleProvider.getLocale(); ItemDAO idao = new ItemDAO(super.ds,
     * itemdigester,locale); idb.setOwnerId(1); idb.setEventCRFId(eventCRFId);
     * idb.setItemId(itemId); ArrayList col = (ArrayList)idao.findAll();
     * Iterator it = col.iterator(); if (it.hasNext()) { ItemBean fkey =
     * (ItemBean)it.next(); //assigns it a random item pkey int pkey =
     * fkey.getId(); idb.setItemId(pkey); } idb.setStatus(Status.AVAILABLE);
     * idb.setValue("yesno--delete me"); idb = (ItemDataBean)iddao.create(idb);
     * assertNotNull("test insert", idb); }
     */

}
