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
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.core.form.DiscrepancyValidator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author ssachs
 */
public class DoubleDataEntryServlet extends DataEntryServlet {

  private boolean userIsOwnerAndLessThanTwelveHoursHavePassed() {
    boolean userIsOwner = (ub.getId() == ecb.getOwnerId());
    boolean lessThanTwelveHoursHavePassed = !DisplayEventCRFBean
        .initialDataEntryCompletedMoreThanTwelveHoursAgo(ecb);

    return userIsOwner && lessThanTwelveHoursHavePassed;
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
      String noAccessMessage = "You may not perform validation on a CRF in this study.  Please change your active study or contact the Study Coordinator.";

      addPageMessage(noAccessMessage);
      throw new InsufficientPermissionException(Page.MENU, exceptionName, "1");
    }

    if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE)) {
      if (userIsOwnerAndLessThanTwelveHoursHavePassed() && !r.equals(Role.STUDYDIRECTOR)
          && !r.equals(Role.COORDINATOR)) {
        addPageMessage("Since you performed the initial round of data entry on this Event CRF, and less than twelve hours have passed since the time data entry was completed, you may not perform validation right now.");
        throw new InsufficientPermissionException(
            Page.SUBMIT_DATA_SERVLET,
            "owner attempting double data entry on event CRF less than 12 hours since data entry completed",
            "1");
      }
    } else if (stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
      if ((ub.getId() != ecb.getValidatorId()) && !r.equals(Role.STUDYDIRECTOR)
          && !r.equals(Role.COORDINATOR)) {
        addPageMessage("Validation has already begun on this Event CRF.  Since you are not the person who began validation, you may not continue validation.");
        throw new InsufficientPermissionException(Page.SUBMIT_DATA_SERVLET,
            "non-validator attempting double data entry on event CRF", "1");
      }
    } else {
      addPageMessage("You may not perform validation on this event CRF, because validation has already been completed.");
      throw new InsufficientPermissionException(Page.SUBMIT_DATA_SERVLET,
          "using double data entry when event CRF has completed double data entry", "1");
    }

    return;
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
    String inputName = getInputName(dib);

    // types TEL and ED are not supported yet
    if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXT)
        || rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXTAREA)) {
      dib = validateDisplayItemBeanText(v, dib);
      if (!StringUtil.isBlank(dib.getData().getValue())) {
        v
            .addValidation(inputName, Validator.MATCHES_INITIAL_DATA_ENTRY_VALUE, dib.getData(),
                false);
        v.setErrorMessage("The value you specify does not match the value:"
            + dib.getData().getValue() + " from initial data entry.");
      }

    } else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.RADIO)
        || rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECT)) {
      dib = validateDisplayItemBeanSingleCV(v, dib);
      if (!StringUtil.isBlank(dib.getData().getValue())) {
        v
            .addValidation(inputName, Validator.MATCHES_INITIAL_DATA_ENTRY_VALUE, dib.getData(),
                false);
        v.setErrorMessage("The value you specify does not match the value:"
            + dib.getData().getValue() + " from initial data entry.");
      }

    } else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CHECKBOX)
        || rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECTMULTI)) {
      dib = validateDisplayItemBeanMultipleCV(v, dib);
      if (!StringUtil.isBlank(dib.getData().getValue())) {
        v.addValidation(inputName, Validator.MATCHES_INITIAL_DATA_ENTRY_VALUE, dib.getData(), true);

        v.setErrorMessage("The value you specify does not match the value:"
            + dib.getData().getValue() + " from initial data entry.");
      }

    }

    // note that this step sets us up both for
    // displaying the data on the form again, in the event of an error
    // and sending the data to the database, in the event of no error
    //
    // we have to do this after adding the validations, so that we don't
    // overwrite the value the item data bean had from initial data entry
    // before the validator stores it as part of the Matches Initial Data Entry
    // Value validation
    dib = loadFormValue(dib);

    return dib;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#getBlankItemStatus()
   */
  protected Status getBlankItemStatus() {
    return Status.PENDING;
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
    return ecb.getValidatorAnnotations();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#setEventCRFAnnotations(java.lang.String)
   */
  protected void setEventCRFAnnotations(String annotations) {
    ecb.setValidatorAnnotations(annotations);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#getJSPPage()
   */
  protected Page getJSPPage() {
    return Page.DOUBLE_DATA_ENTRY;
  }

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
      return Page.DOUBLE_DATA_ENTRY_SERVLET;
    } else {
      Page target = Page.DOUBLE_DATA_ENTRY_SERVLET;
      target.setFileName(target.getFileName() + "?eventCRFId=" + eventCRFId + "sectionId="
          + sectionId + "tab=" + tabId);
      return target;
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#loadDBValues()
   */
  protected boolean shouldLoadDBValues(DisplayItemBean dib) {
    System.out.println("status: " + dib.getData().getStatus().getName() + "; data: "
        + dib.getData().getId());
    if (!dib.getData().getStatus().equals(Status.UNAVAILABLE)) {
      System.out.println("status don't match..");
      return false;
    }

    return true;
  }
}
