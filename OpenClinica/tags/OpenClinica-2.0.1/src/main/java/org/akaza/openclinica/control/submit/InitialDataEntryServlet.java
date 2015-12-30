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
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.core.form.DiscrepancyValidator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author ssachs
 */
public class InitialDataEntryServlet extends DataEntryServlet {

	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
	 */
	protected void mayProceed() throws InsufficientPermissionException {
		getInputBeans();

		DataEntryStage stage = ecb.getStage();
		Role r = currentRole.getRole();
		
		if (stage.equals(DataEntryStage.UNCOMPLETED)) {
			if (!SubmitDataServlet.maySubmitData(ub, currentRole)) {
				String exceptionName = "no permission to perform data entry";
				String noAccessMessage = "You may not perform data entry on a CRF in this study.  Please change your active study or contact the Study Coordinator.";
				
				addPageMessage(noAccessMessage);
				throw new InsufficientPermissionException(Page.MENU, exceptionName, "1");
			}
		}
		else if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY)) {
			if ((ub.getId() != ecb.getOwnerId()) 
					&& !r.equals(Role.STUDYDIRECTOR)
					&& !r.equals(Role.COORDINATOR)) {
				addPageMessage("You may not perform data entry on this event CRF, because you are not the owner, study director, or study coordinator.");
				throw new InsufficientPermissionException(Page.SUBMIT_DATA_SERVLET, "non-owner attempting data entry on event CRF", "1");
			}
		}
		else {
			addPageMessage("You may not enter data on this event CRF, because initial data entry has already been completed or the event CRF is not available.");
			throw new InsufficientPermissionException(Page.SUBMIT_DATA_SERVLET, "using initial data entry when event CRF has completed initial data entry", "1");
		}
		
		return ;
	}

	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#validateInputOnFirstRound()
	 */
	protected boolean validateInputOnFirstRound() {
		return true;
	}
	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#validateDisplayItemBean(org.akaza.openclinica.core.form.Validator, org.akaza.openclinica.bean.submit.DisplayItemBean)
	 */
	protected DisplayItemBean validateDisplayItemBean(DiscrepancyValidator v,
			DisplayItemBean dib) {
		ItemBean ib = dib.getItem();
		org.akaza.openclinica.bean.core.ResponseType rt = dib.getMetadata().getResponseSet().getResponseType();

		
		// note that this step sets us up both for
		// displaying the data on the form again, in the event of an error
		// and sending the data to the database, in the event of no error
		dib = loadFormValue(dib);
		
		// types TEL and ED are not supported yet
		if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXT) ||
				rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXTAREA)) {
			dib = validateDisplayItemBeanText(v, dib);
		}
		else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.RADIO) ||
				rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECT)) {
			dib = validateDisplayItemBeanSingleCV(v, dib);
		}
		else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CHECKBOX) ||
				rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECTMULTI)) {
			dib = validateDisplayItemBeanMultipleCV(v, dib);
		}
		
		return dib;
	}

	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#getBlankItemStatus()
	 */
	protected Status getBlankItemStatus() {
		return Status.AVAILABLE;
	}

	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#getNonBlankItemStatus()
	 */
	protected Status getNonBlankItemStatus() {
		return edcb.isDoubleEntry() ? Status.PENDING : Status.UNAVAILABLE;
	}
	
	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#getEventCRFAnnotations()
	 */
	protected String getEventCRFAnnotations() {
		return ecb.getAnnotations();
	}
	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#setEventCRFAnnotations(java.lang.String)
	 */
	protected void setEventCRFAnnotations(String annotations) {
		ecb.setAnnotations(annotations);
	}
	
	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#getJSPPage()
	 */
	protected Page getJSPPage() {
		return Page.INITIAL_DATA_ENTRY;
	}
	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#getServletPage()
	 */
	protected Page getServletPage() {
        String tabId= fp.getString("tab",true);
        String sectionId= fp.getString(DataEntryServlet.INPUT_SECTION_ID,true);
		String eventCRFId = fp.getString(INPUT_EVENT_CRF_ID,true);
        if (StringUtil.isBlank(sectionId) || StringUtil.isBlank(tabId)) {
        return Page.INITIAL_DATA_ENTRY_SERVLET;
        } else {
          Page target = Page.INITIAL_DATA_ENTRY_SERVLET ;
          target.setFileName(target.getFileName()+"?eventCRFId=" +eventCRFId + "&sectionId="+ sectionId + "&tab=" + tabId);
          return target;
        }
	}
	
	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#loadDBValues()
	 */
	protected boolean shouldLoadDBValues(DisplayItemBean dib) {
		return true;
	}
}
