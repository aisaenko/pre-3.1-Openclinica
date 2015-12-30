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
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.xml.sax.SAXException;

import test.org.akaza.openclinica.dao.core.DAOTestBase;

/**
 * @author jxu
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StudyGroupClassDAOTest extends DAOTestBase {
  private StudyGroupClassDAO sdao;

  private DAODigester digester = new DAODigester();

  public StudyGroupClassDAOTest(String name) {
    super(name);
  }

  public static void main(String[] args) {
    junit.swingui.TestRunner.run(StudyGroupClassDAOTest.class);
  }

  protected void setUp() throws Exception {
    super.setUp();
    digester.setInputStream(new FileInputStream(SQL_DIR + "study_group_class_dao.xml"));

    try {
      digester.run();
    } catch (SAXException saxe) {
      saxe.printStackTrace();
    }

    super.ds = setupTestDataSource();
    //super.ds = setupOraDataSource();
    //super.ds = setupDataSource();
    sdao = new StudyGroupClassDAO(super.ds, digester);
  }

  public void testFindAll() throws Exception {
    Collection col = sdao.findAll();
    assertNotNull("findAll", col);
  }
  

  public void testCreate() throws Exception {
    StudyGroupClassBean sb = new StudyGroupClassBean();
    sb.setName("Study Group 1");   
    sb.setStudyId(1);
    sb.setGroupClassTypeId(9);
    sb.setStatus(Status.AVAILABLE);
    UserAccountBean ub = new UserAccountBean();
    ub.setId(1);
    sb.setOwner(ub);

    StudyGroupClassBean sb2 = (StudyGroupClassBean) sdao.create(sb);
    assertNotNull("simple create test", sb2);
  }

  public void testUpdate() throws Exception {
    StudyGroupClassBean sb = new StudyGroupClassBean();
    sb = (StudyGroupClassBean) sdao.findByPK(9);
    sb.setName("Study Group Class 1");
    sb.setStatus(Status.AVAILABLE);
    UserAccountBean ub = new UserAccountBean();
    ub.setId(1);
    sb.setUpdater(ub); 
    

    StudyGroupClassBean sb2 = (StudyGroupClassBean) sdao.update(sb);
    assertNotNull("simple update test", sb2);
  }

  // tests below commented out because there are now study groups in test2
  // ssachs 10 Dec 04
//  public void testFindByPK() throws Exception {
//    StudyGroupBean sb = (StudyGroupBean) sdao.findByPK(9);
//    assertNotNull("findbypk", sb);
//    assertEquals("check the name", "Study Group 1", sb.getName());
//  }
//  
//
  public void testFindAllByStudy() throws Exception {
 	int studyId = 9;
 	StudyBean sb = new StudyBean();
 	sb.setId(studyId);
 	
 	ArrayList groups = sdao.findAllByStudy(sb);
	
  }
//
//  public void testFindByPKAndStudy() {
//  	int studyId = 9;
//  	int pk = 9;
//  	StudyBean sb = new StudyBean();
//  	sb.setId(studyId);
//  	
//  	StudyGroupBean sgb = (StudyGroupBean) sdao.findByPKAndStudy(pk, sb);
//  	assertNotNull("bean well formed", sgb);
//  	assertTrue("bean id set", sgb.isActive());
//  	assertEquals("bean pk correct", sgb.getId(), pk);
//  	assertEquals("bean id correct study", sgb.getStudyId(), studyId);
//  }
}
