/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.core;

import junit.framework.TestCase;

/**
 * @author thickerson
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SqlFactoryTest extends TestCase {

    private SQLFactory factory = new SQLFactory();

    public SqlFactoryTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(SqlFactoryTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SQLFactory.setXMLDir(DAOTestBase.SQL_DIR);
        factory.run("postgres");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testFactory() throws Exception {
        assertNotNull("digester exists", factory.getDigester(SQLFactory.getInstance().DAO_USERACCOUNT));
    }

    public void testNullFactory() throws Exception {
        assertNull("digester does not exist", factory.getDigester("xxx"));
    }

}
