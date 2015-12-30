/*
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 * 
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 * 
 * Created on Jul 7, 2005
 */
package org.akaza.openclinica.bean.extract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;

public class ExtractBean {
	public static final int SAS_FORMAT = 1;

	public static final int SPSS_FORMAT = 2;

	public static final int CSV_FORMAT = 3;

	public static final int PDF_FORMAT = 4;

	public static final int XLS_FORMAT = 5;

	public static final int TXT_FORMAT = 6;

	public static final String UNGROUPED = "Ungrouped";

	java.text.SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

	private int format = 1;

	private String showUniqueId = "1";

	private StudyBean parentStudy;

	private StudyBean study;

	private DatasetBean dataset;

	private DataSource ds;

	private Date dateCreated;

	// an array of StudyEventDefinitionBean objects
	private ArrayList studyEvents;

	private HashMap eventData;

	// an array of subjects
	private ArrayList subjects;

	// an array of discrepancy notes, followed by a hash map of notes
	// private ArrayList discrepancies;

	// private HashMap discrepancyNotes;

	private HashMap groupNames;

	// a hashmap of group names to use for generating the keys and column names

	// a hashmap indicating which subjects have already been added
	// key is subjectId as Integer, value is Boolean.TRUE
	private HashMap subjectsAdded;

	// keys are studySubjectId-studyEventDefinitionId-sampleOrdinal-crfId-ItemID
	// strings
	// values are the corresponding values in the item_data table
	private HashMap data;

	// keys are studyEventDefinitionId Integer
	// values are the maximum sample ordinal for that sed
	private HashMap maxOrdinals;

	// keys are itemId Integer
	// values are Boolean.TRUE
	// if an item has its id in the keySet for this HashMap,
	// that means the user has chosen to display this item in the report
	private HashMap selectedItems;

	private HashMap selectedSEDs;

	private HashMap selectedSEDCRFs;

	private HashMap<String, String> eventDescriptions;// for spss only

	private ArrayList<String> eventHeaders; // for displaying dataset in HTML
											// view,event

	// header

	private ArrayList<Object> itemNames;// for displaying dataset in HTML
										// view,item header

	private ArrayList rowValues; // for displaying dataset in html view

	private HashMap studyGroupMap;

	private HashMap studyGroupMaps;

	// to contain all the studysubject ids and link them to another hashmap, the
	// study group map above, tbh
	private ArrayList studyGroupClasses; // for displaying groups for
											// subjects in the exported dataset,
											// tbh

	private StudySubjectBean currentSubject;

	private int subjIndex = -1;

	private CRFBean currentCRF;

	private int crfIndex = -1;

	private int maxItemDataBeanOrdinal = 0;

	private StudyEventDefinitionBean currentDef;

	private int sedIndex = -1;

	private ItemBean currentItem;

	private int itemIndex = -1;

	private boolean defChanged = false;

	public ExtractBean(DataSource ds) {
		this.ds = ds;
		study = new StudyBean();
		parentStudy = new StudyBean();
		studyEvents = new ArrayList();

		data = new HashMap();
		maxOrdinals = new HashMap();
		subjects = new ArrayList();
		// discrepancies = new ArrayList();
		// discrepancyNotes = new HashMap();
		// above added 7/07, tbh
		subjectsAdded = new HashMap();
		selectedItems = new HashMap();
		selectedSEDs = new HashMap();
		groupNames = new HashMap();
		selectedSEDCRFs = new HashMap();
		itemNames = new ArrayList<Object>();
		rowValues = new ArrayList();
		eventHeaders = new ArrayList<String>();
		eventDescriptions = new HashMap<String, String>();
		// groups = new HashMap();
		// above added 7/07, tbh
	}

	/**
	 * @return Returns the eventDescriptions.
	 */
	public HashMap getEventDescriptions() {
		return eventDescriptions;
	}

	/**
	 * @param eventDescriptions
	 *            The eventDescriptions to set.
	 */
	public void setEventDescriptions(HashMap<String, String> eventDescriptions) {
		this.eventDescriptions = eventDescriptions;
	}

	//
	// TODO place to add additional metadata, tbh
	//
	public void computeReportMetadata(ReportBean answer) {
		// ///////////////////
		// //
		// HEADER //
		// //
		// ///////////////////
		answer.nextCell("Database Export Header Metadata");
		answer.nextRow();

		answer.nextCell("Dataset Name");
		answer.nextCell(dataset.getName());
		answer.nextRow();

		answer.nextCell("Date");

		answer.nextCell(sdf.format(new Date(System.currentTimeMillis())));
		answer.nextRow();

		answer.nextCell("Protocol ID");
		answer.nextCell(getParentProtocolId());
		answer.nextRow();

		answer.nextCell("Study Name");
		answer.nextCell(getParentStudyName());
		answer.nextRow();

		String siteName = getSiteName();
		if (!siteName.equals("")) {
			answer.nextCell("Site Name");
			answer.nextCell(siteName);
			answer.nextRow();
		}

		answer.nextCell("Subjects");
		answer.nextCell(Integer.toString(getNumSubjects()));
		answer.nextRow();

		int numSEDs = getNumSEDs();
		answer.nextCell("Study Event Definitions");
		answer.nextCell(String.valueOf(numSEDs));
		answer.nextRow();

		for (int i = 1; i <= numSEDs; i++) {
			String repeating = getSEDIsRepeating(i) ? " (Repeating) " : "";
			answer.nextCell("Study Event Definition " + i + repeating);
			answer.nextCell(getSEDName(i));
			answer.nextRow();

			int numSEDCRFs = getSEDNumCRFs(i);
			for (int j = 1; j <= numSEDCRFs; j++) {
				answer.nextCell("CRF ");
				answer.nextCell(getSEDCRFName(i, j));
				answer.nextCell(getSEDCRFCode(i, j));
				answer.nextRow();
			}
		}
	}

	public void computeReportData(ReportBean answer) {
		answer.nextCell("Subject Event Item Values (Item-CRF-Ordinal)");
		answer.nextRow();

		// ///////////////////
		// //
		// COLUMNS //
		// //
		// ///////////////////
		answer.nextCell("SubjID");
		answer.nextCell("ProtocolID");

		// subject column labels
		// general order: subject info first, then group info, then event info,
		// then CRF info
		if (dataset.isShowSubjectDob()) {
			if (study.getStudyParameterConfig().getCollectDob().equals("2")) {
				answer.nextCell("YOB");
			} else {
				answer.nextCell("DOB");
			}
		}
		if (dataset.isShowSubjectGender()) {
			answer.nextCell("Gender");
		}
		// TODO add additional labels here
		if (dataset.isShowSubjectStatus()) {
			answer.nextCell("Subject Status");
		}

		// TODO set datainfo-settable code here, tbh
		if (dataset.isShowSubjectUniqueIdentifier() && "1".equals(showUniqueId)) {
			answer.nextCell("Unique ID");
		}

		// ///////////////////////////////
		// subject group info here, tbh
		if (dataset.isShowSubjectGroupInformation()) {
			// System.out.println("got this far in subject group columns...");
			for (int y = 0; y < studyGroupClasses.size(); y++) {
				// System.out.println("found a study group class here: "+y);
				StudyGroupClassBean studyGroupClassBean = (StudyGroupClassBean) studyGroupClasses
						.get(y);
				answer.nextCell(studyGroupClassBean.getName());
				// System.out.println("found the name:
				// "+studyGroupClassBean.getName());
			}
		}

		// ////////////////////
		// sed column labels
		int numSEDs = getNumSEDs();
		for (int i = 1; i <= numSEDs; i++) {
			int numSamples = getSEDNumSamples(i);

			for (int j = 1; j <= numSamples; j++) {
				// ///////////////////////////////
				// ADD ALL EVENT HEADERS HERE, tbh

				if (dataset.isShowEventLocation()) {
					String location = getColumnLabel(i, j, "Location",
							numSamples);
					String description = getColumnDescription(i, j,
							"Location For ", currentDef.getName(), numSamples);
					answer.nextCell(location);
					eventHeaders.add(location);
					eventDescriptions.put(location, description);
				}
				if (dataset.isShowEventStart()) {
					String start = getColumnLabel(i, j, "StartDate", numSamples);
					String description = getColumnDescription(i, j,
							"Start Date For ", currentDef.getName(), numSamples);
					answer.nextCell(start);
					eventHeaders.add(start);
					eventDescriptions.put(start, description);

				}
				if (dataset.isShowEventEnd()) {
					String end = getColumnLabel(i, j, "EndDate", numSamples);
					String description = getColumnDescription(i, j,
							"End Date For ", currentDef.getName(), numSamples);
					answer.nextCell(end);
					eventHeaders.add(end);
					eventDescriptions.put(end, description);
				}
				if (dataset.isShowEventStatus()) {
					String eventStatus = getColumnLabel(i, j, "EventStatus",
							numSamples);
					String description = getColumnDescription(i, j,
							"Event Status For ", currentDef.getName(),
							numSamples);
					answer.nextCell(eventStatus);
					eventHeaders.add(eventStatus);
					eventDescriptions.put(eventStatus, description);
				}
				if (dataset.isShowSubjectAgeAtEvent()) {
					String subjectAgeAtEvent = getColumnLabel(i, j,
							"AgeAtEvent", numSamples);
					String description = getColumnDescription(i, j,
							"Age At Event for ", currentDef.getName(),
							numSamples);
					answer.nextCell(subjectAgeAtEvent);
					eventHeaders.add(subjectAgeAtEvent);
					eventDescriptions.put(subjectAgeAtEvent, description);
				}
			}
		}

		// item-crf-ordinal column labels
		for (int i = 1; i <= numSEDs; i++) {
			int numSamples = getSEDNumSamples(i);

			for (int j = 1; j <= numSamples; j++) {
				int numSEDCRFs = getSEDNumCRFs(i);
				if (dataset.isShowCRFcompletionDate()) {
					// logger.info();
					String crfCompletionDate = getColumnLabel(i, j,
							"CompletionDate", numSEDCRFs);
					String description = getColumnDescription(i, j,
							"Completion Date for ", currentDef.getName(),
							numSEDCRFs);// FIXME is this correct?
					answer.nextCell(crfCompletionDate);
					eventHeaders.add(crfCompletionDate);
					eventDescriptions.put(crfCompletionDate, description);
				}

				if (dataset.isShowCRFinterviewerDate()) {
					String interviewerDate = getColumnLabel(i, j,
							"InterviewerDate", numSEDCRFs);
					String description = getColumnDescription(i, j,
							"Interviewed Date for ", currentDef.getName(),
							numSEDCRFs);// FIXME is this correct?
					answer.nextCell(interviewerDate);
					eventHeaders.add(interviewerDate);
					eventDescriptions.put(interviewerDate, description);
				}

				if (dataset.isShowCRFinterviewerName()) {
					String interviewerName = getColumnLabel(i, j,
							"InterviewerName", numSEDCRFs);
					String description = getColumnDescription(i, j,
							"Interviewer Name for ", currentDef.getName(),
							numSEDCRFs);// FIXME is this correct?
					answer.nextCell(interviewerName);
					eventHeaders.add(interviewerName);
					eventDescriptions.put(interviewerName, description);
				}

				if (dataset.isShowCRFstatus()) {
					String crfStatus = getColumnLabel(i, j, "CRFStatus",
							numSEDCRFs);
					String description = getColumnDescription(i, j,
							"Event CRF Status for ", currentDef.getName(),
							numSEDCRFs);// FIXME is this correct?
					answer.nextCell(crfStatus);
					eventHeaders.add(crfStatus);
					eventDescriptions.put(crfStatus, description);
				}

				if (dataset.isShowCRFversion()) {
					String crfCompletionDate = getColumnLabel(i, j,
							"VersionName", numSEDCRFs);
					String description = getColumnDescription(i, j,
							"CRF Version Name for ", currentDef.getName(),
							numSEDCRFs);// FIXME is this correct?
					answer.nextCell(crfCompletionDate);
					eventHeaders.add(crfCompletionDate);
					eventDescriptions.put(crfCompletionDate, description);
				}

				for (int k = 1; k <= numSEDCRFs; k++) {
					// add CRF level descriptions here, tbh

					int numItems = getNumItems(i, k);
					for (int l = 1; l <= numItems; l++) {
						// for (int m = 0; m <= maxItemDataBeanOrdinal; m++) {
						for (Iterator iter = groupNames.entrySet().iterator(); iter
								.hasNext();) {
							java.util.Map.Entry groupEntry = (java.util.Map.Entry) iter
									.next();
							String groupName = (String) groupEntry.getKey();
							Integer groupCount = (Integer) groupEntry
									.getValue();
							/*
							 * System.out.println("*** Found a row in
							 * groupNames: key "+ groupName+ ", value "+
							 * groupCount);
							 */

							// int m = groupCount.intValue();
							// String data = getDataByIndex(i, j, k, l,
							// numSamples, m, groupName);
							// if (!groupName.equals(UNGROUPED) ||
							// !data.equals("")) {
							for (int m = 1; m <= groupCount.intValue(); m++) {
								answer.nextCell(getColumnItemLabel(i, j, k, l,
										numSamples, m, groupName));
								DisplayItemHeaderBean dih = new DisplayItemHeaderBean();
								dih.setItemHeaderName(getColumnItemLabel(i, j,
										k, l, numSamples, m, groupName));
								// ItemBean item = new ItemBean();
								dih.setItem(currentItem);
								itemNames.add(dih);
							}
							// }
						}
						// }
					}
				}
			}
		}

		answer.nextRow();

		// ////////////////
		// //
		// DATA //
		// //
		// ////////////////
		for (int h = 1; h <= getNumSubjects(); h++) {
			DisplayItemDataBean didb = new DisplayItemDataBean();
			String label = getSubjectStudyLabel(h);
			answer.nextCell(label);
			didb.setSubjectName(label);

			String protocolId = getParentProtocolId();
			answer.nextCell(protocolId);
			didb.setStudyLabel(protocolId);

			// ////////////////////////
			// subject column data
			if (dataset.isShowSubjectDob()) {
				if (study.getStudyParameterConfig().getCollectDob().equals("2")) {
					String yob = getSubjectYearOfBirth(h);
					answer.nextCell(yob);
					didb.setSubjectDob(yob);
				} else {
					String dob = getSubjectDateOfBirth(h);
					answer.nextCell(dob);
					didb.setSubjectDob(dob);
				}
			}
			if (dataset.isShowSubjectGender()) {
				String gender = getSubjectGender(h);
				answer.nextCell(gender);
				didb.setSubjectGender(gender);
			}

			// TODO column headers above, column values here, tbh
			if (dataset.isShowSubjectStatus()) {
				String status = getSubjectStatusName(h);
				answer.nextCell(status);
				didb.setSubjectStatus(status);
			}
			if (dataset.isShowSubjectUniqueIdentifier()
					&& "1".equals(showUniqueId)) {
				String uniqueName = getSubjectUniqueIdentifier(h);
				answer.nextCell(uniqueName);
				didb.setSubjectUniqueId(uniqueName);
			}
			if (dataset.isShowSubjectGroupInformation()) {
				ArrayList studyGroupList = new ArrayList();
				studyGroupList = getStudyGroupMap(h);// studyGroupMap =
														// getStudyGroupMap(h);
				// System.out.println("+++ picture of study group classes:
				// "+studyGroupClasses.toString());
				// System.out.println("+++ picture of study group list:
				// "+studyGroupList);
				// System.out.println("+++ picture of study group map:
				// "+studyGroupMap.toString());
				for (int y = 0; y < studyGroupClasses.size(); y++) {
					StudyGroupClassBean sgcBean = (StudyGroupClassBean) studyGroupClasses
							.get(y);
					// if the subject is in the group...
					// System.out.println("iterating through keys:
					// "+sgcBean.getId());
					Iterator iter = studyGroupList.iterator();
					while (iter.hasNext()) {
						studyGroupMap = (HashMap) iter.next();

						// System.out.println("+++ picture of study group map:
						// "+studyGroupMap.toString());

						if (studyGroupMap.containsKey(new Integer(sgcBean
								.getId()))) {
							StudyGroupBean groupBean = (StudyGroupBean) studyGroupMap
									.get(new Integer(sgcBean.getId()));
							// System.out.println("found a group name in a group
							// class: "+groupBean.getName());

							answer.nextCell(groupBean.getName());

							didb.setGroupName(new Integer(sgcBean.getId()),
									groupBean.getName());

							break;
							// didb.setGroupName(groupBean.getName());
							// otherwise we don't enter anything...
						} else {
							answer.nextCell("");

							didb.setGroupName(new Integer(sgcBean.getId()), "");
						}// end if
					}// end while
				}// end for
			}// end if

			// sed column values
			for (int i = 1; i <= numSEDs; i++) {
				int numSamples = getSEDNumSamples(i);

				// add event-specific attributes here, tbh
				for (int j = 1; j <= numSamples; j++) {
					if (dataset.isShowEventLocation()) {
						String location = getEventLocation(h, i, j);
						answer.nextCell(location);
						didb.getEventValues().add(location);

					}
					if (dataset.isShowEventStart()) {
						String start = getEventStart(h, i, j);
						answer.nextCell(start);
						didb.getEventValues().add(start);
					}
					if (dataset.isShowEventEnd()) {
						String end = getEventEnd(h, i, j);
						answer.nextCell(end);
						didb.getEventValues().add(end);
					}
					if (dataset.isShowEventStatus()) {
						String status = getEventStatus(h, i, j);
						answer.nextCell(status);
						didb.getEventValues().add(status);
					}
					if (dataset.isShowSubjectAgeAtEvent()) {
						String ageAtEvent = getAgeAtEvent(h, i, j);
						answer.nextCell(ageAtEvent);
						didb.getEventValues().add(ageAtEvent);
					}

				}
			}

			// item-crf-ordinal column labels
			for (int i = 1; i <= numSEDs; i++) {

				int numSamples = getSEDNumSamples(i);
				for (int j = 1; j <= numSamples; j++) {

					int numSEDCRFs = getSEDNumCRFs(i);
					if (dataset.isShowCRFcompletionDate()) {
						String completionDate = getCRFCompletionDate(h, i, j);
						answer.nextCell(completionDate);
						didb.getEventValues().add(completionDate);
					}

					if (dataset.isShowCRFinterviewerDate()) {
						String interviewerDate = getCRFInterviewerDate(h, i, j);
						answer.nextCell(interviewerDate);
						didb.getEventValues().add(interviewerDate);
					}

					if (dataset.isShowCRFinterviewerName()) {
						String interviewerName = getCRFInterviewerName(h, i, j);
						answer.nextCell(interviewerName);
						didb.getEventValues().add(interviewerName);

					}

					if (dataset.isShowCRFstatus()) {
						String crfStatus = getCRFStatus(h, i, j);
						answer.nextCell(crfStatus);
						didb.getEventValues().add(crfStatus);
					}

					if (dataset.isShowCRFversion()) {
						String crfVersion = getCRFVersionName(h, i, j);
						answer.nextCell(crfVersion);
						didb.getEventValues().add(crfVersion);

					}
					for (int k = 1; k <= numSEDCRFs; k++) {
						// add CRF-level column data here, tbh

						int numItems = getNumItems(i, k);
						for (int l = 1; l <= numItems; l++) {
							// adding the extra loop here for repeating items in
							// extract data, tbh
							// for (int m = 0; m <= maxItemDataBeanOrdinal; m++)
							// {
							for (java.util.Iterator iter = groupNames
									.entrySet().iterator(); iter.hasNext();) {
								java.util.Map.Entry groupEntry = (java.util.Map.Entry) iter
										.next();
								String groupName = (String) groupEntry.getKey();
								Integer groupCount = (Integer) groupEntry
										.getValue();
								for (int m = 1; m <= groupCount.intValue(); m++) {
									String data = getDataByIndex(h, i, j, k, l,
											m, groupName);
									// a guard clause here to take care of
									// empties...
									// if (!data.equals("") ||
									// !groupName.equals(UNGROUPED)) {
									answer.nextCell(data);
									didb.getItemValues().add(data);
								}
								// }
								// removing guard clause for now, tbh
								// and the column code above should look about
								// the same, tbh
							}

							// }
						}
					}
				}
			}
			rowValues.add(didb);
			answer.nextRow();
		}
	}

	public void computeReport(ReportBean answer) {
		computeReportMetadata(answer);
		answer.closeMetadata();
		computeReportData(answer);
	}

	private HashMap displayed = new HashMap();

	// keys are Strings returned by getColumnKeys, values are ArrayLists of
	// ItemBean objects in order of their display in the SED/CRF
	private HashMap sedCrfColumns = new HashMap();

	private HashMap sedCrfItemFormMetadataBeans = new HashMap();

	/**
	 * Implements the Column algorithm in "Dataset Export Algorithms" Must be
	 * called after DatasetDAO.getDatasetData();
	 */
	public void getMetadata() {
		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(ds);
		CRFDAO cdao = new CRFDAO(ds);
		CRFVersionDAO cvdao = new CRFVersionDAO(ds);
		ItemDAO idao = new ItemDAO(ds);
		ItemFormMetadataDAO ifmDAO = new ItemFormMetadataDAO(this.ds);
		StudyGroupDAO studygroupDAO = new StudyGroupDAO(ds);
		StudyGroupClassDAO studygroupclassDAO = new StudyGroupClassDAO(ds);
		// SubjectGroupMapDAO subjectGroupMapDAO = new SubjectGroupMapDAO(ds);
		studyGroupClasses = new ArrayList();
		studyGroupMap = new HashMap();
		studyGroupMaps = new HashMap<Integer, ArrayList>();
		sedCrfColumns = new HashMap();
		displayed = new HashMap();
		sedCrfItemFormMetadataBeans = new HashMap();

		studyEvents = seddao.findAllByStudy(study);
		ArrayList finalStudyEvents = new ArrayList();
		// set up group classes first, tbh
		// this bit of code throws an error b/c we try to access
		// currentSubject...

		if (dataset.isShowSubjectGroupInformation()) {
			// System.out.println("found study id for maps: "+study.getId());
			studyGroupMaps = studygroupDAO.findSubjectGroupMaps(study.getId());
			// below is for a given subject; we need a data structure for
			// all subjects
			// studyGroupMap = studygroupDAO.findByStudySubject(currentSubject);
			// problem: can't use currentSubject here, since it's not 'set up'
			// properly
			// how to get the current subject?

			System.out.println("found subject group ids: "
					+ dataset.getSubjectGroupIds().toString());
			// studyGroupClasses = dataset.getSubjectGroupIds();
			for (int h = 0; h < dataset.getSubjectGroupIds().size(); h++) {
				Integer groupId = (Integer) dataset.getSubjectGroupIds().get(h);

				StudyGroupClassBean sgclass = (StudyGroupClassBean) studygroupclassDAO
						.findByPK(groupId.intValue());
				// logger.info();
				// hmm how to link groups to subjects though? only through
				// subject group map
				System.out.println("found a studygroupclass bean: "
						+ sgclass.getName());
				studyGroupClasses.add(sgclass);
			}
		}
		for (int i = 0; i < studyEvents.size(); i++) {
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) studyEvents
					.get(i);

			if (!selectedSED(sed)) {
				continue;
			}
			ArrayList CRFs = (ArrayList) cdao.findAllActiveByDefinition(sed);
			ArrayList CRFsDisplayedInThisSED = new ArrayList();

			for (int j = 0; j < CRFs.size(); j++) {
				CRFBean cb = (CRFBean) CRFs.get(j);

				if (!selectedSEDCRF(sed, cb)) {
					continue;
				} else {

					CRFsDisplayedInThisSED.add(cb);

					ArrayList CRFVersions = cvdao.findAllByCRFId(cb.getId());
					for (int k = 0; k < CRFVersions.size(); k++) {
						CRFVersionBean cvb = (CRFVersionBean) CRFVersions
								.get(k);

						ArrayList Items = idao.findAllItemsByVersionId(cvb
								.getId());
						// sort by ordinal/name
						Collections.sort(Items);
						for (int l = 0; l < Items.size(); l++) {
							ItemBean ib = (ItemBean) Items.get(l);
							if (selected(ib) && !getDisplayed(sed, cb, ib)) {
								// System.out.println("found at
								// itemformmetadatadao: "+ib.getId()+",
								// "+cvb.getId());
								ItemFormMetadataBean ifmb = ifmDAO
										.findByItemIdAndCRFVersionId(
												ib.getId(), cvb.getId());
								addColumn(sed, cb, ib);
								addItemFormMetadataBeans(sed, cb, ifmb);
								markDisplayed(sed, cb, ib);
							}
						}
					}
				}// else
			}// for

			sed.setCrfs(CRFsDisplayedInThisSED);
			finalStudyEvents.add(sed); // make the setCrfs call "stick"
		}
		this.studyEvents = finalStudyEvents;
	}

	protected boolean selected(ItemBean ib) {
		return selectedItems.containsKey(new Integer(ib.getId()));
	}

	protected boolean selectedSEDCRF(StudyEventDefinitionBean sed, CRFBean cb) {
		return selectedSEDCRFs.containsKey(sed.getId() + "_" + cb.getId());
	}

	protected boolean selectedSED(StudyEventDefinitionBean sed) {
		return selectedSEDs.containsKey(new Integer(sed.getId()));
	}

	private void markDisplayed(StudyEventDefinitionBean sed, CRFBean cb,
			ItemBean ib) {
		displayed.put(getDisplayedKey(sed, cb, ib), Boolean.TRUE);
	}

	private boolean getDisplayed(StudyEventDefinitionBean sed, CRFBean cb,
			ItemBean ib) {
		return displayed.containsKey(getDisplayedKey(sed, cb, ib));
	}

	private void addColumn(StudyEventDefinitionBean sed, CRFBean cb, ItemBean ib) {
		String key = getColumnsKey(sed, cb);
		ArrayList columns = (ArrayList) sedCrfColumns.get(key);

		if (columns == null) {
			columns = new ArrayList();
		}

		columns.add(ib);
		sedCrfColumns.put(key, columns);
	}

	public ArrayList getColumns(StudyEventDefinitionBean sed, CRFBean cb) {
		String key = getColumnsKey(sed, cb);
		ArrayList columns = (ArrayList) sedCrfColumns.get(key);

		if (columns == null) {
			columns = new ArrayList();
		}

		return columns;
	}

	private void addItemFormMetadataBeans(StudyEventDefinitionBean sed,
			CRFBean cb, ItemFormMetadataBean ifmb) {
		String key = sed.getId() + "_" + cb.getId();
		ArrayList columns = (ArrayList) sedCrfItemFormMetadataBeans.get(key);

		if (columns == null) {
			columns = new ArrayList();
		}

		columns.add(ifmb);
		sedCrfItemFormMetadataBeans.put(key, columns);
	}

	public ArrayList getItemFormMetadataBeans(StudyEventDefinitionBean sed,
			CRFBean cb) {
		String key = sed.getId() + "_" + cb.getId();
		ArrayList columns = (ArrayList) sedCrfItemFormMetadataBeans.get(key);

		if (columns == null) {
			columns = new ArrayList();
		}

		return columns;
	}

	// public void addDiscrepancyNoteData(
	// Integer itemDataId,
	// String description,
	// Integer resolutionStatusId,
	// String detailedNotes,
	// Integer discTypeId) {
	// //look for discrepancy notes already in the system,
	// //then add if necessary
	// //TODO what if there are more than one disc. note?
	// if (!discrepancyNotes.containsKey(itemDataId)) {
	// DiscrepancyNoteBean dnb = new DiscrepancyNoteBean();
	// dnb.setDescription(description);
	// dnb.setResolutionStatusId(resolutionStatusId.intValue());
	// dnb.setResStatus(
	// ResolutionStatus.get(
	// resolutionStatusId.intValue()));
	// dnb.setDetailedNotes(detailedNotes);
	// dnb.setDiscrepancyNoteTypeId(discTypeId.intValue());
	// discrepancyNotes.put(itemDataId, dnb);
	// }
	//	  
	// }

	public void addStudySubjectData(Integer studySubjectId,
			String studySubjectLabel, Date dateOfBirth, String gender,
			Integer subjectStatusId, Boolean dobCollected,
			String uniqueIdentifier) {
		if (!subjectsAdded.containsKey(studySubjectId)) {
			StudySubjectBean sub = new StudySubjectBean();
			sub.setId(studySubjectId.intValue());
			sub.setLabel(studySubjectLabel);

			sub.setDateOfBirth(dateOfBirth);
			if (gender != null && gender.length() > 0) {
				sub.setGender(gender.charAt(0));
			} else {
				sub.setGender(' ');
			}
			sub.setStatus(Status.get(subjectStatusId.intValue()));
			// sub.setSecondaryLabel(secondaryID);//????
			sub.setUniqueIdentifier(uniqueIdentifier);
			// sub.setEnrollmentDate(enrollmentDate);
			// TODO need to find enrollment date, later, tbh
			subjects.add(sub);
			subjectsAdded.put(studySubjectId, Boolean.TRUE);
		}
	}

	/*
	 * addStudyEventData, a function which puts information about a
	 * study-subject taking an event-crf into the ExtractBean's interface.
	 * 
	 */

	public void addStudyEventData(Integer studySubjectId,
			String studyEventDefinitionName, 
			Integer studyEventDefinitionId,
			Integer sampleOrdinal, 
			String studyEventLocation,
			Date studyEventStart, 
			Date studyEventEnd, 
			String crfVersionName,
			Integer crfVersionStatusId, 
			Date dateInterviewed,
			String interviewerName, 
			Date dateCompleted,
			Date dateValidateCompleted, 
			Integer completionStatusId) {
		
		if ((studySubjectId == null) || (studyEventDefinitionId == null)
				|| (sampleOrdinal == null) || (studyEventLocation == null)
				|| (studyEventStart == null) || (studyEventEnd == null)) {
			return;
		}

		if ((studyEventDefinitionId.intValue() <= 0)
				|| (studySubjectId.intValue() <= 0)
				|| (sampleOrdinal.intValue() <= 0)) {
			return;
		}
		// YW 08-21-2007 << fetch start_time_flag and end_time_flag
		StudyEventDAO sedao = new StudyEventDAO(ds);
		StudyEventBean se = (StudyEventBean) sedao
				.findByStudySubjectIdAndDefinitionIdAndOrdinal(studySubjectId,
						studyEventDefinitionId, sampleOrdinal);
		// YW >>
		StudyEventBean event = new StudyEventBean();
		EventCRFBean eventCRF = new EventCRFBean();
		event.setName(studyEventDefinitionName);
		event.setDateStarted(studyEventStart);
		event.setDateEnded(studyEventEnd);
		event.setLocation(studyEventLocation);
		event.setSampleOrdinal(sampleOrdinal.intValue());
		event.setStudyEventDefinitionId(studyEventDefinitionId.intValue());
		event.setStudySubjectId(studySubjectId.intValue());
		event.setStartTimeFlag(se.getStartTimeFlag());
		event.setEndTimeFlag(se.getEndTimeFlag());

		eventCRF.setCompletionStatusId(completionStatusId.intValue());//
		eventCRF.setInterviewerName(interviewerName);
		eventCRF.setDateCompleted(dateCompleted);
		eventCRF.setDateValidateCompleted(dateValidateCompleted);
		// eventCRF.setCreatedDate();//same as interviewed date? NO
		eventCRF.setDateInterviewed(dateInterviewed);

		CRFVersionBean crfVersion = new CRFVersionBean();
		crfVersion.setName(crfVersionName);
		crfVersion.setStatus(Status.get(crfVersionStatusId.intValue()));
		crfVersion.setStatusId(crfVersionStatusId.intValue());
		eventCRF.setCrfVersion(crfVersion);
		// logger.info();
		ArrayList events = new ArrayList();
		events.add(eventCRF);
		System.out.println("///adding an event CRF..."
				+ eventCRF.getInterviewerName());
		event.setEventCRFs(events);// hmm, one to one relationship?
		// guard clause to see if it's in there already?
		// not rly, the above is only used in auditlogging
		// could fit in crf and crf version ids here, though
		String key = getStudyEventDataKey(studySubjectId.intValue(),
				studyEventDefinitionId.intValue(), sampleOrdinal.intValue());
		if (eventData == null) {
			eventData = new HashMap();
		}
		eventData.put(key, event);
		//the problem: we want to order by start date
		//but hashmaps are by their very nature hard to order
		//and there is no contigous start date that we can sort on, i.e. we only 
		//look at one at a time.
	}

	/*
	 * addStudyGroupData: each subject can have more than one Group, so we need
	 * to create a list for each subject and check if the groupclasses are
	 * there, and which group it is. tbh
	 */
	public void addSubjectGroupData(Integer subjectGroupId,
			Integer subjectGroupClassId) {
		// DO NOT USE -- using another method, tbh
	}

	/*
	 * addGroupName -- check to see if this group name is in the system, if it
	 * is not, add it together with its ordinal If it is already in the system,
	 * look at the ordinals and find out which is bigger, then add the bigger of
	 * the two back into the data structure, tbh
	 */
	public void addGroupName(String name, Integer ordinal) {
		if (name == null) {
			return;
		}

		if (!groupNames.containsKey(name)) {
			groupNames.put(name, ordinal);
		} else {
			Integer numTimes = (Integer) groupNames.get(name);

			if (numTimes > ordinal) {
				groupNames.put(name, numTimes);
			} else {
				groupNames.put(name, ordinal);
			}
		}
	}

	/*
	 * addItemData -- we create a key out of a combination of variables, and
	 * then put the data in a hashmap with the key.
	 */
	public void addItemData(Integer studySubjectId,
			Integer studyEventDefinitionId, Integer sampleOrdinal, // study
																	// event
																	// sample
																	// ordinal
			Integer crfId, Integer itemId, String itemValue,
			Integer itemDataOrdinal,// item data ordinal, having to do
									// specifically with repeating items
			String groupName) {// item group name, having to do with the group
								// name in the database
		// String itemGroupName) {
		if ((studyEventDefinitionId == null) || (studySubjectId == null)
				|| (crfId == null) || (itemId == null)
				|| (sampleOrdinal == null) || (itemValue == null)) {
			return;
		}

		if ((studyEventDefinitionId.intValue() <= 0)
				|| (studySubjectId.intValue() <= 0) || (crfId.intValue() <= 0)
				|| (itemId.intValue() <= 0) || (sampleOrdinal.intValue() <= 0)) {
			return;
		}

		String key = getDataKey(studySubjectId.intValue(),
				studyEventDefinitionId.intValue(), sampleOrdinal.intValue(),
				crfId.intValue(), itemId.intValue(),
				itemDataOrdinal.intValue(), groupName);

		data.put(key, itemValue);
		// groups.put(key, itemGroupName);
		int maxOrdinal = getMaxOrdinal(studyEventDefinitionId.intValue());
		if (maxOrdinal < sampleOrdinal.intValue()) {
			setMaxOrdinal(studyEventDefinitionId.intValue(), sampleOrdinal
					.intValue());
		}
		selectedItems.put(itemId, Boolean.TRUE);
		selectedSEDCRFs.put(studyEventDefinitionId.intValue() + "_"
				+ crfId.intValue(), Boolean.TRUE);
		selectedSEDs.put(studyEventDefinitionId, Boolean.TRUE);

		return;
	}

	protected String getDataByIndex(int subjectInd, int sedInd,
			int sampleOrdinal, int crfInd, int itemInd, int itemOrdinal,
			String groupName) {
		syncSubjectIndex(subjectInd);
		syncItemIndex(sedInd, crfInd, itemInd);
		String key = getDataKey(currentSubject.getId(), currentDef.getId(),
				sampleOrdinal, currentCRF.getId(), currentItem.getId(),
				itemOrdinal, groupName);
		String itemValue = (String) data.get(key);

		if (itemValue == null) {
			itemValue = "";
		}

		return itemValue;
	}

	// //////////////////////////
	// Max Ordinals Section //
	// //////////////////////////

	private Integer getMaxOrdinalsKey(int studySubjectId) {
		return new Integer(studySubjectId);
	}

	private int getMaxOrdinal(int studyEventDefinitionId) {
		Integer key = getMaxOrdinalsKey(studyEventDefinitionId);
		try {
			if (maxOrdinals.containsKey(key)) {
				Integer maxOrdinal = (Integer) maxOrdinals.get(key);
				if (maxOrdinal != null) {
					return maxOrdinal.intValue();
				}
			}
		} catch (Exception e) {
		}

		return 0;
	}

	private void setMaxOrdinal(int studyEventDefinitionId, int sampleOrdinal) {
		Integer key = getMaxOrdinalsKey(studyEventDefinitionId);
		maxOrdinals.put(key, new Integer(sampleOrdinal));
	}

	// /////////////////////////////////////////////
	// //
	// RETRIEVE METADATA AND DATA SECTION //
	// //
	// /////////////////////////////////////////////

	public String getParentProtocolId() {
		if (!parentStudy.isActive()) {
			return study.getIdentifier();
		} else {
			return parentStudy.getIdentifier();
		}
	}

	public String getParentStudyName() {
		if (!parentStudy.isActive()) {
			return study.getName();
		} else {
			return parentStudy.getName();
		}
	}

	public String getParentStudySummary() {
		if (!parentStudy.isActive()) {
			return study.getSummary();
		} else {
			return parentStudy.getSummary();
		}
	}

	private String getSiteName() {
		if (parentStudy.isActive()) {
			return study.getName();
		} else {
			return "";
		}
	}

	public int getNumSubjects() {
		if (subjects != null) {
			return subjects.size();
		} else {
			return 0;
		}
	}

	protected int getNumSEDs() {
		return studyEvents.size();
	}

	public int getMaxItemDataBeanOrdinal() {
		return maxItemDataBeanOrdinal;
	}

	public void setMaxItemDataBeanOrdinal(int maxItemDataBeanOrdinal) {
		this.maxItemDataBeanOrdinal = maxItemDataBeanOrdinal;
	}

	private void syncSubjectIndex(int ind) {
		if (subjIndex != ind) {
			currentSubject = (StudySubjectBean) subjects.get(ind - 1);
			subjIndex = ind;
		}
	}

	private String getSubjectStudyLabel(int h) {
		syncSubjectIndex(h);

		return currentSubject.getLabel();
	}

	private String getSubjectDateOfBirth(int h) {
		syncSubjectIndex(h);
		Date dob = currentSubject.getDateOfBirth();
		return dob == null ? "" : sdf.format(dob);
	}

	private String getSubjectStatusName(int h) {
		syncSubjectIndex(h);
		Status status = currentSubject.getStatus();
		return status.getName();
	}

	private String getSubjectUniqueIdentifier(int h) {
		syncSubjectIndex(h);
		String uni = currentSubject.getSecondaryLabel();
		uni = currentSubject.getUniqueIdentifier();
		System.out.println("+++ comparing " + uni + " vs. secondary label "
				+ currentSubject.getSecondaryLabel());
		return uni;
	}

	private ArrayList getStudyGroupMap(int h) {
		syncSubjectIndex(h);
		Integer key = new Integer(currentSubject.getId());
		ArrayList value = (ArrayList) studyGroupMaps.get(key);
		return (value != null ? value : new ArrayList());
	}

	private String getSubjectYearOfBirth(int h) {
		syncSubjectIndex(h);
		Date dob = currentSubject.getDateOfBirth();

		if (dob == null) {
			return "";
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(dob);
		int year = cal.get(Calendar.YEAR);

		return (year + "");
	}

	private String getSubjectGender(int h) {
		syncSubjectIndex(h);
		return String.valueOf(currentSubject.getGender());
	}

	private void syncSEDIndex(int ind) {
		if (sedIndex != ind) {
			currentDef = (StudyEventDefinitionBean) studyEvents.get(ind - 1);
			sedIndex = ind;
		}
	}

	private boolean getSEDIsRepeating(int ind) {
		syncSEDIndex(ind);
		return currentDef.isRepeating();
	}

	private String getSEDName(int ind) {
		syncSEDIndex(ind);
		return currentDef.getName();
	}

	protected int getSEDNumCRFs(int ind) {
		syncSEDIndex(ind);
		return currentDef.getCrfs().size();
	}

	// below created to support CRF metadata, tbh
	protected String getCRFStatus(int h, int i, int j) {

		// CRFBean crfbean = (CRFBean) currentDef.getCrfs().get(crf);
		// return crfbean.getStatus().getName();
		// return currentCRF.getStatus().getName();
		StudyEventBean seb = getEvent(h, i, j);

		EventCRFBean eventCRF = null;
		if (seb.getEventCRFs().size() > 0) {
			eventCRF = (EventCRFBean) seb.getEventCRFs().get(0);
		}

		return (eventCRF != null ? eventCRF.getStatus().getName() : "");
	}

	protected String getCRFVersionName(int h, int i, int j) {
		// syncCRFIndex(ind, crfv);
		// how to cross ref this with what's being entered???
		// return currentCRF.getVersions()
		StudyEventBean seb = getEvent(h, i, j);

		EventCRFBean eventCRF = null;
		if (seb.getEventCRFs().size() > 0) {
			eventCRF = (EventCRFBean) seb.getEventCRFs().get(0);
		}

		return (eventCRF != null ? eventCRF.getCrfVersion().getName() : "");

	}

	protected String getCRFInterviewerDate(int h, int i, int j) {
		StudyEventBean seb = getEvent(h, i, j);

		EventCRFBean eventCRF = null;
		if (seb.getEventCRFs().size() > 0) {
			eventCRF = (EventCRFBean) seb.getEventCRFs().get(0);
		}

		// Calendar c = Calendar.getInstance();
		// c.setTime(eventCRF.getDateInterviewed());
		return (eventCRF != null ? sdf.format(eventCRF.getDateInterviewed())
				: "");

	}

	protected String getCRFInterviewerName(int h, int i, int j) {
		StudyEventBean seb = getEvent(h, i, j);

		EventCRFBean eventCRF = null;
		if (seb.getEventCRFs().size() > 0) {
			eventCRF = (EventCRFBean) seb.getEventCRFs().get(0);
		}

		return (eventCRF != null ? eventCRF.getInterviewerName() : "");

	}

	protected String getCRFCompletionDate(int h, int i, int j) {
		StudyEventBean seb = getEvent(h, i, j);

		EventCRFBean eventCRF = null;
		if (seb.getEventCRFs().size() > 0) {
			eventCRF = (EventCRFBean) seb.getEventCRFs().get(0);
		}

		return (eventCRF.getDateValidateCompleted() == null ? sdf
				.format(eventCRF.getDateCompleted()) : sdf.format(eventCRF
				.getDateValidateCompleted()));// need to be fixed?

	}

	private void syncCRFIndex(int sedInd, int crfInd) {
		syncSEDIndex(sedInd);

		currentCRF = (CRFBean) currentDef.getCrfs().get(crfInd - 1);
		crfIndex = crfInd;

	}

	private String getSEDCRFName(int sedInd, int crfInd) {
		syncCRFIndex(sedInd, crfInd);
		return currentCRF.getName();
	}

	protected int getNumItems(int sedInd, int crfInd) {
		syncCRFIndex(sedInd, crfInd);
		ArrayList items = getColumns(currentDef, currentCRF);
		return items.size();
	}

	private void syncItemIndex(int sedInd, int crfInd, int itemInd) {
		syncCRFIndex(sedInd, crfInd);

		ArrayList items = getColumns(currentDef, currentCRF);
		currentItem = (ItemBean) items.get(itemInd - 1);
		itemIndex = itemInd;

	}

	private String getItemName(int sedInd, int crfInd, int itemInd) {
		syncItemIndex(sedInd, crfInd, itemInd);
		return currentItem.getName();
	}

	// //////////////////
	// //
	// HASHMAP KEYS //
	// //
	// //////////////////

	private String getDataKey(int studySubjectId, int studyEventDefinitionId,
			int sampleOrdinal, int crfId, int itemId, int itemOrdinal,
			String groupName) {
		String groupString = "";
		if (!groupName.equals(UNGROUPED)) {
			// need to remember that this is hard coded, need to place it
			// outside the code somehow, tbh
			groupString = "_" + groupName + "_" + itemOrdinal;
		}
		return studySubjectId + "_" + studyEventDefinitionId + "_"
				+ sampleOrdinal + "_" + crfId + "_" + itemId + groupString;
	}

	private String getDisplayedKey(StudyEventDefinitionBean sed, CRFBean cb,
			ItemBean ib) {
		return sed.getId() + "_" + cb.getId() + "_" + ib.getId();
	}

	private String getColumnsKey(StudyEventDefinitionBean sed, CRFBean cb) {
		return sed.getId() + "_" + cb.getId();
	}

	private String getStudyEventDataKey(int studySubjectId,
			int studyEventDefinitionId, int sampleOrdinal) {
		String key = studySubjectId + "_" + studyEventDefinitionId + "_"
				+ sampleOrdinal;
		// System.out.println("found key "+key);
		return key;
	}

	// /////////////////////////////
	// //
	// CODES AND COLUMN LABELS //
	// //
	// /////////////////////////////

	public static String getSEDCode(int sedInd) {
		sedInd--;
		if (sedInd > 26) {
			int digit1 = sedInd / 26;
			int digit2 = sedInd % 26;

			char letter1 = (char) ('A' + digit1);
			char letter2 = (char) ('A' + digit2);

			return "" + letter1 + letter2;
		} else {
			char letter = (char) ('A' + sedInd);

			return "" + letter;
		}
	}

	public static String getSEDCRFCode(int sedInd, int crfInd) {
		return getSEDCode(sedInd) + crfInd;
	}

	private String getSampleCode(int ordinal, int numSamples) {
		return numSamples > 1 ? "_" + ordinal : "";
	}

	private String getColumnLabel(int sedInd, int ordinal, String labelType,
			int numSamples) {
		return labelType + "_" + getSEDCode(sedInd)
				+ getSampleCode(ordinal, numSamples);
	}

	private String getColumnDescription(int sedInd, int ordinal,
			String labelType, String defName, int numSamples) {
		return labelType + defName + "(" + getSEDCode(sedInd)
				+ getSampleCode(ordinal, numSamples) + ")";
	}

	private String getColumnItemLabel(int sedInd, int ordinal, int crfInd,
			int itemInd, int numSamples, int itemDataOrdinal, String groupName) {
		String groupEnd = "";
		if (!groupName.equals(UNGROUPED)) {
			groupEnd = "_" + groupName + "_" + itemDataOrdinal;
		}
		return getItemName(sedInd, crfInd, itemInd) + "_"
				+ getSEDCRFCode(sedInd, crfInd)
				+ getSampleCode(ordinal, numSamples) + groupEnd;// "_" +
																// itemDataOrdinal;
	}

	// ////////////////////////////
	// //
	// GETTERS AND SETTERS //
	// //
	// ////////////////////////////

	/**
	 * @return Returns the study.
	 */
	public StudyBean getStudy() {
		return study;
	}

	/**
	 * @param study
	 *            The study to set.
	 */
	public void setStudy(StudyBean study) {
		this.study = study;
	}

	/**
	 * @return Returns the parentStudy.
	 */
	public StudyBean getParentStudy() {
		return parentStudy;
	}

	/**
	 * @param parentStudy
	 *            The parentStudy to set.
	 */
	public void setParentStudy(StudyBean parentStudy) {
		this.parentStudy = parentStudy;
	}

	/**
	 * @return Returns the format.
	 */
	public int getFormat() {
		return format;
	}

	/**
	 * @param format
	 *            The format to set.
	 */
	public void setFormat(int format) {
		this.format = format;
	}

	/**
	 * @return Returns the dataset.
	 */
	public DatasetBean getDataset() {
		return dataset;
	}

	/**
	 * @param dataset
	 *            The dataset to set.
	 */
	public void setDataset(DatasetBean dataset) {
		this.dataset = dataset;
	}

	/**
	 * @return Returns the studyEvents.
	 */
	public ArrayList getStudyEvents() {
		return studyEvents;
	}

	/**
	 * The maximum over all ordinals over all study events for the provided SED.
	 * 
	 * @param i
	 *            An index into the studyEvents list for the SED whose max
	 *            ordinal we want.
	 * @return The maximum number of samples for the i-th SED.
	 */
	public int getSEDNumSamples(int i) {
		syncSEDIndex(i);
		int sedId = currentDef.getId();
		return getMaxOrdinal(sedId);
	}

	/**
	 * Get the event correspodning to the provided study subject, SED and sample
	 * ordinal.
	 * 
	 * @param h
	 *            An index into the array of subjects.
	 * @param i
	 *            An index into the array of SEDs.
	 * @param j
	 *            The sample ordinal.
	 * @return The event correspodning to the provided study subject, SED and
	 *         sample ordinal.
	 */
	private StudyEventBean getEvent(int h, int i, int j) {
		syncSubjectIndex(h);
		syncSEDIndex(i);

		String key = getStudyEventDataKey(currentSubject.getId(), currentDef
				.getId(), j);
		StudyEventBean seb = (StudyEventBean) eventData.get(key);

		if (seb == null) {
			return new StudyEventBean();
		} else {
			return seb;
		}
	}

	private String getEventLocation(int h, int i, int j) {
		return getEvent(h, i, j).getLocation();
	}

	private String getEventStart(int h, int i, int j) {
		StudyEventBean seb = getEvent(h, i, j);
		Date start = seb.getDateStarted();
		// YW 08-20-2007 for displaying time if appliable
		if (seb.getStartTimeFlag()) {
			return (start != null ? new SimpleDateFormat(
					"MM/dd/yyyy hh:mm:ss a").format(start) : "");
		} else {
			return (start != null ? sdf.format(start) : "");
		}
	}

	private String getEventEnd(int h, int i, int j) {
		StudyEventBean seb = getEvent(h, i, j);
		Date end = seb.getDateEnded();
		// YW 08-20-2007 for displaying time if appliable
		if (seb.getStartTimeFlag()) {
			return (end != null ? new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a")
					.format(end) : "");
		} else {
			return (end != null ? sdf.format(end) : "");
		}
	}

	private String getEventStatus(int h, int i, int j) {
		StudyEventBean seb = getEvent(h, i, j);
		/*
		 * System.out.println("+++ found study event "+ seb.getName()+ " id "+
		 * seb.getId()+ " status "+ seb.getStatus().getName());
		 */
		return (seb.getStatus() != null ? seb.getStatus().getName() : "");
	}

	private String getAgeAtEvent(int h, int i, int j) {
		// syncSubjectIndex(h);
		StudyEventBean seb = getEvent(h, i, j);
		Date startDate = seb.getDateStarted();
		// to try and avoid NPEs, tbh 092007
		startDate = (seb.getDateStarted() != null ? seb.getDateStarted()
				: new Date());
		Date age = currentSubject.getDateOfBirth();
		System.out.println("found SEB: " + seb.getName());
		System.out.println(" found dob: " + sdf.format(age)
				+ " for our subject " + currentSubject.getName());
		System.out.println(", found date started: " + sdf.format(startDate));
		String answer = "";
		if (age.before(startDate)) {
			Calendar dateOfBirth = Calendar.getInstance();
			dateOfBirth.setTime(age);// new GregorianCalendar(age);
			Calendar theStartDate = Calendar.getInstance();// new
															// GregorianCalendar(startDate);
			theStartDate.setTime(startDate);
			int theAge = theStartDate.get(Calendar.YEAR)
					- dateOfBirth.get(Calendar.YEAR);
			Calendar today = Calendar.getInstance();
			// add the age to the year to see if it's happened yet
			dateOfBirth.add(Calendar.YEAR, theAge);
			// subtract one from the age if the birthday hasn't happened yet
			if (today.before(dateOfBirth)) {
				theAge--;
			}
			answer = "" + theAge;
		} else {
			// ideally should not get here, but we have an 'error' code if it
			// does, tbh
			answer = "-1";
			// System.out.println("reached error state for age at event");
		}
		return answer;
	}

	protected ArrayList getSubjects() {
		return this.subjects;
	}

	// public String getItemValue(int h, int i, int j, int k, int l) {
	// StudySubjectBean sub = (StudySubjectBean) subjects.get(h);
	// int studyEventDefinitionId = ((StudyEventDefinitionBean)
	// studyEvents.get(i)).getId();
	// ArrayList events = (ArrayList) eventData.get(new
	// Integer(studyEventDefinitionId));
	// int sampleOrdinal = ((StudyEventBean) events.get(j)).getSampleOrdinal();
	// ArrayList items = getColumns(currentDef, currentCRF);
	//
	// String key = getDataKey(sub.getId(), studyEventDefinitionId,
	// sampleOrdinal,
	// currentCRF.getId(),
	// ((ItemBean) items.get(l)).getId());
	// return (String) data.get(key);
	//
	// }

	/**
	 * @return Returns the dateCreated.
	 */
	public Date getDateCreated() {
		return dateCreated;
	}

	/**
	 * @param dateCreated
	 *            The dateCreated to set.
	 */
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	/**
	 * @return Returns the itemNames.
	 */
	public ArrayList getItemNames() {
		return itemNames;
	}

	/**
	 * @param itemNames
	 *            The itemNames to set.
	 */
	public void setItemNames(ArrayList itemNames) {
		this.itemNames = itemNames;
	}

	/**
	 * @return Returns the rowValues.
	 */
	public ArrayList getRowValues() {
		return rowValues;
	}

	/**
	 * @param rowValues
	 *            The rowValues to set.
	 */
	public void setRowValues(ArrayList rowValues) {
		this.rowValues = rowValues;
	}

	/**
	 * @return Returns the eventHeaders.
	 */
	public ArrayList getEventHeaders() {
		return eventHeaders;
	}

	/**
	 * @param eventHeaders
	 *            The eventHeaders to set.
	 */
	public void setEventHeaders(ArrayList eventHeaders) {
		this.eventHeaders = eventHeaders;
	}

	public ArrayList getStudyGroupClasses() {
		return studyGroupClasses;
	}

	public void setStudyGroupClasses(ArrayList studyGroupClasses) {
		this.studyGroupClasses = studyGroupClasses;
	}

	public HashMap getGroupNames() {
		return groupNames;
	}

	public void setGroupNames(HashMap groupNames) {
		this.groupNames = groupNames;
	}

	public String getShowUniqueId() {
		return showUniqueId;
	}

	public void setShowUniqueId(String showUniqueId) {
		this.showUniqueId = showUniqueId;
	}

}