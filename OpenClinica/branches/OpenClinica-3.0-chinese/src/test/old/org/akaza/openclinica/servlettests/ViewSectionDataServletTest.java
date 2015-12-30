package org.akaza.openclinica.servlettests;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.managestudy.ViewSectionDataEntryServlet;
import org.akaza.openclinica.core.SessionManager;
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
 * A Junit test case for a servlet or controller class. The class uses MockEJB
 * (for JNDI and Datasource functionality) and the org.springframework.mock.web
 * package from the Spring framework to create mock implementations of the
 * servlet API and environment.
 */
public class ViewSectionDataServletTest extends TestCase {
    // The JNDI Context that will refer to the DataSource, which
    // provides the connection to PostGreSQL
    private Context context;
    private static final String WEB_APP_PATH = "src" + File.separator + "main" + File.separator + "webapp";
    private static final String PROPERTIES_PATH = "src" + File.separator + "test" + File.separator + "java";
    // The database-connection parameters and the inputs like query strings
    // are stored in properties
    private Properties properties = new Properties();

    public static Test suite() {
        return new TestSuite(ViewSectionDataServletTest.class);
    }

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

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        MockContextFactory.revertSetAsInitial();
    }

    /**
     * Test the servlet's processRequest() method. The test fails if the method
     * throws an exception, or if the "errors" Map the servlet uses contains
     * errors.
     *
     * @throws IOException
     * @throws ServletException
     */
    public void testProcessRequestGet() throws IOException, ServletException {

        // Typical query strings:
        // module=&crfId=379&crfVersionId=619&tabId=1
        // eventDefinitionCRFId=&ecId=699&tabId=1
        // eventDefinitionCRFId=102&crfVersionId=633&tabId=1

        ViewSectionDataEntryServlet servlet = new ViewSectionDataEntryServlet();
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
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "ViewSectionDataEntry");
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
        try {
            servlet.processRequest();
        } catch (Exception e) {
            if (dataSource != null) {
                try {
                    dataSource.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
            assertNull("ViewSectionDataEntryServlet's processRequest() method threw an Exception.", e);
        }
        assertTrue("Errors is empty.", servlet.getErrors().entrySet().isEmpty());
        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void testMayAccess() throws IOException, ServletException {

        ViewSectionDataEntryServlet servlet = new ViewSectionDataEntryServlet();
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
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "ViewSectionDataEntry");
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
        session.setAttribute("study", new org.akaza.openclinica.bean.managestudy.StudyBean());
        UserAccountBean ub = new UserAccountBean();
        ub.setName(properties.getProperty("accountName"));
        servlet.setUserAccountBean(ub);

        session.setAttribute("userBean", ub);
        session.setAttribute("fdnotes", new org.akaza.openclinica.core.form.FormDiscrepancyNotes());
        session.setAttribute("userRole", new org.akaza.openclinica.bean.login.StudyUserRoleBean());

        request.setSession(session);
        HttpServletResponse response = new MockHttpServletResponse();
        servlet.setRequest(request);
        servlet.setResponse(response);
        servlet.setSession(session);
        try {
            servlet.mayAccess();
        } catch (Exception e) {
            if (dataSource != null) {
                try {
                    dataSource.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
            assertNull("ViewSectionDataEntryServlet's mayAccess() method threw an Exception.", e);
        }

        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    /*
     * private void closeDataSource(DataSource dataSource) { try {
     * java.sql.Connection con = dataSource.getConnection(); if(con != null)
     * con.close(); } catch (SQLException e1) { e1.printStackTrace(); } }
     */
}
