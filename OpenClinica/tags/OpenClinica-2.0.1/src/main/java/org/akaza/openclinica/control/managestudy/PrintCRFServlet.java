/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2006 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplaySectionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.control.core.Utils;
import org.akaza.openclinica.control.submit.DataEntryServlet;
import org.akaza.openclinica.core.form.DiscrepancyValidator;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * @author Krikor Krumlian 10/26/2006
 * 
 * 
 * View a CRF version section data entry
 */
public class PrintCRFServlet extends DataEntryServlet {
  /**
   * Checks whether the user has the correct privilege
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }
    if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
        || currentRole.getRole().equals(Role.COORDINATOR)
        || currentRole.getRole().equals(Role.INVESTIGATOR)
        || currentRole.getRole().equals(Role.RESEARCHASSISTANT)) {
      return;
    }

    addPageMessage("You don't have correct privilege in your current active study. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.MENU_SERVLET, "not director", "1");

  }
  
  public void processRequest() throws Exception {
	    FormProcessor fp = new FormProcessor(request);
	    int eventCRFId = fp.getInt("ecId");
	    SectionDAO sdao = new SectionDAO(sm.getDataSource());
	    CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
	    allSectionBeans = new ArrayList();
	    ArrayList sectionBeans = new ArrayList();
	    int crfVersionId = fp.getInt("id");

	    if (crfVersionId == 0) {
	      addPageMessage("Please choose a crf to view details.");
	      forwardPage(Page.CRF_LIST_SERVLET);
	    }
	    else
	    {
	    	super.ecb = new EventCRFBean();
	        super.ecb.setCRFVersionId(crfVersionId);
	    	CRFVersionBean version = (CRFVersionBean) cvdao.findByPK(crfVersionId);
	        ArrayList sects = (ArrayList) sdao.findByVersionId(version.getId());
	        for (int i = 0; i < sects.size(); i++) {
	            SectionBean sb = (SectionBean) sects.get(i);
	            super.sb = sb;
	            int sectId = sb.getId();
	            if (sectId > 0) {
	          	  allSectionBeans.add((SectionBean) sdao.findByPK(sectId));
	            }
	        }
	        sectionBeans = super.getAllDisplayBeans();
	    }
	    DisplaySectionBean dsb = super.getDisplayBean(); 
	    request.setAttribute("allSections", sectionBeans);
	    request.setAttribute("displayAllCRF","1");
	    request.setAttribute(BEAN_DISPLAY, dsb);
	    request.setAttribute(BEAN_ANNOTATIONS, ecb.getAnnotations());
	    request.setAttribute("sec", sb);
	    request.setAttribute("EventCRFBean", super.ecb);
	    forwardPage(Page.VIEW_SECTION_DATA_ENTRY_PRINT);
	  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#getBlankItemStatus()
   */
  protected Status getBlankItemStatus() {
    return Status.AVAILABLE;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#getNonBlankItemStatus()
   */
  protected Status getNonBlankItemStatus() {
    return edcb.isDoubleEntry() ? Status.PENDING : Status.UNAVAILABLE;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#getEventCRFAnnotations()
   */
  protected String getEventCRFAnnotations() {
    return ecb.getAnnotations();
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
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#getJSPPage()
   */
  protected Page getJSPPage() {
    return Page.VIEW_SECTION_DATA_ENTRY;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#getServletPage()
   */
  protected Page getServletPage() {
    return Page.VIEW_SECTION_DATA_ENTRY_SERVLET;
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
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#validateDisplayItemBean(org.akaza.openclinica.core.form.Validator,
   *      org.akaza.openclinica.bean.submit.DisplayItemBean)
   */
  protected DisplayItemBean validateDisplayItemBean(DiscrepancyValidator v, DisplayItemBean dib) {
    ItemBean ib = dib.getItem();
    org.akaza.openclinica.bean.core.ResponseType rt = dib.getMetadata().getResponseSet()
        .getResponseType();

    // note that this step sets us up both for
    // displaying the data on the form again, in the event of an error
    // and sending the data to the database, in the event of no error
    dib = loadFormValue(dib);

    // types TEL and ED are not supported yet
    if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXT)
        || rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXTAREA)) {
      dib = validateDisplayItemBeanText(v, dib);
    } else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.RADIO)
        || rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECT)) {
      dib = validateDisplayItemBeanSingleCV(v, dib);
    } else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CHECKBOX)
        || rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECTMULTI)) {
      dib = validateDisplayItemBeanMultipleCV(v, dib);
    }

    return dib;
  }

  
	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#loadDBValues()
	 */
	protected boolean shouldLoadDBValues(DisplayItemBean dib) {
		return true;
	}
}