/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.view;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.core.form.FormProcessor;

/**
 * Maintain the breadcrumbs on the page, remain seamless, 
 * for example, it gets set when 
 * the Page gets set in the forwardPage() method in the SecureController servlet,
 * Keep track of metadata being sent in the request, so that users
 * can go back down the bread crumb trail.
 * 
 * @author thickerson
 * 
 */
public class BreadcrumbTrail {
	private ArrayList trail = new ArrayList();
	
	public BreadcrumbTrail() {
		
	}
	
	public BreadcrumbTrail(ArrayList trail) {
		this.trail = trail;
	}
	
	
	/**
	 * @return Returns the trail.
	 */
	public ArrayList getTrail() {
		return trail;
	}
	/**
	 * @param trail The trail to set.
	 */
	public void setTrail(ArrayList trail) {
		this.trail = trail;
	}
	
	/**
	 * method to be called right before forwardPage() in the SecureController.
	 * Generates an arraylist of breadcrumb beans, which is then set 
	 * to the request/session.  Has the possibility of getting quite long,
	 * since we will be setting up all breadcrumb bean configurations here
	 * based on the Page submitted to us.
	 *  	
	 * @param jspPage the page which is the new target.
	 * @param request the HTTP request which we will construct the URL with.
	 * @return ArrayList of breadcrumb
	 */
	public ArrayList generateTrail(Page jspPage, 
			HttpServletRequest request) {
		
		try {
			//ArrayList newTrail = new ArrayList();
			if (jspPage.equals(Page.CREATE_DATASET_1)) {
				//when a user first steps onto the trail,
				//it is created new for them;
				//further on down the trail,
				//we update the statuses and collect URL variables
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Extract Datasets",
						"ExtractDatasetsMain",
						Status.AVAILABLE));
				trail.add(
						new BreadcrumbBean("Create Dataset Instructions",
								"CreateDataset"+
								this.generateURLString(request),
								Status.PENDING));//0
				trail.add(
						new BreadcrumbBean("Select Items or Event/Subject Attributes",
								"CreateDataset",
								Status.UNAVAILABLE));//1
				trail.add(
						new BreadcrumbBean("Define Temporal Scope",
								"CreateDataset",
								Status.UNAVAILABLE));//2
				//trail.add(
				//		new BreadcrumbBean("Select Filter",
			    //					"ApplyFilter",
			  	//				Status.UNAVAILABLE));//3
				trail.add(
						new BreadcrumbBean("Specify Dataset Properties",
								"CreateDataset",
								Status.UNAVAILABLE));//3
				trail.add(
						new BreadcrumbBean("Confirm Dataset Properties",
								"CreateDataset",
								Status.UNAVAILABLE));//4
				trail.add(
						new BreadcrumbBean("Generate Dataset",
								"CreateDataset",
								Status.UNAVAILABLE));//5, 6 items total
				
			} else if (jspPage.equals(Page.CREATE_DATASET_2)) {
				//BreadcrumbBean bcb = (BreadcrumbBean)trail.remove(0);
				//bcb.setStatus(Status.AVAILABLE);
				//trail.add(0, bcb);
				openBreadcrumbs(2);
				BreadcrumbBean bcb2 = (BreadcrumbBean)trail.remove(2);
				bcb2.setStatus(Status.PENDING);
				bcb2.setUrl("CreateDataset"+
						this.generateURLString(request));
				trail.add(2, bcb2);
				closeRestOfTrail(2);
			} else if (jspPage.equals(Page.CREATE_DATASET_3)) {
				//BreadcrumbBean bcb = (BreadcrumbBean)trail.remove(1);
				//bcb.setStatus(Status.AVAILABLE);
				//trail.add(1, bcb);
				openBreadcrumbs(3);
				BreadcrumbBean bcb2 = (BreadcrumbBean)trail.remove(3);
				bcb2.setStatus(Status.PENDING);
				bcb2.setUrl("CreateDataset"+
						this.generateURLString(request));
				trail.add(3, bcb2);
				closeRestOfTrail(3);
			} else if (jspPage.equals(Page.CREATE_DATASET_APPLY_FILTER)
					|| jspPage.equals(Page.APPLY_FILTER)) {
				//CREATE_DATASET_APPLY_FILTER might be bogus, tbh
				//BreadcrumbBean bcb = (BreadcrumbBean)trail.remove(2);
				//bcb.setStatus(Status.AVAILABLE);
				//trail.add(2, bcb);
				openBreadcrumbs(4);
				BreadcrumbBean bcb2 = (BreadcrumbBean)trail.remove(4);
				bcb2.setStatus(Status.PENDING);
				bcb2.setUrl("ApplyFilter"+
						this.generateURLString(request));
				trail.add(4, bcb2);
				closeRestOfTrail(4);
			} else if (jspPage.equals(Page.CREATE_DATASET_4)) {
				//BreadcrumbBean bcb = (BreadcrumbBean)trail.remove(3);
				//bcb.setStatus(Status.AVAILABLE);
				//trail.add(3, bcb);
				openBreadcrumbs(4);
				BreadcrumbBean bcb2 = (BreadcrumbBean)trail.remove(4);
				bcb2.setStatus(Status.PENDING);
				bcb2.setUrl("CreateDataset"+
						this.generateURLString(request));
				trail.add(4, bcb2);
				closeRestOfTrail(4);
			} else if (jspPage.equals(Page.CONFIRM_DATASET)) {
				//BreadcrumbBean bcb = (BreadcrumbBean)trail.remove(4);
				//bcb.setStatus(Status.AVAILABLE);
				//trail.add(4, bcb);
				openBreadcrumbs(5);
				BreadcrumbBean bcb2 = (BreadcrumbBean)trail.remove(5);
				bcb2.setStatus(Status.PENDING);
				bcb2.setUrl("CreateDataset"+
						this.generateURLString(request));
				trail.add(5, bcb2);
				closeRestOfTrail(5);
			}
			else if (jspPage.equals(Page.EXPORT_DATASETS) && trail.size() == 7) {
				//i.e. you have the end of the trail here with create dataset
				//BreadcrumbBean bcb = (BreadcrumbBean)trail.remove(5);
				//bcb.setStatus(Status.AVAILABLE);
				//trail.add(5, bcb);
				openBreadcrumbs(6);
				BreadcrumbBean bcb2 = (BreadcrumbBean)trail.remove(6);
				bcb2.setStatus(Status.PENDING);
				bcb2.setUrl("CreateDataset"+
						this.generateURLString(request));
				trail.add(6, bcb2);
				closeRestOfTrail(6);
			} 
			else if (jspPage.equals(Page.EXPORT_DATASETS) && trail.size()!=7) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Extract Datasets",
						"ExtractDatasetsMain",
						Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Download Data",
						"ExportDataset"+
						this.generateURLString(request),
						Status.PENDING));
			}
			else if (jspPage.equals(Page.CREATE_FILTER_SCREEN_2)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Extract Datasets",
						"ExtractDatasetsMain",
						Status.AVAILABLE));
				trail.add(
						new BreadcrumbBean("Instructions",
								"CreateFiltersOne"+
								this.generateURLString(request),
								Status.PENDING));
				trail.add(
						new BreadcrumbBean("Select CRF",
								"#", Status.UNAVAILABLE));
				trail.add(
						new BreadcrumbBean("Select Section",
								"#", Status.UNAVAILABLE));
				trail.add(
						new BreadcrumbBean("Select Parameters",
								"#", Status.UNAVAILABLE));
				trail.add(
						new BreadcrumbBean("Specify Criteria",
								"#", Status.UNAVAILABLE));
				trail.add(
						new BreadcrumbBean("Export",
								"ExportDataset", Status.UNAVAILABLE));
			} 
			else if (jspPage.equals(Page.CREATE_FILTER_SCREEN_3)) {
				trail = advanceTrail(trail, 
						new BreadcrumbBean("Select CRF", "CreateFiltersTwo"+this.generateURLString(request), 
								Status.PENDING), 2);
				closeRestOfTrail(2);
			}
			else if (jspPage.equals(Page.CREATE_FILTER_SCREEN_3_1)) {
				trail = advanceTrail(trail, new BreadcrumbBean("Select Section", "CreateFiltersTwo"+this.generateURLString(request), 
						Status.PENDING), 3);
				closeRestOfTrail(3);
			}
			else if (jspPage.equals(Page.CREATE_FILTER_SCREEN_3_2)) {
				trail = advanceTrail(trail, new BreadcrumbBean("Select Parameters", "CreateFiltersTwo"+this.generateURLString(request), 
						Status.PENDING), 4);
				closeRestOfTrail(4);
			}
			else if (jspPage.equals(Page.CREATE_FILTER_SCREEN_4)) {
				trail = advanceTrail(trail, new BreadcrumbBean("Specify Criteria", "CreateFiltersTwo"+this.generateURLString(request), 
						Status.PENDING), 5);
				closeRestOfTrail(5);
			}
			/*else if (jspPage.equals(Page.CREATE_FILTER_SCREEN_2)
					|| jspPage.equals(Page.CREATE_FILTER_SCREEN_3)
					|| jspPage.equals(Page.CREATE_FILTER_SCREEN_3_1)
					|| jspPage.equals(Page.CREATE_FILTER_SCREEN_3_2)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Extract Datasets",
						"ExtractDatasetsMain",
						Status.AVAILABLE));
				trail.add(
						new BreadcrumbBean("Select Study Events",
								"CreateFiltersTwo"+
								this.generateURLString(request),
								Status.PENDING));
				trail.add(
						new BreadcrumbBean("Specify Dataset Metadata",
								"CreateFiltersThree",
								Status.UNAVAILABLE));
				trail.add(
						new BreadcrumbBean("Export",
								"ExportDataset",
								Status.UNAVAILABLE));
			}
			else if (jspPage.equals(Page.CREATE_FILTER_SCREEN_4)) {
				
				trail = this.advanceTrail(trail,
						new BreadcrumbBean("Specify Dataset Metadata",
								"CreateFiltersThree"+
								this.generateURLString(request),
								Status.PENDING),2);
				closeRestOfTrail(2);
			}*/
			else if (jspPage.equals(Page.SUBMIT_DATA)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Submit Data", "ListStudySubjectsSubmit", Status.PENDING));
			}
			else if (jspPage.equals(Page.VIEW_STUDY_SUBJECT)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "ManageStudy" , Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Manage Subjects", "ListStudySubject" , Status.AVAILABLE));
				trail.add(new BreadcrumbBean("View Study Subject", "ViewStudySubject" + this.generateURLString(request), Status.AVAILABLE));
			}
			else if (jspPage.equals(Page.UPDATE_STUDY_EVENT)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "ManageStudy" , Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Manage Subjects", "ListStudySubject" , Status.AVAILABLE));
				trail.add(new BreadcrumbBean("View Study Subject", "ViewStudySubject" + this.generateURLString(request), Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Update Study Event", "#", Status.PENDING));
			}
			else if (jspPage.equals(Page.INSTRUCTIONS_ENROLL_SUBJECT)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Submit Data", "ListStudySubjectsSubmit", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Enroll Subject Instructions", "AddNewSubject?instr=1", Status.PENDING));
				trail.add(new BreadcrumbBean("Enroll Subject", "AddNewSubject", Status.UNAVAILABLE));
				trail.add(new BreadcrumbBean("Add new Study Event", "CreateNewStudyEvent", Status.UNAVAILABLE));
				trail.add(new BreadcrumbBean("Study Event Overview", "EnterDataForStudyEvent", Status.UNAVAILABLE));
				trail.add(new BreadcrumbBean("Event CRF Data Submission", "TableOfContents", Status.UNAVAILABLE));
				trail.add(new BreadcrumbBean("Data Entry", "InitialDataEntry", Status.UNAVAILABLE));
				trail.add(new BreadcrumbBean("Mark Event CRF Complete", "MarkEventCRFComplete", Status.UNAVAILABLE));
			}
			else if (jspPage.equals(Page.ADD_NEW_SUBJECT)) {
				trail = advanceTrail(trail, new BreadcrumbBean("Enroll Subject", "AddNewSubject", Status.PENDING), 2);
				closeRestOfTrail(2);
			}
			else if (jspPage.equals(Page.CREATE_NEW_STUDY_EVENT)) {
				if (!containsServlet("AddNewSubject")) {
					trail = new ArrayList();
					trail.add(new BreadcrumbBean("Submit Data", "ListStudySubjectsSubmit", Status.AVAILABLE));
					trail.add(new BreadcrumbBean("Add new Study Event", "CreateNewStudyEvent", Status.PENDING));
					trail.add(new BreadcrumbBean("Study Event Overview", "EnterDataForStudyEvent", Status.UNAVAILABLE));
					trail.add(new BreadcrumbBean("Event CRF Data Submission", "TableOfContents", Status.UNAVAILABLE));
					trail.add(new BreadcrumbBean("Data Entry", "InitialDataEntry", Status.UNAVAILABLE));
					trail.add(new BreadcrumbBean("Mark Event CRF Complete", "MarkEventCRFComplete", Status.UNAVAILABLE));
				}
				else {
					trail = advanceTrail(trail, new BreadcrumbBean("Add new Study Event", "CreateNewStudyEvent", Status.PENDING), 3);
					closeRestOfTrail(3);
				}
			}
			else if (jspPage.equals(Page.ENTER_DATA_FOR_STUDY_EVENT)) {
				int ordinal;
				if (containsServlet("AddNewSubject")) {
					ordinal = 4;
					trail = advanceTrail(trail, new BreadcrumbBean("Study Event Overview", "EnterDataForStudyEvent" + generateURLString(request), Status.PENDING), ordinal);				
				}
				else if (containsServlet("CreateNewStudyEvent")) {
					ordinal = 2;
					trail = advanceTrail(trail, new BreadcrumbBean("Study Event Overview", "EnterDataForStudyEvent" + generateURLString(request), Status.PENDING), ordinal);
				}
				else {
					ordinal = 1;
					trail = new ArrayList();
					trail.add(new BreadcrumbBean("Submit Data", "ListStudySubjectsSubmit", Status.AVAILABLE));
					trail.add(new BreadcrumbBean("Study Event Overview", "EnterDataForStudyEvent" + generateURLString(request), Status.PENDING));
					trail.add(new BreadcrumbBean("Event CRF Data Submission", "TableOfContents", Status.UNAVAILABLE));
					trail.add(new BreadcrumbBean("Data Entry", "InitialDataEntry", Status.UNAVAILABLE));
					trail.add(new BreadcrumbBean("Mark Event CRF Complete", "MarkEventCRFComplete", Status.UNAVAILABLE));				
				}
				closeRestOfTrail(ordinal);
			}
			else if (jspPage.equals(Page.TABLE_OF_CONTENTS)) {
				int ordinal;
				if (containsServlet("EnterDataForStudyEvent")) {
					ordinal = trail.size() - 3;
					trail = advanceTrail(trail, new BreadcrumbBean("Event CRF Data Submission", "TableOfContents" + this.generateURLString(request), Status.PENDING), ordinal);
					closeRestOfTrail(ordinal);
				}
				else {
					ordinal = 1;
					trail = new ArrayList();
					trail.add(new BreadcrumbBean("Submit Data", "ListStudySubjectsSubmit", Status.AVAILABLE));
					trail.add(new BreadcrumbBean("Event CRF Data Submission", "TableOfContents" + generateURLString(request), Status.PENDING));
					trail.add(new BreadcrumbBean("Data Entry", "InitialDataEntry", Status.UNAVAILABLE));
					trail.add(new BreadcrumbBean("Mark Event CRF Complete", "MarkEventCRFComplete", Status.UNAVAILABLE));				
				}
				closeRestOfTrail(ordinal);
			}
			else if (jspPage.equals(Page.INITIAL_DATA_ENTRY)) {
				int ordinal = trail.size() - 2;
				trail = advanceTrail(trail, new BreadcrumbBean("Data Entry", "InitialDataEntry" + this.generateURLString(request), Status.PENDING), ordinal);
				closeRestOfTrail(ordinal);
			}
			else if (jspPage.equals(Page.DOUBLE_DATA_ENTRY)) {
				int ordinal = trail.size() - 2;
				trail = advanceTrail(trail, new BreadcrumbBean("Data Entry", "DoubleDataEntry" + this.generateURLString(request), Status.PENDING), ordinal);
				closeRestOfTrail(ordinal);
			}
			else if (jspPage.equals(Page.MARK_EVENT_CRF_COMPLETE)) {
				int ordinal = trail.size() - 1;
				trail = advanceTrail(trail, new BreadcrumbBean("Mark Event CRF Complete", "MarkEventCRFComplete" + this.generateURLString(request), Status.PENDING), ordinal);
				closeRestOfTrail(ordinal);
			}
			
			else if (jspPage.equals(Page.CREATE_STUDY1)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Study Description", "#", Status.PENDING));
				trail.add(new BreadcrumbBean("Study Status", "#", Status.UNAVAILABLE));
				trail.add(new BreadcrumbBean("Study Design", "#", Status.UNAVAILABLE));			
				trail.add(new BreadcrumbBean("Conditions and Eligibility", "#", Status.UNAVAILABLE));
				trail.add(new BreadcrumbBean("Facility Information", "#", Status.UNAVAILABLE));
				trail.add(new BreadcrumbBean("Related Information", "#", Status.UNAVAILABLE));
				trail.add(new BreadcrumbBean("Confirm and Submit", "#", Status.UNAVAILABLE));
			}

			else if (jspPage.equals(Page.CREATE_STUDY2)) {
				advanceTrail(trail, new BreadcrumbBean("Study Status", "#", Status.PENDING), 1);
				closeRestOfTrail(1);
			}
			else if (jspPage.equals(Page.CREATE_STUDY3) || jspPage.equals(Page.CREATE_STUDY4)) {
				advanceTrail(trail, new BreadcrumbBean("Study Design", "#", Status.PENDING), 2);
				closeRestOfTrail(2);
				//closeBreadcrumb(1);
			}
			else if (jspPage.equals(Page.CREATE_STUDY5)) {
				advanceTrail(trail, new BreadcrumbBean("Conditions and Eligibility", "#", Status.PENDING), 3);
				closeRestOfTrail(3);
				//closeBreadcrumb(2);
			}
			else if (jspPage.equals(Page.CREATE_STUDY6)) {
				advanceTrail(trail, new BreadcrumbBean("Facility Information", "#", Status.PENDING), 4);
				closeRestOfTrail(4);
				//closeBreadcrumb(3);
				
			}
			else if (jspPage.equals(Page.CREATE_STUDY7)) {
				advanceTrail(trail, new BreadcrumbBean("Related Information", "#", Status.PENDING), 5);
				closeRestOfTrail(5);
				//closeBreadcrumb(4);
			}
			else if (jspPage.equals(Page.STUDY_CREATE_CONFIRM)) {
				advanceTrail(trail, new BreadcrumbBean("Confirm and Submit", "#", Status.PENDING), 6);
				closeRestOfTrail(6);
				//closeBreadcrumb(5);
			}
			else if (jspPage.equals(Page.UPDATE_STUDY1)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Study Description", "#", Status.PENDING));
				trail.add(new BreadcrumbBean("Study Status", "#", Status.UNAVAILABLE));
				trail.add(new BreadcrumbBean("Study Design", "#", Status.UNAVAILABLE));			
				trail.add(new BreadcrumbBean("Conditions and Eligibility", "#", Status.UNAVAILABLE));
				trail.add(new BreadcrumbBean("Facility Information", "#", Status.UNAVAILABLE));
				trail.add(new BreadcrumbBean("Related Information", "#", Status.UNAVAILABLE));
				trail.add(new BreadcrumbBean("Confirm and Submit", "#", Status.UNAVAILABLE));
			}

			else if (jspPage.equals(Page.UPDATE_STUDY2)) {
				advanceTrail(trail, new BreadcrumbBean("Study Status", "#", Status.PENDING), 1);
				closeRestOfTrail(1);
			}
			else if (jspPage.equals(Page.UPDATE_STUDY3) || jspPage.equals(Page.CREATE_STUDY4)) {
				advanceTrail(trail, new BreadcrumbBean("Study Design", "#", Status.PENDING), 2);
				closeRestOfTrail(2);
			}
			else if (jspPage.equals(Page.UPDATE_STUDY5)) {
				advanceTrail(trail, new BreadcrumbBean("Conditions and Eligibility", "#", Status.PENDING), 3);
				closeRestOfTrail(3);
				
			}
			else if (jspPage.equals(Page.UPDATE_STUDY6)) {
				advanceTrail(trail, new BreadcrumbBean("Facility Information", "#", Status.PENDING), 4);
				closeRestOfTrail(4);
				
			}
			else if (jspPage.equals(Page.UPDATE_STUDY7)) {
				advanceTrail(trail, new BreadcrumbBean("Related Information", "#", Status.PENDING), 5);
				closeRestOfTrail(5);
			}
			else if (jspPage.equals(Page.STUDY_UPDATE_CONFIRM)) {
				advanceTrail(trail, new BreadcrumbBean("Confirm and Submit", "#", Status.PENDING), 6);
				closeRestOfTrail(6);
			}
			else if (jspPage.equals(Page.ADMIN_SYSTEM) || jspPage.equals(Page.TECH_ADMIN_SYSTEM)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Business Admin", "#", Status.PENDING));				
			}
			else if (jspPage.equals(Page.ENTERPRISE)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Home", "MainMenu", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("OpenClinica Enterprise", "#", Status.PENDING));
				
			}
			else if (jspPage.equals(Page.MANAGE_STUDY)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "#", Status.PENDING));				
			}
			else if (jspPage.equals(Page.LIST_USER_IN_STUDY)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Manage Users", "ListStudyUser", Status.PENDING));	
				
			}
			else if (jspPage.equals(Page.LIST_STUDY_SUBJECT)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Manage Subjects", "ListStudySubject", Status.PENDING));			
			}
			else if (jspPage.equals(Page.SITE_LIST)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Manage Sites", "ListSite", Status.PENDING));			
			}
			else if (jspPage.equals(Page.STUDY_EVENT_DEFINITION_LIST)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Manage Event Definitions", "ListEventDefinition", Status.PENDING));			
			}
		
		    else if (jspPage.equals(Page.SUBJECT_GROUP_CLASS_LIST)) {
			  trail = new ArrayList();
			  trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
			  trail.add(new BreadcrumbBean("Manage Templates", "ListSubjectGroupClass", Status.PENDING));			
		    }
		    else if (jspPage.equals(Page.CREATE_SUBJECT_GROUP_CLASS)) {
			  trail = new ArrayList();
			  trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
			  trail.add(new BreadcrumbBean("Manage Templates", "ListSubjectGroupClass", Status.AVAILABLE));	
			  trail.add(new BreadcrumbBean("Create Group Template", "CreateSubjectGroupClass", Status.PENDING));			
		    }
            else if (jspPage.equals(Page.UPDATE_SUBJECT_GROUP_CLASS)) {
              trail = new ArrayList();
              trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
              trail.add(new BreadcrumbBean("Manage Templates", "ListSubjectGroupClass", Status.AVAILABLE)); 
              trail.add(new BreadcrumbBean("Update Group Template", "UpdateSubjectGroupClass", Status.PENDING));            
            }
			else if (jspPage.equals(Page.CRF_LIST)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage CRFs", "ListCRF", Status.PENDING));			
			}
			else if (jspPage.equals(Page.SUBJECT_LIST)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Business Admin", "AdminSystem", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Administer Subjects", "ListSubject", Status.PENDING));			
			}
			else if (jspPage.equals(Page.LIST_USER_ACCOUNTS)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Business Admin", "AdminSystem", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Administer Users", "ListUserAccounts", Status.PENDING));			
			}
			else if (jspPage.equals(Page.STUDY_LIST)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Business Admin", "AdminSystem", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Administer Studies", "ListStudy", Status.PENDING));			
			}
			else if (jspPage.equals(Page.CREATE_CRF)) {
			  trail = new ArrayList();
			  trail.add(new BreadcrumbBean("Manage CRFs", "ListCRF", Status.AVAILABLE));
			  trail.add(new BreadcrumbBean("Create a New CRF", "CreateCRF", Status.PENDING));
			}
			
			else if (jspPage.equals(Page.CREATE_CRF_VERSION)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Enter Version Name", "#", Status.PENDING));
				trail.add(new BreadcrumbBean("Check Version", "#", Status.UNAVAILABLE));
				trail.add(new BreadcrumbBean("Upload Spreadsheet", "#", Status.UNAVAILABLE));			
				trail.add(new BreadcrumbBean("Validate Spreadsheet", "#", Status.UNAVAILABLE));
				trail.add(new BreadcrumbBean("Review New Items", "#", Status.UNAVAILABLE));
				trail.add(new BreadcrumbBean("Review SQL Generated", "#", Status.UNAVAILABLE));
				
			}
			else if (jspPage.equals(Page.CREATE_CRF_VERSION_NODELETE) ||jspPage.equals(Page.REMOVE_CRF_VERSION_DEF) 
			    || jspPage.equals(Page.REMOVE_CRF_VERSION_CONFIRM) ) {
				advanceTrail(trail, new BreadcrumbBean("Check Version", "#", Status.PENDING), 1);
				closeRestOfTrail(1);
			}
			else if (jspPage.equals(Page.UPLOAD_CRF_VERSION)) {
				advanceTrail(trail, new BreadcrumbBean("Upload Spreadsheet", "#", Status.PENDING), 2);
				BreadcrumbBean b = (BreadcrumbBean) trail.get(0);
				b.setStatus(Status.AVAILABLE);
				closeRestOfTrail(2);
			}
			else if (jspPage.equals(Page.CREATE_CRF_VERSION_CONFIRM)) {
				advanceTrail(trail, new BreadcrumbBean("Validate Spreadsheet", "#", Status.PENDING), 3);
				closeRestOfTrail(3);
			}
			else if (jspPage.equals(Page.CREATE_CRF_VERSION_CONFIRMSQL)) {
				advanceTrail(trail, new BreadcrumbBean("Review New Items", "#", Status.PENDING), 4);
				closeRestOfTrail(4);
			}
			else if (jspPage.equals(Page.CREATE_CRF_VERSION_DONE) || jspPage.equals(Page.CREATE_CRF_VERSION_ERROR)) {
				advanceTrail(trail, new BreadcrumbBean("Review SQL Generated", "#", Status.PENDING), 5);
				closeRestOfTrail(5);
			}
			else if (jspPage.equals(Page.VIEW_CRF_VERSION)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage CRFs", "ListCRF", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("View CRF Version", "ViewCRFVersion", Status.PENDING));
			}
			else if (jspPage.equals(Page.VIEW_CRF)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage CRFs", "ListCRF", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("View CRF", "ViewCRF", Status.PENDING));
			}
			else if (jspPage.equals(Page.REMOVE_CRF)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage CRFs", "ListCRF", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Remove CRF", "RemoveCRF", Status.PENDING));
			}
			else if (jspPage.equals(Page.RESTORE_CRF)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage CRFs", "ListCRF", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Restore CRF", "RestoreCRF", Status.PENDING));
			}
			else if (jspPage.equals(Page.REMOVE_CRF_VERSION)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage CRFs", "ListCRF", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Remove CRF Version", "RemoveCRFVersion", Status.PENDING));
			}
			else if (jspPage.equals(Page.RESTORE_CRF_VERSION)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage CRFs", "ListCRF", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Restore CRF Version", "RestoreCRFVersion", Status.PENDING));
			}
			else if (jspPage.equals(Page.UPDATE_CRF)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage CRFs", "ListCRF", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Update CRF", "#", Status.PENDING));
			}
			else if (jspPage.equals(Page.VIEW_SUBJECT)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Business Admin", "AdminSystem", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Administer Subjects", "ListSubject", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("View Subject", "#", Status.PENDING));
			}
			else if (jspPage.equals(Page.UPDATE_SUBJECT)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Business Admin", "AdminSystem", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Administer Subjects", "ListSubject", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Update Subject", "#", Status.PENDING));
			}
			else if (jspPage.equals(Page.REMOVE_SUBJECT)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Business Admin", "AdminSystem", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Administer Subjects", "ListSubject", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Remove Subject", "#", Status.PENDING));
			}
			else if (jspPage.equals(Page.RESTORE_SUBJECT)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Business Admin", "AdminSystem", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Administer Subjects", "ListSubject", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Restore Subject", "#", Status.PENDING));
			}
			else if (jspPage.equals(Page.VIEW_USER_ACCOUNT)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Business Admin", "AdminSystem", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Administer Users", "ListUserAccounts", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("View User Account", "ViewUserAccount" + generateURLString(request), Status.PENDING));
			}
			
			else if (jspPage.equals(Page.EDIT_ACCOUNT)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Administer System", "AdminSystem", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Administer Users", "ListUserAccounts", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Edit User Account", "EditUserAccount" + generateURLString(request), Status.PENDING));
				trail.add(new BreadcrumbBean("Confurm User Account Details", "EditUserAccount", Status.UNAVAILABLE));
			}
			
			else if (jspPage.equals(Page.EDIT_ACCOUNT_CONFIRM)) {
				trail = advanceTrail(trail, new BreadcrumbBean("Confirm User Account Details", "EditUserAccount" + generateURLString(request), Status.PENDING), 3);
			}
			
			else if (jspPage.equals(Page.EDIT_STUDY_USER_ROLE)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Administer Users", "ListUserAccounts", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Edit User Role", "EditStudyUserRole", Status.PENDING));			
			}
			
			else if (jspPage.equals(Page.CREATE_ACCOUNT)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Administer Users", "ListUserAccounts", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Create User Account", "CreateUserAccount", Status.PENDING));			
			}
			else if (jspPage.equals(Page.REASSIGN_STUDY_SUBJECT)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Manage Subjects", "ListStudySubject", Status.AVAILABLE));		
				trail.add(new BreadcrumbBean("Reassign Study Subject", "#", Status.PENDING));			
			}
			else if (jspPage.equals(Page.ASSIGN_STUDY_SUBJECT)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Manage Subjects", "ListStudySubject", Status.AVAILABLE));		
				trail.add(new BreadcrumbBean("Assign Study Subject", "#", Status.PENDING));			
			}
			else if (jspPage.equals(Page.DEFINE_STUDY_EVENT1)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Manage Event Definitions ", "ListEventDefinition", Status.AVAILABLE));		
				trail.add(new BreadcrumbBean("Create Study Event Definition", "#", Status.PENDING));	
				
			}
			else if (jspPage.equals(Page.UPDATE_EVENT_DEFINITION1)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Manage Event Definitions", "ListEventDefinition", Status.AVAILABLE));		
				trail.add(new BreadcrumbBean("Update Study Event Definition", "#", Status.PENDING));	
				
			}
			else if (jspPage.equals(Page.VIEW_EVENT_DEFINITION)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Manage Event Definitions", "ListEventDefinition", Status.AVAILABLE));		
				trail.add(new BreadcrumbBean("View Event Definition", "#", Status.PENDING));	
				
			}
			else if (jspPage.equals(Page.CHANGE_STUDY)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Home", "MainMenu", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Change Current Study", "#", Status.PENDING));	
				
			}else if (jspPage.equals(Page.UPDATE_PROFILE)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Home", "MainMenu", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Update User Profile", "#", Status.PENDING));	
				
			}
			else if (jspPage.equals(Page.CREATE_SUB_STUDY)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Manage Sites ", "ListSite", Status.AVAILABLE));		
				trail.add(new BreadcrumbBean("Create New Site", "#", Status.PENDING));	
				
			}
			else if (jspPage.equals(Page.VIEW_SITE)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Manage Sites ", "ListSite", Status.AVAILABLE));		
				trail.add(new BreadcrumbBean("View Site", "#", Status.PENDING));	
				
			}
			else if (jspPage.equals(Page.SET_USER_ROLE_IN_STUDY)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Manage Users ", "ListStudyUser", Status.AVAILABLE));		
				trail.add(new BreadcrumbBean("Set User Role", "#", Status.PENDING));	
				
			}
			else if (jspPage.equals(Page.STUDY_USER_LIST)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Manage Users ", "ListStudyUser", Status.AVAILABLE));		
				trail.add(new BreadcrumbBean("Assign New Users to Study", "#", Status.PENDING));	
				
			}
			else if (jspPage.equals(Page.LOCK_DEFINITION)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Manage Event Definitions ", "ListEventDefinition", Status.AVAILABLE));		
				trail.add(new BreadcrumbBean("Lock Event Definition", "#", Status.PENDING));	
				
			}
			else if (jspPage.equals(Page.UNLOCK_DEFINITION)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Manage Event Definitions ", "ListEventDefinition", Status.AVAILABLE));		
				trail.add(new BreadcrumbBean("Unlock Event Definition", "#", Status.PENDING));	
				
			}
			else if (jspPage.equals(Page.VIEW_USER_IN_STUDY)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Manage Users ", "ListStudyUser", Status.AVAILABLE));		
				trail.add(new BreadcrumbBean("View User Details", "#", Status.PENDING));				
			}
			else if (jspPage.equals(Page.REMOVE_USER_ROLE_IN_STUDY)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Manage Users ", "ListStudyUser", Status.AVAILABLE));		
				trail.add(new BreadcrumbBean("Remove User Role", "#", Status.PENDING));				
			}
			else if (jspPage.equals(Page.REMOVE_DEFINITION)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Manage Event Definitions ", "ListEventDefinition", Status.AVAILABLE));		
				trail.add(new BreadcrumbBean("Remove Event Definition", "#", Status.PENDING));				
			}
			else if (jspPage.equals(Page.RESTORE_DEFINITION)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Manage Event Definitions ", "ListEventDefinition", Status.AVAILABLE));		
				trail.add(new BreadcrumbBean("Restore Event Definition", "#", Status.PENDING));				
			}
			else if (jspPage.equals(Page.VIEW_STUDY)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Business Admin", "AdminSystem", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Administer Studies ", "ListStudy", Status.AVAILABLE));		
				trail.add(new BreadcrumbBean("View Study Details", "#", Status.PENDING));	
				
			}
			else if (jspPage.equals(Page.SET_USER_ROLE)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Business Admin", "AdminSystem", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Administer Users ", "ListUserAccounts", Status.AVAILABLE));		
				trail.add(new BreadcrumbBean("Set User Role", "#", Status.PENDING));	
			}
			else if (jspPage.equals(Page.REMOVE_STUDY)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Business Admin", "AdminSystem", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Administer Studies ", "ListStudy", Status.AVAILABLE));		
				trail.add(new BreadcrumbBean("Remove a Study", "#", Status.PENDING));	
			}
			else if (jspPage.equals(Page.RESTORE_STUDY)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Business Admin", "AdminSystem", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Administer Studies ", "ListStudy", Status.AVAILABLE));		
				trail.add(new BreadcrumbBean("Restore a Study", "#", Status.PENDING));	
			}
			else if (jspPage.equals(Page.REMOVE_SITE)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Manage Sites", "ListSite", Status.AVAILABLE));		
				trail.add(new BreadcrumbBean("Remove a Site", "#", Status.PENDING));	
			}
			else if (jspPage.equals(Page.RESTORE_SITE)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Manage Sites", "ListSite", Status.AVAILABLE));		
				trail.add(new BreadcrumbBean("Restore a Site", "#", Status.PENDING));	
			}
			else if (jspPage.equals(Page.MENU)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Home", "MainMenu", Status.PENDING));				
				
			}
			else if (jspPage.equals(Page.VIEW_TABLE_OF_CONTENT)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage CRFs", "ListCRF", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("View CRF Version Data Entry", "#", Status.PENDING));	
			}
            else if (jspPage.equals(Page.VIEW_SECTION_DATA_ENTRY) ||
                jspPage.equals(Page.VIEW_SECTION_DATA_ENTRY_SERVLET) ) {
                trail = new ArrayList();
                trail.add(new BreadcrumbBean("Manage CRFs", "ListCRF", Status.AVAILABLE));
                trail.add(new BreadcrumbBean("View CRF Version Section Data", "#", Status.PENDING));  
              
            }
			else if (jspPage.equals(Page.VIEW_EVENT_CRF_CONTENT)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Manage Subjects", "ListStudySubject", Status.AVAILABLE));		
				trail.add(new BreadcrumbBean("View Study Subject", "ViewStudySubject" + this.generateURLString(request), Status.AVAILABLE));		
				trail.add(new BreadcrumbBean("View Event CRF Data", "#", Status.PENDING));	
			}
			else if (jspPage.equals(Page.VIEW_STUDY_EVENTS)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage Study", "ManageStudy", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("View Events", "#", Status.PENDING));	
			}
			else if (jspPage.equals(Page.DELETE_CRF_VERSION)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Manage CRFs", "ListCRF", Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Delete CRF Version", "#", Status.PENDING));	
			}
			
			//TODO fill in your page here if it does not require a
			//breadcrumb trail:

			else if (jspPage.equals(Page.MENU)) {
				trail = new ArrayList();
			}//below are new breadcrumbs added to provide links, tbh
			else if (jspPage.equals(Page.EDIT_DATASET)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Extract Datasets",
						"ExtractDatasetsMain",
						Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Edit Dataset",
						"#",
						Status.PENDING));
			}
			else if (jspPage.equals(Page.EDIT_FILTER)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Extract Datasets",
						"ExtractDatasetsMain",
						Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Edit Filter",
						"#",
						Status.PENDING));
			}
			else if (jspPage.equals(Page.VIEW_DATASET_DETAILS)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Extract Datasets",
						"ExtractDatasetsMain",
						Status.AVAILABLE));
				trail.add(new BreadcrumbBean("View Dataset Details",
						"#",
						Status.PENDING));
			}
			else if (jspPage.equals(Page.EXTRACT_DATASETS_MAIN)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Home",
						"MainMenu",
						Status.AVAILABLE));
				trail.add(new BreadcrumbBean("Extract Datasets",
						"ExtractDatasetsMain",
						Status.PENDING));
			}
			else if (jspPage.equals(Page.VIEW_DATASETS)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Extract Datasets",
						"ExtractDatasetsMain",
						Status.AVAILABLE));
				trail.add(new BreadcrumbBean("View Datasets",
						"#",
						Status.PENDING));
			}
			else if (jspPage.equals(Page.CREATE_FILTER_SCREEN_1)) {
				trail = new ArrayList();
				trail.add(new BreadcrumbBean("Extract Datasets",
						"ExtractDatasetsMain",
						Status.AVAILABLE));
				trail.add(new BreadcrumbBean("View Filters",
						"#",
						Status.PENDING));
			}
			else if (jspPage.equals(Page.SUBJECT_MGMT)) {
				trail = new ArrayList();
                trail.add(new BreadcrumbBean("Subject Management", "SubjectMgmt", Status.PENDING));
			}
            else if(jspPage.equals(Page.PII_SUBJECT_LIST)){
                trail = new ArrayList();
                trail.add(new BreadcrumbBean("Subject Management", "SubjectMgmt", Status.AVAILABLE));
                trail.add(new BreadcrumbBean("Manage Subjects", "ListPiiSubject", Status.PENDING));
            }
            else if(jspPage.equals(Page.LIST_SUBJECT_ENTRY_LABELS)){
                trail = new ArrayList();
                trail.add(new BreadcrumbBean("Subject Management", "SubjectMgmt", Status.AVAILABLE));
                trail.add(new BreadcrumbBean("Subject Entry Label Management", "ListSubjectEntryLabels", Status.PENDING));
            }
            else if(jspPage.equals(Page.VIEW_SUBJECT_ENTRY_LABEL)){
                trail = new ArrayList();
                trail.add(new BreadcrumbBean("Subject Management", "SubjectMgmt", Status.AVAILABLE));
                trail.add(new BreadcrumbBean("Subject Entry Label Management", "ListSubjectEntryLabels", Status.AVAILABLE));
                trail.add(new BreadcrumbBean("View Subject Entry Label", "ViewSubjectEntryLabel", Status.PENDING));
            }
            else if(jspPage.equals(Page.EDIT_SUBJECT_ENTRY_LABEL)){
                trail = new ArrayList();
                trail.add(new BreadcrumbBean("Subject Management", "SubjectMgmt", Status.AVAILABLE));
                trail.add(new BreadcrumbBean("Subject Entry Label Management", "ListSubjectEntryLabels", Status.AVAILABLE));
                trail.add(new BreadcrumbBean("Edit Subject Entry Label", "EditSubjectEntryLabel", Status.PENDING));
            }
            else if(jspPage.equals(Page.EDIT_SUBJECT_ENTRY_LABEL_CONFIRM)){
                trail = new ArrayList();
                trail.add(new BreadcrumbBean("Subject Management", "SubjectMgmt", Status.AVAILABLE));
                trail.add(new BreadcrumbBean("Subject Entry Label Management", "ListSubjectEntryLabels", Status.AVAILABLE));
                trail.add(new BreadcrumbBean("Edit Subject Entry Label - Confirm", "EditSubjectEntryLabel", Status.PENDING));
            }
            else if(jspPage.equals(Page.ADD_NEW_PII_SUBJECT)){
                trail = new ArrayList();
                trail.add(new BreadcrumbBean("Subject Management", "SubjectMgmt", Status.AVAILABLE));
                trail.add(new BreadcrumbBean("Manage Subjects", "ListPiiSubject", Status.AVAILABLE));
                trail.add(new BreadcrumbBean("Add New Subject", "AddNewPiiSubject", Status.PENDING));
            }
            else if(jspPage.equals(Page.ADD_EXISTING_PII_SUBJECT)){
                trail = new ArrayList();
                trail.add(new BreadcrumbBean("Subject Management", "SubjectMgmt", Status.AVAILABLE));
                trail.add(new BreadcrumbBean("Manage Subjects", "ListPiiSubject", Status.AVAILABLE));
                trail.add(new BreadcrumbBean("Add New Subject", "AddNewPiiSubject", Status.PENDING));
            }
            else if(jspPage.equals(Page.EDIT_SUBJECT)){
                trail = new ArrayList();
                trail.add(new BreadcrumbBean("Subject Management", "SubjectMgmt", Status.AVAILABLE));
                trail.add(new BreadcrumbBean("Manage Subjects", "ListPiiSubject", Status.AVAILABLE));
                trail.add(new BreadcrumbBean("Edit Subject", "EditSubject", Status.PENDING));
            }
            else if(jspPage.equals(Page.EDIT_SUBJECT_CONFIRM)){
                trail = new ArrayList();
                trail.add(new BreadcrumbBean("Subject Management", "SubjectMgmt", Status.AVAILABLE));
                trail.add(new BreadcrumbBean("Manage Subjects", "ListPiiSubject", Status.AVAILABLE));
                trail.add(new BreadcrumbBean("Edit Subject - Confirm", "EditSubject", Status.PENDING));
            }
            else if(jspPage.equals(Page.VIEW_PII_SUBJECT)){
                trail = new ArrayList();
                trail.add(new BreadcrumbBean("Subject Management", "SubjectMgmt", Status.AVAILABLE));
                trail.add(new BreadcrumbBean("Manage Subjects", "ListPiiSubject", Status.AVAILABLE));
                trail.add(new BreadcrumbBean("View Subject", "ViewPiiSubject", Status.PENDING));
            }

//		else {
//		  trail = new ArrayList();
//		}
		} catch (IndexOutOfBoundsException ioobe) {
			// TODO Auto-generated catch block, created to disallow errors
			ioobe.printStackTrace();
			
			trail = new ArrayList();
		}
		
		return trail;
	}
	
	public String generateURLString(HttpServletRequest request) {
		String newURL = "?";
		FormProcessor fp = new FormProcessor(request);
		Enumeration en = request.getParameterNames();
		while (en.hasMoreElements()) {
			String title = (String)en.nextElement();
			String value = fp.getString(title);
			newURL += title + "=" + value + "&";
		}
		return newURL;
	}
	
	public ArrayList advanceTrail(ArrayList trail, 
			BreadcrumbBean newBean,
			int ordinal) {
		
		int previous = ordinal - 1;
		
		BreadcrumbBean bcb;
		
		if ((previous >= 0) && (previous < trail.size())) {
			bcb = (BreadcrumbBean)trail.remove(previous);
			bcb.setStatus(Status.AVAILABLE);
			trail.add(previous, bcb);
		}

		if ((ordinal >= 0) && (ordinal < trail.size())) {
			bcb = (BreadcrumbBean)trail.remove(ordinal);
			trail.add(ordinal, newBean);
		}
		
		return trail;
	}
	
	/**
	 * Determines if the trail contains a particular servlet.
	 * 
	 * @param servlet The name of the servlet.
	 * @return <code>true</code> if one of the elements refers to the specified servlet,
	 * <code>false</code> otherwise.
	 */
	public boolean containsServlet(String servlet) {
		servlet = servlet.toLowerCase();
		for (int i = 0; i < trail.size(); i++) {
			BreadcrumbBean b = (BreadcrumbBean) trail.get(i);
			if (b.getUrl().toLowerCase().indexOf(servlet) >= 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Make everything in the trail after the specified ordinal unavailable.
	 * 
	 * It is recommended that this method be called after advanceTrail.  Using this
	 * method ensures that if the user got to the current page by "going back" through
	 * the trail, all the "future" pages will be marked unavailable.
	 * 
	 * @param ordinal The index after which everything will be unavailable.
	 */
	private void closeRestOfTrail(int ordinal) {
		if (ordinal < 0) {
			return ;
		}
		
		for (int i = ordinal+1; i < trail.size(); i++) {
			BreadcrumbBean b = (BreadcrumbBean) trail.get(i);
			b.setStatus(Status.UNAVAILABLE);
			trail.set(i, b);
		}
		
		return ;
	}
	
	/**
	 * Make the breadcrumb at position ordinal unavailable.
	 * @param ordinal The index of the breadcrumb.
	 */
	private void closeBreadcrumb(int ordinal) {
		if ((ordinal < 0) || (ordinal >= trail.size())) {
			return ;
		}
		
		BreadcrumbBean b = (BreadcrumbBean) trail.get(ordinal);
		b.setStatus(Status.UNAVAILABLE);
		trail.set(ordinal, b);
		
		return ;
	}
	
	/**
	 * Makes all breadcrumbs previous to this one open.
	 * Good for when you have to skip a few steps ahead.
	 * @author thickerson
	 * @param ordinal the index of the current breadcrumb.
	 * 
	 */
	private void openBreadcrumbs(int ordinal) {
		if ((ordinal < 0)||(ordinal > trail.size())) {
			return ;
		}
		
		for (int i = 0; i < ordinal; i++) {
			BreadcrumbBean b = (BreadcrumbBean) trail.get(i);
			b.setStatus(Status.AVAILABLE);
			trail.set(i, b);
		}
		return ;
	}
}
