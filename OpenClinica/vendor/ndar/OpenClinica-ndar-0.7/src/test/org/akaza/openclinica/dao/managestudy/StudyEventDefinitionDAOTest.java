/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package test.org.akaza.openclinica.dao.managestudy;

import java.io.FileInputStream;
import java.util.ArrayList;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.xml.sax.SAXException;

import test.org.akaza.openclinica.dao.core.DAOTestBase;

/**
 * @author ssachs
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StudyEventDefinitionDAOTest extends DAOTestBase {
	private StudyEventDefinitionDAO sdao;
	
	private DAODigester digester = new DAODigester();
	
	public StudyEventDefinitionDAOTest(String name) {
		super(name);
	}
	
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(StudySubjectDAOTest.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		
		digester.setInputStream(new FileInputStream(SQL_DIR + "studyeventdefinition_dao.xml"));
		
		try {
			digester.run();
		} catch (SAXException saxe) {
			saxe.printStackTrace();
		}
		
		super.ds = setupTestDataSource();
		//super.ds = setupOraDataSource();
		//super.ds = setupDataSource();
		sdao = new StudyEventDefinitionDAO(super.ds, digester);
	}
	
	public void testFindAllByStudySuccess() {
		StudyBean sb = new StudyBean();
		sb.setId(1);
		
		ArrayList rows = sdao.findAllByStudy(sb);
		
		assertNotNull("rows valid", rows);
		assertTrue("correct number of rows", rows.size() > 5);

		StudyEventDefinitionBean seb = (StudyEventDefinitionBean) rows.get(0);
		assertNotNull("well-formed beans", seb);
		assertTrue("bean id set", seb.isActive());
		assertEquals("correct bean", seb.getId(), 1);
	}
	
	public void testFindAllByStudyFailure() {
		StudyBean sb = new StudyBean();
		sb.setId(-1);
		
		ArrayList rows = sdao.findAllByStudy(sb);
		
		assertNotNull("rows valid", rows);
		assertEquals("no rows", rows.size(), 0);
	}
/*
 * again, need a test here where we have a legit study id, tbh
 */
//	public void testFindByPKAndStudy() {
//		int pk = 1;
//		StudyBean sb = new StudyBean();
//		sb.setId(1);
//		
//		StudyEventDefinitionBean seb = (StudyEventDefinitionBean) sdao.findByPKAndStudy(pk, sb);
//		
//		assertNotNull("well-formed beans", seb);
//		assertTrue("bean id set", seb.isActive());
//		assertEquals("got right bean", seb.getId(), pk);
//	}
	
	public void testFindAllWithStudyEventSuccess() {
		StudyBean sb = new StudyBean();
		sb.setId(1);

		ArrayList rows = sdao.findAllWithStudyEvent(sb);
		StudyEventDefinitionBean seb = (StudyEventDefinitionBean) rows.get(0);
		
		assertNotNull("rows valid", rows);
		assertTrue("at least one row", rows.size() > 0);
		assertNotNull("well-formed beans", seb);
		assertTrue("bean id set", seb.isActive());
		assertEquals("bean from correct study", seb.getStudyId(), 1);
	}

	public void testFindAllWithStudyEventFailure() {
		StudyBean sb = new StudyBean();
		sb.setId(-1);

		ArrayList rows = sdao.findAllWithStudyEvent(sb);
		
		assertNotNull("rows valid", rows);
		assertEquals("no row", rows.size(), 0);
	}

	public void testFindAll() {
		ArrayList rows = (ArrayList) sdao.findAll();
		StudyEventDefinitionBean seb = (StudyEventDefinitionBean) rows.get(0);
		
		assertNotNull("rows valid", rows);
		assertTrue("at least one row", rows.size() > 0);
		assertNotNull("well-formed beans", seb);
		assertTrue("bean id set", seb.isActive());
	}
	
	public void testFindByPKSuccess() {
		StudyEventDefinitionBean seb = (StudyEventDefinitionBean) sdao.findByPK(1);
		
		assertNotNull("well-formed beans", seb);
		assertTrue("bean id set", seb.isActive());
		assertEquals("got right bean", seb.getId(), 1);
	}
	
	public void testFindByPKFailure() {
		StudyEventDefinitionBean seb = (StudyEventDefinitionBean) sdao.findByPK(-1);
		
		assertNotNull("well-formed beans", seb);
		assertTrue("bean id not set", !seb.isActive());
	}

	public void testCreate() throws Exception {
		StudyEventDefinitionBean sb = new StudyEventDefinitionBean();
		
		sb.setCategory("test category");
		sb.setDescription("test description");
		sb.setName("test definition");
		sb.setRepeating(true);
		sb.setStudyId(9);
		sb.setType("common");
		
		sb.setStatus(Status.AVAILABLE);
		UserAccountBean ub = new UserAccountBean();
		ub.setId(1);
		sb.setOwner(ub);
		
		StudyEventDefinitionBean sb2 = (StudyEventDefinitionBean) sdao.create(sb);
		assertNotNull("simple create test", sb2);
		assertTrue("got id back", sb2.isActive());
	}
	
	public void testUpdate() throws Exception {
		StudyEventDefinitionBean sb = new StudyEventDefinitionBean();
		sb = (StudyEventDefinitionBean) sdao.findByPK(2);
		sb.setDescription("test description 2");
		sb.setStatus(Status.AVAILABLE);
		UserAccountBean ub = new UserAccountBean();
		ub.setId(1);
		sb.setUpdater(ub);    
		
		StudyEventDefinitionBean sb2 = (StudyEventDefinitionBean) sdao.update(sb);
		assertNotNull("simple update test", sb2);
	}
	
	public void testFindNextKey() throws Exception {
	  	int pk = sdao.findNextKey();
	  	
	  	assertTrue("pk valid", pk > 0);
	  	
	  	testCreate();
	  	
	  	int pk2 = sdao.findNextKey();
	  	assertEquals("pk sequence correct", pk + 2, pk2);
	}
	
	public void testFindByEventDefinitionCRFIdSuccess() throws Exception {
		StudyEventDefinitionBean seb = sdao.findByEventDefinitionCRFId(3);
		assertNotNull("well-formed beans", seb);
		assertTrue("bean id set", seb.isActive());
		assertEquals("got right bean", seb.getId(), 1);
	}

	public void testFindByEventDefinitionCRFIdFailure() throws Exception {
		StudyEventDefinitionBean seb = sdao.findByEventDefinitionCRFId(-1);
		assertNotNull("well-formed beans", seb);
		assertTrue("bean id not set", !seb.isActive());
	}
}
