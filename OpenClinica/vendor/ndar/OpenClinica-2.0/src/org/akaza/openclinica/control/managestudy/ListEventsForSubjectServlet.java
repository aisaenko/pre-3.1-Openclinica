/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudySubjectBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudySubjectRow;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 */
public class ListEventsForSubjectServlet extends SecureController {
  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
   */
  protected void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }

    Role r = currentRole.getRole();
    if (r.equals(Role.STUDYDIRECTOR) || r.equals(Role.COORDINATOR) || r.equals(Role.INVESTIGATOR)
        || r.equals(Role.RESEARCHASSISTANT)) {
      return;
    }

    addPageMessage("You don't have correct privilege in your current active study. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.MENU_SERVLET, "may not submit data", "1");
  }

  public void processRequest() throws Exception {

    FormProcessor fp = new FormProcessor(request);
    int definitionId = fp.getInt("defId");
    int tabId = fp.getInt("tab");
    if (definitionId <= 0) {
      addPageMessage("Please choose an event definition tab to view details.");
      forwardPage(Page.LIST_STUDY_SUBJECT_SERVLET);
      return;
    }

    StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
    StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(definitionId);

    StudySubjectDAO sdao = new StudySubjectDAO(sm.getDataSource());
    StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());

    SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(sm.getDataSource());
    StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(sm.getDataSource());

    EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
    EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
    CRFDAO crfdao = new CRFDAO(sm.getDataSource());

    // find all the groups in the current study
    ArrayList studyGroupClasses = sgcdao.findAllActiveByStudy(currentStudy);

    // information for the event tabs
    ArrayList allDefs = (ArrayList) seddao.findAllActiveByStudy(currentStudy);

    ArrayList eventDefinitionCRFs = (ArrayList) edcdao
        .findAllActiveByEventDefinitionId(definitionId);

    for (int i = 0; i < eventDefinitionCRFs.size(); i++) {
      EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
      CRFBean crf = (CRFBean) crfdao.findByPK(edc.getCrfId());
      edc.setCrf(crf);

    }
    request.setAttribute("allDefsArray", allDefs);
    request.setAttribute("allDefsNumber", new Integer(allDefs.size()));
    request.setAttribute("groupSize", new Integer(studyGroupClasses.size()));
    request.setAttribute("eventDefCRFSize", new Integer(eventDefinitionCRFs.size()));
    request.setAttribute("tabId", new Integer(tabId));
    request.setAttribute("studyEventDef", sed);
    request.setAttribute("eventDefCRFs", eventDefinitionCRFs);
    
    // find all the subjects in current study
    ArrayList subjects = (ArrayList) sdao.findAllByStudyId(currentStudy.getId());

    ArrayList displayStudySubs = new ArrayList();
    for (int i = 0; i < subjects.size(); i++) {
      StudySubjectBean studySub = (StudySubjectBean) subjects.get(i);

      ArrayList groups = (ArrayList) sgmdao.findAllByStudySubject(studySub.getId());

      ArrayList subGClasses = new ArrayList();
      for (int j = 0; j < studyGroupClasses.size(); j++) {
        StudyGroupClassBean sgc = (StudyGroupClassBean) studyGroupClasses.get(j);
        boolean hasClass = false;
        for (int k = 0; k < groups.size(); k++) {
          SubjectGroupMapBean sgmb = (SubjectGroupMapBean) groups.get(k);
          if (sgmb.getGroupClassName().equalsIgnoreCase(sgc.getName())) {
            subGClasses.add(sgmb);
            hasClass = true;
            break;
          }

        }
        if (!hasClass) {
          subGClasses.add(new SubjectGroupMapBean());
        }

      }

      // find all eventcrfs for each event, for each event tab
      ArrayList displaySubjectEvents = new ArrayList();

      ArrayList displayEvents = new ArrayList();
      ArrayList events = (ArrayList) sedao.findAllByStudySubjectAndDefinition(studySub, sed);
      
      for (int k = 0; k < events.size(); k++) {
        StudyEventBean seb = (StudyEventBean) events.get(k);
        DisplayStudyEventBean dseb = ListStudySubjectServlet.getDisplayStudyEventsForStudySubject(
            studySub, seb, sm.getDataSource(), ub, currentRole);

       // ArrayList eventCRFs = ecdao.findAllByStudyEvent(seb);
        //ArrayList al = ViewStudySubjectServlet.getUncompletedCRFs(sm.getDataSource(),
        //    eventDefinitionCRFs, eventCRFs);
        //dseb.getUncompletedCRFs().add(al);
        displayEvents.add(dseb);

      }
      
     
      
      ArrayList al = new ArrayList();
      for (int k = 0; k < displayEvents.size(); k++) {       
        DisplayStudyEventBean dseb = (DisplayStudyEventBean) displayEvents.get(k);
        ArrayList eventCRFs = dseb.getDisplayEventCRFs();
        //ArrayList uncompletedCRFs = dseb.getUncompletedCRFs();
       
        for (int a = 0; a < eventDefinitionCRFs.size(); a++) {
          EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFs.get(a);
          int crfId = edc.getCrfId();
          boolean hasCRF = false;
          for (int b = 0; b < eventCRFs.size(); b++) {
            DisplayEventCRFBean decb = (DisplayEventCRFBean) eventCRFs.get(b);
            //System.out.println("eventCRF" + decb.getEventCRF().getId() + decb.getStage().getName() );
            if (decb.getEventCRF().getCrf().getId() == crfId) {
              dseb.getAllEventCRFs().add(decb);
              //System.out.println("hasCRf" + crfId + decb.getEventCRF().getCrf().getName());
             
              hasCRF = true;
              break;
            }
          }
          if (hasCRF == false) {
            DisplayEventCRFBean db = new DisplayEventCRFBean();
            db.setEventDefinitionCRF(edc);
            db.getEventDefinitionCRF().setCrf(edc.getCrf());
            dseb.getAllEventCRFs().add(db);
            
            //System.out.println("noCRf" + crfId);
           
          }
         
         
        }
       
        System.out.println("size of all event crfs" + dseb.getAllEventCRFs().size());
      }

      DisplayStudySubjectBean dssb = new DisplayStudySubjectBean();

      dssb.setStudySubject(studySub);
      dssb.setStudyGroups(subGClasses);
      dssb.setStudyEvents(displayEvents);
      displayStudySubs.add(dssb);
    }

    EntityBeanTable table = fp.getEntityBeanTable();
    ArrayList allStudyRows = DisplayStudySubjectRow.generateRowsFromBeans(displayStudySubs);

    ArrayList columnArray = new ArrayList();

    columnArray.add("ID");
    columnArray.add("Subject Status");
    columnArray.add("Gender");
    for (int i = 0; i < studyGroupClasses.size(); i++) {
      StudyGroupClassBean sgc = (StudyGroupClassBean) studyGroupClasses.get(i);
      columnArray.add(sgc.getName());
    }

    //columnArray.add("Event Sequence");
    columnArray.add("Event Status");
    columnArray.add("Event Date");

    for (int i = 0; i < eventDefinitionCRFs.size(); i++) {
      EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
      columnArray.add(edc.getCrf().getName());

    }
    columnArray.add("Actions");
    String columns[] = new String[columnArray.size()];
    columnArray.toArray(columns);

    table.setColumns(new ArrayList(Arrays.asList(columns)));
    table.setQuery("ListEventsForSubject?defId=" + definitionId + "&tab=" + tabId, new HashMap());
    table.hideColumnLink(columnArray.size() - 1);

    table.addLink("Add a new subject", "AddNewSubject?instr=1");
    table.setRows(allStudyRows);
    table.computeDisplay();

    request.setAttribute("table", table);

    
    forwardPage(Page.LIST_EVENTS_FOR_SUBJECT);
  }

}
