package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import javax.sql.DataSource;

/**
 * Created by IntelliJ IDEA.
 * User: bruceperry
 * Date: May 7, 2008
 * Time: 1:24:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class DiscrepancyNoteUtil {

    public static final Map<String,Integer> TYPES = new HashMap<String,Integer>();
    static{
        TYPES.put("Failed Validation Check",1);
        TYPES.put("Incomplete",2);
        TYPES.put("Unclear/Unreadable",3);
        TYPES.put("Annotation",4);
        TYPES.put("Other",5);
        TYPES.put("Query",6);
        TYPES.put("Reason for Change",7);
    }
    public static final Map<String,Integer> RESOLUTION_STATUS = new HashMap<String,Integer>();
    static{
        RESOLUTION_STATUS.put("Open",1);
        RESOLUTION_STATUS.put("Updated",2);
        RESOLUTION_STATUS.put("Resolved",3);
        RESOLUTION_STATUS.put("Closed",4);
    }


    public void injectDiscNotesIntoStudyEvents(List<StudyEventBean> studyBeans,
                                               List<DiscrepancyNoteBean> allDiscNotes,
                                               DataSource dataSource) {

        if(studyBeans == null || allDiscNotes == null){
            return;
        }

        StudySubjectDAO studySubjDAO = new StudySubjectDAO(dataSource);
        StudySubjectBean studySubjBean = new StudySubjectBean();

        //the name of the study event


        for(StudyEventBean sbean : studyBeans) {

            studySubjBean=(StudySubjectBean)
              studySubjDAO.findByPK(sbean.getStudySubjectId());

            for(DiscrepancyNoteBean discBean : allDiscNotes) {
                if(sbean.getStudyEventDefinition().getName().equalsIgnoreCase(
                  discBean.getEventName()) &&
                  studySubjBean.getLabel().equalsIgnoreCase(discBean.getSubjectName()) ){

                    //add disc note to study event collection
                    sbean.getDiscBeanList().add(discBean);
                }

            }
        }
    }

    public void injectDiscNotesIntoDisplayStudyEvents(List<DisplayStudyEventBean>
      displayStudyBeans, int resolutionStatus,
                         DataSource dataSource, int discNoteType) {

        if(displayStudyBeans == null){
            return;
        }
        boolean hasResolutionStatus  = (resolutionStatus >= 1 && resolutionStatus <= 4);
        boolean hasDiscNoteType  = (discNoteType >= 1 && discNoteType <= 4);

        EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);
        DiscrepancyNoteDAO discrepancyNoteDAO = new DiscrepancyNoteDAO(dataSource);

        StudyEventBean studyEventBean;
        List<EventCRFBean> eventCRFBeans = new ArrayList<EventCRFBean>();
        List<DiscrepancyNoteBean> foundDiscNotes  = new ArrayList<DiscrepancyNoteBean>();


        for(DisplayStudyEventBean dStudyEventBean : displayStudyBeans) {
            studyEventBean= dStudyEventBean.getStudyEvent();
            //All EventCRFs for a study event
            eventCRFBeans = eventCRFDAO.findAllByStudyEvent(studyEventBean);

            for(EventCRFBean eventCrfBean : eventCRFBeans){
                //Find ItemData type notes
                foundDiscNotes = discrepancyNoteDAO.findItemDataDNotesFromEventCRF(eventCrfBean);

                //filter for any specified disc note type
                if((! foundDiscNotes.isEmpty()) && hasDiscNoteType){
                    foundDiscNotes = filterforDiscNoteType(foundDiscNotes,discNoteType);
                }

                if(! foundDiscNotes.isEmpty()){
                    if(! hasResolutionStatus) {
                        studyEventBean.getDiscBeanList().addAll(foundDiscNotes);
                    } else {
                        //Only include disc notes with a particular resolution status, specified by
                        //the parameter passed to the servlet
                        for(DiscrepancyNoteBean discBean : foundDiscNotes) {
                            if(discBean.getResolutionStatusId() == resolutionStatus){
                                studyEventBean.getDiscBeanList().add(discBean);
                            }
                        }
                    }
                }
                //Find EventCRF type notes
                foundDiscNotes = discrepancyNoteDAO.findEventCRFDNotesFromEventCRF(eventCrfBean);
                //filter for any specified disc note type
                if((! foundDiscNotes.isEmpty()) && hasDiscNoteType){
                    foundDiscNotes = filterforDiscNoteType(foundDiscNotes,discNoteType);
                }

                if(! foundDiscNotes.isEmpty()){
                    if(! hasResolutionStatus) {
                        studyEventBean.getDiscBeanList().addAll(foundDiscNotes);
                    } else {
                        //Only include disc notes with a particular resolution status, specified by
                        //the parameter passed to the servlet
                        for(DiscrepancyNoteBean discBean : foundDiscNotes) {
                            if(discBean.getResolutionStatusId() == resolutionStatus){
                                studyEventBean.getDiscBeanList().add(discBean);
                            }
                        }
                    }
                }//end  if(! foundDiscNotes.isEmpty()){
            }//end  for(EventCRFBean...
        }//end for (DisplayStudyEventBean
    }

    public List<DiscrepancyNoteBean> getDNotesForStudy(StudyBean currentStudy,
                                                       int resolutionStatus, DataSource dataSource,
                                                       int discNoteType) {

        List<DiscrepancyNoteBean> allDiscNotes =
          new ArrayList<DiscrepancyNoteBean>();
        if(currentStudy == null) return allDiscNotes;

        //Do the returned DN's have to be filtered?  A valid resolution status has to be between 1 and 4; 0 is "invalid";
        //-1 means that no resolutionStatus parameter was passed into the servlet
        boolean filterDiscNotes = (resolutionStatus >= 1 &&
          resolutionStatus <= 4);

        boolean filterforDiscNoteType = (discNoteType >= 1 &&
          discNoteType <= 7);

        DiscrepancyNoteDAO discrepancyNoteDAO = new
          DiscrepancyNoteDAO(dataSource);
        //what is the purpose of this data member?
        discrepancyNoteDAO.setFetchMapping(true);

        EventCRFDAO ecdao = new EventCRFDAO(dataSource);
        ArrayList itemDataNotes = discrepancyNoteDAO.findAllItemDataByStudy(currentStudy);

        ArrayList subjectNotes = discrepancyNoteDAO.findAllSubjectByStudy(currentStudy);

        ArrayList studySubjectNotes = discrepancyNoteDAO.findAllStudySubjectByStudy(currentStudy);

        ArrayList studyEventNotes = discrepancyNoteDAO.findAllStudyEventByStudy(currentStudy);

        ArrayList eventCRFNotes = discrepancyNoteDAO.findAllEventCRFByStudy(currentStudy);


        allDiscNotes.addAll(itemDataNotes);
        allDiscNotes.addAll(subjectNotes);
        allDiscNotes.addAll(studySubjectNotes);
        allDiscNotes.addAll(studyEventNotes);
        allDiscNotes.addAll(eventCRFNotes);
        if(filterDiscNotes) {
            allDiscNotes = filterDiscNotes(allDiscNotes, resolutionStatus);
        }
        if(filterforDiscNoteType) {
            allDiscNotes = filterforDiscNoteType(allDiscNotes, discNoteType);
        }
        return allDiscNotes;
    }

    public List<DiscrepancyNoteBean> filterDiscNotes(
      List<DiscrepancyNoteBean> allDiscNotes,
      int resolutionStatus) {
        //Do not filter this List if the resolutionStatus isn't between 1 and 4
        if(! (resolutionStatus >= 1 && resolutionStatus <= 4)) {
            return  allDiscNotes;
        }
        List<DiscrepancyNoteBean> newDiscNotes =
          new  ArrayList<DiscrepancyNoteBean>();

        for(DiscrepancyNoteBean dnBean : allDiscNotes) {
            if(dnBean.getResolutionStatusId() == resolutionStatus) {
                newDiscNotes.add(dnBean);
            }
        }
        return newDiscNotes;
    }

    public List<DiscrepancyNoteBean> filterforDiscNoteType(
      List<DiscrepancyNoteBean> allDiscNotes,
      int discNoteType) {

        //Do not filter this List if the discNoteType isn't between 1 and 7
        if(! (discNoteType >= 1 && discNoteType <= 7)) {
            return  allDiscNotes;
        }
        List<DiscrepancyNoteBean> newDiscNotes =
          new  ArrayList<DiscrepancyNoteBean>();

        for(DiscrepancyNoteBean dnBean : allDiscNotes) {
            if(dnBean.getDiscrepancyNoteTypeId() == discNoteType) {
                newDiscNotes.add(dnBean);
            }
        }
        return newDiscNotes;
    }

    public List<DiscrepancyNoteBean> filterDiscNotesBySubject(
      List<DiscrepancyNoteBean> allDiscNotes,
      int subjectId,
      DataSource dataSource) {
        //Do not filter this List if the subjectId < 1

        if(! (subjectId >= 1)) {
            return  allDiscNotes;
        }

        StudySubjectDAO studySubjDAO = new StudySubjectDAO(dataSource);
        StudySubjectBean studySubjBean = new StudySubjectBean();
        studySubjBean=(StudySubjectBean)
          studySubjDAO.findByPK(subjectId);

        List<DiscrepancyNoteBean> newDiscNotes =
          new  ArrayList<DiscrepancyNoteBean>();

        for(DiscrepancyNoteBean dnBean : allDiscNotes) {
            if(dnBean.getSubjectName().equalsIgnoreCase(studySubjBean.getLabel())) {
                newDiscNotes.add(dnBean);
            }
        }
        return newDiscNotes;
    }

    public Map generateDiscNoteSummary(List<DiscrepancyNoteBean> allDiscBeans){
        /*Number of discrepancy notes by Type
i.	Also display a breakdown of each status by Type*/
        /*
        3;"Unclear/Unreadable"
        4;"Annotation"
        5;"Other"
        1;"Failed Validation Check"
        2;"Incomplete"
        6;"Query"
        7;"Reason for Change"

        1: Open
        2: Updated
        3: resolved
        4:closed
        */
        if(allDiscBeans == null || allDiscBeans.isEmpty())  return new HashMap();
        /*This container is a Map of Maps.
         * e.g.,  Validation --> Map [Validation total --> total
          *  Open --> total, etc.]*/
        Map<String,Map> summaryMap = new HashMap<String,Map>();
        Map<String,Integer> tempMap = null;
        int tempType = 0;

        //initialize Map
        for(String discNoteTypeName : TYPES.keySet()) {

            tempMap = new HashMap<String,Integer>();
            summaryMap.put(discNoteTypeName,tempMap);
            tempType= TYPES.get(discNoteTypeName);
            tempMap.put("Total",getNumberOfDiscNoteType(allDiscBeans,tempType));

            for(String statusName : RESOLUTION_STATUS.keySet()){
                 tempMap.put(statusName,getNumberByStatusOfNotes(allDiscBeans,tempType,
                   RESOLUTION_STATUS.get(statusName)));
            }


        }

        return summaryMap;
    }

    public int getNumberOfDiscNoteType(List<DiscrepancyNoteBean> discrepancyNoteBeans,
                                       int discNoteType) {
        int typeCount=0;
        for( DiscrepancyNoteBean dBean : discrepancyNoteBeans){
            if(dBean.getDiscrepancyNoteTypeId() == discNoteType){
                typeCount++;
            }
        }

        return typeCount;

    }

    public int getNumberByStatusOfNotes(List<DiscrepancyNoteBean> discrepancyNoteBeans,
                                        int typeId,
                                        int resolutionStatus){
        int tempCount=0;
        for( DiscrepancyNoteBean dBean : discrepancyNoteBeans){
            if(dBean.getDiscrepancyNoteTypeId() == typeId &&
              dBean.getResolutionStatusId() == resolutionStatus) {
                tempCount++;
            }
        }
        return tempCount;

    }

}

