/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.core.form.DiscrepancyValidator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * Performs 'administrative editing' action for study director/study coordinator
 */
public class AdministrativeEditingServlet extends DataEntryServlet {
  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#getServletPage()
   */
  protected Page getServletPage() {
    String tabId = fp.getString("tab", true);
    String sectionId = fp.getString(DataEntryServlet.INPUT_SECTION_ID, true);
    String eventCRFId = fp.getString(INPUT_EVENT_CRF_ID, true);
    if (StringUtil.isBlank(sectionId) || StringUtil.isBlank(tabId)) {
      return Page.ADMIN_EDIT_SERVLET;
    } else {
      Page target = Page.ADMIN_EDIT_SERVLET.cloneWithNewFileName(Page.ADMIN_EDIT_SERVLET.getFileName()
    	  + "?eventCRFId=" + eventCRFId + "&sectionId="+ sectionId + "&tab=" + tabId);
      return target;
    }
    
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#getJSPPage()
   */
  protected Page getJSPPage() {
    return Page.ADMIN_EDIT;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#validateInputOnFirstRound()
   */
  protected boolean validateInputOnFirstRound() {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
   */
  protected void mayProceed() throws InsufficientPermissionException {
    getInputBeans();

    DataEntryStage stage = ecb.getStage();
    Role r = currentRole.getRole();

    if (!SubmitDataServlet.maySubmitData(ub, currentRole)) {
      String exceptionName = "no permission to perform validation";
      String noAccessMessage = "You may not perform administrative editing on a CRF in this study.  Please change your active study or contact the Study Coordinator.";

      addPageMessage(noAccessMessage);
      throw new InsufficientPermissionException(Page.MENU, exceptionName, "1");
    }
    logger.info("stage name:" + stage.getName());
    if (stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE)) {
      if (!r.equals(Role.STUDYDIRECTOR) && !r.equals(Role.COORDINATOR)) {
        addPageMessage("You don't have correct privilege to perform administrative editing. Please change your active study or contact the System Administrator.");
        throw new InsufficientPermissionException(Page.SUBMIT_DATA_SERVLET,
            "no permission to perform administrative editing", "1");
      }
    }

    else {
      addPageMessage("You may not perform administrative editing on this CRF. " +
      		"This may be because the CRF has not been completed by the user doing " +
      		"data entry, or because your role does not have sufficient privileges to " +
      		"perform administrative editing on this CRF. If you think you are receiving" +
      		" this message in error, please contact your system administrator.");
      throw new InsufficientPermissionException(Page.SUBMIT_DATA_SERVLET, "not correct stage", "1");
    }

    return;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#setEventCRFAnnotations(java.lang.String)
   */
  protected void setEventCRFAnnotations(String annotations) {
    ecb.setAnnotations(annotations);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#loadDBValues()
   */
  protected boolean shouldLoadDBValues(DisplayItemBean dib) {   
    if (dib.getData().getStatus() == null) {
      logger.info("dib data status is null" + dib.getData().getId());      
    } else if (!dib.getData().getStatus().equals(Status.UNAVAILABLE)) {
      return false;
    }

    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#validateDisplayItemBean(org.akaza.openclinica.core.form.Validator,
   *      org.akaza.openclinica.bean.submit.DisplayItemBean)
   */
  protected DisplayItemBean validateDisplayItemBean(DiscrepancyValidator v, DisplayItemBean dib) {    
   
	dib = loadFormValue(dib);
	
	return dib;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#getBlankItemStatus()
   */
  protected Status getBlankItemStatus() {
    return Status.UNAVAILABLE;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#getNonBlankItemStatus()
   */
  protected Status getNonBlankItemStatus() {
    return Status.UNAVAILABLE;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#getEventCRFAnnotations()
   */
  protected String getEventCRFAnnotations() {
    return ecb.getAnnotations();
  }

}