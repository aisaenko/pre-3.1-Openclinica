/*
 * Created on Sep 23, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.akaza.openclinica.control.techadmin;

import java.util.Locale;
import java.util.ResourceBundle;

import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.impl.SimpleLog;
//
//import org.quartz.*;
//import org.quartz.impl.jdbcjobstore.*;

/**
 * @author thickerson
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ViewSchedulerServlet extends SecureController {
	
	Locale locale;
	//<  ResourceBundleresexception; 
	
	protected void processRequest() throws Exception {
//		Log psqlLogger = new SimpleLog("QuartzJobLog");
//		//ServletContext ctx = request.getSession().getServletContext();
//		PostgreSQLDelegate delegate = new PostgreSQLDelegate(psqlLogger,
//				"qrtz_",
//				"OpenClinica Jobs");
//		CronTrigger trigger = (CronTrigger)delegate.selectTrigger(sm.getConnection(), 
//				"Refresh Data View",
//				"OpenClinica Jobs");
//		
	}
	
	protected void mayProceed() throws InsufficientPermissionException {
		
		locale = request.getLocale();
		//< resexception=ResourceBundle.getBundle("org.akaza.openclinica.i18n.exceptions",locale);
		
		if (!ub.isTechAdmin()) {
			throw new InsufficientPermissionException(Page.MENU, "You may not perform technical administrative functions", "1");
		}
		
		return;
	}
	
	protected String getAdminServlet() {
		return SecureController.ADMIN_SERVLET_CODE;
	}

}
