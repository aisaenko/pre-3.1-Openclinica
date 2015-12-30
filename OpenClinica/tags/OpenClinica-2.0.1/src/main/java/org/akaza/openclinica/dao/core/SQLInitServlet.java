/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.akaza.openclinica.bean.core.Role;

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

  //  private static final String PROPERTY_FILE =
  //    System.getProperty("catalina.home") + File.separator
  //      + "webapps" + File.separator + "OpenClinica" +
  //      File.separator + "properties" + File.separator
  //      + "datainfo.properties";

  private static Properties params = new Properties();

  private static Properties facParams = new Properties();

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

  public void init() throws ServletException {
    context = getServletContext();
    PROPERTY_DIR = context.getInitParameter("propertiesDir");
    try {
      params.load(new FileInputStream(PROPERTY_DIR + "datainfo.properties"));
      facParams.load(new FileInputStream(PROPERTY_DIR + "facilityinfo.properties"));
      String dbName = params.getProperty("dataBase");
      SQLFactory factory = SQLFactory.getInstance();
      factory.run(dbName);

      Role.COORDINATOR.setDescription(getField("coordinator",true));
      Role.STUDYDIRECTOR.setDescription(getField("director",true));
      Role.INVESTIGATOR.setDescription(getField("investigator",true));
      Role.RESEARCHASSISTANT.setDescription(getField("ra",true));

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
    String name = params.getProperty(key).trim();
    return (name == null ? "" : name);
  }

  /**
   * Gets a field value from properties by its key name
   * 
   * @param key
   * @return String The value of field
   */
  public static String getField(String key, boolean isFacility) {
    String name=null;
    if (isFacility) {
      name = facParams.getProperty(key).trim();
    } else {
      name = params.getProperty(key).trim();
    }
    return (name == null ? "" : name);
  }
}