/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package test.org.akaza.openclinica.dao.submit;

import java.io.FileInputStream;
import java.util.*;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.xml.sax.SAXException;

import test.org.akaza.openclinica.dao.core.DAOTestBase;

/**
 * @author thickerson
 */
public class ItemDAOTest extends DAOTestBase {
	private ItemDAO idao;
	
	private DAODigester digester = new DAODigester();
	
	public ItemDAOTest(String name) {
		super(name);
	}
	
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(ItemDAOTest.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		digester.setInputStream(new FileInputStream(SQL_DIR + "item_dao.xml"));
		
		try {
			digester.run();
		} catch (SAXException saxe) {
			saxe.printStackTrace();
		}
		
		
		super.ds = setupTestDataSource();
		
		//super.ds = setupOraDataSource();
		//super.ds = setupDataSource();
		idao = new ItemDAO(super.ds, digester);
	}
	
	public void testFindAll() throws Exception {
		Collection col = idao.findAll();
		assertNotNull("findAll", col);
	}
	
	public void testFindByPK() throws Exception {
		Collection col = idao.findAll();
		Iterator it = col.iterator();
		int pkey = 0;
		if (it.hasNext()) {
			ItemBean testMe = (ItemBean)it.next();
			pkey = testMe.getId();
		}
		ItemBean ib = (ItemBean)idao.findByPK(pkey);
		System.out.println("Found by primary key "+pkey+" name: "+ib.getName());
		assertNotNull("findByPK", ib);
	}
	
	public void testUpdate() throws Exception {
		Collection col = idao.findAll();
		Iterator it = col.iterator();
		int pkey = 0;
		if (it.hasNext()) {
			ItemBean testMe = (ItemBean)it.next();
			pkey = testMe.getId();
		}
		ItemBean ib = (ItemBean)idao.findByPK(pkey);
		System.out.println("Found by primary key "+pkey+" name: "+ib.getName());
		String keepDesc = ib.getDescription();
		ib.setDescription("foo fighters deluxe");
		ib = (ItemBean)idao.update(ib);
		ib.setDescription(keepDesc);
		ib = (ItemBean)idao.update(ib);
		int equals = 0;
		//how to make strings match with assert equals...?
		if (keepDesc.equals(ib.getDescription())) {
			equals = 1;
		}
		assertEquals("test Update", keepDesc, ib.getDescription());
		assertEquals("test Update three", equals, 1);
	}
	
	public void testInsert() throws Exception {
		ItemBean ib = new ItemBean();
		ib.setName("Delete Me Later");
		ib.setDescription("item created to test use case only");
		ib.setUnits("mg");
		ib.setStatusId(1);
		ib.setStatus(Status.get(ib.getStatusId()));
		ib.setItemDataTypeId(1);
		ib.setItemReferenceTypeId(1);
		ib.setOwnerId(1);
		ib = (ItemBean)idao.create(ib);
		assertNotNull("test Insert", ib);
	}
	
	public void testFindAllByParentIdAndCRFVersionIdSuccess() throws Exception {
		ArrayList answer = idao.findAllByParentIdAndCRFVersionId(651, 101);
		
		assertNotNull("well-formed answer", answer);
		assertEquals("correct number of rows", answer.size(), 2);
		
		ItemBean ib = (ItemBean) answer.get(0);
		assertNotNull("well formed bean", ib);
		assertTrue("valid bean", ib.isActive());
		assertTrue("correct bean", (ib.getId() == 402) || (ib.getId() == 152));
	}	

	public void testFindAllByParentIdAndCRFVersionIdFailureWrongPID() throws Exception {
		ArrayList answer = idao.findAllByParentIdAndCRFVersionId(-1, 101);
		
		assertNotNull("well-formed answer", answer);
		assertEquals("correct number of rows", answer.size(), 0);
	}

	public void testFindAllByParentIdAndCRFVersionIdFailureWrongCRFVID() throws Exception {
		ArrayList answer = idao.findAllByParentIdAndCRFVersionId(651, -1);
		
		assertNotNull("well-formed answer", answer);
		assertEquals("correct number of rows", answer.size(), 0);
	}
	
	public void testFindAllParentsBySectionIdSuccess() throws Exception {
		ArrayList answer = idao.findAllParentsBySectionId(203);
		
		assertNotNull("well formed answer", answer);
		assertEquals("correct number of rows", answer.size(), 36);
		
		ItemBean ib = (ItemBean) answer.get(0);
		assertTrue("valid bean", ib.isActive());
		assertEquals("correct bean", ib.getId(), 272);
	}

	public void testFindAllParentsBySectionIdFailure() throws Exception {
		ArrayList answer = idao.findAllParentsBySectionId(-1);
		
		assertNotNull("well formed answer", answer);
		assertEquals("correct number of rows", answer.size(), 0);
	}
	
	public void testFindAllActiveByCRF() {
	  CRFBean crf = new CRFBean();
	  crf.setId(36);
	  ArrayList items = idao.findAllActiveByCRF(crf);
	}
}