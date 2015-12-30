/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.core;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.login.UserAccountRow;

import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

/**
 * We use UserAccountRow to test this class.
 *
 * @author ssachs
 */
public class EntityBeanTableTest extends TestCase {
    EntityBeanTable table;

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(EntityBeanTableTest.class);
    }

    public EntityBeanTableTest(String name) {
        super(name);
        table = new EntityBeanTable();
    }

    @Override
    protected void setUp() throws Exception {
        populateRows();
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private void populateRows() {
        UserAccountBean a = new UserAccountBean();
        UserAccountBean b = new UserAccountBean();
        UserAccountBean c = new UserAccountBean();

        // order the beans a,b,c by username
        a.setName("abc");
        b.setName("def");
        c.setName("ghi");

        // order the beans b,c,a by first name
        a.setFirstName("Zack");
        b.setFirstName("Bob abc");
        c.setFirstName("Cindy");

        // order the beans c,b,a by last name
        a.setLastName("Xerxes");
        b.setLastName("Zuckerman");
        c.setLastName("Connor");

        UserAccountRow rowA = new UserAccountRow();
        rowA.setBean(a);
        UserAccountRow rowB = new UserAccountRow();
        rowB.setBean(b);
        UserAccountRow rowC = new UserAccountRow();
        rowC.setBean(c);
        UserAccountRow[] rows = { rowA, rowB, rowC };

        ArrayList tempRows = new ArrayList(Arrays.asList(rows));

        // add 15 dummy rows so we can test out pagination
        for (int i = 0; i < 15; i++) {
            UserAccountBean uab = new UserAccountBean();
            // force these beans into the middle of the alphabet so they don't
            // ruin the sorting results
            uab.setName("efg");
            uab.setFirstName("jjj");
            uab.setLastName("jjj");
            UserAccountRow uar = new UserAccountRow();
            uar.setBean(uab);
            tempRows.add(uar);
        }

        table.setRows(tempRows);
    }

    public void testSortByUserNameASC() {
        table.setAscendingSort(true);
        table.setSortingColumnInd(UserAccountRow.COL_USERNAME);

        table.computeDisplay();
        ArrayList rows = table.getRows();

        UserAccountRow first = (UserAccountRow) rows.get(0);
        assertEquals("got first row right", first.getBean().getName(), "abc");
    }

    public void testSortByUserNameDESC() {
        table.setAscendingSort(false);
        table.setSortingColumnInd(UserAccountRow.COL_USERNAME);

        table.computeDisplay();
        ArrayList rows = table.getRows();

        UserAccountRow first = (UserAccountRow) rows.get(0);
        assertEquals("got first row right", first.getBean().getName(), "ghi");
    }

    public void testSortByFirstNameASC() {
        table.setAscendingSort(true);
        table.setSortingColumnInd(UserAccountRow.COL_FIRSTNAME);

        table.computeDisplay();
        ArrayList rows = table.getRows();

        UserAccountRow first = (UserAccountRow) rows.get(0);
        assertEquals("got first row right", first.getBean().getName(), "def");
    }

    public void testSortByFirstNameDESC() {
        table.setAscendingSort(false);
        table.setSortingColumnInd(UserAccountRow.COL_FIRSTNAME);

        table.computeDisplay();
        ArrayList rows = table.getRows();

        UserAccountRow first = (UserAccountRow) rows.get(0);
        assertEquals("got first row right", first.getBean().getName(), "abc");
    }

    public void testSortByLastNameASC() {
        table.setAscendingSort(true);
        table.setSortingColumnInd(UserAccountRow.COL_LASTNAME);

        table.computeDisplay();
        ArrayList rows = table.getRows();

        UserAccountRow first = (UserAccountRow) rows.get(0);
        assertEquals("got first row right", first.getBean().getName(), "ghi");
    }

    public void testSortByLastNameDESC() {
        table.setAscendingSort(false);
        table.setSortingColumnInd(UserAccountRow.COL_LASTNAME);

        table.computeDisplay();
        ArrayList rows = table.getRows();

        UserAccountRow first = (UserAccountRow) rows.get(0);
        assertEquals("got first row right", first.getBean().getName(), "def");
    }

    public void testSearchWithMultipleResults() {
        table.setFiltered(true);
        table.setKeywordFilter("abc");
        table.computeDisplay();
        ArrayList rows = table.getRows();
        assertEquals("got right number of rows", rows.size(), 2);
    }

    public void testSearchWithOneResult() {
        table.setFiltered(true);
        table.setKeywordFilter("ghi");
        table.computeDisplay();
        ArrayList rows = table.getRows();
        assertEquals("got right number of rows", rows.size(), 1);
    }

    public void testSearchWithNoResults() {
        table.setFiltered(true);
        table.setKeywordFilter("zzz");
        table.computeDisplay();
        ArrayList rows = table.getRows();
        assertEquals("got right number of rows", rows.size(), 0);
    }

    public void testSearchAndSortingCombined() {
        table.setFiltered(true);
        table.setKeywordFilter("abc");
        table.setAscendingSort(true);
        table.setSortingColumnInd(UserAccountRow.COL_FIRSTNAME);
        table.computeDisplay();

        // users abc and def should have been returned.
        // since we are sorting on the first name, def should show up first

        ArrayList rows = table.getRows();
        assertEquals("got right number of rows", rows.size(), 2);

        UserAccountRow row = (UserAccountRow) rows.get(0);
        assertEquals("got first row right", row.getBean().getName(), "def");
    }

    public void testPaginationFirstPage() {
        table.setPaginated(true);
        table.setCurrPageNumber(1);

        table.computeDisplay();

        ArrayList rows = table.getRows();
        assertEquals("got right number of rows", rows.size(), EntityBeanTable.NUM_ROWS_PER_PAGE);
    }

    public void testPaginationSecondPage() {
        table.setPaginated(true);
        table.setCurrPageNumber(2);

        table.computeDisplay();

        ArrayList rows = table.getRows();
        assertEquals("got right number of rows", rows.size(), 8);
    }

    public void testPaginationFirstPageWithSortASC() {
        // we're going to sort by username, then paginate and go to page 1
        // since user abc is at the head by username,
        // the first element of rows should be that user

        table.setSortingColumnInd(UserAccountRow.COL_USERNAME);
        table.setAscendingSort(true);
        table.setPaginated(true);
        table.setCurrPageNumber(1);

        table.computeDisplay();

        ArrayList rows = table.getRows();
        assertEquals("got right number of rows", rows.size(), 10);

        UserAccountRow head = (UserAccountRow) rows.get(0);
        UserAccountRow tail = (UserAccountRow) rows.get(rows.size() - 1);
        assertEquals("correct bean at top of the list", head.getBean().getName(), "abc");
        assertEquals("correct bean at end of the list", tail.getBean().getName(), "efg");
    }

    public void testPaginationSecondPageWithSortASC() {
        // we're going to sort by username, then paginate and go to page 2
        // since user efg is in the middle by username,
        // the first element of rows should be that user

        table.setSortingColumnInd(UserAccountRow.COL_USERNAME);
        table.setAscendingSort(true);
        table.setPaginated(true);
        table.setCurrPageNumber(2);

        table.computeDisplay();

        ArrayList rows = table.getRows();
        assertEquals("got right number of rows", rows.size(), 8);

        UserAccountRow head = (UserAccountRow) rows.get(0);
        UserAccountRow tail = (UserAccountRow) rows.get(rows.size() - 1);
        assertEquals("correct bean at top of the list", head.getBean().getName(), "efg");
        assertEquals("correct bean at end of the list", tail.getBean().getName(), "ghi");
    }

    public void testPaginationFirstPageWithSortDESC() {
        // we're going to sort by username, then paginate and go to page 1
        // since user ghi is at the tail by username,
        // the first element of rows should be that user

        table.setSortingColumnInd(UserAccountRow.COL_USERNAME);
        table.setAscendingSort(false);
        table.setPaginated(true);
        table.setCurrPageNumber(1);

        table.computeDisplay();

        ArrayList rows = table.getRows();
        assertEquals("got right number of rows", rows.size(), 10);

        UserAccountRow head = (UserAccountRow) rows.get(0);
        UserAccountRow tail = (UserAccountRow) rows.get(rows.size() - 1);
        assertEquals("correct bean at top of the list", head.getBean().getName(), "ghi");
        assertEquals("correct bean at end of the list", tail.getBean().getName(), "efg");
    }

    public void testPaginationSecondPageWithSortDESC() {
        // we're going to sort by username DESC, then paginate and go to page 2
        // since user efg is in the middle by username,
        // the first element of rows should be that user

        table.setSortingColumnInd(UserAccountRow.COL_USERNAME);
        table.setAscendingSort(false);
        table.setPaginated(true);
        table.setCurrPageNumber(2);

        table.computeDisplay();

        ArrayList rows = table.getRows();
        assertEquals("got right number of rows", rows.size(), 8);

        UserAccountRow head = (UserAccountRow) rows.get(0);
        UserAccountRow tail = (UserAccountRow) rows.get(rows.size() - 1);
        assertEquals("correct bean at top of the list", head.getBean().getName(), "efg");
        assertEquals("correct bean at end of the list", tail.getBean().getName(), "abc");
    }

    public void testPaginationWithSearchFirstPage() {
        table.setFiltered(true);
        table.setPaginated(true);
        table.setKeywordFilter("efg");
        table.setCurrPageNumber(1);

        table.computeDisplay();

        ArrayList rows = table.getRows();
        assertEquals("got right number of rows", rows.size(), 10);

        UserAccountRow head = (UserAccountRow) rows.get(0);
        UserAccountRow tail = (UserAccountRow) rows.get(rows.size() - 1);
        assertEquals("correct bean at top of the list", head.getBean().getName(), "efg");
        assertEquals("correct bean at end of the list", tail.getBean().getName(), "efg");
    }

    public void testPaginationWithSearchSecondPage() {
        table.setFiltered(true);
        table.setPaginated(true);
        table.setKeywordFilter("efg");
        table.setCurrPageNumber(2);

        table.computeDisplay();

        ArrayList rows = table.getRows();
        assertEquals("got right number of rows", rows.size(), 5);

        UserAccountRow head = (UserAccountRow) rows.get(0);
        UserAccountRow tail = (UserAccountRow) rows.get(rows.size() - 1);
        assertEquals("correct bean at top of the list", head.getBean().getName(), "efg");
        assertEquals("correct bean at end of the list", tail.getBean().getName(), "efg");
    }

    public void testSearchMultipleKeywords() {
        // we will use the multi-keyword search feature, searching for "def" and
        // "ghi"
        // this should return only users def and ghi, no one else
        // so we expect exactly two rows

        table.setFiltered(true);
        table.setKeywordFilter("def ghi");
        table.computeDisplay();

        ArrayList rows = table.getRows();
        assertEquals("got right number of rows", rows.size(), 2);
    }
}