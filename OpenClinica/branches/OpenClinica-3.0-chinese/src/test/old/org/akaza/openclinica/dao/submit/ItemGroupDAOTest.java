package org.akaza.openclinica.dao.submit;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.DAOTestBase;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * A JUnit test for ItemGroupDAO.
 */
public class ItemGroupDAOTest extends DAOTestBase {
    private ItemGroupDAO itemGroupDAO;
    private DAODigester digester = new DAODigester();

    public ItemGroupDAOTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(ItemGroupDAOTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        digester.setInputStream(new FileInputStream(SQL_DIR + "item_group_dao.xml"));

        try {
            digester.run();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        }

        super.ds = setupTestDataSource();
        Locale locale = ResourceBundleProvider.getLocale();

        // super.ds = setupOraDataSource();
        // super.ds = setupDataSource();
        itemGroupDAO = new ItemGroupDAO(super.ds, digester, locale);
    }

    public void testCreate() {
        Properties properties = DAOTestBase.getTestProperties();
        int ownerId = Integer.parseInt(properties.getProperty("ownerId"));
        int crfId = Integer.parseInt(properties.getProperty("crfId"));
        ItemGroupBean itemGroupBean = new ItemGroupBean();
        itemGroupBean.setName("tempGroupForJUnit");
        itemGroupBean.setCreatedDate(new Date());
        itemGroupBean.setMeta(new ItemGroupMetadataBean());
        itemGroupBean.setCrfId(crfId);

        UserAccountBean userBean = new UserAccountBean();
        userBean.setId(ownerId);
        itemGroupBean.setOwner(userBean);
        itemGroupBean.setStatus(Status.AVAILABLE);

        ItemGroupBean returnedBean = (ItemGroupBean) itemGroupDAO.create(itemGroupBean);
        assertNotNull("The returned bean is not null.", returnedBean);
        assertTrue("The bean name is correct", "tempGroupForJUnit".equalsIgnoreCase(returnedBean.getName()));
        assertTrue("The returned bean is set as active.", returnedBean.isActive());

        // delete temp item group
        itemGroupDAO.deleteTestGroup("tempGroupForJUnit");

    }

    @Override
    public void testFindAll() {
        List<ItemGroupBean> itemGroupBeans = new ArrayList<ItemGroupBean>();
        itemGroupBeans = (List) itemGroupDAO.findAll();
        assertNotNull("The returned Collection is not null.", itemGroupBeans);
        assertTrue("The List is not empty.", !itemGroupBeans.isEmpty());
        ItemGroupBean groupBean = itemGroupBeans.get(0);
        assertNotNull("First bean in the List is not null.", groupBean);

    }

    public void testUpdate() {
        Properties properties = DAOTestBase.getTestProperties();
        String groupName = properties.getProperty("groupName");
        int ownerId = Integer.parseInt(properties.getProperty("ownerId"));

        assertNotNull("The group name is not null.", groupName);
        ItemGroupBean itemGroupBean = (ItemGroupBean) itemGroupDAO.findByName(groupName);
        itemGroupBean.setName("NewTempName");
        UserAccountBean userBean = new UserAccountBean();
        userBean.setId(ownerId);

        itemGroupBean.setUpdater(userBean);
        itemGroupBean = (ItemGroupBean) itemGroupDAO.update(itemGroupBean);
        assertTrue("The new name is returned in the new bean.", "NewTempName".equalsIgnoreCase(itemGroupBean.getName()));
        // return to the original name and account
        itemGroupBean.setName(groupName);
        itemGroupBean = (ItemGroupBean) itemGroupDAO.update(itemGroupBean);
        assertTrue("The original name is restored.", groupName.equalsIgnoreCase(itemGroupBean.getName()));

    }

    public void testFindByName() {
        Properties properties = DAOTestBase.getTestProperties();
        String groupName = properties.getProperty("groupName");
        ItemGroupBean itemGroupBean = (ItemGroupBean) itemGroupDAO.findByName(groupName);
        assertNotNull("The group name is not null.", groupName);
        assertNotNull("The returned bean is not null.", itemGroupBean);
        assertTrue("the bean has the same name as the searched for name.", groupName.equalsIgnoreCase(itemGroupBean.getName()));

    }

    public void testFindGroupsByItemID() {
        Properties properties = DAOTestBase.getTestProperties();
        int itemId = Integer.parseInt(properties.getProperty("itemId"));
        List<ItemGroupBean> itemGBeans = (List<ItemGroupBean>) itemGroupDAO.findGroupsByItemID(itemId);
        assertNotNull("The beans are not null", itemGBeans);
        assertTrue("There is at least one bean", itemGBeans.size() > 0);

    }

    public void testFindOnlyGroupsByCRFVersionID() {
        Properties properties = DAOTestBase.getTestProperties();
        int versionId = Integer.parseInt(properties.getProperty("crfversionId"));
        List<ItemGroupBean> itemGBeans = itemGroupDAO.findOnlyGroupsByCRFVersionID(versionId);
        assertNotNull("The returned groups are not null.", itemGBeans);
        // if they include any groups, then none of the groups should have a
        // name of
        // 'ungrouped'
        for (ItemGroupBean groupBean : itemGBeans) {
            assertTrue("The name is not 'ungrouped'", !"ungrouped".equalsIgnoreCase(groupBean.getName()));
        }

    }

    public void testFindGroupBySectionId() {
        Properties properties = DAOTestBase.getTestProperties();
        int sectionId = Integer.parseInt(properties.getProperty("sectionId"));
        List<ItemGroupBean> itemGBeans = itemGroupDAO.findGroupBySectionId(sectionId);
        assertNotNull("The returned groups are not null.", itemGBeans);
        for (ItemGroupBean groupBean : itemGBeans) {
            assertNotNull("The name is not null", groupBean.getName());
        }

    }

    public void testGetNextPK() {
        int pkVal = itemGroupDAO.getNextPK();
        assertTrue("The pk is not zero.", pkVal > 0);

    }
}
