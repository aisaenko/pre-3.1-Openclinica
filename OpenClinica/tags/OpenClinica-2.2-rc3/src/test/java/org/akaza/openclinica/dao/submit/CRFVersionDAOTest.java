/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package test.org.akaza.openclinica.dao.submit;

import java.io.FileInputStream;
import java.util.ArrayList;

import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.xml.sax.SAXException;

import test.org.akaza.openclinica.dao.core.DAOTestBase;

/**
 * @author jxu
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CRFVersionDAOTest extends DAOTestBase {
	private CRFVersionDAO cdao;
	
	private DAODigester digester = new DAODigester();
	
	public CRFVersionDAOTest(String name) {
		super(name);
	}
	
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(CRFVersionDAOTest.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		digester.setInputStream(new FileInputStream(SQL_DIR + "crfversion_dao.xml"));
		
		try {
			digester.run();
		} catch (SAXException saxe) {
			saxe.printStackTrace();
		}
		

		super.ds = setupTestDataSource();

		
		cdao = new CRFVersionDAO(super.ds, digester);
	}
	
	public void testFindByPKSuccess() throws Exception {
		int pk = 1;
		CRFVersionBean cvb = (CRFVersionBean) cdao.findByPK(pk);
		
		assertNotNull("bean valid", cvb);
		assertTrue("id set", cvb.isActive());
		assertEquals("correct id", cvb.getId(), pk);
	}
	
	
	public void testFindByPKFailure() throws Exception {
		int pk = -1;
		CRFVersionBean cvb = (CRFVersionBean) cdao.findByPK(pk);
		
		assertNotNull("bean valid", cvb);
		assertTrue("id not set", !cvb.isActive());
	}
	
	public void testFindItemFromMap() throws Exception { 		  	
		
		ArrayList sb = (ArrayList) cdao.findItemFromMap(16);
		//assertEquals("test items", sb.size(),0);
	}
	

	public void testFindAllByCRFSuccess() throws Exception { 		
		int crfid = 1;
		ArrayList sb = (ArrayList) cdao.findAllByCRF(crfid);

		assertNotNull("rows valid", sb);
		assertTrue("at least one row", sb.size() > 0);
		
		CRFVersionBean c = (CRFVersionBean) sb.get(0);
		
		assertNotNull("bean valid", c);
		assertTrue("id set", c.isActive());
		assertEquals("correct bean found", c.getCrfId(), crfid);

	}
	
	public void testFindAllByCRFFailure() throws Exception { 		
		int crfid = -1;
		ArrayList sb = (ArrayList) cdao.findAllByCRF(crfid);

		assertNotNull("rows valid", sb);
		assertEquals("no rows", sb.size(), 0);
	}

	public void testGetCRFIdFromCRFVersionIdSuccess() throws Exception {
		int id = cdao.getCRFIdFromCRFVersionId(1);
		assertEquals("got correct id", id, 1);
	}
	
	public void testGetCRFIdFromCRFVersionIdFailure() throws Exception {
		int id = cdao.getCRFIdFromCRFVersionId(-1);
		assertEquals("got correct answer", id, 0);
	}

	public void testFindAllByCRFIdSuccess() throws Exception {
		ArrayList rows = cdao.findAllByCRFId(1);

		assertNotNull("rows valid", rows);
		assertEquals("correct number of rows", rows.size(), 1);

		CRFVersionBean cvb = (CRFVersionBean) rows.get(0);
		
		assertNotNull("well-formed beans", cvb);
		assertTrue("bean id set", cvb.isActive());
		assertEquals("got correct bean", cvb.getId(), 1);
	}

	public void testFindAllByCRFIdFailure() throws Exception {

		ArrayList rows = cdao.findAllByCRFId(-1);

		assertNotNull("rows valid", rows);
		assertEquals("no rows", rows.size(), 0);
	}
}