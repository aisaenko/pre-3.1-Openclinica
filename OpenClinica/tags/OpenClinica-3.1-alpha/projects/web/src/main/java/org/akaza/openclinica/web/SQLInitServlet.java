/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.web;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.SpringServletAccess;

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

    private ServletContext context;
    private static Properties params = new Properties();
    private static Properties entParams = new Properties();

    @Override
    public void init() throws ServletException {
        context = getServletContext();

        params = (Properties) SpringServletAccess.getApplicationContext(context).getBean("dataInfo");
        entParams = (Properties) SpringServletAccess.getApplicationContext(context).getBean("enterpriseInfo");

        Role.COORDINATOR.setDescription(getField("coordinator"));
        Role.STUDYDIRECTOR.setDescription(getField("director"));
        Role.INVESTIGATOR.setDescription(getField("investigator"));
        Role.RESEARCHASSISTANT.setDescription(getField("ra"));
        Role.MONITOR.setDescription(getField("monitor"));

    }

    /**
     * Gets a field value from properties by its key name
     *
     * @param key
     * @return String The value of field
     */
    public static String getField(String key) {
        String name = params.getProperty(key);
        if (name != null) {
            name = name.trim();
        }
        return name == null ? "" : name;
    }

    /**
     * Gets the supportURL value from properties by its key name
     *
     * @return String The value of supportURL key
     */
    public static String getSupportURL() {
        String name = params.getProperty("supportURL");
        return name == null ? "" : name.trim();
    }

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

    /**
     * We return empty String if DBName is not found in params. 
     * The only reason why this is done this way is for unit testing 
     * to work properly. 
     * 
     * EntityDAO uses SQLInitServlet.getDBName().equals("oracle") , This works 
     * fine in the Servlet environment because of this class but in a unit test 
     * it does not
     * 
     * @author Krikor Krumlian the return portion
     * 
     */
    public static String getDBName() {
        String name = params.getProperty("dataBase");
        return name == null ? "" : name;
    }

}