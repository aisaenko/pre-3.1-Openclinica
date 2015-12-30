/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.submit;
import java.util.List;
import java.util.Locale;

import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplayItemGroupBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.core.form.DiscrepancyValidator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author ssachs
 */
public class DoubleDataEntryServlet extends DataEntryServlet {
	
	Locale locale;
	//<  ResourceBundlerespage,restext,resexception,resword;

  private static final String COUNT_VALIDATE = "countValidate";
  
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
	  
	  locale = request.getLocale();
	  //< respage = ResourceBundle.getBundle("org.akaza.openclinica.i18n.page_messages",locale);
	  //< restext = ResourceBundle.getBundle("org.akaza.openclinica.i18n.notes",locale);
	  //< resexception=ResourceBundle.getBundle("org.akaza.openclinica.i18n.exceptions",locale);
	  //< resword = ResourceBundle.getBundle("org.akaza.openclinica.i18n.words",locale);
	  
    getInputBeans();
    
    //  setting up one-time validation here
    //admit that it's an odd place to put it, but where else?
    //placing it in dataentryservlet is creating too many counts
    int keyId = ecb.getId();
    Integer count = (Integer)session.getAttribute(COUNT_VALIDATE+keyId);
    if (count != null) {
    	count++;
    	session.setAttribute(COUNT_VALIDATE+keyId,count);
    	//logger.info("^^^just set count to session: "+count);
    } else {
    	count = 0;
    	session.setAttribute(COUNT_VALIDATE+keyId, count);
    	//logger.info("***count not found, set to session: "+count);
    }
    
    DataEntryStage stage = ecb.getStage();
    //StudyEventStatus status = 
    Role r = currentRole.getRole();

    if (!SubmitDataServlet.maySubmitData(ub, currentRole)) {
      String exceptionName = resexception.getString("no_permission_validation");
      String noAccessMessage = resexception.getString("not_perfom_validation_syscontact");

      addPageMessage(noAccessMessage);
      throw new InsufficientPermissionException(Page.MENU, exceptionName, "1");
    }

    if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE)) {
      if (userIsOwnerAndLessThanTwelveHoursHavePassed() && !r.equals(Role.STUDYDIRECTOR)
          && !r.equals(Role.COORDINATOR)) {
        addPageMessage(respage.getString("since_perform_data_entry"));
        throw new InsufficientPermissionException(
            Page.SUBMIT_DATA_SERVLET,
            resexception.getString("owner_attempting_double_data_entry"),
            "1");
      }
    } else if (stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
      if ((ub.getId() != ecb.getValidatorId()) && !r.equals(Role.STUDYDIRECTOR)
          && !r.equals(Role.COORDINATOR)) {
        addPageMessage(respage.getString("validation_has_already_begun"));
        throw new InsufficientPermissionException(Page.SUBMIT_DATA_SERVLET,
        		resexception.getString("non_validator_attempting_double_data_entry"), "1");
      }
    } else {
      addPageMessage(respage.getString("not_perform_validation"));
      throw new InsufficientPermissionException(Page.SUBMIT_DATA_SERVLET,
    		  resexception.getString("using_double_data_entry_CRF_completed"), "1");
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
  
  protected DisplayItemBean validateDisplayItemBean(
      DiscrepancyValidator v, 
      DisplayItemBean dib, 
      String inputName) {
    
    
    org.akaza.openclinica.bean.core.ResponseType rt = dib.getMetadata().getResponseSet()
        .getResponseType();    
    
    boolean isSingleItem = false;
    if(StringUtil.isBlank(inputName)){//for single items
      inputName = getInputName(dib);
      isSingleItem = true;
    }
   
   ItemDataBean valueToCompare = dib.getData();
   if (!isSingleItem) {
     valueToCompare = dib.getDbData();
   }
    if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXT)
        || rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXTAREA)) {
      dib = validateDisplayItemBeanText(v, dib,inputName); 
      
      v.addValidation(inputName, Validator.MATCHES_INITIAL_DATA_ENTRY_VALUE, valueToCompare,
          false);
      v.setErrorMessage(respage.getString("value_you_specified")
      + valueToCompare.getValue() + " " + respage.getString("from_initial_data_entry"));
     
        

    } else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.RADIO)
        || rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECT)) {
      dib = validateDisplayItemBeanSingleCV(v, dib,inputName);      
      v.addValidation(inputName, Validator.MATCHES_INITIAL_DATA_ENTRY_VALUE, valueToCompare,
          false);
      v.setErrorMessage(respage.getString("value_you_specified")
      + valueToCompare.getValue() + " " + respage.getString("from_initial_data_entry"));
     

    } else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CHECKBOX)
        || rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECTMULTI)) {
      dib = validateDisplayItemBeanMultipleCV(v, dib,inputName);
     
      v.addValidation(inputName, Validator.MATCHES_INITIAL_DATA_ENTRY_VALUE, valueToCompare,
          true);
      v.setErrorMessage(respage.getString("value_you_specified")
      + valueToCompare.getValue() + " " + respage.getString("from_initial_data_entry"));
     

    }
    //only load form value when an item is not in a group,
    // if in group, the value is already loaded
    //see  formGroups = loadFormValueForItemGroup(digb,digbs,formGroups);
     if(isSingleItem){
       dib = loadFormValue(dib);
     }

    return dib;
    
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#validateDisplayItemBean(org.akaza.openclinica.core.form.Validator,
   *      org.akaza.openclinica.bean.submit.DisplayItemBean)
   */
  /*protected DisplayItemBean validateDisplayItemBean(
		  DiscrepancyValidator v, 
		  DisplayItemBean dib, 
		  String inputName) {
	  
	//logger.info("===reached head of validate display item bean");
    ItemBean ib = dib.getItem();
    org.akaza.openclinica.bean.core.ResponseType rt = dib.getMetadata().getResponseSet()
        .getResponseType();
    
    //YW << I commented the condition which is " if (!StringUtil.isBlank(dib.getData().getValue())) {    "
    //      to fix bug0001167.
    //      This change intends to give an error when there exists difference between initial and second data entry
    //      no matter an item's value is empty or not.
    //      Will this change break anything? 
    //YW >>
    //tbh, put it back in, changes nothing now, so i took it out, 082007
  
    int keyId = ecb.getId();
    Integer count = (Integer)session.getAttribute(COUNT_VALIDATE+keyId);
    
    	// types TEL and ED are not supported yet
    if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXT)
    		|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXTAREA)) {

    	if (count != null && count.intValue() > 0) {
    		//validate nothing from text and textarea for starters
    		//but how/when to remove?
    		//session.removeAttribute(COUNT_VALIDATE);
    		//logger.info("^^^found count_validate with key id"+ keyId + ", should remove here?");
    	} else {
    		//logger.info("===reached text validation with "+dib.getData().getValue());
    		dib = validateDisplayItemBeanText(v, dib,inputName);
    		//if (!StringUtil.isBlank(dib.getData().getValue())) {
    		v.addValidation(inputName, Validator.MATCHES_INITIAL_DATA_ENTRY_VALUE, dib.getData(),
    				false);
    		v.setErrorMessage(respage.getString("value_you_specified")
    				+ dib.getData().getValue() + " " + respage.getString("from_initial_data_entry"));
    	}
    	
    } else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.RADIO)
    		|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECT)) {
    	if (count != null && count.intValue() > 0) {
    		
    	} else {
    		//logger.info("===reached radio or select validation with "+
    		//		inputName+" and "+
    		//		dib.getData().getValue());
    		
    		dib = validateDisplayItemBeanSingleCV(v, dib, inputName);
    		//if (!StringUtil.isBlank(dib.getData().getValue())) {
    		v.addValidation(inputName, Validator.MATCHES_INITIAL_DATA_ENTRY_VALUE, dib.getData(),
    				false);
    		v.setErrorMessage(respage.getString("value_you_specified")
    				+ dib.getData().getValue() + " " + respage.getString("from_initial_data_entry"));
    	}
    	
    } else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CHECKBOX)
    		|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECTMULTI)) {
    	if (count != null && count.intValue() > 0) {
    		
    	} else {
    		//logger.info("===reached check box or multi validation for "+
    		//		inputName+" and "+
    		//		dib.getData().getValue());
    		dib = validateDisplayItemBeanMultipleCV(v, dib, inputName);
    		//if (!StringUtil.isBlank(dib.getData().getValue())) {
    		v.addValidation(inputName, Validator.MATCHES_INITIAL_DATA_ENTRY_VALUE, dib.getData(), true);
    		
    		v.setErrorMessage(respage.getString("value_you_specified")
    				+ dib.getData().getValue() + " " + respage.getString("from_initial_data_entry"));
    		
    	}
    }
    /*}*/

    // note that this step sets us up both for
    // displaying the data on the form again, in the event of an error
    // and sending the data to the database, in the event of no error
    //
    // we have to do this after adding the validations, so that we don't
    // overwrite the value the item data bean had from initial data entry
    // before the validator stores it as part of the Matches Initial Data Entry
    // Value validation
    //dib = loadFormValue(dib);

   // return dib;
 // }
 

  protected List<DisplayItemGroupBean> validateDisplayItemGroupBean(
		  DiscrepancyValidator v,
		  DisplayItemGroupBean digb,
		  List<DisplayItemGroupBean>digbs,//should be from the db, we check here for a difference
		  List<DisplayItemGroupBean>formGroups){
	  
	  //logger.info("===got this far");
	  formGroups = loadFormValueForItemGroup(digb,digbs,formGroups);
	  logger.info("found formgroups size for "+
			  digb.getGroupMetaBean().getName()+
			  ": "+formGroups.size()+ 
			  " compare to db groups size: "+
			  digbs.size());
	  
	  /*if (formGroups.size()!=digbs.size()) {
	   v.addValidation(digb.getGroupMetaBean().getName(), Validator.MATCHES_INITIAL_DATA_ENTRY_VALUE);
	   
	   v.setErrorMessage("You have entered a different number of groups"+
	   " for the item groups entitled "+
	   digb.getGroupMetaBean().getName());
	   }*/
	  
	  for (int i=0; i<formGroups.size(); i++) {   
		  DisplayItemGroupBean displayGroup= formGroups.get(i);
		  
		  List<DisplayItemBean> items = displayGroup.getItems();
		  String inputName = "";
		  for(DisplayItemBean displayItem : items){
            inputName =getGroupItemInputName(displayGroup,
                  displayGroup.getOrdinal(), displayItem);
			  //logger.info("===found this name: "+inputName);
			  
			  validateDisplayItemBean(v, displayItem, inputName);
              
                
			  
		  }
		  
		  if (i==0&&(formGroups.size()!=digbs.size())) {
			  v.addValidation(inputName+"group", Validator.DIFFERENT_NUMBER_OF_GROUPS_IN_DDE);
			  
			  v.setErrorMessage("You have entered a different number of groups"+
					  " for the item groups containing "+
					  inputName);
			  
		  }
	  }
	  
	  return formGroups;
	  
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
      target.setFileName(target.getFileName() + "?eventCRFId=" + eventCRFId + "&sectionId="
          + sectionId + "&tab=" + tabId);
      return target;
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.submit.DataEntryServlet#loadDBValues()
   */
  protected boolean shouldLoadDBValues(DisplayItemBean dib) {
    //my understanding-jxu:
    //if the status is pending, should not load the db value
    //if the status is UNAVAILABLE,load DB value
	  //interesting bug here: some fields load, some don't
	  //remove a session value here:
	  int keyId = ecb.getId();
	  session.removeAttribute(COUNT_VALIDATE+keyId);
	  //logger.info("^^^removed count_validate here");
	  //wonky place to do it, but no other place at the moment, tbh
    if (dib.getData().getStatus() ==null || dib.getData().getStatus().equals(Status.UNAVAILABLE)){
      return true;
    }
    /*if (!dib.getData().getStatus().equals(Status.UNAVAILABLE)) {
      logger.info("status don't match..");
      return false;
      //return true;
    }*/

    //how about this instead:
    //if it's pending, return false
    //otherwise return true?
    if (dib.getData().getStatus().equals(Status.PENDING)) {
        logger.info("status was pending...");
        return false;
        //return true;
      }
    
    return true;
  }
}
