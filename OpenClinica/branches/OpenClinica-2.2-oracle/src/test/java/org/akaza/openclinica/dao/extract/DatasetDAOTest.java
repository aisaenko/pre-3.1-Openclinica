/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package test.org.akaza.openclinica.dao.extract;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.xml.sax.SAXException;

import test.org.akaza.openclinica.dao.core.DAOTestBase;
/**
 * @author thickerson
 *
 * 
 */
public class DatasetDAOTest extends DAOTestBase {
	private DatasetDAO dsdao;
	private DAODigester digester = new DAODigester();
	
	public DatasetDAOTest(String name) {
		super(name);
	}
	
	public static void main(String[] args) {
	    junit.swingui.TestRunner.run(DatasetDAOTest.class);
	  }
	
	protected void setUp() throws Exception {
		System.out.println("Found SQL directory: "+SQL_DIR);
		super.setUp();
		digester.setInputStream(new FileInputStream(SQL_DIR + "dataset_dao.xml"));
		try {
		     digester.run();
		} catch (SAXException saxe) {
		     saxe.printStackTrace();
		}


		super.ds = setupTestDataSource();


		dsdao = new DatasetDAO(super.ds, digester);
	}
	protected void tearDown() throws Exception {
		shutdownDataSource(ds);
		super.tearDown();
	}
	public void testFindAll() throws Exception {
	    Collection col = dsdao.findAll();
	    assertNotNull("findAll", col);
	  }
	
	public void testCreate() throws Exception {
		DatasetBean db = new DatasetBean();
		db.setName("Next Test Dataset again");
		db.setDescription("Next Test dataset, please delete");
		db.setStudyId(1);
		db.setSQLStatement("select * from test_view_three"+
				" where "+
				"item_id = 6");
		db.setNumRuns(0);
		//db.setRunTime(0);
		db.setOwnerId(1);
		db.setStatus(Status.AVAILABLE);
		db = (DatasetBean)dsdao.create(db);
		assertNotNull("test for create", ds);
	}
	//above was removed so that we wouldn't create too much
	//trash data in the database, tbh
	
	public void testFindByPK() throws Exception {
	  	Collection col = dsdao.findAll();
	  	Iterator it = col.iterator();
	  	int pkey = 0;
	  	if (it.hasNext()) {
	  		DatasetBean testMe = (DatasetBean)it.next();
	  		pkey = testMe.getId();
	  	}
	  	DatasetBean ecb = (DatasetBean)dsdao.findByPK(pkey);
	  	System.out.println("Found by primary key "+pkey+" desc: "+
	  			ecb.getDescription());
	  	assertNotNull("findByPK", ecb);
	  }
	
	public void testUpdate() throws Exception {
		Collection col = dsdao.findAll();
	  	Iterator it = col.iterator();
	  	int pkey = 0;
	  	DatasetBean ecb2 = new DatasetBean();
	  	if (it.hasNext()) {
	  		DatasetBean testMe = (DatasetBean)it.next();
	  		pkey = testMe.getId();
	  	} else {
	  		
	  	}
	  	DatasetBean ecb = (DatasetBean)dsdao.findByPK(pkey);
	  	System.out.println("Found by primary key "+pkey+" desc: "+
	  			ecb.getDescription());
	  	String keepName = ecb.getName();
	  	String keepDesc = ecb.getDescription();
	  	ecb.setName("Test Updater One");
	  	ecb.setDescription("Test Desc Two");
	  	//ecb.setUpdaterId(1);
	  	UserAccountBean uab = new UserAccountBean();
	  	uab.setId(1);
	  	ecb.setUpdater(uab);
	  	ecb.setApproverId(1);
	  	ecb.setNumRuns(3);
	  	ecb.setUpdatedDate(new Date());
	  	ecb.setDateLastRun(new Date());
	  	ecb = (DatasetBean)dsdao.update(ecb);
	  	ecb.setName(keepName);
	  	ecb.setDescription(keepDesc);
	  	ecb2 = (DatasetBean)dsdao.update(ecb);
	  	assertNotNull("test update", ecb2);
	}
	
	public void testExtract() throws Exception {
		Collection col = dsdao.findAll();
	  	Iterator it = col.iterator();
	  	int pkey = 0;
	  	while (it.hasNext()) {
	  		DatasetBean testMe = (DatasetBean)it.next();
	  		pkey = testMe.getId();
	  	}
	  	DatasetBean ecb = (DatasetBean)dsdao.findByPK(pkey);
	  	//String exb = dsdao.generateDataset(ecb,1, new StudyBean(), new StudyBean());
	  	//System.out.println("found data set: "+exb);
	  	//assertNotNull("Test with finding the bean",exb);
	}
	
	public void testGenerate() throws Exception {
		ArrayList versionIds = new ArrayList();
		versionIds.add(new Integer(1));
		versionIds.add(new Integer(2));
		versionIds.add(new Integer(3));
		versionIds.add(new Integer(4));
		versionIds.add(new Integer(5));
		String b = "7/26/01";
		String e = "12/10/04";
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
		Date begin = df.parse(b);
		Date end = df.parse(e);
		DatasetBean db = new DatasetBean();
		db.setDateEnd(end);
		db.setDateStart(begin);		
		System.out.println(db.generateQuery());
		assertNotNull("test generate query", db.generateQuery());
	}
}
