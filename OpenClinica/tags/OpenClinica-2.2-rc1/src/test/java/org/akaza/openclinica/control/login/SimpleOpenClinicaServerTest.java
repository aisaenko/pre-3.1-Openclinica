/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package test.org.akaza.openclinica.control.login;

import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.parsing.HTMLParserFactory;
import com.meterware.pseudoserver.HttpUserAgentTest;

/**<P>SimpleOpenClinicaServerTest.java, the initial class for using HttpUnit.
 * <P>Currently only holds the address for the internal server and
 * has timing records, but can also hold methods for login, etc.
 * @author thickerson
 *
 * 
 */
public class SimpleOpenClinicaServerTest extends HttpUserAgentTest {


	public final static String WEB_ADDRESS = "http://192.168.1.103:8080/OpenClinica/";

	public boolean _showTestName;
	private long _startTime;
	
	public SimpleOpenClinicaServerTest( String name ) {
        super( name );
        //_showTestName=true;
        
    }
	
	public SimpleOpenClinicaServerTest( String name, boolean showTestName ) {
        super( name );
        _showTestName = showTestName;
    }
	
	public void setUp() throws Exception {
        super.setUp();
        HttpUnitOptions.reset();
        HTMLParserFactory.reset();
        if (_showTestName) {
            System.out.println( "----------------------- " + getName() + " ------------------------");
            _startTime = System.currentTimeMillis();
        }
    }


    public void tearDown() throws Exception {
        super.tearDown();
        if (_showTestName) {
            long duration = System.currentTimeMillis() - _startTime;
            System.out.println( "... took " + duration + " msec");
        }
    }
    static {
        new WebConversation();
    }
}

