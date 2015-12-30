/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package test.org.akaza.openclinica.dao.core;

import java.io.File;

import javax.sql.DataSource;
import org.akaza.openclinica.dao.core.EntityDAO;
import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * @author jxu
 *
 * <P>Base class of all the other Dao test classes
 * 
 * <P>Note that this class has been updated to access the postgres database, tbh
 * 
 * @version CVS: $Id: DAOTestBase.java,v 1.26 2005/08/01 20:55:23 jxu Exp $ 
 */
public abstract class DAOTestBase extends TestCase {

  protected long startTime;
  
  protected DataSource ds;

//  public final static String SQL_DIR = "C:" + 
//  File.separator + "Program Files" +
//  File.separator + "Eclipse" + 
//  File.separator + "workspace" + 
//  File.separator + "OpenClinica" + 
//  File.separator + "webapp" + 
//  File.separator + "properties" + 
//  File.separator;
  //TRY NOT TO COMMIT THIS FILE -- NEED TO RESOLVE LOCAL DIRECTORY 


 /* public final static String SQL_DIR = File.separator + "usr" +
  	File.separator + "share" +
	File.separator + "Anthill" +
	File.separator + "work" +
	File.separator + "OpenClinica" +
	File.separator + "webapp" +
	File.separator + "properties" + File.separator;
*/
  private int testing = 1;
  
//  public final static String SQL_DIR = "C:" + 
//  File.separator + "work" +
//  File.separator + "Tomcat 5.0" + 
//  File.separator + "webapps" + 
//  File.separator + "OpenClinica" + 
//  File.separator + "webapp" + 
//  File.separator + "properties" + 
//  File.separator;
//  //TRY NOT TO COMMIT THIS FILE -- NEED TO RESOLVE LOCAL DIRECTORY 
//  public final static String SQL_DIR = 
//  	"C:\\work\\eclipse\\workspace\\OpenClinica\\webapp\\properties\\";



  public final static String SQL_DIR = File.separator + "usr" +
  	File.separator + "share" +
	File.separator + "Anthill" +
	File.separator + "work" +
	File.separator + "OpenClinica" +
	File.separator + "webapp" +
	File.separator + "properties" + File.separator;


  /**
   * Default construct
   * @param name
   */
  public DAOTestBase(String name) {
	super(name);
  }

  
  public static DataSource setupTestDataSource() {
  	return setupTestPostgresDataSource();
  	//return setupOracleDataSource();
  }

  public static DataSource setupTestPostgresDataSource() {
    BasicDataSource ds = new BasicDataSource();
    ds.setDriverClassName("org.postgresql.Driver");
    ds.setUsername("clinica");//WAS postgres
    ds.setPassword("clinica");//WAS *clinica*
    //ds.setUrl("jdbc:postgresql://localhost:5432/openclinica");
    //WAS 192.168.1.103:5432/OPENCLINICADEV
    ds.setUrl("jdbc:postgresql://192.168.15.103:5432/openclinicadev");
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
  protected void setUp() throws Exception {
    super.setUp();
    System.out.println("running test: " + getName());
    //now, pull the directory from the database
    System.out.println("SQL dir "+SQL_DIR);
    //EntityDAO getPath = new EntityDAO(this.ds);
    //System.out.println("SQL dir "+SQL_DIR);
    //System.out.println("found catalina: "+System.getProperty("catalina.home"));
    //System.out.println("SQL dir "+SQL_DIR);
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
   * @throws Exception
   */
  public void testFindAll() throws Exception {
    
  }
  
  protected void tearDown() throws Exception {
  	System.out.println("execution time: " + (System.currentTimeMillis() - startTime));
  	super.tearDown();
  }

}
