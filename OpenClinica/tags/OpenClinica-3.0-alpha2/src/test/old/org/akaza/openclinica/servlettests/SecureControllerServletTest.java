package org.akaza.openclinica.servlettests;

import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.apache.commons.dbcp.BasicDataSource;
import org.mockejb.jndi.MockContextFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;

import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A class that uses the JUnit library to test SecureController. The test case
 * tests the SecureController process(), forwardPage(), and entityIncluded()
 * methods (with process(0 being by far the most important method). The class
 * uses mock objects to set up the servlet environment and store a DataSource in
 * a JNDI implementation. This test case also uses SecureControllerTestDelegate,
 * a new SecureController inner class, to call (and therefore test) the private
 * SecureController process() method. This class runs with a "basedir" VM
 * parameter as in: -Dbasedir=/Users/bruceperry/oc_trunk/trunk.
 *
 * @see org.akaza.openclinica.servlettests.SecureControllerWrapper
 * @see org.akaza.openclinica.control.core.SecureController.SecureControllerTestDelegate
 * @author Bruce W. Perry 01/2008
 */
public class SecureControllerServletTest extends TestCase {
    // The JNDI Context that will refer to the DataSource, which
    // provides the connection to PostgreSQL
    private Context context;
    // This path is used in formulating the mock ServletContext
    private static final String WEB_APP_PATH = "src" + File.separator + "main" + File.separator + "webapp";
    // This path is used to connect with a properties file that contains
    // database connection
    // information and test parameters
    private static final String PROPERTIES_PATH = "src" + File.separator + "test" + File.separator + "java";
    // The database-connection parameters and the inputs like query strings
    // are stored in properties
    private Properties properties = new Properties();

    public static Test suite() {
        return new TestSuite(SecureControllerServletTest.class);
    }

    /**
     * This method runs before the test methods. The method creates an initial
     * context and stores a DataSource object in that context. This allows the
     * mock objects to use the same persistent objects that the application uses
     * in Tomcat.
     *
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockContextFactory.setAsInitial();
        // create the initial context that will be used for binding EJBs
        context = new InitialContext();
        // Create an instance of the MockContainer
        // MockContainer mockContainer = new MockContainer( context );
        String baseDir = System.getProperty("basedir");
        if (baseDir == null || "".equalsIgnoreCase(baseDir)) {
            baseDir = System.getProperty("user.dir");
        }
        if (baseDir == null || "".equalsIgnoreCase(baseDir)) {
            throw new IllegalStateException(
                    "The system properties basedir or user.dir were not made available to the application. Therefore we cannot locate the test properties file.");
        }

        try {

            properties.load(new FileInputStream(new File(baseDir + File.separator + PROPERTIES_PATH + File.separator + "servlet_properties")));

        } catch (Exception ioExc) {
            ioExc.printStackTrace();

        }
        org.apache.commons.dbcp.BasicDataSource ds = new org.apache.commons.dbcp.BasicDataSource();
        ds.setAccessToUnderlyingConnectionAllowed(true);
        ds.setDriverClassName(properties.getProperty("driverClassName"));
        ds.setUsername(properties.getProperty("username"));
        ds.setPassword(properties.getProperty("password"));
        ds.setUrl(properties.getProperty("url"));
        context.rebind("java:comp/env/SQLPostgres", ds);

        // set up SQLFactory; this would usually take place in a class
        // like SecureController, so we have to include it in set-up.
        SQLFactory.JUNIT_XML_DIR = baseDir + File.separator + "src/main" + File.separator + "webapp" + File.separator + "properties" + File.separator;
        SQLFactory.getInstance().run("postgres");

    }

    /**
     * Test the SecureController process() method. The SecureControllerWrapper
     * is used by this test case to call the tested class's protected methods.
     * The SecureController's inner class SecureControllerTestDelegate actually
     * calls process(), which is a private method. The test fails if the
     * process() method throws an exception, which can be caused by a variety of
     * exceptions (such as a NullPointerException).
     *
     * @throws Exception
     * @see org.akaza.openclinica.control.core.SecureController
     * @see org.akaza.openclinica.control.core.SecureController.SecureControllerTestDelegate
     * @see SecureControllerWrapper
     */
    public void testProcess() throws Exception {
        SecureControllerWrapper testedServlet = new SecureControllerWrapper();
        // set-up the testedServlet context
        MockServletContext servletContext = new MockServletContext(WEB_APP_PATH);
        MockServletConfig config = new MockServletConfig(servletContext);
        testedServlet.init(config);
        BasicDataSource dataSource = null;
        try {
            Context env = (Context) context.lookup("java:comp/env");
            dataSource = (BasicDataSource) env.lookup("SQLPostgres");
        } catch (NamingException e) {
            e.printStackTrace();
        }
        SessionManager mockSessionMgr = new MockSessionManager(dataSource);
        testedServlet.setSessionManager(mockSessionMgr);
        // This URL has no effect right now, because in the application,
        // SecureController
        // is abstract and not mapped in web.xml
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "SecureController");
        request.setRemoteUser(properties.getProperty("remoteUser"));
        /*
         * request.setParameter("eventDefinitionCRFId",
         * properties.getProperty("eventDefinitionCRFId"));
         * request.setParameter("ecId",properties.getProperty("ecId"));
         * request.setParameter("tabId",properties.getProperty("tabId"));
         */

        // Set current language preferences
        Locale locale = request.getLocale();
        ResourceBundleProvider.updateLocale(locale);

        // Set-up typical attributes in session
        MockHttpSession session = new MockHttpSession(null);
        UserAccountBean userBean = new UserAccountBean();
        session.setAttribute("study", new StudyBean());
        session.setAttribute("userBean", userBean);
        session.setAttribute("userRole", new StudyUserRoleBean());

        mockSessionMgr.setUserBean(userBean);
        request.setSession(session);
        HttpServletResponse response = new MockHttpServletResponse();
        testedServlet.setRequest(request);
        testedServlet.setResponse(response);
        testedServlet.setSession(session);
        try {
            SecureControllerWrapper.SecureControllerTestDelegate delegate = testedServlet.new SecureControllerTestDelegate();

            delegate.process(request, response);
        } catch (Exception e) {
            if (dataSource != null) {
                try {
                    dataSource.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
            assertNull("Exception thrown by process()", e);
        }
        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * This method tests the SecureController forwardPage() method. The test
     * will fail if the method raises an exception (e.g., NullPointerException).
     * SecureControllerWrapper is used to call this protected method.
     *
     * @throws Exception
     * @see SecureControllerWrapper
     */
    public void testForwardPage() throws Exception {
        // forwardPage(Page jspPage, boolean checkTrail)

        SecureControllerWrapper testedServlet = new SecureControllerWrapper();
        // set-up the testedServlet context
        MockServletContext servletContext = new MockServletContext(WEB_APP_PATH);
        MockServletConfig config = new MockServletConfig(servletContext);
        testedServlet.init(config);
        BasicDataSource dataSource = null;
        try {
            Context env = (Context) context.lookup("java:comp/env");
            dataSource = (BasicDataSource) env.lookup("SQLPostgres");
        } catch (NamingException e) {
            e.printStackTrace();
        }
        SessionManager mockSessionMgr = new MockSessionManager(dataSource);
        testedServlet.setSessionManager(mockSessionMgr);
        // This URL has no effect right now, because in the application,
        // SecureController
        // is abstract and not mapped in web.xml
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "SecureController");
        request.setRemoteUser(properties.getProperty("remoteUser"));

        // Set current language preferences
        Locale locale = request.getLocale();
        ResourceBundleProvider.updateLocale(locale);

        // Set-up typical attributes in session
        MockHttpSession session = new MockHttpSession(null);
        UserAccountBean userBean = new org.akaza.openclinica.bean.login.UserAccountBean();
        session.setAttribute("study", new org.akaza.openclinica.bean.managestudy.StudyBean());
        session.setAttribute("userBean", userBean);
        session.setAttribute("userRole", new org.akaza.openclinica.bean.login.StudyUserRoleBean());

        mockSessionMgr.setUserBean(userBean);
        request.setSession(session);
        HttpServletResponse response = new MockHttpServletResponse();
        testedServlet.setRequest(request);
        testedServlet.setResponse(response);
        testedServlet.setSession(session);
        try {
            SecureControllerWrapper.SecureControllerTestDelegate delegate = testedServlet.new SecureControllerTestDelegate();
            // process() would be called before forwardPage() would ever be
            // called
            delegate.process(request, response);
            testedServlet.forwardPageWrapper(org.akaza.openclinica.view.Page.ADMIN_EDIT, true);

        } catch (Exception e) {
            if (dataSource != null) {
                try {
                    dataSource.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
            assertNull("Exception thrown by process()", e);
        }
        // assertTrue("Errors is empty.",
        // testedServlet.getErrors().entrySet().isEmpty());
        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * This method tests the SecureController entityIncluded() method. The test
     * will fail if the method raises an exception (e.g., NullPointerException).
     * SecureControllerWrapper is used to call this protected method.
     *
     * @throws Exception
     * @see SecureControllerWrapper
     */
    public void testEntityIncluded() throws Exception {

        SecureControllerWrapper testedServlet = new SecureControllerWrapper();
        // set-up the testedServlet context
        MockServletContext servletContext = new MockServletContext(WEB_APP_PATH);
        MockServletConfig config = new MockServletConfig(servletContext);
        testedServlet.init(config);
        BasicDataSource dataSource = null;
        try {
            Context env = (Context) context.lookup("java:comp/env");
            dataSource = (BasicDataSource) env.lookup("SQLPostgres");
        } catch (NamingException e) {
            e.printStackTrace();
        }
        SessionManager mockSessionMgr = new MockSessionManager(dataSource);
        testedServlet.setSessionManager(mockSessionMgr);
        // This URL has no effect right now, because in the application,
        // SecureController
        // is abstract and not mapped in web.xml
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "SecureController");
        request.setRemoteUser(properties.getProperty("remoteUser"));

        // Set current language preferences
        Locale locale = request.getLocale();
        ResourceBundleProvider.updateLocale(locale);

        // Set-up typical attributes in session
        MockHttpSession session = new MockHttpSession(null);
        UserAccountBean userBean = new org.akaza.openclinica.bean.login.UserAccountBean();
        session.setAttribute("study", new org.akaza.openclinica.bean.managestudy.StudyBean());
        session.setAttribute("userBean", userBean);
        session.setAttribute("userRole", new org.akaza.openclinica.bean.login.StudyUserRoleBean());

        mockSessionMgr.setUserBean(userBean);
        request.setSession(session);
        HttpServletResponse response = new MockHttpServletResponse();
        testedServlet.setRequest(request);
        testedServlet.setResponse(response);
        testedServlet.setSession(session);
        // boolean entityIncluded(int entityId,
        // String userName, AuditableEntityDAO adao, DataSource ds)
        // any entityID
        int entityId = 0;
        try {
            entityId = Integer.parseInt(properties.getProperty("entityId"));
        } catch (NumberFormatException nfe) {
            // a randomly assigned value
            entityId = 151;
        }
        String userName = properties.getProperty("accountName");
        userName = userName == null ? "proot" : userName;
        // This object can be any AuditableEntityDAO;
        // the entityIncluded() method takes an AuditableEntityDAO
        // as its second parameter
        EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);
        try {
            SecureControllerWrapper.SecureControllerTestDelegate delegate = testedServlet.new SecureControllerTestDelegate();
            // process() would be called before entityIncluded() would ever be
            // called.
            // It's a private method so the delegate inner class mut be used
            delegate.process(request, response);
            // The SecureControllerWrapper can be used to call entityIncluded(),
            // since it's
            // a protected, not private, method
            testedServlet.entityIncludedWrapper(entityId, userName, eventCRFDAO, dataSource);

        } catch (Exception e) {
            if (dataSource != null) {
                try {
                    dataSource.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
            assertNull("Exception thrown by process()", e);
        }
        // assertTrue("Errors is empty.",
        // testedServlet.getErrors().entrySet().isEmpty());
        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

    }

    /**
     * This method is called lastly, after the test case's test methods.
     *
     * @throws Exception
     * @see <a
     *      href='http://www.mockejb.org/javadoc/org/mockejb/jndi/MockContextFactory.html#revertSetAsInitial()'>The
     *      javadoc for the revertSetAsInitial() method</a>
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        MockContextFactory.revertSetAsInitial();
    }

}
