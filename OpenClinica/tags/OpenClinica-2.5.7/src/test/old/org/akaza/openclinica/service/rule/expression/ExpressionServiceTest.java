package org.akaza.openclinica.service.rule.expression;

import org.mockejb.jndi.MockContextFactory;

import java.io.File;
import java.util.Properties;

import javax.naming.Context;

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
public class ExpressionServiceTest extends TestCase {
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
        return new TestSuite(ExpressionServiceTest.class);
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

    }

    public void testStatement() {

        org.apache.commons.dbcp.BasicDataSource ds = new org.apache.commons.dbcp.BasicDataSource();
        ExpressionService expressionService = new ExpressionService(ds);

        // Syntax
        assertEquals(false, expressionService.checkSyntax("StudyEventName[ALL].FormName1.ItemGroupName[ALL].ItemName11."));
        assertEquals(false, expressionService.checkSyntax(".StudyEventName[ALL].FormName1.ItemGroupName[ALL].ItemName11."));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL]..ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].ITEM_GROUP_OID[ALL]..ITEM_OID_11"));

        // STUDY_EVENT_DEFINITION_OID
        assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
        assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[123].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
        assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[10].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
        assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[1].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
        assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[10004].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
        assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID_12[123].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
        assertEquals(true, expressionService.checkSyntax("STUDY12_EVENT_OID_12[123].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
        assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID.FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[.FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID].FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[Krikor].FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[].FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[KK].FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVE[NT_OID[].FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("[].FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("[.FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID$[12].FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[0].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[123].FORM_OID.ITEM_GROUP_OID[0].ITEM_OID_11"));

        // CRF_OID or CRF_VERSION_OID
        assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
        assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID_.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
        assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID_123.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
        assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM123_2__OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID[.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID[ALL].ITEM_GROUP_OID[ALL].ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID[].ITEM_GROUP_OID[ALL].ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID].ITEM_GROUP_OID[ALL].ITEM_OID_11"));

        // ITEM_GROUP_OID
        assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
        assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[23].ITEM_OID_11"));
        assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[2].ITEM_OID_11"));
        assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[100].ITEM_OID_11"));
        assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
        assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID__[ALL].ITEM_OID_11"));
        assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID_123[ALL].ITEM_OID_11"));
        assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.12_ITEM_GROUP_12_OID[ALL].ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.12_iTEM_GROUP_12_OID[ALL].ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID].ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[.ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[].ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALLL].ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL]$.ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL]_KK.ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[0].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[0].ITEM_OID_11"));

        // ITEM_OID
        assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
        assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OI123D_11"));
        assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITE123M_OID_11"));
        assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11["));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11]"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11[]"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11$"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OId_11"));
        assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM-_OID_11"));

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        MockContextFactory.revertSetAsInitial();
    }
}
