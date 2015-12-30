/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package test.org.akaza.openclinica.dao.managestudy;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.xml.sax.SAXException;

import test.org.akaza.openclinica.dao.core.DAOTestBase;

/**
 * @author jxu
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EventDefinitionCRFDAOTest extends DAOTestBase {
  private EventDefinitionCRFDAO sdao;

  private DAODigester digester = new DAODigester();
 

  public EventDefinitionCRFDAOTest(String name) {
    super(name);
  }

  public static void main(String[] args) {
    junit.swingui.TestRunner.run(StudySubjectDAOTest.class);
  }

  protected void setUp() throws Exception {
    super.setUp();
    digester.setInputStream(new FileInputStream(SQL_DIR + "event_definition_crf_dao.xml"));
   

    try {
      digester.run();    
    } catch (SAXException saxe) {
      saxe.printStackTrace();
    }


    super.ds = setupTestDataSource();

    //super.ds = setupOraDataSource();
    //super.ds = setupDataSource();
    sdao = new EventDefinitionCRFDAO(super.ds, digester);   
  }

  public void testFindAll() throws Exception {
    Collection col = sdao.findAll();
    assertNotNull("findAll", col);
  }

 
  public void testCreate() throws Exception {
    EventDefinitionCRFBean sb = new EventDefinitionCRFBean();    
    sb.setCrfId(1);
    sb.setDefaultVersionId(1);
    sb.setDecisionCondition(false);
    sb.setDoubleEntry(true);
    sb.setRequireAllTextFilled(false);
    sb.setStudyEventDefinitionId(2);
    sb.setStudyId(1);
    sb.setStatus(Status.AVAILABLE);
    UserAccountBean ub = new UserAccountBean();
    ub.setId(1);
    sb.setOwner(ub);

    EventDefinitionCRFBean sb2 = (EventDefinitionCRFBean) sdao.create(sb);
    assertNotNull("simple create test", sb2);
  }
  
  public void testFindByPK() throws Exception {
  	Collection col = sdao.findAll();
  	Iterator it = col.iterator();
  	int pkey = 0;
  	EventDefinitionCRFBean sb = new EventDefinitionCRFBean();
  	if (it.hasNext()) {
  		sb = (EventDefinitionCRFBean)it.next();
  		pkey = sb.getId();
  	}
    sb = (EventDefinitionCRFBean) sdao.findByPK(pkey);
    assertNotNull("findbypk", sb);
    System.out.println("crf id" + sb.getCrfId());
   
  }


  public void testUpdate() throws Exception {
  	Collection col = sdao.findAll();
  	Iterator it = col.iterator();
  	EventDefinitionCRFBean sb = new EventDefinitionCRFBean();
  	if (it.hasNext()) {
  		sb = (EventDefinitionCRFBean)it.next();
  	}
    
    //sb = (EventDefinitionCRFBean) sdao.findByPK(2);

    sb.setStatus(Status.AVAILABLE);
    UserAccountBean ub = new UserAccountBean();
    ub.setId(1);
    sb.setUpdater(ub);    

    EventDefinitionCRFBean sb2 = (EventDefinitionCRFBean) sdao.update(sb);
    assertNotNull("simple update test", sb2);
  }

  public void testFindAllByEventDefinitionSuccess() throws Exception {
  	ArrayList rows = sdao.findAllByEventDefinitionId(1);

  	assertNotNull("rows valid", rows);
    assertEquals("right number of rows", rows.size(), 4);

    EventDefinitionCRFBean edcb = (EventDefinitionCRFBean) rows.get(0); 
  	
    assertNotNull("well-formed beans", edcb);
    assertTrue("bean id set", edcb.isActive());
    assertTrue("correct bean", edcb.getId() < 5);
    assertEquals("property set", edcb.getStudyEventDefinitionId(), 1);
  }
  
  public void testFindAllByEventDefinitionIdFailure() throws Exception {
  	ArrayList rows = sdao.findAllByEventDefinitionId(-1);
  	
    assertNotNull("rows valid", rows);
    assertEquals("no rows", rows.size(), 0);
  }
  
  public void testFindByStudyEventDefinitionIdAndCRFIdSuccess() throws Exception {
  	EventDefinitionCRFBean edcb = sdao.findByStudyEventDefinitionIdAndCRFId(1, 77);
  	
  	assertNotNull("well-formed bean", edcb);
  	assertTrue("valid bean", edcb.isActive());
  	assertEquals("correct bean", edcb.getId(), 2);
  	assertEquals("property set - sedid", edcb.getStudyEventDefinitionId(), 1);
  	assertEquals("property set - crfid", edcb.getCrfId(), 77);
  }

  public void testFindByStudyEventDefinitionIdAndCRFIdFailureWrongSED() throws Exception {
  	EventDefinitionCRFBean edcb = sdao.findByStudyEventDefinitionIdAndCRFId(-1, 77);
  	
  	assertNotNull("well-formed bean", edcb);
  	assertTrue("invalid bean", !edcb.isActive());
  }

  public void testFindByStudyEventDefinitionIdAndCRFIdFailureWrongCRF() throws Exception {
  	EventDefinitionCRFBean edcb = sdao.findByStudyEventDefinitionIdAndCRFId(1, -1);
  	
  	assertNotNull("well-formed bean", edcb);
  	assertTrue("invalid bean", !edcb.isActive());
  }
  
  public void findByStudyEventIdAndCRFVersionIdSuccess() throws Exception {
  	EventDefinitionCRFBean edcb = sdao.findByStudyEventIdAndCRFVersionId(1, 11);
  	
  	assertNotNull("well-formed bean", edcb);
  	assertTrue("valid bean", edcb.isActive());
  	assertEquals("correct bean", edcb.getId(), 1);
  }

  public void findByStudyEventIdAndCRFVersionIdFailureWrongSE() throws Exception {
  	EventDefinitionCRFBean edcb = sdao.findByStudyEventIdAndCRFVersionId(-1, 11);
  	
  	assertNotNull("well-formed bean", edcb);
  	assertTrue("invalid bean", !edcb.isActive());
  }

  public void findByStudyEventIdAndCRFVersionIdFailureWrongCRFV() throws Exception {
  	EventDefinitionCRFBean edcb = sdao.findByStudyEventIdAndCRFVersionId(1, -1);
  	
  	assertNotNull("well-formed bean", edcb);
  	assertTrue("invalid bean", !edcb.isActive());
  }
  
  
}