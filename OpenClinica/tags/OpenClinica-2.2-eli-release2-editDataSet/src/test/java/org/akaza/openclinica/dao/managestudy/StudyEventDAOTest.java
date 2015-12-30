/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package test.org.akaza.openclinica.dao.managestudy;

import java.io.FileInputStream;
import java.util.*;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.*;
import org.akaza.openclinica.bean.core.*;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.xml.sax.SAXException;

import test.org.akaza.openclinica.dao.core.DAOTestBase;

/**
 * @author jxu
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StudyEventDAOTest extends DAOTestBase {
	private StudyEventDAO sdao;
	
	private DAODigester digester = new DAODigester();
	
	public StudyEventDAOTest(String name) {
		super(name);
	}
	
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(StudySubjectDAOTest.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		digester.setInputStream(new FileInputStream(SQL_DIR + "study_event_dao.xml"));
		
		try {
			digester.run();
		} catch (SAXException saxe) {
			saxe.printStackTrace();
		}
		

		super.ds = setupTestDataSource();

		//super.ds = setupOraDataSource();
		//super.ds = setupDataSource();
		sdao = new StudyEventDAO(super.ds, digester);
	}
	
	public void testFindAll() throws Exception {
		Collection col = sdao.findAll();
		assertNotNull("findAll", col);
	}
	
	public void testFindAllByDefinition() throws Exception {
		Collection col = sdao.findAllByDefinition(1);
		assertNotNull("findAllByDefinition", col);
	}
	
	public void testFindAllWithSubjectLabelByDefinition() throws Exception {
		Collection col = sdao.findAllWithSubjectLabelByDefinition(5);
		assertNotNull("findAllWithSubjectLabelByDefinition", col);
	}
	
	public void testFindByPK() throws Exception {
		StudyEventBean sb = (StudyEventBean) sdao.findByPK(1);
		assertNotNull("findbypk", sb);
		assertEquals("check the location", "this is the updated location", sb.getLocation());
	}
	
	public void testCreate() throws Exception {
		StudyEventBean sb = new StudyEventBean();
		sb.setName("Study Event 1");
		sb.setDateStarted(new Date());
		sb.setDateEnded(new Date());
		sb.setSampleOrdinal(2);
		sb.setStudyEventDefinitionId(1);
		sb.setStudySubjectId(4);
		sb.setLocation("this is the location");
		
		sb.setStatus(Status.AVAILABLE);
		UserAccountBean ub = new UserAccountBean();
		ub.setId(1);
		sb.setOwner(ub);
		
		StudyEventBean sb2 = (StudyEventBean) sdao.create(sb);
		assertNotNull("simple create test", sb2);
	}
	
	public void testUpdate() throws Exception {
		StudyEventBean sb = new StudyEventBean();
		sb = (StudyEventBean) sdao.findByPK(1);
		sb.setLocation("this is the updated location");
		sb.setStatus(Status.AVAILABLE);
		UserAccountBean ub = new UserAccountBean();
		ub.setId(1);
		sb.setUpdater(ub);    
		
		
		StudyEventBean sb2 = (StudyEventBean) sdao.update(sb);
		assertNotNull("simple update test", sb2);
	}
	
	public void testGetDefinitionIdFromStudyEventId() throws Exception {
		int id = sdao.getDefinitionIdFromStudyEventId(1);
		assertEquals("correct id returned", id, 1);
	}
	
	public void testFindAllByStudyAndStudySubjectId() throws Exception {
		StudyBean sb = new StudyBean();
		sb.setId(1);
		
		ArrayList rows = sdao.findAllByStudyAndStudySubjectId(sb, 14);
		
		assertNotNull("rows valid", rows);
		assertTrue("at least one row", rows.size() > 0);

		StudyEventBean seb = (StudyEventBean) rows.get(0);
		assertNotNull("well-formed beans", seb);
		assertTrue("bean id set", seb.isActive());
		assertEquals("got right bean", seb.getId(), 101);
	}
	/*
	 * rewrite test to find a study id that means something, tbh
	 */
//	public void testFindAllByStudyAndEventDefinitionId() throws Exception {
//		StudyBean sb = new StudyBean();
//		sb.setId(1);
//		
//		ArrayList rows = sdao.findAllByStudyAndEventDefinitionId(sb, 1);
//		StudyEventBean seb = (StudyEventBean) rows.get(0);
//		
//		assertNotNull("rows valid", rows);
//		assertTrue("at least one row", rows.size() > 0);
//		assertNotNull("well-formed beans", seb);
//		assertTrue("bean id set", seb.isActive());
//		assertEquals("got right bean", seb.getId(), 6);
//	}
	
	public void testFindByPKAndStudy() {
		int pk = 106;
		StudyBean sb = new StudyBean();
		sb.setId(1);
		//this cause class cast problem
		/*StudyEventBean seb = (StudyEventBean) sdao.findByPKAndStudy(pk, sb);
		
		assertNotNull("well-formed beans", seb);
		assertTrue("bean id set", seb.isActive());
		assertEquals("got right bean", seb.getId(), pk);*/
	}
	
	//thickerson
	//dev4?
	/*public void testFindCRFsByStudyEvent() throws Exception {
		StudyEventBean seb = new StudyEventBean();
		Collection col = sdao.findAll();
		Iterator it = col.iterator();
		if (it.hasNext()) {
			seb = (StudyEventBean)it.next();
		}
		//seb = (StudyEventBean)sdao.findByPK(1);
		HashMap hm = sdao.findCRFsByStudyEvent(seb);
		Set se = hm.entrySet();
		for (Iterator iter = se.iterator(); iter.hasNext(); ) {
			Map.Entry newMap = (Map.Entry)iter.next();
			CRFBean cb = (CRFBean)newMap.getKey();
			System.out.println("crf name: "+cb.getName());
		}
		assertNotNull("found a bean", seb);
	}*/
	
	public void testFindCRFsByStudy() throws Exception {
		StudyBean sb = new StudyBean();
		sb.setId(9);
		HashMap hm = sdao.findCRFsByStudy(sb);
		Set se = hm.entrySet();
		for (Iterator iter = se.iterator(); iter.hasNext(); ) {
			Map.Entry newMap = (Map.Entry)iter.next();
			EntityBean cb = (EntityBean)newMap.getKey();
			EntityBean eb = (EntityBean)newMap.getValue();
			System.out.println("event name: "+cb.getName());
			System.out.println("crf name: "+eb.getName());
		}
		assertNotNull("found a bean", sb);
	}
}
