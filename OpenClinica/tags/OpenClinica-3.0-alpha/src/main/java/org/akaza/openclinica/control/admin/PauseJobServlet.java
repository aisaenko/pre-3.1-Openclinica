package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;
// import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdScheduler;

/**
 * PauseJobServlet, a small servlet to pause/unpause a trigger in the scehduler.
 * The basic premise, you provide a name which has been validated by JavaScript on the JSP side with a simple confirm dialog box.
 * You get here - the job is either paused or unpaused.
 * Possible complications, if we start using Priority for other things.
 * @author Tom Hickerson, 2009
 *
 */
public class PauseJobServlet extends SecureController {

	private static String SCHEDULER = "schedulerFactoryBean";
	private static String groupName = "DEFAULT";
	private StdScheduler scheduler;
	// private SimpleTrigger trigger;
	
	@Override
	protected void mayProceed() throws InsufficientPermissionException {
		// TODO copied from CreateJobExport - DRY? tbh
		if (ub.isSysAdmin()) {
            return;
        }
        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || 
        		currentRole.getRole().equals(Role.COORDINATOR)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU, resexception.getString("not_allowed_access_extract_data_servlet"), "1");// TODO
        // above copied from create dataset servlet, needs to be changed to allow only admin-level users

	}
	
	private StdScheduler getScheduler() {
		scheduler = this.scheduler != null ? scheduler : (StdScheduler) SpringServletAccess.getApplicationContext(context).getBean(SCHEDULER);
		return scheduler;
	}//also perhaps DRY, tbh

	@Override
	protected void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		String triggerName = fp.getString("tname");
		String deleteMe = fp.getString("del");
		scheduler = getScheduler();
		Trigger trigger = scheduler.getTrigger(triggerName, groupName);
		try {
			//System.out.println("found delete key: "+deleteMe);
			//System.out.println("found is sys admin: "+ub.isSysAdmin());
			//System.out.println("found is tech admin: "+ub.isTechAdmin());
			if (("y".equals(deleteMe)) && (ub.isSysAdmin())) {
				scheduler.deleteJob(triggerName, groupName);
				// set return message here
				System.out.println("deleted job: "+triggerName);
				addPageMessage("The following job "+triggerName+" and its corresponding Trigger have been deleted from the system.");
			} else {

				if (scheduler.getTriggerState(triggerName, groupName) == Trigger.STATE_PAUSED) {
					scheduler.resumeTrigger(triggerName, groupName);
					// trigger.setPriority(Trigger.DEFAULT_PRIORITY);
					System.out.println("-- resuming trigger! "+triggerName);
					addPageMessage("This trigger "+triggerName+" has been resumed and will continue to run until paused or deleted.");
					// set message here
				} else {
					scheduler.pauseTrigger(triggerName, groupName);
					// trigger.setPriority(Trigger.STATE_PAUSED);
					System.out.println("-- pausing trigger! "+triggerName);
					addPageMessage("This trigger "+triggerName+" has been paused, and will not run again until it is restored.");
					// set message here
				}
			}
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// all validation done on JSP side
		// forward back to view job servlet here
		// set a message
		forwardPage(Page.VIEW_JOB_SERVLET);
	}

}
