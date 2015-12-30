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

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.xml.sax.SAXException;

import test.org.akaza.openclinica.dao.core.DAOTestBase;

/**
 * @author jxu
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SubjectGroupMapDAOTest extends DAOTestBase {
  private SubjectGroupMapDAO sdao;

  private DAODigester digester = new DAODigester();

  public SubjectGroupMapDAOTest(String name) {
    super(name);
  }

  public static void main(String[] args) {
    junit.swingui.TestRunner.run(SubjectGroupMapDAOTest.class);
  }

  protected void setUp() throws Exception {
    super.setUp();
    digester.setInputStream(new FileInputStream(SQL_DIR + "subject_group_map_dao.xml"));

    try {
      digester.run();
    } catch (SAXException saxe) {
      saxe.printStackTrace();
    }

    super.ds = setupTestDataSource();
    //super.ds = setupOraDataSource();
    //super.ds = setupDataSource();
    sdao = new SubjectGroupMapDAO(super.ds, digester);
  }

  public void testFindAll() throws Exception {
    Collection col = sdao.findAll();
    assertNotNull("findAll", col);
  }
 

  public void testCreate() throws Exception {
    SubjectGroupMapBean sb = new SubjectGroupMapBean();
    sb.setStudyGroupClassId(1);
    sb.setStudySubjectId(2);
    sb.setStudyGroupId(1);   
    sb.setStatus(Status.AVAILABLE);
    UserAccountBean ub = new UserAccountBean();
    ub.setId(1);
    sb.setOwner(ub); 

    SubjectGroupMapBean sb2 = (SubjectGroupMapBean) sdao.create(sb);
    assertNotNull("simple create test", sb2);
  }

  // commented out because there are no rows in subject_group_map in test2
  // ssachs, 10 dec 04
//  public void testFindByPK() throws Exception {
//    SubjectGroupMapBean sb = (SubjectGroupMapBean) sdao.findByPK(3);
//    assertNotNull("findbypk", sb);
//    System.out.println("the Group Id"+ sb.getStudySubjectId()+ sb.getStudyGroupId());
//    assertEquals("check the Group Id", 1, sb.getStudyGroupId());
//  }

  public void testUpdate() throws Exception {
    SubjectGroupMapBean sb = new SubjectGroupMapBean();
    sb = (SubjectGroupMapBean) sdao.findByPK(3);

    sb.setStatus(Status.AVAILABLE);
    UserAccountBean ub = new UserAccountBean();
    ub.setId(1);
    sb.setUpdater(ub);    

    SubjectGroupMapBean sb2 = (SubjectGroupMapBean) sdao.update(sb);
    assertNotNull("simple update test", sb2);
  }
  
  public void testFindAllByStudySubject() throws Exception {
    int studySubjectId = 291;
    ArrayList groups = (ArrayList)sdao.findAllByStudySubject(291);
    assertTrue("FindAllByStudySubject test", groups.size()>0);
    
  }

  
  public void testFindAllByStudyGroupClassAndGroup() throws Exception {
    int studyGroupClassId =35;
    int studyGroupId =1;
    ArrayList groups = (ArrayList)sdao.findAllByStudyGroupClassAndGroup(studyGroupClassId,studyGroupId);
    assertTrue("FindAllByStudyGroupClassAndGroup test", groups.size()>0);
    
  }


}
