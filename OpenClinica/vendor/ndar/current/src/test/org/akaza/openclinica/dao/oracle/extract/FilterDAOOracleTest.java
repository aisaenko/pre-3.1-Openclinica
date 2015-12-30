/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package test.org.akaza.openclinica.dao.oracle.extract;

/**<P>FilterDAOOracleTest.java, by Tom Hickerson
 * <P>Using an oracle connection and Oracle SQL, we are testing the FilterDAO for
 * its functionality.
 * @author thickerson
 * 
 */
import test.org.akaza.openclinica.dao.core.*;

import org.akaza.openclinica.dao.core.*;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.dao.extract.FilterDAO;
import org.akaza.openclinica.bean.extract.*;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

public class FilterDAOOracleTest extends DAOTestBase {
	private FilterDAO fdao;
	private DAODigester digester = new DAODigester();
	
	public FilterDAOOracleTest(String name) {
		super(name);
	}
	
	public static void main(String[] args) {
	    junit.swingui.TestRunner.run(DatasetDAOTest.class);
	  }
	
	protected void setUp() throws Exception {
		super.setUp();
		digester.setInputStream(new FileInputStream(SQL_DIR + "oracle_filter_dao.xml"));
		try {
		     digester.run();
		} catch (SAXException saxe) {
		     saxe.printStackTrace();
		}


		super.ds = setupOracleDataSource();//setupTestDataSource();


		fdao = new FilterDAO(super.ds, digester);
	}
	protected void tearDown() throws Exception {
		shutdownDataSource(ds);
		super.tearDown();
	}
	public void testFindAll() throws Exception {
	    Collection col = fdao.findAll();
	    assertNotNull("findAll", col);
	  }
	
//	public void testCreate() throws Exception {
//		FilterBean db = new FilterBean();
//		db.setName("Next Test Filter again");
//		db.setDescription("Next Test filter, please delete");
//		
//		db.setSQLStatement("select * from test_view_three"+
//				" where "+
//				"item_id = 6");
//		
//		db.setOwnerId(1);
//		db.setStatus(Status.AVAILABLE);
//		db = (FilterBean)fdao.create(db);
//		assertNotNull("test for create", ds);
//	}
	//above was removed so that we wouldn't create too much
	//trash data in the database, tbh
	
	public void testFindByPK() throws Exception {
	  	Collection col = fdao.findAll();
	  	Iterator it = col.iterator();
	  	int pkey = 0;
	  	if (it.hasNext()) {
	  		FilterBean testMe = (FilterBean)it.next();
	  		pkey = testMe.getId();
	  	}
	  	FilterBean ecb = (FilterBean)fdao.findByPK(pkey);
	  	System.out.println("Found by primary key "+pkey+" desc: "+
	  			ecb.getDescription());
	  	assertNotNull("findByPK", ecb);
	  }
	
//	public void testUpdate() throws Exception {
//		Collection col = fdao.findAll();
//	  	Iterator it = col.iterator();
//	  	int pkey = 0;
//	  	if (it.hasNext()) {
//	  		FilterBean testMe = (FilterBean)it.next();
//	  		pkey = testMe.getId();
//	  	}
//	  	FilterBean ecb = (FilterBean)fdao.findByPK(pkey);
//	  	System.out.println("Found by primary key "+pkey+" desc: "+
//	  			ecb.getDescription());
//	  	String keepName = ecb.getName();
//	  	String keepDesc = ecb.getDescription();
//	  	ecb.setName("Test Updater One");
//	  	ecb.setDescription("Test Desc Two");
//	  	UserAccountBean updater = new UserAccountBean();
//	  	updater.setId(1);
//	  	ecb.setUpdater(updater);
//	  	
//	  	ecb = (FilterBean)fdao.update(ecb);
//	  	ecb.setName(keepName);
//	  	ecb.setDescription(keepDesc);
//	  	FilterBean ecb2 = (FilterBean)fdao.update(ecb);
//	  	assertNotNull("test update", ecb2);
//	}
	
	public void testGenSQLStatement() throws Exception {
		ArrayList al = new ArrayList();
		FilterObjectBean one = new FilterObjectBean();
		FilterObjectBean two = new FilterObjectBean();
		FilterObjectBean three = new FilterObjectBean();
		one.setItemId(2);
		one.setOperand(" and ");
		one.setValue("yes");
		al.add(one);
		two.setItemId(2);
		two.setOperand(" or ");
		two.setValue("deleteme");
		al.add(two);
		three.setItemId(3);
		three.setOperand(" and ");
		three.setValue("19");
		al.add(three);
		String originalStatement = "select distinct item_id, item_name, value from test_view_three "+ 
			"where "+
			"(crf_version_id in (2) "+
			"and "+
			"study_event_id in (1,2,3,4,5,6,7,8,40)) "+ 
			"and "+
			"(date_created >= '01-JAN-90') "+ 
			"and "+
			"(date_created <= '30-NOV-04')";
		String newQuery = 
			fdao.genSQLStatement(originalStatement,
					"and",
					al);
		System.out.println(newQuery);
		String newnewQuery = 
			fdao.genSQLStatement(originalStatement,
					"or",
					al);
		System.out.println(newnewQuery);
		assertNotNull(newQuery);
		assertNotNull(newnewQuery);
	}
}
