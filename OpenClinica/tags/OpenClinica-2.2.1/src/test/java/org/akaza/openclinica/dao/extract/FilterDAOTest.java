/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package test.org.akaza.openclinica.dao.extract;

import java.io.FileInputStream;

import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.extract.FilterDAO;
import org.xml.sax.SAXException;

import test.org.akaza.openclinica.dao.core.DAOTestBase;

/**
 * @author thickerson
 *
 * 
 */
public class FilterDAOTest extends DAOTestBase {
	private FilterDAO fdao;
	private DAODigester digester = new DAODigester();
	
	public FilterDAOTest(String name) {
		super(name);
	}
	public static void main(String[] args) {
	    junit.swingui.TestRunner.run(FilterDAOTest.class);
	}
	protected void setUp() throws Exception {
		super.setUp();
		digester.setInputStream(new FileInputStream(SQL_DIR + "filter_dao.xml"));
		try {
		     digester.run();
		} catch (SAXException saxe) {
		     saxe.printStackTrace();
		}

		super.ds = setupTestDataSource();

		fdao = new FilterDAO(super.ds, digester);
	}

	
	public void testFindAll() {
	  return;
	}

}
