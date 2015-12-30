/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.login;

import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;

import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * <P>
 * BeginLoginTest.java, the first HttpUnit test for testing the system.
 * <P>
 * Currently tests against the instance of system running on the internal
 * system, 192.168.1.103.
 *
 * @author thickerson
 *
 *
 */
public class BeginLoginTest extends SimpleOpenClinicaServerTest {

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static TestSuite suite() {
        return new TestSuite(BeginLoginTest.class);
    }

    public BeginLoginTest(String s) {
        super(s, true);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public WebResponse loggedInPage(String addr) throws Exception {

        WebConversation wc = new WebConversation();
        WebResponse resp = wc.getResponse(addr);

        WebForm passwd = resp.getForms()[0];
        passwd.setParameter("j_username", "user5");
        passwd.setParameter("j_password", "12345678");
        WebResponse entry = passwd.submit();

        return entry;

    }

    public WebResponse notLoggedInPage(String addr) throws Exception {
        WebConversation wc = new WebConversation();
        WebResponse resp = wc.getResponse(addr);

        return resp;
    }

    public WebResponse analyseForms(String addr) throws Exception {
        WebConversation wc = new WebConversation();
        WebResponse resp = wc.getResponse(addr);
        WebForm[] reviewForm = resp.getForms();
        for (int i = 0; i < reviewForm.length; i++) {
            WebForm thisForm = reviewForm[i];
            String[] paramNames = thisForm.getParameterNames();
            System.out.println("*** First param for form " + i + ": " + paramNames[0]);
        }
        return resp;
    }

    public WebResponse loggedInPage(String addr, String name, String pwd) throws Exception {

        WebConversation wc = new WebConversation();
        WebResponse resp = wc.getResponse(addr);

        WebForm passwd = resp.getForms()[0];
        passwd.setParameter("j_username", name);
        passwd.setParameter("j_password", pwd);
        WebResponse entry = passwd.submit();

        return entry;

    }

    /*
     * public void testGetRequestPassword() throws Exception { WebResponse admin =
     * analyseForms(WEB_ADDRESS + "RequestPassword"); assertNotNull("admin page
     * accessed", admin); }
     */

    /*
     * public void testGetMainMenu() throws Exception { WebResponse def =
     * analyseForms(WEB_ADDRESS + "MainMenu"); assertNotNull("default page
     * accessed", def); }
     */

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
