/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package test.org.akaza.openclinica.dao.managestudy;

import java.io.FileInputStream;
import java.util.ArrayList;

import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.xml.sax.SAXException;
import org.akaza.openclinica.bean.login.*;

import test.org.akaza.openclinica.dao.core.DAOTestBase;

/**
 * @author thickerson
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DiscrepancyNoteDAOTest extends DAOTestBase {

	private DiscrepancyNoteDAO dndao;
	private DAODigester digester = new DAODigester();
	
	public DiscrepancyNoteDAOTest(String name) {
		super(name);
	}
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(DiscrepancyNoteDAOTest.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		digester.setInputStream(new FileInputStream(SQL_DIR + 
				"discrepancy_note_dao.xml"));
		//digester.setInputStream(new FileInputStream(SQL_DIR + 
		//	"oracle_study_dao.xml"));
		try {
			digester.run();
		} catch(SAXException saxe) {
			saxe.printStackTrace();
		}

		super.ds = setupTestDataSource();

		//super.ds = setupOraDataSource();
		//super.ds = setupDataSource();
		dndao = new DiscrepancyNoteDAO(super.ds, digester);
	}
	
	public void testFindByPKSuccess() throws Exception {
		int pk = 40;
		DiscrepancyNoteBean dnb = (DiscrepancyNoteBean)dndao.findByPK(pk);
		assertNotNull("findbypk",dnb);
		assertTrue("id set", dnb.isActive());
		assertEquals("right bean", dnb.getId(), pk);
	}
	
	public void testFindByPKFailure() throws Exception {
		DiscrepancyNoteBean dnb = (DiscrepancyNoteBean) dndao.findByPK(-1);
		assertNotNull("findbypk",dnb);
		assertTrue("id not set", !dnb.isActive());
		DiscrepancyNoteBean sb = (DiscrepancyNoteBean)dndao.findByPK(-1);
		assertNotNull("findbypk",sb);
		assertTrue("id not set", !sb.isActive());
	}

	public void testCreate() throws Exception {
		DiscrepancyNoteBean dnb = new DiscrepancyNoteBean();
		dnb.setDescription("Can't read birthday");
		dnb.setDiscrepancyNoteTypeId(DiscrepancyNoteType.UNCLEAR.getId());
		dnb.setResolutionStatusId(ResolutionStatus.OPEN.getId());
		dnb.setDetailedNotes("The birthday might say 4/1/2004 or 9/1/2004.");
		dnb.setEntityType("subject");
		//dnb.setOwnerId(1);
		UserAccountBean owner = new UserAccountBean();
		owner.setId(1);
		
		dnb.setStudyId(1);
		dnb.setOwner(owner);
		dnb.setParentDnId(0);
		
		dnb = (DiscrepancyNoteBean) dndao.create(dnb);
		assertTrue("id set", dnb.isActive());
	}
	
	public void testFindAllParentsByStudy() throws Exception {
		StudyBean sb = new StudyBean();
		sb.setId(32);
		ArrayList notes = dndao.findAllParentsByStudy(sb);
		assertNotNull("findAllParents", notes);
		assertTrue(notes.size() > 0);
	}
	
	public void testFindAllItemDataByStudy() throws Exception {
		StudyBean sb = new StudyBean();
		sb.setId(5);
		ArrayList notes = dndao.findAllItemDataByStudy(sb);
		assertNotNull("findAllParents", notes);
		assertTrue(notes.size() > 0);
		System.out.println("notes.size:"+ notes.size());
	}
}
