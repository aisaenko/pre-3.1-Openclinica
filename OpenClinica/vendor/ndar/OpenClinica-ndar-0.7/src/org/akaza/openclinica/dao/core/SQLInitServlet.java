/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.core;

import java.io.File;
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
  
  private static final String LDAP_PROVIDER_URL = "ldap-provider-url";
  private static final String LDAP_BASE_DN = "ldap-base-dn";
  private static final String SECURITY_AUTHENTICATION = "security-authentication";
  private static final String SECURITY_PRINCIPAL = "security-principal";
  private static final String SECURITY_CREDENTIALS = "security-credentials";
  private static final String GUID_GENERATION = "guid-generation";
  private static final String GUIDWS_URL = "guidws-url";
  private static final String DEFAULT_BASE_DN = "ou=persons, ou=ndar, ou=nih, o=gov";
  private static final String POLICY_LOCATION = "policy-location";
  private static final String REPOSITORY_LOCATION = "repository-location";
  private static final String PRIVATE_KEY_ALIAS = "private-key-alias";
  private static final String PRIVATE_KEY_PASSWORD = "private-key-password";
  
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
    /**
     * Load the parameters for LDAP server.
     */
    params.put(LDAP_PROVIDER_URL, getInitParameter(LDAP_PROVIDER_URL));
    if (getInitParameter(LDAP_BASE_DN) != null) {
        params.put(LDAP_BASE_DN, getInitParameter(LDAP_BASE_DN));
    }else{
        params.put(LDAP_BASE_DN, DEFAULT_BASE_DN);	
    }
    params.put(SECURITY_AUTHENTICATION, getInitParameter(SECURITY_AUTHENTICATION));
    params.put(SECURITY_PRINCIPAL, getInitParameter(SECURITY_PRINCIPAL));
    params.put(SECURITY_CREDENTIALS, getInitParameter(SECURITY_CREDENTIALS));
    
    PROPERTY_DIR = getInitParameter("propertiesDir");
    params.put(GUID_GENERATION, getInitParameter(GUID_GENERATION));
    
    params.put(GUIDWS_URL, getInitParameter(GUIDWS_URL));
    
    String repoLoc = getInitParameter(REPOSITORY_LOCATION);
    if(repoLoc != null){
	params.put(REPOSITORY_LOCATION, context.getRealPath(repoLoc));
    }
    
    String policyLoc = getInitParameter(POLICY_LOCATION);
    if(policyLoc != null){
	params.put(POLICY_LOCATION, context.getRealPath(policyLoc));
    }
    
    String privateKeyAlias = getInitParameter(PRIVATE_KEY_ALIAS);
    if(privateKeyAlias != null){
	params.put(PRIVATE_KEY_ALIAS, privateKeyAlias);
    }
    
    String privateKeyPassword = getInitParameter(PRIVATE_KEY_PASSWORD);
    if(privateKeyPassword != null){
	params.put(PRIVATE_KEY_PASSWORD, privateKeyPassword);
    }
    
    if (PROPERTY_DIR==null) {
    	PROPERTY_DIR=context.getRealPath("/properties") + File.separator;
    }
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
    String name = params.getProperty(key);
    return (name == null ? "" : name.trim());
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
  
  public static String getLdapProviderURL(){
      return params.getProperty(LDAP_PROVIDER_URL);
  }
  
  public static String getLdapBaseDN(){
      return params.getProperty(LDAP_BASE_DN);
  }
  
  public static String getSecurityAuthentication(){
      return params.getProperty(SECURITY_AUTHENTICATION);
  }
  
  public static String getSecurityPrincipal(){
      return params.getProperty(SECURITY_PRINCIPAL);
  }
  
  public static String getSecurityCredentials(){
      return params.getProperty(SECURITY_CREDENTIALS);
  }
  
  public static String getGuidGeneration(){
      return params.getProperty(GUID_GENERATION);
  }
  
  public static String getGuidWSURL(){
      return params.getProperty(GUIDWS_URL);
  }
  
  public static String getRepositoryLocation(){
      return params.getProperty(REPOSITORY_LOCATION);
  }
  
  public static String getPolicyLocation(){
      return params.getProperty(POLICY_LOCATION);
  }
  
  public static String getPrivateKeyAlias(){
      return params.getProperty(PRIVATE_KEY_ALIAS);
  }
  
  public static String getPrivateKeyPassword(){
      return params.getProperty(PRIVATE_KEY_PASSWORD);
  }
}