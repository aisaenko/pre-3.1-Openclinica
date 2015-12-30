/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package test.org.akaza.openclinica.dao.service;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.xml.sax.SAXException;

import test.org.akaza.openclinica.dao.core.DAOTestBase;

public class StudyParameterValueDAOTest extends DAOTestBase{
  private StudyParameterValueDAO spdao;
  private StudyDAO sdao;
    
    private DAODigester digester = new DAODigester();
    
    public StudyParameterValueDAOTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        junit.swingui.TestRunner.run(StudyParameterValueDAOTest.class);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        digester.setInputStream(new FileInputStream(SQL_DIR + "study_parameter_value_dao.xml"));
        
        try {
            digester.run();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        }
        

        super.ds = setupTestDataSource();

        //super.ds = setupOraDataSource();
        //super.ds = setupDataSource();
        spdao = new StudyParameterValueDAO(super.ds, digester);
    }
    
    public void testFindAll() throws Exception {
        Collection col = spdao.findAll();
        assertNotNull("findAll", col);
    }
    
    public void testFindAllParameters() throws Exception {
    
      ArrayList col = spdao.findAllParameters();
      assertNotNull("findParameters", col);
  }

}
