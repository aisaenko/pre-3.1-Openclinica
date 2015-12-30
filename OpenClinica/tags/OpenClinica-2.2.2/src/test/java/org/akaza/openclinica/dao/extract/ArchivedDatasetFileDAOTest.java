/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package test.org.akaza.openclinica.dao.extract;

import java.io.FileInputStream;
import java.util.Collection;

import org.akaza.openclinica.dao.extract.ArchivedDatasetFileDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.xml.sax.SAXException;

import test.org.akaza.openclinica.dao.core.DAOTestBase;

/**
 * @author thickerson
 *
 * 
 */
public class ArchivedDatasetFileDAOTest extends DAOTestBase {
	
	  private ArchivedDatasetFileDAO sdao;

	  private DAODigester digester = new DAODigester();

	  public ArchivedDatasetFileDAOTest(String name) {
	    super(name);
	  }

	  public static void main(String[] args) {
	    junit.swingui.TestRunner.run(ArchivedDatasetFileDAOTest.class);
	  }

	  protected void setUp() throws Exception {
	    super.setUp();
	    digester.setInputStream(new FileInputStream(SQL_DIR + "archived_dataset_file_dao.xml"));

	    try {
	      digester.run();
	    } catch (SAXException saxe) {
	      saxe.printStackTrace();
	    }


	    super.ds = setupTestDataSource();
	    

	    sdao = new ArchivedDatasetFileDAO(super.ds, digester);
	  }

	  public void testFindAll() throws Exception {
	    Collection col = sdao.findAll();
	    assertNotNull("findAll", col);
	  }  

}
