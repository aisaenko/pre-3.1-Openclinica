/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package test.org.akaza.openclinica.dao.core;

import java.io.FileInputStream;
import java.util.HashMap;

import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.xml.sax.SAXException;

/**
 * @author thickerson
 *
 * 
 */
public class AuditableEntityDAOTest extends DAOTestBase {
	private AuditableEntityDAO aedao;

	
	private DAODigester digester = new DAODigester();
	
	public AuditableEntityDAOTest(String name) {
		super(name);
	}
	
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(AuditableEntityDAOTest.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		digester.setInputStream(new FileInputStream(SQL_DIR + "study_group_dao.xml"));
		
		try {
			digester.run();
		} catch (SAXException saxe) {
			saxe.printStackTrace();
		}
		
		super.ds = setupTestDataSource();
		
		aedao = new StudyGroupDAO(super.ds, digester);
	}
	
	public void testSelectSuccess() throws Exception {
		aedao.setTypesExpected();
		aedao.select(digester.getQuery("findAll"));
		assertEquals("success flag set", aedao.isQuerySuccessful(), true);
	}
	
	public void testSelectFailure() throws Exception {
		aedao.select("SELECT * FROM invalid_table");
		assertEquals("failure flag set", aedao.isQuerySuccessful(), false);
		assertNotNull("SQLException set", aedao.getFailureDetails());
	}
	
	public void testInsertFailure() throws Exception {
		aedao.execute("INSERT INTO invalid_table", new HashMap());
		assertEquals("failure flag set", aedao.isQuerySuccessful(), false);
		assertNotNull("SQLException set", aedao.getFailureDetails());		
	}
}