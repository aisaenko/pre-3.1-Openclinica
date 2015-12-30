/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package test.org.akaza.openclinica.dao.submit;

import java.io.FileInputStream;
import java.util.*;

import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.bean.submit.*;
import org.akaza.openclinica.bean.core.Status;

import org.akaza.openclinica.dao.submit.SectionDAO;
import org.xml.sax.SAXException;

import test.org.akaza.openclinica.dao.core.DAOTestBase;


/**
 * @author thickerson
 *
 * 
 */
public class SectionDAOTest extends DAOTestBase {
	private SectionDAO secdao;
	private DAODigester digester = new DAODigester();
	
	public SectionDAOTest(String name) {
		super(name);
	}
	
	public static void main(String[] args) {
	    junit.swingui.TestRunner.run(SectionDAOTest.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		digester.setInputStream(new FileInputStream(SQL_DIR + "section_dao.xml"));
		try {
		     digester.run();
		} catch (SAXException saxe) {
		     saxe.printStackTrace();
		}

		super.ds = setupTestDataSource();

		secdao = new SectionDAO(super.ds, digester);
	}
	
	
	
	public void testFindAll() throws Exception {
	    Collection col = secdao.findAll();
	    assertNotNull("findAll", col);
	}
	
	public void testCreate() throws Exception {
		SectionBean sb = new SectionBean();
		sb.setCRFVersionId(2);
		sb.setStatus(Status.AVAILABLE);
		sb.setLabel("Deleteme");
		sb.setTitle("Delete Me, Please");
		sb.setInstructions("test section to fill space only, please delete");
		sb.setSubtitle("delete");
		sb.setPageNumberLabel("A.1");
		sb.setOrdinal(1);
		sb.setParentId(0);
		sb.setOwnerId(1);
		sb = (SectionBean)secdao.create(sb);
		assertNotNull("test insert", sb);
	}
	
	public void testUpdate() throws Exception {
		Collection col = secdao.findAll();
	  	Iterator it = col.iterator();
	  	int pkey = 0;
	  	if (it.hasNext()) {
	  		SectionBean testMe = (SectionBean)it.next();
	  		pkey = testMe.getId();
	  	}
	  	SectionBean ecb = (SectionBean)secdao.findByPK(pkey);
	  	System.out.println("Found by primary key "+pkey+" desc: "+
	  			ecb.getInstructions());
	  	
	  	String keepDesc = ecb.getInstructions();
	  	ecb.setInstructions("Test Desc Deleteme");
	  	ecb.setUpdaterId(1);
	  	ecb = (SectionBean)secdao.update(ecb);
	  	
	  	ecb.setInstructions(keepDesc);
	  	SectionBean ecb2 = (SectionBean)secdao.update(ecb);
	  	assertNotNull("test update", ecb2);
	}
	
	public void testFindAllByCRFVersionIdSuccess() {
		ArrayList answer = secdao.findAllByCRFVersionId(101);
		
		assertNotNull("wellformed answer", answer);
		assertEquals("correct number of rows", answer.size(), 11);
		
		SectionBean sb = (SectionBean) answer.get(0);
		assertNotNull("wellformed bean", sb);
		assertTrue("valid bean", sb.isActive());
		assertEquals("correct bean", sb.getId(), 201);
		assertEquals("correct crf version", sb.getCRFVersionId(), 101);
	}

	public void testFindAllByCRFVersionIdFailure() {
		ArrayList answer = secdao.findAllByCRFVersionId(-1);
		
		assertNotNull("wellformed answer", answer);
		assertEquals("correct number of rows", answer.size(), 0);
	}
	
	public void testFindByPKSuccess() {
		SectionBean sb = (SectionBean) secdao.findByPK(201);
		assertNotNull("wellformed bean", sb);
		assertTrue("valid bean", sb.isActive());
		assertEquals("correct bean", sb.getId(), 201);
	}

	public void testFindByPKFailure() {
		SectionBean sb = (SectionBean) secdao.findByPK(-1);
		assertNotNull("wellformed bean", sb);
		assertTrue("invalid bean", !sb.isActive());
	}
	
	public void testFindNextSuccess() {
		EventCRFBean ecb = new EventCRFBean();
		SectionBean current = new SectionBean();
		
		ecb.setId(4);
		ecb.setCRFVersionId(101);
		current.setId(201);
		current.setOrdinal(1);
		
		SectionBean sb = (SectionBean) secdao.findNext(ecb, current);
		assertNotNull("wellformed bean", sb);
		assertTrue("valid bean", sb.isActive());
		assertEquals("correct bean", sb.getId(), 202);
	}

	public void testFindNextFailureWrongECBId() {
		EventCRFBean ecb = new EventCRFBean();
		SectionBean current = new SectionBean();
		
		ecb.setId(-1);
		ecb.setCRFVersionId(-1);
		current.setId(201);
		current.setOrdinal(1);
		
		SectionBean sb = (SectionBean) secdao.findNext(ecb, current);
		assertNotNull("wellformed bean", sb);
		assertTrue("invalid bean", !sb.isActive());
	}

	public void testFindNextFailureWrongSectionId() {
		EventCRFBean ecb = new EventCRFBean();
		SectionBean current = new SectionBean();
		
		ecb.setId(4);
		ecb.setCRFVersionId(101);
		current.setId(-1);
		current.setOrdinal(-1);
		
		SectionBean sb = (SectionBean) secdao.findNext(ecb, current);
		assertNotNull("wellformed bean", sb);
		assertTrue("invalid bean", !sb.isActive());
	}

	public void testFindNextFailureNoNextSection() {
		EventCRFBean ecb = new EventCRFBean();
		SectionBean current = new SectionBean();
		
		ecb.setId(4);
		ecb.setCRFVersionId(101);
		current.setId(211);
		current.setOrdinal(11);
		
		SectionBean sb = (SectionBean) secdao.findNext(ecb, current);
		assertNotNull("wellformed bean", sb);
		assertTrue("invalid bean", !sb.isActive());
	}

	public void testFindPreviousSuccess() {
		EventCRFBean ecb = new EventCRFBean();
		SectionBean current = new SectionBean();
		
		ecb.setId(4);
		ecb.setCRFVersionId(101);
		current.setId(211);
		current.setOrdinal(11);
		
		SectionBean sb = (SectionBean) secdao.findPrevious(ecb, current);
		assertNotNull("wellformed bean", sb);
		assertTrue("valid bean", sb.isActive());
		assertEquals("correct bean", sb.getId(), 210);
	}

	public void testFindPreviousFailureWrongECBId() {
		EventCRFBean ecb = new EventCRFBean();
		SectionBean current = new SectionBean();
		
		ecb.setId(-1);
		ecb.setCRFVersionId(-1);
		current.setId(211);
		current.setOrdinal(11);
		
		SectionBean sb = (SectionBean) secdao.findPrevious(ecb, current);
		assertNotNull("wellformed bean", sb);
		assertTrue("invalid bean", !sb.isActive());
	}

	public void testFindPreviousFailureWrongSectionId() {
		EventCRFBean ecb = new EventCRFBean();
		SectionBean current = new SectionBean();
		
		ecb.setId(4);
		ecb.setCRFVersionId(101);
		current.setId(-1);
		current.setOrdinal(-1);
		
		SectionBean sb = (SectionBean) secdao.findPrevious(ecb, current);
		assertNotNull("wellformed bean", sb);
		assertTrue("invalid bean", !sb.isActive());
	}

	public void testFindPreviousFailureNoPreviousSection() {
		EventCRFBean ecb = new EventCRFBean();
		SectionBean current = new SectionBean();
		
		ecb.setId(4);
		ecb.setCRFVersionId(101);
		current.setId(201);
		current.setOrdinal(1);
		
		SectionBean sb = (SectionBean) secdao.findPrevious(ecb, current);
		assertNotNull("wellformed bean", sb);
		assertTrue("invalid bean", !sb.isActive());
	}
	
	public void testGetNumItemsBySectionId() {
		HashMap answer = secdao.getNumItemsBySectionId();
		
		assertNotNull("well formed answer", answer);
		assertTrue("answer not empty", !answer.isEmpty());
		
		Integer num = (Integer) answer.get(new Integer(201));
		assertNotNull("well formed value", num);
		
		int numValue = num.intValue();
		assertEquals("value correct", numValue, 48);
		
		assertTrue("invalid section not in answer", !answer.containsKey(new Integer(-1)));
	}
	
	public void testGetNumItemsCompletedBySectionId() {
		EventCRFBean ecb = new EventCRFBean();
		ecb.setId(1);
		
		HashMap answer = secdao.getNumItemsCompletedBySectionId(ecb);

		assertNotNull("well formed answer", answer);
		assertTrue("answer not empty", !answer.isEmpty());
		
		Integer num = (Integer) answer.get(new Integer(11));
		assertNotNull("well formed value", num);
		
		int numValue = num.intValue();
		assertEquals("value correct", numValue, 2);
		
		assertTrue("invalid section not in answer", !answer.containsKey(new Integer(-1)));
	}
 
	
	/* (non-Javadoc)
	 * @see test.org.akaza.openclinica.dao.core.DAOTestBase#shutDown()
	 */
	protected void shutDown() throws Exception {
		super.shutDown();
	}
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
	    shutdownDataSource(ds);
		super.tearDown();
	}
}
