/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.submit;

import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.DAOTestBase;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Properties;

/**
 * @author jxu
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ItemFormMetadataDAOTest extends DAOTestBase {
    private ItemFormMetadataDAO ifmdao;

    private DAODigester digester = new DAODigester();

    public ItemFormMetadataDAOTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(ItemFormMetadataDAOTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        digester.setInputStream(new FileInputStream(SQL_DIR + "item_form_metadata_dao.xml"));

        try {
            digester.run();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        }

        super.ds = setupTestDataSource();
        Locale locale = ResourceBundleProvider.getLocale();

        ifmdao = new ItemFormMetadataDAO(super.ds, digester, locale);
    }

    @Override
    public void testFindAll() throws Exception {
        Collection col = ifmdao.findAll();
        assertNotNull("findAll", col);
    }

    public void testFindByItemIdAndCRFVersionIdSuccess() {
        Properties properties = DAOTestBase.getTestProperties();
        int crfVersionId = Integer.parseInt(properties.getProperty("crfversionId"));
        int itemId = Integer.parseInt(properties.getProperty("itemId"));

        ItemFormMetadataBean ifm = ifmdao.findByItemIdAndCRFVersionId(itemId, crfVersionId);

        assertNotNull("well formed bean", ifm);
        assertTrue("valid bean", ifm.isActive());
        assertEquals("correct itemid", ifm.getItemId(), itemId);
        assertEquals("correct crfversionid", ifm.getCrfVersionId(), crfVersionId);
    }

    public void testFindByItemIdAndCRFVersionIdFailureWrongItemId() {
        int itemId = -1;
        int crfVersionId = 101;
        ItemFormMetadataBean ifm = ifmdao.findByItemIdAndCRFVersionId(itemId, crfVersionId);

        assertNotNull("well formed bean", ifm);
        assertTrue("invalid bean", !ifm.isActive());
    }

    public void testFindByItemIdAndCRFVersionIdFailureWrongCRFVersionId() {
        int itemId = 377;
        int crfVersionId = -1;
        ItemFormMetadataBean ifm = ifmdao.findByItemIdAndCRFVersionId(itemId, crfVersionId);

        assertNotNull("well formed bean", ifm);
        assertTrue("invalid bean", !ifm.isActive());
    }

    public void testFindAllBySectionIdSuccess() throws Exception {
        Properties properties = DAOTestBase.getTestProperties();
        int sectionId = Integer.parseInt(properties.getProperty("sectionId"));
        ArrayList answer = ifmdao.findAllBySectionId(sectionId);

        assertNotNull("well formed answer", answer);
        assertTrue("The number of rows > 0", answer.size() > 0);

        ItemFormMetadataBean ifm = (ItemFormMetadataBean) answer.get(0);
        assertNotNull("well formed bean", ifm);
        assertTrue("valid bean", ifm.isActive());
        assertTrue("the section id is valid and > 0", ifm.getSectionId() > 0);
    }

    public void testFindAllBySectionIdFailure() throws Exception {
        ArrayList answer = ifmdao.findAllBySectionId(-1);

        assertNotNull("well formed answer", answer);
        assertEquals("correct num rows", answer.size(), 0);
    }

}
