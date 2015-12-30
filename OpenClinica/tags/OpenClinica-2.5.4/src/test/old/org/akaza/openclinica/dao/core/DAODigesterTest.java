/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.core;

import org.xml.sax.SAXException;

import java.io.FileInputStream;

import junit.framework.TestCase;

/**
 * @author thickerson
 *
 *
 */
public class DAODigesterTest extends TestCase {

    private DAODigester userDaoDigester = new DAODigester();

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(DAODigesterTest.class);
    }

    public DAODigesterTest(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        userDaoDigester.setInputStream(new FileInputStream(DAOTestBase.SQL_DIR + "useraccount_dao.xml"));
        try {
            userDaoDigester.run();

        } catch (SAXException saxe) {
            saxe.printStackTrace();
        }
    }

    public void testQuery() throws Exception {
        // note that findAll was selected because the Postgres and Oracle XML
        // files are likely to have the exact same code for this query
        assertEquals(userDaoDigester.getQuery("findAll"), "SELECT * FROM USER_ACCOUNT");
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

    }
}
