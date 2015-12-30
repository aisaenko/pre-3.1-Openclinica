/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.core;

import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.apache.commons.dbcp.BasicDataSource;

import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;
import java.util.Properties;

import javax.sql.DataSource;

import junit.framework.TestCase;

/**
 * @author jxu
 *
 * <P>
 * Base class of all the other Dao test classes
 *
 * <P>
 * Note that this class has been updated to access the postgres database, tbh
 *
 * @version CVS: $Id: DAOTestBase.java 10902 2008-04-10 18:53:11Z kkrumlian $
 */
public abstract class DAOTestBase extends TestCase {

    protected long startTime;

    protected DataSource ds;

    // public final static String SQL_DIR = "C:" +
    // File.separator + "Program Files" +
    // File.separator + "Eclipse" +
    // File.separator + "workspace" +
    // File.separator + "OpenClinica" +
    // File.separator + "webapp" +
    // File.separator + "properties" +
    // File.separator;
    // TRY NOT TO COMMIT THIS FILE -- NEED TO RESOLVE LOCAL DIRECTORY

    /*
     * public final static String SQL_DIR = File.separator + "usr" +
     * File.separator + "share" + File.separator + "Anthill" + File.separator +
     * "work" + File.separator + "OpenClinica" + File.separator + "webapp" +
     * File.separator + "properties" + File.separator;
     */
    private int testing = 1;

    // public final static String SQL_DIR = "C:" +
    // File.separator + "work" +
    // File.separator + "Tomcat 5.0" +
    // File.separator + "webapps" +
    // File.separator + "OpenClinica" +
    // File.separator + "webapp" +
    // File.separator + "properties" +
    // File.separator;
    // //TRY NOT TO COMMIT THIS FILE -- NEED TO RESOLVE LOCAL DIRECTORY
    // public final static String SQL_DIR =
    // "C:\\work\\eclipse\\workspace\\OpenClinica\\webapp\\properties\\";

    private static final String WEB_APP_PATH = "src" + File.separator + "main" + File.separator + "webapp";
    public final static String SQL_DIR = WEB_APP_PATH + File.separator + "properties" + File.separator;
    // This path is used to connect with a properties file that contains
    // database connection
    // information and test parameters
    private static final String PROPERTIES_PATH = "src" + File.separator + "test" + File.separator + "java";

    /*
     * public final static String SQL_DIR = File.separator + "usr" +
     * File.separator + "share" + File.separator + "Anthill" + File.separator +
     * "work" + File.separator + "OpenClinica" + File.separator + "webapp" +
     * File.separator + "properties" + File.separator;
     */

    /**
     * Default construct
     *
     * @param name
     */
    public DAOTestBase(String name) {
        super(name);
    }

    public static DataSource setupTestDataSource() {
        return setupTestPostgresDataSource();
        // return setupOracleDataSource();
    }

    public static DataSource setupTestPostgresDataSource() {
        // use a properties file for this
        String baseDir = System.getProperty("basedir");
        Properties properties = DAOTestBase.getTestProperties();

        // Set current language preferences
        Locale locale = new Locale(properties.getProperty("locale"));
        ResourceBundleProvider.updateLocale(locale);

        BasicDataSource ds = new BasicDataSource();
        ds.setAccessToUnderlyingConnectionAllowed(true);
        ds.setDriverClassName(properties.getProperty("driverClassName"));
        ds.setUsername(properties.getProperty("username"));
        ds.setPassword(properties.getProperty("password"));
        ds.setUrl(properties.getProperty("url"));
        return ds;
    }

    public static DataSource setupOracleDataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        ds.setUsername("openclinicadev");
        ds.setPassword("openclinicadev");
        ds.setUrl("jdbc:oracle:thin:@localhost:1521:openclinicadev");
        return ds;
    }

    public static void shutdownDataSource(DataSource ds) throws Exception {
        BasicDataSource bds = (BasicDataSource) ds;
        bds.close();
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        startTime = System.currentTimeMillis();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void shutDown() throws Exception {
        shutdownDataSource(ds);
        super.tearDown();
    }

    /**
     * Tests finding all records from a table
     *
     * @throws Exception
     */
    public void testFindAll() throws Exception {

    }

    @Override
    protected void tearDown() throws Exception {
        System.out.println("execution time: " + (System.currentTimeMillis() - startTime));
        super.tearDown();
    }

    public static Properties getTestProperties() {
        // use a properties file for this
        String baseDir = System.getProperty("basedir");
        Properties properties = new Properties();

        if (baseDir == null || "".equalsIgnoreCase(baseDir)) {
            baseDir = System.getProperty("user.dir");
        }
        if (baseDir == null || "".equalsIgnoreCase(baseDir)) {
            throw new IllegalStateException(
                    "The system properties basedir or user.dir were not made available to the application. Therefore we cannot locate the test properties file.");
        }

        try {

            properties.load(new FileInputStream(new File(baseDir + File.separator + PROPERTIES_PATH + File.separator + "servlet_test.properties")));

        } catch (Exception ioExc) {
            ioExc.printStackTrace();

        }
        return properties;
    }

}
