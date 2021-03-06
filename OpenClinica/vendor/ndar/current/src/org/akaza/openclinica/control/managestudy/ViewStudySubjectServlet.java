/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.AuditEventBean;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.admin.StudyEventAuditBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.core.SummaryDataEntryStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DisplayEventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudyEventRow;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.submit.CreateNewStudyEventServlet;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.AuditEventDAO;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * Processes 'view subject' request
 */
public class ViewStudySubjectServlet extends SecureController {
  /**
   * Checks whether the user has the right permission to proceed function
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }

    if (currentRole != null) {
		Role r = currentRole.getRole();
		if ((r != null) &&
				(r.equals(Role.COORDINATOR)
						|| r.equals(Role.STUDYDIRECTOR)
						|| r.equals(Role.INVESTIGATOR)
						|| r.equals(Role.RESEARCHASSISTANT))) {
			return;
		}
	}


    addPageMessage("You don't have correct privilege in your current active study. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECT_SERVLET,
        "not study director", "1");

  }

  public static ArrayList getDisplayStudyEventsForStudySubject(StudySubjectBean studySub, DataSource ds, UserAccountBean ub, StudyUserRoleBean currentRole) {
	StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(ds);
	StudyEventDAO sedao = new StudyEventDAO(ds);
	EventCRFDAO ecdao = new EventCRFDAO(ds);
	EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(ds);
    
    ArrayList events = (ArrayList) sedao.findAllByStudySubject(studySub);

    ArrayList displayEvents = new ArrayList();
    for (int i = 0; i < events.size(); i++) {
      StudyEventBean event = (StudyEventBean) events.get(i);

      StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(event
          .getStudyEventDefinitionId());
      event.setStudyEventDefinition(sed);

      //find all active crfs in the definition
      ArrayList eventDefinitionCRFs = (ArrayList) edcdao.findAllActiveByEventDefinitionId(sed.getId());

      ArrayList eventCRFs = ecdao.findAllByStudyEvent(event);
      
      //construct info needed on view study event page
      DisplayStudyEventBean de = new DisplayStudyEventBean();
      de.setStudyEvent(event);
      de.setDisplayEventCRFs(getDisplayEventCRFs(ds, eventCRFs, eventDefinitionCRFs, ub, currentRole));
      ArrayList al = getUncompletedCRFs(ds, eventDefinitionCRFs, eventCRFs);
      populateUncompletedCRFsWithCRFAndVersions(ds, al);
      de.setUncompletedCRFs(al);
      
      displayEvents.add(de);
      //event.setEventCRFs(createAllEventCRFs(eventCRFs, eventDefinitionCRFs));

    }
    
    return displayEvents;
   }
  
  public void processRequest() throws Exception {
    SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
    StudySubjectDAO subdao = new StudySubjectDAO(sm.getDataSource());
    FormProcessor fp = new FormProcessor(request);
    int studySubId = fp.getInt("id", true);//studySubjectId
    String from=fp.getString("from");
    //String studyIdString = request.getParameter("studyId");
    if (studySubId == 0) {
      addPageMessage("Please choose a subject to view.");
      forwardPage(Page.LIST_STUDY_SUBJECT_SERVLET);
    } else {
      if (!StringUtil.isBlank(from)) {
        request.setAttribute("from",from); //form ListSubject or ListStudySubject
      } else {
        request.setAttribute("from","");
      }
      StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);      

      request.setAttribute("studySub", studySub);

      int studyId = studySub.getStudyId();
      int subjectId = studySub.getSubjectId();

      SubjectBean subject = (SubjectBean) sdao.findByPK(subjectId);
      if (currentStudy.getStudyParameterConfig().getCollectDob().equals("2")){
        Date dob = subject.getDateOfBirth();
        if (dob != null) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dob);
        int year = cal.get(Calendar.YEAR);
        request.setAttribute("yearOfBirth",new Integer(year));
        } else {
          request.setAttribute("yearOfBirth","");
        }
      } 
       
      request.setAttribute("subject", subject);

      if (subject.getFatherId() > 0) {
        SubjectBean father = (SubjectBean) sdao.findByPK(subject.getFatherId());
        request.setAttribute("father", father);
      } else {
        request.setAttribute("father", new SubjectBean());
      }

      if (subject.getMotherId() > 0) {
        SubjectBean mother = (SubjectBean) sdao.findByPK(subject.getMotherId());
        request.setAttribute("mother", mother);
      } else {
        request.setAttribute("mother", new SubjectBean());
      }

      StudyDAO studydao = new StudyDAO(sm.getDataSource());
      StudyBean study = (StudyBean) studydao.findByPK(studyId);
      request.setAttribute("study", study);

      if (study.getParentStudyId() > 0) {//this is a site,find parent
        StudyBean parentStudy = (StudyBean) studydao.findByPK(study.getParentStudyId());
        request.setAttribute("parentStudy", parentStudy);
      } else {
        request.setAttribute("parentStudy", new StudyBean());
      }

      ArrayList children = (ArrayList) sdao.findAllChildrenByPK(subjectId);

      request.setAttribute("children", children);

      //find study events
      StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
      StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
      EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());

      //find all eventcrfs for each event
      EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());

      ArrayList displayEvents = getDisplayStudyEventsForStudySubject(studySub, sm.getDataSource(), ub, currentRole);
      
      EntityBeanTable table = fp.getEntityBeanTable();
      table.setSortingIfNotExplicitlySet(1, false);//sort by start date, desc
      ArrayList allEventRows = DisplayStudyEventRow.generateRowsFromBeans(displayEvents);
      
      String[] columns = {"Event", "Start Date",  "Location", "Status", "Data Entry Status", "Actions", "CRFs (Name, Version, Status, Updated, Actions)"};
  	  table.setColumns(new ArrayList<String>(Arrays.asList(columns)));
  	  table.hideColumnLink(5);
  	  table.hideColumnLink(6);
  	  table.addLink("Add new event", "CreateNewStudyEvent?" + CreateNewStudyEventServlet.INPUT_STUDY_SUBJECT_ID_FROM_VIEWSUBJECT + "=" + studySub.getId());
  	  HashMap<String, String> args = new HashMap<String, String>();
  	  args.put("id", ""+studySubId);
  	  table.setQuery("ViewStudySubject", args);
  	  table.setRows(allEventRows);
  	  table.computeDisplay();

  	
  	  request.setAttribute("table", table);
      //request.setAttribute("displayEvents", displayEvents);

      //find group info
      SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(sm.getDataSource());
      ArrayList groupMaps = (ArrayList) sgmdao.findAllByStudySubject(studySubId);
      request.setAttribute("groups", groupMaps);
      
      //find audit log for events
      AuditEventDAO aedao = new AuditEventDAO(sm.getDataSource());
      ArrayList<AuditEventBean> logs = aedao.findEventStatusLogByStudySubject(studySubId);
      //logger.warning("^^^ retrieved logs");
      UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
      ArrayList<StudyEventAuditBean> eventLogs = new ArrayList<StudyEventAuditBean>();
     // logger.warning("^^^ starting to iterate");
      for (AuditEventBean avb : logs) {
        StudyEventAuditBean sea = new StudyEventAuditBean();
        sea.setAuditEvent(avb);
        StudyEventBean se = (StudyEventBean)sedao.findByPK(avb.getEntityId());
        StudyEventDefinitionBean sed = (StudyEventDefinitionBean)seddao.findByPK(se.getStudyEventDefinitionId());
        sea.setDefinition(sed);
        String old =avb.getOldValue().trim();
        try {
			if (!StringUtil.isBlank(old)){
			  SubjectEventStatus oldStatus = SubjectEventStatus.get((new Integer(old)).intValue());
			  sea.setOldSubjectEventStatus(oldStatus);
			  //TODO: add old/new summaryDataEntryStatus?  --AJF
			}
			String newValue =avb.getNewValue().trim();
			if (!StringUtil.isBlank(newValue)){
			  SubjectEventStatus newStatus = SubjectEventStatus.get((new Integer(newValue)).intValue());
			  sea.setNewSubjectEventStatus(newStatus);
			  //TODO: add old/new summaryDataEntryStatus?  --AJF
			}
		} catch (NumberFormatException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			//logger.warning("^^^ caught NFE");
		}
        UserAccountBean updater = (UserAccountBean)udao.findByPK(avb.getUserId());
        sea.setUpdater(updater);
        eventLogs.add(sea);
        
      }
      //logger.warning("^^^ finished iteration");
      request.setAttribute("eventLogs", eventLogs);
      
      forwardPage(Page.VIEW_STUDY_SUBJECT);

    }
  }

  /**
   * Each of the event CRFs with its corresponding CRFBean. Then generates a
   * list of DisplayEventCRFBeans, one for each event CRF.
   * 
   * @param eventCRFs
   *          The list of event CRFs for this study event.
   * @param eventDefinitionCRFs
   *          The list of event definition CRFs for this study event.
   * @return The list of DisplayEventCRFBeans for this study event.
   */
  public static ArrayList getDisplayEventCRFs(DataSource ds, ArrayList eventCRFs, ArrayList eventDefinitionCRFs, UserAccountBean ub, StudyUserRoleBean currentRole) {
  	ArrayList answer = new ArrayList();

  	//HashMap definitionsById = new HashMap();
  	int i;
  	/*for (i = 0; i < eventDefinitionCRFs.size(); i++) {
  		EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
  		definitionsById.put(new Integer(edc.getStudyEventDefinitionId()), edc);
  	}*/
  	
  	StudyEventDAO sedao = new StudyEventDAO(ds);
  	CRFDAO cdao = new CRFDAO(ds);
  	CRFVersionDAO cvdao = new CRFVersionDAO(ds);
  	ItemDataDAO iddao = new ItemDataDAO(ds);
    EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(ds);

  	for (i = 0; i < eventCRFs.size(); i++) {
  		EventCRFBean ecb = (EventCRFBean) eventCRFs.get(i);

  		// populate the event CRF with its crf bean
  		int crfVersionId = ecb.getCRFVersionId();
  		CRFBean cb = cdao.findByVersionId(crfVersionId);
  		ecb.setCrf(cb);
  		
  		CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(crfVersionId);
  		ecb.setCrfVersion(cvb);
  		
  		// then get the definition so we can call DisplayEventCRFBean.setFlags
  		int studyEventId = ecb.getStudyEventId();
  		int studyEventDefinitionId = sedao.getDefinitionIdFromStudyEventId(studyEventId);
  		
  		//EventDefinitionCRFBean edc = (EventDefinitionCRFBean) definitionsById.get(new Integer(
  		//		studyEventDefinitionId));
        //fix problem of the above code(commented out), find the correct edc, note that on definitionId can be related to multiple eventdefinitioncrfBeans
        EventDefinitionCRFBean edc = (EventDefinitionCRFBean)edcdao.findByStudyEventDefinitionIdAndCRFId(studyEventDefinitionId,cb.getId());
  		if (edc != null) {
      //System.out.println("edc is not null, need to set flags");
  			DisplayEventCRFBean dec = new DisplayEventCRFBean();
            //System.out.println("edc.isDoubleEntry()" + edc.isDoubleEntry() + ecb.getId());
  			dec.setFlags(ecb, ub, currentRole, edc.isDoubleEntry());
  			ArrayList idata = iddao.findAllByEventCRFId(ecb.getId());
  			if (!idata.isEmpty()) {
  			  //consider an event crf started only if item data get created
  			  answer.add(dec);
  			}
  		}
    }

    return answer;
  }
  
  /**
   * Finds all the event definitions for which no event CRF exists - which is
   * the list of event definitions with uncompleted event CRFs.
   * 
   * @param eventDefinitionCRFs
   *          All of the event definition CRFs for this study event.
   * @param eventCRFs
   *          All of the event CRFs for this study event.
   * @return The list of event definitions for which no event CRF exists.
   */
  public static ArrayList getUncompletedCRFs(DataSource ds, ArrayList eventDefinitionCRFs, ArrayList eventCRFs) {
    int i;
    HashMap completed = new HashMap();
    HashMap startedButIncompleted = new HashMap();
    ArrayList answer = new ArrayList();

    /**
     * A somewhat non-standard algorithm is used here: let answer = empty;
     * foreach event definition ED, set isCompleted(ED) = false foreach event
     * crf EC, set isCompleted(EC.getEventDefinition()) = true foreach event
     * definition ED, if (!isCompleted(ED)) { answer += ED; } return answer;
     * This algorithm is guaranteed to find all the event definitions for which
     * no event CRF exists.
     * 
     * The motivation for using this algorithm is reducing the number of
     * database hits.
     * 
     * -jun-we have to add more CRFs here: the event CRF which dones't have item
     * data yet
     */

    for (i = 0; i < eventDefinitionCRFs.size(); i++) {
      EventDefinitionCRFBean edcrf = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
      completed.put(new Integer(edcrf.getCrfId()), Boolean.FALSE);
      startedButIncompleted.put(new Integer(edcrf.getCrfId()), new EventCRFBean());
    }

    CRFVersionDAO cvdao = new CRFVersionDAO(ds);
    ItemDataDAO iddao = new ItemDataDAO(ds);
    for (i = 0; i < eventCRFs.size(); i++) {
      EventCRFBean ecrf = (EventCRFBean) eventCRFs.get(i);
      int crfId = cvdao.getCRFIdFromCRFVersionId(ecrf.getCRFVersionId());
      ArrayList idata = iddao.findAllByEventCRFId(ecrf.getId());
      if (!idata.isEmpty()) {//this crf has data already
        completed.put(new Integer(crfId), Boolean.TRUE);
      } else {// event crf got created, but no data entered
        startedButIncompleted.put(new Integer(crfId), ecrf);
      }
    }

    for (i = 0; i < eventDefinitionCRFs.size(); i++) {
      DisplayEventDefinitionCRFBean dedc = new DisplayEventDefinitionCRFBean();
      EventDefinitionCRFBean edcrf = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
      dedc.setEdc(edcrf);
      Boolean b = (Boolean) completed.get(new Integer(edcrf.getCrfId()));
      EventCRFBean ev = (EventCRFBean) startedButIncompleted.get(new Integer(edcrf.getCrfId()));
      if ((b == null) || !b.booleanValue()) {
        dedc.setEventCRF(ev);
        answer.add(dedc);
      }
    }

    return answer;
  }

  public static void populateUncompletedCRFsWithCRFAndVersions(DataSource ds, ArrayList uncompletedEventDefinitionCRFs) {
    CRFDAO cdao = new CRFDAO(ds);
    CRFVersionDAO cvdao = new CRFVersionDAO(ds);

    int size = uncompletedEventDefinitionCRFs.size();
    for (int i = 0; i < size; i++) {
      DisplayEventDefinitionCRFBean dedcrf = (DisplayEventDefinitionCRFBean) uncompletedEventDefinitionCRFs
          .get(i);
      CRFBean cb = (CRFBean) cdao.findByPK(dedcrf.getEdc().getCrfId());
      dedcrf.getEdc().setCrf(cb);

      ArrayList<CRFVersionBean> versions = (ArrayList<CRFVersionBean>) cvdao.findAllActiveByCRF(dedcrf.getEdc().getCrfId());
      dedcrf.getEdc().setVersions(versions);
      uncompletedEventDefinitionCRFs.set(i, dedcrf);
    }
  }


  
  /*
  //Returns an array list which contain all completed eventcrf and uncompleted
  
  private ArrayList createAllEventCRFs(ArrayList eventCRFs, ArrayList eventDefinitionCRFs) {
    CRFDAO cdao = new CRFDAO(sm.getDataSource());
    CRFVersionDAO vdao = new CRFVersionDAO(sm.getDataSource());
    HashMap crfIdMap = new HashMap();
    ArrayList evs = new ArrayList();
    for (int i = 0; i < eventDefinitionCRFs.size(); i++) {
      EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
      crfIdMap.put(new Integer(edc.getCrfId()), Boolean.FALSE);
    }

    for (int i = 0; i < eventCRFs.size(); i++) {
      EventCRFBean eventCRF = (EventCRFBean) eventCRFs.get(i);
      logger.info("\nstage:" + eventCRF.getStage().getName());
      CRFBean crf = cdao.findByVersionId(eventCRF.getCRFVersionId());
      CRFVersionBean cVersion = (CRFVersionBean) vdao.findByPK(eventCRF.getCRFVersionId());
      eventCRF.setCrf(crf);
      eventCRF.setCrfVersion(cVersion);
      evs.add(eventCRF);

      if (crfIdMap.containsKey(new Integer(crf.getId()))) {
        crfIdMap.put(new Integer(crf.getId()), Boolean.TRUE);//already has
                                                             // entry for this
                                                             // crf
      }
    }//for

    //find those crfs which are not started yet(stage=uncompleted)
    Set keys = crfIdMap.keySet();
    Iterator it = keys.iterator();
    while (it.hasNext()) {
      Integer crfId = (Integer) it.next();
      if (crfIdMap.containsKey(crfId) && crfIdMap.get(crfId).equals(Boolean.FALSE)) {
        EventCRFBean ec = new EventCRFBean();
        CRFBean crf1 = (CRFBean) cdao.findByPK(crfId.intValue());
        ArrayList versions = vdao.findAllByCRFId(crfId.intValue());
        crf1.setVersions(versions);
        ec.setCrf(crf1);

        logger.info("\nstage:" + ec.getStage().getName());
        evs.add(ec);
      }
    }//while

    return evs;
  }
  
  */
  protected String getAdminServlet() {
    if (ub.isSysAdmin()) {
      return SecureController.ADMIN_SERVLET_CODE;
    }
    return "";
  }

}