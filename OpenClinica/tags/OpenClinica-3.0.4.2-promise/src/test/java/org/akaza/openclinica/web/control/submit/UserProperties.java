package org.akaza.openclinica.web.control.submit;

import java.util.Locale;
import java.util.Properties;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.templates.HibernateOcDbTestCase;
import org.akaza.openclinica.templates.OcDbTestCase;
import org.apache.commons.dbcp.BasicDataSource;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import junit.framework.TestCase;

public class UserProperties {

	private UserAccountBean userAccountBean;
	private DataSource dataSource;
	private final String locale;
	private final String dbName;
	 protected Properties properties = new Properties();
	public UserProperties(){
		loadProperties();   
		 dbName = properties.getProperty("dbName");
		locale = properties.getProperty("locale");
	        initializeLocale();
	        initializeQueriesInXml();
	       // setUpContext();
	}
	
	public void setUserAccountBean(UserAccountBean userBean)
	{
		this.userAccountBean = userBean;
	}
	
	public UserAccountBean getUserAccountBean()
	{
		return this.userAccountBean;
	}
	
	
	/*public void testFindByPK()
	{
		UserAccountBean userBean = new UserAccountBean();
		//userBean.setId(1);
		UserAccountDAO userAccount = new UserAccountDAO(getDataSource());
		userBean =(UserAccountBean) userAccount.findByPK(1);
		setUserAccountBean(userBean);
	}*/
	private void createDataSource(){
		BasicDataSource datasource = new BasicDataSource();
		datasource.setDriverClassName("org.postgresql.Driver");
		datasource.setUrl("dbc:postgresql://localhost:5432/OpenClinica3-SNAPSHOT");
		datasource.setUsername("clinica");
		datasource.setPassword("clinica");
		this.dataSource = datasource;
	}
	 public DataSource getDataSource() {   
	      return dataSource;   
	   }  
	  public void loadProperties() {
	        try {
	            properties.load(HibernateOcDbTestCase.class.getResourceAsStream(getPropertiesFilePath()));
	        } catch (Exception ioExc) {
	            ioExc.printStackTrace();
	        }
	    }
	 
	  	  


	
	
	    public void initializeLocale() {
	        ResourceBundleProvider.updateLocale(new Locale(locale));
	    }

	    /**
	     * Instantiates SQLFactory and all the xml files that contain the queries that are used in our dao class
	     */
	    public void initializeQueriesInXml() {
	        String baseDir = System.getProperty("basedir");
	        if (baseDir == null || "".equalsIgnoreCase(baseDir)) {
	            throw new IllegalStateException(
	                    "The system properties basedir were not made available to the application. Therefore we cannot locate the test properties file.");
	        }
	        SQLFactory.JUNIT_XML_DIR =
	            baseDir + "/" + "src" + "/" + "main" + "/" + "webapp" + "/" + "properties" + "/";
	        SQLFactory.getInstance().run(dbName);
	    }

	    private String getPropertiesFilePath() {
	        // Don't use File.separator, windows handles / just fine
	        return  "/test.properties";
	    }

	    /**
	     * Gets the path and the name of the xml file holding the data. Example if your Class Name is called
	     * org.akaza.openclinica.service.rule.expression.TestExample.java you need an xml data file in resources folder under same path + testdata + same Class Name
	     * .xml org/akaza/openclinica/service/rule/expression/testdata/TestExample.xml
	     * 
	     * @return path to data file
	     */
	    private String getTestDataFilePath() {
	        // Don't use File.separator, windows handles / just fine
	        StringBuffer path = new StringBuffer("/");
	        path.append(getClass().getPackage().getName().replace(".", "/"));
	        path.append( "/testdata/");
	        path.append(getClass().getSimpleName() + ".xml");
	        System.out.println(path.toString());
	        return path.toString();
	    }


	    public String getDbName() {
	        return dbName;
	    }
}
