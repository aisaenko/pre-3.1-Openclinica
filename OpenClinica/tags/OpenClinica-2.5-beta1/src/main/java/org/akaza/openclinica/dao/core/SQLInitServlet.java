/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.core;

import org.akaza.openclinica.bean.core.Role;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * <P>
 * <b>SqlInitServlet.java </b>, servlet designed to run on startup, gathers all
 * the SQL queries and stores them in memory. Runs the static object SqlFactory,
 * which reads the properties file and then processes all the DAO-based XML
 * files.
 *
 * @author thickerson
 *
 *
 */
public class SQLInitServlet extends HttpServlet {

    public static String PROPERTY_DIR = "";

    private ServletContext context;

    // private static final String PROPERTY_FILE =
    // System.getProperty("catalina.home") + File.separator
    // + "webapps" + File.separator + "OpenClinica" +
    // File.separator + "properties" + File.separator
    // + "datainfo.properties";

    private static Properties params = new Properties();

    private static Properties facParams = new Properties();

    // YW << for enterprise.properties >> YW
    private static Properties entParams = new Properties();

    /*
     * private FileInputStream getPropertiesFile() throws IOException {
     * FileInputStream f;
     *
     * try { f = new FileInputStream(getFileName(dir1)); } catch (Exception e1) {
     * try { f = new FileInputStream(getFileName(dir2)); } catch (Exception e2) {
     * try { f = new FileInputStream(getFileName(dir3)); } catch (Exception e3) {
     * f = new FileInputStream(getFileName(dirFinal)); } } }
     *
     * return f; }
     */

    @Override
    public void init() throws ServletException {
        context = getServletContext();
        PROPERTY_DIR = context.getInitParameter("propertiesDir");
        try {
            params.load(new FileInputStream(PROPERTY_DIR + "datainfo.properties"));
            //facParams.load(new FileInputStream(PROPERTY_DIR + "facilityinfo.properties"));
            entParams.load(new FileInputStream(PROPERTY_DIR + "enterprise.properties"));
            String dbName = params.getProperty("dataBase");
            SQLFactory factory = SQLFactory.getInstance();
            factory.run(dbName);

            // Internationalized Role descriptions are taken from
            // terms.properties now. (package org.akaza.openclinica.i18n)
            // If you change descriptions in facilityinfo.properties, be sure to
            // include the appropiate
            // internationalized versions in terms.properties
            Role.COORDINATOR.setDescription(getField("coordinator"));
            Role.STUDYDIRECTOR.setDescription(getField("director"));
            Role.INVESTIGATOR.setDescription(getField("investigator"));
            Role.RESEARCHASSISTANT.setDescription(getField("ra"));
            Role.MONITOR.setDescription(getField("monitor"));

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Gets a field value from properties by its key name
     *
     * @param key
     * @return String The value of field
     */
    public static String getField(String key) {
        // BWP 01/08>>
        String name = params.getProperty(key);
        if (name != null) {
            name = name.trim();
        }
        // >>
        return name == null ? "" : name;
    }

    /**
     * Gets the supportURL value from properties by its key name
     *
     * @return String The value of supportURL key
     */
    public static String getSupportURL() {
        // BWP 01/08>>
        String name = params.getProperty("supportURL");
        return name == null ? "" : name.trim();
        // <<
    }

    /**
     * Gets a field value from properties by its key name
     *
     * @param key
     * @return String The value of field
     */
    /* 
     public static String getField(String key, boolean isFacility) {
        String name = null;
        if (isFacility) {
            name = facParams.getProperty(key).trim();
        } else {
            name = params.getProperty(key).trim();
        }
        return name == null ? "" : name;
    }
    */
    /**
     * Gets a field value by its key name from the enterprise.properties file
     *
     * @param key
     * @return String The value of field
     */
    public static String getEnterpriseField(String key) {
        String name = null;
        name = entParams.getProperty(key).trim();
        return name == null ? "" : name;
    }

    public static String getDBName() {
        String name = params.getProperty("dataBase");
        return name;
    }

}
