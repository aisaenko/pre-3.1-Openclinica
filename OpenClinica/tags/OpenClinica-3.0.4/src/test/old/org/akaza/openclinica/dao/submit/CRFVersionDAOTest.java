/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.submit;

import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.DAOTestBase;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;

/**
 * @author jxu
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class CRFVersionDAOTest extends DAOTestBase {
    private CRFVersionDAO cdao;

    private DAODigester digester = new DAODigester();

    public CRFVersionDAOTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(CRFVersionDAOTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        digester.setInputStream(new FileInputStream(SQL_DIR + "crfversion_dao.xml"));

        try {
            digester.run();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        }

        super.ds = setupTestDataSource();
        Locale locale = ResourceBundleProvider.getLocale();

        cdao = new CRFVersionDAO(super.ds, digester, locale);
    }

    public void testFindByPKSuccess() throws Exception {
        Properties properties = DAOTestBase.getTestProperties();
        int pk = Integer.parseInt(properties.getProperty("crfversionId"));
        CRFVersionBean cvb = (CRFVersionBean) cdao.findByPK(pk);

        assertNotNull("The returned bean is valid", cvb);
        assertTrue("The bean's isActive() method returns true.", cvb.isActive());
        assertEquals("The id is correct.", cvb.getId(), pk);
    }

    public void testFindByPKFailure() throws Exception {
        int pk = -1;
        CRFVersionBean cvb = (CRFVersionBean) cdao.findByPK(pk);

        assertNotNull("The bean is valid.", cvb);
        assertTrue("The id not set", !cvb.isActive());
    }

    /*
     * BWP> what is the purpose of this method? public void
     * testFindItemFromMap() throws Exception {
     *
     * ArrayList sb = (ArrayList) cdao.findItemFromMap(16); //assertEquals("test
     * items", sb.size(),0); }
     */

    public void testFindAllByCRFSuccess() throws Exception {
        Properties properties = DAOTestBase.getTestProperties();
        int crfid = Integer.parseInt(properties.getProperty("crfId"));
        ArrayList<CRFVersionBean> cRFVersionBeans = (ArrayList) cdao.findAllByCRF(crfid);

        assertNotNull("The rows are valid", cRFVersionBeans);
        assertTrue("At least one row is returned", cRFVersionBeans.size() > 0);

        CRFVersionBean cverBean = cRFVersionBeans.get(0);

        assertNotNull("The bean is valid", cverBean);
        assertTrue("The active method returns true.", cverBean.isActive());
        assertEquals("The id of the returned bean is correct.", cverBean.getCrfId(), crfid);

    }

    public void testFindAllByCRFFailure() throws Exception {
        // BWP>> no changes made to this method
        int crfid = -1;
        ArrayList sb = (ArrayList) cdao.findAllByCRF(crfid);

        assertNotNull("rows valid", sb);
        assertEquals("no rows", sb.size(), 0);
    }

    public void testGetCRFIdFromCRFVersionIdSuccess() throws Exception {
        Properties properties = DAOTestBase.getTestProperties();
        int crfversionId = Integer.parseInt(properties.getProperty("crfversionId"));
        int crfId = Integer.parseInt(properties.getProperty("crfId"));
        int id = cdao.getCRFIdFromCRFVersionId(crfversionId);
        assertEquals("returned the correct id", id, crfId);
    }

    public void testGetCRFIdFromCRFVersionIdFailure() throws Exception {
        // BWP>> no changes made to this method
        int id = cdao.getCRFIdFromCRFVersionId(-1);
        assertEquals("got correct answer", id, 0);
    }

    public void testFindAllByCRFIdSuccess() throws Exception {
        Properties properties = DAOTestBase.getTestProperties();
        int crfversionId = Integer.parseInt(properties.getProperty("crfversionId"));
        int crfId = Integer.parseInt(properties.getProperty("crfId"));
        ArrayList<CRFVersionBean> rows = cdao.findAllByCRFId(crfId);

        assertNotNull("rows valid", rows);
        assertTrue("The number of rows > 0", rows.size() > 0);
        CRFVersionBean cvb = null;
        for (CRFVersionBean verBean : rows) {

            if (verBean.getId() == crfversionId) {
                cvb = verBean;
            }
        }
        if (cvb == null) {
            cvb = new CRFVersionBean();
        }

        assertNotNull("The bean is valid.", cvb);
        assertTrue("The isActive() returns true.", cvb.isActive());
        assertEquals("The id is correct.", cvb.getId(), crfversionId);
    }

    public void testFindAllByCRFIdFailure() throws Exception {
        // BWP>> no changes made to this method
        ArrayList rows = cdao.findAllByCRFId(-1);

        assertNotNull("rows valid", rows);
        assertEquals("no rows", rows.size(), 0);
    }
}