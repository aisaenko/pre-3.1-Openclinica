package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import javax.sql.DataSource;

/**
 * DiscrepancyNoteUtil is a convenience class for managing discrepancy notes, such as
 * getting all notes for a study, or filtering them by subject or resolution status.
 */
public class DiscrepancyNoteUtil {
    //TODO: initialize these static members straight from the database.
    public static final Map<String,Integer> TYPES = new HashMap<String,Integer>();
    static{
        TYPES.put("Failed Validation Check",1);
        TYPES.put("Annotation",2);
        TYPES.put("Query",3);
        TYPES.put("Reason for Change",4);
    }
    public static final Map<String,Integer> RESOLUTION_STATUS = new HashMap<String,Integer>();
    static{
        RESOLUTION_STATUS.put("New",1);
        RESOLUTION_STATUS.put("Updated",2);
        RESOLUTION_STATUS.put("Resolution Proposed",3);
        RESOLUTION_STATUS.put("Closed",4);
        RESOLUTION_STATUS.put("Not Applicable",5);
    }

    /**
     * This method links discrepancy notes to the study event and study subject
     * associated with those notes
     * @param studyBeans  A List of StudyEventBeans.
     * @param allDiscNotes A List of DiscrepancyNoteBeans associated with a single study.
     * @param dataSource   A DataSource used to get from the database a study subject associated
     * with a study event.
     */
    public void injectDiscNotesIntoStudyEvents(List<StudyEventBean> studyBeans,
                                               List<DiscrepancyNoteBean> allDiscNotes,
                                               DataSource dataSource) {

        if(studyBeans == null || allDiscNotes == null){
            return;
        }

        StudySubjectDAO studySubjDAO = new StudySubjectDAO(dataSource);
        StudySubjectBean studySubjBean = new StudySubjectBean();


        for(StudyEventBean sbean : studyBeans) {
            //Get the StudySubjectBean associated with the study event
            studySubjBean=(StudySubjectBean)
              studySubjDAO.findByPK(sbean.getStudySubjectId());
            //For each DiscrepancyNoteBean, find out whether the study event definition name
            //equals the bean's event name property, and whether the bean's subject name
            //equals the StudySubjectBean's name
            for(DiscrepancyNoteBean discBean : allDiscNotes) {
                if(sbean.getStudyEventDefinition().getName().equalsIgnoreCase(
                  discBean.getEventName()) &&
                  studySubjBean.getLabel().equalsIgnoreCase(discBean.getSubjectName()) ){

                    //add discrepancy note to the study event list of notes
                    //Each study bean has a List property containing its associated
                    //DiscrepancyNoteBeans 
                    sbean.getDiscBeanList().add(discBean);
                }

            }
        }
    }

    /**
     *  Add associated discrepancy notes to the appropriate StudyEventBean (contained by
     * DisplayStudyBeans). A StudyEventBean has a List of DiscrepancyNoteBeans.
     * @param displayStudyBeans  A List of DisplayStudyEventBeans
     * @param resolutionStatus  An int representing the resolution status, if we are
     * filtering the DiscrepancyNoteBeans based on their resolutionStatus id number.
     * @param dataSource  A DataSource used for the DAO methods.
     * @param discNoteType  An int representing the discrepancy note type,  if we are
     * filtering the DiscrepancyNoteBeans based on their discrepancyNoteTypeId number.
     */
    public void injectDiscNotesIntoDisplayStudyEvents(List<DisplayStudyEventBean>
      displayStudyBeans, int resolutionStatus,
                         DataSource dataSource, int discNoteType) {

        if(displayStudyBeans == null){
            return;
        }
        //booleans representing whether this method should only get DiscrepancyNoteBeans with
        //certain resolution status or discrepancyNoteTypeId number.
        boolean hasResolutionStatus  = (resolutionStatus >= 1 && resolutionStatus <= 5);
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
                //Find ItemData type notes associated iwth an event crf
                foundDiscNotes = discrepancyNoteDAO.findItemDataDNotesFromEventCRF(eventCrfBean);

                //filter for any specified disc note type
                if(! foundDiscNotes.isEmpty() && hasDiscNoteType){
                    //only include disc notes that have the specified disc note type id
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
                if(! foundDiscNotes.isEmpty() && hasDiscNoteType){
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

    /**
     * This method is an overloaded version of the previous method, with the only difference being the resolutionStatusIds
     * parameter in this method. This method adds the associated discrepancy notes to the appropriate StudyEventBean (contained by
     * DisplayStudyBeans).
     * @param displayStudyBeans A List of DisplayStudyEventBeans.
     * @param resolutionStatusIds  A HashSet object consisting of resolution status ids (i.e., 1,2,3).
     * @param dataSource  A DataSource object, for use with the DAO methods.
     * @param discNoteType An int discrepancy note type, for filtering the discrepancy notes that
     * are attached to various StudyEvents.
     */
    public void injectDiscNotesIntoDisplayStudyEvents(List<DisplayStudyEventBean>
      displayStudyBeans, Set<Integer> resolutionStatusIds,
                         DataSource dataSource, int discNoteType) {

        if(displayStudyBeans == null){
            return;
        }
        //booleans representing whether this method should only get DiscrepancyNoteBeans with
        //certain resolution status or discrepancyNoteTypeId number.
        boolean hasResolutionStatus  = this.checkResolutionStatus(resolutionStatusIds);
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
                //Find ItemData type notes associated iwth an event crf
                foundDiscNotes = discrepancyNoteDAO.findItemDataDNotesFromEventCRF(eventCrfBean);

                //filter for any specified disc note type
                if((! foundDiscNotes.isEmpty()) && hasDiscNoteType){
                    //only include disc notes that have the specified disc note type id
                    foundDiscNotes = filterforDiscNoteType(foundDiscNotes,discNoteType);
                }

                if(! foundDiscNotes.isEmpty()){
                    if(! hasResolutionStatus) {
                        studyEventBean.getDiscBeanList().addAll(foundDiscNotes);
                    } else {
                        //Only include disc notes with a particular resolution status, specified by
                        //the parameter passed to the servlet or saved in a session variable
                        for(DiscrepancyNoteBean discBean : foundDiscNotes) {
                            for(int statusId : resolutionStatusIds){
                                if(discBean.getResolutionStatusId() == statusId){
                                    studyEventBean.getDiscBeanList().add(discBean);
                                }
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
                            for(int statusId : resolutionStatusIds){
                                if(discBean.getResolutionStatusId() == statusId){
                                    studyEventBean.getDiscBeanList().add(discBean);
                                }
                            }
                        }
                    }
                }//end  if(! foundDiscNotes.isEmpty()){
            }//end  for(EventCRFBean...
        }//end for (DisplayStudyEventBean
    }

    /**
     * Acquire the DiscrepancyNoteBeans for a specific study.
     * @param currentStudy  A StudyBean object.
     * @param resolutionStatus An int resolution status; only DiscrepancyNoteBeans will be returned if
     * they have this resolutionStatus id.
     * @param dataSource A DataSource used for various DAO methods.
     * @param discNoteType  An int discrepancy note type id; only DiscrepancyNoteBeans will be returned if
     * they have this discrepancyNoteTypeId.
     * @return  A List of DiscrepancyNoteBeans.
     */
    public List<DiscrepancyNoteBean> getDNotesForStudy(StudyBean currentStudy,
                                                       int resolutionStatus, DataSource dataSource,
                                                       int discNoteType) {

        List<DiscrepancyNoteBean> allDiscNotes =
          new ArrayList<DiscrepancyNoteBean>();
        if(currentStudy == null) return allDiscNotes;

        //Do the returned DN's have to be filtered?  A valid resolution status has to be between 1 and 5; 0 is "invalid";
        //-1 means that no resolutionStatus parameter was passed into the servlet
        boolean filterDiscNotes = resolutionStatus >= 1 &&
          resolutionStatus <= 5;

        boolean filterforDiscNoteType = (discNoteType >= 1 &&
          discNoteType <= 4);

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
            //filter for the resolution status
            allDiscNotes = filterDiscNotes(allDiscNotes, resolutionStatus);
        }
        if(filterforDiscNoteType) {
            //filter for the ddiscrepancyNoteTypeId
            allDiscNotes = filterforDiscNoteType(allDiscNotes, discNoteType);
        }
        return allDiscNotes;
    }

    /**
     * An overloaded version of the prior method, the difference being a HashSet of resolution status ids,
     * instead of a single id.
     * @param currentStudy    A StudyBean object.
     * @param resolutionStatusIds   A HashSet object consisting of resolution status ids (i.e., 1,2,3).
     * @param dataSource A DataSource used for various DAO methods.
     * @param discNoteType  An int discrepancy note type id; only DiscrepancyNoteBeans will be returned if
     * they have this discrepancyNoteTypeId.
     * @return  A List of DiscrepancyNoteBeans.
     */
    public List<DiscrepancyNoteBean> getDNotesForStudy(StudyBean currentStudy,
                                                       Set<Integer> resolutionStatusIds, DataSource dataSource,
                                                       int discNoteType) {

        List<DiscrepancyNoteBean> allDiscNotes =
          new ArrayList<DiscrepancyNoteBean>();
        if(currentStudy == null) return allDiscNotes;

        //Do the returned DN's have to be filtered?  A valid resolution status has to be between 1 and 5; 0 is "invalid";
        //-1 means that no resolutionStatus parameter was passed into the servlet
        boolean filterDiscNotes = checkResolutionStatus(resolutionStatusIds);

        boolean filterforDiscNoteType = (discNoteType >= 1 &&
          discNoteType <= 4);

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
            //filter for the resolution status
            allDiscNotes = filterDiscNotes(allDiscNotes, resolutionStatusIds);
        }
        if(filterforDiscNoteType) {
            //filter for the ddiscrepancyNoteTypeId
            allDiscNotes = filterforDiscNoteType(allDiscNotes, discNoteType);
        }
        return allDiscNotes;
    }

    /**
     * Get all discrepancy notes for a study, including "threads" of parent and child discrepancy notes.
     * @param currentStudy    A StudyBean object.
     * @param resolutionStatusIds   A HashSet object consisting of resolution status ids (i.e., 1,2,3).
     * @param dataSource A DataSource used for various DAO methods.
     * @param discNoteType  An int discrepancy note type id; only DiscrepancyNoteBeans will be returned if
     * they have this discrepancyNoteTypeId.
     * @return  A List of DiscrepancyNoteBeans.
     */
    public List<DiscrepancyNoteBean> getThreadedDNotesForStudy(StudyBean currentStudy,
                                                               Set<Integer> resolutionStatusIds, DataSource dataSource,
                                                               int discNoteType) {

        List<DiscrepancyNoteBean> allDiscNotes =
          new ArrayList<DiscrepancyNoteBean>();
        if(currentStudy == null) return allDiscNotes;

        //Do the returned DN's have to be filtered?  A valid resolution status has to be between 1 and 5; 0 is "invalid";
        //-1 means that no resolutionStatus parameter was passed into the servlet
        boolean filterDiscNotes = checkResolutionStatus(resolutionStatusIds);

        boolean filterforDiscNoteType = (discNoteType >= 1 &&
          discNoteType <= 4);

        DiscrepancyNoteDAO discrepancyNoteDAO = new
          DiscrepancyNoteDAO(dataSource);
        //what is the purpose of this data member?
        discrepancyNoteDAO.setFetchMapping(true);

        EventCRFDAO ecdao = new EventCRFDAO(dataSource);
        ArrayList itemDataNotes = discrepancyNoteDAO.findAllItemDataByStudy(currentStudy);
        //ArrayList eventCRFNotes = discrepancyNoteDAO.findAllEventCRFByStudy(currentStudy);
        allDiscNotes.addAll(itemDataNotes);
        //make sure that any "parent" notes have the last resolution status of any
        //of their child notes
        updateStatusOfParents(allDiscNotes,dataSource,currentStudy);

        if(filterDiscNotes) {
            //filter for the resolution status
            allDiscNotes = filterDiscNotes(allDiscNotes, resolutionStatusIds);
        }
        if(filterforDiscNoteType) {
            //filter for the ddiscrepancyNoteTypeId
            allDiscNotes = filterforDiscNoteType(allDiscNotes, discNoteType);
        }

        return allDiscNotes;
    }

    /**
     * Take a List of "parent" DiscrepancyNoteBeans and if they have any "children," make sure
     * that the resolution status id of the parent matches that of the last child DiscrepancyNoteBean.
     * @param allDiscNotes   A List of DiscrepancyNoteBeans.
     * @param dataSource    The DataSource the DAO uses.
     * @param currentStudy  A StudyBean representing the current study.
     */
    public void updateStatusOfParents(List<DiscrepancyNoteBean> allDiscNotes,
                                      DataSource dataSource, StudyBean currentStudy){
        if(allDiscNotes == null || allDiscNotes.isEmpty()){
            return;
        }
        List<DiscrepancyNoteBean> childDiscBeans = new ArrayList<DiscrepancyNoteBean>();
        DiscrepancyNoteDAO discrepancyNoteDAO = new DiscrepancyNoteDAO(dataSource);
        DiscrepancyNoteBean lastChild = new DiscrepancyNoteBean();
        int resolutionStatusId=0;

        for(DiscrepancyNoteBean discBean : allDiscNotes){
            childDiscBeans = discrepancyNoteDAO.findAllByStudyAndParent(currentStudy,
              discBean.getId());
            if(! childDiscBeans.isEmpty()) {
                lastChild = childDiscBeans.get(childDiscBeans.size()-1);
                resolutionStatusId = lastChild.getResolutionStatusId();
                if(discBean.getResolutionStatusId() != resolutionStatusId){
                    discBean.setResolutionStatusId(resolutionStatusId);
                }
            }

            //clear the List for the next iteration
            if(childDiscBeans != null){
                childDiscBeans.clear();
            }

        }

    }
    /**
     * Check whether the contents of a list of resolution status ids are valid.
     * @param listOfStatusIds  A HashSet of resolution status ids.
     * @return true or false, depending on whether the ids are valid.
     */
    public boolean checkResolutionStatus(Set<Integer> listOfStatusIds){
        if(listOfStatusIds == null)  return false;

        for(int id : listOfStatusIds) {
            if(id >= 1 && id <= 5){
                return true;
            }

        }
        return false;
    }


    /**
     * Filter a List of DiscrepancyNoteBeans for a particular resolution status id. 
     * @param allDiscNotes A List of DiscrepancyNoteBeans prior to being filtered for a resolution status id.
     * @param resolutionStatus An int representing a resolution status id.
     * @return A List of DiscrepancyNoteBeans that have the specified resolution status id.
     */
    public List<DiscrepancyNoteBean> filterDiscNotes(
      List<DiscrepancyNoteBean> allDiscNotes,
      int resolutionStatus) {
        //Do not filter this List if the resolutionStatus isn't between 1 and 5
        if(! (resolutionStatus >= 1 && resolutionStatus <= 5)) {
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

    /**
     * An overloaded method that performs the same task as the previous method.
     * The method filters a List of DiscrepancyNoteBeans for a particular resolution status id.
     * @param allDiscNotes A List of DiscrepancyNoteBeans prior to being filtered for a resolution status id.
     * @param resolutionStatusIds A HashSet object consisting of resolution status ids (i.e., 1,2,3).
     * @return A List of DiscrepancyNoteBeans that have the specified resolution status id.
     */
    public List<DiscrepancyNoteBean> filterDiscNotes(
      List<DiscrepancyNoteBean> allDiscNotes,
      Set<Integer> resolutionStatusIds) {

        List<DiscrepancyNoteBean> newDiscNotes =
          new  ArrayList<DiscrepancyNoteBean>();

        for(DiscrepancyNoteBean dnBean : allDiscNotes) {
            for (int statusId : resolutionStatusIds){
                if(dnBean.getResolutionStatusId() == statusId) {
                    newDiscNotes.add(dnBean);
                }
            }
        }
        return newDiscNotes;
    }

    /**
     * Filter a List of DiscrepancyNoteBeans for a particular discrepancy note type id.
     * @param allDiscNotes A List of DiscrepancyNoteBeans prior to being filtered for a discrepancy note type id.
     * @param discNoteType An it representing a discrepancy note type id.
     * @return A List of DiscrepancyNoteBeans that have the specified discrepancy note type id.
     */
    public List<DiscrepancyNoteBean> filterforDiscNoteType(
      List<DiscrepancyNoteBean> allDiscNotes,
      int discNoteType) {

        //Do not filter this List if the discNoteType isn't between 1 and 4
        if(! (discNoteType >= 1 && discNoteType <= 4)) {
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

    /**
     * Filter a List of DiscrepancyNoteBeans by subject id.
     * @param allDiscNotes A List of DiscrepancyNoteBeans prior to filtering.
     * @param subjectId  An int subject id.
     * @param dataSource  A DataSource object used by DAO methods.
     * @return  The filtered List of DiscrepancyNoteBeans.
     */
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

    /**
     * Generate a summary of statistics for a collection of discrepancy notes.
     * @param allDiscBeans A List of DiscrepancyNoteBeans.
     * @return A Map mapping the name of each type of note (e.g., "Annotation") to another Map
     * containing that type's statistics. 
     */
    public Map generateDiscNoteSummary(List<DiscrepancyNoteBean> allDiscBeans){
        if(allDiscBeans == null || allDiscBeans.isEmpty())  return new HashMap();
        /*This container is a Map of Maps.
         * e.g.,  Failed Validation Check --> Map [Total --> total number of Failed Validation Check type notes,
          *  Open --> total number of Failed Validation Check notes that are Open types, etc.]*/
        Map<String,Map> summaryMap = new HashMap<String,Map>();
        //The internal Map, mapping the name of the status (e.g., Resolved) to the number of
        //Notes that are Resolved for that particular discrepancy note type (e.g., Failed Validation Check).
        Map<String,Integer> tempMap = null;
        int tempType = 0;
        int tempTotal=0;

        //initialize Map
        for(String discNoteTypeName : TYPES.keySet()) {

            tempMap = new HashMap<String,Integer>();
            //Create the summary or outer Map for each type name (e.g., Incomplete)
            summaryMap.put(discNoteTypeName,tempMap);
            tempType= TYPES.get(discNoteTypeName);
            tempTotal = getNumberOfDiscNoteType(allDiscBeans,tempType);
            tempMap.put("Total",tempTotal);
            if(tempTotal == 0) continue;

            for(String statusName : RESOLUTION_STATUS.keySet()){
                tempMap.put(statusName,getNumberByStatusOfNotes(allDiscBeans,tempType,
                  RESOLUTION_STATUS.get(statusName)));
            }


        }

        return summaryMap;
    }

    /**
     * Generate a HashMap containing data on the type of discrepancy note and status that
     * the user is currently filtering a JSP view for.
     * @param discNoteType  An id of a type of discrepancy note (e.g., Annotation).
     * @param discNoteStatus An id of a status for discrepancy notes (e.g., Open).
     * @return  A HashMap mapping a String such as "status" to any filter on the
     * status, for example, Open or resolved.
     */
    public Map generateFilterSummary(int discNoteType,int discNoteStatus){
        Map<String,String> filterSummary = new HashMap<String,String>();
        if(discNoteType == 0 && discNoteStatus == 0) return filterSummary;

        //Identify any filter for the resolution status
        int filterNum = 0;
        for(String statusName : RESOLUTION_STATUS.keySet()){
            filterNum = RESOLUTION_STATUS.get(statusName);
            if(discNoteStatus == filterNum)  {
                filterSummary.put("status",statusName);
            }

        }

        //Identify any filter for the resolution type
        filterNum = 0;
        for(String typeName : TYPES.keySet()){
            filterNum = TYPES.get(typeName);
            if(discNoteType == filterNum)  {
                filterSummary.put("type",typeName);
            }

        }

        return filterSummary;
    }

    /**
     * An overloaded version of the previous method. The method generates
     *  a HashMap containing data on the type of discrepancy note and status that
     * the user is currently filtering a JSP view for.
     * @param discNoteType  An id of a type of discrepancy note (e.g., Annotation).
     * @param discNoteStatus A HashSet of IDs for discrepancy note statuses (e.g., Open, Closed).
     * @return  A HashMap mapping a String such as "status" to any filter on the
     * status, for example, Open or resolved.
     */
    public Map<String,List<String>> generateFilterSummary(int discNoteType,
                                                          Set<Integer> discNoteStatus){
        Map<String,List<String>> filterSummary = new HashMap<String,List<String>>();
        if(discNoteType == 0 && discNoteStatus == null) return filterSummary;
        List<String> listOfStatusNames = new ArrayList<String>();
        filterSummary.put("status",listOfStatusNames);
        List<String> listOfTypeNames = new ArrayList<String>();
        filterSummary.put("type",listOfTypeNames);

        //Identify any filter for the resolution status
        int filterNum = 0;
        if(discNoteStatus != null){
            for(String statusName : RESOLUTION_STATUS.keySet()){
                filterNum = RESOLUTION_STATUS.get(statusName);
                for(int statusId : discNoteStatus) {
                    if(statusId == filterNum)  {
                        filterSummary.get("status").add(statusName);
                    }
                }

            }
        }

        //Identify any filter for the resolution type
        filterNum = 0;
        for(String typeName : TYPES.keySet()){
            filterNum = TYPES.get(typeName);
            if(discNoteType == filterNum)  {
                filterSummary.get("type").add(typeName);
            }

        }

        return filterSummary;
    }



    /**
     * Get the number of DiscrepancyNoteBeans of a particular type, like "Failed Validation Check."
     * @param discrepancyNoteBeans A List of DiscrepancyNoteBeans.
     * @param discNoteType  An int representing the dsicrepancy note type id.
     * @return  Only any DiscrepancyNoteBeans that have this type id.
     */
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
    /**
     * Get the number of DiscrepancyNoteBeans of a particular resolution status,
     * like Open or Resolved; and of a certain discrepancy note type.
     * @param discrepancyNoteBeans A List of DiscrepancyNoteBeans.
     * @param typeId  An int representing the dsicrepancy note type id.
     * @param resolutionStatus  An int representing the resolution status.
     * @return  Only any DiscrepancyNoteBeans that have this type id and resolution status.
     */
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

    /**
     * Examiine a List of StudyEventBeans to determine if any of them have any discrepancy notes. The method is passed
     * either a List of DisplayStudyEventBeans or DisplayStudyEventBeans, in the manner of studyEventsHaveDiscNotes(studyEventBeans, null)
     * or studyEventsHaveDiscNotes(null,displayStudyEventBeans)
     * @param studyEvents A List of StudyEventBeans.
     * @param displayStudyEvents A List of DisplayStudyEventBeans.
     * @return  boolean true if any of the study events have a List that contains some discrepancy notes.
     */
    public boolean studyEventsHaveDiscNotes(List<StudyEventBean> studyEvents,
                                            List<DisplayStudyEventBean> displayStudyEvents)  {

        if(studyEvents != null) {
            for(StudyEventBean studyEBean : studyEvents){
                if(studyEBean.getDiscBeanList().size() > 0) {
                    return true;
                }
            }
        }
        StudyEventBean studyEventBean = null;

        if(displayStudyEvents != null) {
            for(DisplayStudyEventBean displayStudyEventBean : displayStudyEvents){
                studyEventBean = displayStudyEventBean.getStudyEvent();
                if(studyEventBean.getDiscBeanList().size() > 0) {
                    return true;
                }
            }
        }

        return false;
    }


}

