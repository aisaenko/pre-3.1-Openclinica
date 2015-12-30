package org.akaza.openclinica.servlettests;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.core.SQLFactory;
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
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This JUnit TestCase class tests the DataEntryServlet processRequest() method.
 * The test is an "out of container" servlet test, meaning that it does not
 * require Tomcat or another servlet container to run. The class uses mock
 * objects (from the Spring and MockEJB frameworks) to simulate the Servlet API
 * under which the DataEntryServlet normally runs. The class obtains various
 * test parameters, such as values that would typically be part of a POST
 * request or sent to the servlet in a query string, from a properties file (at
 * this writing, the file is called servlet_properties and resides in the
 * /test/java directory). This test runs with a "basedir" java VM parameter;
 * e.g.,-Dbasedir=/Users/bruceperry/oc_trunk/trunk.
 *
 * @author Bruce W. Perry 01/2008
 * @see org.springframework.mock.web
 * @see org.mockejb.jndi
 * @see org.akaza.openclinica.servlettests.DataEntryServletWrapper
 *
 */
public class DataEntryServletTest extends TestCase {
    // The JNDI Context that will refer to the DataSource, which
    // provides the connection to PostGreSQL
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
        return new TestSuite(DataEntryServletTest.class);
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
            throw new IllegalStateException("The test case failed to load the properties file. "
                + "Please make sure that servlet_properties is located in the proper filepath: e.g.,/trunk/test/src/java");
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
     * Test the DataEntryServlet processRequest() method. We use the
     * DataEntryServletWrapper class class to call the test method, since
     * DataEntryServlet is an abstract class. The test will fail if
     * processRequest() throws an exception, which indicates a variety of
     * problems that can arise during the running of this complex method.
     *
     * @throws IOException
     * @throws ServletException
     * @see DataEntryServletWrapper
     */
    public void testProcessRequestGet() throws IOException, ServletException {

        // Typical query strings:
        // module=&crfId=379&crfVersionId=619&tabId=1
        // eventDefinitionCRFId=&ecId=699&tabId=1
        // eventDefinitionCRFId=102&crfVersionId=633&tabId=1

        // The DataEntryServletWrapper class is necessary because
        // DataEntryServlet is an abstract
        // class
        DataEntryServletWrapper servlet = new DataEntryServletWrapper();
        // set-up the servlet context
        MockServletContext servletContext = new MockServletContext(WEB_APP_PATH);
        MockServletConfig config = new MockServletConfig(servletContext);
        servlet.init(config);
        BasicDataSource dataSource = null;
        try {
            Context env = (Context) context.lookup("java:comp/env");
            dataSource = (BasicDataSource) env.lookup("SQLPostgres");
        } catch (NamingException e) {
            e.printStackTrace();
        }
        SessionManager mockSessionMgr = new MockSessionManager(dataSource);
        servlet.setSessionManager(mockSessionMgr);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "DataEntryServlet");
        request.setParameter("eventDefinitionCRFId", properties.getProperty("eventDefinitionCRFId"));
        request.setParameter("ecId", properties.getProperty("ecId"));
        request.setParameter("tabId", properties.getProperty("tabId"));

        // Set current language preferences
        Locale locale = request.getLocale();
        ResourceBundleProvider.updateLocale(locale);

        // Set-up typical attributes in session
        MockHttpSession session = new MockHttpSession(null);
        session.setAttribute("panel", new org.akaza.openclinica.view.StudyInfoPanel());
        ArrayList trailList = new ArrayList();
        trailList.add(new org.akaza.openclinica.view.BreadcrumbBean("beanName", "myURL", org.akaza.openclinica.bean.core.Status.AVAILABLE));
        session.setAttribute("trail", trailList);
        StudyBean studyBean = new org.akaza.openclinica.bean.managestudy.StudyBean();
        studyBean.setId(1);
        session.setAttribute("study", studyBean);
        session.setAttribute("userBean1", new org.akaza.openclinica.bean.login.UserAccountBean());
        session.setAttribute("fdnotes", new org.akaza.openclinica.core.form.FormDiscrepancyNotes());
        session.setAttribute("userRole", new org.akaza.openclinica.bean.login.StudyUserRoleBean());
        servlet.setStudy((StudyBean) session.getAttribute("study"));

        request.setSession(session);

        HttpServletResponse response = new MockHttpServletResponse();
        servlet.setRequest(request);
        servlet.setResponse(response);
        servlet.setSession(session);
        // Apparently, the inheriting classes set the value of "ecb"; so if this
        // initialization doesn't take place then a lot of NullPointers will be
        // thrown
        EventCRFBean eBean = new EventCRFBean();
        int iD = 0;
        try {
            iD = Integer.parseInt(properties.getProperty("ecId"));
        } catch (NumberFormatException nfe) {
            // Just a dummy ID so the test can run
            iD = 20;
        }
        eBean.setId(iD);
        servlet.setEventCRFBean(eBean);
        servlet.setFormProcessor(new FormProcessor(request));
        servlet.initializeMembers(request.getLocale());
        try {
            servlet.processRequestWrapper();
        } catch (Exception e) {
            if (dataSource != null) {
                try {
                    dataSource.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
            assertNull("DataEntryServlet's processRequest() method threw an exception.", e);
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
