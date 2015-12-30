/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package test.org.akaza.openclinica.dao.submit;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Date;

import org.akaza.openclinica.bean.core.*;
import org.akaza.openclinica.bean.login.*;
import org.akaza.openclinica.bean.submit.*;
import org.akaza.openclinica.bean.managestudy.*;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.dao.submit.*;
import org.xml.sax.SAXException;

import test.org.akaza.openclinica.dao.core.DAOTestBase;

/**
 * @author thickerson
 *
 
 */
public class EventCRFDAOTest extends DAOTestBase {
	private EventCRFDAO ecdao;
	
	private DAODigester digester = new DAODigester();
	
	public EventCRFDAOTest(String name) {
		super(name);
	}
	
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(ItemDAOTest.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		digester.setInputStream(new FileInputStream(SQL_DIR + "eventcrf_dao.xml"));
		
		try {
			digester.run();
		} catch (SAXException saxe) {
			saxe.printStackTrace();
		}
		

		super.ds = setupTestDataSource();

		
		ecdao = new EventCRFDAO(super.ds, digester);
	}
	
	public void testFindAll() throws Exception {
		Collection col = ecdao.findAll();
		assertNotNull("findAll", col);
	}
	
	public void testFindByPKSuccess() throws Exception {
		Collection col = ecdao.findAll();
		Iterator it = col.iterator();
		int pkey = 0;
		if (it.hasNext()) {
			EventCRFBean testMe = (EventCRFBean)it.next();
			pkey = testMe.getId();
		}
		EventCRFBean ecb = (EventCRFBean)ecdao.findByPK(pkey);
		assertNotNull("findByPK", ecb);
		assertTrue("valid bean", ecb.isActive());
		assertEquals("correct bean", ecb.getId(), pkey);
	}
	public void testFindTimestamp() throws Exception {
		
		EventCRFBean ecb = (EventCRFBean)ecdao.findByPK(133);
		assertNotNull("findByPK", ecb);
		assertTrue("valid bean", ecb.isActive());
		System.out.println(ecb.getDateCompleted());
	}


	public void testFindByPKFailure() throws Exception {
		EventCRFBean ecb = (EventCRFBean)ecdao.findByPK(-1);
		assertNotNull("findByPK", ecb);
		assertTrue("invalid bean", !ecb.isActive());
	}
	
	public void testFindByPKAndStudySuccess() throws Exception {
		int pkey = 1;
		int studyId = 1;
		StudyBean sb = new StudyBean();
		sb.setId(studyId);
		AuditableEntityBean ecb = ecdao.findByPKAndStudy(pkey, sb);
		//EventCRFBean ecb = (EventCRFBean)ecdao.findByPKAndStudy(pkey, sb);
		assertNotNull("findByPK", ecb);
		//assertTrue("valid bean", ecb.isActive());
		//assertEquals("correct bean", ecb.getId(), pkey);		
	}
	
	public void testFindByPKAndStudyFailureBadPK() throws Exception {
		int pkey = -1;
		int studyId = 1;
		StudyBean sb = new StudyBean();
		sb.setId(studyId);

		AuditableEntityBean ecb = ecdao.findByPKAndStudy(pkey, sb);
		assertNotNull("findByPK", ecb);
		assertTrue("invalid bean", !ecb.isActive());
	}

	public void testFindByPKAndStudyFailureBadStudy() throws Exception {
		int pkey = 1;
		int studyId = 2;
		StudyBean sb = new StudyBean();
		sb.setId(studyId);

		AuditableEntityBean ecb = ecdao.findByPKAndStudy(pkey, sb);
		assertNotNull("findByPK", ecb);
		assertTrue("invalid bean", !ecb.isActive());
	}

	public void testUpdate() throws Exception {
		Collection col = ecdao.findAll();
		Iterator it = col.iterator();
		int pkey = 0;
		if (it.hasNext()) {
			EventCRFBean testMe = (EventCRFBean)it.next();
			pkey = testMe.getId();
		}
		EventCRFBean ecb = (EventCRFBean)ecdao.findByPK(pkey);
		System.out.println("Found by primary key "+pkey+" annotation: "+ecb.getAnnotations());
		String keepAnnot = ecb.getAnnotations();
		ecb.setAnnotations("changed annotations");
		ecb.setStatus(Status.DELETED);
		//ecb.setUpdaterId(1);
		UserAccountBean updater = new UserAccountBean();
		updater.setId(1);
		ecb.setUpdater(updater);
		ecb.setUpdatedDate(new Date(System.currentTimeMillis()));
		ecb = (EventCRFBean)ecdao.update(ecb);
		ecb.setAnnotations(keepAnnot);
		ecb.setStatus(Status.AVAILABLE);
		ecb = (EventCRFBean)ecdao.update(ecb);
		
		assertEquals("test Update", keepAnnot, ecb.getAnnotations());
		
	}
	
	public void testInsert() throws Exception {
		EventCRFBean ecb = new EventCRFBean();
		Date thisDate = new Date(System.currentTimeMillis());
		ecb.setAnnotations("test inserting, delete me later "+thisDate.toString());
		//ecb.setOwnerId(1);
		UserAccountBean owner = new UserAccountBean();
		owner.setId(1);
		ecb.setOwner(owner);
//		ecb.setStatusId(1);
		ecb.setStatus(Status.get(1));
		ecb.setCompletionStatusId(1);
		ecb.setInterviewerName("tomh tomh");
		ecb.setDateInterviewed(thisDate);
		ecb.setStudyEventId(1);
		ecb.setCRFVersionId(7);
		ecb.setStudySubjectId(1);
		ecb = (EventCRFBean)ecdao.create(ecb);
		
		//note: all the above fields should be filled in when creating
		//an event_crf row in the database
		//throws lots of exceptions, but still comes up green, how
		//can we catch that???
		assertNotNull("Test Insert", ecb);
		assertEquals("Test Insert 2", ecb.getInterviewerName(), "tomh tomh");
	}
	
	public void testFindAllByStudyEventSuccess() throws Exception {
		StudyEventBean seb = new StudyEventBean();
		seb.setId(1);
		
		ArrayList rows = ecdao.findAllByStudyEvent(seb);
		
		assertNotNull("rows valid", rows);
		assertTrue("at least one row", rows.size() > 0);

		EventCRFBean ecb = (EventCRFBean) rows.get(0);
		assertNotNull("well-formed beans", ecb);
		assertTrue("bean id set", ecb.isActive());
		assertEquals("correct bean", ecb.getId(), 1);
	}

	public void testFindAllByStudyEventFailure() throws Exception {
		StudyEventBean seb = new StudyEventBean();
		seb.setId(-1);
		
		ArrayList rows = ecdao.findAllByStudyEvent(seb);
		
		assertNotNull("rows valid", rows);
		assertEquals("no rows", rows.size(), 0);
	}
}
